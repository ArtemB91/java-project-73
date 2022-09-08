package hexlet.code.controller;

import com.rollbar.notifier.Rollbar;
import hexlet.code.dto.UserDto;
import hexlet.code.dto.UserShortDto;
import hexlet.code.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private Rollbar rollbar;

    // Бин userService в глобальном контексте не нашел, поэтому надо либо обращаться к реализации,
    // либо через @Component делать бин заранее, либо к репо напрямую обращаться.
    // Выбрал последний вариант.
    private static final String ONLY_THE_SAME_USER = """
            @userRepository.findById(#id).get().getEmail() == authentication.getName()
        """;

    @Operation(summary = "Get specific user by his id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User with that id not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping(path = "/{id}")
    public UserShortDto getUser(
            @Parameter(description = "Id of user to be found")
            @PathVariable(value = "id") Long id) {
        return userService.getUserById(id);
    }


    @Operation(summary = "Create new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created"),
            @ApiResponse(responseCode = "422", description = "Invalid user data")
    })
    @PostMapping(path = "")
    @ResponseStatus(HttpStatus.CREATED)
    public UserShortDto createUser(
            @Parameter(description = "User data to create")
            @Valid @RequestBody UserDto userDto) {
        return userService.createUser(userDto);
    }

    @Operation(summary = "Get list of all users")
    @ApiResponse(responseCode = "200", description = "List of all users")
    @GetMapping(path = "")
    public List<UserShortDto> getUsers() {
        rollbar.info("Get users info message");
        return userService.getUsers();
    }

    @Operation(summary = "Update existing user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated"),
            @ApiResponse(responseCode = "404", description = "User with that id not found"),
            @ApiResponse(responseCode = "422", description = "Invalid user data")
    })
    @PutMapping(path = "/{id}")
    @PreAuthorize(ONLY_THE_SAME_USER)
    public UserShortDto updateUser(
            @Parameter(description = "Id of user to be updated")
            @PathVariable(value = "id") Long id,
            @Parameter(description = "User data to update")
            @Valid @RequestBody UserDto userDto) {
        return userService.updateUser(id, userDto);
    }

    @Operation(summary = "Delete user by his id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted"),
            @ApiResponse(responseCode = "404", description = "User with that id not found")
    })
    @DeleteMapping(path = "/{id}")
    @PreAuthorize(ONLY_THE_SAME_USER)
    public void deleteUser(
            @Parameter(description = "Id of user to be deleted")
            @PathVariable(value = "id") Long id) {
        userService.deleteUser(id);
    }

}
