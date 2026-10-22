package ai.equal.ds.components

import ai.equal.ds.assets.EqualAssetImage
import ai.equal.ds.assets.EqualAssets
import ai.equal.ds.icons.EqualIcon
import ai.equal.ds.icons.EqualIcons
import ai.equal.ds.icons.EqualSymbols
import ai.equal.ds.theme.EqualEasing
import ai.equal.ds.theme.EqualTheme
import ai.equal.ds.tokens.EqualMotion
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Circular progress (Assistant Snooze design): a 2dp `stroke/medium` track with a quarter
 * `button/primary` arc (lime in dark), one turn per 900 ms. Still under reduced motion.
 */
@Composable
fun EqualSpinner(modifier: Modifier = Modifier, size: Dp = 20.dp) {
    val colors = EqualTheme.colors
    val angle = if (rememberReduceMotion()) {
        0f
    } else {
        val t = rememberInfiniteTransition(label = "spinner")
        val a by t.animateFloat(0f, 360f, infiniteRepeatable(tween(900, easing = LinearEasing)), label = "angle")
        a
    }
    val track = colors.strokeMedium
    val arc = colors.buttonPrimary
    Box(
        modifier.size(size).drawBehind {
            val w = 2.dp.toPx()
            drawCircle(track, radius = this.size.minDimension / 2 - w / 2, style = Stroke(w))
            drawArc(arc, startAngle = angle - 90f, sweepAngle = 72f, useCenter = false, style = Stroke(w, cap = StrokeCap.Round))
        },
    )
}

/** How long the assistant stays off. */
enum class SnoozeDuration(val label: String) {
    Minutes15("15 Minutes"),
    Hour1("1 Hour"),
    Hours6("6 Hours"),
    RestOfDay("Rest of the day"),
    Manual("Until I turn it back on"),
}

/**
 * "Want to deactivate assistant?": [EqualDialogContent] with the duration radio group. Nothing is
 * preselected and OK stays disabled until a choice is made (designer note).
 */
@Composable
fun SnoozeDurationDialogContent(
    selected: SnoozeDuration?,
    onSelect: (SnoozeDuration) -> Unit,
    onConfirm: (SnoozeDuration) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    confirmLabel: String = "OK",
) {
    EqualDialogContent(
        title = "Want to deactivate assistant?",
        body = "Choose how long the assistant stays off",
        confirmLabel = confirmLabel,
        onConfirm = { selected?.let(onConfirm) },
        modifier = modifier,
        onDismiss = onCancel,
        confirmEnabled = selected != null,
    ) {
        EqualRadioGroup(
            options = SnoozeDuration.entries.map { EqualRadioOption(it, it.label) },
            selected = selected,
            onSelect = onSelect,
            gap = 20.dp,
        )
    }
}

/** One row of a [TroubleshootList]. */
data class TroubleshootItem(val icon: ImageVector, val label: String)

/** The fixes on "Assistant Activation Failed". */
val ActivationFixes = listOf(
    TroubleshootItem(EqualSymbols.flight, "Toggle Airplane Mode"),
    TroubleshootItem(EqualSymbols.cellTower, "Reset Network Connection"),
    TroubleshootItem(EqualSymbols.powerSettings, "Restart your Phone"),
)

/** Information-only list of fixes: centred icon + label rows (44), inset `stroke/medium` dividers, 1dp `stroke/dark` border, radius 12. */
@Composable
fun TroubleshootList(items: List<TroubleshootItem>, modifier: Modifier = Modifier) {
    val colors = EqualTheme.colors
    val shape = RoundedCornerShape(12.dp)
    Column(modifier.widthIn(max = 272.dp).fillMaxWidth().border(1.dp, colors.strokeDark, shape).padding(horizontal = 16.dp)) {
        items.forEachIndexed { i, it ->
            if (i > 0) EqualDivider(color = colors.strokeMedium)
            Row(
                Modifier.fillMaxWidth().defaultMinSize(minHeight = 44.dp).semantics(mergeDescendants = true) {},
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                EqualIcon(it.icon, contentDescription = null, tint = colors.textSecondary, size = 18.dp)
                EqualText(text = it.label, style = EqualTheme.type.body1, color = colors.textPrimary)
            }
        }
    }
}

