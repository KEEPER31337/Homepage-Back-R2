package com.keeper.homepage.domain.merit.api;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import com.keeper.homepage.IntegrationTest;
import org.springframework.restdocs.payload.FieldDescriptor;

public class MeritApiTestHelper extends IntegrationTest {

  FieldDescriptor[] getMeritLogResponse() {
    return new FieldDescriptor[]{
        fieldWithPath("id").description("상벌점 로그의 ID"),
        fieldWithPath("giveTime").description("상벌점 로그의 생성시간"),
        fieldWithPath("awarderName").description("수상자의 이름"),
        fieldWithPath("awarderGeneration").description("수상자의 학번"),
        fieldWithPath("score").description("상벌점 점수"),
        fieldWithPath("meritTypeId").description("상벌점 타입의 ID"),
        fieldWithPath("reason").description("상벌점의 사유")
    };
  }


  FieldDescriptor[] getAwarderMeritLogResponse() {
    return new FieldDescriptor[]{
        fieldWithPath("id").description("상벌점 로그의 ID"),
        fieldWithPath("giveTime").description("상벌점 로그의 생성시간"),
        fieldWithPath("score").description("상벌점 점수"),
        fieldWithPath("meritTypeId").description("상벌점 타입의 ID"),
        fieldWithPath("reason").description("상벌점의 사유")
    };
  }

}
