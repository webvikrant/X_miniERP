package in.co.itlabs.minierp.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Session {
	private int id;
	private int year;
	private boolean odd;

	public String getName() {
		int nextYear = year + 1;
		String nextYearString = "" + nextYear;
		nextYearString = nextYearString.substring(nextYearString.length() - 2, nextYearString.length());
		String name = year + "-" + nextYearString;
		if (odd) {
			name = name + " (Odd)";
		} else {
			name = name + " (Even)";
		}
		return name;
	}
}