package com.keeper.homepage.domain.study.api;

import static com.keeper.homepage.global.config.security.data.JwtType.ACCESS_TOKEN;
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

  ResultActions callCreateStudyApiWithThumbnail(String accessToken, MockMultipartFile thumbnail, MockPart mockPart)
      throws Exception {
    return mockMvc.perform(multipart(POST, "/studies")
        .file(thumbnail)
        .part(mockPart)
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), accessToken))
        .contentType(MediaType.MULTIPART_FORM_DATA));
  }

  ResultActions callCreateStudyApi(String accessToken, MultiValueMap<String, String> params)
      throws Exception {
    return mockMvc.perform(multipart(POST, "/studies")
        .queryParams(params)
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), accessToken))
        .contentType(MediaType.MULTIPART_FORM_DATA));
  }

  ResultActions callDeleteStudyApi(String accessToken, long studyId)
      throws Exception {
    return mockMvc.perform(delete("/studies/{studyId}", studyId)
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), accessToken)));
  }

  ResultActions callGetStudyApi(String accessToken, long studyId)
      throws Exception {
    return mockMvc.perform(get("/studies/{studyId}", studyId)
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), accessToken)));
  }

  ResultActions callGetStudiesApi(String accessToken, int year, int season)
      throws Exception {
    return mockMvc.perform(get("/studies")
        .param("year", String.valueOf(year))
        .param("season", String.valueOf(season))
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), accessToken)));
  }

  ResultActions callUpdateStudyApi(String accessToken, long studyId, StudyUpdateRequest request)
      throws Exception {
    return mockMvc.perform(put("/studies/{studyId}", studyId)
        .content(asJsonString(request))
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), accessToken))
        .contentType(MediaType.APPLICATION_JSON));
  }

  ResultActions callUpdateStudyThumbnailApi(String accessToken, long studyId, MockMultipartFile thumbnail)
      throws Exception {
    return mockMvc.perform(RestDocumentationRequestBuilders.multipart("/studies/{studyId}/thumbnail", studyId)
        .file(thumbnail)
        .with(request -> {
          request.setMethod("PATCH");
          return request;
        })
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), accessToken))
        .contentType(MediaType.MULTIPART_FORM_DATA));
  }

  ResultActions callJoinStudyApi(String accessToken, long studyId, long memberId)
      throws Exception {
    return mockMvc.perform(post("/studies/{studyId}/members/{memberId}", studyId, memberId)
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), accessToken)));
  }

  ResultActions callLeaveStudyApi(String accessToken, long studyId, long memberId)
      throws Exception {
    return mockMvc.perform(delete("/studies/{studyId}/members/{memberId}", studyId, memberId)
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), accessToken)));
  }
}
