package com.keeper.homepage.domain.member.application;

import static com.keeper.homepage.domain.member.application.convenience.MemberFindService.VIRTUAL_MEMBER_ID;
import static com.keeper.homepage.global.error.ErrorCode.MEMBER_BOOK_NOT_EMPTY;
import static com.keeper.homepage.global.error.ErrorCode.MEMBER_CANNOT_FOLLOW_ME;
import static com.keeper.homepage.global.error.ErrorCode.MEMBER_TYPE_NOT_FOUND;

import com.keeper.homepage.domain.member.application.convenience.MemberDeleteService;
import com.keeper.homepage.domain.member.application.convenience.MemberFindService;
import com.keeper.homepage.domain.member.dao.MemberRepository;
import com.keeper.homepage.domain.member.dao.type.MemberTypeRepository;
import com.keeper.homepage.domain.member.dto.response.MemberPointRankResponse;
import com.keeper.homepage.domain.member.dto.response.MemberResponse;
import com.keeper.homepage.domain.member.dto.response.profile.MemberProfileResponse;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.member.entity.embedded.RealName;
import com.keeper.homepage.domain.member.entity.type.MemberType;
import com.keeper.homepage.global.error.BusinessException;
import com.keeper.homepage.global.error.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

  private final MemberRepository memberRepository;
  private final MemberFindService memberFindService;
  private final MemberProfileService memberProfileService;
  private final MemberTypeRepository memberTypeRepository;
  private final MemberDeleteService memberDeleteService;


  @Transactional
  public void changePassword(Member me, String oldPassword, String newPassword) {
    if (me.getProfile().getPassword().isWrongPassword(oldPassword)) {
      throw new BusinessException(oldPassword, "oldPassword", ErrorCode.MEMBER_WRONG_PASSWORD);
    }
    me.getProfile().changePassword(newPassword);
  }

  public List<MemberResponse> getMembersByRealName(String searchName) {
    if (searchName == null) {
      return memberFindService.findAll()
          .map(MemberResponse::from)
          .toList();
    }
    return memberFindService.findAllByRealName(RealName.from(searchName))
        .map(MemberResponse::from)
        .toList();
  }

  public Page<MemberPointRankResponse> getPointRanking(Pageable pageable) {
    return memberRepository.findAllByIdIsNotOrderByPointDesc(VIRTUAL_MEMBER_ID, pageable)
        .map(MemberPointRankResponse::from);
  }

  @Transactional
  public void follow(Member member, long otherId) {
    Member other = memberFindService.findById(otherId);
    if (other.equals(member)) {
      throw new BusinessException(member.getId(), "memberId", MEMBER_CANNOT_FOLLOW_ME);
    }
    member.follow(other);
  }

  @Transactional
  public void unfollow(Member member, long otherId) {
    Member other = memberFindService.findById(otherId);
    member.unfollow(other);
  }

  public MemberProfileResponse getMemberProfile(Member me, long memberId) {
    Member member = memberRepository.findMemberProfileWithFetchJoin(memberId)
        .orElseThrow();

    return MemberProfileResponse.of(member, me);
  }

  @Transactional
  public void updateMemberType(List<Long> memberIds, long typeId) {
    MemberType findMemberType = memberTypeRepository.findById(typeId)
        .orElseThrow(() -> new BusinessException(typeId, "memberType", MEMBER_TYPE_NOT_FOUND));

    memberIds.stream()
        .map(memberFindService::findById)
        .forEach(m -> m.updateType(findMemberType));
  }

  @Transactional
  public void deleteMember(Member member, String rawPassword) {
    memberProfileService.checkMemberPassword(member, rawPassword);
    checkBorrowedBook(member);

    memberDeleteService.delete(member);
  }

  private void checkBorrowedBook(Member member) {
    if (member.hasAnyBorrowBooks()) {
      throw new BusinessException(member.getBookBorrowInfos(), "memberBorrowInfos",
          MEMBER_BOOK_NOT_EMPTY);
    }
  }

  @Transactional
  public void deleteMemberByAdmin(List<Long> memberIds) {
    for (long memberId : memberIds) {
      Member member = memberFindService.findById(memberId);
      checkBorrowedBook(member);

      memberDeleteService.delete(member);
    }
  }
}
