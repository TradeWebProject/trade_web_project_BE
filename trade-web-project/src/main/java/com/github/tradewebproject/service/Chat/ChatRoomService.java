package com.github.tradewebproject.service.Chat;


import com.github.tradewebproject.Dto.Chat.ChatRoomGetResponse;
import com.github.tradewebproject.Dto.Chat.ChatRoomResponse;
import com.github.tradewebproject.domain.ChatMessage;
import com.github.tradewebproject.domain.ChatRoom;
import com.github.tradewebproject.domain.Product;
import com.github.tradewebproject.domain.User;
import com.github.tradewebproject.repository.Chat.ChatMessageRepository;
import com.github.tradewebproject.repository.Chat.ChatRoomRepository;
import com.github.tradewebproject.repository.Product.ProductRepository;
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
    private ProductRepository productRepository;
    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private UserJpaRepository userRepository;

    public ChatRoom createChatRoom(Long sellerId, Long buyerId, Long productId) {
        User seller = userRepository.findById(sellerId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seller not found"));
        User buyer = userRepository.findById(buyerId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Buyer not found"));

        if (chatRoomRepository.existsByProductIdAndBuyerUserId(productId, buyerId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 해당 상품에 대한 채팅방이 존재합니다.");
        }

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setSeller(seller);
        chatRoom.setBuyer(buyer);
        chatRoom.setProductId(productId);

        return chatRoomRepository.save(chatRoom);
    }


    public List<ChatRoomGetResponse> findAllChatRoomsByUserId(Long userId) {
        List<ChatRoom> chatRooms = chatRoomRepository.findAllBySeller_UserIdOrBuyer_UserId(userId, userId);
        return chatRooms.stream()
                .map(chatRoom -> {
                    Optional<ChatMessage> latestMessageOptional = chatMessageRepository.findTopByChatRoomOrderBySentTimeDesc(chatRoom);
                    String latestMessage = latestMessageOptional.map(ChatMessage::getMessage).orElse(null);
                    LocalDateTime latestMessageTime = latestMessageOptional.map(ChatMessage::getSentTime).orElse(null);

                    // chatRoom.getProductId()가 null인지 확인
                    Long productId = chatRoom.getProductId();
                    if (productId == null) {
                        // 로그를 추가하여 문제를 추적합니다.
                        System.err.println("ChatRoom ID: " + chatRoom.getProductId() + " has null Product ID.");
                        return new ChatRoomGetResponse(chatRoom, latestMessage, latestMessageTime, "Unknown Product");
                    }

                    String productName = productRepository.findById(productId)
                            .map(Product::getProductName)
                            .orElse("Unknown Product");  // productName 조회
                    return new ChatRoomGetResponse(chatRoom, latestMessage, latestMessageTime, productName);  // productName 설정
                })
                .collect(Collectors.toList());
    }


}
