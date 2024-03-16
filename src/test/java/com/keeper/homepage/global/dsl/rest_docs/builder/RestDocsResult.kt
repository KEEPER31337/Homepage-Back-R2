package com.keeper.homepage.global.dsl.rest_docs.builder

import com.keeper.homepage.global.dsl.means
import com.keeper.homepage.global.dsl.rest_docs.Documentation
import com.keeper.homepage.global.dsl.rest_docs.Field
import jakarta.servlet.http.Cookie
import org.springframework.http.MediaType
import org.springframework.restdocs.cookies.CookieDocumentation.*
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.restdocs.snippet.Snippet
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.ResultMatcher
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.util.Assert

class RestDocsResult(
        private val mockMvc: MockMvc,
        private val resultActions: MockHttpServletRequestBuilder,
) {
    private lateinit var request: DocsRequestBuilder
    private lateinit var results : DocsResultBuilder
    private var responses : DocsResponseBuilder? = null

    fun request(init: DocsRequestBuilder.() -> Unit) {
        val docsRequestBuilder = DocsRequestBuilder()
        docsRequestBuilder.init()
        this.request = docsRequestBuilder
    }

    fun result(init: DocsResultBuilder.() -> Unit) {
        val docsResultBuilder = DocsResultBuilder()
        docsResultBuilder.init()
        results = docsResultBuilder
    }

    fun response(init: DocsResponseBuilder.() -> Unit) {
        val docsResponseBuilder = DocsResponseBuilder()
        docsResponseBuilder.init()
        this.responses = docsResponseBuilder
    }

    fun generateDocs() {
        val mock = request.build(mockMvc, resultActions)
        val build = results.build(mock)
        responses?.let { it.build(build) }
    }
}
