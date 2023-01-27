package com.keeper.homepage.domain.member.entity.friend;

import com.keeper.homepage.domain.member.entity.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "friend")
public class Friend {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "follower", nullable = false)
  private Member follower;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "followee", nullable = false)
  private Member followee;

  @Builder
  private Friend(Member follower, Member followee) {
    this.follower = follower;
    this.followee = followee;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Friend friend = (Friend) o;
    return Objects.equals(getId(), friend.getId()) && Objects.equals(
        getFollower(), friend.getFollower()) && Objects.equals(getFollowee(),
        friend.getFollowee());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getFollower(), getFollowee());
  }
}
