package ai.equal.ds.components

import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import ai.equal.ds.assets.EqualCategory
import ai.equal.ds.icons.EqualIcon
import ai.equal.ds.icons.EqualIcons
import ai.equal.ds.theme.EqualTheme
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import ai.equal.ds.icons.EqualSymbols
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/** Chat list row states (SPEC §6). */
enum class ChatListItemState { Unread, Read, Selected }

/** Updated UI row states on [ChatListItem]. */
enum class ChatListItemStatus {
    /** Missed by the assistant: the 14dp call-missed glyph and "Missed by Assistant". */
    Missed,

    /** Live: the assistant is placing a call. "Assistant is dialing" and the "Outgoing call" status pill. */
    Dialing,

    /** Live: the assistant is on the call. "Assistant is on a call" and the "Takeover" pill. */
    OnCall,

    /** Live: the call is ringing for the user. "Calling you" and the "Answer" pill. */
    Ringing,

    /** The summary is still generating: "Loading Summary..." and no chips. */
    Loading,
}

/**
 * Chat list row (SPEC §6). Padding 16 h / 12 v, `bg/surface` (selected: `action-card/highlight`).
 * Avatar 48 ([category] image, default [EqualCategory.NA]; or [initials] for a saved contact)
 * → 16 → column:
 * 1. name `subheading` + date `micro` (`text/action` when unread, else `text/secondary`);
 * 2. preview `body1` `text/secondary` + unread count (20, `button/primary`);
 * 3. (gap 8) horizontally scrolling [SmallActionChip]s, gap 12, clipped at the right edge.
 *
 * Selected: avatar shows an 18dp green check with a 2dp `bg/surface` ring; chips are outlined.
 * [locked] (monetization, free plan past its limit): the preview is blurred ([LockedContent]
 * inline) and the 16dp crown-lock [LockBadge] replaces the unread count. Tapping should open the
 * paywall. [lockedActions] (Actions touchpoint) draws every chip crown-locked and gold
 * ([SmallActionChip] `locked`); a chip tap then calls [onLockedAction] instead of [onChipClick].
 * [official] is Equal AI's own chat: the 48dp [AiAvatar] orb and a 16dp verified tick
 * (`text/action`) after the name.
 * Chip emoji are Unicode strings (see [ActionChipData]).
 *
 * Updated UI rows: [status] [ChatListItemStatus.Missed] puts a 14dp call-missed glyph
 * (`missed-call/icon`) before "Missed by Assistant". Live calls put the row at the top of Chats
 * with a status line (`text/action` SemiBold) and one pill (32 high, 14 h, full radius,
 * `button/primary`, `buttonSmall` `text/on-action`) in place of the date; the count and chips are
 * hidden: [ChatListItemStatus.Dialing] "Assistant is dialing" · "Outgoing call" (a status, not
 * clickable); [ChatListItemStatus.OnCall] "Assistant is on a call" · "Takeover";
 * [ChatListItemStatus.Ringing] "Calling you" · "Answer". The Takeover / Answer pill is a button
 * that calls [onLiveAction]. [ChatListItemStatus.Loading] reads "Loading Summary..." with no chips. [muted] adds a 16dp
 * notifications-off glyph (`text/secondary`, "Muted") after the name.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatListItem(
    name: String,
    preview: String,
    date: String,
    modifier: Modifier = Modifier,
    state: ChatListItemState = ChatListItemState.Read,
    unreadCount: Int = 0,
    chips: List<ActionChipData> = emptyList(),
    onClick: () -> Unit = {},
    onLongClick: (() -> Unit)? = null,
    onChipClick: (ActionChipData) -> Unit = {},
    category: EqualCategory? = EqualCategory.NA,
    initials: String? = null,
    locked: Boolean = false,
    /** Equal AI's own chat: orb avatar + verified tick. */
    official: Boolean = false,
    /** Actions touchpoint: the chips are gold and crown-locked. */
    lockedActions: Boolean = false,
    /** Tapping a locked chip (opens the paywall). */
    onLockedAction: () -> Unit = {},
    /** Missed by the assistant, a live call (dialing, on a call, ringing), or the summary still loading. */
    status: ChatListItemStatus? = null,
    /** Muted from the list: the notifications-off glyph after the name. */
    muted: Boolean = false,
    /** The live pill (Takeover / Answer). Not called for [ChatListItemStatus.Dialing], whose pill is a status. */
    onLiveAction: (() -> Unit)? = null,
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    val unread = state == ChatListItemState.Unread
    val selected = state == ChatListItemState.Selected
    val chipStyle = when (state) {
        ChatListItemState.Unread -> SmallActionChipStyle.Unread
        ChatListItemState.Read -> SmallActionChipStyle.Read
        ChatListItemState.Selected -> SmallActionChipStyle.Outlined
    }
    val live = status?.liveCopy()
    val onCall = live != null
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    Row(
        modifier
            .fillMaxWidth()
            .background(if (selected) colors.actionCardHighlight else colors.bgSurface)
            // Pressed: the `state/chat-pressed` wash over the row (not on a selected row).
            .background(if (pressed && !selected) colors.stateChatPressed else Color.Transparent)
            .semantics { this.selected = selected }
            .combinedClickable(
                interactionSource = interaction,
                indication = null,
                role = Role.Button,
                onLongClickLabel = if (onLongClick != null) "Select" else null,
                onClick = onClick,
                onLongClick = onLongClick,
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        if (official) {
            AiAvatar(size = AiAvatarSize.Large)
        } else {
            ContactAvatar(size = 48.dp, category = category, initials = initials, selected = selected)
        }
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    EqualText(
                        text = name,
                        style = type.subheading,
                        color = colors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false),
                    )
                    if (official) {
                        Spacer(Modifier.width(4.dp))
                        EqualIcon(EqualIcons.Verified, contentDescription = "Verified", tint = colors.textAction, size = 16.dp)
                    }
                    if (muted) {
                        Spacer(Modifier.width(4.dp))
                        EqualIcon(EqualSymbols.silentMode, contentDescription = "Muted", tint = colors.textSecondary, size = 16.dp)
                    }
                }
                Spacer(Modifier.width(8.dp))
                if (live != null) {
                    LiveCallPill(label = live.pill, onClick = if (live.action) onLiveAction ?: {} else null)
                } else {
                    EqualText(
                        text = date,
                        style = type.micro,
                        color = if (unread || selected) colors.textAction else colors.textSecondary,
                        maxLines = 1,
                    )
                }
            }
            Row(Modifier.defaultMinSize(minHeight = 22.dp), verticalAlignment = Alignment.CenterVertically) {
                if (locked) {
                    LockedContent(inline = true, modifier = Modifier.weight(1f)) {
                        EqualText(
                            text = preview,
                            style = type.body1,
                            color = colors.textSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                } else {
                    if (status == ChatListItemStatus.Missed) {
                        EqualIcon(EqualSymbols.callMissed, contentDescription = null, tint = colors.missedCallIcon, size = 14.dp)
                        Spacer(Modifier.width(8.dp))
                    }
                    EqualText(
                        text = when {
                            live != null -> live.line
                            status == ChatListItemStatus.Missed -> "Missed by Assistant"
                            status == ChatListItemStatus.Loading -> "Loading Summary..."
                            else -> preview
                        },
                        style = if (onCall) type.body1.copy(fontWeight = FontWeight.SemiBold) else type.body1,
                        color = if (onCall) colors.textAction else colors.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                }
                if (!locked && !onCall && unreadCount > 0) {
                    Spacer(Modifier.width(8.dp))
                    CountBadge(count = unreadCount)
                }
            }
            if (chips.isNotEmpty() && !onCall && status != ChatListItemStatus.Loading) {
                Spacer(Modifier.height(8.dp))
                Row(
                    Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    chips.forEach { chip ->
                        SmallActionChip(
                            label = chip.label,
                            emoji = chip.emoji,
                            onClick = { if (lockedActions) onLockedAction() else onChipClick(chip) },
                            style = chipStyle,
                            locked = lockedActions,
                        )
                    }
                }
            }
        }
    }
}

