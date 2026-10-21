package ai.equal.ds.components

import ai.equal.ds.theme.EqualTheme
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Skeleton shapes: a text line (radius 4), an avatar circle, or a card / image rect (radius 12). */
enum class EqualSkeletonShape { Line, Circle, Rect }

/** Neutral on app surfaces; Gold on the cream-gold subscription cards (Figma "Shimmer"). */
enum class EqualSkeletonTone { Neutral, Gold }

/**
 * Skeleton (Figma "Shimmer"): `skeleton/base` (or `skeleton/gold-base`) with a `skeleton/highlight`
 * band sweeping left to right every 1.4s; static under reduced motion. Hidden from TalkBack.
 */
@Composable
fun EqualSkeleton(
    modifier: Modifier = Modifier,
    shape: EqualSkeletonShape = EqualSkeletonShape.Line,
    tone: EqualSkeletonTone = EqualSkeletonTone.Neutral,
    height: Dp = when (shape) { EqualSkeletonShape.Line -> 15.dp; EqualSkeletonShape.Circle -> 40.dp; EqualSkeletonShape.Rect -> 80.dp },
) {
    val colors = EqualTheme.colors
    val base = if (tone == EqualSkeletonTone.Gold) colors.skeletonGoldBase else colors.skeletonBase
    val band = if (tone == EqualSkeletonTone.Gold) colors.skeletonGoldHighlight else colors.skeletonHighlight
    val clipShape = when (shape) { EqualSkeletonShape.Circle -> CircleShape; EqualSkeletonShape.Line -> RoundedCornerShape(4.dp); EqualSkeletonShape.Rect -> RoundedCornerShape(12.dp) }
    val sized = if (shape == EqualSkeletonShape.Circle) Modifier.size(height) else Modifier.height(height)
    val still = rememberReduceMotion()
    val progress = if (still) null else {
        val t = rememberInfiniteTransition(label = "skeleton")
        t.animateFloat(-1f, 1f, infiniteRepeatable(tween(1400, easing = LinearEasing), RepeatMode.Restart), label = "sweep")
    }
    Column(
        modifier
            .then(sized)
            .clearAndSetSemantics { }
            .clip(clipShape)
            .background(base)
            .drawWithContent {
                drawContent()
                val p = progress?.value ?: return@drawWithContent
                val x = p * size.width
                drawRect(Brush.horizontalGradient(listOf(Color.Transparent, band, Color.Transparent), startX = x, endX = x + size.width))
            },
    ) {}
}

/** A few skeleton lines, the last one shorter. */
@Composable
fun EqualSkeletonText(lines: Int = 3, modifier: Modifier = Modifier, tone: EqualSkeletonTone = EqualSkeletonTone.Neutral, gap: Dp = 8.dp) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(gap)) {
        repeat(lines) { i -> EqualSkeleton(Modifier.fillMaxWidth(if (i == lines - 1 && lines > 1) 0.62f else 1f), tone = tone) }
    }
}

