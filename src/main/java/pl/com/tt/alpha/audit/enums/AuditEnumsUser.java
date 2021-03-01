package pl.com.tt.alpha.audit.enums;

import pl.com.tt.alpha.common.IAudit;

public enum AuditEnumsUser implements IAudit {
	UPDATE_AUTHORITIES, UPDATE_USER,
	CREATE_USER, DELETE_USER, CHANGE_PASSWORD,
	ACTIVATE_USER_ACCOUNT, DEACTIVATE_USER;

	@Override
	public String getType() {
		return "USER";
	}
}
