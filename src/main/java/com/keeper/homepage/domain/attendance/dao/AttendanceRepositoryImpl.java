package com.keeper.homepage.domain.attendance.dao;


import com.keeper.homepage.domain.attendance.entity.Attendance;
import com.keeper.homepage.domain.member.entity.Member;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

import static com.keeper.homepage.domain.attendance.entity.QAttendance.*;

@RequiredArgsConstructor
public class AttendanceRepositoryImpl implements AttendanceRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Attendance> findAllRecent(@Param("member") Member member, @Param("localDate") LocalDate localDate) {
        return queryFactory
                .selectFrom(attendance)
                .where(
                        attendance.member.eq(member)
                        .and(attendance.date.goe(localDate))
                )
                .fetch();
    }

}
