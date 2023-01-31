package com.keeper.homepage.domain.member.entity;

import static com.keeper.homepage.domain.member.entity.embedded.EmailAddress.MAX_EMAIL_LENGTH;
import static com.keeper.homepage.domain.member.entity.embedded.LoginId.MAX_LOGIN_ID_LENGTH;
import static com.keeper.homepage.domain.member.entity.embedded.Nickname.MAX_NICKNAME_LENGTH;
import static com.keeper.homepage.domain.member.entity.rank.MemberRank.MemberRankType.일반회원;
import static com.keeper.homepage.domain.member.entity.type.MemberType.MemberTypeEnum.정회원;
import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.CascadeType.REMOVE;

import com.keeper.homepage.domain.attendance.entity.Attendance;
import com.keeper.homepage.domain.member.entity.embedded.Generation;
import com.keeper.homepage.domain.member.entity.embedded.MeritDemerit;
import com.keeper.homepage.domain.member.entity.embedded.Profile;
import com.keeper.homepage.domain.member.entity.embedded.RealName;
import com.keeper.homepage.domain.member.entity.friend.Friend;
import com.keeper.homepage.domain.member.entity.job.MemberHasMemberJob;
import com.keeper.homepage.domain.member.entity.job.MemberJob;
import com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType;
import com.keeper.homepage.domain.member.entity.rank.MemberRank;
import com.keeper.homepage.domain.member.entity.type.MemberType;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@DynamicInsert
@DynamicUpdate
@Getter
@Entity
@EqualsAndHashCode(of = {"id"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member")
public class Member {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "loginId", column = @Column(name = "login_id", nullable = false, unique = true, length = MAX_LOGIN_ID_LENGTH)),
      @AttributeOverride(name = "password", column = @Column(name = "password", nullable = false, length = 512)),
      @AttributeOverride(name = "emailAddress", column = @Column(name = "email_address", nullable = false, unique = true, length = MAX_EMAIL_LENGTH)),
      @AttributeOverride(name = "realName", column = @Column(name = "real_name", nullable = false, length = RealName.MAX_REAL_NAME_LENGTH)),
      @AttributeOverride(name = "nickName", column = @Column(name = "nick_name", nullable = false, length = MAX_NICKNAME_LENGTH))
  })
  private Profile profile;

  @Embedded
  private Generation generation;

  @Column(name = "point", nullable = false)
  private Integer point;

  @Column(name = "level", nullable = false)
  private Integer level;

  @Embedded
  private MeritDemerit meritDemerit;

  @Column(name = "total_attendance", nullable = false)
  private Integer totalAttendance;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_type_id")
  private MemberType memberType;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_rank_id")
  private MemberRank memberRank;

  @OneToMany(mappedBy = "member", cascade = REMOVE)
  private final List<Attendance> memberAttendance = new ArrayList<>();

  @OneToMany(mappedBy = "member", cascade = ALL, orphanRemoval = true)
  private final Set<MemberHasMemberJob> memberJob = new HashSet<>();

  @OneToMany(mappedBy = "follower", cascade = ALL, orphanRemoval = true)
  private final Set<Friend> friends = new HashSet<>();

  @Builder
  private Member(Profile profile, Integer point, Integer level, Integer merit, Integer demerit,
      Integer totalAttendance) {
    this.profile = profile;
    this.generation = Generation.generateGeneration(LocalDate.now());
    this.point = point;
    this.level = level;
    this.meritDemerit = MeritDemerit.builder()
        .merit(merit == null ? 0 : merit)
        .demerit(demerit == null ? 0 : demerit)
        .build();
    this.totalAttendance = totalAttendance;
    this.memberType = MemberType.getMemberTypeBy(정회원);
    this.memberRank = MemberRank.getMemberRankBy(일반회원);
    this.assignJob(MemberJobType.ROLE_회원);
  }

  public void assignJob(MemberJobType jobType) {
    this.memberJob.add(MemberHasMemberJob.builder()
        .member(this)
        .memberJob(MemberJob.getMemberJobBy(jobType))
        .build());
  }

  public void deleteJob(MemberJobType jobType) {
    MemberJob deleteJob = MemberJob.getMemberJobBy(jobType);
    this.memberJob.removeIf(job -> job.getMemberJob().equals(deleteJob));
  }

  public boolean containsRole(MemberJobType jobType) {
    return memberJob.contains(MemberHasMemberJob.builder()
        .member(this)
        .memberJob(MemberJob.getMemberJobBy(jobType))
        .build());
  }

  public void follow(Member other) {
    friends.add(Friend.builder()
        .follower(this)
        .followee(other)
        .build());
  }

  public void unfollow(Member other) {
    friends.removeIf(follow -> follow.getFollowee().equals(other));
  }
}
