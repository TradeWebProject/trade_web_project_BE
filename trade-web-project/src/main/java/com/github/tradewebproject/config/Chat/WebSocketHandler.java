package com.github.tradewebproject.config.Chat;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tradewebproject.Dto.Chat.ChatMessageDto;
import com.github.tradewebproject.Dto.Chat.WebSocketMessage;
import com.github.tradewebproject.Dto.User.UserDto;
import com.github.tradewebproject.domain.ChatMessage;
import com.github.tradewebproject.domain.ChatRoom;
import com.github.tradewebproject.domain.User;
import com.github.tradewebproject.repository.Chat.ChatRoomRepository;
import com.github.tradewebproject.repository.User.UserRepository;
import com.github.tradewebproject.service.Chat.ChatMessageService;
import com.github.tradewebproject.service.Jwt.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class WebSocketHandler extends TextWebSocketHandler {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketHandler.class);

    private final Map<Long, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;

    private final ChatMessageService chatMessageService;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final ChatRoomRepository chatRoomRepository;

    @Autowired
    public WebSocketHandler(ChatMessageService chatMessageService, UserRepository userRepository, JwtService jwtService, ChatRoomRepository chatRoomRepository, ObjectMapper objectMapper) {
        this.chatMessageService = chatMessageService;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.chatRoomRepository = chatRoomRepository;
        this.objectMapper = objectMapper;
    }


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String token = getTokenFromSession(session);
        if (token != null) {
            User user = getUserFromToken(token);
            if (user != null) {
                Long chatRoomId = getChatRoomIdFromSession(session); // 채팅방 ID를 세션에서 가져오는 메서드
                if (isUserAuthorizedForChatRoom(user, chatRoomId)) { // 권한 확인 로직
                    sessions.put(user.getUserId(), session);
                    session.getAttributes().put("chatRoomId", chatRoomId); // chatRoomId를 세션 속성에 저장
                } else {
                    session.close(CloseStatus.NOT_ACCEPTABLE);
                }
            } else {
                session.close(CloseStatus.NOT_ACCEPTABLE);
            }
        } else {
            session.close(CloseStatus.NOT_ACCEPTABLE);
        }
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        WebSocketMessage webSocketMessageDto = objectMapper.readValue(message.getPayload(), WebSocketMessage.class);

        String token = getTokenFromSession(session);
        User user = getUserFromToken(token);
        if (user != null) {
            Long chatRoomId = (Long) session.getAttributes().get("chatRoomId");
            if (chatRoomId != null && isUserAuthorizedForChatRoom(user, chatRoomId)) {
                Long senderId = user.getUserId();
                ZonedDateTime utcTime = ZonedDateTime.now(ZoneId.of("UTC"));
                ZonedDateTime koreaTime = utcTime.withZoneSameInstant(ZoneId.of("Asia/Seoul"));
                LocalDateTime sentTime = koreaTime.toLocalDateTime(); // 한국 시간대로 변환된 현재 시간을 sentTime으로 설정

                ChatMessageDto chatMessageDto = null;
                switch (webSocketMessageDto.getMessageType()) {
                    case "TEXT":
                        chatMessageDto = chatMessageService.sendTextMessage(
                                chatRoomId,
                                senderId,
                                webSocketMessageDto.getMessageContent(),
                                sentTime
                        );
                        break;
                    case "IMAGE":
                        String imageUrl = webSocketMessageDto.getImageUrl().replace("\\", "/");
                        chatMessageDto = chatMessageService.sendImageMessage(
                                chatRoomId,
                                senderId,
                                imageUrl,
                                sentTime
                        );
                        break;
                    case "EMOJI":
                        chatMessageDto = chatMessageService.sendEmojiMessage(
                                chatRoomId,
                                senderId,
                                webSocketMessageDto.getEmojiCode(),
                                sentTime
                        );
                        break;
                    default:
                        session.close(CloseStatus.NOT_ACCEPTABLE);
                        return;
                }

                sendNotification(user, chatMessageDto);

                TextMessage responseMessage = new TextMessage(objectMapper.writeValueAsString(chatMessageDto));
                for (WebSocketSession webSocketSession : sessions.values()) {
                    if (webSocketSession.isOpen()) {
                        webSocketSession.sendMessage(responseMessage);
                    }
                }
            } else {
                session.close(CloseStatus.NOT_ACCEPTABLE);
            }
        } else {
            session.close(CloseStatus.NOT_ACCEPTABLE);
        }
    }

    private Long getChatRoomIdFromSession(WebSocketSession session) {
        // WebSocketSession의 URL에서 채팅방 ID를 추출하는 방법
        String query = session.getUri().getQuery();
        if (query != null) {
            // 쿼리 파라미터를 &로 분리하여 각 파라미터를 처리
            String[] params = query.split("&");
            for (String param : params) {
                // 각 파라미터가 'chatRoomId='로 시작하는지 확인
                if (param.startsWith("chatRoomId=")) {
                    // 'chatRoomId=' 이후의 값을 Long으로 변환하여 반환
                    return Long.valueOf(param.substring("chatRoomId=".length()));
                }
            }
        }
        return null;
    }


    private boolean isUserAuthorizedForChatRoom(User user, Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElse(null);
        if (chatRoom == null) {
            return false;
        }
        return chatRoom.getSeller().getUserId().equals(user.getUserId()) ||
                chatRoom.getBuyer().getUserId().equals(user.getUserId());
    }

    private String getTokenFromSession(WebSocketSession session) {
        String query = session.getUri().getQuery();
        System.out.println("Query string: " + query);
        if (query != null) {
            String[] params = query.split("&");
            for (String param : params) {
                if (param.startsWith("token=")) {
                    return param.substring(6); // "token=" 이후의 값
                }
            }
        }
        return null;
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long userId = null;
        for (Map.Entry<Long, WebSocketSession> entry : sessions.entrySet()) {
            if (entry.getValue().equals(session)) {
                userId = entry.getKey();
                break;
            }
        }
        if (userId != null) {
            sessions.remove(userId);
        }
    }

    private User getUserFromToken(String token) {
        try {
            // 토큰을 검증하고 사용자 정보를 가져오는 로직
            UserDto userDto = jwtService.checkAccessTokenValid(token);
            if (userDto != null) {
                return userRepository.findByEmail2(userDto.getEmail()).orElse(null);
            }
            return null;
        } catch (Exception e) {
            log.error("Error while retrieving user from token", e);
            return null;
        }
    }

    private void sendNotification(User user, ChatMessageDto chatMessageDto) {
        // 알림 전송 로직 구현 (예: FCM, APNS, 이메일 등)
        log.info("Send notification to user: " + user.getUserId() + " - Message: " + chatMessageDto.getContent());
    }
}
