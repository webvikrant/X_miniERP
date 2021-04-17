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
}
