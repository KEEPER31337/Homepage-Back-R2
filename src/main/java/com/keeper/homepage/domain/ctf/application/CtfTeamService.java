package com.keeper.homepage.domain.ctf.application;

import static com.keeper.homepage.global.error.ErrorCode.CTF_TEAM_ALREADY_JOIN;
import static com.keeper.homepage.global.error.ErrorCode.CTF_TEAM_INACCESSIBLE;

import com.keeper.homepage.domain.ctf.application.convenience.CtfContestFindService;
import com.keeper.homepage.domain.ctf.application.convenience.CtfTeamFindService;
import com.keeper.homepage.domain.ctf.dao.team.CtfTeamRepository;
import com.keeper.homepage.domain.ctf.dto.request.CreateTeamRequest;
import com.keeper.homepage.domain.ctf.dto.request.UpdateTeamRequest;
import com.keeper.homepage.domain.ctf.dto.response.CtfTeamDetailResponse;
import com.keeper.homepage.domain.ctf.entity.CtfContest;
import com.keeper.homepage.domain.ctf.entity.team.CtfTeam;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CtfTeamService {

  private final CtfTeamRepository ctfTeamRepository;
  private final CtfContestFindService ctfContestFindService;
  private final CtfTeamFindService ctfTeamFindService;

  @Transactional
  public void createTeam(Member member, CreateTeamRequest request) {
    CtfContest contest = ctfContestFindService.findJoinableById(request.getContestId());
    checkMemberHasTeam(contest, member);

    CtfTeam ctfTeam = CtfTeam.builder()
        .name(request.getName())
        .description(request.getDescription())
        .creator(member)
        .score(0)
        .ctfContest(contest)
        .build();

    ctfTeamRepository.save(ctfTeam);
    member.join(ctfTeam);
    // TODO: 팀이 생성될 때 마다 Dynamic score 변경해 줘야 함.
    // TODO: 팀이 생성될 때 마다 모든 문제에 해당하는 flag를 매핑해 줘야 함.
  }

  private void checkMemberHasTeam(CtfContest contest, Member member) {
    if (member.hasTeam(contest)) {
      throw new BusinessException(contest.getName(), "contest", CTF_TEAM_ALREADY_JOIN);
    }
  }

  @Transactional
  public void updateTeam(Member member, long teamId, UpdateTeamRequest request) {
    CtfTeam ctfTeam = ctfTeamFindService.findById(teamId);

    if (!member.isJoin(ctfTeam)) {
      throw new BusinessException(ctfTeam.getName(), "ctfTeam", CTF_TEAM_INACCESSIBLE);
    }
    ctfTeam.update(request.getName(), request.getDescription());
  }

  public CtfTeamDetailResponse getTeam(long teamId) {
    CtfTeam ctfTeam = ctfTeamFindService.findById(teamId);
    return CtfTeamDetailResponse.from(ctfTeam);
  }
}
