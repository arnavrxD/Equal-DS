package ai.equal.ds.components

import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcherOwner
import androidx.activity.findViewTreeOnBackPressedDispatcherOwner
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.lifecycle.compose.LocalLifecycleOwner
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.layout.offset
import ai.equal.ds.R
import ai.equal.ds.assets.EqualCategory
import ai.equal.ds.icons.EqualIcon
import ai.equal.ds.icons.EqualIcons
import ai.equal.ds.theme.EqualTheme
import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Text primitive for the DS. Always pass a style from `EqualTheme.type` and a colour from
 * `EqualTheme.colors`.
 */
@Composable
fun EqualText(
    text: String,
    style: TextStyle,
    color: Color,
    modifier: Modifier = Modifier,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    textAlign: TextAlign? = null,
    softWrap: Boolean = true,
) {
    val resolved = if (textAlign != null) {
        style.copy(color = color, textAlign = textAlign)
    } else {
        style.copy(color = color)
    }
    BasicText(
        text = text,
        modifier = modifier,
        style = resolved,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
    )
}

/** [EqualText] for styled spans (e.g. the bold "Today 6:45pm" in the snooze line). */
@Composable
fun EqualText(
    text: AnnotatedString,
    style: TextStyle,
    color: Color,
    modifier: Modifier = Modifier,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    textAlign: TextAlign? = null,
) {
    BasicText(
        text = text,
        modifier = modifier,
        style = if (textAlign != null) style.copy(color = color, textAlign = textAlign) else style.copy(color = color),
        overflow = overflow,
        maxLines = maxLines,
    )
}

/**
 * A Unicode emoji drawn at an icon size. It uses the platform emoji font (no DM Sans) and
 * converts [size] from dp to sp, so the glyph does not grow with the user's font scale.
 * lineHeight matches the font size. Hidden from TalkBack unless [contentDescription] is set.
 */
@Composable
fun EmojiGlyph(
    emoji: String,
    size: Dp,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    val fontSize = with(LocalDensity.current) { size.toSp() }
    // Decorative by default: the label next to it carries the meaning, so TalkBack must not read
    // the emoji name ("magic wand", "party popper"). Pass [contentDescription] when the emoji
    // stands alone and means something.
    BasicText(
        text = emoji,
        modifier = modifier.clearAndSetSemantics {
            if (contentDescription != null) this.contentDescription = contentDescription
        },
        style = TextStyle(fontSize = fontSize, lineHeight = fontSize, textAlign = TextAlign.Center),
        maxLines = 1,
        softWrap = false,
    )
}

/** 1dp horizontal divider in `stroke/subtle`. */
@Composable
fun EqualDivider(modifier: Modifier = Modifier, color: Color = EqualTheme.colors.strokeSubtle) {
    Box(
        modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(color),
    )
}

/**
 * Category avatar: the [category] image (a full circle with its own coloured background and
 * transparent corners) clipped to a circle. No extra background, no cropping.
 * [selected] adds the green check badge at the bottom-right.
 */
@Composable
fun CategoryAvatar(
    category: EqualCategory,
    size: Dp,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    contentDescription: String? = category.label,
) {
    Box(modifier.size(size)) {
        Image(
            painter = painterResource(id = category.res),
            contentDescription = contentDescription,
            modifier = Modifier
                .size(size)
                .clip(CircleShape),
            contentScale = ContentScale.Fit,
        )
        if (selected) {
            SelectedCheckBadge(Modifier.align(Alignment.BottomEnd))
        }
    }
}

/** The user's profile character (supplied art: `ds_avatar_male` / `ds_avatar_female`). */
enum class ProfileAvatarVariant { Male, Female }

/**
 * Profile avatar: a 3D character in a gold scalloped ring. [width] is the ring width. The head
 * rises above the ring on purpose, so height = width × 2000/1783 (male) or 2000/1772 (female).
 * The image is never cropped.
 */
@Composable
fun ProfileAvatar(
    variant: ProfileAvatarVariant,
    width: Dp,
    modifier: Modifier = Modifier,
    tier: PlanTier = PlanTier.Free,
    contentDescription: String? = "Your profile",
) {
    val aspect = if (variant == ProfileAvatarVariant.Male) 2000f / 1783f else 2000f / 1772f
    val res = when (tier) {
        PlanTier.Paid -> if (variant == ProfileAvatarVariant.Male) R.drawable.ds_avatar_male else R.drawable.ds_avatar_female
        PlanTier.Free -> if (variant == ProfileAvatarVariant.Male) R.drawable.ds_asset_avatar_male_bust else R.drawable.ds_asset_avatar_female_bust
    }
    // Same box for both tiers; the free bust is taller than wide, so it fits centred.
    Image(
        painter = painterResource(id = res),
        contentDescription = contentDescription,
        modifier = modifier.size(width = width, height = width * aspect),
        contentScale = ContentScale.Fit,
    )
}

