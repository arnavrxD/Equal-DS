package ai.equal.ds.components

import ai.equal.ds.R
import com.airbnb.lottie.compose.LottieConstants
import ai.equal.ds.icons.EqualIcon
import ai.equal.ds.icons.EqualSymbols
import ai.equal.ds.theme.EqualTheme
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Hero states (Assistant Healthcheck design). */
enum class HealthHeroState { Idle, Loading, Pass, Fail }

/**
 * The hero (shield + status coin): the supplied Lotties, chained by state. Idle = loading frame 0;
 * Loading loops; Pass / Fail play the transition from the previous state and hold the last frame;
 * leaving Pass / Fail for Loading plays check→loading / cross→loading, then the loop.
 */
@Composable
fun HealthHero(state: HealthHeroState, modifier: Modifier = Modifier, height: Dp = 200.dp) {
    var from by remember { mutableStateOf(state) }
    val settled = from == state
    val transition = when (from to state) {
        HealthHeroState.Loading to HealthHeroState.Pass, HealthHeroState.Idle to HealthHeroState.Pass -> R.raw.lottie_health_loading_check
        HealthHeroState.Loading to HealthHeroState.Fail, HealthHeroState.Idle to HealthHeroState.Fail -> R.raw.lottie_health_loading_cross
        HealthHeroState.Fail to HealthHeroState.Loading -> R.raw.lottie_health_cross_loading
        HealthHeroState.Pass to HealthHeroState.Loading -> R.raw.lottie_health_check_loading
        HealthHeroState.Fail to HealthHeroState.Pass -> R.raw.lottie_health_cross_check
        HealthHeroState.Pass to HealthHeroState.Fail -> R.raw.lottie_health_check_cross
        else -> null
    }
    val raw = if (!settled && transition != null) transition else when (state) {
        HealthHeroState.Pass -> R.raw.lottie_health_loading_check
        HealthHeroState.Fail -> R.raw.lottie_health_loading_cross
        else -> R.raw.lottie_health_loading
    }
    val loop = raw == R.raw.lottie_health_loading && state == HealthHeroState.Loading
    val end = state == HealthHeroState.Pass || state == HealthHeroState.Fail
    val pinned = settled && end
    key(raw, pinned) {
        EqualLottie(
            raw, modifier, width = height * 0.8f, height = height,
            iterations = if (loop) LottieConstants.IterateForever else 1,
            stillAtEnd = end,
            still = pinned || state == HealthHeroState.Idle,
            onComplete = { if (!settled) from = state },
        )
    }
}

/** Tone of a [HealthStatusLabel]. */
enum class HealthTone { Neutral, Progress, Success, Danger }

/** Status label: spinner / filled check / warning (14) + `caption`, announced politely. */
@Composable
fun HealthStatusLabel(tone: HealthTone, label: String, modifier: Modifier = Modifier) {
    val colors = EqualTheme.colors
    val color = when (tone) { HealthTone.Success -> colors.textSuccess; HealthTone.Danger -> colors.textDanger; HealthTone.Progress -> colors.textPrimary; HealthTone.Neutral -> colors.textSecondary }
    val icon: ImageVector? = when (tone) { HealthTone.Progress -> EqualSymbols.progressActivity; HealthTone.Success -> EqualSymbols.checkCircleFill; HealthTone.Danger -> EqualSymbols.warningFill; HealthTone.Neutral -> null }
    Row(modifier.semantics { liveRegion = LiveRegionMode.Polite }, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        if (icon != null) EqualIcon(icon, contentDescription = null, tint = color, size = 14.dp, modifier = if (tone == HealthTone.Progress) Modifier.rotate(rememberSpin()) else Modifier)
        EqualText(text = label, style = EqualTheme.type.caption, color = color, maxLines = 1)
    }
}

@Composable
private fun rememberSpin(): Float {
    if (rememberReduceMotion()) return 0f
    val t = rememberInfiniteTransition(label = "spin")
    val a by t.animateFloat(0f, 360f, infiniteRepeatable(tween(1000, easing = LinearEasing), RepeatMode.Restart), label = "a")
    return a
}

/** Status of a [PermissionRow]. */
enum class PermissionStatus { Idle, Pending, Checking, Granted, NotGranted }

/**
 * 48 status ring (2dp) around a 40 `health-check/icon-bg` disc with a 24 icon. Pending dims the icon;
 * Checking spins a `text/primary` arc on the `stroke/medium` track; Granted = `stroke/focus`;
 * NotGranted = `text/danger` with the filled glyph.
 */
@Composable
fun StatusRing(status: PermissionStatus, icon: ImageVector, filledIcon: ImageVector = icon, modifier: Modifier = Modifier, size: Dp = 48.dp) {
    val colors = EqualTheme.colors
    val ring = when (status) { PermissionStatus.Granted -> colors.strokeFocus; PermissionStatus.NotGranted -> colors.textDanger; else -> colors.strokeMedium }
    val spin = if (status == PermissionStatus.Checking) rememberSpin() else 0f
    val arc = colors.textPrimary
    Box(
        modifier.size(size).drawBehind {
            val w = 2.dp.toPx()
            drawCircle(ring, radius = this.size.minDimension / 2 - w / 2, style = Stroke(w))
            if (status == PermissionStatus.Checking) drawArc(arc, startAngle = spin - 90f, sweepAngle = 90f, useCenter = false, style = Stroke(w, cap = StrokeCap.Round))
        },
        contentAlignment = Alignment.Center,
    ) {
        Box(Modifier.size(size - 8.dp).background(colors.healthCheckIconBg, CircleShape), contentAlignment = Alignment.Center) {
            EqualIcon(
                if (status == PermissionStatus.NotGranted) filledIcon else icon, contentDescription = null, size = 24.dp,
                tint = when (status) { PermissionStatus.Pending -> colors.textPlaceholder; PermissionStatus.NotGranted -> colors.textDanger; else -> colors.textSecondary },
            )
        }
    }
}

