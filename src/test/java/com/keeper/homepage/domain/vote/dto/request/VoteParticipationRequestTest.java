package com.keeper.homepage.domain.vote.dto.request;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.List;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

class VoteParticipationRequestTest {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
  private final ObjectMapper objectMapper = JsonMapper.builderWithJackson2Defaults().build();

  @Test
  void mapsCamelCaseSelectionFieldsWithoutReceiptToken() throws Exception {
    String json = """
        {
          "selections": [
            {
              "agendaId": 20,
              "optionIds": [101, 102]
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
