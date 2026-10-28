package ai.equal.ds.components

import ai.equal.ds.assets.EqualAssetImage
import ai.equal.ds.assets.EqualAssets
import ai.equal.ds.assets.rememberIsPlaceholder
import ai.equal.ds.icons.EqualIcon
import ai.equal.ds.icons.EqualIcons
import ai.equal.ds.theme.EqualEasing
import ai.equal.ds.theme.EqualShadow
import ai.equal.ds.theme.EqualShadowLayer
import ai.equal.ds.theme.EqualShadows
import ai.equal.ds.theme.EqualTheme
import ai.equal.ds.theme.eqShadow
import ai.equal.ds.theme.toBrush
import ai.equal.ds.tokens.EqualMotion
import ai.equal.ds.tokens.EqualPrimitives
import ai.equal.ds.tokens.EqualRadius
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.PaddingValues
import ai.equal.ds.tokens.EqualColors
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.indication
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.BlendMode
import ai.equal.ds.R
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties

// Components for the Profile, Addresses and Instructions flows (the flow designs).
// Mirrors web/src/components/Flows.tsx + Flows.css.

// =============================================================================================
// Shared
// =============================================================================================

/** Card shadow used by the flow cards (web: 0 1px 2px black/4%). */
private val FlowCardShadow = EqualShadow(listOf(EqualShadowLayer(0.dp, 1.dp, 2.dp, EqualPrimitives.black.copy(alpha = 0.04f))))

/** Gender card shadow (web: 0 1px 2px black/5%). */
private val GenderCardShadow = EqualShadow(listOf(EqualShadowLayer(0.dp, 1.dp, 2.dp, EqualPrimitives.black.copy(alpha = 0.05f))))

/**
 * Flow card surface: `bg/surface`, radius 16, padding 14 / 16. [elevated] adds the faint card
 * shadow, otherwise a 1dp `stroke/subtle` border (Recommended, Preset). Dark mode always gets the
 * `stroke/subtle` border.
 */
@Composable
private fun Modifier.flowCard(elevated: Boolean = true): Modifier {
    val colors = EqualTheme.colors
    val shape = RoundedCornerShape(EqualRadius.rLg)
    val shadow = if (elevated) Modifier.eqShadow(FlowCardShadow, shape) else Modifier
    val bordered = !elevated || colors.isDark
    return this
        .then(shadow)
        .background(colors.bgSurface, shape)
        .then(if (bordered) Modifier.border(1.dp, colors.strokeSubtle, shape) else Modifier)
}

/** Centred 3D illustration at the top of a settings detail screen (Silent Mode, WhatsApp Updates). */
@Composable
fun IllustrationHeader(
    asset: EqualAssets,
    modifier: Modifier = Modifier,
    size: Dp = 160.dp,
) {
    Box(
        modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        EqualAssetImage(asset = asset, size = size)
    }
}

/**
 * More-vert button (32dp, icon 20, 48dp touch area; exposes Expanded / Collapsed) that opens an anchored [ContextMenu] (min width 168) in a
 * focusable popup, 30dp below and 6dp in from the right edge. Selecting an item closes the menu
 * first; tapping outside or Back dismisses it. The open state is ephemeral UI state kept here.
 */
@Composable
fun OverflowMenu(
    items: List<ContextMenuItem>,
    modifier: Modifier = Modifier,
    contentDescription: String = "More options",
) {
    val colors = EqualTheme.colors
    var open by remember { mutableStateOf(false) }
    val density = LocalDensity.current
    val offset = with(density) { IntOffset(x = -6.dp.roundToPx(), y = 30.dp.roundToPx()) }
    Box(modifier) {
        val interaction = rememberInteraction()
        // 48dp touch area around the 32dp button (layout unchanged); TalkBack hears Expanded / Collapsed.
        Box(
            Modifier
                .touchTarget()
                .semantics { stateDescription = if (open) "Expanded" else "Collapsed" }
                .clickable(interactionSource = interaction, indication = null, role = Role.Button) { open = !open },
            contentAlignment = Alignment.Center,
        ) {
            Box(
                Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .indication(interaction, LocalIndication.current),
                contentAlignment = Alignment.Center,
            ) {
                EqualIcon(EqualIcons.MoreVert, contentDescription = contentDescription, tint = colors.textPrimary, size = 20.dp)
            }
        }
        if (open) {
            Popup(
                alignment = Alignment.TopEnd,
                offset = offset,
                onDismissRequest = { open = false },
                properties = PopupProperties(focusable = true),
            ) {
                ContextMenu(
                    items = items.map { item ->
                        item.copy(onClick = {
                            open = false
                            item.onClick()
                        })
                    },
                    minWidth = 168.dp,
                )
            }
        }
    }
}

