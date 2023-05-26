package com.keeper.homepage.domain.library.api;

import com.keeper.homepage.domain.library.application.BookService;
import com.keeper.homepage.domain.library.dto.resp.BookResponse;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.global.config.security.annotation.LoginMember;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

  private final BookService bookService;

  @GetMapping
  public ResponseEntity<Page<BookResponse>> getBooks(
      @RequestParam(required = false) String searchType,
      @RequestParam(required = false) String search,
      @RequestParam(defaultValue = "0") @PositiveOrZero @NotNull int page,
      @RequestParam(defaultValue = "10") @PositiveOrZero @NotNull int size
  ) {
    Page<BookResponse> listResponse = bookService.getBooks(searchType, search,
        PageRequest.of(page, size));
    return ResponseEntity.ok(listResponse);
  }

  @PostMapping("/{bookId}/request-borrow")
  public ResponseEntity<Void> requestBorrow(
      @LoginMember Member member,
      @PathVariable long bookId
  ){
    bookService.requestBorrow(member, bookId);
    return ResponseEntity.status(HttpStatus.CREATED)
        .build();
  }
}
