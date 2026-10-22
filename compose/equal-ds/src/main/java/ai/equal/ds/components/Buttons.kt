package ai.equal.ds.components

import ai.equal.ds.icons.EqualIcon
import ai.equal.ds.icons.EqualIcons
import ai.equal.ds.icons.EqualSymbols
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import ai.equal.ds.theme.EqualShadows
import ai.equal.ds.theme.EqualTheme
import ai.equal.ds.theme.eqFocusRing
import ai.equal.ds.theme.eqGlassInset
import ai.equal.ds.theme.eqPressFeedback
import ai.equal.ds.theme.eqShadow
import ai.equal.ds.theme.toBrush
import ai.equal.ds.tokens.EqualPrimitives
import ai.equal.ds.tokens.EqualMotion
import ai.equal.ds.theme.EqualEasing
import ai.equal.ds.theme.cssBlurToShadowRadius
import ai.equal.ds.theme.toPath
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.animation.core.Animatable
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Filled button colour roles (SPEC §19). Disabled is expressed with `enabled = false`.
 * Monetization adds [Gold] (`gradient/cta-gold`, white Bold label: Unlock Unlimited, Re-Subscribe)
 * and [Outline] (transparent, 1.5dp `button/primary` inset border, `text/action`: the revise
 * action next to a commit, e.g. Cancel). [GoldOutline] (`bg/surface`, 1.5dp `text/gold` inset
 * border, `text/gold` label) is the quiet plan action on the gold pages: Cancel Subscription.
 */
enum class EqualButtonVariant { Primary, Secondary, Tonal, Danger, Gold, Outline, GoldOutline }

/** Figma Button "Appearance": brand green, red for destructive, gold for subscription surfaces only. */
enum class EqualButtonAppearance { Default, Critical, Premium }

/** Figma Button "State": filled, gradient with an inner edge, or text only. Disabled is `enabled = false`. */
enum class EqualButtonEmphasis { Primary, Secondary, Tertiary }

/** Figma Button "Size": [Xs] 24 · [Sm] 32 · [Md] 40 · [Lg] 48. [Block] = [Lg] at full width. */
enum class EqualButtonSize { Xs, Sm, Md, Lg, Block }

private fun EqualButtonVariant.pair(): Pair<EqualButtonAppearance, EqualButtonEmphasis>? = when (this) {
    EqualButtonVariant.Primary -> EqualButtonAppearance.Default to EqualButtonEmphasis.Primary
    EqualButtonVariant.Danger -> EqualButtonAppearance.Critical to EqualButtonEmphasis.Primary
    EqualButtonVariant.Gold -> EqualButtonAppearance.Premium to EqualButtonEmphasis.Primary
    EqualButtonVariant.Outline -> EqualButtonAppearance.Default to EqualButtonEmphasis.Secondary
    EqualButtonVariant.GoldOutline -> EqualButtonAppearance.Premium to EqualButtonEmphasis.Secondary
    EqualButtonVariant.Secondary, EqualButtonVariant.Tonal -> null
}

/**
 * The one button (Figma "Button", with "Buton" and "Button Glow" folded in): [appearance] ×
 * [emphasis] × [size]. [variant] is shorthand for a pair (Gold = Premium Primary, Danger =
 * Critical Primary, Outline = Default Secondary, GoldOutline = Premium Secondary) plus two system
 * extras with no Figma pair: [EqualButtonVariant.Secondary] (lime) and [EqualButtonVariant.Tonal]
 * (the "+ Apply" pill).
 *
 * Sizes: Xs 24 / h8 / `buttonSmall` · Sm 32 / h12 / `buttonSmall` · Md 40 / h16 / `buttonLarge` ·
 * Lg 48 / h32 / `buttonLarge`. Gap 6. Secondary paints its gradient (`gradient/button-secondary`,
 * `button-critical-secondary`, `button-premium-secondary`) with a 1dp inner edge; Tertiary is text
 * only. Premium Primary is the gold subscription CTA: `gradient/cta-gold`, white label, the sweeping
 * sheen (two runs, none under reduced motion) and, with [glow], the soft gold halo (Button Glow).
 * Pressed = scale 0.97 + overlay; keyboard focus shows `focusRing`; touch target ≥ 48dp.
 */
