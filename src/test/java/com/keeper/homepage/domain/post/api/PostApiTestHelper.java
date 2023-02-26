package com.keeper.homepage.domain.post.api;

import static com.keeper.homepage.global.config.security.data.JwtType.ACCESS_TOKEN;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

import com.keeper.homepage.IntegrationTest;
import jakarta.servlet.http.Cookie;
import java.util.Collection;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.MultiValueMap;

public class PostApiTestHelper extends IntegrationTest {

  ResultActions callCreatePostApiWithFile(String accessToken, MockMultipartFile file,
      MultiValueMap<String, String> params)
      throws Exception {
    return mockMvc.perform(multipart(HttpMethod.POST, "/posts")
        .file(file)
        .queryParams(params)
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), accessToken))
        .contentType(MediaType.MULTIPART_FORM_DATA));
  }

  ResultActions callCreatePostApiWithFiles(String accessToken,
      MockMultipartFile thumbnail,
      MockMultipartFile file, MultiValueMap<String, String> params) throws Exception {
    return mockMvc.perform(multipart(HttpMethod.POST, "/posts")
        .file(thumbnail)
        .file(file)
        .queryParams(params)
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), accessToken))
        .contentType(MediaType.MULTIPART_FORM_DATA));
  }

  ResultActions callCreatePostApi(String accessToken, MultiValueMap<String, String> params)
      throws Exception {
    return mockMvc.perform(multipart(HttpMethod.POST, "/posts")
        .queryParams(params)
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), accessToken))
        .contentType(MediaType.MULTIPART_FORM_DATA));
  }

  ResultActions callFindPostApi(String accessToken, long postId)
      throws Exception {
    return mockMvc.perform(get("/posts/{postId}", postId)
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), accessToken)));
  }

  ResultActions callFindPostApiWithPassword(String accessToken, long postId,
      String password)
      throws Exception {
    return mockMvc.perform(get("/posts/{postId}", postId)
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), accessToken))
        .param("password", password));
  }
}
