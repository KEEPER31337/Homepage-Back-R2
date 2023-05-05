package com.keeper.homepage.domain.survey.converter;

import com.keeper.homepage.domain.survey.entity.SurveyReply.SurveyReplyType;
import com.keeper.homepage.global.error.BusinessException;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

@Converter
@Slf4j
public class SurveyReplyTypeConverter implements AttributeConverter<SurveyReplyType, String> {

  @Override
  public String convertToDatabaseColumn(SurveyReplyType type) {
    return type != null ? type.getType() : null;
  }

  @Override
  public SurveyReplyType convertToEntityAttribute(String dbData) {
    if (dbData == null) {
      return null;
    }
    try {
      return SurveyReplyType.fromCode(dbData);
    } catch (BusinessException e) {
      log.error("failure to convert cause unexpected code [{}]", dbData, e);
      throw e;
    }
  }
}
