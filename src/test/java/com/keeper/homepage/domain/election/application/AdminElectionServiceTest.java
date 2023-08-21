package com.keeper.homepage.domain.election.application;


import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.election.entity.Election;
import com.keeper.homepage.domain.election.entity.ElectionCandidate;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.member.entity.job.MemberJob;
import com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType;
import com.keeper.homepage.global.error.BusinessException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;


public class AdminElectionServiceTest extends IntegrationTest {

  @Nested
  @DisplayName("선거 삭제 테스트")
  class DeleteElection {

    @Test
    @DisplayName("선거 삭제 시 삭제가 성공한다.")
    public void 선거_삭제_시_삭제가_성공한다() throws Exception {
      Election election = electionTestHelper.builder()
          .name("제 1회 임원진 선거")
          .description("임원진 선거입니다.")
          .isAvailable(false)
          .build();

      long electionId = election.getId();
      adminElectionService.deleteElection(electionId);

      em.flush();
      em.clear();

      assertThat(electionRepository.findById(electionId)).isEmpty();
    }

    @Test
    @DisplayName("선거가 공개 상태라면 삭제는 실패한다.")
    public void 선거가_공개_상태라면_삭제는_실패한다() throws Exception {
      Election election = electionTestHelper.builder()
          .name("제 1회 임원진 선거")
          .description("임원진 선거입니다.")
          .isAvailable(true)
          .build();

      long electionId = election.getId();

      assertThrows(BusinessException.class, () -> adminElectionService.deleteElection(electionId));
    }

  }

  @Nested
  @DisplayName("선거 후보자 등록 테스트")
  class RegisterCandidateTest {

    private Election election;
    private Member candidate;
    private String description;

    @BeforeEach
    void setUp() {
      election = electionTestHelper.generate();
      candidate = memberTestHelper.generate();
      description = "후보";
    }

    @Test
    @DisplayName("선거 후보자 등록에 성공해야 한다.")
    public void 선거_후보자_등록에_성공해야_한다() {
      Long electionCandidateId = adminElectionService
          .registerCandidate(election.getId(), candidate.getId(), description, 1);

      em.flush();
      em.clear();

      ElectionCandidate findElectionCandidate = electionCandidateRepository.findById(electionCandidateId)
          .orElseThrow();
      MemberJob findMemberJob = memberJobRepository.findById(1L).orElseThrow();

      assertThat(findElectionCandidate.getElection().getId()).isEqualTo(election.getId());
      assertThat(findElectionCandidate.getMember().getId()).isEqualTo(candidate.getId());
      assertThat(findElectionCandidate.getMemberJob()).isEqualTo(findMemberJob);
      assertThat(findElectionCandidate.getDescription()).isEqualTo(description);
      assertThat(findElectionCandidate.getVoteCount()).isEqualTo(0L);
    }
  }

}

