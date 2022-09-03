package hexlet.code.controller;

import hexlet.code.dto.LoginDto;
import hexlet.code.service.AuthService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(path = "/login")
    public String login(@RequestBody LoginDto loginDto) throws BadCredentialsException {
        return authService.getToken(loginDto);
    }


}
