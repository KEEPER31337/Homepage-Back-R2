package com.keeper.homepage.global.util.response;

import java.util.List;

public record ListResponse<T>(
    List<T> list
) {

}
