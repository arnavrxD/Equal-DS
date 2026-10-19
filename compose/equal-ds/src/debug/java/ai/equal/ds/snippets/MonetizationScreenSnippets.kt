// Compose code shown on the docs site for the Monetization flow page (keys: "flow-monetization /
// <state key>", the keys of the phones on the page). Extracted by scripts/build-snippets.mjs;
// debug source set only: compiled with the module, never shipped in the release AAR.
@file:Suppress("unused")

package ai.equal.ds.snippets

import ai.equal.ds.catalog.ChatLock
import ai.equal.ds.catalog.ChatScreenSample
import ai.equal.ds.catalog.HomeScreenSample
import ai.equal.ds.catalog.PaywallCoupon
import ai.equal.ds.catalog.PaywallOverlay
import ai.equal.ds.catalog.PaywallScreen
import ai.equal.ds.catalog.PaywallSubtitle
import ai.equal.ds.catalog.PaywallTouchpoint
import ai.equal.ds.catalog.SubscriptionScreen
import ai.equal.ds.catalog.SubscriptionState
import ai.equal.ds.catalog.YouPlan
import ai.equal.ds.catalog.YouScreenSample
import ai.equal.ds.components.CancelStep
import ai.equal.ds.components.MeetAssistantCard
import ai.equal.ds.components.PlanTier
import ai.equal.ds.theme.EqualTheme
import androidx.compose.runtime.Composable

// snippet: flow-monetization / home-free
@Composable
internal fun FlowMonetizationHomeFreeSnippet() {
    EqualTheme {
        // Free plan: rows from index 4 on are locked; tapping one opens the paywall
        HomeScreenSample(
            tier = PlanTier.Free,
            lockedFrom = 4,
            promo = { MeetAssistantCard(onCallClick = {}, onDismiss = {}) },
            onLockedChat = { /* open PaywallScreen(touchpoint = PaywallTouchpoint.ChatList) */ },
        )
    }
}

// snippet: flow-monetization / chat-locked
@Composable
internal fun FlowMonetizationChatLockedSnippet() {
    EqualTheme {
        // Free plan: bubbles blurred, recording locked, Unlock All Details instead of the AI panel
        ChatScreenSample(lock = ChatLock.Details, onUnlock = { /* open PaywallScreen(touchpoint = PaywallTouchpoint.Chat) */ })
    }
}

// snippet: flow-monetization / chat-audio
@Composable
internal fun FlowMonetizationChatAudioSnippet() {
    EqualTheme {
        // Free plan: only the call summary recording is locked (Unlock Audio)
        ChatScreenSample(lock = ChatLock.Audio, onUnlock = { /* open PaywallScreen(touchpoint = PaywallTouchpoint.Audio) */ })
    }
}

// snippet: flow-monetization / paywall
@Composable
internal fun FlowMonetizationPaywallSnippet() {
    EqualTheme {
        // Live: APPLY drops ₹249 to ₹149; Unlock → Waiting → Confirmed → Welcome
        PaywallScreen(touchpoint = PaywallTouchpoint.ChatList)
    }
}

// snippet: flow-monetization / paywall-chat
@Composable
internal fun FlowMonetizationPaywallChatSnippet() {
    EqualTheme {
        PaywallScreen(touchpoint = PaywallTouchpoint.Chat)
    }
}

// snippet: flow-monetization / paywall-audio
@Composable
internal fun FlowMonetizationPaywallAudioSnippet() {
    EqualTheme {
        PaywallScreen(touchpoint = PaywallTouchpoint.Audio, coupon = PaywallCoupon.Applied, subtitle = PaywallSubtitle.Get)
    }
}

// snippet: flow-monetization / waiting
@Composable
internal fun FlowMonetizationWaitingSnippet() {
    EqualTheme {
        PaywallScreen(overlay = PaywallOverlay.Waiting)
    }
}

// snippet: flow-monetization / confirmed
@Composable
internal fun FlowMonetizationConfirmedSnippet() {
    EqualTheme {
        PaywallScreen(overlay = PaywallOverlay.Confirmed)
    }
}

