package com.keeper.homepage.domain.post.api;

import static org.springframework.data.domain.Sort.Direction.DESC;

import com.keeper.homepage.domain.file.application.FileService;
import com.keeper.homepage.domain.file.entity.FileEntity;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.post.application.PostService;
import com.keeper.homepage.domain.post.dto.request.PostCreateRequest;
import com.keeper.homepage.domain.post.dto.request.PostFileDeleteRequest;
import com.keeper.homepage.domain.post.dto.request.PostUpdateRequest;
import com.keeper.homepage.domain.post.dto.response.CategoryResponse;
import com.keeper.homepage.domain.post.dto.response.FileResponse;
import com.keeper.homepage.domain.post.dto.response.MainPostResponse;
import com.keeper.homepage.domain.post.dto.response.MemberPostResponse;
import com.keeper.homepage.domain.post.dto.response.PostDetailResponse;
import com.keeper.homepage.domain.post.dto.response.PostListResponse;
import com.keeper.homepage.domain.post.dto.response.PostResponse;
import com.keeper.homepage.domain.post.dto.response.TempPostResponse;
import com.keeper.homepage.domain.post.entity.category.Category.CategoryType;
import com.keeper.homepage.global.config.security.annotation.LoginMember;
import com.keeper.homepage.global.util.web.WebUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

  private final PostService postService;
  private final FileService fileService;

  @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity<Void> createPost(
      @LoginMember Member member,
      @RequestPart @Valid PostCreateRequest request,
      @RequestPart(required = false) MultipartFile thumbnail,
      @RequestPart(required = false) List<MultipartFile> files
  ) {
    Long postId = postService.create(
        request.toEntity(member, WebUtil.getUserIP()),
        request.getCategoryId(), thumbnail, files);
    return ResponseEntity.status(HttpStatus.CREATED)
        .location(URI.create("/posts/" + postId))
        .build();
  }

  @GetMapping("/{postId}")
  public ResponseEntity<PostDetailResponse> getPost(
      @LoginMember Member member,
      @PathVariable long postId,
      @RequestParam(required = false) String password
  ) {
    PostDetailResponse postDetailResponse = postService.find(member, postId, password);
    return ResponseEntity.status(HttpStatus.OK)
        .body(postDetailResponse);
  }

  @GetMapping("/{postId}/files")
  public ResponseEntity<List<FileResponse>> getPostFiles(
      @LoginMember Member member,
      @PathVariable long postId
  ) {
    List<FileResponse> responses = postService.getFiles(member, postId);
    return ResponseEntity.status(HttpStatus.OK)
        .body(responses);
  }

  @PutMapping("/{postId}")
  public ResponseEntity<Void> updatePost(
      @LoginMember Member member,
      @PathVariable long postId,
      @RequestBody @Valid PostUpdateRequest request
  ) {
    postService.update(member, postId, request.toEntity(WebUtil.getUserIP()));
    return ResponseEntity.status(HttpStatus.CREATED)
        .location(URI.create("/posts/" + postId))
        .build();
  }

  @PatchMapping("/{postId}/thumbnail")
  public ResponseEntity<Void> updatePostThumbnail(
      @LoginMember Member member,
      @PathVariable long postId,
      @ModelAttribute MultipartFile thumbnail
  ) {
    postService.updatePostThumbnail(member, postId, thumbnail);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{postId}/thumbnail")
  public ResponseEntity<Void> deletePostThumbnail(
      @LoginMember Member member,
      @PathVariable long postId
  ) {
    postService.deletePostThumbnail(member, postId);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/{postId}/files")
  public ResponseEntity<Void> addPostFiles(
      @LoginMember Member member,
      @PathVariable long postId,
      @RequestPart List<MultipartFile> files
  ) {
    postService.addPostFiles(member, postId, files);
    return ResponseEntity.status(HttpStatus.CREATED)
        .build();
  }

  @DeleteMapping("/{postId}/files")
  public ResponseEntity<Void> deletePostFile(
      @LoginMember Member member,
      @PathVariable long postId,
      @RequestBody @Valid PostFileDeleteRequest request
  ) {
    postService.deletePostFile(member, postId, request.fileIds());
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{postId}")
  public ResponseEntity<CategoryResponse> deletePost(
      @LoginMember Member member,
      @PathVariable long postId
  ) {
    CategoryType categoryType = postService.delete(member, postId);
    return ResponseEntity.ok(CategoryResponse.from(categoryType));
  }

  @PatchMapping("/{postId}/likes")
  public ResponseEntity<Void> likePost(
      @LoginMember Member member,
      @PathVariable long postId
  ) {
    postService.like(member, postId);
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/{postId}/dislikes")
  public ResponseEntity<Void> dislikePost(
      @LoginMember Member member,
      @PathVariable long postId
  ) {
    postService.dislike(member, postId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/notices")
  public ResponseEntity<PostListResponse> getNoticePosts(
      @RequestParam long categoryId
  ) {
    PostListResponse response = postService.getNoticePosts(categoryId);
    return ResponseEntity.status(HttpStatus.OK)
        .body(response);
  }

  @GetMapping
  public ResponseEntity<Page<PostResponse>> getPosts(
      @RequestParam long categoryId,
      @RequestParam(required = false) String searchType,
      @RequestParam(required = false) String search,
      @RequestParam(defaultValue = "0") @PositiveOrZero int page,
      @RequestParam(defaultValue = "10") @PositiveOrZero int size
  ) {
    return ResponseEntity.ok(postService.getPosts(categoryId, searchType, search, PageRequest.of(page, size)));
  }

  @GetMapping("/recent")
  public ResponseEntity<List<MainPostResponse>> getRecentPosts() {
    return ResponseEntity.ok(postService.getRecentPosts());
  }


  @GetMapping("/trend")
  public ResponseEntity<List<MainPostResponse>> getTrendPosts() {
    return ResponseEntity.ok(postService.getTrendPosts());
  }

  @GetMapping("/members/{memberId}")
  public ResponseEntity<Page<MemberPostResponse>> getMemberPosts(
      @PathVariable long memberId,
      @RequestParam(defaultValue = "0") @PositiveOrZero int page,
      @RequestParam(defaultValue = "10") @PositiveOrZero int size
  ) {
    Page<MemberPostResponse> responses = postService.getMemberPosts(memberId,
        PageRequest.of(page, size, Sort.by(DESC, "registerTime")));
    return ResponseEntity.ok(responses);
  }

  @GetMapping("/temp")
  public ResponseEntity<Page<TempPostResponse>> getTempPosts(
      @LoginMember Member member,
      @RequestParam(defaultValue = "0") @PositiveOrZero int page,
      @RequestParam(defaultValue = "10") @PositiveOrZero int size
  ) {
    Page<TempPostResponse> responses = postService.getTempPosts(member,
        PageRequest.of(page, size, Sort.by(DESC, "registerTime")));
    return ResponseEntity.ok(responses);
  }

  @GetMapping("/{postId}/files/{fileId}")
  public ResponseEntity<Resource> downloadFile(
      @LoginMember Member member,
      @PathVariable long postId,
      @PathVariable long fileId
  ) throws IOException {
    FileEntity file = postService.getFile(member, postId, fileId);
    Resource resource = fileService.getFileResource(file);
    String fileName = fileService.getFileName(file);
    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
        .body(resource);
  }
}
