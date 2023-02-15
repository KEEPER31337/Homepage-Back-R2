package com.keeper.homepage.domain.comment.entity;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.member.entity.comment.MemberHasCommentDislike;
import com.keeper.homepage.domain.member.entity.comment.MemberHasCommentLike;
import com.keeper.homepage.domain.post.entity.Post;
import com.keeper.homepage.domain.post.entity.category.Category;
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
@Table(name = "comment")
public class Comment extends BaseEntity {

  private static final int MAX_IP_ADDRESS_LENGTH = 128;

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "posting_id", nullable = false)
  private Post post;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "parent_id")
  private Comment parent;

  @Column(name = "content", nullable = false, columnDefinition = "TEXT")
  private String content;

  @Column(name = "like_count", nullable = false)
  private Integer likeCount;

  @Column(name = "dislike_count", nullable = false)
  private Integer dislikeCount;

  @Column(name = "ip_address", nullable = false, length = MAX_IP_ADDRESS_LENGTH)
  private String ipAddress;

  @OneToMany(mappedBy = "comment", orphanRemoval = true)
  private final Set<MemberHasCommentLike> commentLikes = new HashSet<>();

  @OneToMany(mappedBy = "comment", orphanRemoval = true)
  private final Set<MemberHasCommentDislike> commentDislikes = new HashSet<>();

  @OneToMany(mappedBy = "parent", cascade = PERSIST)
  private final List<Comment> children = new ArrayList<>();

  @Builder
  private Comment(Member member, Post post, Comment parent, String content,
      Integer likeCount,
      Integer dislikeCount, String ipAddress) {
    this.member = member;
    this.post = post;
    this.parent = parent;
    this.content = content;
    this.likeCount = likeCount;
    this.dislikeCount = dislikeCount;
    this.ipAddress = ipAddress;
  }

  public void addLike(MemberHasCommentLike like) {
    commentLikes.add(like);
  }

  public void removeLike(Comment comment) {
    commentLikes.removeIf(commentLike -> commentLike.getComment().equals(comment));
  }

  public void addDislike(MemberHasCommentDislike dislike) {
    commentDislikes.add(dislike);
  }

  public void removeDislike(Comment comment) {
    commentDislikes.removeIf(commentDislike -> commentDislike.getComment().equals(comment));
  }

  public void registerPost(Post post) {
    this.post = post;
  }

  public void addChild(Comment child) {
    child.assignParent(this);
    children.add(child);
  }

  public void assignParent(Comment parent) {
    this.parent = parent;
  }
}
