package ai.equal.ds.catalog

import ai.equal.ds.assets.EqualCategory
import ai.equal.ds.components.ActionCard
import ai.equal.ds.components.ActionCardAction
import ai.equal.ds.components.ActionCardBody
import ai.equal.ds.components.ActionCardLine
import ai.equal.ds.components.ActionCardTone
import ai.equal.ds.components.ActionChipData
import ai.equal.ds.components.AiChatPanel
import ai.equal.ds.components.AiTurn
import ai.equal.ds.components.AppBarAlign
import ai.equal.ds.components.AssistantSheetShell
import ai.equal.ds.components.AssistantTaskHeader
import ai.equal.ds.components.AssistantThinking
import ai.equal.ds.components.CallFeedback
import ai.equal.ds.components.CallSummaryBubble
import ai.equal.ds.components.ChatBubble
import ai.equal.ds.components.ChatBubbleType
import ai.equal.ds.components.ChatHeader
import ai.equal.ds.components.ChatWallpaper
import ai.equal.ds.components.DialerStandIn
import ai.equal.ds.components.EditBubble
import ai.equal.ds.components.EditDimColor
import ai.equal.ds.components.EqualAppBar
import ai.equal.ds.components.EqualFrameHeightDp
import ai.equal.ds.components.EqualFrameWidthDp
import ai.equal.ds.components.FeedbackCard
import ai.equal.ds.components.HoldToSpeakBar
import ai.equal.ds.components.KeyboardSpace
import ai.equal.ds.components.LargeActionChip
import ai.equal.ds.components.MissedCallCard
import ai.equal.ds.components.NewContactForm
import ai.equal.ds.components.NewContactValue
import ai.equal.ds.components.NewContactVariant
import ai.equal.ds.components.OfflineScreen
import ai.equal.ds.components.OfflineSheet
import ai.equal.ds.components.PermissionDialog
import ai.equal.ds.components.PlaybackSpeed
import ai.equal.ds.components.ReplyAction
import ai.equal.ds.components.SampleMissedReason
import ai.equal.ds.components.StatusBar
import ai.equal.ds.components.SummaryAction
import ai.equal.ds.components.SummaryActions
import ai.equal.ds.components.ThreadEvent
import ai.equal.ds.components.VoiceInputBar
import ai.equal.ds.components.VoiceNotePlayer
import ai.equal.ds.icons.EqualIcons
import ai.equal.ds.theme.EqualTheme
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay

// Chat action flows (Flows/Call Back, Mute, Reply On WhatsApp, Save Contact, Error states).
// One engine: a chat with its AI layer; tapping an action raises the task sheet (it stops below the
// chat header), the assistant asks or drafts, the user confirms, and the result lands in the chat
// as an action card. Every step can be pinned for docs; otherwise the flow runs live.
// Mirrors web/src/screens/ActionFlows.tsx.

/** The action a [ChatActionScreen] carries out. */
enum class ChatAction {
    /** Steps: chat, ask, dialer, done. */
    CallBack,

    /** Steps: chat, confirm, done. */
    Mute,

    /** Steps: chat, draft, sending, edit, edit-long, done. */
    ReplyWhatsApp,

    /** Steps: chat, review, edit-ask, form, form-outlined, saved, no-permission, resume, granted, denied. */
    SaveContact,
}

/** Call Back can start from a missed call: we know why they call, only their name, or nothing. */
enum class MissedCase { Reason, Name, Unknown }

/** The error states of the AI layer ([ChatErrorScreen]). */
enum class ErrorState { Generation, OfflineReply, Voice, OfflineSheet, OfflineScreen }

private data class ActionContact(val name: String, val phone: String, val category: EqualCategory, val company: String)

private fun contactFor(action: ChatAction): ActionContact = when (action) {
    ChatAction.CallBack -> ActionContact("Dr. Mehta Clinic", "+91 98717 46113", EqualCategory.Others, "Dr. Mehta’s Clinic")
    ChatAction.Mute, ChatAction.ReplyWhatsApp -> ActionContact("Amit Nanda", "+91-81720 33604", EqualCategory.NA, "Amit")
    ChatAction.SaveContact -> ActionContact("Rahul", "+91 98717 46113", EqualCategory.NA, "Rahul")
}

