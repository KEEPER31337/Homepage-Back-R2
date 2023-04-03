package com.keeper.homepage.domain.library.api

import com.keeper.homepage.domain.library.application.BookManageService
import com.keeper.homepage.domain.library.dto.req.BookRequest
import com.keeper.homepage.domain.library.dto.req.ModifyBookRequest
import com.keeper.homepage.domain.library.dto.resp.BookDetailResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
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
        @RequestBody @Valid request: ModifyBookRequest
    ): ResponseEntity<Void> {
        bookManageService.modifyBook(
            bookId,
            request.title!!,
            request.author!!,
            request.totalQuantity!!,
            request.bookDepartment!!,
        )
        return ResponseEntity.noContent().build()
    }

    @PatchMapping("/{bookId}/thumbnail")
    fun modifyBookThumbnail(
        @PathVariable bookId: Long,
        @ModelAttribute newThumbnail: MultipartFile?
    ): ResponseEntity<Void> {
        bookManageService.modifyBookThumbnail(
            bookId,
            newThumbnail
        )
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/{bookId}")
    fun getBookDetail(@PathVariable bookId: Long): ResponseEntity<BookDetailResponse> {
        val bookDetailResponse = bookManageService.getBookDetail(bookId)
        return ResponseEntity.ok(bookDetailResponse)
    }
}
