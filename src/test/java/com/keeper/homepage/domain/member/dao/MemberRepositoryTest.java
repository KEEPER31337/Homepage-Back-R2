package com.keeper.homepage.domain.member.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.member.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class MemberRepositoryTest extends IntegrationTest {

  @Nested
  @DisplayName("DB Default 테스트")
  class Default {

    @Test
    @DisplayName("default 값이 있는 컬럼은 null로 저장해도 저장에 성공한다.")
    void should_saveSuccessfully_when_defaultColumnIsNull() {
      Member memberBeforeSave = Member.builder()
          .loginId("ABC")
          .emailAddress("ABC@keeper.com")
          .password("password")
          .realName("realName")
          .nickname("nickname")
          .build();

      assertThat(memberBeforeSave.getPoint()).isNull();
      assertThat(memberBeforeSave.getLevel()).isNull();
      assertThat(memberBeforeSave.getMerit()).isNull();
      assertThat(memberBeforeSave.getDemerit()).isNull();
      assertThat(memberBeforeSave.getTotalAttendance()).isNull();

      Long memberId = memberRepository.save(memberBeforeSave).getId();
      em.flush();
      em.clear();
      Member savedMember = memberRepository.findById(memberId).orElseThrow();

      assertThat(savedMember.getPoint()).isEqualTo(0L);
      assertThat(savedMember.getLevel()).isEqualTo(0L);
      assertThat(savedMember.getMerit()).isEqualTo(0L);
      assertThat(savedMember.getDemerit()).isEqualTo(0L);
      assertThat(savedMember.getTotalAttendance()).isEqualTo(0L);
    }
  }
}