private fun missedContact(case: MissedCase): ActionContact = when (case) {
    MissedCase.Reason -> ActionContact("Kuldeep Solanki", "+91 98450 22781", EqualCategory.RealEstate, "Kuldeep Solanki")
    MissedCase.Name -> ActionContact("Valluri", "+91 99001 38452", EqualCategory.NA, "Valluri")
    MissedCase.Unknown -> ActionContact("+91 80 4681 0750", "", EqualCategory.NA, "this number")
}

private const val NoBrokerSummary = "Vijay from NoBroker called with an offer on a 2BHK flat in Sector 12. You said you were not interested and ended the call."
private const val DinnerSummary = "Amit invited you to dinner this Sunday, 8 PM at his place. He asked you to confirm by tonight."

private fun summaryFor(action: ChatAction): String = when (action) {
    ChatAction.Mute, ChatAction.ReplyWhatsApp -> DinnerSummary
    else -> NoBrokerSummary
}

private fun promptFor(action: ChatAction): String = when (action) {
    ChatAction.CallBack -> "Want me to give a quick call back to the clinic?"
    ChatAction.Mute, ChatAction.ReplyWhatsApp -> "Want me to send Amit a quick reply on WhatsApp?"
    ChatAction.SaveContact -> "This number isn’t saved. Add Rahul to your contacts?"
}

private const val Draft = "Hi Amit! Count me in for Sunday 8 PM. See you there 👍"

/** Scroll state that follows the end, so a chat or task opens at its latest message. */
@Composable
private fun rememberScrollToEnd(key: Any?): ScrollState {
    val scroll = rememberScrollState()
    LaunchedEffect(key, scroll.maxValue) { scroll.scrollTo(scroll.maxValue) }
    return scroll
}

/** The message column of a thread: padding 12, gap 8. */
@Composable
private fun MessageList(scroll: ScrollState, content: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(scroll)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        content = content,
    )
}

@Composable
private fun CenteredEvent(label: String) {
    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { ThreadEvent(label) }
}

/** The call history every action starts from: summary (recording, text, Call Back) and the rating card. */
@Composable
private fun CallHistory(action: ChatAction) {
    var playing by remember { mutableStateOf(false) }
    var speed by remember { mutableStateOf(PlaybackSpeed.X1) }
    var rating by remember { mutableIntStateOf(0) }
    CenteredEvent("Call Ended 4:37 PM")
    ChatBubble(message = "Hey Keshav! Here is the summary of your call", timestamp = "03:21", type = ChatBubbleType.Assistant, sender = "Equal AI")
    CallSummaryBubble(
        summary = summaryFor(action),
        timestamp = "03:21",
        modifier = Modifier.padding(start = 36.dp),
        voiceNote = {
            VoiceNotePlayer(
                isPlaying = playing,
                onPlayPauseClick = { playing = !playing },
                progress = 0f,
                elapsed = "0:00",
                total = "2:37",
                speed = speed,
                onSpeedChange = { speed = it },
            )
        },
        footer = { SummaryActions(actions = listOf(SummaryAction("Call Back", EqualIcons.CallFilled))) },
    )
    FeedbackCard(
        question = "How would you rate the call?",
        rating = rating,
        onRatingChange = { rating = it },
        onShareFeedbackClick = {},
        modifier = Modifier.padding(start = 36.dp),
    )
}

/** The thread when the assistant missed the call: the missed-call card (Call back) and, with a reason, thumbs. */
@Composable
private fun MissedThread(case: MissedCase, onCallBack: () -> Unit) {
    var feedback by remember { mutableStateOf<CallFeedback?>(null) }
    CenteredEvent("Missed Call 3:31 PM")
    Box(Modifier.padding(start = 36.dp)) {
        if (case == MissedCase.Reason) {
            MissedCallCard(
                onCallBack = onCallBack,
                caller = "Kuldeep Solanki",
                category = EqualCategory.RealEstate,
                reason = SampleMissedReason,
                time = "03:21",
                feedback = feedback,
                onFeedback = { feedback = it },
                width = 290.dp,
            )
        } else {
            MissedCallCard(onCallBack = onCallBack, caller = if (case == MissedCase.Name) "Valluri" else null, time = "3:31 PM", width = 290.dp)
        }
    }
}

private data class Chip(val emoji: String, val label: String, val onClick: () -> Unit = {})

@Composable
private fun Chips(items: List<Chip>, enabled: Boolean = true) {
    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items.forEach { LargeActionChip(label = it.label, emoji = it.emoji, enabled = enabled, onClick = it.onClick) }
    }
}

