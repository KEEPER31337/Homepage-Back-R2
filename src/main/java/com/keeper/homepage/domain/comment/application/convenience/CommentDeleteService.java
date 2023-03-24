package com.keeper.homepage.domain.comment.application.convenience;

import com.keeper.homepage.domain.comment.entity.Comment;
import com.keeper.homepage.domain.member.dao.comment.MemberHasCommentDislikeRepository;
import com.keeper.homepage.domain.member.dao.comment.MemberHasCommentLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentDeleteService {

  private final MemberHasCommentLikeRepository commentLikeRepository;
  private final MemberHasCommentDislikeRepository commentDislikeRepository;

  public void deleteAllLikeAndDislike(Comment comment) {
    commentLikeRepository.deleteAllByComment(comment);
    commentDislikeRepository.deleteAllByComment(comment);
  }
}
