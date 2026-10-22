// Compose code shown on the docs site (Flows → Known call usage cap, Monetization touchpoints).
// Each function body is extracted by scripts/build-snippets.mjs; keep the `// snippet:` line above it.
@file:Suppress("unused")

package ai.equal.ds.snippets

import ai.equal.ds.catalog.ChatScreenSample
import ai.equal.ds.catalog.HomeNotice
import ai.equal.ds.catalog.HomeScreenSample
import ai.equal.ds.catalog.OfficialNotice
import ai.equal.ds.catalog.PaywallLayout
import ai.equal.ds.catalog.PaywallScreen
import ai.equal.ds.catalog.PaywallSubtitle
import ai.equal.ds.catalog.PaywallTouchpoint
import ai.equal.ds.catalog.UsageCapHomeSample
import ai.equal.ds.catalog.YouPlan
import ai.equal.ds.catalog.YouScreenSample
import ai.equal.ds.components.CallSplit
import ai.equal.ds.components.PlanTier
import ai.equal.ds.components.PriceDealCard
import ai.equal.ds.components.UsageCardCta
import ai.equal.ds.components.UsageDetailsSheet
import ai.equal.ds.components.UsageLimitCard
import ai.equal.ds.components.UsageMeterCard
import ai.equal.ds.components.UsageMeterSpec
import ai.equal.ds.components.UsageState
import ai.equal.ds.components.ValueSheet
import ai.equal.ds.theme.EqualTheme
import androidx.compose.runtime.Composable

// snippet: flow-usage-cap / under-a
@Composable
internal fun UsageCapUnderASnippet() {
    EqualTheme {
        UsageCapHomeSample(state = UsageState.Under, cta = UsageCardCta.Details)
    }
}

// snippet: flow-usage-cap / under-b
@Composable
internal fun UsageCapUnderBSnippet() {
    EqualTheme {
        UsageCapHomeSample(state = UsageState.Under, cta = UsageCardCta.Chevron)
    }
}

// snippet: flow-usage-cap / under-c
@Composable
internal fun UsageCapUnderCSnippet() {
    EqualTheme {
        UsageCapHomeSample(state = UsageState.Under, cta = UsageCardCta.ChevronGold)
    }
}

// snippet: flow-usage-cap / nudge-a
@Composable
internal fun UsageCapNudgeASnippet() {
    EqualTheme {
        UsageCapHomeSample(state = UsageState.Nudge, cta = UsageCardCta.Details)
    }
}

// snippet: flow-usage-cap / nudge-b
@Composable
internal fun UsageCapNudgeBSnippet() {
    EqualTheme {
        UsageCapHomeSample(state = UsageState.Nudge, cta = UsageCardCta.Chevron)
    }
}

// snippet: flow-usage-cap / nudge-c
@Composable
internal fun UsageCapNudgeCSnippet() {
    EqualTheme {
        UsageCapHomeSample(state = UsageState.Nudge, cta = UsageCardCta.ChevronGold)
    }
}

// snippet: flow-usage-cap / exhausted-a
@Composable
internal fun UsageCapExhaustedASnippet() {
    EqualTheme {
        UsageCapHomeSample(state = UsageState.Exhausted, cta = UsageCardCta.Details)
    }
}

// snippet: flow-usage-cap / exhausted-b
@Composable
internal fun UsageCapExhaustedBSnippet() {
    EqualTheme {
        UsageCapHomeSample(state = UsageState.Exhausted, cta = UsageCardCta.Chevron)
    }
}

// snippet: flow-usage-cap / exhausted-c
@Composable
internal fun UsageCapExhaustedCSnippet() {
    EqualTheme {
        UsageCapHomeSample(state = UsageState.Exhausted, cta = UsageCardCta.ChevronGold)
    }
}

// snippet: flow-usage-cap / paused-a
@Composable
internal fun UsageCapPausedASnippet() {
    EqualTheme {
        UsageCapHomeSample(state = UsageState.Paused, cta = UsageCardCta.Details)
    }
}

// snippet: flow-usage-cap / paused-b
@Composable
internal fun UsageCapPausedBSnippet() {
    EqualTheme {
        UsageCapHomeSample(state = UsageState.Paused, cta = UsageCardCta.Chevron)
    }
}

// snippet: flow-usage-cap / paused-c
@Composable
internal fun UsageCapPausedCSnippet() {
    EqualTheme {
        UsageCapHomeSample(state = UsageState.Paused, cta = UsageCardCta.ChevronGold)
    }
}

