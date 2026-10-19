package ai.equal.ds.components

import ai.equal.ds.assets.EqualAssetImage
import ai.equal.ds.assets.EqualAssets
import ai.equal.ds.icons.EqualIcon
import ai.equal.ds.icons.EqualIcons
import ai.equal.ds.theme.EqualTheme
import ai.equal.ds.tokens.EqualRadius
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.foundation.text.BasicText
import ai.equal.ds.assets.EqualCategory
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.heightIn
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties

// ---------------------------------------------------------------------------------------------
// §13 Feedback card
// ---------------------------------------------------------------------------------------------

/**
 * Feedback card (SPEC §13). Width 246, `bg/surface`, radius 12, padding 10 h / 12 top.
 * Question `body1` → 10 → five 28dp stars, each centred in a 48dp touch cell (filled star
 * `rating/star-filled`, empty = outline star `text/placeholder`), a radio group: tap to rate →
 * divider → 48-high footer "Share Feedback" `button` `text/action` + chevron 16.
 * [rating] is 0..5 (0 = unrated).
 */
@Composable
fun FeedbackCard(
    question: String,
    rating: Int,
    onRatingChange: (Int) -> Unit,
    onShareFeedbackClick: () -> Unit,
    modifier: Modifier = Modifier,
    shareLabel: String = "Share Feedback",
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    Column(
        modifier
            .width(246.dp)
            .clip(RoundedCornerShape(EqualRadius.rMd))
            .background(colors.bgSurface),
    ) {
        Column(Modifier.padding(start = 10.dp, end = 10.dp, top = 12.dp)) {
            EqualText(text = question, style = type.body1, color = colors.textPrimary)
        }
        // Each 28dp star sits centred in a 48dp touch cell. The row spans the card edge to edge,
        // so the first star still starts 10dp in and stays 10dp below the question.
        Row(Modifier.fillMaxWidth().padding(bottom = 2.dp).selectableGroup()) {
            for (star in 1..5) {
                val filled = star <= rating
                Box(
                    Modifier
                        .size(48.dp)
                        .selectable(
                            selected = star == rating,
                            role = Role.RadioButton,
                            onClick = { onRatingChange(star) },
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    EqualIcon(
                        imageVector = if (filled) EqualIcons.Star else EqualIcons.StarOutline,
                        contentDescription = "$star star" + if (star > 1) "s" else "",
                        tint = if (filled) colors.ratingStarFilled else colors.ratingStarEmpty,
                        size = 28.dp,
                    )
                }
            }
        }
        EqualDivider()
        Row(
            Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clickable(role = Role.Button, onClick = onShareFeedbackClick),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            EqualText(text = shareLabel, style = type.button, color = colors.textAction, maxLines = 1)
            EqualIcon(EqualIcons.ChevronRight, contentDescription = null, tint = colors.textAction, size = 16.dp)
        }
    }
}

// ---------------------------------------------------------------------------------------------
// §14 Action card
// ---------------------------------------------------------------------------------------------

/**
 * One action row of an [ActionCard]: a full-width green text row, always led by its [emoji]
 * (✏️ Edit, 📤 Send, 👍 Confirm, 🗑️ Delete). [danger] turns it red.
 */
@Immutable
data class ActionCardAction(
    val label: String,
    val emoji: String,
    val onClick: () -> Unit,
    val danger: Boolean = false,
)

enum class ActionCardTone { Primary, Secondary, Tertiary }

/** One body line of an [ActionCardBody] (`body1`). */
@Immutable
data class ActionCardLine(val text: AnnotatedString, val tone: ActionCardTone = ActionCardTone.Secondary) {
    constructor(text: String, tone: ActionCardTone = ActionCardTone.Secondary) : this(AnnotatedString(text), tone)
}

/** When / Do lines for an instruction: When in `text/primary`, Do in `text/secondary`. */
fun instructionLines(whenText: AnnotatedString, action: AnnotatedString): List<ActionCardLine> = listOf(
    ActionCardLine(buildAnnotatedString { append("When: "); append(whenText) }, ActionCardTone.Primary),
    ActionCardLine(buildAnnotatedString { append("Do: "); append(action) }, ActionCardTone.Secondary),
)

fun instructionLines(whenText: String, action: String): List<ActionCardLine> =
    instructionLines(AnnotatedString(whenText), AnnotatedString(action))

/**
 * The quote block shared by every [ActionCard] and by [InstructionPreview]: `bg/primary`, radius
 * 12, padding 8. A 40dp circle holds, in order of precedence: [category] (the caller's 3D avatar,
 * for a person or business), [icon] (a Material Symbol in `text/action` on `bg/surface`;
 * instructions use [EqualIcons.Instructions], note_stack), or the coded WhatsApp logo. Then 12 →
 * title `body2`, [quote] and [lines] in `body1`, [timestamp] `micro` right-aligned.
 */
@Composable
fun ActionCardBody(
    title: String,
    modifier: Modifier = Modifier,
    quote: String? = null,
    lines: List<ActionCardLine> = emptyList(),
    timestamp: String? = null,
    category: EqualCategory? = null,
    icon: ImageVector? = null,
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    Row(
        modifier
            .fillMaxWidth()
            .background(colors.bgPrimary, RoundedCornerShape(EqualRadius.rMd))
            .padding(8.dp),
        verticalAlignment = Alignment.Top,
    ) {
        if (category != null) {
            CategoryAvatar(category = category, size = 40.dp)
        } else {
            Box(
                Modifier
                    .size(40.dp)
                    .background(colors.bgSurface, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                if (icon != null) {
                    EqualIcon(icon, contentDescription = null, tint = colors.textAction, size = 22.dp)
                } else {
                    EqualIcon(EqualIcons.WhatsApp, contentDescription = "WhatsApp", tint = colors.brandWhatsapp, size = 22.dp)
                }
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            EqualText(text = title, style = type.body2, color = colors.textPrimary)
            if (quote != null) EqualText(text = quote, style = type.body1, color = colors.textSecondary)
            lines.forEach { line ->
                val tint = when (line.tone) {
                    ActionCardTone.Primary -> colors.textPrimary
                    ActionCardTone.Secondary -> colors.textSecondary
                    ActionCardTone.Tertiary -> colors.textSecondary
                }
                BasicText(text = line.text, style = type.body1.copy(color = tint))
            }
            if (timestamp != null) {
                EqualText(
                    text = timestamp,
                    style = type.micro,
                    color = colors.textSecondary,
                    maxLines = 1,
                    modifier = Modifier.align(Alignment.End).padding(top = 2.dp),
                )
            }
        }
    }
}

/**
 * Action card (SPEC §14): something the assistant did or proposes. Width 268, radius 16, a white
 * `bg/surface` card: the grey [ActionCardBody] sits in an 8dp frame (never on a green card). Each
 * action is a full-width green text row (48dp, the touch minimum) under a full-bleed divider, always led by its emoji
 * (14dp, gap 6). All actions share the same weight, the first one ("Confirm", "Send") included:
 * never a filled button.
 */
@Composable
fun ActionCard(
    title: String,
    actions: List<ActionCardAction>,
    modifier: Modifier = Modifier,
    quote: String? = null,
    lines: List<ActionCardLine> = emptyList(),
    timestamp: String? = null,
    category: EqualCategory? = null,
    icon: ImageVector? = null,
) {
    val colors = EqualTheme.colors
    Column(
        modifier
            .width(268.dp)
            .clip(RoundedCornerShape(EqualRadius.rLg))
            .background(colors.bgSurface),
    ) {
        ActionCardBody(
            title = title,
            quote = quote,
            lines = lines,
            timestamp = timestamp,
            category = category,
            icon = icon,
            modifier = Modifier.padding(8.dp),
        )
        actions.forEach { action ->
            EqualDivider()
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clickable(role = Role.Button, onClick = action.onClick),
                contentAlignment = Alignment.Center,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    EmojiGlyph(emoji = action.emoji, size = 14.dp)
                    EqualText(
                        text = action.label,
                        style = EqualTheme.type.button,
                        color = if (action.danger) colors.textDanger else colors.textAction,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

// AiSummaryCard and PlanCard live in YouTab.kt.

// InstructionCard and AddressCard live in Flows.kt.

// ---------------------------------------------------------------------------------------------
// §22 What's New card
// ---------------------------------------------------------------------------------------------

/**
 * What's New card (SPEC §22, Language Preference design). Radius 20, `whats-new/bg` fill with a blurred `whats-new/glow` circle bottom-right behind the art, padding
 * 18/16, min height 172. Left column (badge → 12 → title 18/24 Bold `text/action` → 4 → body caption
 * regular `text/secondary` → 14 → `md` button). The `whats-new-illustration` (robot with scripts,
 * transparent) sits whole at 125dp, 4dp from the right and 35dp from the top.
 * Close (X) 22 `text/placeholder` top-right.
 */
@Composable
fun WhatsNewCard(
    title: String,
    body: String,
    buttonLabel: String,
    onButtonClick: () -> Unit,
    onCloseClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    badgeText: String = "New feature",
    buttonIcon: ImageVector = EqualIcons.Star,
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    Box(
        modifier
            .fillMaxWidth()
            .heightIn(min = 172.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(colors.whatsNewBg),
    ) {
        // The blurred circle behind the illustration, bottom-right. A radial gradient fading to
        // transparent draws the same soft disc as the web's blur(32px), and works below API 31
        // where Modifier.blur does nothing.
        Box(
            Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 76.dp, y = 88.dp)
                .size(230.dp)
                .background(Brush.radialGradient(listOf(colors.whatsNewGlow, colors.whatsNewGlow.copy(alpha = 0f)))),
        )
        EqualAssetImage(
            asset = EqualAssets.WhatsNewIllustration,
            size = 125.dp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 35.dp, end = 4.dp),
        )
        Column(
            Modifier
                // Text may run over the faint watermark but stops ~8dp before the grid.
                .padding(start = 18.dp, top = 18.dp, end = 112.dp, bottom = 18.dp),
        ) {
            FeatureBadge(text = badgeText)
            Spacer(Modifier.height(12.dp))
            EqualText(
                text = title,
                style = type.title.copy(fontSize = 18.sp, lineHeight = 24.sp, fontWeight = FontWeight.Bold),
                color = colors.textAction,
                modifier = Modifier.semantics { heading() },
            )
            Spacer(Modifier.height(4.dp))
            EqualText(text = body, style = type.captionRegular, color = colors.textSecondary, maxLines = 3, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(14.dp))
            EqualButton(text = buttonLabel, onClick = onButtonClick, leadingIcon = buttonIcon)
        }
        if (onCloseClick != null) {
            Box(
                Modifier
                    .align(Alignment.TopEnd)
                    // 48dp touch area centred where the old 32dp button (inset 8) was.
                    .size(48.dp)
                    .clip(CircleShape)
                    .clickable(role = Role.Button, onClick = onCloseClick),
                contentAlignment = Alignment.Center,
            ) {
                EqualIcon(EqualIcons.Close, contentDescription = "Close", tint = colors.textPlaceholder, size = 22.dp)
            }
        }
    }
}

@EqualPreviews
@Composable
private fun CardsPreview() {
    PreviewFrame {
        var rating by remember { mutableIntStateOf(3) }
        FeedbackCard(
            question = "How did Equal AI handle this call?",
            rating = rating,
            onRatingChange = { rating = it },
            onShareFeedbackClick = {},
        )
        VSpace(12.dp)
        ActionCard(
            title = "Dr. Mehta's Clinic Appointment",
            category = EqualCategory.Others,
            lines = listOf(
                ActionCardLine("Tomorrow, 9:00 AM"),
                ActionCardLine("Bring previous medical reports", ActionCardTone.Tertiary),
            ),
            timestamp = "03:21",
            actions = listOf(ActionCardAction("Confirm", emoji = "👍", onClick = {}), ActionCardAction("Edit", emoji = "✏️", onClick = {})),
        )
        VSpace(12.dp)
        ActionCard(
            title = "Instruction saved",
            icon = EqualIcons.Instructions,
            lines = instructionLines("Delivery agents call", "Ask them to leave parcels with security"),
            timestamp = "03:21",
            actions = listOf(ActionCardAction("Edit rule", emoji = "✏️", onClick = {})),
        )
        VSpace(12.dp)
        WhatsNewCard(
            title = "Takeover live calls",
            body = "Jump into any call Equal AI is handling with one tap.",
            buttonLabel = "Try it",
            onButtonClick = {},
            onCloseClick = {},
        )
    }
}
