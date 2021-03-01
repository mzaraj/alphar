package pl.com.tt.alpha.user.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pl.com.tt.alpha.events.repository.EventMembersRepository;
import pl.com.tt.alpha.events.repository.EventRepository;
import pl.com.tt.alpha.security.SecurityUtils;
import pl.com.tt.alpha.user.WithMockTestAdmin;
import pl.com.tt.alpha.user.WithMockTestUser;
import pl.com.tt.alpha.user.domain.UserEntity;
import pl.com.tt.alpha.user.repository.UserRepository;
import pl.com.tt.alpha.user.service.UserService;
import pl.com.tt.alpha.user.vm.UserAuthoritiesVM;
import pl.com.tt.alpha.user.vm.UserAvatarVM;
import pl.com.tt.alpha.user.vm.UserVM;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static pl.com.tt.alpha.user.UserFactory.createDefaultUserAndAdmin;
import static pl.com.tt.alpha.user.UserFactory.createUser;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserResourceTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper json;

	@Autowired
	private UserRepository userRepository;

	@Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventMembersRepository eventMembersRepository;

	@Before
	public void beforeTest() {
		userRepository.saveAll(createDefaultUserAndAdmin());
	}

	@After
	public void afterTest() {

		userRepository.deleteAll();
	}

	@Test
	@WithMockTestAdmin
	public void shouldCreateUser() throws Exception {
		Set<String> authorities = new HashSet<>();
		authorities.add("ROLE_USER");
		UserVM userVm = new UserVM();
		userVm.setLogin("User1234");
		userVm.setFirstName(RandomStringUtils.randomAlphanumeric(10));
		userVm.setLastName(RandomStringUtils.randomAlphanumeric(10));
		userVm.setEmail(RandomStringUtils.randomAlphanumeric(10) + "@localhost");
		userVm.setActive(true);
		userVm.setLangKey("PL");
		userVm.setAuthorities(authorities);
		mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(json.writeValueAsString(userVm))).andDo(print())
			   .andExpect(status().isCreated());
	}

	@Test
	@WithMockTestAdmin
	public void shouldDeleteUser() throws Exception {
		UserEntity userEntity = userRepository.findOneByLogin("testadmin").get();
		userRepository.saveAndFlush(userEntity);
		mockMvc.perform(delete("/api/users/{login}", userEntity.getLogin())).andExpect(status().isOk()).andExpect(authenticated().withRoles("ADMIN"))
			   .andDo(print());
	}

	@Test
	@WithMockTestAdmin
	public void shouldGetUser() throws Exception {
		UserEntity userEntity = userRepository.findOneByLogin("testadmin").get();
		userRepository.save(userEntity);
		mockMvc.perform(get("/api/users/{user}", userEntity.getLogin())).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
			   .andExpect(jsonPath("$.id").value(userEntity.getId())).andExpect(jsonPath("$.login").value(userEntity.getLogin()))
			   .andExpect(jsonPath("$.active").exists()).andExpect(status().isOk()).andDo(print());
	}

	@Test
	@WithMockTestAdmin
	public void shouldGetUserAuthorities() throws Exception {
		mockMvc.perform(get("/api/users/{user}/authorities", "testuser")).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
			   .andExpect(jsonPath("$.authorities").value("ROLE_USER")).andExpect(status().isOk()).andDo(print());
	}

	@Test
	@WithMockTestAdmin
	public void shouldAdminSetUserAuthorities() throws Exception {
		UserAuthoritiesVM userAuthoritiesVM = new UserAuthoritiesVM();
		Set<String> authorityEntities = new HashSet<>();
		authorityEntities.add("ROLE_ADMIN");
		userAuthoritiesVM.setAuthorities(authorityEntities);
		mockMvc.perform(put("/api/users/{user}/authorities", "testadmin").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .content(json.writeValueAsString(userAuthoritiesVM))).andDo(print()).andExpect(status().isOk());
	}

	@Test
	@WithMockTestUser
	public void shouldNotUserSetUsersAuthorities() throws Exception {
		UserAuthoritiesVM userAuthoritiesVM = new UserAuthoritiesVM();
		Set<String> authorityEntities = new HashSet<>();
		authorityEntities.add("ROLE_ADMIN");
		userAuthoritiesVM.setAuthorities(authorityEntities);
		mockMvc.perform(
			put("/api/users/{user}/authorities", "testuser").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(json.writeValueAsString(userAuthoritiesVM)))
			   .andDo(print()).andExpect(status().isForbidden());
	}

	@Test
	@WithMockTestAdmin
	public void shouldGetUsersFiltered() throws Exception {
		final String filterByLogin = "/api/users/filtered?login=";
		mockMvc.perform(get(filterByLogin + "use")).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			   .andExpect(jsonPath("$.[*].login", hasItem("testuser"))).andExpect(status().isOk()).andDo(print());
	}

	@Test
	@WithMockTestAdmin
	@Transactional
	public void shouldUpdateUserAndAddAuditUpgradeUser() throws Exception {
		Set<String> authorities = new HashSet<>();
		authorities.add("user");

		UserEntity userEntity = createUser();
		userEntity = userRepository.save(userEntity);

		UserVM userVM = new UserVM();
		userVM.setId(userEntity.getId());
		userVM.setLogin("Janusz128");
		userVM.setFirstName(RandomStringUtils.randomAlphanumeric(10));
		userVM.setLastName(RandomStringUtils.randomAlphanumeric(10));
		userVM.setEmail(RandomStringUtils.randomAlphanumeric(10) + "@gmail.com");
		userVM.setActive(true);
		userVM.setLangKey("pl");
		userVM.setAuthorities(authorities);

		mockMvc.perform(put("/api/users").contentType(MediaType.APPLICATION_JSON_UTF8).content(json.writeValueAsString(userVM)))
			   .andExpect(jsonPath("$.login").value("Janusz128")).andExpect(status().isOk()).andDo(print());
	}
}

