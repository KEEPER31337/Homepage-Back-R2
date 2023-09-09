package com.keeper.homepage.domain.library.application;

import static com.keeper.homepage.domain.library.entity.BookBorrowStatus.BookBorrowStatusType.대출대기;
import static com.keeper.homepage.domain.library.entity.BookBorrowStatus.BookBorrowStatusType.대출중;
import static com.keeper.homepage.domain.library.entity.BookBorrowStatus.BookBorrowStatusType.반납대기;
import static com.keeper.homepage.domain.library.entity.BookBorrowStatus.getBookBorrowStatusBy;
import static com.keeper.homepage.global.error.ErrorCode.BOOK_BORROWING_COUNT_OVER;
import static com.keeper.homepage.global.error.ErrorCode.BOOK_CURRENT_QUANTITY_IS_ZERO;
import static com.keeper.homepage.global.error.ErrorCode.BOOK_NOT_FOUND;
import static com.keeper.homepage.global.error.ErrorCode.BOOK_SEARCH_TYPE_NOT_FOUND;
import static com.keeper.homepage.global.error.ErrorCode.BORROW_CANCEL_REQUEST_DENY;
import static com.keeper.homepage.global.error.ErrorCode.BORROW_REQUEST_ALREADY;
import static com.keeper.homepage.global.error.ErrorCode.BORROW_REQUEST_RETURN_DENY;
import static com.keeper.homepage.global.error.ErrorCode.BORROW_STATUS_IS_NOT_BORROW_APPROVAL;
import static com.keeper.homepage.global.error.ErrorCode.BORROW_STATUS_IS_NOT_BORROW_WAIT;
import static com.keeper.homepage.global.error.ErrorCode.BORROW_STATUS_IS_NOT_RETURN_WAIT;

