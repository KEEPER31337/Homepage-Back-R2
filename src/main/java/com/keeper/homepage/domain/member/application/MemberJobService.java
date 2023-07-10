package com.keeper.homepage.domain.member.application;

import static com.keeper.homepage.global.error.ErrorCode.MEMBER_JOB_NOT_FOUND;

import com.keeper.homepage.domain.member.application.convenience.MemberFindService;
import com.keeper.homepage.domain.member.dao.role.MemberHasMemberJobRepository;
import com.keeper.homepage.domain.member.dao.role.MemberJobRepository;
import com.keeper.homepage.domain.member.dto.response.MemberJobResponse;
import com.keeper.homepage.domain.member.entity.Member;
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

  @Transactional
  public void addMemberJob(Long memberId, Long jobId) {
    Member member = memberFindService.findById(memberId);
    MemberJobType memberJobType = memberJobRepository.findById(jobId)
        .orElseThrow(() -> new BusinessException(jobId, "jobId", MEMBER_JOB_NOT_FOUND))
        .getType();

    member.assignJob(memberJobType);
  }

  @Transactional
  public void deleteMemberJob(Long memberId, Long jobId) {
    Member member = memberFindService.findById(memberId);
    MemberJobType memberJobType = memberJobRepository.findById(jobId)
        .orElseThrow(() -> new BusinessException(jobId, "jobId", MEMBER_JOB_NOT_FOUND))
        .getType();

    member.deleteJob(memberJobType);
  }
}
