package com.keeper.homepage.domain.game.entity;

import com.keeper.homepage.domain.member.entity.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Table(name = "game_member_info")
@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
    @Column(name = "last_play_time")
    private LocalDateTime lastPlayTime;

    @Column(name = "dice_per_day")
    private Integer dicePerDay;
    @Column(name = "roulette_per_day")
    private Integer roulettePerDay;
    @Column(name = "lotto_per_day")
    private Integer lottoPerDay;

    @Column(name = "dice_day_point")
    private Integer diceDayPoint;
    @Column(name = "roulette_day_point")
    private Integer rouletteDayPoint;
    @Column(name = "lotto_per_point")
    private Integer lottoDayPoint;

}
