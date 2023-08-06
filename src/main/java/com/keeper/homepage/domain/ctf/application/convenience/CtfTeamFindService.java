package com.keeper.homepage.domain.ctf.application.convenience;

import static com.keeper.homepage.global.error.ErrorCode.CTF_TEAM_NOT_FOUND;

import com.keeper.homepage.domain.ctf.dao.team.CtfTeamRepository;
import com.keeper.homepage.domain.ctf.entity.team.CtfTeam;
import com.keeper.homepage.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CtfTeamFindService {

  private static final long VIRTUAL_TEAM_ID = 1;
  private final CtfTeamRepository ctfTeamRepository;

  public CtfTeam findById(long teamId) {
    return ctfTeamRepository.findByIdAndIdNot(teamId, VIRTUAL_TEAM_ID)
        .orElseThrow(() -> new BusinessException(teamId, "teamId", CTF_TEAM_NOT_FOUND));
  }
}
