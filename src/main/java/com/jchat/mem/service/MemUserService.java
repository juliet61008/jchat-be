package com.jchat.mem.service;

import com.jchat.auth.dto.UserInfoDto;
import com.jchat.common.constants.CommonConstants;
import com.jchat.common.context.UserContext;
import com.jchat.mem.dto.*;
import com.jchat.mem.mapper.MemUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemUserService {

    private final MemUserMapper memUserMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원정보조회
     * @param {{@link SearchUserReqDto}} reqDto
     * @return {{@link SearchUserResDto}} resDto
     */
    public SearchUserResDto searchUser(SearchUserReqDto reqDto) {

        SearchUserResDto resDto = new SearchUserResDto();

        if ("juliet61008".equals(reqDto.getId())) {
            resDto.setId("juliet61008");
            resDto.setName("한재경");
        } else {
            resDto.setId("who");
            resDto.setName("누구");
        }

        return resDto;
    }

    /**
     * 회원정보조회
     * @param {{@link SearchUserReqDto}} reqDto
     * @return {{@link SearchUserResDto}} resDto
     */
    public List<SearchUserListResDto> searchUserList(SearchUserListReqDto reqDto) {

        List<SearchUserListResDto> resDto = memUserMapper.searchUserList(reqDto);


        return resDto;
    }

    /**
     * 회원가입
     * @param {{@link RegisterUserReqDto}} reqDto
     * @return {{@link RegisterUserResDto}} resDto
     */
    @Transactional
    public RegisterUserResDto registerUser(RegisterUserReqDto reqDto) {

        // 요청 DTO null체크
        if (null == reqDto) {
            throw new IllegalArgumentException("잘못된 요청입니다.");
        }

        // BCrypt 암호화
        reqDto.setPassword(passwordEncoder.encode(reqDto.getPassword()));

        // insert 요청
        int cnt = memUserMapper.insertUser(reqDto);

        // 정상 insert 아닌 경우
        if (1 > cnt) {
            return RegisterUserResDto.builder()
                    .succYn(CommonConstants.N)
                    .build();
        }

        // 정상 insert
        return RegisterUserResDto.builder()
                .succYn(CommonConstants.Y)
                .id(reqDto.getId())
                .build();
    }
}
