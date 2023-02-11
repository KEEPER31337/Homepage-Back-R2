package com.keeper.homepage.domain.posting.application;

import static com.keeper.homepage.global.error.ErrorCode.CATEGORY_NOT_FOUND;

import com.keeper.homepage.domain.file.entity.FileEntity;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.posting.dao.PostingRepository;
import com.keeper.homepage.domain.posting.dao.category.CategoryRepository;
import com.keeper.homepage.domain.posting.dto.request.PostRequest;
import com.keeper.homepage.domain.posting.entity.Posting;
import com.keeper.homepage.domain.posting.entity.category.Category;
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
public class PostingService {

  private final PostingRepository postingRepository;
  private final CategoryRepository categoryRepository;
  private final ThumbnailUtil thumbnailUtil;
  private final FileUtil fileUtil;

  @Transactional
  public Long createPost(Member member, PostRequest request) {
    Thumbnail savedThumbnail = thumbnailUtil.saveThumbnail(request.getThumbnail()).orElse(null);
    Posting posting = savePost(member, request, savedThumbnail);
    savePostFiles(request.getFiles(), posting);
    return posting.getId();
  }

  private Posting savePost(Member member, PostRequest request, Thumbnail thumbnail) {
    return postingRepository.save(request.toEntity(member, WebUtil.getUserIP(), getCategoryById(request.getCategoryId()), thumbnail));
  }

  private Category getCategoryById(Long categoryId) {
    return categoryRepository.findById(categoryId)
        .orElseThrow(() -> new BusinessException(categoryId, "categoryId", CATEGORY_NOT_FOUND));
  }

  private void savePostFiles(List<MultipartFile> multipartFiles, Posting posting) {
    if (multipartFiles == null) {
      return;
    }
    List<FileEntity> files = fileUtil.saveFiles(multipartFiles.toArray(MultipartFile[]::new));
    files.forEach(posting::addFile);
  }
}
