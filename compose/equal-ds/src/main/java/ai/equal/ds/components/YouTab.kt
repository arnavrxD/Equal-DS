package ai.equal.ds.components

import ai.equal.ds.assets.EqualAssetImage
import ai.equal.ds.assets.EqualAssets
import ai.equal.ds.icons.EqualIcon
import ai.equal.ds.icons.EqualIcons
import ai.equal.ds.theme.EqualEasing
import ai.equal.ds.theme.EqualTheme
import ai.equal.ds.theme.tabular
import ai.equal.ds.theme.toBrush
import ai.equal.ds.tokens.EqualPrimitives
import ai.equal.ds.tokens.EqualRadius
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import kotlin.math.max

// You tab surfaces, from the You (Settings) design. Mirrors web/src/components/YouTab.tsx.

// ---------------------------------------------------------------------------------------------
// Profile hero
// ---------------------------------------------------------------------------------------------

/**
 * Gold hero at the top of the You tab. `gradient/profile-hero` with two faint white arcs
 * (circles r = 250 / 290 centred below the header, stroke 1.2, 55% white).
 * Padding 20 top / 12 sides. Row (8dp inner padding): [ProfileAvatar] 70 → 12 → column
 * [name `headline` + [PlanBadge] + 28dp edit button (gap 8), phone `body1` `text/secondary`
 * tabular]. [content] (usually the [PlanCard]) sits 16dp below the row.
 *
 * The header is light in both themes: the row always renders under the light [EqualTheme]. In
 * dark the gradient becomes a panel with 24dp rounded bottom corners that ends 56dp above the
 * hero's bottom, so the plan card overlaps its edge onto `bg/primary`.
 */
