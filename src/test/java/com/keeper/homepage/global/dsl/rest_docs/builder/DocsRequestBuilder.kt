package com.keeper.homepage.global.dsl.rest_docs.builder

import jakarta.servlet.http.Cookie
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.util.Assert

class DocsRequestBuilder {

    private val cookies: MutableList<Cookie> = mutableListOf()
    private var content: String? = null
    private var contentType: String? = null
    private var paramMap: MutableMap<String, String> = mutableMapOf()

    fun content(content: String) {
        this.content = content
    }

    fun contentType(contentType: MediaType) {
        this.contentType = contentType.toString()
    }

    fun cookie(vararg cookies: Cookie?) {
        Assert.notNull(cookies, "Cookies must not be null")
        cookies.filterNotNull().forEach { this.cookies.add(it) }
    }

    fun param(name: String, value: String) {
        paramMap[name] = value
    }

    fun build(mockMvc: MockMvc, resultActions: MockHttpServletRequestBuilder): ResultActions {
        cookies.forEach { resultActions.cookie(it) }
        paramMap.forEach { (name, value) -> resultActions.param(name, value) }
        content?.let { resultActions.content(it) }
        contentType?.let { resultActions.contentType(it) }
        return mockMvc.perform(resultActions)
    }

}