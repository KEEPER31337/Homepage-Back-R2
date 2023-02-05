package com.keeper.homepage.domain.about.converter;

import com.keeper.homepage.domain.about.entity.StaticWriteTitle.StaticWriteTitleType;
import com.keeper.homepage.global.error.BusinessException;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

@Converter
@Slf4j
public class StaticWriteTitleTypeConverter implements
    AttributeConverter<StaticWriteTitleType, String> {

  @Override
  public String convertToDatabaseColumn(StaticWriteTitleType staticWriteTitleType) {
    return staticWriteTitleType != null ? staticWriteTitleType.getType() : null;
  }

  @Override
  public StaticWriteTitleType convertToEntityAttribute(String dbData) {
    if (dbData == null) {
      return null;
    }
    try {
      return StaticWriteTitleType.fromCode(dbData);
    } catch (BusinessException e) {
      log.error("failure to convert cause unexpected code [{}]", dbData, e);
      throw e;
    }
  }
}
