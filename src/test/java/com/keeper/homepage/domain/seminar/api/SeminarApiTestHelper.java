package com.keeper.homepage.domain.seminar.api;

import static com.keeper.homepage.global.config.security.data.JwtType.ACCESS_TOKEN;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.seminar.dto.request.SeminarAttendanceCodeRequest;
import com.keeper.homepage.domain.seminar.dto.request.SeminarAttendanceStatusRequest;
import com.keeper.homepage.domain.seminar.dto.request.SeminarStartRequest;
import com.keeper.homepage.domain.seminar.dto.response.SeminarIdResponse;
import jakarta.servlet.http.Cookie;
import java.time.LocalDate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

public class SeminarApiTestHelper extends IntegrationTest {

  ResultActions createSeminarUsingApi(String token, LocalDate openDate) throws Exception {
    return mockMvc.perform(post("/seminars?openDate=" + openDate)
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), token)));
  }

  Long createSeminarAndGetId(String adminToken, LocalDate openDate) throws Exception {
    MvcResult mvcResult = createSeminarUsingApi(adminToken, openDate)
        .andDo(print())
        .andExpect(status().isCreated())
        .andReturn();
    Long seminarId = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
        SeminarIdResponse.class).id();
    return seminarId;
  }

  ResultActions startSeminarUsingApi(String token, Long seminarId, SeminarStartRequest request)
      throws Exception {
    return mockMvc.perform(post("/seminars/{seminarId}", seminarId)
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), token))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)));
  }

  ResultActions startSeminarUsingApi(String token, Long seminarId, String strJson)
      throws Exception {
    return mockMvc.perform(post("/seminars/{seminarId}", seminarId)
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), token))
        .contentType(MediaType.APPLICATION_JSON)
        .content(strJson));
  }

  ResultActions searchAllSeminarUsingApi(String token) throws Exception {
    return mockMvc.perform(get("/seminars")
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), token)));
  }

  ResultActions searchAvailableSeminarUsingApi(String token) throws Exception {
    return mockMvc.perform(get("/seminars/available")
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), token)));
  }

  ResultActions searchSeminarUsingApi(String token, Long seminarId) throws Exception {
    return mockMvc.perform(get("/seminars/{seminarId}", seminarId)
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), token)));
  }

  ResultActions searchDateSeminarUsingApi(String token, String date) throws Exception {
    return mockMvc.perform(get("/seminars?date=" + date)
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), token)));
  }

  ResultActions deleteSeminarUsingApi(String token, Long seminarId) throws Exception {
    return mockMvc.perform(delete("/seminars/{seminarId}", seminarId)
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), token)));
  }

  ResultActions attendanceSeminarUsingApi(String token, Long seminarId,
      SeminarAttendanceCodeRequest request)
      throws Exception {
    return mockMvc.perform(patch("/seminars/{seminarId}/attendances", seminarId)
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), token))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)));
  }

  ResultActions attendanceSeminarUsingApi(String token, Long seminarId, String strJson)
      throws Exception {
    return mockMvc.perform(patch("/seminars/{seminarId}/attendances", seminarId)
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), token))
        .contentType(MediaType.APPLICATION_JSON)
        .content(strJson));
  }

  ResultActions changeAttendanceStatusUsingApi(String token, long attendanceId,
      SeminarAttendanceStatusRequest request)
      throws Exception {
    return mockMvc.perform(patch("/seminars/attendances/{attendanceId}", attendanceId)
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), token))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)));
  }

  ResultActions changeAttendanceStatusUsingApi(String token, long attendanceId, String strJson)
      throws Exception {
    return mockMvc.perform(patch("/seminars/attendances/{attendanceId}", attendanceId)
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), token))
        .contentType(MediaType.APPLICATION_JSON)
        .content(strJson));
  }
}