/** One wallpaper tile in dp: the pattern file is 1080 × 2250 px, i.e. 360 × 750dp at 3× (≈9.7 MB decoded; at 8% opacity 4× is indistinguishable). */
private val WallpaperTileWidth = 360.dp
private val WallpaperTileHeight = 750.dp

/**
 * Doodle chat wallpaper: `chat/wallpaper-bg`, then `ds_chat_pattern` (an alpha-only mask in
 * `drawable-nodpi`) tiled at exactly 360 × 750dp in both directions and tinted
 * `chat/wallpaper-pattern` (SrcIn). Used behind chat message lists and the setup [FullSheet].
 */
@Composable
fun ChatWallpaper(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val colors = EqualTheme.colors
    val pattern = ImageBitmap.imageResource(R.drawable.ds_chat_pattern)
    val tint = remember(colors.chatWallpaperPattern) { ColorFilter.tint(colors.chatWallpaperPattern, BlendMode.SrcIn) }
    Box(
        modifier.drawBehind {
            drawRect(colors.chatWallpaperBg)
            val tileW = WallpaperTileWidth.roundToPx()
            val tileH = WallpaperTileHeight.roundToPx()
            if (tileW <= 0 || tileH <= 0) return@drawBehind
            val src = IntSize(pattern.width, pattern.height)
            clipRect {
                var y = 0
                while (y < size.height) {
                    var x = 0
                    while (x < size.width) {
                        drawImage(
                            image = pattern,
                            srcOffset = IntOffset.Zero,
                            srcSize = src,
                            dstOffset = IntOffset(x, y),
                            dstSize = IntSize(tileW, tileH),
                            colorFilter = tint,
                        )
                        x += tileW
                    }
                    y += tileH
                }
            }
        },
    ) {
        content()
    }
}

// =============================================================================================
// Profile flow
// =============================================================================================

/** Gender options for [GenderSelector]. */
enum class Gender { Male, Female }

/**
 * Gender picker: two cards (gap 12), 120 high, radius 16. Each has a faint ♂ / ♀ watermark
 * (96, 5% `text/primary`, bottom-left, clipped), the label in `subheading` (12 from the top) and
 * the bust art at the bottom centre (`avatar-male-bust` / `avatar-female-bust`, 60 × 56; a 52dp
 * [ProfileAvatar] while those are placeholders).
 * Selected: `gradient/gender-selected` + 2dp `gradient/gender-selected-stroke` border (green →
 * lime → green; lime in dark). Unselected: `bg/surface`, 2dp `gender/stroke` border.
 */
