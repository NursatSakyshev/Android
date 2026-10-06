package org.example

import kotlinx.coroutines.delay

suspend fun loadCatalog(): List<Book> {
    println("Loading catalog...")
    delay(1000)
    return listOf(
        Book(1, "War and Peace", "Tolstoy", "Novel", 1225),
        Book(2, "1984", "Orwell", "Dystopia", 328),
        Book(3, "Brave New World", "Huxley", "Dystopia", 311),
        Book(4, "The Hobbit", "Tolkien", "Fantasy", 310),
        Book(5, "Anna Karenina", "Tolstoy", "Novel", 864),
    )
}