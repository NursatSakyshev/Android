# Library Management System

A Kotlin console app that simulates a library: students and teachers borrow books, search the catalog and see statistics.

## How to run

Open the project in IntelliJ IDEA, open `Main.kt` and click ▶ next to `main()`.

## Requirements

- **Data class:** `Book`
- **Sealed class:** `BorrowResult`
- **Inheritance, interface, polymorphism:** `User`, `Student`, `Teacher`, `Printable`
- **List, Set, Map:** `Library` (books, genres, borrowed books)
- **map, filter, reduce:** `getGenres()`, `findBooks()`, `totalPages()`
- **Higher-order function and lambda:** `findBooks { it.pages > 500 }`
- **Conditions and loops:** `borrowBook()`, `for` loop in `main()`
- **Suspend function and coroutine:** `loadCatalog()`, `runBlocking` and `launch` in `main()`
