package com.keeper.homepage.domain.library.api

import com.keeper.homepage.domain.library.dto.req.BookRequest
import com.keeper.homepage.domain.library.dto.req.MAX_AUTHOR_LENGTH
import com.keeper.homepage.domain.library.dto.req.MAX_TITLE_LENGTH
import com.keeper.homepage.domain.library.dto.req.MAX_TOTAL_QUANTITY_LENGTH
import com.keeper.homepage.domain.library.entity.Book
import com.keeper.homepage.domain.library.entity.BookBorrowInfo
import com.keeper.homepage.domain.library.entity.BookBorrowStatus.BookBorrowStatusType.*
import com.keeper.homepage.domain.library.entity.BookBorrowStatus.getBookBorrowStatusBy
import com.keeper.homepage.domain.library.entity.BookDepartment.BookDepartmentType
import com.keeper.homepage.domain.member.entity.job.MemberJob
import com.keeper.homepage.global.config.security.data.JwtType.ACCESS_TOKEN
import com.keeper.homepage.global.config.security.data.JwtType.REFRESH_TOKEN
import com.keeper.homepage.global.restdocs.RestDocsHelper.getSecuredValue
import org.junit.jupiter.api.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.http.HttpHeaders
import org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName
import org.springframework.restdocs.cookies.CookieDocumentation.requestCookies
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.*
import org.springframework.restdocs.snippet.Attributes
import org.springframework.restdocs.snippet.Attributes.key
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.util.MultiValueMap
import java.lang.reflect.Field
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.stream.Stream

/**
 * 0.5ms를 의미합니다.
 */
const val HALF_NANOSECOND = 500000000

fun LocalDateTime.formatting(format: String) =
    if (this.nano > HALF_NANOSECOND)
        this.plusSeconds(1).format(DateTimeFormatter.ofPattern(format))
    else
        this.format(DateTimeFormatter.ofPattern(format))

