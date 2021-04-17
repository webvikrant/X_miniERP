package in.co.itlabs.minierp.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Exam {

	public enum Level {
		HIGH_SCHOOL, INTERMEDIATE, DIPLOMA, DEGREE
	}

	private int id;
	private Level level;
	private String name;
}
