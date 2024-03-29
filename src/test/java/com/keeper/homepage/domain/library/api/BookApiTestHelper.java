package com.keeper.homepage.domain.library.api;

import static com.keeper.homepage.global.config.security.data.JwtType.ACCESS_TOKEN;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import com.keeper.homepage.IntegrationTest;
import jakarta.servlet.http.Cookie;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.MultiValueMap;

public class BookApiTestHelper extends IntegrationTest {

  ResultActions callGetBooksApi(String accessToken, MultiValueMap<String, String> params)
      throws Exception {
    return mockMvc.perform(get("/books")
        .params(params)
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), accessToken)));
  }

  FieldDescriptor[] getBooksResponse() {
    return new FieldDescriptor[]{
        fieldWithPath("bookId").description("책 ID"),
        fieldWithPath("thumbnailPath").description("책 썸네일 주소"),
        fieldWithPath("title").description("책 이름"),
        fieldWithPath("author").description("책 저자"),
        fieldWithPath("currentQuantity").description("책 현재 수량"),
        fieldWithPath("totalQuantity").description("책 전체 수량"),
        fieldWithPath("canBorrow").description("책 대여 가능 여부")
    };
  }

  ResultActions callRequestBorrowBookApi(String accessToken, long bookId)
      throws Exception {
    return mockMvc.perform(post("/books/{bookId}/request-borrow", bookId)
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), accessToken)));
  }

  ResultActions callCancelBorrowBookApi(String accessToken, long borrowId) throws Exception {
    return mockMvc.perform(delete("/books/borrows/{borrowId}/cancel-borrow", borrowId)
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), accessToken)));
  }

  ResultActions callGetBorrowBooksApi(String accessToken, MultiValueMap<String, String> params)
      throws Exception {
    return mockMvc.perform(get("/books/book-borrows")
        .params(params)
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), accessToken)));
  }

  ResultActions callRequestReturnBookApi(String accessToken, long borrowId)
      throws Exception {
    return mockMvc.perform(patch("/books/borrows/{borrowId}/request-return", borrowId)
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), accessToken)));
  }

  ResultActions callRequestCancelReturnBookApi(String accessToken, long borrowId)
      throws Exception {
    return mockMvc.perform(patch("/books/borrows/{borrowId}/cancel-return", borrowId)
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), accessToken)));
  }

  FieldDescriptor[] getBorrowBooksResponse() {
    return new FieldDescriptor[]{
        fieldWithPath("borrowInfoId").description("빌린 정보 ID"),
        fieldWithPath("bookTitle").description("빌린 책 이름"),
        fieldWithPath("thumbnailPath").description("빌린 책 썸네일 주소"),
        fieldWithPath("author").description("빌린 책 저자"),
        fieldWithPath("overdue").description("연체 여부").optional(),
        fieldWithPath("status").description("대여 상태"),
        fieldWithPath("borrowDateTime").description("빌린 날짜").optional(),
        fieldWithPath("expireDateTime").description("반납 날짜").optional()
    };
  }
}
