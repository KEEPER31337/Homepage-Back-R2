package com.keeper.homepage.domain.survey.entity;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.keeper.homepage.domain.survey.converter.SurveyReplyTypeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Arrays;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
@Table(name = "survey_reply")
public class SurveyReply {

  private static final int MAX_TYPE_LENGTH = 10;

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @Convert(converter = SurveyReplyTypeConverter.class)
  @Column(name = "type", nullable = false, length = MAX_TYPE_LENGTH)
  private SurveyReplyType type;

  public static SurveyReply getSurveyReply(SurveyReplyType type) {
    return SurveyReply.builder()
        .id(type.id)
        .type(type)
        .build();
  }

  @Builder
  private SurveyReply(Long id, SurveyReplyType type) {
    this.id = id;
    this.type = type;
  }

  @Getter
  @RequiredArgsConstructor
  public enum SurveyReplyType {
    ACTIVITY(1, "활동"),
    MILITARY_DORMANT(2, "휴면(군휴학)"),
    OTHER_DORMANT(3, "휴면(기타)"),
    GRADUATE(4, "졸업"),
    LEAVE(5, "탈퇴"),
    ;

    private final long id;
    private final String type;

    public static SurveyReplyType fromCode(String type) {
      return Arrays.stream(SurveyReplyType.values())
          .filter(EnumType -> EnumType.getType().equals(type))
          .findAny()
          .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 응답 종류입니다."));
    }
  }
}
