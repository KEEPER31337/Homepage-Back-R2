package com.keeper.homepage.global.dsl.rest_docs

import jakarta.servlet.http.Cookie
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.AbstractMockHttpServletRequestBuilder
import org.springframework.util.Assert
import java.net.URI

class RestDocsRequestBuilder(
        private val mockMvc: MockMvc,
        private val method: DocsMethod,
        private val url: String,
        private val pathParams: Array<*>? = null
) {

    fun build(): AbstractMockHttpServletRequestBuilder<*> {
        val uriVars: Array<Any> = pathParams?.filterNotNull()?.toTypedArray() ?: emptyArray()

        val mockRequestBuilder: AbstractMockHttpServletRequestBuilder<*> = when (method) {
            DocsMethod.GET -> RestDocumentationRequestBuilders.get(url, *uriVars)
            DocsMethod.PUT -> RestDocumentationRequestBuilders.put(url, *uriVars)
            DocsMethod.POST -> RestDocumentationRequestBuilders.post(url, *uriVars)
            DocsMethod.DELETE -> RestDocumentationRequestBuilders.delete(url, *uriVars)
            DocsMethod.PATCH -> RestDocumentationRequestBuilders.patch(url, *uriVars)
            DocsMethod.MULTIPART -> RestDocumentationRequestBuilders.multipart(url, *uriVars)
        }

        return mockRequestBuilder
    }
}