@Composable
fun EqualButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: EqualButtonVariant = EqualButtonVariant.Primary,
    size: EqualButtonSize = EqualButtonSize.Md,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    iconSize: Dp? = null,
    /** Premium Primary only: replaces `gradient/cta-gold` (e.g. `gradient/cta-renew`). */
    goldBrush: Brush? = null,
    appearance: EqualButtonAppearance? = null,
    emphasis: EqualButtonEmphasis? = null,
    /** Premium Primary only: the soft gold halo behind the button (Figma "Button Glow"). */
    glow: Boolean = false,
    /** Shows a spinning `progress-activity` in place of the leading icon and ignores clicks; announced as busy. */
    loading: Boolean = false,
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    val gradients = EqualTheme.gradients
    val pair = if (appearance != null || emphasis != null) {
        (appearance ?: EqualButtonAppearance.Default) to (emphasis ?: EqualButtonEmphasis.Primary)
    } else {
        variant.pair()
    }
    val premiumPrimary = pair == EqualButtonAppearance.Premium to EqualButtonEmphasis.Primary
    val content: Color = when {
        !enabled -> colors.textOnActionDisabled
        pair == null && variant == EqualButtonVariant.Secondary -> colors.textOnSecondary
        pair == null -> colors.textAction
        else -> when (pair.second) {
            EqualButtonEmphasis.Primary -> when (pair.first) {
                EqualButtonAppearance.Default -> colors.textOnAction
                EqualButtonAppearance.Critical -> colors.textOnDanger
                EqualButtonAppearance.Premium -> EqualPrimitives.white
            }
            EqualButtonEmphasis.Secondary -> when (pair.first) {
                EqualButtonAppearance.Default -> colors.buttonSecondaryLabel
                EqualButtonAppearance.Critical -> colors.textDanger
                EqualButtonAppearance.Premium -> colors.textGold
            }
            EqualButtonEmphasis.Tertiary -> when (pair.first) {
                EqualButtonAppearance.Default -> colors.textAction
                EqualButtonAppearance.Critical -> colors.textDanger
                EqualButtonAppearance.Premium -> colors.textGold
            }
        }
    }
    val fillModifier: Modifier = when {
        !enabled -> Modifier.background(colors.buttonDisabled)
        pair == null && variant == EqualButtonVariant.Secondary -> Modifier.background(colors.buttonSecondary)
        pair == null -> Modifier.background(colors.buttonTonal)
        pair.second == EqualButtonEmphasis.Tertiary -> Modifier
        pair.second == EqualButtonEmphasis.Primary -> when (pair.first) {
            EqualButtonAppearance.Default -> Modifier.background(colors.buttonPrimary)
            EqualButtonAppearance.Critical -> Modifier.background(colors.buttonDanger)
            EqualButtonAppearance.Premium -> Modifier.background(goldBrush ?: gradients.ctaGold.toBrush())
        }
        else -> when (pair.first) {
            EqualButtonAppearance.Default -> Modifier.background(gradients.buttonSecondary.toBrush()).border(1.dp, colors.buttonSecondaryStroke, CircleShape)
            EqualButtonAppearance.Critical -> Modifier.background(gradients.buttonCriticalSecondary.toBrush()).border(1.dp, colors.buttonCriticalStroke, CircleShape)
            EqualButtonAppearance.Premium -> Modifier.background(gradients.buttonPremiumSecondary.toBrush()).border(1.dp, colors.textGold, CircleShape)
        }
    }
    val (buttonHeight, paddingH, labelStyle) = when (size) {
        EqualButtonSize.Xs -> Triple(24.dp, 8.dp, type.buttonSmall)
        EqualButtonSize.Sm -> Triple(32.dp, 12.dp, type.buttonSmall)
        EqualButtonSize.Md -> Triple(40.dp, 16.dp, type.buttonLarge)
        EqualButtonSize.Lg, EqualButtonSize.Block -> Triple(48.dp, 32.dp, type.buttonLarge)
    }
    val icon = iconSize ?: when (size) {
        EqualButtonSize.Xs -> 14.dp
        EqualButtonSize.Sm -> 16.dp
        EqualButtonSize.Md -> 20.dp
        else -> 24.dp
    }
    val interaction = rememberInteraction()
    val focused by interaction.collectIsFocusedAsState()
    val sheen = if (enabled && premiumPrimary) rememberSheenShift() else null
    val glowState = if (enabled && premiumPrimary && glow) rememberGoldGlow() else null
    val widthModifier = if (size == EqualButtonSize.Block) Modifier.fillMaxWidth() else Modifier

    Row(
        modifier = modifier
            .then(widthModifier)
            .minimumInteractiveComponentSize()
            .eqPressFeedback(interaction, CircleShape)
            .eqFocusRing(focused, CircleShape)
            .height(buttonHeight)
            .then(if (glowState != null) Modifier.goldGlow(glowState, colors.textGold) else Modifier)
            .clip(CircleShape)
            .then(fillModifier)
            .then(if (sheen != null) Modifier.goldSheen(sheen) else Modifier)
            .clickable(
                interactionSource = interaction,
                indication = null,
                enabled = enabled,
                role = Role.Button,
                onClick = { if (!loading) onClick() },
            )
            .then(if (loading) Modifier.semantics { stateDescription = "Busy" } else Modifier)
            .padding(horizontal = paddingH),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        if (loading) {
            EqualIcon(imageVector = EqualSymbols.progressActivity, contentDescription = null, tint = content, size = icon, modifier = Modifier.rotate(rememberButtonSpin()))
            Spacer(Modifier.width(6.dp))
        } else if (leadingIcon != null) {
            EqualIcon(imageVector = leadingIcon, contentDescription = null, tint = content, size = icon)
            Spacer(Modifier.width(6.dp))
        }
        EqualText(text = text, style = labelStyle, color = content, maxLines = 1, overflow = TextOverflow.Ellipsis)
        if (trailingIcon != null) {
            Spacer(Modifier.width(6.dp))
            EqualIcon(imageVector = trailingIcon, contentDescription = null, tint = content, size = icon)
        }
    }
}

