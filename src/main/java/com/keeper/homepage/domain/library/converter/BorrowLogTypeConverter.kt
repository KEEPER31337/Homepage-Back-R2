package com.keeper.homepage.domain.library.converter

import com.keeper.homepage.domain.library.dto.req.BorrowStatusDto
import com.keeper.homepage.domain.library.entity.BookBorrowLog.LogType
import org.springframework.core.convert.converter.Converter

class BorrowLogTypeConverter : Converter<String, LogType?> {
    override fun convert(source: String): LogType? {
        return LogType.valueOf(source)
    }
}
