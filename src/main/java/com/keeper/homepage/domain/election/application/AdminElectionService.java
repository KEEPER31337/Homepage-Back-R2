package com.keeper.homepage.domain.election.application;

import static com.keeper.homepage.global.error.ErrorCode.ELECTION_NOT_AVAILABLE;

import com.keeper.homepage.domain.election.dao.ElectionRepository;
import com.keeper.homepage.domain.election.entity.Election;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminElectionService {

  private final ElectionRepository electionRepository;

  @Transactional
  public void createElection(Member member, String name, String description, Boolean isAvailable) {
    Election election = Election.builder()
        .member(member)
        .name(name)
        .description(description)
        .isAvailable(isAvailable)
        .build();
    if (election.getIsAvailable() == false) {
      throw new BusinessException(election, "election", ELECTION_NOT_AVAILABLE);
    }
    electionRepository.save(election);
  }

}
