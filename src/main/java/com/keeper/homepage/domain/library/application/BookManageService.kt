package com.keeper.homepage.domain.library.application

import com.keeper.homepage.domain.library.dao.BookRepository
import com.keeper.homepage.domain.library.entity.Book
import com.keeper.homepage.domain.library.entity.BookDepartment
import com.keeper.homepage.global.error.BusinessException
import com.keeper.homepage.global.error.ErrorCode
import com.keeper.homepage.global.util.thumbnail.ThumbnailUtil
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

fun BookRepository.getBookById(bookId: Long) = this.findById(bookId)
    .orElseThrow { throw BusinessException(bookId, "bookId", ErrorCode.BOOK_NOT_FOUND) }

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
        val book = bookRepository.getBookById(bookId)

        if (book.isSomeoneInBorrowing) {
            throw BusinessException(bookId, "bookId", ErrorCode.BOOK_DELETE_FAILED_IN_BORROWING)
        }

        bookRepository.delete(book)
    }

    fun modifyBook(
        bookId: Long,
        title: String,
        author: String,
        totalQuantity: Long,
        bookDepartment: BookDepartment.BookDepartmentType,
        thumbnail: MultipartFile?
    ) = bookRepository.getBookById(bookId)
        .updateBook(
            title,
            author,
            BookDepartment.getBookDepartmentBy(bookDepartment),
            totalQuantity,
            thumbnailUtil.saveThumbnail(thumbnail)
                .orElse(null)
        )
}
