package com.keeper.homepage.domain.election;

import com.keeper.homepage.domain.election.dao.ElectionCandidateRepository;
import com.keeper.homepage.domain.election.entity.Election;
import com.keeper.homepage.domain.election.entity.ElectionCandidate;
import com.keeper.homepage.domain.member.MemberTestHelper;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.member.entity.job.MemberJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ElectionCandidateTestHelper {

  @Autowired
  ElectionCandidateRepository electionCandidateRepository;
  @Autowired
  ElectionTestHelper electionTestHelper;
  @Autowired
  MemberTestHelper memberTestHelper;

  public ElectionCandidate generate() {
    return this.builder().build();
  }

  public ElectionCandidateBuilder builder() {
    return new ElectionCandidateBuilder();
  }

  public final class ElectionCandidateBuilder {

    private Election election;
    private Member member;
    private MemberJob memberJob;
    private String description;
    private Long voteCount;

    public ElectionCandidateBuilder election(Election election) {
      this.election = election;
      return this;
    }

    public ElectionCandidateBuilder member(Member member) {
      this.member = member;
      return this;
    }

    public ElectionCandidateBuilder memberJob(MemberJob memberJob) {
      this.memberJob = memberJob;
      return this;
    }

    public ElectionCandidateBuilder description(String description) {
      this.description = description;
      return this;
    }

    public ElectionCandidateBuilder voteCount(Long voteCount) {
      this.voteCount = voteCount;
      return this;
    }

    public ElectionCandidate build() {
      return electionCandidateRepository.save(ElectionCandidate.builder()
          .election(election != null ? election : electionTestHelper.generate())
          .member(member != null ? member : memberTestHelper.generate())
          .memberJob(memberJob)
          .description(description != null ? description : "후보")
          .voteCount(voteCount != null ? voteCount : 0)
          .build());
    }
  }

}
