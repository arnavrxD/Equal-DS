// Compose code shown on the docs site (Flows → Equal to Equal call; Components → Equal call).
// Each function body is extracted by scripts/build-snippets.mjs; keep the `// snippet:` line above it.
@file:Suppress("unused")

package ai.equal.ds.snippets

import ai.equal.ds.catalog.CallerStage
import ai.equal.ds.catalog.EqualCallCallerSample
import ai.equal.ds.catalog.EqualCallReceiverSample
import ai.equal.ds.catalog.ReceiverStage
import ai.equal.ds.components.CallLogCard
import ai.equal.ds.components.MessageList
import ai.equal.ds.components.TakeoverCard
import ai.equal.ds.components.CallStripStyle
import ai.equal.ds.components.EqualCallRole
import ai.equal.ds.components.EqualCallScreen
import ai.equal.ds.components.EqualCallState
import ai.equal.ds.components.IdentifiedByEqual
import ai.equal.ds.components.IncomingCallNotification
import ai.equal.ds.components.IncomingCallStrip
import ai.equal.ds.components.OngoingCallStrip
import ai.equal.ds.components.SwipeToAnswer
import ai.equal.ds.theme.EqualTheme
import androidx.compose.runtime.Composable

// snippet: flow-equal-call / receiver-lock-ringing
@Composable
internal fun E2eReceiverLockRingingSnippet() {
    EqualTheme {
        EqualCallReceiverSample(stage = ReceiverStage.LockRinging)
    }
}

// snippet: flow-equal-call / receiver-connecting
@Composable
internal fun E2eReceiverConnectingSnippet() {
    EqualTheme {
        EqualCallReceiverSample(stage = ReceiverStage.Connecting)
    }
}

// snippet: flow-equal-call / receiver-connected
@Composable
internal fun E2eReceiverConnectedSnippet() {
    EqualTheme {
        EqualCallReceiverSample(stage = ReceiverStage.Connected)
    }
}

// snippet: flow-equal-call / receiver-heads-up-app
@Composable
internal fun E2eReceiverHeadsUpAppSnippet() {
    EqualTheme {
        EqualCallReceiverSample(stage = ReceiverStage.HeadsUpApp)
    }
}

// snippet: flow-equal-call / receiver-heads-up-launcher
@Composable
internal fun E2eReceiverHeadsUpLauncherSnippet() {
    EqualTheme {
        EqualCallReceiverSample(stage = ReceiverStage.HeadsUpLauncher)
    }
}

// snippet: flow-equal-call / receiver-strip-avatar
@Composable
internal fun E2eReceiverStripAvatarSnippet() {
    EqualTheme {
        EqualCallReceiverSample(stage = ReceiverStage.StripAvatar)
    }
}

// snippet: flow-equal-call / receiver-strip-icon
@Composable
internal fun E2eReceiverStripIconSnippet() {
    EqualTheme {
        EqualCallReceiverSample(stage = ReceiverStage.StripIcon)
    }
}

// snippet: flow-equal-call / receiver-strip-accent
@Composable
internal fun E2eReceiverStripAccentSnippet() {
    EqualTheme {
        EqualCallReceiverSample(stage = ReceiverStage.StripAccent)
    }
}

// snippet: flow-equal-call / receiver-minimised
@Composable
internal fun E2eReceiverMinimisedSnippet() {
    EqualTheme {
        EqualCallReceiverSample(stage = ReceiverStage.Minimised)
    }
}

// snippet: flow-equal-call / caller-chat
@Composable
internal fun E2eCallerChatSnippet() {
    EqualTheme {
        EqualCallCallerSample(stage = CallerStage.Chat)
    }
}

// snippet: flow-equal-call / caller-mic-prompt
@Composable
internal fun E2eCallerMicPromptSnippet() {
    EqualTheme {
        EqualCallCallerSample(stage = CallerStage.MicPrompt)
    }
}

// snippet: flow-equal-call / caller-mic-system
@Composable
internal fun E2eCallerMicSystemSnippet() {
    EqualTheme {
        EqualCallCallerSample(stage = CallerStage.MicSystem)
    }
}

// snippet: flow-equal-call / caller-calling
@Composable
internal fun E2eCallerCallingSnippet() {
    EqualTheme {
        EqualCallCallerSample(stage = CallerStage.Calling)
    }
}

// snippet: flow-equal-call / caller-remote-ringing
@Composable
internal fun E2eCallerRemoteRingingSnippet() {
    EqualTheme {
        EqualCallCallerSample(stage = CallerStage.RemoteRinging)
    }
}

// snippet: flow-equal-call / caller-connected
@Composable
internal fun E2eCallerConnectedSnippet() {
    EqualTheme {
        EqualCallCallerSample(stage = CallerStage.Connected)
    }
}

// snippet: flow-equal-call / caller-chat-ongoing
@Composable
internal fun E2eCallerChatOngoingSnippet() {
    EqualTheme {
        EqualCallCallerSample(stage = CallerStage.ChatOngoing)
    }
}

// snippet: flow-equal-call / caller-chat-after
@Composable
internal fun E2eCallerChatAfterSnippet() {
    EqualTheme {
        EqualCallCallerSample(stage = CallerStage.ChatAfter)
    }
}

// snippet: flow-equal-call / live-receiver
@Composable
internal fun E2eLiveReceiverSnippet() {
    EqualTheme {
        EqualCallReceiverSample(stage = ReceiverStage.StripAvatar)
    }
}

// snippet: flow-equal-call / live-caller
@Composable
internal fun E2eLiveCallerSnippet() {
    EqualTheme {
        EqualCallCallerSample()
    }
}

// snippet: equal-call / Incoming call
@Composable
internal fun E2eIncomingDocSnippet() {
    EqualCallScreen(
        role = EqualCallRole.Receiver,
        state = EqualCallState.Ringing,
        name = "Keshav Reddy",
        initials = "KR",
        onAccept = { /* answer */ },
        onDecline = { /* decline */ },
    )
}

// snippet: equal-call / Swipe to answer and badge
@Composable
internal fun E2eSwipeDocSnippet() {
    IdentifiedByEqual()
    SwipeToAnswer(onAccept = { /* answer */ }, onDecline = { /* decline */ }, onMessage = { /* reply */ })
}

// snippet: equal-call / Call strips
@Composable
internal fun E2eStripsDocSnippet() {
    IncomingCallStrip(name = "Keshav Reddy", initials = "KR", style = CallStripStyle.Avatar, onAnswer = { /* answer */ })
    IncomingCallStrip(name = "Keshav Reddy", style = CallStripStyle.Icon, onAnswer = {})
    IncomingCallStrip(name = "Keshav Reddy", style = CallStripStyle.Accent, onAnswer = {})
    OngoingCallStrip(name = "Keshav Reddy", seconds = 21, onOpen = { /* back to the call */ }, onEnd = { /* end */ })
}

// snippet: equal-call / Heads-up stand-in and call card
@Composable
internal fun E2eHeadsUpDocSnippet() {
    IncomingCallNotification(name = "Keshav Reddy", initials = "KR")
    CallLogCard(duration = "1min")
}

// snippet: equal-call / Takeover card
@Composable
internal fun E2eTakeoverCardDocSnippet() {
    MessageList {
        TakeoverCard(time = "11:03")
    }
}
