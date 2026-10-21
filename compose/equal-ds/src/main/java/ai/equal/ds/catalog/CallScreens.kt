package ai.equal.ds.catalog

import ai.equal.ds.assets.EqualAssets
import ai.equal.ds.components.CallPhase
import ai.equal.ds.components.CallScreen
import ai.equal.ds.components.MeetAssistantCard
import ai.equal.ds.components.MeetAssistantLayout
import ai.equal.ds.components.PermissionDialog
import ai.equal.ds.components.SystemNotificationPreview
import ai.equal.ds.components.SystemPermissionStandIn
import ai.equal.ds.components.rememberCallTimer
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/** Stages of the vOIP "Call me" flow. */
enum class VoipStage { Home, MicPrompt, MicSystem, Connecting, Connected, Ended, Pstn }

/**
 * "Call me" with the assistant over VoIP (vOIP design). Home → mic pre-permission → Android dialog
 * → Connecting → Connected (speaker / mute toggle independently) → Call ended → Home. "Not now"
 * or "Deny" falls back to a normal phone call from the assistant. Port of web `VoipFlow`.
 */
@Composable
fun VoipFlowSample(
    modifier: Modifier = Modifier,
    stage: VoipStage = VoipStage.Home,
    speaker: Boolean = false,
    muted: Boolean = false,
    seconds: Int = 0,
    micGranted: Boolean = false,
) {
    var current by remember(stage) { mutableStateOf(stage) }
    var sp by remember(speaker) { mutableStateOf(speaker) }
    var mu by remember(muted) { mutableStateOf(muted) }
    val elapsed = rememberCallTimer(current == VoipStage.Connected, seconds)
    LaunchedEffect(current) {
        if (current == VoipStage.Connecting && stage != VoipStage.Connecting) { delay(1500); current = VoipStage.Connected }
        if (current == VoipStage.Ended && stage != VoipStage.Ended) { delay(2000); current = VoipStage.Home }
    }
    if (current == VoipStage.Home || current == VoipStage.Pstn) {
        Box(modifier.fillMaxSize()) {
            HomeScreenSample(promo = { MeetAssistantCard(onCallClick = { current = if (micGranted) VoipStage.Connecting else VoipStage.MicPrompt }, layout = MeetAssistantLayout.Banner) })
            if (current == VoipStage.Pstn) {
                SystemNotificationPreview(
                    title = "Incoming call · Your Assistant",
                    body = "Equal AI Assistant is calling you on your phone.",
                    app = "Phone",
                    modifier = Modifier.align(Alignment.TopCenter).padding(8.dp).fillMaxWidth(),
                )
            }
        }
        return
    }
    val phase = when (current) { VoipStage.Connected -> CallPhase.Connected; VoipStage.Ended -> CallPhase.Ended; else -> CallPhase.Connecting }
    CallScreen(
        phase = phase,
        modifier = modifier,
        seconds = if (current == VoipStage.Ended) maxOf(elapsed, seconds) else elapsed,
        speaker = sp,
        muted = mu,
        onSpeaker = { sp = !sp },
        onMute = { mu = !mu },
        onEnd = { current = VoipStage.Ended },
        onBack = { current = VoipStage.Home },
        overlay = when (current) {
            VoipStage.MicPrompt -> { {
                PermissionDialog(
                    title = "Talk to your assistant",
                    body = "Turn on your mic to talk, just like a normal call.",
                    cta = "Allow microphone",
                    art = EqualAssets.PermissionMicrophone,
                    onAction = { current = VoipStage.MicSystem },
                    secondary = "Not now",
                    onSecondary = { current = VoipStage.Pstn },
                    closable = false,
                )
            } }
            VoipStage.MicSystem -> { { SystemPermissionStandIn(onAllow = { current = VoipStage.Connecting }, onDeny = { current = VoipStage.Pstn }) } }
            else -> null
        },
    )
}
