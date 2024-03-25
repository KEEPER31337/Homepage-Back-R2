package com.keeper.homepage.domain.post.application;

import static com.keeper.homepage.global.error.ErrorCode.POST_CONTENT_NEED;
import static com.keeper.homepage.global.error.ErrorCode.POST_INACCESSIBLE;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.post.application.convenience.ValidPostFindService;
import com.keeper.homepage.domain.post.entity.Post;
import com.keeper.homepage.domain.post.entity.embedded.PostContent;
import com.keeper.homepage.domain.thumbnail.entity.Thumbnail;
import com.keeper.homepage.global.error.BusinessException;
import com.keeper.homepage.global.util.thumbnail.ThumbnailUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostContentService {

  private final ThumbnailUtil thumbnailUtil;
  private final ValidPostFindService validPostFindService;

  @Transactional
  public void updatePostThumbnail(Member member, long postId, MultipartFile thumbnail) {
    Post post = validPostFindService.findById(postId);
    PostContent postContent = post.getPostContent();

    if (!post.isMine(member)) {
      throw new BusinessException(post.getId(), "postId", POST_INACCESSIBLE);
    }
    thumbnailUtil.deleteFileAndEntityIfExist(postContent.getThumbnail());
    savePostThumbnail(postContent, thumbnail);
  }

  @Transactional
  public void deletePostThumbnail(Member member, long postId) {
    Post post = validPostFindService.findById(postId);
    PostContent postInfo = post.getPostContent();

    if (!post.isMine(member)) {
      throw new BusinessException(post.getId(), "postId", POST_INACCESSIBLE);
    }
    thumbnailUtil.deleteFileAndEntityIfExist(postInfo.getThumbnail());
    postInfo.deleteThumbnail();
  }

  public void savePostThumbnail(PostContent postContent, MultipartFile thumbnail) {
    Thumbnail savedThumbnail = thumbnailUtil.saveThumbnail(thumbnail).orElse(null);
    postContent.changeThumbnail(savedThumbnail);
  }

  public void checkContent(String content) {
    if (!StringUtils.hasText(content)) {
      throw new BusinessException(content, "content", POST_CONTENT_NEED);
    }
  }

}
