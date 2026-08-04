package com.keeper.homepage.domain.vote.dto.response;

import com.keeper.homepage.domain.vote.entity.VoteChoice;
import com.keeper.homepage.domain.vote.entity.VoteReceipt;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record VoteResultReceiptResponse(
    UUID receiptToken,
    List<VoteResultChoiceResponse> choices
) {

  public static VoteResultReceiptResponse from(VoteReceipt receipt) {
    Map<Long, List<Long>> optionIdsByAgendaId = new LinkedHashMap<>();
    receipt.getChoices().stream()
        .sorted(Comparator
            .comparing((VoteChoice choice) -> choice.getOption().getAgenda().getDisplayOrder())
            .thenComparing(choice -> choice.getOption().getDisplayOrder()))
        .map(VoteChoice::getOption)
        .forEach(option -> optionIdsByAgendaId
            .computeIfAbsent(option.getAgenda().getId(), ignored -> new ArrayList<>())
            .add(option.getId()));

    List<VoteResultChoiceResponse> choices = optionIdsByAgendaId.entrySet().stream()
        .map(entry -> new VoteResultChoiceResponse(entry.getKey(), List.copyOf(entry.getValue())))
        .toList();
    return new VoteResultReceiptResponse(receipt.getToken(), choices);
  }
}
