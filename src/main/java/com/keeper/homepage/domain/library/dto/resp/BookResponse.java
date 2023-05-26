package com.keeper.homepage.domain.library.dto.resp;

import com.keeper.homepage.domain.library.entity.Book;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BookResponse {

  private Long bookId;
  private String thumbnailPath;
  private String title;
  private String author;
  private Long currentQuantity;
  private Long totalQuantity;

  public static BookResponse from(Book book) {
    return BookResponse.builder()
        .bookId(book.getId())
        .thumbnailPath(book.getThumbnailPath())
        .title(book.getTitle())
        .author(book.getAuthor())
        .currentQuantity(book.getCurrentQuantity())
        .totalQuantity(book.getTotalQuantity())
        .build();
  }
}
