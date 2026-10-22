// Compose code shown on the docs site for the paywall experiments: the component page
// ("monetization-experiments / <example title>") and the two flow pages
// ("flow-monetization-touchpoints / <state key>", "flow-paywall-sheet / <state key>").
// Extracted by scripts/build-snippets.mjs; debug source set only: compiled with the module, never
// shipped in the release AAR.
@file:Suppress("unused")

package ai.equal.ds.snippets

import ai.equal.ds.catalog.ChatLock
import ai.equal.ds.catalog.ChatScreenSample
import ai.equal.ds.catalog.HomeNotice
import ai.equal.ds.catalog.HomeScreenSample
import ai.equal.ds.catalog.OfficialNotice
import ai.equal.ds.catalog.PaymentResultScreen
import ai.equal.ds.catalog.PaywallScreen
import ai.equal.ds.catalog.PaywallSheetStage
import ai.equal.ds.catalog.PaywallSubtitle
import ai.equal.ds.catalog.PaywallTouchpoint
import ai.equal.ds.components.AssistantNoticeCard
import ai.equal.ds.components.AssistantToggleArt
import ai.equal.ds.components.EmojiGlyph
import ai.equal.ds.components.ExitOfferDialog
import ai.equal.ds.components.HomeNoticeCard
import ai.equal.ds.components.LockedRecording
import ai.equal.ds.components.MeetAssistantCard
import ai.equal.ds.catalog.MeetAssistantCarousel
import ai.equal.ds.components.OfferStrip
import ai.equal.ds.components.PaymentResult
import ai.equal.ds.components.PaymentResultStatus
import ai.equal.ds.components.PaywallSheet
import ai.equal.ds.components.PendingAction
import ai.equal.ds.components.PlanTier
import ai.equal.ds.components.ProcessingStrip
import ai.equal.ds.components.ProcessingStripVariant
import ai.equal.ds.components.SmallActionChip
import ai.equal.ds.components.SummaryAction
import ai.equal.ds.components.SummaryActions
import ai.equal.ds.components.UsageCapCard
import ai.equal.ds.icons.EqualIcons
import ai.equal.ds.theme.EqualTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// ============================================================ Component page

// snippet: monetization-experiments / Payment results
@Composable
internal fun PaymentResultsSnippet() {
    Row(Modifier.height(440.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        PaymentResult(status = PaymentResultStatus.Success, activeTill = "10th Sept 2026", modifier = Modifier.weight(1f))
        // pendingAction = PendingAction.Support → Contact Support
        PaymentResult(status = PaymentResultStatus.Pending, onClose = {}, onRefresh = {}, modifier = Modifier.weight(1f))
        PaymentResult(status = PaymentResultStatus.Failed, onBack = {}, onRetry = {}, onContactSupport = {}, modifier = Modifier.weight(1f))
    }
}

// snippet: monetization-experiments / Payment processing
@Composable
internal fun PaymentProcessingSnippet() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        ProcessingStrip() // under a chat header
        ProcessingStrip(variant = ProcessingStripVariant.Card) // under the home toggle
        ProcessingStrip(variant = ProcessingStripVariant.Toast, modifier = Modifier.padding(horizontal = 12.dp)) // above the disabled paywall CTA
    }
}

// snippet: monetization-experiments / Sheet paywall
@Composable
internal fun SheetPaywallSnippet() {
    var applied by remember { mutableStateOf(false) }
    // Over a screen: draw it last in a full-size Box (onScrim → the exit offer). Docs: inline = true.
    PaywallSheet(
        applied = applied,
        onApply = { applied = true },
        onSupport = { /* HelpFaqSheet */ },
        onScrim = { /* ExitOfferDialog */ },
        onUnlock = { /* pay */ },
        inline = true,
    )
}

// snippet: monetization-experiments / Exit offer and offer strip
@Composable
internal fun ExitOfferSnippet() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // In a chat: dismissLabel = "Leave", claimLabel = "Keep 25% Off"
        ExitOfferDialog(onDismiss = {}, onClaim = {}, inline = true, modifier = Modifier.padding(horizontal = 16.dp))
        OfferStrip(onClick = { /* reopen the sheet with EQUAL25 applied */ })
    }
}

// snippet: monetization-experiments / Home cards
@Composable
internal fun HomeCardsSnippet() {
    Column {
        HomeNoticeCard(
            lead = { EmojiGlyph(emoji = "👑", size = 14.dp) },
            title = "Your Plan Expired",
            subtitle = "Renew to get Unlimited access back",
            cta = "Renew Now",
            onCta = { /* renew */ },
        )
        UsageCapCard(left = 7, limit = 30, resets = "Mon", onUpgrade = { /* open the paywall */ })
    }
}

