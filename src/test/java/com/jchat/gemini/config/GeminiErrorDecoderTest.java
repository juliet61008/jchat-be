package com.jchat.gemini.config;

import com.jchat.gemini.exception.GeminiApiException;
import com.jchat.gemini.exception.GeminiRateLimitException;
import feign.Request;
import feign.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * GeminiErrorDecoder 단위 테스트
 *
 * 핵심 개념:
 * - Spring 컨텍스트 없이 순수 Java 객체만 테스트 (@ExtendWith 불필요)
 * - Feign의 Response는 빌더 패턴으로 직접 생성
 * - HTTP 상태코드별로 올바른 예외가 반환되는지 검증
 */
@DisplayName("GeminiErrorDecoder 단위 테스트")
class GeminiErrorDecoderTest {

    // 테스트 대상 (실제 객체 - Mock 아님)
    private GeminiErrorDecoder errorDecoder;

    @BeforeEach
    void setUp() {
        errorDecoder = new GeminiErrorDecoder();
    }

    @Test
    @DisplayName("400 응답 → GeminiApiException(statusCode=400) 반환")
    void decode_400_returnsBadRequestException() {
        // given
        Response response = createResponse(400, "잘못된 요청 형식입니다", Collections.emptyMap());

        // when
        Exception result = errorDecoder.decode("GeminiClient#generateContent", response);

        // then
        assertThat(result).isInstanceOf(GeminiApiException.class);
        GeminiApiException ex = (GeminiApiException) result;
        assertThat(ex.getStatusCode()).isEqualTo(400);
        assertThat(ex.getMessage()).contains("잘못된 요청입니다");
    }

    @Test
    @DisplayName("401 응답 → GeminiApiException(statusCode=401) 반환")
    void decode_401_returnsUnauthorizedException() {
        // given
        Response response = createResponse(401, "", Collections.emptyMap());

        // when
        Exception result = errorDecoder.decode("GeminiClient#generateContent", response);

        // then
        assertThat(result).isInstanceOf(GeminiApiException.class);
        GeminiApiException ex = (GeminiApiException) result;
        assertThat(ex.getStatusCode()).isEqualTo(401);
        assertThat(ex.getMessage()).contains("API 키가 유효하지 않습니다");
    }

    @Test
    @DisplayName("403 응답 → GeminiApiException(statusCode=403) 반환")
    void decode_403_returnsForbiddenException() {
        // given
        Response response = createResponse(403, "", Collections.emptyMap());

        // when
        Exception result = errorDecoder.decode("GeminiClient#generateContent", response);

        // then
        assertThat(result).isInstanceOf(GeminiApiException.class);
        GeminiApiException ex = (GeminiApiException) result;
        assertThat(ex.getStatusCode()).isEqualTo(403);
        assertThat(ex.getMessage()).contains("API 접근이 거부되었습니다");
    }

    @Test
    @DisplayName("429 응답 + Retry-After 헤더 → GeminiRateLimitException 반환, retryAfterSeconds 파싱됨")
    void decode_429_returnsRateLimitException() {
        // given - Retry-After: 30 헤더 포함
        Map<String, Collection<String>> headers = Map.of(
                "Retry-After", List.of("30")
        );
        Response response = createResponse(429, "Rate limit exceeded", headers);

        // when
        Exception result = errorDecoder.decode("GeminiClient#generateContent", response);

        // then
        assertThat(result).isInstanceOf(GeminiRateLimitException.class);
        GeminiRateLimitException ex = (GeminiRateLimitException) result;
        assertThat(ex.getStatusCode()).isEqualTo(429);
        assertThat(ex.getRetryAfterSeconds()).isEqualTo(30);
    }

    @Test
    @DisplayName("429 응답 + Retry-After 헤더 없음 → 기본값 60초로 GeminiRateLimitException 반환")
    void decode_429_defaultRetryAfter_whenHeaderMissing() {
        // given - Retry-After 헤더 없음
        Response response = createResponse(429, "Rate limit exceeded", Collections.emptyMap());

        // when
        Exception result = errorDecoder.decode("GeminiClient#generateContent", response);

        // then
        assertThat(result).isInstanceOf(GeminiRateLimitException.class);
        GeminiRateLimitException ex = (GeminiRateLimitException) result;
        assertThat(ex.getRetryAfterSeconds()).isEqualTo(60); // 기본값
    }

    @Test
    @DisplayName("500 응답 → GeminiApiException(statusCode=500) 반환")
    void decode_500_returnsServerErrorException() {
        // given
        Response response = createResponse(500, "Internal Server Error", Collections.emptyMap());

        // when
        Exception result = errorDecoder.decode("GeminiClient#generateContent", response);

        // then
        assertThat(result).isInstanceOf(GeminiApiException.class);
        GeminiApiException ex = (GeminiApiException) result;
        assertThat(ex.getStatusCode()).isEqualTo(500);
        assertThat(ex.getMessage()).contains("Gemini API 서버 오류");
    }

    @Test
    @DisplayName("503 응답 → GeminiApiException(statusCode=503) 반환")
    void decode_503_returnsServiceUnavailableException() {
        // given
        Response response = createResponse(503, "", Collections.emptyMap());

        // when
        Exception result = errorDecoder.decode("GeminiClient#generateContent", response);

        // then
        assertThat(result).isInstanceOf(GeminiApiException.class);
        GeminiApiException ex = (GeminiApiException) result;
        assertThat(ex.getStatusCode()).isEqualTo(503);
        assertThat(ex.getMessage()).contains("서비스를 일시적으로 사용할 수 없습니다");
    }

    @Test
    @DisplayName("그 외 상태코드(404 등) → 기본 ErrorDecoder가 처리한 예외 반환")
    void decode_unknownStatus_returnsDefaultException() {
        // given
        Response response = createResponse(404, "Not Found", Collections.emptyMap());

        // when
        Exception result = errorDecoder.decode("GeminiClient#generateContent", response);

        // then - GeminiApiException이 아닌 일반 예외 (Feign 기본 처리)
        assertThat(result).isNotInstanceOf(GeminiApiException.class);
    }

    // ===================================================================
    // 헬퍼 메서드
    // ===================================================================

    /**
     * 테스트용 Feign Response 생성
     *
     * @param status  HTTP 상태코드
     * @param body    응답 바디 문자열
     * @param headers 응답 헤더 (없으면 Collections.emptyMap())
     */
    private Response createResponse(int status, String body, Map<String, Collection<String>> headers) {
        // Feign Request는 Response 생성에 필수 - 더미 요청 객체 생성
        Request dummyRequest = Request.create(
                Request.HttpMethod.POST,
                "https://generativelanguage.googleapis.com/v1beta/test",
                Collections.emptyMap(),
                null,
                StandardCharsets.UTF_8,
                null
        );

        return Response.builder()
                .status(status)
                .reason("reason")
                .request(dummyRequest)
                .headers(headers)
                .body(body, StandardCharsets.UTF_8)
                .build();
    }
}
