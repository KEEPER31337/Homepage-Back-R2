package com.keeper.homepage.domain.about.api;

import static com.keeper.homepage.domain.about.entity.StaticWriteTitle.StaticWriteTitleType.ACTIVITY;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.keeper.homepage.IntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class StaticWriteControllerTest extends IntegrationTest {

  @Nested
  @DisplayName("타이틀 조회 테스트")
  class StaticWriteTitleTest {

    @Test
    @DisplayName("타이틀 타입 조회 시 모든 값이 성공적으로 조회되어야 한다.")
    void should_getAllTypesSuccessfully_when_getAllTypes() throws Exception {
      mockMvc.perform(get("/about/titles/types"))
          .andExpect(status().isOk())
          .andDo(document("about-title-types",
              responseFields(
                  fieldWithPath("list").description("타이틀 타입의 리스트 입니다.")
              )));
    }

    @Test
    @DisplayName("타이틀 타입으로 페이지 블럭 조회시 해당 타이틀의 페이지 블럭이 성공적으로 조회되어야 한다.")
    void should_getAllTitlesSuccessfully_when_getTitlesByType() throws Exception {
      String activity = ACTIVITY.getType();
      mockMvc.perform(get("/about/titles/types/{type}", activity))
          .andExpect(status().isOk())
          .andDo(document("about-title-by-type",
              pathParameters(
                  parameterWithName("type").description("찾고자 하는 페이지 블럭 타이틀의 타입")
              ),
              responseFields(
                  fieldWithPath("id").description("해당 타입과 일치하는 페이지 블럭 타이틀 ID"),
                  fieldWithPath("title").description("해당 타입과 일치하는 페이지 블럭 타이틀 제목"),
                  fieldWithPath("type").description("해당 타입과 일치하는 페이지 블럭 타이틀 타입"),
                  subsectionWithPath("subtitleImages")
                      .description("페이지 블럭 타이틀과 연결된 페이지 블럭 서브 타이틀 데이터 리스트"),
                  subsectionWithPath("subtitleImages[].id").description("페이지 블럭 서브 타이틀 ID"),
                  subsectionWithPath("subtitleImages[].subtitle").description("페이지 블럭 서브 타이틀 이름"),
                  subsectionWithPath("subtitleImages[].thumbnailPath")
                      .description("페이지 블럭 서브 타이틀 썸네일 경로"),
                  subsectionWithPath("subtitleImages[].displayOrder")
                      .description("페이지 블럭 서브 타이틀 우선순위"),
                  subsectionWithPath("subtitleImages[].staticWriteContents")
                      .description("페이지 블럭 서브 타이틀과 연결된 페이지 블럭 컨텐츠 리스트"),
                  subsectionWithPath("subtitleImages[].staticWriteContents[].id")
                      .description("페이지 블럭 컨텐츠 ID"),
                  subsectionWithPath("subtitleImages[].staticWriteContents[].content")
                      .description("페이지 블럭 컨텐츠 이름"),
                  subsectionWithPath("subtitleImages[].staticWriteContents[].displayOrder")
                      .description("페이지 블럭 컨텐츠 우선순위")
              )));
    }

    @Test
    @DisplayName("DB에서 찾을 수 없는 타입으로 타이틀 조회 시 404 Not Found를 반환해야 한다.")
    void should_404NotFound_when_getTitlesByNotFoundType() throws Exception {
      mockMvc.perform(get("/about/titles/types/{type}", "null"))
          .andExpect(status().isNotFound())
          .andExpect(content().string(containsString("해당 타입의 타이틀을 찾을 수 없습니다.")));
    }
  }

}
