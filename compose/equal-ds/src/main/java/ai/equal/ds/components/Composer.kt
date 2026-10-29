package ai.equal.ds.components

import ai.equal.ds.icons.EqualIcon
import ai.equal.ds.icons.EqualIcons
import ai.equal.ds.theme.EqualShadows
import ai.equal.ds.theme.EqualTheme
import ai.equal.ds.theme.eqPressFeedback
import ai.equal.ds.theme.eqShadow
import ai.equal.ds.theme.toBrush
import ai.equal.ds.tokens.EqualRadius
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll

/**
 * Voice input bar / composer (SPEC §15). Input (fills, 45 high pill, `bg/surface`,
 * 1dp `stroke/subtle`, h20, `body1`; placeholder `text/secondary`) → 10 → 45dp send button.
 *
 * - Empty: send uses `gradient/send-disabled` and is disabled.
 * - Focused / typing: input border 1.5dp `stroke/focus` (brand green; lime in dark), no focus ring; send uses
 *   `gradient/fab` + shadow `fab`.
 *
 * Send is enabled only when [value] is non-empty.
 */
@Composable
fun VoiceInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Type a message",
    enabled: Boolean = true,
    sendContentDescription: String = "Send",
    contentDescription: String = "Message",
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    val interaction = rememberInteraction()
    val focused by interaction.collectIsFocusedAsState()
    val hasText = value.isNotEmpty()
    val active = focused || hasText
    val canSend = enabled && hasText

    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .weight(1f)
                .height(45.dp)
                // The placeholder disappears once there is text, so the field needs its own name.
                .semantics { this.contentDescription = contentDescription }
                .background(colors.bgSurface, CircleShape)
                // Selected: a solid 1.5dp stroke/focus outline only, no focus ring.
                .border(if (active) 1.5.dp else 1.dp, if (active) colors.strokeFocus else colors.strokeSubtle, CircleShape),
            enabled = enabled,
            singleLine = true,
            textStyle = type.body1.copy(color = colors.textPrimary),
            cursorBrush = SolidColor(colors.textAction),
            interactionSource = interaction,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(onSend = { if (canSend) onSend() }),
            decorationBox = { innerTextField ->
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    if (value.isEmpty()) {
                        EqualText(
                            text = placeholder,
                            style = type.body1,
                            color = colors.textSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    innerTextField()
                }
            },
        )
        Spacer(Modifier.width(10.dp))
        SendButton(enabled = canSend, onClick = onSend, contentDescription = sendContentDescription)
    }
}

@Composable
private fun SendButton(enabled: Boolean, onClick: () -> Unit, contentDescription: String) {
    val gradients = EqualTheme.gradients
    val interaction = rememberInteraction()
    val surface = if (enabled) {
        Modifier
            .eqShadow(EqualShadows.fab, CircleShape)
            .background(gradients.fab.toBrush(), CircleShape)
    } else {
        Modifier.background(gradients.sendDisabled.toBrush(), CircleShape)
    }
    // 48dp touch area around the 45dp circle (layout unchanged).
    Box(
        Modifier
            .touchTarget()
            .clickable(
                interactionSource = interaction,
                indication = null,
                enabled = enabled,
                role = Role.Button,
                onClickLabel = contentDescription,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .eqPressFeedback(interaction, CircleShape)
                .size(45.dp)
                .then(surface)
                .clip(CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            EqualIcon(
                EqualIcons.Send,
                contentDescription = contentDescription,
                tint = if (enabled) EqualTheme.colors.textOnAction else EqualTheme.colors.textOnDark,
                size = 20.dp,
            )
        }
    }
}

/** AI suggestion row (SPEC §16): AI avatar 36 → 12 → `body1` `text/primary` (wraps), top-aligned with no extra offset. */
@Composable
fun AiSuggestion(
    text: String,
    modifier: Modifier = Modifier,
) {
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        AiAvatar(size = AiAvatarSize.Medium)
        Spacer(Modifier.width(12.dp))
        EqualText(
            text = text,
            style = EqualTheme.type.body1,
            color = EqualTheme.colors.textPrimary,
            modifier = Modifier.weight(1f),
        )
    }
}

/**
 * AI chat panel inside another conversation (SPEC §12b). Drawn on [ChatWallpaper], so the rounded
 * top corners show the pattern rather than the page. `bg/surface`, top radius 24, padding
 * 16 h / 20 top / 16 bottom. AI avatar 36 → 12 → prompt `body1` `text/secondary`; 24; a single
 * non-wrapping, horizontally scrolling row of [LargeActionChip]s (gap 6); 20; [VoiceInputBar].
 */
@Composable
fun AiChatPanel(
    prompt: String,
    actions: List<ActionChipData>,
    onActionClick: (ActionChipData) -> Unit,
    composerValue: String,
    onComposerValueChange: (String) -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Ask Equal AI",
) {
    val colors = EqualTheme.colors
    // The chat wallpaper runs on behind the panel, so its rounded top corners show the pattern, not the page.
    ChatWallpaper(modifier.fillMaxWidth()) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(colors.bgSurface, RoundedCornerShape(topStart = EqualRadius.r2xl, topEnd = EqualRadius.r2xl))
            .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AiAvatar(size = AiAvatarSize.Medium)
            Spacer(Modifier.width(12.dp))
            EqualText(text = prompt, style = EqualTheme.type.body1, color = colors.textSecondary, modifier = Modifier.weight(1f))
        }
        if (actions.isNotEmpty()) {
            Spacer(Modifier.height(24.dp))
            Row(
                Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                actions.forEach { action ->
                    LargeActionChip(label = action.label, emoji = action.emoji, onClick = { onActionClick(action) })
                }
            }
        }
        Spacer(Modifier.height(20.dp))
        VoiceInputBar(
            value = composerValue,
            onValueChange = onComposerValueChange,
            onSend = onSend,
            placeholder = placeholder,
        )
    }
    }
}

@EqualPreviews
@Composable
private fun ComposerPreview() {
    PreviewFrame {
        var empty by remember { mutableStateOf("") }
        VoiceInputBar(value = empty, onValueChange = { empty = it }, onSend = {})
        VSpace(12.dp)
        var typed by remember { mutableStateOf("Call them back at 5") }
        VoiceInputBar(value = typed, onValueChange = { typed = it }, onSend = { typed = "" })
        VSpace(12.dp)
        AiSuggestion(text = "They sounded like a genuine clinic. Want me to confirm the appointment?")
    }
}

@EqualPreviews
@Composable
private fun AiChatPanelPreview() {
    PreviewFrame(padding = 0.dp) {
        var text by remember { mutableStateOf("") }
        AiChatPanel(
            prompt = "What would you like me to do about this call?",
            actions = listOf(
                ActionChipData("Reply on WhatsApp", "💚"),
                ActionChipData("Add to Calendar", "📅"),
            ),
            onActionClick = {},
            composerValue = text,
            onComposerValueChange = { text = it },
            onSend = { text = "" },
        )
    }
}
