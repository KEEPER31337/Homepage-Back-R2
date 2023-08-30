package com.keeper.homepage.domain.election.api;

import static com.keeper.homepage.global.config.security.data.JwtType.ACCESS_TOKEN;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.election.dto.request.ElectionCandidateRegisterRequest;
import com.keeper.homepage.domain.election.dto.request.ElectionCandidatesRegisterRequest;
import com.keeper.homepage.domain.election.dto.request.ElectionCreateRequest;
import com.keeper.homepage.domain.election.dto.request.ElectionUpdateRequest;
import com.keeper.homepage.domain.election.dto.request.ElectionVotersRequest;
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

  FieldDescriptor[] getElectionResponse() {
    return new FieldDescriptor[]{
        fieldWithPath("id").description("선거 ID"),
        fieldWithPath("name").description("선거 이름"),
        fieldWithPath("description").description("선거 설명"),
        fieldWithPath("isAvailable").description("선거 공개 비공개 여부")
    };

  }

  ResultActions callRegisterCandidateApi(String adminToken, ElectionCandidateRegisterRequest request, long electionId,
      long candidateId) throws Exception {
    return mockMvc.perform(post("/admin/elections/{electionId}/candidates/{candidateId}", electionId, candidateId)
        .content(asJsonString(request))
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), adminToken))
        .contentType(MediaType.APPLICATION_JSON));
  }

  ResultActions callRegisterCandidatesApi(String adminToken, ElectionCandidatesRegisterRequest request, long electionId)
      throws Exception {
    return mockMvc.perform(post("/admin/elections/{electionId}/candidates", electionId)
        .content(asJsonString(request))
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), adminToken))
        .contentType(MediaType.APPLICATION_JSON));
  }

  ResultActions callDeleteCandidateApi(String adminToken, long electionId, long candidateId) throws Exception {
    return mockMvc.perform(delete("/admin/elections/{electionId}/candidates/{candidateId}", electionId, candidateId)
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), adminToken)));
  }

  ResultActions callRegisterVotersApi(String adminToken, ElectionVotersRequest request, long electionId)
      throws Exception {
    return mockMvc.perform(post("/admin/elections/{electionId}/voters", electionId)
        .content(asJsonString(request))
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), adminToken))
        .contentType(MediaType.APPLICATION_JSON));
  }

  ResultActions callDeleteVotersApi(String adminToken, ElectionVotersRequest request, long electionId)
      throws Exception {
    return mockMvc.perform(delete("/admin/elections/{electionId}/voters", electionId)
        .content(asJsonString(request))
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), adminToken))
        .contentType(MediaType.APPLICATION_JSON));
  }

  ResultActions callOpenElectionApi(String adminToken, long electionId) throws Exception {
    return mockMvc.perform(patch("/admin/elections/{electionId}/open", electionId)
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), adminToken)));
  }

  ResultActions callCloseElectionApi(String adminToken, long electionId) throws Exception {
    return mockMvc.perform(patch("/admin/elections/{electionId}/close", electionId)
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), adminToken)));
  }

}
