package com.keeper.homepage.domain.Schedule.entity

import com.keeper.homepage.domain.calendar.entity.ScheduleType
import jakarta.persistence.*
import lombok.AccessLevel
import lombok.NoArgsConstructor
import java.time.LocalDateTime

@Entity
@Table(name = "schedule")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class Schedule(

    @Column(name = "name", nullable = false)
    val name: String,

    @Column(name = "start_time", nullable = false)
    val startTime: LocalDateTime,

    @Column(name = "end_time", nullable = false)
    val endTime: LocalDateTime,

    @JoinColumn(name = "schedule_type_id", nullable = false)
    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.REMOVE])
    val scheduleType: ScheduleType,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
)
