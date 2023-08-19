package com.keeper.homepage.domain.study.dto.response;

import static lombok.AccessLevel.PRIVATE;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.study.entity.Study;
import com.keeper.homepage.domain.study.entity.StudyHasMember;
import java.util.ArrayList;
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
  private List<LinkResponse> links;

  public static StudyDetailResponse from(Study study) {
    List<LinkResponse> links = new ArrayList<>();
    links.add(LinkResponse.of("Github", study.getGitLink()));
    links.add(LinkResponse.of("Notion", study.getNotionLink()));
    links.add(LinkResponse.of(study.getEtcTitle(), study.getEtcLink()));

    return StudyDetailResponse.builder()
        .information(study.getInformation())
        .members(study.getStudyMembers()
            .stream()
            .map(StudyHasMember::getMember)
            .map(Member::getRealName)
            .toList())
        .links(links)
        .build();
  }
}
