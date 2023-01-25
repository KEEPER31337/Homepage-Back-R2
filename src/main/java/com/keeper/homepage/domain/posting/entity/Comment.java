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
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @Column(name = "member_id")
    private Member member;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "posting_id")
    @Column(name = "posting_id")
    private Posting posting;

    @Column(name = "parent_id")
    private Long parent;

    @Column(name = "content")
    private String content;

    @Column(name = "register_time")
    private LocalDateTime registerTime;
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @Column(name = "like_count")
    private Integer likeCount;
    @Column(name = "dislike_count")
    private Integer dislikeCount;

    @Column(name = "ip_address")
    private String ipAddress;


}
