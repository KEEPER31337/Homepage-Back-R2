package com.keeper.homepage.domain.post.application;

import static com.keeper.homepage.domain.post.entity.category.Category.DefaultCategory.ANONYMOUS_CATEGORY;
import static com.keeper.homepage.domain.post.entity.category.Category.DefaultCategory.EXAM_CATEGORY;
import static com.keeper.homepage.domain.thumbnail.entity.Thumbnail.DefaultThumbnail.POST_THUMBNAIL;
import static com.keeper.homepage.global.error.ErrorCode.POST_CATEGORY_NOT_FOUND;
import static com.keeper.homepage.global.error.ErrorCode.POST_CANNOT_ACCESSIBLE;
import static com.keeper.homepage.global.error.ErrorCode.POST_NOT_FOUND;
import static com.keeper.homepage.global.error.ErrorCode.POST_PASSWORD_MISMATCH;
import static java.util.stream.Collectors.toList;

import com.keeper.homepage.domain.comment.dao.CommentRepository;
import com.keeper.homepage.domain.file.entity.FileEntity;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.member.entity.embedded.Nickname;
import com.keeper.homepage.domain.post.dao.PostRepository;
import com.keeper.homepage.domain.post.dao.category.CategoryRepository;
import com.keeper.homepage.domain.post.dto.response.FileResponse;
import com.keeper.homepage.domain.post.dto.response.PostResponse;
import com.keeper.homepage.domain.post.entity.Post;
import com.keeper.homepage.domain.post.entity.category.Category;
import com.keeper.homepage.domain.thumbnail.entity.Thumbnail;
import com.keeper.homepage.global.error.BusinessException;
import com.keeper.homepage.global.util.file.FileUtil;
import com.keeper.homepage.global.util.thumbnail.ThumbnailUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Objects;
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
  private final CommentRepository commentRepository;

  public static final String ANONYMOUS_NAME = "익명";
  public static final int EXAM_ACCESSIBLE_POINT = 20000;
  public static final int EXAM_ACCESSIBLE_COMMENT_COUNT = 5;
  public static final int EXAM_ACCESSIBLE_ATTENDANCE_COUNT = 10;

  @Transactional
  public Long create(Post post, Long categoryId, MultipartFile thumbnail,
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
        .orElseThrow(
            () -> new BusinessException(categoryId, "categoryId", POST_CATEGORY_NOT_FOUND));
  }

  private void savePostFiles(List<MultipartFile> multipartFiles, Post post) {
    if (multipartFiles == null) {
      return;
    }
    List<FileEntity> files = fileUtil.saveFiles(multipartFiles.toArray(MultipartFile[]::new));
    files.forEach(post::addFile);
  }

  @Transactional
  public PostResponse find(Member member, long postId, String password) {
    checkInValidPostId(postId);

    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new BusinessException(postId, "postId", POST_NOT_FOUND));

    checkExamPost(member, post);
    checkTempPost(member, post);
    checkSecretPost(member, post, password);

    //visitCountValidation(post, request, response); TODO: 게시글 조회수 중복 방지 기능 구현
    post.addVisitCount();

    String thumbnailPath = getPostThumbnailPath(post.getThumbnail());
    List<FileResponse> fileResponses = post.getFiles().stream()
        .map(FileResponse::from)
        .collect(toList());

    if (post.getCategory().getId().equals(ANONYMOUS_CATEGORY.getId())) {
      return PostResponse.of(post, Nickname.from(ANONYMOUS_NAME), thumbnailPath, fileResponses);
    }
    return PostResponse.of(post, post.getMember().getProfile().getNickname(), thumbnailPath,
        fileResponses);
  }

  private void checkInValidPostId(long postId) {
    if (postId == 1) {
      throw new BusinessException(postId, "postId", POST_NOT_FOUND);
    }
  }

  private void checkExamPost(Member member, Post post) {
    if (post.getCategory().getId().equals(EXAM_CATEGORY.getId())) {
      checkAccessibleExamPost(member, post);
    }
  }

  private void checkAccessibleExamPost(Member member, Post post) {
    if (post.getIsNotice()) {
      return;
    }
    if (member.getPoint() >= EXAM_ACCESSIBLE_POINT
        && countMemberComment(member) >= EXAM_ACCESSIBLE_COMMENT_COUNT
        && member.getMemberAttendance().size()
        >= EXAM_ACCESSIBLE_ATTENDANCE_COUNT) {
      return;
    }
    throw new BusinessException(post.getId(), "postId", POST_CANNOT_ACCESSIBLE);
  }

  private int countMemberComment(Member member) {
    return commentRepository.findAllByMember(member).size();
  }

  private void checkTempPost(Member member, Post post) {
    if (post.getIsTemp()) {
      checkAccessibleTempPost(member, post);
    }
  }

  private void checkAccessibleTempPost(Member me, Post post) {
    if (post.getMember().equals(me)) {
      return;
    }
    throw new BusinessException(post.getId(), "postId", POST_CANNOT_ACCESSIBLE);
  }

  private void checkSecretPost(Member member, Post post, String password) {
    if (post.getIsSecret()) {
      checkAccessibleSecretPost(member, post, password);
    }
  }

  private void checkAccessibleSecretPost(Member me, Post post, String password) {
    if (post.getMember().equals(me)) {
      return;
    }
    if (Objects.equals(post.getPassword(), password)) {
      return;
    }
    throw new BusinessException(password, "password", POST_PASSWORD_MISMATCH);
  }

  // TODO: 게시글 조회수 중복 방지 기능 추가
  private void visitCountValidation(Post post, HttpServletRequest request,
      HttpServletResponse response) {
    Cookie[] cookies = request.getCookies();
  }

  private String getPostThumbnailPath(Thumbnail thumbnail) {
    return thumbnail != null ? thumbnailUtil.getThumbnailURI(thumbnail.getPath()) :
        thumbnailUtil.getThumbnailURI(POST_THUMBNAIL.getPath());
  }
}
