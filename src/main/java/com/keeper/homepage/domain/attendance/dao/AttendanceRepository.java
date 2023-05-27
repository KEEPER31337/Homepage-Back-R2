package com.keeper.homepage.domain.attendance.dao;

import com.keeper.homepage.domain.attendance.entity.Attendance;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

  List<Attendance> findTop4DistinctByOrderByContinuousDayDesc();
}
