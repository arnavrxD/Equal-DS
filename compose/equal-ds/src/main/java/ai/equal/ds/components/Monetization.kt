package ai.equal.ds.components

import ai.equal.ds.assets.EqualAssetImage
import ai.equal.ds.assets.EqualAssets
import ai.equal.ds.icons.EqualIcon
import ai.equal.ds.icons.EqualIcons
import ai.equal.ds.theme.EqualEasing
import ai.equal.ds.theme.EqualShadow
import ai.equal.ds.theme.EqualShadowLayer
import ai.equal.ds.theme.EqualTheme
import ai.equal.ds.theme.eqShadow
import ai.equal.ds.theme.toBrush
import ai.equal.ds.tokens.EqualGradientSpec
import ai.equal.ds.tokens.EqualMotion
import ai.equal.ds.tokens.EqualPrimitives
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import android.os.Build
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Monetization (Monetization Flow design): the Equal Unlimited paywall, its touchpoints (locked chats,
// bubbles and audio), payment results, plan banners and the cancellation flow.
// Typography is DM Sans only: the design's serif display lines are set in DM Sans (400 over 700).

// ---------------------------------------------------------------------------------------------
// Shared pieces
// ---------------------------------------------------------------------------------------------

/** Default blur over locked content. `Modifier.blur` needs API 31+; below that it is a no-op. */
private val LockedBlur = 4.dp

/** Gold display text: `gradient/gold-text` painted through the glyphs, Bold, -0.02em. */
@Composable
internal fun GoldText(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Center,
) {
    BasicText(
        text = text,
        modifier = modifier,
        style = style.copy(
            brush = EqualTheme.gradients.goldText.toBrush(),
            fontWeight = FontWeight.Bold,
            textAlign = textAlign,
        ),
        maxLines = 1,
        softWrap = false,
    )
}

/**
 * Soft concentric rings behind 3D art (hero, status cards, unlock card, value card): 1dp [color]
 * strokes (white by default) at [alpha], centred horizontally at [centerY], first ring at [firstRadius] then every [spacing],
 * faded out between [fadeStart] and [fadeEnd] (fractions of [maskHeight], or of the full height).
 * Dark theme draws them at [darkOpacity].
 */
@Composable
internal fun RingsLayer(
    modifier: Modifier,
    centerY: Dp,
    firstRadius: Dp,
    spacing: Dp,
    alpha: Float,
    fadeStart: Float,
    fadeEnd: Float,
    darkOpacity: Float,
    maskHeight: Dp? = null,
    color: Color = Color.White,
) {
    val opacity = if (EqualTheme.colors.isDark) darkOpacity else 1f
    Box(
        modifier
            .graphicsLayer(alpha = opacity, compositingStrategy = CompositingStrategy.Offscreen)
            .drawWithCache {
                val cy = centerY.toPx()
                val first = firstRadius.toPx()
                val step = spacing.toPx()
                val stroke = Stroke(width = 1.dp.toPx())
                val height = maskHeight?.toPx()?.coerceAtMost(size.height) ?: size.height
                val mask = Brush.verticalGradient(
                    0f to Color.Black,
                    fadeStart to Color.Black,
                    fadeEnd to Color.Transparent,
                    1f to Color.Transparent,
                    startY = 0f,
                    endY = height,
                )
                val maxR = kotlin.math.hypot(size.width / 2f, maxOf(cy, height - cy))
                val ring = color.copy(alpha = color.alpha * alpha)
                onDrawBehind {
                    clipRect(bottom = height) {
                        var r = first
                        while (r <= maxR) {
                            drawCircle(color = ring, radius = r, center = Offset(size.width / 2f, cy), style = stroke)
                            r += step
                        }
                        drawRect(brush = mask, blendMode = BlendMode.DstIn)
                    }
                }
            },
    )
}

/**
 * Premium Lock (Premium Lock.svg, shipped as the vector `ds_asset_crown_lock`): the gold crown with
 * a padlock that marks locked content (rows, bubbles, audio). Default 18dp (16dp inline).
 */
@Composable
fun LockBadge(
    modifier: Modifier = Modifier,
    size: Dp = 18.dp,
) {
    EqualAssetImage(EqualAssets.CrownLock, size = size, modifier = modifier, contentDescription = "Locked")
}

/**
 * Locked content: [content] is blurred (`Modifier.blur(4.dp)`; the real layout stays, so the
 * paywall shows what's behind it) with a [LockBadge]. Default: the badge (18dp on a
 * `locked/badge-bg` circle, 2dp padding) hangs off the bottom-right corner (6 right, 8 below).
 * [inline]: one full-width row, blurred content then the 16dp badge (gap 6) — chat-list previews.
 * Screen readers hear "Locked. Unlock Equal Unlimited to see this." instead of the content.
 *
 * Blur needs API 31+; on lower APIs the content is drawn unblurred (accepted degradation).
 */
@Composable
fun LockedContent(
    modifier: Modifier = Modifier,
    badge: Boolean = true,
    inline: Boolean = false,
    content: @Composable () -> Unit,
) {
    val a11y = Modifier.clearAndSetSemantics { contentDescription = "Locked. Unlock Equal Unlimited to see this." }
    if (inline) {
        Row(
            modifier.fillMaxWidth().then(a11y),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Box(Modifier.weight(1f).blur(LockedBlur, BlurredEdgeTreatment.Unbounded)) { content() }
            if (badge) LockBadge(size = 16.dp)
        }
    } else {
        Box(modifier.then(a11y)) {
            Box(Modifier.blur(LockedBlur, BlurredEdgeTreatment.Unbounded)) { content() }
            if (badge) {
                Box(
                    Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = 6.dp, y = 8.dp)
                        .background(EqualTheme.colors.lockedBadgeBg, CircleShape)
                        .padding(2.dp),
                ) {
                    LockBadge(size = 18.dp)
                }
            }
        }
    }
}

/** [DividerLabel] colour. */
enum class DividerLabelTone { Primary, Gold }

/**
 * "——— LABEL ———" section label (FAQ, Why people choose): two 1dp `paywall/divider` hairlines
 * either side (gap 12) of the uppercased [text] in `caption` (+0.02em), `text/primary` or
 * `text/gold`. [style] replaces the label style (the value card heading uses `subheading`).
 */
@Composable
fun DividerLabel(
    text: String,
    modifier: Modifier = Modifier,
    tone: DividerLabelTone = DividerLabelTone.Primary,
    style: TextStyle? = null,
) {
    val colors = EqualTheme.colors
    Row(
        modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(Modifier.weight(1f).height(1.dp).background(colors.paywallDivider))
        EqualText(
            text = text.uppercase(),
            style = style ?: EqualTheme.type.caption.copy(letterSpacing = 0.02.em),
            color = if (tone == DividerLabelTone.Gold) colors.textGold else colors.textPrimary,
            maxLines = 1,
        )
        Box(Modifier.weight(1f).height(1.dp).background(colors.paywallDivider))
    }
}

// ---------------------------------------------------------------------------------------------
// Paywall
// ---------------------------------------------------------------------------------------------

/** Turntable sway: one full cycle (−20° → +20° → −20°) every 4.8s. */
private const val CrownHalfCycleMillis = 2400

/** `drop-shadow(0 6px 10px rgba(140, 88, 23, 0.25))` on the web crown. */
private val CrownShadowTint = Color(0x408C5817)

/**
 * The 3D gold crown ([EqualAssets.CrownGold], [size] square) with a slow turntable sway: a
 * `graphicsLayer` rocking rotationY −20° → +20°, rotationZ −2° → +2° and translationY 0 → −4dp
 * (pivot 50% / 60%, cameraDistance 8 × density), FastOutSlowIn, 4.8s per full cycle, forever,
 * with a soft warm drop shadow (0 6 10, #8C5817 at 25%; API 31+, where `Modifier.blur` works).
 * Port of web `AnimatedCrown` / `.eq-crown`.
 *
 * It stands in for the rotating-crown video until a frame sequence / MP4 can be exported.
 * Static (no sway, no shadow) when [animated] is false or the system "Remove animations" setting
 * is on (`Settings.Global.ANIMATOR_DURATION_SCALE == 0`). Decorative: no content description.
 */
@Composable
fun AnimatedCrown(
    modifier: Modifier = Modifier,
    size: Dp = 64.dp,
    animated: Boolean = true,
) {
    if (!animated || rememberReduceMotion()) {
        EqualAssetImage(EqualAssets.CrownGold, size = size, modifier = modifier)
        return
    }
    val transition = rememberInfiniteTransition(label = "crown")
    val sway = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(CrownHalfCycleMillis, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "crown-sway",
    )
    Box(
        modifier
            .size(size)
            .graphicsLayer {
                val t = sway.value
                rotationY = -20f + 40f * t
                rotationZ = -2f + 4f * t
                translationY = -4.dp.toPx() * t
                cameraDistance = 8f * density
                transformOrigin = TransformOrigin(0.5f, 0.6f)
            },
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Image(
                painter = painterResource(EqualAssets.CrownGold.resId),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(CrownShadowTint, BlendMode.SrcIn),
                modifier = Modifier
                    .matchParentSize()
                    .offset(y = 6.dp)
                    .blur(5.dp, BlurredEdgeTreatment.Unbounded),
            )
        }
        EqualAssetImage(EqualAssets.CrownGold, size = size)
    }
}


