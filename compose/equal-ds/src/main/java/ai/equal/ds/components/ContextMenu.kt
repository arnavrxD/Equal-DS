package ai.equal.ds.components

import ai.equal.ds.icons.EqualIcon
import ai.equal.ds.icons.EqualIcons
import ai.equal.ds.theme.EqualShadows
import ai.equal.ds.theme.EqualTheme
import ai.equal.ds.theme.eqShadow
import ai.equal.ds.tokens.EqualRadius
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties

/** One context-menu entry. [destructive] renders label and icon in `text/danger`. */
@Immutable
data class ContextMenuItem(
    val label: String,
    val icon: ImageVector,
    val onClick: () -> Unit,
    val destructive: Boolean = false,
)

/**
 * Long-press context menu surface (SPEC §18). Min width 131, `bg/surface`, radius 12, shadow
 * `card`. Items 48 high (the touch minimum), h12: icon 16 `text/tertiary` → 10 → label `body1` `text/primary`,
 * 1dp `stroke/subtle` between items. Destructive: `text/danger`.
 * Use [ContextMenuPopup] to anchor it.
 */
@Composable
fun ContextMenu(
    items: List<ContextMenuItem>,
    modifier: Modifier = Modifier,
    labelStyle: TextStyle = EqualTheme.type.body1,
    minWidth: Dp = 131.dp,
) {
    val colors = EqualTheme.colors
    val shape = RoundedCornerShape(EqualRadius.rMd)
    Column(
        modifier
            .widthIn(min = minWidth)
            .width(IntrinsicSize.Max)
            .eqShadow(EqualShadows.card, shape)
            .clip(shape)
            .background(colors.bgSurface),
    ) {
        items.forEachIndexed { index, item ->
            if (index > 0) EqualDivider()
            val tint = if (item.destructive) colors.textDanger else colors.textSecondary
            val labelColor = if (item.destructive) colors.textDanger else colors.textPrimary
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clickable(role = Role.Button, onClick = item.onClick)
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                EqualIcon(item.icon, contentDescription = null, tint = tint, size = 16.dp)
                Spacer(Modifier.width(10.dp))
                EqualText(text = item.label, style = labelStyle, color = labelColor, maxLines = 1)
            }
        }
    }
}

/**
 * Shows [ContextMenu] in a focusable [Popup] anchored to the parent layout.
 * Hoist [expanded]; items should call [onDismissRequest] themselves if they close the menu.
 */
@Composable
fun ContextMenuPopup(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    items: List<ContextMenuItem>,
    alignment: Alignment = Alignment.TopStart,
    offset: IntOffset = IntOffset(0, 0),
) {
    if (!expanded) return
    Popup(
        alignment = alignment,
        offset = offset,
        onDismissRequest = onDismissRequest,
        properties = PopupProperties(focusable = true),
    ) {
        ContextMenu(items = items)
    }
}

@EqualPreviews
@Composable
private fun ContextMenuPreview() {
    PreviewFrame {
        ContextMenu(
            items = listOf(
                ContextMenuItem("Mark read", EqualIcons.DoneAll, onClick = {}),
                ContextMenuItem("Share", EqualIcons.Share, onClick = {}),
                ContextMenuItem("Delete", EqualIcons.Delete, onClick = {}, destructive = true),
            ),
        )
    }
}
