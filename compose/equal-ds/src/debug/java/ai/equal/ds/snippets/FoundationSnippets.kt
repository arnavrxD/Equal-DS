// Compose code shown on the docs site's Foundations pages ("foundations / <page>"). Extracted by
// scripts/build-snippets.mjs; debug source set only.
@file:Suppress("unused")

package ai.equal.ds.snippets

import ai.equal.ds.assets.EqualCategory
import ai.equal.ds.components.CategoryAvatar
import ai.equal.ds.components.ChatWallpaper
import ai.equal.ds.components.EmojiGlyph
import ai.equal.ds.components.EqualLogo
import ai.equal.ds.components.EqualLogoOrb
import ai.equal.ds.components.EqualMascot
import ai.equal.ds.components.EqualText
import ai.equal.ds.components.ProfileAvatar
import ai.equal.ds.components.ProfileAvatarVariant
import ai.equal.ds.icons.EqualIcon
import ai.equal.ds.icons.EqualIcons
import ai.equal.ds.theme.EqualShadows
import ai.equal.ds.theme.EqualTheme
import ai.equal.ds.theme.eqShadow
import ai.equal.ds.theme.toBrush
import ai.equal.ds.tokens.EqualRadius
import ai.equal.ds.tokens.EqualSpacing
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// snippet: foundations / colour
@Composable
internal fun FoundationColourSnippet() {
    // Wrap the app once; colours switch with the system theme (or pass darkTheme = …)
    EqualTheme {
        val colors = EqualTheme.colors
        Box(
            Modifier
                .background(colors.bgSurface, RoundedCornerShape(EqualRadius.rLg))
                .padding(EqualSpacing.s16),
        ) {
            EqualText(text = "Equal AI", style = EqualTheme.type.subheading, color = colors.textAction)
        }
        // Gradients are tokens too
        Box(Modifier.size(64.dp).background(EqualTheme.gradients.fab.toBrush(), RoundedCornerShape(EqualRadius.r2xl)))
    }
}

// snippet: foundations / typography
@Composable
internal fun FoundationTypographySnippet() {
    val type = EqualTheme.type
    val colors = EqualTheme.colors
    Column {
        EqualText(text = "Amit Nanda", style = type.headline, color = colors.textPrimary)
        EqualText(text = "Assistant Health Check", style = type.subheading, color = colors.textPrimary)
        EqualText(text = "Check Real-Time Assistant Connectivity", style = type.caption, color = colors.textSecondary)
        EqualText(text = "CALL HISTORY", style = type.overline, color = colors.textSecondary)
    }
}

// snippet: foundations / layout
@Composable
internal fun FoundationLayoutSnippet() {
    val colors = EqualTheme.colors
    val shape = RoundedCornerShape(EqualRadius.rLg)
    Column(verticalArrangement = Arrangement.spacedBy(EqualSpacing.s12)) {
        Box(
            Modifier
                .eqShadow(EqualShadows.card, shape)
                .background(colors.bgSurface, shape)
                .padding(horizontal = EqualSpacing.s16, vertical = EqualSpacing.s12),
        ) {
            EqualText(text = "Card: radius rLg, padding s16 / s12, shadow card", style = EqualTheme.type.body1, color = colors.textPrimary)
        }
    }
}

// snippet: foundations / icons
@Composable
internal fun FoundationIconsSnippet() {
    val colors = EqualTheme.colors
    Row(horizontalArrangement = Arrangement.spacedBy(EqualSpacing.s16)) {
        EqualIcon(EqualIcons.Call, contentDescription = "Call", tint = colors.textAction)
        EqualIcon(EqualIcons.Instructions, contentDescription = null, tint = colors.textPrimary)
        EqualIcon(EqualIcons.SilentMode, contentDescription = "Mute", tint = colors.textSecondary, size = 20.dp)
    }
}

// snippet: foundations / emoji
@Composable
internal fun FoundationEmojiSnippet() {
    // Actions use Unicode emoji, never icon images
    Row(horizontalArrangement = Arrangement.spacedBy(EqualSpacing.s8)) {
        EmojiGlyph(emoji = "📞", size = 16.dp)
        EmojiGlyph(emoji = "🗓️", size = 16.dp)
        EmojiGlyph(emoji = "✏️", size = 16.dp)
    }
}

// snippet: foundations / brand
@Composable
internal fun FoundationBrandSnippet() {
    Column(verticalArrangement = Arrangement.spacedBy(EqualSpacing.s16)) {
        EqualLogo(height = 20.dp) // green in light, lime in dark
        Row(horizontalArrangement = Arrangement.spacedBy(EqualSpacing.s12)) {
            EqualLogoOrb(size = 36.dp)
            EqualMascot(size = 40.dp)
            CategoryAvatar(category = EqualCategory.Delivery, size = 48.dp)
            ProfileAvatar(variant = ProfileAvatarVariant.Female, width = 48.dp)
        }
        ChatWallpaper {
            EqualText(text = "Messages sit on the wallpaper", style = EqualTheme.type.body1, color = EqualTheme.colors.textPrimary)
        }
    }
}
