package com.keeper.homepage.domain.vote.dto.request;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class VoteCreateRequestTest {

  private static ValidatorFactory validatorFactory;
  private static Validator validator;

  @BeforeAll
  static void setUpValidator() {
    validatorFactory = Validation.buildDefaultValidatorFactory();
    validator = validatorFactory.getValidator();
  }

  @AfterAll
  static void closeValidatorFactory() {
    validatorFactory.close();
  }

  @Test
  void validateValidRequest() {
    VoteCreateRequest request = validRequest();

    Set<ConstraintViolation<VoteCreateRequest>> violations = validator.validate(request);

    assertThat(violations).isEmpty();
  }

  @Test
  void rejectWhenStartAtIsNotBeforeEndAt() {
    VoteCreateRequest valid = validRequest();
    VoteCreateRequest request = new VoteCreateRequest(
        valid.title(),
        valid.description(),
        valid.permitByUserIds(),
        valid.endAt(),
        valid.endAt(),
        valid.agendas()
    );

    Set<ConstraintViolation<VoteCreateRequest>> violations = validator.validate(request);

    assertThat(violations)
        .extracting(ConstraintViolation::getMessage)
        .contains("투표 시작 시각은 종료 시각보다 빨라야 합니다.");
  }

  @Test
  void rejectWhenDescriptionIsTooLong() {
    VoteCreateRequest valid = validRequest();
    VoteCreateRequest request = new VoteCreateRequest(
        valid.title(),
        "가".repeat(16_384),
        valid.permitByUserIds(),
        valid.startAt(),
        valid.endAt(),
        valid.agendas()
    );

    Set<ConstraintViolation<VoteCreateRequest>> violations = validator.validate(request);

    assertThat(violations)
        .extracting(ConstraintViolation::getMessage)
        .contains("투표 설명은 16383자 이하로 입력해주세요.");
  }

  @Test
  void rejectWhenMaxSelectExceedsOptionCount() {
    VoteCreateRequest valid = validRequest();
    VoteAgendaCreateRequest invalidAgenda = new VoteAgendaCreateRequest(
        "회장 선출",
        1,
        3,
        valid.agendas().getFirst().options()
    );
    VoteCreateRequest request = new VoteCreateRequest(
        valid.title(),
        valid.description(),
        valid.permitByUserIds(),
        valid.startAt(),
        valid.endAt(),
        List.of(invalidAgenda)
    );

    Set<ConstraintViolation<VoteCreateRequest>> violations = validator.validate(request);

    assertThat(violations)
        .extracting(ConstraintViolation::getMessage)
        .contains("최소 선택 수는 최대 선택 수 이하이고, 최대 선택 수는 선택지 수 이하여야 합니다.");
  }

  @Test
  void rejectNonPositiveMemberId() {
    VoteCreateRequest valid = validRequest();
    VoteCreateRequest request = new VoteCreateRequest(
        valid.title(),
        valid.description(),
        List.of(0L),
        valid.startAt(),
        valid.endAt(),
        valid.agendas()
    );

    Set<ConstraintViolation<VoteCreateRequest>> violations = validator.validate(request);

    assertThat(violations)
        .extracting(ConstraintViolation::getMessage)
        .contains("회원 ID는 양수여야 합니다.");
  }

  private static VoteCreateRequest validRequest() {
    VoteAgendaCreateRequest agenda = new VoteAgendaCreateRequest(
        "회장 선출",
        1,
        1,
        List.of(
            new VoteOptionCreateRequest("후보 A"),
            new VoteOptionCreateRequest("후보 B")
        )
    );
    return new VoteCreateRequest(
        "2026년 회장 선거",
        "2026년도 회장을 선출하기 위한 투표입니다.",
        List.of(16381L, 26381L),
        LocalDateTime.of(2026, 8, 1, 0, 0),
        LocalDateTime.of(2026, 8, 2, 0, 0),
        List.of(agenda)
    );
  }
}
