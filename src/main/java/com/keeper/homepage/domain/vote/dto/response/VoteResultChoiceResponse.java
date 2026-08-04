package com.keeper.homepage.domain.vote.dto.response;

import java.util.List;

public record VoteResultChoiceResponse(
    Long agendaId,
    List<Long> optionIds
) {

}
