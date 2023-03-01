package com.keeper.homepage.domain.library.entity;

import com.keeper.homepage.domain.library.converter.BookBorrowStatusTypeConverter;
import com.keeper.homepage.global.error.BusinessException;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

import static com.keeper.homepage.global.error.ErrorCode.BOOK_TYPE_NOT_FOUND;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
@Table(name = "book_borrow_status")
public class BookBorrowStatus {

    private static final int MAX_TYPE_LENGTH = 20;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Convert(converter = BookBorrowStatusTypeConverter.class)
    @Column(name = "status", nullable = false, length = MAX_TYPE_LENGTH)
    private BookBorrowStatusType type;

    public static BookBorrowStatus getBookBorrowStatusBy(BookBorrowStatus.BookBorrowStatusType type) {
        return BookBorrowStatus.builder()
                .id(type.id)
                .type(type)
                .build();
    }

    @Builder
    private BookBorrowStatus(Long id, BookBorrowStatus.BookBorrowStatusType type) {
        this.id = id;
        this.type = type;
    }

    @Getter
    @RequiredArgsConstructor
    public enum BookBorrowStatusType {
        대출대기중(1, "대출 대기 중"),
        대출거부(2, "대출 거부"),
        대출승인(3, "대출 승인"),
        반납대기중(4, "반납 대기 중");

        private final long id;
        private final String status;

        public static BookBorrowStatus.BookBorrowStatusType fromCode(String type) {
            return Arrays.stream(BookBorrowStatus.BookBorrowStatusType.values())
                    .filter(bookBorrowStatusType -> bookBorrowStatusType.getStatus().equals(type))
                    .findAny()
                    .orElseThrow(() -> new BusinessException(type, "type", BOOK_TYPE_NOT_FOUND));
        }
    }
}
