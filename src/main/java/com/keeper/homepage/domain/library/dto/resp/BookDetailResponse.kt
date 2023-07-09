package com.keeper.homepage.domain.library.dto.resp

import com.keeper.homepage.domain.library.entity.Book

data class BookDetailResponse(
    val id: Long,
    val title: String,
    val author: String,
    val bookDepartment: String,
    val totalCount: Long,
    val borrowingCount: Int,
    val thumbnailPath: String,
    val borrowInfos: List<BorrowDetailResponse>
) {
    constructor(book: Book) : this(
        id = book.id,
        title = book.title,
        author = book.author,
        bookDepartment = book.bookDepartment.type.name,
        totalCount = book.totalQuantity,
        borrowingCount = book.bookBorrowInfos.count { it.isInBorrowing },
        thumbnailPath = book.thumbnailPath,
        borrowInfos = book.bookBorrowInfos.map(::BorrowDetailResponse),
    )
}
