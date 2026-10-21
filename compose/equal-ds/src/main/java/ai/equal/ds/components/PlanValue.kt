package ai.equal.ds.components

import ai.equal.ds.assets.EqualAssetImage
import ai.equal.ds.assets.EqualAssets
import ai.equal.ds.icons.EqualIcon
import ai.equal.ds.icons.EqualIcons
import ai.equal.ds.theme.EqualEasing
import ai.equal.ds.theme.EqualTheme
import ai.equal.ds.theme.eqShadow
import ai.equal.ds.theme.tabular
import ai.equal.ds.theme.toBrush
import ai.equal.ds.tokens.EqualMotion
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.text.style.TextAlign
import kotlin.math.roundToInt
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.CollectionInfo
import androidx.compose.ui.semantics.CollectionItemInfo
import androidx.compose.ui.semantics.collectionInfo
import androidx.compose.ui.semantics.collectionItemInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

// Plan value & renewal (Monetization value design): before a plan ends, show what it
// did for the user, then make renewing one tap. The home expiry strip, the "What Unlimited saved
// you" card and sheet, the expiry chip and the What You Get list. Port of the last section of
// web/src/components/Monetization.tsx. DM Sans only.

// ---------------------------------------------------------------------------------------------
// Plan state
// ---------------------------------------------------------------------------------------------

/** Where the plan stands. Drives the [ExpiryChip], the [PlanExpiryBanner] and the [ValueCard] date row. */
sealed interface PlanExpiry {
    /** Running comfortably: "Plan active until [until]" (e.g. "25th Jun ’26"). No home banner. */
    data class Active(val until: String) : PlanExpiry

    /** The states that end the plan and show the home [PlanExpiryBanner] (web `Exclude<PlanExpiry, active>`). */
    sealed interface Ending : PlanExpiry

    /** [days] left: amber. */
    data class Expiring(val days: Int) : Ending

    /** The last day: red. */
    data object Today : Ending

    /** Ended on [date] (e.g. "30th July"): red. */
    data class Expired(val date: String) : Ending
}

/** [ExpiryChip] tint. */
enum class ExpiryTone { Active, Warning, Danger }

/** The value card's date row for a [PlanExpiry]: the [label], the [chip] text and its [tone]. */
@Immutable
data class ExpiryParts(val label: String, val chip: String, val tone: ExpiryTone)

/**
 * The date row text and chip for [expiry] (web `expiryParts`): active → "Plan active until" +
 * the date (gold); expiring → "Plan Expires in" + "03 Days" (two digits, "Day" for 1; amber);
 * today → "Plan Expires" + "Today" (red); expired → "Plan Expired on" + the date (red).
 */
fun expiryParts(expiry: PlanExpiry): ExpiryParts = when (expiry) {
    is PlanExpiry.Active -> ExpiryParts("Plan active until", expiry.until, ExpiryTone.Active)
    is PlanExpiry.Expiring -> ExpiryParts(
        "Plan Expires in",
        "${expiry.days.toString().padStart(2, '0')} ${if (expiry.days == 1) "Day" else "Days"}",
        ExpiryTone.Warning,
    )
    PlanExpiry.Today -> ExpiryParts("Plan Expires", "Today", ExpiryTone.Danger)
    is PlanExpiry.Expired -> ExpiryParts("Plan Expired on", expiry.date, ExpiryTone.Danger)
}

// ---------------------------------------------------------------------------------------------
// Expiry chip & home banner
// ---------------------------------------------------------------------------------------------

/**
 * "03 Days" · "Today" · "30th July" · "25th Jun ’26": the tinted chip on the value card's date
 * row. 30 high, padding 0 8, radius 6, 18sp Bold (-0.02em, tabular), one line.
 * [ExpiryTone.Active] `expiry-chip/active-bg` + `text/gold`; [ExpiryTone.Warning]
 * `expiry-chip/warning-*`; [ExpiryTone.Danger] `expiry-chip/danger-*`.
 */