// snippet: monetization-experiments / Equal AI notice
@Composable
internal fun EqualAiNoticeSnippet() {
    AssistantNoticeCard(
        title = "Your Plan Expired",
        body = "Renew to unlock unlimited recordings, call summaries, transcripts, and unlimited call screening.",
        cta = "Renew Equal AI Unlimited",
        onCta = { /* open the paywall */ },
    )
}

// snippet: monetization-experiments / Locked recording and actions
@Composable
internal fun LockedRecordingSnippet() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        LockedRecording(onUnlock = { /* open the paywall */ })
        SummaryActions(
            actions = listOf(SummaryAction("Call Back", EqualIcons.CallFilled), SummaryAction("Add to Calendar", EqualIcons.CalendarMonth)),
            locked = true,
            onLocked = { /* open the paywall */ },
        )
        // ChatListItem(lockedActions = true) draws its chips like this
        SmallActionChip(label = "Call Back", emoji = "📞", onClick = {}, locked = true)
    }
}

// ============================================================ Flow: Monetization touchpoints

// snippet: flow-monetization-touchpoints / expired-home
@Composable
internal fun TouchExpiredHomeSnippet() {
    EqualTheme {
        HomeScreenSample(tier = PlanTier.Free, planExpired = true, official = OfficialNotice.PlanExpired)
    }
}

// snippet: flow-monetization-touchpoints / expired-chat
@Composable
internal fun TouchExpiredChatSnippet() {
    EqualTheme {
        ChatScreenSample(official = OfficialNotice.PlanExpired)
    }
}

// snippet: flow-monetization-touchpoints / expired-paywall
@Composable
internal fun TouchExpiredPaywallSnippet() {
    EqualTheme {
        PaywallScreen(touchpoint = PaywallTouchpoint.Recordings, cta = "Get Equal AI Unlimited")
    }
}

// snippet: flow-monetization-touchpoints / expired-card
@Composable
internal fun TouchExpiredCardSnippet() {
    EqualTheme {
        HomeScreenSample(tier = PlanTier.Free, planExpired = true, homeNotice = HomeNotice.PlanExpired())
    }
}

// snippet: flow-monetization-touchpoints / cap-chat-home
@Composable
internal fun TouchCapChatHomeSnippet() {
    EqualTheme {
        HomeScreenSample(tier = PlanTier.Free, assistantOff = true, official = OfficialNotice.UsageCap)
    }
}

// snippet: flow-monetization-touchpoints / cap-chat
@Composable
internal fun TouchCapChatSnippet() {
    EqualTheme {
        ChatScreenSample(official = OfficialNotice.UsageCap)
    }
}

// snippet: flow-monetization-touchpoints / cap-card
@Composable
internal fun TouchCapCardSnippet() {
    EqualTheme {
        HomeScreenSample(tier = PlanTier.Free, assistantOff = true, homeNotice = HomeNotice.UsageCap(callsLeft = 7, limit = 30))
    }
}

// snippet: flow-monetization-touchpoints / actions-home
@Composable
internal fun TouchActionsHomeSnippet() {
    EqualTheme {
        // Try it: a locked chip opens the sheet paywall
        HomeScreenSample(tier = PlanTier.Free, lockedActions = true, paywallSheet = true, promo = { MeetAssistantCarousel() })
    }
}

// snippet: flow-monetization-touchpoints / actions-paywall
@Composable
internal fun TouchActionsPaywallSnippet() {
    EqualTheme {
        PaywallScreen(touchpoint = PaywallTouchpoint.Actions, subtitle = PaywallSubtitle.Actions, cta = "Get Equal AI Unlimited")
    }
}

// snippet: flow-monetization-touchpoints / recordings
@Composable
internal fun TouchRecordingsSnippet() {
    EqualTheme {
        ChatScreenSample(lock = ChatLock.Details)
    }
}

// snippet: flow-monetization-touchpoints / recordings-actions
@Composable
internal fun TouchRecordingsActionsSnippet() {
    EqualTheme {
        ChatScreenSample(lock = ChatLock.Details, summaryActions = true)
    }
}

