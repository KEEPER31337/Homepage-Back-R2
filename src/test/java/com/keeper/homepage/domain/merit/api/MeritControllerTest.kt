package com.keeper.homepage.domain.merit.api

import com.keeper.homepage.IntegrationTest
import com.keeper.homepage.domain.member.entity.Member
import com.keeper.homepage.domain.member.entity.job.MemberJob
import com.keeper.homepage.domain.merit.dto.request.AddMeritTypeRequest
import com.keeper.homepage.domain.merit.entity.MeritType
import com.keeper.homepage.global.config.security.data.JwtType.*
import com.keeper.homepage.global.docs.Documentation
import com.keeper.homepage.global.docs.means
import com.keeper.homepage.global.docs.restDocs
import io.jsonwebtoken.io.IOException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestInstance
import org.springframework.http.HttpMethod.*
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

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
        fun `상벌점 타입 생성 성공해야 한다`() {
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

    }

}