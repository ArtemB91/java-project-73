package hexlet.code.service;

import hexlet.code.dto.LoginDto;

public interface AuthService {
    String getToken(LoginDto loginDto);
}