/** One turn per second (web `eq-spin`); still under reduced motion. */
@Composable
private fun rememberButtonSpin(): Float {
    if (rememberReduceMotion()) return 0f
    val t = rememberInfiniteTransition(label = "button-spin")
    val a by t.animateFloat(0f, 360f, infiniteRepeatable(tween(1000, easing = LinearEasing)), label = "angle")
    return a
}

/** Sheen: hold `motion.shimmerDelay`, then sweep over `motion.shimmer` (web `eq-sheen`: 66% → 100%). */
private const val SheenCycleMillis = EqualMotion.shimmerDelay + EqualMotion.shimmer

/** Glow: rest `buttonGlowDelay`, rise `buttonGlow`, hold `buttonGlowDelay`, fall `buttonGlow` (web `eq-gold-glow`). */
private const val GlowCycleMillis = 2 * (EqualMotion.buttonGlowDelay + EqualMotion.buttonGlow)

/** How many times the sheen and glow play before stopping, so motion ends within 5 s (WCAG 2.2.2). */
private const val GoldMotionRuns = 2

/** The gold CTA sheen's position (−1.2 → 1.2 widths), or null under reduced motion. Plays [GoldMotionRuns] times. */
@Composable
private fun rememberSheenShift(): State<Float>? {
    if (rememberReduceMotion()) return null
    val shift = remember { Animatable(-1.2f) }
    LaunchedEffect(Unit) {
        repeat(GoldMotionRuns) {
            shift.snapTo(-1.2f)
            shift.animateTo(
                1.2f,
                keyframes {
                    durationMillis = SheenCycleMillis
                    -1.2f at 0
                    -1.2f at EqualMotion.shimmerDelay using EqualEasing.standard
                    1.2f at SheenCycleMillis
                },
            )
        }
    }
    return shift.asState()
}