@Composable
fun ExpiryChip(
    text: String,
    tone: ExpiryTone,
    modifier: Modifier = Modifier,
) {
    val colors = EqualTheme.colors
    val (bg, fg) = when (tone) {
        ExpiryTone.Active -> colors.expiryChipActiveBg to colors.textGold
        ExpiryTone.Warning -> colors.expiryChipWarningBg to colors.expiryChipWarningText
        ExpiryTone.Danger -> colors.expiryChipDangerBg to colors.expiryChipDangerText
    }
    val shape = RoundedCornerShape(6.dp)
    Box(
        modifier
            .height(30.dp)
            .clip(shape)
            .background(bg)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        EqualText(
            text = text,
            style = EqualTheme.type.subheading.copy(fontSize = 18.sp, lineHeight = 22.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.02).em).tabular(),
            color = fg,
            maxLines = 1,
            softWrap = false,
        )
    }
}

/**
 * Home strip under the header when a plan is about to end or has ended: amber
 * (`expiry-banner/warning-*`) for "Your Plan Expires in **2 Days**", red (`expiry-banner/danger-*`)
 * for "Your Plan Expires **Today**" and "Your Plan Expired on **30th July**". Full width, min 44
 * high, padding 6 / 12, gap 10: info-fill 20 in a 3dp-padded `expiry-banner/{warning,danger}-icon-bg` circle,
 * the message (`body2`, the bold part Bold) and an underlined Bold "Renew Now" (`button`,
 * [onRenew], 48dp touch target). Everything is in the banner's text colour. Polite live region.
 */
@Composable
fun PlanExpiryBanner(
    expiry: PlanExpiry.Ending,
    modifier: Modifier = Modifier,
    onRenew: () -> Unit = {},
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    val warning = expiry is PlanExpiry.Expiring
    val bg = if (warning) colors.expiryBannerWarningBg else colors.expiryBannerDangerBg
    val fg = if (warning) colors.expiryBannerWarningText else colors.expiryBannerDangerText
    val iconBg = if (warning) colors.expiryBannerWarningIconBg else colors.expiryBannerDangerIconBg
    val message = buildAnnotatedString {
        val bold = SpanStyle(fontWeight = FontWeight.Bold)
        when (expiry) {
            is PlanExpiry.Expiring -> {
                append("Your Plan Expires in ")
                withStyle(bold) { append("${expiry.days} ${if (expiry.days == 1) "Day" else "Days"}") }
            }
            PlanExpiry.Today -> {
                append("Your Plan Expires ")
                withStyle(bold) { append("Today") }
            }
            is PlanExpiry.Expired -> {
                append("Your Plan Expired on ")
                withStyle(bold) { append(expiry.date) }
            }
        }
    }
    Row(
        modifier
            .fillMaxWidth()
            .heightIn(min = 44.dp)
            .background(bg)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            Modifier
                .background(iconBg, CircleShape)
                .padding(3.dp),
        ) {
            EqualIcon(EqualIcons.InfoFill, contentDescription = null, tint = fg, size = 20.dp)
        }
        BasicText(
            text = message,
            style = type.body2.copy(color = fg),
            modifier = Modifier
                .weight(1f)
                .semantics { liveRegion = LiveRegionMode.Polite },
        )
        EqualText(
            text = "Renew Now",
            style = type.button.copy(fontWeight = FontWeight.Bold, textDecoration = TextDecoration.Underline),
            color = fg,
            maxLines = 1,
            modifier = Modifier
                .minimumInteractiveComponentSize()
                .clickable(role = Role.Button, onClick = onRenew),
        )
    }
}

// ---------------------------------------------------------------------------------------------
// Value card & sheet
// ---------------------------------------------------------------------------------------------

/** One perk row on the [ValueCard]: a 16dp [PremiumIcon] ([icon], a `-fill` symbol) and [text] (bold the key phrase with a span). */
@Immutable
class ValuePerk(val icon: ImageVector, val text: AnnotatedString)

private fun perk(icon: ImageVector, lead: String, bold: String, tail: String = "") = ValuePerk(
    icon = icon,
    text = buildAnnotatedString {
        append(lead)
        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(bold) }
        append(tail)
    },
)

/**
 * Premium icon: a filled Material Symbol ([icon], use the `-fill` icons) painted with the gold
 * radial gradient `premium-icon/start` → `premium-icon/end`, centred near the top-left (30% / 25%,
 * radius 85% of [size]). Decorative (no content description). Use on monetization perk rows.
 */
@Composable
fun PremiumIcon(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    size: Dp = 16.dp,
) {
    val colors = EqualTheme.colors
    val start = colors.premiumIconStart
    val end = colors.premiumIconEnd
    EqualIcon(
        icon,
        contentDescription = null,
        tint = Color.Black,
        size = size,
        modifier = modifier
            .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
            .drawWithContent {
                drawContent()
                drawRect(
                    brush = Brush.radialGradient(
                        0f to start,
                        1f to end,
                        center = Offset(this.size.width * 0.3f, this.size.height * 0.25f),
                        radius = this.size.minDimension * 0.85f,
                    ),
                    blendMode = BlendMode.SrcIn,
                )
            },
    )
}

