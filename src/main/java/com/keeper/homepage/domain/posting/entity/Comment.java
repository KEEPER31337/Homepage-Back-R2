package com.keeper.homepage.domain.posting.entity;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.keeper.homepage.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "comment")
public class Comment {

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
  private Posting posting;

  @Column(name = "parent_id", nullable = false)
  private Long parent;

  @Column(name = "content", nullable = false, columnDefinition = "TEXT")
  private String content;

  @Column(name = "register_time", nullable = false)
  private LocalDateTime registerTime;

  @Column(name = "update_time", nullable = false)
  private LocalDateTime updateTime;

  @Column(name = "like_count", nullable = false)
  private Integer likeCount;

  @Column(name = "dislike_count", nullable = false)
  private Integer dislikeCount;

  @Column(name = "ip_address", nullable = false, length = MAX_IP_ADDRESS_LENGTH)
  private String ipAddress;
}
