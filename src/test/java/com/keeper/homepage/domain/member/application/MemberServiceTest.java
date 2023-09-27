package com.keeper.homepage.domain.member.application;

import static com.keeper.homepage.domain.library.entity.BookBorrowStatus.BookBorrowStatusType.대출중;
import static com.keeper.homepage.domain.library.entity.BookBorrowStatus.getBookBorrowStatusBy;
import static com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_회장;
import static com.keeper.homepage.domain.member.entity.job.MemberJob.getMemberJobBy;
import static com.keeper.homepage.domain.member.entity.type.MemberType.MemberTypeEnum.휴면회원;
import static com.keeper.homepage.domain.seminar.entity.SeminarAttendanceStatus.SeminarAttendanceStatusType.ATTENDANCE;
import static com.keeper.homepage.domain.seminar.entity.SeminarAttendanceStatus.getSeminarAttendanceStatusBy;
import static com.keeper.homepage.global.error.ErrorCode.MEMBER_BOOK_NOT_EMPTY;
import static com.keeper.homepage.global.error.ErrorCode.MEMBER_CANNOT_FOLLOW_ME;
import static com.keeper.homepage.global.error.ErrorCode.MEMBER_WRONG_PASSWORD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.comment.entity.Comment;
import com.keeper.homepage.domain.ctf.entity.CtfContest;
import com.keeper.homepage.domain.ctf.entity.challenge.CtfChallenge;
import com.keeper.homepage.domain.ctf.entity.team.CtfTeam;
import com.keeper.homepage.domain.election.entity.Election;
import com.keeper.homepage.domain.election.entity.ElectionCandidate;
import com.keeper.homepage.domain.election.entity.ElectionChartLog;
import com.keeper.homepage.domain.library.entity.Book;
import com.keeper.homepage.domain.member.dto.request.UpdateMemberEmailAddressRequest;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.member.entity.embedded.EmailAddress;
import com.keeper.homepage.domain.member.entity.embedded.Password;
import com.keeper.homepage.domain.member.entity.friend.Friend;
import com.keeper.homepage.domain.post.entity.Post;
import com.keeper.homepage.domain.seminar.entity.Seminar;
import com.keeper.homepage.domain.seminar.entity.SeminarAttendance;
import com.keeper.homepage.domain.study.entity.Study;
import com.keeper.homepage.global.error.BusinessException;
import java.time.LocalDateTime;
import java.util.List;
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

  @Nested
  @DisplayName("회원 탈퇴 기능 테스트")
  class DeleteMemberTest {

    private Member member, other;
    private Book book;

    @BeforeEach
    void setUp() {
      member = memberTestHelper.builder()
          .password(Password.from("TruePassword"))
          .build();
      other = memberTestHelper.generate();
    }

    @Test
    @DisplayName("비밀번호가 틀릴 시 예외를 던진다.")
    public void 비밀번호가_틀릴_시_예외를_던진다() {
      assertThrows(BusinessException.class,
          () -> memberService.deleteMember(member, "FalsePassword"),
          MEMBER_WRONG_PASSWORD.getMessage());
    }

    @Test
    @DisplayName("대출 중인 도서가 있을 경우 예외를 던진다.")
    public void 대출_중인_도서가_있을_경우_예외를_던진다() {
      book = bookTestHelper.generate();
      member.borrow(book, getBookBorrowStatusBy(대출중));

      em.flush();
      em.clear();

      assertThrows(BusinessException.class,
          () -> memberService.deleteMember(member, "TruePassword"),
          MEMBER_BOOK_NOT_EMPTY.getMessage());
    }

    @Test
    @DisplayName("탈퇴 시 남겨야 하는 데이터는 남아야 하고, 더미 회원이 작성자로 남아야 한다.")
    public void 탈퇴_시_남겨야_하는_데이터는_남아야_하고_더미_회원이_작성자로_남아야_한다() throws Exception {
      //given
      Post post = postTestHelper.builder().member(member).build();
      Comment comment = commentTestHelper.builder().member(member).build();
      Study study = studyTestHelper.builder().headMember(member).build();
      Seminar seminar = seminarTestHelper.builder().build();
      seminar.setStarter(member);
      CtfChallenge challenge = ctfChallengeTestHelper.builder().creator(member).build();
      CtfTeam team = ctfTeamTestHelper.builder().creator(member).build();
      CtfContest contest = ctfContestTestHelper.builder().creator(member).build();
      Election election = electionTestHelper.builder().member(member).build();
      em.flush();
      em.clear();

      //when
      member = memberRepository.findById(member.getId()).orElseThrow();
      memberService.deleteMember(member, "TruePassword");
      em.flush();
      em.clear();

      //then
      post = postRepository.findById(post.getId()).orElseThrow();
      comment = commentRepository.findById(comment.getId()).orElseThrow();
      study = studyRepository.findById(study.getId()).orElseThrow();
      seminar = seminarRepository.findById(seminar.getId()).orElseThrow();
      challenge = ctfChallengeRepository.findById(challenge.getId()).orElseThrow();
      team = ctfTeamRepository.findById(team.getId()).orElseThrow();
      contest = ctfContestRepository.findById(contest.getId()).orElseThrow();
      election = electionRepository.findById(election.getId()).orElseThrow();

      Member virtualMember = memberFindService.getVirtualMember();
      assertThat(post.getMember()).isEqualTo(virtualMember);
      assertThat(comment.getMember()).isEqualTo(virtualMember);
      assertThat(study.getHeadMember()).isEqualTo(virtualMember);
      assertThat(seminar.getStarter()).isEqualTo(virtualMember);
      assertThat(challenge.getCreator()).isEqualTo(virtualMember);
      assertThat(team.getCreator()).isEqualTo(virtualMember);
      assertThat(contest.getCreator()).isEqualTo(virtualMember);
      assertThat(election.getMember()).isEqualTo(virtualMember);
    }

    @Test
    @DisplayName("탈퇴 시 필요없는 데이터는 cascade REMOVE로 제거된다.")
    public void 탈퇴_시_필요없는_데이터는_cascade_REMOVE로_제거된다() throws Exception {
      //given
      long memberId = member.getId();
      Long attendanceId = attendanceTestHelper.builder().member(member).build().getId(); // O
      Long borrowId = bookBorrowInfoTestHelper.builder().member(member).build().getId(); // O
      Post post = postTestHelper.builder().member(member).isTemp(true).build(); // O
      Long pointId = pointLogTestHelper.builder().member(member).build().getId(); // O
      member.follow(other); // O
      other.follow(member); // O
      member.like(post); // O
      member.dislike(post); // O
      member.read(post); // O
      Study study = studyTestHelper.generate();
      member.join(study); // O
      CtfTeam ctfTeam = ctfTeamTestHelper.generate();
      member.join(ctfTeam); // O
      Seminar seminar = seminarTestHelper.generate();
      Long seminarAttendanceId = seminarAttendanceRepository.save(SeminarAttendance.builder()
          .seminar(seminar)
          .member(member)
          .seminarAttendanceStatus(getSeminarAttendanceStatusBy(ATTENDANCE))
          .attendTime(LocalDateTime.now())
          .build()).getId();// O
      Long replyId = surveyMemberReplyTestHelper.builder().member(member).build().getId();// O
      ElectionCandidate electionCandidate = electionCandidateTestHelper.builder(getMemberJobBy(ROLE_회장)).member(member)
          .build(); // O
      electionVoterTestHelper.builder().member(member).build(); // O
      electionChartLogRepository.save(ElectionChartLog.builder()
          .electionCandidate(electionCandidate)
          .voteTime(LocalDateTime.now())
          .build()); // O
      Long gameId = gameTestHelper.builder().member(member).build().getId();// O
      em.flush();
      em.clear();

      //when
      member = memberRepository.findById(member.getId()).orElseThrow();
      memberService.deleteMember(member, "TruePassword");
      em.flush();
      em.clear();

      //then
      assertThat(memberRepository.findById(memberId)).isEmpty();
      assertThat(attendanceRepository.findById(attendanceId)).isEmpty();
      assertThat(bookBorrowInfoRepository.findById(borrowId)).isEmpty();
      assertThat(pointLogRepository.findById(pointId)).isEmpty();
      assertThat(seminarAttendanceRepository.findById(seminarAttendanceId)).isEmpty();
      assertThat(surveyMemberReplyRepository.findById(replyId)).isEmpty();
      assertThat(gameRepository.findById(gameId)).isEmpty();
    }
  }
}
