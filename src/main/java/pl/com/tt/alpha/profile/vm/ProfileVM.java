package pl.com.tt.alpha.profile.vm;

import lombok.Data;
import lombok.NoArgsConstructor;
import pl.com.tt.alpha.common.ViewModel;
import pl.com.tt.alpha.user.domain.AuthorityEntity;

import java.util.Set;

@Data
@NoArgsConstructor
public class ProfileVM implements ViewModel {
    private Long id;
    private String login;
    private String firstName;
    private String lastName;
    private String email;
    private Set<String> authorities;
}
