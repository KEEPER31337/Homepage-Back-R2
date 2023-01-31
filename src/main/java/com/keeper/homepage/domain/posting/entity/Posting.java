package com.keeper.homepage.domain.posting.entity;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.thumbnail.entity.Thumbnail;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "posting")
public class Posting {

  private static final int MAX_TITLE_LENGTH = 250;
  private static final int MAX_IP_ADDRESS_LENGTH = 128;
  private static final int MAX_PASSWORD_LENGTH = 512;

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "member_id")
  private Member member;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "category_id")
  private Category category;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "thumbnail_id")
  private Thumbnail thumbnail;

  @Column(name = "title", nullable = false, length = MAX_TITLE_LENGTH)
  private String title;

  @Column(name = "content", nullable = false)
  private String content;

  @Column(name = "register_time", nullable = false)
  private LocalDateTime registerTime;

  @Column(name = "update_time", nullable = false)
  private LocalDateTime updateTime;

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

}
