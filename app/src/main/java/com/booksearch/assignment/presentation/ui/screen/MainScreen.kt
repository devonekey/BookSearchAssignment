package com.booksearch.assignment.presentation.ui.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.booksearch.assignment.R
import com.booksearch.assignment.presentation.ui.components.BookSearchContent

@Composable
fun MainScreen(navController: NavHostController) {
    var currentTab by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentTab == 0,
                    onClick = { currentTab = 0 },
                    icon = {},
                    label = { Text(stringResource(R.string.search)) }
                )
                NavigationBarItem(
                    selected = currentTab == 1,
                    onClick = { currentTab = 1 },
                    icon = {},
                    label = { Text(stringResource(R.string.bookmark)) }
                )
            }
        }
    ) { paddingValues ->
        when (currentTab) {
            0 -> BookSearchContent(
                modifier = Modifier.padding(paddingValues = paddingValues),
                onOpen = { isbn -> navController.navigate("detail/$isbn") }
            )
            else -> {

            }
        }
    }
}