/** The task composer: a [VoiceInputBar] holding its own text. */
@Composable
private fun TaskComposer() {
    var message by remember { mutableStateOf("") }
    VoiceInputBar(value = message, onValueChange = { message = it }, onSend = { message = "" })
}

/** The AI layer's task sheet: in-chat, opening scrolled to its latest message. */
@Composable
private fun TaskSheet(
    title: String,
    scrollKey: Any?,
    onClose: () -> Unit,
    footer: @Composable () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    AssistantSheetShell(
        task = true,
        inChat = true,
        onScrim = onClose,
        header = { AssistantTaskHeader(title = title, onClose = onClose) },
        footer = { Column(verticalArrangement = Arrangement.spacedBy(12.dp)) { footer() } },
    ) {
        ChatWallpaper(Modifier.fillMaxWidth().weight(1f)) {
            MessageList(rememberScrollToEnd(scrollKey), content)
        }
    }
}

@Composable
private fun You(text: String) {
    ChatBubble(message = text, timestamp = "03:21", type = ChatBubbleType.Outgoing, sender = "You")
}

@Composable
private fun Indented(content: @Composable () -> Unit) {
    Box(Modifier.padding(start = 36.dp)) { content() }
}

/** A status bar + chat header column (shadow over the content). */
@Composable
private fun ChatTop(name: String, subtitle: String?, category: EqualCategory, onBack: () -> Unit) {
    Column(Modifier.zIndex(1f)) {
        StatusBar(background = EqualTheme.colors.bgSurface)
        ChatHeader(name = name, subtitle = subtitle?.takeIf { it.isNotEmpty() }, category = category, onBackClick = onBack)
    }
}

/**
 * A chat whose AI layer carries out one [action] end to end. Pin [step] (the web step keys: `chat`,
 * `ask`, `dialer`, `done`, `confirm`, `draft`, `sending`, `edit`, `edit-long`, `review`,
 * `edit-ask`, `form`, `form-outlined`, `saved`, `no-permission`, `resume`, `granted`, `denied`)
 * for docs; with `null` it runs live from `chat`: the AI panel's chip raises the task sheet, its
 * action rows move on, Send goes through "Opening WhatsApp" (1.4s) to done, and closing returns to
 * the chat. Chats and sheets open scrolled to the latest message.
 *
 * [missed] (Call Back only) starts the thread from a [MissedCallCard] instead of a call summary;
 * its Call back opens the ask step. [keyboard] shows the [KeyboardSpace] stand-in on the pinned
 * contact form; the pinned edit step always shows it, the live one lifts with `imePadding()`.
 * Hand-offs (dialer, Settings, WhatsApp) belong to the phone: the dialer step is a docs stand-in.
 */