/** The gold CTA glow level (0 rest → 1 full), or null under reduced motion. Plays [GoldMotionRuns] times. */
@Composable
private fun rememberGoldGlow(): State<Float>? {
    if (rememberReduceMotion()) return null
    val level = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        repeat(GoldMotionRuns) {
            level.animateTo(
                0f,
                keyframes {
                    durationMillis = GlowCycleMillis
                    0f at 0
                    0f at EqualMotion.buttonGlowDelay using EqualEasing.standard
                    1f at EqualMotion.buttonGlowDelay + EqualMotion.buttonGlow
                    1f at 2 * EqualMotion.buttonGlowDelay + EqualMotion.buttonGlow using EqualEasing.standard
                    0f at GlowCycleMillis
                },
            )
        }
    }
    return level.asState()
}

/** Pill shadow in [color] whose offset / blur / alpha follow [level] (read in the draw phase). */
private fun Modifier.goldGlow(level: State<Float>, color: Color): Modifier = drawBehind {
    val g = level.value
    val path = CircleShape.createOutline(size, layoutDirection, this).toPath()
    drawIntoCanvas { canvas ->
        val paint = Paint()
        val fp = paint.asFrameworkPaint()
        fp.isAntiAlias = true
        fp.color = android.graphics.Color.TRANSPARENT
        fp.setShadowLayer(
            cssBlurToShadowRadius((12.dp + 10.dp * g).toPx()),
            0f,
            (4.dp + 2.dp * g).toPx(),
            color.copy(alpha = 0.18f + 0.27f * g).toArgb(),
        )
        canvas.drawPath(path, paint)
    }
}

/**
 * The gold CTA sheen (web `.eq-btn--gold::after`): a 110° white band drawn over the content and
 * translated across the button by [shift] widths, read only in the draw phase.
 */
private fun Modifier.goldSheen(shift: State<Float>): Modifier = drawWithContent {
    drawContent()
    val w = size.width
    val h = size.height
    // CSS 110deg: the gradient line points right and slightly down, through the centre.
    val rad = Math.toRadians(110.0)
    val dx = kotlin.math.sin(rad).toFloat()
    val dy = -kotlin.math.cos(rad).toFloat()
    val half = (kotlin.math.abs(w * dx) + kotlin.math.abs(h * dy)) / 2f
    val c = Offset(w / 2f + shift.value * w, h / 2f)
    val band = Brush.linearGradient(
        0f to Color.Transparent,
        0.30f to Color.Transparent,
        0.48f to EqualPrimitives.white.copy(alpha = 0.35f),
        0.66f to Color.Transparent,
        1f to Color.Transparent,
        start = Offset(c.x - dx * half, c.y - dy * half),
        end = Offset(c.x + dx * half, c.y + dy * half),
    )
    drawRect(band)
}

/**
 * Text button (SPEC §19): no fill, `button` in `text/action` (danger → `text/danger`).
 * Use for the "revise" action next to a filled commit (rule 4).
 */
@Composable
fun EqualTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    danger: Boolean = false,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    /** `text/secondary` label (web `tone="neutral"`): Cancel, Try Again, Maybe Later. */
    neutral: Boolean = false,
    /** Underlined label (Maybe Later). */
    underline: Boolean = false,
) {
    val colors = EqualTheme.colors
    val content = when {
        !enabled -> colors.textActionDisabled
        danger -> colors.textDanger
        neutral -> colors.textSecondary
        else -> colors.textAction
    }
    val interaction = rememberInteraction()
    val shape = CircleShape
    Row(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .eqPressFeedback(interaction, shape)
            .height(36.dp)
            .clip(shape)
            .clickable(
                interactionSource = interaction,
                indication = null,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            )
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        if (leadingIcon != null) {
            EqualIcon(imageVector = leadingIcon, contentDescription = null, tint = content, size = 16.dp)
            Spacer(Modifier.width(6.dp))
        }
        EqualText(text = text, style = if (underline) EqualTheme.type.button.copy(textDecoration = TextDecoration.Underline) else EqualTheme.type.button, color = content, maxLines = 1)
        if (trailingIcon != null) {
            Spacer(Modifier.width(4.dp))
            EqualIcon(imageVector = trailingIcon, contentDescription = null, tint = content, size = 16.dp)
        }
    }
}