/** A live-call row's status line and pill; [action]: the pill is a button (Takeover / Answer). */
private class LiveCopy(val line: String, val pill: String, val action: Boolean)

private fun ChatListItemStatus.liveCopy(): LiveCopy? = when (this) {
    ChatListItemStatus.Dialing -> LiveCopy("Assistant is dialing", "Outgoing call", action = false)
    ChatListItemStatus.OnCall -> LiveCopy("Assistant is on a call", "Takeover", action = true)
    ChatListItemStatus.Ringing -> LiveCopy("Calling you", "Answer", action = true)
    else -> null
}

/**
 * Live-call pill: 32 high, 14 h, full radius, `button/primary`, `buttonSmall` `text/on-action`.
 * With [onClick] it is a button (Takeover / Answer); without, a plain status ("Outgoing call").
 */
@Composable
private fun LiveCallPill(label: String, onClick: (() -> Unit)?) {
    val colors = EqualTheme.colors
    Box(
        Modifier
            .height(32.dp)
            .clip(CircleShape)
            .background(colors.buttonPrimary)
            .then(if (onClick != null) Modifier.clickable(role = Role.Button, onClick = onClick) else Modifier)
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        EqualText(text = label, style = EqualTheme.type.buttonSmall, color = colors.textOnAction, maxLines = 1)
    }
}

