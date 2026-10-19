package ai.equal.ds.components

import ai.equal.ds.icons.EqualIcon
import ai.equal.ds.icons.EqualIcons
import ai.equal.ds.theme.EqualShadows
import ai.equal.ds.theme.EqualTheme
import ai.equal.ds.theme.eqShadow
import ai.equal.ds.tokens.EqualMotion
import ai.equal.ds.tokens.EqualPrimitives
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** CSS `ease-in-out`. */
private val SwitchEasing = CubicBezierEasing(0.42f, 0f, 0.58f, 1f)

/**
 * Switch (SPEC §3). Track 40 × 24 pill: on `button/primary`, off `button/disabled`.
 * Thumb 18 circle, inset 3, shadow `raised`: `text/on-action` when on (white on green in light,
 * black on lime in dark), `brand/white` when off. Animates over `motion.switchToggle` (500ms,
 * ease-in-out); instant when animations are off.
 *
 * Pass `onCheckedChange = null` when a parent row owns the toggle semantics
 * (e.g. [AssistantToggleCard], [ToggleRow]); the switch is then purely visual. A standalone
 * switch needs [contentDescription] as its TalkBack name.
 */
@Composable
fun EqualSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentDescription: String? = null,
) {
    val colors = EqualTheme.colors
    // Figma prototype: 500ms ease-in-out (`motion.switchToggle`); instant under reduced motion.
    val toggleMillis = if (rememberReduceMotion()) 0 else EqualMotion.switchToggle
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) 19.dp else 3.dp,
        animationSpec = tween(durationMillis = toggleMillis, easing = SwitchEasing),
        label = "eqSwitchThumb",
    )
    val trackColor by animateColorAsState(
        targetValue = if (checked) colors.buttonPrimary else colors.buttonDisabled,
        animationSpec = tween(durationMillis = toggleMillis, easing = SwitchEasing),
        label = "eqSwitchTrack",
    )
    val interactive = if (onCheckedChange != null) {
        Modifier
            .minimumInteractiveComponentSize()
            .then(if (contentDescription != null) Modifier.semantics { this.contentDescription = contentDescription } else Modifier)
            .toggleable(value = checked, enabled = enabled, role = Role.Switch, onValueChange = onCheckedChange)
    } else {
        Modifier
    }
    Box(
        modifier
            .then(interactive)
            .wrapContentSize(Alignment.Center)
            .size(width = 40.dp, height = 24.dp)
            .background(trackColor, CircleShape),
    ) {
        Box(
            Modifier
                .offset { IntOffset(thumbOffset.roundToPx(), 3.dp.roundToPx()) }
                .size(18.dp)
                .eqShadow(EqualShadows.raised, CircleShape)
                .background(if (checked) colors.textOnAction else EqualPrimitives.white, CircleShape),
        )
    }
}

/**
 * 18dp checkbox glyph (SPEC §5 select-all row). Checked = `button/primary`, unchecked = `text/tertiary`.
 * Pass `onCheckedChange = null` when the row owns the semantics; otherwise give it a
 * [contentDescription] when nothing beside it names it.
 */
@Composable
fun EqualCheckbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    size: Dp = 18.dp,
    /** Accessible name when no visible label sits beside it (web `ariaLabel`), e.g. "Select Arnav Raj". */
    contentDescription: String? = null,
) {
    val colors = EqualTheme.colors
    val described = if (contentDescription != null) Modifier.semantics { this.contentDescription = contentDescription } else Modifier
    val interactive = if (onCheckedChange != null) {
        Modifier
            .minimumInteractiveComponentSize()
            .then(described)
            .toggleable(value = checked, role = Role.Checkbox, onValueChange = onCheckedChange)
    } else {
        Modifier
    }
    Box(modifier.then(interactive), contentAlignment = Alignment.Center) {
        EqualIcon(
            imageVector = if (checked) EqualIcons.CheckBox else EqualIcons.CheckBoxOutlineBlank,
            contentDescription = null,
            tint = if (checked) colors.buttonPrimary else colors.textSecondary,
            size = size,
        )
    }
}

@EqualPreviews
@Composable
private fun SwitchPreview() {
    PreviewFrame {
        var on by remember { mutableStateOf(true) }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            EqualSwitch(checked = on, onCheckedChange = { on = it })
            EqualSwitch(checked = !on, onCheckedChange = { on = !it })
            EqualCheckbox(checked = on, onCheckedChange = { on = it })
            EqualCheckbox(checked = !on, onCheckedChange = { on = !it })
        }
    }
}
