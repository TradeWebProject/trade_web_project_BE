package com.github.tradewebproject.controller.Chat;

import com.github.tradewebproject.Dto.Chat.ChatMessageResponse;
import com.github.tradewebproject.Dto.Chat.ChatRoomResponse;
import com.github.tradewebproject.Dto.Chat.CreateChatRoomRequest;
import com.github.tradewebproject.domain.ChatMessage;
import com.github.tradewebproject.domain.ChatRoom;
import com.github.tradewebproject.domain.Product;
import com.github.tradewebproject.domain.User;
import com.github.tradewebproject.repository.Product.ProductRepository;
import com.github.tradewebproject.repository.User.UserJpaRepository;
import com.github.tradewebproject.repository.User.UserRepository;
import com.github.tradewebproject.service.Chat.ChatMessageService;
import com.github.tradewebproject.service.Chat.ChatRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Autowired
    public ChatController(ChatRoomService chatRoomService, ChatMessageService chatMessageService,
                          UserRepository userRepository, ProductRepository productRepository) {
        this.chatRoomService = chatRoomService;
        this.chatMessageService = chatMessageService;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @PostMapping("/rooms")
    public ResponseEntity<ChatRoomResponse> createChatRoom(Principal principal, @RequestBody CreateChatRoomRequest request) {
        String email = principal.getName();
        User buyer = getUserByEmail(email);
        Long buyerId = buyer.getUserId();

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        Long sellerId = product.getUser().getUserId();

        ChatRoom chatRoom = chatRoomService.createChatRoom(sellerId, buyerId);
        return ResponseEntity.ok(new ChatRoomResponse(chatRoom));
    }


    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoomResponse>> getChatRooms(Principal principal) {
        String email = principal.getName();
        User user = getUserByEmail(email);
        Long userId = user.getUserId();

        List<ChatRoomResponse> chatRooms = chatRoomService.findAllChatRoomsByUserId(userId);
        return ResponseEntity.ok(chatRooms);
    }

    @GetMapping("/messages")
    public ResponseEntity<List<ChatMessageResponse>> getChatMessages(@RequestParam Long chatRoomId) {
        List<ChatMessage> chatMessages = chatMessageService.findAllMessagesByChatRoomId(chatRoomId);
        List<ChatMessageResponse> response = ChatMessageResponse.fromChatMessages(chatMessages);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/messages")
    public ResponseEntity<ChatMessageResponse> sendMessage(Principal principal, @RequestParam Long chatRoomId, @RequestParam String messageContent) {
        String email = principal.getName();
        User sender = getUserByEmail(email);
        Long senderId = sender.getUserId();

        ChatMessage chatMessage = chatMessageService.sendMessage(chatRoomId, senderId, messageContent);
        return ResponseEntity.ok(new ChatMessageResponse(chatMessage));
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail2(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }
}