/** The three perks of the value sheet: on duty 24/7, every call screened, no Unknown Numbers. */
val DefaultValuePerks: List<ValuePerk> = listOf(
    perk(EqualIcons.Timer, "Your Assistant stayed ", "on duty 24/7"),
    perk(EqualIcons.CallSparkleFill, "Screened & Transcribed ", "every call"),
    perk(EqualIcons.Person, "Never asked you to ", "deal with an Unknown Number"),
)

/** [ValueCard] background. */
enum class ValueCardPattern {
    /** Soft gold rings over `gradient/value-card` (the sheet). */
    Rings,

    /**
     * The gold circuit lines across the top (plan page): `paywall/card-bg` → `bg/surface` at 60%,
     * [EqualAssets.ValueCardPattern] full width at the top, faded out in the centre and towards
     * the bottom; 45% in dark theme. No rings.
     */
    Circuit,
}

/**
 * "What Unlimited saved you": the gold ring card that shows the plan's value before asking for a
 * renewal. Radius 20, 1dp `value-card/stroke`. Background by [pattern] (default
 * [ValueCardPattern.Circuit] when [perks] is `null`, else [ValueCardPattern.Rings]): rings in
 * light are `bg/surface` with [EqualAssets.PremiumGlow] full width at the top; in dark
 * `gradient/value-card` (black with a gold glow) with rings in `value-card/ring` (centre 70dp,
 * first 35.5dp then every 54dp, faded from 30% to the end of the top 260dp); circuit is
 * `paywall/card-bg` → `bg/surface` (60%) under the gold circuit lines. Column padding 18 top /
 * 14 bottom, gap 14:
 * - [title] (h16): a [DividerLabel] in `subheading` Medium, uppercased; `null` on the plan page,
 *   where the page shows it as a divider above;
 * - stat (centred, gap 12, padding 4 / 16): [calls] 48/52 Bold (-0.04em, tabular) beside "Calls
 *   Handled" (`title` Medium) over "by your Assistant" (`body1`);
 * - [perks] (h16): radius 14, `value-card/perks-bg`, 1dp `value-card/stroke`, rows padding 10 / 12
 *   (gap 10, a 16dp [PremiumIcon] 3 down, `body2` Regular) split by the stroke; `null` hides them;
 * - date row (6 above): edge-to-edge `paywall/divider` hairlines (min 12) around the
 *   [expiryParts] label (`body1`) and its [ExpiryChip], gap 8;
 * - plan (h16, bottom-aligned): "Current Plan" `caption` Regular `text/secondary` over [plan]
 *   (`title` Medium); on the right ₹[price] (`subheading` Bold), the struck ₹[was] and
 *   [period] (`caption` Regular `text/secondary`).
 */
@Composable
fun ValueCard(
    calls: Int,
    expiry: PlanExpiry,
    modifier: Modifier = Modifier,
    title: String? = "What Unlimited saved you",
    perks: List<ValuePerk>? = DefaultValuePerks,
    plan: String = "Equal AI Unlimited",
    price: Int = 39,
    was: Int? = 49,
    period: String = "/Monthly",
    pattern: ValueCardPattern? = null,
) {
    ValueCardLayout(calls, expiry, modifier, title, perks, plan, price, was, period, pattern, flush = false)
}

