package ai.equal.ds.components

import ai.equal.ds.assets.EqualCategory
import ai.equal.ds.icons.EqualIcon
import ai.equal.ds.icons.EqualIcons
import ai.equal.ds.theme.EqualShadows
import ai.equal.ds.theme.EqualTheme
import ai.equal.ds.theme.eqGlassInset
import ai.equal.ds.theme.eqPressFeedback
import ai.equal.ds.theme.eqShadow
import ai.equal.ds.theme.tabular
import ai.equal.ds.theme.toBrush
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.layout.Layout

/** Chat header variants (SPEC §5). Selection mode is [SelectionChatHeader]. */
enum class ChatHeaderVariant {
    /** Trailing: phone + more-vert. */
    Default,

    /**
     * Live call / visitor. Trailing: only the [TakeoverPill]; no call or more-options button
     * (`onCallClick` / `onMoreClick` are ignored).
     */
    LiveCall,
}

/**
 * In-conversation header (SPEC §5). 64 high, `bg/surface`, shadow `header`, h12.
 * Back (48 target) → 4 → avatar 36 → 12 → [name `subheading`, subtitle `caption` Regular
 * `text/secondary`] → trailing actions. Set [subtitleIsPhone] for tabular figures.
 * Trailing: [ChatHeaderVariant.Default] shows call + more-options; [ChatHeaderVariant.LiveCall]
 * shows only the Takeover pill, which replaces both.
 * The avatar is the [category] image (default [EqualCategory.NA], the unknown caller), or
 * [initials] for a saved contact. [official] (Equal AI's own conversation) shows the 36dp
 * [AiAvatar] orb instead.
 * Give it `Modifier.zIndex(1f)` in a Column so the shadow draws over the content.
 */
