package com.keeper.homepage.domain.library.api

import com.keeper.homepage.IntegrationTest
import com.keeper.homepage.domain.member.entity.Member
import com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_사서
import com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_회원
import io.mockk.every
import jakarta.servlet.http.Cookie
import org.junit.jupiter.api.BeforeEach
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.test.web.servlet.ResultActions
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap

const val BOOK_URL = "/manage/books"

fun field(
    path: String,
    description: String
) = PayloadDocumentation.fieldWithPath(path)
    .description(description)

fun <K, V> multiValueMapOf(
    vararg pairs: Pair<K, V>
): MultiValueMap<K, V> = LinkedMultiValueMap<K, V>().apply {
    setAll(mapOf(*pairs))
}

class BookManageApiTestHelper : IntegrationTest() {

    private lateinit var bookManager: Member
    private lateinit var bookManagerCookies: Array<Cookie>

    @BeforeEach
    fun setUp() {
        bookManager = memberTestHelper.generate()
        bookManager.assignJob(ROLE_회원)
        bookManager.assignJob(ROLE_사서)
        bookManagerCookies = memberTestHelper.getTokenCookies(bookManager)
    }

    fun callAddBookApi(
        params: MultiValueMap<String, String?> = multiValueMapOf(
            "title" to "삶의 목적을 찾는 45가지 방법",
            "author" to "ChatGPT",
            "totalQuantity" to "10",
            "bookDepartment" to "document"
        ),
        accessCookies: Array<Cookie> = bookManagerCookies,
        hasThumbnail: Boolean = false,
        isMocking: Boolean = false,
    ): ResultActions {
        if (isMocking) {
            every { bookManageService.addBook(any(), any(), any(), any(), any()) } returns 1L
        }

        return if (hasThumbnail) {
            mockMvc.perform(
                multipart(BOOK_URL)
                    .file(thumbnailTestHelper.smallThumbnailFile)
                    .queryParams(params)
                    .cookie(*accessCookies)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
            )
        } else {
            mockMvc.perform(
                multipart(BOOK_URL)
                    .queryParams(params)
                    .cookie(*accessCookies)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
            )
        }
    }
}
