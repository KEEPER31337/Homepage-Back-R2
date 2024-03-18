package com.keeper.homepage.domain.library.application

import com.keeper.homepage.IntegrationTest
import com.keeper.homepage.domain.library.BookBorrowInfoTestHelper
import com.keeper.homepage.domain.library.entity.Book
import com.keeper.homepage.domain.library.entity.BookBorrowInfo
import com.keeper.homepage.domain.library.entity.BookBorrowStatus.BookBorrowStatusType
import com.keeper.homepage.domain.library.entity.BookBorrowStatus.getBookBorrowStatusBy
import com.keeper.homepage.domain.library.entity.BookDepartment
import com.keeper.homepage.domain.member.entity.Member
import com.keeper.homepage.global.error.BusinessException
import com.keeper.homepage.global.error.ErrorCode
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

fun BookBorrowInfoTestHelper.generate(
        member: Member,
        book: Book,
        bookBorrowStatusType: BookBorrowStatusType
): BookBorrowInfo {
    val now = LocalDateTime.now()
    return this.builder()
            .book(book)
            .member(member)
            .borrowDate(now)
            .expireDate(now.plusWeeks(2))
            .borrowStatus(getBookBorrowStatusBy(bookBorrowStatusType))
            .build()
}

class BookManageServiceTest : IntegrationTest() {

    @Nested
    inner class `책 삭제` {

        lateinit var book: Book
        lateinit var member: Member

        @BeforeEach
        fun addBook() {
            book = bookTestHelper.generate()
            member = memberTestHelper.generate()
        }

        @Test
        fun `존재하는 책이면 책은 삭제되어야 한다`() {
            shouldNotThrowAny { bookManageService.deleteBook(book.id) }
        }

        @Test
        fun `존재하지 않는 책이면 책 삭제는 실패해야 한다`() {
            val exception = shouldThrow<BusinessException> { bookManageService.deleteBook(-1) }
            exception.httpStatus shouldBe ErrorCode.BOOK_NOT_FOUND.httpStatus
            exception.message shouldBe ErrorCode.BOOK_NOT_FOUND.message
        }

        @Test
        fun `책을 빌린 사람이 있으면 책 삭제는 실패해야 한다`() {
            bookBorrowInfoTestHelper.generate(member, book, BookBorrowStatusType.대출중)
            em.flush()
            em.clear()

            val exception = shouldThrow<BusinessException> { bookManageService.deleteBook(book.id) }

            exception.httpStatus shouldBe ErrorCode.BOOK_DELETE_FAILED_IN_BORROWING.httpStatus
            exception.message shouldBe ErrorCode.BOOK_DELETE_FAILED_IN_BORROWING.message
        }

        @Test
        fun `반납 대기중인 사람이 있으면 책 삭제는 실패해야 한다`() {
            bookBorrowInfoTestHelper.generate(member, book, BookBorrowStatusType.반납대기)
            em.flush()
            em.clear()

            val exception = shouldThrow<BusinessException> { bookManageService.deleteBook(book.id) }

            exception.httpStatus shouldBe ErrorCode.BOOK_DELETE_FAILED_IN_BORROWING.httpStatus
            exception.message shouldBe ErrorCode.BOOK_DELETE_FAILED_IN_BORROWING.message
        }

        @Test
        fun `대출중인 사람이 없으면 책 삭제는 성공해야 하고 대출 이력도 지워져야 한다`() {
            bookBorrowInfoTestHelper.generate(member, book, BookBorrowStatusType.대출대기)
            bookBorrowInfoTestHelper.generate(member, book, BookBorrowStatusType.대출반려)
            em.flush()
            em.clear()

            shouldNotThrowAny { bookManageService.deleteBook(book.id) }

            bookBorrowInfoRepository.findAll() shouldHaveSize 0
        }
    }

    @Nested
    inner class `책 수정` {

        lateinit var book: Book
        lateinit var member: Member

        @BeforeEach
        fun addBook() {
            book = bookTestHelper.generate()
            member = memberTestHelper.generate()
        }

        @Test
        fun `현재 대여중인 권 수 이하의 책 수량 수정은 실패해야 한다`() {
            val bookCount = 2L
            val book = bookTestHelper.builder()
                    .totalQuantity(bookCount)
                    .currentQuantity(bookCount)
                    .thumbnail(thumbnailTestHelper.generateThumbnail())
                    .build()
            val member = memberTestHelper.generate()

            (1..bookCount).map { // 모든 책 대여
                bookBorrowInfoTestHelper.builder()
                        .book(book)
                        .member(member)
                        .borrowStatus(getBookBorrowStatusBy(BookBorrowStatusType.대출중))
                        .build()
            }

            val exception = shouldThrow<BusinessException> {
                bookManageService.modifyBook(
                        book.id,
                        book.title + "_",
                        book.author + "_",
                        bookCount - 1,
                        BookDepartment.BookDepartmentType.ETC,
                )
            }

            exception.httpStatus shouldBe ErrorCode.BOOK_CANNOT_UPDATE_EXCEED_CURRENT_QUANTITY.httpStatus
            exception.message shouldBe ErrorCode.BOOK_CANNOT_UPDATE_EXCEED_CURRENT_QUANTITY.message
        }
    }
}
