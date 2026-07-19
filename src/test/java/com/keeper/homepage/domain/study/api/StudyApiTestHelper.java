package com.keeper.homepage.domain.study.api;

import static com.keeper.homepage.global.config.security.session.SessionPolicy.SESSION_COOKIE_NAME;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.study.dto.request.StudyUpdateRequest;
import jakarta.servlet.http.Cookie;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.MultiValueMap;

public class StudyApiTestHelper extends IntegrationTest {

  ResultActions callCreateStudyApiWithThumbnail(String sessionId, MockMultipartFile thumbnail, MockPart mockPart)
      throws Exception {
    return mockMvc.perform(multipart(POST, "/studies")
        .file(thumbnail)
        .part(mockPart)
        .cookie(new Cookie(SESSION_COOKIE_NAME, sessionId))
        .contentType(MediaType.MULTIPART_FORM_DATA));
  }

  ResultActions callCreateStudyApi(String sessionId, MultiValueMap<String, String> params)
      throws Exception {
    return mockMvc.perform(multipart(POST, "/studies")
        .queryParams(params)
        .cookie(new Cookie(SESSION_COOKIE_NAME, sessionId))
        .contentType(MediaType.MULTIPART_FORM_DATA));
  }

  ResultActions callDeleteStudyApi(String sessionId, long studyId)
      throws Exception {
    return mockMvc.perform(delete("/studies/{studyId}", studyId)
        .cookie(new Cookie(SESSION_COOKIE_NAME, sessionId)));
  }

  ResultActions callGetStudyApi(String sessionId, long studyId)
      throws Exception {
    return mockMvc.perform(get("/studies/{studyId}", studyId)
        .cookie(new Cookie(SESSION_COOKIE_NAME, sessionId)));
  }

  ResultActions callGetStudiesApi(String sessionId, int year, int season)
      throws Exception {
    return mockMvc.perform(get("/studies")
        .param("year", String.valueOf(year))
        .param("season", String.valueOf(season))
        .cookie(new Cookie(SESSION_COOKIE_NAME, sessionId)));
  }

  ResultActions callUpdateStudyApi(String sessionId, long studyId, StudyUpdateRequest request)
      throws Exception {
    return mockMvc.perform(put("/studies/{studyId}", studyId)
        .content(asJsonString(request))
        .cookie(new Cookie(SESSION_COOKIE_NAME, sessionId))
        .contentType(MediaType.APPLICATION_JSON));
  }

  ResultActions callUpdateStudyThumbnailApi(String sessionId, long studyId, MockMultipartFile thumbnail)
      throws Exception {
    return mockMvc.perform(RestDocumentationRequestBuilders.multipart("/studies/{studyId}/thumbnail", studyId)
        .file(thumbnail)
        .with(request -> {
          request.setMethod("PATCH");
          return request;
        })
        .cookie(new Cookie(SESSION_COOKIE_NAME, sessionId))
        .contentType(MediaType.MULTIPART_FORM_DATA));
  }

  ResultActions callJoinStudyApi(String sessionId, long studyId, long memberId)
      throws Exception {
    return mockMvc.perform(post("/studies/{studyId}/members/{memberId}", studyId, memberId)
        .cookie(new Cookie(SESSION_COOKIE_NAME, sessionId)));
  }

  ResultActions callLeaveStudyApi(String sessionId, long studyId, long memberId)
      throws Exception {
    return mockMvc.perform(delete("/studies/{studyId}/members/{memberId}", studyId, memberId)
        .cookie(new Cookie(SESSION_COOKIE_NAME, sessionId)));
  }
}
