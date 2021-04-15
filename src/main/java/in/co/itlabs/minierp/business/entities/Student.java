package in.co.itlabs.minierp.business.entities;

import java.time.LocalDate;

import in.co.itlabs.minierp.business.util.Gender;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Student {
	private int id;
	private String name;
	private String motherName;
	private String fatherName;
	private String guardianName;
	
	private LocalDate birthDate;
	private Gender gender;
	private String aadhaarNo;

	private String upseeRollNo;
	private int upseeRank;
	private String prnNo;
	private String admissionId;
	private LocalDate admissionDate;
}
