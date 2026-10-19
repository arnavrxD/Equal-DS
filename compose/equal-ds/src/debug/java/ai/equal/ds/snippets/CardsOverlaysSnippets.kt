// Compose code shown on the docs site (Cards, Overlays, Lists & rows). Each function body is
// extracted by scripts/build-snippets.mjs; keep the `// snippet: <doc id> / <example title>` line above it.
// Debug source set only: compiled with the module, never shipped in the release AAR.
@file:Suppress("unused")

package ai.equal.ds.snippets

import ai.equal.ds.assets.EqualAssets
import ai.equal.ds.assets.EqualCategory
import ai.equal.ds.components.ActionCard
import ai.equal.ds.components.ActionCardAction
import ai.equal.ds.components.ActionCardBody
import ai.equal.ds.components.ActionCardLine
import ai.equal.ds.components.ActionCardTone
import ai.equal.ds.components.CallDirection
import ai.equal.ds.components.CallHistoryList
import ai.equal.ds.components.CallHistoryRow
import ai.equal.ds.components.ChatBubble
import ai.equal.ds.components.ChatBubbleType
import ai.equal.ds.components.ConfirmSheetContent
import ai.equal.ds.components.ContextMenu
import ai.equal.ds.components.ContextMenuItem
import ai.equal.ds.components.EqualBottomSheet
import ai.equal.ds.components.EqualButton
import ai.equal.ds.components.EqualButtonSize
import ai.equal.ds.components.EqualButtonVariant
import ai.equal.ds.components.EqualDialog
import ai.equal.ds.components.EqualDialogContent
import ai.equal.ds.components.EqualSnackbar
import ai.equal.ds.components.EqualText
import ai.equal.ds.components.FeedbackCard
import ai.equal.ds.components.FullSheet
import ai.equal.ds.components.HomeHeader
import ai.equal.ds.components.IllustrationHeader
import ai.equal.ds.components.LargeActionChip
import ai.equal.ds.components.LargeActionChipState
import ai.equal.ds.components.MeetAssistantCard
import ai.equal.ds.components.MeetAssistantLayout
import ai.equal.ds.components.OverflowMenu
import ai.equal.ds.components.RowGroup
import ai.equal.ds.components.SectionHeader
import ai.equal.ds.components.SectionHeaderSize
import ai.equal.ds.components.SettingsRow
import ai.equal.ds.components.SystemNotificationPreview
import ai.equal.ds.components.SystemStatusCard
import ai.equal.ds.components.SystemStatusState
import ai.equal.ds.components.ToggleRow
import ai.equal.ds.components.WhatsNewCard
import ai.equal.ds.components.instructionLines
import ai.equal.ds.icons.EqualIcons
import ai.equal.ds.theme.EqualTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

// snippet: feedback-card / Interactive
@Composable
internal fun FeedbackCardInteractiveSnippet() {
    var rating by remember { mutableIntStateOf(0) }
    FeedbackCard(
        question = "How would you rate the call?",
        rating = rating,
        onRatingChange = { rating = it },
        // Compose: the share callback takes no argument; read `rating` from state
        onShareFeedbackClick = {},
    )
}

// snippet: feedback-card / Rated
@Composable
internal fun FeedbackCardRatedSnippet() {
    // Compose: always controlled, so the question and rating callback are required
    FeedbackCard(
        question = "How would you rate the call?",
        rating = 4,
        onRatingChange = {},
        onShareFeedbackClick = {},
    )
}

// snippet: action-card / WhatsApp reply
@Composable
internal fun ActionCardWhatsAppReplySnippet() {
    // The coded WhatsApp logo is the default when no category or icon is set
    ActionCard(
        title = "WhatsApp · Amit Nanda",
        quote = "“Count me in for Sunday 8 PM 👍”",
        timestamp = "03:21",
        actions = listOf(
            ActionCardAction("Send", emoji = "📤", onClick = {}),
            ActionCardAction("Edit reply", emoji = "✏️", onClick = {}),
        ),
    )
}

