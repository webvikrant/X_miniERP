package in.co.itlabs.minierp.entities;

import java.time.LocalDate;
import java.util.List;

import in.co.itlabs.minierp.util.Gender;
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

	// foreign keys
	private int collegeId;
	private int sessionId;

	// transient
	private College college;
	private Session session;

	// ==================================================================================
	public static final String ID = "id";
	public static final String NAME = "name";
	public static final String ADMISSION_ID = "admissionId";

	public static List<String> getFields() {
		return List.of(ID, NAME, ADMISSION_ID);
	}

	public void clear() {
		id = 0;
		name = null;
		motherName = null;
		fatherName = null;
		guardianName = null;
		prnNo = null;
		admissionId = null;
		collegeId = 0;
		sessionId = 0;
	}
}
