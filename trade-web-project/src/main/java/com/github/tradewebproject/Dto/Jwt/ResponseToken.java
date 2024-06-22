package com.github.tradewebproject.Dto.Jwt;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ResponseToken {

    private String userEmail;

    private String accessToken;


    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime accessTokenTime;
    private Long userId;
    private List<String> userInterests;


    @Builder
    public ResponseToken(String userEmail, String accessToken, LocalDateTime accessTokenTime , Long userId, List<String> userInterests) {
        this.userEmail = userEmail;
        this.accessToken = accessToken;
        this.accessTokenTime = accessTokenTime;
        this.userId = userId;
        this.userInterests = userInterests;

    }


    public static ResponseToken of(Token token, List<String> userInterests) {
        return ResponseToken.builder()
                .userEmail(token.getUserEmail())
                .accessToken(token.getAccessToken())
                .accessTokenTime(token.getAccessTokenTime())
                .userId(token.getUserId())
                .userInterests(userInterests)
                .build();
    }

}