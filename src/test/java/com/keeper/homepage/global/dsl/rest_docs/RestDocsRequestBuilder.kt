package com.keeper.homepage.global.dsl.rest_docs

import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.AbstractMockHttpServletRequestBuilder

class RestDocsRequestBuilder(
        private val mockMvc: MockMvc,
        private val method: DocsMethod,
        private val url: String,
        private val pathParams: Array<out String> = emptyArray()
) {

    fun build(): AbstractMockHttpServletRequestBuilder<*> {
        val mockRequestBuilder: AbstractMockHttpServletRequestBuilder<*> = when (method) {
            DocsMethod.GET -> RestDocumentationRequestBuilders.get(url, *pathParams)
            DocsMethod.PUT -> RestDocumentationRequestBuilders.put(url, *pathParams)
            DocsMethod.POST -> RestDocumentationRequestBuilders.post(url, *pathParams)
            DocsMethod.DELETE -> RestDocumentationRequestBuilders.delete(url, *pathParams)
            DocsMethod.PATCH -> RestDocumentationRequestBuilders.patch(url, *pathParams)
            DocsMethod.MULTIPART -> RestDocumentationRequestBuilders.multipart(url, *pathParams)
        }

        return mockRequestBuilder
    }
}