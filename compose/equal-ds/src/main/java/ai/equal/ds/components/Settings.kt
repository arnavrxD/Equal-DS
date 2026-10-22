package ai.equal.ds.components

import ai.equal.ds.assets.EqualAssetImage
import ai.equal.ds.assets.EqualAssets
import ai.equal.ds.icons.EqualIcon
import ai.equal.ds.icons.EqualIcons
import ai.equal.ds.icons.EqualSymbols
import ai.equal.ds.theme.EqualEasing
import ai.equal.ds.theme.EqualShadow
import ai.equal.ds.theme.EqualShadowLayer
import ai.equal.ds.theme.EqualTheme
import ai.equal.ds.theme.eqShadow
import ai.equal.ds.tokens.EqualMotion
import ai.equal.ds.tokens.EqualPrimitives
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Updated UI settings and promos: Call Learning cards, the Account & Data trust strip, the Teach
// Your Assistant promo and the generic feature-intro sheet (port of web Settings.tsx).

// ---------------------------------------------------------------------------------------------
// Call Learning cards
// ---------------------------------------------------------------------------------------------

/** Background tint of a [LearningCard]: `preset/tint-orange`, `-lime` or `-green`. */
enum class LearningTint { Orange, Lime, Green }

/** One "what the assistant learns" card. [example] is shown in italics in the glass box. */
data class LearningCardData(
    val emoji: String,
    val tint: LearningTint,
    val title: String,
    val body: String,
    val example: String,
)

/**
 * What the assistant learns, one card of the Call Learning carousel: [width] (default 320), min
 * height 210, padding 16, radius 16 on the [tint]. A 56dp `bg/surface` tile (radius 16) with the
 * 32dp [emoji] → 12 → [title] `body2` SemiBold → 2 → [body] `caption` Regular `text/secondary` →
 * 12 → [example] `captionWrap` italic in a `glass-button/default` box (radius 16, padding 12 / 10).
 */
