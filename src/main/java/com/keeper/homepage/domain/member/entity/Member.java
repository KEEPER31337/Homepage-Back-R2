package com.keeper.homepage.domain.member.entity;

import static com.keeper.homepage.domain.member.entity.embedded.EmailAddress.MAX_EMAIL_LENGTH;
import static com.keeper.homepage.domain.member.entity.embedded.LoginId.MAX_LOGIN_ID_LENGTH;
import static com.keeper.homepage.domain.member.entity.embedded.Nickname.MAX_NICKNAME_LENGTH;
import static com.keeper.homepage.domain.member.entity.embedded.Password.HASHED_PASSWORD_MAX_LENGTH;
import static com.keeper.homepage.domain.member.entity.embedded.RealName.MAX_REAL_NAME_LENGTH;
import static com.keeper.homepage.domain.member.entity.embedded.StudentId.MAX_STUDENT_ID_LENGTH;
import static com.keeper.homepage.domain.member.entity.rank.MemberRank.MemberRankType.일반회원;
import static com.keeper.homepage.domain.member.entity.type.MemberType.MemberTypeEnum.정회원;
import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REMOVE;

import com.keeper.homepage.domain.attendance.entity.Attendance;
import com.keeper.homepage.domain.comment.entity.Comment;
import com.keeper.homepage.domain.ctf.entity.CtfContest;
import com.keeper.homepage.domain.ctf.entity.team.CtfTeam;
import com.keeper.homepage.domain.ctf.entity.team.CtfTeamHasMember;
import com.keeper.homepage.domain.library.entity.Book;
import com.keeper.homepage.domain.library.entity.BookBorrowInfo;
import com.keeper.homepage.domain.library.entity.BookBorrowStatus;
import com.keeper.homepage.domain.member.entity.comment.MemberHasCommentDislike;
import com.keeper.homepage.domain.member.entity.comment.MemberHasCommentLike;
import com.keeper.homepage.domain.member.entity.embedded.Generation;
import com.keeper.homepage.domain.member.entity.embedded.MeritDemerit;
import com.keeper.homepage.domain.member.entity.embedded.Profile;
import com.keeper.homepage.domain.member.entity.friend.Friend;
import com.keeper.homepage.domain.member.entity.job.MemberHasMemberJob;
import com.keeper.homepage.domain.member.entity.job.MemberJob;
import com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType;
import com.keeper.homepage.domain.member.entity.post.MemberHasPostDislike;
import com.keeper.homepage.domain.member.entity.post.MemberHasPostLike;
import com.keeper.homepage.domain.member.entity.rank.MemberRank;
import com.keeper.homepage.domain.member.entity.type.MemberType;
import com.keeper.homepage.domain.point.entity.PointLog;
import com.keeper.homepage.domain.post.entity.Post;
import com.keeper.homepage.domain.study.entity.Study;
import com.keeper.homepage.domain.study.entity.StudyHasMember;
import com.keeper.homepage.domain.thumbnail.entity.Thumbnail;
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
import java.util.Optional;
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
      @AttributeOverride(name = "password", column = @Column(name = "password", nullable = false, length = HASHED_PASSWORD_MAX_LENGTH)),
      @AttributeOverride(name = "emailAddress", column = @Column(name = "email_address", nullable = false, unique = true, length = MAX_EMAIL_LENGTH)),
      @AttributeOverride(name = "realName", column = @Column(name = "real_name", nullable = false, length = MAX_REAL_NAME_LENGTH)),
      @AttributeOverride(name = "nickName", column = @Column(name = "nick_name", nullable = false, length = MAX_NICKNAME_LENGTH)),
      @AttributeOverride(name = "studentId", column = @Column(name = "student_id", unique = true, length = MAX_STUDENT_ID_LENGTH))
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

  @OneToMany(mappedBy = "member", cascade = PERSIST)
  private final List<BookBorrowInfo> bookBorrowInfos = new ArrayList<>();

  @OneToMany(mappedBy = "member", cascade = ALL, orphanRemoval = true)
  private final Set<MemberHasMemberJob> memberJob = new HashSet<>();

  @OneToMany(mappedBy = "follower", cascade = ALL, orphanRemoval = true)
  private final Set<Friend> friends = new HashSet<>();

  @OneToMany(mappedBy = "member", cascade = ALL, orphanRemoval = true)
  private final Set<MemberHasPostLike> postLikes = new HashSet<>();

  @OneToMany(mappedBy = "member", cascade = ALL, orphanRemoval = true)
  private final Set<MemberHasPostDislike> postDislikes = new HashSet<>();

  @OneToMany(mappedBy = "member", cascade = ALL, orphanRemoval = true)
  private final Set<MemberHasCommentLike> commentLikes = new HashSet<>();

  @OneToMany(mappedBy = "member", cascade = ALL, orphanRemoval = true)
  private final Set<MemberHasCommentDislike> commentDislikes = new HashSet<>();

  @OneToMany(mappedBy = "member", cascade = ALL, orphanRemoval = true)
  private final Set<StudyHasMember> studyMembers = new HashSet<>();

  @OneToMany(mappedBy = "member")
  private final List<Comment> comments = new ArrayList<>();

  @OneToMany(mappedBy = "member")
  private final List<PointLog> pointLogs = new ArrayList<>();

  @OneToMany(mappedBy = "member", cascade = ALL, orphanRemoval = true)
  private final Set<CtfTeamHasMember> ctfTeamHasMembers = new HashSet<>();

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

  public void borrow(Book book, BookBorrowStatus borrowStatus) {
    bookBorrowInfos.add(BookBorrowInfo.builder()
        .member(this)
        .book(book)
        .borrowStatus(borrowStatus)
        .build());
  }

  public void like(Post post) {
    postLikes.add(MemberHasPostLike.builder()
        .member(this)
        .post(post)
        .build());
  }

  public void like(Comment comment) {
    commentLikes.add(MemberHasCommentLike.builder()
        .member(this)
        .comment(comment)
        .build());
  }

  public void cancelLike(Post post) {
    postLikes.removeIf(postLike -> postLike.getPost().equals(post));
  }

  public void cancelLike(Comment comment) {
    commentLikes.removeIf(commentLike -> commentLike.getComment().equals(comment));
  }

  public void dislike(Post post) {
    postDislikes.add(MemberHasPostDislike.builder()
        .member(this)
        .post(post)
        .build());
  }

  public void dislike(Comment comment) {
    commentDislikes.add(MemberHasCommentDislike.builder()
        .member(this)
        .comment(comment)
        .build());
  }

  public void cancelDislike(Post post) {
    postDislikes.removeIf(postDislike -> postDislike.getPost().equals(post));
  }

  public void cancelDislike(Comment comment) {
    commentDislikes.removeIf(commentDislike -> commentDislike.getComment().equals(comment));
  }

  public boolean isLike(Post post) {
    return postLikes.stream()
        .anyMatch(postLike -> postLike.getPost().equals(post));
  }

  public boolean isLike(Comment comment) {
    return commentLikes.stream()
        .anyMatch(commentLike -> commentLike.getComment().equals(comment));
  }

  public boolean isDislike(Post post) {
    return postDislikes.stream()
        .anyMatch(postDislike -> postDislike.getPost().equals(post));
  }

  public boolean isDislike(Comment comment) {
    return commentDislikes.stream()
        .anyMatch(commentDislike -> commentDislike.getComment().equals(comment));
  }

  public void join(Study study) {
    StudyHasMember studyMember = StudyHasMember.builder()
        .study(study)
        .member(this)
        .build();
    studyMembers.add(studyMember);
  }

  public void leave(Study study) {
    studyMembers.removeIf(studyMember -> studyMember.getStudy().equals(study));
  }

  public void join(CtfTeam ctfTeam) {
    ctfTeamHasMembers.add(CtfTeamHasMember.builder()
        .ctfTeam(ctfTeam)
        .member(this)
        .build());
  }

  public void leave(CtfTeam ctfTeam) {
    ctfTeamHasMembers.removeIf(ctfTeamMember -> ctfTeamMember.getCtfTeam().equals(ctfTeam));
  }

  public Long getId() {
    return this.id;
  }

  public String getNickname() {
    return this.profile.getNickname().get();
  }

  public String getRealName() {
    return this.profile.getRealName().get();
  }

  public Float getGeneration() {
    return this.generation.getGeneration();
  }

  public Integer getPoint() {
    return this.point;
  }

  public void addPoint(int point) {
    this.point += point;
  }

  public void minusPoint(int point) {
    if (this.point < point && point < 0) {
      throw new IllegalArgumentException();
    }
    this.point -= point;
  }

  public String getThumbnailPath() {
    return Optional.ofNullable(this.profile.getThumbnail())
        .map(Thumbnail::getPath)
        .orElse(null);
  }

  public boolean isHeadMember(Study study) {
    return study.getHeadMember().equals(this);
  }

  public long getCountInBorrowing() {
    return this.bookBorrowInfos.stream()
        .filter(BookBorrowInfo::isInBorrowing)
        .count();
  }

  public boolean hasTeam(CtfContest contest) {
    return ctfTeamHasMembers.stream()
        .anyMatch(ctfTeamHasMember -> ctfTeamHasMember.getCtfTeam().getCtfContest().equals(contest));
  }

  public boolean isJoin(CtfTeam ctfTeam) {
    return ctfTeamHasMembers.stream()
        .anyMatch(ctfTeamHasMember -> ctfTeamHasMember.getCtfTeam().equals(ctfTeam));
  }

  public boolean isCreator(CtfTeam ctfTeam) {
    return this.equals(ctfTeam.getCreator());
  }
}
