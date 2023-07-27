package com.keeper.homepage.domain.ctf;

import com.keeper.homepage.domain.ctf.dao.team.CtfTeamRepository;
import com.keeper.homepage.domain.ctf.entity.CtfContest;
import com.keeper.homepage.domain.ctf.entity.team.CtfTeam;
import com.keeper.homepage.domain.member.MemberTestHelper;
import com.keeper.homepage.domain.member.entity.Member;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CtfTeamTestHelper {

  @Autowired
  CtfTeamRepository ctfTeamRepository;

  @Autowired
  MemberTestHelper memberTestHelper;

  @Autowired
  CtfContestTestHelper ctfContestTestHelper;

  public CtfTeam generate() {
    return this.builder().build();
  }

  public CtfTeamBuilder builder() {
    return new CtfTeamBuilder();
  }

  public final class CtfTeamBuilder {

    private String name;
    private String description;
    private Member creator;
    private Integer score;
    private CtfContest ctfContest;
    private LocalDateTime lastSolveTime;

    private CtfTeamBuilder() {

    }

    public CtfTeamBuilder name(String name) {
      this.name = name;
      return this;
    }

    public CtfTeamBuilder description(String description) {
      this.description = description;
      return this;
    }

    public CtfTeamBuilder creator(Member creator) {
      this.creator = creator;
      return this;
    }

    public CtfTeamBuilder score(Integer score) {
      this.score = score;
      return this;
    }

    public CtfTeamBuilder ctfContest(CtfContest ctfContest) {
      this.ctfContest = ctfContest;
      return this;
    }

    public CtfTeamBuilder lastSolveTime(LocalDateTime lastSolveTime) {
      this.lastSolveTime = lastSolveTime;
      return this;
    }

    public CtfTeam build() {
      return ctfTeamRepository.save(CtfTeam.builder()
          .name(name != null ? name : "KEEPER TEAM")
          .description(description != null ? description : "2023 CTF TEAM")
          .creator(creator != null ? creator : memberTestHelper.generate())
          .score(score != null ? score : 0)
          .ctfContest(ctfContest != null ? ctfContest : ctfContestTestHelper.generate())
          .lastSolveTime(lastSolveTime != null ? lastSolveTime : LocalDateTime.now())
          .build());
    }
  }

}
