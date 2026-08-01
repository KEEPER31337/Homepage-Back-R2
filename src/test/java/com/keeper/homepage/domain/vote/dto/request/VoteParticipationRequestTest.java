package com.keeper.homepage.domain.vote.dto.request;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class VoteParticipationRequestTest {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void mapsSnakeCaseRequestFields() throws Exception {
    String json = """
        {
          "receipt_token": "123e4567-e89b-42d3-a456-426614174000",
          "selections": [
            {
              "agenda_id": 20,
              "option_ids": [101, 102]
            }
          ]
        }
        """;

    VoteParticipationRequest request = objectMapper.readValue(
        json, VoteParticipationRequest.class);

    assertThat(request.receiptToken())
        .isEqualTo(UUID.fromString("123e4567-e89b-42d3-a456-426614174000"));
    assertThat(request.selections()).containsExactly(
        new VoteSelectionRequest(20L, List.of(101L, 102L)));
    assertThat(validator.validate(request)).isEmpty();
  }

  @Test
  void rejectsUuidThatIsNotVersionFour() {
    VoteParticipationRequest request = new VoteParticipationRequest(
        UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),
        List.of(new VoteSelectionRequest(20L, List.of(101L)))
    );

    assertThat(validator.validate(request))
        .anyMatch(violation -> violation.getMessage()
            .equals("영수증 토큰은 UUID v4 형식이어야 합니다."));
  }

  @Test
  void receiptCheckAlsoRejectsUuidThatIsNotVersionFour() {
    VoteReceiptCheckRequest request = new VoteReceiptCheckRequest(
        UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));

    assertThat(validator.validate(request))
        .anyMatch(violation -> violation.getMessage()
            .equals("영수증 토큰은 UUID v4 형식이어야 합니다."));
  }
}
