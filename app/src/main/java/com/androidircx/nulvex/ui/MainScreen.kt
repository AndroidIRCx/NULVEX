package com.androidircx.nulvex.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.androidircx.nulvex.data.Note
import com.androidircx.nulvex.ui.theme.Brass
import com.androidircx.nulvex.ui.theme.Coal
import com.androidircx.nulvex.ui.theme.Ember
import com.androidircx.nulvex.ui.theme.Ink
import com.androidircx.nulvex.ui.theme.Moss
import com.androidircx.nulvex.ui.theme.Sand

@Composable
fun MainScreen(state: UiState, onSetup: (String, String?) -> Unit, onUnlock: (String) -> Unit,
               onLock: () -> Unit, onOpenNew: () -> Unit, onCreate: (String, Long?, Boolean) -> Unit,
               onOpenNote: (String) -> Unit, onCloseNote: () -> Unit, onDelete: (String) -> Unit,
               onClearError: () -> Unit) {
    AppBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            TopHeader(state, onLock)
            Spacer(modifier = Modifier.height(16.dp))
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(350)) + slideInVertically(animationSpec = tween(350)) { 40 }
            ) {
                when (state.screen) {
                    Screen.Setup -> SetupScreen(state, onSetup)
                    Screen.Unlock -> UnlockScreen(state, onUnlock)
                    Screen.Vault -> VaultScreen(state, onOpenNew, onOpenNote)
                    Screen.NewNote -> NewNoteScreen(state, onCreate)
                    Screen.NoteDetail -> NoteDetailScreen(state, onCloseNote, onDelete)
                }
            }
        }
        ErrorBar(state, onClearError)
    }
}

@Composable
private fun AppBackground(content: @Composable () -> Unit) {
    val gradient = Brush.linearGradient(
        colors = listOf(Ink, Coal, Moss)
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(Brass.copy(alpha = 0.12f), Color.Transparent),
                        radius = 900f
                    )
                )
        )
        content()
    }
}

@Composable
private fun TopHeader(state: UiState, onLock: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "NULVEX",
                style = MaterialTheme.typography.displayLarge,
                color = Sand
            )
            Text(
                text = "Offline secure vault",
                style = MaterialTheme.typography.labelLarge,
                color = Sand.copy(alpha = 0.7f)
            )
        }
        if (state.screen == Screen.Vault || state.screen == Screen.NoteDetail || state.screen == Screen.NewNote) {
            TextButton(onClick = onLock) {
                Text(text = "LOCK", color = Brass)
            }
        }
    }
}

@Composable
private fun SetupScreen(state: UiState, onSetup: (String, String?) -> Unit) {
    var pin by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var decoyEnabled by remember { mutableStateOf(false) }
    var decoyPin by remember { mutableStateOf("") }

    Surface(
        shape = RoundedCornerShape(24.dp),
        color = Coal.copy(alpha = 0.9f)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Create your vault", style = MaterialTheme.typography.titleLarge, color = Sand)
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = pin,
                onValueChange = { pin = it },
                label = { Text("Primary PIN") },
                singleLine = true
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = confirm,
                onValueChange = { confirm = it },
                label = { Text("Confirm PIN") },
                singleLine = true
            )
            Spacer(modifier = Modifier.height(14.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = decoyEnabled, onCheckedChange = { decoyEnabled = it })
                Text("Enable decoy vault", color = Sand)
            }
            if (decoyEnabled) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = decoyPin,
                    onValueChange = { decoyPin = it },
                    label = { Text("Decoy PIN") },
                    singleLine = true
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            val pinMismatch = pin.isNotEmpty() && confirm.isNotEmpty() && pin != confirm
            if (pinMismatch) {
                Text("PINs do not match", color = Ember, style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(8.dp))
            }
            Button(
                onClick = { onSetup(pin, if (decoyEnabled) decoyPin else null) },
                enabled = !state.isBusy && pin.isNotBlank() && !pinMismatch,
                colors = ButtonDefaults.buttonColors(containerColor = Brass, contentColor = Ink)
            ) {
                Text("CREATE VAULT")
            }
        }
    }
}

