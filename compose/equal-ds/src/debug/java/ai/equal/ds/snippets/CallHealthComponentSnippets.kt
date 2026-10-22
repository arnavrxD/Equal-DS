// Compose code shown on the docs site (Call controls, Health check). Each function body is
// extracted by scripts/build-snippets.mjs; keep the `// snippet: <doc id> / <title>` line above it.
@file:Suppress("unused")

package ai.equal.ds.snippets

import ai.equal.ds.components.ActivationFixes
import ai.equal.ds.components.AlertSheet
import ai.equal.ds.components.AlertSheets
import ai.equal.ds.components.CallAvatar
import ai.equal.ds.components.CallControlBar
import ai.equal.ds.components.CallPhase
import ai.equal.ds.components.CallStatusLine
import ai.equal.ds.components.CallTimer
import ai.equal.ds.components.EqualSpinner
import ai.equal.ds.components.HealthCardAction
import ai.equal.ds.components.HealthCardStatus
import ai.equal.ds.components.HealthCheckCard
import ai.equal.ds.components.HealthHero
import ai.equal.ds.components.HealthHeroState
import ai.equal.ds.components.HealthStatusLabel
import ai.equal.ds.components.HealthTone
import ai.equal.ds.components.PermissionRow
import ai.equal.ds.components.PermissionStatus
import ai.equal.ds.components.SnoozeDuration
import ai.equal.ds.components.SnoozeDurationDialogContent
import ai.equal.ds.components.TroubleshootList
import ai.equal.ds.components.UpdateArt
import ai.equal.ds.icons.EqualSymbols
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp

// snippet: call-controls / Control bar
@Composable
internal fun CallControlBarSnippet() {
    var speaker by remember { mutableStateOf(false) }
    var muted by remember { mutableStateOf(false) }
    CallControlBar(
        speaker = speaker,
        muted = muted,
        onSpeaker = { speaker = !speaker },
        onMute = { muted = !muted },
        onEnd = { /* end the call */ },
    )
}

// snippet: call-controls / Avatar, status and timer
@Composable
internal fun CallAvatarStatusSnippet() {
    CallAvatar(ripples = true, size = 160.dp)
    CallStatusLine(phase = CallPhase.Connected)
    CallTimer(seconds = 83)
}

// snippet: health-check / Hero states
@Composable
internal fun HealthHeroStatesSnippet() {
    HealthHero(HealthHeroState.Idle, height = 120.dp)
    HealthHero(HealthHeroState.Loading, height = 120.dp)
    HealthHero(HealthHeroState.Pass, height = 120.dp)
    HealthHero(HealthHeroState.Fail, height = 120.dp)
}

// snippet: health-check / Check card with permission rows
@Composable
internal fun HealthCheckCardSnippet() {
    HealthCheckCard(
        title = "App permissions",
        status = HealthCardStatus.Issues,
        expandable = true,
        expanded = true,
        action = HealthCardAction("Grant Permission", onClick = { /* open settings */ }),
    ) {
        PermissionRow(EqualSymbols.phoneIncoming, "Phone", "Your assistant needs this to handle calls", PermissionStatus.Granted)
        PermissionRow(EqualSymbols.notifications, "Notification", "Required to send you call alerts", PermissionStatus.NotGranted, filledIcon = EqualSymbols.notificationsFill)
        PermissionRow(EqualSymbols.contacts, "Contacts", "Required to skip calls from saved contacts", PermissionStatus.Checking)
    }
}

// snippet: health-check / Status labels
@Composable
internal fun HealthStatusLabelsSnippet() {
    HealthStatusLabel(HealthTone.Neutral, "Pending")
    HealthStatusLabel(HealthTone.Progress, "Checking")
    HealthStatusLabel(HealthTone.Success, "Granted")
    HealthStatusLabel(HealthTone.Danger, "Not Granted")
}

// snippet: snooze / Duration dialog
@Composable
internal fun SnoozeDialogSnippet() {
    var v by remember { mutableStateOf<SnoozeDuration?>(null) }
    SnoozeDurationDialogContent(
        selected = v,
        onSelect = { v = it },
        onConfirm = { /* snooze */ },
        onCancel = { /* close */ },
    )
}

// snippet: snooze / Alert sheet
@Composable
internal fun AlertSheetSnippet() {
    AlertSheet(
        spec = AlertSheets.ActivationFailed,
        onCta = { /* retry */ },
        onSecondary = { /* support */ },
        onClose = { /* close */ },
    ) {
        TroubleshootList(ActivationFixes)
    }
}

// snippet: snooze / Update art and spinner
@Composable
internal fun UpdateArtSpinnerSnippet() {
    UpdateArt(size = 144.dp)
    EqualSpinner(size = 20.dp)
    EqualSpinner(size = 32.dp)
}
