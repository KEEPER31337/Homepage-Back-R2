package com.keeper.homepage.domain.vote.dto.response;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class VoteListItemResponseTest {

  private final ObjectMapper objectMapper = new ObjectMapper()
      .registerModule(new JavaTimeModule())
      .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

  @Test
  void serializeWithoutPermitFields() throws Exception {
    VoteListItemResponse response = new VoteListItemResponse(
        1L,
        "회장 선거",
        "설명",
        LocalDateTime.of(2026, 8, 1, 0, 0),
        LocalDateTime.of(2026, 8, 2, 0, 0),
        3
    );

    String json = objectMapper.writeValueAsString(response);

    assertThat(json)
        .contains("\"startAt\":\"2026-08-01T00:00:00\"")
        .contains("\"endAt\":\"2026-08-02T00:00:00\"")
        .contains("\"participated\":3")
        .doesNotContain("permitByUserIds", "participantCount");
  }
}
