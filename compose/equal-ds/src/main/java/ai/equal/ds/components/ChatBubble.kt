package ai.equal.ds.components

import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.layout.Layout
import androidx.compose.foundation.shape.RoundedCornerShape
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

private val BubbleMaxWidth = 264.dp

/**
 * Chat bubble (SPEC §11). Max width 264, radius 12, padding 8 top / 10 h / 6 bottom.
 * Sender `body2` (first of group only) → message `body1` `text/primary` → timestamp `micro`
 * `text/secondary`.
 *
 * Corners: the first bubble of a group squares its top-outer corner (top-start for incoming and
 * assistant, top-end for outgoing); follow-ups (`isFirstInGroup = false`) round all four at 12.
 * Lay consecutive bubbles out with a 4dp gap; the avatar and sender line appear only on the first.
 *
 * Timestamp: a one-line message keeps the time on the same row (gap 8), set 4dp below the text
 * line; a longer message puts it on its own row, right-aligned, 2dp below ([MessageWithTime]).
 *
 * Incoming bubbles show a 28dp [CategoryAvatar] ([incomingCategory], default
 * [EqualCategory.Others]); assistant bubbles show the 24dp [AiAvatar] orb, 8dp from the bubble.
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
    val shape = bubbleShape(outgoing = type == ChatBubbleType.Outgoing, lead = isFirstInGroup)

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
                Spacer(Modifier.width(8.dp))
            }
            ChatBubbleType.Assistant -> {
                if (isFirstInGroup) AiAvatar(size = AiAvatarSize.Small, contentDescription = null) else Spacer(Modifier.width(24.dp))
                Spacer(Modifier.width(8.dp))
            }
            ChatBubbleType.Outgoing -> Unit
        }
        Column(
            Modifier
                .widthIn(max = BubbleMaxWidth)
                .background(fill, shape)
                .padding(start = 10.dp, end = 10.dp, top = 8.dp, bottom = 6.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            if (isFirstInGroup && sender != null) {
                EqualText(text = sender, style = typeScale.body2, color = senderColor, maxLines = 1)
            }
            if (attachment != null) {
                Box(Modifier.padding(top = 2.dp, bottom = 4.dp)) { attachment() }
            }
            MessageWithTime(
                timestamp = timestamp,
                message = {
                    if (locked) {
                        LockedContent { EqualText(text = message, style = typeScale.body1, color = colors.textPrimary) }
                    } else {
                        EqualText(text = message, style = typeScale.body1, color = colors.textPrimary)
                    }
                },
            )
        }
    }
}

/** Bubble outline: radius 12, the top-outer corner square on the first bubble of a group. */
fun bubbleShape(outgoing: Boolean, lead: Boolean): Shape {
    val r = 12.dp
    val square = if (lead) 0.dp else r
    return if (outgoing) RoundedCornerShape(topStart = r, topEnd = square, bottomEnd = r, bottomStart = r)
    else RoundedCornerShape(topStart = square, topEnd = r, bottomEnd = r, bottomStart = r)
}

/**
 * Message and its time. When the message is one line and message + 8 + time fit the width, the
 * time sits on the same row, its bottom 4dp below the text line's; otherwise it goes on its own
 * row, right-aligned, 2dp below.
 */
@Composable
fun MessageWithTime(timestamp: String, message: @Composable () -> Unit, modifier: Modifier = Modifier) {
    val type = EqualTheme.type
    val colors = EqualTheme.colors
    val density = LocalDensity.current
    Layout(
        modifier = modifier,
        content = {
            Box { message() }
            EqualText(text = timestamp, style = type.micro, color = colors.textSecondary, maxLines = 1)
        },
    ) { measurables, constraints ->
        val loose = constraints.copy(minWidth = 0, minHeight = 0)
        val time = measurables[1].measure(loose)
        val gap = with(density) { 8.dp.roundToPx() }
        val drop = with(density) { 4.dp.roundToPx() }
        val stackGap = with(density) { 2.dp.roundToPx() }
        val text = measurables[0].measure(loose)
        val lineHeight = with(density) { type.body1.lineHeight.toPx() }
        val oneLine = text.height <= lineHeight * 1.5f
        if (oneLine && text.width + gap + time.width <= constraints.maxWidth) {
            val w = text.width + gap + time.width
            val h = maxOf(text.height, text.height - time.height + drop + time.height)
            layout(w.coerceAtLeast(constraints.minWidth), h) {
                text.place(0, 0)
                time.place(w - time.width, text.height - time.height + drop)
            }
        } else {
            val w = maxOf(text.width, time.width).coerceAtLeast(constraints.minWidth)
            layout(w, text.height + stackGap + time.height) {
                text.place(0, 0)
                time.place(w - time.width, text.height + stackGap)
            }
        }
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
