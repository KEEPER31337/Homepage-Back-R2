package com.keeper.homepage.domain.member.application.convenience;

import static com.keeper.homepage.domain.member.application.convenience.MemberDeleteService.VIRTUAL_MEMBER_ID;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.keeper.homepage.domain.comment.dao.CommentRepository;
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
import com.keeper.homepage.domain.vote.dao.VoteParticipationRepository;
import com.keeper.homepage.domain.vote.dao.VoteRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MemberDeleteServiceTest {

  @Mock
  private MemberRepository memberRepository;

  @Mock
  private PostRepository postRepository;

  @Mock
  private CommentRepository commentRepository;

  @Mock
  private MemberHasCommentLikeRepository commentLikeRepository;

  @Mock
  private MemberHasCommentDislikeRepository commentDislikeRepository;

  @Mock
  private MemberHasPostLikeRepository postLikeRepository;

  @Mock
  private MemberHasPostDislikeRepository postDislikeRepository;

  @Mock
  private MemberReadPostRepository readPostRepository;

  @Mock
  private StudyRepository studyRepository;

  @Mock
  private SeminarRepository seminarRepository;

  @Mock
  private ElectionRepository electionRepository;

  @Mock
  private GameRepository gameRepository;

  @Mock
  private FriendRepository friendRepository;

  @Mock
  private MemberHasPostDislikeRepository memberHasPostDislikeRepository;

  @Mock
  private VoteRepository voteRepository;

  @Mock
  private VoteParticipationRepository voteParticipationRepository;

  @InjectMocks
  private MemberDeleteService memberDeleteService;

  @Test
  void deleteChangesVoteReferencesToVirtualMemberBeforeDeletingMember() {
    Member member = mock(Member.class);
    Member virtualMember = mock(Member.class);
    when(memberRepository.findById(VIRTUAL_MEMBER_ID)).thenReturn(Optional.of(virtualMember));

    memberDeleteService.delete(member);

    InOrder order = inOrder(memberRepository, voteRepository, voteParticipationRepository);
    order.verify(memberRepository).findById(VIRTUAL_MEMBER_ID);
    order.verify(voteRepository).updateVirtualMember(member, virtualMember);
    order.verify(voteParticipationRepository).updateVirtualMember(member, virtualMember);
    order.verify(memberRepository).delete(member);
  }
}
