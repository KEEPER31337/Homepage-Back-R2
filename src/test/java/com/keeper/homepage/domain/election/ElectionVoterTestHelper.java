package com.keeper.homepage.domain.election;

import com.keeper.homepage.domain.election.dao.ElectionVoterRepository;
import com.keeper.homepage.domain.election.entity.Election;
import com.keeper.homepage.domain.election.entity.ElectionVoter;
import com.keeper.homepage.domain.member.MemberTestHelper;
import com.keeper.homepage.domain.member.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ElectionVoterTestHelper {

  @Autowired
  ElectionVoterRepository electionVoterRepository;
  @Autowired
  ElectionTestHelper electionTestHelper;
  @Autowired
  MemberTestHelper memberTestHelper;

  public ElectionVoter generate() {
    return this.builder().build();
  }

  public ElectionVoterBuilder builder() {
    return new ElectionVoterBuilder();
  }

  public final class ElectionVoterBuilder {

    private Member member;
    private Election election;
    private Boolean isVoted;

    public ElectionVoterBuilder member(Member member) {
      this.member = member;
      return this;
    }

    public ElectionVoterBuilder election(Election election) {
      this.election = election;
      return this;
    }

    public ElectionVoterBuilder isVoted(Boolean isVoted) {
      this.isVoted = isVoted;
      return this;
    }

    public ElectionVoter build() {
      return electionVoterRepository.save(ElectionVoter.builder()
          .member(member != null ? member : memberTestHelper.generate())
          .election(election != null ? election : electionTestHelper.generate())
          .isVoted(isVoted != null ? isVoted : false)
          .build());
    }
  }
}
