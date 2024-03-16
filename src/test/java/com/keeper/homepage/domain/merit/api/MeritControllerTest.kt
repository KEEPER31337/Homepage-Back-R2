package com.keeper.homepage.domain.merit.api

import com.keeper.homepage.IntegrationTest
import com.keeper.homepage.domain.member.entity.Member
import com.keeper.homepage.domain.member.entity.job.MemberJob
import com.keeper.homepage.domain.merit.dto.request.AddMeritTypeRequest
import com.keeper.homepage.domain.merit.dto.request.UpdateMeritTypeRequest
import com.keeper.homepage.domain.merit.entity.MeritType
import com.keeper.homepage.global.config.security.data.JwtType.*
import com.keeper.homepage.global.dsl.Documentation
import com.keeper.homepage.global.dsl.means
import com.keeper.homepage.global.dsl.restDocs
import com.keeper.homepage.global.dsl.rest_docs.DocsMethod
import com.keeper.homepage.global.dsl.rest_docs.docs
import io.jsonwebtoken.io.IOException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestInstance
import org.springframework.http.HttpMethod.*
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.StatusResultMatchers

class MeritControllerTest1 : IntegrationTest() {

    private var meritType: MeritType? = null
    private var demeritType: MeritType? = null
    private var member: Member? = null
    private var admin: Member? = null
    private var otherMember: Member? = null
    private var userAccessToken: String? = null
    private var adminAccessToken: String? = null

