package com.keeper.homepage.domain.ctf.application;

import static com.keeper.homepage.global.error.ErrorCode.CTF_TEAM_ALREADY_JOIN;
import static com.keeper.homepage.global.error.ErrorCode.CTF_TEAM_INACCESSIBLE;

import com.keeper.homepage.domain.ctf.application.convenience.CtfContestFindService;
import com.keeper.homepage.domain.ctf.application.convenience.CtfTeamFindService;
import com.keeper.homepage.domain.ctf.dao.team.CtfTeamRepository;
import com.keeper.homepage.domain.ctf.dto.response.CtfTeamDetailResponse;
import com.keeper.homepage.domain.ctf.dto.response.CtfTeamResponse;
import com.keeper.homepage.domain.ctf.entity.CtfContest;
import com.keeper.homepage.domain.ctf.entity.team.CtfTeam;
import com.keeper.homepage.domain.member.application.convenience.MemberFindService;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CtfTeamService {

  private final CtfTeamRepository ctfTeamRepository;
  private final CtfContestFindService ctfContestFindService;
  private final CtfTeamFindService ctfTeamFindService;
  private final MemberFindService memberFindService;

  @Transactional
  public void createTeam(Member member, String name, String description, long contestId) {
    CtfContest contest = ctfContestFindService.findJoinableById(contestId);
    checkMemberHasTeam(contest, member);

    CtfTeam ctfTeam = CtfTeam.builder()
        .name(name)
        .description(description)
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
  public void updateTeam(Member member, long teamId, String name, String description) {
    CtfTeam ctfTeam = ctfTeamFindService.findById(teamId);

    if (!member.isCreator(ctfTeam)) {
      throw new BusinessException(ctfTeam.getName(), "ctfTeam", CTF_TEAM_INACCESSIBLE);
    }
    ctfTeam.update(name, description);
  }

  public CtfTeamDetailResponse getTeam(long teamId) {
    CtfTeam ctfTeam = ctfTeamFindService.findById(teamId);
    return CtfTeamDetailResponse.from(ctfTeam);
  }

  public Page<CtfTeamResponse> getTeams(long contestId, String search, Pageable pageable) {
    CtfContest contest = ctfContestFindService.findJoinableById(contestId);
    return ctfTeamRepository.findAllByCtfContestAndNameIgnoreCaseContaining(contest, search, pageable)
        .map(CtfTeamResponse::from);
  }

  @Transactional
  public void joinTeam(long contestId, long teamId, long memberId) {
    CtfContest contest = ctfContestFindService.findJoinableById(contestId);
    Member member = memberFindService.findById(memberId);
    checkMemberHasTeam(contest, member);

    CtfTeam ctfTeam = ctfTeamFindService.findById(teamId);
    member.join(ctfTeam);
  }

  @Transactional
  public void leaveTeam(long teamId, long memberId) {
    CtfTeam ctfTeam = ctfTeamFindService.findById(teamId);
    Member member = memberFindService.findById(memberId);
    member.leave(ctfTeam);
  }
}
