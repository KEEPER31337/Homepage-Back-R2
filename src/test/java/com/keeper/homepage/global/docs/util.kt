package com.keeper.homepage.global.docs

import com.keeper.homepage.IntegrationTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.queryParameters
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers
import org.springframework.stereotype.Component
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.filter.CharacterEncodingFilter

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
    restDocs.generateDocumentation()
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

        val pathParameterDescriptors = pathVariables.map { parameterWithName(it.fieldName).description(it.description) }
        val responseFieldDescriptors = responseFields.map { parameterWithName(it.fieldName).description(it.description) }

        val pathParametersSnippet = queryParameters(pathParameterDescriptors)
        val responseFieldsSnippet = queryParameters(responseFieldDescriptors)
        val path = pathVariables.map { it.fieldName }.toTypedArray()

        println(pathParametersSnippet.toString())
        println(responseFieldsSnippet.toString())
        println(path)

        mockMvc.perform(get("/merit/type"))
                .andExpect(MockMvcResultMatchers.status().isOk)
    }
}

infix fun String.means(description: String): Field {
    return Field(this, description)
}

