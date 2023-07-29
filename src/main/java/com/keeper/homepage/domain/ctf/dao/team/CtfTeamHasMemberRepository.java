package com.keeper.homepage.domain.ctf.dao.team;

import com.keeper.homepage.domain.ctf.entity.team.CtfTeamHasMember;
import com.keeper.homepage.domain.ctf.entity.team.CtfTeamHasMemberPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CtfTeamHasMemberRepository extends JpaRepository<CtfTeamHasMember, CtfTeamHasMemberPK> {

}
