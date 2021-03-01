package pl.com.tt.alpha.account.vm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.com.tt.alpha.account.validation.Password;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordChangeVM {

	private String currentPassword;

	@Password
	private String newPassword;

}
