package com.jchat.mem.service;

import com.jchat.mem.dto.*;
import com.jchat.mem.mapper.MemFriendMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemFriendService {

    private final MemFriendMapper memFriendMapper;

    @Value("${cloudflare.r2.cdn-host}")
    private String cdnHost;

    /**
     * 회원정보조회
     * @param {{@link SearchUserReqDto}} reqDto
     * @return {{@link SearchUserResDto}} resDto
     */
    public List<SearchUserListResDto> searchUserList(SearchUserListReqDto reqDto) {



        return null;
    }

    /**
     * @param {{@link Long} userNo 회원번호
     * @param userNo
     */
    public SearchFriendListResDto searchFriendList(Long userNo) {

        List<ComOtherUser> comOtherUser = memFriendMapper.searchFriendList(userNo);

        // 대표프로필이미지
        for (ComOtherUser otherUser : comOtherUser) {
            otherUser.setProfileImgUrl(cdnHost + otherUser.getProfileImgUrl());
        }

        return SearchFriendListResDto.builder()
                .myUserNo(userNo)
                .friendList(comOtherUser)
                .build();
    }

    /**
     * 친구상태변경
     * @param {{@link MergeFriendReqDto}} reqDto
     * @return {{@link MergeFriendResDto}}
     */
    @Transactional
    public MergeFriendResDto mergeFriend(MergeFriendReqDto reqDto) {

        int rst = memFriendMapper.mergeFriend(reqDto);

        if (rst != 1) {
            throw new IllegalArgumentException("실패 트랜잭션 롤백 실행");
        }

        return MergeFriendResDto.builder()
                .succYn("Y")
                .userNo(reqDto.getUserNo())
                .relationUserNo(reqDto.getRelationUserNo())
                .build();
    }

}
