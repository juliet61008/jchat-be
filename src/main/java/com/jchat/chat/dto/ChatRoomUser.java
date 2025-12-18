package com.jchat.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ChatRoomUser {
    private Long userNo; // 회원번호
    private String name; // 회원이름
    private String friendYn; // 친구여부
    private String mineYn; // 내정보여부
}
