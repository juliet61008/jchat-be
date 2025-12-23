package com.jchat.mem.service;

import com.jchat.mem.dto.*;
import com.jchat.mem.mapper.MemFriendMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemFriendService {

    private final MemFriendMapper memFriendMapper;

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

        return SearchFriendListResDto.builder()
                .myUserNo(userNo)
                .friendList(comOtherUser)
                .build();
    }

    /**
     * 친구추가
     * @param {{@link InsertFriendReqDto}} reqDto
     * @return {{@link InsertFriendResDto}}
     */
    @Transactional
    public InsertFriendResDto insertFriend(InsertFriendReqDto reqDto) throws Exception {

        int rst = memFriendMapper.insertFriend(reqDto);

        if (rst != 0) {
            throw new IllegalArgumentException("실패 트랜잭션 롤백 실행");
        }

        return InsertFriendResDto.builder()
                .succYn("Y")
                .userNo(reqDto.getUserNo())
                .relationUserNo(reqDto.getRelationUserNo())
                .build();
    }

    /**
     * 친구상태변경
     * @param {{@link UpdateFriendReqDto}} reqDto
     * @return {{@link UpdateFriendResDto}}
     */
    @Transactional
    public UpdateFriendResDto updateFriend(UpdateFriendReqDto reqDto) throws Exception {

        int rst = memFriendMapper.updateFriend(reqDto);

        if (rst != 0) {
            throw new IllegalArgumentException("실패 트랜잭션 롤백 실행");
        }

        return UpdateFriendResDto.builder()
                .succYn("Y")
                .userNo(reqDto.getUserNo())
                .relationUserNo(reqDto.getRelationUserNo())
                .build();
    }

}
