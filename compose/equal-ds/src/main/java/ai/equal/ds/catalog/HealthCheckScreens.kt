package ai.equal.ds.catalog

import ai.equal.ds.components.DeviceScreen
import ai.equal.ds.components.EqualAppBar
import ai.equal.ds.components.EqualButton
import ai.equal.ds.components.EqualButtonSize
import ai.equal.ds.components.EqualText
import ai.equal.ds.components.HealthAlert
import ai.equal.ds.components.HealthCardAction
import ai.equal.ds.components.HealthCardStatus
import ai.equal.ds.components.HealthCheckCard
import ai.equal.ds.components.HealthHero
import ai.equal.ds.components.HealthHeroState
import ai.equal.ds.components.PermissionRow
import ai.equal.ds.components.PermissionStatus
import ai.equal.ds.components.SystemNotificationPreview
import ai.equal.ds.icons.EqualSymbols
import ai.equal.ds.theme.EqualTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/** One permission card of the Health Check screen. */
data class HcCard(val status: HealthCardStatus, val rows: List<PermissionStatus>, val expanded: Boolean = false, val grant: Boolean = false)

/** Carrier card action. */
enum class HcCarrierAction { Fix, Fixing, Retry }

/** The carrier card. */
data class HcCarrier(val status: HealthCardStatus, val subtitle: Boolean = false, val reasons: Boolean = false, val action: HcCarrierAction? = null)

/** Bottom CTA. */
enum class HcCta { Start, Checking, Home }

/** A Health Check screen state (port of web `HcState`). */
data class HcState(val hero: HealthHeroState, val app: HcCard, val phone: HcCard, val carrier: HcCarrier, val cta: HcCta, val last: String, val done: Boolean = false)

private data class HcRow(val icon: ImageVector, val filled: ImageVector, val title: String, val description: String)

private val AppRows = listOf(
    HcRow(EqualSymbols.phoneIncoming, EqualSymbols.phoneIncomingFill, "Phone", "Your assistant needs this to handle calls"),
    HcRow(EqualSymbols.notifications, EqualSymbols.notificationsFill, "Notification", "Required to send you call alerts"),
    HcRow(EqualSymbols.contacts, EqualSymbols.contactsFill, "Contacts", "Required to skip calls from saved contacts"),
)
private val PhoneRows = listOf(HcRow(EqualSymbols.layers, EqualSymbols.layersFill, "Overlay", "Required to show call controls over your screen"))
private val Reasons = "Carrier check failed. This may be due to:" to listOf("Your connection may be temporarily unavailable", "Your recharge plan might be inactive or expired")

private const val LastChecked = "Last checked: 2h ago"
private val Idle = PermissionStatus.Idle
private val Ok = PermissionStatus.Granted
private val No = PermissionStatus.NotGranted
private val Busy = PermissionStatus.Checking

private fun idle(expanded: Boolean = false) = HcState(
    HealthHeroState.Idle, HcCard(HealthCardStatus.None, listOf(Idle, Idle, Idle), expanded), HcCard(HealthCardStatus.None, listOf(Idle), expanded),
    HcCarrier(HealthCardStatus.None, subtitle = true), HcCta.Start, LastChecked,
)
private fun run(app: List<PermissionStatus>, hero: HealthHeroState = HealthHeroState.Loading, grant: Boolean = false) = HcState(
    hero, HcCard(HealthCardStatus.None, app, expanded = true, grant = grant), HcCard(HealthCardStatus.None, listOf(Idle)),
    HcCarrier(HealthCardStatus.None, subtitle = true), HcCta.Checking, LastChecked,
)
private val AppOk = HcCard(HealthCardStatus.Passed, listOf(Ok, Ok, Ok))
private val PhoneOk = HcCard(HealthCardStatus.Passed, listOf(Ok))
private fun late(phone: HcCard, carrier: HcCarrier, hero: HealthHeroState = HealthHeroState.Loading) = HcState(hero, AppOk, phone, carrier, HcCta.Checking, LastChecked)

