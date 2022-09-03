package hexlet.code.controller;

import hexlet.code.dto.UserDto;
import hexlet.code.dto.UserShortDto;
import hexlet.code.service.UserService;
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

    // Бин userService в глобальном контексте не нашел, поэтому надо либо обращаться к реализации,
    // либо через @Component делать бин заранее, либо к репо напрямую обращаться.
    // Выбрал последний вариант.
    private static final String ONLY_THE_SAME_USER = """
            @userRepository.findById(#id).get().getEmail() == authentication.getName()
        """;


    @GetMapping(path = "/{id}")
    public UserShortDto getUser(@PathVariable(value = "id") Long id) {
        return userService.getUserById(id);
    }


    @PostMapping(path = "")
    @ResponseStatus(HttpStatus.CREATED)
    public UserShortDto createUser(@Valid @RequestBody UserDto userDto) {
        return userService.createUser(userDto);
    }

    @GetMapping(path = "")
    public List<UserShortDto> getUsers() {
        return userService.getUsers();
    }

    @PutMapping(path = "/{id}")
    @PreAuthorize(ONLY_THE_SAME_USER)
    public UserShortDto updateUser(@PathVariable(value = "id") Long id,
                                   @Valid @RequestBody UserDto userDto) {
        return userService.updateUser(id, userDto);
    }

    @DeleteMapping(path = "/{id}")
    @PreAuthorize(ONLY_THE_SAME_USER)
    public void deleteUser(@PathVariable(value = "id") Long id) {
        userService.deleteUser(id);
    }

}
