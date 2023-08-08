package com.keeper.homepage.domain.ctf.application.convenience;

import static com.keeper.homepage.global.error.ErrorCode.CTF_CONTEST_NOT_FOUND;

import com.keeper.homepage.domain.ctf.dao.CtfContestRepository;
import com.keeper.homepage.domain.ctf.entity.CtfContest;
import com.keeper.homepage.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CtfContestFindService {

  private static final long VIRTUAL_CONTEST_ID = 1;

  private final CtfContestRepository ctfContestRepository;

  public CtfContest findJoinableById(long contestId) {
    return ctfContestRepository.findByIdAndIdNotAndIsJoinableTrue(contestId, VIRTUAL_CONTEST_ID)
        .orElseThrow(() -> new BusinessException(contestId, "contestId", CTF_CONTEST_NOT_FOUND));
  }

  public CtfContest findById(long contestId) {
    return ctfContestRepository.findByIdAndIdNot(contestId, VIRTUAL_CONTEST_ID)
        .orElseThrow(() -> new BusinessException(contestId, "contestId", CTF_CONTEST_NOT_FOUND));
  }
}
