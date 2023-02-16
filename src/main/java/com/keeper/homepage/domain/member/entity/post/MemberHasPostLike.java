package com.keeper.homepage.domain.member.entity.post;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.post.entity.Post;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@EqualsAndHashCode(of = {"member", "post"})
@NoArgsConstructor(access = PROTECTED)
@IdClass(MemberHasPostPK.class)
@Table(name = "member_has_posting_like")
public class MemberHasPostLike {

  @Id
  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "member_id", nullable = false, updatable = false)
  private Member member;

  @Id
  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "posting_id", nullable = false, updatable = false)
  private Post post;

  @Builder
  private MemberHasPostLike(Member member, Post post) {
    this.member = member;
    this.post = post;
  }
}
