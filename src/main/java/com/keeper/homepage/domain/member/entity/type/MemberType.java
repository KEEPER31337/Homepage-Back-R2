package com.keeper.homepage.domain.member.entity.type;

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
@Table(name = "member_type")
public class MemberType {

  private static final int MAX_NAME_LENGTH = 45;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @Enumerated(value = EnumType.STRING)
  @Column(name = "name", nullable = false, length = MAX_NAME_LENGTH)
  private MemberTypeEnum type;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "badge_thumbnail_id", nullable = false)
  private Thumbnail badge;

  public static MemberType getMemberTypeBy(MemberTypeEnum type) {
    return MemberType.builder()
        .id(type.id)
        .type(type)
        .build();
  }

  @Builder
  private MemberType(Long id, MemberTypeEnum type) {
    this.id = id;
    this.type = type;
  }

  public enum MemberTypeEnum {
    비회원(1),
    정회원(2),
    휴면회원(3),
    졸업(4),
    ;

    @Getter
    private final long id;

    MemberTypeEnum(long id) {
      this.id = id;
    }
  }
}