@Composable
fun ProfileHero(
    name: String,
    phone: String,
    modifier: Modifier = Modifier,
    avatar: ProfileAvatarVariant = ProfileAvatarVariant.Male,
    plan: String? = "Unlimited",
    /** Paid while a [plan] is set: the gold-ringed avatar; free: the plain bust. */
    tier: PlanTier = if (plan != null) PlanTier.Paid else PlanTier.Free,
    onEdit: (() -> Unit)? = null,
    content: (@Composable ColumnScope.() -> Unit)? = null,
) {
    val dark = EqualTheme.colors.isDark
    val heroBrush = EqualTheme.gradients.profileHero.toBrush()
    val arcColor = EqualPrimitives.white.copy(alpha = 0.55f)
    Column(
        modifier
            .fillMaxWidth()
            .clipToBounds()
            .drawBehind {
                val cut = if (dark) 56.dp.toPx() else 0f
                val radius = if (dark) 24.dp.toPx() else 0f
                inset(left = 0f, top = 0f, right = 0f, bottom = cut) {
                    val panel = Path().apply {
                        addRoundRect(
                            RoundRect(
                                rect = Rect(Offset.Zero, size),
                                bottomLeft = CornerRadius(radius),
                                bottomRight = CornerRadius(radius),
                            ),
                        )
                    }
                    drawPath(panel, heroBrush)
                    clipPath(panel) {
                        // SVG viewBox 360 × 260, preserveAspectRatio "xMidYMin slice", over the panel.
                        val scale = max(size.width / 360f, size.height / 260f)
                        val offsetX = (size.width - 360f * scale) / 2f
                        val center = Offset(offsetX + 180f * scale, 300f * scale)
                        val stroke = Stroke(width = 1.2f * scale)
                        drawCircle(color = arcColor, radius = 250f * scale, center = center, style = stroke)
                        drawCircle(color = arcColor, radius = 290f * scale, center = center, style = stroke)
                    }
                }
            }
            .padding(start = 12.dp, end = 12.dp, top = 20.dp),
    ) {
        EqualTheme(darkTheme = false) { HeroRow(name, phone, avatar, plan, tier, onEdit) }
        if (content != null) {
            Spacer(Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
private fun HeroRow(
    name: String,
    phone: String,
    avatar: ProfileAvatarVariant,
    plan: String?,
    tier: PlanTier,
    onEdit: (() -> Unit)?,
) {
    val colors = EqualTheme.colors
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ProfileAvatar(variant = avatar, width = 70.dp, tier = tier)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                EqualText(
                    text = name,
                    style = EqualTheme.type.headline,
                    color = colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )
                if (plan != null) {
                    Spacer(Modifier.width(8.dp))
                    PlanBadge(text = plan)
                }
                if (onEdit != null) {
                    Spacer(Modifier.width(8.dp))
                    // 28dp ghost button; Compose extends the hit area to 48dp.
                    Box(
                        Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .clickable(role = Role.Button, onClickLabel = "Edit profile", onClick = onEdit),
                        contentAlignment = Alignment.Center,
                    ) {
                        EqualIcon(EqualIcons.EditProfile, contentDescription = "Edit profile", tint = colors.textSecondary, size = 18.dp)
                    }
                }
            }
            EqualText(text = phone, style = EqualTheme.type.body1.tabular(), color = colors.textSecondary, maxLines = 1)
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Plan card
// ---------------------------------------------------------------------------------------------

/**
 * Dark plan card. Radius 20, `plan-card/bg`, padding 14 / 16 / 16.
 * Top row: "Current Plan" pill (24 high, radius 8, 1dp `plan-card/pill-stroke`, 4% white fill,
 * `caption` `text/on-dark`; [expired] → "Plan Expired", filled `plan-card/expired-pill` with no
 * border and `caption` Medium `text/on-dark`), and
 * on the right an underlined "Manage plan" (`buttonSmall` `text/on-dark`) when [onManageClick]
 * is set. Then [product] `title` `text/on-dark` with [tier] in `text/accent-gold` (12 below),
 * then [price] `caption` Regular `text/on-dark-secondary` (tabular). A coded gold line pattern
 * fills the right 60% of the card.
 */
@Composable
fun PlanCard(
    modifier: Modifier = Modifier,
    product: String = "Equal AI",
    tier: String = "Unlimited",
    price: String = "₹149/Monthly",
    expired: Boolean = false,
    onManageClick: (() -> Unit)? = null,
    currentLabel: String = "Current Plan",
    expiredLabel: String = "Plan Expired",
    manageLabel: String = "Manage plan",
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    val pillShape = RoundedCornerShape(EqualRadius.rSm)
    Box(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(EqualRadius.rXl))
            .background(colors.planCardBg),
    ) {
        PlanPattern(
            color = colors.planCardPattern,
            modifier = Modifier
                .matchParentSize()
                .wrapRightFraction(0.6f),
        )
        Column(Modifier.padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 16.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .height(24.dp)
                        .clip(pillShape)
                        .background(if (expired) colors.planCardExpiredPill else EqualPrimitives.white.copy(alpha = 0.04f))
                        .border(1.dp, if (expired) Color.Transparent else colors.planCardPillStroke, pillShape)
                        .padding(horizontal = 10.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    EqualText(
                        text = if (expired) expiredLabel else currentLabel,
                        style = type.caption,
                        color = colors.textOnDark,
                        maxLines = 1,
                    )
                }
                Spacer(Modifier.weight(1f))
                if (onManageClick != null) {
                    EqualText(
                        text = manageLabel,
                        style = type.buttonSmall.copy(textDecoration = TextDecoration.Underline),
                        color = colors.textOnDark,
                        maxLines = 1,
                        modifier = Modifier.clickable(role = Role.Button, onClick = onManageClick),
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            BasicText(
                text = buildAnnotatedString {
                    append(product)
                    append(" ")
                    withStyle(SpanStyle(color = colors.textAccentGold)) { append(tier.uppercase()) }
                },
                style = type.title.copy(color = colors.textOnDark),
            )
            EqualText(text = price, style = type.captionRegular.tabular(), color = colors.textOnDarkSecondary, maxLines = 1)
        }
    }
}

/** Lays the child out at [fraction] of the incoming width, anchored to the right edge, full height. */
private fun Modifier.wrapRightFraction(fraction: Float): Modifier = layout { measurable, constraints ->
    val width = (constraints.maxWidth * fraction).toInt()
    val placeable = measurable.measure(Constraints.fixed(width, constraints.maxHeight))
    layout(constraints.maxWidth, constraints.maxHeight) {
        placeable.place(constraints.maxWidth - width, 0)
    }
}

/**
 * Gold line pattern, identical to web `PlanPattern`: viewBox 180 × 110 with
 * preserveAspectRatio "xMaxYMax slice" (scaled to cover, anchored bottom-right). Nine stepped
 * polylines plus five shorter ones, stroke 0.9, each fading 0 → 0.55 → 0.8 alpha left to right.
 * Decorative: draws in [color] ([PlanCard] passes `plan-card/pattern`) and fills [modifier]'s bounds.
 */
@Composable
fun PlanPattern(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val scale = max(size.width / 180f, size.height / 110f)
        val dx = size.width - 180f * scale
        val dy = size.height - 110f * scale
        fun pt(x: Float, y: Float) = Offset(dx + x * scale, dy + y * scale)
        val stroke = Stroke(width = 0.9f * scale)

        fun drawPolyline(points: List<Offset>) {
            val path = Path().apply {
                moveTo(points[0].x, points[0].y)
                for (i in 1 until points.size) lineTo(points[i].x, points[i].y)
            }
            // SVG gradient uses objectBoundingBox units: fade across each polyline's own width.
            val minX = points.minOf { it.x }
            val maxX = points.maxOf { it.x }
            val brush = Brush.horizontalGradient(
                0f to color.copy(alpha = 0f),
                0.55f to color.copy(alpha = 0.55f),
                1f to color.copy(alpha = 0.8f),
                startX = minX,
                endX = maxX,
            )
            drawPath(path = path, brush = brush, style = stroke)
        }

        val offsets = (0 until 9).map { it * 7f }
        for (o in offsets) {
            drawPolyline(
                listOf(
                    pt(20f + o, 110f),
                    pt(20f + o, 80f - o * 0.2f),
                    pt(60f + o, 40f - o * 0.2f),
                    pt(120f + o, 40f - o * 0.2f),
                    pt(150f + o, 10f - o * 0.2f),
                    pt(190f, 10f - o * 0.2f),
                ),
            )
        }
        for (o in offsets.take(5)) {
            drawPolyline(
                listOf(
                    pt(90f + o, 110f),
                    pt(90f + o, 95f - o * 0.3f),
                    pt(110f + o, 75f - o * 0.3f),
                    pt(190f, 75f - o * 0.3f),
                ),
            )
        }
    }
}

// ---------------------------------------------------------------------------------------------
// AI Summary card
// ---------------------------------------------------------------------------------------------

/** The three AI summary states in the design. */
enum class AiSummaryStatus { Empty, Loading, Ready }

/**
 * "Your AI Summary": prose the assistant wrote about the user. Radius 20,
 * `gradient/ai-summary`, padding 16, min height 212, gaps 12.
 * Header: 28dp orb + [label] `caption` `text/secondary`.
 * - Empty: centred "I'm still getting to know you..." (`bodyLarge` at Medium).
 * - Loading: five full-width 17dp `skeleton` bars (gap 4) pulsing 0.95 ↔ 0.45 over 1.4s,
 *   staggered 90ms.
 * - Ready: [summary] in `bodyLarge` `text/primary`.
 * Footer, right-aligned `caption` Regular `text/tertiary`: [footer], or by default
 * "Last Updated: [lastUpdated]" while loading and "Usually ready after your first few calls"
 * otherwise.
 */
@Composable
fun AiSummaryCard(
    modifier: Modifier = Modifier,
    status: AiSummaryStatus = AiSummaryStatus.Empty,
    summary: String? = null,
    footer: String? = null,
    lastUpdated: String = "8:06 AM",
    label: String = "Your AI Summary",
    emptyText: String = "I'm still getting to know you...",
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    val foot = footer ?: if (status == AiSummaryStatus.Loading) {
        "Last Updated: $lastUpdated"
    } else {
        "Usually ready after your first few calls"
    }
    Column(
        modifier
            .fillMaxWidth()
            .heightIn(min = 212.dp)
            .clip(RoundedCornerShape(EqualRadius.rXl))
            .background(EqualTheme.gradients.aiSummary.toBrush())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            EqualLogoOrb(size = 28.dp)
            Spacer(Modifier.width(8.dp))
            EqualText(text = label, style = type.caption, color = colors.textSecondary, maxLines = 1)
        }
        when (status) {
            AiSummaryStatus.Ready -> {
                EqualText(text = summary.orEmpty(), style = type.bodyLarge, color = colors.textPrimary)
            }
            AiSummaryStatus.Empty -> {
                // Fills the leftover min height and centres the message.
                Box(
                    Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    EqualText(
                        text = emptyText,
                        style = type.bodyLarge.copy(fontWeight = FontWeight.W500),
                        color = colors.textPrimary,
                        textAlign = TextAlign.Center,
                    )
                }
            }
            AiSummaryStatus.Loading -> {
                Box(
                    Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    SummarySkeleton()
                }
            }
        }
        EqualText(
            text = foot,
            style = type.captionRegular,
            color = colors.textTertiary,
            textAlign = TextAlign.End,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun SummarySkeleton() {
    val color = EqualTheme.colors.skeleton
    val transition = rememberInfiniteTransition(label = "eqSkeleton")
    val time by transition.animateFloat(
        initialValue = 0f,
        targetValue = SkeletonPeriodMs,
        animationSpec = infiniteRepeatable(tween(durationMillis = SkeletonPeriodMs.toInt(), easing = LinearEasing)),
        label = "eqSkeletonTime",
    )
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        for (i in 0 until 5) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(17.dp)
                    // Read the animated time in the layer block: redraws without recomposing.
                    .graphicsLayer { alpha = skeletonAlpha(time - i * 90f) }
                    .background(color),
            )
        }
    }
}

private const val SkeletonPeriodMs = 1400f

/** Keyframes 0% → 0.95, 50% → 0.45, 100% → 0.95, with the standard easing on each half. */
private fun skeletonAlpha(timeMs: Float): Float {
    val phase = (((timeMs % SkeletonPeriodMs) + SkeletonPeriodMs) % SkeletonPeriodMs) / SkeletonPeriodMs
    val half = if (phase < 0.5f) phase * 2f else (1f - phase) * 2f
    return 0.95f - 0.5f * EqualEasing.standard.transform(half)
}

// ---------------------------------------------------------------------------------------------
// Invite card
// ---------------------------------------------------------------------------------------------

/**
 * "Invite a Friend". Min height 68, radius 24, `invite/bg`, 1dp `invite/stroke`,
 * padding 10 / 6 / 10 / 108. The `illustration-high-five` art (113 × 112) sits flush left and
 * rises 29dp above the card top (overflows top and bottom, not clipped). The supplied file already
 * carries the design's −6.39° tilt, so [artRotation] defaults to 0 — set it only for untilted art. [title] `subheading`, [subtitle] `caption` Regular
 * `text/secondary`; trailing 56 × 40 `bg/surface` pill share button (share 22, `text/secondary`).
 */
@Composable
fun InviteCard(
    onShare: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = "Invite a Friend",
    subtitle: String = "Invite Friends To Join",
    artRotation: Float = 0f,
) {
    val colors = EqualTheme.colors
    val shape = RoundedCornerShape(EqualRadius.r2xl)
    Box(
        modifier
            .fillMaxWidth()
            .heightIn(min = 68.dp)
            .background(colors.inviteBg, shape)
            .border(1.dp, colors.inviteStroke, shape),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .heightIn(min = 68.dp)
                .padding(start = 108.dp, top = 10.dp, end = 6.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(Modifier.weight(1f)) {
                EqualText(text = title, style = EqualTheme.type.subheading, color = colors.textPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                EqualText(text = subtitle, style = EqualTheme.type.captionRegular, color = colors.textSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Box(
                Modifier
                    .size(width = 56.dp, height = 40.dp)
                    .clip(CircleShape)
                    .background(colors.bgSurface)
                    .clickable(role = Role.Button, onClickLabel = "Share invite", onClick = onShare),
                contentAlignment = Alignment.Center,
            ) {
                EqualIcon(EqualIcons.Share, contentDescription = "Share invite", tint = colors.textSecondary, size = 22.dp)
            }
        }
        // Zero-size anchor at the top-left: the art doesn't affect the card's size, so it can rise
        // 29dp above the top edge and hang below the bottom, as in the design.
        Box(
            Modifier
                .align(Alignment.TopStart)
                .layout { measurable, _ ->
                    val placeable = measurable.measure(Constraints())
                    layout(0, 0) {
                        placeable.place(0, -(29.dp.roundToPx()))
                    }
                },
        ) {
            EqualAssetImage(
                asset = EqualAssets.IllustrationHighFive,
                size = DpSize(113.dp, 112.dp),
                modifier = Modifier.rotate(artRotation),
            )
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Previews
// ---------------------------------------------------------------------------------------------

@EqualPreviews
@Composable
private fun YouTabPartsPreview() {
    PreviewFrame(padding = 0.dp) {
        ProfileHero(name = "Keshav", phone = "+91 98789 56749", onEdit = {}) {
            PlanCard(onManageClick = {})
        }
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            PlanCard(expired = true, price = "Expired on 12 Aug")
            AiSummaryCard(status = AiSummaryStatus.Empty)
            AiSummaryCard(status = AiSummaryStatus.Loading)
            AiSummaryCard(
                status = AiSummaryStatus.Ready,
                summary = "Your family calls you a lot. They always say it's urgent. They almost never say why.",
            )
            Spacer(Modifier.height(24.dp))
            InviteCard(onShare = {})
        }
    }
}

@EqualPreviews
@Composable
private fun PlanPatternPreview() {
    PreviewFrame {
        PlanPattern(color = EqualTheme.colors.planCardPattern, modifier = Modifier.fillMaxWidth().height(110.dp))
    }
}
