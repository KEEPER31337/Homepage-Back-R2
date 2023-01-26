package com.keeper.homepage.domain.library;

import static com.keeper.homepage.domain.library.entity.BookDepartment.BookDepartmentType.LANGUAGE;

import com.keeper.homepage.domain.library.dao.BookBorrowInfoRepository;
import com.keeper.homepage.domain.library.dao.BookDepartmentRepository;
import com.keeper.homepage.domain.library.dao.BookRepository;
import com.keeper.homepage.domain.library.entity.Book;
import com.keeper.homepage.domain.library.entity.BookBorrowInfo;
import com.keeper.homepage.domain.library.entity.BookDepartment;
import com.keeper.homepage.domain.member.entity.Member;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BookTestHelper {

  @Autowired
  BookRepository bookRepository;

  @Autowired
  BookBorrowInfoRepository bookBorrowInfoRepository;

  @Autowired
  BookDepartmentRepository bookDepartmentRepository;

  public Book generateBook() {
    return bookRepository.save(Book.builder()
        .title("book")
        .author("keeper")
        .information("keeper book")
        .department(getLanguageBookDepartment())
        .total(1L)
        .borrow(1L)
        .enable(1L)
        .thumbnail(null)
        .build());
  }

  public BookDepartment getLanguageBookDepartment() {
    return bookDepartmentRepository.getReferenceById(LANGUAGE.getId());
  }

  public BookBorrowInfo generateBookBorrowInfo(Book book, Member member) {
    return bookBorrowInfoRepository.save(BookBorrowInfo.builder()
        .member(member)
        .book(book)
        .quantity(1L)
        .borrowDate(LocalDateTime.now())
        .expireDate(LocalDateTime.now().plusDays(3))
        .build());
  }
}
