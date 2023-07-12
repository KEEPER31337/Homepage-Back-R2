package com.keeper.homepage.domain.member.dto.response;

import static lombok.AccessLevel.PRIVATE;

import com.keeper.homepage.domain.member.entity.job.MemberJob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = PRIVATE)
public class JobResponse {

  private Long jobId;
  private String jobName;
  private String jobThumbnailPath;

  public static JobResponse from(MemberJob memberJob) {
    return JobResponse.builder()
        .jobId(memberJob.getId())
        .jobName(memberJob.getType().toString())
        .jobThumbnailPath(memberJob.getBadge().getPath())
        .build();
  }
}
