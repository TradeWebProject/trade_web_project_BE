package com.github.tradewebproject.repository.Chat;

import com.github.tradewebproject.domain.ChatMessage;
import com.github.tradewebproject.domain.ChatRoom;
import com.github.tradewebproject.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findAllByChatRoom_ChatRoomId(Long chatRoomId);
}