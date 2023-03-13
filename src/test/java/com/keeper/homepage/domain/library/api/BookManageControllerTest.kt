package com.keeper.homepage.domain.library.api

import com.keeper.homepage.domain.library.dto.req.MAX_AUTHOR_LENGTH
import com.keeper.homepage.domain.library.dto.req.MAX_TITLE_LENGTH
import com.keeper.homepage.domain.library.dto.req.MAX_TOTAL_QUANTITY_LENGTH
import com.keeper.homepage.domain.library.entity.BookDepartment.BookDepartmentType
import com.keeper.homepage.domain.member.entity.job.MemberJob
import com.keeper.homepage.global.config.security.data.JwtType.ACCESS_TOKEN
import com.keeper.homepage.global.config.security.data.JwtType.REFRESH_TOKEN
import com.keeper.homepage.global.restdocs.RestDocsHelper.getSecuredValue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.http.HttpHeaders
import org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName
import org.springframework.restdocs.cookies.CookieDocumentation.requestCookies
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.request.RequestDocumentation.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.util.LinkedMultiValueMap

class BookManageControllerTest : BookManageApiTestHelper() {

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    inner class `책 추가` {

        private val validParams = multiValueMapOf(
            "title" to "삶의 목적을 찾는 45가지 방법",
            "author" to "ChatGPT",
            "totalQuantity" to "10",
            "bookDepartment" to "document"
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
                        queryParameters(
                            parameterWithName("title").description("책 제목 (최대 ${MAX_TITLE_LENGTH}자)"),
                            parameterWithName("author").description("저자 (최대 ${MAX_AUTHOR_LENGTH}자)"),
                            parameterWithName("bookDepartment").description("${BookDepartmentType.values()}"),
                            parameterWithName("totalQuantity").description("책 수량 (1권 이상 ${MAX_TOTAL_QUANTITY_LENGTH}권 이하)"),
                        ),
                        requestParts(
                            partWithName("thumbnail").description("게시글의 썸네일")
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
        @CsvSource(
            "title, ''", "title, ",
            "author, ''", "author, ",
            "totalQuantity, -1", "totalQuantity, 0", "totalQuantity, 21",
            "bookDepartment, ''", "bookDepartment, ", "bookDepartment, NONE",
        )
        fun `유효하지 않은 요청의 책 등록은 실패해야 한다`(key: String, invalidValue: String?) {
            val invalidParams = LinkedMultiValueMap(validParams)
            invalidParams.replace(key, listOf(invalidValue))
            callAddBookApi(params = invalidParams)
                .andExpect(status().isBadRequest)
        }

        @Test
        fun `존재하지 않는 책 종류의 책 등록은 실패해야 한다`() {
            val invalidParams = LinkedMultiValueMap(validParams)
            invalidParams.replace("bookDepartment", listOf("GUSAH"))
            callAddBookApi(params = invalidParams)
                .andExpect(status().isBadRequest)
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
}
