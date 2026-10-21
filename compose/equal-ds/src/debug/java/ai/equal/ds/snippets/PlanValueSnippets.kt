// Compose code shown on the docs site (Components → Monetization: plan value & renewal, discount
// badge). Each function body is extracted by scripts/build-snippets.mjs; keep the
// `// snippet: <doc id> / <example title>` line above it.
// Debug source set only: compiled with the module, never shipped in the release AAR.
@file:Suppress("unused")

package ai.equal.ds.snippets

import ai.equal.ds.components.DiscountBadge
import ai.equal.ds.components.DividerLabel
import ai.equal.ds.components.EqualButton
import ai.equal.ds.components.EqualButtonSize
import ai.equal.ds.components.EqualButtonVariant
import ai.equal.ds.components.ExpiryChip
import ai.equal.ds.components.ExpiryTone
import ai.equal.ds.components.FeatureList
import ai.equal.ds.components.PlanExpiry
import ai.equal.ds.components.PlanExpiryBanner
import ai.equal.ds.components.PriceTag
import ai.equal.ds.components.Quota
import ai.equal.ds.components.QuotaCard
import ai.equal.ds.components.UnlimitedFeatures
import ai.equal.ds.components.ValueCard
import ai.equal.ds.components.ValueSheet
import ai.equal.ds.components.ValueSheetVariant
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp

// ============================================================ Plan expiry banner

// snippet: plan-expiry-banner / States
@Composable
internal fun PlanExpiryBannerStatesSnippet() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        PlanExpiryBanner(expiry = PlanExpiry.Expiring(days = 2), onRenew = { /* open PaywallScreen */ })
        PlanExpiryBanner(expiry = PlanExpiry.Today, onRenew = { /* open PaywallScreen */ })
        PlanExpiryBanner(expiry = PlanExpiry.Expired(date = "30th July"), onRenew = { /* open PaywallScreen */ })
    }
}

// ============================================================ Value card & sheet

// snippet: value-card / Sheet
@Composable
internal fun ValueSheetSnippet() {
    // Overlay: fills the screen (scrim + sheet); pass inline = true to draw only the panel
    ValueSheet(
        expiry = PlanExpiry.Expiring(days = 3),
        onRenew = { /* open PaywallScreen(cta = "Get Equal AI Unlimited") */ },
        onClose = { /* hide the sheet */ },
    )
}

// snippet: value-card / Flush sheet (experiment)
@Composable
internal fun ValueSheetFlushSnippet() {
    ValueSheet(
        expiry = PlanExpiry.Today,
        variant = ValueSheetVariant.Flush,
        onRenew = { /* open PaywallScreen(cta = "Get Equal AI Unlimited") */ },
        onClose = { /* hide the sheet */ },
    )
}

// snippet: value-card / On the plan page
@Composable
internal fun ValueCardPlanPageSnippet() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        DividerLabel(text = "What Unlimited Saved You")
        ValueCard(calls = 20, title = null, perks = null, expiry = PlanExpiry.Expired(date = "30th July"))
    }
}

// snippet: value-card / Quota: calls left
@Composable
internal fun QuotaCallsLeftSnippet() {
    // Free plan: "6 out of 30 Calls Left" with the gold progress bar
    QuotaCard(quota = Quota(used = 24, limit = 30, resetsInDays = 4))
}

// snippet: value-card / Quota: limit reached
@Composable
internal fun QuotaLimitReachedSnippet() {
    QuotaCard(quota = Quota(used = 30, limit = 30, resetsInDays = 2))
    // in the sheet: ValueSheet(quota = Quota(used = 30, limit = 30, resetsInDays = 2), onRenew = openPaywall, onClose = close)
}

// snippet: value-card / Expiry chips
@Composable
internal fun ExpiryChipsSnippet() {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        ExpiryChip(text = "25th Jun ’26", tone = ExpiryTone.Active)
        ExpiryChip(text = "03 Days", tone = ExpiryTone.Warning)
        ExpiryChip(text = "Today", tone = ExpiryTone.Danger)
    }
}

// ============================================================ Feature list

// snippet: feature-list / What You Get
@Composable
internal fun FeatureListSnippet() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        DividerLabel(text = "What You Get")
        FeatureList(items = UnlimitedFeatures)
    }
}

// ============================================================ Discount badge & price drop

// snippet: discount-badge / Badges
@Composable
internal fun DiscountBadgesSnippet() {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        DiscountBadge()
        DiscountBadge(label = "Special Coupon Applied!")
    }
}

// snippet: discount-badge / Price drop
@Composable
internal fun PriceDropSnippet() {
    var replay by remember { mutableIntStateOf(0) }
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Strike the old price → land the new one with "Saved ₹100" → pop the badge
        key(replay) {
            PriceTag(price = "149", was = 249, badge = "Special Coupon Applied!", animate = true)
        }
        EqualButton(text = "Replay", onClick = { replay++ }, variant = EqualButtonVariant.Secondary, size = EqualButtonSize.Xs)
    }
}
