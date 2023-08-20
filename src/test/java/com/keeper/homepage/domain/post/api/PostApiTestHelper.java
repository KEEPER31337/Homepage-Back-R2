package com.keeper.homepage.domain.post.api;

import static com.keeper.homepage.global.config.security.data.JwtType.ACCESS_TOKEN;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.post.dto.request.PostUpdateRequest;
import jakarta.servlet.http.Cookie;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.MultiValueMap;

public class PostApiTestHelper extends IntegrationTest {

  ResultActions callCreatePostApiWithFile(String accessToken, MockMultipartFile file, MockPart mockPart)
      throws Exception {
    return mockMvc.perform(multipart("/posts")
        .file(file)
        .part(mockPart)
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), accessToken))
        .contentType(MediaType.MULTIPART_FORM_DATA));
  }

  ResultActions callCreatePostApiWithFiles(String accessToken,
      MockMultipartFile thumbnail, MockMultipartFile file, MockPart mockPart) throws Exception {
    return mockMvc.perform(multipart("/posts")
        .file(thumbnail)
        .file(file)
        .part(mockPart)
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), accessToken))
        .contentType(MediaType.MULTIPART_FORM_DATA));
  }

  ResultActions callCreatePostApi(String accessToken, MockPart mockPart)
      throws Exception {
    return mockMvc.perform(multipart("/posts")
        .part(mockPart)
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

  ResultActions callUpdatePostApi(String accessToken, long postId, PostUpdateRequest request)
      throws Exception {
    return mockMvc.perform(put("/posts/{postId}", postId)
        .content(asJsonString(request))
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), accessToken))
        .contentType(MediaType.APPLICATION_JSON));
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

  ResultActions callGetPostsApi(String memberToken, MultiValueMap<String, String> params) throws Exception {
    return mockMvc.perform(get("/posts")
        .params(params)
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), memberToken)));
  }

  FieldDescriptor[] getPostsResponse() {
    return new FieldDescriptor[]{
        fieldWithPath("id").description("게시글 ID"),
        fieldWithPath("title").description("게시글 제목"),
        fieldWithPath("writerName").description("게시글 작성자 실명"),
        fieldWithPath("writerThumbnailPath").description("게시글 작성자 썸네일 주소").optional(),
        fieldWithPath("visitCount").description("게시글 조회수"),
        fieldWithPath("commentCount").description("게시글 댓글수"),
        fieldWithPath("isSecret").description("비밀글 여부"),
        fieldWithPath("thumbnailPath").description("게시글 썸네일 주소").optional(),
        fieldWithPath("likeCount").description("게시글 좋아요 수"),
        fieldWithPath("registerTime").description("게시글 등록 시간")
    };
  }

  FieldDescriptor[] getMainPostsResponse() {
    return new FieldDescriptor[]{
        fieldWithPath("id").description("게시글 ID"),
        fieldWithPath("title").description("게시글 제목"),
        fieldWithPath("writerName").description("게시글 작성자 실명"),
        fieldWithPath("writerThumbnailPath").description("게시글 작성자 썸네일 주소").optional(),
        fieldWithPath("categoryId").description("카테고리 ID"),
        fieldWithPath("categoryName").description("카테고리 이름"),
        fieldWithPath("visitCount").description("게시글 조회수"),
        fieldWithPath("isSecret").description("비밀글 여부"),
        fieldWithPath("thumbnailPath").description("게시글 썸네일 주소").optional(),
        fieldWithPath("registerTime").description("게시글 등록 시간")
    };
  }

  FieldDescriptor[] getMemberPostsResponse() {
    return new FieldDescriptor[]{
        fieldWithPath("id").description("게시글 ID"),
        fieldWithPath("title").description("게시글 제목"),
        fieldWithPath("categoryId").description("게시글 카테고리 ID"),
        fieldWithPath("categoryName").description("게시글 카테고리 이름"),
        fieldWithPath("visitCount").description("게시글 조회수"),
        fieldWithPath("isSecret").description("비밀글 여부"),
        fieldWithPath("registerTime").description("게시글 등록 시간")
    };
  }

  FieldDescriptor[] getTempPostsResponse() {
    return new FieldDescriptor[]{
        fieldWithPath("id").description("게시글 ID"),
        fieldWithPath("title").description("게시글 제목"),
        fieldWithPath("categoryId").description("게시글 카테고리 ID"),
        fieldWithPath("categoryName").description("게시글 카테고리 이름"),
        fieldWithPath("registerTime").description("게시글 등록 시간")
    };
  }
}
