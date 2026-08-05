package com.keeper.homepage.domain.vote.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

public record VoteCreateRequest(
    @NotBlank(message = "투표 제목을 입력해주세요.")
    @Size(max = 500, message = "투표 제목은 {max}자 이하로 입력해주세요.")
    String title,

    @Size(max = 16_383, message = "투표 설명은 {max}자 이하로 입력해주세요.")
    String description,

    @NotNull(message = "투표 허용 회원 ID 목록을 입력해주세요.")
    List<@NotNull(message = "회원 ID는 null일 수 없습니다.")
        @Positive(message = "회원 ID는 양수여야 합니다.") Long> permitByUserIds,

    @NotNull(message = "투표 시작 시각을 입력해주세요.")
    LocalDateTime startAt,

    @NotNull(message = "투표 종료 시각을 입력해주세요.")
    LocalDateTime endAt,

    @NotEmpty(message = "안건을 한 개 이상 입력해주세요.")
    List<@NotNull(message = "안건은 null일 수 없습니다.") @Valid VoteAgendaCreateRequest> agendas
) {

  @JsonIgnore
  @AssertTrue(message = "투표 시작 시각은 종료 시각보다 빨라야 합니다.")
  public boolean isPeriodValid() {
    if (startAt == null || endAt == null) {
      return true;
    }
    return startAt.isBefore(endAt);
  }
}
