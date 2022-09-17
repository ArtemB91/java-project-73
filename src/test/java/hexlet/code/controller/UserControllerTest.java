package hexlet.code.controller;

import hexlet.code.TestUtils;
import hexlet.code.dto.LoginDto;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {

    @Autowired
    private TestUtils testUtils;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testGetUsers() throws Exception {

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
    void testGetOneUser() throws Exception {

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
    void testCreateUser() throws Exception {

        UserDto userToCreate = new UserDto();
        userToCreate.setFirstName("John");
        userToCreate.setLastName("Doe");
        userToCreate.setEmail("john@doe.com");
        userToCreate.setPassword("123456");


        MockHttpServletResponse response = mockMvc
                .perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.toJson(userToCreate)))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());

        User user = userRepository.findByEmail(userToCreate.getEmail()).orElse(null);
        assertThat(user).isNotNull();
        assertThat(user.getFirstName()).isEqualTo(userToCreate.getFirstName());
        assertThat(user.getPassword()).isNotEqualTo(userToCreate.getPassword());
    }

    @Test
    void testUpdateUser() throws Exception {

        UserShortDto existingUser = testUtils.regDefaultUser();
        UserDto userToUpdate = new UserDto();
        userToUpdate.setFirstName("John");
        userToUpdate.setLastName("Doe");
        userToUpdate.setEmail("john@doe.com");
        userToUpdate.setPassword("123456");

        MockHttpServletResponse response = testUtils
                .performByUser(put("/users/" + existingUser.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(TestUtils.toJson(userToUpdate)),
                        existingUser.getEmail())
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        User user = userRepository.findById(existingUser.getId()).orElse(null);
        assertThat(user).isNotNull();
        assertThat(user.getFirstName()).isEqualTo(userToUpdate.getFirstName());
        assertThat(user.getLastName()).isEqualTo(userToUpdate.getLastName());
        assertThat(user.getEmail()).isEqualTo(userToUpdate.getEmail());

        assertThat(user.getPassword()).isNotEqualTo(userToUpdate.getPassword());

    }

    @Test
    void testDeleteUser() throws Exception {
        UserShortDto existingUser = testUtils.regDefaultUser();

        MockHttpServletResponse response = testUtils
                .performByUser(delete("/users/" + existingUser.getId()), existingUser.getEmail())
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(userRepository.existsById(existingUser.getId())).isFalse();
    }

    @Test
    void testLogin() throws Exception {
        UserShortDto existingUser = testUtils.regDefaultUser();

        LoginDto loginDto = new LoginDto();
        loginDto.setEmail(existingUser.getEmail());
        loginDto.setPassword(testUtils.defaultUserDto().getPassword());

        MockHttpServletResponse response = testUtils
                .perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.toJson(loginDto)))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isNotEmpty();

    }

}
