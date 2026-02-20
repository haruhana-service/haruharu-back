package org.kwakmunsu.haruhana.domain.streak.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.kwakmunsu.haruhana.IntegrationTestSupport;
import org.kwakmunsu.haruhana.domain.member.MemberFixture;
import org.kwakmunsu.haruhana.domain.member.enums.Role;
import org.kwakmunsu.haruhana.domain.streak.entity.Streak;
import org.kwakmunsu.haruhana.domain.streak.service.dto.response.WeeklySolvedStatusResponse;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * Streak 캐시 통합 테스트
 * <p>
 * Spring AOP 프록시를 통해 @Cacheable / @CacheEvict 가 실제로 동작하는지 검증.
 * 단위 테스트(@InjectMocks)는 프록시를 거치지 않으므로 반드시 통합 테스트로 검증한다.
 */
@RequiredArgsConstructor
class StreakCacheTest extends IntegrationTestSupport {

    final StreakService streakService;
    final CacheManager cacheManager;

    @MockitoBean
    StreakReader streakReader;

    @MockitoBean
    StreakManager streakManager;

    @AfterEach
    void clearCache() {
        Objects.requireNonNull(cacheManager.getCache("streak")).clear();
    }

    @Test
    void 스트릭_두_번_조회_시_DB는_한_번만_호출된다() {
        // given
        var memberId = 1L;
        var member = MemberFixture.createMember(Role.ROLE_MEMBER);
        var streak = Streak.create(member);
        var weeklySolvedStatus = List.of(new WeeklySolvedStatusResponse(LocalDate.now(), true));

        given(streakReader.getByMemberId(memberId)).willReturn(streak);
        given(streakReader.getWeeklySolvedStatus(memberId)).willReturn(weeklySolvedStatus);

        // when: 동일 회원으로 2번 조회
        streakService.getStreak(memberId);
        streakService.getStreak(memberId);

        // then: DB는 1번만 호출
        verify(streakReader, times(1)).getByMemberId(memberId);
        verify(streakReader, times(1)).getWeeklySolvedStatus(memberId);
    }

    @Test
    void 다른_회원의_스트릭_캐시는_독립적으로_관리된다() {
        // given
        var memberIdA = 1L;
        var memberIdB = 2L;
        var member = MemberFixture.createMember(Role.ROLE_MEMBER);
        var streak = Streak.create(member);

        given(streakReader.getByMemberId(any())).willReturn(streak);
        given(streakReader.getWeeklySolvedStatus(any())).willReturn(List.of());

        // when: 각각 2번씩 조회
        streakService.getStreak(memberIdA);
        streakService.getStreak(memberIdA);
        streakService.getStreak(memberIdB);
        streakService.getStreak(memberIdB);

        // then: 각 회원마다 DB는 1번씩만 호출
        verify(streakReader, times(1)).getByMemberId(memberIdA);
        verify(streakReader, times(1)).getByMemberId(memberIdB);
    }

    @Test
    void 스트릭_증가_후_캐시가_무효화된다() {
        // given: 캐시 채우기
        var memberId = 1L;
        var member = MemberFixture.createMember(Role.ROLE_MEMBER);
        var streak = Streak.create(member);

        given(streakReader.getByMemberId(memberId)).willReturn(streak);
        given(streakReader.getWeeklySolvedStatus(memberId)).willReturn(List.of());
        streakService.getStreak(memberId);

        var cache = cacheManager.getCache("streak");
        assertThat(Objects.requireNonNull(cache).get(memberId)).isNotNull();

        // when: 스트릭 증가 (캐시 무효화 트리거)
        streakService.increaseWithRetry(memberId);

        // then: 캐시 무효화
        assertThat(cache.get(memberId)).isNull();

        // then: 재조회 시 DB 재호출
        streakService.getStreak(memberId);
        verify(streakReader, times(2)).getByMemberId(memberId);
    }

}
