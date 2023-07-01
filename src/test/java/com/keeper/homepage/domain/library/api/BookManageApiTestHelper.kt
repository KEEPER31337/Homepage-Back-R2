package com.keeper.homepage.domain.library.api

import com.keeper.homepage.IntegrationTest
import com.keeper.homepage.domain.library.BookBorrowInfoTestHelper
import com.keeper.homepage.domain.library.BookTestHelper
import com.keeper.homepage.domain.library.dto.req.ModifyBookRequest
import com.keeper.homepage.domain.library.dto.resp.RESPONSE_DATETIME_FORMAT
import com.keeper.homepage.domain.library.entity.Book
import com.keeper.homepage.domain.library.entity.BookBorrowInfo
import com.keeper.homepage.domain.library.entity.BookBorrowStatus
import com.keeper.homepage.domain.library.entity.BookDepartment.BookDepartmentType
import com.keeper.homepage.domain.member.entity.Member
import com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_사서
import com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_회원
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import jakarta.servlet.http.Cookie
import org.junit.jupiter.api.BeforeEach
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.test.web.servlet.ResultActions
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import java.time.LocalDateTime

const val BOOK_URL = "/manage/books"

fun field(
    path: String,
    description: String,
    optional: Boolean = false
) = if (optional) {
    PayloadDocumentation.fieldWithPath(path)
        .description(description)
        .optional()
} else {
    PayloadDocumentation.fieldWithPath(path)
        .description(description)
}

fun pageHelper(vararg fieldDescriptors: FieldDescriptor): Array<FieldDescriptor> =
    arrayOf(
        *listHelper("content", *fieldDescriptors),
        field("empty", "가져오는 페이지가 비어 있는 지"),
        field("first", "첫 페이지인지"),
        field("last", "마지막 페이지인지"),
        field("number", "페이지 number (0부터 시작)"),
        field("numberOfElements", "현재 페이지의 데이터 개수"),
        PayloadDocumentation.subsectionWithPath("pageable").description("페이지에 대한 부가 정보"),
        field("sort.empty", "정렬 기준이 비어 있는 지"),
        field("sort.sorted", "정렬이 되었는지"),
        field("sort.unsorted", "정렬이 되지 않았는지"),
        field("totalPages", "총 페이지 수"),
        field("totalElements", "총 페이지 수"),
        field("size", "한 페이지당 데이터 개수")
    )

fun listHelper(objectName: String, vararg fieldDescriptors: FieldDescriptor): Array<FieldDescriptor> =
    fieldDescriptors.map { field("${objectName}[].${it.path}", it.description.toString(), it.isOptional) }
        .toTypedArray()

fun <K, V> multiValueMapOf(
    vararg pairs: Pair<K, V>
): MultiValueMap<K, V> = LinkedMultiValueMap<K, V>().apply {
    setAll(mapOf(*pairs))
}

fun BookBorrowInfoTestHelper.generate(
    borrowStatus: BookBorrowStatus.BookBorrowStatusType,
    expiredDate: LocalDateTime = LocalDateTime.now().plusWeeks(2),
    book: Book = BookTestHelper().generate(),
): BookBorrowInfo {
    return this.builder()
        .book(book)
        .borrowStatus(BookBorrowStatus.getBookBorrowStatusBy(borrowStatus))
        .expireDate(expiredDate)
        .build()
}

class BookManageApiTestHelper : IntegrationTest() {

    lateinit var bookManager: Member
    lateinit var bookManagerCookies: Array<Cookie>

    @BeforeEach
    fun setUp() {
        bookManager = memberTestHelper.generate()
        bookManager.assignJob(ROLE_회원)
        bookManager.assignJob(ROLE_사서)
        bookManagerCookies = memberTestHelper.getTokenCookies(bookManager)
    }

    fun callGetBooksApi(
        params: MultiValueMap<String, String> = multiValueMapOf(
            "bookKeyword" to "",
            "page" to "0",
            "size" to "10",
        ),
        accessCookies: Array<Cookie> = bookManagerCookies,
    ): ResultActions {
        return mockMvc.perform(
            get(BOOK_URL)
                .queryParams(params)
                .cookie(*accessCookies)
        )
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

    fun callDeleteBookApi(
        accessCookies: Array<Cookie> = bookManagerCookies,
        isMocking: Boolean = false,
    ): ResultActions {
        var bookId = 0L
        if (isMocking) {
            every { bookManageService.deleteBook(any()) } just Runs
        } else {
            bookId = bookManageService.addBook(
                "삶의 목적을 찾는 45가지 방법",
                "ChatGPT",
                10,
                BookDepartmentType.DOCUMENT,
                null
            )
        }

        return mockMvc.perform(
            delete("${BOOK_URL}/{bookId}", bookId)
                .cookie(*accessCookies)
        )
    }

    fun callModifyBookApi(
        content: ModifyBookRequest = ModifyBookRequest(
            title = "삶의 목적을 찾는 45가지 방법",
            author = "ChatGPT",
            totalQuantity = 10,
            bookDepartment = BookDepartmentType.DOCUMENT
        ),
        bookId: Long,
        accessCookies: Array<Cookie> = bookManagerCookies,
    ): ResultActions =
        mockMvc.perform(
            put("${BOOK_URL}/{bookId}", bookId)
                .content(asJsonString(content))
                .cookie(*accessCookies)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )

    fun callModifyBookThumbnailApi(bookId: Long): ResultActions =
        mockMvc.perform(
            multipart("${BOOK_URL}/{bookId}/thumbnail", bookId)
                .file(thumbnailTestHelper.smallThumbnailFile)
                .with { request ->
                    request.method = "PATCH"
                    request
                }
                .cookie(*bookManagerCookies)
                .contentType(MediaType.MULTIPART_FORM_DATA)
        )

    fun callGetBookDetailApi(
        bookId: Long,
        accessCookies: Array<Cookie> = bookManagerCookies,
    ): ResultActions = mockMvc.perform(
        get("${BOOK_URL}/{bookId}", bookId)
            .cookie(*accessCookies)
    )

    fun getBorrowDetailResponseDocs(): Array<FieldDescriptor> {
        return arrayOf(
            field("borrowInfoId", "대출 정보 ID"),
            field("bookId", "대출할 책의 ID"),
            field("bookTitle", "대출할 책의 제목"),
            field("author", "대출할 책의 저자"),
            field("borrowerId", "대출자의 ID"),
            field("borrowerNickname", "대출자의 닉네임"),
            field("requestDatetime", "대출 요청을 한 시간 (양식: $RESPONSE_DATETIME_FORMAT)"),
            field("borrowDateTime", "대출 승인을 한 시간 (양식: $RESPONSE_DATETIME_FORMAT)"),
            field("expiredDateTime", "반납 예정 시간 (양식: $RESPONSE_DATETIME_FORMAT)"),
            field(
                "status", "대출의 현재 상태\r\n\r\n${BookBorrowStatus.BookBorrowStatusType.getAllList()}"
            ),
        )
    }

    fun getBookDetailResponseDocs(): Array<FieldDescriptor> {
        return arrayOf(
            field("id", "책의 ID"),
            field("title", "책의 제목"),
            field("author", "책의 저자"),
            field("bookDepartment", "책 카테고리"),
            field("totalCount", "책의 총 권 수"),
            field("borrowingCount", "현재 대출중인 책 수"),
            field("thumbnailPath", "썸네일 URL"),
            *listHelper("borrowInfos", *getBorrowDetailResponseDocs()),
        )
    }
}
