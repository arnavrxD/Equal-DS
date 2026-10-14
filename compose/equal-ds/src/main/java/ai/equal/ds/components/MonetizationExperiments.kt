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
import ai.equal.ds.theme.tabular
import ai.equal.ds.theme.toBrush
import ai.equal.ds.tokens.EqualGradientSpec
import ai.equal.ds.tokens.EqualMotion
import ai.equal.ds.tokens.EqualPrimitives
import ai.equal.ds.tokens.EqualRadius
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

// Monetization experiments (Plan Expired / Usage Cap touchpoints, Recordings + Actions locked, All
// Payment States, Payment Pending, the sheet paywall and the exit offer). Port of
// web/src/components/MonetizationExperiments.tsx. These sit beside the original paywall components
// in Monetization.kt; the Android team A/B tests one against another. DM Sans only (400/500/700).

// ---------------------------------------------------------------------------------------------
// Shared
// ---------------------------------------------------------------------------------------------

/**
 * Concentric rings faded out radially (web `repeating-radial-gradient` under a radial mask): 2dp
 * [color] strokes at [firstRadius] then every [spacing] around the horizontal centre and [centerY]
 * from the top (or [centerYFraction] of the height),
 * fully drawn up to [solidUntil] from the centre and gone by [goneAt]. Decorative.
 */
@Composable
private fun RadialRings(
    modifier: Modifier,
    color: Color,
    firstRadius: Dp,
    spacing: Dp,
    solidUntil: Dp,
    goneAt: Dp,
    strokeWidth: Dp = 2.dp,
    opacity: Float = 1f,
    centerY: Dp? = null,
    centerYFraction: Float = 0.5f,
) {
    Box(
        modifier
            .clearAndSetSemantics {}
            .graphicsLayer(alpha = opacity, compositingStrategy = CompositingStrategy.Offscreen)
            .drawWithCache {
                val c = Offset(size.width / 2f, centerY?.toPx() ?: (size.height * centerYFraction))
                val first = firstRadius.toPx()
                val step = spacing.toPx()
                val solid = solidUntil.toPx()
                val gone = goneAt.toPx()
                val stroke = Stroke(width = strokeWidth.toPx())
                onDrawBehind {
                    var r = first
                    while (r < gone) {
                        val fade = if (r <= solid) 1f else 1f - (r - solid) / (gone - solid)
                        drawCircle(color = color.copy(alpha = color.alpha * fade), radius = r, center = c, style = stroke)
                        r += step
                    }
                }
            },
    )
}

/** Sheet / dialog entrance: slide up 40% + fade over `motion.slow` (none under reduced motion). */
@Composable
private fun SheetLayer(
    modifier: Modifier,
    scrim: Color,
    onScrim: () -> Unit,
    content: @Composable () -> Unit,
) {
    val still = rememberReduceMotion()
    val visible = remember { MutableTransitionState(still).apply { targetState = true } }
    Box(modifier.fillMaxSize()) {
        Box(
            Modifier
                .matchParentSize()
                .background(scrim)
                .clickable(interactionSource = rememberInteraction(), indication = null, onClickLabel = "Close", onClick = onScrim),
        )
        AnimatedVisibility(
            visibleState = visible,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = slideInVertically(tween(EqualMotion.slow, easing = EqualEasing.standard)) { (it * 0.4f).toInt() } +
                fadeIn(tween(EqualMotion.slow, easing = EqualEasing.standard)),
        ) {
            content()
        }
    }
}

private val PaywallSheetShadow = EqualShadow(listOf(EqualShadowLayer(0.dp, (-8).dp, 30.dp, EqualPrimitives.black.copy(alpha = 0.14f))))
private val ResultChromeShadow = EqualShadow(listOf(EqualShadowLayer(0.dp, 2.dp, 8.dp, EqualPrimitives.black.copy(alpha = 0.12f))))
private val ToastShadow = EqualShadow(listOf(EqualShadowLayer(0.dp, 4.dp, 14.dp, EqualPrimitives.black.copy(alpha = 0.12f))))
private val ExitShadow = EqualShadow(listOf(EqualShadowLayer(0.dp, 12.dp, 40.dp, EqualPrimitives.black.copy(alpha = 0.22f))))
private val NoticeShadow = EqualShadow(listOf(EqualShadowLayer(0.dp, 1.dp, 2.dp, EqualPrimitives.black.copy(alpha = 0.06f))))
private val LockBadgeShadow = EqualShadow(listOf(EqualShadowLayer(0.dp, 4.dp, 12.dp, Color(0x598C5817))))

/** "m:ss" for a duration in seconds. */
private fun mmss(seconds: Int): String = "${seconds / 60}:${(seconds % 60).toString().padStart(2, '0')}"

// ---------------------------------------------------------------------------------------------
// Payment result pages
// ---------------------------------------------------------------------------------------------

/** Which full-screen [PaymentResult] page. */
enum class PaymentResultStatus { Success, Pending, Failed }

/** The [PaymentResultStatus.Pending] CTA. */
enum class PendingAction {
    /** Refresh Status (default). */
    Refresh,

    /** Contact Support, after the user closed Payment Pending once. */
    Support,
}

