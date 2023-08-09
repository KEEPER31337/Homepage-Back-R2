package com.keeper.homepage.domain.merit.api;

import static com.keeper.homepage.global.config.security.data.JwtType.ACCESS_TOKEN;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType;
import com.keeper.homepage.domain.merit.application.MeritTypeService;
import com.keeper.homepage.domain.merit.dao.MeritTypeRepository;
import com.keeper.homepage.domain.merit.entity.MeritType;
import com.keeper.homepage.global.config.security.data.JwtType;
import io.jsonwebtoken.io.IOException;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


public class MeritControllerTest extends IntegrationTest {


  @Nested
  @DisplayName("상벌점 타입 조회")
  @Component
  class ControllerTest {

    private MeritType meritType;
    private Member member, other;
    private String memberToken, otherToken;

    @BeforeEach
    void setUp() throws IOException {
      meritType = meritTypeHelper.builder().merit(3).detail("우수기술문서작성").build();
      meritType = meritTypeHelper.builder().merit(-3).detail("무단 결석").build();
      member = memberTestHelper.generate();
      memberToken = jwtTokenProvider.createAccessToken(ACCESS_TOKEN, member.getId(),
          MemberJobType.ROLE_회원);
    }

    @Test
    @DisplayName("상벌점 타입 조회를 성공해야 한다.")
    void 상벌점_조회는_성공해야_한다() throws Exception {

      ResultActions perform = mockMvc.perform(RestDocumentationRequestBuilders.get("/merits/types")
              .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), memberToken)))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
          .andDo(MockMvcResultHandlers.print());

    }


  }

}