/**
 * Hero of the Equal Unlimited pages: `paywall-hero` gradient with soft white concentric rings
 * (web `.eq-paywall-hero::before`: 1dp white/50% rings every 70dp from r 47.5, centred 120dp from
 * the top, fading out over the top 420dp; 25% in dark), padding bottom 8. Bar 56 high (padding 4 / 12): back (48dp, no fill) and
 * "Support" (help-circle 20 + `body2`, gap 6). Head (h24): [AnimatedCrown] 64 → 12 → [title] 22/26 Regular
 * → [titleStrong] 38/42 Bold (-0.03em) → 8 → [subtitle] `body1` `text/secondary`, centred,
 * max 2 lines, max width 300. [content] (price, preview, coupon) follows in a column with padding
 * 20 / 16 / 8, gap 16.
 */
@Composable
fun PaywallHero(
    modifier: Modifier = Modifier,
    title: String = "Unlock Equal",
    titleStrong: String = "Unlimited",
    subtitle: String? = "Unlock Summaries, Transcripts, Recordings & Unlimited Call Screening",
    onBack: () -> Unit = {},
    onSupport: () -> Unit = {},
    content: (@Composable ColumnScope.() -> Unit)? = null,
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    Box(
        modifier
            .fillMaxWidth()
            .background(EqualTheme.gradients.paywallHero.toBrush()),
    ) {
        RingsLayer(
            modifier = Modifier.matchParentSize().clearAndSetSemantics {},
            centerY = 120.dp,
            firstRadius = 47.5.dp,
            spacing = 70.dp,
            alpha = 0.5f,
            fadeStart = 0f,
            fadeEnd = 1f,
            darkOpacity = 0.25f,
            maskHeight = 420.dp,
        )
        Column(Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(start = 4.dp, end = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PlainIconButton(icon = EqualIcons.Back, contentDescription = "Back", onClick = onBack, tint = colors.textPrimary)
                Spacer(Modifier.weight(1f))
                Row(
                    Modifier
                        .minimumInteractiveComponentSize()
                        .clip(CircleShape)
                        .clickable(role = Role.Button, onClick = onSupport)
                        .padding(horizontal = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    EqualIcon(EqualIcons.HelpCircle, contentDescription = null, tint = colors.textPrimary, size = 20.dp)
                    EqualText(text = "Support", style = type.body2, color = colors.textPrimary, maxLines = 1)
                }
            }
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AnimatedCrown(size = 64.dp)
                Spacer(Modifier.height(12.dp))
                EqualText(
                    text = title,
                    style = type.title.copy(fontSize = 22.sp, lineHeight = 26.sp, fontWeight = FontWeight.W400),
                    color = colors.textPrimary,
                    textAlign = TextAlign.Center,
                )
                EqualText(
                    text = titleStrong,
                    style = type.headline.copy(fontSize = 38.sp, lineHeight = 42.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.03).em),
                    color = colors.textPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.semantics { this.contentDescription = "$title $titleStrong" },
                )
                if (subtitle != null) {
                    Spacer(Modifier.height(8.dp))
                    EqualText(
                        text = subtitle,
                        style = type.body1,
                        color = colors.textSecondary,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.widthIn(max = 300.dp),
                    )
                }
            }
            if (content != null) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    content = content,
                )
            }
        }
    }
}

/** [PriceTag] price-drop phases: the old price, its strike line drawing, the new price landed. */
private enum class PricePhase { Old, Strike, Done }

/**
 * Price drop timeline (web `PriceTag`, Figma monetization prototype): the strike starts at
 * `motion.couponDelay` (300ms) and runs 450ms; the new price lands at `motion.priceDelay` (800ms)
 * over `motion.priceDrop` (600ms).
 */
private const val StrikeDelayMillis = EqualMotion.couponDelay.toLong()
private const val StrikeMillis = 450
private const val PriceLandAtMillis = EqualMotion.priceDelay.toLong()

/**
 * "🎊 LIMITED TIME DISCOUNT 🎊" ([label], `caption` Medium `text/gold`, uppercased) + the big
 * gold price ([currency][price], 44/48 Bold `gradient/gold-text`) between two 1dp
 * `paywall/divider` hairlines that fade out toward the edges (gap 16). Column gap 6.
 *
 * With [was] (the struck list price) it is the price drop: beside the price, baseline-aligned
 * (gap 6), the struck [currency][was] (14sp Medium, tabular) and [period] (default "/Monthly",
 * 14sp), both `text/secondary`; under it "Saved ₹N" (`caption` Bold `text/brand`, when [price] is
 * a number below [was]) and, with [badge], a [DiscountBadge] ("Special Coupon Applied!").
 * Without [was], [period] sits under the price (`caption` Regular `text/tertiary`).
 *
 * [animate] (Monetization local components design; pass true only when the user caused the change,
 * e.g. APPLY): the old price shows first, a 3dp `text/gold` strike line (108% wide, 52% down,
 * radius 2) draws left → right from `motion.couponDelay` (300ms) over 450ms, then at
 * `motion.priceDelay` (800ms) the new row lands (fade + 6dp rise + 0.96 → 1 scale over
 * `motion.priceDrop`, standard easing) and the badge pops. Restarts when [price],
 * [was] or [animate] change. Under reduced motion the final state shows at once.
 */
@Composable
fun PriceTag(
    price: String,
    modifier: Modifier = Modifier,
    label: String? = "Limited time discount",
    currency: String = "₹",
    period: String? = null,
    was: Int? = null,
    badge: String? = null,
    animate: Boolean = false,
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    val run = animate && was != null && !rememberReduceMotion()
    var phase by remember(run, was, price) { mutableStateOf(if (run) PricePhase.Old else PricePhase.Done) }
    val strike = remember(run, was, price) { Animatable(0f) }
    val land = remember(run, was, price) { Animatable(if (run) 0f else 1f) }
    LaunchedEffect(run, was, price) {
        if (!run) return@LaunchedEffect
        delay(StrikeDelayMillis)
        phase = PricePhase.Strike
        strike.animateTo(1f, tween(StrikeMillis, easing = EqualEasing.standard))
        delay((PriceLandAtMillis - StrikeDelayMillis - StrikeMillis).coerceAtLeast(0L))
        phase = PricePhase.Done
        land.animateTo(1f, tween(EqualMotion.priceDrop, easing = EqualEasing.standard))
    }
    val saved = if (was != null) price.toIntOrNull()?.let { was - it } else null
    val priceStyle = type.headline.copy(fontSize = 44.sp, lineHeight = 48.sp, letterSpacing = (-0.02).em)
    Column(
        modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        if (label != null) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                EmojiGlyph(emoji = "🎊", size = 12.dp)
                EqualText(text = label.uppercase(), style = type.caption, color = colors.textGold, maxLines = 1)
                EmojiGlyph(emoji = "🎊", size = 12.dp)
            }
        }
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(
                Modifier
                    .weight(1f)
                    .height(1.dp)
                    .background(Brush.horizontalGradient(listOf(Color.Transparent, colors.paywallDivider))),
            )
            if (phase == PricePhase.Done) {
                Row(
                    Modifier.graphicsLayer {
                        val t = land.value
                        alpha = t
                        translationY = 6.dp.toPx() * (1f - t)
                        val s = 0.96f + 0.04f * t
                        scaleX = s
                        scaleY = s
                    },
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    GoldText(text = "$currency$price", style = priceStyle, modifier = Modifier.alignByBaseline())
                    if (was != null) {
                        val wasStyle = type.body1.copy(fontSize = 14.sp, color = colors.textSecondary)
                        BasicText(
                            text = buildAnnotatedString {
                                withStyle(SpanStyle(fontWeight = FontWeight.W500, textDecoration = TextDecoration.LineThrough, fontFeatureSettings = "tnum")) {
                                    append("$currency$was")
                                }
                                append(" ")
                                append(period ?: "/Monthly")
                            },
                            style = wasStyle,
                            maxLines = 1,
                            softWrap = false,
                            modifier = Modifier
                                .alignByBaseline()
                                .semantics { contentDescription = "was $currency$was ${period ?: "/Monthly"}" },
                        )
                    }
                }
            } else {
                val gold = colors.textGold
                GoldText(
                    text = "$currency$was",
                    style = priceStyle,
                    modifier = Modifier.drawWithContent {
                        drawContent()
                        val p = strike.value
                        if (p > 0f) {
                            val thick = 3.dp.toPx()
                            drawRoundRect(
                                color = gold,
                                topLeft = Offset(-0.04f * size.width, 0.52f * size.height - thick / 2f),
                                size = Size(1.08f * size.width * p, thick),
                                cornerRadius = CornerRadius(2.dp.toPx()),
                            )
                        }
                    },
                )
            }
            Box(
                Modifier
                    .weight(1f)
                    .height(1.dp)
                    .background(Brush.horizontalGradient(listOf(colors.paywallDivider, Color.Transparent))),
            )
        }
        if (phase == PricePhase.Done && saved != null && saved > 0) {
            EqualText(
                text = "Saved $currency$saved",
                style = type.caption.copy(fontWeight = FontWeight.Bold),
                color = colors.textBrand,
                textAlign = TextAlign.Center,
            )
        }
        if (phase == PricePhase.Done && badge != null) {
            DiscountBadge(label = badge, animate = run)
        }
        if (was == null && period != null) {
            EqualText(text = period, style = type.captionRegular, color = colors.textTertiary, textAlign = TextAlign.Center)
        }
    }
}

