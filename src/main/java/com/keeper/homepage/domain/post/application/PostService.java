package com.keeper.homepage.domain.post.application;

import static com.keeper.homepage.global.error.ErrorCode.CATEGORY_NOT_FOUND;

import com.keeper.homepage.domain.file.entity.FileEntity;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.post.dao.PostRepository;
import com.keeper.homepage.domain.post.dao.category.CategoryRepository;
import com.keeper.homepage.domain.post.dto.request.PostRequest;
import com.keeper.homepage.domain.post.entity.Post;
import com.keeper.homepage.domain.post.entity.category.Category;
import com.keeper.homepage.domain.thumbnail.entity.Thumbnail;
import com.keeper.homepage.global.error.BusinessException;
import com.keeper.homepage.global.util.file.FileUtil;
import com.keeper.homepage.global.util.thumbnail.ThumbnailUtil;
import com.keeper.homepage.global.util.web.WebUtil;
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
  public Long createPost(Member member, PostRequest request) {
    Thumbnail savedThumbnail = thumbnailUtil.saveThumbnail(request.getThumbnail()).orElse(null);
    Post post = savePost(member, request, savedThumbnail);
    savePostFiles(request.getFiles(), post);
    return post.getId();
  }

  private Post savePost(Member member, PostRequest request, Thumbnail thumbnail) {
    Category category = getCategoryById(request.getCategoryId());
    Post post = request.toEntity(member, WebUtil.getUserIP(), category, thumbnail);
    category.addPost(post);
    return postRepository.save(post);
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
