package com.keeper.homepage.domain.ctf.dao;

import com.keeper.homepage.domain.ctf.entity.CtfContest;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CtfContestRepository extends JpaRepository<CtfContest, Long> {

  Optional<CtfContest> findByIdAndIdNotAndIsJoinableTrue(long contestId, long virtualId);

  Optional<CtfContest> findByIdAndIdNot(long contestId, long virtualId);
}
