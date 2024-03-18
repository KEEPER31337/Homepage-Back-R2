package com.keeper.homepage.domain.merit.dao;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.merit.dto.response.MeritLogsGroupByMemberResponse;
import com.keeper.homepage.domain.merit.entity.MeritLog;
import com.keeper.homepage.domain.merit.entity.MeritType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public class MeritLogRepositoryTest extends IntegrationTest {

  @Nested
  @DisplayName("상벌점 기능 테스트")
  class MeritLogSaveTest {

    @Test
    @DisplayName("DB에 상벌점 로그 등록을 성공해야 한다.")
    void should_success_when_registerMeritType() {
      MeritLog meritLog = meritLogTestHelper.generate();

      em.flush();
      em.clear();

      MeritLog findMeritLog = meritLogRepository.findById(meritLog.getId()).orElseThrow();

      assertThat(meritLog.getId()).isEqualTo(findMeritLog.getId());
      assertThat(findMeritLog.getTime()).isNotNull();
      assertThat(meritLog.getMeritType().getId()).isEqualTo(findMeritLog.getMeritType().getId());
    }
  }

  @Nested
  @DisplayName("상벌점 로그 조회 테스트")
  class MeritLogSearchTest {

    @Test
    @DisplayName("상벌점 로그를 수여자 ID로 조회할 수 있어야 한다.")
    void 상벌점_로그를_수여자_ID로_조회할_수_있어야_한다() {
      Member giver = memberTestHelper.generate();
      Long meritLogIdByGiver = meritLogTestHelper.builder().memberId(giver.getId()).build().getId();
      meritLogTestHelper.generate();
      meritLogTestHelper.generate();

      em.flush();
      em.clear();

      Page<MeritLog> pages = meritLogRepository.findAllByMemberId(PageRequest.of(0, 10),
          giver.getId());

      assertThat(pages.stream().map(MeritLog::getId).collect(toList()))
          .contains(meritLogIdByGiver);

    }
  }

  @Nested
  @DisplayName("회원 통계 상벌점 목록 조회 테스트")
  class GetTotalMeritLogsTest {

    private MeritType meritType, demeritType;
    private Member member, otherMember;

    @BeforeEach
    void setUp() {
      meritType = meritTypeHelper.builder().merit(5).isMerit(true).build();
      demeritType = meritTypeHelper.builder().merit(-3).isMerit(false).build();
      member = memberTestHelper.generate();
      otherMember = memberTestHelper.generate();

      meritLogTestHelper.builder()
          .memberId(member.getId())
          .memberRealName(member.getRealName())
          .memberGeneration(member.getGeneration())
          .meritType(meritType)
          .build();

      meritLogTestHelper.builder()
          .memberId(member.getId())
          .memberRealName(member.getRealName())
          .memberGeneration(member.getGeneration())
          .meritType(demeritType)
          .build();

      meritLogTestHelper.builder()
          .memberId(otherMember.getId())
          .memberRealName(otherMember.getRealName())
          .memberGeneration(otherMember.getGeneration())
          .meritType(meritType)
          .build();

      meritLogTestHelper.builder()
          .memberId(otherMember.getId())
          .memberRealName(otherMember.getRealName())
          .memberGeneration(otherMember.getGeneration())
          .meritType(meritType)
          .build();

      meritLogTestHelper.builder()
          .memberId(otherMember.getId())
          .memberRealName(otherMember.getRealName())
          .memberGeneration(otherMember.getGeneration())
          .meritType(demeritType)
          .build();
    }

    @Test
    @DisplayName("MeritLogsGroupByMemberResponse Dto에 맞게 조회되어야 한다.")
    public void MeritLogsGroupByMemberResponse_Dto에_맞게_조회되어야_한다() {
      em.flush();
      em.clear();

      Page<MeritLogsGroupByMemberResponse> result = meritLogRepository.findAllTotalMeritLogs(
          PageRequest.of(0, 10));

      assertThat(result.map(MeritLogsGroupByMemberResponse::getMemberId).toList())
          .containsExactly(member.getId(), otherMember.getId());

      assertThat(result.map(MeritLogsGroupByMemberResponse::getMemberName).toList())
          .containsExactly(member.getRealName(), otherMember.getRealName());

      assertThat(result.map(MeritLogsGroupByMemberResponse::getGeneration).toList())
          .containsExactly(member.getGeneration(), otherMember.getGeneration());

      assertThat(result.map(MeritLogsGroupByMemberResponse::getMerit).toList())
          .containsExactly(5, 10);

      assertThat(result.map(MeritLogsGroupByMemberResponse::getDemerit).toList())
          .containsExactly(-3, -3);
    }

  }
}
