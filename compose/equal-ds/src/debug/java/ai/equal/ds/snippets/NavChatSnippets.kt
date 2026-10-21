// Compose code shown on the docs site (Navigation and Chat components, plus Assistant toggle).
// Each function body is extracted by scripts/build-snippets.mjs; keep the
// `// snippet: <doc id> / <example title>` line above it.
// Debug source set only: compiled with the module, never shipped in the release AAR.
@file:Suppress("unused")

package ai.equal.ds.snippets

import ai.equal.ds.assets.EqualCategory
import ai.equal.ds.components.ActionChipData
import ai.equal.ds.components.AiAvatar
import ai.equal.ds.components.AiAvatarSize
import ai.equal.ds.components.AiChatPanel
import ai.equal.ds.components.AiSuggestion
import ai.equal.ds.components.AppBarAlign
import ai.equal.ds.components.AssistantToggleCard
import ai.equal.ds.components.BottomNavBar
import ai.equal.ds.components.CallSummaryBubble
import ai.equal.ds.components.CallSummaryTag
import ai.equal.ds.components.ChatBubble
import ai.equal.ds.components.ChatBubbleType
import ai.equal.ds.components.ChatHeader
import ai.equal.ds.components.ChatHeaderVariant
import ai.equal.ds.components.ChatListItem
import ai.equal.ds.components.ChatListItemState
import ai.equal.ds.components.ChatWallpaper
import ai.equal.ds.components.DayDivider
import ai.equal.ds.components.EqualAppBar
import ai.equal.ds.components.EqualFab
import ai.equal.ds.components.EqualTab
import ai.equal.ds.components.EqualTextButton
import ai.equal.ds.components.HomeHeader
import ai.equal.ds.components.PlaybackSpeed
import ai.equal.ds.components.ProfileAvatarVariant
import ai.equal.ds.components.SectionHeader
import ai.equal.ds.components.SelectionChatHeader
import ai.equal.ds.components.TakeoverPill
import ai.equal.ds.components.ThreadEvent
import ai.equal.ds.components.ThreadNotice
import ai.equal.ds.components.VoiceNotePlayer
import ai.equal.ds.icons.EqualIcons
import ai.equal.ds.theme.EqualTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// ============================================================ App bar

// snippet: app-bar / Centred (flows)
@Composable
internal fun AppBarCentredSnippet() {
    EqualAppBar(title = "Personal Details", onBackClick = {})
}

// snippet: app-bar / Start-aligned, elevated, with trailing
@Composable
internal fun AppBarStartElevatedSnippet() {
    EqualAppBar(
        title = "Active Instructions",
        onBackClick = {},
        align = AppBarAlign.Start,
        background = EqualTheme.colors.bgSurface,
        elevated = true,
    ) {
        EqualTextButton(text = "Add", onClick = {}, leadingIcon = EqualIcons.Add)
    }
}

// snippet: app-bar / Section headers
@Composable
internal fun AppBarSectionHeadersSnippet() {
    // Pass normal text; SectionHeader uppercases it.
    SectionHeader(title = "Your assistant")
    // Compose: no `qualifier` slot; it is appended to the title (and uppercased with it).
    SectionHeader(title = "Guiding direction (Optional)")
    SectionHeader(title = "Recommended for you", actionLabel = "Clear all", onActionClick = {})
}

// ============================================================ Bottom nav

// snippet: bottom-nav / Interactive
@Composable
internal fun BottomNavInteractiveSnippet() {
    var tab by remember { mutableStateOf(EqualTab.Chats) }

    BottomNavBar(
        selectedTab = tab,
        onTabSelected = { tab = it },
        onFabClick = {},
        chatsBadgeCount = 7,
    )
}

// snippet: bottom-nav / You active, female avatar, no FAB
@Composable
internal fun BottomNavYouFemaleSnippet() {
    // Compose: BottomNavBar always draws the FAB; there is no showFab = false.
    BottomNavBar(
        selectedTab = EqualTab.You,
        onTabSelected = {},
        onFabClick = {},
        avatar = ProfileAvatarVariant.Female,
    )
}

// snippet: bottom-nav / FAB on its own
@Composable
internal fun BottomNavFabSnippet() {
    EqualFab(onClick = {})
}

