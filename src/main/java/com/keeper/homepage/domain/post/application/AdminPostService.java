package com.keeper.homepage.domain.post.application;

import com.keeper.homepage.domain.member.dao.comment.MemberHasCommentDislikeRepository;
import com.keeper.homepage.domain.member.dao.comment.MemberHasCommentLikeRepository;
import com.keeper.homepage.domain.member.dao.post.MemberHasPostDislikeRepository;
import com.keeper.homepage.domain.member.dao.post.MemberHasPostLikeRepository;
import com.keeper.homepage.domain.post.application.convenience.PostDeleteService;
import com.keeper.homepage.domain.post.application.convenience.ValidPostFindService;
import com.keeper.homepage.domain.post.dao.PostRepository;
import com.keeper.homepage.domain.post.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminPostService {

  private final ValidPostFindService validPostFindService;
  private final PostDeleteService postDeleteService;

  public void delete(long postId) {
    Post post = validPostFindService.findById(postId);

    postDeleteService.delete(post);
  }
}
