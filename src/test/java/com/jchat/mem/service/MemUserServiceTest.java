package com.jchat.mem.service;

import com.jchat.mem.dto.SearchUserListReqDto;
import com.jchat.mem.dto.SearchUserListResDto;
import com.jchat.mem.dto.SearchUserReqDto;
import com.jchat.mem.dto.SearchUserResDto;
import com.jchat.mem.mapper.MemUserMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("MemUserService 단위테스트")
class MemUserServiceTest {

    @InjectMocks
    MemUserService memUserService;

    @Mock
    MemUserMapper memUserMapper;

    @Test
    @DisplayName("searchUser() 기본 테스트 - 성공")
    void searchUser_succ() {
        SearchUserReqDto reqDto = new SearchUserReqDto();
        reqDto.setId("juliet61008");

        SearchUserResDto resDto = memUserService.searchUser(reqDto);

        assertThat(resDto.getId()).isEqualTo("juliet61008");
        assertThat(resDto.getName()).isEqualTo("두재경");
    }

    void searchUser_fail() {
        SearchUserReqDto reqDto = new SearchUserReqDto();
        reqDto.setId("who");

        SearchUserResDto resDto = memUserService.searchUser(reqDto);

        assertThat(resDto.getId()).isNotEqualTo("juliet61008");
    }

    @Test
    @DisplayName("searchUserList - 성공")
    void searchUserList_succ() {
        SearchUserListReqDto reqDto = new SearchUserListReqDto();
        reqDto.setUserNo(1L);

        List<SearchUserListResDto> mockData = new ArrayList<>();
        mockData.add(SearchUserListResDto.builder().userNo(3L).build());

        given(memUserMapper.searchUserList(reqDto)).willReturn(mockData);

        List<SearchUserListResDto> resDto = memUserService.searchUserList(reqDto);

        assertThat(resDto).isEqualTo(mockData);
    }
}