// ============================================================ Chat header

// snippet: chat-header / Default
@Composable
internal fun ChatHeaderDefaultSnippet() {
    ChatHeader(
        name = "Amit Nanda",
        subtitle = "+91 98717 46113",
        initials = "AN",
        onBackClick = {},
        onCallClick = {},
        onMoreClick = {},
    )
}

// snippet: chat-header / Live call
@Composable
internal fun ChatHeaderLiveCallSnippet() {
    ChatHeader(
        name = "Arnav Raj",
        subtitle = "You have a visitor",
        subtitleIsPhone = false,
        variant = ChatHeaderVariant.LiveCall,
        onBackClick = {},
        onTakeoverClick = {},
    )
}

// snippet: chat-header / Selection mode
@Composable
internal fun ChatHeaderSelectionSnippet() {
    var allSelected by remember { mutableStateOf(false) }

    SelectionChatHeader(
        selectedCount = if (allSelected) 3 else 1,
        allSelected = allSelected,
        onSelectAllChange = { allSelected = it },
        onBackClick = {},
        onMarkReadClick = {},
        onDeleteClick = {},
    )
}

// snippet: chat-header / Home
@Composable
internal fun ChatHeaderHomeSnippet() {
    var on by remember { mutableStateOf(true) }

    HomeHeader(assistantOn = on, onAssistantChange = { on = it })
}

// snippet: chat-header / Takeover pill
@Composable
internal fun ChatHeaderTakeoverPillSnippet() {
    TakeoverPill(onClick = {})
}

// ============================================================ Chat list item

// snippet: chat-list-item / Unread and read
@Composable
internal fun ChatListItemUnreadReadSnippet() {
    ChatListItem(
        name = "Arnav Raj",
        preview = "You have a visitor",
        date = "Yesterday",
        state = ChatListItemState.Unread,
        unreadCount = 5,
        category = EqualCategory.NA,
        chips = listOf(
            ActionChipData("Call Back", "📞"),
            ActionChipData("Update Address", "✏️"),
            ActionChipData("Add to Calendar", "🗓️"),
        ),
    )
    ChatListItem(
        name = "Amit Nanda",
        preview = "Count me in for Sunday 8 PM 👍",
        date = "12 July",
        initials = "AN",
        chips = listOf(ActionChipData("Call Back", "📞")),
    )
}

// snippet: chat-list-item / Long-press to select
@Composable
internal fun ChatListItemLongPressSnippet() {
    var selected by remember { mutableStateOf(false) }

    ChatListItem(
        name = "Dr Mehta Clinic",
        preview = "Appointment confirmed for tomorrow, 10 AM",
        date = "03:21",
        state = if (selected) ChatListItemState.Selected else ChatListItemState.Unread,
        unreadCount = 2,
        category = EqualCategory.Others,
        chips = listOf(ActionChipData("Add to Calendar", "🗓️"), ActionChipData("Call Back", "📞")),
        onLongClick = { selected = true },
        onClick = { selected = false },
    )
}

// snippet: chat-list-item / The list
@Composable
internal fun ChatListItemListSnippet() {
    val callBack = listOf(ActionChipData("Call Back", "📞"))
    Column {
        ChatListItem(name = "Amit Nanda", preview = "Count me in for Sunday 8 PM 👍", date = "12 July",
            initials = "AN", chips = callBack)
        ChatListItem(name = "Vijay · NoBroker", preview = "Offer on a 2BHK flat in Sector 12", date = "10 July",
            category = EqualCategory.RealEstate, chips = callBack)
        ChatListItem(name = "Swiggy Delivery", preview = "Your order is at the gate", date = "09 July",
            category = EqualCategory.Delivery, chips = listOf(ActionChipData("Update Address", "✏️")))
        ChatListItem(name = "HDFC Bank", preview = "Pre-approved loan offer. You said not interested", date = "08 July",
            category = EqualCategory.Financial, chips = callBack)
    }
}

// ============================================================ Chat bubble

