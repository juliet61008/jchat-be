package com.jchat.gemini.service;

import com.jchat.gemini.client.GeminiClient;
import com.jchat.gemini.dto.GeminiRequest;
import com.jchat.gemini.dto.GeminiResponse;
import com.jchat.gemini.exception.GeminiApiException;
import com.jchat.gemini.exception.GeminiRateLimitException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

/**
 * GeminiService 단위 테스트
 *
 * 핵심 개념:
 * - @ExtendWith(MockitoExtension.class): Spring Context 없이 Mockito만 사용
 * - @Mock: 가짜 객체 생성 (실제 GeminiClient 호출 X)
 * - @InjectMocks: @Mock 객체들을 주입해서 실제 서비스 객체 생성
 * - ReflectionTestUtils: @Value 처럼 private 필드 값을 주입할 때 사용
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("GeminiService 단위 테스트")
class GeminiServiceTest {

    @Mock
    private GeminiClient geminiClient;

    @InjectMocks
    private GeminiService geminiService;

    @BeforeEach
    void setUp() {
        // @Value("${gemini.api-key}") private String apiKey 에 테스트용 값 주입
        ReflectionTestUtils.setField(geminiService, "apiKey", "test-api-key");
    }

    // ===================================================================
    // generateText() 테스트
    // ===================================================================
    @Nested
    @DisplayName("generateText() - 기본 텍스트 생성")
    class GenerateText {

        @Test
        @DisplayName("정상 프롬프트 입력 시 Gemini API를 호출하고 텍스트를 반환한다")
        void success() {
            // given (준비)
            String prompt = "안녕하세요";
            String expectedText = "안녕하세요! 무엇을 도와드릴까요?";
            GeminiResponse mockResponse = createMockResponse(expectedText);

            // geminiClient.generateContent()가 호출되면 mockResponse를 반환하도록 설정
            given(geminiClient.generateContent(eq("test-api-key"), any(GeminiRequest.class)))
                    .willReturn(mockResponse);

            // when (실행)
            String result = geminiService.generateText(prompt);

            // then (검증)
            assertThat(result).isEqualTo(expectedText);
            // API가 정확히 1번 호출되었는지 검증
            then(geminiClient).should(times(1))
                    .generateContent(eq("test-api-key"), any(GeminiRequest.class));
        }

        @Test
        @DisplayName("응답이 null이면 GeminiApiException(500)을 던진다")
        void throwsException_whenResponseIsNull() {
            // given
            given(geminiClient.generateContent(any(), any())).willReturn(null);

            // when & then
            assertThatThrownBy(() -> geminiService.generateText("test"))
                    .isInstanceOf(GeminiApiException.class)
                    .hasMessageContaining("응답이 비어있습니다");
        }

        @Test
        @DisplayName("candidates가 빈 리스트이면 GeminiApiException(500)을 던진다")
        void throwsException_whenCandidatesEmpty() {
            // given
            GeminiResponse emptyResponse = new GeminiResponse(List.of(), null, null);
            given(geminiClient.generateContent(any(), any())).willReturn(emptyResponse);

            // when & then
            assertThatThrownBy(() -> geminiService.generateText("test"))
                    .isInstanceOf(GeminiApiException.class)
                    .hasMessageContaining("응답이 비어있습니다");
        }

        @Test
        @DisplayName("content.parts가 없으면 GeminiApiException을 던진다")
        void throwsException_whenPartsEmpty() {
            // given - parts가 없는 응답 구성
            GeminiResponse.Content emptyContent = new GeminiResponse.Content(List.of(), "model");
            GeminiResponse.Candidate candidate = new GeminiResponse.Candidate(emptyContent, "STOP", 0, null);
            GeminiResponse response = new GeminiResponse(List.of(candidate), null, null);

            given(geminiClient.generateContent(any(), any())).willReturn(response);

            // when & then
            assertThatThrownBy(() -> geminiService.generateText("test"))
                    .isInstanceOf(GeminiApiException.class)
                    .hasMessageContaining("응답 내용을 파싱할 수 없습니다");
        }

        @Test
        @DisplayName("Rate Limit 초과 시 GeminiRateLimitException이 그대로 전파된다")
        void propagates_rateLimitException() {
            // given
            GeminiRateLimitException rateLimitEx = new GeminiRateLimitException("Rate Limit 초과", 60);
            given(geminiClient.generateContent(any(), any())).willThrow(rateLimitEx);

            // when & then
            assertThatThrownBy(() -> geminiService.generateText("test"))
                    .isInstanceOf(GeminiRateLimitException.class)
                    .extracting(ex -> ((GeminiRateLimitException) ex).getRetryAfterSeconds())
                    .isEqualTo(60);
        }

        @Test
        @DisplayName("Gemini API 오류 시 GeminiApiException이 그대로 전파된다")
        void propagates_geminiApiException() {
            // given
            given(geminiClient.generateContent(any(), any()))
                    .willThrow(new GeminiApiException("API 서버 오류", 500));

            // when & then
            assertThatThrownBy(() -> geminiService.generateText("test"))
                    .isInstanceOf(GeminiApiException.class)
                    .hasMessageContaining("API 서버 오류");
        }
    }

    // ===================================================================
    // generateTextWithConfig() 테스트
    // ===================================================================
    @Nested
    @DisplayName("generateTextWithConfig() - 설정 포함 텍스트 생성")
    class GenerateTextWithConfig {

        @Test
        @DisplayName("temperature와 maxTokens를 지정하면 해당 설정으로 API를 호출한다")
        void success_withCustomConfig() {
            // given
            String expectedText = "설정 포함 결과";
            given(geminiClient.generateContent(any(), any()))
                    .willReturn(createMockResponse(expectedText));

            // when
            String result = geminiService.generateTextWithConfig("테스트 프롬프트", 0.3, 512);

            // then
            assertThat(result).isEqualTo(expectedText);
        }

        @Test
        @DisplayName("temperature, maxTokens가 null이면 기본값(0.7, 2048)으로 동작한다")
        void success_withDefaultConfig() {
            // given - null 전달 시 기본값 사용
            given(geminiClient.generateContent(any(), any()))
                    .willReturn(createMockResponse("기본 설정 결과"));

            // when & then - 예외 없이 정상 동작해야 함
            assertThat(geminiService.generateTextWithConfig("프롬프트", null, null))
                    .isEqualTo("기본 설정 결과");
        }
    }

    // ===================================================================
    // generateWithProModel() / generateWithLiteModel() 테스트
    // ===================================================================
    @Nested
    @DisplayName("모델별 API 호출 테스트")
    class ModelVariants {

        @Test
        @DisplayName("Pro 모델 호출 시 geminiClient.generateContentPro()가 사용된다")
        void proModel_callsProEndpoint() {
            // given
            String expectedText = "Pro 모델 응답";
            given(geminiClient.generateContentPro(any(), any()))
                    .willReturn(createMockResponse(expectedText));

            // when
            String result = geminiService.generateWithProModel("복잡한 질문");

            // then
            assertThat(result).isEqualTo(expectedText);
            // Pro 엔드포인트가 1번 호출되었는지 검증
            then(geminiClient).should(times(1)).generateContentPro(any(), any());
            // Flash 엔드포인트는 호출되지 않아야 함
            then(geminiClient).should(times(0)).generateContent(any(), any());
        }

        @Test
        @DisplayName("Lite 모델 호출 시 geminiClient.generateContentLite()가 사용된다")
        void liteModel_callsLiteEndpoint() {
            // given
            given(geminiClient.generateContentLite(any(), any()))
                    .willReturn(createMockResponse("Lite 모델 응답"));

            // when
            String result = geminiService.generateWithLiteModel("빠른 질문");

            // then
            assertThat(result).isEqualTo("Lite 모델 응답");
            then(geminiClient).should(times(1)).generateContentLite(any(), any());
        }
    }

    // ===================================================================
    // chat() 테스트
    // ===================================================================
    @Nested
    @DisplayName("chat() - 대화형 채팅")
    class Chat {

        @Test
        @DisplayName("대화 히스토리를 포함해서 API를 호출하고 응답을 반환한다")
        void success_withConversationHistory() {
            // given - 2턴의 대화 히스토리
            List<GeminiRequest.Content> history = List.of(
                    GeminiRequest.Content.builder()
                            .parts(List.of(GeminiRequest.Part.builder().text("안녕").build()))
                            .role("user")
                            .build(),
                    GeminiRequest.Content.builder()
                            .parts(List.of(GeminiRequest.Part.builder().text("안녕하세요!").build()))
                            .role("model")
                            .build()
            );

            given(geminiClient.generateContent(any(), any()))
                    .willReturn(createMockResponse("다음 답변"));

            // when
            String result = geminiService.chat(history);

            // then
            assertThat(result).isEqualTo("다음 답변");
        }
    }

    // ===================================================================
    // 공통 Helper
    // ===================================================================

    /**
     * 테스트용 GeminiResponse 생성 헬퍼 메서드
     * 응답 텍스트를 받아서 완전한 GeminiResponse 객체를 생성한다
     */
    private GeminiResponse createMockResponse(String text) {
        GeminiResponse.Part part = new GeminiResponse.Part(text);
        GeminiResponse.Content content = new GeminiResponse.Content(List.of(part), "model");
        GeminiResponse.Candidate candidate = new GeminiResponse.Candidate(content, "STOP", 0, null);
        GeminiResponse.UsageMetadata usage = new GeminiResponse.UsageMetadata(10, 20, 30);
        return new GeminiResponse(List.of(candidate), null, usage);
    }
}
