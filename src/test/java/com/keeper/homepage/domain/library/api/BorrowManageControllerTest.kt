package com.keeper.homepage.domain.library.api

import com.keeper.homepage.domain.library.BookBorrowInfoTestHelper
import com.keeper.homepage.domain.library.dto.resp.RESPONSE_DATETIME_FORMAT
import com.keeper.homepage.domain.library.entity.BookBorrowInfo
import com.keeper.homepage.domain.library.entity.BookBorrowStatus
import com.keeper.homepage.domain.library.entity.BookBorrowStatus.BookBorrowStatusType.*
import com.keeper.homepage.domain.library.entity.BookBorrowStatus.getBookBorrowStatusBy
import com.keeper.homepage.global.config.security.data.JwtType
import com.keeper.homepage.global.restdocs.RestDocsHelper.getSecuredValue
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName
import org.springframework.restdocs.cookies.CookieDocumentation.requestCookies
import org.springframework.restdocs.headers.HeaderDocumentation.*
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun BookBorrowInfoTestHelper.generate(borrowStatus: BookBorrowStatus.BookBorrowStatusType): BookBorrowInfo {
    return this.builder().borrowStatus(getBookBorrowStatusBy(borrowStatus)).build()
}

fun LocalDateTime.formatting(format: String) = this.format(DateTimeFormatter.ofPattern(format))

