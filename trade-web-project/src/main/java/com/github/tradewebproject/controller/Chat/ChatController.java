package com.github.tradewebproject.controller.Chat;

import com.github.tradewebproject.Dto.Chat.*;
import com.github.tradewebproject.domain.ChatMessage;
import com.github.tradewebproject.domain.ChatRoom;
import com.github.tradewebproject.domain.Product;
import com.github.tradewebproject.domain.User;
import com.github.tradewebproject.repository.Chat.ChatRoomRepository;
import com.github.tradewebproject.repository.Product.ProductRepository;
import com.github.tradewebproject.repository.User.UserJpaRepository;
import com.github.tradewebproject.repository.User.UserRepository;
import com.github.tradewebproject.service.Chat.ChatMessageService;
import com.github.tradewebproject.service.Chat.ChatRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

        if (sellerId.equals(buyerId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "자신의 물건은 채팅방을 만들 수 없습니다.");
        }

        ChatRoom chatRoom = chatRoomService.createChatRoom(sellerId, buyerId);
        return ResponseEntity.ok(new ChatRoomResponse(chatRoom));
    }


    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoomGetResponse>> getChatRooms(Principal principal) {
        String email = principal.getName();
        User user = getUserByEmail(email);
        Long userId = user.getUserId();

        List<ChatRoomGetResponse> chatRooms = chatRoomService.findAllChatRoomsByUserId(userId);
        return ResponseEntity.ok(chatRooms);
    }

    @GetMapping("/messages")
    public ResponseEntity<List<ChatMessageResponse>> getChatMessages(@RequestParam Long chatRoomId) {
        List<ChatMessage> chatMessages = chatMessageService.findAllMessagesByChatRoomId(chatRoomId);
        List<ChatMessageResponse> response = ChatMessageResponse.fromChatMessages(chatMessages);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/messages")
    public ResponseEntity<ChatMessageDto> sendMessage(Principal principal,
                                                      @RequestParam Long chatRoomId,
                                                      @RequestParam String messageContent,
                                                      @RequestParam String messageTypeStr,
                                                      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime sentTime) {
        String email = principal.getName();
        User sender = userRepository.findByEmail2(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Long senderId = sender.getUserId();

        MessageType messageType;
        try {
            messageType = MessageType.valueOf(messageTypeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid message type");
        }

        ChatMessageDto chatMessageDto;
        switch (messageType) {
            case TEXT:
                chatMessageDto = chatMessageService.sendTextMessage(chatRoomId, senderId, messageContent, sentTime);
                break;
            case IMAGE:
                chatMessageDto = chatMessageService.sendImageMessage(chatRoomId, senderId, messageContent, sentTime);
                break;
            case EMOJI:
                chatMessageDto = chatMessageService.sendEmojiMessage(chatRoomId, senderId, messageContent, sentTime);
                break;
            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported message type");
        }

        return ResponseEntity.ok(chatMessageDto);
    }


    private User getUserByEmail(String email) {
        return userRepository.findByEmail2(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }
}