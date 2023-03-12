package com.keeper.homepage.domain.library.application

import com.keeper.homepage.domain.library.dao.BookBorrowInfoRepository
import com.keeper.homepage.domain.library.dto.resp.BorrowResponse
import com.keeper.homepage.domain.library.entity.BookBorrowStatus.BookBorrowStatusType.대출대기중
import com.keeper.homepage.domain.library.entity.BookBorrowStatus.getBookBorrowStatusBy
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class BorrowManageService(
    val borrowInfoRepository: BookBorrowInfoRepository
) {
    fun getBorrowRequests(pageable: Pageable): Page<BorrowResponse> =
        borrowInfoRepository.findAllByBorrowStatus(getBookBorrowStatusBy(대출대기중), pageable)
            .map(::BorrowResponse)
}