/** [ValueCard] body. [flush]: no border and top-only radius 24 (the card is the sheet's top). */
@Composable
private fun ValueCardLayout(
    calls: Int,
    expiry: PlanExpiry,
    modifier: Modifier,
    title: String?,
    perks: List<ValuePerk>?,
    plan: String,
    price: Int,
    was: Int?,
    period: String,
    pattern: ValueCardPattern?,
    flush: Boolean,
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    val parts = expiryParts(expiry)
    ValueCardFrame(
        modifier = modifier,
        pattern = pattern ?: if (perks == null) ValueCardPattern.Circuit else ValueCardPattern.Rings,
        flush = flush,
    ) {
        if (title != null) {
            DividerLabel(
                text = title,
                style = type.subheading.copy(fontWeight = FontWeight.W500),
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
        Row(
            Modifier
                .fillMaxWidth()
                .clearAndSetSemantics { contentDescription = "$calls Calls Handled by your Assistant" }
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
        ) {
            EqualText(
                text = calls.toString(),
                style = type.headline.copy(fontSize = 48.sp, lineHeight = 52.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.04).em).tabular(),
                color = colors.textPrimary,
                maxLines = 1,
            )
            Column {
                EqualText(text = "Calls Handled", style = type.title.copy(fontWeight = FontWeight.W500), color = colors.textPrimary, maxLines = 1)
                EqualText(text = "by your Assistant", style = type.body1, color = colors.textPrimary, maxLines = 1)
            }
        }
        ValuePerksList(perks)
        ValueDateRow(label = parts.label, chip = parts.chip, tone = parts.tone)
        ValuePlanRow(label = "Current Plan", plan = plan, price = price, was = was, period = period)
    }
}

/** The card chrome shared by [ValueCard] and [QuotaCard]: shape, border, [pattern] and the 18 / 14, gap 14 column. */
@Composable
private fun ValueCardFrame(
    modifier: Modifier,
    pattern: ValueCardPattern,
    flush: Boolean,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = EqualTheme.colors
    val shape = if (flush) RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp) else RoundedCornerShape(20.dp)
    // Light rings cards: the supplied glow on bg/surface, no coded rings. Dark keeps gradient + rings.
    val glow = pattern == ValueCardPattern.Rings && !colors.isDark
    val background = when {
        glow -> SolidColor(colors.bgSurface)
        pattern == ValueCardPattern.Rings -> EqualTheme.gradients.valueCard.toBrush()
        else -> Brush.verticalGradient(0f to colors.paywallCardBg, 0.6f to colors.bgSurface)
    }
    Box(
        modifier
            .fillMaxWidth()
            .clip(shape)
            .background(background)
            .then(if (flush) Modifier else Modifier.border(1.dp, colors.valueCardStroke, shape)),
    ) {
        when {
            glow -> Box(Modifier.matchParentSize()) {
                Image(
                    painter = painterResource(EqualAssets.PremiumGlow.resId),
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1440f / 984f),
                )
            }
            pattern == ValueCardPattern.Rings -> RingsLayer(
                modifier = Modifier.matchParentSize(),
                centerY = 70.dp,
                firstRadius = 35.5.dp,
                spacing = 54.dp,
                alpha = 1f,
                fadeStart = 0.3f,
                fadeEnd = 1f,
                darkOpacity = 1f,
                maskHeight = 260.dp,
                color = colors.valueCardRing,
            )
            else -> Box(Modifier.matchParentSize()) { CircuitLayer() }
        }
        Column(
            Modifier
                .fillMaxWidth()
                .padding(top = 18.dp, bottom = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            content = content,
        )
    }
}

/**
 * The gold circuit lines (329 × 192, full width, top), masked like web `.eq-value-card__circuit`:
 * an ellipse (34% × 70% at 50% 30%) clear to 55% then opaque at its edge, intersected with a
 * vertical fade (opaque to 35%, clear at 85%). 45% in dark theme.
 */
@Composable
private fun CircuitLayer() {
    val opacity = if (EqualTheme.colors.isDark) 0.45f else 1f
    Image(
        painter = painterResource(EqualAssets.ValueCardPattern.resId),
        contentDescription = null,
        contentScale = ContentScale.FillWidth,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(329f / 192f)
            .graphicsLayer(alpha = opacity, compositingStrategy = CompositingStrategy.Offscreen)
            .drawWithContent {
                drawContent()
                val w = size.width
                val h = size.height
                val center = Offset(w * 0.5f, h * 0.3f)
                val rx = w * 0.34f
                val ry = h * 0.7f
                // The radial brush is circular (radius rx); scaling y by ry / rx makes it the ellipse.
                withTransform({ scale(scaleX = 1f, scaleY = ry / rx, pivot = center) }) {
                    val reach = maxOf(w, h) * 4f
                    drawRect(
                        brush = Brush.radialGradient(
                            0f to Color.Transparent,
                            0.55f to Color.Transparent,
                            1f to Color.Black,
                            center = center,
                            radius = rx,
                        ),
                        topLeft = Offset(center.x - reach, center.y - reach),
                        size = Size(reach * 2f, reach * 2f),
                        blendMode = BlendMode.DstIn,
                    )
                }
                drawRect(
                    brush = Brush.verticalGradient(
                        0f to Color.Black,
                        0.35f to Color.Black,
                        0.85f to Color.Transparent,
                        1f to Color.Transparent,
                    ),
                    blendMode = BlendMode.DstIn,
                )
            },
    )
}

