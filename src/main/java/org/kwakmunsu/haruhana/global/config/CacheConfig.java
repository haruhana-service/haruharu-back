package org.kwakmunsu.haruhana.global.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.cache.CaffeineCacheMetrics;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@EnableCaching
@RequiredArgsConstructor
@Configuration
public class CacheConfig {

    private static final int TODAY_PROBLEM_MAX_SIZE = 5_000;
    private static final int STREAK_MAX_SIZE = 5_000;
    private static final int DAILY_PROBLEM_DETAIL_MAX_SIZE = 15_000;

    private static final long STREAK_TTL_MINUTES = 30;
    private static final long DAILY_PROBLEM_DETAIL_TTL_MINUTES = 5;

    private final MeterRegistry meterRegistry;

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();

        Cache<Object, Object> todayProblemCache = buildTodayProblemCache();
        Cache<Object, Object> streakCache = buildCache(Duration.ofMinutes(STREAK_TTL_MINUTES), STREAK_MAX_SIZE);
        Cache<Object, Object> dailyProblemDetailCache = buildCache(Duration.ofMinutes(DAILY_PROBLEM_DETAIL_TTL_MINUTES), DAILY_PROBLEM_DETAIL_MAX_SIZE);

        cacheManager.registerCustomCache("todayProblem", todayProblemCache);
        cacheManager.registerCustomCache("streak", streakCache);
        cacheManager.registerCustomCache("dailyProblemDetail", dailyProblemDetailCache);

        CaffeineCacheMetrics.monitor(meterRegistry, todayProblemCache, "todayProblem");
        CaffeineCacheMetrics.monitor(meterRegistry, streakCache, "streak");
        CaffeineCacheMetrics.monitor(meterRegistry, dailyProblemDetailCache, "dailyProblemDetail");

        log.info("[CacheConfig] Caffeine 캐시 설정 완료 - todayProblem: 자정 만료, streak: {}min, dailyProblemDetail: {}min",
                STREAK_TTL_MINUTES, DAILY_PROBLEM_DETAIL_TTL_MINUTES);

        return cacheManager;
    }

    /**
     * todayProblem 캐시: 자정에 일괄 만료
     * - 날짜가 바뀌면 키도 바뀌므로(memberId:date) 신규 요청은 항상 새 날짜 키로 캐싱됨
     * - 전날 항목을 자정에 제거하여 메모리 이중 점유(최대 2배) 방지
     */
    private Cache<Object, Object> buildTodayProblemCache() {
        return Caffeine.newBuilder()
                .maximumSize(TODAY_PROBLEM_MAX_SIZE)
                .expireAfter(new Expiry<>() {
                    @Override
                    public long expireAfterCreate(Object key, Object value, long currentTime) {
                        return nanosUntilMidnight();
                    }

                    @Override
                    public long expireAfterUpdate(Object key, Object value, long currentTime, long currentDuration) {
                        return currentDuration;
                    }

                    @Override
                    public long expireAfterRead(Object key, Object value, long currentTime, long currentDuration) {
                        return currentDuration;
                    }
                })
                .recordStats()
                .build();
    }

    private long nanosUntilMidnight() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime midnight = now.toLocalDate().plusDays(1).atStartOfDay();
        long seconds = Duration.between(now, midnight).getSeconds();
        return TimeUnit.SECONDS.toNanos(Math.max(1, seconds));
    }

    private Cache<Object, Object> buildCache(Duration ttl, int maxSize) {
        return Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(ttl)
                .recordStats()
                .build();
    }

}