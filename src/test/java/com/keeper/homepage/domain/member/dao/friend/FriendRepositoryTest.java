package com.keeper.homepage.domain.member.dao.friend;

import static org.assertj.core.api.Assertions.assertThat;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.member.MemberTestHelper.MemberBuilder;
import com.keeper.homepage.domain.member.entity.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class FriendRepositoryTest extends IntegrationTest {

  private MemberBuilder memberBuilder;
  private Member member1;
  private Member member2;

  @BeforeEach
  void setUp() {
    memberBuilder = memberTestHelper.builder();
    member1 = memberBuilder.build();
    member2 = memberBuilder.build();
  }

  @Nested
  @DisplayName("follower, followee 중복 테스트")
  class FriendTest {

    @Test
    @DisplayName("회원의 follower 정보에는 중복된 값이 들어가면 안된다.")
    void should_nothingHappens_when_duplicateFollower() {
      Member findMember = memberRepository.findById(member1.getId()).orElseThrow();

      findMember.addFollower(member2);
      findMember.addFollower(member2);
      findMember.addFollower(member2);
      em.flush();
      em.clear();

      assertThat(friendRepository.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("회원의 followee 정보에는 중복된 값이 들어가면 안된다.")
    void should_nothingHappens_when_duplicateFollowee() {
      Member findMember = memberRepository.findById(member1.getId()).orElseThrow();

      findMember.addFollowee(member2);
      findMember.addFollowee(member2);
      findMember.addFollowee(member2);
      em.flush();
      em.clear();

      assertThat(friendRepository.findAll()).hasSize(1);
    }
  }

}
