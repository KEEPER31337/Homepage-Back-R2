package com.keeper.homepage.domain.study.dao;

import com.keeper.homepage.domain.study.entity.Study;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyRepository extends JpaRepository<Study, Long> {

}
