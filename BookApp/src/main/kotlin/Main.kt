package org.example

import kotlinx.coroutines.*

fun printResult(result: BorrowResult) {
    when (result) {
        is BorrowResult.Success -> println("✅ Success: ${result.book.title}")
        is BorrowResult.NotAvailable -> println("❌ Not available: ${result.book.title}")
        is BorrowResult.LimitExceeded -> println("❌ Limit exceeded: ${result.user.name} (max ${result.user.maxBooks})")
        is BorrowResult.NotFound -> println("❌ Not found: id ${result.id}")
    }
}

fun main() = runBlocking {
    val books = loadCatalog()
    println("Catalog loaded: ${books.size} books")

    val job = launch {
        delay(500)
        println("\n☁️ Borrowed books synced with server")
    }

    val library = Library(books)
    val student = Student("Nursat")
    val teacher = Teacher("Beka")

    println("\n--- Users ---")
    val users: List<User> = listOf(student, teacher)
    for (user in users) {
        user.printInfo()
    }

    println("\n--- 1. Success ---")
    printResult(library.borrowBook(student, 1))

    println("\n--- 2. Not available ---")
    printResult(library.borrowBook(teacher, 1))

    println("\n--- 3. Not found ---")
    printResult(library.borrowBook(student, 999))

    println("\n--- 4. Limit exceeded ---")
    printResult(library.borrowBook(student, 2))
    printResult(library.borrowBook(student, 3))
    printResult(library.borrowBook(student, 4))  // 4th book, limit is 3

    println("\n--- Search ---")
    val tolstoyBooks = library.findBooks { it.author == "Tolstoy" }
    println("Tolstoy: ${tolstoyBooks.map { it.title }}")

    val longBooks = library.findBooks { it.pages > 500 }
    println("Longer than 500 pages: ${longBooks.map { it.title }}")

    println("\n--- Statistics ---")
    println("Genres: ${library.getGenres()}")
    println("Total pages: ${library.totalPages()}")

    job.join()
}