/** The perk rows (h16): radius 14, `value-card/perks-bg`, 1dp `value-card/stroke`; nothing when empty. */
@Composable
private fun ValuePerksList(perks: List<ValuePerk>?) {
    if (perks.isNullOrEmpty()) return
    val colors = EqualTheme.colors
    val perksShape = RoundedCornerShape(12.dp)
    Column(
        Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(perksShape)
            .background(colors.valueCardPerksBg)
            .border(1.dp, colors.valueCardStroke, perksShape),
    ) {
        perks.forEachIndexed { i, p ->
            if (i > 0) Box(Modifier.fillMaxWidth().height(1.dp).background(colors.valueCardStroke))
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                PremiumIcon(p.icon, modifier = Modifier.padding(top = 3.dp))
                BasicText(text = p.text, style = EqualTheme.type.body2.copy(fontWeight = FontWeight.W400, color = colors.textPrimary), modifier = Modifier.weight(1f))
            }
        }
    }
}

/** The date row (6 above): `paywall/divider` hairlines (min 12) around [label] (`body1`) and the [ExpiryChip], gap 8. */
@Composable
private fun ValueDateRow(label: String, chip: String, tone: ExpiryTone) {
    val colors = EqualTheme.colors
    Row(
        Modifier
            .fillMaxWidth()
            .padding(top = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(Modifier.weight(1f).widthIn(min = 12.dp).height(1.dp).background(colors.paywallDivider))
        EqualText(text = label, style = EqualTheme.type.body1, color = colors.textPrimary, maxLines = 1)
        ExpiryChip(text = chip, tone = tone)
        Box(Modifier.weight(1f).widthIn(min = 12.dp).height(1.dp).background(colors.paywallDivider))
    }
}

/** The plan row (h16, bottom-aligned): [label] over [plan], ₹[price] ₹[was] (struck) [period] on the right. */
@Composable
private fun ValuePlanRow(label: String, plan: String, price: Int, was: Int?, period: String) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(Modifier.weight(1f)) {
            EqualText(text = label, style = type.captionRegular, color = colors.textSecondary, maxLines = 1)
            EqualText(text = plan, style = type.title.copy(fontWeight = FontWeight.SemiBold), color = colors.textPrimary)
        }
        BasicText(
            text = buildAnnotatedString {
                withStyle(SpanStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, color = colors.textPrimary)) { append("₹$price") }
                append(" ")
                if (was != null) {
                    withStyle(SpanStyle(textDecoration = TextDecoration.LineThrough)) { append("₹$was") }
                    append(" ")
                }
                append(period)
            },
            style = type.captionRegular.copy(color = colors.textSecondary).tabular(),
            maxLines = 1,
            softWrap = false,
            // The strike-through is visual only: say "was".
            modifier = Modifier.semantics {
                contentDescription = if (was != null) "₹$price, was ₹$was $period" else "₹$price $period"
            },
        )
    }
}

// ---------------------------------------------------------------------------------------------
// Free plan quota
// ---------------------------------------------------------------------------------------------

/** Free-plan weekly quota: [used] of [limit] calls this week, resetting in [resetsInDays]. */
@Immutable
data class Quota(val used: Int, val limit: Int, val resetsInDays: Int)

/** What Unlimited adds, on the [QuotaCard]: on duty 24/7, Unlimited Recordings, AI Call Summaries & Transcripts. */
val QuotaPerks: List<ValuePerk> = listOf(
    perk(EqualIcons.Timer, "Your Assistant stayed ", "on duty 24/7"),
    perk(EqualIcons.CallSparkleFill, "", "Unlimited Recordings"),
    ValuePerk(EqualIcons.CallLog, AnnotatedString("AI Call Summaries & Transcripts")),
)

