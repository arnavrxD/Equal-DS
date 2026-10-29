package ai.equal.ds.components

import androidx.compose.foundation.layout.offset
import ai.equal.ds.icons.EqualSymbols
import ai.equal.ds.assets.EqualAssetImage
import ai.equal.ds.assets.EqualAssets
import ai.equal.ds.icons.EqualIcon
import ai.equal.ds.icons.EqualIcons
import ai.equal.ds.theme.EqualTheme
import ai.equal.ds.theme.eqPressFeedback
import ai.equal.ds.theme.toBrush
import ai.equal.ds.tokens.EqualPrimitives
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/** mm:ss (h:mm:ss past an hour). */
fun formatCallTime(seconds: Int): String {
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60
    return if (h > 0) "%d:%02d:%02d".format(h, m, s) else "%02d:%02d".format(m, s)
}

/** Seconds elapsed while [running]; freezes when it stops. */
@Composable
fun rememberCallTimer(running: Boolean, start: Int = 0): Int {
    var s by remember(start) { mutableIntStateOf(start) }
    LaunchedEffect(running) { while (running) { delay(1000); s++ } }
    return s
}

/** Which in-call control a [CallControlButton] is. */
enum class CallControlKind { Speaker, Mute, End, Accept, Decline, Message }

/**
 * Round in-call control (52, icon 24). Speaker and mute are switches (Role.Switch): resting
 * `call-control/bg` with a 1dp `stroke/medium` edge, on = `call-control/active-bg`. End is
 * `button/end-call` with `text/on-end-call`.
 */
@Composable
fun CallControlButton(kind: CallControlKind, onClick: () -> Unit, modifier: Modifier = Modifier, active: Boolean = false, size: Dp = 52.dp, a11yLabel: String? = null) {
    val colors = EqualTheme.colors
    val (icon, defaultLabel) = when (kind) {
        CallControlKind.Speaker -> EqualIcons.VolumeUp to "Speaker"
        CallControlKind.Mute -> EqualIcons.MicOff to "Mute"
        CallControlKind.End -> EqualIcons.CallEnd to "End call"
        CallControlKind.Accept -> EqualIcons.CallFilled to "Answer"
        CallControlKind.Decline -> EqualIcons.CallEnd to "Decline"
        CallControlKind.Message -> EqualSymbols.message to "Message"
    }
    val label = a11yLabel ?: defaultLabel
    val toggle = kind == CallControlKind.Speaker || kind == CallControlKind.Mute
    val red = kind == CallControlKind.End || kind == CallControlKind.Decline
    val fill = when { red -> colors.buttonEndCall; kind == CallControlKind.Accept -> colors.buttonPrimary; active -> colors.callControlActiveBg; else -> colors.callControlBg }
    val tint = when { red -> colors.textOnEndCall; kind == CallControlKind.Accept -> colors.textOnAction; active -> colors.callControlActiveIcon; else -> colors.textPrimary }
    val edge = when { kind == CallControlKind.Message -> colors.strokeSubtle; toggle && !active -> colors.strokeMedium; else -> null }
    val interaction = rememberInteraction()
    val semanticsMod = if (!toggle) {
        Modifier.clickable(interactionSource = interaction, indication = null, role = Role.Button, onClickLabel = label, onClick = onClick).semantics { contentDescription = label }
    } else {
        Modifier.toggleable(value = active, interactionSource = interaction, indication = null, role = Role.Switch, onValueChange = { onClick() }).semantics { contentDescription = label }
    }
    Box(
        modifier
            .minimumInteractiveComponentSize()
            .then(semanticsMod)
            .eqPressFeedback(interaction, CircleShape)
            .size(size)
            .clip(CircleShape)
            .background(fill)
            .then(if (edge != null) Modifier.border(1.dp, edge, CircleShape) else Modifier),
        contentAlignment = Alignment.Center,
    ) { EqualIcon(icon, contentDescription = null, tint = tint, size = 24.dp) }
}

