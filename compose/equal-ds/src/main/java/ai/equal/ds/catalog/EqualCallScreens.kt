package ai.equal.ds.catalog

import ai.equal.ds.assets.EqualAssets
import ai.equal.ds.components.ActionChipData
import ai.equal.ds.components.AiChatPanel
import ai.equal.ds.components.CallLogCard
import ai.equal.ds.components.CallStripStyle
import ai.equal.ds.components.CallSummaryBubble
import ai.equal.ds.components.ChatBubble
import ai.equal.ds.components.ChatBubbleType
import ai.equal.ds.components.ChatHeader
import ai.equal.ds.components.ChatWallpaper
import ai.equal.ds.components.DayDivider
import ai.equal.ds.components.DeviceScreen
import ai.equal.ds.components.EqualCallRole
import ai.equal.ds.components.EqualCallScreen
import ai.equal.ds.components.EqualCallState
import ai.equal.ds.components.IncomingCallNotification
import ai.equal.ds.components.IncomingCallStrip
import ai.equal.ds.components.OngoingCallStrip
import ai.equal.ds.components.PermissionDialog
import ai.equal.ds.components.StatusBar
import ai.equal.ds.components.SystemPermissionStandIn
import ai.equal.ds.components.rememberCallTimer
import ai.equal.ds.theme.EqualTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/** Receiver POV stages (web `ReceiverStage`). */
enum class ReceiverStage { LockRinging, Connecting, Connected, HeadsUpApp, HeadsUpLauncher, StripAvatar, StripIcon, StripAccent, Minimised }

/**
 * Receiver POV (Equal To Equal Call Experience design): ringing on the lock screen, as a heads-up
 * over Equal or the launcher, or as the in-app strip → Connecting → Connected; Minimise shows the
 * ongoing strip on Chats and tapping it returns. Port of web `EqualCallReceiverFlow`.
 */
@Composable
fun EqualCallReceiverSample(modifier: Modifier = Modifier, stage: ReceiverStage = ReceiverStage.LockRinging) {
    var s by remember(stage) { mutableStateOf(stage) }
    var ended by remember(stage) { mutableStateOf(false) }
    var speaker by remember { mutableStateOf(false) }
    var muted by remember { mutableStateOf(false) }
    val seconds = rememberCallTimer(s == ReceiverStage.Connected || s == ReceiverStage.Minimised, if (stage == ReceiverStage.Minimised) 21 else if (stage == ReceiverStage.Connected) 1 else 0)
    LaunchedEffect(s) { if (s == ReceiverStage.Connecting && stage != ReceiverStage.Connecting) { delay(1500); s = ReceiverStage.Connected } }
    val answer = { s = ReceiverStage.Connecting }
    val name = "Keshav Reddy"
    val initials = "KR"
    if (ended) {
        HomeScreenSample(modifier)
        return
    }
    when (s) {
        ReceiverStage.HeadsUpApp, ReceiverStage.HeadsUpLauncher -> Box(modifier.fillMaxSize()) {
            if (s == ReceiverStage.HeadsUpApp) HomeScreenSample() else DeviceScreen(time = "12:30")
            IncomingCallNotification(name, initials, Modifier.align(Alignment.TopCenter).padding(start = 16.dp, end = 16.dp, top = 68.dp), onAnswer = answer, onDecline = { ended = true })
        }
        ReceiverStage.StripAvatar, ReceiverStage.StripIcon, ReceiverStage.StripAccent -> HomeScreenSample(
            modifier,
            callStrip = {
                IncomingCallStrip(
                    name = name,
                    initials = initials,
                    style = when (s) { ReceiverStage.StripAvatar -> CallStripStyle.Avatar; ReceiverStage.StripIcon -> CallStripStyle.Icon; else -> CallStripStyle.Accent },
                    onAnswer = answer,
                )
            },
        )
        ReceiverStage.Minimised -> HomeScreenSample(
            modifier,
            callStrip = { OngoingCallStrip(name, seconds, onOpen = { s = ReceiverStage.Connected }, onEnd = { ended = true }, muted = muted, onMute = { muted = !muted }) },
        )
        else -> EqualCallScreen(
            role = EqualCallRole.Receiver,
            state = when (s) { ReceiverStage.LockRinging -> EqualCallState.Ringing; ReceiverStage.Connecting -> EqualCallState.Connecting; else -> EqualCallState.Connected },
            name = name,
            initials = initials,
            modifier = modifier,
            seconds = seconds,
            speaker = speaker,
            muted = muted,
            onAccept = answer,
            onDecline = { ended = true },
            onMinimise = { s = ReceiverStage.Minimised },
            onSpeaker = { speaker = !speaker },
            onMute = { muted = !muted },
            onEnd = { ended = true },
        )
    }
}

