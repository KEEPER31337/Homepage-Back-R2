package com.keeper.homepage.domain.study.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.study.entity.Study;
import com.keeper.homepage.domain.study.entity.StudyHasMemberPK;
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

      StudyHasMemberPK key = new StudyHasMemberPK(study.getId(), member.getId());
      assertThat(studyHasMemberRepository.existsById(key)).isTrue();
    }
  }
}