/**
 * Free plan: how much of this week's call quota the assistant has used, with the upsell. The
 * [ValueCard] chrome with [ValueCardPattern.Rings] (the premium glow in light, gradient + rings
 * in dark); column gap 14:
 * - head (centred, gap 10, padding 4 / 16 / 0): under the limit "6 out of 30" (30/36 Bold,
 *   -0.02em, tabular) + " Calls Left" (18sp Medium), a 10dp bar (`paywall/divider` track, the
 *   `gradient/cta-gold` fill at used / limit; a progress bar to accessibility) and "**80%** Of
 *   Your Calls Handled by your Assistant this week"; at the limit "Limit Reached" and "**30 out of
 *   30 (100%)** Of Your Calls Handled…" (`body1` `text/secondary`, the bold part `text/primary`);
 * - "Your Quota Resets in" + an [ExpiryTone.Active] [ExpiryChip] "N Days" ("Day" for 1);
 * - [perks] ([QuotaPerks]; `null` hides them);
 * - "Subscribe to" over [plan], ₹[price] ₹[was] (struck) [period].
 */
@Composable
fun QuotaCard(
    quota: Quota,
    modifier: Modifier = Modifier,
    perks: List<ValuePerk>? = QuotaPerks,
    plan: String = "Equal AI Unlimited",
    price: Int = 149,
    was: Int? = 249,
    period: String = "/Monthly",
) {
    QuotaCardLayout(quota, modifier, perks, plan, price, was, period, flush = false)
}

@Composable
private fun QuotaCardLayout(
    quota: Quota,
    modifier: Modifier,
    perks: List<ValuePerk>?,
    plan: String,
    price: Int,
    was: Int?,
    period: String,
    flush: Boolean,
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    val (used, limit, resetsInDays) = quota
    val pct = if (limit <= 0) 100 else minOf(100, ((used * 100f) / limit).roundToInt())
    val reached = used >= limit
    val left = maxOf(0, limit - used)
    val big = type.headline.copy(fontSize = 30.sp, lineHeight = 36.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.02).em, textAlign = TextAlign.Center).tabular()
    val strong = SpanStyle(fontWeight = FontWeight.Bold, color = colors.textPrimary)
    ValueCardFrame(modifier = modifier, pattern = ValueCardPattern.Rings, flush = flush) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            BasicText(
                text = if (reached) {
                    AnnotatedString("Limit Reached")
                } else {
                    buildAnnotatedString {
                        append("$left out of $limit ")
                        withStyle(SpanStyle(fontSize = 18.sp, fontWeight = FontWeight.W500, letterSpacing = 0.sp)) { append("Calls Left") }
                    }
                },
                style = big.copy(color = colors.textPrimary),
                modifier = Modifier.fillMaxWidth(),
            )
            if (!reached) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(CircleShape)
                        .background(colors.paywallDivider)
                        .semantics {
                            contentDescription = "Calls handled this week"
                            progressBarRangeInfo = ProgressBarRangeInfo(used.toFloat().coerceIn(0f, limit.toFloat()), 0f..limit.toFloat())
                        },
                ) {
                    Box(
                        Modifier
                            .fillMaxWidth(pct / 100f)
                            .fillMaxHeight()
                            .clip(CircleShape)
                            .background(EqualTheme.gradients.ctaGold.toBrush()),
                    )
                }
            }
            BasicText(
                text = buildAnnotatedString {
                    withStyle(strong) { append(if (reached) "$used out of $limit ($pct%)" else "$pct%") }
                    append(" Of Your Calls Handled by your Assistant this week")
                },
                style = type.body1.copy(color = colors.textSecondary, textAlign = TextAlign.Center),
                modifier = Modifier.fillMaxWidth(),
            )
        }
        ValueDateRow(
            label = "Your Quota Resets in",
            chip = "$resetsInDays ${if (resetsInDays == 1) "Day" else "Days"}",
            tone = ExpiryTone.Active,
        )
        ValuePerksList(perks)
        ValuePlanRow(label = "Subscribe to", plan = plan, price = price, was = was, period = period)
    }
}

/** [ValueSheet] layout experiment. */
enum class ValueSheetVariant {
    /** The value card inset on the sheet (padding 12 / 12 / 0). */
    Card,

    /** Experiment: the card is the sheet's top (no border), the CTA on a `bg/surface` footer. */
    Flush,
}

