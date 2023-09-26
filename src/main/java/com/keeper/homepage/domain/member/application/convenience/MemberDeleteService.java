package com.keeper.homepage.domain.member.application.convenience;

import static com.keeper.homepage.global.error.ErrorCode.MEMBER_NOT_FOUND;

import com.keeper.homepage.domain.comment.dao.CommentRepository;
import com.keeper.homepage.domain.ctf.dao.CtfContestRepository;
import com.keeper.homepage.domain.ctf.dao.challenge.CtfChallengeRepository;
import com.keeper.homepage.domain.ctf.dao.team.CtfTeamRepository;
import com.keeper.homepage.domain.election.dao.ElectionRepository;
import com.keeper.homepage.domain.game.dao.GameRepository;
import com.keeper.homepage.domain.member.dao.MemberRepository;
import com.keeper.homepage.domain.member.dao.comment.MemberHasCommentDislikeRepository;
import com.keeper.homepage.domain.member.dao.comment.MemberHasCommentLikeRepository;
import com.keeper.homepage.domain.member.dao.friend.FriendRepository;
import com.keeper.homepage.domain.member.dao.post.MemberHasPostDislikeRepository;
import com.keeper.homepage.domain.member.dao.post.MemberHasPostLikeRepository;
import com.keeper.homepage.domain.member.dao.post.MemberReadPostRepository;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.post.dao.PostRepository;
import com.keeper.homepage.domain.seminar.dao.SeminarRepository;
import com.keeper.homepage.domain.study.dao.StudyRepository;
import com.keeper.homepage.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberDeleteService {

  public static final long VIRTUAL_MEMBER_ID = 1;

  private final MemberRepository memberRepository;
  private final PostRepository postRepository;
  private final CommentRepository commentRepository;
  private final MemberHasCommentLikeRepository commentLikeRepository;
  private final MemberHasCommentDislikeRepository commentDislikeRepository;
  private final MemberHasPostLikeRepository postLikeRepository;
  private final MemberHasPostDislikeRepository postDislikeRepository;
  private final MemberReadPostRepository readPostRepository;
  private final StudyRepository studyRepository;
  private final SeminarRepository seminarRepository;
  private final CtfChallengeRepository ctfChallengeRepository;
  private final CtfTeamRepository ctfTeamRepository;
  private final CtfContestRepository ctfContestRepository;
  private final ElectionRepository electionRepository;
  private final GameRepository gameRepository;
  private final FriendRepository friendRepository;

  public void delete(Member member) {
    Member virtualMember = getVirtualMember();
    postRepository.updateVirtualMember(member, virtualMember);
    commentRepository.updateVirtualMember(member, virtualMember);
    studyRepository.updateVirtualMember(member, virtualMember);
    seminarRepository.updateVirtualMember(member, virtualMember);
    ctfChallengeRepository.updateVirtualMember(member, virtualMember);
    ctfTeamRepository.updateVirtualMember(member, virtualMember);
    ctfContestRepository.updateVirtualMember(member, virtualMember);
    electionRepository.updateVirtualMember(member, virtualMember);

    friendRepository.deleteAllByFollowee(member);
    friendRepository.deleteAllByFollower(member);
    gameRepository.deleteAllByMember(member);
    postLikeRepository.deleteAllByMember(member);
    postDislikeRepository.deleteAllByMember(member);
    commentLikeRepository.deleteAllByMember(member);
    commentDislikeRepository.deleteAllByMember(member);
    readPostRepository.deleteAllByMember(member);
    postRepository.deleteAllByMemberAndIsTempTrue(member);
    memberRepository.delete(member);
  }

  public Member getVirtualMember() {
    return memberRepository.findById(VIRTUAL_MEMBER_ID)
        .orElseThrow(() -> new BusinessException(VIRTUAL_MEMBER_ID, "memberId", MEMBER_NOT_FOUND));
  }
}
