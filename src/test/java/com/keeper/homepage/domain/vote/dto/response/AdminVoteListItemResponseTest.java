package com.keeper.homepage.domain.vote.dto.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.json.JsonMapper;

class AdminVoteListItemResponseTest {

  private final ObjectMapper objectMapper = JsonMapper.builderWithJackson2Defaults()
      .disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
      .build();

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
