package com.keeper.homepage.domain.library;

import static com.keeper.homepage.domain.library.entity.BookDepartment.BookDepartmentType.LANGUAGE;

import com.keeper.homepage.domain.library.dao.BookBorrowInfoRepository;
import com.keeper.homepage.domain.library.dao.BookDepartmentRepository;
import com.keeper.homepage.domain.library.dao.BookRepository;
import com.keeper.homepage.domain.library.entity.Book;
import com.keeper.homepage.domain.library.entity.BookBorrowInfo;
import com.keeper.homepage.domain.library.entity.BookDepartment;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.thumbnail.entity.Thumbnail;
import com.keeper.homepage.global.util.thumbnail.ThumbnailTestHelper;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BookTestHelper {

  @Autowired
  BookRepository bookRepository;

  public Book generate() {
    return this.builder().build();
  }

  public BookBuilder builder() {
    return new BookBuilder();
  }

  public final class BookBuilder {

    private String title;
    private String author;
    private String information;
    private BookDepartment bookDepartment;
    private Long total;
    private Long borrow;
    private Long enable;
    private Thumbnail thumbnail;

    private BookBuilder() {
    }

    public BookBuilder title(String title) {
      this.title = title;
      return this;
    }

    public BookBuilder author(String author) {
      this.author = author;
      return this;
    }

    public BookBuilder information(String information) {
      this.information = information;
      return this;
    }

    public BookBuilder bookDepartment(BookDepartment bookDepartment) {
      this.bookDepartment = bookDepartment;
      return this;
    }

    public BookBuilder total(Long total) {
      this.total = total;
      return this;
    }

    public BookBuilder borrow(Long borrow) {
      this.borrow = borrow;
      return this;
    }

    public BookBuilder enable(Long enable) {
      this.enable = enable;
      return this;
    }

    public Book build() {
      return bookRepository.save(Book.builder()
          .title(title != null ? title : "도서 제목")
          .author(author != null ? author : "도서 저자")
          .information(information)
          .bookDepartment(bookDepartment)
          .total(total)
          .borrow(borrow)
          .enable(enable)
          .thumbnail(thumbnail)
          .build());
    }
  }
}
