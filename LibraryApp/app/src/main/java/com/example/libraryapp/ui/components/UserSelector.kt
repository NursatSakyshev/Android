package com.example.libraryapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.libraryapp.model.Student
import com.example.libraryapp.model.Teacher
import com.example.libraryapp.model.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserSelector(
    users: List<User>,
    currentUser: User,
    borrowedCount: Int,
    onSelect: (User) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            users.forEach { user ->
                val selected = user == currentUser
                FilterChip(
                    selected = selected,
                    onClick = { onSelect(user) },
                    label = { Text("${roleOf(user)}: ${user.name}") },
                    leadingIcon = if (selected) {
                        { Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    } else {
                        null
                    }
                )
            }
        }
        Text(
            text = "Borrowed: $borrowedCount / ${currentUser.maxBooks}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun roleOf(user: User): String = when (user) {
    is Student -> "Student"
    is Teacher -> "Teacher"
    else -> "User"
}
