package com.example.libraryapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.libraryapp.model.Book
import com.example.libraryapp.model.BorrowResult
import com.example.libraryapp.model.Library
import com.example.libraryapp.model.Student
import com.example.libraryapp.model.Teacher
import com.example.libraryapp.model.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class LibraryUiState(
    val users: List<User>,
    val currentUser: User,
    val isLoading: Boolean = true,
    val books: List<Book> = emptyList(),
    val borrowedIds: Set<Int> = emptySet(),
    val myBookIds: Set<Int> = emptySet(),
    val message: String? = null
)

class LibraryViewModel : ViewModel() {
    private val users = listOf(Student("Nursat"), Teacher("Beka"))
    private var library = Library(emptyList())

    var uiState by mutableStateOf(LibraryUiState(users = users, currentUser = users.first()))
        private set

    init {
        loadCatalog()
    }

    private fun loadCatalog() {
        viewModelScope.launch {
            delay(1000)
            library = Library(
                listOf(
                    Book(1, "War and Peace", "Tolstoy", "Novel", 1225),
                    Book(2, "1984", "Orwell", "Dystopia", 328),
                    Book(3, "Brave New World", "Huxley", "Dystopia", 311),
                    Book(4, "The Hobbit", "Tolkien", "Fantasy", 310),
                    Book(5, "Anna Karenina", "Tolstoy", "Novel", 864),
                    Book(6, "The Lord of the Rings", "Tolkien", "Fantasy", 1178),
                    Book(7, "Fahrenheit 451", "Bradbury", "Dystopia", 256),
                    Book(8, "Crime and Punishment", "Dostoevsky", "Novel", 671),
                )
            )
            uiState = uiState.copy(isLoading = false)
            refresh()
        }
    }

    fun selectUser(user: User) {
        uiState = uiState.copy(currentUser = user)
        refresh(message = null)
    }

    fun borrow(bookId: Int) {
        val message = when (val result = library.borrowBook(uiState.currentUser, bookId)) {
            is BorrowResult.Success -> "✅ Borrowed: ${result.book.title}"
            is BorrowResult.NotAvailable -> "❌ Already taken: ${result.book.title}"
            is BorrowResult.LimitExceeded -> "❌ Limit reached (${result.user.maxBooks} books)"
            is BorrowResult.NotFound -> "❌ Book not found"
        }
        refresh(message)
    }

    fun returnBook(bookId: Int) {
        val title = library.books.find { it.id == bookId }?.title
        val returned = library.returnBook(uiState.currentUser, bookId)
        refresh(if (returned) "↩️ Returned: $title" else "❌ You don't have this book")
    }

    fun clearMessage() {
        uiState = uiState.copy(message = null)
    }

    fun filteredBooks(query: String, onlyAvailable: Boolean): List<Book> {
        val state = uiState
        return state.books.filter { book ->
            val matchesQuery = query.isBlank() ||
                book.title.contains(query, ignoreCase = true) ||
                book.author.contains(query, ignoreCase = true)
            val matchesAvailability = !onlyAvailable || book.id !in state.borrowedIds
            matchesQuery && matchesAvailability
        }
    }

    private fun refresh(message: String? = uiState.message) {
        uiState = uiState.copy(
            books = library.books,
            borrowedIds = library.books.filter { library.isBorrowed(it.id) }.map { it.id }.toSet(),
            myBookIds = library.borrowedBy(uiState.currentUser).map { it.id }.toSet(),
            message = message
        )
    }
}
