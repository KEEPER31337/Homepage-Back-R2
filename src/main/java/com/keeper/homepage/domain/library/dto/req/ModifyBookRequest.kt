package com.keeper.homepage.domain.library.dto.req

import com.keeper.homepage.domain.library.entity.BookDepartment
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import org.hibernate.validator.constraints.Length

data class ModifyBookRequest(
        @field:NotNull
        @field:Length(min = 1, max = MAX_TITLE_LENGTH)
        val title: String?,

        @field:NotNull
        @field:Length(min = 1, max = MAX_AUTHOR_LENGTH)
        val author: String?,

        @field:NotNull
        @field:Max(MAX_TOTAL_QUANTITY_LENGTH) @field:Min(1)
        val totalQuantity: Long?,

        @field:NotNull
        val bookDepartment: BookDepartment.BookDepartmentType?,
)
