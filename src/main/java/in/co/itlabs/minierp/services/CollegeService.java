package in.co.itlabs.minierp.services;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.sql2o.Connection;
import org.sql2o.Sql2o;

import in.co.itlabs.minierp.entities.College;
import in.co.itlabs.minierp.entities.Session;

@ApplicationScoped
public class CollegeService {

	@Inject
	private DatabaseService databaseService;

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

}
