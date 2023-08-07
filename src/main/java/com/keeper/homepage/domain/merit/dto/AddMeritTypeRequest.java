package com.keeper.homepage.domain.merit.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@Getter
@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class AddMeritTypeRequest {

    @NotNull
    private Integer reward;

    @NotNull
    private Integer penalty;

    @NotNull
    private String detail;


}