/** The update robot with the coded upload badge (`button/primary`, 3dp `bg/primary` ring) and a soft `suggest/glow` behind it. */
@Composable
fun UpdateArt(modifier: Modifier = Modifier, size: Dp = 160.dp) {
    val colors = EqualTheme.colors
    val glow = colors.suggestGlow
    Box(
        modifier.size(size).drawBehind {
            drawCircle(Brush.radialGradient(listOf(glow, Color.Transparent), center = Offset(this.size.width / 2, this.size.height / 2), radius = this.size.minDimension * 0.65f))
        },
    ) {
        EqualAssetImage(EqualAssets.MascotUpdate, size = size)
        val badge = size * 0.225f
        Box(
            Modifier
                .offset(x = size * 0.66f, y = size * 0.62f)
                .size(badge)
                .background(colors.bgPrimary, CircleShape)
                .padding(3.dp)
                .background(colors.buttonPrimary, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            EqualIcon(EqualSymbols.send, contentDescription = null, tint = colors.textOnAction, size = size * 0.1125f)
        }
    }
}

/** The content of an [AlertSheet]: Snooze errors and the soft update. */
data class AlertSheetSpec(
    val title: String,
    val body: String,
    val cta: String,
    val art: EqualAssets? = null,
    val artSize: Dp = 96.dp,
    val ctaIcon: ImageVector? = null,
    val secondary: String? = null,
    val secondaryIcon: ImageVector? = null,
    val secondaryUnderline: Boolean = false,
    /** Coded art instead of an asset (the update robot). */
    val updateArt: Boolean = false,
)

/** The designed sheets. */
object AlertSheets {
    val Airplane = AlertSheetSpec("Airplane mode is on", "We couldn’t turn your assistant off because your phone is in airplane mode. Please turn it off and try again.", "Go to settings", EqualAssets.IllustrationAirplane, 116.dp, secondary = "Try Again")
    val SnoozeFailed = AlertSheetSpec("Assistant snooze failed", "We were unable to turn off your assistant. Please try again.", "Try again", EqualAssets.IllustrationWarning, 88.dp)
    val Offline = AlertSheetSpec("No internet connection", "Wi-Fi or mobile data required to turn off your assistant. Check your connection and try again.", "Go to settings", EqualAssets.IllustrationNoInternet, 96.dp, secondary = "Try Again")
    val ActivationFailed = AlertSheetSpec("Assistant Activation Failed", "We couldn’t turn your assistant on right now. We can help, try these.", "Try now", EqualAssets.IllustrationErrorSad, 88.dp, secondary = "Contact Support", secondaryIcon = EqualSymbols.phoneInTalk)
    val UpdateAvailable = AlertSheetSpec("A new version is here", "This update brings new improvements to how Equal AI handles your calls.", "Update Now", artSize = 144.dp, ctaIcon = EqualSymbols.download, secondary = "Maybe Later", secondaryUnderline = true, updateArt = true)
}

/**
 * Bottom sheet (radius 32 top, `bg/primary`) with the art straddling its top edge, a floating close,
 * title, body, optional [content] (e.g. [TroubleshootList]), a block CTA and an optional neutral
 * secondary. Over a `bg/overlay` scrim unless [inline].
 */
@Composable
fun AlertSheet(
    spec: AlertSheetSpec,
    onCta: () -> Unit,
    modifier: Modifier = Modifier,
    onSecondary: () -> Unit = {},
    onClose: (() -> Unit)? = null,
    inline: Boolean = false,
    content: (@Composable () -> Unit)? = null,
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    val shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    val half = spec.artSize / 2
    val sheet: @Composable (Modifier) -> Unit = { m ->
        Box(m.fillMaxWidth().padding(top = half)) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .clip(shape)
                    .background(colors.bgPrimary)
                    .clickable(interactionSource = rememberInteraction(), indication = null, onClick = {})
                    .semantics { paneTitle = spec.title },
            ) {
                Column(
                    Modifier.fillMaxWidth().navigationBarsPadding().padding(start = 24.dp, end = 24.dp, top = half + 8.dp, bottom = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    EqualText(text = spec.title, style = type.title, color = colors.textPrimary, textAlign = TextAlign.Center, modifier = Modifier.semantics { heading() })
                    Spacer(Modifier.height(4.dp))
                    EqualText(text = spec.body, style = type.body1, color = colors.textSecondary, textAlign = TextAlign.Center, modifier = Modifier.widthIn(max = 292.dp))
                    if (content != null) {
                        Spacer(Modifier.height(20.dp))
                        content()
                    }
                    Spacer(Modifier.height(28.dp))
                    EqualButton(text = spec.cta, onClick = onCta, size = EqualButtonSize.Block, leadingIcon = spec.ctaIcon)
                    if (spec.secondary != null) {
                        Spacer(Modifier.height(12.dp))
                        EqualTextButton(text = spec.secondary, onClick = onSecondary, neutral = true, underline = spec.secondaryUnderline, leadingIcon = spec.secondaryIcon)
                    }
                }
                if (onClose != null) {
                    EqualIconButton(icon = EqualIcons.Close, contentDescription = "Close", onClick = onClose, modifier = Modifier.align(Alignment.TopEnd).padding(12.dp))
                }
            }
            Box(Modifier.align(Alignment.TopCenter).offset(y = -half)) {
                if (spec.updateArt) UpdateArt(size = spec.artSize) else spec.art?.let { EqualAssetImage(it, size = spec.artSize) }
            }
        }
    }
    if (inline) {
        sheet(modifier)
        return
    }
    if (onClose != null) EqualBackHandler(onBack = onClose)
    val reduceMotion = rememberReduceMotion()
    val visible = remember { MutableTransitionState(reduceMotion).apply { targetState = true } }
    Box(modifier.fillMaxSize()) {
        Box(
            Modifier
                .matchParentSize()
                .background(colors.bgOverlay)
                .clickable(interactionSource = rememberInteraction(), indication = null, onClickLabel = "Close", onClick = onClose ?: {}),
        )
        AnimatedVisibility(
            visibleState = visible,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = slideInVertically(tween(EqualMotion.slow, easing = EqualEasing.standard)) { (it * 0.4f).toInt() } +
                fadeIn(tween(EqualMotion.slow, easing = EqualEasing.standard)),
        ) {
            sheet(Modifier)
        }
    }
}

