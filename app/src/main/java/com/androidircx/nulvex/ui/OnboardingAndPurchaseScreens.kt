package com.androidircx.nulvex.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.androidircx.nulvex.i18n.tx
import com.androidircx.nulvex.ui.theme.Brass
import com.androidircx.nulvex.ui.theme.Ember
import com.androidircx.nulvex.ui.theme.Ink
import com.androidircx.nulvex.ui.theme.Moss
import com.androidircx.nulvex.ui.theme.Sand
import kotlinx.coroutines.launch

@Composable
internal fun PurchaseScreen(
    state: UiState,
    onBack: () -> Unit,
    onBuyRemoveAds: () -> Unit,
    onBuyProFeatures: () -> Unit,
    onRestorePurchases: () -> Unit
) {
    val onSurface = MaterialTheme.colorScheme.onSurface
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
                .imePadding()
        ) {
            Text(tx("Purchase options"), style = MaterialTheme.typography.titleLarge, color = onSurface)
            Spacer(modifier = Modifier.height(8.dp))
            Text(tx("One-time products from Google Play."),
                style = MaterialTheme.typography.bodySmall,
                color = onSurface.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.45f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(tx("Remove Ads (Lifetime)"), style = MaterialTheme.typography.titleMedium, color = onSurface)
                    Text(tx("Permanently removes banner and rewarded ad prompts."),
                        style = MaterialTheme.typography.bodySmall,
                        color = onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        if (state.isAdFree) tx("Owned") else state.removeAdsPrice,
                        color = if (state.isAdFree) Moss else Brass,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = onBuyRemoveAds,
                        enabled = state.billingReady && !state.isAdFree,
                        colors = ButtonDefaults.buttonColors(containerColor = Brass, contentColor = Ink),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (state.isAdFree) tx("OWNED") else tx("BUY REMOVE ADS"))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.45f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(tx("Pro Features (Lifetime)"), style = MaterialTheme.typography.titleMedium, color = onSurface)
                    Text(tx("Unlocks unlimited share credits. Does not remove ads. Backup and more are coming soon."),
                        style = MaterialTheme.typography.bodySmall,
                        color = onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        if (state.hasProFeatures) tx("Owned") else state.proFeaturesPrice,
                        color = if (state.hasProFeatures) Moss else Brass,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = onBuyProFeatures,
                        enabled = state.billingReady && !state.hasProFeatures,
                        colors = ButtonDefaults.buttonColors(containerColor = Brass, contentColor = Ink),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (state.hasProFeatures) tx("OWNED") else tx("BUY PRO FEATURES"))
                    }
                }
            }

            if (!state.billingReady) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(tx("Google Play Billing is not ready yet. Please wait a moment and try again."),
                    style = MaterialTheme.typography.bodySmall,
                    color = Ember
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            TextButton(onClick = onRestorePurchases, modifier = Modifier.fillMaxWidth()) {
                Text(tx("RESTORE PURCHASES"), color = Brass)
            }

            Spacer(modifier = Modifier.height(20.dp))
            TextButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                Text(tx("BACK TO SETTINGS"), color = Brass)
            }
        }
    }
}

private data class OnboardingPage(
    val icon: ImageVector,
    val title: String,
    val description: String
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun OnboardingScreen(
    languageTag: String,
    onSelectLanguage: (String) -> Unit,
    onComplete: () -> Unit
) {
    val onSurface = MaterialTheme.colorScheme.onSurface
    val pages = listOf(
        OnboardingPage(
            icon = Icons.Filled.Shield,
            title = tx("Welcome to Nulvex"),
            description = tx("Your offline-first secure vault. No cloud, no tracking, no compromise.")
        ),
        OnboardingPage(
            icon = Icons.Filled.Security,
            title = tx("Military-grade encryption"),
            description = tx("Your notes are encrypted with XChaCha20-Poly1305 and post-quantum ready key exchange.")
        ),
        OnboardingPage(
            icon = Icons.Filled.Schedule,
            title = tx("Self-destruct notes"),
            description = tx("Set notes to auto-delete after 1 hour, 24 hours, or 7 days. Or make them read-once.")
        ),
        OnboardingPage(
            icon = Icons.Filled.Warning,
            title = tx("Panic button"),
            description = tx("Instantly wipe everything if needed. Optional decoy vault for plausible deniability.")
        ),
        OnboardingPage(
            icon = Icons.Filled.Fingerprint,
            title = tx("Biometric unlock"),
            description = tx("Use your fingerprint to unlock quickly. Your PIN remains the master key.")
        )
    )
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val coroutineScope = rememberCoroutineScope()

    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = tx("Choose language"),
                style = MaterialTheme.typography.labelLarge,
                color = onSurface.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Chip(tx("System"), languageTag == "system") { onSelectLanguage("system") }
                Chip(tx("English"), languageTag == "en") { onSelectLanguage("en") }
                Chip(tx("Serbian"), languageTag == "sr") { onSelectLanguage("sr") }
            }
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                val item = pages[page]
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        tint = Brass,
                        modifier = Modifier
                            .height(80.dp)
                            .width(80.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.headlineSmall,
                        color = onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = onSurface.copy(alpha = 0.8f),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(pages.size) { index ->
                    val selected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .height(8.dp)
                            .width(if (selected) 24.dp else 8.dp)
                            .background(
                                if (selected) Brass else onSurface.copy(alpha = 0.3f),
                                RoundedCornerShape(4.dp)
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (pagerState.currentPage > 0) {
                    TextButton(onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    }) {
                        Text(tx("BACK"), color = onSurface.copy(alpha = 0.7f))
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                if (pagerState.currentPage < pages.size - 1) {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Brass, contentColor = Ink)
                    ) {
                        Text(tx("NEXT"))
                    }
                } else {
                    Button(
                        onClick = onComplete,
                        colors = ButtonDefaults.buttonColors(containerColor = Moss, contentColor = Sand)
                    ) {
                        Text(tx("GET STARTED"))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (pagerState.currentPage < pages.size - 1) {
                TextButton(onClick = onComplete) {
                    Text(tx("SKIP"), color = onSurface.copy(alpha = 0.5f))
                }
            }
        }
    }
}
