package com.example.libraryapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.libraryapp.ui.components.AvailabilityFilter
import com.example.libraryapp.ui.components.BookCard
import com.example.libraryapp.ui.components.BookSearchField
import com.example.libraryapp.ui.components.UserSelector
import com.example.libraryapp.viewmodel.LibraryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(viewModel: LibraryViewModel = viewModel()) {
    val uiState = viewModel.uiState

    var query by rememberSaveable { mutableStateOf("") }
    var onlyAvailable by rememberSaveable { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("📚 Library") }) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            val books = viewModel.filteredBooks(query, onlyAvailable)

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    UserSelector(
                        users = uiState.users,
                        currentUser = uiState.currentUser,
                        borrowedCount = uiState.myBookIds.size,
                        onSelect = { viewModel.selectUser(it) }
                    )
                }
                item {
                    BookSearchField(query = query, onQueryChange = { query = it })
                }
                item {
                    AvailabilityFilter(checked = onlyAvailable, onCheckedChange = { onlyAvailable = it })
                }

                if (books.isEmpty()) {
                    item {
                        Text(
                            text = "No books found",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                items(books, key = { it.id }) { book ->
                    BookCard(
                        book = book,
                        isBorrowed = book.id in uiState.borrowedIds,
                        isMine = book.id in uiState.myBookIds,
                        onBorrow = { viewModel.borrow(book.id) },
                        onReturn = { viewModel.returnBook(book.id) }
                    )
                }
            }
        }
    }
}