@Composable
fun ChatActionScreen(
    action: ChatAction,
    modifier: Modifier = Modifier,
    step: String? = null,
    keyboard: Boolean = false,
    missed: MissedCase? = null,
    onBack: () -> Unit = {},
) {
    var live by remember(action, missed) { mutableStateOf("chat") }
    var text by remember { mutableStateOf(Draft) }
    val pinned = step != null
    val s = step ?: live
    val go: (String) -> Unit = { if (!pinned) live = it }
    val close = { go("chat") }
    val missedCase = if (action == ChatAction.CallBack) missed else null
    val c = if (missedCase != null) missedContact(missedCase) else contactFor(action)

    LaunchedEffect(s, pinned) {
        if (!pinned && s == "sending") {
            delay(1400)
            live = "done"
        }
    }

    val history: @Composable () -> Unit = {
        if (missedCase != null) MissedThread(missedCase, onCallBack = { go("ask") }) else CallHistory(action)
    }

    // ------------------------------------------------------------ full-screen steps
    if (action == ChatAction.CallBack && s == "dialer") {
        DialerStandIn(number = if (missedCase != null) c.phone.ifEmpty { c.name } else "+91 80 4681 0750", modifier = modifier, onEnd = { go("done") })
        return
    }
    if (action == ChatAction.SaveContact && (s == "form" || s == "form-outlined")) {
        Column(modifier.fillMaxSize().background(EqualTheme.colors.bgPrimary)) {
            ChatTop(name = "+91-99536 72604", subtitle = null, category = EqualCategory.NA, onBack = close)
            BoxWithConstraints(Modifier.weight(1f).fillMaxWidth()) {
                val min = maxHeight
                Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                    NewContactForm(
                        value = NewContactValue(first = "Rahul (Electrician)", phone = "+91 98717 46113", sync = s == "form-outlined"),
                        variant = if (s == "form") NewContactVariant.Grouped else NewContactVariant.Outlined,
                        modifier = Modifier.heightIn(min = min),
                        onClose = close,
                        onSave = { go("saved") },
                    )
                }
            }
            if (keyboard) KeyboardSpace()
        }
        return
    }
    if (action == ChatAction.ReplyWhatsApp && (s == "edit" || s == "edit-long")) {
        val value = if (s == "edit-long" && pinned) List(11) { "Hi Amit! Count me in for Sunday 8" }.joinToString(" ") else text
        Column(modifier.fillMaxSize().background(EqualTheme.colors.bgPrimary)) {
            EqualAppBar(title = "Edit Message", onBackClick = { go("draft") }, align = AppBarAlign.Start, statusBar = true, modifier = Modifier.zIndex(1f))
            Box(Modifier.weight(1f).fillMaxWidth()) {
                ChatWallpaper(Modifier.fillMaxSize()) {
                    MessageList(rememberScrollToEnd(s)) { history() }
                }
                Box(Modifier.matchParentSize().background(EditDimColor))
            }
            EditBubble(
                value = value,
                onValueChange = { text = it },
                onDone = { go("draft") },
                autoFocus = !pinned,
                modifier = if (pinned) Modifier else Modifier.imePadding(),
            )
            if (pinned) KeyboardSpace()
        }
        return
    }

    // ------------------------------------------------------------ the chat
    val chips: List<Chip> = when (action) {
        ChatAction.CallBack -> listOf(Chip("📞", "Call Back") { go("ask") }, Chip("🗓️", "Add to Calendar"), Chip("🔔", "Set reminder"))
        ChatAction.Mute -> listOf(Chip("🔕", "Mute") { go("confirm") }, Chip("💬", "Reply on WhatsApp"), Chip("🗓️", "Add to Calendar"))
        ChatAction.ReplyWhatsApp -> listOf(Chip("💬", "Reply on WhatsApp") { go("draft") }, Chip("🗓️", "Add to Calendar"))
        ChatAction.SaveContact -> listOf(Chip("👤", "Save contact") { go("review") }, Chip("🗓️", "Add to Calendar"))
    }
    var message by remember { mutableStateOf("") }
    Box(modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().background(EqualTheme.colors.bgPrimary)) {
            ChatTop(name = c.name, subtitle = c.phone, category = c.category, onBack = onBack)
            ChatWallpaper(Modifier.weight(1f).fillMaxWidth()) {
                MessageList(rememberScrollToEnd(s)) {
                    history()
                    Results(action, s, c, text, missedCase != null, go)
                }
            }
            AiChatPanel(
                prompt = if (missedCase != null) "The assistant missed this call. Want to call back?" else promptFor(action),
                actions = chips.map { ActionChipData(it.label, it.emoji) },
                onActionClick = { chip -> chips.firstOrNull { it.label == chip.label }?.onClick?.invoke() },
                composerValue = message,
                onComposerValueChange = { message = it },
                onSend = { message = "" },
                modifier = Modifier.navigationBarsPadding(),
            )
        }
        Overlay(action, s, c, text, missedCase, go, close)
    }
}

