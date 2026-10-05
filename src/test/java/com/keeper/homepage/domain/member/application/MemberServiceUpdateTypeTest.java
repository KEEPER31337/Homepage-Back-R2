package com.keeper.homepage.domain.member.application;

import static com.keeper.homepage.domain.member.entity.type.MemberType.MemberTypeEnum.휴면회원;
import static com.keeper.homepage.domain.member.entity.type.MemberType.getMemberTypeBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.keeper.homepage.domain.auth.application.SessionService;
import com.keeper.homepage.domain.member.application.convenience.MemberDeleteService;
import com.keeper.homepage.domain.member.application.convenience.MemberFindService;
import com.keeper.homepage.domain.member.dao.MemberRepository;
import com.keeper.homepage.domain.member.dao.type.MemberTypeRepository;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.member.entity.type.MemberType.MemberTypeEnum;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MemberServiceUpdateTypeTest {

  @Mock
  private MemberRepository memberRepository;

  @Mock
  private MemberFindService memberFindService;

  @Mock
  private MemberProfileService memberProfileService;

  @Mock
  private MemberTypeRepository memberTypeRepository;

  @Mock
  private MemberDeleteService memberDeleteService;

  @Mock
  private SessionService sessionService;

  @InjectMocks
  private MemberService memberService;

  @ParameterizedTest
  @EnumSource(value = MemberTypeEnum.class, names = {"가입대기", "비회원"})
  void updateMemberTypeToSignInRestrictedTypeDeletesAllSessions(MemberTypeEnum type) {
    Member member = memberWithId(1L);
    Member other = memberWithId(2L);
    when(memberTypeRepository.findById(type.getId()))
        .thenReturn(Optional.of(getMemberTypeBy(type)));

    memberService.updateMemberType(List.of(1L, 2L), type.getId());

    assertThat(member.isType(type)).isTrue();
    assertThat(other.isType(type)).isTrue();
    verify(sessionService).deleteAllSessions(1L);
    verify(sessionService).deleteAllSessions(2L);
  }

  @Test
  void updateMemberTypeToOtherTypeKeepsSessions() {
    Member member = memberWithId(1L);
    when(memberTypeRepository.findById(휴면회원.getId()))
        .thenReturn(Optional.of(getMemberTypeBy(휴면회원)));

    memberService.updateMemberType(List.of(1L), 휴면회원.getId());

    assertThat(member.isType(휴면회원)).isTrue();
    verify(sessionService, never()).deleteAllSessions(anyLong());
  }

  private Member memberWithId(long id) {
    Member member = spy(Member.builder().build());
    lenient().doReturn(id).when(member).getId();
    when(memberFindService.findById(id)).thenReturn(member);
    return member;
  }
}