// snippet: flow-usage-cap / known-cap-a
@Composable
internal fun UsageCapKnownCapASnippet() {
    EqualTheme {
        UsageCapHomeSample(state = UsageState.KnownCap, cta = UsageCardCta.Details)
    }
}

// snippet: flow-usage-cap / known-cap-b
@Composable
internal fun UsageCapKnownCapBSnippet() {
    EqualTheme {
        UsageCapHomeSample(state = UsageState.KnownCap, cta = UsageCardCta.Chevron)
    }
}

// snippet: flow-usage-cap / known-cap-c
@Composable
internal fun UsageCapKnownCapCSnippet() {
    EqualTheme {
        UsageCapHomeSample(state = UsageState.KnownCap, cta = UsageCardCta.ChevronGold)
    }
}

// snippet: flow-usage-cap / unknown-cap-a
@Composable
internal fun UsageCapUnknownCapASnippet() {
    EqualTheme {
        UsageCapHomeSample(state = UsageState.UnknownCap, cta = UsageCardCta.Details)
    }
}

// snippet: flow-usage-cap / unknown-cap-b
@Composable
internal fun UsageCapUnknownCapBSnippet() {
    EqualTheme {
        UsageCapHomeSample(state = UsageState.UnknownCap, cta = UsageCardCta.Chevron)
    }
}

// snippet: flow-usage-cap / unknown-cap-c
@Composable
internal fun UsageCapUnknownCapCSnippet() {
    EqualTheme {
        UsageCapHomeSample(state = UsageState.UnknownCap, cta = UsageCardCta.ChevronGold)
    }
}

// snippet: flow-usage-cap / sheet-20-used
@Composable
internal fun UsageCapSheet20UsedSnippet() {
    EqualTheme {
        UsageCapHomeSample(sheet = "20-used")
    }
}

// snippet: flow-usage-cap / sheet-20-percent
@Composable
internal fun UsageCapSheet20PercentSnippet() {
    EqualTheme {
        UsageCapHomeSample(sheet = "20-percent")
    }
}

// snippet: flow-usage-cap / sheet-20-rows
@Composable
internal fun UsageCapSheet20RowsSnippet() {
    EqualTheme {
        UsageCapHomeSample(sheet = "20-rows")
    }
}

// snippet: flow-usage-cap / sheet-100
@Composable
internal fun UsageCapSheet100Snippet() {
    EqualTheme {
        UsageCapHomeSample(sheet = "100")
    }
}

// snippet: flow-usage-cap / sheet-100-rows
@Composable
internal fun UsageCapSheet100RowsSnippet() {
    EqualTheme {
        UsageCapHomeSample(sheet = "100-rows")
    }
}

// snippet: flow-usage-cap / sheet-known
@Composable
internal fun UsageCapSheetKnownSnippet() {
    EqualTheme {
        UsageCapHomeSample(sheet = "known")
    }
}

// snippet: flow-usage-cap / sheet-known-rows
@Composable
internal fun UsageCapSheetKnownRowsSnippet() {
    EqualTheme {
        UsageCapHomeSample(sheet = "known-rows")
    }
}

// snippet: flow-usage-cap / sheet-unknown
@Composable
internal fun UsageCapSheetUnknownSnippet() {
    EqualTheme {
        UsageCapHomeSample(sheet = "unknown")
    }
}

// snippet: flow-usage-cap / sheet-unknown-rows
@Composable
internal fun UsageCapSheetUnknownRowsSnippet() {
    EqualTheme {
        UsageCapHomeSample(sheet = "unknown-rows")
    }
}

// snippet: flow-usage-cap / details
@Composable
internal fun UsageCapDetailsSnippet() {
    EqualTheme {
        UsageCapHomeSample(details = false)
    }
}

// snippet: flow-usage-cap / details-info
@Composable
internal fun UsageCapDetailsInfoSnippet() {
    EqualTheme {
        UsageCapHomeSample(details = true)
    }
}

// snippet: flow-usage-cap / p0
@Composable
internal fun UsageCapP0Snippet() {
    EqualTheme {
        HomeScreenSample(tier = PlanTier.Free, assistantOff = true, upgradePill = true)
    }
}

// snippet: flow-monetization-touchpoints / expired-card-plain
@Composable
internal fun TouchExpiredCardPlainSnippet() {
    EqualTheme {
        HomeScreenSample(tier = PlanTier.Free, planExpired = true, homeNotice = HomeNotice.PlanExpired(crown = false))
    }
}