/** Pill of call controls (speaker, mute, end): `bg/primary`, 1dp `stroke/medium`, padding 12 / 32, gap 16. */
@Composable
fun CallControlBar(
    speaker: Boolean,
    muted: Boolean,
    onSpeaker: () -> Unit,
    onMute: () -> Unit,
    onEnd: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = EqualTheme.colors
    Row(
        modifier.background(colors.bgPrimary, CircleShape).border(1.dp, colors.strokeMedium, CircleShape).padding(horizontal = 32.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CallControlButton(CallControlKind.Speaker, onSpeaker, active = speaker)
        CallControlButton(CallControlKind.Mute, onMute, active = muted)
        CallControlButton(CallControlKind.End, onEnd)
    }
}

/** Elapsed call time: `body2` tabular, `text/secondary`. */
@Composable
fun CallTimer(seconds: Int, modifier: Modifier = Modifier) {
    EqualText(text = formatCallTime(seconds), style = EqualTheme.type.body2.copy(fontFeatureSettings = "tnum"), color = EqualTheme.colors.textSecondary, modifier = modifier)
}

/**
 * Who you're on the call with: a 200 disc (`gradient/assistant-call`) with the assistant bust (or a
 * person's [initials] on the green contact disc), plus three `button/primary` ripple rings
 * (280 / 360 / 440 at 35 / 22 / 12 %) when [ripples]. [pulse] sends a ring outward (static under
 * reduced motion).
 */
@Composable
fun CallAvatar(
    modifier: Modifier = Modifier,
    initials: String? = null,
    ripples: Boolean = false,
    pulse: Boolean = false,
    size: Dp = 200.dp,
    contentDescription: String = "Equal AI assistant",
    /** Heads-up: the call-received badge (24) at the bottom-right. */
    badge: Boolean = false,
) {
    val colors = EqualTheme.colors
    val ring = colors.buttonPrimary
    val still = rememberReduceMotion()
    val progress = if (pulse && !still) {
        rememberInfiniteTransition(label = "call-pulse").animateFloat(0f, 1f, infiniteRepeatable(tween(1600, easing = LinearEasing), RepeatMode.Restart), label = "p")
    } else null
    Box(
        modifier
            .size(size)
            .semantics { this.contentDescription = contentDescription }
            .drawBehind {
                val r = this.size.minDimension / 2
                if (ripples) {
                    listOf(Triple(1.4f, 2f, 0.35f), Triple(1.8f, 1.5f, 0.22f), Triple(2.2f, 1f, 0.12f)).forEach { (k, w, a) ->
                        drawCircle(ring.copy(alpha = a), radius = r * k, style = Stroke(w.dp.toPx()))
                    }
                }
                progress?.value?.let { p -> drawCircle(ring.copy(alpha = 0.35f * (1 - p)), radius = r * (1 + 0.8f * p), style = Stroke(2.dp.toPx())) }
            },
        contentAlignment = Alignment.Center,
    ) {
        val disc = Modifier.fillMaxSize().clip(CircleShape)
        if (initials != null) {
            // Initials: DM Sans 600 at 40 % of the disc, on `call-avatar/bg`.
            val fs = (size.value * 0.4f).toInt()
            Box(disc.background(colors.callAvatarBg), contentAlignment = Alignment.Center) {
                EqualText(text = initials, style = EqualTheme.type.headline.copy(fontSize = fs.sp, lineHeight = (fs * 1.15f).toInt().sp, fontWeight = FontWeight.W600), color = colors.callAvatarText)
            }
        } else {
            Box(disc.background(EqualTheme.gradients.assistantCall.toBrush()), contentAlignment = Alignment.BottomCenter) {
                EqualAssetImage(EqualAssets.AvatarAssistantBust, size = size * 0.96f)
            }
        }
        if (badge) {
            Box(
                Modifier.align(Alignment.BottomEnd).offset(4.dp, 4.dp).size(24.dp).clip(CircleShape).background(colors.systemCallBadge),
                contentAlignment = Alignment.Center,
            ) { EqualIcon(EqualIcons.CallReceived, contentDescription = null, tint = colors.textAction, size = 16.dp) }
        }
    }
}

/** Phase of an in-app call. */
enum class CallPhase { Connecting, Calling, Ringing, Connected, Ended }

/** Status line: `title`; Connected in `text/action`, others (and "On mute") `text/secondary`. */
@Composable
fun CallStatusLine(phase: CallPhase, modifier: Modifier = Modifier, muted: Boolean = false) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    if (phase == CallPhase.Connected && muted) {
        Row(modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            EqualIcon(EqualIcons.MicOff, contentDescription = null, tint = colors.textSecondary, size = 24.dp)
            EqualText(text = "On mute", style = type.title, color = colors.textSecondary)
        }
        return
    }
    val label = when (phase) { CallPhase.Connecting -> "Connecting…"; CallPhase.Calling -> "Calling…"; CallPhase.Ringing -> "Ringing…"; CallPhase.Connected -> "Connected"; CallPhase.Ended -> "Call ended" }
    EqualText(text = label, style = type.title, color = if (phase == CallPhase.Connected) colors.textAction else colors.textSecondary, modifier = modifier)
}

