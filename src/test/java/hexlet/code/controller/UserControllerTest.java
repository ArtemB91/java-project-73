package hexlet.code.controller;

import hexlet.code.TestUtils;
import hexlet.code.dto.UserShortDto;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerTest {

    @Autowired
    private TestUtils testUtils;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testGetUsers() throws Exception {

        UserShortDto existingUser = testUtils.regDefaultUser();

        MockHttpServletResponse response =
                testUtils.perform(get("/users"))
                        .andReturn()
                        .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).contains(
                existingUser.getEmail());
    }

    @Test
    public void testGetOneUser() throws Exception {

        UserShortDto existingUser = testUtils.regDefaultUser();

        MockHttpServletResponse response = testUtils
                .performByUser(get("/users/" + existingUser.getId()), existingUser.getEmail())
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        assertThat(response.getContentAsString()).contains(
                existingUser.getFirstName(),
                existingUser.getLastName(),
                existingUser.getEmail());
    }

    @Test
    public void testCreateUser() throws Exception {
        String content = """
                {"firstName": "John",
                "lastName": "Doe",
                "email": "john@doe.com",
                "password" : "123456"}""";

        MockHttpServletResponse response = mockMvc
                .perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());

        User user = userRepository.findByEmail("john@doe.com").get();
        assertThat(user).isNotNull();
        assertThat(user.getFirstName()).isEqualTo("John");
        assertThat(user.getPassword()).isNotEqualTo("123456");
    }

    @Test
    public void testUpdateUser() throws Exception {

        UserShortDto existingUser = testUtils.regDefaultUser();

        String content = """
                {"firstName": "John",
                "lastName": "Doe",
                "email": "john@doe.com",
                "password" : "123456"}""";

        MockHttpServletResponse response = testUtils
                .performByUser(put("/users/" + existingUser.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content),
                        existingUser.getEmail())
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        User user = userRepository.findById(existingUser.getId()).get();
        assertThat(user).isNotNull();
        assertThat(user.getFirstName()).isEqualTo("John");
        assertThat(user.getLastName()).isEqualTo("Doe");
        assertThat(user.getEmail()).isEqualTo("john@doe.com");

        assertThat(user.getPassword()).isNotEqualTo("123456");

    }

    @Test
    public void testDeleteUser() throws Exception {
        UserShortDto existingUser = testUtils.regDefaultUser();

        MockHttpServletResponse response = testUtils
                .performByUser(delete("/users/" + existingUser.getId()), existingUser.getEmail())
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(userRepository.existsById(existingUser.getId())).isFalse();
    }

    @Test
    public void testLogin() throws Exception {
        UserShortDto existingUser = testUtils.regDefaultUser();

        String content = String.format("""
                {"email": "%s",
                "password" : "%s"}""",
                existingUser.getEmail(),
                testUtils.defaultUserDto().getPassword());

        MockHttpServletResponse response = testUtils
                .perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isNotEmpty();

    }

}
