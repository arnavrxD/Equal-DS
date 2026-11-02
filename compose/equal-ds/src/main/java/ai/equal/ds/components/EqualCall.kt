package ai.equal.ds.components

import ai.equal.ds.icons.EqualIcon
import ai.equal.ds.icons.EqualIcons
import ai.equal.ds.icons.EqualSymbols
import ai.equal.ds.theme.EqualEasing
import ai.equal.ds.theme.EqualTheme
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/** "Identified by" + the Equal logo in `text/secondary` (the Equal-user badge). One node: "Identified by Equal AI". */
@Composable
fun IdentifiedByEqual(modifier: Modifier = Modifier, large: Boolean = true) {
    val colors = EqualTheme.colors
    Row(
        modifier.clearAndSetSemantics { contentDescription = "Identified by Equal AI" },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        EqualText(text = "Identified by", style = if (large) EqualTheme.type.body2 else EqualTheme.type.caption, color = colors.textSecondary)
        EqualLogo(height = if (large) 14.dp else 12.dp, color = colors.textSecondary, contentDescription = null)
    }
}

/** A [CallControlButton] with a caption (`body2`, 6 below). */
@Composable
private fun LabelledControl(kind: CallControlKind, label: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        CallControlButton(kind, onClick)
        EqualText(text = label, style = EqualTheme.type.body2, color = EqualTheme.colors.textPrimary, modifier = Modifier.clearAndSetSemantics {})
    }
}

/**
 * Incoming-call actions: Decline · [chevrons → Accept → "Swipe up to Accept"] · Message. Drag Accept
 * up 86dp to answer (it springs back when let go early); TalkBack gets an "Answer" action.
 */
@Composable
fun SwipeToAnswer(onAccept: () -> Unit, onDecline: () -> Unit, modifier: Modifier = Modifier, onMessage: () -> Unit = {}) {
    val colors = EqualTheme.colors
    val scope = rememberCoroutineScope()
    val drag = remember { Animatable(0f) }
    val reduce = rememberReduceMotion()
    val bob = if (reduce) {
        0f
    } else {
        val t = rememberInfiniteTransition(label = "swipe")
        val v by t.animateFloat(0f, -4f, infiniteRepeatable(tween(600, easing = EqualEasing.standard), RepeatMode.Reverse), label = "bob")
        v
    }
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
        LabelledControl(CallControlKind.Decline, "Decline", onDecline)
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Column(Modifier.padding(bottom = 18.dp).offset(y = bob.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(2.dp)) {
                listOf(1f, 0.7f, 0.4f, 0.2f).forEach { EqualIcon(EqualSymbols.chevronUp, contentDescription = null, tint = colors.textPrimary, size = 20.dp, modifier = Modifier.alpha(it)) }
            }
            Box(
                Modifier
                    .offset { IntOffset(0, drag.value.roundToInt()) }
                    .pointerInput(Unit) {
                        val limit = 86.dp.toPx()
                        detectVerticalDragGestures(
                            onDragEnd = { scope.launch { if (-drag.value >= limit) onAccept(); drag.animateTo(0f) } },
                            onDragCancel = { scope.launch { drag.animateTo(0f) } },
                        ) { _, dy -> scope.launch { drag.snapTo((drag.value + dy).coerceIn(-limit, 0f)) } }
                    }
                    .semantics { customActions = listOf(CustomAccessibilityAction("Answer") { onAccept(); true }) },
            ) { CallControlButton(CallControlKind.Accept, onAccept) }
            EqualText(text = "Swipe up to Accept", style = EqualTheme.type.body2, color = colors.textPrimary, modifier = Modifier.clearAndSetSemantics {})
        }
        LabelledControl(CallControlKind.Message, "Message", onMessage)
    }
}

/** Who is calling and from which side. */
enum class EqualCallRole { Caller, Receiver }

/** State of an Equal-to-Equal call screen. */
enum class EqualCallState { Ringing, Connecting, Calling, RemoteRinging, Connected, Ended }

/**
 * Equal user to Equal user call (Equal To Equal Call Experience design). Ringing / Connecting: the
 * large name + [IdentifiedByEqual], the 200 initials disc, [SwipeToAnswer]. Calling / Ringing (caller)
 * / Connected: the compact header (Minimise · name + status · Message for the caller), ripples once
 * connected, "Connected" + timer, [CallControlBar] 40 from the bottom.
 */
