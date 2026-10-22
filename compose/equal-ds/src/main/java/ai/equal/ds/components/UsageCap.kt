package ai.equal.ds.components

import ai.equal.ds.icons.EqualIcon
import ai.equal.ds.icons.EqualIcons
import ai.equal.ds.icons.EqualSymbols
import ai.equal.ds.theme.EqualEasing
import ai.equal.ds.theme.EqualTheme
import ai.equal.ds.theme.toBrush
import ai.equal.ds.tokens.EqualMotion
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

/** Calls used this week by caller type (Known Call Usage Cap design). */
@Immutable
data class CallSplit(val known: Int, val unknown: Int)

/** Fill of a meter segment or legend swatch. */
enum class MeterTone { Known, Unknown, Gold, GoldDeep }

@Composable
private fun MeterTone.brush(): Brush = when (this) {
    MeterTone.Known -> SolidColor(EqualTheme.colors.usageMeterKnown)
    MeterTone.Unknown -> SolidColor(EqualTheme.colors.usageMeterUnknown)
    MeterTone.Gold -> EqualTheme.gradients.usageMeterGold.toBrush()
    MeterTone.GoldDeep -> EqualTheme.gradients.usageMeterGoldDeep.toBrush()
}

/** One segment of a [SegmentedMeter]. */
data class MeterSegment(val value: Int, val tone: MeterTone)

/** A full-radius track with flat segments butted together (1dp gap); one progress bar to TalkBack, read as [valueText]. */
@Composable
fun SegmentedMeter(
    segments: List<MeterSegment>,
    max: Int,
    label: String,
    valueText: String,
    modifier: Modifier = Modifier,
    height: Dp = 6.dp,
    gold: Boolean = false,
) {
    val colors = EqualTheme.colors
    val used = segments.sumOf { it.value }
    BoxWithConstraints(
        modifier
            .fillMaxWidth()
            .height(height)
            .clip(CircleShape)
            .background(if (gold) colors.usageMeterGoldTrack else colors.usageMeterTrack)
            .semantics {
                contentDescription = label
                stateDescription = valueText
                progressBarRangeInfo = ProgressBarRangeInfo(used.coerceIn(0, max).toFloat(), 0f..max.toFloat())
            },
    ) {
        val track = maxWidth
        // Each segment is value / max of the whole track; 1dp gaps between them.
        Row(horizontalArrangement = Arrangement.spacedBy(1.dp)) {
            segments.filter { it.value > 0 }.forEach { s ->
                Box(Modifier.fillMaxHeight().width(track * (s.value.toFloat() / max).coerceIn(0f, 1f)).background(s.tone.brush()))
            }
        }
    }
}

/** A legend item. */
data class LegendItem(val tone: MeterTone, val label: String)

/** Swatches (8, radius 2) + `caption` labels, gap 8; [trailing] on the right (`caption` Regular). */
@Composable
fun MeterLegend(items: List<LegendItem>, modifier: Modifier = Modifier, trailing: String? = null) {
    val colors = EqualTheme.colors
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items.forEach {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(Modifier.size(8.dp).clip(RoundedCornerShape(2.dp)).background(it.tone.brush()))
                EqualText(text = it.label, style = EqualTheme.type.caption, color = colors.textSecondary, maxLines = 1)
            }
        }
        if (trailing != null) {
            Spacer(Modifier.weight(1f))
            EqualText(text = trailing, style = EqualTheme.type.captionRegular, color = colors.textSecondary, maxLines = 1)
        }
    }
}

/** A row of the rows layout: [label] · "**n** out of **m**" (or the padded count with [plain]) · an 8 bar. */
@Composable
fun MeterRow(label: String, used: Int, limit: Int, tone: MeterTone, modifier: Modifier = Modifier, plain: Boolean = false) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    fun pad(n: Int) = n.toString().padStart(2, '0')
    val bold = SpanStyle(fontWeight = FontWeight.W700, color = colors.textPrimary)
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            EqualText(text = label, style = type.caption, color = colors.textPrimary, modifier = Modifier.weight(1f))
            BasicText(
                text = buildAnnotatedString {
                    withStyle(bold) { append(pad(used)) }
                    if (!plain) {
                        append(" out of ")
                        withStyle(bold) { append(pad(limit)) }
                    }
                },
                style = type.caption.copy(color = colors.textSecondary),
            )
        }
        SegmentedMeter(listOf(MeterSegment(used, tone)), max = limit, label = label, valueText = "$used of $limit used", height = 8.dp, gold = tone == MeterTone.Gold || tone == MeterTone.GoldDeep)
    }
}

