package pl.com.tt.alpha.account.vm;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import pl.com.tt.alpha.account.validation.Password;

@Data
@ToString(exclude = "newPassword")
@EqualsAndHashCode(exclude = "newPassword")
public class KeyAndPasswordVM {

	private String key;

	@Password
	private String newPassword;

}
