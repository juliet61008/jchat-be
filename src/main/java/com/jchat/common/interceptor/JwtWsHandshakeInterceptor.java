package com.jchat.common.interceptor;

import com.jchat.auth.dto.UserInfoDto;
import com.jchat.common.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * WebSocket 핸드셰이크 인터셉터
 *
 * 역할:
 * - WebSocket 연결 시도 시 (GET /ws) HTTP 단계에서 토큰 검증
 * - 토큰이 있으면 검증 후 사용자 정보를 Session Attributes에 저장
 * - 토큰이 없어도 연결은 허용 (익명 사용자)
 * - Public API도 지원하기 위해 모든 연결 허용
 *
 * 실행 시점:
 * - 클라이언트가 WebSocket 연결을 시도할 때 1번만 실행
 * - HTTP 프로토콜 단계이므로 쿠키 접근 가능
 *
 * 인증 정책:
 * - 토큰 있음 + 유효: 인증된 사용자로 Session 저장
 * - 토큰 없음 or 무효: 익명 사용자로 연결 허용
 * - 경로별 인증 체크는 JwtWsChannelInterceptor에서 수행
 *
 * 향후 확장:
 * - 토큰 갱신 로직 추가 예정
 * - refreshToken을 이용한 accessToken 자동 갱신
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtWsHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtUtil jwtUtil;

    /**
     * WebSocket 핸드셰이크 전처리
     *
     * @param request HTTP 요청 (쿠키 접근 가능)
     * @param response HTTP 응답
     * @param wsHandler WebSocket 핸들러
     * @param attributes WebSocket Session에 저장될 속성들
     * @return true: 연결 허용 (항상 true - Public API 지원)
     */
    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes) throws Exception {

        log.debug("WebSocket 핸드셰이크 시작");

        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            HttpServletRequest httpRequest = servletRequest.getServletRequest();

            // 1. 쿠키에서 accessToken 추출 (있을 수도 없을 수도 있음)
            String token = extractTokenFromCookie(httpRequest, "accessToken");

            // 2. 토큰이 있고 유효한 경우 - 인증된 사용자
            if (token != null && jwtUtil.validateToken(token)) {
                UserInfoDto userInfo = jwtUtil.getUserInfoFromToken(token);

                // WebSocket Session Attributes에 사용자 정보 저장
                attributes.put("userInfo", userInfo);
                attributes.put("userId", userInfo.getId());
                attributes.put("userName", userInfo.getName());
                attributes.put("userBirth", userInfo.getBirth());
                attributes.put("authenticated", true);  // 인증 여부 표시

                log.info("인증된 사용자 연결 - userId: {}, userName: {}",
                        userInfo.getId(), userInfo.getName());

                // TODO: 토큰 갱신 로직 추가 예정
                // - 토큰 만료 시간이 임박한 경우 refreshToken으로 갱신
                // - 갱신된 accessToken을 응답 쿠키에 설정
            }
            // 3. 토큰이 없거나 유효하지 않은 경우 - 익명 사용자
            else {
                attributes.put("authenticated", false);  // 미인증 상태 표시

                if (token != null) {
                    log.info("유효하지 않은 토큰으로 연결 시도 - 익명 사용자로 처리");
                } else {
                    log.info("토큰 없이 연결 - 익명 사용자");
                }
            }
        }

        // 항상 연결 허용 (경로별 인증은 ChannelInterceptor에서 처리)
        return true;
    }

    /**
     * WebSocket 핸드셰이크 후처리
     *
     * @param exception 핸드셰이크 중 발생한 예외 (없으면 null)
     */
    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception) {

        if (exception != null) {
            log.error("WebSocket 핸드셰이크 에러", exception);
        } else {
            log.debug("WebSocket 핸드셰이크 완료");
        }
    }

    /**
     * 쿠키에서 특정 이름의 값을 추출
     *
     * @param request HTTP 요청
     * @param cookieName 추출할 쿠키 이름
     * @return 쿠키 값 (없으면 null)
     */
    private String extractTokenFromCookie(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}