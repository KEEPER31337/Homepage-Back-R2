package com.keeper.homepage.domain.comment.api;

import static com.keeper.homepage.global.config.security.session.SessionPolicy.SESSION_COOKIE_NAME;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.comment.dto.request.CommentCreateRequest;
import jakarta.servlet.http.Cookie;
import org.springframework.test.web.servlet.ResultActions;

public class CommentApiTestHelper extends IntegrationTest {

  ResultActions callCreateCommentApi(String memberSessionId, CommentCreateRequest request) throws Exception {
    return mockMvc.perform(post("/comments")
        .cookie(new Cookie(SESSION_COOKIE_NAME, memberSessionId))
        .content(asJsonString(request))
        .contentType(APPLICATION_JSON));
  }

  ResultActions callGetCommentsApi(String memberSessionId, long postId) throws Exception {
    return mockMvc.perform(get("/comments/posts/{postId}", postId)
        .cookie(new Cookie(SESSION_COOKIE_NAME, memberSessionId)));
  }

  ResultActions callDeleteCommentApi(String memberSessionId, long commentId) throws Exception {
    return mockMvc.perform(delete("/comments/{commentId}", commentId)
        .cookie(new Cookie(SESSION_COOKIE_NAME, memberSessionId)));
  }

  ResultActions callLikeCommentApi(String memberSessionId, long commentId) throws Exception {
    return mockMvc.perform(patch("/comments/{commentId}/likes", commentId)
        .cookie(new Cookie(SESSION_COOKIE_NAME, memberSessionId)));
  }

  ResultActions callDislikeCommentApi(String memberSessionId, long commentId) throws Exception {
    return mockMvc.perform(patch("/comments/{commentId}/dislikes", commentId)
        .cookie(new Cookie(SESSION_COOKIE_NAME, memberSessionId)));
  }
}