/**
 * Full-screen payment result (All Payment States): `gradient/result-success|pending|failed` wash
 * with soft 2dp `result/ring` rings (centre 52% down, first at 61dp then every 96dp, faded out from
 * 155dp to 467dp), [header] (the status bar) on the wash, a 52dp chrome bar (Pending: close ×,
 * Failed: back ‹; 44dp `bg/surface` circles with a soft shadow), then centred: the 84dp 3D seal,
 * the title 30/36 Bold (-0.02em, 12 above), Success's "You’re Unlimited" 22/28 Regular, one line
 * of help (`body1` `text/secondary`, max 280 wide, 8 above). Success adds "PLAN ACTIVE TILL"
 * between hairlines and [activeTill] (`subheading` Bold) and has no chrome or footer; it leads on
 * to home. Footer (padding 0 / 16 / 24): Pending → block Refresh Status (refresh icon) or Contact
 * Support ([pendingAction]); Failed → block Retry payment + the Contact Support text button.
 * Fills its parent.
 */
@Composable
fun PaymentResult(
    status: PaymentResultStatus,
    modifier: Modifier = Modifier,
    /** Success: the "Plan active till" date. */
    activeTill: String = "10th Sept 2026",
    pendingAction: PendingAction = PendingAction.Refresh,
    onClose: () -> Unit = {},
    onBack: () -> Unit = {},
    onRefresh: () -> Unit = {},
    onRetry: () -> Unit = {},
    onContactSupport: () -> Unit = {},
    /** Status bar drawn on the wash. */
    header: (@Composable () -> Unit)? = null,
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    val gradients = EqualTheme.gradients
    data class Spec(val wash: EqualGradientSpec, val art: EqualAssets, val title: String, val sub: String?, val body: String)
    val r = when (status) {
        PaymentResultStatus.Success -> Spec(gradients.resultSuccess, EqualAssets.StatusConfirmed, "Congratulations", "You’re Unlimited", "No more limits. Everything Equal AI can do, now yours.")
        PaymentResultStatus.Pending -> Spec(gradients.resultPending, EqualAssets.StatusPending, "Payment Pending", null, "We’re confirming your UPI payment. This may take a few moments.")
        PaymentResultStatus.Failed -> Spec(gradients.resultFailed, EqualAssets.StatusFailed, "Payment Failed", null, "Many banks block UPI Autopay by default. A different UPI app, linked to another bank, usually works.")
    }
    Box(
        modifier
            .fillMaxSize()
            .background(r.wash.toBrush())
            .semantics { paneTitle = r.title },
    ) {
        RadialRings(
            modifier = Modifier.matchParentSize(),
            color = colors.resultRing,
            centerYFraction = 0.52f,
            firstRadius = 61.dp,
            spacing = 96.dp,
            solidUntil = 155.dp,
            goneAt = 467.dp,
        )
        Column(Modifier.fillMaxSize()) {
            header?.invoke()
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp),
            ) {
                if (status != PaymentResultStatus.Success) {
                    val pending = status == PaymentResultStatus.Pending
                    Box(
                        Modifier
                            .size(44.dp)
                            .eqShadow(ResultChromeShadow, CircleShape)
                            .clip(CircleShape)
                            .background(colors.bgSurface)
                            .clickable(role = Role.Button, onClickLabel = if (pending) "Close" else "Back", onClick = if (pending) onClose else onBack),
                        contentAlignment = Alignment.Center,
                    ) {
                        EqualIcon(
                            if (pending) EqualIcons.Close else EqualIcons.Back,
                            contentDescription = if (pending) "Close" else "Back",
                            tint = colors.textSecondary,
                            size = 20.dp,
                        )
                    }
                }
            }
            Column(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(start = 28.dp, end = 28.dp, top = 40.dp, bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                EqualAssetImage(r.art, size = 84.dp)
                Spacer(Modifier.height(12.dp))
                EqualText(
                    text = r.title,
                    style = type.headline.copy(fontSize = 30.sp, lineHeight = 36.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.02).em),
                    color = colors.textPrimary,
                    textAlign = TextAlign.Center,
                )
                if (r.sub != null) {
                    EqualText(
                        text = r.sub,
                        style = type.title.copy(fontSize = 22.sp, lineHeight = 28.sp, fontWeight = FontWeight.W400),
                        color = colors.textPrimary,
                        textAlign = TextAlign.Center,
                    )
                }
                Spacer(Modifier.height(8.dp))
                EqualText(
                    text = r.body,
                    style = type.body1.copy(lineHeight = 20.sp),
                    color = colors.textSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.widthIn(max = 280.dp),
                )
                if (status == PaymentResultStatus.Success) {
                    Spacer(Modifier.height(20.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Box(Modifier.weight(1f).height(1.dp).background(colors.paywallDivider))
                        EqualText(text = "PLAN ACTIVE TILL", style = type.captionRegular, color = colors.textSecondary, maxLines = 1)
                        Box(Modifier.weight(1f).height(1.dp).background(colors.paywallDivider))
                    }
                    Spacer(Modifier.height(4.dp))
                    EqualText(
                        text = activeTill,
                        style = type.subheading.copy(fontWeight = FontWeight.Bold),
                        color = colors.textPrimary,
                        textAlign = TextAlign.Center,
                    )
                }
            }
            if (status != PaymentResultStatus.Success) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    when {
                        status == PaymentResultStatus.Pending && pendingAction == PendingAction.Refresh ->
                            EqualButton(text = "Refresh Status", onClick = onRefresh, size = EqualButtonSize.Block, leadingIcon = EqualIcons.Refresh)
                        status == PaymentResultStatus.Pending ->
                            EqualButton(text = "Contact Support", onClick = onContactSupport, size = EqualButtonSize.Block)
                        else -> {
                            EqualButton(text = "Retry payment", onClick = onRetry, size = EqualButtonSize.Block)
                            Spacer(Modifier.height(12.dp))
                            EqualTextButton(text = "Contact Support", onClick = onContactSupport)
                        }
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Payment processing
// ---------------------------------------------------------------------------------------------

/** Where a [ProcessingStrip] sits. */
enum class ProcessingStripVariant {
    /** Full-width strip under a chat header. */
    Strip,

    /** Rounded card inside the home header, under the toggle (margin 0 12 12). */
    Card,

    /** Rounded toast with a shadow, above the disabled paywall CTA. */
    Toast,
}

/**
 * "Payment processing · We’ll update your order once it’s confirmed.": `processing/bg`, padding
 * 8 / 12, gap 10; a 30dp ring (3dp `processing/ring` at 22% with a 90° arc in full colour spinning
 * once every 1.1s, linear; still under reduced motion) around an 18dp `processing/ring` info dot
 * ("i" 12/18 Bold in `bg/surface`); [title] `body2` Medium over [body] `caption` Regular
 * `text/secondary`. [variant]: strip, card or toast. A polite live region.
 */
@Composable
fun ProcessingStrip(
    modifier: Modifier = Modifier,
    variant: ProcessingStripVariant = ProcessingStripVariant.Strip,
    title: String = "Payment processing",
    body: String = "We’ll update your order once it’s confirmed.",
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    val shape = if (variant == ProcessingStripVariant.Strip) RoundedCornerShape(0.dp) else RoundedCornerShape(12.dp)
    val outer = when (variant) {
        ProcessingStripVariant.Card -> Modifier.padding(start = 12.dp, end = 12.dp, bottom = 12.dp)
        else -> Modifier
    }
    val shadow = if (variant == ProcessingStripVariant.Toast) Modifier.eqShadow(ToastShadow, shape) else Modifier
    Row(
        modifier
            .then(outer)
            .fillMaxWidth()
            .then(shadow)
            .clip(shape)
            .background(colors.processingBg)
            .semantics(mergeDescendants = true) { liveRegion = LiveRegionMode.Polite }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        ProcessingRing()
        Column(Modifier.weight(1f)) {
            EqualText(text = title, style = type.body2.copy(fontWeight = FontWeight.W500), color = colors.textPrimary)
            EqualText(text = body, style = type.captionRegular, color = colors.textSecondary)
        }
    }
}

@Composable
private fun ProcessingRing() {
    val colors = EqualTheme.colors
    val ring = colors.processingRing
    val turn: State<Float>? = if (!rememberReduceMotion()) {
        rememberInfiniteTransition(label = "processing").animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(tween(1100, easing = LinearEasing), RepeatMode.Restart),
            label = "processing-turn",
        )
    } else {
        null
    }
    Box(Modifier.size(30.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.matchParentSize()) {
            val w = 3.dp.toPx()
            val inset = w / 2f
            val arcSize = Size(size.width - w, size.height - w)
            drawCircle(color = ring.copy(alpha = 0.22f), radius = (size.minDimension - w) / 2f, style = Stroke(w))
            drawArc(
                color = ring,
                startAngle = -135f + (turn?.value ?: 0f),
                sweepAngle = 90f,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = arcSize,
                style = Stroke(w),
            )
        }
        Box(
            Modifier
                .size(18.dp)
                .background(ring, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            EqualText(
                text = "i",
                style = EqualTheme.type.caption.copy(fontSize = 12.sp, lineHeight = 18.sp, fontWeight = FontWeight.Bold),
                color = colors.bgSurface,
                maxLines = 1,
            )
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Sheet paywall
// ---------------------------------------------------------------------------------------------

/**
 * The paywall as a bottom sheet over the screen the user was on: `bg/surface` panel (radius 28
 * top, shadow 0 -8 30 black/14%, 12 top) holding a compact gold card (inset 12, radius 20,
 * `paywall-hero` gradient with soft white rings (web `.eq-paywall-sheet__rings`), 1dp `paywall/card-stroke`, padding 20 / 16 / 16): Support (top right), [AnimatedCrown] 56, "Equal AI" 22/26 Regular over "Unlimited"
 * 34/38 Bold (-0.03em), [body] (`caption` Regular `text/secondary`), "🎊 LIMITED TIME DISCOUNT 🎊"
 * (`micro` Medium `text/gold`, 14 above), the gold price between fading hairlines (₹[price], or
 * ₹[discountedPrice] once [applied], landing with fade + rise + scale over `motion.priceDrop`)
 * and the [CouponRow]. Footer: a flat [PaywallFooter] (the gold block [cta] + "Cancel Anytime.
 * Automatically renews"). [processing] (a payment in flight): the footer is disabled ("Unlock
 * Unlimited") with the toast [ProcessingStrip] above it.
 *
 * Overlay (default): fills its parent with the `scrim` (tap → [onScrim]: the exit offer) and
 * slides the panel up. [inline] draws only the panel, radius 28 all round (docs). Support opens
 * the [HelpFaqSheet].
 */
@Composable
fun PaywallSheet(
    modifier: Modifier = Modifier,
    price: Int = 249,
    discountedPrice: Int = 149,
    /** Coupon state: EQUAL25 offered (APPLY) or applied. */
    applied: Boolean = false,
    onApply: () -> Unit = {},
    onSupport: () -> Unit = {},
    onUnlock: () -> Unit = {},
    onScrim: () -> Unit = {},
    cta: String = "Unlock Equal AI Unlimited",
    body: String = "Get Unlimited Recordings, AI Call Summaries & Conversations. Trusted by 10 Lakh+ Indians.",
    /** Payment already in flight: toast above a disabled CTA. */
    processing: Boolean = false,
    inline: Boolean = false,
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    val panelShape = if (inline) RoundedCornerShape(28.dp) else RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    val panel: @Composable (Modifier) -> Unit = { panelModifier ->
        Column(
            panelModifier
                .fillMaxWidth()
                .eqShadow(PaywallSheetShadow, panelShape)
                .clip(panelShape)
                .background(colors.bgSurface)
                .semantics { paneTitle = "Equal AI Unlimited" }
                .padding(top = 12.dp),
        ) {
            val cardShape = RoundedCornerShape(20.dp)
            Box(
                Modifier
                    .padding(horizontal = 12.dp)
                    .fillMaxWidth()
                    .clip(cardShape)
                    .background(EqualTheme.gradients.paywallHero.toBrush())
                    .border(1.dp, colors.paywallCardStroke, cardShape),
            ) {
                // web .eq-paywall-sheet__rings: 520px box centred 70px from the top, radial mask
                // #000 20% → transparent 60% of its farthest corner (≈ 73.5 / 220.6dp); 30% in dark.
                RadialRings(
                    modifier = Modifier.matchParentSize(),
                    color = Color.White.copy(alpha = 0.55f),
                    firstRadius = 47.5.dp,
                    spacing = 70.dp,
                    solidUntil = 73.5.dp,
                    goneAt = 220.6.dp,
                    strokeWidth = 1.dp,
                    opacity = if (colors.isDark) 0.3f else 1f,
                    centerY = 70.dp,
                )
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    AnimatedCrown(size = 56.dp)
                    Spacer(Modifier.height(8.dp))
                    EqualText(
                        text = "Equal AI",
                        style = type.title.copy(fontSize = 22.sp, lineHeight = 26.sp, fontWeight = FontWeight.W400),
                        color = colors.textPrimary,
                        textAlign = TextAlign.Center,
                    )
                    EqualText(
                        text = "Unlimited",
                        style = type.headline.copy(fontSize = 34.sp, lineHeight = 38.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.03).em),
                        color = colors.textPrimary,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(8.dp))
                    EqualText(
                        text = body,
                        style = type.captionRegular,
                        color = colors.textSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp),
                    )
                    Spacer(Modifier.height(14.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        EmojiGlyph(emoji = "🎊", size = 10.dp)
                        EqualText(text = "LIMITED TIME DISCOUNT", style = type.micro.copy(fontWeight = FontWeight.W500), color = colors.textGold, maxLines = 1)
                        EmojiGlyph(emoji = "🎊", size = 10.dp)
                    }
                    Spacer(Modifier.height(4.dp))
                    SheetPrice(text = "₹${if (applied) discountedPrice else price}", land = applied)
                    Spacer(Modifier.height(12.dp))
                    CouponRow(applied = applied, onApply = onApply)
                }
                // Support, top right of the card.
                Row(
                    Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 8.dp, end = 8.dp)
                        .minimumInteractiveComponentSize()
                        .clip(CircleShape)
                        .clickable(role = Role.Button, onClick = onSupport)
                        .padding(horizontal = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    EqualIcon(EqualIcons.HelpCircle, contentDescription = null, tint = colors.textPrimary, size = 18.dp)
                    EqualText(text = "Support", style = type.body2, color = colors.textPrimary, maxLines = 1)
                }
            }
            PaywallFooter(
                onClick = onUnlock,
                cta = if (processing) "Unlock Unlimited" else cta,
                disabled = processing,
                flat = true,
                above = if (processing) ({ ProcessingStrip(variant = ProcessingStripVariant.Toast) }) else null,
            )
        }
    }
    if (inline) {
        panel(modifier)
    } else {
        SheetLayer(modifier, scrim = colors.scrim, onScrim = onScrim) { panel(Modifier) }
    }
}

/** The sheet price between two fading hairlines; [land] animates it in (fade + 6dp rise + 0.96 → 1). */
@Composable
private fun SheetPrice(text: String, land: Boolean) {
    val colors = EqualTheme.colors
    val run = land && !rememberReduceMotion()
    key(text) {
        val progress = remember { Animatable(if (run) 0f else 1f) }
        LaunchedEffect(Unit) { if (run) progress.animateTo(1f, tween(EqualMotion.priceDrop, easing = EqualEasing.standard)) }
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(Modifier.weight(1f).height(1.dp).background(Brush.horizontalGradient(listOf(Color.Transparent, colors.paywallDivider))))
            GoldText(
                text = text,
                style = EqualTheme.type.headline.copy(fontSize = 34.sp, lineHeight = 40.sp).tabular(),
                modifier = Modifier
                    .semantics { liveRegion = LiveRegionMode.Polite }
                    .graphicsLayer {
                        val p = progress.value
                        alpha = p
                        translationY = 6.dp.toPx() * (1f - p)
                        scaleX = 0.96f + 0.04f * p
                        scaleY = 0.96f + 0.04f * p
                    },
            )
            Box(Modifier.weight(1f).height(1.dp).background(Brush.horizontalGradient(listOf(colors.paywallDivider, Color.Transparent))))
        }
    }
}

/**
 * Compact coupon row: radius 14, `gradient/coupon`, 1dp `paywall/card-stroke`, padding 10 / 14,
 * gap 10; [code] (`subheading` Bold `text/gold`) over [description] (`caption` Regular), and APPLY
 * (`body2` Bold `text/gold`, a pill tinted gold 18% in dark) or APPLIED once [applied].
 */
@Composable
fun CouponRow(
    modifier: Modifier = Modifier,
    code: String = "EQUAL25",
    description: String = "Save Flat 25% with UPI Payment",
    applied: Boolean = false,
    onApply: () -> Unit = {},
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    val shape = RoundedCornerShape(14.dp)
    val state = type.body2.copy(fontWeight = FontWeight.Bold)
    Row(
        modifier
            .fillMaxWidth()
            .clip(shape)
            .background(EqualTheme.gradients.coupon.toBrush())
            .border(1.dp, colors.paywallCardStroke, shape)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Column(Modifier.weight(1f)) {
            EqualText(text = code, style = type.subheading.copy(fontWeight = FontWeight.Bold), color = colors.textGold, maxLines = 1)
            EqualText(text = description, style = type.captionRegular, color = colors.textPrimary)
        }
        if (applied) {
            EqualText(
                text = "APPLIED",
                style = state,
                color = colors.textGold,
                maxLines = 1,
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
            )
        } else {
            EqualText(
                text = "APPLY",
                style = state,
                color = colors.textGold,
                maxLines = 1,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .clip(CircleShape)
                    .background(if (colors.isDark) colors.textGold.copy(alpha = 0.18f) else Color.Transparent)
                    .clickable(role = Role.Button, onClick = onApply)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            )
        }
    }
}

/**
 * Help Center as a sheet (from the [PaywallSheet]'s Support): `sheet/bg` panel, radius 28 top;
 * back + "Help Center" (`title` Medium), the intro (`body1` `text/secondary`, margin 4 16 12), then
 * the [FaqList] (no heading, 4th open; padding 0 16 20). Overlay with the `scrim` ([onScrim]) by
 * default; [inline] draws only the panel (radius 28 all round).
 */
@Composable
fun HelpFaqSheet(
    items: List<FaqItem>,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onScrim: () -> Unit = {},
    inline: Boolean = false,
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    val shape = if (inline) RoundedCornerShape(28.dp) else RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    val panel: @Composable (Modifier) -> Unit = { panelModifier ->
        Column(
            panelModifier
                .fillMaxWidth()
                .eqShadow(PaywallSheetShadow, shape)
                .clip(shape)
                .background(colors.sheetBg)
                .semantics { paneTitle = "Help Center" },
        ) {
            Row(
                Modifier.padding(start = 4.dp, end = 12.dp, top = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                PlainIconButton(EqualIcons.Back, "Back", onBack, tint = colors.textPrimary)
                EqualText(text = "Help Center", style = type.title.copy(fontWeight = FontWeight.W500), color = colors.textPrimary)
            }
            EqualText(
                text = "Check out this section to get answers for all the frequently asked questions",
                style = type.body1,
                color = colors.textSecondary,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 12.dp),
            )
            FaqList(
                items = items,
                title = null,
                defaultOpen = listOf(3),
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 20.dp),
            )
        }
    }
    if (inline) {
        panel(modifier)
    } else {
        SheetLayer(modifier, scrim = colors.scrim, onScrim = onScrim) { panel(Modifier) }
    }
}

/**
 * Exit offer when the user leaves the paywall: "Leave Without Unlocking?" with the coupon as the
 * commit. Card: radius 20, `bg/surface`, shadow 0 12 40 black/22%, padding 18 / 16 / 16; [title]
 * (`title` Medium), [body] (`body1` `text/secondary`, 4 above, 16 below), then two equal buttons
 * (gap 10): [dismissLabel] gold outline and [claimLabel] gold. The labels are an experiment (Not
 * Now / Claim 25% Off on home; Leave / Keep 25% Off in a chat).
 *
 * Overlay (default): centred over a black 55% scrim (padding 16), springing in (fade + 0.92 → 1
 * over `motion.couponSpring`; still under reduced motion). [inline] draws only the card.
 */
@Composable
fun ExitOfferDialog(
    modifier: Modifier = Modifier,
    title: String = "Leave Without Unlocking?",
    body: String = "Claim 25% off with EQUAL25 first, go Unlimited for ₹149/month.",
    dismissLabel: String = "Not Now",
    claimLabel: String = "Claim 25% Off",
    onDismiss: () -> Unit = {},
    onClaim: () -> Unit = {},
    inline: Boolean = false,
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    val shape = RoundedCornerShape(20.dp)
    val card: @Composable (Modifier) -> Unit = { cardModifier ->
        Column(
            cardModifier
                .fillMaxWidth()
                .eqShadow(ExitShadow, shape)
                .clip(shape)
                .background(colors.bgSurface)
                .semantics { paneTitle = title }
                .padding(start = 16.dp, end = 16.dp, top = 18.dp, bottom = 16.dp),
        ) {
            EqualText(text = title, style = type.title.copy(fontWeight = FontWeight.W500), color = colors.textPrimary)
            Spacer(Modifier.height(4.dp))
            EqualText(text = body, style = type.body1, color = colors.textSecondary)
            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                EqualButton(text = dismissLabel, onClick = onDismiss, variant = EqualButtonVariant.GoldOutline, modifier = Modifier.weight(1f))
                EqualButton(text = claimLabel, onClick = onClaim, variant = EqualButtonVariant.Gold, modifier = Modifier.weight(1f))
            }
        }
    }
    if (inline) {
        card(modifier)
        return
    }
    val still = rememberReduceMotion()
    val enter = remember { Animatable(if (still) 1f else 0f) }
    LaunchedEffect(Unit) { if (!still) enter.animateTo(1f, tween(EqualMotion.couponSpring, easing = EqualEasing.emphasized)) }
    Box(
        modifier
            .fillMaxSize()
            .background(EqualPrimitives.black.copy(alpha = 0.55f))
            .clickable(interactionSource = rememberInteraction(), indication = null, onClick = {})
            .padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        card(
            Modifier.graphicsLayer {
                val p = enter.value
                alpha = p.coerceIn(0f, 1f)
                scaleX = 0.92f + 0.08f * p
                scaleY = 0.92f + 0.08f * p
            },
        )
    }
}

/**
 * Home strip after the user declined the exit offer: info-fill 16 + [text] (`body2` Medium) + the
 * underlined [cta] (`body2` Bold), all `offer-strip/text` on `offer-strip/bg`, padding 8 / 12, gap 8.
 */
@Composable
fun OfferStrip(
    modifier: Modifier = Modifier,
    text: String = "Go unlimited for ₹149 with EQUAL25",
    cta: String = "Claim Now",
    onClick: () -> Unit = {},
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    Row(
        modifier
            .fillMaxWidth()
            .background(colors.offerStripBg)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        EqualIcon(EqualIcons.InfoFill, contentDescription = null, tint = colors.offerStripText, size = 16.dp)
        EqualText(text = text, style = type.body2.copy(fontWeight = FontWeight.W500), color = colors.offerStripText, modifier = Modifier.weight(1f))
        EqualText(
            text = cta,
            style = type.body2.copy(fontWeight = FontWeight.Bold, textDecoration = TextDecoration.Underline),
            color = colors.offerStripText,
            maxLines = 1,
            modifier = Modifier
                .minimumInteractiveComponentSize()
                .clickable(role = Role.Button, onClick = onClick),
        )
    }
}

// ---------------------------------------------------------------------------------------------
// Home & chat touchpoints
// ---------------------------------------------------------------------------------------------

/**
 * Home card under the header (Plan Expired option 2, Usage Cap option 2): margin 12 12 0, padding
 * 12 / 14, radius 16, `bg/surface`, 1dp `stroke/subtle`. [lead] (an emoji or icon, gap 6) +
 * [title] (`subheading`) over [subtitle] (`caption` Regular `text/secondary`; [subtitleTrailing]
 * follows a 1 × 12 `stroke/medium` separator), the `md` green [cta] on the right, and with
 * [progress] (0–1) a 6dp usage bar (`button/primary` on `stroke/subtle`, 10 above).
 */
@Composable
fun HomeNoticeCard(
    title: String,
    subtitle: String,
    cta: String,
    modifier: Modifier = Modifier,
    onCta: () -> Unit = {},
    lead: (@Composable () -> Unit)? = null,
    subtitleTrailing: String? = null,
    progress: Float? = null,
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier
            .padding(start = 12.dp, end = 12.dp, top = 12.dp)
            .fillMaxWidth()
            .clip(shape)
            .background(colors.bgSurface)
            .border(1.dp, colors.strokeSubtle, shape)
            .padding(horizontal = 14.dp, vertical = 12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    lead?.invoke()
                    EqualText(text = title, style = type.subheading, color = colors.textPrimary)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    EqualText(text = subtitle, style = type.captionRegular, color = colors.textSecondary)
                    if (subtitleTrailing != null) {
                        Box(
                            Modifier
                                .padding(horizontal = 6.dp)
                                .size(width = 1.dp, height = 12.dp)
                                .background(colors.strokeMedium),
                        )
                        EqualText(text = subtitleTrailing, style = type.captionRegular, color = colors.textSecondary)
                    }
                }
            }
            EqualButton(text = cta, onClick = onCta, size = EqualButtonSize.Md)
        }
        if (progress != null) {
            val p = progress.coerceIn(0f, 1f)
            Spacer(Modifier.height(10.dp))
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape)
                    .background(colors.strokeSubtle)
                    .semantics { contentDescription = "${(p * 100).toInt()}% used" },
            ) {
                Box(
                    Modifier
                        .fillMaxWidth(p)
                        .height(6.dp)
                        .clip(CircleShape)
                        .background(colors.buttonPrimary),
                )
            }
        }
    }
}