/** The result the action leaves in the chat (an action card), or the resume prompt. */
@Composable
private fun Results(action: ChatAction, s: String, c: ActionContact, text: String, missed: Boolean, go: (String) -> Unit) {
    when {
        action == ChatAction.CallBack && s == "done" -> AiTurn(
            text = "",
            width = 290.dp,
            actions = listOf(ReplyAction(label = "Call back", emoji = "📞")),
        ) {
            ActionCardBody(
                title = "Outgoing call",
                icon = EqualIcons.CallFilled,
                lines = buildList {
                    add(ActionCardLine("You called back ${c.company}", ActionCardTone.Primary))
                    if (!missed) add(ActionCardLine("Bring previous medical reports"))
                },
                modifier = Modifier.padding(top = 4.dp),
            )
        }
        action == ChatAction.Mute && s == "done" -> {
            AiTurn(text = "This caller has been muted", width = 220.dp)
            Indented {
                ActionCard(
                    title = "${c.phone} muted",
                    icon = EqualIcons.SilentMode,
                    lines = listOf(ActionCardLine("Calls from this caller will be muted")),
                    actions = listOf(ActionCardAction("Open muted caller list", "📋", onClick = {})),
                    modifier = CardWidth,
                )
            }
        }
        action == ChatAction.ReplyWhatsApp && s == "done" -> {
            AiTurn(text = "Wrote a draft for Amit", width = 200.dp)
            Indented {
                ActionCard(
                    title = "WhatsApp · Amit Nanda",
                    quote = "“${if (text.length > 34) "Count me in for Sunday 8 PM 👍" else text}”",
                    actions = listOf(ActionCardAction("View message", "👀", onClick = {})),
                    modifier = CardWidth,
                )
            }
        }
        action == ChatAction.SaveContact && s == "saved" -> {
            AiTurn(text = "Saved! Rahul is in your contacts now.", width = 260.dp)
            Indented { ContactCard(c.phone, listOf(ActionCardAction("View contact", "👤", onClick = {}))) }
        }
        action == ChatAction.SaveContact && s == "resume" -> AiTurn(
            text = "You stopped between saving this contact. Want to continue?",
            width = 260.dp,
            actions = listOf(ReplyAction(label = "Continue saving", emoji = "▶️", onClick = { go("granted") })),
        )
    }
}

/** Result cards are 280 wide. */
private val CardWidth = Modifier.width(280.dp)

@Composable
private fun ContactCard(phone: String, actions: List<ActionCardAction>) {
    ActionCard(
        title = "Rahul (Electrician)",
        icon = EqualIcons.Person,
        lines = listOf(ActionCardLine(phone)),
        actions = actions,
        modifier = CardWidth,
    )
}

private val SendChips = listOf(Chip("📤", "Send"), Chip("✏️", "Edit draft"), Chip("🖋️", "Custom"))

