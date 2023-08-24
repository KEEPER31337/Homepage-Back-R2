package com.keeper.homepage.domain.study.application;

import static com.keeper.homepage.global.error.ErrorCode.STUDY_LINK_NEED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.study.entity.Study;
import com.keeper.homepage.domain.study.entity.StudyHasMember;
import com.keeper.homepage.domain.study.entity.embedded.Link;
import com.keeper.homepage.global.error.BusinessException;
import java.util.List;
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
        studyService.create(member, study, List.of(), null);
      }, STUDY_LINK_NEED.getMessage());
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
  @DisplayName("스터디 삭제")
  class DeleteStudy {

    @Test
    @DisplayName("스터디를 삭제하면 스터디원도 모두 삭제되어야 한다.")
    public void 스터디를_삭제하면_스터디원도_모두_삭제되어야_한다() throws Exception {
      //given
      long studyId = study.getId();

      //when
      studyService.delete(member, studyId);
      em.flush();
      em.clear();

      //then
      member = memberRepository.findById(member.getId()).orElseThrow();

      assertThat(studyRepository.findById(studyId)).isEmpty();
      assertThat(studyHasMemberRepository.findByStudyAndMember(study, member)).isEmpty();
    }
  }

  @Nested
  @DisplayName("스터디 수정")
  class UpdateStudy {

    @Test
    @DisplayName("기존의 스터디원은 삭제되고, 현재 스터디원은 추가되어야 한다.")
    public void 기존의_스터디원은_삭제되고_현재_스터디원은_추가되어야_한다() throws Exception {
      //given
      Member beforeMember = memberTestHelper.generate();
      studyHasMemberRepository.save(StudyHasMember.builder()
          .study(study)
          .member(beforeMember)
          .build());

      em.flush();
      em.clear();
      //when
      Study newStudy = Study.builder()
          .title("자바 스터디")
          .information("자바 스터디 입니다.")
          .year(2023)
          .season(2)
          .link(Link.builder()
              .etcLink("etc.com")
              .build())
          .build();
      Member afterMember = memberTestHelper.generate();
      studyService.update(member, study.getId(), newStudy, List.of(afterMember.getId()));

      em.flush();
      em.clear();
      //then
      assertThat(studyHasMemberRepository.findByStudyAndMember(study, beforeMember)).isEmpty();
      assertThat(studyHasMemberRepository.findByStudyAndMember(study, afterMember)).isNotEmpty();
    }
  }
}
