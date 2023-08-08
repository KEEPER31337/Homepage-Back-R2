package com.keeper.homepage.domain.merit.application;

import com.keeper.homepage.domain.merit.dao.MeritTypeRepository;
import com.keeper.homepage.domain.merit.dto.AddMeritTypeRequest;
import com.keeper.homepage.domain.merit.entity.MeritType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MeritTypeService {

  private MeritTypeRepository meritTypeRepository;

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


}