// snippet: action-card / Appointment: Confirm + Edit
@Composable
internal fun ActionCardAppointmentSnippet() {
    ActionCard(
        category = EqualCategory.Others,
        title = "Dr. Mehta's Clinic Appointment",
        lines = listOf(
            ActionCardLine("Tomorrow, 9:00 AM"),
            ActionCardLine("Bring previous medical reports", ActionCardTone.Tertiary),
        ),
        timestamp = "03:21",
        actions = listOf(
            ActionCardAction("Confirm", emoji = "👍", onClick = {}),
            ActionCardAction("Edit", emoji = "✏️", onClick = {}),
        ),
    )
}

// snippet: action-card / Instruction saved
@Composable
internal fun ActionCardInstructionSavedSnippet() {
    ActionCard(
        icon = EqualIcons.Instructions,
        title = "Instruction saved",
        lines = instructionLines("Delivery agents call", "Ask them to leave parcels with security"),
        timestamp = "03:21",
        actions = listOf(ActionCardAction("Edit rule", emoji = "✏️", onClick = {})),
    )
}

// snippet: action-card / Instruction (body only)
@Composable
internal fun ActionCardBodyOnlySnippet() {
    ActionCardBody(
        icon = EqualIcons.Instructions,
        title = "Instructions",
        lines = instructionLines("Bank calls come", "Tell callers I’ll call back after 6 PM"),
        timestamp = "03:21",
    )
}

// snippet: whats-new-card / Default
@Composable
internal fun WhatsNewCardDefaultSnippet() {
    WhatsNewCard(
        title = "Language Preference",
        body = "Set your preferred language and choose how your assistant sounds",
        buttonLabel = "Try Voices",
        buttonIcon = EqualIcons.Translate,
        onButtonClick = {},
        onCloseClick = {},
    )
}

// snippet: whats-new-card / Custom badge and icon, not dismissible
@Composable
internal fun WhatsNewCardCustomBadgeSnippet() {
    WhatsNewCard(
        badgeText = "Just added",
        title = "Instructions",
        body = "Tell your assistant how to handle deliveries, sales calls and more.",
        buttonLabel = "Add instruction",
        buttonIcon = EqualIcons.Add,
        onButtonClick = {},
        onCloseClick = null, // null hides the close (X)
    )
}

// snippet: system-status-card / Unavailable
@Composable
internal fun SystemStatusUnavailableSnippet() {
    SystemStatusCard(state = SystemStatusState.Unavailable, onNotifyClick = {})
}

// snippet: system-status-card / Notify requested
@Composable
internal fun SystemStatusNotifiedSnippet() {
    // The confirmation is not tappable, so onNotifyClick is never called in this state
    SystemStatusCard(state = SystemStatusState.Notified, onNotifyClick = {})
}

// snippet: system-status-card / In the Chats header
@Composable
internal fun SystemStatusInHeaderSnippet() {
    var on by remember { mutableStateOf(true) }
    var notify by remember { mutableStateOf(SystemStatusState.Unavailable) }
    val down = true
    HomeHeader(
        assistantOn = on,
        onAssistantChange = { on = it },
        status = if (down) {
            { SystemStatusCard(state = notify, onNotifyClick = { notify = SystemStatusState.Notified }) }
        } else {
            null
        },
    )
}

// snippet: meet-assistant-card / Compact (Chats)
@Composable
internal fun MeetAssistantCompactSnippet() {
    MeetAssistantCard(onCallClick = {}, onDismiss = {})
}

// snippet: meet-assistant-card / Banner
@Composable
internal fun MeetAssistantBannerSnippet() {
    MeetAssistantCard(layout = MeetAssistantLayout.Banner, onCallClick = {}, onDismiss = {})
}

// snippet: system-notification / Back on
@Composable
internal fun SystemNotificationBackOnSnippet() {
    // On a device, post a NotificationCompat instead; this preview is for mocks and flows
    SystemNotificationPreview(
        title = "Equal AI Assistant is back on",
        body = "Equal AI is now handling all your unknown calls.",
    )
}

