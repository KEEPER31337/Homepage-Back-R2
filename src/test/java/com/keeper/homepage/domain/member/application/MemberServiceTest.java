package com.keeper.homepage.domain.member.application;

import static com.keeper.homepage.domain.library.entity.BookBorrowStatus.*;
import static com.keeper.homepage.domain.library.entity.BookBorrowStatus.BookBorrowStatusType.대출대기;
import static com.keeper.homepage.domain.library.entity.BookBorrowStatus.BookBorrowStatusType.대출중;
import static com.keeper.homepage.domain.member.entity.type.MemberType.MemberTypeEnum.*;
import static com.keeper.homepage.global.error.ErrorCode.MEMBER_BOOK_NOT_EMPTY;
import static com.keeper.homepage.global.error.ErrorCode.MEMBER_CANNOT_FOLLOW_ME;
import static com.keeper.homepage.global.error.ErrorCode.MEMBER_WRONG_PASSWORD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.library.entity.Book;
import com.keeper.homepage.domain.library.entity.BookBorrowStatus;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.member.entity.embedded.Password;
import com.keeper.homepage.domain.member.entity.friend.Friend;
import com.keeper.homepage.domain.member.entity.type.MemberType.MemberTypeEnum;
import com.keeper.homepage.global.config.password.PasswordFactory;
import com.keeper.homepage.global.error.BusinessException;
import java.util.HashSet;
import java.util.List;
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
  @DisplayName("회원 탈퇴 기능 테스트")
  class DeleteMemberTest {

    private Member member;
    private Book book;

    @BeforeEach
    void setUp() {
      member = memberTestHelper.builder()
          .password(Password.from("TruePassword"))
          .build();
    }

    @Test
    @DisplayName("회원 탈퇴 시 유저 정보는 '삭제'로 마킹되어야 한다.")
    public void 회원_탈퇴_시_유저_정보는_삭제로_마킹되어야_한다() {
      memberService.deleteMember(member, "TruePassword");

      em.flush();
      em.clear();

      Member findMember = memberRepository.findById(member.getId())
          .orElseThrow();

      assertThat(findMember.getProfile().getLoginId().get()).isEqualTo("삭제");
      assertThat(findMember.getProfile().getEmailAddress().get()).isEqualTo("delete@delete.com");
      assertThat(findMember.getProfile().getRealName().get()).isEqualTo("삭제");
      assertThat(findMember.getProfile().getBirthday()).isNull();
      assertThat(findMember.getProfile().getStudentId().get()).isEqualTo("0");
      assertThat(findMember.getProfile().getPassword().isWrongPassword("deletePassword")).isFalse();

      assertThat(findMember.getTotalAttendance()).isEqualTo(0);
      assertThat(findMember.getPoint()).isEqualTo(0);
      assertThat(findMember.getLevel()).isEqualTo(0);
      assertThat(findMember.getIsDeleted()).isEqualTo(true);

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
      member.borrow(book, getBookBorrowStatusBy(대출대기));

      em.flush();
      em.clear();

      assertThrows(BusinessException.class,
          () -> memberService.deleteMember(member, "TruePassword"),
          MEMBER_BOOK_NOT_EMPTY.getMessage());
    }
  }

}