class BorrowManageControllerTest : BorrowManageApiTestHelper() {

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    inner class `대출 신청 도서 조회` {

        private lateinit var borrowInfoList: List<BookBorrowInfo>

        @BeforeEach
        fun setBorrowInfo() {
            borrowInfoList = (1..20).map { bookBorrowInfoTestHelper.generate(대출대기중) }
        }

        @Test
        fun `유효한 요청이면 책 대여 정보 가져오기는 성공해야 한다`() {
            val securedValue =
                getSecuredValue(BorrowManageController::class.java, "getBorrowRequests")
            callGetBorrowRequestsApi(params = multiValueMapOf("page" to "0", "size" to "3"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.content[0].borrowInfoId").value(borrowInfoList[0].id))
                .andExpect(jsonPath("$.content[0].bookId").value(borrowInfoList[0].book.id))
                .andExpect(jsonPath("$.content[0].bookTitle").value(borrowInfoList[0].book.title))
                .andExpect(jsonPath("$.content[0].author").value(borrowInfoList[0].book.author))
                .andExpect(jsonPath("$.content[0].borrowerId").value(borrowInfoList[0].member.id))
                .andExpect(jsonPath("$.content[0].borrowerNickname").value(borrowInfoList[0].member.nickname))
                .andExpect(
                    jsonPath("$.content[0].requestDatetime")
                        .value(borrowInfoList[0].borrowDate.formatting(RESPONSE_DATETIME_FORMAT))
                )
                .andExpect(jsonPath("$.number").value("0"))
                .andExpect(jsonPath("$.size").value("3"))
                .andExpect(jsonPath("$.totalPages").value("7"))
                .andDo(
                    document(
                        "get-borrow-requests",
                        requestCookies(
                            cookieWithName(JwtType.ACCESS_TOKEN.tokenName).description("ACCESS TOKEN ${securedValue}"),
                            cookieWithName(JwtType.REFRESH_TOKEN.tokenName).description("REFRESH TOKEN ${securedValue}")
                        ),
                        queryParameters(
                            parameterWithName("page").description("페이지 (양수여야 합니다.)")
                                .optional(),
                            parameterWithName("size").description("한 페이지당 불러올 개수 (default: ${DEFAULT_SIZE}) 최대: ${MAX_SIZE} 최소: ${MIN_SIZE}")
                                .optional(),
                        ),
                        responseFields(
                            *pageHelper(
                                field("borrowInfoId", "대출 정보 ID"),
                                field("bookId", "대출할 책의 ID"),
                                field("bookTitle", "대출할 책의 제목"),
                                field("author", "대출할 책의 저자"),
                                field("borrowerId", "대출자의 ID"),
                                field("borrowerNickname", "대출자의 닉네임"),
                                field("requestDatetime", "대출 요청을 한 시간")
                            )
                        )
                    )
                )
        }

        @Test
        fun `페이지와 사이즈에 해당하는 대여 목록을 가져와야 한다`() {
            callGetBorrowRequestsApi(params = multiValueMapOf("page" to "1", "size" to "5"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.content[0].borrowInfoId").value(borrowInfoList[5].id))
                .andExpect(jsonPath("$.content[0].bookId").value(borrowInfoList[5].book.id))
                .andExpect(jsonPath("$.content[0].bookTitle").value(borrowInfoList[5].book.title))
                .andExpect(jsonPath("$.content[0].author").value(borrowInfoList[5].book.author))
                .andExpect(jsonPath("$.content[0].borrowerId").value(borrowInfoList[5].member.id))
                .andExpect(jsonPath("$.content[0].borrowerNickname").value(borrowInfoList[5].member.nickname))
                .andExpect(
                    jsonPath("$.content[0].requestDatetime")
                        .value(borrowInfoList[5].borrowDate.formatting(RESPONSE_DATETIME_FORMAT))
                )
                .andExpect(jsonPath("$.number").value("1"))
                .andExpect(jsonPath("$.size").value("5"))
                .andExpect(jsonPath("$.totalPages").value("4"))
        }

        @Test
        fun `페이지와 사이즈가 인자로 오지 않아도 default 결과를 반환해야 한다`() {
            callGetBorrowRequestsApi()
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.content[0].borrowInfoId").value(borrowInfoList[0].id))
                .andExpect(jsonPath("$.content[0].bookId").value(borrowInfoList[0].book.id))
                .andExpect(jsonPath("$.content[0].bookTitle").value(borrowInfoList[0].book.title))
                .andExpect(jsonPath("$.content[0].author").value(borrowInfoList[0].book.author))
                .andExpect(jsonPath("$.content[0].borrowerId").value(borrowInfoList[0].member.id))
                .andExpect(jsonPath("$.content[0].borrowerNickname").value(borrowInfoList[0].member.nickname))
                .andExpect(
                    jsonPath("$.content[0].requestDatetime")
                        .value(borrowInfoList[0].borrowDate.formatting(RESPONSE_DATETIME_FORMAT))
                )
                .andExpect(jsonPath("$.number").value("0"))
                .andExpect(jsonPath("$.size").value("10"))
                .andExpect(jsonPath("$.totalPages").value("2"))
        }

        @ParameterizedTest
        @CsvSource("-1, 10", "0, -1", "0, 2", "0, 101")
        fun `올바르지 않은 요청은 실패해야 한다`(page: String, size: String) {
            callGetBorrowRequestsApi(params = multiValueMapOf("page" to page, "size" to size))
                .andExpect(status().isBadRequest)
        }

        @Test
        fun `대출 대기중인 목록만 가져와야 한다`() {
            (1..3).map { bookBorrowInfoTestHelper.generate(대출거부) }
            (1..3).map { bookBorrowInfoTestHelper.generate(대출승인) }
            (1..3).map { bookBorrowInfoTestHelper.generate(반납대기중) }
            callGetBorrowRequestsApi()
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.content[0].borrowInfoId").value(borrowInfoList[0].id))
                .andExpect(jsonPath("$.content[0].bookId").value(borrowInfoList[0].book.id))
                .andExpect(jsonPath("$.content[0].bookTitle").value(borrowInfoList[0].book.title))
                .andExpect(jsonPath("$.content[0].author").value(borrowInfoList[0].book.author))
                .andExpect(jsonPath("$.content[0].borrowerId").value(borrowInfoList[0].member.id))
                .andExpect(jsonPath("$.content[0].borrowerNickname").value(borrowInfoList[0].member.nickname))
                .andExpect(
                    jsonPath("$.content[0].requestDatetime")
                        .value(borrowInfoList[0].borrowDate.formatting(RESPONSE_DATETIME_FORMAT))
                )
                .andExpect(jsonPath("$.number").value("0"))
                .andExpect(jsonPath("$.size").value("10"))
                .andExpect(jsonPath("$.totalPages").value("2"))
        }
    }

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    inner class `대출 신청 승인 거절` {

        private lateinit var borrowInfo: BookBorrowInfo

        @BeforeEach
        fun setBorrowInfo() {
            borrowInfo = bookBorrowInfoTestHelper.generate(대출대기중)
        }

        @Test
        fun `유효한 요청이면 책 대여 승인이 성공해야 한다`() {
            val beforeQuantity = borrowInfo.book.currentQuantity
            val securedValue = getSecuredValue(BorrowManageController::class.java, "approveBorrow")
            callApproveBorrowApi(borrowInfo.id)
                .andExpect(status().isNoContent)
                .andDo(
                    document(
                        "borrow-requests-approve",
                        requestCookies(
                            cookieWithName(JwtType.ACCESS_TOKEN.tokenName).description("ACCESS TOKEN ${securedValue}"),
                            cookieWithName(JwtType.REFRESH_TOKEN.tokenName).description("REFRESH TOKEN ${securedValue}")
                        ),
                        pathParameters(
                            parameterWithName("borrowId").description("대출 ID")
                        )
                    )
                )
            borrowInfo.borrowStatus.type shouldBe 대출승인
            borrowInfo.book.currentQuantity shouldBe beforeQuantity - 1
        }

        @Test
        fun `유효한 요청이면 책 대여 거절이 성공해야 한다`() {
            val securedValue = getSecuredValue(BorrowManageController::class.java, "denyBorrow")
            callDenyBorrowApi(borrowInfo.id)
                .andExpect(status().isNoContent)
                .andDo(
                    document(
                        "borrow-requests-deny",
                        requestCookies(
                            cookieWithName(JwtType.ACCESS_TOKEN.tokenName).description("ACCESS TOKEN ${securedValue}"),
                            cookieWithName(JwtType.REFRESH_TOKEN.tokenName).description("REFRESH TOKEN ${securedValue}")
                        ),
                        pathParameters(
                            parameterWithName("borrowId").description("대출 ID")
                        )
                    )
                )
            borrowInfo.borrowStatus.type shouldBe 대출거부
        }
    }
}
