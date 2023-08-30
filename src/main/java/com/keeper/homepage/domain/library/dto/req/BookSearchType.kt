package com.keeper.homepage.domain.library.dto.req

enum class BookSearchType(val type: String) {
    TITLE("title"),
    AUTHOR("author"),
    ALL("all"),
    ;
}

