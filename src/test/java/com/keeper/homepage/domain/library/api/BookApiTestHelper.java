package com.keeper.homepage.domain.library.api;

import static com.keeper.homepage.global.config.security.data.JwtType.ACCESS_TOKEN;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;

import com.keeper.homepage.IntegrationTest;
import jakarta.servlet.http.Cookie;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.MultiValueMap;

public class BookApiTestHelper extends IntegrationTest {

  ResultActions callGetBooksApi(String accessToken, MultiValueMap<String, String> params)
      throws Exception {
    return mockMvc.perform(
        get("/books").params(params).cookie(new Cookie(ACCESS_TOKEN.getTokenName(), accessToken)));
  }

  ResultActions callRequestBorrowBookApi(String accessToken, long bookId)
      throws Exception {
    return mockMvc.perform(post("/books/{bookId}/request-borrow", bookId)
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), accessToken)));
  }
}
