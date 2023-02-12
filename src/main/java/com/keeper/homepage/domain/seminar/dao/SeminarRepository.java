package com.keeper.homepage.domain.seminar.dao;

import com.keeper.homepage.domain.seminar.entity.Seminar;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeminarRepository extends JpaRepository<Seminar, Long> {

}
