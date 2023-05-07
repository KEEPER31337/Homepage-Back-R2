package com.keeper.homepage.domain.study.api;

import static com.keeper.homepage.global.config.security.data.JwtType.ACCESS_TOKEN;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

import com.keeper.homepage.IntegrationTest;
import jakarta.servlet.http.Cookie;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.MultiValueMap;

public class StudyApiTestHelper extends IntegrationTest {

  ResultActions callCreateStudyApiWithThumbnail(String accessToken, MockMultipartFile thumbnail,
      MultiValueMap<String, String> params)
      throws Exception {
    return mockMvc.perform(multipart(POST, "/studies")
        .file(thumbnail)
        .queryParams(params)
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
}