    @BeforeEach
    @Throws(IOException::class)
    fun setUp() {
        meritType = meritTypeHelper.generate()
        member = memberTestHelper.generate()
        otherMember = memberTestHelper.generate()
        admin = memberTestHelper.generate().apply { assignJob(MemberJob.MemberJobType.ROLE_회장) }
        userAccessToken = jwtTokenProvider.createAccessToken(ACCESS_TOKEN, member!!.id,
                MemberJob.MemberJobType.ROLE_회원)
        adminAccessToken = jwtTokenProvider.createAccessToken(ACCESS_TOKEN, member!!.id,
                MemberJob.MemberJobType.ROLE_회장, MemberJob.MemberJobType.ROLE_부회장, MemberJob.MemberJobType.ROLE_서기, MemberJob.MemberJobType.ROLE_회원)
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class MeritTypeTest {

        @Documentation("search-meritType-kt")
        fun `상벌점 조회는 성공해야 한다`() {
            restDocs(mockMvc, GET, "/merits/types") {
                cookie(*memberTestHelper.getTokenCookies(admin))
                expect(HttpStatus.OK, MediaType.APPLICATION_JSON_UTF8)
                cookieVariable(
                        ACCESS_TOKEN.tokenName means "ACCESS TOKEN",
                        REFRESH_TOKEN.tokenName means "REFRESH TOKEN",
                )
                responseBodyWithPaging(
                        "content[].id" means "상벌점 타입의 ID",
                        "content[].score" means "상벌점 점수",
                        "content[].detail" means "상벌점 사유",
                        "content[].isMerit" means "상벌점 타입",
                )
            }
        }

        @Documentation("create-meritType")
        fun `상벌점 타입 생성을 성공해야 한다`() {
            val request = AddMeritTypeRequest.builder()
                    .score(3)
                    .reason("우수기술문서 작성")
                    .isMerit(true)
                    .build()

            restDocs(mockMvc, POST, "/merits/types") {
                cookie(*memberTestHelper.getTokenCookies(admin))
                requestJson(asJsonString(request))
                expect(HttpStatus.CREATED, MediaType.APPLICATION_JSON)
                cookieVariable(
                        ACCESS_TOKEN.tokenName means "ACCESS TOKEN",
                        REFRESH_TOKEN.tokenName means "REFRESH TOKEN",
                )
                requestBody(
                        "score" means "상벌점 점수",
                        "reason" means "상벌점 사유",
                        "isMerit" means "상벌점 타입",
                )
            }
        }

        @Documentation("update-meritType")
        fun `상벌점 부여 로그 수정을 성공해야 한다`() {
            val request = UpdateMeritTypeRequest.builder()
                    .score(-5)
                    .reason("거짓 스터디")
                    .isMerit(false)
                    .build()

            restDocs(mockMvc, PUT, "/merits/types/{meritTypeId}", "${meritType!!.id}") {
                cookie(*memberTestHelper.getTokenCookies(admin))
                requestJson(asJsonString(request))
                expect(HttpStatus.CREATED, MediaType.APPLICATION_JSON)
                pathVariable("meritTypeId" means "수정하고자 하는 상벌점 타입의 ID")
                cookieVariable(
                        ACCESS_TOKEN.tokenName means "ACCESS TOKEN",
                        REFRESH_TOKEN.tokenName means "REFRESH TOKEN",
                )
                requestBody(
                        "score" means "상벌점 점수",
                        "reason" means "상벌점 사유",
                        "isMerit" means "상벌점 타입",
                )
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class MeritLogTest {

        @BeforeEach
        fun setUp() {
            meritLogTestHelper.generate()
            meritLogTestHelper.generate()
        }

        @Documentation("search-member-meritLog")
        fun `회원별 상벌점 목록 조회를 성공해야 한다`() {
            meritLogTestHelper.builder()
                    .memberId(member!!.id)
                    .meritType(meritTypeHelper.builder()
                            .merit(3)
                            .build())
                    .build()

            meritLogTestHelper.builder()
                    .memberId(member!!.id)
                    .meritType(meritTypeHelper.builder()
                            .merit(2)
                            .build())
                    .build()

            meritLogTestHelper.builder()
                    .memberId(member!!.id)
                    .meritType(meritTypeHelper.builder()
                            .merit(-1)
                            .build())
                    .build()

            meritLogTestHelper.builder()
                    .memberId(member!!.id)
                    .meritType(meritTypeHelper.builder()
                            .merit(-3)
                            .build())
                    .build()

            restDocs(mockMvc, GET, "/merits/members/{memberId}", "${member!!.id}") {
                cookie(*memberTestHelper.getTokenCookies(admin))
                expect(HttpStatus.OK, MediaType.APPLICATION_JSON_UTF8)
                cookieVariable(
                        ACCESS_TOKEN.tokenName means "ACCESS TOKEN",
                        REFRESH_TOKEN.tokenName means "REFRESH TOKEN",
                )
                pathVariable("memberId" means "조회하고자 하는 멤버의 ID 값")
                responseBodyWithPaging(
                        "content[].id" means "상벌점 로그의 ID",
                        "content[].giveTime" means "상벌점 로그의 생성시간",
                        "content[].score" means "상벌점 점수",
                        "content[].meritTypeId" means "상벌점 타입의 ID",
                        "content[].reason" means "상벌점 사유",
                        "content[].isMerit" means "상벌점 타입",
                )
            }
        }

        @Documentation("search-meritLog")
        fun `상벌점 목록 조회를 성공해야 한다`() {
            restDocs(mockMvc, GET, "/merits") {
                cookie(*memberTestHelper.getTokenCookies(admin))
                expect(HttpStatus.OK, MediaType.APPLICATION_JSON_UTF8)
                cookieVariable(
                        ACCESS_TOKEN.tokenName means "ACCESS TOKEN",
                        REFRESH_TOKEN.tokenName means "REFRESH TOKEN",
                )
                responseBodyWithPaging(
                        "content[].id" means "상벌점 로그의 ID",
                        "content[].giveTime" means "상벌점 로그의 생성시간",
                        "content[].score" means "상벌점 점수",
                        "content[].meritTypeId" means "상벌점 타입의 ID",
                        "content[].reason" means "상벌점 사유",
                        "content[].isMerit" means "상벌점 타입",
                        "content[].awarderName" means "수상자의 이름",
                        "content[].awarderGeneration" means "수상자의 학번",
                )
            }

            docs(mockMvc, DocsMethod.GET, "/merits") {
                request {
                    cookie(*memberTestHelper.getTokenCookies(admin))
                }

                result {
                    expect(MockMvcResultMatchers.status().isOk())
                    expect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                }

                response {
                    cookie(
                            ACCESS_TOKEN.tokenName means "ACCESS TOKEN",
                            REFRESH_TOKEN.tokenName means "REFRESH TOKEN",
                    )

                    responseBodyWithPaging(
                            "content[].id" means "상벌점 로그의 ID",
                            "content[].giveTime" means "상벌점 로그의 생성시간",
                            "content[].score" means "상벌점 점수",
                            "content[].meritTypeId" means "상벌점 타입의 ID",
                            "content[].reason" means "상벌점 사유",
                            "content[].isMerit" means "상벌점 타입",
                            "content[].awarderName" means "수상자의 이름",
                            "content[].awarderGeneration" means "수상자의 학번",
                    )
                }


            }
        }

        @Documentation("update-merit")
        fun `상벌점 부여 로그 수정을 성공해야 한다`() {
            val request = UpdateMeritTypeRequest.builder()
                    .score(-5)
                    .reason("거짓 스터디")
                    .isMerit(false)
                    .build()
        }
    }
}