package com.keeper.homepage.domain.post.entity;

import static com.keeper.homepage.domain.post.entity.category.Category.getCategoryBy;
import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.keeper.homepage.domain.comment.entity.Comment;
import com.keeper.homepage.domain.file.entity.FileEntity;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.member.entity.post.MemberHasPostDislike;
import com.keeper.homepage.domain.member.entity.post.MemberHasPostLike;
import com.keeper.homepage.domain.post.entity.category.Category;
import com.keeper.homepage.domain.post.entity.category.Category.CategoryType;
import com.keeper.homepage.domain.thumbnail.entity.Thumbnail;
import com.keeper.homepage.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@DynamicInsert
@DynamicUpdate
@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@EqualsAndHashCode(of = {"id"}, callSuper = false)
@Table(name = "posting")
public class Post extends BaseEntity {

  private static final int MAX_TITLE_LENGTH = 250;
  private static final int MAX_IP_ADDRESS_LENGTH = 128;
  private static final int MAX_PASSWORD_LENGTH = 512;

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "category_id", nullable = false)
  private Category category;

  @OneToOne(fetch = LAZY)
  @JoinColumn(name = "thumbnail_id")
  private Thumbnail thumbnail;

  @Column(name = "title", nullable = false, length = MAX_TITLE_LENGTH)
  private String title;

  @Column(name = "content", nullable = false)
  private String content;

  @Column(name = "visit_count", nullable = false)
  private Integer visitCount;

  @Column(name = "allow_comment", nullable = false)
  private Boolean allowComment;

  @Column(name = "is_notice", nullable = false)
  private Boolean isNotice;

  @Column(name = "is_secret", nullable = false)
  private Boolean isSecret;

  @Column(name = "is_temp", nullable = false)
  private Boolean isTemp;

  @Column(name = "ip_address", nullable = false, length = MAX_IP_ADDRESS_LENGTH)
  private String ipAddress;

  @Column(name = "password", length = MAX_PASSWORD_LENGTH)
  private String password;

  @OneToMany(mappedBy = "post", cascade = REMOVE)
  private final List<Comment> comments = new ArrayList<>();

  @OneToMany(mappedBy = "post", cascade = ALL)
  private final Set<PostHasFile> postHasFiles = new HashSet<>();

  @OneToMany(mappedBy = "post")
  private final Set<MemberHasPostLike> postLikes = new HashSet<>();

  @OneToMany(mappedBy = "post")
  private final Set<MemberHasPostDislike> postDislikes = new HashSet<>();

  @Builder
  private Post(String title, String content, Member member, Integer visitCount, String ipAddress, Boolean allowComment,
      Boolean isNotice, Boolean isSecret, Boolean isTemp, String password, Category category, Thumbnail thumbnail) {
    this.title = title;
    this.content = content;
    this.member = member;
    this.visitCount = visitCount;
    this.ipAddress = ipAddress;
    this.allowComment = allowComment;
    this.isNotice = isNotice;
    this.isSecret = isSecret;
    this.isTemp = isTemp;
    this.password = password;
    this.category = category;
    this.thumbnail = thumbnail;
  }

  public void addFile(FileEntity file) {
    postHasFiles.add(PostHasFile.builder()
        .post(this)
        .file(file)
        .build());
  }

  public void addCategory(Category category) {
    this.category = category;
  }

  public void changeThumbnail(Thumbnail thumbnail) {
    this.thumbnail = thumbnail;
  }

  public void addVisitCount() {
    this.visitCount++;
  }

  public boolean isCategory(CategoryType category) {
    return this.category.equals(getCategoryBy(category));
  }

  public boolean isMine(Member member) {
    return this.member.equals(member);
  }

  public boolean isSamePassword(String password) {
    return Objects.equals(this.getPassword(), password);
  }

  public Boolean isNotice() {
    return isNotice;
  }

  public Boolean isTemp() {
    return isTemp;
  }

  public Boolean isSecret() {
    return isSecret;
  }

  public Boolean allowComment() {
    return allowComment;
  }

  public String getWriterRealName() {
    return this.member.getRealName();
  }

  public FileEntity getThumbnailFile() {
    return this.thumbnail.getFileEntity();
  }

  public void update(Post post) {
    this.title = post.getTitle();
    this.content = post.getContent();
    this.ipAddress = post.getIpAddress();
    this.allowComment = post.allowComment();
    this.isNotice = post.isNotice();
    this.isSecret = post.isSecret();
    this.isTemp = post.isTemp();
    this.password = post.getPassword();
  }

  public String getThumbnailPath() {
    return Optional.ofNullable(this.thumbnail)
        .map(Thumbnail::getPath)
        .orElse(null);
  }

  public String getCategoryName() {
    return this.category.getType().getName();
  }

  public CategoryType getCategoryType() {
    return this.category.getType();
  }

  public void changeWriter(Member member) {
    this.member = member;
  }

  public void deleteThumbnail() {
    this.thumbnail = null;
  }
}
