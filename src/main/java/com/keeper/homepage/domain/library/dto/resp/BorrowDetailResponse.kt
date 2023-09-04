package com.keeper.homepage.domain.library.dto.resp

import com.fasterxml.jackson.annotation.JsonFormat
import com.keeper.homepage.domain.library.entity.BookBorrowInfo
import java.time.LocalDateTime

const val RESPONSE_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss"

data class BorrowDetailResponse(
    val borrowInfoId: Long,
    val bookId: Long,
    val bookTitle: String,
    val author: String,
    val totalQuantity: Long,
    val currentQuantity: Long,
    val borrowerId: Long,
    val borrowerRealName: String,
    @JsonFormat(pattern = RESPONSE_DATETIME_FORMAT)
    val requestDatetime: LocalDateTime?,
    @JsonFormat(pattern = RESPONSE_DATETIME_FORMAT)
    val borrowDateTime: LocalDateTime?,
    @JsonFormat(pattern = RESPONSE_DATETIME_FORMAT)
    val expiredDateTime: LocalDateTime?,
    val status: String
) {
    constructor(borrowInfo: BookBorrowInfo) : this(
        borrowInfoId = borrowInfo.id,
        bookId = borrowInfo.book.id,
        bookTitle = borrowInfo.book.title,
        author = borrowInfo.book.author,
        totalQuantity = borrowInfo.book.totalQuantity,
        currentQuantity = borrowInfo.book.currentQuantity,
        borrowerId = borrowInfo.member.id,
        borrowerRealName = borrowInfo.member.realName,
        requestDatetime = borrowInfo.lastRequestDate,
        borrowDateTime = borrowInfo.borrowDate,
        expiredDateTime = borrowInfo.expireDate,
        status = borrowInfo.borrowStatus.type.name,
    )
}

