package com.github.tradewebproject.controller.Chat;

import com.github.tradewebproject.domain.ChatRoom;
import com.github.tradewebproject.repository.User.UserRepository;
import com.github.tradewebproject.service.Chat.ChatRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/chat")
public class ChatRoomController {

    @Autowired
    private ChatRoomService chatRoomService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/create")
    public ResponseEntity<ChatRoom> createChatRoom(@RequestParam Long sellerId, @RequestParam Long buyerId) {
        ChatRoom chatRoom = chatRoomService.createChatRoom(sellerId, buyerId);
        return ResponseEntity.ok(chatRoom);
    }

    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoom>> getChatRooms(Principal principal) {
        String email = principal.getName();
        // UserRepository를 사용하여 userId를 가져와야 합니다.
        Long userId = userRepository.findByEmail2(email).orElseThrow(() -> new RuntimeException("User not found")).getUserId();
        List<ChatRoom> chatRooms = chatRoomService.getChatRoomsForUser(userId);
        return ResponseEntity.ok(chatRooms);
    }
}

