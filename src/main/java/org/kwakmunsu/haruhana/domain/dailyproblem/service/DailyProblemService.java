package org.kwakmunsu.haruhana.domain.dailyproblem.service;

import lombok.RequiredArgsConstructor;
import org.kwakmunsu.haruhana.domain.dailyproblem.entity.DailyProblem;
import org.kwakmunsu.haruhana.domain.dailyproblem.service.dto.response.TodayProblemResponse;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class DailyProblemService {

    private final DailyProblemReader dailyProblemReader;

    /**
     * 오늘의 문제 조회
     * @param memberId 회원 ID
     */
    public TodayProblemResponse getTodayProblem(Long memberId) {
        DailyProblem dailyProblem = dailyProblemReader.findDailyProblemByMember(memberId);

        return TodayProblemResponse.from(dailyProblem);
    }

    /**
     * 문제 상세 조회
     * @param dailyProblemId 오늘의 문제 ID
     * @param memberId 회원 ID
     */
    public void findDailyProblem(Long dailyProblemId, Long memberId) {
        // 본인의 문제인가를 확인하고 맞으면 상세 조회 시켜줌
        DailyProblem dailyProblem = dailyProblemReader.find(dailyProblemId, memberId);

    }
}