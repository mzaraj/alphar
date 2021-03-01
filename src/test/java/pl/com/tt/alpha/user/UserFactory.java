package pl.com.tt.alpha.user;

import org.apache.commons.lang3.RandomStringUtils;
import pl.com.tt.alpha.security.AuthoritiesConstants;
import pl.com.tt.alpha.user.domain.AuthorityEntity;
import pl.com.tt.alpha.user.domain.UserEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class UserFactory {

	public static UserEntity createUser() {
		UserEntity userEntity = new UserEntity();
		userEntity.setEmail(RandomStringUtils.randomAlphanumeric(10) + "@localhost");
		userEntity.setLogin(RandomStringUtils.randomAlphanumeric(15));
		userEntity.setActive(true);
		userEntity.setPassword("$2y$12$E1exCtKqnrL036DtXxBJdeHgeRkhK.3xeAf4IwLcxRJhqT9dM4Si2");
		userEntity.setLangKey("PL");
		userEntity.setFirstName("usertest");
		Set<AuthorityEntity> authorities = new HashSet<>();
		AuthorityEntity authorityEntity = new AuthorityEntity();
		authorityEntity.setName(AuthoritiesConstants.USER);
		authorities.add(authorityEntity);
		userEntity.setAuthorities(authorities);
		return userEntity;
	}

	public static List<UserEntity> createDefaultUserAndAdmin() {
		List<UserEntity> userEntities = new ArrayList<>();
		UserEntity userEntity = createUser();
		userEntity.setLogin("testuser");
		userEntities.add(userEntity);
		userEntity = createAdmin();
		userEntity.setLogin("testadmin");
		userEntities.add(userEntity);
		return userEntities;
	}

	public static UserEntity createAdmin(){
		UserEntity userEntity = new UserEntity();
		userEntity.setEmail(RandomStringUtils.randomAlphanumeric(10) + "@localhost");
		userEntity.setLogin(RandomStringUtils.randomAlphanumeric(15));
		userEntity.setActive(true);
		userEntity.setPassword("$2y$12$e.27ZvxznDD5pOys2YhGw.jhZah6H.wt6wIFExD/wbV4cRJhgA/HC");
		userEntity.setLangKey("PL");
		userEntity.setFirstName("admin");
		userEntity.setLastName("test");
		Set<AuthorityEntity> authorities = new HashSet<>();
		AuthorityEntity authorityEntity = new AuthorityEntity();
		authorityEntity.setName(AuthoritiesConstants.ADMIN);
		authorities.add(authorityEntity);
		userEntity.setAuthorities(authorities);
		return userEntity;
	}

	public static List<UserEntity> createUsers() {
		List<UserEntity> userEntities = new ArrayList<>();
		userEntities.add(createUser());
		userEntities.add(createUser());
		userEntities.add(createAdmin());
		return userEntities;
	}
}
