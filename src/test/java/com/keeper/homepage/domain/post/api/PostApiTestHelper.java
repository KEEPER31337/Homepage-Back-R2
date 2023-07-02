package com.keeper.homepage.domain.post.api;

import static com.keeper.homepage.global.config.security.data.JwtType.ACCESS_TOKEN;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

import com.keeper.homepage.IntegrationTest;
import jakarta.servlet.http.Cookie;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
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

  ResultActions callUpdatePostApi(String accessToken, long postId,
      MultiValueMap<String, String> params)
      throws Exception {
    return mockMvc.perform(multipart("/posts/{postId}", postId)
        .with(request -> {
          request.setMethod("PUT");
          return request;
        })
        .queryParams(params)
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), accessToken))
        .contentType(MediaType.MULTIPART_FORM_DATA));
  }

  ResultActions callUpdatePostThumbnail(String accessToken, long postId, MockMultipartFile thumbnail)
      throws Exception {
    return mockMvc.perform(multipart("/posts/{postId}/thumbnail", postId)
        .file(thumbnail)
        .with(request -> {
          request.setMethod("PATCH");
          return request;
        })
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), accessToken))
        .contentType(MediaType.MULTIPART_FORM_DATA));
  }

  ResultActions callDeletePostApi(String accessToken, long postId)
      throws Exception {
    return mockMvc.perform(delete("/posts/{postId}", postId)
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), accessToken)));
  }

  ResultActions callDeletePostThumbnailApi(String accessToken, long postId)
      throws Exception {
    return mockMvc.perform(delete("/posts/{postId}/thumbnail", postId)
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), accessToken)));
  }

  ResultActions callAdminDeletePostApi(String adminToken, long postId)
      throws Exception {
    return mockMvc.perform(delete("/admin/posts/{postId}", postId)
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), adminToken)));
  }

  ResultActions callLikePostApi(String memberToken, long postId)
      throws Exception {
    return mockMvc.perform(patch("/posts/{postId}/likes", postId)
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), memberToken)));
  }

  ResultActions callDislikePostApi(String memberToken, long postId)
      throws Exception {
    return mockMvc.perform(patch("/posts/{postId}/dislikes", postId)
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), memberToken)));
  }

  ResultActions callGetNoticePostsApi(String memberToken, long categoryId) throws Exception {
    return mockMvc.perform(get("/posts/notices")
        .param("categoryId", String.valueOf(categoryId))
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), memberToken)));
  }

  ResultActions callAddPostFilesApi(String accessToken, long postId, MockMultipartFile file)
      throws Exception {
    return mockMvc.perform(multipart("/posts/{postId}/files", postId)
        .file(file)
        .with(request -> {
          request.setMethod("POST");
          return request;
        })
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), accessToken))
        .contentType(MediaType.MULTIPART_FORM_DATA));
  }

  ResultActions callDeletePostFileApi(String accessToken, long postId, long fileId)
      throws Exception {
    return mockMvc.perform(delete("/posts/{postId}/files/{fileId}", postId, fileId)
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), accessToken)));
  }
}
