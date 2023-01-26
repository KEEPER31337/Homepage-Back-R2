package com.keeper.homepage.domain.member.entity.rank;

import com.keeper.homepage.domain.thumbnail.entity.Thumbnail;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"type"})
@Table(name = "member_rank")
public class MemberRank {

  private static final int MAX_NAME_LENGTH = 45;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @Enumerated(value = EnumType.STRING)
  @Column(name = "name", nullable = false, length = MAX_NAME_LENGTH)
  private MemberRankType type;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "badge_thumbnail_id", nullable = false)
  private Thumbnail badge;

  public static MemberRank getMemberRankBy(MemberRankType type) {
    return MemberRank.builder()
        .id(type.id)
        .type(type)
        .build();
  }

  @Builder
  private MemberRank(Long id, MemberRankType type) {
    this.id = id;
    this.type = type;
  }

  public enum MemberRankType {
    일반회원(1),
    우수회원(2),
    ;

    @Getter
    private final long id;

    MemberRankType(long id) {
      this.id = id;
    }
  }
}
