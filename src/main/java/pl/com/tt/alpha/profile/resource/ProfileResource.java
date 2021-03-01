package pl.com.tt.alpha.profile.resource;

import com.codahale.metrics.annotation.Timed;
import io.github.jhipster.web.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.com.tt.alpha.configuration.Constants;
import pl.com.tt.alpha.profile.mapper.ProfileMapper;
import pl.com.tt.alpha.profile.vm.ProfileVM;
import pl.com.tt.alpha.user.service.UserService;

@Slf4j
@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileResource {

    private final UserService userService;
    private final ProfileMapper profileMapper;

    @GetMapping("/{login:" + Constants.LOGIN_REGEX + "}")
    @Timed
    public ResponseEntity<ProfileVM> getProfile(@PathVariable String login) {
        log.debug("REST request to get ProfileVM: {}", login);
        return ResponseUtil.wrapOrNotFound(userService.getUserWithAuthoritiesByLogin(login).map(profileMapper::toVm));
    }
}
