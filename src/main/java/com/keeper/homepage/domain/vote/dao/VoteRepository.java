package com.keeper.homepage.domain.vote.dao;

import com.keeper.homepage.domain.vote.entity.Vote;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<Vote, Long> {

  List<Vote> findAllByStartAtGreaterThanEqualAndStartAtLessThanOrderByStartAtDescIdDesc(
      LocalDateTime startAt,
      LocalDateTime endAt
  );
}
