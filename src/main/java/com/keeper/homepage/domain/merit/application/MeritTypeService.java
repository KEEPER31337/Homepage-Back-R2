package com.keeper.homepage.domain.merit.application;

import static com.keeper.homepage.global.error.ErrorCode.MERIT_TYPE_DETAIL_DUPLICATE;
import static com.keeper.homepage.global.error.ErrorCode.MERIT_TYPE_NOT_FOUND;

import com.keeper.homepage.domain.merit.dao.MeritTypeRepository;
import com.keeper.homepage.domain.merit.entity.MeritType;
import com.keeper.homepage.global.error.BusinessException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MeritTypeService {

  private final MeritTypeRepository meritTypeRepository;

  @Transactional
  public Long addMeritType(int score, String reason) {
    checkDuplicateMeritTypeDetail(reason);
    return meritTypeRepository.save(MeritType.builder()
        .merit(score)
        .detail(reason)
        .build()).getId();
  }

  private void checkDuplicateMeritTypeDetail(String reason) {
    meritTypeRepository.findByDetail(reason)
        .ifPresent((meritTypeDetail) -> {
          throw new BusinessException(reason, "Detail", MERIT_TYPE_DETAIL_DUPLICATE);
        });
  }

  public Page<MeritType> findAll(Pageable pageable) {
    return meritTypeRepository.findAll(pageable);
  }

  @Transactional
  public void updateMeritType(long meritTypeId, int score, String reason) {
    MeritType meritType = meritTypeRepository.findById(meritTypeId)
        .orElseThrow(() -> new BusinessException(meritTypeId, "meritType", MERIT_TYPE_NOT_FOUND));
    checkDuplicateMeritTypeDetail(reason, meritType.getId());
    meritType.update(score, reason);
  }

  private void checkDuplicateMeritTypeDetail(String reason, long meritTypeId) {
    meritTypeRepository.findByDetail(reason)
        .ifPresent(meritType -> {
          if (meritType.getId() != meritTypeId) {
            throw new BusinessException(reason, "Detail", MERIT_TYPE_DETAIL_DUPLICATE);
          }
        });
  }
}