@Composable
fun GenderSelector(
    value: Gender,
    onValueChange: (Gender) -> Unit,
    modifier: Modifier = Modifier,
    /** Free: the plain bust. Paid: the avatar on its gold background and ring. */
    tier: PlanTier = PlanTier.Free,
) {
    Row(
        modifier
            .fillMaxWidth()
            .selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        GenderCard(Gender.Male, selected = value == Gender.Male, tier = tier, onClick = { onValueChange(Gender.Male) }, modifier = Modifier.weight(1f))
        GenderCard(Gender.Female, selected = value == Gender.Female, tier = tier, onClick = { onValueChange(Gender.Female) }, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun GenderCard(gender: Gender, selected: Boolean, tier: PlanTier, onClick: () -> Unit, modifier: Modifier) {
    val colors = EqualTheme.colors
    val shape = RoundedCornerShape(EqualRadius.rLg)
    // Selected: the green-lime-green `gradient/gender-selected-stroke`; otherwise `gender/stroke`.
    val borderBrush = if (selected) EqualTheme.gradients.genderSelectedStroke.toBrush() else SolidColor(colors.genderStroke)
    val fill = if (selected) {
        Modifier.background(EqualTheme.gradients.genderSelected.toBrush(), shape)
    } else {
        Modifier.background(colors.bgSurface, shape)
    }
    val bust = if (gender == Gender.Male) EqualAssets.AvatarMaleBust else EqualAssets.AvatarFemaleBust
    val bustIsPlaceholder = rememberIsPlaceholder(bust)
    val markSize = with(LocalDensity.current) { 96.dp.toSp() }
    Box(
        modifier
            .height(120.dp)
            .eqShadow(GenderCardShadow, shape)
            .then(fill)
            .border(2.dp, borderBrush, shape)
            .clip(shape)
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick),
    ) {
        // Decorative watermark; sized in dp (converted) so it doesn't scale with the font size.
        BasicText(
            text = if (gender == Gender.Male) "♂" else "♀",
            style = TextStyle(fontSize = markSize, lineHeight = markSize, color = colors.textPrimary.copy(alpha = 0.05f)),
            modifier = Modifier
                .clearAndSetSemantics {}
                .align(Alignment.BottomStart)
                .offset(x = (-6).dp, y = 18.dp),
        )
        EqualText(
            text = if (gender == Gender.Male) "Male" else "Female",
            style = EqualTheme.type.subheading,
            color = colors.textPrimary,
            maxLines = 1,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 12.dp),
        )
        Box(Modifier.align(Alignment.BottomCenter)) {
            if (tier == PlanTier.Paid || bustIsPlaceholder) {
                ProfileAvatar(
                    variant = if (gender == Gender.Male) ProfileAvatarVariant.Male else ProfileAvatarVariant.Female,
                    width = 52.dp,
                    tier = PlanTier.Paid,
                    contentDescription = null,
                )
            } else {
                EqualAssetImage(asset = bust, size = DpSize(60.dp, 56.dp))
            }
        }
    }
}

// =============================================================================================
// Addresses flow
// =============================================================================================

/** A saved address. [line2] and [direction] are optional (blank = absent). */
@Immutable
data class SavedAddress(
    val id: String,
    val label: String,
    val line1: String,
    val line2: String? = null,
    val city: String,
    val pincode: String,
    val direction: String? = null,
)

/** "line1, line2, City- 500032" (blank parts skipped), as on web. */
fun formatAddress(address: SavedAddress): String =
    listOf(address.line1, address.line2.orEmpty(), "${address.city}- ${address.pincode}")
        .filter { it.isNotBlank() }
        .joinToString(", ")

/**
 * Saved address card: label `subheading`, address `captionWrap` `text/secondary` (4 below), and an
 * [OverflowMenu] with Edit / Delete address (danger) when [onEdit] / [onDelete] are set.
 */
@Composable
fun AddressCard(
    label: String,
    address: String,
    modifier: Modifier = Modifier,
    onEdit: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
) {
    val colors = EqualTheme.colors
    Row(
        modifier
            .fillMaxWidth()
            .flowCard()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Column(Modifier.weight(1f)) {
            EqualText(text = label, style = EqualTheme.type.subheading, color = colors.textPrimary)
            Spacer(Modifier.height(4.dp))
            EqualText(text = address, style = EqualTheme.type.captionWrap, color = colors.textSecondary)
        }
        val items = editDeleteItems(onEdit, onDelete, deleteLabel = "Delete address")
        if (items.isNotEmpty()) {
            Spacer(Modifier.width(8.dp))
            OverflowMenu(
                items = items,
                contentDescription = "$label options",
                modifier = Modifier.offset(x = 8.dp, y = (-6).dp),
            )
        }
    }
}

private fun editDeleteItems(onEdit: (() -> Unit)?, onDelete: (() -> Unit)?, deleteLabel: String): List<ContextMenuItem> =
    buildList {
        if (onEdit != null) add(ContextMenuItem("Edit", EqualIcons.Edit, onClick = onEdit))
        if (onDelete != null) add(ContextMenuItem(deleteLabel, EqualIcons.Delete, onClick = onDelete, destructive = true))
    }

private val AddressLabels = listOf("Home", "Work", "Other")
private val SixDigits = Regex("^\\d{6}$")

/**
 * Add / edit address form: LABEL* chips (Home / Work / Other) → ADDRESS: two multiline fields,
 * then City + Pincode (centred; pincode digits only, max 6, error unless 6) → optional Guiding
 * direction → Save (block, lg), enabled once line 1, city and a valid pincode are filled.
 *
 * The form keeps its own draft state, seeded from [initial] (key it by the address being edited).
 * [onSave] gets the draft with `initial.id` (empty for a new address; the caller assigns one).
 */
@Composable
fun AddressForm(
    onSave: (SavedAddress) -> Unit,
    modifier: Modifier = Modifier,
    initial: SavedAddress? = null,
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    val initialLabel = when {
        initial == null -> "Home"
        initial.label in AddressLabels -> initial.label
        else -> "Other"
    }
    var labelIndex by remember { mutableIntStateOf(AddressLabels.indexOf(initialLabel)) }
    var line1 by remember { mutableStateOf(initial?.line1.orEmpty()) }
    var line2 by remember { mutableStateOf(initial?.line2.orEmpty()) }
    var city by remember { mutableStateOf(initial?.city.orEmpty()) }
    var pincode by remember { mutableStateOf(initial?.pincode.orEmpty()) }
    var direction by remember { mutableStateOf(initial?.direction.orEmpty()) }
    val pinValid = SixDigits.matches(pincode)
    val pinError = if (pincode.isNotEmpty() && !pinValid) "Enter a 6-digit pincode" else null
    val canSave = line1.isNotBlank() && city.isNotBlank() && pinValid

    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Column {
            EqualText(text = "LABEL*", style = type.overline, color = colors.textSecondary)
            Spacer(Modifier.height(10.dp))
            SegmentedChips(options = AddressLabels, selectedIndex = labelIndex, onSelectedIndexChange = { labelIndex = it }, contentDescription = "Address label")
        }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            EqualText(text = "ADDRESS", style = type.overline, color = colors.textSecondary)
            EqualTextField(
                value = line1,
                onValueChange = { line1 = it },
                placeholder = "Flat, house no., building",
                multiline = true,
                contentDescription = "Address line 1",
            )
            EqualTextField(
                value = line2,
                onValueChange = { line2 = it },
                placeholder = "Street, area, landmark",
                multiline = true,
                contentDescription = "Address line 2",
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.Top) {
                EqualTextField(
                    value = city,
                    onValueChange = { city = it },
                    placeholder = "City",
                    align = TextFieldAlign.Center,
                    contentDescription = "City",
                    modifier = Modifier.weight(1f),
                )
                EqualTextField(
                    value = pincode,
                    onValueChange = { v -> pincode = v.filter { it.isDigit() }.take(6) },
                    placeholder = "Pincode",
                    align = TextFieldAlign.Center,
                    tabular = true,
                    error = pinError,
                    contentDescription = "Pincode",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                )
            }
        }
        EqualTextField(
            value = direction,
            onValueChange = { direction = it },
            label = "Guiding direction",
            optional = true,
            placeholder = "e.g. Near City Center Mall",
        )
        EqualButton(
            text = "Save",
            onClick = {
                if (canSave) {
                    onSave(
                        SavedAddress(
                            id = initial?.id.orEmpty(),
                            label = AddressLabels[labelIndex],
                            line1 = line1,
                            line2 = line2.ifBlank { null },
                            city = city,
                            pincode = pincode,
                            direction = direction.ifBlank { null },
                        ),
                    )
                }
            },
            size = EqualButtonSize.Block,
            enabled = canSave,
        )
    }
}