@Composable
fun LearningCard(
    emoji: String,
    tint: LearningTint,
    title: String,
    body: String,
    example: String,
    modifier: Modifier = Modifier,
    width: Dp = 320.dp,
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    val bg = when (tint) {
        LearningTint.Orange -> colors.presetTintOrange
        LearningTint.Lime -> colors.presetTintLime
        LearningTint.Green -> colors.presetTintGreen
    }
    Column(
        modifier
            .width(width)
            .defaultMinSize(minHeight = 210.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(bg)
            .padding(16.dp),
    ) {
        Box(
            Modifier
                .size(56.dp)
                .background(colors.bgSurface, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center,
        ) {
            EmojiGlyph(emoji = emoji, size = 32.dp)
        }
        Spacer(Modifier.height(12.dp))
        EqualText(
            text = title,
            style = type.body2.copy(fontWeight = FontWeight.SemiBold),
            color = colors.textPrimary,
            modifier = Modifier.semantics { heading() },
        )
        Spacer(Modifier.height(2.dp))
        EqualText(text = body, style = type.captionRegular, color = colors.textSecondary)
        Spacer(Modifier.height(12.dp))
        EqualText(
            text = example,
            style = type.captionWrap.copy(fontStyle = FontStyle.Italic),
            color = colors.textPrimary,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(colors.glassButtonDefault)
                .padding(horizontal = 12.dp, vertical = 10.dp),
        )
    }
}

/** [LearningCard] from its data. */
@Composable
fun LearningCard(card: LearningCardData, modifier: Modifier = Modifier, width: Dp = 320.dp) {
    LearningCard(emoji = card.emoji, tint = card.tint, title = card.title, body = card.body, example = card.example, modifier = modifier, width = width)
}

/** The three Call Learning cards: addresses, relationships, call preferences. */
val LearningCards: List<LearningCardData> = listOf(
    LearningCardData(
        emoji = "📍",
        tint = LearningTint.Orange,
        title = "Your addresses & directions",
        body = "So it can take delivery and pickup calls for you — no stepping away to explain where you live.",
        example = "Delivery agent calls for directions → assistant guides them to Gate 2, Tower B",
    ),
    LearningCardData(
        emoji = "👪",
        tint = LearningTint.Lime,
        title = "Your relationships",
        body = "Helps respond differently to family, friends, coworkers, and businesses.",
        example = "“Your mom says it’s urgent.” → assistant knows family calls matter, so it gets the details for you.",
    ),
    LearningCardData(
        emoji = "🔔",
        tint = LearningTint.Green,
        title = "Your call preferences",
        body = "Understands what type of promotional calls you are interested in and which ones annoy you.",
        example = "“Your bank needs identity verification.” → assistant notifies you immediately; routine promos are handled quietly.",
    ),
)

// ---------------------------------------------------------------------------------------------
// Account & Data trust strip
// ---------------------------------------------------------------------------------------------

/** One stat on a [TrustStrip]: 40dp [art], [value] and [label]. */
data class TrustStat(val art: EqualAssets, val value: String, val label: String)

/**
 * Account & Data: the stats side by side on `trust-strip/bg` (radius 20, padding 16 / 12), split
 * by 1dp `stroke/medium` dividers. Each: 40dp art → 6 → value `body2` SemiBold → label `caption`
 * Regular `text/secondary`, centred.
 */
@Composable
fun TrustStrip(
    stats: List<TrustStat>,
    modifier: Modifier = Modifier,
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    Row(
        modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clip(RoundedCornerShape(20.dp))
            .background(colors.trustStripBg)
            .padding(horizontal = 12.dp, vertical = 16.dp),
    ) {
        stats.forEachIndexed { i, stat ->
            if (i > 0) {
                Box(
                    Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(colors.strokeMedium),
                )
            }
            Column(
                Modifier
                    .weight(1f)
                    .semantics(mergeDescendants = true) {},
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                EqualAssetImage(stat.art, 40.dp)
                Spacer(Modifier.height(6.dp))
                EqualText(text = stat.value, style = type.body2.copy(fontWeight = FontWeight.SemiBold), color = colors.textPrimary, textAlign = TextAlign.Center)
                EqualText(text = stat.label, style = type.captionRegular, color = colors.textSecondary, textAlign = TextAlign.Center)
            }
        }
    }
}

/** Account & Data stats: 5L+ Indian users, 4.4 star rating, 150 Mn+ calls handled. */
val TrustStats: List<TrustStat> = listOf(
    TrustStat(EqualAssets.MascotUpdate, "5L+", "Indian users"),
    TrustStat(EqualAssets.StatStar, "4.4 star", "Rating"),
    TrustStat(EqualAssets.StatPhone, "150 Mn+", "Calls handled"),
)

// ---------------------------------------------------------------------------------------------
// Teach Your Assistant promo
// ---------------------------------------------------------------------------------------------

/**
 * Teach Your Assistant (Preset Instruction design): the teal promo in the home carousel. Radius
 * 18, padding 20 / 16 / 20 / 18, `instructions-promo/bg`. Text column: [title] `title` Bold
 * `instructions-promo/title` → 4 → [body] `caption` Regular → 14 → the [cta] pill (32 high, full
 * radius, `instructions-promo/cta`, 16dp instructions glyph + `buttonSmall` Bold
 * `instructions-promo/cta-text`). The 96dp instructions mascot on the right. [onDismiss] adds a
 * 32dp `text/placeholder` close at the top-right (inset 6).
 */
@Composable
fun InstructionsPromoCard(
    modifier: Modifier = Modifier,
    title: String = "Teach Your Assistant",
    body: String = "Pick a ready-made instruction or write your own",
    cta: String = "Try Instructions",
    onCta: () -> Unit = {},
    onDismiss: (() -> Unit)? = null,
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    Box(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(colors.instructionsPromoBg),
    ) {
        Row(
            Modifier.padding(start = 18.dp, top = 20.dp, end = 16.dp, bottom = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Column(Modifier.weight(1f)) {
                EqualText(
                    text = title,
                    style = type.title.copy(fontWeight = FontWeight.Bold),
                    color = colors.instructionsPromoTitle,
                    modifier = Modifier.semantics { heading() },
                )
                Spacer(Modifier.height(4.dp))
                EqualText(text = body, style = type.captionRegular, color = colors.textPrimary)
                Spacer(Modifier.height(14.dp))
                Row(
                    Modifier
                        .touchTarget()
                        .height(32.dp)
                        .clip(CircleShape)
                        .background(colors.instructionsPromoCta)
                        .clickable(role = Role.Button, onClick = onCta)
                        .padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    EqualIcon(EqualIcons.Instructions, contentDescription = null, tint = colors.instructionsPromoCtaText, size = 16.dp)
                    EqualText(text = cta, style = type.buttonSmall.copy(fontWeight = FontWeight.Bold), color = colors.instructionsPromoCtaText, maxLines = 1)
                }
            }
            EqualAssetImage(EqualAssets.MascotInstructions, 96.dp)
        }
        if (onDismiss != null) {
            CompactIconButton(
                EqualIcons.Close,
                "Dismiss",
                onClick = onDismiss,
                tint = colors.textPlaceholder,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 6.dp, end = 6.dp),
                size = 32.dp,
                iconSize = 20.dp,
            )
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Feature intro sheet
// ---------------------------------------------------------------------------------------------

/** One icon bullet on a [FeatureIntroSheet]. */
data class FeatureBullet(val icon: ImageVector, val text: String)

/** 0 −8 30 black/12%: the feature-intro sheet over the screen. */
private val FeatureIntroShadow = EqualShadow(listOf(EqualShadowLayer(0.dp, (-8).dp, 30.dp, EqualPrimitives.black.copy(alpha = 0.12f))))

/**
 * New-feature sheet: the tinted [hero] (`instructions-promo/bg`, padding 28 / 16 / 20, centred),
 * a 44dp glass close at the top-right ([onClose]), then (padding 20 / 16 / 24) the soft [badge]
 * ("NEW FEATURE"), [title] `headline` SemiBold, the [bullets] (20dp `text/action` icon + `body1`
 * Medium, gap 20, min 36 high), a full-width [cta] and an optional [secondary] text button.
 * `bg/primary`, top radius 28.
 *
 * As an overlay (default) it fills its parent: `scrim` (tap → [onClose]) and the sheet at the
 * bottom, sliding up over `motion.slow` (none under reduced motion); system Back calls [onClose].
 * [inline] draws only the panel with all corners rounded, for docs.
 */
@Composable
fun FeatureIntroSheet(
    title: String,
    bullets: List<FeatureBullet>,
    cta: String,
    hero: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    badge: String = "New feature",
    onCta: () -> Unit = {},
    secondary: String? = null,
    onSecondary: () -> Unit = {},
    onClose: (() -> Unit)? = null,
    inline: Boolean = false,
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    val shape = if (inline) RoundedCornerShape(28.dp) else RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    val panel: @Composable (Modifier) -> Unit = { m ->
        Box(
            m
                .fillMaxWidth()
                .eqShadow(FeatureIntroShadow, shape)
                .clip(shape)
                .background(colors.bgPrimary)
                // Swallow taps so they don't reach the scrim.
                .clickable(interactionSource = rememberInteraction(), indication = null, onClick = {})
                .semantics { paneTitle = title },
        ) {
            Column(Modifier.fillMaxWidth().then(if (inline) Modifier else Modifier.navigationBarsPadding())) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .background(colors.instructionsPromoBg)
                        .padding(start = 16.dp, top = 28.dp, end = 16.dp, bottom = 20.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    hero()
                }
                Column(Modifier.fillMaxWidth().padding(start = 16.dp, top = 20.dp, end = 16.dp, bottom = 24.dp)) {
                    NewBadge(text = badge, soft = true)
                    Spacer(Modifier.height(8.dp))
                    EqualText(
                        text = title,
                        style = type.headline.copy(fontWeight = FontWeight.SemiBold),
                        color = colors.textPrimary,
                        modifier = Modifier.semantics { heading() },
                    )
                    Column(
                        Modifier.padding(top = 16.dp, bottom = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                    ) {
                        bullets.forEach { b ->
                            Row(
                                Modifier.defaultMinSize(minHeight = 36.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                EqualIcon(b.icon, contentDescription = null, tint = colors.textAction, size = 20.dp)
                                EqualText(text = b.text, style = type.body1.copy(fontWeight = FontWeight.W500), color = colors.textPrimary)
                            }
                        }
                    }
                    EqualButton(text = cta, onClick = onCta, size = EqualButtonSize.Block)
                    if (secondary != null) {
                        Spacer(Modifier.height(8.dp))
                        EqualTextButton(secondary, onClick = onSecondary, modifier = Modifier.align(Alignment.CenterHorizontally))
                    }
                }
            }
            if (onClose != null) {
                EqualIconButton(
                    icon = EqualIcons.Close,
                    contentDescription = "Close",
                    onClick = onClose,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 12.dp, end = 12.dp),
                    variant = EqualIconButtonVariant.Glass,
                )
            }
        }
    }
    if (inline) {
        panel(modifier)
        return
    }
    if (onClose != null) EqualBackHandler(onBack = onClose)
    val reduceMotion = rememberReduceMotion()
    val visible = remember { MutableTransitionState(reduceMotion).apply { targetState = true } }
    Box(modifier.fillMaxSize()) {
        Box(
            Modifier
                .matchParentSize()
                .background(colors.bgOverlay)
                .clickable(interactionSource = rememberInteraction(), indication = null, onClickLabel = "Close", onClick = onClose ?: {}),
        )
        AnimatedVisibility(
            visibleState = visible,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = slideInVertically(tween(EqualMotion.slow, easing = EqualEasing.standard)) { it },
        ) {
            panel(Modifier)
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Previews
// ---------------------------------------------------------------------------------------------

@EqualPreviews
@Composable
private fun SettingsPreview() {
    PreviewFrame {
        LearningCard(LearningCards.first())
        TrustStrip(stats = TrustStats)
        InstructionsPromoCard(onDismiss = {})
        FeatureIntroSheet(
            title = "Get every call handled your way with Instructions",
            bullets = listOf(
                FeatureBullet(EqualSymbols.edit, "Tell your assistant what to do on any call, in your own words"),
                FeatureBullet(EqualSymbols.touch, "Start in one tap with presets for spam, loan and delivery calls"),
            ),
            cta = "Try Instructions",
            hero = { EqualAssetImage(EqualAssets.MascotInstructions, 96.dp) },
            secondary = "Not now",
            onClose = {},
            inline = true,
        )
    }
}
