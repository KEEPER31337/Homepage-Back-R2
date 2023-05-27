package com.keeper.homepage.domain.comment.application;

import static com.keeper.homepage.global.error.ErrorCode.COMMENT_NOT_PARENT;
import static com.keeper.homepage.global.error.ErrorCode.COMMENT_NOT_WRITER;

import com.keeper.homepage.domain.comment.application.convenience.CommentDeleteService;
import com.keeper.homepage.domain.comment.application.convenience.CommentFindService;
import com.keeper.homepage.domain.comment.dao.CommentRepository;
import com.keeper.homepage.domain.comment.dto.request.CommentCreateRequest;
import com.keeper.homepage.domain.comment.dto.response.CommentResponse;
import com.keeper.homepage.domain.comment.entity.Comment;
import com.keeper.homepage.domain.member.application.convenience.MemberFindService;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.post.application.convenience.ValidPostFindService;
import com.keeper.homepage.domain.post.entity.Post;
import com.keeper.homepage.global.error.BusinessException;
import com.keeper.homepage.global.util.thumbnail.ThumbnailUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

  private final CommentRepository commentRepository;

  private final ValidPostFindService validPostFindService;
  private final CommentFindService commentFindService;
  private final MemberFindService memberFindService;
  private final CommentDeleteService commentDeleteService;

  public static final String DELETED_COMMENT_CONTENT = "(삭제된 댓글입니다)";

  @Transactional
  public long create(Member member, CommentCreateRequest request) {
    Post post = validPostFindService.findById(request.getPostId());
    Comment parent = getParentById(request.getParentId());
    Comment comment = request.toEntity(member, post, parent);

    return commentRepository.save(comment).getId();
  }

  private Comment getParentById(Long parentId) {
    if (parentId == null) {
      return null;
    }
    Comment parent = commentFindService.findById(parentId);
    if (parent.hasParent()) {
      throw new BusinessException(parentId, "parentId", COMMENT_NOT_PARENT);
    }
    return parent;
  }

  public List<CommentResponse> getComments(Long postId) {
    Post post = validPostFindService.findById(postId);
    return post.getComments().stream()
        .map(CommentResponse::from)
        .toList();
  }

  @Transactional
  public long update(Member member, Long commentId, String content) {
    Comment comment = commentFindService.findById(commentId);
    checkCommentIsMine(comment, member);

    comment.updateContent(content);
    return comment.getPost().getId();
  }

  private void checkCommentIsMine(Comment comment, Member member) {
    if (!comment.isMine(member)) {
      String nickname = member.getNickname();
      throw new BusinessException(nickname, "nickname", COMMENT_NOT_WRITER);
    }
  }

  @Transactional
  public void delete(Member member, Long commentId) {
    Comment comment = commentFindService.findById(commentId);
    checkCommentIsMine(comment, member);

    Member virtualMember = memberFindService.getVirtualMember();
    comment.changeWriter(virtualMember);
    comment.updateContent(DELETED_COMMENT_CONTENT);
    commentDeleteService.deleteAllLikeAndDislike(comment);
  }

  @Transactional
  public void like(Member member, Long commentId) {
    Comment comment = commentFindService.findById(commentId);

    if (member.isLike(comment)) {
      member.cancelLike(comment);
      return;
    }
    member.like(comment);
  }

  @Transactional
  public void dislike(Member member, Long commentId) {
    Comment comment = commentFindService.findById(commentId);

    if (member.isDislike(comment)) {
      member.cancelDislike(comment);
      return;
    }
    member.dislike(comment);
  }
}