/**
 * "Value bottom sheet once called": after the assistant handles a call near the end of a plan,
 * a sheet shows what Unlimited saved the user ([ValueCard] with [calls], [expiry], [perks]) and
 * a block gold [cta] ([onRenew]). Free plan: pass [quota] to show the weekly [QuotaCard] instead
 * ([expiry] is then unused; [perks] defaults to [QuotaPerks]; `null` hides them). [cta] defaults to "Get Equal AI Unlimited" with a [quota], else
 * "Renew your Equal AI". Panel `sheet/bg`, radius 24 top, shadow 0 -8 30 black/12%;
 * [ValueSheetVariant.Card] insets the card (12 / 12 / 0), [ValueSheetVariant.Flush] makes it the
 * sheet's top with the CTA on a `bg/surface` footer under a 1dp `stroke/subtle` line. Footer
 * padding 16. A neutral 44dp close floats 16dp above the panel, centred ([onClose]).
 *
 * As an overlay it fills its parent: `scrim` (tap closes) and the panel at the bottom, which
 * slides up 40% + fades in over `motion.slow` with the standard easing (close included; no
 * motion under reduced motion). System Back calls [onClose]. [inline] renders only the panel (radius 24 all round) with the close above, for docs.
 */
@Composable
fun ValueSheet(
    modifier: Modifier = Modifier,
    expiry: PlanExpiry = PlanExpiry.Active(""),
    calls: Int = 20,
    onRenew: () -> Unit = {},
    onClose: () -> Unit = {},
    variant: ValueSheetVariant = ValueSheetVariant.Card,
    cta: String? = null,
    inline: Boolean = false,
    quota: Quota? = null,
    perks: List<ValuePerk>? = if (quota != null) QuotaPerks else DefaultValuePerks,
) {
    val colors = EqualTheme.colors
    val flush = variant == ValueSheetVariant.Flush
    val shape = if (inline) RoundedCornerShape(24.dp) else RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    val label = cta ?: if (quota != null) "Get Equal AI Unlimited" else "Renew your Equal AI"
    val panel: @Composable (Modifier) -> Unit = { panelModifier ->
        Column(panelModifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            // 48dp touch box around the 44dp circle: 14 + 2 = 16dp between the circle and the panel.
            EqualIconButton(icon = EqualIcons.Close, contentDescription = "Close", onClick = onClose)
            Spacer(Modifier.height(14.dp))
            Column(
                Modifier
                    .fillMaxWidth()
                    .eqShadow(SheetShadow, shape)
                    .clip(shape)
                    .background(colors.bgPrimary)
                    .semantics { paneTitle = if (quota != null) "Equal AI Unlimited" else "What Unlimited saved you" },
            ) {
                Box(if (flush) Modifier else Modifier.padding(start = 12.dp, end = 12.dp, top = 12.dp)) {
                    if (quota != null) {
                        QuotaCardLayout(
                            quota = quota,
                            modifier = Modifier,
                            perks = perks,
                            plan = "Equal AI Unlimited",
                            price = 149,
                            was = 249,
                            period = "/Monthly",
                            flush = flush,
                        )
                    } else {
                        ValueCardLayout(
                            calls = calls,
                            expiry = expiry,
                            modifier = Modifier,
                            title = "What Unlimited saved you",
                            perks = perks,
                            plan = "Equal AI Unlimited",
                            price = 39,
                            was = 49,
                            period = "/Monthly",
                            pattern = null,
                            flush = flush,
                        )
                    }
                }
                Column(Modifier.fillMaxWidth().then(if (flush) Modifier.background(colors.bgSurface) else Modifier)) {
                    if (flush) Box(Modifier.fillMaxWidth().height(1.dp).background(colors.strokeSubtle))
                    EqualButton(
                        text = label,
                        onClick = onRenew,
                        variant = EqualButtonVariant.Gold,
                        size = EqualButtonSize.Block,
                        modifier = Modifier.padding(16.dp),
                        // "Renew your Equal AI" has its own radial; the quota upsell keeps cta-gold.
                        goldBrush = if (quota == null) EqualTheme.gradients.ctaRenew.toBrush() else null,
                    )
                }
            }
        }
    }
    if (inline) {
        panel(modifier.padding(top = 4.dp))
        return
    }
    EqualBackHandler(onBack = onClose)
    val reduceMotion = rememberReduceMotion()
    val visible = remember { MutableTransitionState(false).apply { targetState = true } }
    Box(modifier.fillMaxSize()) {
        Box(
            Modifier
                .matchParentSize()
                .background(colors.bgOverlay)
                .clickable(interactionSource = rememberInteraction(), indication = null, onClickLabel = "Close", onClick = onClose),
        )
        AnimatedVisibility(
            visibleState = visible,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = (
                slideInVertically(tween(EqualMotion.slow, easing = EqualEasing.standard)) { (it * 0.4f).toInt() } +
                    fadeIn(tween(EqualMotion.slow, easing = EqualEasing.standard))
                ).unlessReduced(reduceMotion),
        ) {
            panel(Modifier)
        }
    }
}