// snippet: bottom-sheet / Delete confirm (inline)
@Composable
internal fun BottomSheetDeleteConfirmSnippet() {
    // Compose: no inline mode; the sheet always draws over its parent with the scrim
    EqualBottomSheet(visible = true, onDismissRequest = {}) {
        ConfirmSheetContent(
            title = "Delete chat with\nAmit Nanda",
            phone = "(+91 9871746113)",
            body = "All past conversations will be deleted. This action cannot be undone.",
            confirmLabel = "Delete",
            destructive = true,
            onConfirm = {},
        )
    }
}

// snippet: bottom-sheet / Titled sheet (inline)
@Composable
internal fun BottomSheetTitledSnippet() {
    // Compose: no inline mode; the sheet always draws over its parent with the scrim
    EqualBottomSheet(visible = true, onDismissRequest = {}, title = "Turn off WhatsApp updates?") {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            IllustrationHeader(asset = EqualAssets.IllustrationWhatsapp, size = 120.dp)
            EqualText(
                text = "You'll stop getting call alerts and account updates on WhatsApp.",
                style = EqualTheme.type.body1,
                color = EqualTheme.colors.textSecondary,
                textAlign = TextAlign.Center,
            )
            EqualButton(text = "Turn off updates", onClick = {}, size = EqualButtonSize.Block, modifier = Modifier.padding(top = 16.dp))
        }
    }
}

// snippet: bottom-sheet / Overlay in a phone
@Composable
internal fun BottomSheetOverlaySnippet() {
    var open by remember { mutableStateOf(false) }
    Box(Modifier.fillMaxSize()) {
        EqualButton(text = "Delete chat", onClick = { open = true }, variant = EqualButtonVariant.Danger, leadingIcon = EqualIcons.Delete)
        EqualBottomSheet(visible = open, onDismissRequest = { open = false }) {
            ConfirmSheetContent(
                title = "Delete chat with\nAmit Nanda",
                phone = "(+91 9871746113)",
                // Compose: body is required
                body = "All past conversations will be deleted. This action cannot be undone.",
                confirmLabel = "Delete",
                destructive = true,
                onConfirm = { open = false },
            )
        }
    }
}

// snippet: bottom-sheet / Full sheet
@Composable
internal fun BottomSheetFullSheetSnippet() {
    var open by remember { mutableStateOf(false) }
    FullSheet(
        visible = open,
        onDismissRequest = { open = false },
        title = "Setting up instruction",
        footer = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                LargeActionChip(label = "Edit", emoji = "✍️", onClick = {}, state = LargeActionChipState.Read)
                LargeActionChip(label = "Confirm", emoji = "👍", onClick = { open = false })
            }
        },
    ) {
        // Compose: no MessageList; stack bubbles in a Column
        Column(Modifier.padding(horizontal = 12.dp, vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ChatBubble(message = "Take a message & Callback request", timestamp = "03:21", type = ChatBubbleType.Outgoing, sender = "You")
            ChatBubble(message = "Got it! Here's your instruction:", timestamp = "03:21", type = ChatBubbleType.Assistant, sender = "Equal AI")
        }
    }
}

// snippet: dialog / Inline
@Composable
internal fun DialogInlineSnippet() {
    // EqualDialogContent is the dialog surface without the window, for inline use
    EqualDialogContent(
        title = "Turn off Call Learning?",
        subject = "Call learning",
        body = "Your assistant will stop learning from new calls. What it already knows stays until you clear it.",
        confirmLabel = "Turn off",
        onConfirm = {},
        onDismiss = {},
    )
}

// snippet: dialog / Destructive
@Composable
internal fun DialogDestructiveSnippet() {
    EqualDialogContent(
        title = "Delete address?",
        subject = "Work",
        body = "Your assistant will no longer share this address with delivery partners.",
        confirmLabel = "Delete",
        destructive = true,
        onConfirm = {},
        onDismiss = {},
    )
}

// snippet: dialog / Overlay + snackbar
@Composable
internal fun DialogOverlaySnackbarSnippet() {
    var open by remember { mutableStateOf(false) }
    var toast by remember { mutableStateOf<String?>(null) }
    EqualButton(text = "Turn off Call Learning", onClick = { toast = null; open = true })
    if (open) {
        EqualDialog(
            onDismissRequest = { open = false },
            title = "Turn off Call Learning?",
            subject = "Call learning",
            body = "Your assistant will stop learning from new calls. What it already knows stays until you clear it.",
            confirmLabel = "Turn off",
            onConfirm = { open = false; toast = "Call learning turned off" },
        )
    }
    toast?.let { EqualSnackbar(message = it, actionLabel = "Undo", onActionClick = { toast = null }) }
}