/** Badge pop: the spring overshoot `cubic-bezier(0.34, 1.56, 0.64, 1)` over `motion.couponSpring` (590ms) after 120ms; the seal spins with it. */
private val BadgePopEasing = CubicBezierEasing(0.34f, 1.56f, 0.64f, 1f)
private const val BadgePopMillis = EqualMotion.couponSpring
private const val SealSpinMillis = EqualMotion.couponSpring
private const val BadgeDelayMillis = 120
private val BadgeShadow = EqualShadow(listOf(EqualShadowLayer(0.dp, 2.dp, 10.dp, EqualPrimitives.black.copy(alpha = 0.08f))))

/**
 * Coded green wavy seal behind "25% OFF" (web `OffSeal`): a 12-lobe polygon on a 100-unit grid
 * (radius 46 ± 4 · cos 12θ, 121 points) in `text/brand`, with [off] (26u) over "OFF" (22u), DM
 * Sans Bold in `text/on-action`, centred on baselines 47u and 72u. Text scales with the seal, not the font scale.
 */
@Composable
fun OffSeal(off: String, modifier: Modifier = Modifier, size: Dp = 28.dp) {
    val fill = EqualTheme.colors.textBrand
    val ink = EqualTheme.colors.textOnAction
    val measurer = rememberTextMeasurer()
    val base = EqualTheme.type.caption
    Spacer(
        modifier
            .size(size)
            .drawWithCache {
                val u = this.size.minDimension / 100f
                val path = Path()
                for (i in 0..120) {
                    val t = i / 120.0 * 2.0 * Math.PI
                    val rr = 46.0 + 4.0 * kotlin.math.cos(12.0 * t)
                    val x = ((50.0 + rr * kotlin.math.cos(t)) * u).toFloat()
                    val y = ((50.0 + rr * kotlin.math.sin(t)) * u).toFloat()
                    if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                path.close()
                fun line(text: String, units: Float) = measurer.measure(
                    AnnotatedString(text),
                    style = base.copy(
                        fontSize = (units * u).toSp(),
                        lineHeight = TextUnit.Unspecified,
                        letterSpacing = 0.sp,
                        fontWeight = FontWeight.Bold,
                        color = ink,
                    ),
                    softWrap = false,
                )
                val top = line(off, 26f)
                val bottom = line("OFF", 22f)
                onDrawBehind {
                    drawPath(path, fill)
                    drawText(top, topLeft = Offset(50f * u - top.size.width / 2f, 47f * u - top.firstBaseline))
                    drawText(bottom, topLeft = Offset(50f * u - bottom.size.width / 2f, 72f * u - bottom.firstBaseline))
                }
            },
    )
}

/**
 * "25% OFF" seal + a green label ("Discounted!!", "Special Coupon Applied!"): pill, padding
 * 2 / 10 / 2 / 2, `bg/surface`, shadow 0 2 10 black/8%, gap 6; the 28dp coded seal ([off]) and
 * [label] in `caption` Bold `text/brand`. Polite live region.
 *
 * [animate]: after 120ms the badge pops (scale 0.6 → 1 with overshoot + fade, 420ms) while the
 * seal spins in from −90° (600ms, standard easing). Still under reduced motion.
 */
@Composable
fun DiscountBadge(
    modifier: Modifier = Modifier,
    label: String = "Discounted!!",
    off: String = "25%",
    animate: Boolean = false,
) {
    val colors = EqualTheme.colors
    val run = animate && !rememberReduceMotion()
    val pop = remember(run) { Animatable(if (run) 0f else 1f) }
    val spin = remember(run) { Animatable(if (run) -90f else 0f) }
    LaunchedEffect(run) {
        if (!run) return@LaunchedEffect
        launch { spin.animateTo(0f, tween(SealSpinMillis, delayMillis = BadgeDelayMillis, easing = BadgePopEasing)) }
        pop.animateTo(1f, tween(BadgePopMillis, delayMillis = BadgeDelayMillis, easing = BadgePopEasing))
    }
    Row(
        modifier
            .graphicsLayer {
                val p = pop.value
                alpha = p.coerceIn(0f, 1f)
                val s = 0.6f + 0.4f * p
                scaleX = s
                scaleY = s
            }
            .eqShadow(BadgeShadow, CircleShape)
            .clip(CircleShape)
            .background(colors.bgSurface)
            .clearAndSetSemantics {
                contentDescription = "$off off. $label"
                liveRegion = LiveRegionMode.Polite
            }
            .padding(start = 2.dp, top = 2.dp, bottom = 2.dp, end = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        OffSeal(off = off, modifier = Modifier.graphicsLayer { rotationZ = spin.value })
        EqualText(text = label, style = EqualTheme.type.caption.copy(fontWeight = FontWeight.Bold), color = colors.textBrand, maxLines = 1)
    }
}

/**
 * Cream card on the hero that frames a preview of what's locked (rows, bubbles or a call
 * summary): radius 20, `paywall/card-bg`, 1dp `paywall/card-stroke`, padding 8; inside, a radius
 * 14 `bg/surface` panel with its own 1dp `paywall/card-stroke`, clipping [content].
 */
@Composable
fun LockedPreviewCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val colors = EqualTheme.colors
    val outer = RoundedCornerShape(20.dp)
    val inner = RoundedCornerShape(14.dp)
    Box(
        modifier
            .fillMaxWidth()
            .clip(outer)
            .background(colors.paywallCardBg)
            .border(1.dp, colors.paywallCardStroke, outer)
            .padding(8.dp),
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .clip(inner)
                .background(colors.bgSurface)
                .border(1.dp, colors.paywallCardStroke, inner),
        ) {
            content()
        }
    }
}

/**
 * Sticky paywall footer (web `PaywallFooter`): `bg/surface`, shadow 0 -6 18 black/5%, padding
 * 12 / 16 / 16. Optional [above] content (e.g. the toast [ProcessingStrip], 10 below it), a block
 * [cta] button ([variant]: [EqualButtonVariant.Gold] on the paywall, [EqualButtonVariant.GoldOutline]
 * for Cancel Subscription on the plan page, or [EqualButtonVariant.Primary]) and the [note]
 * (`caption` Regular `text/secondary`, centred, 8 above). [disabled] greys the CTA (a payment is
 * processing). [flat] drops the surface and shadow and pads 14 / 16 / 18, for use inside a sheet
 * ([PaywallSheet]). Every paywall-style footer uses it.
 */
