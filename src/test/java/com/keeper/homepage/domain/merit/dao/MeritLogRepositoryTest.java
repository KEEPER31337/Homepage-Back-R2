package com.keeper.homepage.domain.merit.dao;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.member.MemberTestHelper;
import com.keeper.homepage.domain.member.dao.MemberRepository;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.merit.entity.MeritLog;
import com.keeper.homepage.domain.merit.entity.MeritType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

public class MeritLogRepositoryTest extends IntegrationTest {

    @Nested
    @DisplayName("상벌점 기능 테스트")
    @Component
    class MeritTest {

        @Autowired
        MeritLogRepository meritLogRepository;

        @Autowired
        MeritTypeRepository meritTypeRepository;

        @Autowired
        MemberTestHelper memberTestHelper;

        @Test
        @DisplayName("DB에 상벌점 로그 등록을 성공해야 한다.")
        void should_success_when_registerMeritType() {
            Member member1 = memberTestHelper.builder().build();
            Member member2 = memberTestHelper.builder().build();

            MeritType meritType = meritTypeRepository.save(MeritType.builder()
                    .merit(3)
                    .isMerit(false)
                    .detail("결석")
                    .build());

            MeritLog meritLog = meritLogRepository.save(MeritLog.builder()
                    .awarderId(member1)
                    .giverId(member2)
                    .meritType(meritType)
                    .build());

            MeritLog findMeritLog = meritLogRepository.findById(meritLog.getId()).orElseThrow();

            assertThat(meritLog.getId()).isEqualTo(findMeritLog.getId());
            assertThat(meritLog.getGiverId()).isEqualTo(findMeritLog.getGiverId());
            assertThat(meritLog.getAwarderId()).isEqualTo(findMeritLog.getAwarderId());
            assertThat(meritLog.getTime()).isEqualTo(findMeritLog.getTime());
            assertThat(meritLog.getMeritType().getId()).isEqualTo(findMeritLog.getMeritType().getId());

        }

    }




}