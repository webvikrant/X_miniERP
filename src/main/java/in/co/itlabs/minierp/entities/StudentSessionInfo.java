package in.co.itlabs.minierp.entities;

import java.math.BigDecimal;

import in.co.itlabs.minierp.util.ScholarshipStatus;
import in.co.itlabs.minierp.util.Semester;
import in.co.itlabs.minierp.util.SemesterStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StudentSessionInfo {
	private int id;
	private int studentId;
	
	private int sessionId;
	private int programId;

	private Semester semester;
	private SemesterStatus semesterStatus;

	private boolean hostel;
	private boolean scholarship;
	
	private String scholarshipFormNo;
	private BigDecimal scholarshipAmount;
	private ScholarshipStatus collegeScholarshipStatus;
	private ScholarshipStatus dswoScholarshipStatus;
}
