package com.keeper.homepage.domain.election.application;


import com.keeper.homepage.domain.election.application.convenience.ElectionDeleteService;
import com.keeper.homepage.domain.election.application.convenience.ValidCandidateService;
import com.keeper.homepage.domain.election.application.convenience.ValidElectionFindService;
import com.keeper.homepage.domain.election.application.convenience.ValidVoterService;
import com.keeper.homepage.domain.election.dao.ElectionCandidateRepository;
import com.keeper.homepage.domain.election.dao.ElectionRepository;
import com.keeper.homepage.domain.election.dao.ElectionVoterRepository;
import com.keeper.homepage.domain.election.dto.response.ElectionResponse;
import com.keeper.homepage.domain.election.entity.Election;
import com.keeper.homepage.domain.election.entity.ElectionCandidate;
import com.keeper.homepage.domain.election.entity.ElectionVoter;
import com.keeper.homepage.domain.member.application.convenience.MemberFindService;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.member.entity.job.MemberJob;
import com.keeper.homepage.global.error.BusinessException;
import com.keeper.homepage.global.error.ErrorCode;
import java.util.List;
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
  private final ElectionCandidateRepository electionCandidateRepository;
  private final ElectionVoterRepository electionVoterRepository;

  private final ValidElectionFindService validElectionFindService;
  private final ValidCandidateService validCandidateService;
  private final ValidVoterService validVoterService;
  private final ElectionDeleteService electionDeleteService;
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
  public void registerCandidate(String description, Long memberJobId, long electionId, long candidateId) {
    Election election = validElectionFindService.findById(electionId);
    Member member = memberFindService.findById(candidateId);
    MemberJob memberJob = validCandidateService.findJobById(memberJobId);
    ElectionCandidate electionCandidate = ElectionCandidate.builder()
        .election(election)
        .member(member)
        .memberJob(memberJob)
        .description(description)
        .build();

    electionCandidateRepository.save(electionCandidate);
  }

  @Transactional
  public void registerCandidates(List<Long> candidateIds, String description, Long memberJobId, long electionId) {
    for (Long candidateId : candidateIds) {
      registerCandidate(description, memberJobId, electionId, candidateId);
    }
  }

  @Transactional
  public void deleteCandidate(long electionId, long candidateId) {
    ElectionCandidate electionCandidate = validCandidateService.findById(candidateId);
    Election election = validElectionFindService.findById(electionId);
    if (election.isAvailable()) {
      throw new BusinessException(electionId, "electionId", ErrorCode.ELECTION_CANDIDATE_CANNOT_DELETE);
    }

    electionCandidateRepository.delete(electionCandidate);
  }

  @Transactional
  public void registerVoters(List<Long> voterIds, long electionId) {
    Election election = validElectionFindService.findById(electionId);
    for (Long votersId : voterIds) {
      Member member = memberFindService.findById(votersId);
      ElectionVoter electionVoter = ElectionVoter.builder()
          .member(member)
          .election(election)
          .isVoted(false)
          .build();

      electionVoterRepository.save(electionVoter);
    }
  }

  @Transactional
  public void deleteVoters(List<Long> voterIds, long electionId) {
    Election election = validElectionFindService.findById(electionId);
    if (election.isAvailable()) {
      throw new BusinessException(electionId, "electionId", ErrorCode.ELECTION_VOTER_CANNOT_DELETE);
    }
    for (Long votersId : voterIds) {
      ElectionVoter electionVoter = validVoterService.findById(votersId);

      electionVoterRepository.delete(electionVoter);
    }
  }

  @Transactional
  public void openElection(long electionId) {
    Election election = validElectionFindService.findById(electionId);
    election.open();
  }

  @Transactional
  public void closeElection(long electionId) {
    Election election = validElectionFindService.findById(electionId);
    election.close();
  }

}
