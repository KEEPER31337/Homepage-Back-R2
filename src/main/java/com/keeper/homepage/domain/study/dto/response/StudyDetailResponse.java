package com.keeper.homepage.domain.study.dto.response;

import static lombok.AccessLevel.PRIVATE;

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
  private StudyMemberResponse headMember;
  private List<StudyMemberResponse> members;
  private List<LinkResponse> links;

  public static StudyDetailResponse from(Study study) {
    return StudyDetailResponse.builder()
        .information(study.getInformation())
        .headMember(StudyMemberResponse.from(study.getHeadMember()))
        .members(study.getStudyMembers()
            .stream()
            .map(StudyHasMember::getMember)
            .filter(member -> !member.isHeadMember(study))
            .map(StudyMemberResponse::from)
            .toList())
        .links(List.of(LinkResponse.of("Github", study.getGitLink()),
            LinkResponse.of("Notion", study.getNotionLink()),
            LinkResponse.of(study.getEtcTitle(), study.getEtcLink())))
        .build();
  }
}