// =============================================================================================
// Instructions flow
// =============================================================================================

/** The 3D art of the three presets. */
enum class PresetArt(val asset: EqualAssets) {
    ScreenProtect(EqualAssets.PresetScreenProtect),
    EverydayCalls(EqualAssets.PresetEverydayCalls),
    Business(EqualAssets.PresetBusiness),
}

/** Where an instruction came from (a preset), shown as the [SourceBadge] on active instructions. */
@Immutable
data class InstructionSource(val art: PresetArt, val label: String)

/**
 * An assistant instruction. [whenText] / [doText] are the web `when` / `do` lines (Kotlin
 * keywords). [doText] may contain `{key}` placeholders filled by the setup chat.
 */
@Immutable
data class Instruction(
    val id: String,
    val title: String,
    val whenText: String,
    val doText: String,
    val source: InstructionSource? = null,
)

/**
 * Tinted "+ Apply" pill (Preset Instruction design): `button/tonal` (brand green 8 %), add 16 +
 * `buttonSmall` in `text/action`, 24 high. Used on recommended cards and preset instructions.
 */
@Composable
fun ApplyButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String = "Apply",
    contentDescription: String? = null,
) {
    EqualButton(
        text = text,
        onClick = onClick,
        modifier = if (contentDescription != null) modifier.semantics { this.contentDescription = contentDescription } else modifier,
        variant = EqualButtonVariant.Tonal,
        size = EqualButtonSize.Xs,
        leadingIcon = EqualIcons.Add,
    )
}

/**
 * "Recommended for you" card: 191 × 114 min, radius 12, `bg/surface`, 1dp `stroke/subtle`,
 * padding 12. Emoji 24 + [ApplyButton] → title 14 SemiBold → description 12/16 `text/primary`.
 */
