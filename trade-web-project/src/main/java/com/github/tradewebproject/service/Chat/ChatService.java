package com.github.tradewebproject.service.Chat;

import com.github.tradewebproject.domain.ChatMessage;
import com.github.tradewebproject.repository.Chat.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    public List<ChatMessage> getChatHistory(String sender, String recipient) {
        return chatMessageRepository.findBySenderAndRecipient(sender, recipient);
    }
}
