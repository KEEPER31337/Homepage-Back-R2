package com.keeper.homepage.domain.member.dao.type;

import static org.assertj.core.api.Assertions.assertThat;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.member.entity.type.MemberType;
import com.keeper.homepage.domain.member.entity.type.MemberType.MemberTypeEnum;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class MemberTypeRepositoryTest extends IntegrationTest {

  @Nested
  @DisplayName("회원 타입 테스트")
  class MemberRankTypeEnumTest {

    @Test
    @DisplayName("MemberType Enum에 DB 상의 모든 역할이 들어가 있어야 한다.")
    void should_allTypeExist_when_givenMemberTypeEnum() {
      List<MemberType> allMemberTypes = memberTypeRepository.findAll();
      List<MemberType> allMemberTypeEnums = Arrays.stream(MemberTypeEnum.values())
          .map(MemberType::getMemberTypeBy)
          .toList();

      assertThat(getId(allMemberTypes)).containsAll(getId(allMemberTypeEnums));
      assertThat(getName(allMemberTypes)).containsAll(getName(allMemberTypeEnums));
    }

    private static List<Long> getId(List<MemberType> allMemberTypes) {
      return allMemberTypes.stream()
          .map(MemberType::getId)
          .toList();
    }

    private static List<String> getName(List<MemberType> allMemberTypes) {
      return allMemberTypes.stream()
          .map(MemberType::getType)
          .map(MemberTypeEnum::name)
          .toList();
    }
  }
}