/** Every designed state, keyed as on the flow page (`flow-health-check / <key>`). */
val HealthCheckStates: Map<String, HcState> = linkedMapOf(
    "idle" to idle(),
    "expanded" to idle(true),
    "phone-checking" to run(listOf(Busy, Idle, Idle)),
    "notification-checking" to run(listOf(Ok, Busy, Idle)),
    "contacts-checking" to run(listOf(Ok, No, Busy)),
    "permissions-failed" to run(listOf(Ok, No, No), HealthHeroState.Fail, grant = true),
    "overlay-pending" to late(HcCard(HealthCardStatus.None, listOf(PermissionStatus.Pending), expanded = true), HcCarrier(HealthCardStatus.Pending)),
    "overlay-checking" to late(HcCard(HealthCardStatus.None, listOf(Busy), expanded = true), HcCarrier(HealthCardStatus.Pending)),
    "overlay-failed" to late(HcCard(HealthCardStatus.None, listOf(No), expanded = true, grant = true), HcCarrier(HealthCardStatus.Pending), HealthHeroState.Fail),
    "carrier-pending" to late(PhoneOk, HcCarrier(HealthCardStatus.Pending)),
    "carrier-checking" to late(PhoneOk, HcCarrier(HealthCardStatus.Checking, subtitle = true)),
    "carrier-issues" to late(PhoneOk, HcCarrier(HealthCardStatus.Issues, subtitle = true, action = HcCarrierAction.Fix), HealthHeroState.Fail),
    "fixing" to late(PhoneOk, HcCarrier(HealthCardStatus.Issues, subtitle = true, action = HcCarrierAction.Fixing)),
    "fix-failed" to late(PhoneOk, HcCarrier(HealthCardStatus.Issues, reasons = true, action = HcCarrierAction.Retry), HealthHeroState.Fail),
    "fixing-again" to late(PhoneOk, HcCarrier(HealthCardStatus.Issues, reasons = true, action = HcCarrierAction.Fixing)),
    "check-failed" to late(PhoneOk, HcCarrier(HealthCardStatus.Failed, reasons = true, action = HcCarrierAction.Retry), HealthHeroState.Fail),
    "all-good" to HcState(HealthHeroState.Pass, AppOk, PhoneOk, HcCarrier(HealthCardStatus.Passed), HcCta.Home, "Last checked: 1s ago", done = true),
)

/** The Health Check screen for one state (390 wide; taller states scroll). Port of web `HealthCheckScreen`. */
@Composable
fun HealthCheckScreen(
    state: HcState,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onStart: () -> Unit = {},
    onHome: () -> Unit = {},
    onGrantApp: () -> Unit = {},
    onGrantPhone: () -> Unit = {},
    onFix: () -> Unit = {},
    onToggleApp: () -> Unit = {},
    onTogglePhone: () -> Unit = {},
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    val running = state.cta == HcCta.Checking
    Column(modifier.fillMaxSize().background(colors.bgPrimary)) {
        EqualAppBar(title = "Assistant Health Check", onBackClick = onBack, statusBar = true)
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            HealthHero(state.hero, height = 180.dp)
            Spacer(Modifier.height(16.dp))
            EqualText(
                text = if (state.done) "Check complete. Your assistant is good to go" else "Check your assistant’s health. See if it’s operational.",
                style = type.headline, color = colors.textPrimary, modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(24.dp))
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                HealthCheckCard(
                    title = "App permissions", status = state.app.status,
                    expandable = !running || state.app.status == HealthCardStatus.Passed, expanded = state.app.expanded, onToggle = onToggleApp,
                    action = if (state.app.grant) HealthCardAction("Grant Permission", onGrantApp) else null,
                ) { AppRows.forEachIndexed { i, r -> PermissionRow(r.icon, r.title, r.description, state.app.rows[i], filledIcon = r.filled) } }
                HealthCheckCard(
                    title = "Phone settings", status = state.phone.status,
                    expandable = !running || state.phone.status == HealthCardStatus.Passed, expanded = state.phone.expanded, onToggle = onTogglePhone,
                    action = if (state.phone.grant) HealthCardAction("Grant Permission", onGrantPhone) else null,
                ) { PhoneRows.forEachIndexed { i, r -> PermissionRow(r.icon, r.title, r.description, state.phone.rows[i], filledIcon = r.filled) } }
                HealthCheckCard(
                    title = "Carrier check", status = state.carrier.status,
                    subtitle = if (state.carrier.subtitle) "Verifies your calls reach the assistant" else null,
                    reasons = if (state.carrier.reasons) Reasons else null,
                    action = state.carrier.action?.let { HealthCardAction(if (it == HcCarrierAction.Retry) "Try Again" else "Fix Now", onFix, loading = it == HcCarrierAction.Fixing) },
                )
            }
        }
        // Pinned under the scrolling cards: it doesn't move when a section expands.
        Column(Modifier.fillMaxWidth().background(colors.bgPrimary).navigationBarsPadding().padding(start = 24.dp, end = 24.dp, top = 12.dp, bottom = 24.dp)) {
            EqualButton(
                text = when (state.cta) { HcCta.Home -> "Back To Home"; HcCta.Checking -> "Checking…"; HcCta.Start -> "Start Health Check" },
                onClick = if (state.cta == HcCta.Home) onHome else onStart,
                size = EqualButtonSize.Block, enabled = !running,
            )
            Spacer(Modifier.height(8.dp))
            EqualText(text = state.last, style = type.captionWrap, color = colors.textSecondary, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        }
    }
}

