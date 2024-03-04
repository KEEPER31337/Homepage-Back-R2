package com.keeper.homepage.domain.post.dao;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.post.entity.Post;
import com.keeper.homepage.domain.post.entity.category.Category;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.keeper.homepage.domain.post.entity.QPost.*;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 카테고리 + 공지글 + 등록시간 최신순 정렬
     *
     * @param category 게시글 카테고리
     */
    @Override
    public List<Post> findAllNoticeByCategory(@Param("category") Category category) {
        return jpaQueryFactory
                .selectFrom(post)
                .where(
                        post.category.eq(category)
                        .and(post.isNotice.isTrue())
                )
                .orderBy(post.registerTime.desc())
                .fetch();
    }

    /**
     * 임시 저장글 제외 + 등록 시간 최신순 정렬
     */
    @Override
    public List<Post> findAllRecent(Pageable pageable) {
        return jpaQueryFactory
                .selectFrom(post)
                .where(
                        post.isTemp.isFalse()
                        .and(post.id.ne(1L))
                )
                .orderBy(post.registerTime.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    /**
     * 임시 저장글 제외 + 등록 시간 최신순 정렬 + 날짜 사이의 게시글
     *
     * @param startDate 가져올 시작 시간
     * @param endDate   가져올 끝 시간
     */
    @Override
    public List<Post> findAllTrend(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate) {
        return jpaQueryFactory
                .selectFrom(post)
                .where(
                        post.isTemp.isFalse()
                        .and(post.registerTime.between(startDate, endDate))
                )
                .fetch();
    }

    @Override
    public Optional<Post> findNextPost(@Param("postId") Long postId, @Param("category") Category category) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(post)
                        .where(
                                post.id.gt(postId)
                                .and(post.category.eq(category))
                                .and(post.isTemp.isFalse())
                        )
                        .orderBy(post.id.asc())
                        .limit(1)
                        .fetchFirst()
                );
    }

    @Override
    public Optional<Post> findPreviousPost(@Param("postId") Long postId, @Param("category") Category category) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(post)
                        .where(
                                post.id.lt(postId)
                                .and(post.category.eq(category))
                                .and(post.isTemp.isFalse())
                        )
                        .orderBy(post.id.desc())
                        .limit(1)
                        .fetchFirst()
                );
    }

    @Override
    public void updateVirtualMember(@Param("member") Member member, @Param("virtualMember") Member virtualMember) {
        jpaQueryFactory
                .update(post)
                .set(post.member, virtualMember)
                .where(
                        post.member.eq(member)
                        .and(post.isTemp.isFalse())
                )
                .execute();
    }

}
