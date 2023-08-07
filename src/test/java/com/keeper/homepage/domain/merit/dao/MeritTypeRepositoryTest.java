package com.keeper.homepage.domain.merit.dao;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.merit.entity.MeritType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

public class MeritTypeRepositoryTest extends IntegrationTest {

    @Nested
    @DisplayName("상벌점 타입 생성 테스트")
    @Component
    class MeritTypeTest {

        @Autowired
        MeritTypeRepository meritTypeRepository;

        @Test
        @DisplayName("DB에 상벌점 타입 등록을 성공해야 한다.")
        void should_success_when_registerMeritType() {
            MeritType meritType = meritTypeRepository.save(MeritType.builder()
                    .merit(1)
                    .isMerit(false)
                    .detail("지각")
                    .build());

            MeritType findMeritType = meritTypeRepository.findByDetail("지각").orElseThrow();

            assertThat(meritType.getMerit()).isEqualTo(findMeritType.getMerit());
            assertThat(meritType.getIsMerit()).isEqualTo(findMeritType.getIsMerit());
            assertThat(meritType.getDetail()).isEqualTo(findMeritType.getDetail());
        }
    }
}