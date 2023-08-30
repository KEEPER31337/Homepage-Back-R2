package com.keeper.homepage.domain.library.api

import com.keeper.homepage.domain.library.application.BookManageService
import com.keeper.homepage.domain.library.dto.req.BookRequest
import com.keeper.homepage.domain.library.dto.req.BookSearchType
import com.keeper.homepage.domain.library.dto.req.ModifyBookRequest
import com.keeper.homepage.domain.library.dto.resp.BookDetailResponse
import com.keeper.homepage.domain.library.dto.resp.BookResponse
import com.keeper.homepage.domain.library.entity.Book
import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PositiveOrZero
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.net.URI

const val GET_BOOKS_MIN_SIZE = 3L
const val GET_BOOKS_MAX_SIZE = 100L
const val GET_BOOKS_DEFAULT_SIZE = 10

@Validated
@RequestMapping("/manage/books")
@RestController
@Secured("ROLE_회장", "ROLE_부회장", "ROLE_사서")
class BookManageController(
    private val bookManageService: BookManageService
) {
    @GetMapping
    fun getBooks(
        @RequestParam(required = false, defaultValue = "") search: String,
        @RequestParam(required = false, defaultValue = "ALL") searchType: BookSearchType,
        @RequestParam(defaultValue = "0") @PositiveOrZero @NotNull page: Int,
        @RequestParam(defaultValue = GET_BOOKS_DEFAULT_SIZE.toString()) @Min(GET_BOOKS_MIN_SIZE) @Max(GET_BOOKS_MAX_SIZE) @NotNull size: Int,
    ): ResponseEntity<Page<BookDetailResponse>> {
        return ResponseEntity.ok(
            bookManageService.getBooks(search, searchType, PageRequest.of(page, size))
                .map(::BookDetailResponse)
        )
    }

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE])
    fun addBook(
        @RequestPart @Valid bookMetaData: BookRequest,
        @RequestPart thumbnail: MultipartFile?,
    ): ResponseEntity<Void> {
        val addedBookId = bookManageService.addBook(
            bookMetaData.title!!,
            bookMetaData.author!!,
            bookMetaData.totalQuantity!!,
            bookMetaData.bookDepartment!!,
            thumbnail,
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
