package com.keeper.homepage.domain.member.application;

import static com.keeper.homepage.domain.member.entity.type.MemberType.MemberTypeEnum.*;
import static com.keeper.homepage.global.error.ErrorCode.MEMBER_CANNOT_FOLLOW_ME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.member.entity.friend.Friend;
import com.keeper.homepage.domain.member.entity.type.MemberType.MemberTypeEnum;
import com.keeper.homepage.global.error.BusinessException;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

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
      Set<Long> memberSet = Set.of(member.getId(), other.getId());

      memberService.updateMemberType(memberSet, 3);

      em.flush();
      em.clear();

      Member findMember = memberRepository.findById(member.getId()).orElseThrow();
      Member findOtherMember = memberRepository.findById(other.getId()).orElseThrow();

      assertThat(findMember.getMemberType().getType()).isEqualTo(휴면회원);
      assertThat(findOtherMember.getMemberType().getType()).isEqualTo(휴면회원);
    }

    @Test
    @DisplayName("존재하지 않는 회원이 있을경우 롤백해야 한다.")
    public void 존재하지_않는_회원이_있을경우_롤백해야_한다() {
      Set<Long> memberSet = Set.of(member.getId(), 12398173L, other.getId());

      assertThrows(BusinessException.class,
          () -> memberService.updateMemberType(memberSet, 3));

      em.flush();
      em.clear();

      Member findMember = memberRepository.findById(member.getId()).orElseThrow();
      Member findOtherMember = memberRepository.findById(other.getId()).orElseThrow();

      assertThat(findMember.getMemberType().getType()).isEqualTo(정회원);
      assertThat(findOtherMember.getMemberType().getType()).isEqualTo(정회원);
    }
  }
}
