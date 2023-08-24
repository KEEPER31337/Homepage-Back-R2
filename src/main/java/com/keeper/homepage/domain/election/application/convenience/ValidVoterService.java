package com.keeper.homepage.domain.election.application.convenience;

import static com.keeper.homepage.global.error.ErrorCode.ELECTION_VOTER_NOT_FOUND;

import com.keeper.homepage.domain.election.dao.ElectionVoterRepository;
import com.keeper.homepage.domain.election.entity.ElectionVoter;
import com.keeper.homepage.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ValidVoterService {

  private static final long VIRTUAL_ELECTION_VOTER_ID = 1;

  private final ElectionVoterRepository electionVoterRepository;

  public ElectionVoter findById(long voterId) {
    return electionVoterRepository.findByMemberIdAndMemberIdNot(voterId, VIRTUAL_ELECTION_VOTER_ID)
        .orElseThrow(() -> new BusinessException(voterId, "voterId", ELECTION_VOTER_NOT_FOUND));
  }

}