@Composable
private fun UnlockScreen(state: UiState, onUnlock: (String) -> Unit) {
    var pin by remember { mutableStateOf("") }
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = Coal.copy(alpha = 0.9f)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Unlock", style = MaterialTheme.typography.titleLarge, color = Sand)
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = pin,
                onValueChange = { pin = it },
                label = { Text("PIN") },
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { onUnlock(pin) },
                enabled = !state.isBusy && pin.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Brass, contentColor = Ink)
            ) {
                Text("UNLOCK")
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Decoy vault opens with its own PIN.",
                color = Sand.copy(alpha = 0.6f),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
private fun VaultScreen(state: UiState, onOpenNew: () -> Unit, onOpenNote: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Your vault", style = MaterialTheme.typography.titleLarge, color = Sand)
        Button(
            onClick = onOpenNew,
            colors = ButtonDefaults.buttonColors(containerColor = Moss, contentColor = Sand)
        ) {
            Text("NEW NOTE")
        }
    }
    Spacer(modifier = Modifier.height(12.dp))
    HorizontalDivider(color = Sand.copy(alpha = 0.12f))
    Spacer(modifier = Modifier.height(12.dp))
    if (state.notes.isEmpty()) {
        Text(
            text = "No notes yet. Create the first one.",
            color = Sand.copy(alpha = 0.7f),
            style = MaterialTheme.typography.bodyLarge
        )
        return
    }
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(state.notes, key = { it.id }) { note ->
            NoteCard(note, onOpenNote)
        }
    }
}

@Composable
private fun NoteCard(note: Note, onOpenNote: (String) -> Unit) {
    val preview = note.content.trim().replace("\n", " ")
    Card(
        onClick = { onOpenNote(note.id) },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Coal.copy(alpha = 0.92f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = preview.ifBlank { "Empty note" },
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = Sand
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (note.readOnce) {
                    Text(
                        text = "READ ONCE",
                        style = MaterialTheme.typography.labelLarge,
                        color = Brass
                    )
                }
                if (note.expiresAt != null) {
                    Text(
                        text = "EXPIRING",
                        style = MaterialTheme.typography.labelLarge,
                        color = Ember
                    )
                }
            }
        }
    }
}

@Composable
private fun NewNoteScreen(state: UiState, onCreate: (String, Long?, Boolean) -> Unit) {
    var content by remember { mutableStateOf("") }
    var readOnce by remember { mutableStateOf(false) }
    var expiryChoice by remember { mutableStateOf("none") }

    Surface(
        shape = RoundedCornerShape(24.dp),
        color = Coal.copy(alpha = 0.9f)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("New note", style = MaterialTheme.typography.titleLarge, color = Sand)
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Write safely...") },
                minLines = 6
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = readOnce, onCheckedChange = { readOnce = it })
                Text("Read once", color = Sand)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text("Expiry", style = MaterialTheme.typography.labelLarge, color = Sand)
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Chip("None", expiryChoice == "none") { expiryChoice = "none" }
                Chip("1h", expiryChoice == "1h") { expiryChoice = "1h" }
                Chip("24h", expiryChoice == "24h") { expiryChoice = "24h" }
                Chip("7d", expiryChoice == "7d") { expiryChoice = "7d" }
            }
            Spacer(modifier = Modifier.height(16.dp))
            val expiresAt = when (expiryChoice) {
                "1h" -> System.currentTimeMillis() + 3_600_000L
                "24h" -> System.currentTimeMillis() + 86_400_000L
                "7d" -> System.currentTimeMillis() + 604_800_000L
                else -> null
            }
            Button(
                onClick = { onCreate(content, expiresAt, readOnce) },
                enabled = !state.isBusy,
                colors = ButtonDefaults.buttonColors(containerColor = Brass, contentColor = Ink)
            ) {
                Text("SAVE NOTE")
            }
        }
    }
}

@Composable
private fun NoteDetailScreen(state: UiState, onClose: () -> Unit, onDelete: (String) -> Unit) {
    val note = state.selectedNote
    if (note == null) {
        Text("Note unavailable", color = Sand)
        return
    }
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = Coal.copy(alpha = 0.9f)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Note", style = MaterialTheme.typography.titleLarge, color = Sand)
            Spacer(modifier = Modifier.height(10.dp))
            Text(note.content, color = Sand, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = { onDelete(note.id) },
                    colors = ButtonDefaults.buttonColors(containerColor = Ember, contentColor = Sand)
                ) {
                    Text("DELETE")
                }
                TextButton(onClick = onClose) {
                    Text("BACK", color = Brass)
                }
            }
        }
    }
}

@Composable
private fun Chip(text: String, selected: Boolean, onClick: () -> Unit) {
    val bg = if (selected) Brass else Coal
    val fg = if (selected) Ink else Sand
    TextButton(
        onClick = onClick,
        colors = ButtonDefaults.textButtonColors(containerColor = bg, contentColor = fg),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.height(32.dp)
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
private fun ErrorBar(state: UiState, onClear: () -> Unit) {
    val msg = state.error ?: return
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .alpha(0.95f)
            .background(Ember, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(msg, color = Sand, modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(8.dp))
            TextButton(onClick = onClear) {
                Text("OK", color = Sand, fontWeight = FontWeight.Bold)
            }
        }
    }
}
