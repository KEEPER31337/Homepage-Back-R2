package com.keeper.homepage.domain.survey;

import com.keeper.homepage.domain.survey.dao.SurveyRepository;
import com.keeper.homepage.domain.survey.entity.Survey;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SurveyTestHelper {

  @Autowired
  SurveyRepository surveyRepository;

  public Survey generate() {
    return this.builder().build();
  }

  public SurveyBuilder builder() {
    return new SurveyBuilder();
  }

  public final class SurveyBuilder {

    private LocalDateTime openTime;
    private LocalDateTime closeTime;
    private String name;
    private String description;
    private Boolean isVisible;

    public SurveyBuilder openTime(LocalDateTime openTime) {
      this.openTime = openTime;
      return this;
    }

    public SurveyBuilder closeTime(LocalDateTime closeTime) {
      this.closeTime = closeTime;
      return this;
    }

    public SurveyBuilder name(String name) {
      this.name = name;
      return this;
    }

    public SurveyBuilder description(String description) {
      this.description = description;
      return this;
    }

    public SurveyBuilder isVisible(Boolean isVisible) {
      this.isVisible = isVisible;
      return this;
    }

    public Survey build() {
      return surveyRepository.save(Survey.builder()
          .openTime(openTime)
          .closeTime(closeTime)
          .name(name)
          .description(description)
          .isVisible(isVisible)
          .build());
    }
  }
}
