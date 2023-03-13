package com.keeper.homepage.domain.library.api

import jakarta.servlet.http.Cookie
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import org.springframework.test.web.servlet.ResultActions
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap

const val BORROW_URL = "/manage/borrow-infos"

class BorrowManageApiTestHelper : BookManageApiTestHelper() {

    fun callGetBorrowRequestsApi(
        params: MultiValueMap<String, String> = LinkedMultiValueMap(),
        accessCookies: Array<Cookie> = bookManagerCookies,
    ): ResultActions = mockMvc.perform(
        get("${BORROW_URL}/requests")
            .params(params)
            .cookie(*accessCookies)
    )

    fun callApproveBorrowApi(
        borrowId: Long,
        accessCookies: Array<Cookie> = bookManagerCookies
    ): ResultActions = mockMvc.perform(
        post("${BORROW_URL}/{borrowId}/requests-approve", borrowId)
            .cookie(*accessCookies)
    )

    fun callDenyBorrowApi(
        borrowId: Long,
        accessCookies: Array<Cookie> = bookManagerCookies
    ): ResultActions = mockMvc.perform(
        post("${BORROW_URL}/{borrowId}/requests-deny", borrowId)
            .cookie(*accessCookies)
    )

    fun callApproveReturnApi(
        borrowId: Long,
        accessCookies: Array<Cookie> = bookManagerCookies
    ): ResultActions = mockMvc.perform(
        post("${BORROW_URL}/{borrowId}/return-approve", borrowId)
            .cookie(*accessCookies)
    )

    fun callDenyReturnApi(
        borrowId: Long,
        accessCookies: Array<Cookie> = bookManagerCookies
    ): ResultActions = mockMvc.perform(
        post("${BORROW_URL}/{borrowId}/return-deny", borrowId)
            .cookie(*accessCookies)
    )
}
