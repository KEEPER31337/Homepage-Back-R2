package com.keeper.homepage.domain.ctf;

import com.keeper.homepage.domain.ctf.dao.CtfFlagRepository;
import com.keeper.homepage.domain.ctf.entity.CtfFlag;
import com.keeper.homepage.domain.ctf.entity.challenge.CtfChallenge;
import com.keeper.homepage.domain.ctf.entity.team.CtfTeam;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CtfFlagTestHelper {

  @Autowired
  CtfFlagRepository ctfFlagRepository;

  @Autowired
  CtfTeamTestHelper ctfTeamTestHelper;

  @Autowired
  CtfChallengeTestHelper ctfChallengeTestHelper;

  public CtfFlag generate() {
    return this.builder().build();
  }

  public CtfFlagBuilder builder() {
    return new CtfFlagBuilder();
  }

  public final class CtfFlagBuilder {

    private String content;
    private CtfTeam ctfTeam;
    private CtfChallenge ctfChallenge;
    private Boolean isCorrect;
    private LocalDateTime solvedTime;
    private LocalDateTime lastTryTime;
    private Integer remainedSubmitCount;

    private CtfFlagBuilder() {

    }

    public CtfFlagBuilder content(String content) {
      this.content = content;
      return this;
    }

    public CtfFlagBuilder ctfTeam(CtfTeam ctfTeam) {
      this.ctfTeam = ctfTeam;
      return this;
    }

    public CtfFlagBuilder ctfChallenge(CtfChallenge ctfChallenge) {
      this.ctfChallenge = ctfChallenge;
      return this;
    }

    public CtfFlagBuilder isCorrect(Boolean isCorrect) {
      this.isCorrect = isCorrect;
      return this;
    }

    public CtfFlagBuilder solvedTime(LocalDateTime solvedTime) {
      this.solvedTime = solvedTime;
      return this;
    }

    public CtfFlagBuilder lastTryTime(LocalDateTime lastTryTime) {
      this.lastTryTime = lastTryTime;
      return this;
    }

    public CtfFlagBuilder remainedSubmitCount(Integer remainedSubmitCount) {
      this.remainedSubmitCount = remainedSubmitCount;
      return this;
    }

    public CtfFlag build() {
      return ctfFlagRepository.save(CtfFlag.builder()
          .content(content != null ? content : "KEEPER{}")
          .ctfTeam(ctfTeam != null ? ctfTeam : ctfTeamTestHelper.generate())
          .ctfChallenge(ctfChallenge != null ? ctfChallenge : ctfChallengeTestHelper.generate())
          .isCorrect(isCorrect != null ? isCorrect : true)
          .solvedTime(solvedTime != null ? solvedTime : LocalDateTime.now())
          .lastTryTime(lastTryTime != null ? lastTryTime : LocalDateTime.now())
          .remainedSubmitCount(remainedSubmitCount != null ? remainedSubmitCount : 15)
          .build());
    }
  }
}
