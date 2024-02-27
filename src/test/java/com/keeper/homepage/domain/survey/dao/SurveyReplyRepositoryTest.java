package com.keeper.homepage.domain.survey.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.survey.entity.SurveyReply;
import com.keeper.homepage.domain.survey.entity.SurveyReply.SurveyReplyType;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@Disabled
public class SurveyReplyRepositoryTest extends IntegrationTest {

  @Nested
  @DisplayName("활동조사 응답 테스트")
  class SurveyReplyTypeTest {

    @Test
    @DisplayName("DB에 활동조사 응답의 모든 타입이 있어야 한다.")
    public void DB에_활동조사_응답의_모든_타입이_있어야_한다() throws Exception {
      List<SurveyReply> allReplyRepository = surveyReplyRepository.findAll();

      List<SurveyReply> allReplyTypes = Arrays.stream(SurveyReplyType.values())
          .map(SurveyReply::getSurveyReply)
          .toList();

      assertThat(getId(allReplyRepository)).containsAll(getId(allReplyTypes));
      assertThat(getType(allReplyRepository)).containsAll(getType(allReplyTypes));
    }

    private static List<Long> getId(List<SurveyReply> surveyReplies) {
      return surveyReplies.stream()
          .map(SurveyReply::getId)
          .toList();
    }

    private static List<SurveyReplyType> getType(List<SurveyReply> surveyReplies) {
      return surveyReplies.stream()
          .map(SurveyReply::getType)
          .toList();
    }
  }
}
