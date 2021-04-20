package in.co.itlabs.minierp.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import in.co.itlabs.minierp.util.Gender;
import in.co.itlabs.minierp.util.Semester;
import in.co.itlabs.minierp.util.StudentStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Student {
	private int id;
	private String name;
	private String motherName;
	private String fatherName;
	private String localGuardianName;

	private LocalDate birthDate;
	private Gender gender;
	private String aadhaarNo;

	private String upseeRollNo;
	private int upseeRank;
	private String prnNo;
	private String admissionId;
	private LocalDate admissionDate;

	private BigDecimal interEnglishPercent;

	private StudentStatus studentStatus;

	private int collegeId;

	private int admissionSessionId;
	private int admissionProgramId;
	private Semester admissionSemester;

	private boolean lateralEntry;
	private boolean feeWaiver;
	private boolean kashmiriMigrant;
	private boolean pmsss;

	private boolean hostel;
	private boolean scholarship;

	private int photographMediaId;
	private int signatureMediaId;

	private int currentSessionInfoId;

	// transient
	private Session admissionSession;
	private Program admissionProgram;
	private Media photographMedia;
	private Media signatureMedia;

	private StudentSessionInfo lastestSessionInfo;
	
	// ==================================================================================
	public static final String ID = "id";
	public static final String NAME = "name";
	public static final String ADMISSION_ID = "admissionId";

	public static List<String> getFields() {
		List<String> fields = new ArrayList<String>();
		fields.add(ID);
		fields.add(ADMISSION_ID);
		fields.add(NAME);
		return fields;
	}

	public void clear() {
		id = 0;
		name = null;
		motherName = null;
		fatherName = null;
		localGuardianName = null;
		prnNo = null;
		admissionId = null;
		collegeId = 0;
		photographMediaId = 0;
		signatureMediaId = 0;

	}
}
