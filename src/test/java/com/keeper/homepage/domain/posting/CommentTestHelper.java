package com.keeper.homepage.domain.posting;

import com.keeper.homepage.domain.member.MemberTestHelper;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.posting.dao.comment.CommentRepository;
import com.keeper.homepage.domain.posting.entity.Posting;
import com.keeper.homepage.domain.posting.entity.comment.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommentTestHelper {

  @Autowired
  CommentRepository commentRepository;

  @Autowired
  MemberTestHelper memberTestHelper;

  @Autowired
  PostingTestHelper postingTestHelper;

  public Comment generate() {
    return this.builder().build();
  }

  public CommentBuilder builder() {
    return new CommentBuilder();
  }

  public final class CommentBuilder {

    private Member member;
    private Posting posting;
    private Long parentId;
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

    private CommentBuilder posting(Posting posting) {
      this.posting = posting;
      return this;
    }

    private CommentBuilder parentId(Long parentId) {
      this.parentId = parentId;
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
          .posting(posting != null ? posting : postingTestHelper.generate())
          .parentId(parentId != null ? parentId : 0L)
          .content(content != null ? content : "댓글내용")
          .likeCount(likeCount != null ? likeCount : 0)
          .dislikeCount(dislikeCount != null ? dislikeCount : 0)
          .ipAddress(ipAddress != null ? ipAddress : "0.0.0.0")
          .build());
    }
  }
}
