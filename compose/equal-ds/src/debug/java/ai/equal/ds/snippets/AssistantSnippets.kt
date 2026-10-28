// Compose code shown on the docs site for Global MYA: the assistant-sheet component page
// ("assistant-sheet / <example title>") and the flow page ("flow-global-mya / <state key>").
// Extracted by scripts/build-snippets.mjs; debug source set only.
@file:Suppress("unused")

package ai.equal.ds.snippets

import ai.equal.ds.catalog.HomeScreenSample
import ai.equal.ds.assets.EqualCategory
import ai.equal.ds.components.ReplyAction
import ai.equal.ds.components.AssistantReplyCard
import ai.equal.ds.catalog.ChatTaskStage
import ai.equal.ds.catalog.ChatTaskSheet
import ai.equal.ds.catalog.ChatScreenSample
import ai.equal.ds.catalog.MeetAssistantCarousel
import ai.equal.ds.catalog.MyaReminderResult
import ai.equal.ds.catalog.MyaSampleChats
import ai.equal.ds.catalog.MyaStage
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
import ai.equal.ds.components.LargeActionChip
import ai.equal.ds.components.RecorderState
import ai.equal.ds.components.VoiceRecorder
import ai.equal.ds.icons.EqualIcons
import ai.equal.ds.theme.EqualTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// ---- Component: assistant-sheet --------------------------------------------------------------

// snippet: assistant-sheet / Greeting & quick actions
@Composable
internal fun AssistantGreetingSnippet() {
    EqualTheme {
        AssistantSheetShell(
            onScrim = { /* close */ },
            footer = { AssistantInput(mode = AssistantInputMode.Text, onMic = { /* to voice */ }, onSubmit = { /* send */ }) },
        ) {
            AssistantGreeting(name = "Keshav")
            AssistantActions(
                items = listOf(
                    AssistantAction("Talk to Equal"), // null icon: the Equal mark
                    AssistantAction("Set Instruction", EqualIcons.AutoFix),
                    AssistantAction("Make a Call", EqualIcons.CallFilled),
                ),
            )
            AssistantChips {
                LargeActionChip(label = "Reply Amit on WhatsApp", emoji = "\uD83D\uDDD3\uFE0F", onClick = {})
                LargeActionChip(label = "Set reminder", emoji = "\uD83D\uDD14", onClick = {})
            }
        }
    }
}

// snippet: assistant-sheet / Input: text and voice
@Composable
internal fun AssistantInputSnippet() {
    EqualTheme {
        var mode by remember { mutableStateOf(AssistantInputMode.Text) }
        var text by remember { mutableStateOf("") }
        AssistantInput(
            mode = mode,
            value = text,
            onValueChange = { text = it },
            onMic = { mode = AssistantInputMode.Voice },
            onKeyboard = { mode = AssistantInputMode.Text },
            onSubmit = { /* send it */ },
            onVoiceNote = { /* record */ },
            onVoiceHold = { /* hold to record */ },
            modifier = Modifier.imePadding(), // lift above the system keyboard
        )
    }
}

// snippet: assistant-sheet / Recorder states
@Composable
internal fun AssistantRecorderSnippet() {
    EqualTheme {
        Column(Modifier.background(EqualTheme.colors.bgPrimary).padding(16.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
            VoiceRecorder(state = RecorderState.Idle, onDelete = {}, onSend = {})
            VoiceRecorder(state = RecorderState.Recording, seconds = 4, onDelete = {}, onSend = {})
            VoiceRecorder(state = RecorderState.Hold) // press and hold: release to send, slide left to cancel
            VoiceRecorder(state = RecorderState.Cancel) // slid left: release to cancel
        }
    }
}

// snippet: assistant-sheet / Task conversation
@Composable
internal fun AssistantTaskSnippet() {
    EqualTheme {
        Column(Modifier.background(EqualTheme.colors.chatWallpaperBg)) {
            AssistantTaskHeader(title = "Setting up a reminder for appointment at Dr. Mehta\u2019s", onClose = { /* close */ })
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                AssistantThinking()
                AssistantReplies(onEdit = { /* edit */ }, onConfirm = { /* confirm */ })
            }
        }
    }
}

// snippet: assistant-sheet / Reply card
@Composable
internal fun AssistantReplyCardSnippet() {
    EqualTheme {
        Column(Modifier.background(EqualTheme.colors.chatWallpaperBg).padding(16.dp)) {
            AssistantReplyCard(
                text = "Sure! Here\u2019s a draft for Amit:\n\nHi Amit! Count me in for Sunday 8 PM. See you there \uD83D\uDC4D",
                time = "03:21",
                actions = listOf(
                    ReplyAction(label = "Edit", emoji = "\u270F\uFE0F", onClick = { /* edit */ }),
                    ReplyAction(label = "Send", emoji = "\uD83D\uDCE4", onClick = { /* send */ }),
                ),
            )
        }
    }
}

// snippet: assistant-sheet / Chat task sheet
@Composable
internal fun AssistantChatTaskSheetSnippet() {
    EqualTheme {
        // From a chat's AI layer (Reply on WhatsApp, or a sent message): stops below the chat header
        ChatTaskSheet(
            contact = "Amit",
            request = "Reply on whatsapp",
            onClose = { /* close */ },
            onSend = { /* open WhatsApp */ },
        )
    }
}

// snippet: assistant-sheet / Carousel dots
@Composable
internal fun AssistantCarouselDotsSnippet() {
    EqualTheme {
        CarouselDots(count = 3, active = 0)
    }
}

