package com.keeper.homepage.domain.comment.entity;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.member.entity.comment.MemberHasCommentDislike;
import com.keeper.homepage.domain.member.entity.comment.MemberHasCommentLike;
import com.keeper.homepage.domain.post.entity.Post;
import com.keeper.homepage.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
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

  @Column(name = "ip_address", nullable = false, length = MAX_IP_ADDRESS_LENGTH)
  private String ipAddress;

  @Column(name = "is_deleted", nullable = false)
  private Boolean isDeleted;

  @OneToMany(mappedBy = "comment")
  private final Set<MemberHasCommentLike> commentLikes = new HashSet<>();

  @OneToMany(mappedBy = "comment")
  private final Set<MemberHasCommentDislike> commentDislikes = new HashSet<>();

  @Builder
  private Comment(Member member, Post post, Comment parent, String content, String ipAddress) {
    this.member = member;
    this.post = post;
    this.parent = parent;
    this.content = content;
    this.ipAddress = ipAddress;
  }

  public void updateContent(String content) {
    this.content = content;
  }

  public void changeWriter(Member member) {
    this.member = member;
  }

  public boolean isMine(Member member) {
    return this.member.equals(member);
  }

  public String getWriterThumbnailPath() {
    return this.member.getThumbnailPath();
  }

  public boolean hasParent() {
    return this.parent != null;
  }

  public void delete() {
    this.isDeleted = true;
  }

  public boolean isDeleted() {
    return this.isDeleted;
  }
}
