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

    @ManyToOne( fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne( fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne( fetch = FetchType.LAZY)
    @JoinColumn(name = "thumbnail_id")
    private Thumbnail thumbnail;

    @Column(name = "title")
    private String title;
    @Column(name = "content")
    private String content;

    @Column(name = "register_time")
    private LocalDateTime registerTime;
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @Column(name = "visit_count")
    private Integer visitCount;
    @Column(name = "like_count")
    private Integer likeCount;
    @Column(name = "dislike_count")
    private Integer dislikeCount;
    @Column(name = "comment_count")
    private Integer commentCount;

    @Column(name = "allow_comment")
    private boolean allowComment;
    @Column(name = "is_notice")
    private boolean isNotice;
    @Column(name = "is_secret")
    private boolean isSecret;
    @Column(name = "is_temp")
    private boolean isTemp;

    @Column(name = "ip_address")
    private String ipAddress;
    @Column(name = "password")
    private String password;

}
