package com.keeper.homepage.domain.study.dto.response;

import static lombok.AccessLevel.PRIVATE;

import com.keeper.homepage.domain.study.entity.Study;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = PRIVATE)
public class StudyResponse {

  private Long studyId;
  private String title;
  private String headName;
  private Integer memberCount;
  private String thumbnailPath;

  public static StudyResponse from(Study study) {
    return StudyResponse.builder()
        .studyId(study.getId())
        .title(study.getTitle())
        .headName(study.getHeadMember().getRealName())
        .memberCount(study.getStudyMembers().size())
        .thumbnailPath(study.getThumbnailPath())
        .build();
  }
}
