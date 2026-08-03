package com.keeper.homepage.domain.vote.dto.response;

import com.keeper.homepage.domain.vote.entity.VoteAgenda;
import com.keeper.homepage.domain.vote.entity.VoteOption;
import com.keeper.homepage.domain.vote.entity.VoteReceipt;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record VoteParticipationResponse(
    UUID receiptToken,
    List<VoteReceiptSelectionResponse> selections
) {

  public static VoteParticipationResponse from(
      VoteReceipt receipt,
      List<VoteOption> selectedOptions
  ) {
    Map<Long, VoteAgenda> agendasById = new LinkedHashMap<>();
    Map<Long, List<VoteReceiptOptionResponse>> optionsByAgendaId = new LinkedHashMap<>();
    selectedOptions.stream()
        .sorted(Comparator
            .comparing((VoteOption option) -> option.getAgenda().getDisplayOrder())
            .thenComparing(VoteOption::getDisplayOrder))
        .forEach(option -> {
          VoteAgenda agenda = option.getAgenda();
          agendasById.putIfAbsent(agenda.getId(), agenda);
          optionsByAgendaId.computeIfAbsent(agenda.getId(), ignored -> new ArrayList<>())
              .add(VoteReceiptOptionResponse.from(option));
        });

    List<VoteReceiptSelectionResponse> selections = optionsByAgendaId.entrySet().stream()
        .map(entry -> VoteReceiptSelectionResponse.from(
            agendasById.get(entry.getKey()), entry.getValue()))
        .toList();
    return new VoteParticipationResponse(receipt.getToken(), selections);
  }
}
