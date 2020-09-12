package com.studyolle.infra.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * <h1>비동기 처리 설정 클래스</h1>
 *
 * 비동기 Executor의 스레드 설정 클래스
 */
@Configuration
@EnableAsync
@Slf4j
public class AsyncConfig implements AsyncConfigurer
{
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 현재 시스템의 프로세서 Core 개수
        int processorCnt = Runtime.getRuntime().availableProcessors();
        log.info("creating pool with core : {}", processorCnt);
        /*
         * 현재 일하고 있는 Thread 개수(active thread)가 코어 개수(core pool size)보다 작으면 남아있는 Thread 를 사용한다.
         * 현재 일하고 있는 Thread 개수가 코어 개수만큼 차있으면 큐 용량(queue capacity)이 찰때까지 큐에 쌓아둔다.
         * 큐 용량이 다 차면, 코어 개수를 넘어서 맥스 개수(max pool size)에 다르기 전까지 새로운 Thread를 만들어 처리한다.
         * 맥스 개수를 넘기면 테스크를 처리하지 못한다.
         */
        executor.setCorePoolSize(processorCnt);
        executor.setMaxPoolSize(processorCnt * 2);
        executor.setQueueCapacity(50);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("AsyncExecutor-");
        executor.initialize();
        return executor;
    }
}
