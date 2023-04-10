package com.example.weatherapp.ui.component

import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.launch

@Composable
fun SnackbarWithoutScaffold(
    message: String,
    showSb: Boolean,
    openSnackbar: (Boolean) -> Unit
) {

    val snackState = remember { SnackbarHostState() }
    val snackScope = rememberCoroutineScope()

    SnackbarHost(
        modifier = Modifier,
        hostState = snackState
    ) {
        Snackbar(
            snackbarData = it,
            backgroundColor = Color.White,
            contentColor = Color.Blue
        )
    }


    if (showSb) {
        LaunchedEffect(Unit) {
            snackScope.launch { snackState.showSnackbar(message) }
            openSnackbar(false)
        }

    }
}
