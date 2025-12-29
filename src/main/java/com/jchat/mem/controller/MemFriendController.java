package com.jchat.mem.controller;

import com.jchat.common.context.UserContext;
import com.jchat.mem.dto.*;
import com.jchat.mem.service.MemFriendService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mem/friend")
public class MemFriendController {

    private final MemFriendService memFriendService;

    /**
     * 친구목록 조회
     * @return
     */
    @GetMapping("/list")
    public ResponseEntity<?> searchFriendList() {

        if (!UserContext.hasUser()) {
            return ResponseEntity.status(401)
                .body(Map.of("code", 401, "message", "No access token"));
        }

        return ResponseEntity.ok(memFriendService.searchFriendList(UserContext.getUserNo()));
    }

    /**
     * 친구추가
     * @param reqDto
     * @return
     */
    @PostMapping("/insert")
    public ResponseEntity<?> insertFriend(@Valid MergeFriendReqDto reqDto) {

        try {
            reqDto.setUserNo(UserContext.getUserNo());

            MergeFriendResDto resDto = memFriendService.mergeFriend(reqDto);

            return ResponseEntity.ok(resDto);
        } catch (Exception e) {
            return ResponseEntity.ok(MergeFriendResDto.builder()
                    .succYn("N")
                    .userNo(reqDto.getUserNo())
                    .relationUserNo(reqDto.getRelationUserNo())
                    .build());
        }

    }

    /**
     * 친구상태변경
     * @param reqDto
     * @return
     */
    @PostMapping("/update")
    public ResponseEntity<?> updateFriend(@Valid UpdateFriendReqDto reqDto) {

        try {
            UpdateFriendResDto resDto = memFriendService.updateFriend(reqDto);

            return ResponseEntity.ok(resDto);
        } catch (Exception e) {
            return ResponseEntity.ok(UpdateFriendResDto.builder()
                    .succYn("N")
                    .userNo(reqDto.getUserNo())
                    .relationUserNo(reqDto.getRelationUserNo())
                    .build());
        }

    }

}