import com.keeper.homepage.domain.library.dao.BookBorrowInfoRepository;
import com.keeper.homepage.domain.library.dao.BookBorrowLogRepository;
import com.keeper.homepage.domain.library.dao.BookRepository;
import com.keeper.homepage.domain.library.dto.req.BookSearchType;
import com.keeper.homepage.domain.library.dto.resp.BookBorrowResponse;
import com.keeper.homepage.domain.library.dto.resp.BookResponse;
import com.keeper.homepage.domain.library.entity.Book;
import com.keeper.homepage.domain.library.entity.BookBorrowInfo;
import com.keeper.homepage.domain.library.entity.BookBorrowLog;
import com.keeper.homepage.domain.library.entity.BookBorrowLog.LogType;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.global.error.BusinessException;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
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
  private final BookBorrowLogRepository borrowLogRepository;

  private final BookBorrowInfoFindService bookBorrowInfoFindService;

  public static final long MAX_BORROWING_COUNT = 5;

  public Page<BookResponse> getBooks(Member member, @NotNull BookSearchType searchType, String search,
      PageRequest pageable) {
    boolean isUnderLimit = member.getCountWaitOrInBorrowing() < MAX_BORROWING_COUNT;

    return switch (searchType) {
      case ALL -> bookRepository.findAllByTitleOrAuthor(search, pageable)
          .map(book -> BookResponse.of(book, canBorrow(member, book) && isUnderLimit));
      case TITLE -> bookRepository.findAllByTitleIgnoreCaseContaining(search, pageable)
          .map(book -> BookResponse.of(book, canBorrow(member, book) && isUnderLimit));
      case AUTHOR -> bookRepository.findAllByAuthorIgnoreCaseContaining(search, pageable)
          .map(book -> BookResponse.of(book, canBorrow(member, book) && isUnderLimit));
      default -> throw new BusinessException(searchType, "searchType", BOOK_SEARCH_TYPE_NOT_FOUND);
    };
  }

  private boolean canBorrow(Member member, Book book) {
    Optional<BookBorrowInfo> borrowInfo = bookBorrowInfoRepository.findByMemberAndBook(member, book);
    if (borrowInfo.isEmpty()) {
      return book.getCurrentQuantity() > 0L;
    }
    return !borrowInfo.get().isWaitOrInBorrowing() && book.getCurrentQuantity() > 0L;
  }

  @Transactional
  public void requestBorrow(Member member, long bookId) {
    checkCountInBorrowing(member);

    Book book = bookRepository.findById(bookId)
        .orElseThrow(() -> new BusinessException(bookId, "bookId", BOOK_NOT_FOUND));
    checkCurrentQuantity(book);
    checkBorrowRequestAlready(member, book);
    member.borrow(book, getBookBorrowStatusBy(대출대기));
  }

  private void checkCountInBorrowing(Member member) {
    long countWaitOrInBorrowing = member.getCountWaitOrInBorrowing();
    if (countWaitOrInBorrowing >= MAX_BORROWING_COUNT) {
      throw new BusinessException(countWaitOrInBorrowing, "countWaitOrInBorrowing", BOOK_BORROWING_COUNT_OVER);
    }
  }

  private void checkCurrentQuantity(Book book) {
    Long currentQuantity = book.getCurrentQuantity();
    if (currentQuantity == 0L) {
      throw new BusinessException(currentQuantity, "currentQuantity", BOOK_CURRENT_QUANTITY_IS_ZERO);
    }
  }

  private void checkBorrowRequestAlready(Member member, Book book) {
    Optional<BookBorrowInfo> borrowInfo = bookBorrowInfoRepository.findByMemberAndBook(member, book);
    if (borrowInfo.isPresent() && borrowInfo.get().isWaitOrInBorrowing()) {
      throw new BusinessException(borrowInfo.get().getId(), "bookBorrowInfoId", BORROW_REQUEST_ALREADY);
    }
  }

  @Transactional
  public void cancelBorrow(Member member, long borrowId) {
    BookBorrowInfo bookBorrowInfo = bookBorrowInfoFindService.findById(borrowId);

    if (!bookBorrowInfo.isStatus(대출대기)) {
      throw new BusinessException(borrowId, "borrowId", BORROW_STATUS_IS_NOT_BORROW_WAIT);
    }
    if (!bookBorrowInfo.isMine(member)) {
      throw new BusinessException(borrowId, "borrowId", BORROW_CANCEL_REQUEST_DENY);
    }
    bookBorrowInfoRepository.delete(bookBorrowInfo);
  }

  @Transactional
  public void requestReturn(Member member, long borrowId) {
    BookBorrowInfo bookBorrowInfo = bookBorrowInfoFindService.findById(borrowId);

    if (!bookBorrowInfo.isStatus(대출중)) {
      throw new BusinessException(borrowId, "borrowId", BORROW_STATUS_IS_NOT_BORROW_APPROVAL);
    }
    if (!bookBorrowInfo.isMine(member)) {
      throw new BusinessException(borrowId, "borrowId", BORROW_REQUEST_RETURN_DENY);
    }
    bookBorrowInfo.changeLastRequestDate(LocalDateTime.now());
    bookBorrowInfo.changeBorrowStatus(반납대기);
    borrowLogRepository.save(BookBorrowLog.of(bookBorrowInfo, LogType.반납대기));
  }

  public Page<BookBorrowResponse> getBookBorrows(Member member, PageRequest pageable) {
    return bookBorrowInfoRepository.findAllByMemberAndInBorrowingOrWait(member, pageable)
        .map(BookBorrowResponse::from);
  }

  @Transactional
  public void cancelReturn(Member member, long borrowId) {
    BookBorrowInfo bookBorrowInfo = bookBorrowInfoFindService.findById(borrowId);

    if (!bookBorrowInfo.isStatus(반납대기)) {
      throw new BusinessException(borrowId, "borrowId", BORROW_STATUS_IS_NOT_RETURN_WAIT);
    }
    if (!bookBorrowInfo.isMine(member)) {
      throw new BusinessException(borrowId, "borrowId", BORROW_REQUEST_RETURN_DENY);
    }
    bookBorrowInfo.changeLastRequestDate(LocalDateTime.now());
    bookBorrowInfo.changeBorrowStatus(대출중);
  }
}
