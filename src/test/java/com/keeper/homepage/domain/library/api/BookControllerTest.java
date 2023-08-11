package com.keeper.homepage.domain.library.api;

import static com.keeper.homepage.domain.library.entity.BookBorrowStatus.BookBorrowStatusType.대출승인;
import static com.keeper.homepage.domain.library.entity.BookBorrowStatus.getBookBorrowStatusBy;
import static com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_회원;
import static com.keeper.homepage.global.config.security.data.JwtType.ACCESS_TOKEN;
import static com.keeper.homepage.global.restdocs.RestDocsHelper.getSecuredValue;
import static com.keeper.homepage.global.restdocs.RestDocsHelper.pageHelper;

import com.keeper.homepage.domain.library.entity.BookBorrowInfo;
import com.keeper.homepage.domain.member.entity.Member;
import org.springframework.restdocs.snippet.Attributes.Attribute;

import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.keeper.homepage.domain.library.entity.Book;
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
                      .attributes(new Attribute("format",
                          "title: 제목, author: 저자, all: 제목 + 저자, null : 전체 도서 목록 조회"))
                      .description("검색 타입")
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

  @Nested
  @DisplayName("도서 대출 신청")
  class RequestBorrow {

    @Test
    @DisplayName("유효한 요청은 도서 대출 신청이 성공해야 한다.")
    public void 유효한_요청은_도서_대출_신청이_성공해야_한다() throws Exception {
      String securedValue = getSecuredValue(BookController.class, "requestBorrow");

      Book book = bookTestHelper.generate();

      callRequestBorrowBookApi(memberToken, book.getId())
          .andExpect(status().isCreated())
          .andDo(document("request-book-borrow",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              ),
              pathParameters(
                  parameterWithName("bookId")
                      .description("대출하고자 하는 도서 ID")
              )));
    }
  }

  @Nested
  @DisplayName("대여 도서 조회")
  class GetBorrowCount {

    private Member member;
    private Book book;
    private String memberToken;

    @BeforeEach
    void setup() {
      member = memberTestHelper.generate();
      book = bookTestHelper.generate();
      memberToken = jwtTokenProvider.createAccessToken(ACCESS_TOKEN, member.getId(), ROLE_회원);
    }

    @Test
    @DisplayName("회원의 빌린 책의 개수를 성공적으로 반환해야 한다.")
    public void 회원의_빌린_책의_개수를_성공적으로_반환해야_한다() throws Exception {
      String securedValue = getSecuredValue(BookController.class, "getBookBorrows");

      bookBorrowInfoTestHelper.builder()
          .member(member)
          .book(book)
          .borrowStatus(getBookBorrowStatusBy(대출승인))
          .build();
      params.add("page", "0");
      params.add("size", "3");
      callGetBorrowBooksApi(memberToken, params)
          .andExpect(status().isOk())
          .andDo(document("get-book-borrows",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              ),
              queryParameters(
                  parameterWithName("page").description("페이지 (default: 0)")
                      .optional(),
                  parameterWithName("size").description("한 페이지당 불러올 개수 (default: 10)")
                      .optional()
              ),
              responseFields(
                  pageHelper(getBorrowBooksResponse())
              )));
    }

    @Nested
    @DisplayName("도서 반납 요청")
    class RequestReturn {

      @Test
      @DisplayName("유효한 도서 반납 요청은 성공해야 한다.")
      public void 유효한_도서_반납_요청은_성공해야_한다() throws Exception {
        String securedValue = getSecuredValue(BookController.class, "requestReturn");
        BookBorrowInfo bookBorrowInfo = bookBorrowInfoTestHelper.builder()
            .member(member)
            .book(book)
            .borrowStatus(getBookBorrowStatusBy(대출승인))
            .build();

        long borrowId = bookBorrowInfo.getId();

        callRequestReturnBookApi(memberToken, borrowId)
            .andExpect(status().isNoContent())
            .andDo(document("request-book-return",
                requestCookies(
                    cookieWithName(ACCESS_TOKEN.getTokenName())
                        .description("ACCESS TOKEN %s".formatted(securedValue))
                ),
                pathParameters(
                    parameterWithName("borrowId")
                        .description("도서 대출 내역 ID")
                )));
      }
    }
  }
}
