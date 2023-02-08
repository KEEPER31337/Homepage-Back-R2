package com.keeper.homepage.domain.member.dao.comment;

import com.keeper.homepage.domain.member.entity.comment.MemberHasCommentDislike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberHasCommentDislikeRepository extends JpaRepository<MemberHasCommentDislike, Long> {

}
