package com.keeper.homepage.domain.survey.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.survey.entity.Survey;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@Disabled
public class SurveyRepositoryTest extends IntegrationTest {

  @Nested
  @DisplayName("DB 기본 값 테스트")
  class SurveyDefaultTest {

    @Test
    @DisplayName("Survey 저장 시 default 값 설정이 적용되어야 한다.")
    public void Survey_저장_시_default_값_설정이_적용되어야_한다() throws Exception {
      Survey survey = surveyTestHelper.generate();

      em.flush();
      em.clear();
      survey = surveyRepository.findById(survey.getId()).orElseThrow();

      assertThat(survey.getOpenTime()).isBefore(LocalDateTime.now());
      assertThat(survey.getName()).isNotNull();
      assertThat(survey.isVisible()).isEqualTo(false);
    }
  }
}
