package com.keeper.homepage.domain.member.dao.comment;

import com.keeper.homepage.domain.member.entity.comment.MemberHasCommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberHasCommentLikeRepository extends JpaRepository<MemberHasCommentLike, Long> {

}
