package com.keeper.homepage.domain.study.dao;

import static org.assertj.core.api.Assertions.*;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.study.entity.Study;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class StudyRepositoryTest extends IntegrationTest {

  private Study study;
  private Member member;

  @BeforeEach
  void setUp() {
    study = studyTestHelper.generate();
    member = memberTestHelper.generate();
  }

  @Nested
  @DisplayName("DB 스터디 관리 테스트")
  class StudyTest {
    @Test
    @DisplayName("default 값이 있는 컬럼을 비워도 스터디 등록을 성공해야 한다.")
    public void should_successfullyRegister_when_defaultColumnIsNull() {
      Study studyBeforeSave = Study.builder()
          .title("스터디명")
          .information("스터디 소개")
          .headMember(member)
          .build();

      assertThat(studyBeforeSave.getMemberNumber()).isNull();
      assertThat(studyBeforeSave.getThumbnail()).isNull();
      assertThat(studyBeforeSave.getRegisterTime()).isNull();
      assertThat(studyBeforeSave.getUpdateTime()).isNull();

      Long studyId = studyRepository.save(studyBeforeSave).getId();
      em.flush();
      em.clear();
      Study savedStudy = studyRepository.findById(studyId).orElseThrow();

      assertThat(savedStudy.getMemberNumber()).isEqualTo(0L);
      assertThat(savedStudy.getThumbnail().getId()).isEqualTo(1L);
    }
    @Test
    @DisplayName("등록된 스터디인 경우 성공적으로 삭제되어야 한다.")
    public void should_successfullyDelete_study(){
      Long studyId = studyRepository.save(study).getId();
      em.flush();
      em.clear();
      Study savedStudy = studyRepository.findById(studyId).orElseThrow();
      studyRepository.delete(savedStudy);

      assertThatThrownBy(() -> studyRepository.findById(savedStudy.getId()).orElseThrow()).isInstanceOf(
          NoSuchElementException.class);
    }
  }
}