@Composable
fun PaywallFooter(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    cta: String = "Unlock Unlimited",
    note: String? = "Cancel Anytime. Automatically renews",
    variant: EqualButtonVariant = EqualButtonVariant.Gold,
    disabled: Boolean = false,
    flat: Boolean = false,
    above: (@Composable () -> Unit)? = null,
) {
    val colors = EqualTheme.colors
    Column(
        modifier
            .fillMaxWidth()
            .then(if (flat) Modifier else Modifier.eqShadow(FooterShadow).background(colors.bgSurface))
            .padding(start = 16.dp, end = 16.dp, top = if (flat) 14.dp else 12.dp, bottom = if (flat) 18.dp else 16.dp),
    ) {
        if (above != null) {
            above()
            Spacer(Modifier.height(10.dp))
        }
        EqualButton(text = cta, onClick = onClick, variant = variant, size = EqualButtonSize.Block, enabled = !disabled)
        if (note != null) {
            Spacer(Modifier.height(8.dp))
            EqualText(
                text = note,
                style = EqualTheme.type.captionRegular,
                color = colors.textSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

private val FooterShadow = EqualShadow(listOf(EqualShadowLayer(0.dp, (-6).dp, 18.dp, EqualPrimitives.black.copy(alpha = 0.05f))))

// ---------------------------------------------------------------------------------------------
// Coupon
// ---------------------------------------------------------------------------------------------

/**
 * Ticket outline: a rounded rect ([radius]) with two [notchRadius] half-circles cut out of the
 * left and right edges at [notchAt] of the height.
 */
@Immutable
class TicketShape(
    private val radius: Dp = 20.dp,
    private val notchRadius: Dp = 9.dp,
    private val notchAt: Float = 0.58f,
) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val r = with(density) { radius.toPx() }
        val n = with(density) { notchRadius.toPx() }
        val cy = size.height * notchAt
        val body = Path().apply { addRoundRect(RoundRect(Rect(Offset.Zero, size), CornerRadius(r, r))) }
        val notches = Path().apply {
            addOval(Rect(center = Offset(0f, cy), radius = n))
            addOval(Rect(center = Offset(size.width, cy), radius = n))
        }
        return Outline.Generic(Path.combine(PathOperation.Difference, body, notches))
    }

    override fun equals(other: Any?): Boolean =
        other is TicketShape && other.radius == radius && other.notchRadius == notchRadius && other.notchAt == notchAt

    override fun hashCode(): Int = (radius.hashCode() * 31 + notchRadius.hashCode()) * 31 + notchAt.hashCode()
}

/**
 * Coupon ticket: [TicketShape] card (radius 20, two 9dp notches at 58% height), `gradient/coupon`,
 * 1dp `paywall/card-stroke`, padding 14 / 16 / 12. Top row (gap 12): [code] `subheading` Bold
 * `text/gold` over [description] `caption` Regular `text/secondary`; on the right APPLY (tap) or
 * APPLIED (`buttonSmall` Bold `text/gold`, 4 from the top). Then a dashed `paywall/divider` rule
 * with the 3D % seal (28dp, gap 10, 6 above / 2 below) and [discount] 30/36 Bold
 * `gradient/gold-text`, centred.
 */
@Composable
fun CouponTicket(
    code: String,
    description: String,
    discount: String,
    modifier: Modifier = Modifier,
    applied: Boolean = false,
    onApply: () -> Unit = {},
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    val shape = remember { TicketShape() }
    val stateStyle = type.buttonSmall.copy(fontWeight = FontWeight.Bold)
    Column(
        modifier
            .fillMaxWidth()
            .clip(shape)
            .background(EqualTheme.gradients.coupon.toBrush())
            .border(1.dp, colors.paywallCardStroke, shape)
            .padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 12.dp),
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(Modifier.weight(1f)) {
                EqualText(text = code, style = type.subheading.copy(fontWeight = FontWeight.Bold), color = colors.textGold, maxLines = 1)
                EqualText(text = description, style = type.captionRegular, color = colors.textSecondary)
            }
            if (applied) {
                EqualText(
                    text = "APPLIED",
                    style = stateStyle,
                    color = colors.textGold,
                    maxLines = 1,
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .semantics { liveRegion = LiveRegionMode.Polite },
                )
            } else {
                EqualText(
                    text = "APPLY",
                    style = stateStyle,
                    color = colors.textGold,
                    maxLines = 1,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .clickable(role = Role.Button, onClick = onApply)
                        .padding(top = 4.dp),
                )
            }
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 6.dp, bottom = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            DashedRule(colors.paywallDivider, Modifier.weight(1f))
            EqualAssetImage(EqualAssets.CouponSeal, size = 28.dp)
            DashedRule(colors.paywallDivider, Modifier.weight(1f))
        }
        GoldText(
            text = discount,
            style = type.headline.copy(fontSize = 30.sp, lineHeight = 36.sp, letterSpacing = (-0.02).em),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun DashedRule(color: Color, modifier: Modifier) {
    Canvas(modifier.height(1.dp)) {
        val dash = 3.dp.toPx()
        drawLine(
            color = color,
            start = Offset(0f, size.height / 2f),
            end = Offset(size.width, size.height / 2f),
            strokeWidth = 1.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(dash, dash)),
        )
    }
}

// ---------------------------------------------------------------------------------------------
// FAQ
// ---------------------------------------------------------------------------------------------

/** One FAQ row. */
@Immutable
data class FaqItem(val question: String, val answer: String)

/**
 * FAQ accordion under a [DividerLabel] ([title], gap 14). One `bg/surface` card, radius 16, 1dp
 * `stroke/subtle`, rows split by 1dp `stroke/subtle`. Question row: padding 16 / 14, `bodyLarge`
 * `text/primary` + chevron-down 20 `text/secondary` (rotated 180 when open, gap 12). Answer:
 * `body1` `text/secondary`, padding 0 16 14. Open rows survive configuration changes
 * (rememberSaveable); [defaultOpen] lists the indices expanded at first.
 */
@Composable
fun FaqList(
    items: List<FaqItem>,
    modifier: Modifier = Modifier,
    title: String? = "Frequently asked questions",
    defaultOpen: List<Int> = emptyList(),
) {
    val colors = EqualTheme.colors
    var open by rememberSaveable(saver = OpenRowsSaver) { mutableStateOf(defaultOpen.toList()) }
    val cardShape = RoundedCornerShape(16.dp)
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        if (title != null) DividerLabel(text = title)
        Column(
            Modifier
                .fillMaxWidth()
                .clip(cardShape)
                .background(colors.bgSurface)
                .border(1.dp, colors.strokeSubtle, cardShape),
        ) {
            items.forEachIndexed { index, item ->
                if (index > 0) EqualDivider()
                val isOpen = index in open
                FaqEntry(item = item, open = isOpen, onToggle = { open = if (isOpen) open - index else open + index })
            }
        }
    }
}

/**
 * One question in a FAQ (web `FaqEntry`): the question row (padding 16 / 14, `bodyLarge`
 * `text/primary` + chevron-down 20 `text/secondary`, rotated 180 when [open], gap 12) and, when
 * [open], the answer (`body1` `text/secondary`, padding 0 16 14). [onToggle] null makes the row
 * static. [FaqList] renders its rows through it.
 */
@Composable
fun FaqEntry(
    item: FaqItem,
    open: Boolean,
    modifier: Modifier = Modifier,
    onToggle: (() -> Unit)? = null,
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    Column(modifier.fillMaxWidth()) {
        Row(
            Modifier
                .fillMaxWidth()
                .then(if (onToggle != null) Modifier.clickable(role = Role.Button, onClick = onToggle) else Modifier)
                .semantics { stateDescription = if (open) "Expanded" else "Collapsed" }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            EqualText(text = item.question, style = type.bodyLarge, color = colors.textPrimary, modifier = Modifier.weight(1f))
            EqualIcon(
                EqualIcons.ChevronDown,
                contentDescription = null,
                tint = colors.textSecondary,
                size = 20.dp,
                modifier = Modifier.rotate(if (open) 180f else 0f),
            )
        }
        if (open) {
            EqualText(
                text = item.answer,
                style = type.body1,
                color = colors.textSecondary,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 14.dp),
            )
        }
    }
}

private val OpenRowsSaver = listSaver<MutableState<List<Int>>, Int>(
    save = { it.value },
    restore = { mutableStateOf(it) },
)

// ---------------------------------------------------------------------------------------------
// Status cards & sheets
// ---------------------------------------------------------------------------------------------

/** One full Sunburst turn per `motion.reward` (6s, linear; web `eq-sunburst-turn`). */
private const val SunburstTurnMillis = EqualMotion.reward

/** Gentle step-in for the payment status title: `cubic-bezier(0.25, 0.8, 0.25, 1)`. */
private val GentleEasing = CubicBezierEasing(0.25f, 0.8f, 0.25f, 1f)

/**
 * Slowly rotating ray burst behind success art (Payment Confirmed, Congratulations; web
 * `Sunburst`). [rays] wedges, each half of its 360/[rays]° step (starting straight up, clockwise),
 * in `sunburst/ray` (white 60% light, gold 12% dark), faded radially like the web mask: full at
 * the centre, 60% at 30% and gone at 62% of √2 × [radius] (the 560dp square's corner distance).
 * Drawn centred horizontally in its bounds at [centerY] from the top (or the vertical centre),
 * and free to overflow them (clip in the parent). One turn every 40s, linear, forever; static
 * when [animated] is false or animations are off. Decorative.
 */
@Composable
fun Sunburst(
    modifier: Modifier = Modifier,
    rays: Int = 24,
    centerY: Dp? = null,
    radius: Dp = 280.dp,
    animated: Boolean = true,
) {
    val ray = EqualTheme.colors.sunburstRay
    val turn: State<Float>? = if (animated && !rememberReduceMotion()) {
        rememberInfiniteTransition(label = "sunburst").animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(tween(SunburstTurnMillis, easing = LinearEasing), RepeatMode.Restart),
            label = "sunburst-turn",
        )
    } else {
        null
    }
    Spacer(
        modifier
            .clearAndSetSemantics {}
            .drawWithCache {
                val c = Offset(size.width / 2f, centerY?.toPx() ?: (size.height / 2f))
                val reach = radius.toPx() * kotlin.math.sqrt(2f)
                val step = 2.0 * Math.PI / rays
                val path = Path()
                for (i in 0 until rays) {
                    val a0 = i * step
                    val a1 = a0 + step / 2.0
                    path.moveTo(c.x, c.y)
                    path.lineTo(c.x + (reach * kotlin.math.sin(a0)).toFloat(), c.y - (reach * kotlin.math.cos(a0)).toFloat())
                    path.lineTo(c.x + (reach * kotlin.math.sin(a1)).toFloat(), c.y - (reach * kotlin.math.cos(a1)).toFloat())
                    path.close()
                }
                val fade = Brush.radialGradient(
                    0f to ray,
                    0.30f to ray.copy(alpha = ray.alpha * 0.6f),
                    0.62f to Color.Transparent,
                    1f to Color.Transparent,
                    center = c,
                    radius = reach,
                )
                onDrawBehind {
                    rotate(turn?.value ?: 0f, pivot = c) { drawPath(path, fade) }
                }
            },
    )
}

