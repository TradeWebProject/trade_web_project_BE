package com.github.tradewebproject.Dto.Chat;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class WebSocketMessage {

    private Long chatRoomId;
    private Long senderId;
    private String messageContent;


}