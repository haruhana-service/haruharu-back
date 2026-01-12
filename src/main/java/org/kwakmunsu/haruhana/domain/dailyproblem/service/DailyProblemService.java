package org.kwakmunsu.haruhana.domain.dailyproblem.service;

import lombok.RequiredArgsConstructor;
import org.kwakmunsu.haruhana.domain.dailyproblem.entity.DailyProblem;
import org.kwakmunsu.haruhana.domain.dailyproblem.service.dto.response.DailyProblemResponse;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class DailyProblemService {

    private final DailyProblemReader dailyProblemReader;

    public DailyProblemResponse getTodayProblem(Long memberId) {
        DailyProblem dailyProblem = dailyProblemReader.findDailyProblemByMember(memberId);

        return DailyProblemResponse.from(dailyProblem);
    }

}