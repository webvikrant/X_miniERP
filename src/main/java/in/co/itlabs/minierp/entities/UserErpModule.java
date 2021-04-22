package in.co.itlabs.minierp.entities;

import in.co.itlabs.minierp.services.AuthService.ErpModule;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserErpModule {
	private int id;
	private int userId;
	private ErpModule erpModule;
	private boolean canCreate;
	private boolean canRead;
	private boolean canUpdate;
	private boolean canDelete;
}
