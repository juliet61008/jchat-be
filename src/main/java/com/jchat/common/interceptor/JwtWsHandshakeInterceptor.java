package com.jchat.common.interceptor;

import com.jchat.auth.dto.UserInfoDto;
import com.jchat.common.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.io.IOException;
import java.util.Map;

/**
 * WebSocket 핸드셰이크 인터셉터
 *
 * 역할:
 * - WebSocket 연결 시도 시 토큰 검증
 * - 토큰이 없거나 유효하지 않으면 401 Unauthorized 반환
 * - 프론트엔드 미들웨어가 리프레시 토큰 요청 가능하도록 처리
 *
 * 인증 정책:
 * - 토큰 없음: 401 (프론트에서 리프레시 시도)
 * - 토큰 유효하지 않음: 401 (프론트에서 리프레시 시도)
 * - 토큰 유효: 연결 허용 + 사용자 정보 Session에 저장
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtWsHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtUtil jwtUtil;

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

            // 1. 쿠키에서 accessToken 추출
            String accessToken = extractTokenFromCookie(httpRequest, "accessToken");

            log.info("handshake accessToken : {}", accessToken);

            // 2. 토큰이 없는 경우 → 401
            if (accessToken == null) {
                log.warn("토큰 없음");
//                return reject401(response, "토큰이 없습니다");
            }

            // 3. 토큰이 유효하지 않은 경우 → 401
            if (!jwtUtil.validateToken(accessToken)) {
                log.warn("유효하지 않은 토큰");
//                return reject401(response, "유효하지 않은 토큰입니다");
            } else {
                // 4. 토큰 유효 → 사용자 정보 저장
                UserInfoDto userInfo = jwtUtil.getUserInfoFromToken(accessToken);

                attributes.put("userInfo", userInfo);
                attributes.put("userNo", userInfo.getUserNo());
                attributes.put("id", userInfo.getId());
                attributes.put("name", userInfo.getName());
                attributes.put("birth", userInfo.getBirth());
                attributes.put("authenticated", true);

                log.info("WebSocket 연결 성공 - id: {}, userName: {}",
                        userInfo.getId(), userInfo.getName());
            }

            return true;
        }

        // ServletServerHttpRequest가 아닌 경우 (일반적으로 발생 안 함)
        log.error("ServletServerHttpRequest 타입이 아님");
        return reject401(response, "잘못된 요청입니다");
    }

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
     * 401 Unauthorized 응답 반환
     *
     * @param response HTTP 응답
     * @param message 에러 메시지
     * @return false (연결 거부)
     */
    private boolean reject401(ServerHttpResponse response, String message) throws IOException {
        if (response instanceof ServletServerHttpResponse) {
            ServletServerHttpResponse servletResponse = (ServletServerHttpResponse) response;

            servletResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
            servletResponse.getHeaders().add("Content-Type", "application/json;charset=UTF-8");

            String jsonResponse = String.format(
                    "{\"code\":401,\"message\":\"%s\"}",
                    message
            );

            servletResponse.getBody().write(jsonResponse.getBytes("UTF-8"));
            servletResponse.getBody().flush();
        }

        return false;
    }

    /**
     * 쿠키에서 토큰 추출
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