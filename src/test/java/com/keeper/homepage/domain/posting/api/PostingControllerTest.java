package com.keeper.homepage.domain.posting.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.posting.dto.request.PostRequest;
import com.keeper.homepage.domain.posting.entity.category.Category;
import jakarta.servlet.http.Cookie;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.multipart.MultipartFile;

public class PostingControllerTest extends IntegrationTest {

  private Member member;
  private Cookie[] tokenCookies;
  private Category category;

  @BeforeEach
  void setUp() {
    member = memberTestHelper.generate();
    tokenCookies = memberTestHelper.getTokenCookies(member);
    category = categoryTestHelper.generate();
  }

  @Nested
  @DisplayName("게시글 생성")
  class CreatePost {

    @Test
    @DisplayName("게시글을 생성하면 201 CREATED를 반환한다.")
    void should_201CREATED_when_createPost() throws Exception {
      PostRequest postRequest = PostRequest.builder()
          .title("게시글 제목")
          .content("게시글 내용")
          .allowComment(true)
          .isNotice(true)
          .isSecret(true)
          .isTemp(true)
          .password("게시글 암호")
          .categoryId(category.getId())
          .build();

      MockMultipartFile thumbnailFile = thumbnailTestHelper.getThumbnailFile();

      MockMultipartFile files = new MockMultipartFile("files",
          "testImage_210x210.png", "image/png",
          new FileInputStream("src/test/resources/images/testImage_210x210.png"));

      mockMvc.perform(multipart("/postings/create")
              .file(thumbnailFile)
              .file(files)
              .param("title", postRequest.getTitle())
              .param("content", postRequest.getContent())
              .param("allowComment", postRequest.getAllowComment().toString())
              .param("isNotice", postRequest.getIsNotice().toString())
              .param("isSecret", postRequest.getIsSecret().toString())
              .param("isTemp", postRequest.getIsTemp().toString())
              .param("password", postRequest.getPassword())
              .param("categoryId", postRequest.getCategoryId().toString())
              .with(request -> {
                request.setMethod("POST");
                return request;
              })
              .contentType(MediaType.MULTIPART_FORM_DATA)
              .cookie(tokenCookies))
          .andDo(print())
          .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("게시글 제목이 빈칸일 수 없다.")
    void should_notBlank_when_titleBlank() {

    }

    @Test
    @DisplayName("게시글 제목은 250자를 넘길 수 없다.")
    void noooo() {

    }

    @Test
    @DisplayName("게시글 비밀번호는 512자를 넘길 수 없다.")
    void no() {

    }

    @Test
    @DisplayName("카테고리가 null 일 수 없다.")
    void notCategoryNull() {

    }
  }
}
