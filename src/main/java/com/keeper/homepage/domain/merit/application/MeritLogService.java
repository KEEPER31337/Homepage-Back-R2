package com.keeper.homepage.domain.merit.application;


import com.keeper.homepage.domain.member.dao.MemberRepository;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.merit.dao.MeritLogRepository;
import com.keeper.homepage.domain.merit.dao.MeritTypeRepository;
import com.keeper.homepage.domain.merit.dto.GiveMeritPointRequest;
import com.keeper.homepage.domain.merit.entity.MeritLog;
import com.keeper.homepage.domain.merit.entity.MeritType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MeritLogService {

    private final MeritLogRepository meritLogRepository;
    private final MeritTypeRepository meritTypeRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void recordMerit(GiveMeritPointRequest request) {
        Member awarder = memberRepository.findById(request.getAwarderId()).orElseThrow();
        Member giver = memberRepository.findById(request.getGiverId()).orElseThrow();
        MeritType meritType = meritTypeRepository.findByDetail(request.getReason()).orElseThrow();

        meritLogRepository.save(MeritLog.builder()
                .awarder(awarder)
                .giver(giver)
                .meritType(meritType)
                .build());
    }
}
