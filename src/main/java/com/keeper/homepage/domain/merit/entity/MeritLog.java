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
    private Member awarderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "giver_id")
    private Member giverId;

    @Column(name = "time", nullable = false)
    private LocalDateTime time;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merit_type_id")
    private MeritType meritType;

    @Builder
    public MeritLog(Member awarderId, Member giverId, MeritType meritType) {
        this.awarderId = awarderId;
        this.giverId = giverId;
        this.time = LocalDateTime.now();
        this.meritType = meritType;

    }

}
