package hexlet.code.controller;

import hexlet.code.TestUtils;
import hexlet.code.dto.UserDto;
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

        testUtils.regDefaultUser();

        MockHttpServletResponse response = mockMvc
                .perform(get("/users"))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).contains(
                testUtils.defaultUserDto().getEmail());
    }

    @Test
    public void testGetOneUser() throws Exception {

        UserShortDto userShortDto = testUtils.regDefaultUser();

        MockHttpServletResponse response = mockMvc
                .perform(get("/users/" + userShortDto.getId()))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        UserDto defUserDto = testUtils.defaultUserDto();
        assertThat(response.getContentAsString()).contains(
                defUserDto.getFirstName(),
                defUserDto.getLastName(),
                defUserDto.getEmail());
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

        assertThat(userRepository.count()).isEqualTo(1);

        User user = userRepository.findByEmail("john@doe.com").get();
        assertThat(user).isNotNull();
        assertThat(user.getFirstName()).isEqualTo("John");
        assertThat(user.getPassword()).isNotEqualTo("123456");
    }

    @Test
    public void testUpdateUser() throws Exception {

        Long existingUserId = testUtils.regDefaultUser().getId();

        String content = """
                {"firstName": "John",
                "lastName": "Doe",
                "email": "john@doe.com",
                "password" : "123456"}""";

        MockHttpServletResponse response = mockMvc
                .perform(put("/users/" + existingUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        User user = userRepository.findById(existingUserId).get();
        assertThat(user).isNotNull();
        assertThat(user.getFirstName()).isEqualTo("John");
        assertThat(user.getLastName()).isEqualTo("Doe");
        assertThat(user.getEmail()).isEqualTo("john@doe.com");

        assertThat(user.getPassword()).isNotEqualTo("123456");

    }

}