// snippet: chat-bubble / A call thread
@Composable
internal fun ChatBubbleCallThreadSnippet() {
    // Compose: no MessageList; a Column with 12dp spacing gives the between-group rhythm.
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        ChatBubble(type = ChatBubbleType.Outgoing, sender = "Equal AI", timestamp = "03:21", message = "Hi, who is this?")
        ChatBubble(type = ChatBubbleType.Incoming, sender = "Dr Mehta Clinic", timestamp = "03:21",
            message = "This is Priya from Dr. Mehta's clinic. Your appointment is confirmed for tomorrow.")
        ChatBubble(type = ChatBubbleType.Outgoing, sender = "You", timestamp = "03:21", message = "Yes, I'll be there at 10 AM")
        ChatBubble(type = ChatBubbleType.Incoming, sender = "Dr Mehta Clinic", timestamp = "03:21",
            message = "Great. Please bring your previous reports too.")
        ChatBubble(type = ChatBubbleType.Assistant, sender = "Equal AI", timestamp = "03:21",
            message = "Hey Keshav! Here is the summary of your call")
    }
}

// snippet: chat-bubble / Corners and grouping
@Composable
internal fun ChatBubbleTailsGroupingSnippet() {
    Column {
        ChatBubble(type = ChatBubbleType.Incoming, incomingCategory = EqualCategory.RealEstate,
            sender = "Vijay · NoBroker", timestamp = "03:21",
            message = "I have an offer on a 2BHK flat in Sector 12.")
        // Follow-up in the same group: 4dp gap, no avatar, no sender line.
        ChatBubble(type = ChatBubbleType.Incoming, incomingCategory = EqualCategory.RealEstate,
            isFirstInGroup = false, timestamp = "03:21", modifier = Modifier.padding(top = 4.dp),
            message = "Are you free for a site visit this weekend?")
        ChatBubble(type = ChatBubbleType.Outgoing, sender = "Equal AI", timestamp = "03:22",
            modifier = Modifier.padding(top = 12.dp), message = "Thanks, but Keshav is not interested right now.")
    }
}

// snippet: chat-bubble / Bare content
@Composable
internal fun ChatBubbleBareContentSnippet() {
    // Compose: ChatBubble has no `bare` mode; lay the AI avatar beside the card yourself.
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        AiAvatar(size = AiAvatarSize.Small)
        CallSummaryBubble(
            summary = "Delivery agent will arrive between 4 and 5 PM.",
            timestamp = "03:21",
            modifier = Modifier.width(240.dp),
        )
    }
}

// ============================================================ Call summary

// snippet: call-summary / With recording and share
@Composable
internal fun CallSummaryRecordingShareSnippet() {
    var playing by remember { mutableStateOf(false) }
    var speed by remember { mutableStateOf(PlaybackSpeed.X1) }

    CallSummaryBubble(
        summary = "Amit invited you to dinner this Sunday, 8 PM at his place. He asked you to confirm by tonight.",
        timestamp = "03:21",
        voiceNote = {
            // Compose: the player is controlled; drive progress/elapsed from your media player.
            VoiceNotePlayer(isPlaying = playing, onPlayPauseClick = { playing = !playing },
                progress = 0f, elapsed = "0:00", total = "2:37",
                speed = speed, onSpeedChange = { speed = it })
        },
        onShareClick = {},
    )
}

// snippet: call-summary / Instruction applied
@Composable
internal fun CallSummaryInstructionAppliedSnippet() {
    CallSummaryBubble(
        summary = "Priya from Dr. Mehta's clinic confirmed your appointment for tomorrow at 10 AM. Bring your previous reports.",
        timestamp = "03:32",
        tag = CallSummaryTag(label = "2 instructions applied in this call", emoji = "🪄"),
        modifier = Modifier.width(264.dp),
    )
}

// snippet: call-summary / Summary only
@Composable
internal fun CallSummarySummaryOnlySnippet() {
    // No voiceNote slot = no recording player.
    CallSummaryBubble(
        summary = "Vijay from NoBroker called with an offer on a 2BHK flat in Sector 12. You said you were not interested and ended the call.",
        timestamp = "03:21",
    )
}