/** [StatusCard] tint: `gradient/status-*`. */
enum class StatusTone { Neutral, Success, Warning, Danger, Gold }

@Composable
private fun StatusTone.gradient(): EqualGradientSpec {
    val g = EqualTheme.gradients
    return when (this) {
        StatusTone.Neutral -> g.statusNeutral
        StatusTone.Success -> g.statusSuccess
        StatusTone.Warning -> g.statusWarning
        StatusTone.Danger -> g.statusDanger
        StatusTone.Gold -> g.statusGold
    }
}

/**
 * The ring card used by every payment result and the cancellation steps: radius 20, 1dp
 * `stroke/subtle`, tinted `gradient/status-*` ([tone]) with soft concentric rings (centre 70dp
 * from the top, first ring 41dp then every 62dp, white 60%, faded from 40% to 70% of the height;
 * 20% in dark); [StatusTone.Success] and [StatusTone.Gold] add a rotating [Sunburst] centred on
 * the art (70dp down). Padding 18 / 16. 100dp 3D [art] ([AnimatedCrown] for
 * [EqualAssets.CrownGold]) → 10 → two display lines: [strong] 28/32 Bold (-0.02em) and [light]
 * 22/26 Regular, [strongFirst] picks the order → [body] `body1` `text/secondary` (8 above) →
 * optional [content] (16 above, full width).
 */
@Composable
fun StatusCard(
    tone: StatusTone,
    art: EqualAssets,
    strong: String,
    modifier: Modifier = Modifier,
    light: String? = null,
    strongFirst: Boolean = true,
    body: String? = null,
    content: (@Composable () -> Unit)? = null,
) {
    StatusCardLayout(tone, art, strong, modifier, light, strongFirst, body, titleEnter = false, content = content)
}

/**
 * [StatusCard] body. [titleEnter] lets the two display lines rise in (fade + 8dp, `motion.slow`
 * standard) when the card enters composition: the status sheets key it per step (web
 * `.eq-status-sheet .eq-status-card__title`). Off under reduced motion.
 */
@Composable
private fun StatusCardLayout(
    tone: StatusTone,
    art: EqualAssets,
    strong: String,
    modifier: Modifier,
    light: String?,
    strongFirst: Boolean,
    body: String?,
    titleEnter: Boolean,
    content: (@Composable () -> Unit)?,
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    val shape = RoundedCornerShape(20.dp)
    Box(
        modifier
            .fillMaxWidth()
            .clip(shape)
            .background(tone.gradient().toBrush())
            .border(1.dp, colors.strokeSubtle, shape),
    ) {
        RingsLayer(
            modifier = Modifier.matchParentSize(),
            centerY = 70.dp,
            firstRadius = 41.5.dp,
            spacing = 62.dp,
            alpha = 0.6f,
            fadeStart = 0.4f,
            fadeEnd = 0.7f,
            darkOpacity = 0.2f,
        )
        if (tone == StatusTone.Success || tone == StatusTone.Gold) {
            Sunburst(modifier = Modifier.matchParentSize(), centerY = 70.dp)
        }
        val enter = titleEnter && !rememberReduceMotion()
        val rise = remember { Animatable(if (enter) 0f else 1f) }
        if (enter) LaunchedEffect(Unit) { rise.animateTo(1f, tween(EqualMotion.paymentStatus, easing = GentleEasing)) }
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (art == EqualAssets.CrownGold) AnimatedCrown(size = 100.dp) else EqualAssetImage(art, size = 100.dp)
            Spacer(Modifier.height(10.dp))
            val strongLine: @Composable () -> Unit = {
                EqualText(
                    text = strong,
                    style = type.headline.copy(fontSize = 28.sp, lineHeight = 32.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.02).em),
                    color = colors.textPrimary,
                    textAlign = TextAlign.Center,
                )
            }
            val lightLine: @Composable () -> Unit = {
                if (light != null) {
                    EqualText(
                        text = light,
                        style = type.title.copy(fontSize = 22.sp, lineHeight = 26.sp, fontWeight = FontWeight.W400),
                        color = colors.textPrimary,
                        textAlign = TextAlign.Center,
                    )
                }
            }
            Column(
                Modifier.graphicsLayer {
                    alpha = rise.value
                    translationY = 8.dp.toPx() * (1f - rise.value)
                },
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (strongFirst) {
                    strongLine()
                    lightLine()
                } else {
                    lightLine()
                    strongLine()
                }
            }
            if (body != null) {
                Spacer(Modifier.height(8.dp))
                EqualText(text = body, style = type.body1, color = colors.textSecondary, textAlign = TextAlign.Center)
            }
            if (content != null) {
                Spacer(Modifier.height(16.dp))
                Box(Modifier.fillMaxWidth()) { content() }
            }
        }
    }
}

/**
 * Live "Redirecting [to …] in 00:5 Secs" countdown (`body2` `text/action`, centred, polite live
 * region). Ticks once a second from [seconds] and calls [onDone] at zero (restarts when
 * [seconds] changes).
 */
@Composable
fun RedirectNote(
    modifier: Modifier = Modifier,
    seconds: Int = 5,
    to: String? = null,
    onDone: () -> Unit = {},
) {
    var left by remember(seconds) { mutableIntStateOf(seconds) }
    val done by rememberUpdatedState(onDone)
    LaunchedEffect(seconds) {
        left = seconds
        while (left > 0) {
            delay(1000)
            left -= 1
        }
        done()
    }
    val target = if (to != null) " to $to" else ""
    EqualText(
        text = "Redirecting$target in 00:$left Secs",
        style = EqualTheme.type.body2,
        color = EqualTheme.colors.textAction,
        textAlign = TextAlign.Center,
        modifier = modifier
            .fillMaxWidth()
            .semantics { liveRegion = LiveRegionMode.Polite },
    )
}

internal val SheetShadow = EqualShadow(listOf(EqualShadowLayer(0.dp, (-8).dp, 30.dp, EqualPrimitives.black.copy(alpha = 0.12f))))

/**
 * Sheet over a dimmed screen with no close button (payment results, cancellation, Help Center):
 * a full-size layer with the `scrim` (swallows taps) and the panel at the bottom — `bg/surface`,
 * radius 24 top, shadow 0 -8 30 black/12%, padding 12 / 12 / 20; [content] (usually a
 * [StatusCard]) on top, [footer] below (padding 16 top, 8 sides). The panel slides up 40% + fades
 * in over `motion.slow` with the standard easing. [inline] renders only the panel, radius 24 all
 * round, for the catalog and docs. [label] is the pane title for accessibility.
 */
@Composable
fun StatusSheet(
    modifier: Modifier = Modifier,
    inline: Boolean = false,
    label: String = "Status",
    footer: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val colors = EqualTheme.colors
    val shape = if (inline) RoundedCornerShape(24.dp) else RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    val panel: @Composable (Modifier) -> Unit = { panelModifier ->
        Column(
            panelModifier
                .fillMaxWidth()
                .eqShadow(SheetShadow, shape)
                .clip(shape)
                .background(colors.bgSurface)
                .semantics { paneTitle = label }
                .padding(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 20.dp),
        ) {
            content()
            if (footer != null) {
                Box(Modifier.fillMaxWidth().padding(start = 8.dp, end = 8.dp, top = 16.dp)) { footer() }
            }
        }
    }
    if (inline) {
        panel(modifier)
        return
    }
    val visible = remember { MutableTransitionState(false).apply { targetState = true } }
    Box(modifier.fillMaxSize()) {
        Box(
            Modifier
                .matchParentSize()
                .background(colors.scrim)
                .clickable(interactionSource = rememberInteraction(), indication = null, onClick = {}),
        )
        AnimatedVisibility(
            visibleState = visible,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = slideInVertically(tween(EqualMotion.slow, easing = EqualEasing.standard)) { (it * 0.4f).toInt() } +
                fadeIn(tween(EqualMotion.slow, easing = EqualEasing.standard)),
        ) {
            panel(Modifier)
        }
    }
}

/** Payment result shown by [PaymentStatusSheet]. */
enum class PaymentStatus { Waiting, Confirmed, Pending, Failed, Welcome }

