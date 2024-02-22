package com.keeper.homepage.global.docs

import com.keeper.homepage.IntegrationTest
import org.junit.jupiter.api.Test
import org.springframework.http.HttpMethod

class keeperRestDocsTest : IntegrationTest() {

    @Test
    fun `test`() {
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

}