@Composable
fun RecommendedCard(
    emoji: String,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    onAdd: (() -> Unit)? = null,
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    val shape = RoundedCornerShape(EqualRadius.rMd)
    Column(
        modifier
            .width(191.dp)
            .defaultMinSize(minHeight = 114.dp)
            .background(colors.bgSurface, shape)
            .border(1.dp, colors.strokeSubtle, shape)
            .padding(12.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(Modifier.fillMaxWidth().defaultMinSize(minHeight = 28.dp), verticalAlignment = Alignment.CenterVertically) {
            EmojiGlyph(emoji = emoji, size = 24.dp)
            Spacer(Modifier.weight(1f))
            if (onAdd != null) ApplyButton(onClick = onAdd, contentDescription = "Apply $title")
        }
        Spacer(Modifier.height(8.dp))
        Column {
            EqualText(text = title, style = type.body2.copy(fontWeight = FontWeight.SemiBold, lineHeight = 18.sp), color = colors.textPrimary)
            EqualText(text = description, style = type.captionRegular, color = colors.textPrimary)
        }
    }
}

/**
 * Shown in place of recommendations when all are applied or none exist: radius 16, `bg/primary`,
 * 1dp `stroke/medium`, three soft `suggest/glow` circles, orb 18 + "New suggestions coming…"
 * (12 Bold `text/secondary`), 13 → `body2` line (max 184 wide), and the 140dp
 * `illustration-suggestions` at the top-right.
 */
@Composable
fun SuggestionsComingCard(
    modifier: Modifier = Modifier,
    label: String = "New suggestions coming…",
    body: String = "More suggestions will show up here as I learn your calls",
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    val shape = RoundedCornerShape(EqualRadius.rLg)
    val glow = colors.suggestGlow
    Box(
        modifier
            .fillMaxWidth()
            .height(116.dp)
            .clip(shape)
            .background(colors.bgPrimary)
            .border(1.dp, colors.strokeMedium, shape)
            // Figma: #00B140 @ 20 % circles under a 50dp Gaussian blur, drawn as soft radial falloffs.
            .drawBehind {
                listOf(Triple(334.5f, 194.5f, 44.5f), Triple(22f, 201f, 34f), Triple(172f, 110f, 65f)).forEach { (cx, cy, r) ->
                    val center = Offset(cx.dp.toPx(), cy.dp.toPx())
                    val radius = (r + 100f).dp.toPx()
                    drawCircle(Brush.radialGradient(listOf(glow, glow.copy(alpha = 0f)), center, radius), radius, center)
                }
            },
    ) {
        EqualAssetImage(
            asset = EqualAssets.IllustrationSuggestions,
            size = 140.dp,
            modifier = Modifier.align(Alignment.TopEnd).offset(x = 6.dp, y = (-5).dp),
        )
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.height(24.dp), verticalAlignment = Alignment.CenterVertically) {
                EqualLogoOrb(size = 18.dp)
                Spacer(Modifier.width(4.dp))
                EqualText(text = label, style = type.caption.copy(fontWeight = FontWeight.Bold), color = colors.textSecondary, maxLines = 1)
            }
            Spacer(Modifier.height(13.dp))
            EqualText(text = body, style = type.body2.copy(lineHeight = 18.sp), color = colors.textPrimary, modifier = Modifier.widthIn(max = 184.dp))
        }
    }
}

/** Tint behind a preset's art: Green = Screen & Protect, Blue = Handle everyday calls, Orange = Run my business, Lime = Active Instructions. */
enum class PresetTint { Green, Blue, Orange, Lime }

/** 3D art in its tinted circle (44dp circle, art 75 % of it). */
@Composable
fun PresetIcon(asset: EqualAssets, tint: PresetTint, modifier: Modifier = Modifier, size: Dp = 44.dp) {
    val colors = EqualTheme.colors
    val tintColor = when (tint) {
        PresetTint.Green -> colors.presetTintGreen
        PresetTint.Blue -> colors.presetTintBlue
        PresetTint.Orange -> colors.presetTintOrange
        PresetTint.Lime -> colors.presetTintLime
    }
    Box(modifier.size(size).clip(CircleShape).background(tintColor), contentAlignment = Alignment.Center) {
        EqualAssetImage(asset = asset, size = size * 0.75f)
    }
}

/** Shared shell of [PresetCard] and [ActiveInstructionsRow]: radius 16, `bg/surface`, 1dp `stroke/subtle`. */
@Composable
private fun PresetRowShell(onClick: () -> Unit, minHeight: Dp, padding: PaddingValues, modifier: Modifier, content: @Composable RowScope.() -> Unit) {
    val colors = EqualTheme.colors
    val shape = RoundedCornerShape(EqualRadius.rLg)
    Row(
        modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = minHeight)
            .clip(shape)
            .background(colors.bgSurface)
            .border(1.dp, colors.strokeSubtle, shape)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(padding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        content()
        EqualIcon(EqualIcons.ChevronRight, contentDescription = null, tint = colors.textPlaceholder, size = 22.dp)
    }
}

/**
 * "Browse presets" card (320 × 85): [PresetIcon] → 8 → title 16 SemiBold → description 12/16
 * `text/secondary` → chevron 22 `text/placeholder`.
 */
