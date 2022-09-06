package hexlet.code.service;

import hexlet.code.dto.UserDto;
import hexlet.code.dto.UserShortDto;
import hexlet.code.exceptions.DataNotFoundException;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
public final class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           TaskRepository taskRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserShortDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("User not found"));
        return convertToUserShortDto(user);
    }

    public UserShortDto createUser(UserDto userDto) {
        User user = convertToUser(userDto);
        return convertToUserShortDto(userRepository.save(user));
    }

    public List<UserShortDto> getUsers() {
        return StreamSupport.stream(userRepository.findAll().spliterator(), false)
                .map(this::convertToUserShortDto)
                .toList();
    }

    public UserShortDto updateUser(Long id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("User not found"));
        convertToUser(user, userDto);
        return convertToUserShortDto(userRepository.save(user));
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        // Сделаем проверку на уровне бизнес-логики, в т.ч. для выдачи человеко-читаемого сообщения.
        // Без проверки сообщение также можно получить на уровне exception драйвера БД,
        // но оно малоинформативно для пользователя сервиса
        if (taskRepository.existsByAuthor(user)) {
            throw new IllegalArgumentException("Deletion is prohibited. The user has tasks.");
        }

        userRepository.delete(user);
    }

    private UserShortDto convertToUserShortDto(User user) {
        return new UserShortDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getCreatedAt()
        );
    }

    private User convertToUser(User user, UserDto userDto) {
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        return user;
    }
    private User convertToUser(UserDto userDto) {
        return convertToUser(new User(), userDto);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("USER"))
        );
    }

    public User currentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String email = "";
        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else {
            email = principal.toString();
        }
        return userRepository.findByEmail(email).orElse(null);
    }
}
