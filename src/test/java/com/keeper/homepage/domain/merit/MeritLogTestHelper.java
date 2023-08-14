package com.keeper.homepage.domain.merit;

import com.keeper.homepage.domain.member.MemberTestHelper;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.merit.dao.MeritLogRepository;
import com.keeper.homepage.domain.merit.entity.MeritLog;
import com.keeper.homepage.domain.merit.entity.MeritType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MeritLogTestHelper {

    @Autowired
    MeritLogRepository meritLogRepository;
    @Autowired
    MemberTestHelper memberTestHelper;
    @Autowired
    MeritTypeHelper meritTypeHelper;

    public MeritLog generate() {
        return this.builder().build();
    }

    public MeritLogBuilder builder() {
        return new MeritLogBuilder();
    }

    public final class MeritLogBuilder {
        private Member awarder;
        private Member giver;
        private LocalDateTime time;
        private MeritType meritType;

        private MeritLogBuilder() {
        }

        public MeritLogBuilder awarder(Member member) {
            this.awarder = member;
            return this;
        }

        public MeritLogBuilder giver(Member member) {
            this.giver = member;
            return this;
        }

        public MeritLogBuilder time(LocalDateTime time) {
            this.time = time;
            return this;
        }

        public MeritLogBuilder meritType(MeritType meritType) {
            this.meritType = meritType;
            return this;
        }

        public MeritLog build() {
            return meritLogRepository.save(MeritLog.builder()
                    .awarder(awarder != null ? awarder : memberTestHelper.generate())
                    .giver(giver != null ? giver : memberTestHelper.generate())
                    .meritType(meritType != null ? meritType : meritTypeHelper.generate())
                    .build());
        }
    }
}
