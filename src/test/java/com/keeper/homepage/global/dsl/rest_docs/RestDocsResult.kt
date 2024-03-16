package com.keeper.homepage.global.dsl.rest_docs

import com.keeper.homepage.global.dsl.Field
import com.keeper.homepage.global.dsl.means
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
    private val results = mutableListOf<DocsResultBuilder>()
    private val responses = mutableListOf<DocsResponseBuilder>()
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

    fun result(init: DocsResultBuilder.() -> Unit) {
        val docsResultBuilder = DocsResultBuilder()
        docsResultBuilder.init()
        results.add(docsResultBuilder)
    }

    fun response(init: DocsResponseBuilder.() -> Unit) {
        val docsResponseBuilder = DocsResponseBuilder()
        docsResponseBuilder.init()
        responses.add(docsResponseBuilder)
    }

//    fun pathVariable(vararg field: Field) {
//        pathVariables.addAll(field)
//    }
//
//    fun cookie(vararg cookies: Cookie) {
//        cookieVariables.addAll(cookies)
//    }
//
//    fun cookieVariable(vararg fields: Field) {
//        cookieFields.addAll(fields)
//    }
//
//    fun requestJson(jsonBody: String) {
//        this.requestJson = jsonBody
//    }
//
//    fun responseBody(vararg field: Field) {
//        requestBodyFields.addAll(field)
//    }
//
//    fun requestBody(vararg field: Field) {
//        requestBodyFields.addAll(field)
//    }

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

//        results.forEach { resultActions.andExpect(it) }

        resultActions.andDo(
                document(
                        "",
                        *snippets.toTypedArray(),
                )
        )

//        return results
    }

}

class DocsResponseBuilder() {

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


}

class DocsResultBuilder() {

    private val resultMatcher: MutableList<ResultMatcher> = mutableListOf()

    fun expect(result: ResultMatcher) {
        resultMatcher.add(result)
    }

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
