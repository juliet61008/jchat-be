package com.jchat.gemini.service;

import com.jchat.gemini.client.GeminiClient;
import com.jchat.gemini.dto.GeminiRequest;
import com.jchat.gemini.dto.GeminiResponse;
import com.jchat.gemini.exception.GeminiApiException;
import com.jchat.gemini.exception.GeminiRateLimitException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiService {

    private final GeminiClient geminiClient;

    @Value("${gemini.api-key}")
    private String apiKey;

    /**
     * 기본 텍스트 생성 (Flash 모델 사용)
     */
    public String generateText(String prompt) {
        log.info("Gemini API 호출 - prompt: {}", prompt);

        try {
            GeminiRequest request = createBasicRequest(prompt);
            GeminiResponse response = geminiClient.generateContent(apiKey, request);

            String result = extractTextFromResponse(response);
            logTokenUsage(response);

            return result;

        } catch (GeminiRateLimitException e) {
            log.error("Rate Limit 초과: {}초 후 재시도", e.getRetryAfterSeconds());
            throw e;
        } catch (GeminiApiException e) {
            log.error("Gemini API 오류: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 설정 옵션과 함께 텍스트 생성
     */
    public String generateTextWithConfig(
            String prompt,
            Double temperature,
            Integer maxTokens
    ) {
        log.info("Gemini API 호출 (설정 포함) - temp: {}, maxTokens: {}",
                temperature, maxTokens);

        GeminiRequest.GenerationConfig config = GeminiRequest.GenerationConfig.builder()
                .temperature(temperature != null ? temperature : 0.7)
                .maxOutputTokens(maxTokens != null ? maxTokens : 2048)
                .topP(0.95)
                .topK(40)
                .build();

        GeminiRequest request = createRequestWithConfig(prompt, config);
        GeminiResponse response = geminiClient.generateContent(apiKey, request);

        return extractTextFromResponse(response);
    }

    /**
     * Pro 모델 사용 (복잡한 추론용)
     */
    public String generateWithProModel(String prompt) {
        log.info("Gemini Pro API 호출 - prompt: {}", prompt);

        GeminiRequest request = createBasicRequest(prompt);
        GeminiResponse response = geminiClient.generateContentPro(apiKey, request);

        return extractTextFromResponse(response);
    }

    /**
     * Lite 모델 사용 (빠른 처리용)
     */
    public String generateWithLiteModel(String prompt) {
        log.info("Gemini Lite API 호출 - prompt: {}", prompt);

        GeminiRequest request = createBasicRequest(prompt);
        GeminiResponse response = geminiClient.generateContentLite(apiKey, request);

        return extractTextFromResponse(response);
    }

    /**
     * 대화형 채팅 (히스토리 포함)
     */
    public String chat(List<GeminiRequest.Content> conversationHistory) {
        log.info("Gemini Chat API 호출 - 메시지 수: {}", conversationHistory.size());

        GeminiRequest request = GeminiRequest.builder()
                .contents(conversationHistory)
                .build();

        GeminiResponse response = geminiClient.generateContent(apiKey, request);

        return extractTextFromResponse(response);
    }

    // ============ Private Helper Methods ============

    private GeminiRequest createBasicRequest(String prompt) {
        GeminiRequest.Part part = GeminiRequest.Part.builder()
                .text(prompt)
                .build();

        GeminiRequest.Content content = GeminiRequest.Content.builder()
                .parts(List.of(part))
                .role("user")
                .build();

        return GeminiRequest.builder()
                .contents(List.of(content))
                .build();
    }

    private GeminiRequest createRequestWithConfig(
            String prompt,
            GeminiRequest.GenerationConfig config
    ) {
        GeminiRequest.Part part = GeminiRequest.Part.builder()
                .text(prompt)
                .build();

        GeminiRequest.Content content = GeminiRequest.Content.builder()
                .parts(List.of(part))
                .role("user")
                .build();

        return GeminiRequest.builder()
                .contents(List.of(content))
                .generationConfig(config)
                .build();
    }

    private String extractTextFromResponse(GeminiResponse response) {
        if (response == null ||
                response.getCandidates() == null ||
                response.getCandidates().isEmpty()) {
            throw new GeminiApiException("응답이 비어있습니다", 500);
        }

        GeminiResponse.Candidate candidate = response.getCandidates().get(0);

        if (candidate.getContent() == null ||
                candidate.getContent().getParts() == null ||
                candidate.getContent().getParts().isEmpty()) {
            throw new GeminiApiException("응답 내용을 파싱할 수 없습니다", 500);
        }

        return candidate.getContent().getParts().get(0).getText();
    }

    private void logTokenUsage(GeminiResponse response) {
        if (response.getUsageMetadata() != null) {
            GeminiResponse.UsageMetadata usage = response.getUsageMetadata();
            log.info("토큰 사용량 - Input: {}, Output: {}, Total: {}",
                    usage.getPromptTokenCount(),
                    usage.getCandidatesTokenCount(),
                    usage.getTotalTokenCount()
            );
        }
    }
}