package com.keeper.homepage.domain.library.application;

import static com.keeper.homepage.domain.library.entity.BookBorrowStatus.BookBorrowStatusType.대출대기중;
import static com.keeper.homepage.domain.library.entity.BookBorrowStatus.BookBorrowStatusType.대출승인;
import static com.keeper.homepage.domain.library.entity.BookBorrowStatus.BookBorrowStatusType.반납대기중;
import static com.keeper.homepage.domain.library.entity.BookBorrowStatus.getBookBorrowStatusBy;
import static com.keeper.homepage.global.error.ErrorCode.BOOK_BORROWING_COUNT_OVER;
import static com.keeper.homepage.global.error.ErrorCode.BOOK_CURRENT_QUANTITY_IS_ZERO;
import static com.keeper.homepage.global.error.ErrorCode.BOOK_NOT_FOUND;
import static com.keeper.homepage.global.error.ErrorCode.BOOK_SEARCH_TYPE_NOT_FOUND;
import static com.keeper.homepage.global.error.ErrorCode.BORROW_NOT_FOUND;
import static com.keeper.homepage.global.error.ErrorCode.BORROW_REQUEST_ALREADY;
import static com.keeper.homepage.global.error.ErrorCode.BORROW_REQUEST_RETURN_DENY;
import static com.keeper.homepage.global.error.ErrorCode.BORROW_STATUS_IS_NOT_BORROW_APPROVAL;

import com.keeper.homepage.domain.library.dao.BookBorrowInfoRepository;
import com.keeper.homepage.domain.library.dao.BookRepository;
import com.keeper.homepage.domain.library.dto.resp.BookBorrowResponse;
import com.keeper.homepage.domain.library.dto.resp.BookResponse;
import com.keeper.homepage.domain.library.entity.Book;
import com.keeper.homepage.domain.library.entity.BookBorrowInfo;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.global.error.BusinessException;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

  private final BookRepository bookRepository;
  private final BookBorrowInfoRepository bookBorrowInfoRepository;
  public static final long MAX_BORROWING_COUNT = 5;

  public Page<BookResponse> getBooks(Member member, String searchType, String search,
      PageRequest pageable) {
    boolean isUnderBorrowingLimit = member.getCountInBorrowing() < MAX_BORROWING_COUNT;
    if (searchType == null) {
      return bookRepository.findAll(pageable)
          .map(book -> BookResponse.of(book, canBorrow(isUnderBorrowingLimit, book)));
    }
    if (searchType.equals("title")) {
      return bookRepository.findAllByTitleIgnoreCaseContaining(search, pageable)
          .map(book -> BookResponse.of(book, canBorrow(isUnderBorrowingLimit, book)));
    }
    if (searchType.equals("author")) {
      return bookRepository.findAllByAuthorIgnoreCaseContaining(search, pageable)
          .map(book -> BookResponse.of(book, canBorrow(isUnderBorrowingLimit, book)));
    }
    if (searchType.equals("all")) {
      return bookRepository.findAllByTitleOrAuthor(search, pageable)
          .map(book -> BookResponse.of(book, canBorrow(isUnderBorrowingLimit, book)));
    }
    throw new BusinessException(searchType, "searchType", BOOK_SEARCH_TYPE_NOT_FOUND);
  }

  private boolean canBorrow(boolean isUnderBorrowingLimit, Book book) {
    return book.getCurrentQuantity() != 0L && isUnderBorrowingLimit;
  }

  @Transactional
  public void requestBorrow(Member member, long bookId) {
    checkCountInBorrowing(member);

    Book book = bookRepository.findById(bookId)
        .orElseThrow(() -> new BusinessException(bookId, "bookId", BOOK_NOT_FOUND));
    checkCurrentQuantity(book);
    checkBorrowRequestAlready(member, book);
    member.borrow(book, getBookBorrowStatusBy(대출대기중));
  }

  private void checkCountInBorrowing(Member member) {
    long countInBorrowing = member.getCountInBorrowing();
    if (countInBorrowing >= MAX_BORROWING_COUNT) {
      throw new BusinessException(countInBorrowing, "countInBorrowing", BOOK_BORROWING_COUNT_OVER);
    }
  }

  private void checkCurrentQuantity(Book book) {
    Long currentQuantity = book.getCurrentQuantity();
    if (currentQuantity == 0L) {
      throw new BusinessException(currentQuantity, "currentQuantity",
          BOOK_CURRENT_QUANTITY_IS_ZERO);
    }
  }

  private void checkBorrowRequestAlready(Member member, Book book) {
    Optional<BookBorrowInfo> bookBorrowInfo = bookBorrowInfoRepository
        .findByMemberAndBookAndBorrowStatus(member, book, getBookBorrowStatusBy(대출대기중));
    if (bookBorrowInfo.isPresent()) {
      throw new BusinessException(bookBorrowInfo.get().getId(), "bookBorrowInfoId",
          BORROW_REQUEST_ALREADY);
    }
  }

  @Transactional
  public void requestReturn(Member member, long borrowId) {
    BookBorrowInfo bookBorrowInfo = bookBorrowInfoRepository
        .findById(borrowId)
        .orElseThrow(() -> new BusinessException(borrowId, "borrowId", BORROW_NOT_FOUND));
    if (!bookBorrowInfo.getBorrowStatus().equals(대출승인)) {
      throw new BusinessException(borrowId, "borrowId", BORROW_STATUS_IS_NOT_BORROW_APPROVAL);
    }
    if (!bookBorrowInfo.isMine(member)) {
      throw new BusinessException(borrowId, "borrowId", BORROW_REQUEST_RETURN_DENY);
    }
    bookBorrowInfo.changeLastRequestDate(LocalDateTime.now());
    bookBorrowInfo.changeBorrowStatus(반납대기중);
  }

  public Page<BookBorrowResponse> getBookBorrows(Member member, PageRequest pageable) {
    return bookBorrowInfoRepository.findAllByMemberAndInBorrowing(member, pageable)
        .map(BookBorrowResponse::from);
  }
}