@Composable
fun EqualCallScreen(
    role: EqualCallRole,
    state: EqualCallState,
    name: String,
    initials: String,
    modifier: Modifier = Modifier,
    seconds: Int = 0,
    speaker: Boolean = false,
    muted: Boolean = false,
    onAccept: () -> Unit = {},
    onDecline: () -> Unit = {},
    onMessage: () -> Unit = {},
    onMinimise: () -> Unit = {},
    onSpeaker: () -> Unit = {},
    onMute: () -> Unit = {},
    onEnd: () -> Unit = {},
    overlay: (@Composable () -> Unit)? = null,
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    val large = state == EqualCallState.Ringing || state == EqualCallState.Connecting
    val live = state == EqualCallState.Connected
    Box(modifier.fillMaxSize()) {
        ChatWallpaper(Modifier.fillMaxSize()) {
            Column(Modifier.fillMaxSize()) {
                StatusBar(background = androidx.compose.ui.graphics.Color.Transparent)
                if (large) {
                    Column(Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp, top = 48.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        EqualText(text = name, style = type.headline, color = colors.textPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center, modifier = Modifier.semantics { heading() })
                        IdentifiedByEqual()
                    }
                } else {
                    Row(Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp, top = 48.dp), verticalAlignment = Alignment.CenterVertically) {
                        SubtleCircle(EqualSymbols.minimise, "Minimise call", onMinimise)
                        Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                            EqualText(text = name, style = type.subheading, color = colors.textPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.semantics { heading() })
                            when (state) {
                                EqualCallState.Calling -> EqualText(text = "Calling…", style = type.body1, color = colors.textSecondary, modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite })
                                EqualCallState.RemoteRinging -> EqualText(text = "Ringing…", style = type.body1, color = colors.textSecondary, modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite })
                                else -> IdentifiedByEqual(large = false)
                            }
                        }
                        if (role == EqualCallRole.Caller) SubtleCircle(EqualSymbols.message, "Message", onMessage) else Spacer(Modifier.width(48.dp))
                    }
                }
                Column(
                    Modifier.fillMaxWidth().weight(1f).padding(top = when (state) { EqualCallState.Ringing -> 163.dp; EqualCallState.Connecting -> 139.dp; else -> 116.dp }),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    CallAvatar(initials = initials, ripples = live, pulse = state == EqualCallState.RemoteRinging, contentDescription = name)
                    when {
                        state == EqualCallState.Connecting -> {
                            Spacer(Modifier.height(24.dp))
                            EqualText(text = "Connecting...", style = type.title, color = colors.textSecondary, modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite })
                        }
                        live -> {
                            Spacer(Modifier.height(24.dp))
                            CallStatusLine(CallPhase.Connected, muted = muted)
                            Spacer(Modifier.height(4.dp))
                            CallTimer(seconds)
                        }
                        state == EqualCallState.Ended -> {
                            Spacer(Modifier.height(24.dp))
                            CallStatusLine(CallPhase.Ended)
                        }
                        else -> Unit
                    }
                }
                Box(Modifier.fillMaxWidth().navigationBarsPadding().padding(start = 24.dp, end = 24.dp, bottom = 40.dp), contentAlignment = Alignment.Center) {
                    if (state == EqualCallState.Ringing) SwipeToAnswer(onAccept = onAccept, onDecline = onDecline, onMessage = onMessage)
                    if (!large && state != EqualCallState.Ended) CallControlBar(speaker = speaker, muted = muted, onSpeaker = onSpeaker, onMute = onMute, onEnd = onEnd)
                }
            }
        }
        overlay?.invoke()
    }
}