@EqualPreviews
@Composable
private fun ChatListItemPreview() {
    val chips = listOf(
        ActionChipData("Call back", "📞"),
        ActionChipData("Update address", "✏️"),
        ActionChipData("Add to Calendar", "📅"),
    )
    PreviewFrame(padding = 0.dp) {
        ChatListItem(
            name = "Dr. Mehta Clinic",
            category = EqualCategory.HomeServices,
            preview = "Your appointment is confirmed for 4 PM",
            date = "12:30",
            state = ChatListItemState.Unread,
            unreadCount = 2,
            chips = chips,
        )
        ChatListItem(
            name = "+91 98765 43210",
            preview = "Delivery partner called",
            date = "Yesterday",
            chips = chips,
            category = EqualCategory.Delivery,
        )
        ChatListItem(name = "Priya Sharma", preview = "See you at 6", date = "Tue", initials = "PS")
        ChatListItem(
            name = "Unknown caller",
            preview = "Asked about a loan offer",
            date = "Mon",
            state = ChatListItemState.Selected,
            chips = chips.take(1),
        )
        ChatListItem(
            name = "Equal AI",
            preview = "Your Plan Expired, Renew to get Unlimited",
            date = "Now",
            state = ChatListItemState.Unread,
            unreadCount = 1,
            chips = listOf(ActionChipData("Renew Now", "👑")),
            official = true,
        )
        ChatListItem(name = "Dr. Mehta Clinic", preview = "Amit invited you to dinner", date = "Yesterday", chips = chips.take(2), lockedActions = true)
        ChatListItem(name = "Amit Nanda", preview = "", date = "12:30", initials = "AN", status = ChatListItemStatus.Dialing)
        ChatListItem(name = "Priya Sharma", preview = "", date = "12:30", initials = "PS", status = ChatListItemStatus.OnCall, chips = chips, onLiveAction = {})
        ChatListItem(name = "Priya Sharma", preview = "", date = "12:30", initials = "PS", status = ChatListItemStatus.Ringing, onLiveAction = {})
        ChatListItem(name = "HDFC Bank", preview = "", date = "12:30", state = ChatListItemState.Unread, unreadCount = 1, status = ChatListItemStatus.Missed)
        ChatListItem(name = "AX-SWIGGY", preview = "", date = "Mon", status = ChatListItemStatus.Loading, chips = chips)
        ChatListItem(name = "Amazon", preview = "OTP for your delivery is 4821", date = "07 July", muted = true)
    }
}
