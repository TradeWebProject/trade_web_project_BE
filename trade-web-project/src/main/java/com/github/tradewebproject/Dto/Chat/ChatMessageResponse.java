package com.github.tradewebproject.Dto.Chat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.tradewebproject.domain.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponse {
    private Long chatMessageId;
    private Long chatRoomId;
    private Long senderId;
    private String message;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime sentTime;

    private MessageType messageType;


    public static List<ChatMessageResponse> fromChatMessages(List<ChatMessage> chatMessages) {
        return chatMessages.stream()
                .map(chatMessage -> new ChatMessageResponse(
                        chatMessage.getChatMessageId(),
                        chatMessage.getChatRoom().getChatRoomId(),
                        chatMessage.getSender().getUserId(),
                        chatMessage.getMessage(),
                        chatMessage.getSentTime(),
                        chatMessage.getMessageType()
                ))
                .collect(Collectors.toList());
    }

}