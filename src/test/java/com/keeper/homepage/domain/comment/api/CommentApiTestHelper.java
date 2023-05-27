package com.keeper.homepage.domain.comment.api;

import static com.keeper.homepage.global.config.security.data.JwtType.ACCESS_TOKEN;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.comment.dto.request.CommentCreateRequest;
import jakarta.servlet.http.Cookie;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.web.servlet.ResultActions;

public class CommentApiTestHelper extends IntegrationTest {

  ResultActions callCreateCommentApi(String memberToken, CommentCreateRequest request) throws Exception {
    return mockMvc.perform(post("/comments")
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), memberToken))
        .content(asJsonString(request))
        .contentType(APPLICATION_JSON));
  }

  ResultActions callGetCommentsApi(String memberToken, long postId) throws Exception {
    return mockMvc.perform(get("/comments/posts/{postId}", postId)
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), memberToken)));
  }

  FieldDescriptor[] getCommentsResponse() {
    return new FieldDescriptor[]{
        fieldWithPath("writerName").description("댓글 작성자 이름"),
        fieldWithPath("writerThumbnailPath").description("댓글 작성자의 썸네일 경로"),
        fieldWithPath("content").description("댓글 내용"),
        fieldWithPath("registerTime").description("댓글 등록 시간"),
        fieldWithPath("parentId").description("부모 댓글 ID"),
        fieldWithPath("likeCount").description("댓글 좋아요 개수"),
        fieldWithPath("dislikeCount").description("댓글 싫어요 개수")
    };
  }

  ResultActions callUpdateCommentApi(String memberToken, long commentId, String content) throws Exception {
    return mockMvc.perform(put("/comments/{commentId}", commentId)
        .queryParam("content", content)
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), memberToken)));
  }

  ResultActions callDeleteCommentApi(String memberToken, long commentId) throws Exception {
    return mockMvc.perform(delete("/comments/{commentId}", commentId)
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), memberToken)));
  }

  ResultActions callLikeCommentApi(String memberToken, long commentId) throws Exception {
    return mockMvc.perform(patch("/comments/{commentId}/likes", commentId)
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), memberToken)));
  }

  ResultActions callDislikeCommentApi(String memberToken, long commentId) throws Exception {
    return mockMvc.perform(patch("/comments/{commentId}/dislikes", commentId)
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), memberToken)));
  }
}
