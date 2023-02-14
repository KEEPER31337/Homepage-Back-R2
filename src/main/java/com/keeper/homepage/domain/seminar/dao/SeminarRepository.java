package com.keeper.homepage.domain.seminar.dao;

import com.keeper.homepage.domain.seminar.entity.Seminar;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SeminarRepository extends JpaRepository<Seminar, Long> {

  long virtualSeminarId = 1L;

  // TODO: 2023-02-15 나머지 기능들도 default 메서드로 재정의할지 모두 삭제할지 고민해보기

  /**
   * 사용하지 않는 virtual_seminar를 제외한 목록을 가져오기 위해
   * {@link SeminarRepository#findAll() findAll()}을 default 메서드로 <br> 재정의했습니다.
   * {@link SeminarRepository#findAll() findAll()}을 제외한 기능들은 virtual_seminar를 조회할 수 있으므로 주의해야 합니다.
   */
  @Override
  default List<Seminar> findAll() {
    return findAllByIdIsNot(virtualSeminarId);
  }

  List<Seminar> findAllByIdIsNot(Long seminarId);
  
  @Query("SELECT s FROM Seminar s WHERE DATE(s.openTime) =:date")
  Seminar findByOpenTime(@Param("date") LocalDate date);
}
