package com.keeper.homepage.domain.member.entity.posting;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.posting.entity.Posting;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@EqualsAndHashCode(of = {"member", "posting"})
@NoArgsConstructor(access = PROTECTED)
@IdClass(MemberHasPostingPK.class)
@Getter
@Table(name = "member_has_posting_dislike")
public class MemberHasPostingDislike {

  @Id
  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "member_id")
  private Member member;

  @Id
  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "posting_id")
  private Posting posting;

  @Builder
  private MemberHasPostingDislike(Member member, Posting posting) {
    this.member = member;
    this.posting = posting;
  }
}


