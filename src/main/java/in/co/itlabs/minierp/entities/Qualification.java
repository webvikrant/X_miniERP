package in.co.itlabs.minierp.entities;

import java.math.BigDecimal;

import in.co.itlabs.minierp.entities.Exam.Level;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Qualification {

	private int id;
	private int studentId;

	private Level level;
	private int examId;

	private int boardId;
	private int schoolId;

	private int year;
	private String rollNo;

	private int maximumMarks;
	private int obtainedMarks;
	private BigDecimal percentMarks;

	// transient
	private Exam exam;
	private Board board;
	private School school;

	public void clear() {
		id = 0;
		studentId = 0;
		level = null;
		examId = 0;
		boardId = 0;
		schoolId = 0;
		year = 0;
		rollNo = null;
		maximumMarks = 0;
		obtainedMarks = 0;
		percentMarks = null;
		exam = null;
		board = null;
		school = null;
	}

}
