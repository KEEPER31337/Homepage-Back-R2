package com.keeper.homepage.domain.library.application;

import static com.keeper.homepage.domain.library.entity.BookBorrowStatus.BookBorrowStatusType.대출대기중;
import static com.keeper.homepage.domain.library.entity.BookBorrowStatus.getBookBorrowStatusBy;
import static com.keeper.homepage.global.error.ErrorCode.BOOK_BORROWING_COUNT_OVER;
import static com.keeper.homepage.global.error.ErrorCode.BOOK_NOT_FOUND;
import static com.keeper.homepage.global.error.ErrorCode.BOOK_SEARCH_TYPE_NOT_FOUND;

import com.keeper.homepage.domain.library.dao.BookRepository;
import com.keeper.homepage.domain.library.dto.resp.BookResponse;
import com.keeper.homepage.domain.library.entity.Book;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.global.error.BusinessException;
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
  public static final long MAX_BORROWING_COUNT = 5;

  public Page<BookResponse> getBooks(String searchType, String search, PageRequest pageable) {
    if (searchType == null) {
      return bookRepository.findAll(pageable).map(BookResponse::from);
    }
    if (searchType.equals("title")) {
      return bookRepository.findAllByTitleIgnoreCaseContaining(search, pageable).map(BookResponse::from);
    }
    if(searchType.equals("author")) {
      return bookRepository.findAllByAuthorIgnoreCaseContaining(search, pageable).map(BookResponse::from);
    }
    if(searchType.equals("all")) {
      return bookRepository.findAllByTitleOrAuthor(search, pageable).map(BookResponse::from);
    }
    throw new BusinessException(searchType, "searchType", BOOK_SEARCH_TYPE_NOT_FOUND);
  }

  public void requestBorrow(Member member, long bookId) {
    checkBorrowBookCount(member);

    Book book = bookRepository.findById(bookId)
        .orElseThrow(()->new BusinessException(bookId, "bookId", BOOK_NOT_FOUND));
    member.borrow(book, getBookBorrowStatusBy(대출대기중));
  }

  private void checkBorrowBookCount(Member member){
    long countInBorrowing = member.getCountInBorrowing();
    if(countInBorrowing < MAX_BORROWING_COUNT){
      return;
    }
    throw new BusinessException(countInBorrowing, "countInBorrowing", BOOK_BORROWING_COUNT_OVER);
  }
}
