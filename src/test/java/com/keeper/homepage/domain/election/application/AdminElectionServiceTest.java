package com.keeper.homepage.domain.election.application;


import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.election.entity.Election;
import com.keeper.homepage.global.error.BusinessException;
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

}

