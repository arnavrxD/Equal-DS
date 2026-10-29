package ai.equal.ds.components

import ai.equal.ds.icons.DefaultWaveformSamples
import ai.equal.ds.icons.EqualIcon
import ai.equal.ds.icons.EqualIcons
import ai.equal.ds.icons.EqualWaveform
import ai.equal.ds.theme.EqualTheme
import ai.equal.ds.theme.tabular
import ai.equal.ds.tokens.EqualRadius
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.indication
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import androidx.compose.runtime.Immutable

/** Playback speeds; tapping the pill cycles through them (SPEC §11). */
enum class PlaybackSpeed(val label: String) {
    X1("1x"),
    X1_2("1.2x"),
    X1_5("1.5x"),
    X2("2x"),
    ;

    fun next(): PlaybackSpeed = entries[(ordinal + 1) % entries.size]
}

/** [VoiceNotePlayer] surface. */
enum class VoicePlayerTone {
    /** `voice-player/bg`: a voice note in the thread (call recording). */
    Neutral,

    /** `voice/player-bg-sent` with the speed pill on `bg/surface` + `text/primary`: inside your own outgoing bubble. */
    Sent,
}

/**
 * Voice note player box (SPEC §11). Radius 12, filled `voice-player/bg` ([tone] Sent:
 * `voice/player-bg-sent`, speed pill `bg/surface` / `text/primary`), padding 10. The neutral player has a
 * 1dp `voice/player-stroke` hairline that turns `button/primary` while playing; Sent and a custom
 * [background] have none.
 * Play/pause 22 `text/secondary` → 8 → waveform (fills) → 8 → speed pill (22 high, h8,
 * `playback/pill-bg`, `caption` `text/on-dark`, tabular; tap cycles). Elapsed and total `micro`
 * `text/secondary` sit under the two ends of the waveform (gap 4).
 *
 * The play button and pill are smaller than 48dp visually; each has a 48dp touch and TalkBack
 * area ([touchTarget]) that spills past it without changing the layout. [background] replaces the fill (the gold wash of a
 * [LockedRecording]).
 */
@Composable
fun VoiceNotePlayer(
    isPlaying: Boolean,
    onPlayPauseClick: () -> Unit,
    progress: Float,
    elapsed: String,
    total: String,
    speed: PlaybackSpeed,
    onSpeedChange: (PlaybackSpeed) -> Unit,
    modifier: Modifier = Modifier,
    samples: List<Float> = DefaultWaveformSamples,
    tone: VoicePlayerTone = VoicePlayerTone.Neutral,
    background: Brush? = null,
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    val shape = RoundedCornerShape(EqualRadius.rMd)
    val sent = tone == VoicePlayerTone.Sent
    Row(
        modifier
            .fillMaxWidth()
            .then(
                if (background != null) {
                    Modifier.background(background, shape)
                } else {
                    Modifier.background(if (sent) colors.voicePlayerBgSent else colors.voicePlayerBg, shape)
                },
            )
            .then(
                // Neutral player: 1dp `voice/player-stroke` hairline, brand green while playing.
                if (background == null && !sent) {
                    Modifier.border(1.dp, if (isPlaying) colors.buttonPrimary else colors.voicePlayerStroke, shape)
                } else {
                    Modifier
                },
            )
            .padding(10.dp),
        verticalAlignment = Alignment.Top,
    ) {
        val playInteraction = rememberInteraction()
        Box(
            Modifier
                .touchTarget()
                .clickable(interactionSource = playInteraction, indication = null, role = Role.Button, onClick = onPlayPauseClick),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .indication(playInteraction, LocalIndication.current),
                contentAlignment = Alignment.Center,
            ) {
                EqualIcon(
                    imageVector = if (isPlaying) EqualIcons.Pause else EqualIcons.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = colors.textSecondary,
                    size = 22.dp,
                )
            }
        }
        Spacer(Modifier.width(8.dp))
        Column(Modifier.weight(1f)) {
            EqualWaveform(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(22.dp)
                    .semantics {
                        contentDescription = "Playback"
                        progressBarRangeInfo = ProgressBarRangeInfo(progress.coerceIn(0f, 1f), 0f..1f)
                        stateDescription = "$elapsed of $total"
                    },
                samples = samples,
                progress = progress,
            )
            Spacer(Modifier.height(4.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                EqualText(text = elapsed, style = type.micro, color = colors.textSecondary, maxLines = 1)
                EqualText(text = total, style = type.micro, color = colors.textSecondary, maxLines = 1)
            }
        }
        Spacer(Modifier.width(8.dp))
        val speedInteraction = rememberInteraction()
        Box(
            Modifier
                .touchTarget()
                .clickable(
                    interactionSource = speedInteraction,
                    indication = null,
                    role = Role.Button,
                    onClickLabel = "Change speed",
                ) { onSpeedChange(speed.next()) },
            contentAlignment = Alignment.Center,
        ) {
            Box(
                Modifier
                    .height(22.dp)
                    .clip(CircleShape)
                    .background(if (sent) colors.bgSurface else colors.playbackPillBg)
                    .indication(speedInteraction, LocalIndication.current)
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                EqualText(text = speed.label, style = type.caption.tabular(), color = if (sent) colors.textPrimary else colors.textOnDark, maxLines = 1)
            }
        }
    }
}