/** 40 `call-control/subtle-bg` circle with a 24 `text/primary` icon, in a 48 target. */
@Composable
private fun SubtleCircle(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {
    val colors = EqualTheme.colors
    Box(
        Modifier.minimumInteractiveComponentSize().clickable(role = Role.Button, onClickLabel = label, onClick = onClick).semantics { contentDescription = label },
        contentAlignment = Alignment.Center,
    ) {
        Box(Modifier.size(40.dp).clip(CircleShape).background(colors.callControlSubtleBg), contentAlignment = Alignment.Center) {
            EqualIcon(icon, contentDescription = null, tint = colors.textPrimary, size = 24.dp)
        }
    }
}

/** Incoming [CallStrip] options (the experiment). */
enum class CallStripStyle { Avatar, Icon, Accent }

/**
 * Full-width in-app incoming-call strip under the status bar (`call-strip/bg`): one button "Answer
 * call from …". [style] Avatar (option 1, 60 high), Icon (2) or Accent (3, all green), 52 high.
 */
@Composable
fun IncomingCallStrip(name: String, onAnswer: () -> Unit, modifier: Modifier = Modifier, initials: String = "", style: CallStripStyle = CallStripStyle.Avatar) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    Row(
        modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = if (style == CallStripStyle.Avatar) 60.dp else 52.dp)
            .background(colors.callStripBg)
            .clickable(role = Role.Button, onClickLabel = "Answer", onClick = onAnswer)
            .clearAndSetSemantics { contentDescription = "Answer call from $name" }
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(if (style == CallStripStyle.Avatar) 8.dp else 4.dp),
    ) {
        if (style == CallStripStyle.Avatar) CallAvatar(initials = initials, size = 28.dp, contentDescription = "") else EqualIcon(EqualIcons.CallFilled, contentDescription = null, tint = colors.textAction, size = 20.dp)
        EqualText(text = name, style = type.body2.copy(fontWeight = if (style == CallStripStyle.Accent) androidx.compose.ui.text.font.FontWeight.W500 else androidx.compose.ui.text.font.FontWeight.W600), color = if (style == CallStripStyle.Avatar) colors.textPrimary else colors.textAction, maxLines = 1)
        if (style == CallStripStyle.Accent) {
            EqualText(text = "- Incoming call", style = type.body2, color = colors.textAction, maxLines = 1)
        } else {
            EqualText(text = "- Incoming Call", style = type.body1, color = colors.textSecondary, maxLines = 1)
        }
    }
}

/** Ongoing-call strip (72): mute · "Name - 0:21" (opens the call) · end, on `call-strip/bg`. */
@Composable
fun OngoingCallStrip(name: String, seconds: Int, onOpen: () -> Unit, onEnd: () -> Unit, modifier: Modifier = Modifier, muted: Boolean = false, onMute: () -> Unit = {}) {
    val colors = EqualTheme.colors
    val time = formatCallTime(seconds).removePrefix("0")
    Row(
        modifier.fillMaxWidth().defaultMinSize(minHeight = 72.dp).background(colors.callStripBg).padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        CallControlButton(CallControlKind.Mute, onMute, active = muted, size = 40.dp)
        Row(
            Modifier
                .weight(1f)
                .minimumInteractiveComponentSize()
                .clickable(role = Role.Button, onClick = onOpen)
                .clearAndSetSemantics { contentDescription = "Return to call with $name, $time" },
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            EqualIcon(EqualIcons.CallFilled, contentDescription = null, tint = colors.textAction, size = 20.dp)
            EqualText(text = name, style = EqualTheme.type.subheading, color = colors.textAction, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f, fill = false))
            EqualText(text = "-", style = EqualTheme.type.body2, color = colors.textAction)
            EqualText(text = time, style = EqualTheme.type.subheading.copy(fontFeatureSettings = "tnum"), color = colors.textAction)
        }
        CallControlButton(CallControlKind.End, onEnd, size = 40.dp)
    }
}

/**
 * Docs stand-in for Android's CallStyle heads-up (the OS draws it; post
 * `NotificationCompat.CallStyle.forIncomingCall`). Always drawn dark, as the system does.
 */
