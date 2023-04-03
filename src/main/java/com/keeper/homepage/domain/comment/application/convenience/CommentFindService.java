package com.keeper.homepage.domain.comment.application.convenience;

import static com.keeper.homepage.global.error.ErrorCode.COMMENT_NOT_FOUND;

import com.keeper.homepage.domain.comment.dao.CommentRepository;
import com.keeper.homepage.domain.comment.entity.Comment;
import com.keeper.homepage.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentFindService {

  private final CommentRepository commentRepository;

  public Comment findById(long commentId) {
    return commentRepository.findById(commentId)
        .orElseThrow(() -> new BusinessException(commentId, "commentId", COMMENT_NOT_FOUND));
  }
}