/**
 * Voice note + call summary bubble (SPEC §11). Width 268, `bg/surface`, radius 16.
 * [voiceNote] (usually a [VoiceNotePlayer]) sits with an 8dp margin; then h8: "Call Summary"
 * `caption` `text/secondary` → summary `body1` → the optional [tag] line (8 above, padding 4: emoji 12
 * + 12 Regular `text/secondary`, e.g. 🪄 2 instructions applied in this call) → the timestamp `micro`
 * at the right.
 * With [onShareClick] a footer appears: 1dp divider + 48-high centred "Share"
 * (`button` `text/action`, share icon 18).
 *
 * [locked] (monetization): [CallSummaryLock.Audio] blurs the recording (3dp) under an
 * [UnlockAudioPill] ([onUnlock]; a tap anywhere on the blurred player also calls it);
 * [CallSummaryLock.All] also wraps the summary text in [LockedContent]. Blur needs API 31+.
 * [actions] is drawn at the bottom of the bubble, e.g. [SummaryActions] (Call Back / Add to
 * Calendar), above the Share footer. [width] defaults to 268; [Dp.Unspecified] fills the width.
 *
 * Slots (web `recording` / `footer`): [recording] replaces the recording player outright (no lock
 * treatment), e.g. the gold crown-locked [LockedRecording] on the free plan; [footer] is drawn
 * under the summary, e.g. [SummaryActions] rows (same place as [actions]; use one of them).
 */
@Composable
fun CallSummaryBubble(
    summary: String,
    timestamp: String?,
    modifier: Modifier = Modifier,
    title: String = "Call Summary",
    voiceNote: (@Composable () -> Unit)? = null,
    onShareClick: (() -> Unit)? = null,
    shareLabel: String = "Share",
    tag: CallSummaryTag? = null,
    locked: CallSummaryLock? = null,
    onUnlock: () -> Unit = {},
    actions: (@Composable () -> Unit)? = null,
    width: Dp = 268.dp,
    recording: (@Composable () -> Unit)? = null,
    footer: (@Composable () -> Unit)? = null,
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    val shape = RoundedCornerShape(EqualRadius.rLg)
    Column(
        modifier
            .then(if (width.isSpecified) Modifier.width(width) else Modifier.fillMaxWidth())
            .clip(shape)
            .background(colors.bgSurface),
    ) {
        if (recording != null) {
            Box(Modifier.padding(8.dp)) { recording() }
        } else if (voiceNote != null && locked != null) {
            Box(Modifier.padding(8.dp), contentAlignment = Alignment.Center) {
                Box(
                    Modifier
                        .blur(3.dp, BlurredEdgeTreatment.Unbounded)
                        .clearAndSetSemantics {},
                ) { voiceNote() }
                Box(
                    Modifier
                        .matchParentSize()
                        .clickable(interactionSource = rememberInteraction(), indication = null, onClick = onUnlock),
                )
                UnlockAudioPill(onClick = onUnlock)
            }
        } else if (voiceNote != null) {
            Box(Modifier.padding(8.dp)) { voiceNote() }
        } else {
            Spacer(Modifier.height(8.dp))
        }
        Column(
            Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
        ) {
            EqualText(text = title, style = type.caption, color = colors.textSecondary, maxLines = 1)
            if (locked == CallSummaryLock.All) {
                LockedContent { EqualText(text = summary, style = type.body1, color = colors.textPrimary) }
            } else {
                EqualText(text = summary, style = type.body1, color = colors.textPrimary)
            }
            if (tag != null) {
                // Which instructions ran in this call: a plain line under the summary (8 above, padding 4).
                Row(
                    Modifier
                        .padding(top = 8.dp)
                        .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (tag.emoji != null) {
                        EmojiGlyph(emoji = tag.emoji, size = 12.dp)
                        Spacer(Modifier.width(4.dp))
                    }
                    EqualText(text = tag.label, style = type.captionRegular, color = colors.textSecondary, maxLines = 1)
                }
            }
            if (timestamp != null) {
                Row(Modifier.fillMaxWidth().padding(top = if (tag != null) 0.dp else 6.dp), horizontalArrangement = Arrangement.End) {
                    EqualText(text = timestamp, style = type.micro, color = colors.textSecondary, maxLines = 1)
                }
            }
        }
        actions?.invoke()
        footer?.invoke()
        if (onShareClick != null) {
            EqualDivider()
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clickable(role = Role.Button, onClick = onShareClick),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                EqualIcon(EqualIcons.Share, contentDescription = null, tint = colors.textAction, size = 18.dp)
                Spacer(Modifier.width(6.dp))
                EqualText(text = shareLabel, style = type.button, color = colors.textAction, maxLines = 1)
            }
        }
    }
}