/** Where the weekly allowance stands. */
enum class UsageState { Under, Nudge, Exhausted, Paused, KnownCap, UnknownCap }

/** The [UsageState] for a split, from the shared [total] and the per-type [limits]. */
fun usageState(used: CallSplit, total: Int, limits: CallSplit? = null): UsageState {
    val sum = used.known + used.unknown
    if (limits != null && used.known >= limits.known && sum < total) return UsageState.KnownCap
    if (limits != null && used.unknown >= limits.unknown && sum < total) return UsageState.UnknownCap
    val pct = sum.toFloat() / total
    return when {
        pct >= 1f -> UsageState.Exhausted
        pct >= 0.8f -> UsageState.Nudge
        else -> UsageState.Under
    }
}

/** [UsageMeterCard] experiment options. */
enum class UsageCardCta {
    /** A: Upgrade Now in the header, View Details footer. */
    Details,

    /** B: a chevron chip, full-width green Upgrade Now. */
    Chevron,

    /** C: a chevron chip, full-width gold Upgrade Now. */
    ChevronGold,
}

/** Everything a [UsageMeterCard] shows (every number is a prop). */
@Immutable
data class UsageMeterSpec(
    val used: CallSplit,
    val total: Int,
    val state: UsageState? = null,
    val limits: CallSplit? = null,
    val resetsIn: String = "3 days",
    val pausedUntil: String = "Monday",
    val cta: UsageCardCta = UsageCardCta.Details,
    /** Default: shown from 20 % used. */
    val showUpgrade: Boolean? = null,
    val icon: Boolean = true,
)

/**
 * Home card under the assistant toggle: calls left, the known / unknown meter, the reset, and the
 * upgrade path (option A / B / C). Radius 16, `bg/surface`, 1dp `value-card/stroke`, a mint
 * `usage-meter/glow` wash at the top-right.
 */
@Composable
fun UsageMeterCard(spec: UsageMeterSpec, modifier: Modifier = Modifier, onUpgrade: () -> Unit = {}, onDetails: () -> Unit = {}) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    val used = spec.used
    val s = spec.state ?: usageState(used, spec.total, spec.limits)
    val sum = used.known + used.unknown
    val left = maxOf(0, spec.total - sum)
    val upgrade = spec.showUpgrade ?: (sum.toFloat() / spec.total >= 0.2f)
    val title = when (s) {
        UsageState.Exhausted -> "Weekly Call limit reached"
        UsageState.Paused -> "Assistant Paused till ${spec.pausedUntil}"
        UsageState.KnownCap -> "Known Call limit reached"
        UsageState.UnknownCap -> "Unknown Call limit reached"
        else -> "$left Calls left this week"
    }
    val known = LegendItem(MeterTone.Known, "${used.known} Known" + if (s == UsageState.Paused || s == UsageState.KnownCap) " used" else "")
    val unknown = LegendItem(MeterTone.Unknown, "${used.unknown} Unknown" + if (s == UsageState.Paused || s == UsageState.UnknownCap) " used" else "")
    val unknownFirst = s == UsageState.UnknownCap
    val segs = if (unknownFirst) listOf(MeterSegment(used.unknown, MeterTone.Unknown), MeterSegment(used.known, MeterTone.Known)) else listOf(MeterSegment(used.known, MeterTone.Known), MeterSegment(used.unknown, MeterTone.Unknown))
    val shape = RoundedCornerShape(16.dp)
    val glow = colors.usageMeterGlow
    Column(
        modifier
            .padding(horizontal = 12.dp)
            .fillMaxWidth()
            .clip(shape)
            .background(colors.bgSurface)
            .border(1.dp, colors.valueCardStroke, shape)
            .drawBehind {
                drawCircle(Brush.radialGradient(listOf(glow, Color.Transparent), center = Offset(size.width - 30.dp.toPx(), -90.dp.toPx()), radius = 153.dp.toPx()))
            },
    ) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth().defaultMinSize(minHeight = 28.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                if (spec.icon) EqualIcon(EqualIcons.CallFilled, contentDescription = null, tint = colors.textPrimary, size = 16.dp)
                EqualText(text = title, style = type.body2, color = colors.textPrimary, modifier = Modifier.weight(1f).semantics { heading() })
                if (spec.cta != UsageCardCta.Details) {
                    Box(
                        Modifier
                            .minimumInteractiveComponentSize()
                            .clickable(role = Role.Button, onClickLabel = "View details", onClick = onDetails)
                            .semantics { contentDescription = "View details" },
                        contentAlignment = Alignment.Center,
                    ) {
                        Box(
                            Modifier.size(24.dp).clip(RoundedCornerShape(8.dp)).background(colors.bgSurface).border(1.dp, colors.inviteStroke, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center,
                        ) { EqualIcon(EqualIcons.ChevronRight, contentDescription = null, tint = colors.textPrimary, size = 16.dp) }
                    }
                } else if (upgrade) {
                    EqualButton(text = "Upgrade Now", onClick = onUpgrade, size = EqualButtonSize.Xs)
                }
            }
            SegmentedMeter(segs, max = spec.total, label = "Weekly calls used", valueText = "${used.known} known, ${used.unknown} unknown of ${spec.total} calls used")
            MeterLegend(if (unknownFirst) listOf(unknown, known) else listOf(known, unknown), trailing = if (s == UsageState.Paused) null else "Resets in ${spec.resetsIn}")
        }
        if (spec.cta == UsageCardCta.Details) {
            EqualDivider()
            Box(Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp), contentAlignment = Alignment.Center) {
                EqualTextButton(text = "View Details", onClick = onDetails, trailingIcon = EqualIcons.ChevronRight)
            }
        } else if (upgrade) {
            EqualButton(
                text = "Upgrade Now",
                onClick = onUpgrade,
                size = EqualButtonSize.Block,
                appearance = if (spec.cta == UsageCardCta.ChevronGold) EqualButtonAppearance.Premium else EqualButtonAppearance.Default,
                modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 12.dp),
            )
        }
    }
}

