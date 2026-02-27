package org.kwakmunsu.haruhana.domain.dailyproblem.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.kwakmunsu.haruhana.domain.member.entity.Member;
import org.kwakmunsu.haruhana.domain.problem.entity.Problem;
import org.kwakmunsu.haruhana.global.entity.BaseEntity;

@Table(
        indexes = {
                @Index(name = "idx_member_date", columnList = "member_id, assigned_at")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class DailyProblem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    Problem problem;

    @Column(nullable = false)
    LocalDate assignedAt;

    @Column(nullable = false)
    boolean isSolved;

    public static DailyProblem create(
            Member member,
            Problem problem,
            LocalDate assignedAt
    ) {
        DailyProblem dailyProblem = new DailyProblem();

        dailyProblem.member = member;
        dailyProblem.problem = problem;
        dailyProblem.assignedAt = assignedAt;
        dailyProblem.isSolved = false;

        return dailyProblem;
    }

    public void markAsSolved() {
        this.isSolved = true;
    }

}