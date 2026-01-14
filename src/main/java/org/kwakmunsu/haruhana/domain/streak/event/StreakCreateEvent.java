package org.kwakmunsu.haruhana.domain.streak.event;

import org.kwakmunsu.haruhana.domain.member.entity.Member;

public record StreakCreateEvent(Member member) {

}