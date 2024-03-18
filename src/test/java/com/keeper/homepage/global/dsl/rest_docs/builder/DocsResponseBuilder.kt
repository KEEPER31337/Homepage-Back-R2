package com.keeper.homepage.global.dsl.rest_docs.builder

import com.keeper.homepage.global.dsl.means
import com.keeper.homepage.global.dsl.rest_docs.Documentation
import com.keeper.homepage.global.dsl.rest_docs.Field
import org.springframework.restdocs.cookies.CookieDocumentation
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.restdocs.snippet.Snippet
import org.springframework.test.web.servlet.ResultActions

class DocsResponseBuilder {

    private val responseFields: MutableList<Field> = mutableListOf()
    private val requestFields: MutableList<Field> = mutableListOf()
    private val cookieFields: MutableList<Field> = mutableListOf()
    private val pathFields: MutableList<Field> = mutableListOf()
    private var pageable: Boolean = false

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
        pageable = true
        fields.forEach { it.addContentString() }
        fields.forEach { println(it.fieldName) }
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

        val cookieFieldDescriptors = cookieFields.map { CookieDocumentation.cookieWithName(it.fieldName).description(it.description) }
        val pathParameterDescriptors = pathFields.map { RequestDocumentation.parameterWithName(it.fieldName).description(it.description) }
        val requestFieldDescriptors = requestFields.map { PayloadDocumentation.fieldWithPath(it.fieldName).description(it.description) }
        val responseFieldDescriptors = responseFields.map { PayloadDocumentation.fieldWithPath(it.fieldName).description(it.description) }.toMutableList()

        if (pageable) {
            responseFieldDescriptors.add(PayloadDocumentation.subsectionWithPath("pageable").description("페이지에 대한 부가 정보"))
        }

        val cookieFieldsSnippet = CookieDocumentation.requestCookies(*cookieFieldDescriptors.toTypedArray())
        val pathParametersSnippet = RequestDocumentation.pathParameters(*pathParameterDescriptors.toTypedArray())
        val requestFieldsSnippet = PayloadDocumentation.requestFields(*requestFieldDescriptors.toTypedArray())
        val responseFieldsSnippet = PayloadDocumentation.responseFields(*responseFieldDescriptors.toTypedArray())

        if (pageable) {
            responseFieldsSnippet.apply { PayloadDocumentation.subsectionWithPath("pageable").description("페이지에 대한 부가 정보") }
        }

        val snippets = mutableListOf<Snippet>().apply {
            if (pathParameterDescriptors.isNotEmpty()) { add(pathParametersSnippet) }
            if (cookieFieldDescriptors.isNotEmpty()) { add(cookieFieldsSnippet) }
            if (requestFieldDescriptors.isNotEmpty()) { add(requestFieldsSnippet) }
            if (responseFieldDescriptors.isNotEmpty()) { add(responseFieldsSnippet) }
        }

        mockMvc.andDo(
                MockMvcRestDocumentation.document(documentName, *snippets.toTypedArray())
        )
    }
}