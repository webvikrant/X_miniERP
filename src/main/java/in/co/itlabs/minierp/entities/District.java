package in.co.itlabs.minierp.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class District {
	private int id;
	private int stateId;
	private String name;

	// transient
	private State state;
}
