package org.kwakmunsu.haruhana.domain.submission.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.kwakmunsu.haruhana.domain.dailyproblem.entity.DailyProblem;
import org.kwakmunsu.haruhana.domain.member.entity.Member;
import org.kwakmunsu.haruhana.global.entity.BaseEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Submission extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    private DailyProblem dailyProblem;

    @Column(nullable = false, length = 5000)
    private String answer;

    @Column(nullable = false)
    private LocalDateTime submittedAt;

    private boolean isOnTime; // 스트릭 계산을 위한 제시간 제출 여부

    public static Submission create(
            Member member,
            DailyProblem dailyProblem,
            String answer
    ) {
        LocalDateTime now = LocalDateTime.now();

        Submission submission = new Submission();

        submission.member = member;
        submission.dailyProblem = dailyProblem;
        submission.answer = answer;
        submission.submittedAt = now;
        // 제출 시간이 할당 날짜 이후 면 false, 아니면 true
        submission.isOnTime = !now.toLocalDate().isAfter(dailyProblem.getAssignedAt());

        return submission;
    }

    public void updateAnswer(String answer) {
        this.answer = answer;
    }

}