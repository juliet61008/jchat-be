package com.jchat.gemini.controller;

import com.jchat.common.advice.CustomExceptionHandler;
import com.jchat.gemini.exception.GeminiApiException;
import com.jchat.gemini.exception.GeminiRateLimitException;
import com.jchat.gemini.service.GeminiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * GeminiController 단위 테스트 (standaloneSetup 방식)
 *
 * 핵심 개념:
 * - standaloneSetup: Spring 컨텍스트를 전혀 올리지 않고 컨트롤러만 테스트
 *   → @WebMvcTest처럼 WebSocketConfig, SecurityConfig 등이 끼어들지 않음
 *   → 외부 의존성(Security, WebSocket 등) 설정 없이 순수하게 컨트롤러 로직만 검증
 * - setControllerAdvice: @ControllerAdvice 빈을 직접 등록해서 예외 처리도 검증 가능
 *
 * @WebMvcTest와 비교:
 *   - @WebMvcTest: Spring 컨텍스트 올림 → Security, WebConfig 등 전부 로드됨 (복잡)
 *   - standaloneSetup: Spring 컨텍스트 없음 → 진짜 컨트롤러만 테스트 (단순하고 빠름)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("GeminiController 단위 테스트")
class GeminiControllerTest {

    @Mock
    private GeminiService geminiService;

    @InjectMocks
    private GeminiController geminiController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        // standaloneSetup: GeminiController만 등록, CustomExceptionHandler도 수동 등록
        // Security, WebSocket, JWT 등 다른 설정 일절 없음
        mockMvc = MockMvcBuilders
                .standaloneSetup(geminiController)
                .setControllerAdvice(new CustomExceptionHandler())
                .build();
    }

    // ===================================================================
    // 성공 케이스
    // ===================================================================

    @Test
    @DisplayName("GET /gemini/test - prompt 파라미터 전달 시 200 OK와 AI 응답 텍스트 반환")
    void test_success() throws Exception {
        // given
        String prompt = "스프링부트가 뭐야?";
        String aiResponse = "스프링부트는 자바 기반의 웹 프레임워크입니다.";
        given(geminiService.generateText(prompt)).willReturn(aiResponse);

        // when & then
        mockMvc.perform(get("/gemini/test")
                        .param("prompt", prompt))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(aiResponse));
    }

    @Test
    @DisplayName("GET /gemini/test - prompt 파라미터 없이 요청해도 서비스를 호출한다")
    void test_withNullPrompt() throws Exception {
        // given - prompt 없으면 TestReqDto.prompt가 null (유효성 어노테이션 없음)
        given(geminiService.generateText(null)).willReturn("기본 응답");

        // when & then
        mockMvc.perform(get("/gemini/test"))
                .andExpect(status().isOk())
                .andExpect(content().string("기본 응답"));
    }

    // ===================================================================
    // 예외 케이스 - CustomExceptionHandler가 처리
    // ===================================================================

    @Test
    @DisplayName("GeminiApiException 발생 시 CustomExceptionHandler.handleException()이 처리한다")
    void test_geminiApiException() throws Exception {
        // given
        given(geminiService.generateText(any()))
                .willThrow(new GeminiApiException("API 키가 유효하지 않습니다", 401));

        // when & then
        // CustomExceptionHandler.handleException()이 GeminiApiException을 받아
        // ApiResponse.error(-1, message) 형태의 JSON을 반환
        mockMvc.perform(get("/gemini/test")
                        .param("prompt", "테스트"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(-1))
                .andExpect(jsonPath("$.message").value("API 키가 유효하지 않습니다"));
    }

    @Test
    @DisplayName("GeminiRateLimitException 발생 시 CustomExceptionHandler.handleException()이 처리한다")
    void test_rateLimitException() throws Exception {
        // given
        given(geminiService.generateText(any()))
                .willThrow(new GeminiRateLimitException("Rate Limit 초과. 60초 후 재시도하세요", 60));

        // when & then
        mockMvc.perform(get("/gemini/test")
                        .param("prompt", "테스트"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(-1))
                .andExpect(jsonPath("$.message").value("Rate Limit 초과. 60초 후 재시도하세요"));
    }
}
