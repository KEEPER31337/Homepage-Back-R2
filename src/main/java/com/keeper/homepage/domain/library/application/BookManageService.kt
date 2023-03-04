package com.keeper.homepage.domain.library.application

import com.keeper.homepage.domain.library.entity.BookDepartment
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
@Transactional(readOnly = true)
class BookManageService() {
    @Transactional
    fun addBook(
        title: String,
        author: String,
        totalQuantity: Long,
        bookDepartment: BookDepartment.BookDepartmentType,
        thumbnail: MultipartFile?
    ): Long {
        return 1
    }
}