/**
 * Usage meter for the free weekly cap: a [HomeNoticeCard] with the filled phone, "[left] Calls
 * left this week", "Resets [resets] | Unlimited on premium", Get Unlimited ([onUpgrade]) and the
 * bar at ([limit] − [left]) / [limit].
 */
@Composable
fun UsageCapCard(
    left: Int,
    limit: Int,
    modifier: Modifier = Modifier,
    resets: String = "Mon",
    onUpgrade: () -> Unit = {},
) {
    HomeNoticeCard(
        title = "$left Calls left this week",
        subtitle = "Resets $resets",
        subtitleTrailing = "Unlimited on premium",
        cta = "Get Unlimited",
        onCta = onUpgrade,
        modifier = modifier,
        lead = { EqualIcon(EqualIcons.CallFilled, contentDescription = null, tint = EqualTheme.colors.textPrimary, size = 16.dp) },
        progress = if (limit > 0) (limit - left).toFloat() / limit else 0f,
    )
}

/**
 * A message from Equal AI in its own chat (Plan Expired option 1, Usage Cap option 1): radius 16,
 * `bg/surface`, shadow 0 1 2 black/6%, [width] wide. A voice note ([duration] seconds, padding 8),
 * then (padding 0 12 12) "Equal AI" (`body2` `text/action`), [title] (`subheading`), [body]
 * (`body1`, 4 above); a divider and the 48-high CTA row: crown-lock 18 + [cta] (`body2` Medium
 * `text/action`) → [onCta].
 */
