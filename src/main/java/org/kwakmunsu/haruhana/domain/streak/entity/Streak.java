package org.kwakmunsu.haruhana.domain.streak.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Version;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.kwakmunsu.haruhana.domain.member.entity.Member;
import org.kwakmunsu.haruhana.global.entity.BaseEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Streak extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Member member;

    @Column(nullable = false)
    private Long currentStreak;

    @Column(nullable = false)
    private Long maxStreak;

    private LocalDate lastSolvedAt;

    @Version
    private Long version;

    public static Streak create(Member member) {
        Streak streak = new Streak();

        streak.member = member;
        streak.currentStreak = 0L;
        streak.maxStreak = 0L;
        streak.lastSolvedAt = null;

        return streak;
    }


    public void increase() {
        this.currentStreak += 1;
        if (currentStreak > maxStreak) {
            this.maxStreak = currentStreak;
        }
        this.lastSolvedAt = LocalDate.now();
    }

    public void reset() {
        this.currentStreak = 0L;
    }

}