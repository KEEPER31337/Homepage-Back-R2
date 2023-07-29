package com.keeper.homepage.domain.ctf;

import com.keeper.homepage.domain.ctf.dao.CtfContestRepository;
import com.keeper.homepage.domain.ctf.entity.CtfContest;
import com.keeper.homepage.domain.member.MemberTestHelper;
import com.keeper.homepage.domain.member.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CtfContestTestHelper {

  @Autowired
  CtfContestRepository ctfContestRepository;

  @Autowired
  MemberTestHelper memberTestHelper;

  public CtfContest generate() {
    return this.builder().build();
  }

  public CtfContestBuilder builder() {
    return new CtfContestBuilder();
  }

  public final class CtfContestBuilder {

    private String name;
    private String description;
    private Member creator;
    private Boolean isJoinable;

    private CtfContestBuilder() {

    }

    public CtfContestBuilder name(String name) {
      this.name = name;
      return this;
    }

    public CtfContestBuilder description(String description) {
      this.description = description;
      return this;
    }

    public CtfContestBuilder creator(Member creator) {
      this.creator = creator;
      return this;
    }

    public CtfContestBuilder isJoinable(Boolean isJoinable) {
      this.isJoinable = isJoinable;
      return this;
    }

    public CtfContest build() {
      return ctfContestRepository.save(CtfContest.builder()
          .name(name != null ? name : "2023 CTF")
          .description(description != null ? description : "2023 CTF 대회")
          .creator(creator != null ? creator : memberTestHelper.generate())
          .isJoinable(isJoinable != null ? isJoinable : true)
          .build());
    }
  }
}
