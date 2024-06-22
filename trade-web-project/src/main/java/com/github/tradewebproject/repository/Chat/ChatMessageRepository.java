package com.github.tradewebproject.repository.Chat;

import com.github.tradewebproject.domain.ChatMessage;
import com.github.tradewebproject.domain.ChatRoom;
import com.github.tradewebproject.domain.User;
import org.hibernate.Hibernate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findAllByChatRoom_ChatRoomId(Long chatRoomId);

    Optional<ChatMessage> findTopByChatRoomOrderBySentTimeDesc(ChatRoom chatRoom);
}