// snippet: dialog / Snackbars
@Composable
internal fun DialogSnackbarsSnippet() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        EqualSnackbar(message = "Contact saved")
        EqualSnackbar(message = "Chat deleted", actionLabel = "Undo", onActionClick = {})
    }
}

// snippet: context-menu / Chat long-press
@Composable
internal fun ContextMenuChatLongPressSnippet() {
    ContextMenu(
        items = listOf(
            ContextMenuItem("Mark as read", EqualIcons.DoneAll, onClick = {}),
            ContextMenuItem("Call back", EqualIcons.Call, onClick = {}),
            ContextMenuItem("Delete chat", EqualIcons.Delete, onClick = {}, destructive = true),
        ),
    )
}

// snippet: context-menu / Overflow menu
@Composable
internal fun ContextMenuOverflowSnippet() {
    OverflowMenu(
        items = listOf(
            ContextMenuItem("Edit", EqualIcons.Edit, onClick = {}),
            ContextMenuItem("Delete instruction", EqualIcons.Delete, onClick = {}, destructive = true),
        ),
    )
}

// snippet: settings-row / You tab group
@Composable
internal fun SettingsRowYouTabSnippet() {
    SectionHeader(title = "Your assistant")
    RowGroup {
        SettingsRow(
            label = "Instructions",
            description = "Tell assistant how you'd like it to help",
            leadingIcon = EqualIcons.Instructions,
            showNewBadge = true,
            onClick = {},
        )
        SettingsRow(label = "Assistant Health Check", description = "Check Real-Time Assistant Connectivity", leadingIcon = EqualIcons.HealthCheck, onClick = {})
        SettingsRow(label = "Silent Mode", description = "Mute promotional calls", leadingIcon = EqualIcons.SilentMode, onClick = {})
        SettingsRow(label = "Voice & Language", description = "How your assistant sounds", leadingIcon = EqualIcons.VoiceLanguage, onClick = {})
    }
}

// snippet: settings-row / Toggle rows
@Composable
internal fun SettingsRowToggleSnippet() {
    var mute by remember { mutableStateOf(true) }
    RowGroup {
        ToggleRow(
            label = "Mute Promotional Calls",
            description = "Equal AI Assistant can identify Promotional Calls and automatically mute them",
            descriptionWraps = true,
            checked = mute,
            onCheckedChange = { mute = it },
        )
    }
}

// snippet: settings-row / Trailing variants
@Composable
internal fun SettingsRowTrailingSnippet() {
    RowGroup {
        SettingsRow(label = "Active Instructions", description = "3 Active", onClick = {})
        SettingsRow(label = "Muted Contacts", onClick = {}, trailing = {
            EqualText(text = "4", style = EqualTheme.type.body1, color = EqualTheme.colors.textSecondary)
        })
        SettingsRow(label = "Version", description = "2.4.0", trailing = null) // null = no chevron
    }
}

// snippet: call-history / Directions
@Composable
internal fun CallHistoryDirectionsSnippet() {
    SectionHeader(title = "Call history", size = SectionHeaderSize.Label, inset = 22.dp)
    CallHistoryList {
        CallHistoryRow(CallDirection.Incoming, meta = "Today • 30 Sec.", time = "02:30 PM")
        CallHistoryRow(CallDirection.Missed, meta = "Yesterday", time = "11:04 AM")
        CallHistoryRow(CallDirection.Outgoing, meta = "12 July • 2 Min.", time = "06:15 PM")
    }
}

// snippet: call-history / With trailing
@Composable
internal fun CallHistoryWithTrailingSnippet() {
    CallHistoryList {
        // Compose: no custom trailing slot; onCallClick adds the only trailing button (call back, not play)
        CallHistoryRow(CallDirection.Incoming, meta = "Today • 2 Min.", time = "03:21 PM", onCallClick = {})
    }
}
