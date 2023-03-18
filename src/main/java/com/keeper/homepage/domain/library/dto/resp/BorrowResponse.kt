package com.keeper.homepage.domain.library.dto.resp

import com.fasterxml.jackson.annotation.JsonFormat
import com.keeper.homepage.domain.library.entity.BookBorrowInfo
import java.time.LocalDateTime

const val RESPONSE_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss"

data class BorrowResponse constructor(
    val borrowInfoId: Long,
    val bookId: Long,
    val bookTitle: String,
    val author: String,
    val borrowerId: Long,
    val borrowerNickname: String,
    @JsonFormat(pattern = RESPONSE_DATETIME_FORMAT)
    val requestDatetime: LocalDateTime,
) {
    constructor(borrowInfo: BookBorrowInfo) : this(
        borrowInfoId = borrowInfo.id,
        bookId = borrowInfo.book.id,
        bookTitle = borrowInfo.book.title,
        author = borrowInfo.book.author,
        borrowerId = borrowInfo.member.id,
        borrowerNickname = borrowInfo.member.nickname,
        requestDatetime = borrowInfo.registerTime,
    )
}
