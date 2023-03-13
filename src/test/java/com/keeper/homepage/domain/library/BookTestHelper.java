package com.keeper.homepage.domain.library;

import static com.keeper.homepage.domain.library.entity.BookDepartment.BookDepartmentType.ETC;

import com.keeper.homepage.domain.library.dao.BookRepository;
import com.keeper.homepage.domain.library.entity.Book;
import com.keeper.homepage.domain.library.entity.BookDepartment;
import com.keeper.homepage.domain.thumbnail.entity.Thumbnail;
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
    private BookDepartment bookDepartment;
    private Long totalQuantity;
    private Long currentQuantity;
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

    public BookBuilder bookDepartment(BookDepartment bookDepartment) {
      this.bookDepartment = bookDepartment;
      return this;
    }

    public BookBuilder totalQuantity(Long totalQuantity) {
      this.totalQuantity = totalQuantity;
      return this;
    }

    public BookBuilder currentQuantity(Long currentQuantity) {
      this.currentQuantity = currentQuantity;
      return this;
    }

    public BookBuilder thumbnail(Thumbnail thumbnail) {
      this.thumbnail = thumbnail;
      return this;
    }

    public Book build() {
      return bookRepository.save(Book.builder()
          .title(title != null ? title : "도서 제목")
          .author(author != null ? author : "도서 저자")
          .bookDepartment(bookDepartment != null ? bookDepartment : BookDepartment.getBookDepartmentBy(ETC))
          .totalQuantity(totalQuantity != null ? totalQuantity : 1)
          .currentQuantity(currentQuantity != null ? currentQuantity : 1)
          .thumbnail(thumbnail)
          .build());
    }
  }
}
