package ai.equal.ds.components

import ai.equal.ds.icons.EqualIcon
import ai.equal.ds.icons.EqualIcons
import ai.equal.ds.theme.EqualEasing
import ai.equal.ds.theme.EqualShadow
import ai.equal.ds.theme.EqualShadowLayer
import ai.equal.ds.theme.EqualShadows
import ai.equal.ds.theme.EqualTheme
import ai.equal.ds.theme.eqPressFeedback
import ai.equal.ds.theme.eqShadow
import ai.equal.ds.theme.tabular
import ai.equal.ds.theme.toBrush
import ai.equal.ds.tokens.EqualMotion
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import ai.equal.ds.assets.EqualAssets
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.BasicText
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

// Global MYA (Global MYA design): the assistant sheet that opens from the green orb (FAB) on any
// screen. Greeting + quick actions, suggestion chips, text or voice input, hold-to-record, and the
// short task conversation it turns into (voice note → Thinking… → result card → Edit / Confirm).
// Mirrors web/src/components/Assistant.tsx.

/** Soft green glow under the voice-note buttons: 0 6 16 `button/primary` at 30%. */
@Composable
private fun voiceGlow(): EqualShadow =
    EqualShadow(listOf(EqualShadowLayer(0.dp, 6.dp, 16.dp, EqualTheme.colors.buttonPrimary.copy(alpha = 0.30f))))

// ---------------------------------------------------------------------------------------------
// Greeting
// ---------------------------------------------------------------------------------------------

/**
 * Sheet greeting: 56dp [EqualLogoOrb] → 12 → "[greeting], [name]" `headline` Bold (the name in
 * `text/action`) → [prompt] `body1` `text/secondary`, all centred. Padding 24 / 24 / 0, gap 2.
 * Use [greetingFor] for the time of day.
 */
@Composable
fun AssistantGreeting(
    name: String,
    modifier: Modifier = Modifier,
    greeting: String = "Good morning",
    prompt: String = "What can I help you with today?",
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    Column(
        modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp, top = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        EqualLogoOrb(size = 56.dp, contentDescription = null)
        Spacer(Modifier.height(8.dp))
        BasicText(
            text = buildAnnotatedString {
                append("$greeting, ")
                withStyle(SpanStyle(color = colors.textAction)) { append(name) }
            },
            style = type.headline.copy(color = colors.textPrimary, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center),
            modifier = Modifier.semantics { this.contentDescription = "$greeting, $name" },
        )
        EqualText(text = prompt, style = type.body1, color = colors.textSecondary, textAlign = TextAlign.Center)
    }
}

/** Time-of-day greeting for [AssistantGreeting]: before 12 morning, before 17 afternoon, else evening. */
fun greetingFor(hour: Int): String = when {
    hour < 12 -> "Good morning"
    hour < 17 -> "Good afternoon"
    else -> "Good evening"
}

// ---------------------------------------------------------------------------------------------
// Quick actions
// ---------------------------------------------------------------------------------------------

/** One [AssistantActions] tile. [icon] `null` draws the Equal six-dot mark. */
@Immutable
data class AssistantAction(
    val label: String,
    val icon: ImageVector? = null,
    val onClick: () -> Unit = {},
)

/**
 * The quick-action tiles under the greeting: equal-width white tiles (gap 10, padding 24 top /
 * 16 sides). Each tile: min 92 high, radius 16, `bg/surface`, `raised` shadow; icon 30 in
 * `text/action` (or the [EqualMark]) → 10 → label `body2` Regular `text/primary`, centred.
 */
