// Compose code shown on the docs site (Flows → AI personalisation, Chat list actions; Components →
// Settings & promos). Each function body is extracted by scripts/build-snippets.mjs.
@file:Suppress("unused")

package ai.equal.ds.snippets

import ai.equal.ds.catalog.CallerContextScreen
import ai.equal.ds.catalog.CallerContextSheet
import ai.equal.ds.catalog.ChatListActionsSample
import ai.equal.ds.catalog.ChatListActionsStage
import ai.equal.ds.catalog.PersonalisationFlowSample
import ai.equal.ds.catalog.PersonalisationFyiHomeSample
import ai.equal.ds.catalog.PersonalisationScreen
import ai.equal.ds.catalog.YouNoticed
import ai.equal.ds.catalog.YouScreenSample
import ai.equal.ds.components.CallerContextState
import ai.equal.ds.components.FeatureBullet
import ai.equal.ds.components.FeatureIntroSheet
import ai.equal.ds.components.InstructionsPromoCard
import ai.equal.ds.components.LearningCard
import ai.equal.ds.components.LearningCards
import ai.equal.ds.components.TrustStats
import ai.equal.ds.components.TrustStrip
import ai.equal.ds.icons.EqualSymbols
import ai.equal.ds.theme.EqualTheme
import androidx.compose.runtime.Composable

// snippet: flow-personalisation / fyi
@Composable
internal fun PersFyiSnippet() {
    EqualTheme {
        PersonalisationFyiHomeSample()
    }
}

// snippet: flow-personalisation / you-coach
@Composable
internal fun PersYouCoachSnippet() {
    EqualTheme {
        YouScreenSample(noticed = YouNoticed.Coach)
    }
}

// snippet: flow-personalisation / you-reveal
@Composable
internal fun PersYouRevealSnippet() {
    EqualTheme {
        YouScreenSample(noticed = YouNoticed.Reveal)
    }
}

// snippet: flow-personalisation / you-ready
@Composable
internal fun PersYouReadySnippet() {
    EqualTheme {
        YouScreenSample(noticed = YouNoticed.Ready)
    }
}

// snippet: flow-personalisation / you-multiple
@Composable
internal fun PersYouMultipleSnippet() {
    EqualTheme {
        YouScreenSample(noticed = YouNoticed.Multiple)
    }
}

// snippet: flow-personalisation / screen
@Composable
internal fun PersScreenSnippet() {
    EqualTheme {
        PersonalisationScreen()
    }
}

// snippet: flow-personalisation / screen-delete
@Composable
internal fun PersScreenDeleteSnippet() {
    EqualTheme {
        PersonalisationScreen(sheet = true)
    }
}

// snippet: flow-personalisation / caller-empty
@Composable
internal fun PersCallerEmptySnippet() {
    EqualTheme {
        CallerContextScreen(state = CallerContextState.Empty)
    }
}

// snippet: flow-personalisation / caller-learning
@Composable
internal fun PersCallerLearningSnippet() {
    EqualTheme {
        CallerContextScreen(state = CallerContextState.Learning)
    }
}

// snippet: flow-personalisation / caller-filled
@Composable
internal fun PersCallerFilledSnippet() {
    EqualTheme {
        CallerContextScreen(state = CallerContextState.Filled)
    }
}

// snippet: flow-personalisation / caller-paused
@Composable
internal fun PersCallerPausedSnippet() {
    EqualTheme {
        CallerContextScreen(state = CallerContextState.Paused)
    }
}

// snippet: flow-personalisation / caller-stop
@Composable
internal fun PersCallerStopSnippet() {
    EqualTheme {
        CallerContextScreen(sheet = CallerContextSheet.Stop)
    }
}

// snippet: flow-personalisation / caller-delete
@Composable
internal fun PersCallerDeleteSnippet() {
    EqualTheme {
        CallerContextScreen(sheet = CallerContextSheet.Delete)
    }
}

// snippet: flow-personalisation / live
@Composable
internal fun PersLiveSnippet() {
    EqualTheme {
        PersonalisationFlowSample()
    }
}

// snippet: flow-chat-list-actions / one
@Composable
internal fun ListActionsOneSnippet() {
    EqualTheme {
        ChatListActionsSample(stage = ChatListActionsStage.One)
    }
}

// snippet: flow-chat-list-actions / many
@Composable
internal fun ListActionsManySnippet() {
    EqualTheme {
        ChatListActionsSample(stage = ChatListActionsStage.Many)
    }
}

// snippet: flow-chat-list-actions / menu
@Composable
internal fun ListActionsMenuSnippet() {
    EqualTheme {
        ChatListActionsSample(stage = ChatListActionsStage.One)
    }
}

// snippet: flow-chat-list-actions / delete
@Composable
internal fun ListActionsDeleteSnippet() {
    EqualTheme {
        ChatListActionsSample(stage = ChatListActionsStage.Delete)
    }
}

// snippet: flow-chat-list-actions / deleting
@Composable
internal fun ListActionsDeletingSnippet() {
    EqualTheme {
        ChatListActionsSample(stage = ChatListActionsStage.Deleting)
    }
}

// snippet: flow-chat-list-actions / mute
@Composable
internal fun ListActionsMuteSnippet() {
    EqualTheme {
        ChatListActionsSample(stage = ChatListActionsStage.Mute)
    }
}

// snippet: flow-chat-list-actions / unmute
@Composable
internal fun ListActionsUnmuteSnippet() {
    EqualTheme {
        ChatListActionsSample(stage = ChatListActionsStage.Unmute)
    }
}

// snippet: flow-chat-list-actions / muted
@Composable
internal fun ListActionsMutedSnippet() {
    EqualTheme {
        ChatListActionsSample(stage = ChatListActionsStage.Muted)
    }
}

// snippet: flow-chat-list-actions / favourite
@Composable
internal fun ListActionsFavouriteSnippet() {
    EqualTheme {
        ChatListActionsSample(stage = ChatListActionsStage.Favourite)
    }
}

// snippet: flow-chat-list-actions / live
@Composable
internal fun ListActionsLiveSnippet() {
    EqualTheme {
        ChatListActionsSample()
    }
}

// snippet: settings-promos / Call Learning cards
@Composable
internal fun SettingsLearningCardsSnippet() {
    LearningCards.forEach { LearningCard(it) }
}

// snippet: settings-promos / Trust strip
@Composable
internal fun SettingsTrustStripSnippet() {
    TrustStrip(TrustStats)
}

// snippet: settings-promos / Teach Your Assistant
@Composable
internal fun SettingsInstrPromoSnippet() {
    InstructionsPromoCard(onCta = { /* open the sheet */ }, onDismiss = { /* hide */ })
}

// snippet: settings-promos / Feature intro sheet
@Composable
internal fun SettingsFeatureIntroSnippet() {
    FeatureIntroSheet(
        title = "Get every call handled your way with Instructions",
        bullets = listOf(FeatureBullet(EqualSymbols.edit, "Tell your assistant what to do on any call, in your own words")),
        cta = "Try Instructions",
        hero = { /* InstructionCard */ },
        secondary = "Not now",
        inline = true,
    )
}
