package com.keeper.homepage.domain.post.entity;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.keeper.homepage.domain.file.entity.FileEntity;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.member.entity.post.MemberHasPostDislike;
import com.keeper.homepage.domain.member.entity.post.MemberHasPostLike;
import com.keeper.homepage.domain.post.entity.category.Category;
import com.keeper.homepage.domain.comment.entity.Comment;
import com.keeper.homepage.domain.thumbnail.entity.Thumbnail;
import com.keeper.homepage.global.entity.BaseEntity;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

  public static final int MAX_TITLE_LENGTH = 250;
  private static final int MAX_IP_ADDRESS_LENGTH = 128;
  public static final int MAX_PASSWORD_LENGTH = 512;

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "category_id", nullable = false)
  private Category category;

  @OneToOne(fetch = LAZY, cascade = REMOVE)
  @JoinColumn(name = "thumbnail_id")
  private Thumbnail thumbnail;

  @Column(name = "title", nullable = false, length = MAX_TITLE_LENGTH)
  private String title;

  @Column(name = "content", nullable = false)
  private String content;

  @Column(name = "visit_count", nullable = false)
  private Integer visitCount;

  @Column(name = "like_count", nullable = false)
  private Integer likeCount;

  @Column(name = "dislike_count", nullable = false)
  private Integer dislikeCount;

  @Column(name = "comment_count", nullable = false)
  private Integer commentCount;

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

  @OneToMany(mappedBy = "post", cascade = ALL, orphanRemoval = true)
  private final List<Comment> comments = new ArrayList<>();

  @OneToMany(mappedBy = "post", orphanRemoval = true)
  private final List<FileEntity> files = new ArrayList<>();

  @OneToMany(mappedBy = "post", orphanRemoval = true)
  private final Set<MemberHasPostLike> postLikes = new HashSet<>();

  @OneToMany(mappedBy = "post", orphanRemoval = true)
  private final Set<MemberHasPostDislike> postDislikes = new HashSet<>();

  @Builder
  private Post(String title, String content, Member member, Integer visitCount,
      Integer likeCount, Integer dislikeCount, Integer commentCount, String ipAddress,
      Boolean allowComment, Boolean isNotice, Boolean isSecret, Boolean isTemp, String password,
      Category category, Thumbnail thumbnail) {
    this.title = title;
    this.content = content;
    this.member = member;
    this.visitCount = visitCount;
    this.likeCount = likeCount;
    this.dislikeCount = dislikeCount;
    this.commentCount = commentCount;
    this.ipAddress = ipAddress;
    this.allowComment = allowComment;
    this.isNotice = isNotice;
    this.isSecret = isSecret;
    this.isTemp = isTemp;
    this.password = password;
    this.category = category;
    this.thumbnail = thumbnail;
  }

  public void addComment(Comment comment) {
    comment.registerPost(this);
    comments.add(comment);
  }

  public void addFile(FileEntity file) {
    file.registerPost(this);
    files.add(file);
  }

  public void addLike(MemberHasPostLike like) {
    postLikes.add(like);
  }

  public void removeLike(Post post) {
    postLikes.removeIf(postLike -> postLike.getPost().equals(post));
  }

  public void addDislike(MemberHasPostDislike dislike) {
    postDislikes.add(dislike);
  }

  public void removeDislike(Post post) {
    postDislikes.removeIf(postDislike -> postDislike.getPost().equals(post));
  }

  public void assignCategory(Category category) {
    this.category = category;
  }
}