// snippet: call-summary / Voice player
@Composable
internal fun CallSummaryVoicePlayerSnippet() {
    var playing by remember { mutableStateOf(false) }
    var speed by remember { mutableStateOf(PlaybackSpeed.X1) }

    // Compose: no `seed`; pass `samples` for a recording-specific waveform (defaults are fixed).
    VoiceNotePlayer(
        isPlaying = playing,
        onPlayPauseClick = { playing = !playing },
        progress = 0f,
        elapsed = "0:00",
        total = "1:34",
        speed = speed,
        onSpeedChange = { speed = it },
    )
}

// ============================================================ Call status

// snippet: call-status / All indicators
@Composable
internal fun CallStatusAllIndicatorsSnippet() {
    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        DayDivider("Today")
        ThreadEvent("Call Started 03:31")
        ThreadEvent("Call Ended 03:31")
        ThreadNotice("Call has ended. You are now talking to your AI assistant.")
    }
}

// snippet: call-status / In a thread
@Composable
internal fun CallStatusInThreadSnippet() {
    ChatWallpaper {
        Column(
            Modifier.fillMaxWidth().padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            DayDivider("12 July")
            ThreadEvent("Call Started 18:02")
            ChatBubble(type = ChatBubbleType.Outgoing, sender = "Equal AI", timestamp = "18:02", message = "Hi, who is this?")
            ChatBubble(type = ChatBubbleType.Incoming, incomingCategory = EqualCategory.NA, sender = "Amit Nanda",
                timestamp = "18:02", message = "It's Amit. Dinner at mine this Sunday, 8 PM?")
            ThreadEvent("Call Ended 18:04")
            ThreadNotice("Call has ended. You are now talking to your AI assistant.")
        }
    }
}

// ============================================================ AI panel

// snippet: ai-panel / After a call
@Composable
internal fun AiPanelAfterCallSnippet() {
    var draft by remember { mutableStateOf("") }

    AiChatPanel(
        prompt = "Want me to send Amit a quick reply on WhatsApp?",
        actions = listOf(
            ActionChipData("Reply on WhatsApp", "💬"),
            ActionChipData("Add to Calendar", "🗓️"),
        ),
        onActionClick = {},
        composerValue = draft,
        onComposerValueChange = { draft = it },
        onSend = {},
    )
}

// snippet: ai-panel / During a live call, no composer
@Composable
internal fun AiPanelLiveCallSnippet() {
    var draft by remember { mutableStateOf("") }

    // Compose: AiChatPanel always shows the composer; there is no composer = false.
    AiChatPanel(
        prompt = "Arnav is at the gate with a parcel. Want to take over?",
        actions = listOf(
            ActionChipData("Takeover", "📞"),
            ActionChipData("Ask to reschedule", "🗓️"),
        ),
        onActionClick = {},
        composerValue = draft,
        onComposerValueChange = { draft = it },
        onSend = {},
    )
}

// snippet: ai-panel / AI suggestion
@Composable
internal fun AiPanelSuggestionSnippet() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        AiSuggestion("Want me to send Amit a quick reply on WhatsApp?")
        // Compose: no `tone`; AiSuggestion is always text/primary.
        AiSuggestion("I can also add Sunday dinner to your calendar.")
    }
}

// ============================================================ Chat wallpaper

// snippet: chat-wallpaper / Behind a thread
@Composable
internal fun ChatWallpaperBehindThreadSnippet() {
    ChatWallpaper(Modifier.fillMaxSize()) {
        Column(
            Modifier.fillMaxWidth().padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            DayDivider("Today")
            ChatBubble(type = ChatBubbleType.Outgoing, sender = "Equal AI", timestamp = "03:21", message = "Hi, who is this?")
            ChatBubble(type = ChatBubbleType.Incoming, sender = "Dr Mehta Clinic", timestamp = "03:21",
                message = "This is Priya from Dr. Mehta's clinic. Your appointment is confirmed for tomorrow.")
        }
    }
}

// ============================================================ Assistant toggle

// snippet: assistant-toggle / Interactive
@Composable
internal fun AssistantToggleInteractiveSnippet() {
    var on by remember { mutableStateOf(true) }
    AssistantToggleCard(checked = on, onCheckedChange = { on = it })
}

// snippet: assistant-toggle / On and off
@Composable
internal fun AssistantToggleOnOffSnippet() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        AssistantToggleCard(checked = true, onCheckedChange = {})
        AssistantToggleCard(checked = false, onCheckedChange = {})
    }
}
