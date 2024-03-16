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

    constructor(mockMvc: MockMvc, method: DocsMethod, url: String) {
        this.method = method
        this.url = url
        this.mockMvc = mockMvc
    }

    fun build(): MockHttpServletRequestBuilder? {
        val mockRequestBuilder = when (method) {
            DocsMethod.GET -> RestDocumentationRequestBuilders.get(URI.create(url))
            DocsMethod.PUT -> RestDocumentationRequestBuilders.put(URI.create(url))
            DocsMethod.POST -> RestDocumentationRequestBuilders.post(URI.create(url))
            DocsMethod.DELETE -> RestDocumentationRequestBuilders.delete(URI.create(url))
            DocsMethod.PATCH -> RestDocumentationRequestBuilders.patch(URI.create(url))
            DocsMethod.MULTIPART -> RestDocumentationRequestBuilders.multipart(URI.create(url))
        }

        return mockRequestBuilder
    }
}