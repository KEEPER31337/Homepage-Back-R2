package com.keeper.homepage.domain.member.entity.job;

import static com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_출제자;
import static com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_회원;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"type"})
@EqualsAndHashCode(of = {"id"})
@Table(name = "member_job")
public class MemberJob {

  private static final int MAX_NAME_LENGTH = 45;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @Enumerated(value = EnumType.STRING)
  @Column(name = "name", nullable = false, length = MAX_NAME_LENGTH)
  private MemberJobType type;

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

  @Getter
  public enum MemberJobType {
    ROLE_회장(1),
    ROLE_부회장(2),
    ROLE_대외부장(3),
    ROLE_학술부장(4),
    ROLE_FRONT_전산관리자(5),
    ROLE_서기(6),
    ROLE_총무(7),
    ROLE_사서(8),
    ROLE_회원(9),
    ROLE_출제자(10),
    ROLE_BACK_전산관리자(11),
    ROLE_INFRA_전산관리자(12),
    ;

    private final long id;

    MemberJobType(long id) {
      this.id = id;
    }
  }

  public boolean isExecutive() {
    return this.type != ROLE_회원 && this.type != ROLE_출제자;
  }
}
