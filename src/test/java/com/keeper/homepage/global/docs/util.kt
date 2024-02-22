package com.keeper.homepage.global.docs

import com.keeper.homepage.IntegrationTest
import org.springframework.http.HttpMethod
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters

fun main() {
    val memberId: String = "10"
    restDocs(HttpMethod.GET, "/merits/members/{memberId}", memberId) {
        pathVariable(
                "memberId" means "회원의 ID 값",
                "memberRank" means "회원의 랭크"
        )
        requestBody("awarderId" means "수여자의 ID")
        responseBody("memberId" means "회원의 ID 값")
    }
}

fun restDocs(
        method: HttpMethod,
        uri: String,
        vararg pathParams: String,
        init: RestDocs.() -> Unit
) {
    val restDocs = RestDocs(
            method,
            uri,
            if (pathParams.isNotEmpty()) pathParams.toList() else null
    )
    restDocs.init()
    return restDocs.generateDocumentation()
}

class RestDocs(
        private val method: HttpMethod,
        private val uri: String,
        private val pathParams: List<String>?
) : IntegrationTest() {
    private val pathVariables = mutableListOf<Field>()
    private val requestFields = mutableListOf<Field>()
    private val responseFields = mutableListOf<Field>()

    fun pathVariable(vararg fields: Field) {
        pathVariables.addAll(fields)
    }

    fun requestBody(vararg fields: Field) {
        requestFields.addAll(fields)
    }

    fun responseBody(vararg fields: Field) {
        responseFields.addAll(fields)
    }

    fun generateDocumentation() {
        println("Method: $method, URI: $uri")
        pathParams?.let { println("Path paramters: ${it}") }
        pathVariables.forEach { println("Path variable: ${it.fieldName} - ${it.description}") }
        requestFields.forEach { println("Request body: ${it.fieldName} - ${it.description}") }
        requestFields.forEach { println("Response body: ${it.fieldName} - ${it.description}") }

        val pathParametersSnippet = pathParameters(*pathVariables.map { parameterWithName(it.fieldName).description(it.description) }.toTypedArray())
        val responseFieldsSnippet = responseFields(*responseFields.map { fieldWithPath(it.fieldName).description(it.description) }.toTypedArray())

        mockMvc.perform(get(uri, *pathVariables.map { it.fieldName }.toTypedArray()))
                .andDo(document("dsl Test", pathParametersSnippet, responseFieldsSnippet))
    }
}

infix fun String.means(description: String): Field {
    return Field(this, description)
}

