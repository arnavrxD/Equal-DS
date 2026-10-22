package ai.equal.ds.catalog

import ai.equal.ds.components.CallSplit
import ai.equal.ds.components.PlanTier
import ai.equal.ds.components.UsageCardCta
import ai.equal.ds.components.UsageDetailsSheet
import ai.equal.ds.components.UsageHead
import ai.equal.ds.components.UsageLimitCard
import ai.equal.ds.components.UsageMeterSpec
import ai.equal.ds.components.UsageReason
import ai.equal.ds.components.UsageState
import ai.equal.ds.components.ValueSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

/** The shared weekly allowance in the design. */
const val UsageTotal = 80

/** Per-type caps in the design. */
val UsageLimits = CallSplit(known = 68, unknown = 18)

/** The Home card per state (the design's numbers). Port of web `usageCards`. */
val UsageCards: Map<UsageState, UsageMeterSpec> = mapOf(
    UsageState.Under to UsageMeterSpec(CallSplit(9, 4), UsageTotal, UsageState.Under, resetsIn = "6 days"),
    UsageState.Nudge to UsageMeterSpec(CallSplit(49, 15), UsageTotal, UsageState.Nudge, resetsIn = "3 days"),
    UsageState.Exhausted to UsageMeterSpec(CallSplit(62, 18), UsageTotal, UsageState.Exhausted, resetsIn = "2 days"),
    UsageState.Paused to UsageMeterSpec(CallSplit(62, 18), UsageTotal, UsageState.Paused, pausedUntil = "Monday"),
    UsageState.KnownCap to UsageMeterSpec(CallSplit(62, 18), UsageTotal, UsageState.KnownCap, resetsIn = "2 days"),
    UsageState.UnknownCap to UsageMeterSpec(CallSplit(18, 40), UsageTotal, UsageState.UnknownCap, resetsIn = "2 days"),
)

/** A gold weekly limit sheet. */
data class UsageSheet(val used: CallSplit, val head: UsageHead, val rows: Boolean, val reason: UsageReason, val resetsInDays: Int)

/** The gold sheets, keyed as on the flow page (web `usageSheets`). */
val UsageSheets: Map<String, UsageSheet> = linkedMapOf(
    "20-used" to UsageSheet(CallSplit(12, 4), UsageHead.Used, false, UsageReason.Weekly, 4),
    "20-percent" to UsageSheet(CallSplit(12, 4), UsageHead.Percent, false, UsageReason.Weekly, 4),
    "20-rows" to UsageSheet(CallSplit(12, 4), UsageHead.Percent, true, UsageReason.Weekly, 4),
    "100" to UsageSheet(CallSplit(62, 18), UsageHead.Reached, false, UsageReason.Weekly, 2),
    "100-rows" to UsageSheet(CallSplit(62, 18), UsageHead.Reached, true, UsageReason.Weekly, 2),
    "known" to UsageSheet(CallSplit(68, 12), UsageHead.Reached, false, UsageReason.Known, 2),
    "known-rows" to UsageSheet(CallSplit(68, 12), UsageHead.Reached, true, UsageReason.Known, 2),
    "unknown" to UsageSheet(CallSplit(48, 18), UsageHead.Reached, false, UsageReason.Unknown, 2),
    "unknown-rows" to UsageSheet(CallSplit(48, 18), UsageHead.Reached, true, UsageReason.Unknown, 2),
)

/**
 * Free Home in one usage state (port of web `UsageCapHome`). [sheet] pins a gold limit sheet,
 * [details] the green View Details sheet (true = the info note); View Details / the chevron open it
 * live. [p0] is the baseline free home with the 👑 Upgrade pill and no card.
 */
@Composable
fun UsageCapHomeSample(
    modifier: Modifier = Modifier,
    state: UsageState = UsageState.Nudge,
    cta: UsageCardCta = UsageCardCta.Details,
    sheet: String? = null,
    details: Boolean? = null,
    p0: Boolean = false,
    onUpgrade: () -> Unit = {},
) {
    var openSheet by remember(sheet) { mutableStateOf(sheet) }
    var openDetails by remember(details) { mutableStateOf(details) }
    if (p0) {
        HomeScreenSample(modifier, tier = PlanTier.Free, assistantOff = true, upgradePill = true, onRenew = onUpgrade)
        return
    }
    val spec = UsageCards.getValue(state).copy(cta = cta)
    HomeScreenSample(
        modifier,
        tier = PlanTier.Free,
        assistantOff = state == UsageState.Exhausted || state == UsageState.Paused,
        usageMeter = spec,
        onUsageDetails = { openDetails = false },
        onRenew = onUpgrade,
        overlay = when {
            openSheet != null -> {
                {
                    val sh = UsageSheets.getValue(openSheet!!)
                    ValueSheet(
                        card = { UsageLimitCard(sh.used, UsageLimits, UsageTotal, sh.resetsInDays, head = sh.head, rows = sh.rows, reason = sh.reason) },
                        cta = if (openSheet!!.startsWith("20")) "Upgrade to Equal AI Unlimited" else "Get Equal AI Unlimited",
                        onRenew = onUpgrade,
                        onClose = { openSheet = null },
                    )
                }
            }
            openDetails != null -> {
                {
                    UsageDetailsSheet(
                        used = CallSplit(9, 4),
                        limits = CallSplit(68, 12),
                        total = UsageTotal,
                        resetsInDays = 2,
                        info = openDetails == true,
                        onUpgrade = onUpgrade,
                        onClose = { openDetails = null },
                    )
                }
            }
            else -> null
        },
    )
}

