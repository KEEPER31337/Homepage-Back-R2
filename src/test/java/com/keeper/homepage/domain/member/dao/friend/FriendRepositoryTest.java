package com.keeper.homepage.domain.member.dao.friend;

import static org.assertj.core.api.Assertions.assertThat;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.member.MemberTestHelper.MemberBuilder;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.member.entity.friend.Friend;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class FriendRepositoryTest extends IntegrationTest {

  private Member member;
  private Member other;

  @BeforeEach
  void setUp() {
    MemberBuilder memberBuilder = memberTestHelper.builder();
    member = memberBuilder.build();
    other = memberBuilder.build();
  }

  @Nested
  @DisplayName("follow 테스트")
  class FollowTest {

    @Test
    @DisplayName("회원을 중복 팔로우 했을 때 friend에는 중복된 값이 존재하지 않는다.")
    void should_nothingDuplicateFriendExist_when_duplicateFollow() {
      Member findMember = memberRepository.findById(member.getId()).orElseThrow();

      findMember.follow(other);
      findMember.follow(other);
      findMember.follow(other);
      em.flush();
      em.clear();
      List<Friend> friends = friendRepository.findAll();

      assertThat(friends).hasSize(1);
      assertThat(friends.get(0).getFollower()).isEqualTo(member);
      assertThat(friends.get(0).getFollowee()).isEqualTo(other);
    }

    @Test
    @DisplayName("회원을 언팔로우 하면 friend 값은 삭제되어야 한다.")
    void should_deleteFriendSuccessfully_when_unfollow() {
      Member findMember = memberRepository.findById(member.getId()).orElseThrow();

      findMember.follow(other);
      findMember.unfollow(other);
      em.flush();
      em.clear();
      List<Friend> friends = friendRepository.findAll();

      assertThat(friends).hasSize(0);
    }
  }
}
