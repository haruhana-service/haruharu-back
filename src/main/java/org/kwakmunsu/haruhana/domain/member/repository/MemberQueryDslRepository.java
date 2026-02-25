package org.kwakmunsu.haruhana.domain.member.repository;

import static org.kwakmunsu.haruhana.domain.member.entity.QMember.member;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.kwakmunsu.haruhana.admin.member.enums.SortBy;
import org.kwakmunsu.haruhana.domain.member.entity.Member;
import org.kwakmunsu.haruhana.global.support.OffsetLimit;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
@Repository
public class MemberQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public List<Member> findMembers(String search, SortBy sortBy, OffsetLimit offsetLimit) {
        return queryFactory.selectFrom(member)
                .where(searchCondition(search))
                .orderBy(sortOrderSpecifier(sortBy))
                .offset(offsetLimit.offset())
                .limit(offsetLimit.limit()  + 1)
                .fetch();
    }

    private BooleanExpression searchCondition(String search) {
        if (!StringUtils.hasText(search)) {
            return null;
        }
        String trimmedQuery = search.trim();

        return member.nickname.containsIgnoreCase(trimmedQuery)
                .or(member.loginId.containsIgnoreCase(trimmedQuery));
    }

    private OrderSpecifier<?> sortOrderSpecifier(SortBy sortBy) {
        if (sortBy == null) {
            return member.id.desc(); // 기본값
        }

        return switch (sortBy) {
            case JOIN_ASC -> member.id.asc();
            case JOIN_DESC -> member.id.desc();
        };
    }

}