// ---------------------------------------------------------------------------------------------
// What You Get
// ---------------------------------------------------------------------------------------------

/** One Unlimited feature: 3D gold [art] and its [label]. */
@Immutable
data class FeatureItem(val art: EqualAssets, val label: String)

/** The Unlimited features on the plan page. */
val UnlimitedFeatures: List<FeatureItem> = listOf(
    FeatureItem(EqualAssets.FeatureCallScreening, "24/7 Unlimited Call Screening"),
    FeatureItem(EqualAssets.FeatureRecordings, "Unlimited Recordings"),
    FeatureItem(EqualAssets.FeatureSummaries, "Unlimited Summaries"),
)

/**
 * "What You Get": one card per feature, gap 10. Card: radius 12, `bg/surface`, 1dp
 * `stroke/subtle`, padding 14 / 16, gap 12: the 28dp 3D gold art and the label (`subheading`
 * Regular `text/primary`).
 */
@Composable
fun FeatureList(
    modifier: Modifier = Modifier,
    items: List<FeatureItem> = UnlimitedFeatures,
) {
    val colors = EqualTheme.colors
    val shape = RoundedCornerShape(12.dp)
    // A list for TalkBack ("3 items"); each card is one stop.
    Column(
        modifier
            .fillMaxWidth()
            .semantics { collectionInfo = CollectionInfo(rowCount = items.size, columnCount = 1) },
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items.forEachIndexed { index, f ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .semantics(mergeDescendants = true) {
                        collectionItemInfo = CollectionItemInfo(rowIndex = index, rowSpan = 1, columnIndex = 0, columnSpan = 1)
                    }
                    .clip(shape)
                    .background(colors.bgSurface)
                    .border(1.dp, colors.strokeSubtle, shape)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                EqualAssetImage(f.art, size = 28.dp)
                EqualText(text = f.label, style = EqualTheme.type.subheading.copy(fontWeight = FontWeight.W400), color = colors.textPrimary)
            }
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Previews
// ---------------------------------------------------------------------------------------------

@EqualPreviews
@Composable
private fun PlanExpiryBannerPreview() {
    PreviewFrame(padding = 0.dp) {
        PlanExpiryBanner(expiry = PlanExpiry.Expiring(2))
        VSpace(12.dp)
        PlanExpiryBanner(expiry = PlanExpiry.Today)
        VSpace(12.dp)
        PlanExpiryBanner(expiry = PlanExpiry.Expired("30th July"))
        VSpace(12.dp)
        Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ExpiryChip(text = "25th Jun ’26", tone = ExpiryTone.Active)
            ExpiryChip(text = "03 Days", tone = ExpiryTone.Warning)
            ExpiryChip(text = "Today", tone = ExpiryTone.Danger)
        }
    }
}

@EqualPreviews
@Composable
private fun ValueSheetPreview() {
    PreviewFrame {
        ValueSheet(expiry = PlanExpiry.Expiring(3), inline = true)
        VSpace(16.dp)
        ValueSheet(expiry = PlanExpiry.Today, variant = ValueSheetVariant.Flush, inline = true)
    }
}

@EqualPreviews
@Composable
private fun PlanPageValuePreview() {
    PreviewFrame {
        DividerLabel(text = "What Unlimited Saved You")
        VSpace(16.dp)
        ValueCard(calls = 20, title = null, perks = null, expiry = PlanExpiry.Expired("30th July"))
        VSpace(16.dp)
        DividerLabel(text = "What You Get")
        VSpace(16.dp)
        FeatureList()
    }
}

@EqualPreviews
@Composable
private fun QuotaCardPreview() {
    PreviewFrame {
        QuotaCard(quota = Quota(used = 24, limit = 30, resetsInDays = 4))
        VSpace(16.dp)
        QuotaCard(quota = Quota(used = 30, limit = 30, resetsInDays = 2))
    }
}

@EqualPreviews
@Composable
private fun QuotaSheetPreview() {
    PreviewFrame {
        ValueSheet(quota = Quota(used = 24, limit = 30, resetsInDays = 4), inline = true)
        VSpace(16.dp)
        ValueSheet(quota = Quota(used = 30, limit = 30, resetsInDays = 2), inline = true)
    }
}
