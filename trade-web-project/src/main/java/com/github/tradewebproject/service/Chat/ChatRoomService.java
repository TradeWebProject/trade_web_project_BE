package com.github.tradewebproject.service.Chat;


import com.github.tradewebproject.Dto.Chat.ChatRoomGetResponse;
import com.github.tradewebproject.Dto.Chat.ChatRoomResponse;
import com.github.tradewebproject.domain.ChatMessage;
import com.github.tradewebproject.domain.ChatRoom;
import com.github.tradewebproject.domain.User;
import com.github.tradewebproject.repository.Chat.ChatMessageRepository;
import com.github.tradewebproject.repository.Chat.ChatRoomRepository;
import com.github.tradewebproject.repository.User.UserJpaRepository;
import com.github.tradewebproject.repository.User.UserRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChatRoomService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private UserJpaRepository userRepository;

    public ChatRoom createChatRoom(Long sellerId, Long buyerId) {
        User seller = userRepository.findById(sellerId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seller not found"));
        User buyer = userRepository.findById(buyerId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Buyer not found"));

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setSeller(seller);
        chatRoom.setBuyer(buyer);

        return chatRoomRepository.save(chatRoom);
    }

    public List<ChatRoomGetResponse> findAllChatRoomsByUserId(Long userId) {
        List<ChatRoom> chatRooms = chatRoomRepository.findAllBySeller_UserIdOrBuyer_UserId(userId, userId);
        return chatRooms.stream()
                .map(chatRoom -> {
                    Optional<ChatMessage> latestMessageOptional = chatMessageRepository.findTopByChatRoomOrderBySentTimeDesc(chatRoom);
                    String latestMessage = latestMessageOptional.map(ChatMessage::getMessage).orElse(null);
                    LocalDateTime latestMessageTime = latestMessageOptional.map(ChatMessage::getSentTime).orElse(null);
                    return new ChatRoomGetResponse(chatRoom, latestMessage, latestMessageTime);
                })
                .collect(Collectors.toList());
    }

}
