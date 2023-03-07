package com.keeper.homepage.domain.library.converter;

import com.keeper.homepage.domain.library.entity.BookDepartment.BookDepartmentType;
import com.keeper.homepage.global.error.BusinessException;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

@Converter
@Slf4j
public class BookDepartmentTypeConverter implements AttributeConverter<BookDepartmentType, String> {

  @Override
  public String convertToDatabaseColumn(BookDepartmentType bookDepartmentType) {
    return bookDepartmentType != null ? bookDepartmentType.getName() : null;
  }

  @Override
  public BookDepartmentType convertToEntityAttribute(String dbData) {
    if (dbData == null) {
      return null;
    }
    try {
      return BookDepartmentType.fromCode(dbData);
    } catch (BusinessException e) {
      log.error("failure to convert cause unexpected code [{}]", dbData, e);
      throw e;
    }
  }
}
