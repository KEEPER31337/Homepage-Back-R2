package com.keeper.homepage.domain.game.entity;

import com.keeper.homepage.domain.member.entity.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@DynamicInsert
@DynamicUpdate
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "game_member_info")
@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "last_play_time")
    private LocalDateTime lastPlayTime;

    @Column(name = "dice_per_day", nullable = false)
    private Integer dicePerDay;

    @Column(name = "roulette_per_day", nullable = false)
    private Integer roulettePerDay;

    @Column(name = "lotto_per_day", nullable = false)
    private Integer lottoPerDay;

    @Column(name = "dice_day_point", nullable = false)
    private Integer diceDayPoint;

    @Column(name = "roulette_day_point", nullable = false)
    private Integer rouletteDayPoint;

    @Column(name = "lotto_day_point", nullable = false)
    private Integer lottoDayPoint;

    public void increaseDiceTimes() {
        this.dicePerDay += 1;
    }

    public void increaseRouletteTimes() {
        this.roulettePerDay += 1;
    }

    public void increaseLottoTimes() {
        this.lottoPerDay += 1;
    }

    public Game reset() {
        dicePerDay = 0;
        roulettePerDay = 0;
        lottoPerDay = 0;
        diceDayPoint = 0;
        rouletteDayPoint = 0;
        lottoDayPoint = 0;
        return this;
    }

}
