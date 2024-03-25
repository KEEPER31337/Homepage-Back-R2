package com.keeper.homepage.domain.post.dao;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.post.entity.Post;
import com.keeper.homepage.domain.post.entity.category.Category;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PostRepositoryCustom {

    List<Post> findAllNoticeByCategory(@Param("category") Category category);

    List<Post> findAllRecent(Pageable pageable);

    List<Post> findAllTrend(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    Optional<Post> findNextPost(@Param("postId") Long postId, @Param("category") Category category);

    Optional<Post> findPreviousPost(@Param("postId") Long postId, @Param("category") Category category);

    void updateVirtualMember(@Param("member") Member member, @Param("virtualMember") Member virtualMember);

}
