package ai.equal.ds.components

import ai.equal.ds.icons.EqualIcon
import ai.equal.ds.icons.EqualIcons
import ai.equal.ds.theme.EqualTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Figma Radio Button / Checkbox "Size": [S] 16 · [M] 20 · [L] 24 (label 12 / 14 / 16 Medium). */
enum class EqualControlSize(val box: Dp, val dot: Dp, val ring: Dp) { S(16.dp, 8.dp, 2.dp), M(20.dp, 12.dp, 3.dp), L(24.dp, 16.dp, 4.dp) }

@Composable
private fun controlLabel(size: EqualControlSize): TextStyle = when (size) {
    EqualControlSize.S -> EqualTheme.type.caption
    EqualControlSize.M -> EqualTheme.type.body2
    EqualControlSize.L -> EqualTheme.type.subheading
}

/** The radio mark alone: a 2dp `radio/ring` ring, or a `button/primary` disc with a `bg/surface` inner ring when [selected]. */
@Composable
fun EqualRadioMark(selected: Boolean, modifier: Modifier = Modifier, size: EqualControlSize = EqualControlSize.M) {
    val colors = EqualTheme.colors
    if (selected) {
        Box(modifier.size(size.box).background(colors.buttonPrimary, CircleShape), contentAlignment = Alignment.Center) {
            Box(Modifier.size(size.dot).border(size.ring, colors.bgSurface, CircleShape).background(colors.buttonPrimary, CircleShape))
        }
    } else {
        Box(modifier.size(size.box).border(2.dp, colors.radioRing, CircleShape))
    }
}

/**
 * Radio (Figma "Radio Button"): mark → 8 → label (`caption` / `body2` / `subheading`, `text/action`
 * when selected) over optional [support] (`captionWrap` `text/secondary`). Role.RadioButton; put a
 * set inside [EqualRadioGroup]. Touch target ≥ 48dp.
 */
@Composable
fun EqualRadio(
    selected: Boolean,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    label: String? = null,
    support: String? = null,
    size: EqualControlSize = EqualControlSize.M,
    enabled: Boolean = true,
    contentDescription: String? = null,
) {
    val colors = EqualTheme.colors
    val clickable = if (onClick != null) {
        Modifier.minimumInteractiveComponentSize().selectable(selected = selected, enabled = enabled, role = Role.RadioButton, onClick = onClick)
    } else Modifier
    val described = if (contentDescription != null) Modifier.semantics { this.contentDescription = contentDescription } else Modifier
    Row(modifier.then(clickable).then(described), verticalAlignment = Alignment.Top) {
        EqualRadioMark(selected, size = size)
        if (label != null || support != null) {
            Spacer(Modifier.width(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                if (label != null) EqualText(text = label, style = controlLabel(size), color = if (selected) colors.textAction else colors.textPrimary)
                if (support != null) EqualText(text = support, style = EqualTheme.type.captionWrap, color = colors.textSecondary)
            }
        }
    }
}

/** One option of an [EqualRadioGroup]. */
data class EqualRadioOption<T>(val value: T, val label: String, val support: String? = null, val enabled: Boolean = true)

/** A vertical set of [EqualRadio]s; TalkBack reads it as one group ("1 of 3"). */
@Composable
fun <T> EqualRadioGroup(
    options: List<EqualRadioOption<T>>,
    selected: T?,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
    size: EqualControlSize = EqualControlSize.M,
    gap: Dp = 16.dp,
) {
    Column(modifier.selectableGroup(), verticalArrangement = Arrangement.spacedBy(gap)) {
        options.forEach { o ->
            EqualRadio(selected = o.value == selected, onClick = { onSelect(o.value) }, label = o.label, support = o.support, size = size, enabled = o.enabled)
        }
    }
}

/**
 * Checkbox (Figma "Checkbox"): a 16 / 20 / 24 box, radius 4, 1.5dp `radio/ring` edge; checked fills
 * `button/primary` with a `text/on-action` check and the label turns `text/action`.
 */
@Composable
fun EqualCheckboxField(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    label: String? = null,
    support: String? = null,
    size: EqualControlSize = EqualControlSize.L,
    contentDescription: String? = null,
) {
    val colors = EqualTheme.colors
    val shape = RoundedCornerShape(4.dp)
    val toggle = if (onCheckedChange != null) {
        Modifier.minimumInteractiveComponentSize().toggleable(value = checked, role = Role.Checkbox, onValueChange = onCheckedChange)
    } else Modifier
    val described = if (contentDescription != null) Modifier.semantics { this.contentDescription = contentDescription } else Modifier
    Row(modifier.then(toggle).then(described), verticalAlignment = Alignment.Top) {
        Box(
            Modifier.size(size.box).then(if (checked) Modifier.background(colors.buttonPrimary, shape) else Modifier.border(1.5.dp, colors.radioRing, shape)),
            contentAlignment = Alignment.Center,
        ) {
            if (checked) EqualIcon(EqualIcons.Check, contentDescription = null, tint = colors.textOnAction, size = size.box * 0.7f)
        }
        if (label != null || support != null) {
            Spacer(Modifier.width(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                if (label != null) EqualText(text = label, style = controlLabel(size), color = if (checked) colors.textAction else colors.textPrimary)
                if (support != null) EqualText(text = support, style = EqualTheme.type.captionWrap, color = colors.textSecondary)
            }
        }
    }
}
