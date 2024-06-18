package com.github.tradewebproject.Dto.Chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ChatMessageDto {
    private String sender;
    private String recipient;
    private String content;
    private LocalDateTime timestamp;

}
