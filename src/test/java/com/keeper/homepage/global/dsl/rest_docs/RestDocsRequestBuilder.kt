package com.keeper.homepage.global.dsl.rest_docs

import jakarta.servlet.http.Cookie
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.util.Assert
import java.net.URI

class RestDocsRequestBuilder {

    private val mockMvc: MockMvc
    private val method: DocsMethod
    private val url: String
    private val pathParams: Array<*>?

    constructor(mockMvc: MockMvc, method: DocsMethod, url: String, pathParams: Array<*>? = null) {
        this.method = method
        this.url = url
        this.mockMvc = mockMvc
        this.pathParams = pathParams
    }

    fun build(): MockHttpServletRequestBuilder? {
        val mockRequestBuilder = when (method) {
            DocsMethod.GET -> RestDocumentationRequestBuilders.get(url, pathParams)
            DocsMethod.PUT -> RestDocumentationRequestBuilders.put(url, pathParams)
            DocsMethod.POST -> RestDocumentationRequestBuilders.post(url, pathParams)
            DocsMethod.DELETE -> RestDocumentationRequestBuilders.delete(url, pathParams)
            DocsMethod.PATCH -> RestDocumentationRequestBuilders.patch(url, pathParams)
            DocsMethod.MULTIPART -> RestDocumentationRequestBuilders.multipart(url, pathParams)
        }

        return mockRequestBuilder
    }
}