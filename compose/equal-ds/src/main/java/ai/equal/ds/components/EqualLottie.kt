package ai.equal.ds.components

import android.provider.Settings
import androidx.annotation.RawRes
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieDynamicProperty

/**
 * Plays a Lottie animation shipped as `res/raw/lottie_<name>.json` (web: `assets/lottie/<name>.json`,
 * copied here by `npm run assets`). Illustrative motion only (mascot moments, celebrations,
 * typing); micro-interactions stay in code on the motion tokens.
 *
 * Respects the system "Remove animations" setting (animator scale 0): it shows the first frame and
 * doesn't play. [tint] recolours every fill and stroke, for single-colour animations that should
 * follow the theme (e.g. `EqualTheme.colors.textAction`: green in light, lime in dark).
 */
@Composable
fun EqualLottie(
    @RawRes rawRes: Int,
    modifier: Modifier = Modifier,
    width: Dp = 120.dp,
    height: Dp = 120.dp,
    iterations: Int = LottieConstants.IterateForever,
    tint: Color? = null,
    contentDescription: String? = null,
    /** Called once a finite play reaches its last frame (it then holds that frame). */
    onComplete: (() -> Unit)? = null,
    /** Under reduced motion, show the last frame instead of the first (end states such as a check). */
    stillAtEnd: Boolean = false,
    /** Don't play: hold the first frame (or the last with [stillAtEnd]), e.g. a settled end state. */
    still: Boolean = false,
) {
    val context = LocalContext.current
    val reduceMotion = remember(context) {
        Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
    }
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(rawRes))
    val state = animateLottieCompositionAsState(
        composition = composition,
        iterations = iterations,
        isPlaying = !reduceMotion && !still,
    )
    val progress by state
    val finished = composition != null && (reduceMotion || still || (iterations != LottieConstants.IterateForever && state.isAtEnd && !state.isPlaying))
    LaunchedEffect(finished) { if (finished) onComplete?.invoke() }
    val dynamicProperties = if (tint != null) {
        val argb = tint.toArgb()
        rememberLottieDynamicProperties(
            rememberLottieDynamicProperty(property = LottieProperty.COLOR, value = argb, "**"),
            rememberLottieDynamicProperty(property = LottieProperty.STROKE_COLOR, value = argb, "**"),
        )
    } else {
        null
    }
    LottieAnimation(
        composition = composition,
        progress = { if (reduceMotion || still) (if (stillAtEnd) 1f else 0f) else progress },
        dynamicProperties = dynamicProperties,
        modifier = modifier
            .size(width = width, height = height)
            .then(if (contentDescription != null) Modifier.semantics { this.contentDescription = contentDescription } else Modifier),
    )
}
