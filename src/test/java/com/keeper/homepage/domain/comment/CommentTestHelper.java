package com.keeper.homepage.domain.comment;

import com.keeper.homepage.domain.member.MemberTestHelper;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.comment.dao.CommentRepository;
import com.keeper.homepage.domain.post.PostTestHelper;
import com.keeper.homepage.domain.post.entity.Post;
import com.keeper.homepage.domain.comment.entity.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommentTestHelper {

  @Autowired
  CommentRepository commentRepository;

  @Autowired
  MemberTestHelper memberTestHelper;

  @Autowired
  PostTestHelper postTestHelper;

  public Comment generate() {
    return this.builder().build();
  }

  public CommentBuilder builder() {
    return new CommentBuilder();
  }

  public final class CommentBuilder {

    private Member member;
    private Post post;
    private Long parentCommentId;
    private String content;
    private Integer likeCount;
    private Integer dislikeCount;
    private String ipAddress;

    private CommentBuilder() {

    }

    private CommentBuilder member(Member member) {
      this.member = member;
      return this;
    }

    private CommentBuilder post(Post post) {
      this.post = post;
      return this;
    }

    private CommentBuilder parentCommentId(Long parentCommentId) {
      this.parentCommentId = parentCommentId;
      return this;
    }

    private CommentBuilder content(String content) {
      this.content = content;
      return this;
    }

    private CommentBuilder likeCount(Integer likeCount) {
      this.likeCount = likeCount;
      return this;
    }

    private CommentBuilder dislikeCount(Integer dislikeCount) {
      this.dislikeCount = dislikeCount;
      return this;
    }

    private CommentBuilder ipAddress(String ipAddress) {
      this.ipAddress = ipAddress;
      return this;
    }

    public Comment build() {
      return commentRepository.save(Comment.builder()
          .member(member != null ? member : memberTestHelper.generate())
          .post(post != null ? post : postTestHelper.generate())
          .parentCommentId(parentCommentId != null ? parentCommentId : 0L)
          .content(content != null ? content : "댓글내용")
          .likeCount(likeCount)
          .dislikeCount(dislikeCount)
          .ipAddress(ipAddress != null ? ipAddress : "0.0.0.0")
          .build());
    }
  }
}