@Composable
fun PresetCard(
    art: PresetArt,
    tint: PresetTint,
    title: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    PresetRowShell(onClick, 85.dp, PaddingValues(start = 13.dp, top = 12.dp, end = 12.dp, bottom = 12.dp), modifier) {
        PresetIcon(asset = art.asset, tint = tint)
        Column(Modifier.weight(1f)) {
            EqualText(text = title, style = type.subheading.copy(fontWeight = FontWeight.SemiBold, lineHeight = 19.sp), color = colors.textPrimary)
            Spacer(Modifier.height(2.dp))
            EqualText(text = description, style = type.captionRegular, color = colors.textSecondary)
        }
    }
}

/**
 * "Your instructions" entry (319 × 76): the robot-with-wand on the lime tint → Active Instructions
 * (14 SemiBold) over "N active instructions" (12 `text/secondary`) → chevron.
 */
@Composable
fun ActiveInstructionsRow(count: Int, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    PresetRowShell(onClick, 76.dp, PaddingValues(start = 14.dp, top = 15.dp, end = 16.dp, bottom = 15.dp), modifier) {
        PresetIcon(asset = EqualAssets.MascotInstructions, tint = PresetTint.Lime)
        Column(Modifier.weight(1f)) {
            EqualText(text = "Active Instructions", style = type.body2.copy(fontWeight = FontWeight.SemiBold), color = colors.textPrimary, maxLines = 1)
            EqualText(
                text = if (count > 0) "$count Active" else "Manage how your assistant handles calls",
                style = type.captionRegular,
                color = colors.textSecondary,
                maxLines = 1,
            )
        }
    }
}

/** Which line of an instruction a [WhenDoRow] shows. */
enum class WhenDoKind { When, Do }

/**
 * When / Do line of an instruction (preset and active instructions alike): a 52 × 24 pill fading
 * from `gradient/when-chip` / `gradient/do-chip` to the card colour, WHEN / DO 12 SemiBold caps
 * `text/secondary` → 8 → text 14/18 `text/primary`.
 */
@Composable
fun WhenDoRow(
    kind: WhenDoKind,
    text: AnnotatedString,
    modifier: Modifier = Modifier,
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    val gradient = if (kind == WhenDoKind.When) EqualTheme.gradients.whenChip else EqualTheme.gradients.doChip
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Box(
            Modifier
                .offset(y = (-3).dp)
                .size(width = 52.dp, height = 24.dp)
                .background(gradient.toBrush(), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            EqualText(
                text = if (kind == WhenDoKind.When) "WHEN" else "DO",
                style = type.caption.copy(fontWeight = FontWeight.SemiBold),
                color = colors.textSecondary,
                maxLines = 1,
            )
        }
        Spacer(Modifier.width(8.dp))
        BasicText(
            text = text,
            style = type.body1.copy(color = colors.textPrimary, lineHeight = 18.sp),
            modifier = Modifier.weight(1f),
        )
    }
}

/** Plain-text [WhenDoRow]. */
@Composable
fun WhenDoRow(
    kind: WhenDoKind,
    text: String,
    modifier: Modifier = Modifier,
) {
    WhenDoRow(kind = kind, text = AnnotatedString(text), modifier = modifier)
}

/** Outlined instruction card shell: `bg/surface`, 1dp `stroke/subtle`, radius 16, gap 16. */
private fun Modifier.instructionCard(colors: EqualColors, padding: PaddingValues): Modifier {
    val shape = RoundedCornerShape(EqualRadius.rLg)
    return fillMaxWidth().background(colors.bgSurface, shape).border(1.dp, colors.strokeSubtle, shape).padding(padding)
}

/**
 * Preset instruction (preset detail screen): title 16/20 SemiBold with the [ApplyButton], or an
 * "Applied" pill (24 high, `action-card/highlight`, check 16 + `buttonSmall` `text/action`) once
 * [applied] → WHEN / DO rows (gap 16). Padding 12 / 16.
 */
