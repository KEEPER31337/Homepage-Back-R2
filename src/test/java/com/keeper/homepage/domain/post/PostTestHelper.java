package com.keeper.homepage.domain.post;

import static com.keeper.homepage.domain.post.entity.category.Category.CategoryType.자유게시판;
import static com.keeper.homepage.domain.post.entity.category.Category.getCategoryBy;

import com.keeper.homepage.domain.member.MemberTestHelper;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.post.dao.PostRepository;
import com.keeper.homepage.domain.post.entity.Post;
import com.keeper.homepage.domain.post.entity.category.Category;
import com.keeper.homepage.domain.post.entity.embedded.PostContent;
import com.keeper.homepage.domain.post.entity.embedded.PostStatus;
import com.keeper.homepage.domain.thumbnail.entity.Thumbnail;
import com.keeper.homepage.global.util.thumbnail.ThumbnailTestHelper;
import com.keeper.homepage.global.util.web.WebUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PostTestHelper {

  @Autowired
  PostRepository postRepository;

  @Autowired
  MemberTestHelper memberTestHelper;

  @Autowired
  ThumbnailTestHelper thumbnailTestHelper;

  public Post generate() {
    return this.builder().build();
  }

  public PostBuilder builder() {
    return new PostBuilder();
  }

  public final class PostBuilder {

    private String title;
    private String content;
    private Member member;
    private Integer visitCount;
    private String ipAddress;
    private Boolean allowComment;
    private Boolean isNotice;
    private Boolean isSecret;
    private Boolean isTemp;
    private String password;
    private Category category;
    private Thumbnail thumbnail;

    private PostBuilder() {

    }

    public PostBuilder title(String title) {
      this.title = title;
      return this;
    }

    public PostBuilder content(String content) {
      this.content = content;
      return this;
    }

    public PostBuilder member(Member member) {
      this.member = member;
      return this;
    }

    public PostBuilder visitCount(Integer visitCount) {
      this.visitCount = visitCount;
      return this;
    }

    public PostBuilder ipAddress(String ipAddress) {
      this.ipAddress = ipAddress;
      return this;
    }

    public PostBuilder allowComment(Boolean allowComment) {
      this.allowComment = allowComment;
      return this;
    }

    public PostBuilder isNotice(Boolean isNotice) {
      this.isNotice = isNotice;
      return this;
    }

    public PostBuilder isSecret(Boolean isSecret) {
      this.isSecret = isSecret;
      return this;
    }

    public PostBuilder isTemp(Boolean isTemp) {
      this.isTemp = isTemp;
      return this;
    }

    public PostBuilder password(String password) {
      this.password = password;
      return this;
    }

    public PostBuilder category(Category category) {
      this.category = category;
      return this;
    }

    public PostBuilder thumbnail(Thumbnail thumbnail) {
      this.thumbnail = thumbnail;
      return this;
    }

    public Post build() {
      PostStatus postStatus = PostStatus.builder()
          .isNotice(isNotice != null ? isNotice : false)
          .isSecret(isSecret != null ? isSecret : false)
          .isTemp(isTemp != null ? isTemp : false)
          .build();
      PostContent postContent = PostContent.builder()
          .title(title != null ? title : "게시글 제목")
          .content(content != null ? content : "게시글 내용")
          .thumbnail(thumbnail != null ? thumbnail : thumbnailTestHelper.generateThumbnail())
          .build();
      return postRepository.save(Post.builder()
          .postContent(postContent)
          .member(member != null ? member : memberTestHelper.generate())
          .visitCount(visitCount)
          .ipAddress(ipAddress != null ? ipAddress : WebUtil.getUserIP())
          .allowComment(allowComment != null ? allowComment : true)
          .postStatus(postStatus)
          .password(password)
          .category(category != null ? category : getCategoryBy(자유게시판))
          .build());
    }
  }
}
