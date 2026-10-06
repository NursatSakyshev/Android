package org.example

class Library(val books: List<Book>) {
    private val borrowed = mutableMapOf<User, MutableList<Book>>()

    fun borrowBook(user: User, bookId: Int): BorrowResult {
        val book = books.find { it.id == bookId } ?: return BorrowResult.NotFound(bookId)
        if(borrowed.values.flatten().any { it.id == bookId }) {
            return BorrowResult.NotAvailable(book)
        }
        if((borrowed[user]?.size ?: 0) >= user.maxBooks) {
            return BorrowResult.LimitExceeded(user)
        }
        borrowed.getOrPut(user) { mutableListOf() }.add(book)
        return BorrowResult.Success(book)
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