@Composable
fun PresetInstructionCard(
    instruction: Instruction,
    applied: Boolean,
    onApply: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = EqualTheme.colors
    Column(
        modifier.instructionCard(colors, PaddingValues(horizontal = 16.dp, vertical = 12.dp)),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(verticalAlignment = Alignment.Top) {
            EqualText(
                text = instruction.title,
                style = EqualTheme.type.subheading.copy(fontWeight = FontWeight.SemiBold, lineHeight = 20.sp),
                color = colors.textPrimary,
                modifier = Modifier.weight(1f),
            )
            Spacer(Modifier.width(12.dp))
            // One persistent polite live region: the Apply → Applied swap is announced once.
            Box(Modifier.semantics { liveRegion = LiveRegionMode.Polite }) {
                if (applied) {
                    Row(
                        Modifier
                            .height(24.dp)
                            .background(colors.actionCardHighlight, CircleShape)
                            .padding(horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        EqualIcon(EqualIcons.Check, contentDescription = null, tint = colors.textAction, size = 16.dp)
                        Spacer(Modifier.width(4.dp))
                        EqualText(text = "Added", style = EqualTheme.type.buttonSmall, color = colors.textAction, maxLines = 1)
                    }
                } else {
                    ApplyButton(onClick = onApply, contentDescription = "Apply ${instruction.title}")
                }
            }
        }
        WhenDoRow(kind = WhenDoKind.When, text = instruction.whenText)
        WhenDoRow(kind = WhenDoKind.Do, text = instruction.doText)
    }
}

/** Source badge on an active instruction: outlined pill (1dp `stroke/medium`, padding 4 / 8), preset art 16 + 10sp SemiBold `text/secondary`. */
@Composable
fun SourceBadge(source: InstructionSource, modifier: Modifier = Modifier) {
    val colors = EqualTheme.colors
    Row(
        modifier
            .defaultMinSize(minHeight = 24.dp)
            .background(colors.bgSurface, CircleShape)
            .border(1.dp, colors.strokeMedium, CircleShape)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        EqualAssetImage(asset = source.art.asset, size = 16.dp)
        Spacer(Modifier.width(4.dp))
        EqualText(
            text = source.label,
            style = EqualTheme.type.caption.copy(fontSize = 10.sp, lineHeight = 12.sp, fontWeight = FontWeight.SemiBold),
            color = colors.textSecondary,
            maxLines = 1,
        )
    }
}

/**
 * Active instruction (328dp card, padding 16, gap 16): title 16/20 Bold + [OverflowMenu] (Edit /
 * Delete instruction), the [SourceBadge] 6 below it, then the WHEN and DO rows (gap 12). No divider.
 */
@Composable
fun InstructionCard(
    instruction: Instruction,
    modifier: Modifier = Modifier,
    onEdit: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
) {
    val colors = EqualTheme.colors
    Column(
        modifier.instructionCard(colors, PaddingValues(16.dp)),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                EqualText(
                    text = instruction.title,
                    style = EqualTheme.type.subheading.copy(lineHeight = 20.sp, fontWeight = FontWeight.Bold),
                    color = colors.textPrimary,
                    modifier = Modifier.weight(1f),
                )
                val items = editDeleteItems(onEdit, onDelete, deleteLabel = "Delete instruction")
                if (items.isNotEmpty()) {
                    Spacer(Modifier.width(8.dp))
                    OverflowMenu(
                        items = items,
                        contentDescription = "${instruction.title} options",
                        modifier = Modifier.offset(x = 14.dp, y = (-10).dp),
                    )
                }
            }
            instruction.source?.let { SourceBadge(it) }
        }
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            WhenDoRow(kind = WhenDoKind.When, text = instruction.whenText)
            WhenDoRow(kind = WhenDoKind.Do, text = instruction.doText)
        }
    }
}

/**
 * Instruction preview inside the setup chat: the shared [ActionCardBody] (note_stack icon on a
 * white circle, title, "When: …" `text/primary`, "Do: …" `text/secondary`, time) in the action
 * card's white frame (radius 16, padding 8, max width 280). [confirmed] adds a ✅ reaction badge
 * (32 circle, shadow `raised`) overlapping the bottom-left; leave 16dp below the preview for it.
 */
@Composable
fun InstructionPreview(
    title: String,
    whenText: AnnotatedString,
    action: AnnotatedString,
    modifier: Modifier = Modifier,
    time: String? = null,
    confirmed: Boolean = false,
) {
    val colors = EqualTheme.colors
    Box(modifier) {
        Box(
            Modifier
                .widthIn(max = 280.dp)
                .background(colors.bgSurface, RoundedCornerShape(EqualRadius.rLg))
                .padding(8.dp),
        ) {
            ActionCardBody(
                title = title,
                icon = EqualIcons.Instructions,
                lines = instructionLines(whenText, action),
                timestamp = time,
            )
        }
        if (confirmed) {
            Box(
                Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = 12.dp, y = 16.dp)
                    .size(32.dp)
                    .eqShadow(EqualShadows.raised, CircleShape)
                    .background(colors.bgSurface, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                EmojiGlyph(emoji = "✅", size = 18.dp, contentDescription = "Confirmed")
            }
        }
    }
}

/**
 * Full-height sheet (the "Setting up instruction" chat). Scrim `scrim`; the panel starts 72 from
 * the top with radius 24 and `bg/surface`. Header (padding 14 / 14 / 14 / 16, gap 10, 1dp
 * `stroke/subtle` below): orb 32 + [title] `subheading` + neutral close. The body is a
 * [ChatWallpaper]: scrolling [content] ([scrollState], hoist it to auto-scroll) and an optional
 * [footer] (padding 8 / 16 / 16, plus the navigation-bar inset). Place it last in a full-screen Box.
 * Back calls [onDismissRequest] while [visible]; the panel is announced as [title]; reduced motion
 * skips the fade / slide.
 */