@Composable
fun IncomingCallNotification(name: String, initials: String, modifier: Modifier = Modifier, app: String = "Equal AI", time: String = "now", onAnswer: () -> Unit = {}, onDecline: () -> Unit = {}) {
    EqualTheme(darkTheme = true) {
        val colors = EqualTheme.colors
        val type = EqualTheme.type
        Column(modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(colors.notificationBg).padding(horizontal = 12.dp, vertical = 24.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CallAvatar(initials = initials, size = 48.dp, badge = true, contentDescription = "")
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        EqualText(text = name, style = type.subheading, color = colors.textOnDark, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f, fill = false))
                        EqualText(text = "• $app • $time", style = type.body2, color = colors.textOnDarkSecondary, maxLines = 1)
                    }
                    Spacer(Modifier.height(4.dp))
                    EqualText(text = "Incoming Voice Call", style = type.body2, color = colors.textOnDarkSecondary)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SystemCallPill("DECLINE", EqualSymbols.callEndOutline, colors.systemCallDecline, colors.systemCallDeclineText, onDecline, Modifier.weight(1f))
                SystemCallPill("ANSWER", EqualIcons.Call, colors.systemCallAnswer, colors.systemCallAnswerText, onAnswer, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun SystemCallPill(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, fill: androidx.compose.ui.graphics.Color, ink: androidx.compose.ui.graphics.Color, onClick: () -> Unit, modifier: Modifier) {
    Row(
        modifier.height(40.dp).clip(CircleShape).background(fill).clickable(role = Role.Button, onClick = onClick),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        EqualIcon(icon, contentDescription = null, tint = ink, size = 24.dp)
        EqualText(text = label, style = EqualTheme.type.subheading, color = ink)
    }
}

/**
 * Call Takeover message (Personal Number call): your right-side bubble (`chat-bubble/other-bg`,
 * radius 12 with the top-end corner square, padding 20 top / 8) with the "You" label (10 SemiBold
 * `text/success`) in its top corner, holding the takeover card: 252 wide, `takeover-card/bg` with a
 * 1dp `takeover-card/stroke`, radius 12, padding 8; a 40 `bg/surface` disc with phone-incoming in
 * `text/action`, the title (body2) over what happened (captionWrap, 2 apart), and the time
 * (badgeCount) in the card's bottom-end corner. Put it in a [MessageList] like a [ChatBubble].
 */
@Composable
fun TakeoverCard(time: String, modifier: Modifier = Modifier, title: String = "Call taken over", body: String = "You took over the call", sender: String = "You") {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    Row(modifier.messageSide(MessageSide.Right).fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        Box(
            Modifier
                .clip(bubbleShape(outgoing = true, lead = true))
                .background(colors.chatBubbleOtherBg)
                .padding(start = 8.dp, end = 8.dp, top = 20.dp, bottom = 8.dp)
                .semantics(mergeDescendants = true) {},
        ) {
            EqualText(text = sender, style = type.badgeCount.copy(fontWeight = FontWeight.SemiBold), color = colors.textSuccess, modifier = Modifier.offset(y = (-16).dp))
            Box(
                Modifier
                    .width(252.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.takeoverCardBg)
                    .border(1.dp, colors.takeoverCardStroke, RoundedCornerShape(12.dp))
                    .padding(8.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(Modifier.size(40.dp).clip(CircleShape).background(colors.bgSurface), contentAlignment = Alignment.Center) {
                        EqualIcon(EqualIcons.PhoneIncoming, contentDescription = null, tint = colors.textAction, size = 24.dp)
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        EqualText(text = title, style = type.body2, color = colors.textPrimary)
                        EqualText(text = body, style = type.captionWrap, color = colors.textSecondary)
                    }
                }
                EqualText(text = time, style = type.badgeCount, color = colors.textSecondary, modifier = Modifier.align(Alignment.BottomEnd).offset(y = 5.dp))
            }
        }
    }
}

/** A call in the thread: icon circle (40, `action-card/highlight`) + "Voice Call" + the duration, for an outgoing bubble. */
@Composable
fun CallLogCard(duration: String, modifier: Modifier = Modifier, outgoing: Boolean = true, title: String = "Voice Call") {
    val colors = EqualTheme.colors
    Row(
        modifier.clip(RoundedCornerShape(12.dp)).background(colors.bgPrimary).padding(8.dp).semantics(mergeDescendants = true) {},
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(Modifier.size(40.dp).clip(CircleShape).background(colors.actionCardHighlight), contentAlignment = Alignment.Center) {
            EqualIcon(if (outgoing) EqualIcons.CallMade else EqualIcons.CallReceived, contentDescription = null, tint = colors.textAction, size = 24.dp)
        }
        Column {
            EqualText(text = title, style = EqualTheme.type.body2, color = colors.textPrimary)
            EqualText(text = duration, style = EqualTheme.type.captionWrap, color = colors.textSecondary)
        }
    }
}
