package com.keeper.homepage.domain.member.dto.response;

import static lombok.AccessLevel.PRIVATE;

import com.keeper.homepage.domain.member.entity.job.MemberHasMemberJob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = PRIVATE)
public class MemberJobResponse {

  private Long jobId;
  private String jobName;
  private String jobThumbnailPath;
  private Long memberId;
  private Float generation;
  private String realName;

  public static MemberJobResponse from(MemberHasMemberJob memberHasMemberJob) {
    return MemberJobResponse.builder()
        .jobId(memberHasMemberJob.getMemberJob().getId())
        .jobName(memberHasMemberJob.getMemberJob().getType().toString())
        .jobThumbnailPath(memberHasMemberJob.getMemberJob().getBadge().getPath())
        .memberId(memberHasMemberJob.getMember().getId())
        .generation(memberHasMemberJob.getMember().getGeneration())
        .realName(memberHasMemberJob.getMember().getRealName())
        .build();
  }
}
