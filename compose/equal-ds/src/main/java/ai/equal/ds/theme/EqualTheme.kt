package ai.equal.ds.theme

import ai.equal.ds.tokens.DarkEqualColors
import ai.equal.ds.tokens.DarkEqualGradients
import ai.equal.ds.tokens.EqualColors
import ai.equal.ds.tokens.EqualGradients
import ai.equal.ds.tokens.EqualTypeTokens
import ai.equal.ds.tokens.LightEqualColors
import ai.equal.ds.tokens.LightEqualGradients
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em

/**
 * The Equal type scale with DM Sans applied. Every field is a token from [EqualTypeTokens],
 * except the three documented derived styles at the bottom.
 */
@Immutable
data class EqualTypography(
    val headline: TextStyle,
    val title: TextStyle,
    val subheading: TextStyle,
    val bodyLarge: TextStyle,
    val body2: TextStyle,
    val body1: TextStyle,
    val body1Italic: TextStyle,
    val button: TextStyle,
    /** Figma Button M / L label: 16 SemiBold. */
    val buttonLarge: TextStyle,
    val buttonSmall: TextStyle,
    val caption: TextStyle,
    val captionWrap: TextStyle,
    val micro: TextStyle,
    /** Uppercase the string (`text.uppercase()`), never the renderer. */
    val overline: TextStyle,
    /** Uppercase the string (`text.uppercase()`), never the renderer. */
    val badge: TextStyle,
    val badgeCount: TextStyle,
    /**
     * Derived: `caption` at Regular (12/16, 400). SPEC calls this "caption Regular" for single-line
     * descriptions; once it wraps use [captionWrap].
     */
    val captionRegular: TextStyle,
    /** Derived: `micro` at Regular (tabular). SPEC: Call History Row meta line. */
    val microRegular: TextStyle,
)

/** Builds [EqualTypography] by applying [fontFamily] to every [EqualTypeTokens] style. */
fun equalTypography(fontFamily: FontFamily = DmSans): EqualTypography {
    fun TextStyle.f(): TextStyle = copy(fontFamily = fontFamily)
    return EqualTypography(
        headline = EqualTypeTokens.headline.f(),
        title = EqualTypeTokens.title.f(),
        subheading = EqualTypeTokens.subheading.f(),
        bodyLarge = EqualTypeTokens.bodyLarge.f(),
        body2 = EqualTypeTokens.body2.f(),
        body1 = EqualTypeTokens.body1.f(),
        body1Italic = EqualTypeTokens.body1Italic.f(),
        button = EqualTypeTokens.button.f(),
        buttonLarge = EqualTypeTokens.buttonLarge.f(),
        buttonSmall = EqualTypeTokens.buttonSmall.f(),
        caption = EqualTypeTokens.caption.f(),
        captionWrap = EqualTypeTokens.captionWrap.f(),
        micro = EqualTypeTokens.micro.f(),
        overline = EqualTypeTokens.overline.f(),
        badge = EqualTypeTokens.badge.f(),
        badgeCount = EqualTypeTokens.badgeCount.f(),
        captionRegular = EqualTypeTokens.caption.copy(fontWeight = FontWeight.W400).f(),
        microRegular = EqualTypeTokens.micro.copy(fontWeight = FontWeight.W400).f(),
    )
}

/** Tabular figures (phone numbers, prices, postal codes, playback pill). Typography rule 6. */
fun TextStyle.tabular(): TextStyle = copy(fontFeatureSettings = "tnum")

/** Non-Latin strings use 0em tracking. Typography rule 7. */
fun TextStyle.nonLatin(): TextStyle = copy(letterSpacing = 0.em)

internal val LocalEqualColors = staticCompositionLocalOf { LightEqualColors }
internal val LocalEqualGradients = staticCompositionLocalOf { LightEqualGradients }
internal val LocalEqualTypography = staticCompositionLocalOf { equalTypography() }

/**
 * Root theme for the Equal AI design system. Provides colours, gradients and typography
 * through CompositionLocals; read them with [EqualTheme.colors], [EqualTheme.gradients] and
 * [EqualTheme.type].
 *
 * Dark values are used exactly as the tokens define them.
 */
@Composable
fun EqualTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) DarkEqualColors else LightEqualColors
    val gradients = if (darkTheme) DarkEqualGradients else LightEqualGradients
    CompositionLocalProvider(
        LocalEqualColors provides colors,
        LocalEqualGradients provides gradients,
        LocalEqualTypography provides EqualDefaultTypography,
        content = content,
    )
}

private val EqualDefaultTypography: EqualTypography = equalTypography()

/** Accessors for the current Equal theme values. */
object EqualTheme {
    val colors: EqualColors
        @Composable
        @ReadOnlyComposable
        get() = LocalEqualColors.current

    val gradients: EqualGradients
        @Composable
        @ReadOnlyComposable
        get() = LocalEqualGradients.current

    val type: EqualTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalEqualTypography.current
}
