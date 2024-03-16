package com.keeper.homepage.global.dsl.rest_docs

import com.keeper.homepage.global.dsl.Documentation
import com.keeper.homepage.global.dsl.Field
import com.keeper.homepage.global.dsl.means
import jakarta.servlet.http.Cookie
import org.springframework.restdocs.cookies.CookieDocumentation
import org.springframework.restdocs.cookies.CookieDocumentation.*
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.restdocs.snippet.Snippet
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.ResultMatcher
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.util.Assert

class RestDocsResult(
        private val mockMvc: MockMvc,
        private val resultActions: MockHttpServletRequestBuilder,
) {
    private lateinit var request: DocsRequestBuilder
    private lateinit var results : DocsResultBuilder
    private var responses : DocsResponseBuilder? = null
    private var documentName: String? = null

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

class DocsResponseBuilder {

    private val responseFields: MutableList<Field> = mutableListOf()
    private val requestFields: MutableList<Field> = mutableListOf()
    private val cookieFields: MutableList<Field> = mutableListOf()
    private val pathFields: MutableList<Field> = mutableListOf()

    fun path(vararg fields: Field) {
        pathFields.addAll(fields)
    }

    fun cookie(vararg cookies: Field) {
        cookieFields.addAll(cookies)
    }

    fun requestBody(vararg fields: Field) {
        requestFields.addAll(fields)
    }

    fun responseBody(vararg fields: Field) {
        responseFields.addAll(fields)
    }

    fun responseBodyWithPaging(vararg fields: Field) {
        responseFields.addAll(fields)
        responseFields.add("empty" means "가져오는 페이지가 비어 있는 지")
        responseFields.add("first" means "가져오는 페이지가 비어 있는 지")
        responseFields.add("last" means "가져오는 페이지가 비어 있는 지")
        responseFields.add("number" means "가져오는 페이지가 비어 있는 지")
        responseFields.add("numberOfElements" means "가져오는 페이지가 비어 있는 지")
        responseFields.add("sort.empty" means "가져오는 페이지가 비어 있는 지")
        responseFields.add("sort.sorted" means "가져오는 페이지가 비어 있는 지")
        responseFields.add("sort.unsorted" means "가져오는 페이지가 비어 있는 지")
        responseFields.add("totalPages" means "가져오는 페이지가 비어 있는 지")
        responseFields.add("totalElements" means "가져오는 페이지가 비어 있는 지")
        responseFields.add("size" means "가져오는 페이지가 비어 있는 지")
    }

    fun build(mockMvc: ResultActions) {
        val currentStackTraceElement = Thread.currentThread().stackTrace.firstOrNull { it.fileName?.endsWith("Test.kt") == true }
        val className = currentStackTraceElement?.className
        val methodName = currentStackTraceElement?.methodName

        val method = Class.forName(className).methods.firstOrNull { it.name == methodName }
        val documentName = method?.getAnnotation(Documentation::class.java)?.documentName

        val cookieDescriptors = cookieFields.map { cookieWithName(it.fieldName).description(it.description) }
        val pathParameterDescriptors = pathFields.map { parameterWithName(it.fieldName).description(it.description) }
        val responseFieldDescriptors = responseFields.map { fieldWithPath(it.fieldName).description(it.description) }

        val cookieFieldsSnippet = requestCookies(cookieDescriptors)
        val pathParametersSnippet = pathParameterDescriptors?.let { pathParameters(it) }
        val responseFieldsSnippet = responseFieldDescriptors?.let { responseFields(*it.toTypedArray()) }

        val snippets = mutableListOf<Snippet>().apply {
            cookieFieldsSnippet?.let { add(it) }
            pathParametersSnippet?.let { add(it) }
            responseFieldsSnippet?.let { add(it) }
        }

        mockMvc.andDo(
                document(documentName, *snippets.toTypedArray())
        )

    }

}

class DocsResultBuilder() {

    private val resultMatcher: MutableList<ResultMatcher> = mutableListOf()

    fun expect(result: ResultMatcher) {
        resultMatcher.add(result)
    }

    fun build(mockMvc: ResultActions): ResultActions {
        resultMatcher.forEach { mockMvc.andExpect(it) }
        return mockMvc
    }

}

class DocsRequestBuilder {

    private val cookies: MutableList<Cookie> = mutableListOf()
    private var content: String? = null
    private var contentType: String? = null

    fun content(content: String) {
        this.content = content
    }

    fun contentType(contentType: String) {
        this.contentType = contentType
    }

    fun cookie(vararg cookies: Cookie?) {
        Assert.notNull(cookies, "Cookies must not be null")
        cookies.filterNotNull().forEach { this.cookies.add(it) }
    }

    fun build(mockMvc: MockMvc, resultActions: MockHttpServletRequestBuilder): ResultActions {
        cookies.forEach { resultActions.cookie(it) }
        content?.let { resultActions.content(it) }
        contentType?.let { resultActions.contentType(it) }
        return mockMvc.perform(resultActions)
    }

}
