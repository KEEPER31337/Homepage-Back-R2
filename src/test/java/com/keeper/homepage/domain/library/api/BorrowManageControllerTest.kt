package com.keeper.homepage.domain.library.api

import com.keeper.homepage.domain.library.dto.req.BorrowStatusDto
import com.keeper.homepage.domain.library.entity.BookBorrowInfo
import com.keeper.homepage.domain.library.entity.BookBorrowLog
import com.keeper.homepage.domain.library.entity.BookBorrowLog.LogType
import com.keeper.homepage.domain.library.entity.BookBorrowStatus
import com.keeper.homepage.domain.library.entity.BookBorrowStatus.BookBorrowStatusType.*
import com.keeper.homepage.domain.member.entity.embedded.RealName
import com.keeper.homepage.global.config.security.data.JwtType
import com.keeper.homepage.global.restdocs.RestDocsHelper.getSecuredValue
import io.kotest.matchers.shouldBe
import org.assertj.core.api.Assertions.assertThat
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
            borrowInfoList = (1..20).map { bookBorrowInfoTestHelper.generate(대출대기, bookTestHelper.generate()) }
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
                .andExpect(jsonPath("$.content[0].totalQuantity").value(borrowInfoList[0].book.totalQuantity))
                .andExpect(jsonPath("$.content[0].currentQuantity").value(borrowInfoList[0].book.currentQuantity))
                .andExpect(jsonPath("$.content[0].borrowerId").value(borrowInfoList[0].member.id))
                .andExpect(jsonPath("$.content[0].borrowerRealName").value(borrowInfoList[0].member.realName))
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
                            parameterWithName("search").description("검색 키워드. 도서명, 저자, 실명에서 검색해옵니다.")
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
                .andExpect(jsonPath("$.content[0].borrowerRealName").value(borrowInfoList[5].member.realName))
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
                .andExpect(jsonPath("$.content[0].borrowerRealName").value(borrowInfoList[0].member.realName))
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
            (1..3).map { bookBorrowInfoTestHelper.generate(대출반려, bookTestHelper.generate()) }
            (1..3).map { bookBorrowInfoTestHelper.generate(대출중, bookTestHelper.generate()) }
            (1..3).map { bookBorrowInfoTestHelper.generate(반납대기, bookTestHelper.generate()) }
            callGetBorrowApi(borrowStatus = null)
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.number").value("0"))
                .andExpect(jsonPath("$.size").value("10"))
                .andExpect(jsonPath("$.totalPages").value("3"))
                .andExpect(jsonPath("$.totalElements").value("29"))
        }

        @Test
        fun `대출 대기중인 목록만 가져와야 한다`() {
            (1..3).map { bookBorrowInfoTestHelper.generate(대출반려, bookTestHelper.generate()) }
            (1..3).map { bookBorrowInfoTestHelper.generate(대출중, bookTestHelper.generate()) }
            (1..3).map { bookBorrowInfoTestHelper.generate(반납대기, bookTestHelper.generate()) }
            callGetBorrowApi(borrowStatus = BorrowStatusDto.REQUESTS)
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.content[0].borrowInfoId").value(borrowInfoList[0].id))
                .andExpect(jsonPath("$.content[0].bookId").value(borrowInfoList[0].book.id))
                .andExpect(jsonPath("$.content[0].bookTitle").value(borrowInfoList[0].book.title))
                .andExpect(jsonPath("$.content[0].author").value(borrowInfoList[0].book.author))
                .andExpect(jsonPath("$.content[0].borrowerId").value(borrowInfoList[0].member.id))
                .andExpect(jsonPath("$.content[0].borrowerRealName").value(borrowInfoList[0].member.realName))
                .andExpect(jsonPath("$.number").value("0"))
                .andExpect(jsonPath("$.size").value("10"))
                .andExpect(jsonPath("$.totalPages").value("2"))
        }

        @Test
        fun `대출 대기중이거나 반납 대기중인 목록만 가져와야 한다`() {
            (1..3).map { bookBorrowInfoTestHelper.generate(대출반려, bookTestHelper.generate()) }
            (1..3).map { bookBorrowInfoTestHelper.generate(대출중, bookTestHelper.generate()) }
            (1..3).map { bookBorrowInfoTestHelper.generate(반납대기, bookTestHelper.generate()) }
            callGetBorrowApi(borrowStatus = BorrowStatusDto.REQUESTS_OR_WILL_RETURN)
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.content[0].borrowInfoId").value(borrowInfoList[0].id))
                .andExpect(jsonPath("$.content[0].bookId").value(borrowInfoList[0].book.id))
                .andExpect(jsonPath("$.content[0].bookTitle").value(borrowInfoList[0].book.title))
                .andExpect(jsonPath("$.content[0].author").value(borrowInfoList[0].book.author))
                .andExpect(jsonPath("$.content[0].borrowerId").value(borrowInfoList[0].member.id))
                .andExpect(jsonPath("$.content[0].borrowerRealName").value(borrowInfoList[0].member.realName))
                .andExpect(jsonPath("$.number").value("0"))
                .andExpect(jsonPath("$.size").value("10"))
                .andExpect(jsonPath("$.totalPages").value("3"))
                .andExpect(jsonPath("$.totalElements").value("23"))
        }

        @Test
        fun `대출 대기중이거나 반납 대기중인 목록만 가져와야 하고 제목 검색이 가능해야 한다`() {
            val searchKeyword = "나다"
            generateBorrowInfoByTitle(대출대기, "가나다라") // O
            generateBorrowInfoByTitle(대출대기, "가나다") // O
            generateBorrowInfoByTitle(대출대기, "나다라") // O
            generateBorrowInfoByTitle(대출대기, "나다") // O
            generateBorrowInfoByTitle(대출대기, "마바사") // X
            generateBorrowInfoByTitle(대출대기, "나마사") // X
            generateBorrowInfoByTitle(반납대기, "가나다라") // O
            generateBorrowInfoByTitle(반납대기, "가나다") // O
            generateBorrowInfoByTitle(반납대기, "나다라") // O
            generateBorrowInfoByTitle(반납대기, "나다") // O
            generateBorrowInfoByTitle(대출반려, "나다") // X
            generateBorrowInfoByTitle(대출중, "나다") // X
            generateBorrowInfoByTitle(반납완료, "나다") // X
            callGetBorrowApi(
                params = multiValueMapOf("page" to "0", "size" to "5", "search" to searchKeyword),
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
            generateBorrowInfoByAuthor(대출대기, "가나다라") // O
            generateBorrowInfoByAuthor(대출대기, "가나다") // O
            generateBorrowInfoByAuthor(대출대기, "나다라") // O
            generateBorrowInfoByAuthor(대출대기, "나다") // O
            generateBorrowInfoByAuthor(대출대기, "마바사") // X
            generateBorrowInfoByAuthor(대출대기, "나마사") // X
            generateBorrowInfoByAuthor(반납대기, "가나다라") // O
            generateBorrowInfoByAuthor(반납대기, "가나다") // O
            generateBorrowInfoByAuthor(반납대기, "나다라") // O
            generateBorrowInfoByAuthor(반납대기, "나다") // O
            generateBorrowInfoByAuthor(대출반려, "나다") // X
            generateBorrowInfoByAuthor(대출중, "나다") // X
            generateBorrowInfoByAuthor(반납완료, "나다") // X
            callGetBorrowApi(
                params = multiValueMapOf("page" to "0", "size" to "5", "search" to searchKeyword),
                borrowStatus = BorrowStatusDto.REQUESTS_OR_WILL_RETURN
            ).andExpect(status().isOk)
                .andExpect(jsonPath("$.totalElements").value("8"))
                .andExpect(jsonPath("$.number").value("0"))
                .andExpect(jsonPath("$.size").value("5"))
                .andExpect(jsonPath("$.totalPages").value("2"))
        }

        @Test
        fun `대출 대기중이거나 반납 대기중인 목록만 가져와야 하고 대여자 실명 검색이 가능해야 한다`() {
            val searchKeyword = "나다"
            generateBorrowInfoByMemberRealName(대출대기, "가나다라") // O
            generateBorrowInfoByMemberRealName(대출대기, "가나다") // O
            generateBorrowInfoByMemberRealName(대출대기, "나다라") // O
            generateBorrowInfoByMemberRealName(대출대기, "나다") // O
            generateBorrowInfoByMemberRealName(대출대기, "마바사") // X
            generateBorrowInfoByMemberRealName(대출대기, "나마사") // X
            generateBorrowInfoByMemberRealName(반납대기, "가나다라") // O
            generateBorrowInfoByMemberRealName(반납대기, "가나다") // O
            generateBorrowInfoByMemberRealName(반납대기, "나다라") // O
            generateBorrowInfoByMemberRealName(반납대기, "나다") // O
            generateBorrowInfoByMemberRealName(대출반려, "나다") // X
            generateBorrowInfoByMemberRealName(대출중, "나다") // X
            generateBorrowInfoByMemberRealName(반납완료, "나다") // X
            callGetBorrowApi(
                params = multiValueMapOf("page" to "0", "size" to "5", "search" to searchKeyword),
                borrowStatus = BorrowStatusDto.REQUESTS_OR_WILL_RETURN
            ).andExpect(status().isOk)
                .andExpect(jsonPath("$.totalElements").value("8"))
                .andExpect(jsonPath("$.number").value("0"))
                .andExpect(jsonPath("$.size").value("5"))
                .andExpect(jsonPath("$.totalPages").value("2"))
        }

        @Test
        fun `대출 대기중이거나 반납 대기중인 목록 검색이 가능해야 한다`() {
            (1..3).map { bookBorrowInfoTestHelper.generate(대출반려, bookTestHelper.generate()) }
            (1..3).map { bookBorrowInfoTestHelper.generate(대출중, bookTestHelper.generate()) }
            (1..3).map { bookBorrowInfoTestHelper.generate(반납대기, bookTestHelper.generate()) }
            callGetBorrowApi(borrowStatus = BorrowStatusDto.REQUESTS_OR_WILL_RETURN).andExpect(status().isOk)
                .andExpect(jsonPath("$.number").value("0"))
                .andExpect(jsonPath("$.size").value("10"))
                .andExpect(jsonPath("$.totalPages").value("3"))
        }

        @Test
        fun `반납 대기중인 목록만 가져와야 하고 페이징이 되어야 한다`() {
            (1..2).map { bookBorrowInfoTestHelper.generate(대출반려, bookTestHelper.generate()) }
            (1..4).map { bookBorrowInfoTestHelper.generate(대출중, bookTestHelper.generate()) }
            (1..8).map { bookBorrowInfoTestHelper.generate(반납대기, bookTestHelper.generate()) }
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
            (1..2).map { bookBorrowInfoTestHelper.generate(대출중, bookTestHelper.generate()) } // 연체 안됨
            (1..2).map { bookBorrowInfoTestHelper.generate(반납대기, bookTestHelper.generate()) } // 연체 안됨
            (1..2).map { bookBorrowInfoTestHelper.generate(대출반려, bookTestHelper.generate()) }
            (1..4).map {
                bookBorrowInfoTestHelper.generate(
                    대출중,
                    bookTestHelper.generate(),
                    LocalDateTime.now().minusDays(1)
                )
            }
            (1..8).map {
                bookBorrowInfoTestHelper.generate(
                    반납대기,
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

        private fun generateBorrowInfoByMemberRealName(
            type: BookBorrowStatus.BookBorrowStatusType,
            name: String
        ): BookBorrowInfo {
            return bookBorrowInfoTestHelper.builder()
                .borrowStatus(BookBorrowStatus.getBookBorrowStatusBy(type))
                .member(
                    memberTestHelper.builder()
                        .realName(RealName.from(name))
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
            borrowInfo = bookBorrowInfoTestHelper.generate(대출대기, bookTestHelper.generate())
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
            borrowInfo.borrowStatus.type shouldBe 대출중
            borrowInfo.book.countInBorrowing shouldBe beforeBorrowingQuantity + 1
        }

        @Test
        fun `책 대여 승인시 로그가 남아야 한다`() {
            val beforeBorrowTime = LocalDateTime.now()
            callApproveBorrowApi(borrowInfo.id).andExpect(status().isNoContent)

            val borrowLogs = bookBorrowLogRepository.findAll()

            checkBorrowLog(borrowLogs[0], borrowInfo, LogType.대출중)
            assertThat(borrowLogs[0].borrowDate).isAfter(beforeBorrowTime)
            assertThat(borrowLogs[0].borrowDate).isBefore(LocalDateTime.now())
            assertThat(borrowLogs[0].expireDate).isAfter(beforeBorrowTime.plusWeeks(2))
            assertThat(borrowLogs[0].expireDate).isBefore(LocalDateTime.now().plusWeeks(2))
            assertThat(borrowLogs[0].returnDate).isNull()
            assertThat(borrowLogs[0].rejectDate).isNull()
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
            borrowInfo.borrowStatus.type shouldBe 대출반려
        }

        @Test
        fun `책 대여 거절시 로그가 남아야 한다`() {
            val beforeBorrowTime = LocalDateTime.now()
            callDenyBorrowApi(borrowInfo.id).andExpect(status().isNoContent)

            val borrowLogs = bookBorrowLogRepository.findAll()

            checkBorrowLog(borrowLogs[0], borrowInfo, LogType.대출반려)
            assertThat(borrowLogs[0].borrowDate).isBefore(beforeBorrowTime)
            assertThat(borrowLogs[0].expireDate).isBefore(beforeBorrowTime.plusWeeks(2))
            assertThat(borrowLogs[0].returnDate).isNull()
            assertThat(borrowLogs[0].rejectDate).isAfter(beforeBorrowTime)
            assertThat(borrowLogs[0].rejectDate).isBefore(LocalDateTime.now())
        }
    }

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    inner class `반납 신청 승인 거절` {

        private lateinit var borrowInfo: BookBorrowInfo

        @BeforeEach
        fun setBorrowInfo() {
            borrowInfo = bookBorrowInfoTestHelper.generate(반납대기, bookTestHelper.generate())
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
            borrowInfo.borrowStatus.type shouldBe 반납완료
            borrowInfo.book.countInBorrowing shouldBe beforeBorrowingQuantity - 1
        }

        @Test
        fun `책 반납 승인시 로그가 남아야 한다`() {
            val beforeBorrowTime = LocalDateTime.now()
            callApproveReturnApi(borrowInfo.id).andExpect(status().isNoContent)

            val borrowLogs = bookBorrowLogRepository.findAll()

            checkBorrowLog(borrowLogs[0], borrowInfo, LogType.반납완료)
            assertThat(borrowLogs[0].borrowDate).isBefore(beforeBorrowTime)
            assertThat(borrowLogs[0].expireDate).isBefore(beforeBorrowTime.plusWeeks(2))
            assertThat(borrowLogs[0].rejectDate).isNull()
            assertThat(borrowLogs[0].returnDate).isAfter(beforeBorrowTime)
            assertThat(borrowLogs[0].returnDate).isBefore(LocalDateTime.now())
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
            borrowInfo.borrowStatus.type shouldBe 대출중
        }
    }

    private fun checkBorrowLog(borrowLog: BookBorrowLog, borrowInfo: BookBorrowInfo, logType: LogType) {
        assertThat(borrowLog.bookId).isEqualTo(borrowInfo.book.id)
        assertThat(borrowLog.bookTitle).isEqualTo(borrowInfo.book.title)
        assertThat(borrowLog.bookAuthor).isEqualTo(borrowInfo.book.author)
        assertThat(borrowLog.borrowId).isEqualTo(borrowInfo.id)
        assertThat(borrowLog.borrowStatus).isEqualTo(logType.name)
        assertThat(borrowLog.memberId).isEqualTo(borrowInfo.member.id)
        assertThat(borrowLog.memberRealName).isEqualTo(borrowInfo.member.realName)
    }

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    inner class `대출 현황 로그 조회` {

        private lateinit var borrowLogList: List<BookBorrowLog>

        @BeforeEach
        fun setBorrowInfo() {
            borrowLogList = listOf(
                borrowLogTestHelper.builder().borrowStatus(LogType.대출중.name).build(),
                borrowLogTestHelper.builder().rejectDate(LocalDateTime.now())
                    .borrowStatus(LogType.대출반려.name).build(),
                borrowLogTestHelper.builder().borrowStatus(LogType.반납대기.name).build(),
                borrowLogTestHelper.builder().returnDate(LocalDateTime.now())
                    .borrowStatus(LogType.반납완료.name).build(),
            )
        }

        @Test
        fun `유효한 요청이면 책 대출 현황 로그 조회는 성공해야 한다`() {
            val securedValue = getSecuredValue(BorrowManageController::class.java, "getBorrowLogs")
            callGetBorrowLogApi(
                params = multiValueMapOf(
                    "page" to "0",
                    "size" to "3"
                ),
            ).andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.content[0].borrowStatus").value("대출중"))
                .andExpect(jsonPath("$.content[1].borrowStatus").value("대출반려"))
                .andExpect(jsonPath("$.content[2].borrowStatus").value("반납대기"))
                .andExpect(jsonPath("$.number").value("0"))
                .andExpect(jsonPath("$.size").value("3"))
                .andExpect(jsonPath("$.totalPages").value("2"))
                .andDo(
                    document(
                        "get-borrow-logs",
                        requestCookies(
                            cookieWithName(JwtType.ACCESS_TOKEN.tokenName).description("ACCESS TOKEN ${securedValue}"),
                            cookieWithName(JwtType.REFRESH_TOKEN.tokenName).description("REFRESH TOKEN ${securedValue}")
                        ),
                        queryParameters(
                            parameterWithName("page").description("페이지 (양수여야 합니다.)")
                                .optional(),
                            parameterWithName("size").description("한 페이지당 불러올 개수 (default: ${DEFAULT_SIZE}) 최대: ${MAX_SIZE} 최소: ${MIN_SIZE}")
                                .optional(),
                            parameterWithName("search").description("검색 키워드. 도서명, 저자, 실명에서 검색해옵니다.")
                                .optional(),
                            parameterWithName("searchType")
                                .attributes(
                                    key("format").value(LogType.values().map(LogType::name).joinToString())
                                ).description("만약 null로 보낼 경우 대출 관련 정보를 모두 가져옵니다.")
                                .optional()
                        ),
                        responseFields(
                            *pageHelper(*getBorrowLogResponseDocs())
                        )
                    )
                )

            callGetBorrowLogApi(params = multiValueMapOf("page" to "1", "size" to "3"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.content[0].borrowStatus").value("반납완료"))
                .andExpect(jsonPath("$.number").value("1"))
                .andExpect(jsonPath("$.size").value("3"))
                .andExpect(jsonPath("$.totalPages").value("2"))
        }
    }
}