/**
 * Payment result sheet ([StatusSheet] + [StatusCard]):
 * - [PaymentStatus.Waiting]: neutral, "Once your payment is being verified…", no action
 *   ([waitingArt]: coin or cash experiment). With [progress] (default) the title steps through
 *   [PaymentProgressSteps] every 1.4s (Connecting To Razorpay → Waiting for Confirmation →
 *   Verifying Transaction, looping), each rising in; otherwise "Waiting for / Confirmation";
 * - [PaymentStatus.Confirmed]: success with the [Sunburst], "Payment / Confirmed", 5s
 *   [RedirectNote] → [onRedirect];
 * - [PaymentStatus.Pending]: warning, refund line with [cardLast4] + Contact Support;
 * - [PaymentStatus.Failed]: danger, refund line + Retry Payment;
 * - [PaymentStatus.Welcome]: gold crown, "Congratulations / Welcome to Equal Unlimited.",
 *   "No more limits…" + 5s redirect to home.
 * Footer buttons are block `lg` primary. The card's title rises in whenever the status or step
 * changes.
 */
@Composable
fun PaymentStatusSheet(
    status: PaymentStatus,
    modifier: Modifier = Modifier,
    cardLast4: String = "2652",
    onRetry: () -> Unit = {},
    onContactSupport: () -> Unit = {},
    onRedirect: () -> Unit = {},
    inline: Boolean = false,
    waitingArt: EqualAssets = EqualAssets.StatusWaiting,
    progress: Boolean = true,
) {
    val cycling = status == PaymentStatus.Waiting && progress
    var step by remember(cycling) { mutableIntStateOf(0) }
    LaunchedEffect(cycling) {
        while (cycling) {
            delay(PaymentProgressStepMillis)
            step = (step + 1) % PaymentProgressSteps.size
        }
    }
    val refund = "If money was deducted from card (**$cardLast4), refund will be initiated within 24hours"
    data class Spec(val tone: StatusTone, val art: EqualAssets, val strong: String, val light: String, val strongFirst: Boolean, val body: String?)
    val s = when (status) {
        PaymentStatus.Waiting -> Spec(StatusTone.Neutral, waitingArt, "Confirmation", "Waiting for", false, "Once your payment is being verified. You will be Redirected Automatically")
        PaymentStatus.Confirmed -> Spec(StatusTone.Success, EqualAssets.StatusConfirmed, "Payment", "Confirmed", true, null)
        PaymentStatus.Pending -> Spec(StatusTone.Warning, EqualAssets.StatusPending, "Payment", "Pending", true, refund)
        PaymentStatus.Failed -> Spec(StatusTone.Danger, EqualAssets.StatusFailed, "Payment", "Failed", true, refund)
        PaymentStatus.Welcome -> Spec(StatusTone.Gold, EqualAssets.CrownGold, "Congratulations", "Welcome to Equal Unlimited.", true, "No more limits. Everything Equal AI can do, now yours.")
    }
    val footer: (@Composable () -> Unit)? = when (status) {
        PaymentStatus.Waiting -> null
        PaymentStatus.Confirmed -> { { RedirectNote(seconds = 5, onDone = onRedirect) } }
        PaymentStatus.Welcome -> { { RedirectNote(seconds = 5, to = "home", onDone = onRedirect) } }
        PaymentStatus.Pending -> { { EqualButton(text = "Contact Support", onClick = onContactSupport, size = EqualButtonSize.Block) } }
        PaymentStatus.Failed -> { { EqualButton(text = "Retry Payment", onClick = onRetry, size = EqualButtonSize.Block) } }
    }
    val stepSpec = if (cycling) PaymentProgressSteps[step] else null
    val strong = stepSpec?.strong ?: s.strong
    val light = stepSpec?.light ?: s.light
    StatusSheet(modifier = modifier, inline = inline, label = "$light $strong", footer = footer) {
        key(status, if (cycling) step else -1) {
            StatusCardLayout(
                tone = s.tone,
                art = s.art,
                strong = strong,
                modifier = Modifier,
                light = light,
                strongFirst = s.strongFirst,
                body = s.body,
                titleEnter = true,
                content = null,
            )
        }
    }
}

/** One title of the waiting sheet's progress: [light] (22/26 Regular) over [strong] (28/32 Bold). */
@Immutable
data class PaymentProgressStep(val light: String, val strong: String)

/** Monetization local components design: the waiting sheet's titles while the payment goes through. */
val PaymentProgressSteps: List<PaymentProgressStep> = listOf(
    PaymentProgressStep(light = "Connecting To", strong = "Razorpay"),
    PaymentProgressStep(light = "Waiting for", strong = "Confirmation"),
    PaymentProgressStep(light = "Verifying", strong = "Transaction"),
)

private const val PaymentProgressStepMillis = 1400L

/**
 * Help Center sheet from Support (or Contact Support): [StatusSheet] with "Help Center" (`title`
 * Medium), the intro (`body1` `text/secondary`, 4 above) and two rows (gap 12, 16 above): radius
 * 14, 1dp `stroke/subtle`, padding 12 / 12 / 12 / 16, title `subheading` over value `body1`
 * `text/secondary`, then a 52 × 36 `action-card/highlight` pill with the 22dp icon in
 * `text/action` (call, mail).
 */
@Composable
fun HelpCenterSheet(
    modifier: Modifier = Modifier,
    phone: String = "+91 9876 543210",
    email: String = "hello@equal.in",
    onCall: () -> Unit = {},
    onMail: () -> Unit = {},
    inline: Boolean = false,
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    StatusSheet(modifier = modifier, inline = inline, label = "Help Center") {
        Column(Modifier.fillMaxWidth().padding(start = 8.dp, end = 8.dp, top = 8.dp)) {
            SheetHeading(title = "Help Center", body = "At the moment we can help you in following ways, feel free to contact us.")
            Spacer(Modifier.height(16.dp))
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                HelpContactRow(title = "Call Us", value = phone, icon = EqualIcons.Call, onClick = onCall)
                HelpContactRow(title = "Mail Us", value = email, icon = EqualIcons.Mail, onClick = onMail)
            }
        }
    }
}

/** A sheet's [title] (`title` Medium `text/primary`) and an optional line of help, [body] (`body1` `text/secondary`, 4 above). */
@Composable
fun SheetHeading(
    title: String,
    modifier: Modifier = Modifier,
    body: String? = null,
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    Column(modifier) {
        EqualText(
            text = title,
            style = type.title.copy(fontWeight = FontWeight.W500),
            color = colors.textPrimary,
            modifier = Modifier.semantics { heading() },
        )
        if (body != null) {
            Spacer(Modifier.height(4.dp))
            EqualText(text = body, style = type.body1, color = colors.textSecondary)
        }
    }
}

/**
 * A way to reach support (web `HelpContactRow`): radius 14, 1dp `stroke/subtle`, padding
 * 12 / 12 / 12 / 16, [title] `subheading` over [value] (number or email) `body1` `text/secondary`,
 * then a 52 × 36 `action-card/highlight` pill with the 22dp [icon] in `text/action`.
 */
