package in.co.itlabs.minierp.services;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.sql2o.Connection;
import org.sql2o.Sql2o;

import in.co.itlabs.minierp.entities.Address;
import in.co.itlabs.minierp.entities.Contact;
import in.co.itlabs.minierp.entities.District;
import in.co.itlabs.minierp.entities.State;

@ApplicationScoped
public class ContactService {

	@Inject
	private DatabaseService databaseService;

	// =====================================================================================
	// contact
	// =====================================================================================

	// create
	public int createContact(List<String> messages, Contact contact) {
		int id = 0;
		Sql2o sql2o = databaseService.getSql2o();
		String sql = "insert into contact (studentId, type, mobileNo, whatsappNo, emailId)"
				+ " values(:studentId, :type, :mobileNo, :whatsappNo, :emailId)";

		try (Connection con = sql2o.open()) {
			id = con.createQuery(sql).addParameter("studentId", contact.getStudentId())
					.addParameter("type", contact.getType()).addParameter("mobileNo", contact.getMobileNo())
					.addParameter("whatsappNo", contact.getWhatsappNo()).addParameter("emailId", contact.getEmailId())
					.executeUpdate().getKey(Integer.class);

			con.close();
		} catch (Exception e) {
			messages.add(e.getMessage());
		}
		return id;
	}

	public List<Contact> getAllContacts(int studentId) {
		List<Contact> contacts = null;

		Sql2o sql2o = databaseService.getSql2o();
		String sql = "select * from contact where studentId = :studentId";

		try (Connection con = sql2o.open()) {
			contacts = con.createQuery(sql).addParameter("studentId", studentId).executeAndFetch(Contact.class);
			con.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return contacts;
	}

	// update
	public boolean updateContact(List<String> messages, Contact contact) {
		boolean success = false;
		Sql2o sql2o = databaseService.getSql2o();
		String sql = "update contact set mobileNo = :mobileNo, whatsappNo = :whatsappNo, emailId = :emailId"
				+ " where id = :id";

		try (Connection con = sql2o.open()) {
			con.createQuery(sql).addParameter("id", contact.getId()).addParameter("mobileNo", contact.getMobileNo())
					.addParameter("whatsappNo", contact.getWhatsappNo()).addParameter("emailId", contact.getEmailId())
					.executeUpdate();

			success = true;
			con.close();
		} catch (Exception e) {
			messages.add(e.getMessage());
		}
		return success;
	}

	// =====================================================================================
	// address
	// =====================================================================================

	// create
	public int createAddress(List<String> messages, Address address) {
		int id = 0;
		Sql2o sql2o = databaseService.getSql2o();
		String sql = "insert into address (studentId, type, districtId, description, pincode)"
				+ " values(:studentId, :type, :mobileNo, :whatsappNo, :emailId)";

		try (Connection con = sql2o.open()) {
			id = con.createQuery(sql).addParameter("studentId", address.getStudentId())
					.addParameter("type", address.getType()).addParameter("districtId", address.getDistrictId())
					.addParameter("description", address.getDescription()).addParameter("pincode", address.getPincode())
					.executeUpdate().getKey(Integer.class);

			con.close();
		} catch (Exception e) {
			messages.add(e.getMessage());
		}
		return id;
	}

	public List<Address> getAllAddresses(int studentId) {
		List<Address> addresses = null;

		Sql2o sql2o = databaseService.getSql2o();
		String sql = "select * from address where studentId = :studentId";

		try (Connection con = sql2o.open()) {
			addresses = con.createQuery(sql).addParameter("studentId", studentId).executeAndFetch(Address.class);
			con.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return addresses;
	}

	// update
	public boolean updateAddress(List<String> messages, Address address) {
		boolean success = false;
		Sql2o sql2o = databaseService.getSql2o();
		String sql = "update address set districtId = :districtId, description = :description, pincode = :pincode"
				+ " where id = :id";

		try (Connection con = sql2o.open()) {
			con.createQuery(sql).addParameter("id", address.getId()).addParameter("districtId", address.getDistrictId())
					.addParameter("description", address.getDescription()).addParameter("pincode", address.getPincode())
					.executeUpdate();

			success = true;
			con.close();
		} catch (Exception e) {
			messages.add(e.getMessage());
		}
		return success;
	}

	
	// states
		public List<State> getAllStates() {
			List<State> states = null;

			Sql2o sql2o = databaseService.getSql2o();
			String sql = "select * from state order by name";

			try (Connection con = sql2o.open()) {
				states = con.createQuery(sql).executeAndFetch(State.class);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			return states;
		}
		
		public List<District> getDistricts(int stateId) {
			List<District> districts = null;

			Sql2o sql2o = databaseService.getSql2o();
			String sql = "select * from district where stateId = :stateId order by name";

			try (Connection con = sql2o.open()) {
				districts = con.createQuery(sql).addParameter("stateId", stateId).executeAndFetch(District.class);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			return districts;
		}

		public District getDistrict(int id) {
			District district = null;

			Sql2o sql2o = databaseService.getSql2o();
			String sql = "select * from district where id = :id";
			String stateSql = "select * from state where id = :id";
			
			try (Connection con = sql2o.open()) {
				district = con.createQuery(sql).addParameter("id", id).executeAndFetchFirst(District.class);
				State state = con.createQuery(stateSql).addParameter("id", district.getStateId()).executeAndFetchFirst(State.class);
				district.setState(state);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			return district;
		}
}