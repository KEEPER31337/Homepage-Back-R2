package com.keeper.homepage.domain.seminar.dao;

import com.keeper.homepage.domain.seminar.entity.SeminarAttendance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeminarAttendanceRepository extends JpaRepository<SeminarAttendance, Long> {

}
