package com.keeper.homepage.domain.library.api

import com.keeper.homepage.domain.library.application.BookManageService
import com.keeper.homepage.domain.library.dto.req.AddBookRequest
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RequestMapping("/manage/books")
@RestController
@Secured("ROLE_회장", "ROLE_부회장", "ROLE_사서")
class BookManageController(
    private val bookManageService: BookManageService
) {
    @PostMapping
    fun addBook(
        @ModelAttribute @Valid request: AddBookRequest
    ): ResponseEntity<Void> {
        val addedBookId = bookManageService.addBook(
            request.title!!,
            request.author!!,
            request.totalQuantity!!,
            request.bookDepartment!!,
            request.thumbnail,
        )
        return ResponseEntity.created(URI.create("/books/${addedBookId}"))
            .build()
    }
}