@Composable
fun HelpContactRow(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    val colors = EqualTheme.colors
    val shape = RoundedCornerShape(14.dp)
    Row(
        modifier
            .fillMaxWidth()
            .clip(shape)
            .border(1.dp, colors.strokeSubtle, shape)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(start = 16.dp, end = 12.dp, top = 12.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(Modifier.weight(1f)) {
            EqualText(text = title, style = EqualTheme.type.subheading, color = colors.textPrimary, maxLines = 1)
            EqualText(text = value, style = EqualTheme.type.body1, color = colors.textSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Box(
            Modifier
                .size(width = 52.dp, height = 36.dp)
                .background(colors.actionCardHighlight, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            EqualIcon(icon, contentDescription = null, tint = colors.textAction, size = 22.dp)
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Touchpoints
// ---------------------------------------------------------------------------------------------

private val UnlockCardShadow = EqualShadow(listOf(EqualShadowLayer(0.dp, (-10).dp, 30.dp, EqualPrimitives.black.copy(alpha = 0.08f))))

/**
 * Chat-thread paywall card, docked over the bottom of a locked conversation: radius 20,
 * `gradient/status-gold`, shadow 0 -10 30 black/8%, rings (centre 40dp, first 31dp, every 48dp,
 * white 60%, faded 30% → 75%). Padding 16 / 16 / 18: [AnimatedCrown] 56 → 6 → [title] 22/26 Bold →
 * [body] `body1` `text/secondary` → 14 → `lg` gold button ([EqualButtonVariant.Gold]) with the lock icon ([cta]).
 */
@Composable
fun UnlockDetailsCard(
    onUnlock: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = "Unlock All Details",
    body: String = "See full assistant response",
    cta: String = "Unlock Details",
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    val shape = RoundedCornerShape(20.dp)
    Box(
        modifier
            .fillMaxWidth()
            .eqShadow(UnlockCardShadow, shape)
            .clip(shape)
            .background(EqualTheme.gradients.statusGold.toBrush()),
    ) {
        RingsLayer(
            modifier = Modifier.matchParentSize(),
            centerY = 40.dp,
            firstRadius = 31.5.dp,
            spacing = 48.dp,
            alpha = 0.6f,
            fadeStart = 0.3f,
            fadeEnd = 0.75f,
            darkOpacity = 0.2f,
        )
        Column(
            Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AnimatedCrown(size = 56.dp)
            Spacer(Modifier.height(6.dp))
            EqualText(
                text = title,
                style = type.title.copy(fontSize = 22.sp, lineHeight = 26.sp, fontWeight = FontWeight.Bold),
                color = colors.textPrimary,
                textAlign = TextAlign.Center,
            )
            EqualText(text = body, style = type.body1, color = colors.textSecondary, textAlign = TextAlign.Center)
            Spacer(Modifier.height(14.dp))
            EqualButton(text = cta, onClick = onUnlock, variant = EqualButtonVariant.Gold, size = EqualButtonSize.Lg, leadingIcon = EqualIcons.Lock)
        }
    }
}

/**
 * Pill over a locked recording: lock 14 + [label] (`buttonSmall` Bold), `text/on-action` on
 * `button/primary`, 32 high, h16, gap 6, shadow 0 6 16 `button/primary`/30%. Touch target ≥ 48dp.
 */
@Composable
fun UnlockAudioPill(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Unlock Audio",
) {
    val colors = EqualTheme.colors
    val shadow = EqualShadow(listOf(EqualShadowLayer(0.dp, 6.dp, 16.dp, colors.buttonPrimary.copy(alpha = 0.30f))))
    Row(
        modifier
            .minimumInteractiveComponentSize()
            .height(32.dp)
            .eqShadow(shadow, CircleShape)
            .clip(CircleShape)
            .background(colors.buttonPrimary)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        EqualIcon(EqualIcons.Lock, contentDescription = null, tint = colors.textOnAction, size = 14.dp)
        EqualText(
            text = label,
            style = EqualTheme.type.buttonSmall.copy(fontWeight = FontWeight.Bold),
            color = colors.textOnAction,
            maxLines = 1,
        )
    }
}

/**
 * Amber strip for a lapsed plan: info-fill 16 + [text] (`caption`), both `plan-banner/text`, on
 * `plan-banner/bg`, padding 8 / 12, gap 8. [attached] hangs it under a [PlanCard]: inset 8 each
 * side, pulled 14dp up under the card (drawn behind it), top padding 20, radius 14 on the bottom
 * corners only. [onClick] makes the strip a button.
 */
@Composable
fun PlanBanner(
    text: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    attached: Boolean = false,
) {
    val colors = EqualTheme.colors
    val shape = if (attached) RoundedCornerShape(bottomStart = 14.dp, bottomEnd = 14.dp) else RoundedCornerShape(0.dp)
    val attach = if (attached) {
        Modifier
            .zIndex(-1f)
            .layout { measurable, constraints ->
                val pull = 14.dp.roundToPx()
                val placeable = measurable.measure(constraints)
                layout(placeable.width, (placeable.height - pull).coerceAtLeast(0)) { placeable.place(0, -pull) }
            }
            .padding(horizontal = 8.dp)
    } else {
        Modifier
    }
    val click = if (onClick != null) Modifier.clickable(role = Role.Button, onClick = onClick) else Modifier
    Row(
        modifier
            .then(attach)
            .fillMaxWidth()
            .clip(shape)
            .background(colors.planBannerBg)
            .then(click)
            .padding(start = 12.dp, end = 12.dp, top = if (attached) 20.dp else 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        EqualIcon(EqualIcons.InfoFill, contentDescription = null, tint = colors.planBannerText, size = 16.dp)
        EqualText(text = text, style = EqualTheme.type.caption, color = colors.planBannerText, modifier = Modifier.weight(1f))
    }
}

// ---------------------------------------------------------------------------------------------
// Subscription page
// ---------------------------------------------------------------------------------------------

/** Big gold stat: [value] 52/56 Bold (-0.04em) `gradient/gold-text` over [label] `body2`, centred. */
@Composable
fun StatBlock(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        GoldText(
            text = value,
            style = EqualTheme.type.headline.copy(fontSize = 52.sp, lineHeight = 56.sp, letterSpacing = (-0.04).em),
        )
        EqualText(text = label, style = EqualTheme.type.body2, color = EqualTheme.colors.textPrimary, textAlign = TextAlign.Center)
    }
}

private val RatingShadow = EqualShadow(listOf(EqualShadowLayer(0.dp, 2.dp, 6.dp, EqualPrimitives.black.copy(alpha = 0.06f))))

/**
 * "⭐ Rated 4.5 /5" pill: 36 high, h14, radius 10, `bg/surface`, 1dp `paywall/card-stroke`,
 * shadow 0 2 6 black/6%, gap 6. Gold star 20 → "Rated" `body2` → [rating] `body2` Bold →
 * "/[outOf]" `body1` `text/tertiary`. Whole ratings drop the ".0".
 */
@Composable
fun RatingPill(
    rating: Float,
    modifier: Modifier = Modifier,
    outOf: Int = 5,
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    val shape = RoundedCornerShape(10.dp)
    val shown = if (rating % 1f == 0f) rating.toInt().toString() else rating.toString()
    Row(
        modifier
            .height(36.dp)
            .eqShadow(RatingShadow, shape)
            .clip(shape)
            .background(colors.bgSurface)
            .border(1.dp, colors.paywallCardStroke, shape)
            .clearAndSetSemantics { contentDescription = "Rated $shown out of $outOf" }
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        EqualAssetImage(EqualAssets.StarGold, size = 20.dp)
        EqualText(text = "Rated", style = type.body2, color = colors.textPrimary, maxLines = 1)
        EqualText(text = shown, style = type.body2.copy(fontWeight = FontWeight.Bold), color = colors.textPrimary, maxLines = 1)
        EqualText(text = "/$outOf", style = type.body1, color = colors.textTertiary, maxLines = 1)
    }
}

/** One testimonial card. [avatar] is drawn at the start of the name row (e.g. a 36dp image). */
class Testimonial(
    val quote: String,
    val name: String,
    val avatar: (@Composable () -> Unit)? = null,
)

/**
 * Horizontally scrolling testimonial cards with page dots. LazyRow (snap fling), content padding
 * 16 h / 2 top / 4 bottom, gap 12. Card: 240 wide, radius 16, `bg/surface`, 1dp `stroke/subtle`,
 * padding 14, gap 14: quote `body1` `text/secondary` → avatar + name `body2` (gap 10). Dots
 * (10 below, gap 6): 6dp circles, `stroke/medium`, the current page `text/gold`.
 */
@Composable
fun TestimonialCarousel(
    items: List<Testimonial>,
    modifier: Modifier = Modifier,
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    val state = rememberLazyListState()
    val half = with(androidx.compose.ui.platform.LocalDensity.current) { 126.dp.roundToPx() }
    val page by remember(items.size) {
        derivedStateOf {
            when {
                items.isEmpty() -> 0
                !state.canScrollForward -> items.lastIndex
                else -> (state.firstVisibleItemIndex + if (state.firstVisibleItemScrollOffset > half) 1 else 0).coerceAtMost(items.lastIndex)
            }
        }
    }
    val cardShape = RoundedCornerShape(16.dp)
    Column(modifier.fillMaxWidth()) {
        LazyRow(
            state = state,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 2.dp, bottom = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            flingBehavior = rememberSnapFlingBehavior(state),
        ) {
            itemsIndexed(items, key = { _, t -> t.name }) { _, t ->
                Column(
                    Modifier
                        .width(240.dp)
                        .clip(cardShape)
                        .background(colors.bgSurface)
                        .border(1.dp, colors.strokeSubtle, cardShape)
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    EqualText(text = t.quote, style = type.body1, color = colors.textSecondary)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        t.avatar?.invoke()
                        EqualText(text = t.name, style = type.body2, color = colors.textPrimary, maxLines = 1)
                    }
                }
            }
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .clearAndSetSemantics {},
            horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
        ) {
            items.indices.forEach { i ->
                Box(
                    Modifier
                        .size(6.dp)
                        .background(if (i == page) colors.textGold else colors.strokeMedium, CircleShape),
                )
            }
        }
    }
}

/** The headline stat in [SocialProof]: [value] (e.g. 400k+) over [label]. */
@Immutable
data class SocialProofStat(val value: String, val label: String)

/**
 * Social proof on the plan page (web `SocialProof`), on the gold-washed panel: radius 28 on the
 * bottom corners, `bg/surface` → `paywall/card-bg` (30%) vertical wash, padding 24 / 16 / 20,
 * gap 16. "Why people choose" [DividerLabel] over "EQUAL UNLIMITED" (`title` Bold `text/gold`,
 * gap 2), the [StatBlock] ([stat]), the [RatingPill] ([rating]) and the [TestimonialCarousel]
 * ([testimonials], full bleed).
 */
@Composable
fun SocialProof(
    testimonials: List<Testimonial>,
    modifier: Modifier = Modifier,
    stat: SocialProofStat = SocialProofStat("400k+", "People never miss a call"),
    rating: Float = 4.5f,
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    Column(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
            .background(
                Brush.verticalGradient(
                    0f to colors.bgSurface,
                    0.3f to colors.paywallCardBg,
                    1f to colors.paywallCardBg,
                ),
            )
            .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            DividerLabel(text = "Why people choose")
            EqualText(text = "EQUAL UNLIMITED", style = type.title.copy(fontWeight = FontWeight.Bold), color = colors.textGold, maxLines = 1)
        }
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            StatBlock(value = stat.value, label = stat.label)
        }
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { RatingPill(rating = rating) }
        TestimonialCarousel(items = testimonials, modifier = Modifier.bleedHorizontal(16.dp))
    }
}

/** Pulls [this] out to the full width of a parent with [inset] horizontal padding (web `margin: 0 -16px`). */
private fun Modifier.bleedHorizontal(inset: Dp): Modifier = layout { measurable, constraints ->
    val extra = (inset * 2).roundToPx()
    val width = constraints.maxWidth + extra
    val placeable = measurable.measure(constraints.copy(minWidth = width, maxWidth = width))
    layout(constraints.maxWidth, placeable.height) { placeable.place(-inset.roundToPx(), 0) }
}

// ---------------------------------------------------------------------------------------------
// Cancellation
// ---------------------------------------------------------------------------------------------

/** [CancelSheet] step. */
enum class CancelStep { Feedback, Confirm, Submitted }

/**
 * Cancellation sheet ([StatusSheet] + neutral [StatusCard]), three steps:
 * - [CancelStep.Feedback]: sad face, "We’re Sad to / See you Cancel", a 3-line reason field
 *   (radius 12, 1dp `stroke/subtle` → `action/stroke/unread` focused, `bg/surface`, padding
 *   12 / 14, `body1`); block Proceed stays disabled until there is text → [onProceed].
 * - [CancelStep.Confirm]: "Are you really / Sure?" + [accessUntil] copy; Cancel (outline, [onKeep])
 *   and Confirm ([onConfirm]), `lg`, equal widths, gap 12.
 * - [CancelStep.Submitted]: thumbs up, "Your Request is / Submitted", 3s redirect to home.
 * The reason is controlled when [onReasonChange] is set ([reason] is the value); otherwise the
 * sheet keeps it itself (seeded from [reason]).
 */
@Composable
fun CancelSheet(
    step: CancelStep,
    modifier: Modifier = Modifier,
    accessUntil: String = "15 June 2026",
    reason: String? = null,
    onReasonChange: ((String) -> Unit)? = null,
    onProceed: () -> Unit = {},
    onKeep: () -> Unit = {},
    onConfirm: () -> Unit = {},
    onRedirect: () -> Unit = {},
    inline: Boolean = false,
) {
    var local by rememberSaveable { mutableStateOf(reason ?: "") }
    val value = if (onReasonChange != null) reason ?: "" else local
    val set: (String) -> Unit = onReasonChange ?: { local = it }
    when (step) {
        CancelStep.Feedback -> StatusSheet(
            modifier = modifier,
            inline = inline,
            label = "Cancel subscription",
            footer = {
                EqualButton(text = "Proceed", onClick = onProceed, size = EqualButtonSize.Block, enabled = value.isNotBlank())
            },
        ) {
            StatusCard(
                tone = StatusTone.Neutral,
                art = EqualAssets.EmojiSad,
                light = "We’re Sad to",
                strong = "See you Cancel",
                strongFirst = false,
                body = "Tell us why you’re Cancelling Subscription. Your feedback helps us improve.",
            ) {
                CancelReasonField(value = value, onValueChange = set)
            }
        }
        CancelStep.Confirm -> StatusSheet(
            modifier = modifier,
            inline = inline,
            label = "Confirm cancellation",
            footer = {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    EqualButton(text = "Cancel", onClick = onKeep, variant = EqualButtonVariant.Outline, size = EqualButtonSize.Lg, modifier = Modifier.weight(1f))
                    EqualButton(text = "Confirm", onClick = onConfirm, size = EqualButtonSize.Lg, modifier = Modifier.weight(1f))
                }
            },
        ) {
            StatusCard(
                tone = StatusTone.Neutral,
                art = EqualAssets.EmojiSad,
                light = "Are you really",
                strong = "Sure?",
                strongFirst = false,
                body = "Your subscription stays active & have full access until $accessUntil. We’ll stop the autopay, and your account will switch to free after that date.",
            )
        }
        CancelStep.Submitted -> StatusSheet(
            modifier = modifier,
            inline = inline,
            label = "Request submitted",
            footer = { RedirectNote(seconds = 3, to = "home", onDone = onRedirect) },
        ) {
            StatusCard(
                tone = StatusTone.Neutral,
                art = EqualAssets.ThumbsUp,
                light = "Your Request is",
                strong = "Submitted",
                strongFirst = false,
                body = "No more charges from us. You’ll keep full access until $accessUntil.",
            )
        }
    }
}

@Composable
private fun CancelReasonField(value: String, onValueChange: (String) -> Unit) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    val interaction = rememberInteraction()
    val focused by interaction.collectIsFocusedAsState()
    val shape = RoundedCornerShape(12.dp)
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.bgSurface)
            .border(1.dp, if (focused) colors.actionStrokeUnread else colors.strokeSubtle, shape)
            .semantics { contentDescription = "Why are you cancelling?" }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        textStyle = type.body1.copy(color = colors.textPrimary),
        cursorBrush = SolidColor(colors.textAction),
        minLines = 3,
        interactionSource = interaction,
        decorationBox = { inner ->
            Box {
                if (value.isEmpty()) {
                    EqualText(text = "Tell us more in detail..", style = type.body1, color = colors.textPlaceholder)
                }
                inner()
            }
        },
    )
}

