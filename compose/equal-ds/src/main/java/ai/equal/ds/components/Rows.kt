package ai.equal.ds.components

import ai.equal.ds.icons.EqualIcon
import ai.equal.ds.icons.EqualIcons
import ai.equal.ds.theme.EqualTheme
import ai.equal.ds.theme.tabular
import ai.equal.ds.tokens.EqualRadius
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.size
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.border
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.semantics.CollectionInfo
import androidx.compose.ui.semantics.collectionInfo
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Section header (derived). `overline` `text/secondary` (uppercased in the string), padding
 * [inset] h (default 20, the You tab inset), 24 top, 12 bottom. Optional trailing `buttonSmall`
 * `text/action` action (e.g. "Clear all").
 */
@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onActionClick: () -> Unit = {},
    inset: Dp = 20.dp,
    /** [SectionHeaderSize.Label]: 13sp medium `text/secondary` (Contact Info's "CALL HISTORY"). */
    size: SectionHeaderSize = SectionHeaderSize.Overline,
) {
    val colors = EqualTheme.colors
    val label = size == SectionHeaderSize.Label
    Row(
        modifier
            .fillMaxWidth()
            .padding(start = inset, end = inset, top = if (label) 30.dp else 24.dp, bottom = if (label) 18.dp else 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        EqualText(
            text = title.uppercase(),
            style = if (label) EqualTheme.type.caption.copy(fontSize = 13.sp, lineHeight = 18.sp, letterSpacing = 0.13.sp) else EqualTheme.type.overline,
            color = if (label) colors.textSecondary else colors.textSecondary,
            maxLines = 1,
            modifier = Modifier.weight(1f).semantics { heading() },
        )
        if (actionLabel != null) {
            // 48dp touch area around the text action (layout unchanged).
            Box(
                Modifier
                    .touchTarget()
                    .clickable(role = Role.Button, onClick = onActionClick),
                contentAlignment = Alignment.Center,
            ) {
                EqualText(
                    text = actionLabel,
                    style = EqualTheme.type.buttonSmall,
                    color = colors.textAction,
                    maxLines = 1,
                )
            }
        }
    }
}

/**
 * Settings row (derived). Min height 64, padding 16 h / 12 v. Optional leading icon 24 →
 * column [label `subheading` (+ NEW badge, gap 8), description `text/secondary`] → trailing.
 * Description is `caption` Regular on one line; set [descriptionWraps] to switch to `captionWrap`.
 * Leading icon tint `text/primary`; [showNewBadge] adds the solid green [NewBadge].
 * Trailing defaults to a 22dp `text/placeholder` chevron; pass [trailing] to replace it (e.g. a
 * switch). Group rows with [RowGroup].
 */
@Composable
fun SettingsRow(
    label: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    descriptionWraps: Boolean = false,
    leadingIcon: ImageVector? = null,
    showNewBadge: Boolean = false,
    onClick: (() -> Unit)? = null,
    /** Puts [leadingIcon] (20dp) in a 36dp circle of this colour (e.g. `preset/tint-lime` on Active Instructions). */
    leadingIconBg: Color? = null,
    trailing: (@Composable () -> Unit)? = {
        EqualIcon(EqualIcons.ChevronRight, contentDescription = null, tint = EqualTheme.colors.textPlaceholder, size = 22.dp)
    },
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    val clickModifier = if (onClick != null) Modifier.clickable(role = Role.Button, onClick = onClick) else Modifier
    Row(
        modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 64.dp)
            .background(colors.bgSurface)
            .then(clickModifier)
            .padding(start = 16.dp, end = 12.dp, top = 12.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leadingIcon != null) {
            if (leadingIconBg != null) {
                Box(Modifier.size(36.dp).background(leadingIconBg, CircleShape), contentAlignment = Alignment.Center) {
                    EqualIcon(leadingIcon, contentDescription = null, tint = colors.textPrimary, size = 20.dp)
                }
            } else {
                EqualIcon(leadingIcon, contentDescription = null, tint = colors.textPrimary, size = 24.dp)
            }
            Spacer(Modifier.width(12.dp))
        }
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                EqualText(
                    text = label,
                    style = type.subheading,
                    color = colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )
                if (showNewBadge) {
                    Spacer(Modifier.width(8.dp))
                    NewBadge()
                }
            }
            if (description != null) {
                if (descriptionWraps) {
                    EqualText(text = description, style = type.captionWrap, color = colors.textSecondary)
                } else {
                    EqualText(
                        text = description,
                        style = type.captionRegular,
                        color = colors.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
        if (trailing != null) {
            Spacer(Modifier.width(12.dp))
            trailing()
        }
    }
}

/** Toggle row (derived): a [SettingsRow] whose whole surface toggles a trailing [EqualSwitch]. */
@Composable
fun ToggleRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    description: String? = null,
    descriptionWraps: Boolean = false,
    leadingIcon: ImageVector? = null,
    showNewBadge: Boolean = false,
    enabled: Boolean = true,
) {
    SettingsRow(
        label = label,
        modifier = modifier.toggleable(value = checked, enabled = enabled, role = Role.Switch, onValueChange = onCheckedChange),
        description = description,
        descriptionWraps = descriptionWraps,
        leadingIcon = leadingIcon,
        showNewBadge = showNewBadge,
        onClick = null,
        trailing = { EqualSwitch(checked = checked, onCheckedChange = null, enabled = enabled) },
    )
}

/**
 * A rounded group of settings rows (You tab): [inset] side margin (default 20), radius 16,
 * `bg/surface`, 1dp `stroke/subtle` between rows. The dividers are the group's background
 * showing through 1dp gaps, so children must be opaque (like [SettingsRow] / [ToggleRow]).
 */
@Composable
fun RowGroup(
    modifier: Modifier = Modifier,
    inset: Dp = 20.dp,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = RoundedCornerShape(EqualRadius.rLg)
    Column(
        modifier
            .padding(horizontal = inset)
            .fillMaxWidth()
            .clip(shape)
            .background(EqualTheme.colors.strokeSubtle),
        verticalArrangement = Arrangement.spacedBy(1.dp),
        content = content,
    )
}

enum class SectionHeaderSize { Overline, Label }

/**
 * The call-history card (Contact Info design): radius 16, `bg/surface`, 1dp dividers between rows
 * (`stroke/subtle`; `stroke/medium` in dark), and in dark a 1dp `stroke/subtle` outline. Side
 * margin [inset] (22 on Contact Info). Exposed to TalkBack as a list; pass [itemCount] (the number
 * of rows) so it announces how many calls there are.
 */
@Composable
fun CallHistoryList(
    modifier: Modifier = Modifier,
    inset: Dp = 22.dp,
    itemCount: Int = -1,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = EqualTheme.colors
    val shape = RoundedCornerShape(EqualRadius.rLg)
    Column(
        modifier
            .semantics { collectionInfo = CollectionInfo(rowCount = itemCount, columnCount = 1) }
            .padding(horizontal = inset)
            .fillMaxWidth()
            .clip(shape)
            .background(if (colors.isDark) colors.strokeMedium else colors.strokeSubtle)
            .then(if (colors.isDark) Modifier.border(1.dp, colors.strokeSubtle, shape) else Modifier),
        verticalArrangement = Arrangement.spacedBy(1.dp),
        content = content,
    )
}

/** Call direction for [CallHistoryRow]. */
enum class CallDirection { Incoming, Outgoing, Missed }

/**
 * Call history row (Contact Info design): min height 63, padding 0 / 21 / 0 / 22, `bg/surface`.
 * Direction icon 24 (phone incoming / outgoing / missed; missed in `text/danger`, others
 * `text/primary`) → 10 → [label "Incoming Call" / "Outgoing Call" / "Missed Call", `bodyLarge`
 * `text/primary` (never red); [meta] 11sp Regular `text/secondary`] → [time] `body1`
 * `text/secondary`. Put rows in a [CallHistoryList]. [onCallClick] adds a trailing call button.
 */
@Composable
fun CallHistoryRow(
    direction: CallDirection,
    meta: String,
    time: String,
    modifier: Modifier = Modifier,
    label: String = when (direction) {
        CallDirection.Incoming -> "Incoming Call"
        CallDirection.Outgoing -> "Outgoing Call"
        CallDirection.Missed -> "Missed Call"
    },
    onCallClick: (() -> Unit)? = null,
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    val icon = when (direction) {
        CallDirection.Incoming -> EqualIcons.PhoneIncoming
        CallDirection.Outgoing -> EqualIcons.PhoneOutgoing
        CallDirection.Missed -> EqualIcons.PhoneMissed
    }
    Row(
        modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 63.dp)
            // One TalkBack stop per call (label, meta, time); the call button stays separate.
            .semantics(mergeDescendants = true) {}
            .background(colors.bgSurface)
            .padding(start = 22.dp, end = 21.dp, top = 10.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        EqualIcon(
            icon,
            contentDescription = null,
            tint = if (direction == CallDirection.Missed) colors.textDanger else colors.textPrimary,
            size = 24.dp,
        )
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            EqualText(text = label, style = type.bodyLarge.copy(lineHeight = 20.sp), color = colors.textPrimary, maxLines = 1)
            EqualText(text = meta, style = type.microRegular.copy(fontSize = 11.sp, lineHeight = 14.sp), color = colors.textSecondary, maxLines = 1)
        }
        Spacer(Modifier.width(12.dp))
        EqualText(text = time, style = type.body1.tabular(), color = colors.textSecondary, maxLines = 1)
        if (onCallClick != null) {
            PlainIconButton(EqualIcons.Call, "Call back", onCallClick, tint = colors.textAction)
        }
    }
}

@EqualPreviews
@Composable
private fun RowsPreview() {
    PreviewFrame(padding = 0.dp) {
        SectionHeader(title = "Notifications", actionLabel = "Clear all")
        SettingsRow(label = "Call screening", description = "Choose who Equal AI answers", showNewBadge = true, onClick = {})
        var on by remember { mutableStateOf(true) }
        ToggleRow(label = "Spam protection", checked = on, onCheckedChange = { on = it }, description = "Block known spam numbers")
        SettingsRow(
            label = "Language",
            description = "Equal AI replies in the caller's language when it can, and falls back to English otherwise.",
            descriptionWraps = true,
            onClick = {},
        )
        RowGroup {
            CallHistoryRow(CallDirection.Incoming, meta = "Today • 30 Sec.", time = "02:30 PM")
            CallHistoryRow(CallDirection.Missed, meta = "Yesterday • 0 Sec.", time = "02:30 PM", onCallClick = {})
        }
    }
}
