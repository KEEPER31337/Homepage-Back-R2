package com.keeper.homepage.domain.merit.dto;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@Getter
@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class GiveMeritPointRequest {

    @NotNull
    private Long awarderId;

    @NotNull
    private Long giverId;

    @NotNull
    private String reason;

}