// snippet: flow-monetization / pending
@Composable
internal fun FlowMonetizationPendingSnippet() {
    EqualTheme {
        PaywallScreen(overlay = PaywallOverlay.Pending)
    }
}

// snippet: flow-monetization / failed
@Composable
internal fun FlowMonetizationFailedSnippet() {
    EqualTheme {
        PaywallScreen(overlay = PaywallOverlay.Failed)
    }
}

// snippet: flow-monetization / help
@Composable
internal fun FlowMonetizationHelpSnippet() {
    EqualTheme {
        PaywallScreen(overlay = PaywallOverlay.Help)
    }
}

// snippet: flow-monetization / welcome
@Composable
internal fun FlowMonetizationWelcomeSnippet() {
    EqualTheme {
        PaywallScreen(overlay = PaywallOverlay.Welcome)
    }
}

// snippet: flow-monetization / home-paid
@Composable
internal fun FlowMonetizationHomePaidSnippet() {
    EqualTheme {
        // Paid (the default tier): nothing locked
        HomeScreenSample(promo = { MeetAssistantCard(onCallClick = {}, onDismiss = {}) })
    }
}

// snippet: flow-monetization / settings-none
@Composable
internal fun FlowMonetizationSettingsNoneSnippet() {
    EqualTheme {
        // No subscription and no plan: mint header, no plan card, rows without NEW markers.
        YouScreenSample(plan = YouPlan.None)
    }
}

// snippet: flow-monetization / settings
@Composable
internal fun FlowMonetizationSettingsSnippet() {
    EqualTheme {
        YouScreenSample(plan = YouPlan.Active, onManagePlan = { /* open SubscriptionScreen() */ })
    }
}

// snippet: flow-monetization / subscription
@Composable
internal fun FlowMonetizationSubscriptionSnippet() {
    EqualTheme {
        // Live: Cancel Subscription → feedback → confirm → submitted → cancelled (Re-Subscribe)
        SubscriptionScreen()
    }
}

// snippet: flow-monetization / cancel-feedback
@Composable
internal fun FlowMonetizationCancelFeedbackSnippet() {
    EqualTheme {
        SubscriptionScreen(cancelStep = CancelStep.Feedback)
    }
}

// snippet: flow-monetization / cancel-filled
@Composable
internal fun FlowMonetizationCancelFilledSnippet() {
    EqualTheme {
        SubscriptionScreen(cancelStep = CancelStep.Feedback, reason = "Felt it too expensive, I hope it would be under my budget")
    }
}

// snippet: flow-monetization / cancel-confirm
@Composable
internal fun FlowMonetizationCancelConfirmSnippet() {
    EqualTheme {
        SubscriptionScreen(cancelStep = CancelStep.Confirm)
    }
}

// snippet: flow-monetization / cancel-submitted
@Composable
internal fun FlowMonetizationCancelSubmittedSnippet() {
    EqualTheme {
        SubscriptionScreen(cancelStep = CancelStep.Submitted)
    }
}

// snippet: flow-monetization / cancelled
@Composable
internal fun FlowMonetizationCancelledSnippet() {
    EqualTheme {
        SubscriptionScreen(state = SubscriptionState.Cancelled)
    }
}

// snippet: flow-monetization / settings-restore
@Composable
internal fun FlowMonetizationSettingsRestoreSnippet() {
    EqualTheme {
        // Plan ended: free tier, Restore Plan
        YouScreenSample(tier = PlanTier.Free, plan = YouPlan.Restore, onManagePlan = { /* open PaywallScreen() */ })
    }
}

// snippet: flow-monetization / settings-reactivate
@Composable
internal fun FlowMonetizationSettingsReactivateSnippet() {
    EqualTheme {
        // Autopay off: Reactivate plan + the attached banner under the plan card
        YouScreenSample(plan = YouPlan.Reactivate, onManagePlan = { /* open SubscriptionScreen() */ })
    }
}

// snippet: flow-monetization / chat-lapsed
@Composable
internal fun FlowMonetizationChatLapsedSnippet() {
    EqualTheme {
        // Plan lapsed: readable, Reactivate banner under the header
        ChatScreenSample(lock = ChatLock.Lapsed, onUnlock = { /* open SubscriptionScreen() */ })
    }
}
