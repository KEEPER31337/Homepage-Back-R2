package com.keeper.homepage.global.docs

import com.keeper.homepage.IntegrationTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.http.HttpMethod
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

class keeperRestDocsTest : IntegrationTest() {

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    inner class `레스트 독스` {
        @Test
        fun `test`() {
            val memberId = "10"
            restDocs(HttpMethod.GET, "/") {
                pathVariable(
                        "memberId" means "회원의 ID 값",
                )
                requestBody("awarderId" means "수여자의 ID")
                responseBody("memberId" means "회원의 ID 값")
            }

            val mock = mockMvc.perform(get("/merit/type1"))
                    .andExpect(MockMvcResultMatchers.status().isOk)
            println(mock)
        }

    }

}