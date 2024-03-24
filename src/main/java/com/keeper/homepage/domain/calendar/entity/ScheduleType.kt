package com.keeper.homepage.domain.calendar.entity

import jakarta.persistence.*
import lombok.AccessLevel
import lombok.NoArgsConstructor

@Entity
@Table(name = "schedule_type")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class ScheduleType(

    @Column(name = "type", nullable = false)
    val type: String,

    @Column(name = "description", nullable = false)
    val description: String,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?,
)