/** The gold sheet head. */
enum class UsageHead { Used, Percent, Reached }

/** Which cap was hit; orders the meter (the copy stays "Weekly Limit Reached"). */
enum class UsageReason { Weekly, Known, Unknown }

/**
 * The gold card on the weekly limit sheets (20 %, 100 %, known cap, unknown cap): the [ValueCard]
 * frame with the premium glow, the head ([head]), a stacked or rows meter in a gold box, "Resets
 * in" + chip, the perks and the plan row. Use it as [ValueSheet]'s `card`.
 */
@Composable
fun UsageLimitCard(
    used: CallSplit,
    limits: CallSplit,
    total: Int,
    resetsInDays: Int,
    modifier: Modifier = Modifier,
    head: UsageHead = UsageHead.Reached,
    rows: Boolean = false,
    reason: UsageReason = UsageReason.Weekly,
    perks: List<ValuePerk>? = QuotaPerks,
    plan: String = "Equal AI Unlimited",
    price: Int = 199,
    was: Int? = 249,
    period: String = "/Monthly",
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    val sum = used.known + used.unknown
    val pct = minOf(100, ((sum * 100f) / total).roundToInt())
    val left = maxOf(0, total - sum)
    val reached = head == UsageHead.Reached
    val big = type.headline.copy(fontSize = 30.sp, lineHeight = 36.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.02).em, textAlign = TextAlign.Center)
    val items = when {
        reason == UsageReason.Unknown -> listOf(LegendItem(MeterTone.Gold, "${used.unknown} Unknown" + if (reached) " used" else ""), LegendItem(MeterTone.GoldDeep, "${used.known} Known"))
        reached -> listOf(LegendItem(MeterTone.GoldDeep, "${used.known} Known used"), LegendItem(MeterTone.Gold, "${used.unknown} Unknown" + if (reason == UsageReason.Known) "" else " used"))
        else -> listOf(LegendItem(MeterTone.Gold, "${used.unknown} Unknown"), LegendItem(MeterTone.GoldDeep, "${used.known} Known"))
    }
    val segs = items.map { MeterSegment(if (it.tone == MeterTone.Gold) used.unknown else used.known, it.tone) }
    ValueCardFrame(modifier = modifier, pattern = ValueCardPattern.Rings, flush = false) {
        Column(
            Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            when (head) {
                UsageHead.Used -> EqualText(text = "Used $pct% of your Weekly AI Calls", style = big, color = colors.textPrimary, modifier = Modifier.fillMaxWidth())
                UsageHead.Percent -> Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    EqualText(text = "$pct%", style = big, color = colors.textPrimary)
                    Column {
                        EqualText(text = "Limit Consumed", style = type.subheading, color = colors.textPrimary)
                        EqualText(text = "$left Calls Left this week", style = type.subheading.copy(fontWeight = FontWeight.W600), color = colors.textPrimary)
                    }
                }
                UsageHead.Reached -> {
                    EqualText(text = "Weekly Limit Reached", style = type.headline.copy(fontWeight = FontWeight.Bold), color = colors.textPrimary, textAlign = TextAlign.Center)
                    BasicText(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = colors.textPrimary)) { append("$sum out of $total ($pct%)") }
                            append(" Of Your Calls Handled by your Assistant this week")
                        },
                        style = type.body1.copy(color = colors.textSecondary, textAlign = TextAlign.Center),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
            val box = RoundedCornerShape(12.dp)
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(box)
                    .background(Brush.verticalGradient(listOf(colors.bgSurface.copy(alpha = 0.81f), Color.Transparent)))
                    .border(1.dp, colors.lockedBadgeStroke, box)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                if (rows) {
                    MeterRow("Known Calls", used.known, limits.known, MeterTone.GoldDeep, plain = !reached)
                    MeterRow("Unknown Calls", used.unknown, limits.unknown, MeterTone.Gold, plain = !reached)
                } else {
                    SegmentedMeter(segs, max = total, label = "Weekly calls used", valueText = "${used.known} known, ${used.unknown} unknown of $total calls used", height = 8.dp, gold = true)
                    MeterLegend(items)
                }
            }
            if (head == UsageHead.Used && !rows) EqualText(text = "$left Calls Left this week", style = type.subheading, color = colors.textPrimary)
        }
        ValueDateRow(label = "Resets in", chip = "$resetsInDays ${if (resetsInDays == 1) "Day" else "Days"}", tone = ExpiryTone.Active)
        ValuePerksList(perks)
        ValuePlanRow(label = "Subscribe to", plan = plan, price = price, was = was, period = period)
    }
}

