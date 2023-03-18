package com.keeper.homepage.domain.library.dto.req

enum class BorrowStatusDto(val status: String) {
    REQUESTS("requests"),
    WILL_RETURN("willreturn"),
    OVERDUE("overdue");

    fun isMatch(status: String) = this.status == status
}
