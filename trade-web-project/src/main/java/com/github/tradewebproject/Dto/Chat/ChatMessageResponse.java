package com.github.tradewebproject.Dto.Chat;

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
    private LocalDateTime sentTime;

    public ChatMessageResponse(ChatMessage chatMessage) {
        this.chatMessageId = chatMessage.getChatMessageId(); // ChatMessage의 ID 필드가 있다고 가정
        this.chatRoomId = chatMessage.getChatRoom().getChatRoomId();
        this.senderId = chatMessage.getSender().getUserId();
        this.message = chatMessage.getMessage();
        this.sentTime = chatMessage.getSentTime();
    }

    public static List<ChatMessageResponse> fromChatMessages(List<ChatMessage> chatMessages) {
        return chatMessages.stream()
                .map(ChatMessageResponse::new)
                .collect(Collectors.toList());
    }

}