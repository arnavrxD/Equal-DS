// Compose code shown on the docs site for Call Handling: the component examples
// ("call-handling / <example title>") and the flow pages ("flow-call-handling / <state key>",
// "flow-known-calls / <state key>"). Extracted by scripts/build-snippets.mjs; debug source set
// only: compiled with the module, never shipped in the release AAR.
@file:Suppress("unused")

package ai.equal.ds.snippets

import ai.equal.ds.assets.EqualAssets
import ai.equal.ds.assets.EqualCategory
import ai.equal.ds.catalog.AlwaysRingScreen
import ai.equal.ds.catalog.CallHandlingCopy
import ai.equal.ds.catalog.CallHandlingScreen
import ai.equal.ds.catalog.CallHandlingStep
import ai.equal.ds.catalog.CallMode
import ai.equal.ds.catalog.CallSetupOverlay
import ai.equal.ds.catalog.ChatScreenSample
import ai.equal.ds.catalog.DirectCallsEmptyScreen
import ai.equal.ds.catalog.HomeScreenSample
import ai.equal.ds.catalog.KnownCallNotificationScreen
import ai.equal.ds.catalog.MeetAssistantHero
import ai.equal.ds.catalog.YouScreenSample
import ai.equal.ds.components.AlphabetIndex
import ai.equal.ds.components.CallNotification
import ai.equal.ds.components.CallNotificationState
import ai.equal.ds.components.ChoiceCard
import ai.equal.ds.components.ContactCheckRow
import ai.equal.ds.components.ContactPick
import ai.equal.ds.components.ContactPickMark
import ai.equal.ds.components.InfoNote
import ai.equal.ds.components.OngoingCallBar
import ai.equal.ds.components.PermissionSheet
import ai.equal.ds.components.SampleCallSummary
import ai.equal.ds.icons.EqualIcons
import ai.equal.ds.theme.EqualTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// ============================================================ Components

// snippet: call-handling / Choice cards
@Composable
internal fun CallHandlingChoiceCardsSnippet() {
    var mode by remember { mutableStateOf(CallMode.All) }
    Column(Modifier.selectableGroup(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        ChoiceCard(
            icon = EqualIcons.Globe,
            title = "All calls except",
            description = "Equal answers all your calls, except the people you choose.",
            selected = mode == CallMode.All,
            onSelect = { mode = CallMode.All },
        )
        ChoiceCard(
            icon = EqualIcons.Call,
            title = "Unknown numbers only",
            description = "Equal only answers calls from numbers you don’t have saved.",
            selected = mode == CallMode.Unknown,
            onSelect = { mode = CallMode.Unknown },
        )
        InfoNote(
            title = "Contact access needed",
            body = "Without it, the assistant cannot handle all calls.",
            link = "Give contacts permission",
            onLink = { /* ask for READ_CONTACTS */ },
        )
    }
}

// snippet: call-handling / Contact pickers
@Composable
internal fun CallHandlingContactPickersSnippet() {
    var picked by remember { mutableStateOf(true) }
    Column {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ContactPick(initials = "SU", name = "Mom", onClick = { /* add */ })
            ContactPick(initials = "VC", name = "Vijay Cousin", onClick = { /* remove */ }, mark = ContactPickMark.Remove)
        }
        Box(Modifier.fillMaxWidth()) {
            ContactCheckRow(initials = "AR", name = "Arnav Raj", phone = "+91 8986461540", checked = picked, onCheckedChange = { picked = it })
            AlphabetIndex(Modifier.align(Alignment.CenterEnd).padding(end = 2.dp), onPick = { /* jump to letter */ })
        }
    }
}

// snippet: call-handling / Permission sheet
@Composable
internal fun CallHandlingPermissionSheetSnippet() {
    // Overlay: fills the screen (scrim + sheet); pass inline = true to draw only the sheet
    PermissionSheet(
        art = EqualAssets.PermissionContacts,
        title = "Allow contacts permission",
        body = "I need your contact list to know who is calling.",
        cta = "Allow",
        onAllow = { /* request READ_CONTACTS */ },
        onClose = { /* hide the sheet */ },
    )
}

// snippet: call-handling / Call notification
@Composable
internal fun CallHandlingCallNotificationSnippet() {
    // Design reference + in-app preview. On device, build the same content into an ongoing
    // NotificationCompat (CATEGORY_CALL, custom RemoteViews); the system draws the shell.
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        CallNotification(name = "Mom", state = CallNotificationState.Answering)
        CallNotification(
            name = "Mom",
            state = CallNotificationState.Expanded,
            category = EqualCategory.HomeServices,
            summary = SampleCallSummary,
            replies = listOf("Takeover", "I’ll bring them all", "I’ll call back to get more"),
            onReply = { /* say it (it == "Takeover": pick up) */ },
            onAnswer = { /* pick up */ },
        )
        CallNotification(name = "Mom", state = CallNotificationState.Connecting)
    }
}

// snippet: call-handling / Ongoing call bar
@Composable
internal fun CallHandlingOngoingCallBarSnippet() {
    OngoingCallBar(elapsed = "02:34")
}

// ============================================================ Flow: Call Handling

// snippet: flow-call-handling / home-new
@Composable
internal fun FlowCallHandlingHomeNewSnippet() {
    EqualTheme {
        // No chats yet: the search field hides and the hero promo fills the home
        HomeScreenSample(
            chats = emptyList(),
            promo = { MeetAssistantHero() },
            assistantDescription = "Equal is managing your calls!",
        )
    }
}

