package com.github.tradewebproject.service.Chat;

import com.github.tradewebproject.Dto.Chat.ChatMessageDto;
import com.github.tradewebproject.Dto.Chat.MessageType;
import com.github.tradewebproject.domain.ChatMessage;
import com.github.tradewebproject.domain.ChatRoom;
import com.github.tradewebproject.domain.User;
import com.github.tradewebproject.repository.Chat.ChatMessageRepository;
import com.github.tradewebproject.repository.Chat.ChatRoomRepository;
import com.github.tradewebproject.repository.User.UserJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatMessageService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private UserJpaRepository userRepository;

    public ChatMessageDto sendTextMessage(Long chatRoomId, Long senderId, String messageContent) {
        return sendMessage(chatRoomId, senderId, messageContent, MessageType.TEXT);
    }

    public ChatMessageDto sendImageMessage(Long chatRoomId, Long senderId, String imageUrl) {
        return sendMessage(chatRoomId, senderId, imageUrl, MessageType.IMAGE);
    }

    public ChatMessageDto sendEmojiMessage(Long chatRoomId, Long senderId, String emojiCode) {
        return sendMessage(chatRoomId, senderId, emojiCode, MessageType.EMOJI);
    }

    private ChatMessageDto sendMessage(Long chatRoomId, Long senderId, String content, MessageType messageType) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chat room not found"));
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sender not found"));

        ChatMessage message = ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .message(content)
                .messageType(messageType)
                .sentTime(LocalDateTime.now())
                .build();

        ChatMessage savedMessage = chatMessageRepository.save(message);

        ChatMessageDto chatMessageDto = new ChatMessageDto();
        chatMessageDto.setChatRoomId(savedMessage.getChatRoom().getChatRoomId());
        chatMessageDto.setSenderId(savedMessage.getSender().getUserId());
        chatMessageDto.setSenderNickName(savedMessage.getSender().getUserNickname());
        String imageUrl = "/images/" + savedMessage.getSender().getUserImg();
        chatMessageDto.setSenderImgUrl(imageUrl);
        chatMessageDto.setContent(savedMessage.getMessage());
        chatMessageDto.setSendTime(savedMessage.getSentTime());
        chatMessageDto.setMessageType(savedMessage.getMessageType());

        return chatMessageDto;
    }

    public List<ChatMessage> findAllMessagesByChatRoomId(Long chatRoomId) {
        return chatMessageRepository.findAllByChatRoom_ChatRoomId(chatRoomId);
    }
}