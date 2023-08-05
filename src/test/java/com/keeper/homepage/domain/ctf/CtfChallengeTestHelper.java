package com.keeper.homepage.domain.ctf;

import static com.keeper.homepage.domain.ctf.entity.challenge.CtfChallengeType.ChallengeType.STANDARD;
import static com.keeper.homepage.domain.ctf.entity.challenge.CtfChallengeType.getCtfChallengeType;

import com.keeper.homepage.domain.ctf.dao.challenge.CtfChallengeRepository;
import com.keeper.homepage.domain.ctf.entity.CtfContest;
import com.keeper.homepage.domain.ctf.entity.challenge.CtfChallenge;
import com.keeper.homepage.domain.ctf.entity.challenge.CtfChallengeType;
import com.keeper.homepage.domain.member.MemberTestHelper;
import com.keeper.homepage.domain.member.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CtfChallengeTestHelper {

  @Autowired
  CtfChallengeRepository ctfChallengeRepository;

  @Autowired
  MemberTestHelper memberTestHelper;

  @Autowired
  CtfContestTestHelper ctfContestTestHelper;

  public CtfChallenge generate() {
    return this.builder().build();
  }

  public CtfChallengeBuilder builder() {
    return new CtfChallengeBuilder();
  }

  public final class CtfChallengeBuilder {

    private String name;
    private String description;
    private Member creator;
    private Boolean isSolvable;
    private CtfChallengeType type;
    private Integer score;
    private CtfContest ctfContest;
    private Integer maxSubmitCount;

    private CtfChallengeBuilder() {

    }

    public CtfChallengeBuilder name(String name) {
      this.name = name;
      return this;
    }

    public CtfChallengeBuilder description(String description) {
      this.description = description;
      return this;
    }

    public CtfChallengeBuilder creator(Member creator) {
      this.creator = creator;
      return this;
    }

    public CtfChallengeBuilder isSolvable(Boolean isSolvable) {
      this.isSolvable = isSolvable;
      return this;
    }

    public CtfChallengeBuilder type(CtfChallengeType type) {
      this.type = type;
      return this;
    }

    public CtfChallengeBuilder score(Integer score) {
      this.score = score;
      return this;
    }

    public CtfChallengeBuilder ctfContest(CtfContest ctfContest) {
      this.ctfContest = ctfContest;
      return this;
    }

    public CtfChallengeBuilder maxSubmitCount(Integer maxSubmitCount) {
      this.maxSubmitCount = maxSubmitCount;
      return this;
    }

    public CtfChallenge build() {
      return ctfChallengeRepository.save(CtfChallenge.builder()
          .name(name != null ? name : "CTF 문제이름")
          .description(description != null ? description : "CTF 문제설명")
          .creator(creator != null ? creator : memberTestHelper.generate())
          .isSolvable(isSolvable != null ? isSolvable : true)
          .type(type != null ? type : getCtfChallengeType(STANDARD))
          .score(score != null ? score : 1000)
          .ctfContest(ctfContest != null ? ctfContest : ctfContestTestHelper.generate())
          .maxSubmitCount(maxSubmitCount != null ? maxSubmitCount : 15)
          .build());
    }
  }
}