@Composable
fun AssistantNoticeCard(
    title: String,
    body: String,
    cta: String,
    modifier: Modifier = Modifier,
    onCta: () -> Unit = {},
    duration: Int = 97,
    width: Dp = 296.dp,
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    val shape = RoundedCornerShape(16.dp)
    var playing by remember { mutableStateOf(false) }
    var speed by remember { mutableStateOf(PlaybackSpeed.X1) }
    Column(
        modifier
            .width(width)
            .eqShadow(NoticeShadow, shape)
            .clip(shape)
            .background(colors.bgSurface),
    ) {
        Box(Modifier.padding(8.dp)) {
            VoiceNotePlayer(
                isPlaying = playing,
                onPlayPauseClick = { playing = !playing },
                progress = 0f,
                elapsed = "0:00",
                total = mmss(duration),
                speed = speed,
                onSpeedChange = { speed = it },
            )
        }
        Column(Modifier.padding(start = 12.dp, end = 12.dp, bottom = 12.dp)) {
            EqualText(text = "Equal AI", style = type.body2, color = colors.textAction, maxLines = 1)
            EqualText(text = title, style = type.subheading, color = colors.textPrimary)
            Spacer(Modifier.height(4.dp))
            EqualText(text = body, style = type.body1, color = colors.textPrimary)
        }
        EqualDivider()
        Row(
            Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clickable(role = Role.Button, onClick = onCta),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            EqualAssetImage(EqualAssets.CrownLock, size = 18.dp)
            Spacer(Modifier.width(8.dp))
            EqualText(text = cta, style = type.body2.copy(fontWeight = FontWeight.W500), color = colors.textAction, maxLines = 1)
        }
    }
}

