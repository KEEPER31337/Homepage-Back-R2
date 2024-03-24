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

    @OneToMany(mappedBy = "calendar", cascade = [CascadeType.ALL])
    val scheduleType: MutableList<ScheduleType> = mutableListOf(),

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?,
)
