package com.keeper.homepage.domain.post.application;

import static com.keeper.homepage.domain.post.entity.category.Category.CategoryType.ANONYMOUS_CATEGORY;
import static com.keeper.homepage.domain.post.entity.category.Category.CategoryType.EXAM_CATEGORY;
import static com.keeper.homepage.global.error.ErrorCode.FILE_NOT_FOUND;
import static com.keeper.homepage.global.error.ErrorCode.POST_ACCESS_CONDITION_NEED;
import static com.keeper.homepage.global.error.ErrorCode.POST_CONTENT_NEED;
import static com.keeper.homepage.global.error.ErrorCode.POST_INACCESSIBLE;
import static com.keeper.homepage.global.error.ErrorCode.POST_PASSWORD_MISMATCH;
import static com.keeper.homepage.global.error.ErrorCode.POST_PASSWORD_NEED;
import static com.keeper.homepage.global.error.ErrorCode.POST_SEARCH_TYPE_NOT_FOUND;

import com.keeper.homepage.domain.file.dao.FileRepository;
import com.keeper.homepage.domain.file.entity.FileEntity;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.post.application.convenience.CategoryFindService;
import com.keeper.homepage.domain.post.application.convenience.PostDeleteService;
import com.keeper.homepage.domain.post.application.convenience.ValidPostFindService;
import com.keeper.homepage.domain.post.dao.PostHasFileRepository;
import com.keeper.homepage.domain.post.dao.PostRepository;
import com.keeper.homepage.domain.post.dto.response.PostDetailResponse;
import com.keeper.homepage.domain.post.dto.response.PostListResponse;
import com.keeper.homepage.domain.post.dto.response.PostResponse;
import com.keeper.homepage.domain.post.entity.Post;
import com.keeper.homepage.domain.post.entity.category.Category;
import com.keeper.homepage.domain.thumbnail.entity.Thumbnail;
import com.keeper.homepage.global.error.BusinessException;
import com.keeper.homepage.global.util.file.FileUtil;
import com.keeper.homepage.global.util.thumbnail.ThumbnailUtil;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
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
  public static final int EXAM_ACCESSIBLE_POINT = 30000;

  @Transactional
  public Long create(Post post, Long categoryId, MultipartFile thumbnail, List<MultipartFile> multipartFiles) {
    if (post.isSecret()) {
      checkPassword(post.getPassword());
    }
    if (!post.isTemp()) {
      checkContent(post.getContent());
    }

    savePostThumbnail(post, thumbnail);
    savePostFiles(post, multipartFiles);
    return savePost(post, categoryId);
  }

  private void checkPassword(String password) {
    if (!StringUtils.hasText(password)) {
      throw new BusinessException(password, "password", POST_PASSWORD_NEED);
    }
  }

  private void checkContent(String content) {
    if (!StringUtils.hasText(content)) {
      throw new BusinessException(content, "content", POST_CONTENT_NEED);
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
    String writerThumbnailPath = getWriterThumbnailPath(post);

    Post previousPost = postRepository.findPreviousPost(postId, post.getCategory()).orElse(null);
    Post nextPost = postRepository.findNextPost(postId, post.getCategory()).orElse(null);
    return PostDetailResponse.of(post, writerName, writerThumbnailPath, previousPost, nextPost);
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
    if (member.getPoint() >= EXAM_ACCESSIBLE_POINT) {
      return;
    }
    throw new BusinessException(member.getPoint(), "point", POST_ACCESS_CONDITION_NEED);
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

  private String getWriterThumbnailPath(Post post) {
    if (post.isCategory(ANONYMOUS_CATEGORY.getId())) {
      return null;
    }
    return post.getMember().getThumbnailPath();
  }

  @Transactional
  public void update(Member member, long postId, Post newPost) {
    Post post = validPostFindService.findById(postId);

    if (!post.isMine(member)) {
      throw new BusinessException(post.getId(), "postId", POST_INACCESSIBLE);
    }
    if (newPost.isSecret()) {
      checkPassword(newPost.getPassword());
    }
    if (!newPost.isTemp()) {
      checkContent(newPost.getContent());
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

  @Transactional
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
          .map(this::getPostResponse);
    }
    if (searchType.equals("title")) {
      return postRepository.findAllRecentByCategoryAndTitle(category, search, pageable)
          .map(this::getPostResponse);
    }
    if (searchType.equals("content")) {
      return postRepository.findAllRecentByCategoryAndContent(category, search, pageable)
          .map(this::getPostResponse);
    }
    if (searchType.equals("writer")) {
      return postRepository.findAllRecentByCategoryAndWriter(category, search, pageable)
          .map(this::getPostResponse);
    }
    if (searchType.equals("title+content")) {
      return postRepository.findAllRecentByCategoryAndTitleOrContent(category, search, pageable)
          .map(this::getPostResponse);
    }
    throw new BusinessException(searchType, "searchType", POST_SEARCH_TYPE_NOT_FOUND);
  }

  private PostResponse getPostResponse(Post post) {
    if (post.isCategory(ANONYMOUS_CATEGORY.getId())) {
      return PostResponse.of(post, ANONYMOUS_NAME, null);
    }
    return PostResponse.from(post);
  }

  public List<PostResponse> getRecentPosts() {
    return postRepository.findAllRecent().stream()
        .map(this::getPostResponse)
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
        .map(this::getPostResponse)
        .limit(10)
        .toList();
  }

  private int getPostScore(Post post) {
    return post.getVisitCount() + post.getPostLikes().size() * 2 - post.getPostDislikes().size();
  }
}