// snippet: flow-call-handling / setup-prompt
@Composable
internal fun FlowCallHandlingSetupPromptSnippet() {
    EqualTheme {
        HomeScreenSample(
            chats = emptyList(),
            promo = { MeetAssistantHero() },
            assistantDescription = "Equal is managing your calls!",
            callSetup = CallSetupOverlay.Prompt,
            onCallSetup = { /* open CallHandlingScreen() */ },
        )
    }
}

// snippet: flow-call-handling / question
@Composable
internal fun FlowCallHandlingQuestionSnippet() {
    EqualTheme {
        // Runs live: question → (permissions) → exclude → Perfect!, or → All Sorted!
        CallHandlingScreen(onBack = { /* pop */ }, onDone = { /* pop to home */ })
    }
}

// snippet: flow-call-handling / exclude
@Composable
internal fun FlowCallHandlingExcludeSnippet() {
    EqualTheme {
        CallHandlingScreen(step = CallHandlingStep.Exclude, picked = emptyList())
    }
}

// snippet: flow-call-handling / exclude-picked
@Composable
internal fun FlowCallHandlingExcludePickedSnippet() {
    EqualTheme {
        CallHandlingScreen(step = CallHandlingStep.Exclude)
    }
}

// snippet: flow-call-handling / perfect
@Composable
internal fun FlowCallHandlingPerfectSnippet() {
    EqualTheme {
        CallHandlingScreen(step = CallHandlingStep.Perfect)
    }
}

// snippet: flow-call-handling / unknown
@Composable
internal fun FlowCallHandlingUnknownSnippet() {
    EqualTheme {
        CallHandlingScreen(mode = CallMode.Unknown)
    }
}

// snippet: flow-call-handling / whose
@Composable
internal fun FlowCallHandlingWhoseSnippet() {
    EqualTheme {
        // Copy experiment
        CallHandlingScreen(copy = CallHandlingCopy.Whose, mode = CallMode.Unknown)
    }
}

// snippet: flow-call-handling / sorted
@Composable
internal fun FlowCallHandlingSortedSnippet() {
    EqualTheme {
        CallHandlingScreen(step = CallHandlingStep.Sorted, mode = CallMode.Unknown)
    }
}

// snippet: flow-call-handling / no-permission
@Composable
internal fun FlowCallHandlingNoPermissionSnippet() {
    EqualTheme {
        CallHandlingScreen(copy = CallHandlingCopy.Whose, mode = CallMode.Unknown, contactsAllowed = false)
    }
}

// snippet: flow-call-handling / perm-contacts
@Composable
internal fun FlowCallHandlingPermContactsSnippet() {
    EqualTheme {
        CallHandlingScreen(copy = CallHandlingCopy.Whose, step = CallHandlingStep.PermContacts, contactsAllowed = false)
    }
}

// snippet: flow-call-handling / perm-history
@Composable
internal fun FlowCallHandlingPermHistorySnippet() {
    EqualTheme {
        CallHandlingScreen(copy = CallHandlingCopy.Whose, step = CallHandlingStep.PermHistory)
    }
}

// snippet: flow-call-handling / finish
@Composable
internal fun FlowCallHandlingFinishSnippet() {
    EqualTheme {
        HomeScreenSample(
            chats = emptyList(),
            promo = { MeetAssistantHero() },
            assistantDescription = "Equal is managing your calls!",
            callSetup = CallSetupOverlay.Finish,
            onCallSetup = { /* open CallHandlingScreen(step = CallHandlingStep.Exclude) */ },
            onSkipCallSetup = { /* answer everyone */ },
        )
    }
}

// snippet: flow-call-handling / always-ring
@Composable
internal fun FlowCallHandlingAlwaysRingSnippet() {
    EqualTheme {
        AlwaysRingScreen(onBack = { /* pop */ })
    }
}

// snippet: flow-call-handling / no-contacts
@Composable
internal fun FlowCallHandlingNoContactsSnippet() {
    EqualTheme {
        DirectCallsEmptyScreen(onBack = { /* pop */ }, onDone = { /* pop */ })
    }
}

// snippet: flow-call-handling / you
@Composable
internal fun FlowCallHandlingYouSnippet() {
    EqualTheme {
        YouScreenSample(onOpenCallHandling = { /* open CallHandlingScreen() */ })
    }
}

// ============================================================ Flow: Known-call notifications

// snippet: flow-known-calls / answering
@Composable
internal fun FlowKnownCallsAnsweringSnippet() {
    EqualTheme {
        CallNotification(name = "Mom", state = CallNotificationState.Answering)
    }
}

// snippet: flow-known-calls / summary
@Composable
internal fun FlowKnownCallsSummarySnippet() {
    EqualTheme {
        CallNotification(name = "Mom", state = CallNotificationState.Summary, summary = SampleCallSummary)
    }
}

// snippet: flow-known-calls / expanded
@Composable
internal fun FlowKnownCallsExpandedSnippet() {
    EqualTheme {
        CallNotification(
            name = "Mom",
            state = CallNotificationState.Expanded,
            replies = listOf("Takeover", "I’ll bring them all", "I’ll call back to get more"),
            onReply = { /* say it (it == "Takeover": pick up) */ },
            onAnswer = { /* pick up */ },
        )
    }
}

// snippet: flow-known-calls / connecting
@Composable
internal fun FlowKnownCallsConnectingSnippet() {
    EqualTheme {
        // Docs: on the coded wallpaper. On device the system draws the shade.
        KnownCallNotificationScreen(state = CallNotificationState.Connecting)
    }
}

// snippet: flow-known-calls / chat
@Composable
internal fun FlowKnownCallsChatSnippet() {
    EqualTheme {
        ChatScreenSample(
            name = "Mom",
            category = EqualCategory.HomeServices,
            live = true,
            ongoing = "02:34",
            onTakeover = { /* pick up */ },
        )
    }
}
