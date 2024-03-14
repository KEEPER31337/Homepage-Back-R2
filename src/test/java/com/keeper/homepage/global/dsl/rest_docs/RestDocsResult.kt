package com.keeper.homepage.global.dsl.rest_docs

import com.keeper.homepage.global.dsl.Field
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.snippet.Snippet
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.ResultMatcher

class RestDocsResult(
        private val resultActions: ResultActions,
) {
    private val results: MutableList<ResultMatcher> = mutableListOf()
    private val snippets: MutableList<Snippet> = mutableListOf()
    private val responseBodyFields: MutableList<Field> = mutableListOf()
    private val requestBodyFields: MutableList<Field> = mutableListOf()

    fun expect(result: ResultMatcher) {
        results.add(result)
    }

    fun responseBody(vararg field: Field) {
        requestBodyFields.addAll(field)
    }

    fun requestBody(vararg field: Field) {
        requestBodyFields.addAll(field)
    }

    fun generateDocs(): List<ResultMatcher> {
        val responseFieldDescriptors = responseBodyFields.takeIf { it.isNotEmpty() }?.map {
            fieldWithPath(it.fieldName).description(it.description)
        }

        val responseFieldsSnippet = responseFieldDescriptors?.let { PayloadDocumentation.responseFields(*it.toTypedArray()) }
        responseFieldsSnippet.apply { PayloadDocumentation.subsectionWithPath("pageable").description("페이지에 대한 부가 정보") }

        val snippets = mutableListOf<Snippet>().apply {
//            cookieFieldsSnippet?.let { add(it) }
//            pathParametersSnippet?.let { add(it) }
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