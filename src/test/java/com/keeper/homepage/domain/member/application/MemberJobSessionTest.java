package com.keeper.homepage.domain.member.application;

import static com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_부회장;
import static com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_회원;
import static com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_회장;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.keeper.homepage.domain.auth.application.SessionService;
import com.keeper.homepage.domain.member.application.convenience.MemberFindService;
import com.keeper.homepage.domain.member.dao.role.MemberHasMemberJobRepository;
import com.keeper.homepage.domain.member.dao.role.MemberJobRepository;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.member.entity.job.MemberJob;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

class MemberJobSessionTest {

  private final MemberFindService memberFindService = mock(MemberFindService.class);
  private final MemberJobRepository jobRepository = mock(MemberJobRepository.class);
  private final SessionService sessionService = mock(SessionService.class);
  private final MemberJobService service = new MemberJobService(memberFindService,
      mock(MemberHasMemberJobRepository.class), jobRepository, sessionService);
  private Member member;

  @BeforeEach
  void setUp() {
    member = Member.builder().build();
    ReflectionTestUtils.setField(member, "id", 123L);
    when(memberFindService.findById(123L)).thenReturn(member);
    when(jobRepository.findById(ROLE_회장.getId()))
        .thenReturn(Optional.of(MemberJob.getMemberJobBy(ROLE_회장)));
  }

  @Test
  void addingRolePropagatesCompleteRoleListToExistingSessions() {
    member.assignJob(ROLE_부회장);

    service.addMemberExecutiveJob(123L, ROLE_회장.getId());

    ArgumentCaptor<List<String>> roles = ArgumentCaptor.captor();
    verify(sessionService).updateAllSessionRoles(eq(123L), roles.capture());
    assertThat(roles.getValue())
        .containsExactlyInAnyOrder(ROLE_회원.name(), ROLE_부회장.name(), ROLE_회장.name());
  }

  @Test
  void removingRolePreservesOtherRolesInExistingSessions() {
    member.assignJob(ROLE_회장);
    member.assignJob(ROLE_부회장);

    service.deleteMemberExecutiveJob(123L, ROLE_회장.getId());

    ArgumentCaptor<List<String>> roles = ArgumentCaptor.captor();
    verify(sessionService).updateAllSessionRoles(eq(123L), roles.capture());
    assertThat(roles.getValue()).containsExactlyInAnyOrder(ROLE_회원.name(), ROLE_부회장.name());
  }
}
