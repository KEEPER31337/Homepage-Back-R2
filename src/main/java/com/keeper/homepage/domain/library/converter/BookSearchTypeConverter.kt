package com.keeper.homepage.domain.library.converter

import com.keeper.homepage.domain.library.dto.req.BookSearchType
import org.springframework.core.convert.converter.Converter
import java.util.*

class BookSearchTypeConverter : Converter<String, BookSearchType> {
    override fun convert(source: String): BookSearchType {
        return Arrays.stream(BookSearchType.values())
                .filter { bookSearchType: BookSearchType -> bookSearchType.type == source.lowercase() }
                .findFirst()
                .get()
    }
}
