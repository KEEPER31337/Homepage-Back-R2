package com.keeper.homepage.domain.library.application;

import static com.keeper.homepage.global.error.ErrorCode.BORROW_NOT_FOUND;

import com.keeper.homepage.domain.library.dao.BookBorrowInfoRepository;
import com.keeper.homepage.domain.library.entity.BookBorrowInfo;
import com.keeper.homepage.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookBorrowInfoFindService {

  private final BookBorrowInfoRepository bookBorrowInfoRepository;

  public BookBorrowInfo findById(long borrowId) {
    return bookBorrowInfoRepository.findById(borrowId)
        .orElseThrow(() -> new BusinessException(borrowId, "borrowId", BORROW_NOT_FOUND));
  }

}
