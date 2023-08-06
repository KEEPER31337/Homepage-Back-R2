package com.keeper.homepage.domain.study.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.study.entity.Study;
import com.keeper.homepage.domain.study.entity.embedded.Link;
import com.keeper.homepage.global.error.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class StudyServiceTest extends IntegrationTest {

  private Member member;
  private Study study;

  @BeforeEach
  void setUp() {
    member = memberTestHelper.generate();
    study = studyTestHelper.builder().headMember(member).build();
  }

  @Nested
  @DisplayName("스터디 생성")
  class CreateStudy {

    @Test
    @DisplayName("스터디 링크가 하나도 없을 경우 스터디 생성은 실패한다.")
    public void 스터디_링크가_하나도_없을_경우_스터디_생성은_실패한다() throws Exception {
      Study study = Study.builder()
          .title("자바 스터디")
          .information("자바 스터디 입니다.")
          .year(2023)
          .season(2)
          .link(Link.builder().build())
          .build();
      assertThrows(BusinessException.class, () -> {
        studyService.create(study, null);
      });
    }
  }

  @Nested
  @DisplayName("스터디 썸네일 수정")
  class UpdateStudyThumbnail {

    @Test
    @DisplayName("썸네일을 null로 보낼 경우 기본 썸네일로 저장된다.")
    public void 썸네일을_null로_보낼_경우_기본_썸네일로_저장된다() throws Exception {
      studyService.updateStudyThumbnail(member, study.getId(), null);
      em.flush();
      em.clear();

      study = studyRepository.findById(study.getId()).get();

      assertThat(study.getThumbnail().getId()).isEqualTo(1L);
    }
  }

  @Nested
  @DisplayName("스터디원 추가")
  class AddStudyMember {

    @Test
    @DisplayName("스터디원 추가는 성공해야 한다.")
    public void 스터디원_추가는_성공해야_한다() throws Exception {
      studyService.joinStudy(study.getId(), member.getId());

      assertThat(studyHasMemberRepository.findByStudyAndMember(study, member)).isNotEmpty();
    }
  }

  @Nested
  @DisplayName("스터디원 삭제")
  class DeleteStudyMember {

    @Test
    @DisplayName("스터디원 삭제는 성공해야 한다.")
    public void 스터디원_삭제는_성공해야_한다() throws Exception {
      Member other = memberTestHelper.generate();
      studyService.joinStudy(study.getId(), other.getId());
      em.flush();
      em.clear();
      other = memberRepository.findById(other.getId()).get();
      study = studyRepository.findById(study.getId()).get();

      studyService.leaveStudy(study.getId(), other.getId());

      assertThat(studyHasMemberRepository.findByStudyAndMember(study, other)).isEmpty();
    }
  }
}
