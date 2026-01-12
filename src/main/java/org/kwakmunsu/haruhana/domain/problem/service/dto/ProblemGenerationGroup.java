package org.kwakmunsu.haruhana.domain.problem.service.dto;

import java.util.List;
import lombok.Builder;
import org.kwakmunsu.haruhana.domain.category.entity.CategoryTopic;
import org.kwakmunsu.haruhana.domain.member.entity.Member;

/**
 * 같은 카테고리, 난이도를 설정한 회원들을 그룹화
 */
@Builder
public record ProblemGenerationGroup(
        ProblemGenerationKey key,
        CategoryTopic categoryTopic,
        List<Member> members
) {

    public int getMemberCount() {
        return members.size();
    }

}

