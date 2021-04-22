package in.co.itlabs.minierp.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import in.co.itlabs.minierp.entities.College;
import in.co.itlabs.minierp.entities.User;
import lombok.Data;
import lombok.Getter;

@ApplicationScoped
public class AuthService {

	private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
	private Argon2 argon2 = Argon2Factory.create();

	public enum ErpModule {
		Students, Student_PersonalDetails, Student_AdmissionDetails, Student_ContactDetails
	}

	@Getter
	public final class Permission {
		private final boolean canCreate;
		private final boolean canRead;
		private final boolean canUpdate;
		private final boolean canDelete;

		private Permission(boolean canCreate, boolean canRead, boolean canUpdate, boolean canDelete) {
			this.canCreate = canCreate;
			this.canRead = canRead;
			this.canUpdate = canUpdate;
			this.canDelete = canDelete;
		}
	}

	@Getter
	public final class AuthenticatedUser {
		private final boolean superUser;
		private final int id;
		private final String name;
		private final List<College> colleges;
		private final Map<ErpModule, Permission> accessMap;

		private AuthenticatedUser(boolean superUser, int id, String name, List<College> colleges,
				Map<ErpModule, Permission> accessMap) {
			this.superUser = superUser;
			this.id = id;
			this.name = name;
			if (colleges != null) {
				this.colleges = Collections.unmodifiableList(colleges);
			} else {
				this.colleges = null;
			}
			if (accessMap != null) {
				this.accessMap = Collections.unmodifiableMap(accessMap);
			} else {
				this.accessMap = null;
			}

		}
	}

	@Data
	public static final class Credentials {
		private String username;
		private String password;
	}

	@Inject
	private DatabaseService databaseService;

	@Inject
	private AcademicService academicService;

	// =================================================================================
	// users
	// =================================================================================

	// create
	public int createUser(List<String> messages, User user) {

		int newUserId = 0;
		Sql2o sql2o = databaseService.getSql2o();
		String insertUserSql = "insert into user (name, username, passwordHash, disabled, emailId)"
				+ " values(:name, :username, :passwordHash, :disabled, :emailId)";

		String insertUserCollegeSql = "insert into user_college(userId, collegeId) values(:userId, :collegeId)";

		char[] passwordChars = user.getPassword().toCharArray();
		String passwordHash = argon2.hash(4, 16 * 1024, 1, passwordChars);

		try (Connection con = sql2o.beginTransaction()) {
			int userId = con.createQuery(insertUserSql).addParameter("name", user.getName())
					.addParameter("username", user.getUsername()).addParameter("passwordHash", passwordHash)
					.addParameter("disabled", false).addParameter("emailId", user.getEmailId()).executeUpdate()
					.getKey(Integer.class);

			Set<College> colleges = user.getColleges();
			if (colleges != null && !colleges.isEmpty()) {
				Query query = con.createQuery(insertUserCollegeSql);
				for (College college : colleges) {
					query.addParameter("userId", userId).addParameter("collegeId", college.getId()).executeUpdate();
				}
			}

			con.commit();
			newUserId = userId;
		} catch (Exception e) {
			logger.debug(e.getMessage());
			messages.add(e.getMessage());
		}
		return newUserId;
	}

