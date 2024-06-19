package com.github.tradewebproject.Dto.Chat;

import com.github.tradewebproject.domain.ChatRoom;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChatRoomResponse {
    private Long ChatRoomid;
    private Long sellerId;
    private Long buyerId;
    private String sellerNickname;
    private String buyerNickname;

    // 생성자
    public ChatRoomResponse(ChatRoom chatRoom) {
        this.ChatRoomid = chatRoom.getChatRoomId();
        this.sellerId = chatRoom.getSeller().getUserId();
        this.buyerId = chatRoom.getBuyer().getUserId();
        this.sellerNickname = chatRoom.getSeller().getUserNickname();
        this.buyerNickname = chatRoom.getBuyer().getUserNickname();

    }

}
