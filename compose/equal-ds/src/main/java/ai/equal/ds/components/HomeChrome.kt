package ai.equal.ds.components

import androidx.compose.ui.semantics.clearAndSetSemantics
import ai.equal.ds.assets.EqualAssetImage
import ai.equal.ds.assets.EqualAssets
import ai.equal.ds.icons.EqualIcon
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import ai.equal.ds.icons.EqualSymbols
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.clickable
import ai.equal.ds.icons.EqualIcons
import ai.equal.ds.theme.DmSans
import ai.equal.ds.theme.EqualShadows
import ai.equal.ds.theme.EqualTheme
import ai.equal.ds.theme.eqShadow
import ai.equal.ds.tokens.EqualRadius
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.TextStyle
import androidx.compose.foundation.text.BasicText
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/** Material 3 status-bar height on the 390 × 800 frame. */
val EqualStatusBarHeight = 52.dp

/**
 * Status bar, Material 3 (SPEC §1), for catalog screens and flows only: in the app draw
 * edge-to-edge and pad content with `WindowInsets.statusBars`. 52 high, 24 side padding. Time in
 * the system font (Roboto) Medium 14/20, tracking 0.1, [color] (default `text/primary`); wifi, signal_cellular_4_bar
 * (filled triangle) and an upright battery_full, 18dp, gap 4. Background defaults to `bg/primary`.
 */
@Composable
fun StatusBar(
    modifier: Modifier = Modifier,
    time: String = "12:30",
    background: Color = EqualTheme.colors.bgPrimary,
    /** Time and glyph colour (e.g. `profile-hero/status-text` over the You hero). */
    color: Color = EqualTheme.colors.textPrimary,
) {
    val tint = color
    // A drawn mock of the system bar: TalkBack must not read its fake time.
    Row(
        modifier
            .fillMaxWidth()
            .clearAndSetSemantics {}
            .height(EqualStatusBarHeight)
            .background(background)
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BasicText(
            text = time,
            style = TextStyle(
                color = tint,
                fontFamily = DmSans,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                letterSpacing = 0.1.sp,
                fontFeatureSettings = "tnum",
            ),
            maxLines = 1,
        )
        Spacer(Modifier.weight(1f))
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
            EqualIcon(EqualIcons.StatusWifi, contentDescription = null, tint = tint, size = 18.dp)
            EqualIcon(EqualIcons.StatusSignal, contentDescription = null, tint = tint, size = 18.dp)
            EqualIcon(EqualIcons.StatusBattery, contentDescription = null, tint = tint, size = 18.dp)
        }
    }
}

/**
 * Top bar (SPEC §2). 56 high, h16. Full vector [EqualLogo] (20dp high, `text/action`) + 8 + [PlanBadge]
 * ([planTone]: gold, or the red "PLAN EXPIRED" badge).
 */
@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    planLabel: String? = "Unlimited",
    planTone: PlanBadgeTone = PlanBadgeTone.Paid,
    trailing: @Composable RowScope.() -> Unit = {},
) {
    Row(
        modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        EqualLogo(height = 20.dp)
        if (planLabel != null) {
            Spacer(Modifier.width(8.dp))
            PlanBadge(text = planLabel, tone = planTone)
        }
        Spacer(Modifier.weight(1f))
        trailing()
    }
}

/** Art on the [AssistantToggleCard] (Component 25 experiment). */
enum class AssistantToggleArt {
    /** The robot mascot (default, final). */
    Robot,

    /** The 3D phone with sparkles ([EqualAssets.AssistantPhone]); 50% opacity while off. */
    Phone,
}

/**
 * Assistant availability toggle card (SPEC §3). 64 high, radius 20, padding 12.
 * On: `assistant-toggle/bg` with an `action/stroke/unread` (at 40%) radial glow at each end.
 * Off: `bg/surface` + 1dp `stroke/subtle`.
 * The whole card is the toggle (role Switch, 64dp target); the [EqualSwitch] inside is visual.
 * The parent supplies the 12dp side margins.
 */
