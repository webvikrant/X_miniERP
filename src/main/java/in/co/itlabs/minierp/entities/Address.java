package in.co.itlabs.minierp.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Address {
	
	public enum Type {
		PERMANENT, CORRESPONDENCE, LOCAL_GUARDIAN
	}
	
	private int id;
	private int studentId;
	private Type type;
	private int districtId;
	private String description;
	private String pinCode;
}
