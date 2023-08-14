package com.keeper.homepage.domain.election.dao;

import com.keeper.homepage.domain.election.entity.Election;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ElectionRepository extends JpaRepository<Election, Long> {
  Optional<Election> findByIdAndIdNot(Long electionId, Long virtualId);

}
