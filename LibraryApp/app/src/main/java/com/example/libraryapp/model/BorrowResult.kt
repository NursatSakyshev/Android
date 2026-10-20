package com.example.libraryapp.model

sealed class BorrowResult {
    data class Success(val book: Book) : BorrowResult()
    data class NotAvailable(val book: Book) : BorrowResult()
    data class LimitExceeded(val user: User) : BorrowResult()
    data class NotFound(val id: Int) : BorrowResult()
}