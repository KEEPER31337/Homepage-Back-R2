package com.keeper.homepage.domain.election.application;


import com.keeper.homepage.domain.election.application.convenience.ElectionDeleteService;
import com.keeper.homepage.domain.election.application.convenience.ValidElectionFindService;
import com.keeper.homepage.domain.election.dao.ElectionRepository;
import com.keeper.homepage.domain.election.entity.Election;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.global.error.BusinessException;
import com.keeper.homepage.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminElectionService {

  private final ElectionRepository electionRepository;

  private final ValidElectionFindService validElectionFindService;

  private final ElectionDeleteService electionDeleteService;

  @Transactional
  public void createElection(Member member, String name, String description, Boolean isAvailable) {
    Election election = Election.builder()
        .member(member)
        .name(name)
        .description(description)
        .isAvailable(isAvailable)
        .build();

    electionRepository.save(election);
  }

  @Transactional
  public void deleteElection(long electionId) {
    Election election = validElectionFindService.findById(electionId);

    if (election.isAvailable()) {
      throw new BusinessException(electionId, "electionId", ErrorCode.ELECTION_CANNOT_DELETE);
    }
    electionDeleteService.delete(election);
  }

}
