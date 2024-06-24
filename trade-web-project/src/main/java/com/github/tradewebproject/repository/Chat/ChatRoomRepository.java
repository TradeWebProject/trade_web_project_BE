package com.github.tradewebproject.repository.Chat;


import com.github.tradewebproject.domain.ChatMessage;
import com.github.tradewebproject.domain.ChatRoom;
import com.github.tradewebproject.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    List<ChatRoom> findAllBySeller_UserIdOrBuyer_UserId(Long sellerId, Long buyerId);

    boolean existsByProductIdAndBuyerUserId(Long productId, Long buyerId);


}