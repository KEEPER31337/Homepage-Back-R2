package com.keeper.homepage.domain.post.application.convenience;

import com.keeper.homepage.domain.member.dao.comment.MemberHasCommentDislikeRepository;
import com.keeper.homepage.domain.member.dao.comment.MemberHasCommentLikeRepository;
import com.keeper.homepage.domain.member.dao.post.MemberHasPostDislikeRepository;
import com.keeper.homepage.domain.member.dao.post.MemberHasPostLikeRepository;
import com.keeper.homepage.domain.post.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostDeleteService {

  private final MemberHasPostLikeRepository postLikeRepository;
  private final MemberHasPostDislikeRepository postDislikeRepository;
  private final MemberHasCommentLikeRepository commentLikeRepository;
  private final MemberHasCommentDislikeRepository commentDislikeRepository;

  public void deleteAllLikeAndDislike(Post post) {
    postLikeRepository.deleteAllByPost(post);
    postDislikeRepository.deleteAllByPost(post);
    commentLikeRepository.deleteAllByComment_Post(post);
    commentDislikeRepository.deleteAllByComment_Post(post);
  }
}
