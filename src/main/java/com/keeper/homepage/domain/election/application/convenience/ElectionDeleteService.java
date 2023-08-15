package com.keeper.homepage.domain.election.application.convenience;

import com.keeper.homepage.domain.election.dao.ElectionRepository;
import com.keeper.homepage.domain.election.dao.ElectionVoterRepository;
import com.keeper.homepage.domain.election.entity.Election;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ElectionDeleteService {

  private final ElectionRepository electionRepository;
  private final ElectionVoterRepository electionVoterRepository;

  public void delete(Election election) {
    electionRepository.delete(election);
    electionVoterRepository.deleteAllByElection(election);
  }

}
