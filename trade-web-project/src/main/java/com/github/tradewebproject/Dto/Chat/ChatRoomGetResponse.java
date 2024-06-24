package com.github.tradewebproject.Dto.Chat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.tradewebproject.domain.ChatRoom;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ChatRoomGetResponse {
    private Long ChatRoomid;
    private Long sellerId;
    private Long buyerId;
    private String sellerNickname;
    private String buyerNickname;
    private String latestMessage;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime latestMessageTime;

    private String sellerImgUrl;
    private String buyerImgUrl;
    private Long productId;


    // 생성자
    public ChatRoomGetResponse(ChatRoom chatRoom,String latestMessage,LocalDateTime latestMessageTime) {
        this.ChatRoomid = chatRoom.getChatRoomId();
        this.sellerId = chatRoom.getSeller().getUserId();
        this.buyerId = chatRoom.getBuyer().getUserId();
        this.sellerNickname = chatRoom.getSeller().getUserNickname();
        this.buyerNickname = chatRoom.getBuyer().getUserNickname();
        this.latestMessage= latestMessage;
        this.latestMessageTime = latestMessageTime;
        this.sellerImgUrl = "/images/" + chatRoom.getSeller().getUserImg();
        this.buyerImgUrl = "/images/" + chatRoom.getBuyer().getUserImg();
        this.productId = chatRoom.getProductId();
    }

}
