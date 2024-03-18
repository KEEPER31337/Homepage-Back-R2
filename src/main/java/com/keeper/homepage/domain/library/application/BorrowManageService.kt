package com.keeper.homepage.domain.library.application

import com.keeper.homepage.domain.library.dao.BookBorrowInfoRepository
import com.keeper.homepage.domain.library.dao.BookBorrowLogRepository
import com.keeper.homepage.domain.library.dto.req.BorrowStatusDto
import com.keeper.homepage.domain.library.dto.resp.BorrowDetailResponse
import com.keeper.homepage.domain.library.dto.resp.BorrowLogResponse
import com.keeper.homepage.domain.library.entity.BookBorrowLog
import com.keeper.homepage.domain.library.entity.BookBorrowLog.LogType
import com.keeper.homepage.domain.library.entity.BookBorrowStatus.BookBorrowStatusType.*
import com.keeper.homepage.global.error.BusinessException
import com.keeper.homepage.global.error.ErrorCode
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

fun BookBorrowInfoRepository.getBorrowById(borrowId: Long) = this.findById(borrowId)
        .orElseThrow { throw BusinessException(borrowId, "borrowId", ErrorCode.BORROW_NOT_FOUND) }

@Service
@Transactional(readOnly = true)
class BorrowManageService(
        val borrowInfoRepository: BookBorrowInfoRepository,
        val borrowLogRepository: BookBorrowLogRepository,
) {
    fun getBorrow(search: String, pageable: Pageable, borrowStatusDto: BorrowStatusDto?): Page<BorrowDetailResponse> {
        if (borrowStatusDto == null) {
            return borrowInfoRepository.findAll(pageable)
                    .map(::BorrowDetailResponse)
        }
        return borrowStatusDto.getBorrowInfo(search, borrowInfoRepository, pageable)
                .map(::BorrowDetailResponse)
    }

    @Transactional
    fun approveBorrow(borrowId: Long) {
        val borrowInfo = borrowInfoRepository.getBorrowById(borrowId)
        if (borrowInfo.borrowStatus.type != 대출대기) {
            throw BusinessException(borrowId, "borrowId", ErrorCode.BORROW_STATUS_IS_NOT_REQUESTS)
        }
        val book = borrowInfo.book
        if (book.currentQuantity <= 0) {
            throw BusinessException(borrowId, "borrowId", ErrorCode.BOOK_BORROWING_COUNT_OVER)
        }

        borrowInfo.setBorrowTime(LocalDateTime.now())
        borrowInfo.changeBorrowStatus(대출중)
        borrowLogRepository.save(BookBorrowLog.of(borrowInfo, LogType.대출중))
    }

    @Transactional
    fun denyBorrow(borrowId: Long) {
        val borrowInfo = borrowInfoRepository.getBorrowById(borrowId)
        if (borrowInfo.borrowStatus.type != 대출대기) {
            throw BusinessException(borrowId, "borrowId", ErrorCode.BORROW_STATUS_IS_NOT_REQUESTS)
        }

        borrowInfoRepository.delete(borrowInfo)
        borrowLogRepository.save(BookBorrowLog.of(borrowInfo, LogType.대출반려))
    }

    @Transactional
    fun approveReturn(borrowId: Long) {
        val borrowInfo = borrowInfoRepository.getBorrowById(borrowId)
        if (borrowInfo.borrowStatus.type != 반납대기) {
            throw BusinessException(borrowId, "borrowId", ErrorCode.BORROW_STATUS_IS_NOT_WAITING_RETURN)
        }
        borrowInfo.changeBorrowStatus(반납완료)
        borrowLogRepository.save(BookBorrowLog.of(borrowInfo, LogType.반납완료))
    }

    @Transactional
    fun denyReturn(borrowId: Long) {
        val borrowInfo = borrowInfoRepository.getBorrowById(borrowId)
        if (borrowInfo.borrowStatus.type != 반납대기) {
            throw BusinessException(borrowId, "borrowId", ErrorCode.BORROW_STATUS_IS_NOT_WAITING_RETURN)
        }
        borrowInfo.changeBorrowStatus(대출중)
    }

    fun getBorrowLogs(
            search: String,
            pageable: PageRequest,
            searchType: LogType?
    ): Page<BorrowLogResponse> {
        return when (searchType) {
            null, LogType.전체 -> borrowLogRepository.findAll(search, pageable)
            LogType.대출중,
            LogType.반납대기,
            LogType.반납완료,
            LogType.대출반려 -> borrowLogRepository.findAllByStatus(
                    searchType.name,
                    search,
                    pageable
            )
        }.map(::BorrowLogResponse)
    }
}
