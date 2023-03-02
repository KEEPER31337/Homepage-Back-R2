package com.keeper.homepage.domain.post.application;

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

  private final PostRepository postRepository;
  private final ValidPostFindService validPostFindService;

  public void delete(long postId) {
    Post post = validPostFindService.findById(postId);
    postRepository.delete(post);
  }
}