/**
 * The call summary as Equal's message in a thread (web `CallSummaryMessage`): a 28dp avatar column
 * holding the 24dp [AiAvatar] when [first] (empty otherwise, for a follow-up right after another
 * Equal message) → 8 → [CallSummaryBubble] with every other parameter passed through. A left-side
 * message in a [MessageList].
 */
@Composable
fun CallSummaryMessage(
    summary: String,
    timestamp: String?,
    modifier: Modifier = Modifier,
    first: Boolean = true,
    title: String = "Call Summary",
    voiceNote: (@Composable () -> Unit)? = null,
    onShareClick: (() -> Unit)? = null,
    shareLabel: String = "Share",
    tag: CallSummaryTag? = null,
    locked: CallSummaryLock? = null,
    onUnlock: () -> Unit = {},
    actions: (@Composable () -> Unit)? = null,
    width: Dp = 268.dp,
    recording: (@Composable () -> Unit)? = null,
    footer: (@Composable () -> Unit)? = null,
) {
    Row(modifier.messageSide(MessageSide.Left).fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Box(Modifier.width(28.dp), contentAlignment = Alignment.TopCenter) {
            if (first) AiAvatar(size = AiAvatarSize.Small, contentDescription = null)
        }
        Spacer(Modifier.width(8.dp))
        CallSummaryBubble(
            summary = summary,
            timestamp = timestamp,
            modifier = Modifier.weight(1f, fill = !width.isSpecified),
            title = title,
            voiceNote = voiceNote,
            onShareClick = onShareClick,
            shareLabel = shareLabel,
            tag = tag,
            locked = locked,
            onUnlock = onUnlock,
            actions = actions,
            width = width,
            recording = recording,
            footer = footer,
        )
    }
}

/** What a locked [CallSummaryBubble] hides: only the recording, or the recording and the summary. */
enum class CallSummaryLock { Audio, All }

/** Pill shown left of the call summary timestamp. */
@Immutable
data class CallSummaryTag(val label: String, val emoji: String? = null)

@EqualPreviews
@Composable
private fun CallSummaryPreview() {
    PreviewFrame {
        var playing by remember { mutableStateOf(false) }
        var speed by remember { mutableStateOf(PlaybackSpeed.X1) }
        CallSummaryBubble(
            summary = "Dr. Mehta Clinic called to confirm your appointment tomorrow at 4 PM.",
            timestamp = "03:36",
            voiceNote = {
                VoiceNotePlayer(
                    isPlaying = playing,
                    onPlayPauseClick = { playing = !playing },
                    progress = 0.4f,
                    elapsed = "0:12",
                    total = "0:30",
                    speed = speed,
                    onSpeedChange = { speed = it },
                )
            },
            onShareClick = {},
            tag = CallSummaryTag("2 instructions applied in this call", "🪄"),
        )
    }
}
