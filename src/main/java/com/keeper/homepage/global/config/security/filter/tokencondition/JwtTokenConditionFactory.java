package com.keeper.homepage.global.config.security.filter.tokencondition;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenConditionFactory {

    private final JwtTokenValidCondition jwtTokenValidCondition;
    private final AccessTokenReissueCondition accessTokenReissueCondition;
    private final JwtTokenResetCondition jwtTokenResetCondition;

    public List<JwtTokenCondition> createJwtTokenConditions() {
        return List.of(jwtTokenValidCondition, accessTokenReissueCondition, jwtTokenResetCondition);
    }
}
