package com.github.tradewebproject.Dto.Jwt;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ResponseToken {

    private String userEmail;

    private String accessToken;
    private String refreshToken;
    private LocalDateTime accessTokenTime;
    private Long userId;

    @Builder
    public ResponseToken(String userEmail, String accessToken, String refreshToken, LocalDateTime accessTokenTime , Long userId) {
        this.userEmail = userEmail;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.accessTokenTime = accessTokenTime;
        this.userId = userId;
    }

    public static ResponseToken of(Token token){
        return ResponseToken.builder()
                .userEmail(token.getUserEmail())
                .accessToken(token.getAccessToken())
                .refreshToken(token.getRefreshToken())
                .accessTokenTime(token.getAccessTokenTime())
                .userId(token.getUserId())
                .build();
    }

}