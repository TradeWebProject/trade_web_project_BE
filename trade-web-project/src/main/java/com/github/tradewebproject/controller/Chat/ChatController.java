package com.github.tradewebproject.controller.Chat;

import com.github.tradewebproject.Dto.Chat.ChatMessageDto;
import com.github.tradewebproject.domain.ChatMessage;
import com.github.tradewebproject.repository.Chat.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
public class ChatController {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessageDto sendMessage(@Payload ChatMessageDto chatMessageDto) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSender(chatMessageDto.getSender());
        chatMessage.setRecipient(chatMessageDto.getRecipient());
        chatMessage.setContent(chatMessageDto.getContent());
        chatMessage.setTimestamp(LocalDateTime.now());
        chatMessageRepository.save(chatMessage);


        return chatMessageDto;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessageDto addUser(@Payload ChatMessageDto chatMessageDto, SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", chatMessageDto.getSender());
        return chatMessageDto;
    }
}
