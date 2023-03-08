package com.keeper.homepage.domain.library.application

import com.keeper.homepage.domain.library.dao.BookRepository
import com.keeper.homepage.domain.library.entity.Book
import com.keeper.homepage.domain.library.entity.BookDepartment
import com.keeper.homepage.global.util.thumbnail.ThumbnailUtil
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
@Transactional(readOnly = true)
class BookManageService(
    val bookRepository: BookRepository,
    val thumbnailUtil: ThumbnailUtil
) {
    @Transactional
    fun addBook(
        title: String,
        author: String,
        totalQuantity: Long,
        bookDepartment: BookDepartment.BookDepartmentType,
        thumbnail: MultipartFile?
    ): Long = bookRepository.save(
        Book(
            title,
            author,
            BookDepartment.getBookDepartmentBy(bookDepartment),
            totalQuantity,
            0L,
            thumbnailUtil.saveThumbnail(thumbnail)
                .orElse(null)
        )
    ).id

    @Transactional
    fun deleteBook(bookId: Long) {
        TODO("Not yet implemented")
    }
}
