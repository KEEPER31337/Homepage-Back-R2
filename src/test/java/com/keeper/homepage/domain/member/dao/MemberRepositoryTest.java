package com.keeper.homepage.domain.member.dao;

import static com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_서기;
import static com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_출제자;
import static com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_회원;
import static com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_회장;
import static org.assertj.core.api.Assertions.assertThat;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.member.entity.job.MemberHasMemberJob;
import com.keeper.homepage.domain.member.entity.job.MemberJob;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class MemberRepositoryTest extends IntegrationTest {

  private Member member;

  @BeforeEach
  void setUp() {
    member = memberTestHelper.generate();
  }

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
      assertThat(memberBeforeSave.getTotalAttendance()).isNull();

      Long memberId = memberRepository.save(memberBeforeSave).getId();
      em.flush();
      em.clear();
      Member savedMember = memberRepository.findById(memberId).orElseThrow();

      assertThat(savedMember.getPoint()).isEqualTo(0L);
      assertThat(savedMember.getLevel()).isEqualTo(0L);
      assertThat(savedMember.getTotalAttendance()).isEqualTo(0L);
    }
  }

  @Nested
  @DisplayName("회원 권한 테스트")
  class MemberJobTest {

    @Test
    @DisplayName("회원을 저장할 때 권한을 올바르게 가지고 있는다.")
    void should_assignUserRoleCorrectly_when_saveUser() {
      em.flush();
      em.clear();

      member = memberRepository.findById(member.getId()).orElseThrow();

      assertThat(member.containsRole(ROLE_회원)).isTrue();
      assertThat(member.containsRole(ROLE_회장)).isFalse();
    }

    @Test
    @DisplayName("회원은 중복되는 권한을 가지지 않는다.")
    void should_nothingHappens_when_duplicateRoles() {
      em.flush();
      em.clear();
      Member findMember = memberRepository.findById(member.getId()).orElseThrow();

      findMember.assignJob(ROLE_회원);
      findMember.assignJob(ROLE_회원);
      findMember.assignJob(ROLE_출제자);
      findMember.assignJob(ROLE_서기);
      findMember.assignJob(ROLE_회원);
      em.flush();
      em.clear();

      assertThat(memberHasMemberJobRepository.findAll()).hasSize(3);
      List<MemberJob> memberJobs = memberHasMemberJobRepository.findAll().stream()
          .map(MemberHasMemberJob::getMemberJob)
          .toList();
      assertThat(memberJobs).containsAll(List.of(
          MemberJob.getMemberJobBy(ROLE_회원),
          MemberJob.getMemberJobBy(ROLE_출제자),
          MemberJob.getMemberJobBy(ROLE_서기)));
    }

    @Test
    @DisplayName("회원이 가지고 있는 직업은 삭제에 성공해야 한다.")
    void should_removeSuccessfully_when_hasJob() {
      em.flush();
      em.clear();
      Member findMember = memberRepository.findById(member.getId()).orElseThrow();
      findMember.assignJob(ROLE_회원);
      findMember.assignJob(ROLE_출제자);
      findMember.assignJob(ROLE_서기);

      em.flush();
      em.clear();
      findMember = memberRepository.findById(member.getId()).orElseThrow();
      findMember.deleteJob(ROLE_회원);
      findMember.deleteJob(ROLE_출제자);
      findMember.deleteJob(ROLE_서기);
      em.flush();
      em.clear();

      assertThat(memberHasMemberJobRepository.findAll()).hasSize(0);
      Assertions.assertDoesNotThrow(() -> memberRepository.findById(member.getId()).orElseThrow()
          .deleteJob(ROLE_회원));
    }
  }
}