@Composable
fun ChatHeader(
    name: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    subtitleIsPhone: Boolean = true,
    variant: ChatHeaderVariant = ChatHeaderVariant.Default,
    onCallClick: () -> Unit = {},
    onMoreClick: () -> Unit = {},
    onTakeoverClick: () -> Unit = {},
    takeoverLabel: String = "Takeover",
    category: EqualCategory? = EqualCategory.NA,
    initials: String? = null,
    /** Equal AI's own chat: the orb avatar. */
    official: Boolean = false,
) {
    val colors = EqualTheme.colors
    HeaderContainer(modifier) {
        PlainIconButton(EqualIcons.Back, "Back", onBackClick, tint = colors.textPrimary)
        Spacer(Modifier.width(4.dp))
        if (official) {
            AiAvatar(size = AiAvatarSize.Medium)
        } else {
            ContactAvatar(size = 36.dp, category = category, initials = initials)
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            EqualText(
                text = name,
                style = EqualTheme.type.subheading,
                color = colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.semantics { heading() },
            )
            if (subtitle != null) {
                val style = EqualTheme.type.captionRegular
                EqualText(
                    text = subtitle,
                    style = if (subtitleIsPhone) style.tabular() else style,
                    color = colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        when (variant) {
            ChatHeaderVariant.Default -> {
                PlainIconButton(EqualIcons.Call, "Call", onCallClick, tint = colors.textPrimary)
                PlainIconButton(EqualIcons.MoreVert, "More options", onMoreClick, tint = colors.textPrimary)
            }
            ChatHeaderVariant.LiveCall -> {
                // The pill replaces both the call and the more-options buttons.
                Spacer(Modifier.width(8.dp))
                TakeoverPill(label = takeoverLabel, onClick = onTakeoverClick)
            }
        }
    }
}

/**
 * Takeover pill (SPEC §5 live call). 34 high pill, h14, `gradient/takeover`,
 * 1dp `takeover/stroke`, shadow `glass`. Filled phone icon 18 `takeover/text` + 6 + `button` `takeover/text`.
 */
@Composable
fun TakeoverPill(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Takeover",
) {
    val colors = EqualTheme.colors
    val shape = CircleShape
    val interaction = rememberInteraction()
    Row(
        modifier
            .minimumInteractiveComponentSize()
            .eqPressFeedback(interaction, shape)
            .height(34.dp)
            .eqShadow(EqualShadows.glass, shape)
            .background(EqualTheme.gradients.takeover.toBrush(), shape)
            .eqGlassInset(colors.actionBottomColor, shape)
            .border(1.dp, colors.takeoverStroke, shape)
            .clip(shape)
            .clickable(interactionSource = interaction, indication = null, role = Role.Button, onClick = onClick)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        EqualIcon(EqualIcons.CallFilled, contentDescription = null, tint = colors.takeoverText, size = 18.dp)
        Spacer(Modifier.width(6.dp))
        EqualText(text = label, style = EqualTheme.type.button, color = colors.takeoverText, maxLines = 1)
    }
}

/**
 * Selection-mode header (SPEC §5). Row 1 (64): back + "N Selected" `subheading` → spacer →
 * Mark read pill → 12 → trash icon `button/danger`. Row 2 (40): checkbox 18 + 12 + "Select All"
 * `bodyLarge`.
 */
@Composable
fun SelectionChatHeader(
    selectedCount: Int,
    allSelected: Boolean,
    onSelectAllChange: (Boolean) -> Unit,
    onBackClick: () -> Unit,
    onMarkReadClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
    markReadLabel: String = "Mark read",
    selectAllLabel: String = "Select All",
    selectedLabel: (Int) -> String = { "$it Selected" },
) {
    val colors = EqualTheme.colors
    Column(
        modifier
            .fillMaxWidth()
            .eqShadow(EqualShadows.header)
            .background(colors.bgSurface),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PlainIconButton(EqualIcons.Back, "Back", onBackClick, tint = colors.textPrimary)
            Spacer(Modifier.width(4.dp))
            EqualText(
                text = selectedLabel(selectedCount),
                style = EqualTheme.type.subheading,
                color = colors.textPrimary,
                maxLines = 1,
                // Announced (politely) whenever the count changes.
                modifier = Modifier.weight(1f).semantics { liveRegion = LiveRegionMode.Polite },
            )
            MarkReadPill(label = markReadLabel, onClick = onMarkReadClick)
            Spacer(Modifier.width(12.dp))
            PlainIconButton(EqualIcons.Delete, "Delete", onDeleteClick, tint = colors.buttonDanger)
        }
        // The row stays 40 high; its touch area is 48 (4dp spills over each edge).
        Box(Modifier.fillMaxWidth().height(40.dp), contentAlignment = Alignment.Center) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .requiredHeight(48.dp)
                    .toggleable(value = allSelected, role = Role.Checkbox, onValueChange = onSelectAllChange)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                EqualCheckbox(checked = allSelected, onCheckedChange = null)
                Spacer(Modifier.width(12.dp))
                EqualText(text = selectAllLabel, style = EqualTheme.type.bodyLarge, color = colors.textPrimary, maxLines = 1)
            }
        }
    }
}

@Composable
private fun MarkReadPill(label: String, onClick: () -> Unit) {
    val colors = EqualTheme.colors
    val shape = CircleShape
    val interaction = rememberInteraction()
    Row(
        Modifier
            .minimumInteractiveComponentSize()
            .eqPressFeedback(interaction, shape)
            .height(26.dp)
            .background(colors.bgSurface, shape)
            .border(1.dp, colors.actionStrokeUnread, shape)
            .clip(shape)
            .clickable(interactionSource = interaction, indication = null, role = Role.Button, onClick = onClick)
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        EqualIcon(EqualIcons.DoneAll, contentDescription = null, tint = colors.textAction, size = 16.dp)
        Spacer(Modifier.width(6.dp))
        EqualText(text = label, style = EqualTheme.type.buttonSmall, color = colors.textPrimary, maxLines = 1)
    }
}

/** Title alignment for [EqualAppBar]. The flows use [Center]. */
enum class AppBarAlign { Center, Start }

/**
 * App bar (derived surface), matching web `AppBar`.
 * - [AppBarAlign.Center] (default): 56 high, padding 4 / 8; back button on the left, `subheading`
 *   title truly centred (both sides reserve the width of the wider side), [actions] on the right.
 * - [AppBarAlign.Start]: 64 high; back + title (fills) + [actions].
 *
 * Background defaults to `bg/primary`; [elevated] adds the `header` shadow. [statusBar] draws a
 * [StatusBar] on the same background above the bar. [onBackClick] null hides the back button.
 * For selection mode use [SelectionChatHeader].
 */
