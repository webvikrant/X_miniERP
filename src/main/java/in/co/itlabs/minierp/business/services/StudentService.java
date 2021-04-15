package in.co.itlabs.minierp.business.services;

import java.util.List;

import javax.inject.Inject;

import org.sql2o.Connection;
import org.sql2o.Sql2o;

import com.vaadin.cdi.annotation.VaadinSessionScoped;

import in.co.itlabs.minierp.business.entities.Student;

@VaadinSessionScoped
public class StudentService {

	@Inject
	private DatabaseService databaseService;

	// read many
	public List<Student> getAllStudents() {
		List<Student> students = null;

		Sql2o sql2o = databaseService.getSql2o();
		
		String studentSql = "select * from student";

		try (Connection con = sql2o.open()) {
			students = con.createQuery(studentSql).executeAndFetch(Student.class);
			con.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return students;
	}
}
