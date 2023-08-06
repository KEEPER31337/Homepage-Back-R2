package com.keeper.homepage.domain.ctf.dao.team;

import com.keeper.homepage.domain.ctf.entity.CtfContest;
import com.keeper.homepage.domain.ctf.entity.team.CtfTeam;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CtfTeamRepository extends JpaRepository<CtfTeam, Long> {

  Optional<CtfTeam> findByIdAndIdNot(long teamId, long virtualId);

  Page<CtfTeam> findAllByCtfContestAndNameIgnoreCaseContaining(CtfContest ctfContest, String search, Pageable pageable);
}
