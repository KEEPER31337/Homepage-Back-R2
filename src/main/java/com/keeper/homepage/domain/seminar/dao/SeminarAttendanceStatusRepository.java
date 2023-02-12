package com.keeper.homepage.domain.seminar.dao;

import com.keeper.homepage.domain.seminar.entity.SeminarAttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeminarAttendanceStatusRepository extends JpaRepository<SeminarAttendanceStatus, Long> {

}
