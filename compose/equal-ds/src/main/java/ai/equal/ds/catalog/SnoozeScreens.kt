package ai.equal.ds.catalog

import ai.equal.ds.components.ActivationFixes
import ai.equal.ds.components.AlertSheet
import ai.equal.ds.components.AlertSheetSpec
import ai.equal.ds.components.AlertSheets
import ai.equal.ds.components.AssistantBusy
import ai.equal.ds.components.AssistantToggleArt
import ai.equal.ds.components.ForceUpdateScreen
import ai.equal.ds.components.MeetAssistantCard
import ai.equal.ds.components.MeetAssistantLayout
import ai.equal.ds.components.SnoozeDuration
import ai.equal.ds.components.SnoozeDurationDialogContent
import ai.equal.ds.components.SystemNotificationPreview
import ai.equal.ds.components.TroubleshootList
import ai.equal.ds.theme.EqualTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/** The toggle in a Snooze state. */
data class SnoozeToggle(val on: Boolean, val busy: AssistantBusy? = null, val until: String? = null, val resting: Boolean = false)

/** What's drawn over Home. */
sealed interface SnoozeOverlay {
    data class Dialog(val selected: SnoozeDuration?) : SnoozeOverlay
    data class Notification(val body: String) : SnoozeOverlay
    data class Sheet(val spec: AlertSheetSpec) : SnoozeOverlay
}

/** A Snooze state (port of web `SnoozeState`). */
data class SnoozeState(val toggle: SnoozeToggle, val overlay: SnoozeOverlay? = null)

private val Off645 = SnoozeToggle(on = false, until = "Today 6:45pm")

/** Every designed state, keyed as on the flow page (`flow-snooze / <key>`). */
val SnoozeStates: Map<String, SnoozeState> = linkedMapOf(
    "on" to SnoozeState(SnoozeToggle(true)),
    "dialog-empty" to SnoozeState(SnoozeToggle(true), SnoozeOverlay.Dialog(null)),
    "dialog-selected" to SnoozeState(SnoozeToggle(true), SnoozeOverlay.Dialog(SnoozeDuration.Minutes15)),
    "deactivating" to SnoozeState(SnoozeToggle(true, AssistantBusy.Deactivating)),
    "off-notification" to SnoozeState(Off645, SnoozeOverlay.Notification("Equal AI will be back 6:45pm today")),
    "off" to SnoozeState(Off645),
    "off-resting" to SnoozeState(SnoozeToggle(on = false, resting = true)),
    "activating" to SnoozeState(Off645.copy(busy = AssistantBusy.Activating)),
    "on-again" to SnoozeState(SnoozeToggle(true)),
    "error-airplane" to SnoozeState(SnoozeToggle(true), SnoozeOverlay.Sheet(AlertSheets.Airplane)),
    "error-snooze-failed" to SnoozeState(SnoozeToggle(true), SnoozeOverlay.Sheet(AlertSheets.SnoozeFailed)),
    "error-offline" to SnoozeState(SnoozeToggle(true), SnoozeOverlay.Sheet(AlertSheets.Offline)),
    "error-activation-failed" to SnoozeState(Off645, SnoozeOverlay.Sheet(AlertSheets.ActivationFailed)),
)

/** The return time for a duration on the sample clock (12:30 pm): toggle "until" text and notification body. */
fun snoozeUntil(d: SnoozeDuration): Pair<String?, String> = when (d) {
    SnoozeDuration.Minutes15 -> "Today 12:45pm" to "Equal AI will be back 12:45pm today"
    SnoozeDuration.Hour1 -> "Today 1:30pm" to "Equal AI will be back 1:30pm today"
    SnoozeDuration.Hours6 -> "Today 6:30pm" to "Equal AI will be back 6:30pm today"
    SnoozeDuration.RestOfDay -> "Tomorrow" to "Equal AI will be back tomorrow"
    SnoozeDuration.Manual -> null to "Turn it back on from Chats anytime"
}

private fun backAt(until: String): AnnotatedString = buildAnnotatedString {
    append("Equal will be back ")
    withStyle(SpanStyle(fontWeight = FontWeight.W700)) { append(until) }
}

