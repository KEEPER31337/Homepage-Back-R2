package com.keeper.homepage.domain.library.converter

import com.keeper.homepage.domain.library.dto.req.BorrowStatusDto
import org.springframework.core.convert.converter.Converter

class BorrowStatusDtoConverter : Converter<String, BorrowStatusDto?> {
    override fun convert(source: String): BorrowStatusDto? {
        return BorrowStatusDto.values().find { dto -> dto.isMatch(source) }
    }
}