/**
 * The in-app call screen (vOIP design), 360 × 800: wallpaper → status bar → back + logo → call
 * avatar → status → timer → control bar 32 from the bottom. Ripples and controls show only while
 * connected. [overlay] draws on top (the mic permission dialog).
 */
@Composable
fun CallScreen(
    phase: CallPhase,
    modifier: Modifier = Modifier,
    seconds: Int = 0,
    speaker: Boolean = false,
    muted: Boolean = false,
    onSpeaker: () -> Unit = {},
    onMute: () -> Unit = {},
    onEnd: () -> Unit = {},
    onBack: () -> Unit = {},
    initials: String? = null,
    name: String? = null,
    subtitle: (@Composable () -> Unit)? = null,
    overlay: (@Composable BoxScope.() -> Unit)? = null,
) {
    val live = phase == CallPhase.Connected
    Box(modifier.fillMaxSize()) {
        ChatWallpaper(Modifier.fillMaxSize()) {
            Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                StatusBar(background = androidx.compose.ui.graphics.Color.Transparent)
                Box(Modifier.fillMaxWidth().height(56.dp).padding(horizontal = 8.dp)) {
                    EqualIconButton(EqualIcons.Back, contentDescription = "Back", onClick = onBack, modifier = Modifier.align(Alignment.CenterStart), variant = EqualIconButtonVariant.Glass)
                    EqualLogo(height = 24.dp, modifier = Modifier.align(Alignment.Center))
                }
                Spacer(Modifier.height(172.dp))
                CallAvatar(initials = initials, ripples = live, pulse = phase == CallPhase.Connecting || phase == CallPhase.Ringing, contentDescription = name ?: "Equal AI assistant")
                if (name != null) EqualText(text = name, style = EqualTheme.type.headline, color = EqualTheme.colors.textPrimary, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 24.dp))
                subtitle?.invoke()
                Spacer(Modifier.height(if (name != null) 8.dp else 20.dp))
                CallStatusLine(phase, muted = muted)
                if (live || phase == CallPhase.Ended) CallTimer(seconds, Modifier.padding(top = 4.dp))
            }
        }
        if (live) CallControlBar(speaker, muted, onSpeaker, onMute, onEnd, Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp))
        overlay?.invoke(this)
    }
}

/**
 * Docs stand-in for Android's runtime permission dialog (the real one is drawn by the OS): the
 * message with the app name in bold, then full-width Allow / Deny rows.
 */
@Composable
fun SystemPermissionStandIn(onAllow: () -> Unit, onDeny: () -> Unit, modifier: Modifier = Modifier, app: String = "Equal AI", what: String = "access your microphone") {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    Box(modifier.fillMaxSize().background(EqualPrimitives.black.copy(alpha = 0.45f)), contentAlignment = Alignment.Center) {
        Column(Modifier.width(280.dp).clip(RoundedCornerShape(8.dp)).background(colors.bgSurface).padding(top = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            EqualIcon(EqualIcons.Mic, contentDescription = null, tint = colors.textPrimary, size = 24.dp)
            Spacer(Modifier.height(16.dp))
            BasicText(
                text = buildAnnotatedString { append("Allow "); withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(app) }; append(" to $what.") },
                style = type.bodyLarge.copy(color = colors.textPrimary, textAlign = TextAlign.Center),
                modifier = Modifier.padding(horizontal = 24.dp),
            )
            Spacer(Modifier.height(16.dp))
            listOf("Allow" to onAllow, "Deny" to onDeny).forEach { (label, action) ->
                EqualDivider()
                Box(Modifier.fillMaxWidth().height(48.dp).clickable(role = Role.Button, onClick = action), contentAlignment = Alignment.Center) {
                    EqualText(text = label, style = type.button, color = colors.textAction)
                }
            }
        }
    }
}

