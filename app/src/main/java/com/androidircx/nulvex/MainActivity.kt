package com.androidircx.nulvex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.androidircx.nulvex.ui.MainScreen
import com.androidircx.nulvex.ui.MainViewModel
import com.androidircx.nulvex.ui.theme.NULVEXTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NULVEXTheme {
                val vm: MainViewModel = viewModel()
                val state = vm.uiState.value
                MainScreen(
                    state = state,
                    onSetup = vm::setupPins,
                    onUnlock = vm::unlock,
                    onLock = vm::lock,
                    onOpenNew = vm::openNewNote,
                    onCreate = vm::createNote,
                    onOpenNote = vm::openNote,
                    onCloseNote = vm::closeNoteDetail,
                    onDelete = vm::deleteNote,
                    onClearError = vm::clearError
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NULVEXTheme {
        MainScreen(
            state = com.androidircx.nulvex.ui.UiState(screen = com.androidircx.nulvex.ui.Screen.Setup),
            onSetup = { _, _ -> },
            onUnlock = {},
            onLock = {},
            onOpenNew = {},
            onCreate = { _, _, _ -> },
            onOpenNote = {},
            onCloseNote = {},
            onDelete = {},
            onClearError = {}
        )
    }
}
