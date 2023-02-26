package com.keeper.homepage.domain.seminar.converter;

import com.keeper.homepage.domain.seminar.entity.SeminarAttendanceStatus.SeminarAttendanceStatusType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

@Converter
@Slf4j
public class SeminarAttendanceStatusTypeConverter implements
    AttributeConverter<SeminarAttendanceStatusType, String> {

  @Override
  public String convertToDatabaseColumn(SeminarAttendanceStatusType type) {
    return type != null ? type.getType() : null;
  }

  @Override
  public SeminarAttendanceStatusType convertToEntityAttribute(String dbData) {
    if (dbData == null) {
      return null;
    }
    try {
      return SeminarAttendanceStatusType.fromCode(dbData);
    } catch (IllegalArgumentException e) {
      log.error("failure to convert cause unexpected code [{}]", dbData, e);
      throw e;
    }
  }
}
