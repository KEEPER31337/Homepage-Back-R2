package com.keeper.homepage.domain.library.api

import com.keeper.homepage.domain.library.dto.req.BorrowStatusDto
import com.keeper.homepage.domain.library.entity.BookBorrowInfo
import com.keeper.homepage.domain.library.entity.BookBorrowStatus
import com.keeper.homepage.domain.library.entity.BookBorrowStatus.BookBorrowStatusType.*
import com.keeper.homepage.domain.member.entity.embedded.Nickname
import com.keeper.homepage.global.config.security.data.JwtType
import com.keeper.homepage.global.restdocs.RestDocsHelper.getSecuredValue
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName
import org.springframework.restdocs.cookies.CookieDocumentation.requestCookies
import org.springframework.restdocs.headers.HeaderDocumentation.*
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.*
import org.springframework.restdocs.snippet.Attributes.key
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDateTime

class BorrowManageControllerTest : BorrowManageApiTestHelper() {

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    inner class `대출 정보 조회` {

        private lateinit var borrowInfoList: List<BookBorrowInfo>

        @BeforeEach
        fun setBorrowInfo() {
            borrowInfoList = (1..20).map { bookBorrowInfoTestHelper.generate(대출대기중, bookTestHelper.generate()) }
        }

        @Test
        fun `유효한 요청이면 책 대여 정보 가져오기는 성공해야 한다`() {
            val securedValue =
                getSecuredValue(BorrowManageController::class.java, "getBorrowRequests")
            callGetBorrowApi(
                params = multiValueMapOf("page" to "0", "size" to "3"),
                borrowStatus = BorrowStatusDto.REQUESTS
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.content[0].borrowInfoId").value(borrowInfoList[0].id))
                .andExpect(jsonPath("$.content[0].bookId").value(borrowInfoList[0].book.id))
                .andExpect(jsonPath("$.content[0].bookTitle").value(borrowInfoList[0].book.title))
                .andExpect(jsonPath("$.content[0].author").value(borrowInfoList[0].book.author))
                .andExpect(jsonPath("$.content[0].borrowerId").value(borrowInfoList[0].member.id))
                .andExpect(jsonPath("$.content[0].borrowerNickname").value(borrowInfoList[0].member.nickname))
                .andExpect(jsonPath("$.number").value("0"))
                .andExpect(jsonPath("$.size").value("3"))
                .andExpect(jsonPath("$.totalPages").value("7"))
                .andDo(
                    document(
                        "get-borrow-infos",
                        requestCookies(
                            cookieWithName(JwtType.ACCESS_TOKEN.tokenName).description("ACCESS TOKEN ${securedValue}"),
                            cookieWithName(JwtType.REFRESH_TOKEN.tokenName).description("REFRESH TOKEN ${securedValue}")
                        ),
                        queryParameters(
                            parameterWithName("page").description("페이지 (양수여야 합니다.)")
                                .optional(),
                            parameterWithName("size").description("한 페이지당 불러올 개수 (default: ${DEFAULT_SIZE}) 최대: ${MAX_SIZE} 최소: ${MIN_SIZE}")
                                .optional(),
                            parameterWithName("keyword").description("검색 키워드. 도서명, 저자, 닉네임에서 검색해옵니다.")
                                .optional(),
                            parameterWithName("status")
                                .attributes(
                                    key("format").value(
                                        BorrowStatusDto.values().map(BorrowStatusDto::status).joinToString()
                                    )
                                ).description("만약 빈 값으로 보낼 경우 대출 관련 정보를 모두 가져옵니다.")
                                .optional()
                        ),
                        responseFields(
                            *pageHelper(*getBorrowDetailResponseDocs())
                        )
                    )
                )
        }

        @Test
        fun `페이지와 사이즈에 해당하는 대여 목록을 가져와야 한다`() {
            callGetBorrowApi(
                params = multiValueMapOf("page" to "1", "size" to "5"),
                borrowStatus = BorrowStatusDto.REQUESTS
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.content[0].borrowInfoId").value(borrowInfoList[5].id))
                .andExpect(jsonPath("$.content[0].bookId").value(borrowInfoList[5].book.id))
                .andExpect(jsonPath("$.content[0].bookTitle").value(borrowInfoList[5].book.title))
                .andExpect(jsonPath("$.content[0].author").value(borrowInfoList[5].book.author))
                .andExpect(jsonPath("$.content[0].borrowerId").value(borrowInfoList[5].member.id))
                .andExpect(jsonPath("$.content[0].borrowerNickname").value(borrowInfoList[5].member.nickname))
                .andExpect(jsonPath("$.number").value("1"))
                .andExpect(jsonPath("$.size").value("5"))
                .andExpect(jsonPath("$.totalPages").value("4"))
        }

        @Test
        fun `페이지와 사이즈가 인자로 오지 않아도 default 결과를 반환해야 한다`() {
            callGetBorrowApi(borrowStatus = BorrowStatusDto.REQUESTS)
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.content[0].borrowInfoId").value(borrowInfoList[0].id))
                .andExpect(jsonPath("$.content[0].bookId").value(borrowInfoList[0].book.id))
                .andExpect(jsonPath("$.content[0].bookTitle").value(borrowInfoList[0].book.title))
                .andExpect(jsonPath("$.content[0].author").value(borrowInfoList[0].book.author))
                .andExpect(jsonPath("$.content[0].borrowerId").value(borrowInfoList[0].member.id))
                .andExpect(jsonPath("$.content[0].borrowerNickname").value(borrowInfoList[0].member.nickname))
                .andExpect(jsonPath("$.number").value("0"))
                .andExpect(jsonPath("$.size").value("10"))
                .andExpect(jsonPath("$.totalPages").value("2"))
        }

        @ParameterizedTest
        @CsvSource("-1, 10", "0, -1", "0, 2", "0, 101")
        fun `올바르지 않은 요청은 실패해야 한다`(page: String, size: String) {
            callGetBorrowApi(
                params = multiValueMapOf("page" to page, "size" to size),
                borrowStatus = BorrowStatusDto.REQUESTS
            )
                .andExpect(status().isBadRequest)
        }

        @Test
        fun `status 없이 보낼 경우 모든 대출 관련 목록을 가져와야 한다`() {
            (1..3).map { bookBorrowInfoTestHelper.generate(대출거부, bookTestHelper.generate()) }
            (1..3).map { bookBorrowInfoTestHelper.generate(대출승인, bookTestHelper.generate()) }
            (1..3).map { bookBorrowInfoTestHelper.generate(반납대기중, bookTestHelper.generate()) }
            callGetBorrowApi(borrowStatus = null)
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.number").value("0"))
                .andExpect(jsonPath("$.size").value("10"))
                .andExpect(jsonPath("$.totalPages").value("3"))
                .andExpect(jsonPath("$.totalElements").value("29"))
        }

        @Test
        fun `대출 대기중인 목록만 가져와야 한다`() {
            (1..3).map { bookBorrowInfoTestHelper.generate(대출거부, bookTestHelper.generate()) }
            (1..3).map { bookBorrowInfoTestHelper.generate(대출승인, bookTestHelper.generate()) }
            (1..3).map { bookBorrowInfoTestHelper.generate(반납대기중, bookTestHelper.generate()) }
            callGetBorrowApi(borrowStatus = BorrowStatusDto.REQUESTS)
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.content[0].borrowInfoId").value(borrowInfoList[0].id))
                .andExpect(jsonPath("$.content[0].bookId").value(borrowInfoList[0].book.id))
                .andExpect(jsonPath("$.content[0].bookTitle").value(borrowInfoList[0].book.title))
                .andExpect(jsonPath("$.content[0].author").value(borrowInfoList[0].book.author))
                .andExpect(jsonPath("$.content[0].borrowerId").value(borrowInfoList[0].member.id))
                .andExpect(jsonPath("$.content[0].borrowerNickname").value(borrowInfoList[0].member.nickname))
                .andExpect(jsonPath("$.number").value("0"))
                .andExpect(jsonPath("$.size").value("10"))
                .andExpect(jsonPath("$.totalPages").value("2"))
        }

        @Test
        fun `대출 대기중이거나 반납 대기중인 목록만 가져와야 한다`() {
            (1..3).map { bookBorrowInfoTestHelper.generate(대출거부, bookTestHelper.generate()) }
            (1..3).map { bookBorrowInfoTestHelper.generate(대출승인, bookTestHelper.generate()) }
            (1..3).map { bookBorrowInfoTestHelper.generate(반납대기중, bookTestHelper.generate()) }
            callGetBorrowApi(borrowStatus = BorrowStatusDto.REQUESTS_OR_WILL_RETURN)
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.content[0].borrowInfoId").value(borrowInfoList[0].id))
                .andExpect(jsonPath("$.content[0].bookId").value(borrowInfoList[0].book.id))
                .andExpect(jsonPath("$.content[0].bookTitle").value(borrowInfoList[0].book.title))
                .andExpect(jsonPath("$.content[0].author").value(borrowInfoList[0].book.author))
                .andExpect(jsonPath("$.content[0].borrowerId").value(borrowInfoList[0].member.id))
                .andExpect(jsonPath("$.content[0].borrowerNickname").value(borrowInfoList[0].member.nickname))
                .andExpect(jsonPath("$.number").value("0"))
                .andExpect(jsonPath("$.size").value("10"))
                .andExpect(jsonPath("$.totalPages").value("3"))
                .andExpect(jsonPath("$.totalElements").value("23"))
        }

        @Test
        fun `대출 대기중이거나 반납 대기중인 목록만 가져와야 하고 제목 검색이 가능해야 한다`() {
            val searchKeyword = "나다"
            generateBorrowInfoByTitle(대출대기중, "가나다라") // O
            generateBorrowInfoByTitle(대출대기중, "가나다") // O
            generateBorrowInfoByTitle(대출대기중, "나다라") // O
            generateBorrowInfoByTitle(대출대기중, "나다") // O
            generateBorrowInfoByTitle(대출대기중, "마바사") // X
            generateBorrowInfoByTitle(대출대기중, "나마사") // X
            generateBorrowInfoByTitle(반납대기중, "가나다라") // O
            generateBorrowInfoByTitle(반납대기중, "가나다") // O
            generateBorrowInfoByTitle(반납대기중, "나다라") // O
            generateBorrowInfoByTitle(반납대기중, "나다") // O
            generateBorrowInfoByTitle(대출거부, "나다") // X
            generateBorrowInfoByTitle(대출승인, "나다") // X
            generateBorrowInfoByTitle(반납, "나다") // X
            callGetBorrowApi(
                params = multiValueMapOf("page" to "0", "size" to "5", "keyword" to searchKeyword),
                borrowStatus = BorrowStatusDto.REQUESTS_OR_WILL_RETURN
            ).andExpect(status().isOk)
                .andExpect(jsonPath("$.totalElements").value("8"))
                .andExpect(jsonPath("$.number").value("0"))
                .andExpect(jsonPath("$.size").value("5"))
                .andExpect(jsonPath("$.totalPages").value("2"))
        }

        @Test
        fun `대출 대기중이거나 반납 대기중인 목록만 가져와야 하고 저자 검색이 가능해야 한다`() {
            val searchKeyword = "나다"
            generateBorrowInfoByAuthor(대출대기중, "가나다라") // O
            generateBorrowInfoByAuthor(대출대기중, "가나다") // O
            generateBorrowInfoByAuthor(대출대기중, "나다라") // O
            generateBorrowInfoByAuthor(대출대기중, "나다") // O
            generateBorrowInfoByAuthor(대출대기중, "마바사") // X
            generateBorrowInfoByAuthor(대출대기중, "나마사") // X
            generateBorrowInfoByAuthor(반납대기중, "가나다라") // O
            generateBorrowInfoByAuthor(반납대기중, "가나다") // O
            generateBorrowInfoByAuthor(반납대기중, "나다라") // O
            generateBorrowInfoByAuthor(반납대기중, "나다") // O
            generateBorrowInfoByAuthor(대출거부, "나다") // X
            generateBorrowInfoByAuthor(대출승인, "나다") // X
            generateBorrowInfoByAuthor(반납, "나다") // X
            callGetBorrowApi(
                params = multiValueMapOf("page" to "0", "size" to "5", "keyword" to searchKeyword),
                borrowStatus = BorrowStatusDto.REQUESTS_OR_WILL_RETURN
            ).andExpect(status().isOk)
                .andExpect(jsonPath("$.totalElements").value("8"))
                .andExpect(jsonPath("$.number").value("0"))
                .andExpect(jsonPath("$.size").value("5"))
                .andExpect(jsonPath("$.totalPages").value("2"))
        }

        @Test
        fun `대출 대기중이거나 반납 대기중인 목록만 가져와야 하고 대여자 닉네임 검색이 가능해야 한다`() {
            val searchKeyword = "나다"
            generateBorrowInfoByMemberNickname(대출대기중, "가나다라") // O
            generateBorrowInfoByMemberNickname(대출대기중, "가나다") // O
            generateBorrowInfoByMemberNickname(대출대기중, "나다라") // O
            generateBorrowInfoByMemberNickname(대출대기중, "나다") // O
            generateBorrowInfoByMemberNickname(대출대기중, "마바사") // X
            generateBorrowInfoByMemberNickname(대출대기중, "나마사") // X
            generateBorrowInfoByMemberNickname(반납대기중, "가나다라") // O
            generateBorrowInfoByMemberNickname(반납대기중, "가나다") // O
            generateBorrowInfoByMemberNickname(반납대기중, "나다라") // O
            generateBorrowInfoByMemberNickname(반납대기중, "나다") // O
            generateBorrowInfoByMemberNickname(대출거부, "나다") // X
            generateBorrowInfoByMemberNickname(대출승인, "나다") // X
            generateBorrowInfoByMemberNickname(반납, "나다") // X
            callGetBorrowApi(
                params = multiValueMapOf("page" to "0", "size" to "5", "keyword" to searchKeyword),
                borrowStatus = BorrowStatusDto.REQUESTS_OR_WILL_RETURN
            ).andExpect(status().isOk)
                .andExpect(jsonPath("$.totalElements").value("8"))
                .andExpect(jsonPath("$.number").value("0"))
                .andExpect(jsonPath("$.size").value("5"))
                .andExpect(jsonPath("$.totalPages").value("2"))
        }

        @Test
        fun `대출 대기중이거나 반납 대기중인 목록 검색이 가능해야 한다`() {
            (1..3).map { bookBorrowInfoTestHelper.generate(대출거부, bookTestHelper.generate()) }
            (1..3).map { bookBorrowInfoTestHelper.generate(대출승인, bookTestHelper.generate()) }
            (1..3).map { bookBorrowInfoTestHelper.generate(반납대기중, bookTestHelper.generate()) }
            callGetBorrowApi(borrowStatus = BorrowStatusDto.REQUESTS_OR_WILL_RETURN).andExpect(status().isOk)
                .andExpect(jsonPath("$.number").value("0"))
                .andExpect(jsonPath("$.size").value("10"))
                .andExpect(jsonPath("$.totalPages").value("3"))
        }

        @Test
        fun `반납 대기중인 목록만 가져와야 하고 페이징이 되어야 한다`() {
            (1..2).map { bookBorrowInfoTestHelper.generate(대출거부, bookTestHelper.generate()) }
            (1..4).map { bookBorrowInfoTestHelper.generate(대출승인, bookTestHelper.generate()) }
            (1..8).map { bookBorrowInfoTestHelper.generate(반납대기중, bookTestHelper.generate()) }
            callGetBorrowApi(
                params = multiValueMapOf("page" to "0", "size" to "3"),
                borrowStatus = BorrowStatusDto.WILL_RETURN
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.number").value("0"))
                .andExpect(jsonPath("$.size").value("3"))
                .andExpect(jsonPath("$.totalPages").value("3"))
                .andExpect(jsonPath("$.totalElements").value("8"))
        }

        @Test
        fun `연체중인 목록만 가져와야 하고 페이징이 되어야 한다`() {
            (1..2).map { bookBorrowInfoTestHelper.generate(대출승인, bookTestHelper.generate()) } // 연체 안됨
            (1..2).map { bookBorrowInfoTestHelper.generate(반납대기중, bookTestHelper.generate()) } // 연체 안됨
            (1..2).map { bookBorrowInfoTestHelper.generate(대출거부, bookTestHelper.generate()) }
            (1..4).map {
                bookBorrowInfoTestHelper.generate(
                    대출승인,
                    bookTestHelper.generate(),
                    LocalDateTime.now().minusDays(1)
                )
            }
            (1..8).map {
                bookBorrowInfoTestHelper.generate(
                    반납대기중,
                    bookTestHelper.generate(),
                    LocalDateTime.now().minusDays(1)
                )
            }
            callGetBorrowApi(
                params = multiValueMapOf("page" to "0", "size" to "5"),
                borrowStatus = BorrowStatusDto.OVERDUE
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.number").value("0"))
                .andExpect(jsonPath("$.size").value("5"))
                .andExpect(jsonPath("$.totalElements").value("12"))
                .andExpect(jsonPath("$.totalPages").value("3"))
        }

        private fun generateBorrowInfoByTitle(
            type: BookBorrowStatus.BookBorrowStatusType,
            title: String
        ): BookBorrowInfo {
            return bookBorrowInfoTestHelper.builder()
                .borrowStatus(BookBorrowStatus.getBookBorrowStatusBy(type))
                .book(
                    bookTestHelper.builder()
                        .title(title)
                        .build()
                )
                .build()
        }

        private fun generateBorrowInfoByAuthor(
            type: BookBorrowStatus.BookBorrowStatusType,
            author: String
        ): BookBorrowInfo {
            return bookBorrowInfoTestHelper.builder()
                .borrowStatus(BookBorrowStatus.getBookBorrowStatusBy(type))
                .book(
                    bookTestHelper.builder()
                        .author(author)
                        .build()
                )
                .build()
        }

        private fun generateBorrowInfoByMemberNickname(
            type: BookBorrowStatus.BookBorrowStatusType,
            nickname: String
        ): BookBorrowInfo {
            return bookBorrowInfoTestHelper.builder()
                .borrowStatus(BookBorrowStatus.getBookBorrowStatusBy(type))
                .member(
                    memberTestHelper.builder()
                        .nickname(Nickname.from(nickname))
                        .build()
                )
                .build()
        }
    }

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    inner class `대출 신청 승인 거절` {

        private lateinit var borrowInfo: BookBorrowInfo

        @BeforeEach
        fun setBorrowInfo() {
            borrowInfo = bookBorrowInfoTestHelper.generate(대출대기중, bookTestHelper.generate())
        }

        @Test
        fun `유효한 요청이면 책 대여 승인이 성공해야 한다`() {
            val beforeBorrowingQuantity = borrowInfo.book.countInBorrowing
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
            borrowInfo.book.countInBorrowing shouldBe beforeBorrowingQuantity + 1
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

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    inner class `반납 신청 승인 거절` {

        private lateinit var borrowInfo: BookBorrowInfo

        @BeforeEach
        fun setBorrowInfo() {
            borrowInfo = bookBorrowInfoTestHelper.generate(반납대기중, bookTestHelper.generate())
        }

        @Test
        fun `유효한 요청이면 책 반납 승인이 성공해야 한다`() {
            val beforeBorrowingQuantity = borrowInfo.book.countInBorrowing
            val securedValue = getSecuredValue(BorrowManageController::class.java, "approveReturn")
            callApproveReturnApi(borrowInfo.id)
                .andExpect(status().isNoContent)
                .andDo(
                    document(
                        "borrow-return-approve",
                        requestCookies(
                            cookieWithName(JwtType.ACCESS_TOKEN.tokenName).description("ACCESS TOKEN ${securedValue}"),
                            cookieWithName(JwtType.REFRESH_TOKEN.tokenName).description("REFRESH TOKEN ${securedValue}")
                        ),
                        pathParameters(
                            parameterWithName("borrowId").description("대출 ID")
                        )
                    )
                )
            borrowInfo.borrowStatus.type shouldBe 반납
            borrowInfo.book.countInBorrowing shouldBe beforeBorrowingQuantity - 1
        }

        @Test
        fun `유효한 요청이면 책 반납 거절이 성공해야 한다`() {
            val securedValue = getSecuredValue(BorrowManageController::class.java, "denyReturn")
            callDenyReturnApi(borrowInfo.id)
                .andExpect(status().isNoContent)
                .andDo(
                    document(
                        "borrow-return-deny",
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
        }
    }
}
