package com.keeper.homepage.domain.about.api;

import static com.keeper.homepage.domain.auth.application.EmailAuthService.EMAIL_EXPIRED_SECONDS;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.keeper.homepage.IntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class StaticWriteControllerTest extends IntegrationTest {

  @Nested
  @DisplayName("타이틀 타입 조회 테스트")
  class StaticWriteTitleTest {

    @Test
    @DisplayName("타이틀 타입 조회 시 모든 값이 성공적으로 조회되어야 한다.")
    void should_getAllTypesSuccessfully_when_getAllTypes() throws Exception {
      mockMvc.perform(get("/about/types"))
          .andExpect(status().isOk())
          .andDo(document("about-types",
              responseFields(
                  fieldWithPath("list").description("타이틀 타입의 리스트 입니다.")
              )));
    }
  }

}
