package com.keeper.homepage.domain.member.entity.job;

import com.keeper.homepage.domain.thumbnail.entity.Thumbnail;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@ToString(of = {"name"})
@Table(name = "member_job")
public class MemberJob {

  private static final int MAX_NAME_LENGTH = 45;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @Column(name = "name", nullable = false, length = MAX_NAME_LENGTH)
  private MemberJobType type;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "badge_thumbnail_id", nullable = false)
  private Thumbnail badge;

  public static MemberJob getMemberJobBy(MemberJobType type) {
    return MemberJob.builder()
        .id(type.id)
        .type(type)
        .build();
  }

  @Builder
  private MemberJob(Long id, MemberJobType type) {
    this.id = id;
    this.type = type;
  }

  public enum MemberJobType {
    ROLE_회원(1),
    ROLE_관리자(2);

    private final long id;

    MemberJobType(long id) {
      this.id = id;
    }
  }
}
