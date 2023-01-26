package com.keeper.homepage.domain.member.entity;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.CascadeType.REMOVE;

import com.keeper.homepage.domain.attendance.entity.Attendance;
import com.keeper.homepage.domain.member.entity.embedded.MeritDemerit;
import com.keeper.homepage.domain.member.entity.embedded.Profile;
import com.keeper.homepage.domain.member.entity.friend.Friend;
import com.keeper.homepage.domain.member.entity.job.MemberHasMemberJob;
import com.keeper.homepage.domain.member.entity.job.MemberJob;
import com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType;
import com.keeper.homepage.domain.thumbnail.entity.Thumbnail;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@DynamicInsert
@DynamicUpdate
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member")
public class Member {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @Embedded
  private Profile profile;

  @Column(name = "point", nullable = false)
  private Integer point;

  @Column(name = "level", nullable = false)
  private Integer level;

  @Embedded
  private MeritDemerit meritDemerit;

  @Column(name = "total_attendance", nullable = false)
  private Integer totalAttendance;

  @OneToMany(mappedBy = "member", cascade = REMOVE)
  private final List<Attendance> memberAttendance = new ArrayList<>();

  @OneToMany(mappedBy = "member", cascade = ALL, orphanRemoval = true)
  private final Set<MemberHasMemberJob> memberJob = new HashSet<>();

  @OneToMany(mappedBy = "followee", cascade = CascadeType.REMOVE)
  private final Set<Friend> follower = new HashSet<>();

  @OneToMany(mappedBy = "follower", cascade = CascadeType.REMOVE)
  private final Set<Friend> followee = new HashSet<>();

  @Builder
  private Member(String loginId, String emailAddress, String password, String realName,
      String nickname, LocalDate birthday, String studentId, Integer point, Integer level,
      Thumbnail thumbnail, Integer merit, Integer demerit, Float generation,
      Integer totalAttendance) {
    this.profile = Profile.builder()
        .loginId(loginId)
        .emailAddress(emailAddress)
        .password(password)
        .realName(realName)
        .nickname(nickname)
        .birthday(birthday)
        .studentId(studentId)
        .thumbnail(thumbnail)
        .generation(generation)
        .build();
    this.point = point;
    this.level = level;
    this.meritDemerit = MeritDemerit.builder()
        .merit(merit == null ? 0 : merit)
        .demerit(demerit == null ? 0 : demerit)
        .build();
    this.totalAttendance = totalAttendance;
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
}
