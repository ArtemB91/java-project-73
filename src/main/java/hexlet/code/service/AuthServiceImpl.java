package hexlet.code.service;

import hexlet.code.component.JWTHelper;
import hexlet.code.dto.LoginDto;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY;

@Service
public class AuthServiceImpl implements AuthService {

    private final Validator validator;
    private final JWTHelper jwtHelper;
    private final AuthenticationManager authenticationManager;

    public AuthServiceImpl(Validator validator, JWTHelper jwtHelper, AuthenticationManager authenticationManager) {
        this.validator = validator;
        this.jwtHelper = jwtHelper;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public String getToken(LoginDto loginDto) throws BadCredentialsException {
        validateLoginData(loginDto);

        Authentication authResult = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getEmail(), loginDto.getPassword()
                )
        );

        final UserDetails user = (UserDetails) authResult.getPrincipal();
        final String token = jwtHelper.expiring(Map.of(SPRING_SECURITY_FORM_USERNAME_KEY, user.getUsername()));

        return token;
    }

    private void validateLoginData(LoginDto loginDto) {
        Set<ConstraintViolation<LoginDto>> validate = validator.validate(loginDto);
        if (!validate.isEmpty()) {
            String exceptionText = validate.stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .collect(Collectors.joining("\n"));
            throw new BadCredentialsException(exceptionText);
        }
    }
}
