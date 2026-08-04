package com.keeper.homepage.domain.vote.dto.response;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class VoteResultResponseTest {

  private final ObjectMapper objectMapper = new ObjectMapper()
      .registerModule(new JavaTimeModule())
      .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

  @Test
  void serializeResultStructureInCamelCase() throws Exception {
    VoteResultResponse response = new VoteResultResponse(
        List.of(new VoteResultParticipationResponse("홍길동", "17.5")),
        List.of(new VoteResultReceiptResponse(
            UUID.fromString("123e4567-e89b-42d3-a456-426614174000"),
            List.of(new VoteResultChoiceResponse(20L, List.of(101L, 102L))))),
        new VoteDetailResponse(
            42L,
            "회장 선거",
            "설명",
            LocalDateTime.of(2026, 8, 1, 0, 0),
            LocalDateTime.of(2026, 8, 2, 0, 0),
            List.of())
    );

    String json = objectMapper.writeValueAsString(response);

    assertThat(json)
        .contains("\"participations\":[{\"realName\":\"홍길동\",\"generation\":\"17.5\"}]")
        .contains("\"receiptTokenChoices\":[{\"receiptToken\":"
            + "\"123e4567-e89b-42d3-a456-426614174000\"")
        .contains("\"choices\":[{\"agendaId\":20,\"optionIds\":[101,102]}]")
        .contains("\"vote\":{\"id\":42");
  }
}
