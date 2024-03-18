package com.keeper.homepage.domain.merit.api

import com.keeper.homepage.IntegrationTest
import com.keeper.homepage.domain.member.entity.Member
import com.keeper.homepage.domain.member.entity.job.MemberJob
import com.keeper.homepage.domain.merit.dto.request.AddMeritTypeRequest
import com.keeper.homepage.domain.merit.dto.request.GiveMeritPointRequest
import com.keeper.homepage.domain.merit.dto.request.UpdateMeritTypeRequest
import com.keeper.homepage.domain.merit.entity.MeritType
import com.keeper.homepage.global.config.security.data.JwtType.*
import com.keeper.homepage.global.dsl.rest_docs.Documentation
import com.keeper.homepage.global.dsl.means
import com.keeper.homepage.global.dsl.restDocs
import com.keeper.homepage.global.dsl.rest_docs.DocsMethod
import com.keeper.homepage.global.dsl.rest_docs.DocsMethod.*
import com.keeper.homepage.global.dsl.rest_docs.docs
import io.jsonwebtoken.io.IOException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.http.HttpMethod.*
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

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
            docs(mockMvc, DocsMethod.GET, "/merits/types") {
                request {
                    cookie(*memberTestHelper.getTokenCookies(admin))
                }

                result {
                    expect(status().isOk())
                    expect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                }

                response {
                    cookie(
                            ACCESS_TOKEN.tokenName means "ACCESS TOKEN",
                            REFRESH_TOKEN.tokenName means "REFRESH TOKEN",
                    )

                    responseBodyWithPaging(
                            "id" means "상벌점 타입의 ID",
                            "score" means "상벌점 점수",
                            "detail" means "상벌점 사유",
                            "isMerit" means "상벌점 타입",
                    )
                }

            }
        }

        @Test
        fun `일반 회원은 조회할 수 없다`() {
            docs(mockMvc, DocsMethod.GET, "/merits/types") {
                request {
                    cookie(*memberTestHelper.getTokenCookies(member!!))
                }

                result {
                    expect(status().isForbidden())
                    expect(jsonPath("$.message").exists())
                }
            }
        }


        @Documentation("create-meritType-kt")
        fun `상벌점 타입 생성을 성공해야 한다`() {
            val request = AddMeritTypeRequest.builder()
                    .score(3)
                    .reason("우수기술문서 작성")
                    .isMerit(true)
                    .build()

            docs(mockMvc, DocsMethod.POST, "/merits/types") {
                request {
                    cookie(*memberTestHelper.getTokenCookies(admin))
                    content(asJsonString(request))
                    contentType(MediaType.APPLICATION_JSON)
                }

                result {
                    expect(status().isCreated())
                }

                response {
                    cookie(
                            ACCESS_TOKEN.tokenName means "ACCESS TOKEN",
                            REFRESH_TOKEN.tokenName means "REFRESH TOKEN",
                    )

                    requestBody(
                            "score" means "상벌점 점수를 입력해주세요.",
                            "reason" means "상벌점 사유를 입력해주세요.",
                            "isMerit" means "상벌점 타입를 입력해주세요.",
                    )
                }
            }
        }

        @Test
        fun `일반 회원은 상벌점 타입을 생성할 수 없다`() {
            val request = AddMeritTypeRequest.builder()
                    .score(3)
                    .reason("우수기술문서 작성")
                    .isMerit(true)
                    .build()

            docs(mockMvc, DocsMethod.POST, "/merits/types") {
                request {
                    cookie(*memberTestHelper.getTokenCookies(member!!))
                    content(asJsonString(request))
                    contentType(MediaType.APPLICATION_JSON)
                }

                result {
                    expect(status().isForbidden())
                    expect(jsonPath("$.message").exists())
                }
            }
        }

        @Documentation("update-meritType-kt")
        fun `상벌점 부여 로그 수정을 성공해야 한다`() {
            val request = UpdateMeritTypeRequest.builder()
                    .score(-5)
                    .reason("거짓 스터디")
                    .isMerit(false)
                    .build()

            docs(mockMvc, DocsMethod.PUT, "/merits/types/{meritTypeId}", "${meritType!!.id}") {
                request {
                    cookie(*memberTestHelper.getTokenCookies(admin))
                    content(asJsonString(request))
                    contentType(MediaType.APPLICATION_JSON)
                }

                result {
                    expect(status().isCreated())
                }

                response {
                    path("meritTypeId" means "수정하고자 하는 상벌점 타입의 ID")

                    cookie(
                            ACCESS_TOKEN.tokenName means "ACCESS TOKEN",
                            REFRESH_TOKEN.tokenName means "REFRESH TOKEN",
                    )

                    requestBody(
                            "score" means "상벌점 점수를 입력해주세요.",
                            "reason" means "상벌점 사유를 입력해주세요.",
                            "isMerit" means "상벌점 타입를 입력해주세요.",
                    )
                }
            }
        }

        @Test
        fun `일반회원은 상벌점 타입 수정을 성공할 수 없다`() {
            val request = UpdateMeritTypeRequest.builder()
                    .score(-5)
                    .reason("거짓 스터디")
                    .isMerit(false)
                    .build()

            docs(mockMvc, DocsMethod.PUT, "/merits/types/{meritTypeId}", "${meritType!!.id}") {
                request {
                    cookie(*memberTestHelper.getTokenCookies(member!!))
                    content(asJsonString(request))
                    contentType(MediaType.APPLICATION_JSON)
                }

                result {
                    expect(status().isForbidden())
                    expect(jsonPath("$.message").exists())
                }
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

            docs(mockMvc, DocsMethod.GET, "/merits/members/{memberId}", "${member!!.id}") {
                request {
                    cookie(*memberTestHelper.getTokenCookies(admin))
                }

                result {
                    expect(status().isOk())
                    expect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                }

                response {
                    cookie(
                            ACCESS_TOKEN.tokenName means "ACCESS TOKEN",
                            REFRESH_TOKEN.tokenName means "REFRESH TOKEN",
                    )

                    path("memberId" means "조회하고자 하는 멤버의 ID 값")

                    responseBodyWithPaging(
                            "id" means "상벌점 로그의 ID",
                            "giveTime" means "상벌점 로그의 생성시간",
                            "score" means "상벌점 점수",
                            "meritTypeId" means "상벌점 타입의 ID",
                            "reason" means "상벌점 사유",
                            "isMerit" means "상벌점 타입",
                    )
                }
            }
        }

        @Test
        fun `일반회원은 회원별 상벌점 목록 조회를 할 수 없다`() {
            meritLogTestHelper.builder()
                    .memberId(member!!.id)
                    .build()

            docs(mockMvc, DocsMethod.GET, "/merits/members/{memberId}", "${member!!.id}") {
                request {
                    cookie(*memberTestHelper.getTokenCookies(member!!))
                }

                result {
                    expect(status().isForbidden())
                    expect(jsonPath("$.message").exists())
                }
            }
        }

        @Documentation("search-meritLog-kt")
        fun `상벌점 목록 조회를 성공해야 한다`() {
            docs(mockMvc, DocsMethod.GET, "/merits") {
                request {
                    cookie(*memberTestHelper.getTokenCookies(admin))
                }

                result {
                    expect(status().isOk())
                    expect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                }

                response {
                    cookie(
                            ACCESS_TOKEN.tokenName means "ACCESS TOKEN",
                            REFRESH_TOKEN.tokenName means "REFRESH TOKEN",
                    )

                    responseBodyWithPaging(
                            "id" means "상벌점 로그의 ID",
                            "giveTime" means "상벌점 로그의 생성시간",
                            "score" means "상벌점 점수",
                            "meritTypeId" means "상벌점 타입의 ID",
                            "reason" means "상벌점 사유",
                            "isMerit" means "상벌점 타입",
                            "awarderName" means "수상자의 이름",
                            "awarderGeneration" means "수상자의 학번",
                    )
                }
            }
        }

        @Test
        fun `벌점 목록 조회를 성공해야 한다`() {
            meritLogTestHelper.builder()
                    .memberId(member!!.id)
                    .meritType(meritTypeHelper.builder()
                            .merit(3)
                            .isMerit(true)
                            .build())
                    .build()

            meritLogTestHelper.builder()
                    .memberId(member!!.id)
                    .meritType(meritTypeHelper.builder()
                            .merit(-3)
                            .isMerit(false)
                            .build())
                    .build()

            docs(mockMvc, DocsMethod.GET, "/merits") {
                request {
                    param("meritType", "DEMERIT")
                    cookie(*memberTestHelper.getTokenCookies(admin!!))
                }

                result {
                    expect(status().isOk())
                    expect(jsonPath("$.content[0].isMerit").value("false"))
                }
            }
        }

        @Test
        fun `상점 목록 조회를 성공해야 한다`() {
            meritLogTestHelper.builder()
                    .memberId(member!!.id)
                    .meritType(meritTypeHelper.builder()
                            .merit(-3)
                            .isMerit(false)
                            .build())
                    .build()

            meritLogTestHelper.builder()
                    .memberId(member!!.id)
                    .meritType(meritTypeHelper.builder()
                            .merit(3)
                            .isMerit(true)
                            .build())
                    .build()

            docs(mockMvc, DocsMethod.GET, "/merits") {
                request {
                    param("meritType", "MERIT")
                    cookie(*memberTestHelper.getTokenCookies(admin!!))
                }

                result {
                    expect(status().isOk())
                    expect(jsonPath("$.content[0].isMerit").value("true"))
                }
            }
        }

        @Documentation("create-meritLog-kt")
        fun `상벌점 부여 로그 생성을 성공해야 한다`() {
            val request = GiveMeritPointRequest.builder()
                    .awarderId(member!!.id)
                    .meritTypeId(meritType!!.id)
                    .build()

            docs(mockMvc, MULTIPART, "/merits") {
                request {
                    cookie(*memberTestHelper.getTokenCookies(admin))
                    content(asJsonString(request))
                    contentType(MediaType.APPLICATION_JSON)
                }

                result {
                    expect(status().isCreated())
                }

                response {
                    cookie(
                            ACCESS_TOKEN.tokenName means "ACCESS TOKEN",
                            REFRESH_TOKEN.tokenName means "REFRESH TOKEN",
                    )

                    requestBody(
                            "awarderId" means "수여자의 ID",
                            "meritTypeId" means "상벌점 타입의 ID",
                    )
                }
            }
        }

        @Test
        fun `일반회원은 상벌점 부여 로그를 생성할 수 없다`() {
            val request = GiveMeritPointRequest.builder()
                    .awarderId(member!!.id)
                    .meritTypeId(meritType!!.id)
                    .build()

            docs(mockMvc, MULTIPART, "/merits") {
                request {
                    cookie(*memberTestHelper.getTokenCookies(member!!))
                    content(asJsonString(request))
                    contentType(MediaType.APPLICATION_JSON)
                }

                result {
                    expect(status().isForbidden())
                    expect(jsonPath("$.message").exists())
                }
            }
        }

    }
}