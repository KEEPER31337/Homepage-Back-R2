package com.keeper.homepage.domain.post.application;

import static com.keeper.homepage.domain.post.entity.category.Category.CategoryType.시험게시판;
import static com.keeper.homepage.domain.post.entity.category.Category.CategoryType.익명게시판;
import static com.keeper.homepage.global.error.ErrorCode.POST_ACCESS_CONDITION_NEED;
import static com.keeper.homepage.global.error.ErrorCode.POST_COMMENT_NEED;
import static com.keeper.homepage.global.error.ErrorCode.POST_CONTENT_NEED;
import static com.keeper.homepage.global.error.ErrorCode.POST_HAS_NOT_THAT_FILE;
import static com.keeper.homepage.global.error.ErrorCode.POST_INACCESSIBLE;
import static com.keeper.homepage.global.error.ErrorCode.POST_PASSWORD_MISMATCH;
import static com.keeper.homepage.global.error.ErrorCode.POST_PASSWORD_NEED;
import static com.keeper.homepage.global.error.ErrorCode.POST_SEARCH_TYPE_NOT_FOUND;

import com.keeper.homepage.domain.file.application.FileService;
import com.keeper.homepage.domain.file.entity.FileEntity;
import com.keeper.homepage.domain.member.application.convenience.MemberFindService;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.post.application.convenience.CategoryFindService;
import com.keeper.homepage.domain.post.application.convenience.PostDeleteService;
import com.keeper.homepage.domain.post.application.convenience.ValidPostFindService;
import com.keeper.homepage.domain.post.dao.PostHasFileRepository;
import com.keeper.homepage.domain.post.dao.PostRepository;
import com.keeper.homepage.domain.post.dto.response.FileResponse;
import com.keeper.homepage.domain.post.dto.response.MainPostResponse;
import com.keeper.homepage.domain.post.dto.response.MemberPostResponse;
import com.keeper.homepage.domain.post.dto.response.PostDetailResponse;
import com.keeper.homepage.domain.post.dto.response.PostListResponse;
import com.keeper.homepage.domain.post.dto.response.PostResponse;
import com.keeper.homepage.domain.post.dto.response.TempPostResponse;
import com.keeper.homepage.domain.post.entity.Post;
import com.keeper.homepage.domain.post.entity.PostHasFile;
import com.keeper.homepage.domain.post.entity.category.Category;
import com.keeper.homepage.domain.post.entity.category.Category.CategoryType;
import com.keeper.homepage.domain.thumbnail.entity.Thumbnail;
import com.keeper.homepage.global.error.BusinessException;
import com.keeper.homepage.global.util.file.FileUtil;
import com.keeper.homepage.global.util.redis.RedisUtil;
import com.keeper.homepage.global.util.thumbnail.ThumbnailUtil;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

  private final PostRepository postRepository;
  private final PostHasFileRepository postHasFileRepository;

  private final ThumbnailUtil thumbnailUtil;
  private final FileUtil fileUtil;
  private final ValidPostFindService validPostFindService;
  private final PostDeleteService postDeleteService;
  private final CategoryFindService categoryFindService;
  private final MemberFindService memberFindService;
  private final FileService fileService;
  private final RedisUtil redisUtil;

  private static final String ANONYMOUS_NAME = "익명";
  private static final int EXAM_ACCESSIBLE_POINT = 30000;
  private static final int EXAM_READ_DEDUCTION_POINT = 10000;
  private static final String EXAM_READ_POINT_MESSAGE = "족보 열람";

  @Transactional
  public Long create(Post post, Long categoryId, MultipartFile thumbnail, List<MultipartFile> multipartFiles) {
    if (post.isSecret()) {
      checkPassword(post.getPassword());
    }
    if (!post.isTemp()) {
      checkContent(post.getContent());
    }
    if (categoryId == 익명게시판.getId()) {
      Member virtualMember = memberFindService.getVirtualMember();
      post.changeWriter(virtualMember);
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

    addVisitCount(post, member);

    Post previousPost = postRepository.findPreviousPost(postId, post.getCategory()).orElse(null);
    Post nextPost = postRepository.findNextPost(postId, post.getCategory()).orElse(null);
    boolean isLike = member.isLike(post);
    boolean isDislike = member.isDislike(post);

    if (post.isCategory(익명게시판)) {
      return PostDetailResponse.of(post, ANONYMOUS_NAME, isLike, isDislike, previousPost, nextPost);
    }
    if (post.isCategory(시험게시판) && !post.isMine(member)) {
      boolean isRead = member.isRead(post);
      return PostDetailResponse.of(post, isLike, isDislike, isRead, previousPost, nextPost);
    }
    return PostDetailResponse.of(post, isLike, isDislike, previousPost, nextPost);
  }

  private void checkExamPost(Member member, Post post) {
    if (post.isCategory(시험게시판)) {
      checkAccessibleExamPost(member, post);
    }
  }

  private void checkAccessibleExamPost(Member member, Post post) {
    if (post.isMine(member)) {
      return;
    }
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

  private void addVisitCount(Post post, Member member) {
    String key = "post:" + post.getId() + ":memberId:" + member.getId();
    Optional<String> isView = redisUtil.getData(key, String.class);
    if (isView.isEmpty()) {
      redisUtil.setDataExpire(key, "true", RedisUtil.toMidNight());
      post.addVisitCount();
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

  @Transactional
  public List<FileResponse> getFiles(Member member, long postId) {
    Post post = validPostFindService.findById(postId);

    if (post.isCategory(시험게시판) && !member.isRead(post) && !post.isMine(member)) {
      member.read(post);
      member.minusPoint(EXAM_READ_DEDUCTION_POINT, EXAM_READ_POINT_MESSAGE);
    }
    return post.getPostHasFiles()
        .stream()
        .map(PostHasFile::getFile)
        .map(FileResponse::from)
        .toList();
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
    post.deleteThumbnail();
  }

  @Transactional
  public CategoryType delete(Member member, long postId) {
    Post post = validPostFindService.findById(postId);
    CategoryType categoryType = post.getCategoryType();

    if (!post.isMine(member)) {
      throw new BusinessException(post.getId(), "postId", POST_INACCESSIBLE);
    }
    postDeleteService.delete(post);
    return categoryType;
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
        .map(this::getPostResponse)
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
  public void deletePostFile(Member member, long postId, List<Long> fileIds) {
    Post post = validPostFindService.findById(postId);

    if (!post.isMine(member)) {
      throw new BusinessException(post.getId(), "postId", POST_INACCESSIBLE);
    }
    fileIds.stream().forEach(fileId -> {
      FileEntity file = fileService.findById(fileId);
      postHasFileRepository.deleteByPostAndFile(post, file);
      fileUtil.deleteFileAndEntity(file);
    });
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
    if (post.isCategory(익명게시판)) {
      return PostResponse.of(post, ANONYMOUS_NAME);
    }
    return PostResponse.from(post);
  }

  private MainPostResponse getMainPostResponse(Post post) {
    if (post.isCategory(익명게시판)) {
      return MainPostResponse.of(post, ANONYMOUS_NAME);
    }
    return MainPostResponse.from(post);
  }

  public List<MainPostResponse> getRecentPosts() {
    return postRepository.findAllRecent().stream()
        .map(this::getMainPostResponse)
        .limit(10)
        .toList();
  }

  public List<MainPostResponse> getTrendPosts() {
    LocalDateTime startDateTime = LocalDateTime.now().minusWeeks(2);
    LocalDateTime endDateTime = LocalDateTime.now().plusDays(1);
    List<Post> posts = postRepository.findAllTrend(startDateTime, endDateTime);
    posts.sort((post1, post2) -> {
      int postScore1 = getPostScore(post1);
      int postScore2 = getPostScore(post2);
      return Integer.compare(postScore2, postScore1);
    });
    return posts.stream()
        .map(this::getMainPostResponse)
        .limit(10)
        .toList();
  }

  private int getPostScore(Post post) {
    return post.getVisitCount() + post.getPostLikes().size() * 2 - post.getPostDislikes().size();
  }

  public Page<MemberPostResponse> getMemberPosts(long memberId, Pageable pageable) {
    Member member = memberFindService.findById(memberId);
    return postRepository.findAllByMemberAndIsTempFalse(member, pageable)
        .map(MemberPostResponse::from);
  }

  public Page<TempPostResponse> getTempPosts(Member member, Pageable pageable) {
    return postRepository.findAllByMemberAndIsTempTrue(member, pageable)
        .map(TempPostResponse::from);
  }

  public FileEntity getFile(Member member, long postId, long fileId) {
    Post post = validPostFindService.findById(postId);
    if (!member.hasComment(post) && !post.isMine(member)) {
      throw new BusinessException(postId, "postId", POST_COMMENT_NEED);
    }
    FileEntity file = fileService.findById(fileId);
    if (!file.isPost(post)) {
      throw new BusinessException(postId, "postId", POST_HAS_NOT_THAT_FILE);
    }
    return file;
  }
}