@Composable
fun FullSheet(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    scrollState: ScrollState = rememberScrollState(),
    footer: (@Composable ColumnScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = EqualTheme.colors
    val reduceMotion = rememberReduceMotion()
    EqualBackHandler(enabled = visible, onBack = onDismissRequest)
    Box(Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(EqualMotion.slow)).unlessReduced(reduceMotion),
            exit = fadeOut(tween(EqualMotion.slow)).unlessReduced(reduceMotion),
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(colors.bgOverlay)
                    .clickable(
                        interactionSource = rememberInteraction(),
                        indication = null,
                        onClickLabel = "Dismiss",
                        role = Role.Button,
                        onClick = onDismissRequest,
                    ),
            )
        }
        AnimatedVisibility(
            visible = visible,
            modifier = Modifier.fillMaxSize(),
            enter = slideInVertically(tween(EqualMotion.slow, easing = EqualEasing.standard)) { it }.unlessReduced(reduceMotion),
            exit = slideOutVertically(tween(EqualMotion.slow, easing = EqualEasing.standard)) { it }.unlessReduced(reduceMotion),
        ) {
            val shape = RoundedCornerShape(topStart = EqualRadius.r2xl, topEnd = EqualRadius.r2xl)
            Column(
                modifier
                    .fillMaxSize()
                    .padding(top = 72.dp)
                    .overlayPane(title)
                    .clip(shape)
                    .background(colors.bgSurface)
                    .pointerInput(Unit) { detectTapGestures { } },
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 14.dp, end = 14.dp, bottom = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    EqualLogoOrb(size = 32.dp)
                    Spacer(Modifier.width(10.dp))
                    EqualText(
                        text = title,
                        style = EqualTheme.type.subheading,
                        color = colors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f).semantics { heading() },
                    )
                    Spacer(Modifier.width(10.dp))
                    EqualIconButton(
                        icon = EqualIcons.Close,
                        contentDescription = "Close",
                        onClick = onDismissRequest,
                        variant = EqualIconButtonVariant.Neutral,
                        iconSize = 22.dp,
                    )
                }
                EqualDivider()
                ChatWallpaper(
                    Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                ) {
                    Column(Modifier.fillMaxSize()) {
                        Column(
                            Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .verticalScroll(scrollState),
                            content = content,
                        )
                        if (footer != null) {
                            Column(
                                Modifier
                                    .fillMaxWidth()
                                    .navigationBarsPadding()
                                    .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 16.dp),
                                content = footer,
                            )
                        }
                    }
                }
            }
        }
    }
}

// =============================================================================================
// Previews
// =============================================================================================

private val PreviewInstruction = Instruction(
    id = "decline-sales",
    title = "Decline sales, promo spam calls",
    whenText = "The caller is selling loan, insurance, credit card, product, or another unsolicited offer",
    doText = "Politely say I am not interested and end the conversation without discussing the offer",
    source = InstructionSource(PresetArt.ScreenProtect, "Screen & Protect"),
)

@EqualPreviews
@Composable
private fun FlowCardsPreview() {
    PreviewFrame {
        var gender by remember { mutableStateOf(Gender.Male) }
        GenderSelector(value = gender, onValueChange = { gender = it })
        VSpace(12.dp)
        AddressCard(label = "Home", address = "House no. 4, Tony Street, Financial District, Hyderabad- 500032", onEdit = {}, onDelete = {})
        VSpace(12.dp)
        InstructionCard(instruction = PreviewInstruction, onEdit = {}, onDelete = {})
        VSpace(12.dp)
        PresetInstructionCard(instruction = PreviewInstruction, applied = false, onApply = {})
        VSpace(12.dp)
        PresetCard(art = PresetArt.ScreenProtect, tint = PresetTint.Green, title = "Screen & Protect", description = "Decline spam, hold the line on loan and payments call, close wrong call", onClick = {})
        VSpace(12.dp)
        RecommendedCard(emoji = "🚫", title = "Decline sales & spam", description = "Politely declines sales and insurance calls.", onAdd = {})
        VSpace(12.dp)
        SuggestionsComingCard()
        VSpace(12.dp)
        InstructionPreview(
            title = PreviewInstruction.title,
            whenText = AnnotatedString(PreviewInstruction.whenText),
            action = AnnotatedString(PreviewInstruction.doText),
            time = "03:21",
            confirmed = true,
        )
        VSpace(24.dp)
    }
}

@EqualPreviews
@Composable
private fun AddressFormPreview() {
    PreviewFrame {
        AddressForm(onSave = {})
    }
}
