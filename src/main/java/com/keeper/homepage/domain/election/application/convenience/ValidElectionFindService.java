package com.keeper.homepage.domain.election.application.convenience;

import static com.keeper.homepage.global.error.ErrorCode.ELECTION_NOT_FOUND;

import com.keeper.homepage.domain.election.dao.ElectionRepository;
import com.keeper.homepage.domain.election.dto.response.ElectionResponse;
import com.keeper.homepage.domain.election.entity.Election;
import com.keeper.homepage.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ValidElectionFindService {

  private static final long VIRTUAL_ELECTION_ID = 1;

  private final ElectionRepository electionRepository;

  public Election findById(long electionId) {
    return electionRepository.findByIdAndIdNot(electionId, VIRTUAL_ELECTION_ID)
        .orElseThrow(() -> new BusinessException(electionId, "electionId", ELECTION_NOT_FOUND));
  }

  public Page<Election> findAll(Pageable pageable) {
    return electionRepository.findAllByIdNot(VIRTUAL_ELECTION_ID, pageable);
  }

}
