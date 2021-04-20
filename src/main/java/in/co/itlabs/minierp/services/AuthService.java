package in.co.itlabs.minierp.services;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import in.co.itlabs.minierp.entities.College;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

	@Data
	@NoArgsConstructor
	private class User {
		private int id;
		private String name;
		private String username;
		private String passwordHash;
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
	public int createUser(List<String> messages, String name, String username, String password) {

		int newUserId = 0;
		Sql2o sql2o = databaseService.getSql2o();
		String sql = "insert into user (name, username, passwordHash)" + " values(:name, :username, :passwordHash)";

		char[] passwordChars = password.toCharArray();
		String passwordHash = argon2.hash(4, 16 * 1024, 1, passwordChars);

		try (Connection con = sql2o.open()) {
			int userId = con.createQuery(sql).addParameter("name", name).addParameter("username", username)
					.addParameter("passwordHash", passwordHash).executeUpdate().getKey(Integer.class);

			con.close();
			newUserId = userId;
		} catch (Exception e) {
			logger.debug(e.getMessage());
			messages.add(e.getMessage());
		}
		return newUserId;
	}

	// read many
	public List<User> getAllUsers() {
		List<User> users = null;

		Sql2o sql2o = databaseService.getSql2o();

		try (Connection con = sql2o.open()) {
			String sql = "select * from user";
			users = con.createQuery(sql).executeAndFetch(User.class);
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

				boolean success = argon2.verify(user.passwordHash, passwordChars);

				if (success) {

					// ========================================================================================
					// CHANGE THIS -- hard coded for the time being -- CHANGE THIS
					// ========================================================================================

					List<College> colleges = academicService.getAllColleges();

					Map<ErpModule, Permission> accessMap = new HashMap<>();
					accessMap.put(ErpModule.Students, new Permission(true, true, true, true));
					accessMap.put(ErpModule.Student_PersonalDetails, new Permission(true, true, true, true));
					accessMap.put(ErpModule.Student_ContactDetails, new Permission(true, true, true, true));

					authUser = new AuthenticatedUser(false, user.id, user.name, colleges, accessMap);
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

}