class BookManageControllerTest : BookManageApiTestHelper() {

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    inner class `관리자 책 목록 검색` {
        private lateinit var validParams: MultiValueMap<String, String>
        private lateinit var bookList: List<Book>

        @BeforeEach
        fun setUp() {
            bookList = (0..3).map { bookTestHelper.builder().totalQuantity(2).build() }
            (0..3).map { bookBorrowInfoTestHelper.generate(borrowStatus = 대출대기, book = bookList[it]) }
            bookBorrowInfoTestHelper.generate(borrowStatus = 대출중, book = bookList[0])
            validParams = multiValueMapOf(
                "search" to "",
                "page" to "0",
                "size" to "3",
                "searchType" to "all",
            )
        }

        @Test
        fun `유효한 요청이면 관리자 책 목록 가져오기는 성공해야 한다`() {
            val securedValue = getSecuredValue(BookManageController::class.java, "getBooks")
            callGetBooksApi(validParams).andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.content[0].bookId").value(bookList[0].id))
                .andExpect(jsonPath("$.content[0].title").value(bookList[0].title))
                .andExpect(jsonPath("$.content[0].author").value(bookList[0].author))
                .andExpect(jsonPath("$.content[0].totalQuantity").value(bookList[0].totalQuantity))
                .andExpect(jsonPath("$.content[0].currentQuantity").value(bookList[0].currentQuantity))
                .andExpect(jsonPath("$.content[0].bookDepartment").value(bookList[0].bookDepartment.type.name))
                .andExpect(jsonPath("$.content[0].totalQuantity").value(2))
                .andExpect(jsonPath("$.content[0].currentQuantity").value(1))
                .andExpect(jsonPath("$.content[0].thumbnailPath").value(bookList[0].thumbnailPath))
                .andExpect(jsonPath("$.number").value("0"))
                .andExpect(jsonPath("$.size").value("3"))
                .andExpect(jsonPath("$.totalPages").value("2"))
                .andDo(
                    document(
                        "manager-get-books",
                        requestCookies(
                            cookieWithName(ACCESS_TOKEN.tokenName).description("ACCESS TOKEN ${securedValue}"),
                            cookieWithName(REFRESH_TOKEN.tokenName).description("REFRESH TOKEN ${securedValue}")
                        ),
                        queryParameters(
                            parameterWithName("page").description("페이지 (양수여야 합니다.)")
                                .optional(),
                            parameterWithName("size").description("한 페이지당 불러올 개수 (default: ${DEFAULT_SIZE}) 최대: ${MAX_SIZE} 최소: ${MIN_SIZE}")
                                .optional(),
                            parameterWithName("search").description("책의 제목이나 저자를 검색합니다. (만약 빈 값으로 보낼 경우 책 관련 정보를 모두 가져옵니다.)")
                                .optional(),
                            parameterWithName("searchType").description("검색 타입")
                                .attributes(
                                    Attributes.Attribute(
                                        "format",
                                        "title: 제목, author: 저자, all: 제목 + 저자, null : 전체 도서 목록 조회"
                                    )
                                )
                                .optional(),
                        ),
                        responseFields(
                            *pageHelper(*getBookDetailResponseDocs())
                        )
                    )
                )
        }
    }

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    inner class `책 추가` {

        private val validRequest = BookRequest(
            title = "삶의 목적을 찾는 45가지 방법",
            author = "ChatGPT",
            totalQuantity = 10,
            bookDepartment = BookDepartmentType.DOCUMENT
        )

        @Test
        fun `유효한 요청이면 책 등록은 성공해야 한다`() {
            val securedValue = getSecuredValue(BookManageController::class.java, "addBook")
            callAddBookApi(hasThumbnail = true)
                .andExpect(status().isCreated)
                .andDo(
                    document(
                        "assign-book",
                        requestCookies(
                            cookieWithName(ACCESS_TOKEN.tokenName).description("ACCESS TOKEN ${securedValue}"),
                            cookieWithName(REFRESH_TOKEN.tokenName).description("REFRESH TOKEN ${securedValue}")
                        ),
                        requestPartFields(
                            "bookMetaData",
                            fieldWithPath("title").description("책 제목 (최대 ${MAX_TITLE_LENGTH}자)"),
                            fieldWithPath("author").description("저자 (최대 ${MAX_AUTHOR_LENGTH}자)"),
                            fieldWithPath("bookDepartment")
                                .attributes(
                                    key("format")
                                        .value(
                                            BookDepartmentType.values().map(BookDepartmentType::getName).joinToString()
                                        )
                                ).description("책 카테고리"),
                            fieldWithPath("totalQuantity").description("책 수량 (1권 이상 ${MAX_TOTAL_QUANTITY_LENGTH}권 이하)"),
                        ),
                        requestParts(
                            partWithName("bookMetaData").description("책 정보"),
                            partWithName("thumbnail").description("책의 썸네일")
                                .optional(),
                        ),
                        responseHeaders(
                            headerWithName(HttpHeaders.LOCATION).description("생성된 책의 URI")
                        )
                    )
                )
        }

        @Test
        fun `썸네일이 없어도 책 등록은 성공해야 한다`() {
            callAddBookApi()
                .andExpect(status().isCreated)
        }

        @ParameterizedTest
        @MethodSource
        fun `유효하지 않은 요청의 책 등록은 실패해야 한다`(key: String, invalidValue: Any?) {
            val invalidRequest = validRequest.copy()
            val property: Field = BookRequest::class.java.getDeclaredField(key)
            property.isAccessible = true
            property.set(invalidRequest, invalidValue)
            callAddBookApi(request = invalidRequest)
                .andExpect(status().isBadRequest)
        }

        fun `유효하지 않은 요청의 책 등록은 실패해야 한다`(): Stream<Arguments> {
            return Stream.of(
                Arguments.arguments("title", ""),
                Arguments.arguments("title", null),
                Arguments.arguments("author", ""),
                Arguments.arguments("author", null),
                Arguments.arguments("totalQuantity", -1L),
                Arguments.arguments("totalQuantity", 0L),
                Arguments.arguments("totalQuantity", 21L),
                Arguments.arguments("bookDepartment", null),
            )
        }

        @Test
        fun `회원 권한의 책 등록은 실패해야 한다`() {
            val member = memberTestHelper.generate()
            member.assignJob(MemberJob.MemberJobType.ROLE_회원)

            callAddBookApi(accessCookies = memberTestHelper.getTokenCookies(member))
                .andExpect(status().isForbidden)
        }
    }

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    inner class `책 삭제` {

        @Test
        fun `유효한 요청이면 책 삭제는 성공해야 한다`() {
            val securedValue = getSecuredValue(BookManageController::class.java, "deleteBook")
            callDeleteBookApi()
                .andExpect(status().isNoContent)
                .andDo(
                    document(
                        "delete-book",
                        requestCookies(
                            cookieWithName(ACCESS_TOKEN.tokenName).description("ACCESS TOKEN ${securedValue}"),
                            cookieWithName(REFRESH_TOKEN.tokenName).description("REFRESH TOKEN ${securedValue}")
                        ),
                        pathParameters(
                            parameterWithName("bookId").description("책 id")
                        )
                    )
                )
        }

        @Test
        fun `회원 권한의 책 삭제는 실패해야 한다`() {
            val member = memberTestHelper.generate()
            member.assignJob(MemberJob.MemberJobType.ROLE_회원)

            callDeleteBookApi(accessCookies = memberTestHelper.getTokenCookies(member))
                .andExpect(status().isForbidden)
        }
    }

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    inner class `책 수정` {

        lateinit var book: Book

        @BeforeEach
        fun generateNewBook() {
            book = bookTestHelper.builder()
                .thumbnail(thumbnailTestHelper.generateThumbnail())
                .build()
        }

        @Test
        fun `유효한 요청이면 책 수정은 성공해야 한다`() {
            val securedValue = getSecuredValue(BookManageController::class.java, "addBook")
            callModifyBookApi(bookId = book.id)
                .andExpect(status().isNoContent)
                .andDo(
                    document(
                        "modify-book",
                        requestCookies(
                            cookieWithName(ACCESS_TOKEN.tokenName).description("ACCESS TOKEN ${securedValue}"),
                            cookieWithName(REFRESH_TOKEN.tokenName).description("REFRESH TOKEN ${securedValue}")
                        ),
                        pathParameters(
                            parameterWithName("bookId").description("책 id")
                        ),
                        requestFields(
                            field("title", "책 제목 (최대 ${MAX_TITLE_LENGTH}자)"),
                            field("author", "저자 (최대 ${MAX_AUTHOR_LENGTH}자)"),
                            field("bookDepartment", "책 카테고리")
                                .attributes(
                                    key("format")
                                        .value(
                                            BookDepartmentType.values().map(BookDepartmentType::getName).joinToString()
                                        )
                                ),
                            field("totalQuantity", "책 수량 (1권 이상 ${MAX_TOTAL_QUANTITY_LENGTH}권 이하)"),
                        ),
                    )
                )
        }

        @Test
        fun `유효한 요청이면 책 썸네일 수정은 성공해야 한다`() {
            val securedValue = getSecuredValue(BookManageController::class.java, "addBook")
            callModifyBookThumbnailApi(bookId = book.id)
                .andExpect(status().isNoContent)
                .andDo(
                    document(
                        "modify-book-thumbnail",
                        requestCookies(
                            cookieWithName(ACCESS_TOKEN.tokenName).description("ACCESS TOKEN ${securedValue}"),
                            cookieWithName(REFRESH_TOKEN.tokenName).description("REFRESH TOKEN ${securedValue}")
                        ),
                        pathParameters(
                            parameterWithName("bookId").description("책 id")
                        ),
                        requestParts(
                            partWithName("thumbnail").description("책의 썸네일 (null 값으로 보낼 경우 기본 썸네일로 지정됩니다.)")
                                .optional(),
                        )
                    )
                )
        }

        @Test
        fun `회원 권한의 책 수정은 실패해야 한다`() {
            val member = memberTestHelper.generate()
            member.assignJob(MemberJob.MemberJobType.ROLE_회원)

            callModifyBookApi(bookId = book.id, accessCookies = memberTestHelper.getTokenCookies(member))
                .andExpect(status().isForbidden)
        }
    }


    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    inner class `책 상세 정보 가져오기` {

        lateinit var book: Book
        lateinit var borrowList: List<BookBorrowInfo>

        @BeforeEach
        fun generateNewBook() {
            book = bookTestHelper.builder()
                .totalQuantity(3)
                .currentQuantity(3)
                .thumbnail(thumbnailTestHelper.generateThumbnail())
                .build()
            borrowList = listOf(
                bookBorrowInfoTestHelper.builder()
                    .book(book)
                    .borrowStatus(getBookBorrowStatusBy(대출중))
                    .build(),
                bookBorrowInfoTestHelper.builder()
                    .book(book)
                    .borrowStatus(getBookBorrowStatusBy(반납대기))
                    .build(),
                bookBorrowInfoTestHelper.builder()
                    .book(book)
                    .borrowStatus(getBookBorrowStatusBy(반납완료))
                    .build(),
            )
        }

        @Test
        fun `유효한 요청이면 책 상세 정보 가져 오기는 성공 해야 한다`() {
            em.flush()
            em.clear()
            val securedValue = getSecuredValue(BookManageController::class.java, "getBookDetail")
            callGetBookDetailApi(bookId = book.id)
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.bookId").value(book.id))
                .andExpect(jsonPath("$.title").value(book.title))
                .andExpect(jsonPath("$.author").value(book.author))
                .andExpect(jsonPath("$.totalQuantity").value(book.totalQuantity))
                .andExpect(jsonPath("$.currentQuantity").value(book.currentQuantity))
                .andExpect(jsonPath("$.bookDepartment").value(book.bookDepartment.type.name))
                .andExpect(jsonPath("$.totalQuantity").value(3))
                .andExpect(jsonPath("$.currentQuantity").value(1))
                .andExpect(jsonPath("$.thumbnailPath").value(book.thumbnail.path))
                .andExpect(jsonPath("$.borrowInfos[0].borrowInfoId").value(borrowList[0].id))
                .andExpect(jsonPath("$.borrowInfos[0].bookId").value(borrowList[0].book.id))
                .andExpect(jsonPath("$.borrowInfos[0].bookTitle").value(borrowList[0].book.title))
                .andExpect(jsonPath("$.borrowInfos[0].author").value(borrowList[0].book.author))
                .andExpect(jsonPath("$.borrowInfos[0].borrowerId").value(borrowList[0].member.id))
                .andExpect(
                    jsonPath("$.borrowInfos[0].borrowerRealName")
                        .value(borrowList[0].member.realName)
                )
                .andDo(
                    document(
                        "get-book-detail",
                        requestCookies(
                            cookieWithName(ACCESS_TOKEN.tokenName).description("ACCESS TOKEN ${securedValue}"),
                            cookieWithName(REFRESH_TOKEN.tokenName).description("REFRESH TOKEN ${securedValue}")
                        ),
                        pathParameters(
                            parameterWithName("bookId").description("책 id")
                        ),
                        responseFields(
                            *getBookDetailResponseDocs()
                        ),
                    )
                )
        }
    }
}
