package com.keeper.homepage.domain.member.dao.role;

import static org.assertj.core.api.Assertions.assertThat;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.member.entity.job.MemberJob;
import com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class MemberJobRepositoryTest extends IntegrationTest {

  @Nested
  @DisplayName("회원 역할 테스트")
  class MemberRankTypeEnumTest {

    @Test
    @DisplayName("MemberJobType Enum에 DB 상의 모든 역할이 들어가 있어야 한다.")
    void should_allTypeExist_when_givenMemberJobTypeEnum() {
      List<MemberJob> allMemberJobs = memberJobRepository.findAll();
      List<MemberJob> allMemberJobTypes = Arrays.stream(MemberJobType.values())
          .map(MemberJob::getMemberJobBy)
          .toList();

      assertThat(getId(allMemberJobs)).containsAll(getId(allMemberJobTypes));
      assertThat(getName(allMemberJobs)).containsAll(getName(allMemberJobTypes));
    }

    private static List<Long> getId(List<MemberJob> allMemberJobs) {
      return allMemberJobs.stream()
          .map(MemberJob::getId)
          .toList();
    }

    private static List<String> getName(List<MemberJob> allMemberJobs) {
      return allMemberJobs.stream()
          .map(MemberJob::getType)
          .map(MemberJobType::name)
          .toList();
    }
  }
}
