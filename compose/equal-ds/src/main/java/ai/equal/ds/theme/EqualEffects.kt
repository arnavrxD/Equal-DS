package ai.equal.ds.theme

import ai.equal.ds.tokens.EqualMotion
import ai.equal.ds.tokens.EqualPrimitives
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** One CSS-style drop-shadow layer (`x y blur color`). */
@Immutable
data class EqualShadowLayer(val offsetX: Dp, val offsetY: Dp, val blur: Dp, val color: Color)

/** A stack of [EqualShadowLayer]s, mirroring `tokens.json → shadow`. */
@Immutable
data class EqualShadow(val layers: List<EqualShadowLayer>)

/** Shadow tokens from `tokens.json → shadow`. Same values in light and dark. */
object EqualShadows {
    private val black = EqualPrimitives.black
    private val green = EqualPrimitives.green

    /** 0 1 2 black/4%, 0 2 8 black/6% */
    val card = EqualShadow(
        listOf(
            EqualShadowLayer(0.dp, 1.dp, 2.dp, black.copy(alpha = 0.04f)),
            EqualShadowLayer(0.dp, 2.dp, 8.dp, black.copy(alpha = 0.06f)),
        ),
    )

    /** 0 2 6 black/10% */
    val raised = EqualShadow(listOf(EqualShadowLayer(0.dp, 2.dp, 6.dp, black.copy(alpha = 0.10f))))

    /** 0 6 24 black/10% */
    val nav = EqualShadow(listOf(EqualShadowLayer(0.dp, 6.dp, 24.dp, black.copy(alpha = 0.10f))))

    /** 0 6 16 green/30% */
    val fab = EqualShadow(listOf(EqualShadowLayer(0.dp, 6.dp, 16.dp, green.copy(alpha = 0.30f))))

    /** Drop part of `glass` (0 1 2 black/6%). Pair with [eqGlassInset] for the inset bottom edge. */
    val glass = EqualShadow(listOf(EqualShadowLayer(0.dp, 1.dp, 2.dp, black.copy(alpha = 0.06f))))

    /** 0 2 6 black/8% */
    val header = EqualShadow(listOf(EqualShadowLayer(0.dp, 2.dp, 6.dp, black.copy(alpha = 0.08f))))

    /** focusRing: 0 0 0 3px green/12% */
    val focusRingColor: Color = green.copy(alpha = 0.12f)
    val focusRingWidth: Dp = 3.dp

    /** Inset height of the `glass` bottom edge. */
    val glassInset: Dp = 2.dp
}

/** Motion easings from `tokens.json → motion.easing`. Durations are in [EqualMotion]. */
object EqualEasing {
    val standard = CubicBezierEasing(0.2f, 0f, 0f, 1f)
    val emphasized = CubicBezierEasing(0.3f, 0f, 0f, 1.3f)
}

/** Interaction state values (SPEC §19: pressed = scale 0.97 + 8% black overlay). */
object EqualStates {
    val pressedOverlay: Color = EqualPrimitives.black.copy(alpha = 0.08f)
    const val pressedScale: Float = 0.97f
}

internal fun Outline.toPath(): Path = when (this) {
    is Outline.Rectangle -> Path().apply { addRect(rect) }
    is Outline.Rounded -> Path().apply { addRoundRect(roundRect) }
    is Outline.Generic -> path
}

/** CSS blur radius (2σ) → Android `setShadowLayer` radius (σ = 0.57735·r + 0.5). */
internal fun cssBlurToShadowRadius(blurPx: Float): Float =
    ((blurPx / 2f - 0.5f) / 0.57735f).coerceAtLeast(0.5f)

/**
 * Draws a CSS-like drop shadow behind the element, following [shape].
 * Put it *before* `background` in the chain.
 *
 * Uses `Paint.setShadowLayer`, which renders on hardware canvases from API 28. On API 24–27
 * the shadow is simply not drawn (graceful degradation).
 *
 * [outsideOnly] cuts the shape out of the shadow, as CSS does, so a translucent fill on top
 * (e.g. `choice/default-bg`, `glass-button/default`) doesn't show the shadow through it.
 */
fun Modifier.eqShadow(shadow: EqualShadow, shape: Shape = RectangleShape, outsideOnly: Boolean = false): Modifier = this.drawBehind {
    val path = shape.createOutline(size, layoutDirection, this).toPath()
    drawIntoCanvas { canvas ->
        if (outsideOnly) {
            canvas.save()
            canvas.clipPath(path, ClipOp.Difference)
        }
        for (layer in shadow.layers) {
            val paint = Paint()
            val frameworkPaint = paint.asFrameworkPaint()
            frameworkPaint.isAntiAlias = true
            frameworkPaint.color = android.graphics.Color.TRANSPARENT
            frameworkPaint.setShadowLayer(
                cssBlurToShadowRadius(layer.blur.toPx()),
                layer.offsetX.toPx(),
                layer.offsetY.toPx(),
                layer.color.toArgb(),
            )
            canvas.drawPath(path, paint)
        }
        if (outsideOnly) canvas.restore()
    }
}

/**
 * The inset part of the `glass` shadow: a [EqualShadows.glassInset] band along the inner bottom
 * edge of [shape] in [color] (normally `EqualTheme.colors.actionBottomColor`).
 * Put it *after* `background` in the chain.
 */
fun Modifier.eqGlassInset(color: Color, shape: Shape): Modifier = this.drawBehind {
    val outlinePath = shape.createOutline(size, layoutDirection, this).toPath()
    val shifted = Path().apply { addPath(outlinePath, Offset(0f, -EqualShadows.glassInset.toPx())) }
    val band = Path().apply { op(outlinePath, shifted, PathOperation.Difference) }
    drawPath(band, color)
}

/**
 * `focusRing` token: a 3dp translucent-green ring outside [shape] when [visible].
 * Put it *before* `background` in the chain.
 */
fun Modifier.eqFocusRing(visible: Boolean, shape: Shape): Modifier = this.drawBehind {
    if (visible) {
        val spread = EqualShadows.focusRingWidth.toPx()
        val ringSize = Size(size.width + spread * 2f, size.height + spread * 2f)
        val outline = shape.createOutline(ringSize, layoutDirection, this)
        translate(left = -spread, top = -spread) {
            drawOutline(outline, EqualShadows.focusRingColor)
        }
    }
}

/**
 * SPEC §19 pressed feedback: scales to 0.97 and draws an 8% black overlay clipped to [shape]
 * while [interactionSource] reports a press. Put it at the *start* of the chain so it covers
 * background and content; pass the same source to `clickable(indication = null)`.
 */
fun Modifier.eqPressFeedback(interactionSource: InteractionSource, shape: Shape): Modifier = composed {
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) EqualStates.pressedScale else 1f,
        animationSpec = tween(durationMillis = EqualMotion.fast, easing = EqualEasing.standard),
        label = "eqPressScale",
    )
    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .drawWithContent {
            drawContent()
            if (pressed) {
                drawOutline(shape.createOutline(size, layoutDirection, this), EqualStates.pressedOverlay)
            }
        }
}