// snippet: flow-monetization-touchpoints / expired-profile
@Composable
internal fun TouchExpiredProfileSnippet() {
    EqualTheme {
        YouScreenSample(tier = PlanTier.Free, plan = YouPlan.Expired, planRenew = true)
    }
}

// snippet: flow-monetization-touchpoints / ios-paywall
@Composable
internal fun TouchIosPaywallSnippet() {
    EqualTheme {
        PaywallScreen(layout = PaywallLayout.Ios, price = 499)
    }
}

// snippet: flow-monetization-touchpoints / ios-paywall-image
@Composable
internal fun TouchIosPaywallImageSnippet() {
    EqualTheme {
        PaywallScreen(layout = PaywallLayout.Ios, crownAnimated = false, price = 499)
    }
}

// snippet: flow-monetization-touchpoints / cap-paywall
@Composable
internal fun TouchCapPaywallSnippet() {
    EqualTheme {
        PaywallScreen(touchpoint = PaywallTouchpoint.UsageCap, subtitle = PaywallSubtitle.Cap, cta = "Get Equal AI Unlimited")
    }
}

// snippet: flow-monetization-touchpoints / cap-card-plain
@Composable
internal fun TouchCapCardPlainSnippet() {
    EqualTheme {
        HomeScreenSample(tier = PlanTier.Free, assistantOff = true, homeNotice = HomeNotice.UsageCap(callsLeft = 7, limit = 30, icon = false))
    }
}

// snippet: flow-monetization-touchpoints / rec-chat-home
@Composable
internal fun TouchRecChatHomeSnippet() {
    EqualTheme {
        HomeScreenSample(tier = PlanTier.Free, official = OfficialNotice.RecordingsLocked)
    }
}

// snippet: flow-monetization-touchpoints / rec-chat
@Composable
internal fun TouchRecChatSnippet() {
    EqualTheme {
        ChatScreenSample(official = OfficialNotice.RecordingsLocked)
    }
}

// snippet: flow-monetization-touchpoints / rec-paywall
@Composable
internal fun TouchRecPaywallSnippet() {
    EqualTheme {
        PaywallScreen(touchpoint = PaywallTouchpoint.Recordings, cta = "Get Equal AI Unlimited")
    }
}

// snippet: flow-monetization-touchpoints / rec-card
@Composable
internal fun TouchRecCardSnippet() {
    EqualTheme {
        HomeScreenSample(tier = PlanTier.Free, homeNotice = HomeNotice.RecordingsLocked())
    }
}

// snippet: flow-monetization-touchpoints / rec-card-plain
@Composable
internal fun TouchRecCardPlainSnippet() {
    EqualTheme {
        HomeScreenSample(tier = PlanTier.Free, homeNotice = HomeNotice.RecordingsLocked(icon = false))
    }
}

// snippet: usage-meter / Home card options
@Composable
internal fun UsageMeterOptionsSnippet() {
    UsageMeterCard(UsageMeterSpec(CallSplit(49, 15), total = 80, cta = UsageCardCta.Details), onUpgrade = { /* paywall */ }, onDetails = { /* details */ })
    UsageMeterCard(UsageMeterSpec(CallSplit(49, 15), total = 80, cta = UsageCardCta.Chevron))
    UsageMeterCard(UsageMeterSpec(CallSplit(18, 40), total = 80, state = UsageState.UnknownCap, resetsIn = "2 days", cta = UsageCardCta.ChevronGold))
}

// snippet: usage-meter / Limit card
@Composable
internal fun UsageLimitCardSnippet() {
    ValueSheet(
        card = { UsageLimitCard(CallSplit(62, 18), CallSplit(68, 18), total = 80, resetsInDays = 2, rows = true) },
        onRenew = { /* paywall */ },
        onClose = { /* close */ },
    )
}

// snippet: usage-meter / View Details
@Composable
internal fun UsageDetailsSnippet() {
    UsageDetailsSheet(
        used = CallSplit(9, 4),
        limits = CallSplit(68, 12),
        total = 80,
        resetsInDays = 2,
        info = true,
        onUpgrade = { /* paywall */ },
        onClose = { /* close */ },
    )
}

// snippet: usage-meter / iOS-parity price card
@Composable
internal fun PriceDealCardSnippet() {
    PriceDealCard(price = 499)
}
