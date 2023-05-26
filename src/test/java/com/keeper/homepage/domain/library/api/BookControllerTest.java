package com.keeper.homepage.domain.library.api;

import static com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_회원;
import static com.keeper.homepage.global.config.security.data.JwtType.ACCESS_TOKEN;
import static com.keeper.homepage.global.restdocs.RestDocsHelper.field;
import static com.keeper.homepage.global.restdocs.RestDocsHelper.getSecuredValue;
import static com.keeper.homepage.global.restdocs.RestDocsHelper.pageHelper;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class BookControllerTest extends BookApiTestHelper {

  private String memberToken;
  private final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

  @BeforeEach
  void setUp() {
    long memberId = memberTestHelper.generate().getId();
    memberToken = jwtTokenProvider.createAccessToken(ACCESS_TOKEN, memberId, ROLE_회원);
  }

  @Nested
  @DisplayName("도서 목록 조회")
  class GetBooks {

    @BeforeEach
    void setUp() {
      for (int i = 0; i < 5; i++) {
        bookTestHelper.generate();
      }
    }

    @Test
    @DisplayName("유효한 유형이면 도서 목록 조회는 성공해야한다.")
    public void 유효한_유형이면_도서_목록_조회는_성공해야한다() throws Exception {
      String securedValue = getSecuredValue(BookController.class, "getBooks");

      params.add("searchType", null);
      params.add("search", null);
      params.add("page", "0");
      params.add("size", "3");
      callGetBooksApi(memberToken, params)
          .andExpect(status().isOk())
          .andDo(document("get-books",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              ),
              queryParameters(
                  parameterWithName("searchType")
                      .description("검색 타입 (title: 제목, author: 저자, all: 제목 + 저자, null : 전체 도서 목록 조회)")
                      .optional(),
                  parameterWithName("search").description("검색할 단어")
                      .optional(),
                  parameterWithName("page").description("페이지 (default: 0)")
                      .optional(),
                  parameterWithName("size").description("한 페이지당 불러올 개수 (default: 10)")
                      .optional()
              ),
              responseFields(
                  pageHelper(getBooksResponse())
              )));
    }
  }
}
