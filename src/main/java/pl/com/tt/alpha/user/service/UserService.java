package pl.com.tt.alpha.user.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;
import pl.com.tt.alpha.authentication.vm.LoginVM;
import pl.com.tt.alpha.user.domain.UserEntity;
import pl.com.tt.alpha.user.vm.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface UserService {

	UserEntity createUser(UserVM userVM);

	UserVM addAvatarToUser(MultipartFile avatar) throws IOException;

	void updateUser(String firstName, String lastName, String email, String langKey);

	Optional<UserVM> updateUser(UserVM userVM);

	Optional<UserAuthoritiesVM> updateUserAuthorities(String login, UserAuthoritiesVM userAuthoritiesVM);

	void deleteUser(String login);

	Page<UserVM> getAllManagedUsers(Pageable pageable);

	Optional<UserEntity> getUserWithAuthoritiesByLogin(String login);

    Long getUserId(String login);

	Optional<UserEntity> getUserWithAuthorities(Long id);

	Optional<UserEntity> getUserWithAuthorities();

	List<String> getAuthorities();

    boolean isUserWithEmailExists( String email);

	Page<UserVM> getAllUsersFiltered(Pageable pageable, UserFilterVM filter);

	Optional<UserAvatarVM> getAvatar(Long userId);


    boolean checkAdmin(String login);

    List<UserEntity> getAllAdmins();
}
