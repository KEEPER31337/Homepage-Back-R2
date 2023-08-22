package com.keeper.homepage.domain.election.application.convenience;

import static com.keeper.homepage.global.error.ErrorCode.ELECTION_CANDIDATE_CANNOT_REGISTER;
import static com.keeper.homepage.global.error.ErrorCode.ELECTION_CANDIDATE_NOT_FOUND;
import static com.keeper.homepage.global.error.ErrorCode.MEMBER_JOB_NOT_FOUND;

import com.keeper.homepage.domain.election.dao.ElectionCandidateRepository;
import com.keeper.homepage.domain.election.entity.ElectionCandidate;
import com.keeper.homepage.domain.member.dao.role.MemberJobRepository;
import com.keeper.homepage.domain.member.entity.job.MemberJob;
import com.keeper.homepage.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ValidCandidateService {

  private static final long VIRTUAL_ELECTION_CANDIDATE_ID = 1;

  private final ElectionCandidateRepository electionCandidateRepository;

  private final MemberJobRepository memberJobRepository;

  public ElectionCandidate findById(long candidateId) {
    return electionCandidateRepository.findByIdAndIdNot(candidateId, VIRTUAL_ELECTION_CANDIDATE_ID)
        .orElseThrow(() -> new BusinessException(candidateId, "candidateId", ELECTION_CANDIDATE_NOT_FOUND));
  }

  public enum MemberJobId{
    ROLE_회장(1),
    ROLE_부회장(2),
    ROLE_총무(8);
    private final long id;

    MemberJobId(long id) {
      this.id = id;
    }

    public static boolean isValid(long id) {
      for (MemberJobId jobId : values()) {
        if (jobId.id == id) {
          return true;
        }
      }
      return false;
    }
  }

  public MemberJob findJobById(long memberJobId) {
    if (!MemberJobId.isValid(memberJobId)) {
      throw new BusinessException(memberJobId, "memJobId", ELECTION_CANDIDATE_CANNOT_REGISTER);
    }
    return memberJobRepository.findById(memberJobId)
        .orElseThrow(() -> new BusinessException(memberJobId, "memberJobId", MEMBER_JOB_NOT_FOUND));
  }

}
