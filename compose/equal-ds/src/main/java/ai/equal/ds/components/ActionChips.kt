package ai.equal.ds.components

import ai.equal.ds.assets.EqualAssetImage
import ai.equal.ds.assets.EqualAssets
import ai.equal.ds.icons.EqualIcon
import ai.equal.ds.icons.EqualIcons
import ai.equal.ds.theme.EqualShadows
import ai.equal.ds.theme.EqualTheme
import ai.equal.ds.theme.eqGlassInset
import ai.equal.ds.theme.eqPressFeedback
import ai.equal.ds.theme.eqShadow
import ai.equal.ds.theme.toBrush
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

/** Data for an action chip: sentence-case label + optional Unicode emoji (e.g. "📞"). */
@Immutable
data class ActionChipData(val label: String, val emoji: String? = null)

/** Small action chip styles (SPEC §9). */
enum class SmallActionChipStyle { Unread, Read, Outlined }

/**
 * Small action chip (SPEC §9), used outside chats (chat list row 3).
 * 30 high pill, padding 8/10: emoji 12, the label size (Unicode, [EmojiGlyph]) → 6 → `buttonSmall` label → 4 →
 * chevron 14.
 * - Unread: `gradient/chip-small-unread`, 1dp `action/stroke/unread`, `glass`, label `text/action`.
 * - Read: `gradient/chip-small-read`, 1dp `stroke/subtle`, `glass`, label `text/secondary`.
 * - Outlined: transparent, 1dp `action/stroke/unread`, label `text/secondary`.
 * Touch target 48 high (extra space is outside the visual).
 *
 * [locked] (Actions touchpoint, free plan): the 14dp crown-lock replaces the emoji, the fill is
 * `paywall/hero-top` mixed 45% into `bg/surface` with a 1dp `paywall/card-stroke`, and the label
 * and chevron turn `text/gold` (any [style]). Tapping should open the paywall.
 */
@Composable
fun SmallActionChip(
    label: String,
    emoji: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: SmallActionChipStyle = SmallActionChipStyle.Unread,
    locked: Boolean = false,
) {
    val colors = EqualTheme.colors
    val gradients = EqualTheme.gradients
    val shape = CircleShape
    val interaction = rememberInteraction()
    val surface: Modifier = if (locked) {
        Modifier
            .background(lerp(colors.bgSurface, colors.paywallHeroTop, 0.45f), shape)
            .border(1.dp, colors.paywallCardStroke, shape)
    } else when (style) {
        // Figma: a soft outer shadow only, no inner bottom edge.
        SmallActionChipStyle.Unread -> Modifier
            .eqShadow(EqualShadows.chipUnread, shape)
            .background(gradients.chipSmallUnread.toBrush(), shape)
            .border(1.dp, colors.actionStrokeUnread, shape)
        SmallActionChipStyle.Read -> Modifier
            .eqShadow(EqualShadows.chipRead, shape)
            .background(gradients.chipSmallRead.toBrush(), shape)
            .border(1.dp, colors.strokeSubtle, shape)
        SmallActionChipStyle.Outlined -> Modifier
            .border(1.dp, colors.actionStrokeUnread, shape)
    }
    val content = when {
        locked -> colors.textGold
        style == SmallActionChipStyle.Unread -> colors.chipLabelUnread
        style == SmallActionChipStyle.Read -> colors.chipLabelRead
        else -> colors.textSecondary
    }
    Row(
        modifier
            .minimumInteractiveComponentSize()
            .eqPressFeedback(interaction, shape)
            .height(30.dp)
            .then(surface)
            .clip(shape)
            .clickable(interactionSource = interaction, indication = null, role = Role.Button, onClick = onClick)
            .padding(start = 8.dp, end = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (locked) {
            EqualAssetImage(EqualAssets.CrownLock, size = 14.dp, contentDescription = "Locked")
            Spacer(Modifier.width(6.dp))
        } else if (emoji != null) {
            EmojiGlyph(emoji = emoji, size = 12.dp)
            Spacer(Modifier.width(6.dp))
        }
        EqualText(text = label, style = EqualTheme.type.buttonSmall, color = content, maxLines = 1)
        Spacer(Modifier.width(4.dp))
        EqualIcon(imageVector = EqualIcons.ChevronRight, contentDescription = null, tint = content, size = 14.dp)
    }
}

/**
 * Large action chip (SPEC §10), post-call inside chats and in the setup chat. 40 high pill,
 * padding 10 / 12. Emoji 18 (Unicode) → 6 → `button` `text/primary`.
 * - [LargeActionChipState.Unread] (the suggested action): `gradient/chip-large`,
 *   1dp `action/stroke/unread`.
 * - [LargeActionChipState.Read] (the alternative, e.g. Edit): `gradient/chip-small-read`,
 *   1dp `stroke/subtle`, `glass` shadow.
 *
 * [enabled] `false` (e.g. while the assistant is sending) draws the chip at 45% and ignores taps.
 */
@Composable
fun LargeActionChip(
    label: String,
    emoji: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    state: LargeActionChipState = LargeActionChipState.Unread,
    enabled: Boolean = true,
) {
    val colors = EqualTheme.colors
    val gradients = EqualTheme.gradients
    val shape = CircleShape
    val interaction = rememberInteraction()
    val surface = when (state) {
        LargeActionChipState.Unread -> Modifier
            .background(gradients.chipLarge.toBrush(), shape)
            .border(1.dp, colors.actionStrokeUnread, shape)
        LargeActionChipState.Read -> Modifier
            .eqShadow(EqualShadows.glass, shape)
            .background(gradients.chipSmallRead.toBrush(), shape)
            .eqGlassInset(colors.actionBottomColor, shape)
            .border(1.dp, colors.strokeSubtle, shape)
    }
    Row(
        modifier
            .minimumInteractiveComponentSize()
            .alpha(if (enabled) 1f else 0.45f)
            .eqPressFeedback(interaction, shape)
            .height(40.dp)
            .then(surface)
            .clip(shape)
            .clickable(interactionSource = interaction, indication = null, enabled = enabled, role = Role.Button, onClick = onClick)
            .padding(start = 10.dp, end = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (emoji != null) {
            EmojiGlyph(emoji = emoji, size = 14.dp)
            Spacer(Modifier.width(6.dp))
        }
        EqualText(text = label, style = EqualTheme.type.button, color = colors.textPrimary, maxLines = 1)
    }
}

/** [LargeActionChip] states. */
enum class LargeActionChipState { Unread, Read }

@EqualPreviews
@Composable
private fun ActionChipsPreview() {
    PreviewFrame {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SmallActionChip("Call back", "📞", onClick = {})
            SmallActionChip("Update address", "✏️", onClick = {}, style = SmallActionChipStyle.Read)
        }
        SmallActionChip("Call back", "📞", onClick = {}, style = SmallActionChipStyle.Outlined)
        SmallActionChip("Call back", "📞", onClick = {}, locked = true)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            LargeActionChip("Takeover", "📞", onClick = {})
            LargeActionChip("Ask to reschedule", "🗓️", onClick = {})
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            LargeActionChip("Edit", "✍️", onClick = {}, state = LargeActionChipState.Read)
            LargeActionChip("Confirm", "👍", onClick = {})
        }
    }
}
