package com.example.libraryapp.model

class Library(val books: List<Book>) {
    private val borrowed = mutableMapOf<User, MutableList<Book>>()

    fun borrowBook(user: User, bookId: Int): BorrowResult {
        val book = books.find { it.id == bookId } ?: return BorrowResult.NotFound(bookId)
        if(isBorrowed(bookId)) {
            return BorrowResult.NotAvailable(book)
        }
        if(borrowedBy(user).size >= user.maxBooks) {
            return BorrowResult.LimitExceeded(user)
        }
        borrowed.getOrPut(user) { mutableListOf() }.add(book)
        return BorrowResult.Success(book)
    }

    fun isBorrowed(bookId: Int): Boolean {
        return borrowed.values.flatten().any { it.id == bookId }
    }

    fun returnBook(user: User, bookId: Int): Boolean {
        return borrowed[user]?.removeIf { it.id == bookId } ?: false
    }

    fun borrowedBy(user: User): List<Book> {
        return borrowed[user] ?: emptyList()
    }

    fun findBooks(predicate: (Book) -> Boolean): List<Book> {
        return books.filter(predicate)
    }

    fun getGenres(): Set<String> {
        return books.map { it.genre }.toSet()
    }

    fun totalPages(): Int {
        return books.map { it.pages }.reduce { acc, pages -> acc + pages }
    }
}