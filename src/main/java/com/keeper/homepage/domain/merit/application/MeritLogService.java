package com.keeper.homepage.domain.merit.application;


import static com.keeper.homepage.global.error.ErrorCode.MERIT_TYPE_NOT_FOUND;

import com.keeper.homepage.domain.member.application.convenience.MemberFindService;
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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MeritLogService {

  private final MeritLogRepository meritLogRepository;
  private final MeritTypeRepository meritTypeRepository;
  private final MemberFindService memberFindService;

  @Transactional
  public Long recordMerit(long memberId, long meritTypeId) {
    Member member = memberFindService.findById(memberId);
    MeritType meritType = meritTypeRepository.findById(meritTypeId)
        .orElseThrow(() -> new BusinessException(meritTypeId, "meritType", MERIT_TYPE_NOT_FOUND));

    return meritLogRepository.save(MeritLog.builder()
            .memberId(member.getId())
            .memberRealName(member.getRealName())
            .memberGeneration(member.getGeneration())
            .meritType(meritType)
            .build())
        .getId();
  }

  public Page<MeritLog> findAll(Pageable pageable) {
    return meritLogRepository.findAll(pageable);
  }

  public Page<MeritLog> findAllByMemberId(Pageable pageable, long memberId) {
    long findMemberId = memberFindService.findById(memberId).getId();
    return meritLogRepository.findAllByMemberId(pageable, findMemberId);
  }
}
