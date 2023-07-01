package com.keeper.homepage.domain.study.dto.response;

import static lombok.AccessLevel.PRIVATE;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.study.entity.Study;
import com.keeper.homepage.domain.study.entity.StudyHasMember;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = PRIVATE)
public class StudyDetailResponse {

  private String information;
  private List<String> members;
  private String gitLink;
  private String noteLink;
  private String etcLink;

  public static StudyDetailResponse from(Study study) {
    return StudyDetailResponse.builder()
        .information(study.getInformation())
        .members(study.getStudyMembers()
            .stream()
            .map(StudyHasMember::getMember)
            .map(Member::getRealName)
            .toList())
        .gitLink(study.getGitLink())
        .noteLink(study.getNoteLink())
        .etcLink(study.getEtcLink())
        .build();
  }
}
