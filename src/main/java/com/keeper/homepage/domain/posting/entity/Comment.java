package com.keeper.homepage.domain.posting.entity;

import com.keeper.homepage.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "comment")
public class Comment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @Column(name = "member_id", nullable = false)
    private Member member;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "posting_id")
    @Column(name = "posting_id", nullable = false)
    private Posting posting;

    @Column(name = "parent_id", nullable = false)
    private Long parent;

    @Column(name = "content")
    private String content;

    @Column(name = "register_time", nullable = false)
    private LocalDateTime registerTime;
    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateTime;

    @Column(name = "like_count", nullable = false)
    private Integer likeCount;
    @Column(name = "dislike_count", nullable = false)
    private Integer dislikeCount;

    @Column(name = "ip_address", nullable = false)
    private String ipAddress;


}
