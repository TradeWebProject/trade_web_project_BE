package com.github.tradewebproject.repository.Chat;

import com.github.tradewebproject.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    List<ChatRoom> findBySellerId(Long sellerId);
    List<ChatRoom> findByBuyerId(Long buyerId);
}

