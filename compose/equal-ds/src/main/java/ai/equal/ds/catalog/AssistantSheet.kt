package ai.equal.ds.catalog

import ai.equal.ds.components.MessageList
import androidx.compose.foundation.layout.PaddingValues
import ai.equal.ds.components.ActionCardBody
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.horizontalScroll
import ai.equal.ds.components.VoiceInputBar
import ai.equal.ds.components.ReplyAction
import ai.equal.ds.components.AssistantReplyCard
import ai.equal.ds.components.AiAvatarSize
import ai.equal.ds.components.AiAvatar
import ai.equal.ds.components.ActionCardLine
import ai.equal.ds.components.AssistantAction
import ai.equal.ds.components.AssistantActions
import ai.equal.ds.components.AssistantChips
import ai.equal.ds.components.AssistantGreeting
import ai.equal.ds.components.AssistantInput
import ai.equal.ds.components.AssistantInputMode
import ai.equal.ds.components.AssistantReplies
import ai.equal.ds.components.AssistantSheetShell
import ai.equal.ds.components.AssistantTaskHeader
import ai.equal.ds.components.AssistantThinking
import ai.equal.ds.components.CarouselDots
import ai.equal.ds.components.ChatBubble
import ai.equal.ds.components.ChatBubbleType
import ai.equal.ds.components.ChatWallpaper
import ai.equal.ds.components.EmojiGlyph
import ai.equal.ds.components.EqualFrameHeightDp
import ai.equal.ds.components.EqualFrameWidthDp
import ai.equal.ds.components.KeyboardSpace
import ai.equal.ds.components.LargeActionChip
import ai.equal.ds.components.MeetAssistantCard
import ai.equal.ds.components.MeetAssistantLayout
import ai.equal.ds.components.PlaybackSpeed
import ai.equal.ds.components.RecorderState
import ai.equal.ds.components.TrySaying
import ai.equal.ds.components.VoiceNotePlayer
import ai.equal.ds.components.VoicePlayerTone
import ai.equal.ds.components.VoiceRecorder
import ai.equal.ds.icons.EqualIcons
import ai.equal.ds.theme.EqualShadows
import ai.equal.ds.theme.EqualTheme
import ai.equal.ds.theme.eqShadow
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

// Global MYA (Global MYA design): the assistant sheet that opens from the green orb, as a live flow.
// greeting → mic → voice → Send voice note → recording → send → sent → Thinking… → reminder card
// → Confirm → confirmed → closes (the chat row then reads "Reminder added").
// Mirrors web/src/screens/AssistantSheet.tsx.

/** Stages of the [AssistantSheet] (the flow-global-mya states). */
enum class MyaStage {
    /** Greeting, quick actions, chips and the message field. */
    Greeting,

    /** The mic swapped the field for Send voice note. */
    Voice,

    /** Field focused: tiles hidden, keyboard open. */
    Typing,

    /** A request typed in the field. */
    Typed,

    /** Tapped Send voice note: "Try saying" + the idle recorder, then recording starts. */
    VoicePrompt,

    /** Recording: timer + live waveform. */
    Recording,

    /** Press and hold: "Release to send, slide left to cancel". */
    Hold,

    /** Slid left while holding: "Release to cancel". */
    HoldCancel,

    /** The task sheet with the sent voice note. */
    Sent,

    /** The assistant working ("Thinking…"). */
    Thinking,

    /** The result card with Edit / Confirm. */
    Reminder,

    /** Confirmed: the card ticked; the live flow then calls onDone. */
    Confirmed,
}

/** The three quick-action tiles of the sample (the Equal mark, auto-fix and Make a Call). */
val MyaActions: List<AssistantAction> = listOf(
    AssistantAction("Talk to Equal"),
    AssistantAction("Set Instruction", EqualIcons.AutoFix),
    AssistantAction("Make a Call", EqualIcons.CallFilled),
)

