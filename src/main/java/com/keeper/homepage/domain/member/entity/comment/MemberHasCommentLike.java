package com.keeper.homepage.domain.member.entity.comment;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.posting.entity.comment.Comment;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import java.io.Serializable;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@EqualsAndHashCode(of = {"member", "comment"})
@NoArgsConstructor(access = PROTECTED)
@IdClass(MemberHasCommentPK.class)
@Table(name = "member_has_comment_like")
public class MemberHasCommentLike {

  @Id
  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  @Id
  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "comment_id", nullable = false)
  private Comment comment;

  @Builder
  private MemberHasCommentLike(Member member, Comment comment) {
    this.member = member;
    this.comment = comment;
  }
}
