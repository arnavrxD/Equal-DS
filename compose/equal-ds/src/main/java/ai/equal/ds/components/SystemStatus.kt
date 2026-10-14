package ai.equal.ds.components

import ai.equal.ds.assets.EqualAssetImage
import ai.equal.ds.assets.EqualAssets
import ai.equal.ds.icons.EqualIcon
import ai.equal.ds.icons.EqualIcons
import ai.equal.ds.theme.EqualShadow
import ai.equal.ds.theme.EqualShadowLayer
import ai.equal.ds.theme.EqualTheme
import ai.equal.ds.theme.eqShadow
import ai.equal.ds.theme.toBrush
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Temporarily Unavailable design: the system-status card on Chats, the heads-up notification
// when the assistant comes back, and the Meet Your Assistant card. Port of web SystemStatus.tsx.

enum class SystemStatusState { Unavailable, Notified }

/**
 * "System status" card that takes the assistant toggle's place (pass it to [HomeHeader]'s
 * `status` slot) while Equal AI is down. [SystemStatusState.Unavailable] shows the amber
 * "Notify me when ready" pill; after the tap flip to [SystemStatusState.Notified].
 */
@Composable
fun SystemStatusCard(
    state: SystemStatusState,
    onNotifyClick: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = "Temporarily Unavailable",
    body: String = "We’re already working on a fix. Thanks for your patience.",
    label: String = "System status",
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    Box(
        modifier
            .fillMaxWidth()
            .heightIn(min = 140.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(EqualTheme.gradients.statusCard.toBrush())
            .semantics { liveRegion = LiveRegionMode.Polite },
    ) {
        EqualAssetImage(
            asset = EqualAssets.MascotSleeping,
            size = 104.dp,
            modifier = Modifier.align(Alignment.CenterEnd).offset(y = 4.dp),
        )
        Column(Modifier.padding(16.dp)) {
            Box(
                Modifier
                    .height(20.dp)
                    .clip(CircleShape)
                    .background(colors.statusCardPillBg)
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                EqualText(text = label.uppercase(), style = type.overline.copy(fontSize = 10.sp), color = colors.textSecondary)
            }
            Spacer(Modifier.height(8.dp))
            EqualText(
                text = title,
                style = type.subheading.copy(fontSize = 18.sp, lineHeight = 24.sp, fontWeight = FontWeight.Bold),
                color = colors.textPrimary,
                maxLines = 1,
                softWrap = false,
            )
            Spacer(Modifier.height(2.dp))
            EqualText(text = body, style = type.captionRegular, color = colors.textSecondary, modifier = Modifier.widthIn(max = 190.dp))
            Spacer(Modifier.height(14.dp))
            Box(Modifier.heightIn(min = 32.dp), contentAlignment = Alignment.CenterStart) {
                when (state) {
                    SystemStatusState.Unavailable -> Row(
                        Modifier
                            .height(32.dp)
                            .clip(CircleShape)
                            .background(colors.statusCardButton)
                            .clickable(role = Role.Button, onClick = onNotifyClick)
                            .padding(start = 12.dp, end = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        EqualIcon(EqualIcons.NotifyMe, contentDescription = null, tint = colors.statusCardButtonText, size = 16.dp)
                        EqualText(text = "Notify me when ready", style = type.buttonSmall.copy(fontWeight = FontWeight.Bold), color = colors.statusCardButtonText)
                    }
                    SystemStatusState.Notified -> Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        EqualIcon(EqualIcons.Check, contentDescription = null, tint = colors.textAction, size = 16.dp)
                        EqualText(text = "You’ll be notified soon", style = type.buttonSmall.copy(fontWeight = FontWeight.Bold), color = colors.textAction)
                    }
                }
            }
        }
    }
}

private val NotificationShadow = EqualShadow(
    listOf(
        EqualShadowLayer(0.dp, 8.dp, 28.dp, Color.Black.copy(alpha = 0.14f)),
        EqualShadowLayer(0.dp, 1.dp, 3.dp, Color.Black.copy(alpha = 0.08f)),
    ),
)

/**
 * The Android heads-up notification, drawn for catalog screens and flows. In the app, post a real
 * notification (NotificationCompat) with the same title/body; the OS draws it.
 */
@Composable
fun SystemNotificationPreview(
    title: String,
    body: String,
    modifier: Modifier = Modifier,
    app: String = "Equal AI",
    time: String = "Now",
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    val shape = RoundedCornerShape(20.dp)
    Column(
        modifier
            .fillMaxWidth()
            .eqShadow(NotificationShadow, shape)
            .background(colors.notificationBg, shape)
            .padding(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            EqualText(text = app, style = type.micro.copy(fontSize = 10.sp), color = colors.textPrimary)
            Box(Modifier.size(3.dp).clip(CircleShape).background(colors.textPlaceholder))
            EqualText(text = time, style = type.micro.copy(fontSize = 10.sp), color = colors.textPrimary)
        }
        Spacer(Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            EqualLogoOrb(size = 32.dp, contentDescription = null)
            Column {
                EqualText(text = title, style = type.caption.copy(fontWeight = FontWeight.Bold), color = colors.textPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                EqualText(text = body, style = type.captionRegular, color = colors.textSecondary)
            }
        }
    }
}

/** [MeetAssistantCard] layouts. */
enum class MeetAssistantLayout {
    /** Mascot left, title + Call me: sits on Chats once the assistant is back. */
    Compact,

    /** Text left, mascot right: the standalone promo. */
    Banner,

    /** Centred mascot, headline, body and a full-width Call me: fills an empty home for a new user (Known Calls Flow). */
    Hero,
}

/**
 * The green "Call me" pill (web `CallMeButton`): `gradient/call-me`, 40 high (h18 / 22) with a
 * shadow 0 6 16 `button/primary` 28%, call-sparkle 18 + [label] `button`, both `text/on-action`
 * (gap 8). [block] makes it 52 high and full width (the [MeetAssistantLayout.Hero] CTA).
 */
@Composable
fun CallMeButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    block: Boolean = false,
) {
    val colors = EqualTheme.colors
    Row(
        modifier
            .then(if (block) Modifier.fillMaxWidth().height(52.dp) else Modifier.height(40.dp))
            .eqShadow(EqualShadow(listOf(EqualShadowLayer(0.dp, 6.dp, 16.dp, colors.buttonPrimary.copy(alpha = 0.28f)))), CircleShape)
            .clip(CircleShape)
            .background(EqualTheme.gradients.callMe.toBrush())
            .clickable(role = Role.Button, onClick = onClick)
            .padding(start = 18.dp, end = 22.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
    ) {
        EqualIcon(EqualIcons.CallSparkle, contentDescription = null, tint = colors.textOnAction, size = 18.dp)
        EqualText(text = label, style = EqualTheme.type.button, color = colors.textOnAction)
    }
}

/**
 * Promo that introduces the voice assistant. [MeetAssistantLayout.Compact] sits in the Chats promo
 * slot once the assistant is back; [MeetAssistantLayout.Banner] is the standalone card;
 * [MeetAssistantLayout.Hero] (radius 24, padding 40 / 24 / 28) centres the 170 × 190 mascot, the
 * [title] `headline` Bold `text/action`, the [body] and a 52dp full-width Call me, for a new
 * user's empty home.
 * Pass [onDismiss] to show the close (X).
 */
@Composable
fun MeetAssistantCard(
    onCallClick: () -> Unit,
    modifier: Modifier = Modifier,
    layout: MeetAssistantLayout = MeetAssistantLayout.Compact,
    title: String = "Meet Your Assistant",
    body: String = "Try talking to your assistant about what all it can do",
    ctaLabel: String = "Call me",
    onDismiss: (() -> Unit)? = null,
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    val radius = when (layout) {
        MeetAssistantLayout.Banner -> 20.dp
        MeetAssistantLayout.Hero -> 24.dp
        MeetAssistantLayout.Compact -> 16.dp
    }
    Box(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(radius))
            .background(EqualTheme.gradients.meetAssistant.toBrush()),
    ) {
        when (layout) {
            MeetAssistantLayout.Compact -> Row(
                Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                EqualAssetImage(EqualAssets.AvatarAssistant, DpSize(84.dp, 94.dp), contentDescription = "Your assistant")
                Column {
                    EqualText(text = title, style = type.subheading, color = colors.textPrimary)
                    Spacer(Modifier.height(10.dp))
                    CallMeButton(label = ctaLabel, onClick = onCallClick)
                }
            }
            MeetAssistantLayout.Banner -> Row(
                Modifier.padding(start = 18.dp, top = 20.dp, end = 16.dp, bottom = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Column(Modifier.weight(1f).padding(bottom = 6.dp)) {
                    EqualText(text = title, style = type.title.copy(fontWeight = FontWeight.Bold), color = colors.textAction)
                    Spacer(Modifier.height(2.dp))
                    EqualText(text = body, style = type.body1, color = colors.textSecondary)
                    Spacer(Modifier.height(16.dp))
                    CallMeButton(label = ctaLabel, onClick = onCallClick)
                }
                EqualAssetImage(
                    EqualAssets.AvatarAssistant,
                    DpSize(100.dp, 112.dp),
                    modifier = Modifier.align(Alignment.Bottom).padding(top = 24.dp),
                    contentDescription = "Your assistant",
                )
            }
            MeetAssistantLayout.Hero -> Column(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, top = 40.dp, end = 24.dp, bottom = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                EqualAssetImage(EqualAssets.AvatarAssistant, DpSize(170.dp, 190.dp), contentDescription = "Your assistant")
                Spacer(Modifier.height(28.dp))
                EqualText(
                    text = title,
                    style = type.headline.copy(fontWeight = FontWeight.Bold),
                    color = colors.textAction,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(6.dp))
                EqualText(
                    text = body,
                    style = type.subheading.copy(fontWeight = FontWeight.W400),
                    color = colors.textPrimary,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(32.dp))
                CallMeButton(label = ctaLabel, onClick = onCallClick, block = true)
            }
        }
        if (onDismiss != null) {
            Box(
                Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .size(32.dp)
                    .clip(CircleShape)
                    .clickable(role = Role.Button, onClick = onDismiss),
                contentAlignment = Alignment.Center,
            ) {
                EqualIcon(EqualIcons.Close, contentDescription = "Dismiss", tint = colors.textPlaceholder, size = 20.dp)
            }
        }
    }
}

@EqualPreviews
@Composable
private fun SystemStatusPreview() {
    PreviewFrame {
        var state by remember { mutableStateOf(SystemStatusState.Unavailable) }
        SystemStatusCard(state = state, onNotifyClick = { state = SystemStatusState.Notified })
        VSpace(16.dp)
        SystemNotificationPreview(title = "Equal AI Assistant is back on", body = "Equal AI is now handling all your unknown calls.")
        VSpace(16.dp)
        MeetAssistantCard(onCallClick = {}, onDismiss = {})
        VSpace(16.dp)
        MeetAssistantCard(onCallClick = {}, layout = MeetAssistantLayout.Banner, onDismiss = {})
        VSpace(16.dp)
        MeetAssistantCard(
            onCallClick = {},
            layout = MeetAssistantLayout.Hero,
            title = "Talk With Your Assistant",
            body = "Explore the app, check out features, ask questions, or just say \"Hi\"!",
        )
    }
}

@EqualPreviews
@Composable
private fun CallMeButtonPreview() {
    PreviewFrame {
        CallMeButton(label = "Call me", onClick = {})
        VSpace(16.dp)
        CallMeButton(label = "Call me", onClick = {}, block = true)
    }
}
