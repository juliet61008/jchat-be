package com.jchat.common.context;

import com.jchat.auth.dto.UserInfoDto;

/**
 * ThreadLocal을 이용한 사용자 컨텍스트
 * 요청 스레드 내에서 어디서든 현재 사용자 정보에 접근 가능
 */
public class UserContext {

    private static final ThreadLocal<UserInfoDto> userInfoHolder = new ThreadLocal<>();

    /**
     * 현재 스레드에 사용자 정보 저장
     */
    public static void setUserInfo(UserInfoDto userInfo) {
        userInfoHolder.set(userInfo);
    }

    /**
     * 현재 스레드의 사용자 정보 조회
     */
    public static UserInfoDto getUserInfo() {
        return userInfoHolder.get();
    }

    /**
     * 현재 사용자 유저 번호 조회
     */
    public static Long getUserNo() {
        UserInfoDto userInfo = userInfoHolder.get();
        return userInfo != null ? userInfo.getUserNo() : null;
    }

    /**
     * 현재 사용자 ID 조회
     */
    public static String getId() {
        UserInfoDto userInfo = userInfoHolder.get();
        return userInfo != null ? userInfo.getId() : null;
    }

    /**
     * 현재 사용자 이름 조회
     */
    public static String getUserName() {
        UserInfoDto userInfo = userInfoHolder.get();
        return userInfo != null ? userInfo.getName() : null;
    }

    /**
     * 현재 사용자 생년월일 조회
     */
    public static Integer getUserBirth() {
        UserInfoDto userInfo = userInfoHolder.get();
        return userInfo != null ? userInfo.getBirth() : null;
    }

    /**
     * 현재 스레드의 사용자 정보 제거 (메모리 누수 방지)
     */
    public static void clear() {
        userInfoHolder.remove();
    }

    /**
     * 사용자 정보 존재 여부 확인
     */
    public static boolean hasUser() {
        return userInfoHolder.get() != null;
    }
}