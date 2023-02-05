package com.keeper.homepage.domain.posting;

import com.keeper.homepage.domain.member.MemberTestHelper;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.posting.dao.PostingRepository;
import com.keeper.homepage.domain.posting.entity.Posting;
import com.keeper.homepage.domain.posting.entity.category.Category;
import com.keeper.homepage.domain.thumbnail.entity.Thumbnail;
import com.keeper.homepage.global.util.thumbnail.ThumbnailTestHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PostingTestHelper {

  @Autowired
  PostingRepository postingRepository;

  @Autowired
  MemberTestHelper memberTestHelper;

  @Autowired
  ThumbnailTestHelper thumbnailTestHelper;

  @Autowired
  CategoryTestHelper categoryTestHelper;

  public Posting generate() {
    return this.builder().build();
  }

  public PostingBuilder builder() {
    return new PostingBuilder();
  }

  public final class PostingBuilder {

    private String title;
    private String content;
    private Member member;
    private Integer visitCount;
    private Integer likeCount;
    private Integer dislikeCount;
    private Integer commentCount;
    private String ipAddress;
    private Boolean allowComment;
    private Boolean isNotice;
    private Boolean isSecret;
    private Boolean isTemp;
    private String password;
    private Category category;
    private Thumbnail thumbnail;

    private PostingBuilder() {

    }

    public PostingBuilder title(String title) {
      this.title = title;
      return this;
    }

    public PostingBuilder content(String content) {
      this.content = content;
      return this;
    }

    public PostingBuilder member(Member member) {
      this.member = member;
      return this;
    }

    public PostingBuilder visitCount(Integer visitCount) {
      this.visitCount = visitCount;
      return this;
    }

    public PostingBuilder likeCount(Integer likeCount) {
      this.likeCount = likeCount;
      return this;
    }

    public PostingBuilder dislikeCount(Integer dislikeCount) {
      this.dislikeCount = dislikeCount;
      return this;
    }

    public PostingBuilder commentCount(Integer commentCount) {
      this.commentCount = commentCount;
      return this;
    }

    public PostingBuilder ipAddress(String ipAddress) {
      this.ipAddress = ipAddress;
      return this;
    }

    public PostingBuilder allowComment(Boolean allowComment) {
      this.allowComment = allowComment;
      return this;
    }

    public PostingBuilder isNotice(Boolean isNotice) {
      this.isNotice = isNotice;
      return this;
    }

    public PostingBuilder isSecret(Boolean isSecret) {
      this.isSecret = isSecret;
      return this;
    }

    public PostingBuilder isTemp(Boolean isTemp) {
      this.isTemp = isTemp;
      return this;
    }

    public PostingBuilder password(String password) {
      this.password = password;
      return this;
    }

    public PostingBuilder category(Category category) {
      this.category = category;
      return this;
    }

    public PostingBuilder thumbnail(Thumbnail thumbnail) {
      this.thumbnail = thumbnail;
      return this;
    }

    public Posting build() {
      return postingRepository.save(Posting.builder()
          .title(title != null ? title : "포스팅 타이틀")
          .content(content != null ? content : "포스팅 컨텐츠")
          .member(member != null ? member : memberTestHelper.generate())
          .visitCount(visitCount)
          .likeCount(likeCount)
          .dislikeCount(dislikeCount)
          .commentCount(commentCount)
          .ipAddress(ipAddress != null ? ipAddress : "0.0.0.0")
          .allowComment(allowComment)
          .isNotice(isNotice)
          .isSecret(isSecret)
          .isTemp(isTemp)
          .password(password)
          .category(category != null ? category : categoryTestHelper.generate())
          .thumbnail(thumbnail != null ? thumbnail : thumbnailTestHelper.generateThumbnail())
          .build());
    }
  }
}
