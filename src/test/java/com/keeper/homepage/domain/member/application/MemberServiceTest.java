package com.keeper.homepage.domain.member.application;

import static com.keeper.homepage.domain.member.entity.type.MemberType.MemberTypeEnum.*;
import static com.keeper.homepage.global.error.ErrorCode.MEMBER_CANNOT_FOLLOW_ME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.member.dto.request.UpdateMemberEmailAddressRequest;
import com.keeper.homepage.domain.member.dto.request.UpdateMemberEmailAddressRequest.UpdateMemberEmailAddressRequestBuilder;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.member.entity.embedded.EmailAddress;
import com.keeper.homepage.domain.member.entity.embedded.Password;
import com.keeper.homepage.domain.member.entity.friend.Friend;
import com.keeper.homepage.domain.member.entity.type.MemberType.MemberTypeEnum;
import com.keeper.homepage.global.error.BusinessException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class MemberServiceTest extends IntegrationTest {

  @Nested
  @DisplayName("회원 팔로우 언팔로우 테스트")
  class MemberFollowTest {

    private Member member;
    private Member other;

    @BeforeEach
    void setUp() {
      member = memberTestHelper.generate();
      other = memberTestHelper.generate();
    }

    @Test
    @DisplayName("회원의 팔로우는 성공해야 한다.")
    public void 회원의_팔로우는_성공해야_한다() throws Exception {
      memberService.follow(member, other.getId());

      em.flush();
      em.clear();

      member = memberRepository.findById(member.getId()).orElseThrow();
      other = memberRepository.findById(other.getId()).orElseThrow();

      assertThat(member.getFollower().stream().map(Friend::getFollowee)).contains(other);
      assertThat(other.getFollowee().stream().map(Friend::getFollower)).contains(member);
    }

    @Test
    @DisplayName("스스로를 팔로우 했을 경우 팔로우는 실패한다.")
    public void 스스로를_팔로우_했을_경우_팔로우는_실패한다() throws Exception {
      assertThatThrownBy(() -> memberService.follow(member, member.getId()))
          .isInstanceOf(BusinessException.class)
          .hasMessageContaining(MEMBER_CANNOT_FOLLOW_ME.getMessage());
    }
  }

  @Nested
  @DisplayName("회원 타입 변경 테스트")
  class UpdateMemberTypeTest {

    private Member member, other;

    @BeforeEach
    void setUp() {
      member = memberTestHelper.generate();
      other = memberTestHelper.generate();
    }

    @Test
    @DisplayName("회원 타입 변경에 성공해야 한다.")
    public void 회원_타입_변경에_성공해야_한다() {
      List<Long> memberSet = List.of(member.getId(), other.getId());

      memberService.updateMemberType(memberSet, 3);

      em.flush();
      em.clear();

      Member findMember = memberRepository.findById(member.getId()).orElseThrow();
      Member findOtherMember = memberRepository.findById(other.getId()).orElseThrow();

      assertThat(findMember.getMemberType().getType()).isEqualTo(휴면회원);
      assertThat(findOtherMember.getMemberType().getType()).isEqualTo(휴면회원);
    }
  }

  @Nested
  @DisplayName("회원 이메일 변경 테스트")
  class UpdateEmailTest {

    private Member member;
    private UpdateMemberEmailAddressRequest request;

    @BeforeEach
    void setUp() {
      member = memberTestHelper.builder()
          .emailAddress(EmailAddress.from("beforeUpdated@gmail.com"))
          .password(Password.from("truePassword"))
          .build();

      request = UpdateMemberEmailAddressRequest.builder()
          .email("Updated@gmail.com")
          .auth("123456789")
          .password("truePassword")
          .build();
    }

    @Test
    @DisplayName("회원 이메일 변경을 성공해야 한다.")
    public void 회원_이메일_변경을_성공해야_한다() {
      doNothing().when(memberProfileService).checkEmailAuth(any(), any());
      memberProfileService.updateProfileEmailAddress(member, request.getEmail(),
          request.getAuth(), request.getPassword());

      em.flush();
      em.clear();

      Member savedMember = memberFindService.findById(member.getId());
      assertThat(savedMember.getProfile().getEmailAddress().get()).isEqualTo("Updated@gmail.com");
    }

  }
}
