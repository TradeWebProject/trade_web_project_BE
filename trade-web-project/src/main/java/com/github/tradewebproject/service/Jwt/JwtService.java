package com.github.tradewebproject.service.Jwt;

import com.github.tradewebproject.Dto.User.UserDto;
import jakarta.servlet.http.HttpServletResponse;

public interface JwtService {

    void createAccessTokenHeader(HttpServletResponse response, String refreshToken);
    UserDto checkAccessTokenValid(String accessToken);

}