package com.keeper.homepage.domain.merit.application;

import com.keeper.homepage.domain.merit.dao.MeritTypeRepository;
import com.keeper.homepage.domain.merit.dto.request.AddMeritTypeRequest;
import com.keeper.homepage.domain.merit.entity.MeritType;
import java.util.List;
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
  public void addMeritType(AddMeritTypeRequest request) {
    boolean isMerit = request.getReward() > 0;
    int merit = isMerit ? request.getReward() : request.getPenalty();

    meritTypeRepository.save(MeritType.builder()
        .merit(merit)
        .isMerit(isMerit)
        .detail(request.getDetail())
        .build());

  }


  public Page<MeritType> findAll(Pageable pageable) {
    return meritTypeRepository.findAll(pageable);
  }

  @Transactional
  public void updateMeritType(Long meritTypeId, Integer score, String reason) {
    MeritType meritType = meritTypeRepository.findById(meritTypeId).orElseThrow();
    meritType.update(score, reason);
  }
}
