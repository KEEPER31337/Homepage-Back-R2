package com.keeper.homepage.domain.merit.api

import com.keeper.homepage.IntegrationTest
import com.keeper.homepage.domain.member.entity.Member
import com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType
import com.keeper.homepage.domain.merit.entity.MeritType
import com.keeper.homepage.global.config.security.data.JwtType.*
import com.keeper.homepage.global.docs.means
import com.keeper.homepage.global.docs.restDocs
import com.keeper.homepage.global.docs.Documentation
import io.jsonwebtoken.io.IOException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

class MeritControllerTestWithKotlinDsl : IntegrationTest() {

    private var meritType: MeritType? = null
    private var demeritType: MeritType? = null
    private var member: Member? = null
    private var admin: Member? = null
    private var otherMember: Member? = null
    private var userAccessToken: String? = null
    private var adminAccessToken: kotlin.String? = null

    @BeforeEach
    @Throws(IOException::class)
    fun setUp() {
        meritType = meritTypeHelper.generate()
        member = memberTestHelper.generate()
        otherMember = memberTestHelper.generate()
        admin = memberTestHelper.generate().apply { assignJob(MemberJobType.ROLE_회장) }
        userAccessToken = jwtTokenProvider.createAccessToken(ACCESS_TOKEN, member!!.id,
                MemberJobType.ROLE_회원)
        adminAccessToken = jwtTokenProvider.createAccessToken(ACCESS_TOKEN, member!!.id,
                MemberJobType.ROLE_회장, MemberJobType.ROLE_부회장, MemberJobType.ROLE_서기, MemberJobType.ROLE_회원)
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class MeritTypeTest {

        @Test
        @Documentation("search-meritType-kt")
        fun `상벌점 조회는 성공해야 한다`() {
            restDocs(mockMvc, HttpMethod.GET, "/merits/types") {
                expect(HttpStatus.OK, MediaType.APPLICATION_JSON_UTF8)
                cookie(*memberTestHelper.getTokenCookies(admin))
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

    }

}