	// read many
	public List<User> getAllUsers(String queryString) {
		List<User> users = null;

		if (queryString == null) {
			queryString = "";
		}

		queryString = "%" + queryString.toLowerCase() + "%";
		Sql2o sql2o = databaseService.getSql2o();

		try (Connection con = sql2o.open()) {
			String sql = "select * from user where lower(name) like :name or lower(username) like :username";
			users = con.createQuery(sql).addParameter("name", queryString).addParameter("username", queryString)
					.executeAndFetch(User.class);
			con.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return users;
	}

	public User getUserById(int id) {
		User user = null;

		Sql2o sql2o = databaseService.getSql2o();

		try (Connection con = sql2o.open()) {
			String sql = "select * from user where id = :id";
			user = con.createQuery(sql).addParameter("id", id).executeAndFetchFirst(User.class);

			if (user != null) {
				String userCollegeSql = "select collegeId from user_college where userId = :userId";
				List<Integer> collegeIds = con.createQuery(userCollegeSql).addParameter("userId", user.getId())
						.executeScalarList(Integer.class);

				Set<College> colleges = new HashSet<College>();
				String collegeSql = "select * from college where id = :id";
				for (int collegeId : collegeIds) {
					College college = con.createQuery(collegeSql).addParameter("id", collegeId)
							.executeAndFetchFirst(College.class);
					colleges.add(college);
				}
				user.setColleges(colleges);
			}

			con.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return user;
	}

	public AuthenticatedUser authenticate(List<String> messages, Credentials credentials) {
		AuthenticatedUser authUser = null;
		Sql2o sql2o = databaseService.getSql2o();

		try (Connection con = sql2o.open()) {
			String sql = "select * from user where username = :username";
			User user = con.createQuery(sql).addParameter("username", credentials.username)
					.executeAndFetchFirst(User.class);
			if (user != null) {
				char[] passwordChars = credentials.password.toCharArray();

				boolean success = argon2.verify(user.getPasswordHash(), passwordChars);

				if (success) {

					String userCollegeSql = "select collegeId from user_college where userId = :userId";
					List<Integer> collegeIds = con.createQuery(userCollegeSql).addParameter("userId", user.getId())
							.executeScalarList(Integer.class);

					List<College> colleges = new ArrayList<College>();
					String collegeSql = "select * from college where id = :id";
					for (int collegeId : collegeIds) {
						College college = con.createQuery(collegeSql).addParameter("id", collegeId)
								.executeAndFetchFirst(College.class);
						colleges.add(college);
					}

					// ==============================================================
					// hard coded - CHANGE THIS
					// ==============================================================
					Map<ErpModule, Permission> accessMap = new HashMap<>();
					accessMap.put(ErpModule.Students, new Permission(true, true, true, true));
					accessMap.put(ErpModule.Student_PersonalDetails, new Permission(true, true, true, true));
					accessMap.put(ErpModule.Student_ContactDetails, new Permission(true, true, true, true));

					authUser = new AuthenticatedUser(false, user.getId(), user.getName(), colleges, accessMap);
				}
			}
			con.close();
		} catch (Exception e) {
			logger.error(e.getMessage());
			System.out.println(e.getMessage());
			messages.add(e.getMessage());
		}

		return authUser;
	}

	public AuthenticatedUser authenticateSuperUser(List<String> messages, Credentials credentials) {
		AuthenticatedUser authUser = null;
		Sql2o sql2o = databaseService.getSql2o();

		try (Connection con = sql2o.open()) {

			// =========================================================================================
			// CHANGE THIS - hard coded for the time being, EXTERNALIZE THIS
			// =========================================================================================
			String existingName = "Super User";
//			String existingUsername = "su"; must match credentials.username
			String existingPassword = "password";
			char[] existingPasswordChars = existingPassword.toCharArray();
			String existingPasswordHash = argon2.hash(4, 16 * 1024, 1, existingPasswordChars);

			char[] passwordChars = credentials.password.toCharArray();

			boolean success = argon2.verify(existingPasswordHash, passwordChars);

			if (success) {
				List<College> colleges = academicService.getAllColleges();
				authUser = new AuthenticatedUser(true, 0, existingName, colleges, null);
			}
			con.close();
		} catch (Exception e) {
			logger.error(e.getMessage());
			System.out.println(e.getMessage());
			messages.add(e.getMessage());
		}

		return authUser;
	}

	// update
	public boolean updateUserColleges(List<String> messages, int userId, Set<College> colleges) {

		boolean success = false;

		Sql2o sql2o = databaseService.getSql2o();

		String deleteUserCollegeSql = "delete from user_college where userId = :userId";
		String insertUserCollegeSql = "insert into user_college(userId, collegeId) values(:userId, :collegeId)";

		try (Connection con = sql2o.beginTransaction()) {
			con.createQuery(deleteUserCollegeSql).addParameter("userId", userId).executeUpdate();
			if (colleges != null && !colleges.isEmpty()) {
				Query query = con.createQuery(insertUserCollegeSql);
				for (College college : colleges) {
					query.addParameter("userId", userId).addParameter("collegeId", college.getId()).executeUpdate();
				}
			}
			con.commit();
			success = true;
		} catch (Exception e) {
			logger.debug(e.getMessage());
			messages.add(e.getMessage());
		}
		return success;
	}
}
