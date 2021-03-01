package pl.com.tt.alpha.audit.resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import pl.com.tt.alpha.user.domain.UserEntity;
import pl.com.tt.alpha.user.service.UserService;
import pl.com.tt.alpha.user.vm.UserVM;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AuditResourceTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	UserService userService;

	@Test
	@WithUserDetails("admin")
	@Transactional
	public void shouldAddAuditCreateUserToAuditTable() throws Exception {

		UserVM userVmTest = createUserVm();

		UserEntity userEntity = userService.createUser(userVmTest);

		mockMvc.perform(get("/management/audits/entity/{entity}/entityId/{entityId}",
			"USER",
			userEntity.getId()))
			   .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			   .andExpect(jsonPath("$.[*].auditEventType", hasItem("CREATE_USER"))).andExpect(status().isOk()).andDo(print());
	}

	@Test
	@WithUserDetails("admin")
	@Transactional
	public void shouldUpgradeUserAndGenerateChangeAudit() throws Exception {
		UserVM userVmTest = createUserVm();

		UserEntity userEntity = userService.createUser(userVmTest);

		UserVM userVmTest2 = createUserVm();
		userVmTest2.setLogin("Janusz12");
		userVmTest2.setId(userEntity.getId());

		userService.updateUser(userVmTest2);

		mockMvc.perform(get("/management/audits/entity/{entity}/entityId/{entityId}",
			"USER", userVmTest2.getId()))
			   .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			   .andExpect(jsonPath("$.[*].auditEventType", hasItem("UPDATE_USER"))).andExpect(status().isOk()).andDo(print());
	}

	@Test
	@WithUserDetails("admin")
	@Transactional
	public void shouldGetAllAuditByUserId() throws Exception {

		UserVM userVmTest = createUserVm();

		UserEntity userEntity = userService.createUser(userVmTest);

		UserVM userVmTest2 = createUserVm();
		userVmTest2.setLogin("Janusz12");
		userVmTest2.setId(userEntity.getId());

		userService.updateUser(userVmTest2);

		mockMvc.perform(get("/management/audits/entity/{entity}/entityId/{entityId}","USER", userVmTest2.getId())).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			   .andExpect(jsonPath("$", hasSize(2)));
	}

	private UserVM createUserVm(){
		Set<String> authorities = new HashSet<>();
		authorities.add("user");

		return UserVM.builder().login("Janusz1234").firstName("Janek").lastName("Kozlowski").email("janek12@gmail.com").active(true).langKey("pl")
								  .authorities(authorities).build();
	}

}
