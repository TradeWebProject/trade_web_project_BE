package com.github.tradewebproject.repository.Chat;

import com.github.tradewebproject.domain.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByRecipient(String recipient);
    List<ChatMessage> findBySenderAndRecipient(String sender, String recipient);
}