// snippet: flow-monetization-touchpoints / recordings-paywall
@Composable
internal fun TouchRecordingsPaywallSnippet() {
    EqualTheme {
        PaywallScreen(touchpoint = PaywallTouchpoint.RecordingsActions, cta = "Get Equal AI Unlimited")
    }
}

// snippet: flow-monetization-touchpoints / phone-toggle
@Composable
internal fun TouchPhoneToggleSnippet() {
    EqualTheme {
        HomeScreenSample(assistantArt = AssistantToggleArt.Phone, promo = { MeetAssistantCard(onCallClick = {}, onDismiss = {}) })
    }
}

// ============================================================ Flow: Sheet paywall & payment

// snippet: flow-paywall-sheet / sheet
@Composable
internal fun SheetFlowSheetSnippet() {
    EqualTheme {
        // Try it: Support → Help Center, tap outside → exit offer, Claim → EQUAL25 applied
        HomeScreenSample(tier = PlanTier.Free, paywallSheetStage = PaywallSheetStage.Sheet)
    }
}

// snippet: flow-paywall-sheet / help
@Composable
internal fun SheetFlowHelpSnippet() {
    EqualTheme {
        HomeScreenSample(tier = PlanTier.Free, paywallSheetStage = PaywallSheetStage.Help)
    }
}

// snippet: flow-paywall-sheet / exit
@Composable
internal fun SheetFlowExitSnippet() {
    EqualTheme {
        HomeScreenSample(tier = PlanTier.Free, paywallSheetStage = PaywallSheetStage.Exit)
    }
}

// snippet: flow-paywall-sheet / offer
@Composable
internal fun SheetFlowOfferSnippet() {
    EqualTheme {
        HomeScreenSample(tier = PlanTier.Free, offerStrip = true, promo = { MeetAssistantCarousel() })
    }
}

// snippet: flow-paywall-sheet / claimed
@Composable
internal fun SheetFlowClaimedSnippet() {
    EqualTheme {
        HomeScreenSample(tier = PlanTier.Free, paywallSheetStage = PaywallSheetStage.Claimed)
    }
}

// snippet: flow-paywall-sheet / chat-sheet
@Composable
internal fun SheetFlowChatSheetSnippet() {
    EqualTheme {
        ChatScreenSample(lock = ChatLock.Details, summaryActions = true, paywallSheetStage = PaywallSheetStage.Sheet)
    }
}

// snippet: flow-paywall-sheet / chat-exit
@Composable
internal fun SheetFlowChatExitSnippet() {
    EqualTheme {
        // In a chat the exit offer reads Leave / Keep 25% Off
        ChatScreenSample(lock = ChatLock.Details, summaryActions = true, paywallSheetStage = PaywallSheetStage.Exit)
    }
}

// snippet: flow-paywall-sheet / success
@Composable
internal fun SheetFlowSuccessSnippet() {
    EqualTheme {
        PaymentResultScreen(status = PaymentResultStatus.Success)
    }
}

// snippet: flow-paywall-sheet / pending
@Composable
internal fun SheetFlowPendingSnippet() {
    EqualTheme {
        // Try it: Refresh Status → Congratulations
        PaymentResultScreen(status = PaymentResultStatus.Pending)
    }
}

// snippet: flow-paywall-sheet / failed
@Composable
internal fun SheetFlowFailedSnippet() {
    EqualTheme {
        // Try it: Retry payment → Payment Pending
        PaymentResultScreen(status = PaymentResultStatus.Failed)
    }
}

// snippet: flow-paywall-sheet / pending-support
@Composable
internal fun SheetFlowPendingSupportSnippet() {
    EqualTheme {
        PaymentResultScreen(status = PaymentResultStatus.Pending, pendingAction = PendingAction.Support)
    }
}

// snippet: flow-paywall-sheet / processing-chat
@Composable
internal fun SheetFlowProcessingChatSnippet() {
    EqualTheme {
        ChatScreenSample(lock = ChatLock.Details, summaryActions = true, paymentProcessing = true)
    }
}

// snippet: flow-paywall-sheet / processing-paywall
@Composable
internal fun SheetFlowProcessingPaywallSnippet() {
    EqualTheme {
        PaywallScreen(touchpoint = PaywallTouchpoint.RecordingsActions, processing = true)
    }
}

// snippet: flow-paywall-sheet / processing-home
@Composable
internal fun SheetFlowProcessingHomeSnippet() {
    EqualTheme {
        HomeScreenSample(paymentProcessing = true, promo = { MeetAssistantCard(onCallClick = {}, onDismiss = {}) })
    }
}