/** The AI layer's task sheet for the step, if any (plus the permission dialog). */
@Composable
private fun Overlay(
    action: ChatAction,
    s: String,
    c: ActionContact,
    text: String,
    missed: MissedCase?,
    go: (String) -> Unit,
    close: () -> Unit,
) {
    when {
        action == ChatAction.CallBack && s == "ask" -> TaskSheet(
            title = "Calling ${c.company}",
            scrollKey = s,
            onClose = close,
            footer = { Chips(SendChips); TaskComposer() },
        ) {
            You("Call Back")
            AiTurn(
                text = when (missed) {
                    MissedCase.Reason -> "Kuldeep usually calls to promote L&T Realty residential projects. Want to call back anyway?"
                    null -> "Try asking the following:\n\n•  what reports to bring\n•  confirm location or parking\n•  consultation charge\n•  insurance coverage"
                    else -> "I couldn’t pick this call, so I don’t know what it was about. Call back to find out?"
                },
                actions = listOf(ReplyAction(label = "Call Back", emoji = "📞", onClick = { go("dialer") })),
            )
        }
        action == ChatAction.Mute && s == "confirm" -> TaskSheet(
            title = "Muting ${c.name}",
            scrollKey = s,
            onClose = close,
            footer = { Chips(SendChips); TaskComposer() },
        ) {
            You("Mute")
            AiTurn(
                text = "Are you sure? You’ll not get any call from them even if it’s important",
                actions = listOf(ReplyAction(label = "Mute", emoji = "🔕", onClick = { go("done") })),
            )
        }
        action == ChatAction.ReplyWhatsApp && (s == "draft" || s == "sending") -> TaskSheet(
            title = "Replying Amit on WhatsApp",
            scrollKey = s,
            onClose = close,
            footer = { Chips(SendChips, enabled = s != "sending"); TaskComposer() },
        ) {
            You("Reply on whatsapp")
            AiTurn(
                text = "Sure! Here’s a draft for Amit:\n\n$text",
                actions = listOf(
                    ReplyAction(label = "Edit", emoji = "✏️", onClick = { go("edit") }),
                    ReplyAction(label = "Send", emoji = "📤", onClick = { go("sending") }),
                ),
            )
            if (s == "sending") {
                You("send as is")
                AssistantThinking(label = "Opening WhatsApp")
            }
        }
        action == ChatAction.SaveContact && s in setOf("review", "edit-ask", "granted", "denied", "no-permission") -> {
            val voice = s == "edit-ask"
            TaskSheet(
                title = "Saving contact",
                scrollKey = s,
                onClose = close,
                footer = { Chips(SendChips); if (voice) HoldToSpeakBar() else TaskComposer() },
            ) {
                when (s) {
                    "review", "edit-ask" -> {
                        You("Save contact")
                        AiTurn(text = "Here you go", width = 110.dp)
                        Indented {
                            ContactCard(
                                c.phone,
                                listOf(ActionCardAction("Edit", "✏️", onClick = { go("edit-ask") }), ActionCardAction("Save", "💾", onClick = { go("saved") })),
                            )
                        }
                        AiTurn(text = "Looks right?\nTap Save to add them.", width = 220.dp)
                        if (voice) {
                            You("Edit")
                            AiTurn(text = "What should I change?", width = 200.dp)
                        }
                    }
                    "granted" -> {
                        You("Continue Saving")
                        AiTurn(text = "Here’s the contact, pre-filled from the call:", width = 240.dp)
                        Indented {
                            ContactCard(
                                c.phone,
                                listOf(ActionCardAction("Edit", "✏️", onClick = { go("form") }), ActionCardAction("Confirm", "👍", onClick = { go("saved") })),
                            )
                        }
                        AiTurn(text = "Does this look right? Tap Confirm to save, or let me know what to change", width = 260.dp)
                    }
                    else -> {
                        You(if (s == "denied") "Continue Saving" else "Save contact")
                        AiTurn(text = "To save a contact, I need contact permission on your phone", width = 260.dp)
                        Indented {
                            ActionCard(
                                title = "Contact Permission Required",
                                icon = EqualIcons.PersonAdd,
                                lines = listOf(ActionCardLine("Allow Equal AI to create and manage known contacts")),
                                actions = listOf(ActionCardAction("Open Settings", "⚙️", onClick = { go("resume") })),
                                modifier = CardWidth,
                            )
                        }
                    }
                }
            }
            if (s == "no-permission") {
                PermissionDialog(
                    title = "Allow contacts permission",
                    body = "I need your contacts permission to save contacts into your phone",
                    cta = "Open Settings",
                    onAction = { go("resume") },
                    onClose = close,
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Error states
// ---------------------------------------------------------------------------------------------

/**
 * How the AI layer fails: in a task ([ErrorState.Generation], [ErrorState.OfflineReply]: 🔄 Retry
 * in the task sheet over Hold to speak), on voice input ([ErrorState.Voice]: the AI panel asks
 * again), offline over a chat ([ErrorState.OfflineSheet]: Refresh) or with nothing to show yet
 * ([ErrorState.OfflineScreen]: Try again). [onRetry] is the one action that fixes it.
 */
@Composable
fun ChatErrorScreen(
    state: ErrorState,
    modifier: Modifier = Modifier,
    onRetry: () -> Unit = {},
) {
    val colors = EqualTheme.colors
    if (state == ErrorState.OfflineScreen) {
        Column(modifier.fillMaxSize().background(colors.bgPrimary)) {
            StatusBar(background = Color.Transparent)
            OfflineScreen(modifier = Modifier.weight(1f), onRetry = onRetry)
        }
        return
    }
    if (state == ErrorState.Generation || state == ErrorState.OfflineReply) {
        Box(modifier.fillMaxSize()) {
            Column(Modifier.fillMaxSize().background(colors.bgPrimary)) {
                ChatTop(name = "+91-99536 72604", subtitle = null, category = EqualCategory.NA, onBack = {})
                ChatWallpaper(Modifier.weight(1f).fillMaxWidth()) {
                    MessageList(rememberScrollState()) {
                        CenteredEvent("Call Ended 4:37 PM")
                        ChatBubble(message = "Hey Keshav! Here is the summary of your call", timestamp = "03:21", type = ChatBubbleType.Assistant, sender = "Equal AI")
                    }
                }
            }
            TaskSheet(
                title = "Mute promotional calls from +91-99536 72604",
                scrollKey = state,
                onClose = {},
                footer = {
                    Chips(listOf(Chip("✅", "Add event"), Chip("✏️", "Edit details"), Chip("🖋️", "Custom")))
                    HoldToSpeakBar()
                },
            ) {
                if (state == ErrorState.Generation) You("Mute")
                AiTurn(
                    text = if (state == ErrorState.Generation) "Sorry, I couldn’t do that just now. Please try again." else "It looks like you’re offline.\nTry again once you’re back online.",
                    width = 260.dp,
                    actions = listOf(ReplyAction(label = "Retry", emoji = "🔄", onClick = onRetry)),
                )
            }
        }
        return
    }
    var message by remember { mutableStateOf("") }
    Box(modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().background(colors.bgPrimary)) {
            ChatTop(name = "Rahul", subtitle = "+91 98717 46113", category = EqualCategory.NA, onBack = {})
            ChatWallpaper(Modifier.weight(1f).fillMaxWidth()) {
                MessageList(rememberScrollToEnd(state)) {
                    CallHistory(ChatAction.SaveContact)
                    AiTurn(text = "Saved! Rahul is in your contacts now.", width = 260.dp)
                    Indented { ContactCard("+91 98717 46113", listOf(ActionCardAction("View contact", "👤", onClick = {}))) }
                }
            }
            if (state == ErrorState.Voice) {
                AiChatPanel(
                    prompt = "Sorry, I didn’t catch that. Could you hold and speak again?",
                    actions = listOf(ActionChipData("Save contact", "👤"), ActionChipData("Add to Calendar", "🗓️")),
                    onActionClick = {},
                    composerValue = message,
                    onComposerValueChange = { message = it },
                    onSend = { message = "" },
                    modifier = Modifier.navigationBarsPadding(),
                )
            }
        }
        if (state == ErrorState.OfflineSheet) OfflineSheet(onRefresh = onRetry)
    }
}

// ---------------------------------------------------------------------------------------------
// Previews
// ---------------------------------------------------------------------------------------------

@Preview(name = "Call back · ask · light", widthDp = EqualFrameWidthDp, heightDp = EqualFrameHeightDp, showBackground = true)
@Composable
private fun CallBackAskLightPreview() {
    EqualTheme(darkTheme = false) { ChatActionScreen(action = ChatAction.CallBack, step = "ask") }
}

@Preview(name = "Call back · missed · dark", widthDp = EqualFrameWidthDp, heightDp = EqualFrameHeightDp, showBackground = true)
@Composable
private fun CallBackMissedDarkPreview() {
    EqualTheme(darkTheme = true) { ChatActionScreen(action = ChatAction.CallBack, missed = MissedCase.Reason) }
}

@Preview(name = "Mute · done · dark", widthDp = EqualFrameWidthDp, heightDp = EqualFrameHeightDp, showBackground = true)
@Composable
private fun MuteDoneDarkPreview() {
    EqualTheme(darkTheme = true) { ChatActionScreen(action = ChatAction.Mute, step = "done") }
}

@Preview(name = "WhatsApp · edit · light", widthDp = EqualFrameWidthDp, heightDp = EqualFrameHeightDp, showBackground = true)
@Composable
private fun WhatsAppEditLightPreview() {
    EqualTheme(darkTheme = false) { ChatActionScreen(action = ChatAction.ReplyWhatsApp, step = "edit") }
}

@Preview(name = "Save contact · no permission · dark", widthDp = EqualFrameWidthDp, heightDp = EqualFrameHeightDp, showBackground = true)
@Composable
private fun SaveNoPermissionDarkPreview() {
    EqualTheme(darkTheme = true) { ChatActionScreen(action = ChatAction.SaveContact, step = "no-permission") }
}

@Preview(name = "Save contact · form outlined · light", widthDp = EqualFrameWidthDp, heightDp = EqualFrameHeightDp, showBackground = true)
@Composable
private fun SaveFormLightPreview() {
    EqualTheme(darkTheme = false) { ChatActionScreen(action = ChatAction.SaveContact, step = "form-outlined", keyboard = true) }
}

@Preview(name = "Errors · offline sheet · light", widthDp = EqualFrameWidthDp, heightDp = EqualFrameHeightDp, showBackground = true)
@Composable
private fun ErrorOfflineSheetLightPreview() {
    EqualTheme(darkTheme = false) { ChatErrorScreen(state = ErrorState.OfflineSheet) }
}

@Preview(name = "Errors · offline screen · dark", widthDp = EqualFrameWidthDp, heightDp = EqualFrameHeightDp, showBackground = true)
@Composable
private fun ErrorOfflineScreenDarkPreview() {
    EqualTheme(darkTheme = true) { ChatErrorScreen(state = ErrorState.OfflineScreen) }
}