/** Home in one Snooze state. Port of web `SnoozeScreen`. */
@Composable
fun SnoozeScreen(
    state: SnoozeState,
    modifier: Modifier = Modifier,
    onToggle: (Boolean) -> Unit = {},
    onSelect: (SnoozeDuration) -> Unit = {},
    onConfirm: (SnoozeDuration) -> Unit = {},
    onCancel: () -> Unit = {},
    onSheetCta: () -> Unit = {},
    onSheetSecondary: () -> Unit = {},
    onSheetClose: () -> Unit = {},
) {
    val t = state.toggle
    HomeScreenSample(
        modifier = modifier,
        assistantArt = AssistantToggleArt.Phone,
        promo = { MeetAssistantCard(onCallClick = {}, layout = MeetAssistantLayout.Compact) },
        assistantControl = AssistantControl(
            on = t.on,
            busy = t.busy,
            offDescription = if (t.resting) "Assistant is resting." else null,
            offRich = t.until?.let(::backAt),
            onChange = onToggle,
        ),
        overlay = state.overlay?.let { o ->
            {
                when (o) {
                    is SnoozeOverlay.Dialog -> Box(
                        Modifier.fillMaxSize().background(EqualTheme.colors.bgOverlay)
                            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClickLabel = "Close", onClick = onCancel),
                        contentAlignment = Alignment.Center,
                    ) {
                        SnoozeDurationDialogContent(selected = o.selected, onSelect = onSelect, onConfirm = onConfirm, onCancel = onCancel)
                    }
                    is SnoozeOverlay.Notification -> Box(Modifier.fillMaxSize()) {
                        SystemNotificationPreview(
                            title = "Equal AI Assistant is off",
                            body = o.body,
                            modifier = Modifier.align(Alignment.TopCenter).padding(8.dp).fillMaxWidth(),
                        )
                    }
                    is SnoozeOverlay.Sheet -> AlertSheet(
                        spec = o.spec,
                        onCta = onSheetCta,
                        onSecondary = onSheetSecondary,
                        onClose = onSheetClose,
                        content = if (o.spec == AlertSheets.ActivationFailed) ({ TroubleshootList(ActivationFixes) }) else null,
                    )
                }
            }
        },
    )
}

/**
 * Live run: tap the switch → pick a duration (OK stays disabled until you do) → Deactivating →
 * Off with the heads-up → tap the switch → Activating → On. [fail] makes the next request fail
 * once with that sheet; Try again / Try now retries and succeeds.
 */
@Composable
fun SnoozeFlowSample(modifier: Modifier = Modifier, initial: String = "on", fail: AlertSheetSpec? = null) {
    var s by remember(initial) { mutableStateOf(SnoozeStates.getValue(initial)) }
    var until by remember { mutableStateOf(snoozeUntil(SnoozeDuration.Minutes15)) }
    var failed by remember(initial) { mutableStateOf(false) }
    var request by remember { mutableStateOf<AssistantBusy?>(null) }
    val activationFails = fail == AlertSheets.ActivationFailed
    LaunchedEffect(request) {
        when (request) {
            AssistantBusy.Deactivating -> {
                delay(1500)
                if (fail != null && !activationFails && !failed) {
                    failed = true
                    s = SnoozeState(SnoozeToggle(true), SnoozeOverlay.Sheet(fail))
                } else {
                    val (u, body) = until
                    s = SnoozeState(SnoozeToggle(on = false, until = u, resting = u == null), SnoozeOverlay.Notification(body))
                    delay(3500)
                    if (s.overlay is SnoozeOverlay.Notification) s = SnoozeState(s.toggle)
                }
                request = null
            }
            AssistantBusy.Activating -> {
                delay(1500)
                if (activationFails && !failed) {
                    failed = true
                    s = SnoozeState(s.toggle.copy(busy = null), SnoozeOverlay.Sheet(AlertSheets.ActivationFailed))
                } else {
                    s = SnoozeState(SnoozeToggle(true))
                }
                request = null
            }
            null -> Unit
        }
    }
    val deactivate = { s = SnoozeState(SnoozeToggle(true, AssistantBusy.Deactivating)); request = AssistantBusy.Deactivating }
    val activate = { s = SnoozeState(s.toggle.copy(busy = AssistantBusy.Activating)); request = AssistantBusy.Activating }
    val sheet = (s.overlay as? SnoozeOverlay.Sheet)?.spec
    val retry = { if (sheet == AlertSheets.ActivationFailed) activate() else deactivate() }
    SnoozeScreen(
        state = s,
        modifier = modifier,
        onToggle = { on ->
            if (s.toggle.busy == null) {
                if (!on) s = SnoozeState(s.toggle, SnoozeOverlay.Dialog(null)) else activate()
            }
        },
        onSelect = { s = SnoozeState(s.toggle, SnoozeOverlay.Dialog(it)) },
        onConfirm = { until = snoozeUntil(it); deactivate() },
        onCancel = { s = SnoozeState(s.toggle) },
        onSheetCta = { if (sheet == AlertSheets.Airplane || sheet == AlertSheets.Offline) s = SnoozeState(s.toggle) else retry() },
        onSheetSecondary = { if (sheet == AlertSheets.ActivationFailed) s = SnoozeState(s.toggle) else retry() },
        onSheetClose = { s = SnoozeState(s.toggle) },
    )
}

/** Force update: [hard] blocks the app; otherwise the dismissible soft-update sheet over Chats. */
@Composable
fun ForceUpdateSample(hard: Boolean, modifier: Modifier = Modifier, onUpdate: () -> Unit = {}, onLater: () -> Unit = {}) {
    if (hard) {
        ForceUpdateScreen(onUpdate = onUpdate, modifier = modifier)
        return
    }
    var open by remember { mutableStateOf(true) }
    val close = { open = false; onLater() }
    HomeScreenSample(
        modifier = modifier,
        promo = { MeetAssistantCard(onCallClick = {}, layout = MeetAssistantLayout.Compact) },
        overlay = if (open) ({ AlertSheet(spec = AlertSheets.UpdateAvailable, onCta = onUpdate, onSecondary = close, onClose = close) }) else null,
    )
}
