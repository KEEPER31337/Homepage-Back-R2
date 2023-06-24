package com.keeper.homepage.domain.library.api;

import static com.keeper.homepage.global.config.security.data.JwtType.ACCESS_TOKEN;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
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
}
