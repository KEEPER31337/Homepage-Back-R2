package com.keeper.homepage.domain.vote.dto.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.json.JsonMapper;

class VoteListItemResponseTest {

  private final ObjectMapper objectMapper = JsonMapper.builderWithJackson2Defaults()
      .disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
      .build();

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