@Composable
fun AssistantActions(
    items: List<AssistantAction>,
    modifier: Modifier = Modifier,
) {
    val colors = EqualTheme.colors
    val shape = RoundedCornerShape(16.dp)
    Row(
        modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items.forEach { action ->
            val interaction = rememberInteraction()
            Column(
                Modifier
                    .weight(1f)
                    .heightIn(min = 92.dp)
                    .eqPressFeedback(interaction, shape)
                    .eqShadow(EqualShadows.raised, shape)
                    .clip(shape)
                    .background(colors.bgSurface)
                    .clickable(interactionSource = interaction, indication = null, role = Role.Button, onClick = action.onClick)
                    .padding(horizontal = 6.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
            ) {
                if (action.icon == null) {
                    EqualMark(width = 30.dp, color = colors.textAction)
                } else {
                    EqualIcon(action.icon, contentDescription = null, tint = colors.textAction, size = 30.dp)
                }
                EqualText(
                    text = action.label,
                    style = EqualTheme.type.body2.copy(fontWeight = FontWeight.Normal),
                    color = colors.textPrimary,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

/**
 * "Try saying" + an example request, shown while recording. Both `title` Regular in
 * `text/secondary`, centred (the example 22/28 in curly quotes). Padding 32 / 32 / 24, gap 14.
 */
@Composable
fun TrySaying(
    example: String,
    modifier: Modifier = Modifier,
) {
    val colors = EqualTheme.colors
    val title = EqualTheme.type.title.copy(fontWeight = FontWeight.Normal)
    Column(
        modifier
            .fillMaxWidth()
            .padding(start = 32.dp, end = 32.dp, top = 32.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        EqualText(text = "Try saying", style = title, color = colors.textSecondary, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        EqualText(
            text = "“$example”",
            style = title.copy(fontSize = 22.sp, lineHeight = 28.sp),
            color = colors.textSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

// ---------------------------------------------------------------------------------------------
// Input
// ---------------------------------------------------------------------------------------------

/** [AssistantInput] modes. */
enum class AssistantInputMode {
    /** Message field + mic. */
    Text,

    /** Keyboard toggle + "Send voice note". */
    Voice,
}

/** 52dp `gradient/voice-note` circle with the green glow: the mic in text mode, send in the recorder. */
@Composable
private fun VoiceCircleButton(icon: ImageVector, iconSize: Dp, contentDescription: String, onClick: () -> Unit, rotation: Float = 0f) {
    val interaction = rememberInteraction()
    Box(
        Modifier
            .minimumInteractiveComponentSize()
            .eqPressFeedback(interaction, CircleShape)
            .size(52.dp)
            .eqShadow(voiceGlow(), CircleShape)
            .clip(CircleShape)
            .background(EqualTheme.gradients.voiceNote.toBrush())
            .clickable(interactionSource = interaction, indication = null, role = Role.Button, onClickLabel = contentDescription, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        EqualIcon(icon, contentDescription = contentDescription, tint = EqualTheme.colors.textOnAction, size = iconSize, modifier = Modifier.rotate(rotation))
    }
}

/** 48dp white circle with the `raised` shadow: keyboard toggle and delete. */
@Composable
private fun RoundSurfaceButton(icon: ImageVector, tint: Color, iconSize: Dp, contentDescription: String, onClick: () -> Unit) {
    val interaction = rememberInteraction()
    Box(
        Modifier
            .eqPressFeedback(interaction, CircleShape)
            .size(48.dp)
            .eqShadow(EqualShadows.raised, CircleShape)
            .clip(CircleShape)
            .background(EqualTheme.colors.bgSurface)
            .clickable(interactionSource = interaction, indication = null, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        EqualIcon(icon, contentDescription = contentDescription, tint = tint, size = iconSize)
    }
}

/**
 * The assistant sheet's input row (gap 12).
 * - [AssistantInputMode.Text]: a 48dp pill field (`bg/surface`, 1dp `stroke/subtle`;
 *   focused or [focused]: 1.5dp `button/primary`), `bodyLarge`, placeholder `text/placeholder`, padding 20;
 *   then the 52dp mic ([onMic]) in `gradient/voice-note`. IME Send calls [onSubmit] with the
 *   trimmed text.
 * - [AssistantInputMode.Voice]: a 48dp keyboard toggle ([onKeyboard]) and the 52dp "Send voice
 *   note" pill (mic 22 + `subheading` Bold `text/on-action`): tap → [onVoiceNote], press and
 *   hold → [onVoiceHold].
 *
 * Lift it above the keyboard in real use with `imePadding()` (the [AssistantSheetShell] overlay
 * does this).
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AssistantInput(
    mode: AssistantInputMode,
    modifier: Modifier = Modifier,
    value: String = "",
    onValueChange: (String) -> Unit = {},
    onSubmit: (String) -> Unit = {},
    onFocus: () -> Unit = {},
    onMic: () -> Unit = {},
    onKeyboard: () -> Unit = {},
    onVoiceNote: () -> Unit = {},
    onVoiceHold: (() -> Unit)? = null,
    focused: Boolean = false,
    placeholder: String = "Type a message...",
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    Row(
        modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        when (mode) {
            AssistantInputMode.Voice -> {
                RoundSurfaceButton(EqualIcons.Keyboard, colors.textSecondary, 26.dp, "Type instead", onKeyboard)
                val interaction = rememberInteraction()
                val shape = CircleShape
                Row(
                    Modifier
                        .weight(1f)
                        .eqPressFeedback(interaction, shape)
                        .height(52.dp)
                        .eqShadow(voiceGlow(), shape)
                        .clip(shape)
                        .background(EqualTheme.gradients.voiceNote.toBrush())
                        .combinedClickable(
                            interactionSource = interaction,
                            indication = null,
                            role = Role.Button,
                            onLongClickLabel = if (onVoiceHold != null) "Hold to record" else null,
                            onLongClick = onVoiceHold,
                            onClick = onVoiceNote,
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                ) {
                    EqualIcon(EqualIcons.MicFill, contentDescription = null, tint = colors.textOnAction, size = 22.dp)
                    EqualText(
                        text = "Send Voice Note",
                        style = type.subheading.copy(fontWeight = FontWeight.Bold),
                        color = colors.textOnAction,
                        maxLines = 1,
                    )
                }
            }
            AssistantInputMode.Text -> {
                var hasFocus by remember { mutableStateOf(false) }
                val active = focused || hasFocus
                val shape = CircleShape
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .background(colors.bgSurface, shape)
                        .border(if (active) 1.5.dp else 1.dp, if (active) colors.strokeFocus else colors.strokeSubtle, shape)
                        .onFocusChanged {
                            if (it.isFocused && !hasFocus) onFocus()
                            hasFocus = it.isFocused
                        }
                        .semantics { contentDescription = "Message your assistant" },
                    singleLine = true,
                    textStyle = type.bodyLarge.copy(color = colors.textPrimary),
                    cursorBrush = SolidColor(colors.textAction),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = { if (value.isNotBlank()) onSubmit(value.trim()) }),
                    decorationBox = { inner ->
                        Box(Modifier.fillMaxSize().padding(horizontal = 20.dp), contentAlignment = Alignment.CenterStart) {
                            if (value.isEmpty()) {
                                EqualText(text = placeholder, style = type.bodyLarge, color = colors.textPlaceholder, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                            inner()
                        }
                    },
                )
                // Mic while empty; as soon as there is text it becomes Send.
                if (value.isNotBlank()) {
                    VoiceCircleButton(EqualIcons.Send, 22.dp, "Send", { onSubmit(value.trim()) })
                } else {
                    VoiceCircleButton(EqualIcons.MicFill, 24.dp, "Voice note", onMic)
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Recorder
// ---------------------------------------------------------------------------------------------

/** [VoiceRecorder] states. */
enum class RecorderState {
    /** Ready (voice input prompt): dotted line instead of the waveform. */
    Idle,

    /** Recording after a tap: delete, timer + live waveform, send. */
    Recording,

    /** Press and hold: "Release to send, slide left to cancel". */
    Hold,

    /** Slid left while holding: "Release to cancel", red waveform. */
    Cancel,
}

private const val RecorderBarCount = 22

/** Live bar heights (dp): 4 + 10·|sin(i·1.7 + t·0.6)·cos(i·0.45 + t·0.2)|, ticking every 120ms. */
@Composable
private fun rememberRecordingBars(active: Boolean): List<Int> {
    val reduceMotion = rememberReduceMotion()
    var t by remember { mutableIntStateOf(0) }
    LaunchedEffect(active, reduceMotion) {
        if (!active || reduceMotion) return@LaunchedEffect
        while (true) {
            delay(120)
            t++
        }
    }
    return List(RecorderBarCount) { i ->
        4 + (10 * abs(sin(i * 1.7 + t * 0.6) * cos(i * 0.45 + t * 0.2))).roundToInt()
    }
}

private fun mmss(seconds: Int): String = "${seconds / 60}:${(seconds % 60).toString().padStart(2, '0')}"

/** Waveform (3dp bars, gap 2, 24 high) in [color], or the dotted idle line. */
@Composable
private fun RecorderWave(state: RecorderState, color: Color, modifier: Modifier = Modifier) {
    val bars = rememberRecordingBars(state != RecorderState.Idle)
    val dot = EqualTheme.colors.textSecondary
    if (state == RecorderState.Idle) {
        Canvas(modifier.widthIn(max = 160.dp).width(160.dp).height(24.dp)) {
            val step = 6.dp.toPx()
            val r = 1.2.dp.toPx()
            var x = step / 2f
            while (x < size.width) {
                drawCircle(dot, radius = r, center = Offset(x, size.height / 2f))
                x += step
            }
        }
        return
    }
    val barW = 3.dp
    val gap = 2.dp
    Canvas(modifier.width(barW * RecorderBarCount + gap * (RecorderBarCount - 1)).height(24.dp)) {
        val w = barW.toPx()
        val g = gap.toPx()
        bars.forEachIndexed { i, hDp ->
            val h = hDp.dp.toPx()
            drawRoundRect(
                color = color,
                topLeft = Offset(i * (w + g), (size.height - h) / 2f),
                size = Size(w, h),
                cornerRadius = CornerRadius(2.dp.toPx()),
            )
        }
    }
}

/**
 * Voice recorder bar for the assistant sheet.
 * - [RecorderState.Idle] / [RecorderState.Recording]: 48dp delete (`text/danger`, [onDelete]) →
 *   pill bar (48 high, fully rounded, `bg/surface`, 1.5dp `action/stroke/unread`, padding 18) with the timer
 *   ([seconds], `body1` tabular) and the waveform (idle: dotted line) → 52dp send ([onSend]).
 * - [RecorderState.Hold] / [RecorderState.Cancel]: hint `caption` Regular `text/secondary`
 *   ("Release to send, slide left to cancel" / "Release to cancel") above the pill with
 *   slide-left 18 + "Slide left to cancel" `body1` `text/secondary` and the waveform
 *   (`text/action`; cancel `text/danger`).
 *
 * The waveform animates while live and holds still under reduced motion.
 */
@Composable
fun VoiceRecorder(
    state: RecorderState,
    modifier: Modifier = Modifier,
    seconds: Int = 0,
    onDelete: () -> Unit = {},
    onSend: () -> Unit = {},
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    val waveColor = if (state == RecorderState.Cancel) colors.textDanger else colors.textAction
    val barShape = CircleShape
    val pill = Modifier
        .height(48.dp)
        .background(colors.bgSurface, barShape)
        .border(1.5.dp, colors.actionStrokeUnread, barShape)
        .padding(horizontal = 18.dp)
    if (state == RecorderState.Hold || state == RecorderState.Cancel) {
        Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            EqualText(
                text = if (state == RecorderState.Cancel) "Release to cancel" else "Release to send, slide left to cancel",
                style = type.captionRegular,
                color = colors.textSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().semantics { liveRegion = LiveRegionMode.Polite },
            )
            Row(pill.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                EqualIcon(EqualIcons.SlideLeft, contentDescription = null, tint = colors.textSecondary, size = 18.dp)
                EqualText(text = "Slide left to cancel", style = type.body1, color = colors.textSecondary, maxLines = 1, modifier = Modifier.weight(1f))
                RecorderWave(state, waveColor, Modifier.clearAndSetSemantics { })
            }
        }
        return
    }
    Row(
        modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        RoundSurfaceButton(EqualIcons.Delete, colors.textDanger, 24.dp, "Delete recording", onDelete)
        Row(
            Modifier
                .weight(1f)
                .then(pill)
                .clearAndSetSemantics { contentDescription = "Recording ${mmss(seconds)}" },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            EqualText(text = mmss(seconds), style = type.body1.tabular(), color = colors.textPrimary, maxLines = 1)
            Spacer(Modifier.weight(1f))
            RecorderWave(state, waveColor)
        }
        VoiceCircleButton(EqualIcons.Navigation, 26.dp, "Send voice note", onSend)
    }
}

// ---------------------------------------------------------------------------------------------
// Conversation
// ---------------------------------------------------------------------------------------------

/**
 * Header once the request becomes a task: 32dp orb → 12 → [title] (what the assistant is doing)
 * `subheading` Regular → neutral close ([onClose]). `bg/surface`, `raised` shadow, padding
 * 16 / 16 / 14.
 */
@Composable
fun AssistantTaskHeader(
    title: String,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = EqualTheme.colors
    Row(
        modifier
            .fillMaxWidth()
            .eqShadow(EqualShadows.raised)
            .background(colors.bgSurface)
            .padding(start = 16.dp, end = 12.dp, top = 12.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        EqualLogoOrb(size = 32.dp, contentDescription = null)
        EqualText(
            text = title,
            style = EqualTheme.type.subheading.copy(fontWeight = FontWeight.Normal),
            color = colors.textPrimary,
            modifier = Modifier.weight(1f),
        )
        EqualIconButton(icon = EqualIcons.Close, contentDescription = "Close", onClick = onClose)
    }
}

/**
 * The assistant working: 28dp orb → 10 → "[label]…" `subheading` italic Regular
 * `text/secondary`. The three dots pulse (1.2s, staggered 0.2s); still under reduced motion.
 */
@Composable
fun AssistantThinking(
    modifier: Modifier = Modifier,
    label: String = "Thinking",
) {
    val colors = EqualTheme.colors
    val style = EqualTheme.type.subheading.copy(fontStyle = FontStyle.Italic, fontWeight = FontWeight.Normal)
    val reduceMotion = rememberReduceMotion()
    Row(
        modifier
            .padding(4.dp)
            .semantics(mergeDescendants = true) {
                liveRegion = LiveRegionMode.Polite
                contentDescription = "$label…"
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        EqualLogoOrb(size = 28.dp, contentDescription = null)
        Row(Modifier.clearAndSetSemantics { }) {
            EqualText(text = label, style = style, color = colors.textSecondary, maxLines = 1)
            if (reduceMotion) {
                EqualText(text = "...", style = style, color = colors.textSecondary, maxLines = 1)
            } else {
                val transition = rememberInfiniteTransition(label = "thinking")
                repeat(3) { i ->
                    val a by transition.animateFloat(
                        initialValue = 0.2f,
                        targetValue = 0.2f,
                        animationSpec = infiniteRepeatable(
                            animation = keyframes {
                                durationMillis = 1200
                                0.2f at 0
                                1f at 360
                                0.2f at 720
                                0.2f at 1200
                            },
                            initialStartOffset = StartOffset(i * 200),
                        ),
                        label = "dot$i",
                    )
                    EqualText(text = ".", style = style, color = colors.textSecondary, maxLines = 1, modifier = Modifier.alpha(a))
                }
            }
        }
    }
}

/**
 * Two reply pills under a result (equal width, gap 12, 48 high): revise ([editLabel], ✍️,
 * `bg/surface` + 1dp `stroke/subtle`) and commit ([confirmLabel], 👍, `gradient/chip-large` +
 * 1dp `action/stroke/unread`). Labels `subheading` Regular `text/primary`.
 */
@Composable
fun AssistantReplies(
    onEdit: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    editLabel: String = "Edit",
    confirmLabel: String = "Confirm",
) {
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        ReplyPill("✍️", editLabel, commit = false, onClick = onEdit, modifier = Modifier.weight(1f))
        ReplyPill("👍", confirmLabel, commit = true, onClick = onConfirm, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun ReplyPill(emoji: String, label: String, commit: Boolean, onClick: () -> Unit, modifier: Modifier) {
    val colors = EqualTheme.colors
    val shape = CircleShape
    val interaction = rememberInteraction()
    val surface = if (commit) {
        Modifier.background(EqualTheme.gradients.chipLarge.toBrush(), shape).border(1.dp, colors.actionStrokeUnread, shape)
    } else {
        Modifier.background(colors.bgSurface, shape).border(1.dp, colors.strokeSubtle, shape)
    }
    Row(
        modifier
            .eqPressFeedback(interaction, shape)
            .height(48.dp)
            .then(surface)
            .clip(shape)
            .clickable(interactionSource = interaction, indication = null, role = Role.Button, onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
    ) {
        EmojiGlyph(emoji = emoji, size = 16.dp, modifier = Modifier.clearAndSetSemantics { })
        EqualText(text = label, style = EqualTheme.type.subheading.copy(fontWeight = FontWeight.Normal), color = colors.textPrimary, maxLines = 1)
    }
}

/**
 * The system keyboard, for docs and previews only: the supplied [EqualAssets.SystemKeyboard]
 * image (390 × 300) at full width, keeping its aspect. Never ship it as UI; the keyboard is the
 * OS's, so in real use lift the input with `imePadding()` instead.
 */
@Composable
fun KeyboardSpace(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = EqualAssets.SystemKeyboard.resId),
        contentDescription = null,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(390f / 300f)
            .clearAndSetSemantics { },
        contentScale = ContentScale.Fit,
    )
}

// ---------------------------------------------------------------------------------------------
// Sheet shell
// ---------------------------------------------------------------------------------------------

/**
 * The assistant sheet: `sheet/bg`, radius 32 top (all round when [inline]), shadow 0 -8 30
 * black/12%. [header] (e.g. [AssistantTaskHeader]) → scrolling body ([content]) → [footer]
 * (padding 12 / 16 / 20) → [keyboard] (full-bleed, e.g. [KeyboardSpace] in docs).
 * [task] makes it the taller conversation sheet: 82% of the screen with a `chat/wallpaper-bg`
 * body and a footer drawn on [ChatWallpaper] (the pattern runs to the bottom edge); the body then has a fixed height and doesn't scroll itself, so put a
 * [ChatWallpaper] with `weight(1f)` and a scrolling message column in [content].
 *
 * [inChat]: opened from a chat's AI layer, the sheet stops below the chat header so the user still
 * sees which chat it belongs to: its height is the parent's minus 112dp (status bar 52 + chat
 * header ~60), taking precedence over [task]'s 82%.
 *
 * As an overlay (default) it fills its parent: `scrim` (tap → [onScrim]) and the panel at the
 * bottom, lifted above the keyboard with `imePadding()`, sliding up over `motion.slow` (none under
 * reduced motion); system Back also calls [onScrim]. [inline] renders only the panel, for docs.
 */
@Composable
fun AssistantSheetShell(
    modifier: Modifier = Modifier,
    task: Boolean = false,
    inChat: Boolean = false,
    inline: Boolean = false,
    onScrim: () -> Unit = {},
    header: (@Composable () -> Unit)? = null,
    footer: (@Composable () -> Unit)? = null,
    keyboard: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = EqualTheme.colors
    val shape = if (inline) RoundedCornerShape(32.dp) else RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    val bodyBg = if (task) colors.chatWallpaperBg else Color.Transparent
    val panel: @Composable (Modifier) -> Unit = { panelModifier ->
        Column(
            panelModifier
                .fillMaxWidth()
                .eqShadow(SheetShadow, shape)
                .clip(shape)
                .background(colors.bgPrimary)
                .semantics { paneTitle = "Your assistant" },
        ) {
            header?.invoke()
            Column(
                Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = task)
                    .background(bodyBg)
                    .then(if (task) Modifier else Modifier.verticalScroll(rememberScrollState())),
                content = content,
            )
            if (footer != null) {
                val footerBox: @Composable () -> Unit = {
                    Box(
                        // No fill: transparent outside a task, the wallpaper shows through in one.
                        Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 20.dp),
                    ) { footer() }
                }
                // Task sheet: the chat wallpaper runs on behind the chips and the input, to the bottom edge.
                if (task) ChatWallpaper(Modifier.fillMaxWidth()) { footerBox() } else footerBox()
            }
            keyboard?.invoke()
        }
    }
    if (inline) {
        panel(modifier)
        return
    }
    EqualBackHandler(onBack = onScrim)
    val reduceMotion = rememberReduceMotion()
    val visible = remember { MutableTransitionState(reduceMotion).apply { targetState = true } }
    BoxWithConstraints(modifier.fillMaxSize()) {
        val sheetHeight = if (inChat) (maxHeight - InChatSheetInset).coerceAtLeast(0.dp) else maxHeight * 0.82f
        Box(
            Modifier
                .matchParentSize()
                .background(colors.bgOverlay)
                .clickable(interactionSource = rememberInteraction(), indication = null, onClickLabel = "Close", onClick = onScrim),
        )
        AnimatedVisibility(
            visibleState = visible,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = slideInVertically(tween(EqualMotion.slow, easing = EqualEasing.standard)) { it } +
                fadeIn(tween(EqualMotion.slow, easing = EqualEasing.standard)),
        ) {
            panel(
                Modifier
                    .imePadding()
                    .then(if (task || inChat) Modifier.height(sheetHeight) else Modifier.heightIn(max = maxHeight * 0.92f))
                    // Swallow taps so they don't reach the scrim.
                    .pointerInput(Unit) { detectTapGestures { } },
            )
        }
    }
}

/** What an [AssistantSheetShell] with `inChat` leaves visible above it: status bar 52 + chat header ~60. */
private val InChatSheetInset = 112.dp

// ---------------------------------------------------------------------------------------------
// Reply card
// ---------------------------------------------------------------------------------------------

/** One action row of an [AssistantReplyCard]: always emoji + label (✏️ Edit, 📤 Send). */
@Immutable
data class ReplyAction(
    val label: String,
    val emoji: String,
    val onClick: () -> Unit = {},
)

/**
 * The assistant's answer as a card in a task conversation (e.g. a drafted WhatsApp reply): max
 * width 300, radius 16, `bg/surface`, `glass` shadow (0 1 2 black/6%). Padding 10 / 12 / 6:
 * [sender] `body2` `text/action` → [text] `body1` Regular (line breaks kept) → [time] `micro`
 * `text/secondary`, right-aligned. Then each [actions] row: full width, min 48 high, 1dp
 * `stroke/subtle` top border, emoji (14dp) → 6 → label `button` in `text/action`.
 * [body] adds rich content under the text (e.g. an [ActionCardBody]); an empty [text] is skipped.
 */
@Composable
fun AssistantReplyCard(
    text: String,
    modifier: Modifier = Modifier,
    time: String? = null,
    actions: List<ReplyAction> = emptyList(),
    sender: String = "Equal AI",
    body: (@Composable ColumnScope.() -> Unit)? = null,
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier
            .widthIn(max = 300.dp)
            .eqShadow(EqualShadows.glass, shape)
            .clip(shape)
            .background(colors.bgSurface),
    ) {
        Column(Modifier.padding(start = 12.dp, end = 12.dp, top = 10.dp, bottom = 6.dp)) {
            EqualText(text = sender, style = type.body2, color = colors.textAction)
            if (text.isNotEmpty()) {
                EqualText(text = text, style = type.body1.copy(fontWeight = FontWeight.Normal), color = colors.textPrimary)
            }
            body?.invoke(this)
            if (time != null) {
                EqualText(
                    text = time,
                    style = type.micro,
                    color = colors.textSecondary,
                    maxLines = 1,
                    modifier = Modifier.align(Alignment.End),
                )
            }
        }
        actions.forEach { action ->
            val interaction = rememberInteraction()
            Box(Modifier.fillMaxWidth().height(1.dp).background(colors.strokeSubtle))
            Row(
                Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .eqPressFeedback(interaction, RoundedCornerShape(0.dp))
                    .clickable(interactionSource = interaction, indication = null, role = Role.Button, onClick = action.onClick),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
            ) {
                EmojiGlyph(emoji = action.emoji, size = 14.dp, modifier = Modifier.clearAndSetSemantics { })
                EqualText(text = action.label, style = type.button, color = colors.textAction, maxLines = 1)
            }
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Carousel dots
// ---------------------------------------------------------------------------------------------

/**
 * Page dots under a promo carousel: 7dp `stroke/medium` dots, gap 8, the [active] one 11dp in
 * `action/stroke/unread`. Padding 14 top / 2 bottom, centred.
 */
@Composable
fun CarouselDots(
    count: Int,
    active: Int,
    modifier: Modifier = Modifier,
) {
    val colors = EqualTheme.colors
    Row(
        modifier
            .fillMaxWidth()
            .padding(top = 14.dp, bottom = 2.dp)
            .clearAndSetSemantics { contentDescription = "Card ${active + 1} of $count" },
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(count) { i ->
            val on = i == active
            Box(
                Modifier
                    .size(if (on) 11.dp else 7.dp)
                    .background(if (on) colors.actionStrokeUnread else colors.strokeMedium, CircleShape),
            )
        }
    }
}

/** The sheet's suggestion chips row: horizontally scrolling, gap 12, padding 20 / 16 / 4. */
@Composable
fun AssistantChips(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Row(
        modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) { content() }
}

// ---------------------------------------------------------------------------------------------
// Previews
// ---------------------------------------------------------------------------------------------

private val PreviewActions = listOf(
    AssistantAction("Talk to Equal"),
    AssistantAction("Set Instruction", EqualIcons.AutoFix),
    AssistantAction("Make a Call", EqualIcons.CallFilled),
)

@EqualPreviews
@Composable
private fun AssistantGreetingPreview() {
    PreviewFrame(padding = 0.dp) {
        AssistantSheetShell(inline = true, footer = { AssistantInput(mode = AssistantInputMode.Text) }) {
            AssistantGreeting(name = "Keshav")
            AssistantActions(items = PreviewActions)
            AssistantChips {
                LargeActionChip(label = "Reply Amit on WhatsApp", emoji = "🗓️", onClick = {})
                LargeActionChip(label = "Set reminder", emoji = "🔔", onClick = {})
            }
        }
    }
}

@EqualPreviews
@Composable
private fun AssistantInputPreview() {
    PreviewFrame {
        Column(Modifier.background(EqualTheme.colors.bgPrimary).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            AssistantInput(mode = AssistantInputMode.Text)
            AssistantInput(mode = AssistantInputMode.Text, value = "Set a remainder for appointment", focused = true)
            AssistantInput(mode = AssistantInputMode.Voice)
        }
    }
}

@EqualPreviews
@Composable
private fun VoiceRecorderPreview() {
    PreviewFrame {
        Column(Modifier.background(EqualTheme.colors.bgPrimary).padding(16.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
            VoiceRecorder(state = RecorderState.Idle)
            VoiceRecorder(state = RecorderState.Recording, seconds = 4)
            VoiceRecorder(state = RecorderState.Hold)
            VoiceRecorder(state = RecorderState.Cancel)
        }
    }
}

@EqualPreviews
@Composable
private fun AssistantTaskPreview() {
    PreviewFrame(padding = 0.dp) {
        Column(Modifier.background(EqualTheme.colors.chatWallpaperBg)) {
            AssistantTaskHeader(title = "Setting up a reminder for appointment at Dr. Mehta’s", onClose = {})
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                AssistantThinking()
                AssistantReplies(onEdit = {}, onConfirm = {})
            }
            KeyboardSpace()
        }
        CarouselDots(count = 3, active = 0)
    }
}
