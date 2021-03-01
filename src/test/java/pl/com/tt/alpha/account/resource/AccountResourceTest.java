package pl.com.tt.alpha.account.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import pl.com.tt.alpha.account.service.AccountService;
import pl.com.tt.alpha.account.vm.KeyAndPasswordVM;
import pl.com.tt.alpha.account.vm.PasswordChangeVM;
import pl.com.tt.alpha.authentication.vm.LoginVM;
//import pl.com.tt.alpha.captcha.domain.Captcha;
//import pl.com.tt.alpha.captcha.repository.CaptchaRepository;
//import pl.com.tt.alpha.captcha.service.CaptchaService;
import pl.com.tt.alpha.configuration.ApplicationProperties;
import pl.com.tt.alpha.mail.MailService;
import pl.com.tt.alpha.security.SecurityUtils;
import pl.com.tt.alpha.user.WithMockTestAdmin;
import pl.com.tt.alpha.user.WithMockTestUser;
import pl.com.tt.alpha.user.domain.UserEntity;
import pl.com.tt.alpha.user.mapper.UserMapper;
import pl.com.tt.alpha.user.repository.UserRepository;
import pl.com.tt.alpha.user.service.UserService;
import pl.com.tt.alpha.user.vm.ResetPasswordDetailsVM;
import pl.com.tt.alpha.user.vm.UserVM;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.com.tt.alpha.user.UserFactory.createDefaultUserAndAdmin;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AccountResourceTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper json;

	@Autowired
	private ApplicationProperties applicationProperties;

	@Autowired
	private AccountService accountService;

	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private MailService mailService;

	@Mock
	private MailService mailMockService;

	@Autowired
	private UserMapper userMapper;

