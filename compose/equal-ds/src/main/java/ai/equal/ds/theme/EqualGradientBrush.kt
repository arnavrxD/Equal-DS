package ai.equal.ds.theme

import ai.equal.ds.tokens.EqualGradientSpec
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.RadialGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * Converts a CSS `linear-gradient(<angle>deg, …)` spec into a size-aware [Brush].
 *
 * CSS semantics: 0deg points to the top, 90deg to the right, 180deg to the bottom. The gradient
 * line passes through the centre and its length is `|w·sin a| + |h·cos a|`, so the first and last
 * stops land exactly on the corners, whatever the size of the painted area.
 */
fun EqualGradientSpec.toBrush(): Brush {
    val r = radial
    return if (r != null) {
        CssRadialGradientBrush(r.radiusX, r.radiusY, r.centerX, r.centerY, stops.map { it.second }, stops.map { it.first })
    } else {
        CssLinearGradientBrush(angleDegrees = angle, colors = stops.map { it.second }, positions = stops.map { it.first })
    }
}

/**
 * CSS `radial-gradient(rx ry at cx cy, …)`: an ellipse with radii [radiusX]·width and [radiusY]·height
 * centred at ([centerX]·width, [centerY]·height). Drawn as a circle of radius rx, scaled on y.
 */
private data class CssRadialGradientBrush(
    val radiusX: Float,
    val radiusY: Float,
    val centerX: Float,
    val centerY: Float,
    val colors: List<Color>,
    val positions: List<Float>,
) : ShaderBrush() {
    override fun createShader(size: Size): Shader {
        val rx = (radiusX * size.width).coerceAtLeast(1f)
        val ry = (radiusY * size.height).coerceAtLeast(1f)
        val center = Offset(centerX * size.width, centerY * size.height)
        val shader = RadialGradientShader(center = center, radius = rx, colors = colors, colorStops = positions, tileMode = TileMode.Clamp)
        shader.setLocalMatrix(android.graphics.Matrix().apply { setScale(1f, ry / rx, center.x, center.y) })
        return shader
    }
}

private data class CssLinearGradientBrush(
    val angleDegrees: Float,
    val colors: List<Color>,
    val positions: List<Float>,
) : ShaderBrush() {
    override fun createShader(size: Size): Shader {
        val radians = angleDegrees * (PI.toFloat() / 180f)
        val dirX = sin(radians)
        val dirY = -cos(radians)
        val length = abs(size.width * dirX) + abs(size.height * dirY)
        val half = length / 2f
        val cx = size.width / 2f
        val cy = size.height / 2f
        return LinearGradientShader(
            from = Offset(cx - dirX * half, cy - dirY * half),
            to = Offset(cx + dirX * half, cy + dirY * half),
            colors = colors,
            colorStops = positions,
            tileMode = TileMode.Clamp,
        )
    }
}
