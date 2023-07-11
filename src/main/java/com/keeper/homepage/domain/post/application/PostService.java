package com.keeper.homepage.domain.post.application;

import static com.keeper.homepage.domain.post.entity.category.Category.DefaultCategory.ANONYMOUS_CATEGORY;
import static com.keeper.homepage.domain.post.entity.category.Category.DefaultCategory.EXAM_CATEGORY;
import static com.keeper.homepage.global.error.ErrorCode.FILE_NOT_FOUND;
import static com.keeper.homepage.global.error.ErrorCode.POST_INACCESSIBLE;
import static com.keeper.homepage.global.error.ErrorCode.POST_PASSWORD_MISMATCH;
import static com.keeper.homepage.global.error.ErrorCode.POST_PASSWORD_NEED;
import static com.keeper.homepage.global.error.ErrorCode.POST_SEARCH_TYPE_NOT_FOUND;
import static java.lang.Boolean.TRUE;

import com.keeper.homepage.domain.file.dao.FileRepository;
import com.keeper.homepage.domain.file.entity.FileEntity;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.post.application.convenience.CategoryFindService;
import com.keeper.homepage.domain.post.application.convenience.PostDeleteService;
import com.keeper.homepage.domain.post.application.convenience.ValidPostFindService;
import com.keeper.homepage.domain.post.dao.PostHasFileRepository;
import com.keeper.homepage.domain.post.dao.PostRepository;
import com.keeper.homepage.domain.post.dto.response.PostListResponse;
import com.keeper.homepage.domain.post.dto.response.PostResponse;
import com.keeper.homepage.domain.post.dto.response.PostDetailResponse;
import com.keeper.homepage.domain.post.entity.Post;
import com.keeper.homepage.domain.post.entity.category.Category;
import com.keeper.homepage.domain.thumbnail.entity.Thumbnail;
import com.keeper.homepage.global.error.BusinessException;
import com.keeper.homepage.global.util.file.FileUtil;
import com.keeper.homepage.global.util.thumbnail.ThumbnailUtil;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

  private final PostRepository postRepository;
  private final FileRepository fileRepository;
  private final PostHasFileRepository postHasFileRepository;

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
  public PostDetailResponse find(Member member, long postId, String password) {
    Post post = validPostFindService.findById(postId);

    checkExamPost(member, post);
    checkTempPost(member, post);
    checkSecretPost(member, post, password);

    //visitCountValidation(post, request, response); TODO: 게시글 조회수 중복 방지 기능 구현
    post.addVisitCount();

    String writerName = getWriterName(post);

    Post previousPost = postRepository.findPreviousPost(postId, post.getCategory()).orElse(null);
    Post nextPost = postRepository.findNextPost(postId, post.getCategory()).orElse(null);
    return PostDetailResponse.of(post, writerName, previousPost, nextPost);
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
    throw new BusinessException(post.getId(), "postId", POST_INACCESSIBLE);
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
    throw new BusinessException(post.getId(), "postId", POST_INACCESSIBLE);
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

  private String getWriterName(Post post) {
    if (post.isCategory(ANONYMOUS_CATEGORY.getId())) {
      return ANONYMOUS_NAME;
    }
    return post.getWriterNickname();
  }

  @Transactional
  public void update(Member member, long postId, Post newPost) {
    Post post = validPostFindService.findById(postId);

    if (!post.isMine(member)) {
      throw new BusinessException(post.getId(), "postId", POST_INACCESSIBLE);
    }
    if (TRUE.equals(newPost.isSecret())) {
      checkPassword(newPost.getPassword());
    }
    post.update(newPost);
  }

  @Transactional
  public void updatePostThumbnail(Member member, long postId, MultipartFile thumbnail) {
    Post post = validPostFindService.findById(postId);

    if (!post.isMine(member)) {
      throw new BusinessException(post.getId(), "postId", POST_INACCESSIBLE);
    }
    thumbnailUtil.deleteFileAndEntityIfExist(post.getThumbnail());
    savePostThumbnail(post, thumbnail);
  }

  @Transactional
  public void deletePostThumbnail(Member member, long postId) {
    Post post = validPostFindService.findById(postId);

    if (!post.isMine(member)) {
      throw new BusinessException(post.getId(), "postId", POST_INACCESSIBLE);
    }
    thumbnailUtil.deleteFileAndEntityIfExist(post.getThumbnail());
  }

  public void delete(Member member, long postId) {
    Post post = validPostFindService.findById(postId);

    if (!post.isMine(member)) {
      throw new BusinessException(post.getId(), "postId", POST_INACCESSIBLE);
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
    List<Post> posts = postRepository.findAllNoticeByCategory(category);
    List<PostResponse> postResponses = posts.stream()
        .map(PostResponse::from)
        .toList();
    return PostListResponse.from(postResponses);
  }

  @Transactional
  public void addPostFiles(Member member, long postId, List<MultipartFile> files) {
    Post post = validPostFindService.findById(postId);

    if (!post.isMine(member)) {
      throw new BusinessException(post.getId(), "postId", POST_INACCESSIBLE);
    }
    savePostFiles(post, files);
  }

  @Transactional
  public void deletePostFile(Member member, long postId, long fileId) {
    Post post = validPostFindService.findById(postId);

    if (!post.isMine(member)) {
      throw new BusinessException(post.getId(), "postId", FILE_NOT_FOUND);
    }
    FileEntity file = fileRepository.findById(fileId)
        .orElseThrow(() -> new BusinessException(fileId, "fileId", FILE_NOT_FOUND));
    postHasFileRepository.deleteByPostAndFile(post, file);
    fileUtil.deleteFileAndEntity(file);
  }

  public Page<PostResponse> getPosts(long categoryId, String searchType, String search, PageRequest pageable) {
    Category category = categoryFindService.findById(categoryId);
    if (searchType == null) {
      return postRepository.findAllRecentByCategory(category, pageable)
          .map(PostResponse::from);
    }
    if (searchType.equals("title")) {
      return postRepository.findAllRecentByCategoryAndTitle(category, search, pageable)
          .map(PostResponse::from);
    }
    if (searchType.equals("content")) {
      return postRepository.findAllRecentByCategoryAndContent(category, search, pageable)
          .map(PostResponse::from);
    }
    if (searchType.equals("writer")) {
      return postRepository.findAllRecentByCategoryAndWriter(category, search, pageable)
          .map(PostResponse::from);
    }
    if (searchType.equals("title+content")) {
      return postRepository.findAllRecentByCategoryAndTitleOrContent(category, search, pageable)
          .map(PostResponse::from);
    }
    throw new BusinessException(searchType, "searchType", POST_SEARCH_TYPE_NOT_FOUND);
  }

  public List<PostResponse> getRecentPosts() {
    return postRepository.findAllRecent().stream()
        .map(PostResponse::from)
        .limit(10)
        .toList();
  }

  public List<PostResponse> getTrendPosts() {
    LocalDateTime startDateTime = LocalDateTime.now().minusWeeks(2);
    LocalDateTime endDateTime = LocalDateTime.now().plusDays(1);
    List<Post> posts = postRepository.findAllTrend(startDateTime, endDateTime);
    posts.sort((post1, post2) -> {
      int postScore1 = getPostScore(post1);
      int postScore2 = getPostScore(post2);
      return Integer.compare(postScore2, postScore1);
    });
    return posts.stream()
        .map(PostResponse::from)
        .limit(10)
        .toList();
  }

  private int getPostScore(Post post) {
    return post.getVisitCount() + post.getPostLikes().size() * 2 - post.getPostDislikes().size();
  }
}
