package in.co.itlabs.minierp.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Exam {

	public enum Level {
		CLASS_10, CLASS_12, DIPLOMA, DEGREE
	}

	private int id;
	private Level level;
	private String name;
}
