// Compose code shown on the docs site (Flows → Home, You and settings updates).
// Each function body is extracted by scripts/build-snippets.mjs; keep the `// snippet:` line above it.
@file:Suppress("unused")

package ai.equal.ds.snippets

import ai.equal.ds.catalog.AccountDataFlowSample
import ai.equal.ds.catalog.AccountDataScreen
import ai.equal.ds.catalog.AssistantControl
import ai.equal.ds.catalog.CallLearningScreen
import ai.equal.ds.catalog.HomeScreenSample
import ai.equal.ds.catalog.InstructionsPromoHomeSample
import ai.equal.ds.catalog.RowStateChats
import ai.equal.ds.catalog.SplashScreenSample
import ai.equal.ds.catalog.YouNotice
import ai.equal.ds.catalog.YouScreenSample
import ai.equal.ds.theme.EqualTheme
import androidx.compose.runtime.Composable

// snippet: flow-home-you-updates / home-empty
@Composable
internal fun HomeEmptySnippet() {
    EqualTheme {
        HomeScreenSample(chats = emptyList())
    }
}

// snippet: flow-home-you-updates / home-offline
@Composable
internal fun HomeOfflineSnippet() {
    EqualTheme {
        HomeScreenSample(offline = true)
    }
}

// snippet: flow-home-you-updates / home-rows
@Composable
internal fun HomeRowsSnippet() {
    EqualTheme {
        HomeScreenSample(chats = RowStateChats)
    }
}

// snippet: flow-home-you-updates / home-resting
@Composable
internal fun HomeRestingSnippet() {
    EqualTheme {
        HomeScreenSample(
            assistantControl = AssistantControl(on = false, offDescription = "Assistant is resting."),
        )
    }
}

// snippet: flow-home-you-updates / you-notice
@Composable
internal fun YouNoticeSnippet() {
    EqualTheme {
        YouScreenSample(notice = YouNotice.Card)
    }
}

// snippet: flow-home-you-updates / you-notice-line
@Composable
internal fun YouNoticeLineSnippet() {
    EqualTheme {
        YouScreenSample(notice = YouNotice.Line)
    }
}

// snippet: flow-home-you-updates / splash
@Composable
internal fun SplashSnippet() {
    EqualTheme {
        SplashScreenSample(tagline = "Your personal AI call assistant")
    }
}

// snippet: flow-home-you-updates / call-learning
@Composable
internal fun CallLearningSnippet() {
    EqualTheme {
        CallLearningScreen(onBack = {})
    }
}

// snippet: flow-home-you-updates / call-learning-off
@Composable
internal fun CallLearningOffSnippet() {
    EqualTheme {
        CallLearningScreen(sheet = true, onBack = {})
    }
}

// snippet: flow-home-you-updates / account-data
@Composable
internal fun AccountDataSnippet() {
    EqualTheme {
        AccountDataScreen(onBack = {}, onCallLearning = {})
    }
}

// snippet: flow-home-you-updates / account-data-live
@Composable
internal fun AccountDataLiveSnippet() {
    EqualTheme {
        AccountDataFlowSample()
    }
}

// snippet: flow-home-you-updates / instructions-promo
@Composable
internal fun InstructionsPromoSnippet() {
    EqualTheme {
        HomeScreenSample(
            instructionsPromo = true,
            onPromo = { /* open the Instructions sheet */ },
        )
    }
}

// snippet: flow-home-you-updates / instructions-sheet
@Composable
internal fun InstructionsSheetSnippet() {
    EqualTheme {
        InstructionsPromoHomeSample(sheet = true)
    }
}