// ---- Flow: global-mya ------------------------------------------------------------------------

// snippet: flow-global-mya / home
@Composable
internal fun FlowMyaHomeSnippet() {
    EqualTheme {
        // Home: tap the green orb (nav FAB) to open the live sheet
        HomeScreenSample(chats = MyaSampleChats, promo = { MeetAssistantCarousel(onDismiss = {}) })
    }
}

// snippet: flow-global-mya / greeting
@Composable
internal fun FlowMyaGreetingSnippet() {
    EqualTheme {
        HomeScreenSample(chats = MyaSampleChats, promo = { MeetAssistantCarousel(onDismiss = {}) }, assistant = MyaStage.Greeting)
    }
}

// snippet: flow-global-mya / voice
@Composable
internal fun FlowMyaVoiceSnippet() {
    EqualTheme {
        HomeScreenSample(chats = MyaSampleChats, promo = { MeetAssistantCarousel(onDismiss = {}) }, assistant = MyaStage.Voice)
    }
}

// snippet: flow-global-mya / typing
@Composable
internal fun FlowMyaTypingSnippet() {
    EqualTheme {
        // Real use: the system keyboard opens and the sheet lifts with imePadding()
        HomeScreenSample(chats = MyaSampleChats, promo = { MeetAssistantCarousel(onDismiss = {}) }, assistant = MyaStage.Typing)
    }
}

// snippet: flow-global-mya / typed
@Composable
internal fun FlowMyaTypedSnippet() {
    EqualTheme {
        HomeScreenSample(chats = MyaSampleChats, promo = { MeetAssistantCarousel(onDismiss = {}) }, assistant = MyaStage.Typed)
    }
}

// snippet: flow-global-mya / voice-prompt
@Composable
internal fun FlowMyaVoicePromptSnippet() {
    EqualTheme {
        HomeScreenSample(chats = MyaSampleChats, promo = { MeetAssistantCarousel(onDismiss = {}) }, assistant = MyaStage.VoicePrompt)
    }
}

// snippet: flow-global-mya / recording
@Composable
internal fun FlowMyaRecordingSnippet() {
    EqualTheme {
        HomeScreenSample(chats = MyaSampleChats, promo = { MeetAssistantCarousel(onDismiss = {}) }, assistant = MyaStage.Recording)
    }
}

// snippet: flow-global-mya / hold
@Composable
internal fun FlowMyaHoldSnippet() {
    EqualTheme {
        HomeScreenSample(chats = MyaSampleChats, promo = { MeetAssistantCarousel(onDismiss = {}) }, assistant = MyaStage.Hold)
    }
}

// snippet: flow-global-mya / hold-cancel
@Composable
internal fun FlowMyaHoldCancelSnippet() {
    EqualTheme {
        HomeScreenSample(chats = MyaSampleChats, promo = { MeetAssistantCarousel(onDismiss = {}) }, assistant = MyaStage.HoldCancel)
    }
}

// snippet: flow-global-mya / sent
@Composable
internal fun FlowMyaSentSnippet() {
    EqualTheme {
        HomeScreenSample(chats = MyaSampleChats, promo = { MeetAssistantCarousel(onDismiss = {}) }, assistant = MyaStage.Sent)
    }
}

// snippet: flow-global-mya / thinking
@Composable
internal fun FlowMyaThinkingSnippet() {
    EqualTheme {
        HomeScreenSample(chats = MyaSampleChats, promo = { MeetAssistantCarousel(onDismiss = {}) }, assistant = MyaStage.Thinking)
    }
}

// snippet: flow-global-mya / reminder
@Composable
internal fun FlowMyaReminderSnippet() {
    EqualTheme {
        HomeScreenSample(chats = MyaSampleChats, promo = { MeetAssistantCarousel(onDismiss = {}) }, assistant = MyaStage.Reminder)
    }
}

// snippet: flow-global-mya / confirmed
@Composable
internal fun FlowMyaConfirmedSnippet() {
    EqualTheme {
        HomeScreenSample(chats = MyaSampleChats, promo = { MeetAssistantCarousel(onDismiss = {}) }, assistant = MyaStage.Confirmed)
    }
}

// snippet: flow-global-mya / after
@Composable
internal fun FlowMyaAfterSnippet() {
    EqualTheme {
        // After Confirm: the chat the assistant acted on reads "Reminder added"
        HomeScreenSample(chats = MyaSampleChats, promo = { MeetAssistantCarousel(onDismiss = {}) }, assistantResult = MyaReminderResult)
    }
}

// snippet: flow-global-mya / chat-ai
@Composable
internal fun FlowMyaChatAiSnippet() {
    EqualTheme {
        // Tap Reply on WhatsApp (or send a message) to open the task sheet
        ChatScreenSample(name = "Amit Nanda", initials = "AN", category = EqualCategory.NA, subtitle = "+91 98717 46113")
    }
}

// snippet: flow-global-mya / chat-task
@Composable
internal fun FlowMyaChatTaskSnippet() {
    EqualTheme {
        ChatScreenSample(name = "Amit Nanda", initials = "AN", category = EqualCategory.NA, subtitle = "+91 98717 46113", aiTask = ChatTaskStage.Draft)
    }
}

// snippet: flow-global-mya / chat-sending
@Composable
internal fun FlowMyaChatSendingSnippet() {
    EqualTheme {
        ChatScreenSample(name = "Amit Nanda", initials = "AN", category = EqualCategory.NA, subtitle = "+91 98717 46113", aiTask = ChatTaskStage.Sending)
    }
}
