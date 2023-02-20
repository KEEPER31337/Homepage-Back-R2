package com.keeper.homepage.domain.post.application;

import static com.keeper.homepage.global.error.ErrorCode.CATEGORY_NOT_FOUND;

import com.keeper.homepage.domain.file.entity.FileEntity;
import com.keeper.homepage.domain.post.dao.PostRepository;
import com.keeper.homepage.domain.post.dao.category.CategoryRepository;
import com.keeper.homepage.domain.post.entity.Post;
import com.keeper.homepage.domain.post.entity.category.Category;
import com.keeper.homepage.domain.thumbnail.entity.Thumbnail;
import com.keeper.homepage.global.error.BusinessException;
import com.keeper.homepage.global.util.file.FileUtil;
import com.keeper.homepage.global.util.thumbnail.ThumbnailUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

  private final PostRepository postRepository;
  private final CategoryRepository categoryRepository;
  private final ThumbnailUtil thumbnailUtil;
  private final FileUtil fileUtil;

  @Transactional
  public Long createPost(Post post, Long categoryId, MultipartFile thumbnail,
      List<MultipartFile> multipartFiles) {
    Thumbnail savedThumbnail = thumbnailUtil.saveThumbnail(thumbnail).orElse(null);
    savePostFiles(multipartFiles, post);
    return savePost(post, savedThumbnail, categoryId);
  }

  private Long savePost(Post post, Thumbnail thumbnail, Long categoryId) {
    post.registerThumbnail(thumbnail);
    post.registerCategory(getCategoryById(categoryId));
    return postRepository.save(post).getId();
  }

  private Category getCategoryById(Long categoryId) {
    return categoryRepository.findById(categoryId)
        .orElseThrow(() -> new BusinessException(categoryId, "categoryId", CATEGORY_NOT_FOUND));
  }

  private void savePostFiles(List<MultipartFile> multipartFiles, Post post) {
    if (multipartFiles == null) {
      return;
    }
    List<FileEntity> files = fileUtil.saveFiles(multipartFiles.toArray(MultipartFile[]::new));
    files.forEach(post::addFile);
  }
}
