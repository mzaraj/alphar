package pl.com.tt.alpha.user.vm;

import pl.com.tt.alpha.common.ViewModel;
import pl.com.tt.alpha.configuration.Constants;

import javax.validation.constraints.*;
import java.time.Instant;
import java.util.Set;

public class UserEventVm implements ViewModel {

    private Long id;

    private String login;

    private String email;

    private String city;

    private Instant bday;

    private Set<String> authorities;
}
