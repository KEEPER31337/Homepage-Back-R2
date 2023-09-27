package com.keeper.homepage.domain.member.dao.comment;

import com.keeper.homepage.domain.comment.entity.Comment;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.member.entity.comment.MemberHasCommentLike;
import com.keeper.homepage.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberHasCommentLikeRepository extends JpaRepository<MemberHasCommentLike, Long> {

  void deleteAllByComment(Comment comment);

  void deleteAllByComment_Post(Post post);

  void deleteAllByMember(Member member);
}
