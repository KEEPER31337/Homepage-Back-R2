package com.keeper.homepage.domain.merit.entity;


import com.keeper.homepage.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "merit_log")
public class MeritLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "awarder_id")
    private Member awarder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "giver_id")
    private Member giver;

    @Column(name = "time", nullable = false)
    private LocalDateTime time;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merit_type_id")
    private MeritType meritType;

    @Builder
    public MeritLog(Member awarder, Member giver, MeritType meritType) {
        this.awarder = awarder;
        this.giver = giver;
        this.time = LocalDateTime.now();
        this.meritType = meritType;
    }
}
