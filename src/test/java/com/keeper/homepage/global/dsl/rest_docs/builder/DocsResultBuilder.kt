package com.keeper.homepage.global.dsl.rest_docs.builder

import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.ResultMatcher

class DocsResultBuilder {

    private val resultMatcher: MutableList<ResultMatcher> = mutableListOf()

    fun expect(result: ResultMatcher) {
        resultMatcher.add(result)
    }

    fun build(mockMvc: ResultActions): ResultActions {
        resultMatcher.forEach { mockMvc.andExpect(it) }
        return mockMvc
    }

}