package com.keeper.homepage.domain.member.application;

import static com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_출제자;
import static com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_회원;
import static com.keeper.homepage.global.error.ErrorCode.MEMBER_JOB_IS_NOT_EXECUTIVE;
import static com.keeper.homepage.global.error.ErrorCode.MEMBER_JOB_NOT_FOUND;

import com.keeper.homepage.domain.member.application.convenience.MemberFindService;
import com.keeper.homepage.domain.member.dao.role.MemberHasMemberJobRepository;
import com.keeper.homepage.domain.member.dao.role.MemberJobRepository;
import com.keeper.homepage.domain.member.dto.response.MemberJobResponse;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.member.entity.job.MemberJob;
import com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType;
import com.keeper.homepage.global.error.BusinessException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberJobService {

  private final MemberFindService memberFindService;
  private final MemberHasMemberJobRepository memberHasMemberJobRepository;
  private final MemberJobRepository memberJobRepository;

  public List<MemberJobResponse> getExecutives() {
    return memberHasMemberJobRepository.findAll()
        .stream()
        .filter(memberHasMemberJob -> memberHasMemberJob.getMemberJob().isExecutive())
        .map(MemberJobResponse::from)
        .toList();
  }

  public List<JobResponse> getExecutiveJobs() {
    return memberJobRepository.findAll()
        .stream()
        .filter(MemberJob::isExecutive)
        .map(JobResponse::from)
        .toList();
  }

  @Transactional
  public void addMemberExecutiveJob(Long memberId, Long jobId) {
    Member member = memberFindService.findById(memberId);
    MemberJobType memberJobType = memberJobRepository.findById(jobId)
        .orElseThrow(() -> new BusinessException(jobId, "jobId", MEMBER_JOB_NOT_FOUND))
        .getType();

    checkExecutiveJob(memberJobType);
    member.assignJob(memberJobType);
  }

  private void checkExecutiveJob(MemberJobType memberJobType) {
    if (memberJobType.equals(ROLE_회원) || memberJobType.equals(ROLE_출제자)) {
      throw new BusinessException(memberJobType.toString(), "memberJobType", MEMBER_JOB_IS_NOT_EXECUTIVE);
    }
  }

  @Transactional
  public void deleteMemberExecutiveJob(Long memberId, Long jobId) {
    Member member = memberFindService.findById(memberId);
    MemberJobType memberJobType = memberJobRepository.findById(jobId)
        .orElseThrow(() -> new BusinessException(jobId, "jobId", MEMBER_JOB_NOT_FOUND))
        .getType();

    checkExecutiveJob(memberJobType);
    member.deleteJob(memberJobType);
  }
}