/**
 * Live run: Start → Phone ✓, Notification ✗, Contacts ✗ → Grant Permission → re-check ✓ → Overlay ✗
 * → Grant → ✓ → carrier finds issues → Fix Now → the first fix fails → Try Again → All good.
 */
@Composable
fun HealthCheckFlowSample(modifier: Modifier = Modifier, initial: String = "idle", onBack: () -> Unit = {}) {
    val h = HealthCheckStates
    var s by remember(initial) { mutableStateOf(h.getValue(initial)) }
    var queue by remember { mutableStateOf<Pair<List<HcState>, Long>>(emptyList<HcState>() to 900L) }
    var fixes by remember { mutableIntStateOf(0) }
    LaunchedEffect(queue) {
        for (next in queue.first) { delay(queue.second); s = next }
    }
    fun seq(steps: List<HcState>, gap: Long = 900L) { queue = steps to gap }
    HealthCheckScreen(
        state = s,
        modifier = modifier,
        onBack = onBack,
        onStart = { s = h.getValue("phone-checking"); seq(listOf(h.getValue("notification-checking"), h.getValue("contacts-checking"), h.getValue("permissions-failed"))) },
        onHome = { fixes = 0; s = h.getValue("idle") },
        onGrantApp = { s = run(listOf(Ok, Busy, Busy)); seq(listOf(h.getValue("overlay-pending"), h.getValue("overlay-checking"), h.getValue("overlay-failed"))) },
        onGrantPhone = { s = h.getValue("overlay-checking"); seq(listOf(h.getValue("carrier-pending"), h.getValue("carrier-checking"), h.getValue("carrier-issues"))) },
        onFix = {
            fixes += 1
            s = h.getValue(if (fixes > 1) "fixing-again" else "fixing")
            seq(listOf(h.getValue(if (fixes > 1) "all-good" else "fix-failed")), 1800L)
        },
        onToggleApp = { s = s.copy(app = s.app.copy(expanded = !s.app.expanded)) },
        onTogglePhone = { s = s.copy(phone = s.phone.copy(expanded = !s.phone.expanded)) },
    )
}

/** How the user reaches Health Check. */
enum class HealthCheckEntryKind { Notification, NotificationActions, Home }

/** The entry points: the notification (collapsed / with actions) or Home with the Fix Now strip. */
@Composable
fun HealthCheckEntry(entry: HealthCheckEntryKind, modifier: Modifier = Modifier, onFix: () -> Unit = {}) {
    if (entry == HealthCheckEntryKind.Home) {
        HomeScreenSample(modifier, healthAlert = HealthAlert(onFix = onFix))
        return
    }
    DeviceScreen(modifier) {
        SystemNotificationPreview(
            title = "Assistant health",
            body = "Calls are bypassing Equal AI. Fix now.",
            modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp),
            actions = if (entry == HealthCheckEntryKind.NotificationActions) listOf("Fix Now" to onFix, "Notify Later" to {}) else emptyList(),
        )
    }
}
