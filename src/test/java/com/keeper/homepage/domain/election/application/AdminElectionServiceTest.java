package com.keeper.homepage.domain.election.application;


import static org.junit.jupiter.api.Assertions.assertThrows;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.election.entity.Election;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.global.error.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;


public class AdminElectionServiceTest extends IntegrationTest {

  @Nested
  @DisplayName("선거 가능 여부 동작 테스트")
  class electionAvailableTest {

    private Member member;
    private Election election;

    @BeforeEach
    void setUp() {
      member = memberTestHelper.generate();
      election = electionTestHelper.generate();
    }

    @Test
    @DisplayName("선거 가능 여부가 false 라면 선거 생성은 실패해야 한다.")
    public void 선거_가능_여부가_false_라면_선거_생성은_실패해야_한다() throws Exception {
      Election election = electionTestHelper.builder()
          .member(member)
          .name("제 1회 임원진 선거")
          .description("임원진 선거입니다.")
          .isAvailable(false)
          .build();

      assertThrows(BusinessException.class, () -> adminElectionService.createElection(member, election.getName(),
          election.getDescription(), election.getIsAvailable()));
    }

  }

}

