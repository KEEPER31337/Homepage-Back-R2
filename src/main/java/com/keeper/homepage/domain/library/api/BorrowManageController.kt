package com.keeper.homepage.domain.library.api

import com.keeper.homepage.domain.library.application.BorrowManageService
import com.keeper.homepage.domain.library.dto.req.BorrowStatusDto
import com.keeper.homepage.domain.library.dto.resp.BorrowDetailResponse
import com.keeper.homepage.domain.library.dto.resp.BorrowLogResponse
import com.keeper.homepage.domain.library.entity.BookBorrowLog
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
    @GetMapping
    fun getBorrowRequests(
        @RequestParam(defaultValue = "") search: String,
        @RequestParam(defaultValue = "0") @PositiveOrZero @NotNull page: Int,
        @RequestParam(defaultValue = DEFAULT_SIZE.toString()) @Min(MIN_SIZE) @Max(MAX_SIZE) @NotNull size: Int,
        @RequestParam status: BorrowStatusDto?
    ): ResponseEntity<Page<BorrowDetailResponse>> {
        val borrowRequests = borrowManageService.getBorrow(search, PageRequest.of(page, size), status)
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

    @PostMapping("/{borrowId}/return-approve")
    fun approveReturn(@PathVariable borrowId: Long): ResponseEntity<Void> {
        borrowManageService.approveReturn(borrowId)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{borrowId}/return-deny")
    fun denyReturn(@PathVariable borrowId: Long): ResponseEntity<Void> {
        borrowManageService.denyReturn(borrowId)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/logs")
    fun getBorrowLogs(
        @RequestParam(defaultValue = "") search: String,
        @RequestParam(defaultValue = "0") @PositiveOrZero @NotNull page: Int,
        @RequestParam(defaultValue = DEFAULT_SIZE.toString()) @Min(MIN_SIZE) @Max(MAX_SIZE) @NotNull size: Int,
        @RequestParam searchType: BookBorrowLog.LogType?,
    ): ResponseEntity<Page<BorrowLogResponse>> {
        val borrowLogs = borrowManageService.getBorrowLogs(search, PageRequest.of(page, size), searchType)
        return ResponseEntity.ok(borrowLogs)
    }
}
