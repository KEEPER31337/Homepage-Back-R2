package com.keeper.homepage.domain.seminar.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.seminar.entity.SeminarAttendanceStatus;
import com.keeper.homepage.domain.seminar.entity.SeminarAttendanceStatus.SeminarAttendanceStatusType;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class SeminarAttendanceStatusRepositoryTest extends IntegrationTest {

  @Nested
  @DisplayName("세미나 출석 타입 테스트")
  class SeminarAttendanceStatusTypeTest {

    @Test
    @DisplayName("DB에 세미나 출석에 필요한 모든 타입이 있어야 한다.")
    void should_dbAllTypeExist_when_givenSeminarAttendanceStatusTypeEnum() {
      List<SeminarAttendanceStatus> dbAllStatusTypes = seminarAttendanceStatusRepository.findAll();

      List<SeminarAttendanceStatus> allStatusTypesEnum = Arrays.stream(
              SeminarAttendanceStatusType.values())
          .map(SeminarAttendanceStatus::getSeminarAttendanceStatusBy)
          .toList();

      assertThat(getId(dbAllStatusTypes)).containsAll(getId(allStatusTypesEnum));
      assertThat(getName(dbAllStatusTypes)).containsAll(getName(allStatusTypesEnum));
      assertThat(getType(dbAllStatusTypes)).containsAll(getType(allStatusTypesEnum));
    }

    private static List<Long> getId(List<SeminarAttendanceStatus> typesList) {
      return typesList.stream()
          .map(SeminarAttendanceStatus::getId)
          .toList();
    }

    private static List<String> getName(List<SeminarAttendanceStatus> typesList) {
      return typesList.stream()
          .map(SeminarAttendanceStatus::toString)
          .toList();
    }

    private static List<SeminarAttendanceStatusType> getType(
        List<SeminarAttendanceStatus> typesList) {
      return typesList.stream()
          .map(SeminarAttendanceStatus::getType)
          .toList();
    }

    @Test
    @DisplayName("DB에 존재하지 않는 타입은 Exception이 발생한다.")
    void should_throwBusinessException_when_NotFoundType() {
      assertThatThrownBy(() -> SeminarAttendanceStatusType.fromCode("null"))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("존재하지 않는 세미나 타입입니다.");
    }
  }
}
