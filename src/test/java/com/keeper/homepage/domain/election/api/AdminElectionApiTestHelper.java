package com.keeper.homepage.domain.election.api;

import static com.keeper.homepage.global.config.security.session.SessionPolicy.SESSION_COOKIE_NAME;
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
import org.junit.jupiter.api.Disabled;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.web.servlet.ResultActions;

@Disabled
public class AdminElectionApiTestHelper extends IntegrationTest {

  ResultActions callCreateElectionApi(String adminSessionId, ElectionCreateRequest request) throws Exception {
    return mockMvc.perform(post("/admin/elections")
        .content(asJsonString(request))
        .cookie(new Cookie(SESSION_COOKIE_NAME, adminSessionId))
        .contentType(MediaType.APPLICATION_JSON));
  }

  ResultActions callDeleteElectionApi(String adminSessionId, long electionId) throws Exception {
    return mockMvc.perform(delete("/admin/elections/{electionId}", electionId)
        .cookie(new Cookie(SESSION_COOKIE_NAME, adminSessionId)));
  }

  ResultActions callUpdateElectionApi(String adminSessionId, long electionId, ElectionUpdateRequest request)
      throws Exception {
    return mockMvc.perform(put("/admin/elections/{electionId}", electionId)
        .content(asJsonString(request))
        .cookie(new Cookie(SESSION_COOKIE_NAME, adminSessionId))
        .contentType(MediaType.APPLICATION_JSON));
  }

  ResultActions callGetElectionsApi(String adminSessionId) throws Exception {
    return mockMvc.perform(get("/admin/elections")
        .cookie(new Cookie(SESSION_COOKIE_NAME, adminSessionId)));
  }

  FieldDescriptor[] getElectionResponse() {
    return new FieldDescriptor[]{
        fieldWithPath("id").description("선거 ID"),
        fieldWithPath("name").description("선거 이름"),
        fieldWithPath("description").description("선거 설명"),
        fieldWithPath("isAvailable").description("선거 공개 비공개 여부")
    };

  }

  ResultActions callRegisterCandidateApi(String adminSessionId, ElectionCandidateRegisterRequest request, long electionId,
      long candidateId) throws Exception {
    return mockMvc.perform(post("/admin/elections/{electionId}/candidates/{candidateId}", electionId, candidateId)
        .content(asJsonString(request))
        .cookie(new Cookie(SESSION_COOKIE_NAME, adminSessionId))
        .contentType(MediaType.APPLICATION_JSON));
  }

  ResultActions callRegisterCandidatesApi(String adminSessionId, ElectionCandidatesRegisterRequest request, long electionId)
      throws Exception {
    return mockMvc.perform(post("/admin/elections/{electionId}/candidates", electionId)
        .content(asJsonString(request))
        .cookie(new Cookie(SESSION_COOKIE_NAME, adminSessionId))
        .contentType(MediaType.APPLICATION_JSON));
  }

  ResultActions callDeleteCandidateApi(String adminSessionId, long electionId, long candidateId) throws Exception {
    return mockMvc.perform(delete("/admin/elections/{electionId}/candidates/{candidateId}", electionId, candidateId)
        .cookie(new Cookie(SESSION_COOKIE_NAME, adminSessionId)));
  }

  ResultActions callRegisterVotersApi(String adminSessionId, ElectionVotersRequest request, long electionId)
      throws Exception {
    return mockMvc.perform(post("/admin/elections/{electionId}/voters", electionId)
        .content(asJsonString(request))
        .cookie(new Cookie(SESSION_COOKIE_NAME, adminSessionId))
        .contentType(MediaType.APPLICATION_JSON));
  }

  ResultActions callDeleteVotersApi(String adminSessionId, ElectionVotersRequest request, long electionId)
      throws Exception {
    return mockMvc.perform(delete("/admin/elections/{electionId}/voters", electionId)
        .content(asJsonString(request))
        .cookie(new Cookie(SESSION_COOKIE_NAME, adminSessionId))
        .contentType(MediaType.APPLICATION_JSON));
  }

  ResultActions callOpenElectionApi(String adminSessionId, long electionId) throws Exception {
    return mockMvc.perform(patch("/admin/elections/{electionId}/open", electionId)
        .cookie(new Cookie(SESSION_COOKIE_NAME, adminSessionId)));
  }

  ResultActions callCloseElectionApi(String adminSessionId, long electionId) throws Exception {
    return mockMvc.perform(patch("/admin/elections/{electionId}/close", electionId)
        .cookie(new Cookie(SESSION_COOKIE_NAME, adminSessionId)));
  }

}
