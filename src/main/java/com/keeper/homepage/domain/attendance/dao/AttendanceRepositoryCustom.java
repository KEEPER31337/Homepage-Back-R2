package com.keeper.homepage.domain.attendance.dao;

import com.keeper.homepage.domain.attendance.entity.Attendance;
import com.keeper.homepage.domain.member.entity.Member;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepositoryCustom {

    List<Attendance> findAllRecent(@Param("member") Member member, @Param("localDate") LocalDate localDate);

}
