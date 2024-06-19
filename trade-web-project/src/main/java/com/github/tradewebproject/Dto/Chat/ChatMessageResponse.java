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

//    public ChatMessageResponse(ChatMessage chatMessage) {
//        this.chatMessageId = chatMessage.getChatMessageId(); // ChatMessage의 ID 필드가 있다고 가정
//        this.chatRoomId = chatMessage.getChatRoom().getChatRoomId();
//        this.senderId = chatMessage.getSender().getUserId();
//        this.message = chatMessage.getMessage();
//        this.sentTime = chatMessage.getSentTime();
//    }

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