private const val MyaRequest = "Set up a remainder for appointment at Dr. Mehta’s"
private const val MyaTyped = "Set a remainder for appointment"

/**
 * The Global MYA sheet as a stage machine. Pin [stage] for docs; with `null` it runs live from
 * [MyaStage.Greeting]: the mic → voice; Send voice note → voice prompt (0.9s) → recording (timer
 * counts up; press and hold → hold); send → sent (0.9s) → thinking (1.5s) → reminder; Confirm →
 * confirmed → [onDone] after 1.6s. Edit goes back to typing with the request in the field.
 * [onClose] is the scrim and the task header's close. [inline] renders only the panel.
 *
 * The keyboard is the system's: a pinned typing stage shows a [KeyboardSpace] placeholder, the
 * live sheet lifts the input with `imePadding()`.
 */
@Composable
fun AssistantSheet(
    modifier: Modifier = Modifier,
    stage: MyaStage? = null,
    name: String = "Keshav",
    onClose: () -> Unit = {},
    onDone: () -> Unit = {},
    inline: Boolean = false,
) {
    var live by remember { mutableStateOf(MyaStage.Greeting) }
    var text by remember { mutableStateOf("") }
    var seconds by remember { mutableIntStateOf(0) }
    val s = stage ?: live
    val pinned = stage != null
    val go: (MyaStage) -> Unit = { next -> if (!pinned) live = next }
    val done by rememberUpdatedState(onDone)

    // Live timing: recording counts up; sent → thinking → reminder; confirmed → done.
    LaunchedEffect(s, pinned) {
        if (pinned) return@LaunchedEffect
        when (s) {
            MyaStage.VoicePrompt -> {
                delay(900)
                seconds = 0
                live = MyaStage.Recording
            }
            MyaStage.Recording -> while (true) {
                delay(1000)
                seconds++
            }
            MyaStage.Sent -> {
                delay(900)
                live = MyaStage.Thinking
            }
            MyaStage.Thinking -> {
                delay(1500)
                live = MyaStage.Reminder
            }
            MyaStage.Confirmed -> {
                delay(1600)
                done()
            }
            else -> Unit
        }
    }

    val task = s == MyaStage.Sent || s == MyaStage.Thinking || s == MyaStage.Reminder || s == MyaStage.Confirmed
    val recording = s == MyaStage.VoicePrompt || s == MyaStage.Recording || s == MyaStage.Hold || s == MyaStage.HoldCancel
    val typing = s == MyaStage.Typing || s == MyaStage.Typed
    val voice = text.isEmpty()
    val value = if (pinned) (if (s == MyaStage.Typed) MyaTyped else "") else text
    // One shell for every stage, so the sheet slides up once and then just changes.
    AssistantSheetShell(
        modifier = modifier,
        task = task,
        inline = inline,
        onScrim = onClose,
        header = if (task) ({ AssistantTaskHeader(title = "Setting up a reminder for appointment at Dr. Mehta\u2019s", onClose = onClose) }) else null,
        footer = {
            when {
                task -> {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        if (s == MyaStage.Reminder) {
                            AssistantReplies(
                                onEdit = {
                                    text = MyaRequest
                                    go(MyaStage.Typing)
                                },
                                onConfirm = { go(MyaStage.Confirmed) },
                            )
                        }
                        AssistantInput(
                            mode = AssistantInputMode.Voice,
                            onKeyboard = { go(MyaStage.Typing) },
                            onVoiceNote = { go(MyaStage.VoicePrompt) },
                        )
                    }
                }
                recording -> {
                    VoiceRecorder(
                        state = when (s) {
                            MyaStage.VoicePrompt -> RecorderState.Idle
                            MyaStage.Recording -> RecorderState.Recording
                            MyaStage.Hold -> RecorderState.Hold
                            else -> RecorderState.Cancel
                        },
                        seconds = if (!pinned) seconds else if (s == MyaStage.Recording) 4 else 0,
                        onDelete = { go(MyaStage.Voice) },
                        onSend = {
                            text = ""
                            go(MyaStage.Sent)
                        },
                    )
                }
                else -> {
                    AssistantInput(
                        mode = if (s == MyaStage.Voice) AssistantInputMode.Voice else AssistantInputMode.Text,
                        value = value,
                        focused = typing,
                        onValueChange = { text = it },
                        onFocus = { go(MyaStage.Typing) },
                        onSubmit = { go(MyaStage.Sent) },
                        onMic = { go(MyaStage.Voice) },
                        onKeyboard = { go(MyaStage.Greeting) },
                        onVoiceNote = { go(MyaStage.VoicePrompt) },
                        onVoiceHold = { go(MyaStage.Hold) },
                    )
                }
            }
        },
        keyboard = if (typing && pinned) ({ KeyboardSpace() }) else null,
    ) {
        when {
            task -> {
                ChatWallpaper(Modifier.fillMaxWidth().weight(1f)) {
                    MessageList(
                        Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 16.dp),
                    ) {
                        ChatBubble(
                            message = if (voice) MyaRequest else text,
                            timestamp = "03:21",
                            type = ChatBubbleType.Outgoing,
                            sender = "You",
                            attachment = if (voice) ({ SentVoiceNote() }) else null,
                        )
                        if (s == MyaStage.Thinking) AssistantThinking()
                        if (s == MyaStage.Reminder || s == MyaStage.Confirmed) {
                            ChatBubble(
                                message = "Got it! Here’s your remainder:",
                                timestamp = "03:21",
                                type = ChatBubbleType.Assistant,
                                sender = "Equal AI",
                            )
                            ReminderCard(confirmed = s == MyaStage.Confirmed)
                            if (s == MyaStage.Confirmed) {
                                ChatBubble(message = "Confirm", timestamp = "03:21", type = ChatBubbleType.Outgoing, sender = "You")
                            }
                        }
                    }
                }
            }
            recording -> {
                AssistantGreeting(name = name)
                TrySaying(example = "Remind me to call Amit at 6")
            }
            else -> {
                AssistantGreeting(name = name)
                if (!typing) AssistantActions(items = MyaActions.map { it.copy(onClick = { go(MyaStage.Voice) }) })
                AssistantChips {
                    LargeActionChip(label = "Reply Amit on WhatsApp", emoji = "🗓️", onClick = { go(MyaStage.Typed) })
                    LargeActionChip(
                        label = "Set reminder",
                        emoji = "🔔",
                        onClick = {
                            text = MyaTyped
                            go(MyaStage.Typed)
                        },
                    )
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------------------------
// The AI layer inside a chat
// ---------------------------------------------------------------------------------------------

/** Stages of the [ChatTaskSheet]. */
enum class ChatTaskStage {
    /** The drafted reply with ✏️ Edit / 📤 Send. */
    Draft,

    /** The user confirmed: "send as is" + "Opening WhatsApp…", chips greyed out. */
    Sending,
}

/** The drafted WhatsApp reply in the [ChatTaskSheet] for [contact] (first name). */
fun chatTaskDraft(contact: String): String =
    "Sure! Here\u2019s a draft for $contact:\n\nHi $contact! Count me in for Sunday 8 PM. See you there \uD83D\uDC4D"

/**
 * A chat's AI layer turned into a task (web `ChatTaskSheet`): the sheet rises over the
 * conversation but stops below its header ([AssistantSheetShell] `inChat`), so the user still sees
 * which chat it belongs to. Header "Replying [contact] on WhatsApp" → You bubble with [request] →
 * AI avatar + [AssistantReplyCard] with the draft (✏️ Edit / 📤 Send). Footer: 📤 Send · ✏️ Edit
 * draft · 🖋️ Custom chips over a [VoiceInputBar]. Sending (Send, or anything typed) adds a You
 * bubble ("send as is" when pinned) and [AssistantThinking] "Opening WhatsApp", greys the chips out
 * and calls [onSend] (open WhatsApp).
 *
 * Pin [stage] for docs; with `null` it runs live from [ChatTaskStage.Draft]. [onClose] is the
 * scrim and the header's close. [inline] renders only the panel (give it a height).
 */
@Composable
fun ChatTaskSheet(
    contact: String,
    request: String,
    modifier: Modifier = Modifier,
    stage: ChatTaskStage? = null,
    onClose: () -> Unit = {},
    onSend: () -> Unit = {},
    inline: Boolean = false,
) {
    var live by remember { mutableStateOf(ChatTaskStage.Draft) }
    var said by remember { mutableStateOf<String?>(null) }
    var message by remember { mutableStateOf("") }
    val pinned = stage != null
    val s = stage ?: live
    val confirm = if (pinned) "send as is" else said
    val send: (String) -> Unit = { text ->
        if (!pinned) {
            said = text
            live = ChatTaskStage.Sending
            onSend()
        }
    }
    val sending = s == ChatTaskStage.Sending
    AssistantSheetShell(
        modifier = modifier,
        task = true,
        inChat = true,
        inline = inline,
        onScrim = onClose,
        header = { AssistantTaskHeader(title = "Replying $contact on WhatsApp", onClose = onClose) },
        footer = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    LargeActionChip(label = "Send", emoji = "\uD83D\uDCE4", enabled = !sending, onClick = { send("Send") })
                    LargeActionChip(label = "Edit draft", emoji = "\u270F\uFE0F", enabled = !sending, onClick = {})
                    LargeActionChip(label = "Custom", emoji = "\uD83D\uDD8B\uFE0F", enabled = !sending, onClick = {})
                }
                VoiceInputBar(
                    value = message,
                    onValueChange = { message = it },
                    onSend = {
                        val text = message.trim()
                        if (text.isNotEmpty()) {
                            message = ""
                            send(text)
                        }
                    },
                )
            }
        },
    ) {
        ChatWallpaper(Modifier.fillMaxWidth().weight(1f)) {
            MessageList(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 16.dp),
            ) {
                ChatBubble(message = request, timestamp = "03:21", type = ChatBubbleType.Outgoing, sender = "You")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.Top) {
                    AiAvatar(size = AiAvatarSize.Small)
                    AssistantReplyCard(
                        text = chatTaskDraft(contact),
                        time = "03:21",
                        actions = listOf(
                            ReplyAction(label = "Edit", emoji = "\u270F\uFE0F"),
                            ReplyAction(label = "Send", emoji = "\uD83D\uDCE4", onClick = { send("Send") }),
                        ),
                    )
                }
                if (sending) {
                    ChatBubble(message = confirm ?: "send as is", timestamp = "03:21", type = ChatBubbleType.Outgoing, sender = "You")
                    AssistantThinking(label = "Opening WhatsApp")
                }
            }
        }
    }
}

