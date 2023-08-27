package com.keeper.homepage.domain.election.dao;

import com.keeper.homepage.domain.election.entity.ElectionCandidate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ElectionCandidateRepository extends JpaRepository<ElectionCandidate, Long> {

  Optional<ElectionCandidate> findByIdAndIdNot(Long candidateId, Long virtualId);

}
