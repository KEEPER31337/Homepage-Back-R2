package com.keeper.homepage.domain.library.converter;

import com.keeper.homepage.domain.library.entity.BookBorrowStatus.BookBorrowStatusType;
import com.keeper.homepage.global.error.BusinessException;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

@Converter
@Slf4j
public class BookBorrowStatusTypeConverter implements AttributeConverter<BookBorrowStatusType, String> {

    @Override
    public String convertToDatabaseColumn(BookBorrowStatusType bookBorrowStatusType) {
        return bookBorrowStatusType != null ? bookBorrowStatusType.getStatus() : null;
    }

    @Override
    public BookBorrowStatusType convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        try {
            return BookBorrowStatusType.fromCode(dbData);
        } catch (BusinessException e) {
            log.error("failure to convert cause unexpected code [{}]", dbData, e);
            throw e;
        }
    }
}