/** The user's voice note in the sent bubble (2:37). */
@Composable
private fun SentVoiceNote() {
    var playing by remember { mutableStateOf(false) }
    var speed by remember { mutableStateOf(PlaybackSpeed.X1) }
    VoiceNotePlayer(
        isPlaying = playing,
        onPlayPauseClick = { playing = !playing },
        progress = 0f,
        elapsed = "0:00",
        total = "2:37",
        speed = speed,
        onSpeedChange = { speed = it },
        tone = VoicePlayerTone.Sent,
    )
}

/** The WhatsApp reminder result: [ActionCardBody] on a `bg/surface` card; [confirmed] adds the ✅ badge. */
@Composable
private fun ReminderCard(confirmed: Boolean) {
    val colors = EqualTheme.colors
    Box(Modifier.padding(start = 36.dp, bottom = if (confirmed) 14.dp else 0.dp)) {
        Box(
            Modifier
                .widthIn(max = 300.dp)
                .background(colors.bgSurface, RoundedCornerShape(16.dp))
                .padding(8.dp),
        ) {
            ActionCardBody(
                title = "Reminder · Dr. Mehta’s Clinic",
                lines = listOf(ActionCardLine("Tomorrow, 9:00 AM"), ActionCardLine("Bring previous medical reports")),
                timestamp = "03:21",
            )
        }
        if (confirmed) {
            Box(
                Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = (-10).dp, y = 14.dp)
                    .size(32.dp)
                    .eqShadow(EqualShadows.raised, CircleShape)
                    .background(colors.bgSurface, CircleShape)
                    .semantics { contentDescription = "Confirmed" },
                contentAlignment = Alignment.Center,
            ) {
                EmojiGlyph(emoji = "✅", size = 18.dp)
            }
        }
    }
}

