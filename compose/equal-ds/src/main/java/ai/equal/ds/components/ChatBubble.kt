package ai.equal.ds.components

import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import ai.equal.ds.assets.EqualCategory
import ai.equal.ds.theme.EqualTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

/** Bubble roles (SPEC §11). */
enum class ChatBubbleType {
    /** User / Equal AI speaking on the call. Right aligned, `chat-bubble/user-bg`. */
    Outgoing,

    /** The contact. Left aligned with a 28dp category avatar, `chat-bubble/other-bg`. */
    Incoming,

    /** Equal AI talking to the user. Left aligned with a 24dp AI avatar, `bg/surface`. */
    Assistant,
}

private val BubbleTailWidth = 8.dp
private val BubbleMaxWidth = 264.dp

/**
 * Chat bubble (SPEC §11). Max width 264, radius 12, padding 8 top / 10 h / 6 bottom.
 * Sender `body2` (first of group only) → message `body1` `text/primary` → timestamp `micro`
 * `text/secondary` right-aligned, gap 2.
 *
 * Grouping: for consecutive bubbles from the same sender pass `isFirstInGroup = false` and lay
 * them out with a 4dp gap; the avatar and sender line appear only on the first. [tail] draws an
 * 8dp tail on the top-outer corner (defaults to the first bubble of a group). An 8dp gutter is
 * always reserved on the tail side so grouped bubbles stay aligned.
 *
 * Incoming bubbles show a 28dp [CategoryAvatar] ([incomingCategory], default
 * [EqualCategory.Others]); assistant bubbles show the 24dp [AiAvatar] orb.
 * [locked] (monetization): the message is wrapped in [LockedContent] (blurred + crown-lock badge).
 * [attachment] (e.g. a [VoiceNotePlayer] for a voice note the user sent) sits between the sender
 * and the message, with 4dp above and 6dp below.
 */
@Composable
fun ChatBubble(
    message: String,
    timestamp: String,
    type: ChatBubbleType,
    modifier: Modifier = Modifier,
    sender: String? = null,
    isFirstInGroup: Boolean = true,
    tail: Boolean = isFirstInGroup,
    incomingCategory: EqualCategory = EqualCategory.Others,
    locked: Boolean = false,
    attachment: (@Composable () -> Unit)? = null,
) {
    val colors = EqualTheme.colors
    val typeScale = EqualTheme.type
    val fill = when (type) {
        ChatBubbleType.Outgoing -> colors.chatBubbleUserBg
        ChatBubbleType.Incoming -> colors.chatBubbleOtherBg
        ChatBubbleType.Assistant -> colors.bgSurface
    }
    val senderColor = when (type) {
        ChatBubbleType.Outgoing -> colors.chatBubbleUserTitle
        ChatBubbleType.Incoming -> colors.chatBubbleContactTitle
        ChatBubbleType.Assistant -> colors.textAction
    }
    val tailOnStart = type != ChatBubbleType.Outgoing
    val shape = BubbleShape(tailOnStart = tailOnStart, hasTail = tail, tailWidth = BubbleTailWidth, radius = 12.dp)

    // One TalkBack stop per bubble (sender, message, time). Follow-up bubbles hide the sender
    // visually, so it is added back as the description; controls inside (a voice note) stay separate.
    val hiddenSender = if (!isFirstInGroup) sender else null
    Row(
        modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {
                if (hiddenSender != null) contentDescription = hiddenSender
            },
        horizontalArrangement = if (type == ChatBubbleType.Outgoing) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Top,
    ) {
        when (type) {
            ChatBubbleType.Incoming -> {
                if (isFirstInGroup) {
                    CategoryAvatar(category = incomingCategory, size = 28.dp, contentDescription = null)
                } else {
                    Spacer(Modifier.width(28.dp))
                }
                // 8dp avatar gap is provided by the tail gutter.
            }
            ChatBubbleType.Assistant -> {
                if (isFirstInGroup) AiAvatar(size = AiAvatarSize.Small, contentDescription = null) else Spacer(Modifier.width(24.dp))
            }
            ChatBubbleType.Outgoing -> Unit
        }
        Column(
            Modifier
                .widthIn(max = BubbleMaxWidth + BubbleTailWidth)
                .background(fill, shape)
                .padding(
                    start = 10.dp + if (tailOnStart) BubbleTailWidth else 0.dp,
                    end = 10.dp + if (tailOnStart) 0.dp else BubbleTailWidth,
                    top = 8.dp,
                    bottom = 6.dp,
                ),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            if (isFirstInGroup && sender != null) {
                EqualText(text = sender, style = typeScale.body2, color = senderColor, maxLines = 1)
            }
            if (attachment != null) {
                Box(Modifier.padding(top = 2.dp, bottom = 4.dp)) { attachment() }
            }
            if (locked) {
                LockedContent { EqualText(text = message, style = typeScale.body1, color = colors.textPrimary) }
            } else {
                EqualText(text = message, style = typeScale.body1, color = colors.textPrimary)
            }
            EqualText(
                text = timestamp,
                style = typeScale.micro,
                color = colors.textSecondary,
                maxLines = 1,
                modifier = Modifier.align(Alignment.End),
            )
        }
    }
}

