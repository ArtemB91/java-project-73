package hexlet.code;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.component.JWTHelper;
import hexlet.code.dto.UserDto;
import hexlet.code.dto.UserShortDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Component
public final class TestUtils {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JWTHelper jwtHelper;

    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    public UserShortDto regDefaultUser() throws Exception {
        UserDto userDto = defaultUserDto();
        final var request = post("/users")
                .content(toJson(userDto))
                .contentType(APPLICATION_JSON);

        MockHttpServletResponse response = mockMvc.perform(request).andReturn().getResponse();
        UserShortDto userResponse = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        return userResponse;
    }

    public UserDto defaultUserDto() {
        UserDto userDto = new UserDto();
        userDto.setFirstName("defaultName");
        userDto.setLastName("defaultSurname");
        userDto.setEmail("default@default.com");
        userDto.setPassword("defaultPass");

        return userDto;
    }

    public static String toJson(final Object object) throws JsonProcessingException {
        return MAPPER.writeValueAsString(object);
    }

    public static <T> T fromJson(final String json, final TypeReference<T> to) throws JsonProcessingException {
        return MAPPER.readValue(json, to);
    }

    public ResultActions perform(final MockHttpServletRequestBuilder request) throws Exception {
        return mockMvc.perform(request);
    }

    public ResultActions perform(final MockHttpServletRequestBuilder request, final String byUser) throws Exception {
        final String token = jwtHelper.expiring(Map.of("username", byUser));
        request.header(AUTHORIZATION, token);

        return perform(request);
    }

}
