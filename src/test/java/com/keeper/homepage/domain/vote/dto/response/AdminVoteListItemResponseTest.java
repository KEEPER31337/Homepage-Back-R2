package com.keeper.homepage.domain.vote.dto.response;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class AdminVoteListItemResponseTest {

  private final ObjectMapper objectMapper = new ObjectMapper()
      .registerModule(new JavaTimeModule())
      .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

  @Test
  void serializeWithPermitUserIdsAndParticipantCount() throws Exception {
    AdminVoteListItemResponse response = new AdminVoteListItemResponse(
        42L,
        "회장 선거",
        "설명",
        LocalDateTime.of(2026, 8, 1, 0, 0),
        LocalDateTime.of(2026, 8, 2, 0, 0),
        List.of(16381L, 26381L),
        3L
    );

    String json = objectMapper.writeValueAsString(response);

    assertThat(json)
        .contains("\"permitByUserIds\":[16381,26381]")
        .contains("\"participantCount\":3");
  }
}