/**
 * Hard update (installed version below the minimum): full screen, no close; Back exits the app.
 * Art + title + body are centred above the pinned CTA.
 */
@Composable
fun ForceUpdateScreen(onUpdate: () -> Unit, modifier: Modifier = Modifier, time: String = "12:00") {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    Column(modifier.fillMaxSize().background(colors.bgPrimary).semantics { paneTitle = "Update Equal AI App" }) {
        StatusBar(time = time)
        Column(
            Modifier.weight(1f).fillMaxWidth().padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            UpdateArt(size = 160.dp)
            Spacer(Modifier.height(16.dp))
            EqualText(text = "Update Equal AI App", style = type.headline.copy(fontWeight = FontWeight.W700), color = colors.textPrimary, textAlign = TextAlign.Center, modifier = Modifier.semantics { heading() })
            Spacer(Modifier.height(8.dp))
            EqualText(text = "This version is no longer supported. Update now to keep using Equal AI.", style = type.body1, color = colors.textSecondary, textAlign = TextAlign.Center, modifier = Modifier.widthIn(max = 300.dp))
        }
        Box(Modifier.navigationBarsPadding().padding(start = 24.dp, end = 24.dp, bottom = 24.dp)) {
            EqualButton(text = "Update Equal AI", onClick = onUpdate, size = EqualButtonSize.Block, leadingIcon = EqualSymbols.download)
        }
    }
}

