package com.keeper.homepage.domain.ctf.converter;

import com.keeper.homepage.domain.ctf.entity.challenge.CtfChallengeCategory.CtfChallengeCategoryType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

@Converter
@Slf4j
public class CtfChallengeCategoryTypeConverter implements AttributeConverter<CtfChallengeCategoryType, String> {

  @Override
  public String convertToDatabaseColumn(CtfChallengeCategoryType type) {
    return type != null ? type.getType() : null;
  }

  @Override
  public CtfChallengeCategoryType convertToEntityAttribute(String dbData) {
    if (dbData == null) {
      return null;
    }
    try {
      return CtfChallengeCategoryType.fromCode(dbData);
    } catch (IllegalArgumentException e) {
      log.error("failure to convert cause unexpected code [{}]", dbData, e);
      throw e;
    }
  }
}