/** Icon button roles (SPEC §19). */
enum class EqualIconButtonVariant { Neutral, Primary, Glass }

/**
 * Circular icon button (SPEC §19).
 * - [EqualIconButtonVariant.Neutral]: 44 circle, `bg/surface`, shadow `raised`, icon `text/secondary` (close).
 * - [EqualIconButtonVariant.Primary]: 42 circle, `button/primary`, icon `text/on-action` (confirm).
 * - [EqualIconButtonVariant.Glass]: 44 frosted circle, `glass-button/default`, shadow `glass`, icon
 *   `text/primary` (close/back over art or colour).
 */
@Composable
fun EqualIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: EqualIconButtonVariant = EqualIconButtonVariant.Neutral,
    enabled: Boolean = true,
    iconSize: Dp = 22.dp,
) {
    val colors = EqualTheme.colors
    val diameter = if (variant == EqualIconButtonVariant.Primary) 42.dp else 44.dp
    val fill = when {
        variant == EqualIconButtonVariant.Neutral -> colors.bgSurface
        variant == EqualIconButtonVariant.Glass -> colors.glassButtonDefault
        enabled -> colors.buttonPrimary
        else -> colors.buttonDisabled
    }
    val tint = when {
        variant == EqualIconButtonVariant.Neutral -> colors.textSecondary
        variant == EqualIconButtonVariant.Glass -> colors.textPrimary
        enabled -> colors.textOnAction
        else -> colors.textOnActionDisabled
    }
    val interaction = rememberInteraction()
    val shadowModifier = when (variant) {
        EqualIconButtonVariant.Neutral -> Modifier.eqShadow(EqualShadows.raised, CircleShape)
        EqualIconButtonVariant.Glass -> Modifier.eqShadow(EqualShadows.glass, CircleShape, outsideOnly = true)
        EqualIconButtonVariant.Primary -> Modifier
    }
    // The glass inner-bottom highlight paints over the fill, as on the glass action chips.
    val insetModifier = if (variant == EqualIconButtonVariant.Glass) {
        Modifier.eqGlassInset(colors.actionBottomColor, CircleShape)
    } else {
        Modifier
    }
    Box(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .eqPressFeedback(interaction, CircleShape)
            .size(diameter)
            .then(shadowModifier)
            .clip(CircleShape)
            .background(fill)
            .then(insetModifier)
            .clickable(
                interactionSource = interaction,
                indication = null,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        EqualIcon(imageVector = icon, contentDescription = contentDescription, tint = tint, size = iconSize)
    }
}

@EqualPreviews
@Composable
private fun ButtonsPreview() {
    PreviewFrame {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            EqualButton("Small", onClick = {}, size = EqualButtonSize.Xs)
            EqualButton("Medium", onClick = {})
            EqualButton("Large", onClick = {}, size = EqualButtonSize.Lg)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            EqualButton("Secondary", onClick = {}, variant = EqualButtonVariant.Secondary)
            EqualButton("Delete", onClick = {}, variant = EqualButtonVariant.Danger, leadingIcon = EqualIcons.Delete)
            EqualButton("Disabled", onClick = {}, enabled = false)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            EqualButton("Unlock Unlimited", onClick = {}, variant = EqualButtonVariant.Gold)
            EqualButton("Cancel", onClick = {}, variant = EqualButtonVariant.Outline)
        }
        EqualButton("Cancel Subscription", onClick = {}, variant = EqualButtonVariant.GoldOutline, size = EqualButtonSize.Block)
        EqualButton("Continue", onClick = {}, size = EqualButtonSize.Block, trailingIcon = EqualIcons.ChevronRight)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            EqualTextButton("Edit", onClick = {})
            EqualTextButton("Remove", onClick = {}, danger = true)
            EqualIconButton(EqualIcons.Close, "Close", onClick = {})
            EqualIconButton(EqualIcons.Check, "Confirm", onClick = {}, variant = EqualIconButtonVariant.Primary)
        }
    }
}
