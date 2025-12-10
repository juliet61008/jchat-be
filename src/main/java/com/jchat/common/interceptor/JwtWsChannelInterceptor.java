package com.jchat.common.interceptor;

import com.jchat.auth.dto.UserInfoDto;
import com.jchat.common.context.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * WebSocket 메시지 채널 인터셉터
 *
 * 역할:
 * - 클라이언트 → 서버로 들어오는 모든 메시지 가로채기
 * - 경로별 인증 체크 (public/** 제외)
 * - UserContext에 사용자 정보 설정 및 정리
 * - STOMP 명령어별 처리 (CONNECT, SEND, DISCONNECT)
 *
 * 실행 시점:
 * - 메시지가 전송될 때마다 실행 (CONNECT, SEND, SUBSCRIBE, DISCONNECT 등)
 * - @MessageMapping 실행 전에 preSend() 실행
 * - 메시지 처리 완료 후 postSend() 실행
 *
 * 인증 정책:
 * - /app/public/** : 인증 불필요 (익명 사용자 접근 가능)
 * - 그 외 모든 경로 : 인증 필수 (authenticated = true 필요)
 */
@Slf4j
@Component
public class JwtWsChannelInterceptor implements ChannelInterceptor {

    /**
     * 인증 없이 접근 가능한 Public 경로 프리픽스
     * /app/public/** 형태의 모든 경로는 인증 생략
     */
    private static final String PUBLIC_PATH_PREFIX = "/app/public/";

    /**
     * 메시지 전송 전 전처리
     * - 인증 체크
     * - UserContext 세팅
     *
     * @param message STOMP 메시지
     * @param channel 메시지 채널
     * @return 처리할 메시지 (null 반환 시 메시지 차단)
     */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null) {
            StompCommand command = accessor.getCommand();

            // STOMP 명령어별 처리
            if (StompCommand.CONNECT.equals(command)) {
                handleConnect(accessor);
            }
            else if (StompCommand.SEND.equals(command)) {
                handleSend(accessor);
            }
            else if (StompCommand.DISCONNECT.equals(command)) {
                handleDisconnect(accessor);
            }
        }

        return message;
    }

    /**
     * 메시지 전송 후 후처리
     * - UserContext 정리 (ThreadLocal 메모리 누수 방지)
     *
     * @param message STOMP 메시지
     * @param channel 메시지 채널
     * @param sent 전송 성공 여부
     */
    @Override
    public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
        // 메시지 처리 완료 후 반드시 UserContext 정리
        // ThreadLocal 특성상 스레드 재사용 시 이전 데이터가 남을 수 있음
        UserContext.clear();
    }

    /**
     * CONNECT 명령어 처리
     * - WebSocket 연결 후 STOMP 프로토콜 연결 시
     * - 인증/익명 구분 없이 모두 허용 (로깅만 수행)
     *
     * @param accessor STOMP 헤더 접근자
     */
    private void handleConnect(StompHeaderAccessor accessor) {
        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
        Boolean authenticated = (Boolean) sessionAttributes.get("authenticated");

        if (Boolean.TRUE.equals(authenticated)) {
            Long userId = (Long) sessionAttributes.get("userId");
            log.info("WebSocket CONNECT - 인증된 사용자 (userId: {})", userId);
        } else {
            log.info("WebSocket CONNECT - 익명 사용자");
        }
    }

    /**
     * SEND 명령어 처리
     * - 클라이언트가 메시지를 보낼 때
     * - @MessageMapping 실행 전 경로별 인증 체크 및 UserContext 세팅
     *
     * @param accessor STOMP 헤더 접근자
     */
    private void handleSend(StompHeaderAccessor accessor) {
        String destination = accessor.getDestination();

        // 1. Public 경로는 인증 생략 (익명 사용자 접근 가능)
        if (destination != null && isPublicPath(destination)) {
            log.debug("Public 경로 - 인증 생략: {}", destination);
            return;
        }

        // 2. Private 경로는 인증 필수
        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
        Boolean authenticated = (Boolean) sessionAttributes.get("authenticated");

        // 인증되지 않은 사용자가 Private 경로 접근 시도
        if (!Boolean.TRUE.equals(authenticated)) {
            log.warn("인증되지 않은 사용자의 Private 경로 접근 시도 - destination: {}", destination);
            throw new IllegalArgumentException("Unauthorized - Authentication required for this endpoint");
        }

        // 3. UserContext에 사용자 정보 설정
        // @MessageMapping, Service 등에서 UserContext.getUserId() 사용 가능
        UserInfoDto userInfo = (UserInfoDto) sessionAttributes.get("userInfo");
        UserContext.setUserInfo(userInfo);

        log.debug("인증 완료 - userId: {}, destination: {}",
                userInfo.getId(), destination);
    }

    /**
     * DISCONNECT 명령어 처리
     * - WebSocket 연결 종료 시
     * - UserContext 정리 및 로깅
     *
     * @param accessor STOMP 헤더 접근자
     */
    private void handleDisconnect(StompHeaderAccessor accessor) {
        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
        Boolean authenticated = (Boolean) sessionAttributes.get("authenticated");
        Long userId = sessionAttributes != null ? (Long) sessionAttributes.get("userId") : null;

        // UserContext 정리
        UserContext.clear();

        if (Boolean.TRUE.equals(authenticated)) {
            log.info("WebSocket DISCONNECT - 인증된 사용자 (userId: {})", userId);
        } else {
            log.info("WebSocket DISCONNECT - 익명 사용자");
        }
    }

    /**
     * Public 경로 여부 확인
     * /app/public/** 패턴에 매칭되는지 체크
     *
     * @param destination 목적지 경로 (예: /app/public/announce)
     * @return true: Public 경로, false: Private 경로
     */
    private boolean isPublicPath(String destination) {
        return destination.startsWith(PUBLIC_PATH_PREFIX);
    }
}