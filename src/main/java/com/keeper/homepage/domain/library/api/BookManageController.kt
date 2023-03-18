package com.keeper.homepage.domain.library.api

import com.keeper.homepage.domain.library.application.BookManageService
import com.keeper.homepage.domain.library.dto.req.BookRequest
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*
import java.net.URI

@RequestMapping("/manage/books")
@RestController
@Secured("ROLE_회장", "ROLE_부회장", "ROLE_사서")
class BookManageController(
    private val bookManageService: BookManageService
) {
    @PostMapping
    fun addBook(
        @ModelAttribute @Valid request: BookRequest
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

    @DeleteMapping("/{bookId}")
    fun deleteBook(@PathVariable bookId: Long): ResponseEntity<Void> {
        bookManageService.deleteBook(bookId)
        return ResponseEntity.noContent().build()
    }

    @PutMapping("/{bookId}")
    fun modifyBook(
        @PathVariable bookId: Long,
        @ModelAttribute @Valid request: BookRequest
    ): ResponseEntity<Void> {
        bookManageService.modifyBook(
            bookId,
            request.title!!,
            request.author!!,
            request.totalQuantity!!,
            request.bookDepartment!!,
            request.thumbnail
        )
        return ResponseEntity.noContent().build()
    }
}
