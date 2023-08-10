package com.keeper.homepage.domain.ctf.application;

import com.keeper.homepage.domain.ctf.application.convenience.CtfContestFindService;
import com.keeper.homepage.domain.ctf.dao.CtfContestRepository;
import com.keeper.homepage.domain.ctf.dto.response.contest.CtfContestResponse;
import com.keeper.homepage.domain.ctf.entity.CtfContest;
import com.keeper.homepage.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CtfContestService {

  private final CtfContestRepository ctfContestRepository;
  private final CtfContestFindService ctfContestFindService;

  @Transactional
  public void createContest(Member member, String name, String description) {
    CtfContest contest = CtfContest.builder()
        .creator(member)
        .name(name)
        .description(description)
        .isJoinable(false)
        .build();

    ctfContestRepository.save(contest);
  }

  @Transactional
  public void updateContest(long contestId, String name, String description, boolean isJoinable) {
    CtfContest contest = ctfContestFindService.findById(contestId);

    contest.update(name, description, isJoinable);
  }

  @Transactional
  public void openContest(long contestId) {
    CtfContest contest = ctfContestFindService.findById(contestId);
    contest.open();
  }

  @Transactional
  public void closeContest(long contestId) {
    CtfContest contest = ctfContestFindService.findById(contestId);
    contest.close();
  }

  public Page<CtfContestResponse> getContests(Pageable pageable) {
    return ctfContestFindService.findAll(pageable)
        .map(CtfContestResponse::from);
  }
}
