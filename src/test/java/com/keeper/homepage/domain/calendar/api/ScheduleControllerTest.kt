package com.keeper.homepage.domain.calendar.api

import com.keeper.homepage.IntegrationTest
import com.keeper.homepage.domain.calendar.dto.request.FindScheduleRequest
import com.keeper.homepage.domain.calendar.dto.request.SaveScheduleRequest
import com.keeper.homepage.domain.member.entity.Member
import com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.*
import com.keeper.homepage.global.config.security.data.JwtType.*
import com.keeper.homepage.global.dsl.rest_docs.DocsMethod
import com.keeper.homepage.global.dsl.rest_docs.Documentation
import com.keeper.homepage.global.dsl.rest_docs.docs
import com.keeper.homepage.global.dsl.rest_docs.means
import com.keeper.homepage.global.error.BusinessException
import com.keeper.homepage.global.error.ErrorCode
import com.keeper.homepage.global.restdocs.RestDocsHelper
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.ResultMatcher
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.result.StatusResultMatchers
import java.time.LocalDateTime

class ScheduleControllerTest: IntegrationTest() {

    companion object {
        const val DEFAULT_SCHEDULE_NAME = "Default Name"
        const val DEFAULT_SCHEDULE_TYPE = "일반"
        const val DEFAULT_SCHEDULE_TYPE_DESCRIPTION = "정기 세미나, 기술문서 발표, 1학기 시작/종료 등"
    }

    private var member: Member? = null
    private var adminMember: Member? = null

    @BeforeEach
    fun setUp() {
        member = memberTestHelper.generate()
        adminMember = memberTestHelper.generate().apply { assignJob(ROLE_회장) }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class SaveSchedule {

        @Test
        fun `캘린더 일정 종료 시간이 시간 시간보다 빠르면 안된다`() {
            Assertions.assertThatThrownBy { SaveScheduleRequest(
                name = "일정 이름",
                startTime = LocalDateTime.now(),
                endTime = LocalDateTime.now().minusHours(1),
                scheduleTypeId = "1",
            )}.isInstanceOf(BusinessException::class.java)
                .hasMessage(ErrorCode.END_TIME_IS_EARLIER_THAN_START_TIME.message)
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

            docs(mockMvc, DocsMethod.POST, "/schedules") {
                request {
                    cookie(*memberTestHelper.getTokenCookies(adminMember!!))
                    content(asJsonString(saveScheduleRequest))
                    contentType(MediaType.APPLICATION_JSON)
                }
                result { expect(status().isCreated()) }
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
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class FindSchedules {

        @Documentation("find-schedules")
        fun `일정 조회는 성공해야 한다`() {
            val securedValue = RestDocsHelper.getSecuredValue(ScheduleController::class.java, "findAllSchedule")
            scheduleService.saveSchedule( // 2021-05-10 ~ 2021-06-10
                DEFAULT_SCHEDULE_NAME,
                LocalDateTime.of(2021, 5, 10, 0, 0),
                LocalDateTime.of(2021, 6, 10, 0, 0),
                1L
            )
            val findScheduleRequest = FindScheduleRequest(
                startTime = LocalDateTime.of(2021, 5, 10, 0, 0),
                endTime = LocalDateTime.of(2021, 6, 10, 0, 0),
            )
            docs(mockMvc, DocsMethod.GET, "/schedules") {
                request {
                    cookie(*memberTestHelper.getTokenCookies(member!!))
                    content(asJsonString(findScheduleRequest))
                    contentType(MediaType.APPLICATION_JSON)
                }
                result {
                    expect(status().isOk())
                    expect(jsonPath("$[0].name").value(DEFAULT_SCHEDULE_NAME))
                    expect(jsonPath("$[0].startTime").value("2021-05-10T00:00:00"))
                    expect(jsonPath("$[0].endTime").value("2021-06-10T00:00:00"))
                    expect(jsonPath("$[0].scheduleTypeResponse.id").value(1))
                    expect(jsonPath("$[0].scheduleTypeResponse.type").value(DEFAULT_SCHEDULE_TYPE))
                    expect(jsonPath("$[0].scheduleTypeResponse.description").value(DEFAULT_SCHEDULE_TYPE_DESCRIPTION))
                }
                response {
                    cookie(
                        ACCESS_TOKEN.tokenName means "ACCESS TOKEN $securedValue",
                        REFRESH_TOKEN.tokenName means "REFRESH TOKEN",
                    )
                    requestBody(
                        "startTime" means "일정 조회 시작 시간",
                        "endTime" means "일정 조회 종료 시간",

                    )
                    responseBody(
                        "[].id" means "일정 ID",
                        "[].name" means "일정 이름",
                        "[].startTime" means "일정 시작 시간",
                        "[].endTime" means "일정 종료 시간",
                        "[].scheduleTypeResponse.id" means "일정 타입의 ID 값",
                        "[].scheduleTypeResponse.type" means "일정 타입",
                        "[].scheduleTypeResponse.description" means "일정 타입의 설명",
                    )
                }
            }
        }

    }
}
