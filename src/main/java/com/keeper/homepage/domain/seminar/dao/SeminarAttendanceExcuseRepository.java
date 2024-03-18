package com.keeper.homepage.domain.seminar.dao;

import com.keeper.homepage.domain.seminar.entity.SeminarAttendance;
import com.keeper.homepage.domain.seminar.entity.SeminarAttendanceExcuse;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeminarAttendanceExcuseRepository extends
    JpaRepository<SeminarAttendanceExcuse, Long> {

  Optional<SeminarAttendanceExcuse> findAllBySeminarAttendance(SeminarAttendance seminarAttendance);

}
