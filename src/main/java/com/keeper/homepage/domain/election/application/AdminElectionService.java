package com.keeper.homepage.domain.election.application;


import static com.keeper.homepage.global.error.ErrorCode.MEMBER_JOB_NOT_FOUND;

import com.keeper.homepage.domain.election.application.convenience.ElectionDeleteService;
import com.keeper.homepage.domain.election.application.convenience.ValidElectionFindService;
import com.keeper.homepage.domain.election.dao.ElectionCandidateRepository;
import com.keeper.homepage.domain.election.dao.ElectionRepository;
import com.keeper.homepage.domain.election.dto.response.ElectionResponse;
import com.keeper.homepage.domain.election.entity.Election;
import com.keeper.homepage.domain.election.entity.ElectionCandidate;
import com.keeper.homepage.domain.member.application.convenience.MemberFindService;
import com.keeper.homepage.domain.member.dao.role.MemberJobRepository;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.member.entity.job.MemberJob;
import com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType;
import com.keeper.homepage.global.error.BusinessException;
import com.keeper.homepage.global.error.ErrorCode;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminElectionService {

  private final ElectionRepository electionRepository;
  private final ValidElectionFindService validElectionFindService;
  private final ElectionDeleteService electionDeleteService;
  private final ElectionCandidateRepository electionCandidateRepository;
  private final MemberJobRepository memberJobRepository;
  private final MemberFindService memberFindService;


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

  @Transactional
  public void updateElection(long electionId, String name, String description, Boolean isAvailable) {
    Election election = validElectionFindService.findById(electionId);

    election.update(name, description, isAvailable);
  }

  @Transactional
  public Page<ElectionResponse> getElections(Pageable pageable) {
    return validElectionFindService.findAll(pageable)
        .map(ElectionResponse::from);
  }

  @Transactional
  public void registerCandidate(long electionId, long candidateId, String description, long memberJobId) {
    Election election = validElectionFindService.findById(electionId);
    Member candidate = memberFindService.findById(candidateId);
    MemberJob memberJob = memberJobRepository.findById(memberJobId)
        .orElseThrow(() -> new BusinessException(memberJobId, "memberJobId", MEMBER_JOB_NOT_FOUND));

    electionCandidateRepository.save(ElectionCandidate.builder()
        .election(election)
        .member(candidate)
        .memberJob(memberJob)
        .description(description)
        .voteCount(0L)
        .build());
  }

}
