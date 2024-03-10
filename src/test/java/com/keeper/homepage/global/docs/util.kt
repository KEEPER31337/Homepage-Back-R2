package com.keeper.homepage.global.docs

import com.keeper.homepage.IntegrationTest
import com.keeper.homepage.domain.library.api.field
import jakarta.servlet.http.Cookie
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpMethod
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName
import org.springframework.restdocs.cookies.CookieDocumentation.requestCookies
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.queryParameters
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers
import org.springframework.stereotype.Component
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.filter.CharacterEncodingFilter

fun restDocs(
        mockMvc: MockMvc,
        method: HttpMethod,
        uri: String,
        vararg pathParams: String,
        init: RestDocs.() -> Unit
) {
    val restDocs = RestDocs(
            mockMvc,
            method,
            uri,
            if (pathParams.isNotEmpty()) pathParams.toList() else null,
    )
    restDocs.init()
    restDocs.generateDocumentation()
}

class RestDocs(
        private val mockMvc: MockMvc,
        private val method: HttpMethod,
        private val uri: String,
        private val pathParams: List<String>?,
) {
    private val pathVariables = mutableListOf<Field>()
    private val cookieVariables = mutableListOf<Cookie>()
    private val cookieFields = mutableListOf<Field>()
    private val requestFields = mutableListOf<Field>()
    private val responseFields = mutableListOf<Field>()

    fun pathVariable(vararg fields: Field) {
        pathVariables.addAll(fields)
    }

    fun cookie(vararg cookies: Cookie) {
        cookieVariables.addAll(cookies)
    }

    fun cookieVariable(vararg fields: Field) {
        cookieFields.addAll(fields)
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
        val pathParameterDescriptors = pathVariables.map { parameterWithName(it.fieldName).description(it.description) }
        val responseFieldDescriptors = responseFields.map { fieldWithPath(it.fieldName).description(it.description) } + subsectionWithPath("pageable").description("페이지에 대한 부가 정보")

        val cookieFieldsSnippet = requestCookies(cookieDescriptors)
        val pathParametersSnippet = queryParameters(pathParameterDescriptors)
        val responseFieldsSnippet = responseFields(responseFieldDescriptors)
        responseFieldsSnippet.apply { subsectionWithPath("pageable").description("페이지에 대한 부가 정보") }
        val path = pathVariables.map { it.fieldName }.toTypedArray()

        mockMvc.perform(MockMvcRequestBuilders.request(method, uri)
                .apply { cookieVariables.forEach { cookie -> this.cookie(cookie) } })
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andDo(
                        document(
                                "merit",
                                cookieFieldsSnippet,
                                pathParametersSnippet,
                                responseFieldsSnippet,
                        )
                )
    }
}

infix fun String.means(description: String): Field {
    return Field(this, description)
}

