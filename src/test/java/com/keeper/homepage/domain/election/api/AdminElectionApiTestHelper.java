package com.keeper.homepage.domain.election.api;

import static com.keeper.homepage.global.config.security.data.JwtType.ACCESS_TOKEN;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.election.dto.request.ElectionCreateRequest;
import jakarta.servlet.http.Cookie;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

public class AdminElectionApiTestHelper extends IntegrationTest {

  ResultActions createElectionApi(String adminToken, ElectionCreateRequest request) throws Exception {
    return mockMvc.perform(post("/admin/elections")
        .content(asJsonString(request))
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), adminToken))
        .contentType(MediaType.APPLICATION_JSON));
  }
}
