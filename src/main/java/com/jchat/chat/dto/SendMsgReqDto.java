package com.jchat.chat.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SendMsgReqDto {

    // 임시 번호
    private String tempId;
    // 채팅방 번호
    private Long roomId;
    // 채팅방 이름
    private String roomName;
    // 메세지 내용
    @NotBlank
    private String msgContent;

    // 초대할 친구 유저 리스트
    List<InviteUserListDto> inviteUserList;

    @Data
    public static class InviteUserListDto {
        private Long userNo;
    }
}
