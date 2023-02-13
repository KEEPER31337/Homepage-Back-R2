package com.keeper.homepage.domain.seminar.dao;

import com.keeper.homepage.domain.seminar.entity.Seminar;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeminarRepository extends JpaRepository<Seminar, Long> {

  long virtualSeminarId = 1L;

  @Override
  default List<Seminar> findAll() {
    return findAllByIdIsNot(virtualSeminarId);
  }

  List<Seminar> findAllByIdIsNot(Long seminarId);
}
