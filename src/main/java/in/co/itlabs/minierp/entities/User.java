package in.co.itlabs.minierp.entities;

import java.util.Set;

import in.co.itlabs.minierp.util.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class User {

	private int id;
	private String name;
	private String username;
	private String password;
	private String passwordHash;
	private boolean disabled;
	private String emailId;
	private Entity entity;
	private int entityId;

	// transient
	private String entityName;
	private Set<College> colleges;

	public String getCollegesString() {
		String collegesString = "";
		if (colleges != null && !colleges.isEmpty()) {
			int count = 0;
			for (College college : colleges) {
				count++;
				if (count == 1) {
					collegesString = college.getCode();
				} else {
					collegesString = collegesString + ", " + college.getCode();
				}
			}
		}

		return collegesString;
	}

}