@Composable
fun AssistantToggleCard(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    titleOn: String = "AI Assistant is On",
    titleOff: String = "AI Assistant is Off",
    description: String = "Equal is managing your unknown calls!",
    /** Shown while off, matching web. */
    descriptionOff: String = "Unknown calls will ring normally",
    /** Leading art: the robot (default) or the phone experiment. */
    art: AssistantToggleArt = AssistantToggleArt.Robot,
    /** Assistant Healthcheck: a pink strip under the card ("Poor Assistant Health · Fix Now →"). */
    healthAlert: HealthAlert? = null,
    /** Assistant Snooze: a spinner replaces the switch while the request runs; the card isn't interactive. */
    busy: AssistantBusy? = null,
    /** Rich off line (snooze: "Equal will be back **Today 6:45pm**"); wins over [descriptionOff]. */
    descriptionOffRich: AnnotatedString? = null,
) {
    if (healthAlert != null) {
        val colors = EqualTheme.colors
        Column(modifier.fillMaxWidth().clip(RoundedCornerShape(EqualRadius.rXl)).background(colors.expiryBannerDangerBg)) {
            AssistantToggleCard(checked, onCheckedChange, Modifier, titleOn, titleOff, description, descriptionOff, art, busy = busy, descriptionOffRich = descriptionOffRich)
            Row(
                Modifier.fillMaxWidth().defaultMinSize(minHeight = 44.dp).padding(start = 16.dp, end = 8.dp, top = 4.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                EqualText(text = healthAlert.label, style = EqualTheme.type.body2, color = colors.textPrimary, modifier = Modifier.weight(1f))
                Row(
                    Modifier.minimumInteractiveComponentSize().clip(CircleShape).clickable(role = Role.Button, onClick = healthAlert.onFix).padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    EqualText(text = healthAlert.cta, style = EqualTheme.type.buttonSmall, color = colors.textDanger)
                    Spacer(Modifier.width(4.dp))
                    EqualIcon(EqualSymbols.arrowForward, contentDescription = null, tint = colors.textDanger, size = 16.dp)
                }
            }
        }
        return
    }
    val colors = EqualTheme.colors
    val shape = RoundedCornerShape(EqualRadius.rXl)
    // Deactivating keeps the on look; activating keeps the off look until the request succeeds.
    val lit = if (busy != null) busy == AssistantBusy.Deactivating else checked
    val fillModifier = if (lit) {
        // Figma: the flat `assistant-toggle/bg` fill with a soft `action/stroke/unread` glow at each end.
        val glow = colors.actionStrokeUnread.copy(alpha = colors.actionStrokeUnread.alpha * 0.4f)
        Modifier
            .clip(shape)
            .background(colors.assistantToggleBg)
            .drawBehind {
                val radius = size.width * 0.5f
                for (x in listOf(0f, size.width)) {
                    drawRect(
                        Brush.radialGradient(
                            colors = listOf(glow, Color.Transparent),
                            center = Offset(x, size.height / 2f),
                            radius = radius,
                        ),
                    )
                }
            }
    } else {
        Modifier
            .background(colors.bgSurface, shape)
            .border(1.dp, colors.strokeSubtle, shape)
    }
    Row(
        modifier
            .fillMaxWidth()
            .height(64.dp)
            .then(fillModifier)
            .then(
                if (busy == null) Modifier.toggleable(value = checked, role = Role.Switch, onValueChange = onCheckedChange)
                else Modifier.semantics { stateDescription = "Busy" },
            )
            .padding(start = 12.dp, end = 10.dp, top = 12.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (art == AssistantToggleArt.Phone) {
            EqualAssetImage(EqualAssets.AssistantPhone, size = 36.dp, modifier = Modifier.alpha(if (lit) 1f else 0.5f))
        } else {
            EqualMascot(size = 36.dp, dimmed = !lit)
        }
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f).semantics { liveRegion = LiveRegionMode.Polite }) {
            EqualText(
                text = when (busy) { AssistantBusy.Deactivating -> "Deactivating your assistant"; AssistantBusy.Activating -> "Activating your assistant"; null -> if (checked) titleOn else titleOff },
                style = EqualTheme.type.subheading,
                color = colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            val line = when (busy) { AssistantBusy.Deactivating -> "This will take just a moment…"; AssistantBusy.Activating -> "Resuming on your Equal AI Assistant"; null -> null }
            if (line == null && !checked && descriptionOffRich != null) {
                EqualText(text = descriptionOffRich, style = EqualTheme.type.captionRegular, color = colors.textSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
            } else {
                EqualText(
                    text = line ?: if (checked) description else descriptionOff,
                    style = EqualTheme.type.captionRegular,
                    color = colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        if (busy != null) {
            Box(Modifier.size(width = 40.dp, height = 24.dp), contentAlignment = Alignment.Center) { EqualSpinner(size = 20.dp) }
        } else {
            EqualSwitch(checked = checked, onCheckedChange = null)
        }
    }
}

/** [AssistantToggleCard] while a snooze request runs. */
enum class AssistantBusy { Deactivating, Activating }

/** The Home health strip under [AssistantToggleCard]. */
data class HealthAlert(val onFix: () -> Unit, val label: String = "Poor Assistant Health", val cta: String = "Fix Now")

/**
 * Home header (SPEC §5 "Home"): [StatusBar] + [TopBar] + [AssistantToggleCard] stacked on
 * `bg/primary` with the `header` shadow below. Give it `Modifier.zIndex(1f)` inside a Column so
 * the shadow draws over the list.
 * [trailing] fills the end of the [TopBar] (e.g. the search button). Monetization experiments:
 * [planTone] (the red "PLAN EXPIRED" badge), [assistantArt] (toggle art) and [below] (drawn under
 * the toggle, e.g. the "Payment processing" [ProcessingStrip] card).
 */
@Composable
fun HomeHeader(
    assistantOn: Boolean,
    onAssistantChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    time: String = "12:30",
    planLabel: String? = "Unlimited",
    /** Replaces the assistant toggle, e.g. a [SystemStatusCard] while Equal AI is down. */
    status: (@Composable () -> Unit)? = null,
    /** End of the [TopBar], e.g. the search button. */
    trailing: @Composable RowScope.() -> Unit = {},
    /** The assistant toggle's line while on (new users: "Equal is managing your calls!"); null keeps the default. */
    assistantDescription: String? = null,
    /** Plan badge tone: gold, or the red "PLAN EXPIRED". */
    planTone: PlanBadgeTone = PlanBadgeTone.Paid,
    /** Toggle art experiment (Component 25). */
    assistantArt: AssistantToggleArt = AssistantToggleArt.Robot,
    /** Under the toggle, e.g. `ProcessingStrip(variant = ProcessingStripVariant.Card)`. */
    below: (@Composable () -> Unit)? = null,
    /** The toggle's line while off; null keeps the default. */
    assistantOffDescription: String? = null,
    /** Assistant Healthcheck: the pink "Poor Assistant Health · Fix Now" strip under the toggle. */
    healthAlert: HealthAlert? = null,
    /** Assistant Snooze: spinner in place of the switch. */
    assistantBusy: AssistantBusy? = null,
    /** Assistant Snooze: "Equal will be back **Today 6:45pm**". */
    assistantOffRich: AnnotatedString? = null,
    /** Equal-to-Equal: the incoming or ongoing call strip under the status bar. */
    callStrip: (@Composable () -> Unit)? = null,
) {
    val colors = EqualTheme.colors
    Column(
        modifier
            .fillMaxWidth()
            .eqShadow(EqualShadows.header)
            .background(colors.bgPrimary),
    ) {
        StatusBar(time = time, background = if (callStrip != null) colors.callStripBg else colors.bgPrimary)
        callStrip?.invoke()
        TopBar(planLabel = planLabel, trailing = trailing, planTone = planTone)
        Box(Modifier.padding(start = 12.dp, end = 12.dp, bottom = 12.dp)) {
            if (status != null) {
                status()
            } else {
                AssistantToggleCard(
                    checked = assistantOn,
                    onCheckedChange = onAssistantChange,
                    description = assistantDescription ?: "Equal is managing your unknown calls!",
                    descriptionOff = assistantOffDescription ?: "Unknown calls will ring normally",
                    art = assistantArt,
                    healthAlert = healthAlert,
                    busy = assistantBusy,
                    descriptionOffRich = assistantOffRich,
                )
            }
        }
        below?.invoke()
    }
}

@EqualPreviews
@Composable
private fun HomeHeaderPreview() {
    PreviewFrame(padding = 0.dp) {
        var on by remember { mutableStateOf(true) }
        HomeHeader(assistantOn = on, onAssistantChange = { on = it })
        VSpace(16.dp)
        AssistantToggleCard(checked = false, onCheckedChange = {}, modifier = Modifier.padding(horizontal = 12.dp))
        VSpace(16.dp)
        AssistantToggleCard(checked = true, onCheckedChange = {}, modifier = Modifier.padding(horizontal = 12.dp), art = AssistantToggleArt.Phone)
        VSpace(16.dp)
        HomeHeader(assistantOn = false, onAssistantChange = {}, planLabel = "Plan Expired", planTone = PlanBadgeTone.Expired)
    }
}