/** The user's plan. [Paid]: gold background + scalloped gold ring on the avatar; [Free]: the plain bust. */
enum class PlanTier { Free, Paid }

/** Disc colours for saved-contact initials: six hues at the same saturation and lightness (AA in both themes). */
enum class ContactTone { Blue, Green, Purple, Orange, Pink, Teal }

/** Stable tone for a contact, the same hash as web `contactTone`: the same initials always get the same colour. */
fun contactTone(seed: String): ContactTone {
    var h = 0L
    for (ch in seed.uppercase()) h = (h * 31 + ch.code) and 0xFFFFFFFFL
    return ContactTone.entries[(h % ContactTone.entries.size).toInt()]
}

/**
 * Initials avatar for saved contacts, matching web: a tinted circle ([tone], picked from the
 * initials by default so a list isn't all one colour) with initials in the matching dark tint,
 * Medium at 37.5% of [size] (36sp at 96, 18sp at 48, 15sp at 40), plus the saved-contact badge.
 */
@Composable
fun InitialsAvatar(
    initials: String,
    size: Dp,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    savedBadge: Boolean = true,
    tone: ContactTone = contactTone(initials),
) {
    val colors = EqualTheme.colors
    val (discBg, discText) = when (tone) {
        ContactTone.Blue -> colors.contactAvatarBlueBg to colors.contactAvatarBlueText
        ContactTone.Green -> colors.contactAvatarGreenBg to colors.contactAvatarGreenText
        ContactTone.Purple -> colors.contactAvatarPurpleBg to colors.contactAvatarPurpleText
        ContactTone.Orange -> colors.contactAvatarOrangeBg to colors.contactAvatarOrangeText
        ContactTone.Pink -> colors.contactAvatarPinkBg to colors.contactAvatarPinkText
        ContactTone.Teal -> colors.contactAvatarTealBg to colors.contactAvatarTealText
    }
    val fontSize = with(LocalDensity.current) { (size * 0.375f).toSp() }
    val style = EqualTheme.type.subheading.copy(fontSize = fontSize, lineHeight = fontSize * 1.2f, fontWeight = FontWeight.Medium)
    val badge = size * 0.36f
    Box(modifier.size(size)) {
        Box(
            Modifier
                .size(size)
                .background(discBg, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            EqualText(text = initials.take(2).uppercase(), style = style, color = discText, maxLines = 1)
        }
        if (selected) {
            SelectedCheckBadge(Modifier.align(Alignment.BottomEnd))
        } else if (savedBadge) {
            // Saved-contact signifier: filled person on a disc that cuts into the avatar's bottom-right.
            Box(
                Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = size * 0.1f)
                    .size(badge)
                    .background(colors.contactAvatarBadgeBg, CircleShape)
                    .semantics { contentDescription = "Saved contact" },
                contentAlignment = Alignment.Center,
            ) {
                EqualIcon(EqualIcons.Person, contentDescription = null, tint = colors.contactAvatarBadgeIcon, size = badge * 0.7f)
            }
        }
    }
}

/**
 * Contact / chat avatar: [initials] for saved contacts (light-blue disc + the person badge, the
 * signifier for a known contact), otherwise the [category] image (`null` falls back to
 * [EqualCategory.NA], the unknown-caller avatar).
 */
@Composable
fun ContactAvatar(
    size: Dp,
    modifier: Modifier = Modifier,
    category: EqualCategory? = EqualCategory.NA,
    initials: String? = null,
    selected: Boolean = false,
) {
    if (initials != null) {
        InitialsAvatar(initials = initials, size = size, modifier = modifier, selected = selected)
    } else {
        CategoryAvatar(category = category ?: EqualCategory.NA, size = size, modifier = modifier, selected = selected)
    }
}

/** 18dp green check with a 2dp `bg/surface` ring (SPEC §6 selected row). */
@Composable
internal fun SelectedCheckBadge(modifier: Modifier = Modifier) {
    val colors = EqualTheme.colors
    Box(
        modifier
            .size(22.dp)
            .background(colors.bgSurface, CircleShape)
            .padding(2.dp)
            .background(colors.buttonPrimary, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        EqualIcon(EqualIcons.Check, contentDescription = "Selected", tint = colors.textOnAction, size = 12.dp)
    }
}

/** Plain 48dp circular icon button (no fill) used in headers and rows. */
@Composable
internal fun PlainIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    tint: Color,
    modifier: Modifier = Modifier,
    iconSize: Dp = 24.dp,
    enabled: Boolean = true,
) {
    Box(
        modifier
            .minimumInteractiveComponentSize()
            .size(48.dp)
            .clip(CircleShape)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        EqualIcon(imageVector = icon, contentDescription = contentDescription, tint = tint, size = iconSize)
    }
}

@Composable
internal fun rememberInteraction(): MutableInteractionSource = remember { MutableInteractionSource() }