/** View Details hero: three rings (276 / 186 / 116) breathing, six twinkling sparkles, a glow and the 80 `button/primary` disc with call-sparkle. Still under reduced motion. */
@Composable
fun UsageOrbit(modifier: Modifier = Modifier) {
    val colors = EqualTheme.colors
    val reduce = rememberReduceMotion()
    val phase = if (reduce) {
        0.5f
    } else {
        val t = rememberInfiniteTransition(label = "orbit")
        val p by t.animateFloat(0f, 1f, infiniteRepeatable(tween(3200, easing = EqualEasing.standard), RepeatMode.Reverse), label = "phase")
        p
    }
    val ring = colors.buttonPrimary.copy(alpha = 0.12f)
    val spark = colors.buttonPrimary
    val glow = colors.buttonPrimary.copy(alpha = 0.45f)
    val sparks = listOf(0.18f to 0.30f, 0.82f to 0.22f, 0.08f to 0.70f, 0.92f to 0.66f, 0.30f to 0.92f, 0.70f to 0.90f)
    Box(
        modifier.fillMaxWidth().height(166.dp).drawBehind {
            val c = Offset(size.width / 2, size.height / 2)
            listOf(138.dp, 93.dp, 58.dp).forEach { drawCircle(ring, radius = it.toPx() * (1f + 0.04f * phase), center = c, style = Stroke(1.dp.toPx())) }
            drawCircle(Brush.radialGradient(listOf(glow, Color.Transparent), center = c, radius = 60.dp.toPx()), radius = 60.dp.toPx(), center = c)
            sparks.forEachIndexed { i, (x, y) ->
                val a = if (reduce) 0.6f else 0.25f + 0.65f * (if (i % 2 == 0) phase else 1f - phase)
                drawCircle(spark.copy(alpha = a), radius = 3.dp.toPx(), center = Offset(size.width * x, size.height * y))
            }
        },
        contentAlignment = Alignment.Center,
    ) {
        Box(Modifier.size(80.dp).clip(CircleShape).background(colors.buttonPrimary), contentAlignment = Alignment.Center) {
            EqualIcon(EqualIcons.CallSparkleFill, contentDescription = null, tint = colors.textOnAction, size = 44.dp)
        }
    }
}

/**
 * The green View Details sheet: close, the orbit, "N Calls left this week", per-type meters, "Resets
 * in" + the green chip, the perks ([info] false) or the blue info note ([info] true), and Get Equal AI
 * Unlimited. Radius 28 top, `bg/primary`, padding 24 / 16.
 */
