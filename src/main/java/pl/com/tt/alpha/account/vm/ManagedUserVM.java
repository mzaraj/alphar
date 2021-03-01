package pl.com.tt.alpha.account.vm;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import pl.com.tt.alpha.user.vm.UserVM;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@ToString(exclude = "password")
@EqualsAndHashCode(exclude = "password", callSuper = true)
public class ManagedUserVM extends UserVM {

	@NotBlank
	private String password;

}
