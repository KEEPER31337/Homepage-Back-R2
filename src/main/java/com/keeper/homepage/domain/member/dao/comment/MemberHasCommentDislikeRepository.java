package com.keeper.homepage.domain.member.dao.comment;

import com.keeper.homepage.domain.comment.entity.Comment;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.member.entity.comment.MemberHasCommentDislike;
import com.keeper.homepage.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberHasCommentDislikeRepository extends JpaRepository<MemberHasCommentDislike, Long> {

  void deleteAllByComment(Comment comment);

  void deleteAllByComment_Post(Post post);

  void deleteAllByMember(Member member);
}
