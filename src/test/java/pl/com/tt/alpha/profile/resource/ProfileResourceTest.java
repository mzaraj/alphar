package pl.com.tt.alpha.profile.resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import pl.com.tt.alpha.events.repository.EventMembersRepository;
import pl.com.tt.alpha.events.repository.EventRepository;
import pl.com.tt.alpha.user.WithMockTestAdmin;
import pl.com.tt.alpha.user.repository.UserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static pl.com.tt.alpha.user.UserFactory.createDefaultUserAndAdmin;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProfileResourceTest {

    @Autowired
    private MockMvc mockMvc;

	@Autowired
    private UserRepository userRepository;

    @Autowired
    private EventMembersRepository eventMembersRepository;

    @Autowired
    private EventRepository eventRepository;

	@Before
	public void beforeTest() {
		userRepository.saveAll(createDefaultUserAndAdmin());
	}

	@After
	public void afterTest() {
        eventMembersRepository.deleteAll();
        eventRepository.deleteAll();
        userRepository.deleteAll();

	}

    @Test
    @WithMockTestAdmin
    public void shouldGetProfile() throws Exception {
        mockMvc.perform(get("/api/profile/{profile}", "testadmin"))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.login").exists())
            .andExpect(jsonPath("$.firstName").exists())
            .andExpect(jsonPath("$.lastName").exists())
            .andExpect(jsonPath("$.email").exists())
            .andExpect(jsonPath("$.authorities[:1]").exists())
            .andExpect(jsonPath("$.password").doesNotExist())
            .andExpect(jsonPath("$.activationKey").doesNotExist())
            .andExpect(jsonPath("$.reset_key").doesNotExist())
            .andExpect(status().isOk())
            .andDo(print());
    }
}
