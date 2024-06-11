package com.github.tradewebproject.Dto.Jwt;

import com.github.tradewebproject.domain.TokenEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class Token {

    private Long id;    //토큰 id

    private String grantType;
    private String accessToken;
    private String refreshToken;
    private LocalDateTime accessTokenTime;
    private LocalDateTime refreshTokenTime;
    private Long userId;
    private String userEmail;

    @Builder
    public Token(Long id,
                 String grantType,
                 String accessToken,
                 String refreshToken,
                 String userEmail,
                 LocalDateTime accessTokenTime,
                 LocalDateTime refreshTokenTime,
                 Long userId) {
        this.id = id;
        this.grantType = grantType;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.userEmail = userEmail;
        this.accessTokenTime = accessTokenTime;
        this.refreshTokenTime = refreshTokenTime;
        this.userId = userId;
    }

    public static Token from(TokenEntity token){
        return Token.builder()
                .id(token.getId())
                .grantType(token.getGrantType())
                .accessToken(token.getAccessToken())
                .refreshToken(token.getRefreshToken())
                .userEmail(token.getUserEmail())
                .accessTokenTime(token.getAccessTokenTime())
                .refreshTokenTime(token.getRefreshTokenTime())
                .userId(token.getUserId())
                .build();
    }

    public void setAccessToken(String reIssueAccessToken, LocalDateTime expiredTime) {
        this.accessToken = reIssueAccessToken;
        this.accessTokenTime = expiredTime;
    }

    public void setRefreshToken(String reIssueRefreshToken, LocalDateTime expiredTime) {
        this.refreshToken = reIssueRefreshToken;
        this.refreshTokenTime = expiredTime;
    }

}