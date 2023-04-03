package com.keeper.homepage.domain.comment;

import com.keeper.homepage.domain.comment.dao.CommentRepository;
import com.keeper.homepage.domain.comment.entity.Comment;
import com.keeper.homepage.domain.member.MemberTestHelper;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.post.PostTestHelper;
import com.keeper.homepage.domain.post.entity.Post;
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
    private Comment parent;
    private String content;
    private String ipAddress;

    private CommentBuilder() {

    }

    public CommentBuilder member(Member member) {
      this.member = member;
      return this;
    }

    public CommentBuilder post(Post post) {
      this.post = post;
      return this;
    }

    public CommentBuilder parent(Comment parent) {
      this.parent = parent;
      return this;
    }

    public CommentBuilder content(String content) {
      this.content = content;
      return this;
    }

    public CommentBuilder ipAddress(String ipAddress) {
      this.ipAddress = ipAddress;
      return this;
    }

    public Comment build() {
      return commentRepository.save(Comment.builder()
          .member(member != null ? member : memberTestHelper.generate())
          .post(post != null ? post : postTestHelper.generate())
          .parent(parent)
          .content(content != null ? content : "댓글내용")
          .ipAddress(ipAddress != null ? ipAddress : "0.0.0.0")
          .build());
    }
  }
}
