package com.github.tradewebproject.repository.Jwt;

import com.github.tradewebproject.Dto.Jwt.Token;

public interface TokenRepository {

    Token save(Token token);
    Token findByRefreshToken(String refreshToken);
    Token findByUserEmail(String userEmail);
    Token findByAccessToken(String accessToken);
    void deleteById(Long id);

}
