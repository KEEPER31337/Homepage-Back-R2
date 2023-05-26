package com.keeper.homepage.domain.library.api;

import com.keeper.homepage.domain.library.application.BookService;
import com.keeper.homepage.domain.library.dto.resp.BookResponse;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
      @RequestParam(defaultValue = "0") @PositiveOrZero int page,
      @RequestParam(defaultValue = "10") @PositiveOrZero int size
  ) {
    Page<BookResponse> listResponse = bookService.getBooks(searchType, search,
        PageRequest.of(page, size));
    return ResponseEntity.ok(listResponse);
  }
}
