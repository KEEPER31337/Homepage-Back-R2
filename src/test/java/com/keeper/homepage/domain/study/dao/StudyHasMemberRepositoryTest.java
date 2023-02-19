package com.keeper.homepage.domain.study.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.study.entity.Study;
import com.keeper.homepage.domain.study.entity.StudyHasMemberPK;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class StudyHasMemberRepositoryTest extends IntegrationTest {

  private Study study;
  private Member member;

  @BeforeEach
  void setUp() {
    study = studyTestHelper.generate();
    member = memberTestHelper.generate();
  }

  @Nested
  @DisplayName("DB studyHasMember(스터디-스터디원) 관리 테스트")
  class StudyHasMemberTest {
    @Test
    @DisplayName("DB에 스터디와 스터디원이 성공적으로 추가되어야 한다.")
    public void should_member_successfullyJoin_study() {
      member.join(study);

      em.flush();
      em.clear();

      assertThat(studyHasMemberRepository.findAll()).hasSize(1);
    }


    @Test
    @DisplayName("DB에 스터디와 스터디원이 성공적으로 삭제되어야 한다.")
    public void should_member_successfullyLeave_study() {
      member.join(study);
      assertThat(studyHasMemberRepository.findAll()).hasSize(1);

      member.leave(study);

      em.flush();
      em.clear();

      assertThat(studyHasMemberRepository.findAll()).hasSize(0);
    }

    @Test
    @DisplayName("스터디를 삭제하면 스터디원에 대한 정보도 삭제되어야 한다.")
    public void should_successfullyDelete_studyMember_when_studyIsDeleted() {
      Member memberA = memberTestHelper.generate();
      Member memberB = memberTestHelper.generate();

      memberA.join(study);
      memberB.join(study);

      em.flush();
      em.clear();

      StudyHasMemberPK keyA = new StudyHasMemberPK(study.getId(), memberA.getId());
      StudyHasMemberPK keyB = new StudyHasMemberPK(study.getId(), memberB.getId());
      assertThat(studyHasMemberRepository.existsById(keyA)).isTrue();
      assertThat(studyHasMemberRepository.findById(keyB)).isNotEmpty();

      study = studyRepository.findById(study.getId()).orElseThrow();
      studyRepository.delete(study);

      em.flush();
      em.clear();

      assertThat(studyHasMemberRepository.existsById(keyA)).isFalse();
      assertThat(studyHasMemberRepository.findById(keyB)).isEmpty();
      assertThatThrownBy(() -> studyHasMemberRepository.findById(keyA).orElseThrow())
          .isInstanceOf(NoSuchElementException.class);
      assertThatThrownBy(() -> studyHasMemberRepository.findById(keyB).orElseThrow())
          .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("회원이 탈퇴하면 그 회원이 포함된 스터디들에 대한 정보도 삭제되어야 한다.")
    public void should_successfullyDelete_studyMember_when_memberIsDeleted() {
      Study studyA = studyTestHelper.generate();
      Study studyB = studyTestHelper.generate();

      member.join(studyA);
      member.join(studyB);
      assertThat(studyHasMemberRepository.findAll()).hasSize(2);

      memberRepository.delete(member);

      em.flush();
      em.clear();

      StudyHasMemberPK key = new StudyHasMemberPK(study.getId(), member.getId());

      assertThatThrownBy(() -> studyHasMemberRepository.findById(key).orElseThrow())
          .isInstanceOf(NoSuchElementException.class);
    }
  }

}