/** One row of [SummaryActions]. */
@Immutable
data class SummaryAction(val label: String, val icon: ImageVector, val onClick: () -> Unit = {})

/** The sample Call Back / Add to Calendar rows. */
val SampleSummaryActions: List<SummaryAction> = listOf(
    SummaryAction("Call Back", EqualIcons.CallFilled),
    SummaryAction("Add to Calendar", EqualIcons.CalendarMonth),
)

/**
 * Action rows under a call summary (Call Back, Add to Calendar): each 48 high, full width, a 1dp
 * `stroke/subtle` rule on top, icon 18 + label (`body2` Medium, gap 8) in `text/action`, centred.
 * [locked] (free plan) swaps each icon for the 16dp crown-lock and turns the rows `text/gold`;
 * tapping then calls [onLocked] (open the paywall).
 */
@Composable
fun SummaryActions(
    actions: List<SummaryAction>,
    modifier: Modifier = Modifier,
    locked: Boolean = false,
    onLocked: () -> Unit = {},
) {
    val colors = EqualTheme.colors
    val tint = if (locked) colors.textGold else colors.textAction
    Column(modifier.fillMaxWidth()) {
        actions.forEach { a ->
            EqualDivider()
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clickable(role = Role.Button, onClick = if (locked) onLocked else a.onClick),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                if (locked) {
                    EqualAssetImage(EqualAssets.CrownLock, size = 16.dp, contentDescription = "Locked")
                } else {
                    EqualIcon(a.icon, contentDescription = null, tint = tint, size = 18.dp)
                }
                Spacer(Modifier.width(8.dp))
                EqualText(text = a.label, style = EqualTheme.type.body2.copy(fontWeight = FontWeight.W500), color = tint, maxLines = 1)
            }
        }
    }
}

