package in.co.itlabs.minierp.util;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor

public class StudentFilterParams {
	
	public enum FilterType {
		BASIC
	}

	private FilterType filterType;
	private String query;
}
