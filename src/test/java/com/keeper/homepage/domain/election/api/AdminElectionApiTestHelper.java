package com.keeper.homepage.domain.election.api;

import static com.keeper.homepage.global.config.security.data.JwtType.ACCESS_TOKEN;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.election.dto.request.ElectionCreateRequest;
import com.keeper.homepage.domain.election.dto.request.ElectionRegisterRequest;
import com.keeper.homepage.domain.election.dto.request.ElectionUpdateRequest;
import jakarta.servlet.http.Cookie;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.web.servlet.ResultActions;

public class AdminElectionApiTestHelper extends IntegrationTest {

  ResultActions callCreateElectionApi(String adminToken, ElectionCreateRequest request) throws Exception {
    return mockMvc.perform(post("/admin/elections")
        .content(asJsonString(request))
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), adminToken))
        .contentType(MediaType.APPLICATION_JSON));
  }

  ResultActions callDeleteElectionApi(String adminToken, long electionId) throws Exception {
    return mockMvc.perform(delete("/admin/elections/{electionId}", electionId)
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), adminToken)));
  }

  ResultActions callUpdateElectionApi(String adminToken, long electionId, ElectionUpdateRequest request)
      throws Exception {
    return mockMvc.perform(put("/admin/elections/{electionId}", electionId)
        .content(asJsonString(request))
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), adminToken))
        .contentType(MediaType.APPLICATION_JSON));
  }

  ResultActions callGetElectionsApi(String adminToken) throws Exception {
    return mockMvc.perform(get("/admin/elections")
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), adminToken)));
  }

  ResultActions callRegisterCandidateApi(String adminToken, long electionId, long candidateId,
      ElectionRegisterRequest request) throws Exception {
    return mockMvc.perform(post("/admin/elections/{electionId}/candidates/{candidateId}",
        electionId, candidateId)
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), adminToken))
        .content(asJsonString(request))
        .contentType(MediaType.APPLICATION_JSON));
  }

  FieldDescriptor[] getElectionResponse() {
    return new FieldDescriptor[]{
        fieldWithPath("id").description("선거 ID"),
        fieldWithPath("name").description("선거 이름"),
        fieldWithPath("description").description("선거 설명"),
        fieldWithPath("isAvailable").description("선거 공개 비공개 여부")
    };

  }

}