@Composable
fun UsageDetailsSheet(
    used: CallSplit,
    limits: CallSplit,
    total: Int,
    resetsInDays: Int,
    modifier: Modifier = Modifier,
    info: Boolean = false,
    protectionLimit: Int = 30,
    onUpgrade: () -> Unit = {},
    onClose: () -> Unit = {},
    inline: Boolean = false,
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    val left = maxOf(0, total - used.known - used.unknown)
    val shape = if (inline) RoundedCornerShape(28.dp) else RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    val card = RoundedCornerShape(16.dp)
    val panel: @Composable (Modifier) -> Unit = { m ->
        Box(m.fillMaxWidth().clip(shape).background(colors.bgPrimary).clickable(interactionSource = rememberInteraction(), indication = null, onClick = {}).semantics { paneTitle = "Weekly call details" }) {
            Column(Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 16.dp, vertical = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                UsageOrbit()
                Spacer(Modifier.height(24.dp))
                EqualText(text = "$left Calls left this week", style = type.title.copy(fontWeight = FontWeight.Bold), color = colors.textPrimary, textAlign = TextAlign.Center, modifier = Modifier.semantics { heading() })
                Spacer(Modifier.height(16.dp))
                Column(Modifier.fillMaxWidth().clip(card).background(colors.bgSurface).border(1.dp, colors.valueCardStroke, card).padding(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    MeterRow("Known Calls", used.known, limits.known, MeterTone.Known)
                    MeterRow("Unknown Calls", used.unknown, limits.unknown, MeterTone.Unknown)
                }
                Spacer(Modifier.height(16.dp))
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(Modifier.weight(1f).height(1.dp).background(colors.buttonPrimary.copy(alpha = 0.2f)))
                    EqualText(text = "Resets in", style = type.body1, color = colors.textPrimary)
                    ExpiryChip(text = "$resetsInDays ${if (resetsInDays == 1) "Day" else "Days"}", tone = ExpiryTone.Success)
                    Box(Modifier.weight(1f).height(1.dp).background(colors.buttonPrimary.copy(alpha = 0.2f)))
                }
                Spacer(Modifier.height(16.dp))
                if (!info) {
                    Column(Modifier.fillMaxWidth().clip(card).background(colors.bgSurface).border(1.dp, colors.valueCardStroke, card)) {
                        listOf(EqualIcons.Timer to "Your Assistant stayed on duty 24/7", EqualIcons.CallSparkleFill to "Unlimited Recordings", EqualIcons.CallLog to "AI Call Summaries & Transcripts").forEachIndexed { i, (icon, label) ->
                            if (i > 0) Box(Modifier.fillMaxWidth().height(1.dp).background(colors.valueCardStroke))
                            Row(Modifier.fillMaxWidth().defaultMinSize(minHeight = 44.dp).padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                EqualIcon(icon, contentDescription = null, tint = colors.textAction, size = 20.dp)
                                EqualText(text = label, style = type.body1, color = colors.textPrimary)
                            }
                        }
                    }
                } else {
                    Column(Modifier.fillMaxWidth().clip(card).background(colors.infoNoteBg).padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            EqualIcon(EqualSymbols.infoFill, contentDescription = null, tint = colors.infoNoteTitle, size = 20.dp)
                            EqualText(text = "Know how weekly call limit works", style = type.subheading.copy(fontWeight = FontWeight.W600), color = colors.infoNoteTitle)
                        }
                        Spacer(Modifier.height(8.dp))
                        EqualText(text = "Known and unknown calls share the same $total-call allowance.", style = type.captionRegular, color = colors.textSecondary)
                        Spacer(Modifier.height(4.dp))
                        EqualText(text = "Unknown calls also have a $protectionLimit-call protection limit. If one call type reaches its limit first, the other can continue while shared capacity remains.", style = type.captionRegular, color = colors.textSecondary)
                    }
                }
                Spacer(Modifier.height(24.dp))
                EqualButton(text = "Get Equal AI Unlimited", onClick = onUpgrade, size = EqualButtonSize.Block)
            }
            EqualIconButton(icon = EqualIcons.Close, contentDescription = "Close", onClick = onClose, modifier = Modifier.align(Alignment.TopEnd).padding(top = 16.dp, end = 12.dp))
        }
    }
    if (inline) {
        panel(modifier)
        return
    }
    EqualBackHandler(onBack = onClose)
    val reduceMotion = rememberReduceMotion()
    val visible = remember { MutableTransitionState(reduceMotion).apply { targetState = true } }
    Box(modifier.fillMaxSize()) {
        Box(Modifier.matchParentSize().background(colors.bgOverlay).clickable(interactionSource = rememberInteraction(), indication = null, onClickLabel = "Close", onClick = onClose))
        AnimatedVisibility(
            visibleState = visible,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = slideInVertically(tween(EqualMotion.slow, easing = EqualEasing.standard)) { (it * 0.4f).toInt() } + fadeIn(tween(EqualMotion.slow, easing = EqualEasing.standard)),
        ) { panel(Modifier) }
    }
}

