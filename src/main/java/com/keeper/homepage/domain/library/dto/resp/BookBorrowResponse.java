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
  private String title;
  private String author;
  private boolean overdue;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime borrowDate;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime expireDate;

  public static BookBorrowResponse from(BookBorrowInfo bookBorrowInfo) {
    LocalDateTime now = LocalDateTime.now();
    return BookBorrowResponse.builder()
        .borrowInfoId(bookBorrowInfo.getId())
        .title(bookBorrowInfo.getBook().getTitle())
        .author(bookBorrowInfo.getBook().getAuthor())
        .overdue(bookBorrowInfo.getExpireDate().isBefore(now))
        .borrowDate(bookBorrowInfo.getBorrowDate())
        .expireDate(bookBorrowInfo.getExpireDate())
        .build();
  }
}
