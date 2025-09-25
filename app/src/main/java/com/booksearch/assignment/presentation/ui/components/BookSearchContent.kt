package com.booksearch.assignment.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.booksearch.assignment.R

@Composable
fun BookSearchContent(
    modifier: Modifier = Modifier,
    onOpen: (isbn: String) -> Unit
) {
    Column(modifier.fillMaxSize()) {
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = stringResource(R.string.search)
        )
        Row(Modifier.padding(12.dp)) {
            OutlinedTextField(
                value = "",
                onValueChange = {},
                modifier = Modifier.weight(1f),
                placeholder = { Text("검색어") }
            )
            Spacer(Modifier.width(8.dp))
            Button(onClick = {}) {
                Text("검색")
            }
        }
        FilterChip(
            selected = false,
            onClick = {},
            label = { Text("정확도순") }
        )
        FilterChip(
            selected = false,
            onClick = {},
            label = { Text("발간일순") }
        )
        LazyColumn(Modifier.weight(1f)) {}
    }
}
