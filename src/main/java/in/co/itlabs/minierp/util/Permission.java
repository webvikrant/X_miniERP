package in.co.itlabs.minierp.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Permission {
	private boolean canCreate;
	private boolean canRead;
	private boolean canUpdate;
	private boolean canDelete;
}
