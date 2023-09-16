package com.keeper.homepage.domain.merit.application;


import static com.keeper.homepage.global.error.ErrorCode.MERIT_TYPE_NOT_FOUND;

import com.keeper.homepage.domain.member.application.convenience.MemberFindService;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.merit.dao.MeritLogRepository;
import com.keeper.homepage.domain.merit.dao.MeritTypeRepository;
import com.keeper.homepage.domain.merit.entity.MeritLog;
import com.keeper.homepage.domain.merit.entity.MeritType;
import com.keeper.homepage.global.error.BusinessException;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

  private static final long SEMINAR_ABSENCE_MERIT_TYPE_ID = 2;
  private static final long SEMINAR_DUAL_LATENESS_MERIT_TYPE_ID = 3;

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

  @Transactional
  public void giveSeminarAbsenceDemerit(Member member) {
    MeritType meritType = meritTypeRepository.findById(SEMINAR_ABSENCE_MERIT_TYPE_ID).orElseThrow();

    meritLogRepository.save(MeritLog.builder()
        .memberId(member.getId())
        .memberRealName(member.getRealName())
        .memberGeneration(member.getGeneration())
        .meritType(meritType)
        .build());
  }

  @Transactional
  public void giveDualSeminarLatenessDemerit(Member member) {
    MeritType meritType = meritTypeRepository.findById(SEMINAR_DUAL_LATENESS_MERIT_TYPE_ID).orElseThrow();

    meritLogRepository.save(MeritLog.builder()
        .memberId(member.getId())
        .memberRealName(member.getRealName())
        .memberGeneration(member.getGeneration())
        .meritType(meritType)
        .build());
  }

  public Page<MeritLog> findAllByMeritType(Pageable pageable, String meritType) {
    return switch (meritType) {
      case "MERIT" -> meritLogRepository.findMeritLogs(pageable);
      case "DEMERIT" -> meritLogRepository.findDeMeritLogs(pageable);
      default -> meritLogRepository.findAll(pageable);
    };
  }

  public Page<MeritLog> findAllByMemberId(Pageable pageable, long memberId) {
    long findMemberId = memberFindService.findById(memberId).getId();
    return meritLogRepository.findAllByMemberId(pageable, findMemberId);
  }

}
