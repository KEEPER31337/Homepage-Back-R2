package com.keeper.homepage.domain.clerk.dao.seminar;

import com.keeper.homepage.domain.clerk.entity.seminar.SeminarAttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeminarAttendanceStatusRepository extends JpaRepository<SeminarAttendanceStatus, Long> {

}
