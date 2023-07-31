package com.keeper.homepage.domain.ctf.dto.response;

import static lombok.AccessLevel.PRIVATE;

import com.keeper.homepage.domain.ctf.entity.team.CtfTeam;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = PRIVATE)
public class CtfTeamDetailResponse {

  private Long id;
  private String name;
  private String description;
  private int rank;
  private int score;
  private List<String> memberNames;
  private List<CtfChallengeResponse> solves;

  public static CtfTeamDetailResponse from(CtfTeam ctfTeam) {
    return CtfTeamDetailResponse.builder()
        .id(ctfTeam.getId())
        .name(ctfTeam.getName())
        .description(ctfTeam.getDescription())
        .rank(0) // TODO: 현재 순위 반환
        .score(ctfTeam.getScore())
        .memberNames(ctfTeam.getCtfTeamHasMembers()
            .stream()
            .map(ctfTeamHasMember -> ctfTeamHasMember.getMember().getRealName())
            .toList())
        .solves(List.of()) // TODO: 푼 문제 응답 반환
        .build();
  }
}
