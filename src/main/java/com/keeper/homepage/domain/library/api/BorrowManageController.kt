package com.keeper.homepage.domain.library.api

import com.keeper.homepage.domain.library.application.BorrowManageService
import com.keeper.homepage.domain.library.dto.resp.BorrowResponse
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PositiveOrZero
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

const val MIN_SIZE = 3L
const val MAX_SIZE = 100L
const val DEFAULT_SIZE = 10

@Validated
@RequestMapping("/manage/borrow-infos")
@RestController
@Secured("ROLE_회장", "ROLE_부회장", "ROLE_사서")
class BorrowManageController(
    private val borrowManageService: BorrowManageService
) {
    @GetMapping("/requests")
    fun getBorrowRequests(
        @RequestParam(defaultValue = "0") @PositiveOrZero @NotNull page: Int,
        @RequestParam(defaultValue = DEFAULT_SIZE.toString()) @Min(MIN_SIZE) @Max(MAX_SIZE) @NotNull size: Int,
    ): ResponseEntity<Page<BorrowResponse>> {
        val borrowRequests = borrowManageService.getBorrowRequests(PageRequest.of(page, size))
        return ResponseEntity.ok(borrowRequests)
    }

    @PostMapping("/{borrowId}/requests-approve")
    fun approveBorrow(@PathVariable borrowId: Long): ResponseEntity<Void> {
        borrowManageService.approveBorrow(borrowId)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{borrowId}/requests-deny")
    fun denyBorrow(@PathVariable borrowId: Long): ResponseEntity<Void> {
        borrowManageService.denyBorrow(borrowId)
        return ResponseEntity.noContent().build()
    }
}
