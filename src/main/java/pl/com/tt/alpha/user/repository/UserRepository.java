package pl.com.tt.alpha.user.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.com.tt.alpha.events.domain.EventEntity;
import pl.com.tt.alpha.user.domain.UserEntity;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> {

	String USERS_BY_LOGIN_CACHE = "usersByLogin";

	String USERS_BY_EMAIL_CACHE = "usersByEmail";

	Optional<UserEntity> findOneByActivationKey(String activationKey);

	Optional<UserEntity> findOneByResetKey(String resetKey);

	Optional<UserEntity> findOneByEmailIgnoreCase(String email);

	Optional<UserEntity> findOneByLogin(String login);

    UserEntity findOneById(String login);


	Optional<UserEntity> findOneById(Long userId);

    UserEntity getOneByLogin(String login);

	@EntityGraph(attributePaths = "authorities")
	Optional<UserEntity> findOneWithAuthoritiesById(Long id);

	@EntityGraph(attributePaths = "authorities")
	@Cacheable(cacheNames = USERS_BY_LOGIN_CACHE)
	Optional<UserEntity> findOneWithAuthoritiesByLogin(String login);

	@EntityGraph(attributePaths = "authorities")
	@Cacheable(cacheNames = USERS_BY_EMAIL_CACHE)
	Optional<UserEntity> findOneWithAuthoritiesByEmail(String email);

	Page<UserEntity> findAllByLoginNot(Pageable pageable, String login);

	@Query("from UserEntity")
	Page<UserEntity> findAll(Pageable pageable);

    UserEntity getOneById(Long userId);
}
