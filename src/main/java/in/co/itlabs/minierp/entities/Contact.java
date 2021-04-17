package in.co.itlabs.minierp.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Contact {

	public enum Type {
		STUDENT, MOTHER, FATHER, LOCAL_GUARDIAN
	}

	private int id;
	private int studentId;
	private Type type;
	private String mobileNo;
	private String whatsappNo;
	private String emailId;

	// transient
	private Student student;

	public String getName() {
		String name = "";
		if (student != null) {

			switch (type) {
			case STUDENT:
				if (student.getName() != null) {
					name = student.getName();
				}
				break;

			case MOTHER:
				if (student.getMotherName() != null) {
					name = student.getMotherName();
				}
				break;

			case FATHER:
				if (student.getFatherName() != null) {
					name = student.getFatherName();
				}
				break;

			case LOCAL_GUARDIAN:
				if (student.getLocalGuardianName() != null) {
					name = student.getLocalGuardianName();
				}
				break;

			default:
				break;
			}
		}

		return name;
	}
}