/**
 * A locked recording (recordings locked on the free plan): the voice note blurred 2.5dp on a gold
 * wash (`paywall/hero-top` 70% into `bg/surface` → `paywall/card-bg`), radius 12, with a 40dp
 * `gradient/cta-gold` badge holding the 26dp crown-lock in the middle (warm shadow). The whole
 * thing is one button → [onUnlock]. Blur needs API 31+.
 */
@Composable
fun LockedRecording(
    modifier: Modifier = Modifier,
    duration: Int = 157,
    onUnlock: () -> Unit = {},
) {
    val colors = EqualTheme.colors
    val wash = Brush.verticalGradient(listOf(lerp(colors.bgSurface, colors.paywallHeroTop, 0.7f), colors.paywallCardBg))
    Box(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(EqualRadius.rMd))
            .clickable(onClick = onUnlock)
            .clearAndSetSemantics {
                contentDescription = "Recording locked. Unlock Equal AI Unlimited"
                role = Role.Button
            },
        contentAlignment = Alignment.Center,
    ) {
        Box(Modifier.blur(2.5.dp, BlurredEdgeTreatment.Unbounded)) {
            VoiceNotePlayer(
                isPlaying = false,
                onPlayPauseClick = {},
                progress = 0f,
                elapsed = "0:00",
                total = mmss(duration),
                speed = PlaybackSpeed.X1,
                onSpeedChange = {},
                background = wash,
            )
        }
        Box(
            Modifier
                .size(40.dp)
                .eqShadow(LockBadgeShadow, CircleShape)
                .background(EqualTheme.gradients.ctaGold.toBrush(), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            EqualAssetImage(EqualAssets.CrownLock, size = 26.dp)
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Previews
// ---------------------------------------------------------------------------------------------

@EqualPreviews
@Composable
private fun PaymentResultPreview() {
    PreviewFrame(padding = 0.dp) {
        Row(Modifier.height(440.dp), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            PaymentResult(status = PaymentResultStatus.Success, modifier = Modifier.weight(1f))
            PaymentResult(status = PaymentResultStatus.Pending, modifier = Modifier.weight(1f))
            PaymentResult(status = PaymentResultStatus.Failed, modifier = Modifier.weight(1f))
        }
    }
}

@EqualPreviews
@Composable
private fun ProcessingStripPreview() {
    PreviewFrame(padding = 0.dp) {
        VSpace(12.dp)
        ProcessingStrip()
        VSpace(12.dp)
        ProcessingStrip(variant = ProcessingStripVariant.Card)
        ProcessingStrip(variant = ProcessingStripVariant.Toast, modifier = Modifier.padding(horizontal = 12.dp))
        VSpace(12.dp)
    }
}

@EqualPreviews
@Composable
private fun PaywallSheetPreview() {
    PreviewFrame {
        PaywallSheet(inline = true)
        VSpace(16.dp)
        PaywallSheet(inline = true, applied = true, processing = true)
    }
}

@EqualPreviews
@Composable
private fun ExitOfferPreview() {
    PreviewFrame(padding = 0.dp) {
        ExitOfferDialog(inline = true, modifier = Modifier.padding(16.dp))
        OfferStrip()
        VSpace(16.dp)
    }
}

@EqualPreviews
@Composable
private fun HomeCardsPreview() {
    PreviewFrame(padding = 0.dp) {
        HomeNoticeCard(
            lead = { EmojiGlyph(emoji = "👑", size = 14.dp) },
            title = "Your Plan Expired",
            subtitle = "Renew to get Unlimited access back",
            cta = "Renew Now",
        )
        UsageCapCard(left = 7, limit = 30)
        VSpace(12.dp)
    }
}

@EqualPreviews
@Composable
private fun AssistantNoticePreview() {
    PreviewFrame {
        AssistantNoticeCard(
            title = "Your Plan Expired",
            body = "Renew to unlock unlimited recordings, call summaries, transcripts, and unlimited call screening.",
            cta = "Renew Equal AI Unlimited",
        )
    }
}

@EqualPreviews
@Composable
private fun LockedRecordingPreview() {
    PreviewFrame {
        Column(
            Modifier
                .clip(RoundedCornerShape(EqualRadius.rLg))
                .background(EqualTheme.colors.bgSurface),
        ) {
            Box(Modifier.padding(8.dp)) { LockedRecording() }
            SummaryActions(actions = SampleSummaryActions, locked = true)
        }
        VSpace(12.dp)
        SummaryActions(actions = SampleSummaryActions)
        VSpace(12.dp)
        SmallActionChip(label = "Call Back", emoji = "📞", onClick = {}, locked = true)
        VSpace(12.dp)
        HelpFaqSheet(items = listOf(FaqItem("What do I get?", "Unlimited everything.")), inline = true)
    }
}
