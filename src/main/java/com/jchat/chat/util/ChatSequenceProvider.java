package com.jchat.chat.util;

import com.jchat.chat.mapper.ChatMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 멀티스레드 동시성 보장하는 postgresql 시퀀스 캐싱 Provider
 * 시퀀스저장용량: 100
 * 시퀀스리필기준: 20
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatSequenceProvider {
    private final ChatMapper chatMapper;

    // 시퀀스저장용량 상수
    private static final Integer CACHE_POOL = 30;
    // 시퀀스리필기준 상수
    private static final Integer CACHE_REFILL = 20;

    // 시퀀스 담을 동시성 보장하는 큐
    ConcurrentLinkedQueue<Long> sequences = new ConcurrentLinkedQueue<>();

    // 리필중 여부 체크 아토믹불린 true: 리필중 false: 리필가능
    AtomicBoolean isRefilling = new AtomicBoolean();


    /**
     * 다음 시퀀스
     * @return
     */
    public Long getNext() {
        log.info("메세지 시퀀스 getNext 호출 사이즈 : {}", sequences.size());
        // pop과 동일 시퀀스 하나 꺼냄
        Long sequence = sequences.poll();

        // 시퀀스 큐 비어있는 경우
        if (sequence == null) {
            // 동기 시퀀스 리필 호출
            refillSequences();
            // 시퀀스 하나 꺼냄
            sequence = sequences.poll();
        }

        // 시퀀스 큐 20개 미만인 경우
        if (sequences.size() < CACHE_REFILL) {
            // 비동기 시퀀스 리필 호출
            refillSequencesAsync();
        }

        return sequence;
    }

    /**
     * 동기 시퀀스 리필
     */
    private void refillSequences() {
        log.info("메세지 시퀀스 refillSequences 호출");
        // 스레드풀 리필 100회
        for (int i = 0; i < CACHE_POOL; i++) {
            // 시퀀스 DB 조회 후 저장
            sequences.offer(chatMapper.selectChatRoomMsgSeq());
        }
        log.info("메세지 시퀀스 사이즈: {}", sequences.size());
    }

    /**
     * 비동기 시퀀스 리필
     */
    private void refillSequencesAsync() {
        log.info("메세지 시퀀스 refillSequencesAsync 호출");
        // 리필중 체크
        if (!isRefilling.compareAndSet(false, true)) {
            return;
        }

        // 새 스레드 생성하여 비동기 동작하도록함
        new Thread(() -> {
            try {
                // 시퀀스 리필 동기 함수 호출
                refillSequences();
            } catch (Exception e) {
                log.error("시스템 비동기 호출 실패 메세지: {}", e.getMessage());
            } finally {
                // 리필종료 표시
                isRefilling.set(false);
            }
        }, "msg-sequences-refilling-thread").start();

    }

    /**
     * 어플리케이션 실행시 초기화
     */
    @PostConstruct
    private void init() {
        log.info("메세지 시퀀스 init 호출");
        refillSequences();
    }
}
