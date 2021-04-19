package in.co.itlabs.minierp.services;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import in.co.itlabs.minierp.entities.Address;
import in.co.itlabs.minierp.entities.Contact;
import in.co.itlabs.minierp.entities.Contact.Type;
import in.co.itlabs.minierp.entities.Student;
import in.co.itlabs.minierp.entities.StudentSessionInfo;
import in.co.itlabs.minierp.util.SemesterStatus;
import in.co.itlabs.minierp.util.StudentFilterParams;
import in.co.itlabs.minierp.util.StudentFilterParams.FilterType;

@ApplicationScoped
public class StudentService {

	private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

	@Inject
	private DatabaseService databaseService;

	// create
	public int createStudent(List<String> messages, Student student) {

		int newStudentId = 0;
		Sql2o sql2o = databaseService.getSql2o();
		String sql = "insert into student (collegeId, admissionId, admissionSessionId, admissionProgramId, admissionSemester,"
				+ " lateralEntry, feeWaiver, kashmiriMigrant, pmsss, hostel, scholarship, prnNo, name)"
				+ " values(:collegeId, :admissionId, :admissionSessionId, :admissionProgramId, :admissionSemester,"
				+ " :lateralEntry, :feeWaiver, :kashmiriMigrant, :pmsss, :hostel, :scholarship, :prnNo, :name)";

		// insert multiple records into contact table and address table
		String contactSql = "insert into contact (studentId, type) values(:studentId, :type)";

		String addressSql = "insert into address (studentId, type) values(:studentId, :type)";

		try (Connection con = sql2o.beginTransaction()) {
			int studentId = con.createQuery(sql).addParameter("collegeId", student.getCollegeId())
					.addParameter("admissionId", student.getAdmissionId())
					.addParameter("admissionSessionId", student.getAdmissionSessionId())
					.addParameter("admissionProgramId", student.getAdmissionProgramId())
					.addParameter("admissionSemester", student.getAdmissionSemester())
					.addParameter("lateralEntry", student.isLateralEntry())
					.addParameter("feeWaiver", student.isFeeWaiver())
					.addParameter("kashmiriMigrant", student.isKashmiriMigrant())
					.addParameter("pmsss", student.isPmsss()).addParameter("hostel", student.isHostel())
					.addParameter("scholarship", student.isScholarship()).addParameter("prnNo", student.getPrnNo())
					.addParameter("name", student.getName()).executeUpdate().getKey(Integer.class);

			// insert 4 contacts
			con.createQuery(contactSql).addParameter("studentId", studentId).addParameter("type", Type.STUDENT)
					.executeUpdate();
			con.createQuery(contactSql).addParameter("studentId", studentId).addParameter("type", Contact.Type.MOTHER)
					.executeUpdate();
			con.createQuery(contactSql).addParameter("studentId", studentId).addParameter("type", Contact.Type.FATHER)
					.executeUpdate();
			con.createQuery(contactSql).addParameter("studentId", studentId)
					.addParameter("type", Contact.Type.LOCAL_GUARDIAN).executeUpdate();

			// insert 3 addresses
			con.createQuery(addressSql).addParameter("studentId", studentId)
					.addParameter("type", Address.Type.PERMANENT).executeUpdate();
			con.createQuery(addressSql).addParameter("studentId", studentId)
					.addParameter("type", Address.Type.CORRESPONDENCE).executeUpdate();
			con.createQuery(addressSql).addParameter("studentId", studentId)
					.addParameter("type", Address.Type.LOCAL_GUARDIAN).executeUpdate();

			// update current session info and associated logs
			StudentSessionInfo sessionInfo = new StudentSessionInfo();
			sessionInfo.setStudentId(studentId);
			sessionInfo.setSessionId(student.getAdmissionSessionId());
			sessionInfo.setLatest(true);
			sessionInfo.setProgramId(student.getAdmissionProgramId());
			sessionInfo.setSemester(student.getAdmissionSemester());
			sessionInfo.setSemesterStatus(SemesterStatus.Regular);
			sessionInfo.setHostel(student.isHostel());
			sessionInfo.setScholarship(student.isScholarship());

			insertStudentSessionInfo(messages, sessionInfo, con);
			updateLatestStudentSessionInfo(messages, studentId, con);

			con.commit();

			newStudentId = studentId;
		} catch (Exception e) {
			logger.debug(e.getMessage());
			messages.add(e.getMessage());
		}
		return newStudentId;
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

	// update personal details
	public boolean updateStudentInterMarks(List<String> messages, Student student) {
		boolean success = false;
		Sql2o sql2o = databaseService.getSql2o();
		String sql = "update student set interEnglishPercent = :interEnglishPercent where id = :id";

		try (Connection con = sql2o.open()) {
			con.createQuery(sql).addParameter("id", student.getId())
					.addParameter("interEnglishPercent", student.getInterEnglishPercent()).executeUpdate();
			success = true;
			con.close();
		} catch (Exception e) {
			logger.debug(student.toString());
			messages.add(e.getMessage());
		}
		return success;
	}

	// session info
	public List<StudentSessionInfo> getAllSessionInfos(int studentId) {
		List<StudentSessionInfo> sessionInfos = null;

		Sql2o sql2o = databaseService.getSql2o();
		String sql = "select * from student_session_info where studentId = :studentId order by sessionId desc";

		try (Connection con = sql2o.open()) {
			sessionInfos = con.createQuery(sql).addParameter("studentId", studentId)
					.executeAndFetch(StudentSessionInfo.class);
			con.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return sessionInfos;
	}

	// update personal details
	public boolean updateStudentSessionInfo(List<String> messages, StudentSessionInfo studentSessionInfo) {
		boolean success = false;
		Sql2o sql2o = databaseService.getSql2o();

		String insertInfoSql = "update student_session_info set programId = :programId, semester = :semester,"
				+ " semesterStatus = :semesterStatus, hostel = :hostel, scholarship = :scholarship,"
				+ " scholarshipFormNo = :scholarshipFormNo, scholarshipAmount = :scholarshipAmount,"
				+ " collegeScholarshipStatus = :collegeScholarshipStatus,"
				+ " dswoScholarshipStatus = :dswoScholarshipStatus where id = :id";

		try (Connection con = sql2o.open()) {

			// insert new record
			con.createQuery(insertInfoSql).addParameter("programId", studentSessionInfo.getProgramId())
					.addParameter("semester", studentSessionInfo.getSemester())
					.addParameter("semesterStatus", studentSessionInfo.getSemesterStatus())
					.addParameter("hostel", studentSessionInfo.isHostel())
					.addParameter("scholarship", studentSessionInfo.isScholarship())
					.addParameter("scholarshipFormNo", studentSessionInfo.getScholarshipFormNo())
					.addParameter("scholarshipAmount", studentSessionInfo.getScholarshipAmount())
					.addParameter("collegeScholarshipStatus", studentSessionInfo.getCollegeScholarshipStatus())
					.addParameter("dswoScholarshipStatus", studentSessionInfo.getDswoScholarshipStatus())
					.addParameter("id", studentSessionInfo.getId()).executeUpdate();

			success = true;
			con.close();
		} catch (Exception e) {
			logger.debug(studentSessionInfo.toString());
			messages.add(e.getMessage());
		}
		return success;
	}

	// delete personal details
	public boolean deleteStudentSessionInfo(List<String> messages, StudentSessionInfo studentSessionInfo) {
		boolean success = false;
		Sql2o sql2o = databaseService.getSql2o();

		String deleteSql = "delete student_session_info where id = :id";

		try (Connection con = sql2o.open()) {
			con.createQuery(deleteSql).addParameter("id", studentSessionInfo.getId()).executeUpdate();
			updateLatestStudentSessionInfo(messages, studentSessionInfo.getStudentId(), con);
			con.close();
		} catch (Exception e) {
			logger.debug(studentSessionInfo.toString());
			messages.add(e.getMessage());
		}
		return success;
	}

	// insert current session info
	private void insertStudentSessionInfo(List<String> messages, StudentSessionInfo studentSessionInfo,
			Connection con) {

		String insertInfoSql = "insert into student_session_info(studentId, sessionId, latest, programId, semester, semesterStatus, hostel, scholarship)"
				+ " values(:studentId, :sessionId, :latest, :programId, :semester, :semesterStatus, :hostel, :scholarship) ";

		// insert new record
		con.createQuery(insertInfoSql).addParameter("studentId", studentSessionInfo.getStudentId())
				.addParameter("sessionId", studentSessionInfo.getSessionId())
				.addParameter("latest", studentSessionInfo.isLatest())
				.addParameter("programId", studentSessionInfo.getProgramId())
				.addParameter("semester", studentSessionInfo.getSemester())
				.addParameter("semesterStatus", studentSessionInfo.getSemesterStatus())
				.addParameter("hostel", studentSessionInfo.isHostel())
				.addParameter("scholarship", studentSessionInfo.isScholarship()).executeUpdate();

	}

	// update current session info
	private void updateLatestStudentSessionInfo(List<String> messages, int studentId, Connection con) {

		String updateLatestFalseSql = "update student_session_info set latest = false where studentId = :studentId";
		String maxSessionIdSql = "select max(sessionId) from student_session_info where studentId = :studentId";
		String updateLatestTrueSql = "update student_session_info set latest = true where studentId = :studentId and sessionId = :sessionId";

		con.createQuery(updateLatestFalseSql).addParameter("studentId", studentId).executeUpdate();
		int maxSessionId = con.createQuery(maxSessionIdSql).addParameter("studentId", studentId)
				.executeScalar(Integer.class);
		con.createQuery(updateLatestTrueSql).addParameter("studentId", studentId)
				.addParameter("sessionId", maxSessionId).executeUpdate();

	}

}
