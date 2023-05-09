package com.keeper.homepage.domain.post.application;

import static com.keeper.homepage.domain.post.entity.category.Category.DefaultCategory.ANONYMOUS_CATEGORY;
import static com.keeper.homepage.domain.post.entity.category.Category.DefaultCategory.EXAM_CATEGORY;
import static com.keeper.homepage.domain.thumbnail.entity.Thumbnail.DefaultThumbnail.DEFAULT_POST_THUMBNAIL;
import static com.keeper.homepage.global.error.ErrorCode.POST_CATEGORY_NOT_FOUND;
import static com.keeper.homepage.global.error.ErrorCode.POST_CANNOT_ACCESSIBLE;
import static com.keeper.homepage.global.error.ErrorCode.POST_PASSWORD_MISMATCH;
import static com.keeper.homepage.global.error.ErrorCode.POST_PASSWORD_NEED;
import static java.lang.Boolean.TRUE;
import static java.util.stream.Collectors.toList;

import com.keeper.homepage.domain.comment.dao.CommentRepository;
import com.keeper.homepage.domain.file.dao.FileRepository;
import com.keeper.homepage.domain.file.entity.FileEntity;
import com.keeper.homepage.domain.member.dao.post.MemberHasPostDislikeRepository;
import com.keeper.homepage.domain.member.dao.post.MemberHasPostLikeRepository;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.post.application.convenience.CategoryFindService;
import com.keeper.homepage.domain.post.application.convenience.PostDeleteService;
import com.keeper.homepage.domain.post.application.convenience.ValidPostFindService;
import com.keeper.homepage.domain.post.dao.PostRepository;
import com.keeper.homepage.domain.post.dao.category.CategoryRepository;
import com.keeper.homepage.domain.post.dto.response.PostListResponse;
import com.keeper.homepage.domain.post.dto.response.PostResponse;
import com.keeper.homepage.domain.post.dto.response.PostWriteResponse;
import com.keeper.homepage.domain.post.entity.Post;
import com.keeper.homepage.domain.post.entity.category.Category;
import com.keeper.homepage.domain.thumbnail.entity.Thumbnail;
import com.keeper.homepage.global.error.BusinessException;
import com.keeper.homepage.global.util.file.FileUtil;
import com.keeper.homepage.global.util.thumbnail.ThumbnailUtil;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

  private final PostRepository postRepository;
  private final CommentRepository commentRepository;
  private final FileRepository fileRepository;
  private final MemberHasPostLikeRepository postLikeRepository;
  private final MemberHasPostDislikeRepository postDislikeRepository;

  private final ThumbnailUtil thumbnailUtil;
  private final FileUtil fileUtil;
  private final ValidPostFindService validPostFindService;
  private final PostDeleteService postDeleteService;
  private final CategoryFindService categoryFindService;

  public static final String ANONYMOUS_NAME = "익명";
  public static final int EXAM_ACCESSIBLE_POINT = 20000;
  public static final int EXAM_ACCESSIBLE_COMMENT_COUNT = 5;
  public static final int EXAM_ACCESSIBLE_ATTENDANCE_COUNT = 10;

  @Transactional
  public Long create(Post post, Long categoryId, MultipartFile thumbnail, List<MultipartFile> multipartFiles) {
    if (TRUE.equals(post.isSecret())) {
      checkPassword(post.getPassword());
    }

    savePostThumbnail(post, thumbnail);
    savePostFiles(post, multipartFiles);
    return savePost(post, categoryId);
  }

  private void checkPassword(String password) {
    if (password == null) {
      throw new BusinessException(password, "password", POST_PASSWORD_NEED);
    }
  }

  private void savePostThumbnail(Post post, MultipartFile thumbnail) {
    Thumbnail savedThumbnail = thumbnailUtil.saveThumbnail(thumbnail).orElse(null);
    post.changeThumbnail(savedThumbnail);
  }

  private void savePostFiles(Post post, List<MultipartFile> multipartFiles) {
    if (multipartFiles == null) {
      return;
    }
    List<FileEntity> files = fileUtil.saveFiles(multipartFiles.toArray(MultipartFile[]::new));
    files.forEach(post::addFile);
  }

  private Long savePost(Post post, Long categoryId) {
    Category category = categoryFindService.findById(categoryId);
    post.addCategory(category);
    return postRepository.save(post).getId();
  }

  @Transactional
  public PostWriteResponse find(Member member, long postId, String password) {
    Post post = validPostFindService.findById(postId);

    checkExamPost(member, post);
    checkTempPost(member, post);
    checkSecretPost(member, post, password);

    //visitCountValidation(post, request, response); TODO: 게시글 조회수 중복 방지 기능 구현
    post.addVisitCount();

    String writerName = getWriterName(post);
    String thumbnailPath = getPostThumbnailPath(post);
    return PostWriteResponse.of(post, writerName, thumbnailPath);
  }

  private void checkExamPost(Member member, Post post) {
    if (post.isCategory(EXAM_CATEGORY.getId())) {
      checkAccessibleExamPost(member, post);
    }
  }

  private void checkAccessibleExamPost(Member member, Post post) {
    if (post.isNotice()) {
      return;
    }
    if (member.getPoint() >= EXAM_ACCESSIBLE_POINT
        && member.getComments().size() >= EXAM_ACCESSIBLE_COMMENT_COUNT
        && member.getMemberAttendance().size() >= EXAM_ACCESSIBLE_ATTENDANCE_COUNT) {
      return;
    }
    throw new BusinessException(post.getId(), "postId", POST_CANNOT_ACCESSIBLE);
  }

  private void checkTempPost(Member member, Post post) {
    if (post.isTemp()) {
      checkAccessibleTempPost(member, post);
    }
  }

  private void checkAccessibleTempPost(Member member, Post post) {
    if (post.isMine(member)) {
      return;
    }
    throw new BusinessException(post.getId(), "postId", POST_CANNOT_ACCESSIBLE);
  }

  private void checkSecretPost(Member member, Post post, String password) {
    if (post.isSecret()) {
      checkAccessibleSecretPost(member, post, password);
    }
  }

  private void checkAccessibleSecretPost(Member member, Post post, String password) {
    if (post.isMine(member)) {
      return;
    }
    if (post.isSamePassword(password)) {
      return;
    }
    throw new BusinessException(password, "password", POST_PASSWORD_MISMATCH);
  }

  private String getPostThumbnailPath(Post post) {
    Thumbnail thumbnail = post.getThumbnail();
    String thumbnailPath = Optional.ofNullable(thumbnail)
        .map(Thumbnail::getPath)
        .orElse(DEFAULT_POST_THUMBNAIL.getPath());
    return thumbnailUtil.getThumbnailPath(thumbnailPath);
  }

  private String getWriterName(Post post) {
    if (post.isCategory(ANONYMOUS_CATEGORY.getId())) {
      return ANONYMOUS_NAME;
    }
    return post.getWriterNickname();
  }

  @Transactional
  public void update(Member member, long postId, Post newPost, List<MultipartFile> files) {
    Post post = validPostFindService.findById(postId);

    if (!post.isMine(member)) {
      throw new BusinessException(post.getId(), "postId", POST_CANNOT_ACCESSIBLE);
    }
    if (TRUE.equals(newPost.isSecret())) {
      checkPassword(newPost.getPassword());
    }

    updateFiles(post, files);
    post.update(newPost);
  }

  public void updatePostThumbnail(Member member, long postId, MultipartFile thumbnail) {
    Post post = validPostFindService.findById(postId);

    if (!post.isMine(member)) {
      throw new BusinessException(post.getId(), "postId", POST_CANNOT_ACCESSIBLE);
    }

    thumbnailUtil.deleteFileAndEntityIfExist(post.getThumbnail());
    savePostThumbnail(post, thumbnail);
  }

  private void updateFiles(Post post, List<MultipartFile> files) {
    List<FileEntity> postFiles = getPostFilesWithoutThumbnailFile(post);
    postFiles.forEach(fileUtil::deleteFileAndEntity);
    savePostFiles(post, files);
  }

  private List<FileEntity> getPostFilesWithoutThumbnailFile(Post post) {
    if (post.getThumbnail() == null) {
      return fileRepository.findAllByPost(post);
    }
    return fileRepository.findAllByPostAndIdNot(post, post.getThumbnailFile().getId());
  }

  public void delete(Member member, long postId) {
    Post post = validPostFindService.findById(postId);

    if (!post.isMine(member)) {
      throw new BusinessException(post.getId(), "postId", POST_CANNOT_ACCESSIBLE);
    }
    postDeleteService.delete(post);
  }

  @Transactional
  public void like(Member member, long postId) {
    Post post = validPostFindService.findById(postId);

    if (member.isLike(post)) {
      member.cancelLike(post);
      return;
    }
    member.like(post);
  }

  @Transactional
  public void dislike(Member member, long postId) {
    Post post = validPostFindService.findById(postId);

    if (member.isDislike(post)) {
      member.cancelDislike(post);
      return;
    }
    member.dislike(post);
  }

  public PostListResponse getNoticePosts(long categoryId) {
    Category category = categoryFindService.findById(categoryId);
    List<Post> posts = postRepository.findAllByCategoryAndIsNoticeTrue(category);
    List<PostResponse> postResponses = posts.stream()
        .map(post -> PostResponse.of(post, getPostThumbnailPath(post)))
        .collect(toList());
    return PostListResponse.from(postResponses);
  }
}
