package com.keeper.homepage.global.docs

import jakarta.servlet.http.Cookie
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName
import org.springframework.restdocs.cookies.CookieDocumentation.requestCookies
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.queryParameters
import org.springframework.restdocs.snippet.Snippet
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultMatcher
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

fun restDocs(
        mockMvc: MockMvc,
        httpMethod: HttpMethod,
        uri: String,
        vararg pathParams: String,
        init: RestDocs.() -> Unit
) {

    val currentStackTraceElement = Thread.currentThread().stackTrace.firstOrNull { it.fileName?.endsWith("Test.kt") == true }
    val className = currentStackTraceElement?.className
    val methodName = currentStackTraceElement?.methodName

    val method = Class.forName(className).methods.firstOrNull { it.name == methodName }
    val documentName = method?.getAnnotation(Documentation::class.java)?.documentName

    val restDocs = RestDocs(
            mockMvc,
            documentName,
            httpMethod,
            uri,
            if (pathParams.isNotEmpty()) pathParams.toList() else null,
    )
    restDocs.init()
    restDocs.generateDocumentation()
}

class RestDocs(
        private val mockMvc: MockMvc,
        private val documentName: String?,
        private val method: HttpMethod,
        private val uri: String,
        private val pathParams: List<String>?,
) {
    private lateinit var httpStatus: ResultMatcher
    private lateinit var contentType: ResultMatcher
    private var requestJson: String? = null
    private val pathVariables = mutableListOf<Field>()
    private val cookieVariables = mutableListOf<Cookie>()
    private val cookieFields = mutableListOf<Field>()
    private val requestFields = mutableListOf<Field>()
    private val responseFields = mutableListOf<Field>()

    fun expect(httpStatus: HttpStatus, contentType: MediaType) {
        this.httpStatus = when (httpStatus) { // 추가와 구조 변경
            HttpStatus.OK -> MockMvcResultMatchers.status().isOk()
            HttpStatus.FORBIDDEN -> MockMvcResultMatchers.status().isForbidden()
            HttpStatus.CREATED -> MockMvcResultMatchers.status().isCreated()
            else -> throw IllegalArgumentException()
        }

        this.contentType = when (contentType) { // 추가와 구조 변경
            MediaType.APPLICATION_JSON -> MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)
            MediaType.APPLICATION_JSON_UTF8 -> MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8)
            else -> throw IllegalArgumentException()
        }
    }

    fun pathVariable(vararg fields: Field) {
        pathVariables.addAll(fields)
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

    fun generateDocumentation() {
        val cookieDescriptors = cookieFields.map { cookieWithName(it.fieldName).description(it.description) }
        val pathParameterDescriptors = pathVariables.takeIf { it.isNotEmpty() }?.map { parameterWithName(it.fieldName).description(it.description) }
        val responseFieldDescriptors = responseFields.takeIf { it.isNotEmpty() }?.map {
            if (it.isOptional == true) {
                fieldWithPath(it.fieldName).description(it.description).optional()
            } else {
                fieldWithPath(it.fieldName).description(it.description)
            }
        }?.plus(subsectionWithPath("pageable").description("페이지에 대한 부가 정보"))

        val cookieFieldsSnippet = requestCookies(cookieDescriptors)
        val pathParametersSnippet = pathParameterDescriptors?.let { queryParameters(it) }
        val responseFieldsSnippet = responseFieldDescriptors?.let { responseFields(*it.toTypedArray()) }

        responseFieldsSnippet.apply { subsectionWithPath("pageable").description("페이지에 대한 부가 정보") }
        val path = pathVariables.map { it.fieldName }.toTypedArray()

        val result = mockMvc.perform(MockMvcRequestBuilders.request(method, uri)
                .contentType(MediaType.APPLICATION_JSON)
                .apply {
                    cookieVariables.forEach { cookie -> this.cookie(cookie) }
                    requestJson?.let {
                        this.content(it)
                    }
                })

        httpStatus?.let { result.andExpect(it) }
//        contentType?.let { result.andExpect(it) }

        val snippets = mutableListOf<Snippet>().apply {
            cookieFieldsSnippet?.let { add(it) }
            pathParametersSnippet?.let { add(it) }
            responseFieldsSnippet?.let { add(it) }
        }

        result.andDo(
                document(
                        documentName,
                        *snippets.toTypedArray()
                )
        )
    }
}

infix fun String.means(description: String): Field {
    return Field(this, description, false)
}

infix fun Field.isOptional(isOptional: Boolean): Field {
    return Field(this, isOptional)
}

