package com.keeper.homepage.global.config.security.filter.token_condition;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtTokenConditionFactory {

    private final JwtTokenValidCondition jwtTokenValidCondition;
    private final AccessTokenReissueCondition accessTokenReissueCondition;

    public List<JwtTokenCondition> createJwtTokenConditions() {
        return List.of(jwtTokenValidCondition, accessTokenReissueCondition);
    }
}
