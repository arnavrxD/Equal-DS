// Compose code shown on the docs site (Flows → Call your assistant). Each function body is
// extracted by scripts/build-snippets.mjs; keep the `// snippet: <doc id> / <key>` line above it.
@file:Suppress("unused")

package ai.equal.ds.snippets

import ai.equal.ds.catalog.VoipFlowSample
import ai.equal.ds.catalog.VoipStage
import ai.equal.ds.theme.EqualTheme
import androidx.compose.runtime.Composable

// snippet: flow-voip / home
@Composable
internal fun FlowVoipHomeSnippet() {
    EqualTheme {
        VoipFlowSample()
    }
}

// snippet: flow-voip / mic-prompt
@Composable
internal fun FlowVoipMicPromptSnippet() {
    EqualTheme {
        VoipFlowSample(stage = VoipStage.MicPrompt)
    }
}

// snippet: flow-voip / mic-system
@Composable
internal fun FlowVoipMicSystemSnippet() {
    EqualTheme {
        VoipFlowSample(stage = VoipStage.MicSystem)
    }
}

// snippet: flow-voip / connecting
@Composable
internal fun FlowVoipConnectingSnippet() {
    EqualTheme {
        VoipFlowSample(stage = VoipStage.Connecting)
    }
}

// snippet: flow-voip / connected
@Composable
internal fun FlowVoipConnectedSnippet() {
    EqualTheme {
        VoipFlowSample(stage = VoipStage.Connected, seconds = 1)
    }
}

// snippet: flow-voip / speaker
@Composable
internal fun FlowVoipSpeakerSnippet() {
    EqualTheme {
        VoipFlowSample(stage = VoipStage.Connected, speaker = true, seconds = 1)
    }
}

// snippet: flow-voip / muted
@Composable
internal fun FlowVoipMutedSnippet() {
    EqualTheme {
        VoipFlowSample(stage = VoipStage.Connected, muted = true, seconds = 5)
    }
}

// snippet: flow-voip / ended
@Composable
internal fun FlowVoipEndedSnippet() {
    EqualTheme {
        VoipFlowSample(stage = VoipStage.Ended, seconds = 155)
    }
}

// snippet: flow-voip / pstn
@Composable
internal fun FlowVoipPstnSnippet() {
    EqualTheme {
        VoipFlowSample(stage = VoipStage.Pstn)
    }
}