private fun PermissionStatus.label(): Pair<HealthTone, String>? = when (this) {
    PermissionStatus.Pending -> HealthTone.Neutral to "Pending"
    PermissionStatus.Checking -> HealthTone.Progress to "Checking"
    PermissionStatus.Granted -> HealthTone.Success to "Granted"
    PermissionStatus.NotGranted -> HealthTone.Danger to "Not Granted"
    PermissionStatus.Idle -> null
}

/** Permission row: ring → title (`body2`) over description (`captionWrap`) → status. Reads "Phone, Granted". */
@Composable
fun PermissionRow(icon: ImageVector, title: String, description: String, status: PermissionStatus, modifier: Modifier = Modifier, filledIcon: ImageVector = icon) {
    val colors = EqualTheme.colors
    val l = status.label()
    Row(
        modifier.fillMaxWidth().defaultMinSize(minHeight = 64.dp).padding(vertical = 8.dp).padding(end = 16.dp)
            .semantics(mergeDescendants = true) { if (l != null) stateDescription = l.second },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        StatusRing(status, icon, filledIcon)
        Column(Modifier.weight(1f)) {
            EqualText(text = title, style = EqualTheme.type.body2, color = colors.textPrimary)
            EqualText(text = description, style = EqualTheme.type.captionWrap, color = colors.textSecondary)
        }
        if (l != null) HealthStatusLabel(l.first, l.second)
    }
}

/** Header status of a [HealthCheckCard]. */
enum class HealthCardStatus { None, Pending, Checking, Passed, Issues, Failed }

/** An inline card action (Grant Permission / Fix Now / Try Again); [loading] shows "Fixing". */
data class HealthCardAction(val label: String, val onClick: () -> Unit, val loading: Boolean = false)

/**
 * Check card: radius 16, `bg/surface`, 1dp `stroke/subtle`. Header (title `subheading` + status +
 * drop-down caret when [expandable]), optional [subtitle], [rows] separated by dividers, failure
 * [reasons], and an inline md block [action].
 */
@Composable
fun HealthCheckCard(
    title: String,
    modifier: Modifier = Modifier,
    status: HealthCardStatus = HealthCardStatus.None,
    subtitle: String? = null,
    expandable: Boolean = false,
    expanded: Boolean = false,
    onToggle: () -> Unit = {},
    reasons: Pair<String, List<String>>? = null,
    action: HealthCardAction? = null,
    rows: (@Composable () -> Unit)? = null,
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    val shape = RoundedCornerShape(16.dp)
    val label = when (status) {
        HealthCardStatus.Pending -> HealthTone.Neutral to "Pending"
        HealthCardStatus.Checking -> HealthTone.Progress to "Checking"
        HealthCardStatus.Passed -> HealthTone.Success to "All Good"
        HealthCardStatus.Issues -> HealthTone.Danger to "Issues Found"
        HealthCardStatus.Failed -> HealthTone.Danger to "Check Failed"
        HealthCardStatus.None -> null
    }
    val caret = expandable && status != HealthCardStatus.Checking
    val rotation by animateFloatAsState(if (expanded) 180f else 0f, label = "caret")
    Column(modifier.fillMaxWidth().clip(shape).background(colors.bgSurface).border(1.dp, colors.strokeSubtle, shape)) {
        Row(
            Modifier.fillMaxWidth().defaultMinSize(minHeight = 56.dp)
                .then(if (caret) Modifier.clickable(role = Role.Button, onClick = onToggle).semantics { stateDescription = if (expanded) "Expanded" else "Collapsed" } else Modifier)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Column(Modifier.weight(1f)) {
                EqualText(text = title, style = type.subheading, color = colors.textPrimary)
                if (subtitle != null) EqualText(text = subtitle, style = type.captionWrap, color = colors.textSecondary)
            }
            if (label != null) HealthStatusLabel(label.first, label.second)
            if (caret) EqualIcon(EqualSymbols.arrowDropDown, contentDescription = null, tint = colors.textSecondary, size = 24.dp, modifier = Modifier.rotate(rotation))
        }
        if (expanded && rows != null) {
            Column(Modifier.padding(start = 16.dp)) { EqualDivider(); rows() }
        }
        if (reasons != null) {
            Column(Modifier.padding(horizontal = 16.dp).padding(bottom = 4.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                EqualText(text = reasons.first, style = type.captionWrap, color = colors.textSecondary)
                reasons.second.forEach { EqualText(text = "•  $it", style = type.captionWrap, color = colors.textSecondary, modifier = Modifier.padding(start = 8.dp)) }
            }
        }
        if (action != null) {
            Box(Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 16.dp)) {
                EqualButton(text = if (action.loading) "Fixing" else action.label, onClick = action.onClick, size = EqualButtonSize.Md, loading = action.loading, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

