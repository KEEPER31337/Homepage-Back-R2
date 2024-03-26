package com.keeper.homepage.domain.calendar.api

import com.keeper.homepage.IntegrationTest
import com.keeper.homepage.domain.calendar.dto.request.SaveScheduleRequest
import com.keeper.homepage.domain.member.entity.Member
import com.keeper.homepage.domain.member.entity.job.MemberJob
import com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.*
import com.keeper.homepage.domain.merit.api.MeritController
import com.keeper.homepage.global.config.security.data.JwtType
import com.keeper.homepage.global.config.security.data.JwtType.*
import com.keeper.homepage.global.dsl.rest_docs.DocsMethod
import com.keeper.homepage.global.dsl.rest_docs.Documentation
import com.keeper.homepage.global.dsl.rest_docs.docs
import com.keeper.homepage.global.dsl.rest_docs.means
import com.keeper.homepage.global.restdocs.RestDocsHelper
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.result.StatusResultMatchers
import java.time.LocalDateTime

class ScheduleControllerTest: IntegrationTest() {

    private var member: Member? = null
    private var adminMember: Member? = null

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class SaveSchedule {

        @BeforeEach
        fun setUp() {
            member = memberTestHelper.generate()
            adminMember = memberTestHelper.generate().apply { assignJob(ROLE_회장) }
        }

        @Documentation("save-schedule")
        fun `일정 저장은 성공해야 한다`() {
            val securedValue = RestDocsHelper.getSecuredValue(ScheduleController::class.java, "saveSchedule")
            val saveScheduleRequest = SaveScheduleRequest(
                name = "일정 이름",
                startTime = LocalDateTime.now(),
                endTime = LocalDateTime.now().plusHours(1),
                scheduleTypeId = "1",
            )

            docs(mockMvc, DocsMethod.POST, "/schedule") {
                request {
                    cookie(*memberTestHelper.getTokenCookies(adminMember!!))
                    content(asJsonString(saveScheduleRequest))
                    contentType(MediaType.APPLICATION_JSON)
                }
                result { expect(status().isOk()) }
                response {
                    cookie(
                        ACCESS_TOKEN.tokenName means "ACCESS TOKEN $securedValue",
                        REFRESH_TOKEN.tokenName means "REFRESH TOKEN",
                    )
                    requestBody(
                        "name" means "일정의 이름",
                        "startTime" means "일정 시작 시간",
                        "endTime" means "일정 종료 시간",
                        "scheduleTypeId" means "일정 타입 ID",
                    )
                }
            }

            // when
        }

    }

}