package com.keeper.homepage.domain.member.application;

import static com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_출제자;
import static com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_회원;
import static com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_회장;
import static org.assertj.core.api.Assertions.assertThat;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.member.dto.response.MemberJobResponse;
import com.keeper.homepage.domain.member.entity.Member;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class MemberJobServiceTest extends IntegrationTest {

  @Nested
  @DisplayName("임원진 조회 테스트")
  class GetExecutive {

    @Test
    @DisplayName("임원진 직책 회원만 조회되어야 한다. (출제자와 일반 회원은 조회되면 안된다.)")
    public void 임원진_직책_회원만_조회되어야_한다() throws Exception {
      Member 회장 = memberTestHelper.generate();
      Member 출제자 = memberTestHelper.generate();
      Member 회원 = memberTestHelper.generate();
      회장.assignJob(ROLE_회장);
      출제자.assignJob(ROLE_출제자);
      회원.assignJob(ROLE_회원);

      em.flush();
      em.clear();
      List<MemberJobResponse> executives = memberJobService.getExecutives();

      List<Long> results = executives.stream()
          .map(MemberJobResponse::getJobId)
          .toList();
      assertThat(results).contains(ROLE_회장.getId());
      assertThat(results).doesNotContain(ROLE_출제자.getId());
      assertThat(results).doesNotContain(ROLE_회원.getId());
    }
  }
}