/**
 * The Home promo for Global MYA: [MeetAssistantCard] in the banner layout with [CarouselDots]
 * (3, first active) under it. Pass it as [HomeScreenSample]'s `promo`.
 */
@Composable
fun MeetAssistantCarousel(
    modifier: Modifier = Modifier,
    onCallClick: () -> Unit = {},
    onDismiss: (() -> Unit)? = null,
) {
    Column(modifier.fillMaxWidth()) {
        MeetAssistantCard(onCallClick = onCallClick, layout = MeetAssistantLayout.Banner, onDismiss = onDismiss)
        CarouselDots(count = 3, active = 0)
    }
}

// ---------------------------------------------------------------------------------------------
// Previews
// ---------------------------------------------------------------------------------------------

@Preview(name = "MYA · greeting · light", widthDp = EqualFrameWidthDp, heightDp = EqualFrameHeightDp, showBackground = true)
@Composable
private fun AssistantSheetGreetingLightPreview() {
    EqualTheme(darkTheme = false) { HomeScreenSample(chats = MyaSampleChats, promo = { MeetAssistantCarousel() }, assistant = MyaStage.Greeting) }
}

@Preview(name = "MYA · greeting · dark", widthDp = EqualFrameWidthDp, heightDp = EqualFrameHeightDp, showBackground = true)
@Composable
private fun AssistantSheetGreetingDarkPreview() {
    EqualTheme(darkTheme = true) { HomeScreenSample(chats = MyaSampleChats, promo = { MeetAssistantCarousel() }, assistant = MyaStage.Greeting) }
}

