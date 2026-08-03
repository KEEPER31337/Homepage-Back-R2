package com.keeper.homepage.domain.vote.dto.request;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.List;
import org.junit.jupiter.api.Test;

class VoteParticipationRequestTest {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void mapsSnakeCaseSelectionFieldsWithoutReceiptToken() throws Exception {
    String json = """
        {
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

    assertThat(request.selections()).containsExactly(
        new VoteSelectionRequest(20L, List.of(101L, 102L)));
    assertThat(validator.validate(request)).isEmpty();
  }

}
