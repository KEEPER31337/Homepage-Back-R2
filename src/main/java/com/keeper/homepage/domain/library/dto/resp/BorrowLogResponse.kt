package com.keeper.homepage.domain.library.dto.resp

import com.fasterxml.jackson.annotation.JsonFormat
import com.keeper.homepage.domain.library.entity.BookBorrowLog
import java.time.LocalDateTime

data class BorrowLogResponse(
        val borrowInfoId: Long,
        val bookId: Long,
        val bookTitle: String,
        val author: String,
        val borrowerId: Long,
        val borrowerRealName: String,
        @JsonFormat(pattern = RESPONSE_DATETIME_FORMAT)
        val borrowDateTime: LocalDateTime?,
        @JsonFormat(pattern = RESPONSE_DATETIME_FORMAT)
        val expireDateTime: LocalDateTime?,
        @JsonFormat(pattern = RESPONSE_DATETIME_FORMAT)
        val returnDateTime: LocalDateTime?,
        @JsonFormat(pattern = RESPONSE_DATETIME_FORMAT)
        val rejectDateTime: LocalDateTime?,
        val status: String
) {
    constructor(log: BookBorrowLog) : this(
            borrowInfoId = log.borrowId,
            bookId = log.bookId,
            bookTitle = log.bookTitle,
            author = log.bookAuthor,
            borrowerId = log.memberId,
            borrowerRealName = log.memberRealName,
            borrowDateTime = log.borrowDate,
            expireDateTime = log.expireDate,
            returnDateTime = log.returnDate,
            rejectDateTime = log.rejectDate,
            status = log.borrowStatus,
    )
}