/**
 * The Android design frame: every screen starts at 390 × 800dp and extends only vertically when
 * the content needs it (previews of long screens pass a larger heightDp, never a different width).
 */
const val EqualFrameWidthDp = 390
const val EqualFrameHeightDp = 800

/** Light + dark multipreview at the frame width. `EqualTheme()` follows the preview's uiMode. */
@Preview(name = "Light", showBackground = true, widthDp = EqualFrameWidthDp)
@Preview(
    name = "Dark",
    showBackground = true,
    widthDp = EqualFrameWidthDp,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
)
annotation class EqualPreviews

/** Preview frame: EqualTheme + `bg/primary` + 16dp padding. */
@Composable
internal fun PreviewFrame(
    padding: Dp = 16.dp,
    content: @Composable ColumnScope.() -> Unit,
) {
    EqualTheme {
        Column(
            Modifier
                .background(EqualTheme.colors.bgPrimary)
                .padding(padding),
            content = content,
        )
    }
}

@Composable
internal fun VSpace(height: Dp) {
    Spacer(Modifier.height(height))
}

/**
 * True when the system "Remove animations" setting is on (`Settings.Global.ANIMATOR_DURATION_SCALE
 * == 0`), the Android counterpart of `prefers-reduced-motion: reduce`. Looping decoration (crown
 * sway, sunburst, gold sheen) and entrance motion hold still when it is.
 */
@Composable
internal fun rememberReduceMotion(): Boolean {
    val context = androidx.compose.ui.platform.LocalContext.current
    return remember(context) {
        android.provider.Settings.Global.getFloat(
            context.contentResolver,
            android.provider.Settings.Global.ANIMATOR_DURATION_SCALE,
            1f,
        ) == 0f
    }
}


/**
 * System Back for overlays (sheets, dialogs, menus): while [enabled], Back calls [onBack] instead
 * of leaving the screen. Built on the platform OnBackPressedDispatcher, so it works in any
 * ComponentActivity and is a no-op in previews.
 */
@Composable
internal fun EqualBackHandler(enabled: Boolean = true, onBack: () -> Unit) {
    val currentOnBack by rememberUpdatedState(onBack)
    val callback = remember {
        object : OnBackPressedCallback(enabled) {
            override fun handleOnBackPressed() {
                currentOnBack()
            }
        }
    }
    SideEffect { callback.isEnabled = enabled }
    val view = LocalView.current
    val context = LocalContext.current
    val owner = remember(view, context) { view.findViewTreeOnBackPressedDispatcherOwner() ?: context.findBackOwner() }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(owner, lifecycleOwner, callback) {
        owner?.onBackPressedDispatcher?.addCallback(lifecycleOwner, callback)
        onDispose { callback.remove() }
    }
}

private tailrec fun Context.findBackOwner(): OnBackPressedDispatcherOwner? = when (this) {
    is OnBackPressedDispatcherOwner -> this
    is ContextWrapper -> baseContext.findBackOwner()
    else -> null
}

/** Names an overlay surface for TalkBack (announced when it appears). */
internal fun Modifier.overlayPane(title: String): Modifier = semantics { paneTitle = title }

/** [enter] unless reduced motion is on, then no transition. */
internal fun EnterTransition.unlessReduced(reduce: Boolean): EnterTransition = if (reduce) EnterTransition.None else this

/** [exit] unless reduced motion is on, then no transition. */
internal fun ExitTransition.unlessReduced(reduce: Boolean): ExitTransition = if (reduce) ExitTransition.None else this

/**
 * A touch area (and TalkBack focus area) of at least [min] × [min] for a control drawn smaller,
 * without changing the layout: the node lays out at its visual size and the extra area spills
 * evenly past its edges (Compose hit-tests outside a parent's bounds unless an ancestor clips).
 * Put it first in the chain, before the clickable; draw the visual (size, clip, background) on
 * a child. Use [minimumInteractiveComponentSize] instead where the layout has room for 48dp.
 * [minHeight] can be lowered for tightly stacked targets (an A–Z index) so they don't overlap.
 */
internal fun Modifier.touchTarget(min: Dp = 48.dp, minHeight: Dp = min): Modifier = layout { measurable, constraints ->
    val visualW = measurable.maxIntrinsicWidth(Constraints.Infinity).coerceIn(constraints.minWidth, constraints.maxWidth)
    val visualH = measurable.maxIntrinsicHeight(visualW).coerceIn(constraints.minHeight, constraints.maxHeight)
    val touchW = maxOf(visualW, min.roundToPx())
    val touchH = maxOf(visualH, minHeight.roundToPx())
    val placeable = measurable.measure(Constraints.fixed(touchW, touchH))
    layout(visualW, visualH) {
        placeable.place((visualW - touchW) / 2, (visualH - touchH) / 2)
    }
}
