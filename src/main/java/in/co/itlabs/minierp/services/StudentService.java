package in.co.itlabs.minierp.services;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import in.co.itlabs.minierp.entities.Student;
import in.co.itlabs.minierp.util.StudentFilterParams;
import in.co.itlabs.minierp.util.StudentFilterParams.FilterType;

@ApplicationScoped
public class StudentService {

	private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

	@Inject
	private DatabaseService databaseService;

	// create
	public int createStudent(List<String> messages, Student student) {
		int id = 0;
		Sql2o sql2o = databaseService.getSql2o();
		String sql = "insert into student (collegeId, sessionId, prnNo, admissionId, name)"
				+ " values(:collegeId, :sessionId, :prnNo, :admissionId, :name)";

		try (Connection con = sql2o.open()) {
			id = con.createQuery(sql).addParameter("collegeId", student.getCollegeId())
					.addParameter("sessionId", student.getSessionId()).addParameter("prnNo", student.getPrnNo())
					.addParameter("admissionId", student.getAdmissionId()).addParameter("name", student.getName())
					.executeUpdate().getKey(Integer.class);

			con.close();
		} catch (Exception e) {
			logger.debug(student.toString());
			messages.add(e.getMessage());
		}
		return id;
	}

	// read one
	public Student getStudentById(int id) {
		Student student = null;

		Sql2o sql2o = databaseService.getSql2o();

		String sql = "select * from student where id = :id";

		try (Connection con = sql2o.open()) {
			student = con.createQuery(sql).addParameter("id", id).executeAndFetchFirst(Student.class);
			con.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return student;
	}

	// read many
	public List<Student> getStudents(int collegeId, StudentFilterParams searchParams) {
		List<Student> students = null;

		Sql2o sql2o = databaseService.getSql2o();

		try (Connection con = sql2o.open()) {
			String studentSql = "";

			if (searchParams == null) {
				studentSql = "select * from student where collegeId = :collegeId";
				students = con.createQuery(studentSql).addParameter("collegeId", collegeId)
						.executeAndFetch(Student.class);

			} else if (searchParams.getFilterType() == FilterType.BASIC) {
				String query = searchParams.getQuery();
				if (query == null) {
					query = "";
				}

				query = "%" + query.toLowerCase() + "%";
				studentSql = "select * from student where collegeId = :collegeId and lower(name) like :name";
				students = con.createQuery(studentSql).addParameter("collegeId", collegeId).addParameter("name", query)
						.executeAndFetch(Student.class);
			}

			con.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return students;
	}

	// update personal details
	public boolean updateStudentPersonalDetails(List<String> messages, Student student) {
		System.out.println("Student: " + student.toString());
		boolean success = false;
		Sql2o sql2o = databaseService.getSql2o();
		String sql = "update student set photographMediaId = :photographMediaId,"
				+ " signatureMediaId = :signatureMediaId, name = :name, birthDate = :birthDate, gender = :gender"
				+ " where id = :id";

		try (Connection con = sql2o.open()) {
			con.createQuery(sql).addParameter("id", student.getId())
					.addParameter("photographMediaId", student.getPhotographMediaId())
					.addParameter("signatureMediaId", student.getSignatureMediaId())
					.addParameter("name", student.getName()).addParameter("birthDate", student.getBirthDate())
					.addParameter("gender", student.getGender()).executeUpdate();
			success = true;
			con.close();
		} catch (Exception e) {
			logger.debug(student.toString());
			messages.add(e.getMessage());
		}
		return success;
	}

}