/** The caller's chat with Chaitanya: summary, the ongoing strip while a call is live (call icon disabled), the Voice Call card after. */
@Composable
fun EqualCallChatSample(
    modifier: Modifier = Modifier,
    ongoing: Boolean = false,
    seconds: Int = 21,
    afterCall: Boolean = false,
    onCall: () -> Unit = {},
    onOpenCall: () -> Unit = {},
    onEnd: () -> Unit = {},
) {
    val colors = EqualTheme.colors
    var muted by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    Column(modifier.fillMaxSize().background(colors.bgPrimary)) {
        StatusBar(background = if (ongoing) colors.callStripBg else colors.bgSurface)
        if (ongoing) OngoingCallStrip("Chaitanya Sai", seconds, onOpen = onOpenCall, onEnd = onEnd, muted = muted, onMute = { muted = !muted })
        ChatHeader(name = "Chaitanya Sai", onBackClick = {}, subtitle = "+91-81720 33604", initials = "CS", category = null, onCallClick = onCall, callDisabled = ongoing)
        ChatWallpaper(Modifier.weight(1f).fillMaxWidth()) {
            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DayDivider("Today", Modifier.align(Alignment.CenterHorizontally))
                ChatBubble(message = "Hey Keshav! Here is the summary of your call", timestamp = "03:21", type = ChatBubbleType.Assistant, sender = "Equal AI")
                CallSummaryBubble(summary = "Chaitanya invited you to dinner this Sunday, 8 PM at his place. He asked you to confirm by tonight.", timestamp = "03:21", modifier = Modifier.padding(start = 36.dp), onShareClick = {})
                if (afterCall) Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) { CallLogCard(duration = "1min") }
            }
        }
        AiChatPanel(
            prompt = "Want me to send Chaitanya a quick reply on WhatsApp?",
            actions = listOf(ActionChipData("Reply on WhatsApp", "💬"), ActionChipData("Add to Calendar", "🗓️"), ActionChipData("Call back", "📞")),
            onActionClick = {},
            composerValue = message,
            onComposerValueChange = { message = it },
            onSend = { message = "" },
        )
    }
}

/** Caller POV stages (web `CallerStage`). */
enum class CallerStage { Chat, MicPrompt, MicSystem, Calling, RemoteRinging, Connected, ChatOngoing, ChatAfter }

/**
 * Caller POV: the chat → call → (first time) "Let’s get you talking" → Android's dialog → Calling… →
 * Ringing… → Connected; Minimise puts the strip over the chat; End leaves the Voice Call card.
 */
@Composable
fun EqualCallCallerSample(modifier: Modifier = Modifier, stage: CallerStage = CallerStage.Chat, micGranted: Boolean = false) {
    var s by remember(stage) { mutableStateOf(stage) }
    var speaker by remember { mutableStateOf(false) }
    var muted by remember { mutableStateOf(false) }
    val seconds = rememberCallTimer(s == CallerStage.Connected || s == CallerStage.ChatOngoing, if (stage == CallerStage.ChatOngoing) 21 else if (stage == CallerStage.Connected) 1 else 0)
    LaunchedEffect(s) {
        if (s == stage) return@LaunchedEffect
        if (s == CallerStage.Calling) { delay(1500); s = CallerStage.RemoteRinging }
        if (s == CallerStage.RemoteRinging) { delay(2000); s = CallerStage.Connected }
    }
    when (s) {
        CallerStage.Chat, CallerStage.ChatOngoing, CallerStage.ChatAfter -> EqualCallChatSample(
            modifier,
            ongoing = s == CallerStage.ChatOngoing,
            seconds = seconds,
            afterCall = s == CallerStage.ChatAfter,
            onCall = { s = if (micGranted) CallerStage.Calling else CallerStage.MicPrompt },
            onOpenCall = { s = CallerStage.Connected },
            onEnd = { s = CallerStage.ChatAfter },
        )
        CallerStage.MicPrompt, CallerStage.MicSystem -> Box(modifier.fillMaxSize()) {
            EqualCallChatSample()
            if (s == CallerStage.MicPrompt) {
                PermissionDialog(
                    title = "Let’s get you talking",
                    body = "Equal needs access to your microphone so you can talk to people on calls.",
                    cta = "Allow microphone",
                    art = EqualAssets.PermissionMicrophone,
                    onAction = { s = CallerStage.MicSystem },
                    secondary = "Not now",
                    onSecondary = { s = CallerStage.Chat },
                    closable = false,
                )
            } else {
                SystemPermissionStandIn(onAllow = { s = CallerStage.Calling }, onDeny = { s = CallerStage.Chat })
            }
        }
        else -> EqualCallScreen(
            role = EqualCallRole.Caller,
            state = when (s) { CallerStage.Calling -> EqualCallState.Calling; CallerStage.RemoteRinging -> EqualCallState.RemoteRinging; else -> EqualCallState.Connected },
            name = "Chaitanya Sai",
            initials = "CS",
            modifier = modifier,
            seconds = seconds,
            speaker = speaker,
            muted = muted,
            onMinimise = { s = CallerStage.ChatOngoing },
            onMessage = { s = CallerStage.ChatOngoing },
            onSpeaker = { speaker = !speaker },
            onMute = { muted = !muted },
            onEnd = { s = CallerStage.ChatAfter },
        )
    }
}
