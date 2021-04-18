package in.co.itlabs.minierp.services;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.sql2o.Connection;
import org.sql2o.Sql2o;

import in.co.itlabs.minierp.entities.Board;
import in.co.itlabs.minierp.entities.Exam;
import in.co.itlabs.minierp.entities.Qualification;
import in.co.itlabs.minierp.entities.School;

@ApplicationScoped
public class QualificationService {

	@Inject
	private DatabaseService databaseService;
	
	// qualification

	// create
	public int createQualification(Qualification qualification) {
		int id = 0;
		Sql2o sql2o = databaseService.getSql2o();
		String sql = "insert into qualification (studentId, level, examId, boardId, schoolId, year, rollNo, maximumMarks, obtainedMarks, percentMarks)"
				+ " values(:studentId, :level, :examId, :boardId, :schoolId, :year, :rollNo, :maximumMarks, :obtainedMarks, :percentMarks)";

		try (Connection con = sql2o.open()) {
			id = con.createQuery(sql).addParameter("studentId", qualification.getStudentId())
					.addParameter("level", qualification.getLevel())
					.addParameter("examId", qualification.getExamId())
					.addParameter("boardId", qualification.getBoardId())
					.addParameter("schoolId", qualification.getSchoolId())
					.addParameter("year", qualification.getYear())
					.addParameter("rollNo", qualification.getRollNo())
					.addParameter("maximumMarks", qualification.getMaximumMarks())
					.addParameter("obtainedMarks", qualification.getObtainedMarks())
					.addParameter("percentMarks", qualification.getPercentMarks()).executeUpdate()
					.getKey(Integer.class);

			con.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return id;
	}

	public List<Qualification> getQualifications(int studentId) {
		List<Qualification> qualifications = null;

		Sql2o sql2o = databaseService.getSql2o();
		String sql = "select * from qualification where studentId = :studentId order by year";
		String examSql = "select * from exam where id = :id";
		String boardSql = "select * from board where id = :id";
		String schoolSql = "select * from school where id = :id";

		try (Connection con = sql2o.open()) {
			qualifications = con.createQuery(sql).addParameter("studentId", studentId)
					.executeAndFetch(Qualification.class);

			for (Qualification qualification : qualifications) {
				Exam exam = con.createQuery(examSql).addParameter("id", qualification.getExamId())
						.executeAndFetchFirst(Exam.class);
				qualification.setExam(exam);

				Board board = con.createQuery(boardSql).addParameter("id", qualification.getBoardId())
						.executeAndFetchFirst(Board.class);
				qualification.setBoard(board);

				School school = con.createQuery(schoolSql).addParameter("id", qualification.getSchoolId())
						.executeAndFetchFirst(School.class);
				qualification.setSchool(school);

			}
			con.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return qualifications;
	}

}
