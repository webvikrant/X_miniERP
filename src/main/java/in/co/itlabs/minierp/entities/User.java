package in.co.itlabs.minierp.entities;

import java.util.Map;

import in.co.itlabs.minierp.util.ErpModule;
import in.co.itlabs.minierp.util.Permission;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class User {
	private int id;
	private String username;
	private String hashedPassword;
	private Map<ErpModule,Permission> accessMap;
}
