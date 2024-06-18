package com.github.tradewebproject.service.Chat;


import com.github.tradewebproject.domain.ChatRoom;
import com.github.tradewebproject.repository.Chat.ChatRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatRoomService {

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    public ChatRoom createChatRoom(Long sellerId, Long buyerId) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setSellerId(sellerId);
        chatRoom.setBuyerId(buyerId);
        return chatRoomRepository.save(chatRoom);
    }

    public List<ChatRoom> getChatRoomsForUser(Long userId) {
        List<ChatRoom> sellerRooms = chatRoomRepository.findBySellerId(userId);
        List<ChatRoom> buyerRooms = chatRoomRepository.findByBuyerId(userId);
        sellerRooms.addAll(buyerRooms);
        return sellerRooms;
    }
}