//	@Autowired
//	private CaptchaService captchaService;
//
//	@Autowired
//	private CaptchaRepository captchaRepository;

	@Before
	public void beforeTest() {
		userRepository.saveAll(createDefaultUserAndAdmin());
	}

	@After
	public void afterTest() {
		userRepository.deleteAll();
	}

	@Test
	@Transactional
	@WithMockTestAdmin
	public void shouldActivateAccount() throws Exception {
		UserVM userVM = new UserVM();
		Set<String> authorities = new HashSet<>();
		authorities.add("ROLE_USER");
		userVM.setAuthorities(authorities);
		userVM.setEmail(RandomStringUtils.randomAlphanumeric(10) + "@localhost");
		userVM.setFirstName(RandomStringUtils.randomAlphanumeric(10));
		userVM.setLastName(RandomStringUtils.randomAlphanumeric(10));
		userVM.setLogin("user1234");
		userVM.setLangKey("pl");
		userService.createUser(userVM);
		UserEntity user = userRepository.findOneByLogin("user1234").get();
		KeyAndPasswordVM keyAndPasswordVM = new KeyAndPasswordVM();
		keyAndPasswordVM.setKey(user.getActivationKey());
		keyAndPasswordVM.setNewPassword("Admin1.");
		assertFalse(user.isActive());
		assertNotNull(user.getActivationKey());
		mockMvc.perform(post("/api/activate")
			.contentType(MediaType.APPLICATION_JSON)
			.content(json.writeValueAsString(keyAndPasswordVM)))
			   .andExpect(status().isOk())
			   .andDo(print());
		user = userRepository.findOneByLogin("user1234").get();
		assertTrue(user.isActive());
		assertNull(user.getActivationKey());
	}

	@Test
	@Transactional
	@WithMockTestAdmin
	public void shouldDeactivateAccount() throws Exception {
		UserVM userVM = new UserVM();
		Set<String> authorities = new HashSet<>();
		authorities.add("ROLE_USER");
		userVM.setAuthorities(authorities);
		userVM.setEmail(RandomStringUtils.randomAlphanumeric(10) + "@localhost");
		userVM.setFirstName(RandomStringUtils.randomAlphanumeric(10));
		userVM.setLastName(RandomStringUtils.randomAlphanumeric(10));
		userVM.setLogin("user1234");
		userVM.setLangKey("pl");
		userService.createUser(userVM);
		UserEntity user = userRepository.findOneByLogin(userVM.getLogin()).get();
		KeyAndPasswordVM keyAndPasswordVM = new KeyAndPasswordVM();
		keyAndPasswordVM.setKey(user.getActivationKey());
		keyAndPasswordVM.setNewPassword("Admin1.");
		accountService.activateRegistration(keyAndPasswordVM);
		assertTrue(SecurityUtils.isAuthenticated());
		mockMvc.perform(get("/api/deactivate/{login}", userVM.getLogin()))
			   .andExpect(status().isOk())
			   .andDo(print());
		assertFalse(SecurityUtils.isAuthenticated());
	}

	@Test
	@WithMockTestUser
	@Transactional
	public void shouldNotAllowToSignInWhenUserNotActivated() throws Exception {
		UserVM userVM = new UserVM();
		Set<String> authorities = new HashSet<>();
		authorities.add("USER_ROLE");
		userVM.setLogin("jkowalski");
		userVM.setEmail("jkowalski@email.pl");
		userVM.setFirstName("Jan");
		userVM.setLastName("Kowalski");
		userVM.setAuthorities(authorities);
		userService.createUser(userVM);
		UserEntity user = userService.getUserWithAuthoritiesByLogin("jkowalski").get();
		mockMvc.perform(get("/api/activate").param("key", "")).andDo(print()).andExpect(status().isNotFound());
		assertNotNull(user.getActivationKey());
		assertFalse(user.isActive());
	}

	private void authFailure() throws Exception {
		LoginVM badCredentials = new LoginVM();
		badCredentials.setUsername("admin");
		badCredentials.setPassword("Password123");

		for (int i = 0; i < applicationProperties.getLoginAttempts() - 1; i++) {
			mockMvc.perform(post("/api/authenticate")
				.contentType(MediaType.APPLICATION_JSON)
				.content(json.writeValueAsString(badCredentials)))
				   .andExpect(status().isForbidden())
				   .andExpect(jsonPath("$.description").isEmpty());
		}
	}

	private void authFailureCaptchaRequired() throws Exception {
		LoginVM badCredentials = new LoginVM();
		badCredentials.setUsername("admin");
		badCredentials.setPassword("Password123");

		mockMvc.perform(post("/api/authenticate")
			.contentType(MediaType.APPLICATION_JSON)
			.content(json.writeValueAsString(badCredentials)))
			   .andExpect(status().isForbidden())
			   .andExpect(jsonPath("$.description").isNotEmpty());

	}


	@Test
	@WithMockTestAdmin
	@Transactional
	public void shouldNotSendEmailResetPasswordIfValidationFailed() throws Exception {
		UserVM userVM = new UserVM();
		Set<String> authorities = new HashSet<>();
		authorities.add("USER_ROLE");
		userVM.setLogin("jkowalski");
		userVM.setEmail("jkowalski@email.pl");
		userVM.setFirstName("Jan");
		userVM.setLastName("Kowalski");
		userVM.setLangKey("pl");
		userVM.setAuthorities(authorities);
		userService.createUser(userVM);
		ResetPasswordDetailsVM resetPasswordDetails = new ResetPasswordDetailsVM();
		resetPasswordDetails.setEmail("jkowalski@email.pl");
		mockMvc.perform(post("/api/account/reset-password/init").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(resetPasswordDetails)))
			   .andDo(print()).andExpect(status().isBadRequest());
		verify(mailMockService, times(0)).sendPasswordResetMail(Mockito.any());
	}

	@Test
	@WithMockTestUser
	@Transactional
	public void shouldNotChangeUserPasswordIfCurrentPasswordIsIncorrect() throws Exception {
		PasswordChangeVM passwordChange = new PasswordChangeVM();
		passwordChange.setCurrentPassword("badPassword");
		passwordChange.setNewPassword("user123");
		mockMvc.perform(post("/api/account/change-password").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(passwordChange)))
			   .andDo(print()).andExpect(status().isBadRequest());
	}

	@Test
	@WithMockTestUser
	@Transactional
	public void shouldNotChangeUserPasswordIfNewPasswordHasBadLength() throws Exception {
		PasswordChangeVM passwordChange = new PasswordChangeVM();
		passwordChange.setCurrentPassword("user");
		passwordChange.setNewPassword("us");
		mockMvc.perform(post("/api/account/change-password").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(passwordChange)))
			   .andDo(print()).andExpect(status().isBadRequest());
		assertThat(passwordChange.getNewPassword().length()).isLessThan(4);
		passwordChange.setNewPassword("eu ultrices vitae auctor eu augue ut lectus arcu bibendum at varius vel pharetra vel "
			+ "turpis nunc eget lorem dolor sed viverra ipsum nunc aliquet bibendum enim facilisis gravida neque");
		mockMvc.perform(post("/api/account/change-password").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(passwordChange)))
			   .andDo(print()).andExpect(status().isBadRequest());
		assertThat(passwordChange.getNewPassword().length()).isGreaterThan(100);
	}

	@Test
	@WithMockTestUser
	@Transactional
	public void shouldLogoutUser() throws Exception {
		assertTrue(SecurityUtils.isAuthenticated());
		assertTrue(SecurityUtils.getCurrentUserLogin().isPresent());
		assertTrue(SecurityUtils.getCurrentUserJWT().isPresent());
		mockMvc.perform(get("/api/logout")).andExpect(status().isOk()).andDo(print());
		assertFalse(SecurityUtils.isAuthenticated());
		assertFalse(SecurityUtils.getCurrentUserLogin().isPresent());
		assertFalse(SecurityUtils.getCurrentUserJWT().isPresent());
	}

	@Test
	@WithUserDetails("admin")
	@Transactional
	public void shouldChangePassword() throws Exception{
		PasswordChangeVM passwordChangeVM = new PasswordChangeVM();
		passwordChangeVM.setCurrentPassword("admin");
		passwordChangeVM.setNewPassword("Adminadmin=123");
		mockMvc.perform(post("/api/account/change-password")
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.content(json.writeValueAsString(passwordChangeVM)))
			   .andExpect(status().isOk())
			   .andDo(print());
	}

	@Test
	@WithMockTestAdmin
	@Transactional
	public void shouldNotChangePasswordWithoutSpecialChars() throws Exception{
		PasswordChangeVM passwordChangeVM = new PasswordChangeVM();
		passwordChangeVM.setCurrentPassword("Admin1#");
		passwordChangeVM.setNewPassword("Admin-123");
		mockMvc.perform(post("/api/account/change-password")
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.content(json.writeValueAsString(passwordChangeVM))).andExpect(status().isBadRequest()).andDo(print());
	}

	@Test
	@WithMockTestAdmin
	@Transactional
	public void shouldNotChangePasswordWithoutNumber() throws Exception{
		PasswordChangeVM passwordChangeVM = new PasswordChangeVM();
		passwordChangeVM.setCurrentPassword("Admin1#");
		passwordChangeVM.setNewPassword("Admin#");
		mockMvc.perform(post("/api/account/change-password")
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.content(json.writeValueAsString(passwordChangeVM))).andExpect(status().isBadRequest()).andDo(print());
	}

	@Test
	@WithMockTestAdmin
	@Transactional
	public void shouldNotChangePasswordWithoutUpperLetter() throws Exception{
		PasswordChangeVM passwordChangeVM = new PasswordChangeVM();
		passwordChangeVM.setCurrentPassword("Admin1#");
		passwordChangeVM.setNewPassword("admin1#");
		mockMvc.perform(post("/api/account/change-password")
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.content(json.writeValueAsString(passwordChangeVM))).andExpect(status().isBadRequest()).andDo(print());
	}

	@Test
	@WithMockTestAdmin
	@Transactional
	public void shouldNotChangePasswordWithoutLowerLetter() throws Exception{
		PasswordChangeVM passwordChangeVM = new PasswordChangeVM();
		passwordChangeVM.setCurrentPassword("Admin1#");
		passwordChangeVM.setNewPassword("ADMIN1#");
		mockMvc.perform(post("/api/account/change-password")
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.content(json.writeValueAsString(passwordChangeVM))).andExpect(status().isBadRequest()).andDo(print());
	}

	@Test
	@WithMockTestAdmin
	@Transactional
	public void shouldNotChangePasswordIfLengthIsTooLarge() throws Exception{
		PasswordChangeVM passwordChangeVM = new PasswordChangeVM();
		passwordChangeVM.setCurrentPassword("Admin1#");
		passwordChangeVM.setNewPassword("Admin1#qqwerefgvdefsfdcewfsdcasfaewfcasesfdcewfcdcaesfadcawefcaecdaesdcasdcasdcfdsfaesfhwefjhdsfhkeasfk");
		mockMvc.perform(post("/api/account/change-password")
		.contentType(MediaType.APPLICATION_JSON_UTF8)
			.content(json.writeValueAsString(passwordChangeVM))).andExpect(status().isBadRequest()).andDo(print());
	}

}

