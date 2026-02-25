package com.androidircx.nulvex.ui

import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.core.os.LocaleListCompat
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.androidircx.nulvex.data.Note
import com.androidircx.nulvex.pro.BackupRecord
import com.androidircx.nulvex.ui.theme.NULVEXTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NulvexUiTest {

    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Before
    fun forceEnglishLocale() {
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("en"))
    }

    // ---- Helper ----------------------------------------------------------------

    /**
     * Renders MainScreen with the given [state] and selected callback overrides.
     * All other callbacks are no-ops so tests only wire what they care about.
     */
    private fun show(
        state: UiState,
        onCompleteOnboarding: () -> Unit = {},
        onSetup: (String, String?, Boolean) -> Unit = { _, _, _ -> },
        onUnlock: (String) -> Unit = {},
        onPanic: () -> Unit = {},
        onOpenNew: () -> Unit = {},
        onOpenNote: (String) -> Unit = {},
        onDelete: (String) -> Unit = {},
        onSetShowArchived: (Boolean) -> Unit = {},
        onToggleArchived: (String) -> Unit = {},
        onUndoNoteEdit: (String) -> Unit = {},
        onRedoNoteEdit: (String) -> Unit = {},
        onClearNoteReminder: (String) -> Unit = {},
        onDeleteSharedKey: (String) -> Unit = {},
        onDeleteSavedBackup: (String) -> Unit = {},
        onRestoreBackup: (String, String, Boolean, String?, Long?) -> Unit = { _, _, _, _, _ -> },
        onOpenPurchases: () -> Unit = {},
        onRestorePurchases: () -> Unit = {}
    ) {
        rule.setContent {
            NULVEXTheme {
                MainScreen(
                    state = state,
                    onCompleteOnboarding = onCompleteOnboarding,
                    onSetup = onSetup,
                    onUnlock = onUnlock,
                    onLock = {},
                    onPanic = onPanic,
                    onOpenSettings = {},
                    onCloseSettings = {},
                    onUpdateDecoyPin = { _, _ -> },
                    onDisableDecoy = {},
                    onUpdateLockTimeout = {},
                    onUpdateDefaultExpiry = {},
                    onUpdateDefaultReadOnce = {},
                    onRequestBiometricEnroll = {},
                    onRequestBiometricUnlock = {},
                    onDisableBiometric = {},
                    onChangeRealPin = { _, _, _ -> },
                    onUpdateThemeMode = {},
                    onOpenNew = onOpenNew,
                    onCreate = { _, _, _, _, _, _, _, _ -> },
                    onOpenNote = onOpenNote,
                    onCloseNote = {},
                    onUpdateNoteText = { _, _, _ -> },
                    onDelete = onDelete,
                    onTogglePinned = {},
                    onToggleChecklistItem = { _, _ -> },
                    onAddChecklistItem = { _, _ -> },
                    onRemoveChecklistItem = { _, _ -> },
                    onUpdateChecklistText = { _, _, _ -> },
                    onMoveChecklistItem = { _, _, _ -> },
                    onAddLabel = { _, _ -> },
                    onRemoveLabel = { _, _ -> },
                    onSearchQueryChange = {},
                    onSelectLabel = {},
                    onSetShowArchived = onSetShowArchived,
                    onLoadAttachmentPreview = { _, _ -> },
                    onRemoveAttachment = { _, _ -> },
                    onToggleArchived = onToggleArchived,
                    onUndoNoteEdit = onUndoNoteEdit,
                    onRedoNoteEdit = onRedoNoteEdit,
                    onClearNoteReminder = onClearNoteReminder,
                    onClearError = {},
                    onOpenPurchases = onOpenPurchases,
                    onRestorePurchases = onRestorePurchases,
                    onDeleteSharedKey = onDeleteSharedKey,
                    onDeleteSavedBackup = onDeleteSavedBackup,
                    onRestoreBackup = onRestoreBackup
                )
            }
        }
    }

    private fun note(
        id: String = "1",
        text: String = "Test note",
        reminderAt: Long? = null
    ) = Note(
        id = id,
        text = text,
        checklist = emptyList(),
        labels = emptyList(),
        attachments = emptyList(),
        pinned = false,
        createdAt = System.currentTimeMillis(),
        expiresAt = null,
        readOnce = false,
        reminderAt = reminderAt
    )

    // ---- Onboarding -----------------------------------------------------------

    @Test
    fun onboarding_skipButtonIsDisplayed() {
        show(state = UiState(screen = Screen.Onboarding))
        rule.onNodeWithText("SKIP").assertIsDisplayed()
    }

    @Test
    fun onboarding_skipCallsOnComplete() {
        var called = false
        show(
            state = UiState(screen = Screen.Onboarding),
            onCompleteOnboarding = { called = true }
        )
        rule.onNodeWithText("SKIP").performClick()
        rule.waitForIdle()
        assertTrue(called)
    }

    @Test
    fun onboarding_firstPageShowsNextNotGetStarted() {
        show(state = UiState(screen = Screen.Onboarding))
        rule.onNodeWithText("NEXT").assertIsDisplayed()
        rule.onNodeWithText("GET STARTED").assertDoesNotExist()
    }

    // ---- Setup ----------------------------------------------------------------

    @Test
    fun setup_screenTitleIsVisible() {
        show(state = UiState(screen = Screen.Setup))
        rule.onNodeWithText("Create your vault").assertIsDisplayed()
    }

    @Test
    fun setup_createVaultDisabledWhenEmpty() {
        show(state = UiState(screen = Screen.Setup))
        rule.onNodeWithText("CREATE VAULT").assertIsNotEnabled()
    }

    @Test
    fun setup_createVaultEnabledWithMatchingPins() {
        show(state = UiState(screen = Screen.Setup))
        // Primary PIN field is the first text-action node, Confirm is the second
        rule.onAllNodes(hasSetTextAction())[0].performTextInput("1234")
        rule.onAllNodes(hasSetTextAction())[1].performTextInput("1234")
        rule.onNodeWithText("CREATE VAULT").assertIsEnabled()
    }

    @Test
    fun setup_showsErrorOnPinMismatch() {
        show(state = UiState(screen = Screen.Setup))
        rule.onAllNodes(hasSetTextAction())[0].performTextInput("1234")
        rule.onAllNodes(hasSetTextAction())[1].performTextInput("9999")
        rule.onNodeWithText("PINs do not match").assertIsDisplayed()
    }

    @Test
    fun setup_createVaultDisabledOnMismatch() {
        show(state = UiState(screen = Screen.Setup))
        rule.onAllNodes(hasSetTextAction())[0].performTextInput("1234")
        rule.onAllNodes(hasSetTextAction())[1].performTextInput("9999")
        rule.onNodeWithText("CREATE VAULT").assertIsNotEnabled()
    }

    @Test
    fun setup_callsOnSetupWithEnteredPin() {
        var capturedPin: String? = null
        show(
            state = UiState(screen = Screen.Setup),
            onSetup = { pin, _, _ -> capturedPin = pin }
        )
        rule.onAllNodes(hasSetTextAction())[0].performTextInput("5678")
        rule.onAllNodes(hasSetTextAction())[1].performTextInput("5678")
        rule.onNodeWithText("CREATE VAULT").performClick()
        rule.waitForIdle()
        assertEquals("5678", capturedPin)
    }

    @Test
    fun setup_decoyPinFieldHiddenByDefault() {
        show(state = UiState(screen = Screen.Setup))
        rule.onNodeWithText("Decoy PIN").assertDoesNotExist()
    }

    @Test
    fun setup_decoyPinFieldAppearsAfterCheckbox() {
        show(state = UiState(screen = Screen.Setup))
        // Click the first Checkbox node (Enable decoy vault) by semantic Role
        val isCheckbox = SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Checkbox)
        rule.onAllNodes(isCheckbox)[0].performClick()
        rule.waitForIdle()
        rule.onNodeWithText("Decoy PIN").assertIsDisplayed()
    }

    // ---- Unlock ---------------------------------------------------------------

    @Test
    fun unlock_titleIsVisible() {
        show(state = UiState(isSetup = true, screen = Screen.Unlock))
        rule.onNodeWithText("Unlock").assertIsDisplayed()
    }

    @Test
    fun unlock_unlockButtonDisabledWithNoPinEntered() {
        show(state = UiState(isSetup = true, screen = Screen.Unlock))
        rule.onNodeWithText("UNLOCK").assertIsNotEnabled()
    }

    @Test
    fun unlock_unlockButtonEnabledAfterDigitEntry() {
        show(state = UiState(isSetup = true, screen = Screen.Unlock))
        rule.onNodeWithText("1").performClick()
        rule.onNodeWithText("2").performClick()
        rule.onNodeWithText("3").performClick()
        rule.onNodeWithText("UNLOCK").assertIsEnabled()
    }

    @Test
    fun unlock_callsOnUnlockWithCorrectPin() {
        var capturedPin: String? = null
        show(
            state = UiState(isSetup = true, screen = Screen.Unlock),
            onUnlock = { capturedPin = it }
        )
        rule.onNodeWithText("1").performClick()
        rule.onNodeWithText("2").performClick()
        rule.onNodeWithText("3").performClick()
        rule.onNodeWithText("4").performClick()
        rule.onNodeWithText("UNLOCK").performClick()
        rule.waitForIdle()
        assertEquals("1234", capturedPin)
    }

    @Test
    fun unlock_backspaceRemovesLastDigit() {
        var capturedPin: String? = null
        show(
            state = UiState(isSetup = true, screen = Screen.Unlock),
            onUnlock = { capturedPin = it }
        )
        rule.onNodeWithText("1").performClick()
        rule.onNodeWithText("2").performClick()
        rule.onNodeWithText("3").performClick()
        rule.onNodeWithText("⌫").performClick()
        rule.onNodeWithText("UNLOCK").performClick()
        rule.waitForIdle()
        assertEquals("12", capturedPin)
    }

    @Test
    fun unlock_showsLockoutMessageWhenLockedOut() {
        val lockoutUntil = System.currentTimeMillis() + 60_000L
        show(state = UiState(
            isSetup = true,
            screen = Screen.Unlock,
            lockoutUntil = lockoutUntil
        ))
        rule.onNodeWithText("Too many attempts", substring = true).assertIsDisplayed()
    }

    @Test
    fun unlock_hidesUnlockButtonWhenLockedOut() {
        val lockoutUntil = System.currentTimeMillis() + 60_000L
        show(state = UiState(
            isSetup = true,
            screen = Screen.Unlock,
            lockoutUntil = lockoutUntil
        ))
        rule.onNodeWithText("UNLOCK").assertDoesNotExist()
    }

    @Test
    fun unlock_hidesBiometricButtonWhenDisabled() {
        show(state = UiState(
            isSetup = true,
            screen = Screen.Unlock,
            biometricEnabled = false
        ))
        rule.onNodeWithText("UNLOCK WITH FINGERPRINT").assertDoesNotExist()
    }

    @Test
    fun unlock_showsBiometricButtonWhenEnabled() {
        show(state = UiState(
            isSetup = true,
            screen = Screen.Unlock,
            biometricEnabled = true
        ))
        rule.onNodeWithText("UNLOCK WITH FINGERPRINT").assertIsDisplayed()
    }

    // ---- Vault ----------------------------------------------------------------

    @Test
    fun vault_newNoteFabIsDisplayed() {
        show(state = UiState(isSetup = true, screen = Screen.Vault))
        rule.onNodeWithContentDescription("New note").assertIsDisplayed()
    }

    @Test
    fun vault_newNoteFabCallsOnOpenNew() {
        var clicked = false
        show(
            state = UiState(isSetup = true, screen = Screen.Vault),
            onOpenNew = { clicked = true }
        )
        rule.onNodeWithContentDescription("New note").performClick()
        rule.waitForIdle()
        assertTrue(clicked)
    }

    @Test
    fun vault_searchBarIsDisplayed() {
        show(state = UiState(isSetup = true, screen = Screen.Vault))
        rule.onNodeWithContentDescription("Search").assertIsDisplayed()
    }

    @Test
    fun vault_noteTextIsDisplayed() {
        show(state = UiState(
            isSetup = true,
            screen = Screen.Vault,
            notes = listOf(note(text = "Top secret note"))
        ))
        rule.onNodeWithText("Top secret note").assertIsDisplayed()
    }

    @Test
    fun vault_multipleNotesAllDisplayed() {
        show(state = UiState(
            isSetup = true,
            screen = Screen.Vault,
            notes = listOf(
                note("1", "First note"),
                note("2", "Second note")
            )
        ))
        rule.onNodeWithText("First note").assertIsDisplayed()
        rule.onNodeWithText("Second note").assertIsDisplayed()
    }

    @Test
    fun vault_tapNoteCallsOnOpenNote() {
        var openedId: String? = null
        show(
            state = UiState(
                isSetup = true,
                screen = Screen.Vault,
                notes = listOf(note("abc", "Tap me"))
            ),
            onOpenNote = { openedId = it }
        )
        rule.onNodeWithText("Tap me").performClick()
        rule.waitForIdle()
        assertEquals("abc", openedId)
    }

    @Test
    fun vault_archiveTabCallsSetShowArchived() {
        var showArchived: Boolean? = null
        show(
            state = UiState(isSetup = true, screen = Screen.Vault),
            onSetShowArchived = { showArchived = it }
        )

        rule.onNodeWithText("Archived").performClick()
        rule.waitForIdle()

        assertEquals(true, showArchived)
    }

    @Test
    fun noteDetail_archiveButtonCallsCallback() {
        var archivedId: String? = null
        show(
            state = UiState(
                isSetup = true,
                screen = Screen.NoteDetail,
                selectedNote = note(id = "archive-id", text = "Archive me")
            ),
            onToggleArchived = { archivedId = it }
        )

        rule.onNodeWithText("ARCHIVE").performClick()
        rule.waitForIdle()

        assertEquals("archive-id", archivedId)
    }

    @Test
    fun noteDetail_setReminderButtonIsVisible() {
        show(
            state = UiState(
                isSetup = true,
                screen = Screen.NoteDetail,
                selectedNote = note(id = "rem-1", text = "Reminder note")
            )
        )

        rule.onNodeWithText("SET REMINDER").assertIsDisplayed()
    }

    @Test
    fun noteDetail_clearReminderCallsCallback() {
        var clearedId: String? = null
        show(
            state = UiState(
                isSetup = true,
                screen = Screen.NoteDetail,
                selectedNote = note(
                    id = "rem-2",
                    text = "Reminder set",
                    reminderAt = System.currentTimeMillis() + 60_000L
                )
            ),
            onClearNoteReminder = { clearedId = it }
        )

        rule.onNodeWithText("CLEAR REMINDER").performClick()
        rule.waitForIdle()

        assertEquals("rem-2", clearedId)
    }

    @Test
    fun noteDetail_undoRedoButtonsRespectEnabledFlags() {
        show(
            state = UiState(
                isSetup = true,
                screen = Screen.NoteDetail,
                selectedNote = note(id = "undo-1", text = "Editable"),
                canUndoNoteEdit = false,
                canRedoNoteEdit = false
            )
        )

        rule.onNodeWithContentDescription("Edit note").performClick()
        rule.onNodeWithText("UNDO").assertIsDisplayed().assertIsNotEnabled()
        rule.onNodeWithText("REDO").assertIsDisplayed().assertIsNotEnabled()
    }

    @Test
    fun noteDetail_undoRedoButtonsCallCallbacksWhenEnabled() {
        var undoId: String? = null
        var redoId: String? = null
        show(
            state = UiState(
                isSetup = true,
                screen = Screen.NoteDetail,
                selectedNote = note(id = "undo-2", text = "Editable"),
                canUndoNoteEdit = true,
                canRedoNoteEdit = true
            ),
            onUndoNoteEdit = { undoId = it },
            onRedoNoteEdit = { redoId = it }
        )

        rule.onNodeWithContentDescription("Edit note").performClick()
        rule.onNodeWithText("UNDO").assertIsEnabled().performClick()
        rule.onNodeWithText("REDO").assertIsEnabled().performClick()
        rule.waitForIdle()

        assertEquals("undo-2", undoId)
        assertEquals("undo-2", redoId)
    }

    // ---- Panic button ---------------------------------------------------------

    @Test
    fun panic_buttonVisibleOnVaultScreen() {
        show(state = UiState(isSetup = true, screen = Screen.Vault))
        rule.onNodeWithContentDescription("Panic wipe").assertIsDisplayed()
    }

    @Test
    fun panic_buttonNotVisibleOnSetupScreen() {
        show(state = UiState(screen = Screen.Setup))
        rule.onNodeWithContentDescription("Panic wipe").assertDoesNotExist()
    }

    @Test
    fun panic_buttonNotVisibleOnUnlockScreen() {
        show(state = UiState(isSetup = true, screen = Screen.Unlock))
        rule.onNodeWithContentDescription("Panic wipe").assertDoesNotExist()
    }

    // ---- Error banner ---------------------------------------------------------

    @Test
    fun errorBanner_showsWhenErrorPresent() {
        show(state = UiState(
            isSetup = true,
            screen = Screen.Vault,
            error = "Something went wrong"
        ))
        rule.onNodeWithText("Something went wrong").assertIsDisplayed()
    }

    @Test
    fun errorBanner_notShownWhenErrorNull() {
        show(state = UiState(isSetup = true, screen = Screen.Vault, error = null))
        rule.onNodeWithText("Something went wrong").assertDoesNotExist()
    }

    // ---- Purchases ------------------------------------------------------------

    @Test
    fun settings_openPurchaseOptionsCallsCallback() {
        var called = false
        show(
            state = UiState(isSetup = true, screen = Screen.Settings),
            onOpenPurchases = { called = true }
        )

        rule.onNodeWithText("Rewards & Ads").performClick()
        rule.onNodeWithText("OPEN PURCHASE OPTIONS").performClick()
        rule.waitForIdle()

        assertTrue(called)
    }

    @Test
    fun purchases_screenShowsProductsAndRestoreButton() {
        show(
            state = UiState(
                isSetup = true,
                screen = Screen.Purchases,
                billingReady = true,
                removeAdsPrice = "$1.99",
                proFeaturesPrice = "$4.99"
            )
        )

        rule.onNodeWithText("Purchase options").assertIsDisplayed()
        rule.onNodeWithText("Remove Ads (Lifetime)").assertIsDisplayed()
        rule.onNodeWithText("Pro Features (Lifetime)").assertIsDisplayed()
        rule.onNodeWithText("RESTORE PURCHASES").assertIsDisplayed()
        rule.onNodeWithText("Does not remove ads", substring = true).assertIsDisplayed()
    }

    @Test
    fun purchases_restoreButtonCallsCallback() {
        var called = false
        show(
            state = UiState(isSetup = true, screen = Screen.Purchases),
            onRestorePurchases = { called = true }
        )

        rule.onNodeWithText("RESTORE PURCHASES").performClick()
        rule.waitForIdle()

        assertTrue(called)
    }

    @Test
    fun settings_proFeaturesShowsUnlimitedAndHidesShareAdButton() {
        show(
            state = UiState(
                isSetup = true,
                screen = Screen.Settings,
                hasProFeatures = true,
                shareCredits = 3
            )
        )

        rule.onNodeWithText("Rewards & Ads").performClick()
        rule.onNodeWithText("UNLIMITED").assertIsDisplayed()
        rule.onNodeWithText("WATCH AD - EARN 1 SHARE CREDIT").assertDoesNotExist()
    }

    @Test
    fun settings_removeAdsLifetimeDisablesWatchAdButton() {
        show(
            state = UiState(
                isSetup = true,
                screen = Screen.Settings,
                isAdFree = true
            )
        )

        rule.onNodeWithText("Rewards & Ads").performClick()
        rule.onNodeWithText("ADS REMOVED").assertIsNotEnabled()
    }

    @Test
    fun settings_sectionsAreCollapsedUntilHeaderClick() {
        show(state = UiState(isSetup = true, screen = Screen.Settings))

        rule.onNodeWithText("WATCH AD - 10 MIN NO ADS").assertDoesNotExist()
        rule.onNodeWithText("Rewards & Ads").performClick()
        rule.onNodeWithText("WATCH AD - 10 MIN NO ADS").assertIsDisplayed()
    }

    @Test
    fun settings_searchClearButtonAppearsAndClearsInput() {
        show(state = UiState(isSetup = true, screen = Screen.Settings))

        rule.onNodeWithText("Search settings").performTextInput("ads")
        rule.onNodeWithContentDescription("Clear search").assertIsDisplayed()
        rule.onNodeWithContentDescription("Clear search").performClick()
        rule.onNodeWithText("Search settings").performTextInput("security")
        rule.onNodeWithText("Search settings").performTextClearance()
    }

    @Test
    fun settings_searchNoMatchShowsEmptyState() {
        show(state = UiState(isSetup = true, screen = Screen.Settings))

        rule.onNodeWithText("Search settings").performTextInput("zzzzzzz")
        rule.onNodeWithText("No settings match your search.").assertIsDisplayed()
    }


    @Test
    fun settings_deleteSavedBackup_yesDeletes() {
        var deletedRecordId: String? = null
        show(
            state = UiState(
                isSetup = true,
                screen = Screen.Settings,
                backupRecords = listOf(
                    BackupRecord(
                        id = "rec-1",
                        mediaId = "media-1",
                        downloadPathId = "download-1",
                        keyId = "key-1",
                        downloadToken = null,
                        downloadExpires = null,
                        sizeBytes = 10,
                        sha256 = "abc",
                        createdAt = 1L
                    )
                )
            ),
            onDeleteSavedBackup = { deletedRecordId = it }
        )

        rule.onNodeWithText("Search settings").performTextInput("backup")
        rule.onNodeWithText("Backup").performClick()
        rule.onNodeWithText("DELETE").performClick()
        rule.onNodeWithText("Delete backup record?").assertIsDisplayed()
        rule.onNodeWithText("YES").performClick()
        rule.waitForIdle()

        assertEquals("rec-1", deletedRecordId)
    }

    @Test
    fun settings_generatedStatus_showsSuccessDialog() {
        show(
            state = UiState(
                isSetup = true,
                screen = Screen.Settings,
                backupStatus = "XChaCha key generated"
            )
        )

        rule.onNodeWithText("Success").assertIsDisplayed()
        rule.onNodeWithText("Key created successfully.").assertIsDisplayed()
    }

}
