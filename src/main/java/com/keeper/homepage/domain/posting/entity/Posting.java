package com.keeper.homepage.domain.posting.entity;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.thumbnail.entity.Thumbnail;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "posting")
public class Posting {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private Member member;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id")
  private Category category;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "thumbnail_id", nullable = true)
  private Thumbnail thumbnail;

  @Column(name = "title", nullable = false, length = 250)
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
  private Integer allowComment;
  @Column(name = "is_notice", nullable = false)
  private Integer isNotice;
  @Column(name = "is_secret", nullable = false)
  private Integer isSecret;
  @Column(name = "is_temp", nullable = false)
  private Integer isTemp;

  @Column(name = "ip_address", nullable = false, length = 128)
  private String ipAddress;
  @Column(name = "password", nullable = true, length = 512)
  private String password;

}
