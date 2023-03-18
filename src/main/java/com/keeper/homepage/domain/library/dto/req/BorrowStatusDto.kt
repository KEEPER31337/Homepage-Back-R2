package com.keeper.homepage.domain.library.dto.req

enum class BorrowStatusDto(val status: String) {
    REQUESTS("requests");

    fun isMatch(status: String) = this.status == status
}
