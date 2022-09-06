package hexlet.code.service;

import hexlet.code.dto.UserDto;
import hexlet.code.dto.UserShortDto;
import hexlet.code.model.User;

import java.util.List;

public interface UserService {

    UserShortDto getUserById(Long id);

    UserShortDto createUser(UserDto userDto);

    List<UserShortDto> getUsers();

    UserShortDto updateUser(Long id, UserDto userDto);

    void deleteUser(Long id);

    User currentUser();

}
