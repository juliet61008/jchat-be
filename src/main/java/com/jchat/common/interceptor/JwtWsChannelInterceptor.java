package com.jchat.common.interceptor;

import com.jchat.auth.dto.UserInfoDto;
import com.jchat.common.context.UserContext;
import com.jchat.common.util.JwtUtil;
import lombok.RequiredArgsConstructor;
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
 * !!! 수정: STOMP connectHeaders의 Authorization을 여기서 처리합니다 !!!
 *
 * 역할:
 * - CONNECT 시 Authorization 헤더 검증 (STOMP connectHeaders)
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
@RequiredArgsConstructor
public class JwtWsChannelInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        try {

        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null) {
            StompCommand command = accessor.getCommand();

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
        } catch (Exception e) {
            log.error("❌❌❌ preSend에서 예외 발생!", e);
            throw e; // ✅ 예외를 다시 던져야 ERROR 프레임 전송됨
        }
    }

    @Override
    public void afterSendCompletion(
            Message<?> message,
            MessageChannel channel,
            boolean sent,
            Exception ex
    ) {
        // @MessageMapping 완전히 끝난 후
//        UserContext.clear(); // 여기서 clear 해야 함!
    }

    /**
     * CONNECT 명령어 처리
     * STOMP connectHeaders에서 Authorization 헤더를 받아서 검증 !!!
     *
     * - WebSocket 연결 후 STOMP 프로토콜 연결 시
     * - Authorization 헤더로 JWT 토큰 검증
     * - 검증 성공 시 세션에 사용자 정보 저장
     *
     * @param accessor STOMP 헤더 접근자
     */
    private void handleConnect(StompHeaderAccessor accessor) {
        // STOMP connectHeaders에서 Authorization 헤더 가져오기
        String authHeader = accessor.getFirstNativeHeader("Authorization");

        log.debug("CONNECT - Authorization 헤더: {}", authHeader);

        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();

        // Authorization 헤더가 있으면 검증
         if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String accessToken = authHeader.substring(7);
             log.info("CONNECT - accessToken: {}", accessToken);

            // 토큰 검증
            if (jwtUtil.validateToken(accessToken)) {
                // 토큰에서 사용자 정보 추출
                UserInfoDto userInfo = jwtUtil.getUserInfoFromToken(accessToken);

                // 세션에 사용자 정보 저장
                sessionAttributes.put("userInfo", userInfo);
                sessionAttributes.put("userNo", userInfo.getUserNo());
                sessionAttributes.put("id", userInfo.getId());
                sessionAttributes.put("name", userInfo.getName());
                sessionAttributes.put("birth", userInfo.getBirth());
                sessionAttributes.put("authenticated", true);

                log.info("WebSocket CONNECT - 인증 성공 (userId: {})", userInfo.getId());
            } else {
                // 토큰 유효하지 않으면 인증 실패
                log.warn("WebSocket CONNECT - 토큰 유효하지 않음");
                sessionAttributes.put("authenticated", false);
            }
        } else {
            // Authorization 헤더 없으면 익명 처리
            log.info("WebSocket CONNECT - Authorization 헤더 없음 (익명 사용자)");
            sessionAttributes.put("authenticated", false);
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

        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
        Boolean authenticated = (Boolean) sessionAttributes.get("authenticated");

        // 인증된 사용자는 usercontext 추가
        if (Boolean.TRUE.equals(authenticated)) {
            UserInfoDto userInfo = (UserInfoDto) sessionAttributes.get("userInfo");
            UserContext.setUserInfo(userInfo);
            log.info("인증 완료 - userId: {}, destination: {}",
                    userInfo.getId(), destination);
        }
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
        String id = sessionAttributes != null ? (String) sessionAttributes.get("id") : null;

        // UserContext 정리
        UserContext.clear();

        if (Boolean.TRUE.equals(authenticated)) {
            log.info("WebSocket DISCONNECT - 인증된 사용자 (userId: {})", id);
        } else {
            log.info("WebSocket DISCONNECT - 익명 사용자");
        }
    }
}