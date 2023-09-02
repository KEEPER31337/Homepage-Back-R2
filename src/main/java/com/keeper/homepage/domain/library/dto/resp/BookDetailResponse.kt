package com.keeper.homepage.domain.library.dto.resp

import com.keeper.homepage.domain.library.entity.Book

data class BookDetailResponse(
    val bookId: Long,
    val title: String,
    val author: String,
    val bookDepartment: String,
    val totalQuantity: Long,
    val currentQuantity: Long,
    val thumbnailPath: String?,
    val borrowInfos: List<BorrowDetailResponse>
) {
    constructor(book: Book) : this(
        bookId = book.id,
        title = book.title,
        author = book.author,
        bookDepartment = book.bookDepartment.type.name,
        totalQuantity = book.totalQuantity,
        currentQuantity = book.currentQuantity,
        thumbnailPath = book.thumbnailPath,
        borrowInfos = book.bookBorrowInfos
            .filter { borrowInfo -> borrowInfo.isInBorrowing }
            .map(::BorrowDetailResponse),
    )
}