/**
 * Rounded bubble with an optional tail. The tail lives in a [tailWidth] gutter on the tail side:
 * the top-outer corner becomes square and a small curved notch extends into the gutter.
 */
@Immutable
class BubbleShape(
    private val tailOnStart: Boolean,
    private val hasTail: Boolean,
    private val tailWidth: Dp = 8.dp,
    private val radius: Dp = 12.dp,
) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val gutter = with(density) { tailWidth.toPx() }
        val r = with(density) { radius.toPx() }
        val tailLeft = if (layoutDirection == LayoutDirection.Ltr) tailOnStart else !tailOnStart
        val bodyLeft = if (tailLeft) gutter else 0f
        val bodyRight = if (tailLeft) size.width else size.width - gutter
        val round = CornerRadius(r, r)
        val sharp = CornerRadius.Zero
        val body = Path().apply {
            addRoundRect(
                RoundRect(
                    left = bodyLeft,
                    top = 0f,
                    right = bodyRight,
                    bottom = size.height,
                    topLeftCornerRadius = if (hasTail && tailLeft) sharp else round,
                    topRightCornerRadius = if (hasTail && !tailLeft) sharp else round,
                    bottomRightCornerRadius = round,
                    bottomLeftCornerRadius = round,
                ),
            )
        }
        if (!hasTail) return Outline.Generic(body)

        val depth = (gutter * 1.5f).coerceAtMost(size.height)
        val tail = Path().apply {
            if (tailLeft) {
                moveTo(bodyLeft + 1f, 0f)
                lineTo(0f, 0f)
                quadraticTo(bodyLeft - gutter * 0.2f, depth * 0.33f, bodyLeft, depth)
                lineTo(bodyLeft + 1f, depth)
                close()
            } else {
                moveTo(bodyRight - 1f, 0f)
                lineTo(size.width, 0f)
                quadraticTo(bodyRight + gutter * 0.2f, depth * 0.33f, bodyRight, depth)
                lineTo(bodyRight - 1f, depth)
                close()
            }
        }
        val combined = Path().apply { op(body, tail, PathOperation.Union) }
        return Outline.Generic(combined)
    }

    override fun equals(other: Any?): Boolean =
        other is BubbleShape &&
            other.tailOnStart == tailOnStart &&
            other.hasTail == hasTail &&
            other.tailWidth == tailWidth &&
            other.radius == radius

    override fun hashCode(): Int {
        var result = tailOnStart.hashCode()
        result = 31 * result + hasTail.hashCode()
        result = 31 * result + tailWidth.hashCode()
        result = 31 * result + radius.hashCode()
        return result
    }
}

@EqualPreviews
@Composable
private fun ChatBubblePreview() {
    PreviewFrame {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            ChatBubble(
                message = "Hello, this is Equal AI answering for Arnav. How can I help?",
                timestamp = "03:31",
                type = ChatBubbleType.Outgoing,
                sender = "Equal AI",
            )
            ChatBubble(
                message = "Could you ask him to call back?",
                timestamp = "03:32",
                type = ChatBubbleType.Outgoing,
                sender = "Equal AI",
                isFirstInGroup = false,
            )
        }
        VSpace(12.dp)
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            ChatBubble(
                message = "Hi, I'm calling from Dr. Mehta Clinic about tomorrow's appointment.",
                timestamp = "03:32",
                type = ChatBubbleType.Incoming,
                sender = "Dr. Mehta Clinic",
            )
            ChatBubble(
                message = "Is 4 PM still okay?",
                timestamp = "03:32",
                type = ChatBubbleType.Incoming,
                sender = "Dr. Mehta Clinic",
                isFirstInGroup = false,
            )
        }
        VSpace(12.dp)
        ChatBubble(
            message = "The clinic wants to confirm your 4 PM slot. Want me to reschedule?",
            timestamp = "03:35",
            type = ChatBubbleType.Assistant,
            sender = "Equal AI",
        )
    }
}
