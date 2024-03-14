package com.keeper.homepage.global.dsl.rest_docs

import jakarta.servlet.http.Cookie
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.util.Assert
import java.net.URI

class RestDocsRequestBuilder {

    private val mockMvc: MockMvc
    private val method: DocsMethod
    private val url: String
    private val cookies: MutableList<Cookie>
    private var content: String? = null
    private var contentType: String? = null

    constructor(mockMvc: MockMvc, method: DocsMethod, url: String) {
        this.method = method
        this.url = url
        this.cookies = mutableListOf()
        this.mockMvc = mockMvc
    }

    fun content(content: String): RestDocsRequestBuilder {
        this.content = content
        return this
    }

    fun contentType(contentType: String): RestDocsRequestBuilder {
        this.contentType = contentType
        return this
    }

    fun cookie(vararg cookies: Cookie?): RestDocsRequestBuilder {
        Assert.notNull(cookies, "Cookies must not be null")
        cookies.filterNotNull().forEach { this.cookies.add(it) }
        return this
    }

    fun build(): ResultActions {
        val mockRequestBuilder = when (method) {
            DocsMethod.GET -> RestDocumentationRequestBuilders.get(URI.create(url))
            DocsMethod.PUT -> RestDocumentationRequestBuilders.put(URI.create(url))
            DocsMethod.POST -> RestDocumentationRequestBuilders.post(URI.create(url))
            DocsMethod.DELETE -> RestDocumentationRequestBuilders.delete(URI.create(url))
            DocsMethod.PATCH -> RestDocumentationRequestBuilders.patch(URI.create(url))
            DocsMethod.MULTIPART -> RestDocumentationRequestBuilders.multipart(URI.create(url))
        }

        content?.let { mockRequestBuilder.content(it) }
        contentType?.let { mockRequestBuilder.contentType(it) }

        return mockMvc.perform(mockRequestBuilder)
    }
}