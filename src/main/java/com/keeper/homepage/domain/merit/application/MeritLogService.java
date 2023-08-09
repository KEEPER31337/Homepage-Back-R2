package com.keeper.homepage.domain.merit.application;


import com.keeper.homepage.domain.member.dao.MemberRepository;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.merit.dao.MeritLogRepository;
import com.keeper.homepage.domain.merit.dao.MeritTypeRepository;
import com.keeper.homepage.domain.merit.entity.MeritLog;
import com.keeper.homepage.domain.merit.entity.MeritType;
import com.keeper.homepage.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.keeper.homepage.global.error.ErrorCode.MERIT_TYPE_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MeritLogService {

  private final MeritLogRepository meritLogRepository;
  private final MeritTypeRepository meritTypeRepository;
  private final MemberRepository memberRepository;

  @Transactional
  public Long recordMerit(long awarderId, long giverId, String reason) {
    Member awarder = memberRepository.findById(awarderId).orElseThrow();
    Member giver = memberRepository.findById(giverId).orElseThrow();
    MeritType meritType = meritTypeRepository.findByDetail(reason)
        .orElseThrow(() -> new BusinessException(reason, "meritType", MERIT_TYPE_NOT_FOUND));

    return meritLogRepository.save(MeritLog.builder()
            .awarder(awarder)
            .giver(giver)
            .meritType(meritType)
            .build())
        .getId();
  }

  public Page<MeritLog> findAll(Pageable pageable) {
    return meritLogRepository.findAll(pageable);
  }

  public Page<MeritLog> findByGiver_Id(Pageable pageable, Long memberId) {
    return meritLogRepository.findByGiver_Id(pageable, memberId);
  }

}
