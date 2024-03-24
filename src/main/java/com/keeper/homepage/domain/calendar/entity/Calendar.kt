package com.keeper.homepage.domain.calendar.entity

import jakarta.persistence.*
import lombok.AccessLevel
import lombok.NoArgsConstructor
import java.time.LocalDateTime

@Entity
@Table(name = "calendar")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class Calendar(

    @Column(name = "name", nullable = false)
    val name: String,

    @Column(name = "start_datetime", nullable = false)
    val startDateTime: LocalDateTime,

    @Column(name = "end_datetime", nullable = false)
    val endDateTime: LocalDateTime,

    @JoinColumn(name = "schedule_type_id", nullable = false)
    @OneToOne(mappedBy = "calendar", fetch = FetchType.LAZY, cascade = [CascadeType.REMOVE])
    val scheduleType: ScheduleType,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
)
