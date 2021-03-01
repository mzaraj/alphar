package pl.com.tt.alpha.user.vm;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class ResetPasswordDetailsVM {

	@Email
	@Size(min = 5, max = 254)
	private String email;

}
