package com.keeper.homepage.domain.library.dto.resp;

import static lombok.AccessLevel.PRIVATE;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.keeper.homepage.domain.library.entity.BookBorrowInfo;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = PRIVATE)
public class BookBorrowResponse {

  private Long borrowInfoId;
  private String bookTitle;
  private String author;
  private boolean overdue;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime borrowDateTime;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime expireDateTime;

  public static BookBorrowResponse from(BookBorrowInfo bookBorrowInfo) {
    LocalDateTime now = LocalDateTime.now();
    return BookBorrowResponse.builder()
        .borrowInfoId(bookBorrowInfo.getId())
        .bookTitle(bookBorrowInfo.getBook().getTitle())
        .author(bookBorrowInfo.getBook().getAuthor())
        .overdue(bookBorrowInfo.getExpireDate().isBefore(now))
        .borrowDateTime(bookBorrowInfo.getBorrowDate())
        .expireDateTime(bookBorrowInfo.getExpireDate())
        .build();
  }
}
