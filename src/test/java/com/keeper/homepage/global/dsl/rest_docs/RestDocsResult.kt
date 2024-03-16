package com.keeper.homepage.global.dsl.rest_docs

import com.keeper.homepage.global.dsl.Field
import jakarta.servlet.http.Cookie
import org.springframework.restdocs.cookies.CookieDocumentation
import org.springframework.restdocs.cookies.CookieDocumentation.*
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.restdocs.snippet.Snippet
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.ResultMatcher
import org.springframework.util.Assert

class RestDocsResult(
        private val resultActions: ResultActions,
) {
    private val requests = mutableListOf<DocsRequestBuilder>()
    private var documentName: String? = null
    private var requestJson: String? = null
//    private val results: MutableList<ResultMatcher> = mutableListOf()
    private val pathVariables: MutableList<Field> = mutableListOf()
    private val cookieVariables: MutableList<Cookie> = mutableListOf()
    private val cookieFields: MutableList<Field> = mutableListOf()
    private val snippets: MutableList<Snippet> = mutableListOf()
    private val responseBodyFields: MutableList<Field> = mutableListOf()
    private val requestBodyFields: MutableList<Field> = mutableListOf()

    fun request(init: DocsRequestBuilder.() -> Unit) {
        val docsRequestBuilder = DocsRequestBuilder(resultActions)
        docsRequestBuilder.init()
        requests.add(docsRequestBuilder)
    }

    fun result(init: RestDocsResult.() -> Unit) {
        init()
    }

    fun expect(result: ResultMatcher) {
        results.add(result)
    }

    fun pathVariable(vararg field: Field) {
        pathVariables.addAll(field)
    }

    fun cookie(vararg cookies: Cookie) {
        cookieVariables.addAll(cookies)
    }

    fun cookieVariable(vararg fields: Field) {
        cookieFields.addAll(fields)
    }

    fun requestJson(jsonBody: String) {
        this.requestJson = jsonBody
    }

    fun responseBody(vararg field: Field) {
        requestBodyFields.addAll(field)
    }

    fun requestBody(vararg field: Field) {
        requestBodyFields.addAll(field)
    }

    fun generateDocs(): List<ResultMatcher> {
        val cookieDescriptors = cookieFields.map { cookieWithName(it.fieldName).description(it.description) }
        val pathParameterDescriptors = pathVariables.takeIf { it.isNotEmpty() }?.map { RequestDocumentation.parameterWithName(it.fieldName).description(it.description) }
        val responseFieldDescriptors = responseBodyFields.takeIf { it.isNotEmpty() }?.map {
            fieldWithPath(it.fieldName).description(it.description)
        }?.plus(subsectionWithPath("pageable").description("페이지에 대한 부가 정보"))

        val cookieFieldsSnippet = requestCookies(cookieDescriptors)
        val pathParametersSnippet = pathParameterDescriptors?.let { RequestDocumentation.pathParameters(it) }
        val responseFieldsSnippet = responseFieldDescriptors?.let { responseFields(*it.toTypedArray()) }
        responseFieldsSnippet.apply { subsectionWithPath("pageable").description("페이지에 대한 부가 정보") }

        val snippets = mutableListOf<Snippet>().apply {
            cookieFieldsSnippet?.let { add(it) }
            pathParametersSnippet?.let { add(it) }
            responseFieldsSnippet?.let { add(it) }
        }

        results.forEach { resultActions.andExpect(it) }

        resultActions.andDo(
                document(
                        "",
                        *snippets.toTypedArray(),
                )
        )

        return results
    }

}

class DocsResultBuilder() {

}

class DocsRequestBuilder(
        private val resultActions: ResultActions,
) {

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

}
