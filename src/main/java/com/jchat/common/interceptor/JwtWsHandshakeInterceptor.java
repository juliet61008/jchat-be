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
 * 주의: STOMP 사용 시 이 인터셉터는 connectHeaders를 받을 수 없음
 * Authorization 헤더는 JwtWsChannelInterceptor에서 처리됩니다
 *
 * 역할:
 * - WebSocket HTTP 업그레이드 시점의 기본 검증
 * - Query Parameter 기반 인증 (선택사항)
 *
 * 인증 정책:
 * - 현재는 모든 연결 허용 (인증은 ChannelInterceptor에서 처리)
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

            String origin = httpRequest.getHeader("Origin");
            log.info("WebSocket 핸드셰이크 - Origin: {}", origin);
            //  Query Parameter로 토큰을 받는 경우만 여기서 처리
            String tokenFromQuery = httpRequest.getParameter("token");

            if (tokenFromQuery != null && !tokenFromQuery.isEmpty()) {
                log.info("Query Parameter에서 토큰 발견");

                if (jwtUtil.validateToken(tokenFromQuery)) {
                    UserInfoDto userInfo = jwtUtil.getUserInfoFromToken(tokenFromQuery);
                    attributes.put("userInfo", userInfo);
                    attributes.put("userNo", userInfo.getUserNo());
                    attributes.put("id", userInfo.getId());
                    attributes.put("name", userInfo.getName());
                    attributes.put("birth", userInfo.getBirth());
                    attributes.put("authenticated", true);
                    log.info("Query Parameter 토큰 인증 성공 - id: {}", userInfo.getId());
                } else {
                    log.warn("Query Parameter 토큰 유효하지 않음");
                    return reject401(response, "유효하지 않은 토큰입니다");
                }
            } else {
                // !!! 수정: 토큰 없어도 연결 허용 (STOMP CONNECT에서 인증할 예정) !!!
                log.info("토큰 없음 - STOMP CONNECT에서 인증 예정");
            }

            return true;
        }

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