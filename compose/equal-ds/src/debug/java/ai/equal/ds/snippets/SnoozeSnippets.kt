// Compose code shown on the docs site (Flows → Assistant snooze, Force update). Each function body
// is extracted by scripts/build-snippets.mjs; keep the `// snippet: <doc id> / <key>` line above it.
@file:Suppress("unused")

package ai.equal.ds.snippets

import ai.equal.ds.catalog.ForceUpdateSample
import ai.equal.ds.catalog.SnoozeFlowSample
import ai.equal.ds.components.AlertSheets
import ai.equal.ds.theme.EqualTheme
import androidx.compose.runtime.Composable

// snippet: flow-snooze / on
@Composable
internal fun FlowSnoozeOnSnippet() {
    EqualTheme {
        SnoozeFlowSample(initial = "on")
    }
}

// snippet: flow-snooze / dialog-empty
@Composable
internal fun FlowSnoozeDialogEmptySnippet() {
    EqualTheme {
        SnoozeFlowSample(initial = "dialog-empty")
    }
}

// snippet: flow-snooze / dialog-selected
@Composable
internal fun FlowSnoozeDialogSelectedSnippet() {
    EqualTheme {
        SnoozeFlowSample(initial = "dialog-selected")
    }
}

// snippet: flow-snooze / deactivating
@Composable
internal fun FlowSnoozeDeactivatingSnippet() {
    EqualTheme {
        SnoozeFlowSample(initial = "deactivating")
    }
}

// snippet: flow-snooze / off-notification
@Composable
internal fun FlowSnoozeOffNotificationSnippet() {
    EqualTheme {
        SnoozeFlowSample(initial = "off-notification")
    }
}

// snippet: flow-snooze / off
@Composable
internal fun FlowSnoozeOffSnippet() {
    EqualTheme {
        SnoozeFlowSample(initial = "off")
    }
}

// snippet: flow-snooze / off-resting
@Composable
internal fun FlowSnoozeOffRestingSnippet() {
    EqualTheme {
        SnoozeFlowSample(initial = "off-resting")
    }
}

// snippet: flow-snooze / activating
@Composable
internal fun FlowSnoozeActivatingSnippet() {
    EqualTheme {
        SnoozeFlowSample(initial = "activating")
    }
}

// snippet: flow-snooze / on-again
@Composable
internal fun FlowSnoozeOnAgainSnippet() {
    EqualTheme {
        SnoozeFlowSample(initial = "on-again")
    }
}

// snippet: flow-snooze / error-airplane
@Composable
internal fun FlowSnoozeErrorAirplaneSnippet() {
    EqualTheme {
        SnoozeFlowSample(initial = "error-airplane")
    }
}

// snippet: flow-snooze / error-snooze-failed
@Composable
internal fun FlowSnoozeErrorSnoozeFailedSnippet() {
    EqualTheme {
        SnoozeFlowSample(initial = "error-snooze-failed")
    }
}

// snippet: flow-snooze / error-offline
@Composable
internal fun FlowSnoozeErrorOfflineSnippet() {
    EqualTheme {
        SnoozeFlowSample(initial = "error-offline")
    }
}

// snippet: flow-snooze / error-activation-failed
@Composable
internal fun FlowSnoozeErrorActivationFailedSnippet() {
    EqualTheme {
        SnoozeFlowSample(initial = "error-activation-failed")
    }
}

// snippet: flow-snooze / live
@Composable
internal fun FlowSnoozeLiveSnippet() {
    EqualTheme {
        SnoozeFlowSample()
    }
}

// snippet: flow-snooze / live-fail
@Composable
internal fun FlowSnoozeLiveFailSnippet() {
    EqualTheme {
        SnoozeFlowSample(fail = AlertSheets.SnoozeFailed)
    }
}

// snippet: flow-force-update / hard
@Composable
internal fun FlowForceUpdateHardSnippet() {
    EqualTheme {
        ForceUpdateSample(hard = true, onUpdate = { /* open the Play Store / IMMEDIATE update */ })
    }
}

// snippet: flow-force-update / soft
@Composable
internal fun FlowForceUpdateSoftSnippet() {
    EqualTheme {
        ForceUpdateSample(hard = false, onUpdate = { /* FLEXIBLE update */ }, onLater = { /* snooze this version */ })
    }
}
