package com.github.tradewebproject.repository.Jwt;

import com.github.tradewebproject.domain.TokenEntity;
import com.github.tradewebproject.Dto.Jwt.Token;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TokenRepositoryImpl implements TokenRepository{

    private final TokenJpaRepository tokenJpaRepository;

    @Override
    public Token save(Token token) {
        return Token.from(tokenJpaRepository.save(TokenEntity.from(token)));
    }

    @Override
    public Token findByRefreshToken(String refreshToken) {
        return Token.from(tokenJpaRepository.findByRefreshToken(refreshToken));
    }

    @Override
    public Token findByUserEmail(String email) {
        if(tokenJpaRepository.findByUserEmail(email).isPresent())
            return Token.from(tokenJpaRepository.findByUserEmail(email).get());

        return null;
    }

    @Override
    public Token findByAccessToken(String accessToken) {
        return Token.from(tokenJpaRepository.findByAccessToken(accessToken));
    }

    @Override
    public void deleteById(Long id) {
        tokenJpaRepository.deleteById(id);
    }

}
