package com.keeper.homepage.domain.comment.dao;

import com.keeper.homepage.domain.member.entity.Member;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import static com.keeper.homepage.domain.comment.entity.QComment.*;

@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Modifying
    @Override
    public void updateVirtualMember(@Param("member") Member member, @Param("virtualMember") Member virtualMember) {
        jpaQueryFactory
                .update(comment)
                .set(comment.member, virtualMember)
                .where(comment.member.eq(member))
                .execute();
    }

}
