package com.keeper.homepage.domain.post.converter;

import com.keeper.homepage.domain.post.entity.category.Category.CategoryType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

@Converter
@Slf4j
public class CategoryTypeConverter implements AttributeConverter<CategoryType, String> {

  @Override
  public String convertToDatabaseColumn(CategoryType type) {
    return type != null ? type.getName() : null;
  }

  @Override
  public CategoryType convertToEntityAttribute(String dbData) {
    if (dbData == null) {
      return null;
    }
    try {
      return CategoryType.fromCode(dbData);
    } catch (IllegalArgumentException e) {
      log.error("failure to convert cause unexpected code [{}]", dbData, e);
      throw e;
    }
  }
}
