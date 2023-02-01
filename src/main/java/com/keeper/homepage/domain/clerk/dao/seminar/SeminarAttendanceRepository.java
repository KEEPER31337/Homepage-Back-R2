package com.keeper.homepage.domain.clerk.dao.seminar;

import com.keeper.homepage.domain.clerk.entity.seminar.SeminarAttendance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeminarAttendanceRepository extends JpaRepository<SeminarAttendance, Long> {

}