@Preview(name = "MYA · recording · light", widthDp = EqualFrameWidthDp, heightDp = EqualFrameHeightDp, showBackground = true)
@Composable
private fun AssistantSheetRecordingLightPreview() {
    EqualTheme(darkTheme = false) { HomeScreenSample(chats = MyaSampleChats, assistant = MyaStage.Recording) }
}

@Preview(name = "MYA · reminder · light", widthDp = EqualFrameWidthDp, heightDp = EqualFrameHeightDp, showBackground = true)
@Composable
private fun AssistantSheetReminderLightPreview() {
    EqualTheme(darkTheme = false) { HomeScreenSample(chats = MyaSampleChats, assistant = MyaStage.Reminder) }
}

@Preview(name = "MYA · confirmed · dark", widthDp = EqualFrameWidthDp, heightDp = EqualFrameHeightDp, showBackground = true)
@Composable
private fun AssistantSheetConfirmedDarkPreview() {
    EqualTheme(darkTheme = true) { HomeScreenSample(chats = MyaSampleChats, assistant = MyaStage.Confirmed) }
}

@Preview(name = "MYA · typing · dark", widthDp = EqualFrameWidthDp, heightDp = EqualFrameHeightDp, showBackground = true)
@Composable
private fun AssistantSheetTypingDarkPreview() {
    EqualTheme(darkTheme = true) { HomeScreenSample(chats = MyaSampleChats, assistant = MyaStage.Typing) }
}