// ---------------------------------------------------------------------------------------------
// Previews
// ---------------------------------------------------------------------------------------------

@EqualPreviews
@Composable
private fun PaywallPreview() {
    PreviewFrame(padding = 0.dp) {
        PaywallHero {
            PriceTag(price = "249")
            CouponTicket(code = "EQUAL25", description = "Save Flat ₹100 with this Code", discount = "25% OFF")
        }
        PaywallFooter(onClick = {})
    }
}

@EqualPreviews
@Composable
private fun PriceDropPreview() {
    PreviewFrame {
        PriceTag(price = "149", was = 249, badge = "Special Coupon Applied!")
        VSpace(16.dp)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            DiscountBadge()
            DiscountBadge(label = "Special Coupon Applied!")
        }
    }
}

@EqualPreviews
@Composable
private fun StatusSunburstPreview() {
    PreviewFrame {
        PaymentStatusSheet(status = PaymentStatus.Confirmed, inline = true)
        VSpace(16.dp)
        PaymentStatusSheet(status = PaymentStatus.Waiting, inline = true, progress = false)
    }
}

@EqualPreviews
@Composable
private fun StatusSheetsPreview() {
    PreviewFrame {
        PaymentStatusSheet(status = PaymentStatus.Failed, inline = true)
        VSpace(16.dp)
        CancelSheet(step = CancelStep.Confirm, inline = true)
    }
}

@EqualPreviews
@Composable
private fun FaqEntryPreview() {
    PreviewFrame {
        FaqEntry(item = FaqItem("Can I cancel my Equal Unlimited plan later?", "Yes, cancel anytime."), open = true, onToggle = {})
        FaqEntry(item = FaqItem("Will Equal screen all types of calls?", "Yes, Equal handles them all."), open = false, onToggle = {})
    }
}

@EqualPreviews
@Composable
private fun PaywallFooterVariantsPreview() {
    PreviewFrame(padding = 0.dp) {
        PaywallFooter(onClick = {}, cta = "Cancel Subscription", variant = EqualButtonVariant.GoldOutline)
        VSpace(16.dp)
        PaywallFooter(onClick = {}, disabled = true, above = { ProcessingStrip(variant = ProcessingStripVariant.Toast) })
        VSpace(16.dp)
        PaywallFooter(onClick = {}, flat = true)
    }
}

@EqualPreviews
@Composable
private fun SocialProofPreview() {
    PreviewFrame(padding = 0.dp) {
        SocialProof(
            testimonials = listOf(
                Testimonial("It is a very useful app for screening the unknown calls.", "Arnav Raj", avatar = {
                    EqualAssetImage(EqualAssets.AvatarTestimonial1, 36.dp)
                }),
                Testimonial("My parents get loan calls all day. Equal answers them.", "Riya Kapoor", avatar = {
                    EqualAssetImage(EqualAssets.AvatarTestimonial2, 36.dp)
                }),
            ),
        )
    }
}

@EqualPreviews
@Composable
private fun HelpCenterPartsPreview() {
    PreviewFrame {
        SheetHeading(title = "Help Center", body = "At the moment we can help you in following ways, feel free to contact us.")
        VSpace(16.dp)
        HelpContactRow(title = "Call Us", value = "+91 9876 543210", icon = EqualIcons.Call)
        VSpace(12.dp)
        OffSeal(off = "25%", size = 56.dp)
    }
}
