package com.keeper.homepage.domain.member.dao.rank;

import static org.assertj.core.api.Assertions.assertThat;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.member.entity.rank.MemberRank;
import com.keeper.homepage.domain.member.entity.rank.MemberRank.MemberRankType;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class MemberRankRepositoryTest extends IntegrationTest {

  @Nested
  @DisplayName("회원 랭크 테스트")
  class MemberRankTypeEnumTest {

    @Test
    @DisplayName("MemberRankType Enum에 DB 상의 모든 역할이 들어가 있어야 한다.")
    void should_allTypeExist_when_givenMemberRankTypeEnum() {
      List<MemberRank> allMemberRanks = memberRankRepository.findAll();
      List<MemberRank> allMemberRankTypes = Arrays.stream(MemberRankType.values())
          .map(MemberRank::getMemberRankBy)
          .toList();

      assertThat(getId(allMemberRanks)).containsAll(getId(allMemberRankTypes));
      assertThat(getName(allMemberRanks)).containsAll(getName(allMemberRankTypes));
    }

    private static List<Long> getId(List<MemberRank> allMemberRanks) {
      return allMemberRanks.stream()
          .map(MemberRank::getId)
          .toList();
    }

    private static List<String> getName(List<MemberRank> allMemberRanks) {
      return allMemberRanks.stream()
          .map(MemberRank::getType)
          .map(MemberRankType::name)
          .toList();
    }
  }
}
