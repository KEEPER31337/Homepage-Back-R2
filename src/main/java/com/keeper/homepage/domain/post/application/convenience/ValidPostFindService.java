package com.keeper.homepage.domain.post.application.convenience;

import static com.keeper.homepage.global.error.ErrorCode.POST_NOT_FOUND;

import com.keeper.homepage.domain.post.dao.PostRepository;
import com.keeper.homepage.domain.post.entity.Post;
import com.keeper.homepage.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ValidPostFindService {

  private static final long VIRTUAL_POST_ID = 1;
  private final PostRepository postRepository;

  public Post findById(long postId) {
    return postRepository.findByIdAndIdNot(postId, VIRTUAL_POST_ID)
        .orElseThrow(() -> new BusinessException(postId, "postId", POST_NOT_FOUND));
  }
}
