package com.keeper.homepage.domain.survey.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.survey.entity.SurveyMemberReply;
import com.keeper.homepage.domain.survey.entity.SurveyReplyExcuse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class SurveyMemberReplyRepositoryTest extends IntegrationTest {

  private SurveyMemberReply memberReply;
  private long memberReplyId;

  @BeforeEach
  void setUp() {
    memberReply = surveyMemberReplyTestHelper.generate();
    memberReplyId = memberReply.getId();
  }

  @Nested
  @DisplayName("활동 조사 응답 DB 테스트")
  class SurveyMemberReplyTest {

    @Test
    @DisplayName("활동조사 기타 사유는 활동 조사 응답이 관리한다.")
    public void 활동조사_기타_사유는_활동_조사_응답이_관리한다() throws Exception {
      SurveyReplyExcuse excuse = SurveyReplyExcuse.builder()
          .restExcuse("BOB 합격을 하게되어 휴면 신청합니다.")
          .build();
      memberReply.changeReplyExcuse(excuse);

      em.flush();
      em.clear();
      SurveyMemberReply memberReply = surveyMemberReplyRepository.findById(memberReplyId).orElseThrow();

      assertThat(memberReply.getReplyExcuse().getRestExcuse()).isEqualTo(excuse.getRestExcuse());
      assertThat(surveyReplyExcuseRepository.findById(memberReplyId)).isNotEmpty();
    }

    @Test
    @DisplayName("활동조사 응답 삭제 시 기타 사유도 지워진다.")
    public void 활동조사_응답_삭제_시_기타_사유도_지워진다() throws Exception {
      SurveyReplyExcuse excuseBuild = SurveyReplyExcuse.builder()
          .restExcuse("BOB 합격을 하게되어 휴면 신청합니다.")
          .build();
      memberReply.changeReplyExcuse(excuseBuild);

      em.flush();
      em.clear();
      surveyMemberReplyRepository.deleteById(memberReplyId);

      assertThat(surveyReplyExcuseRepository.findById(memberReplyId)).isEmpty();
    }
  }
}