@Composable
fun EqualAppBar(
    title: String,
    onBackClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    align: AppBarAlign = AppBarAlign.Center,
    statusBar: Boolean = false,
    background: Color = EqualTheme.colors.bgPrimary,
    elevated: Boolean = false,
    actions: @Composable RowScope.() -> Unit = {},
) {
    val colors = EqualTheme.colors
    val shadow = if (elevated) Modifier.eqShadow(EqualShadows.header) else Modifier
    Column(
        modifier
            .fillMaxWidth()
            .then(shadow)
            .background(background),
    ) {
        if (statusBar) StatusBar(background = background)
        val back: @Composable () -> Unit = {
            if (onBackClick != null) PlainIconButton(EqualIcons.Back, "Back", onBackClick, tint = colors.textPrimary)
        }
        when (align) {
            AppBarAlign.Center -> CenteredBarLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(start = 4.dp, end = 8.dp),
                start = back,
                title = {
                    EqualText(
                        text = title,
                        style = EqualTheme.type.subheading,
                        color = colors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.semantics { heading() },
                    )
                },
                end = { Row(verticalAlignment = Alignment.CenterVertically, content = actions) },
            )
            AppBarAlign.Start -> Row(
                Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(start = if (onBackClick != null) 4.dp else 16.dp, end = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                back()
                if (onBackClick != null) Spacer(Modifier.width(8.dp))
                EqualText(
                    text = title,
                    style = EqualTheme.type.subheading,
                    color = colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f).semantics { heading() },
                )
                actions()
            }
        }
    }
}

/** `1fr auto 1fr`: the title is centred in the bar and never overlaps either side. */
@Composable
private fun CenteredBarLayout(
    modifier: Modifier,
    start: @Composable () -> Unit,
    title: @Composable () -> Unit,
    end: @Composable () -> Unit,
) {
    Layout(
        modifier = modifier,
        content = {
            Box(Modifier.layoutId("start"), contentAlignment = Alignment.CenterStart) { start() }
            Box(Modifier.layoutId("title"), contentAlignment = Alignment.Center) { title() }
            Box(Modifier.layoutId("end"), contentAlignment = Alignment.CenterEnd) { end() }
        },
    ) { measurables, constraints ->
        val loose = constraints.copy(minWidth = 0, minHeight = 0)
        val startP = measurables.first { it.layoutId == "start" }.measure(loose)
        val endP = measurables.first { it.layoutId == "end" }.measure(loose)
        val side = maxOf(startP.width, endP.width)
        val titleMax = (constraints.maxWidth - 2 * side).coerceAtLeast(0)
        val titleP = measurables.first { it.layoutId == "title" }.measure(loose.copy(maxWidth = titleMax))
        val width = constraints.maxWidth
        val height = if (constraints.hasBoundedHeight) constraints.maxHeight else maxOf(startP.height, endP.height, titleP.height)
        layout(width, height) {
            startP.place(0, (height - startP.height) / 2)
            titleP.place((width - titleP.width) / 2, (height - titleP.height) / 2)
            endP.place(width - endP.width, (height - endP.height) / 2)
        }
    }
}

@Composable
private fun HeaderContainer(modifier: Modifier, content: @Composable RowScope.() -> Unit) {
    Row(
        modifier
            .fillMaxWidth()
            .height(64.dp)
            .eqShadow(EqualShadows.header)
            .background(EqualTheme.colors.bgSurface)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        content = content,
    )
}

@EqualPreviews
@Composable
private fun ChatHeaderPreview() {
    PreviewFrame(padding = 0.dp) {
        ChatHeader(name = "Unknown caller", subtitle = "+91 98765 43210", onBackClick = {})
        VSpace(12.dp)
        ChatHeader(
            name = "Dr. Mehta Clinic",
            category = EqualCategory.HomeServices,
            subtitle = "On call with Equal AI",
            subtitleIsPhone = false,
            onBackClick = {},
            variant = ChatHeaderVariant.LiveCall,
        )
        VSpace(12.dp)
        var all by remember { mutableStateOf(false) }
        SelectionChatHeader(
            selectedCount = 1,
            allSelected = all,
            onSelectAllChange = { all = it },
            onBackClick = {},
            onMarkReadClick = {},
            onDeleteClick = {},
        )
        VSpace(12.dp)
        EqualAppBar(title = "Settings", onBackClick = {})
    }
}
