package com.keeper.homepage.global.dsl.rest_docs

import org.springframework.test.web.servlet.MockMvc

fun docs(
        mockMvc: MockMvc,
        method: DocsMethod,
        url: String,
        init: RestDocsResult.() -> Unit
): RestDocsRequestBuilder {
    val restDocsRequestBuilder = RestDocsRequestBuilder(mockMvc, method, url)
    val requestBuilder = restDocsRequestBuilder.build()!!

    val restDocsResult = RestDocsResult(mockMvc, requestBuilder)
    restDocsResult.init()
    restDocsResult.generateDocs()

    return restDocsRequestBuilder
}