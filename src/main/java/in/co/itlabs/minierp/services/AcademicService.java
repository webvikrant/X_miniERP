package in.co.itlabs.minierp.services;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.sql2o.Connection;
import org.sql2o.Sql2o;

import in.co.itlabs.minierp.entities.Board;
import in.co.itlabs.minierp.entities.College;
import in.co.itlabs.minierp.entities.Exam;
import in.co.itlabs.minierp.entities.Exam.Level;
import in.co.itlabs.minierp.entities.Program;
import in.co.itlabs.minierp.entities.School;
import in.co.itlabs.minierp.entities.Session;

@ApplicationScoped
public class AcademicService {

	@Inject
	private DatabaseService databaseService;

	// =================================================================================
	// colleges
	// =================================================================================

	// read many
	public List<College> getAllColleges() {
		List<College> colleges = null;

		Sql2o sql2o = databaseService.getSql2o();

		try (Connection con = sql2o.open()) {
			String sql = "select * from college";
			colleges = con.createQuery(sql).executeAndFetch(College.class);
			con.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return colleges;
	}

	// =================================================================================
	// programs
	// =================================================================================

	// read many
	public List<Program> getAllPrograms() {
		List<Program> programs = null;

		Sql2o sql2o = databaseService.getSql2o();

		try (Connection con = sql2o.open()) {
			String sql = "select * from program";
			programs = con.createQuery(sql).executeAndFetch(Program.class);
			con.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return programs;
	}

	// =================================================================================
	// sessions
	// =================================================================================

	public List<Session> getAllSessions() {
		List<Session> sessions = null;

		Sql2o sql2o = databaseService.getSql2o();

		try (Connection con = sql2o.open()) {
			String sql = "select * from session";
			sessions = con.createQuery(sql).executeAndFetch(Session.class);
			con.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return sessions;
	}

	// =================================================================================
	// boards
	// =================================================================================

	public List<Board> getAllBoards() {
		List<Board> boards = null;

		Sql2o sql2o = databaseService.getSql2o();
		String sql = "select * from board order by name";

		try (Connection con = sql2o.open()) {
			boards = con.createQuery(sql).executeAndFetch(Board.class);
			con.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return boards;
	}

	// =================================================================================
	// exams
	// =================================================================================

	public List<Exam> getAllExams() {
		List<Exam> exams = null;

		Sql2o sql2o = databaseService.getSql2o();
		String sql = "select * from exam order by name";

		try (Connection con = sql2o.open()) {
			exams = con.createQuery(sql).executeAndFetch(Exam.class);
			con.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return exams;
	}

	public List<Exam> getExams(Level level) {
		List<Exam> exams = null;

		Sql2o sql2o = databaseService.getSql2o();
		String sql = "select * from exam where level = :level order by name";

		try (Connection con = sql2o.open()) {
			exams = con.createQuery(sql).addParameter("level", level).executeAndFetch(Exam.class);
			con.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return exams;
	}

	// =================================================================================
	// schools
	// =================================================================================

	public List<School> getAllSchools() {
		List<School> schools = null;

		Sql2o sql2o = databaseService.getSql2o();
		String sql = "select * from school order by name";

		try (Connection con = sql2o.open()) {
			schools = con.createQuery(sql).executeAndFetch(School.class);
			con.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return schools;
	}

}
