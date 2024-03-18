package com.keeper.homepage.domain.library.dto.req

import com.keeper.homepage.domain.library.dao.BookBorrowInfoRepository
import com.keeper.homepage.domain.library.entity.BookBorrowInfo
import com.keeper.homepage.domain.library.entity.BookBorrowStatus.BookBorrowStatusType.대출대기
import com.keeper.homepage.domain.library.entity.BookBorrowStatus.BookBorrowStatusType.반납대기
import com.keeper.homepage.domain.library.entity.BookBorrowStatus.getBookBorrowStatusBy
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDateTime

enum class BorrowStatusDto(val status: String) {
    REQUESTS("requests") {
        override fun getBorrowInfo(
                search: String,
                repository: BookBorrowInfoRepository,
                pageable: Pageable
        ) = repository.findAllByBorrowStatus(getBookBorrowStatusBy(대출대기).id, search, pageable)!!
    },
    WILL_RETURN("willreturn") {
        override fun getBorrowInfo(
                search: String,
                repository: BookBorrowInfoRepository,
                pageable: Pageable
        ) = repository.findAllByBorrowStatus(getBookBorrowStatusBy(반납대기).id, search, pageable)!!
    },
    REQUESTS_OR_WILL_RETURN("requests_or_willreturn") {
        override fun getBorrowInfo(
                search: String,
                repository: BookBorrowInfoRepository,
                pageable: Pageable
        ) = repository.findAllByTwoBorrowStatus(
                getBookBorrowStatusBy(대출대기).id,
                getBookBorrowStatusBy(반납대기).id,
                search,
                pageable
        )!!
    },
    OVERDUE("overdue") {
        override fun getBorrowInfo(
                search: String,
                repository: BookBorrowInfoRepository,
                pageable: Pageable
        ) = repository.findAllOverDue(LocalDateTime.now(), pageable)!!
    };

    fun isMatch(status: String) = this.status == status

    abstract fun getBorrowInfo(
            search: String,
            repository: BookBorrowInfoRepository,
            pageable: Pageable
    ): Page<BookBorrowInfo>
}
