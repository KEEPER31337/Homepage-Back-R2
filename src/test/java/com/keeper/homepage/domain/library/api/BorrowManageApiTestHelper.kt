package com.keeper.homepage.domain.library.api

import jakarta.servlet.http.Cookie
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
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
}
