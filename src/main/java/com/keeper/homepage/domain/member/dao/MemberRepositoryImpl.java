package com.keeper.homepage.domain.member.dao;

import com.keeper.homepage.domain.member.entity.Member;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

import static com.keeper.homepage.domain.member.entity.QMember.*;
import static com.keeper.homepage.domain.member.entity.job.QMemberHasMemberJob.*;
import static com.keeper.homepage.domain.member.entity.job.QMemberJob.*;
import static com.keeper.homepage.domain.member.entity.type.QMemberType.*;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Member> findMemberProfileWithFetchJoin(@Param("id") long id) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(member)
                        .join(member.memberType, memberType).fetchJoin()
                        .join(member.memberJob, memberHasMemberJob).fetchJoin()
                        .join(memberHasMemberJob.memberJob, memberJob).fetchJoin()
                        .where(member.id.eq(id))
                        .fetchOne()
        );
    }

}
