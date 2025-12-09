package com.jchat.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket 설정
 */
@Configuration
@EnableWebSocketMessageBroker  // WebSocket 메시지 브로커 활성화
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * 메시지 브로커 설정
     * - 클라이언트가 메시지를 보내고 받을 경로 설정
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 메시지 구독 경로 (클라이언트가 메시지를 받을 경로)
        // 예: /topic/chat/1 → 1번 채팅방 구독
        registry.enableSimpleBroker("/topic");

        // 메시지 발행 경로 (클라이언트가 메시지를 보낼 경로)
        // 예: /app/chat.send → ChatController로 라우팅
        registry.setApplicationDestinationPrefixes("/app");
    }

    /**
     * WebSocket 연결 엔드포인트 설정
     * - 클라이언트가 WebSocket에 연결할 URL
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint("/ws")  // ws://localhost:8080/ws
                .setAllowedOriginPatterns("*")  // CORS 허용 (개발용)
                .withSockJS();  // SockJS 폴백 활성화
    }
}