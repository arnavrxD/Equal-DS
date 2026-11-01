package ai.equal.ds.components

import ai.equal.ds.R
import ai.equal.ds.assets.EqualAssetImage
import ai.equal.ds.assets.EqualAssets
import ai.equal.ds.assets.EqualCategory
import ai.equal.ds.icons.EqualIcon
import ai.equal.ds.icons.EqualIcons
import ai.equal.ds.theme.EqualEasing
import ai.equal.ds.theme.EqualShadow
import ai.equal.ds.theme.EqualShadowLayer
import ai.equal.ds.theme.EqualTheme
import ai.equal.ds.theme.eqPressFeedback
import ai.equal.ds.theme.eqShadow
import ai.equal.ds.theme.tabular
import ai.equal.ds.theme.toBrush
import ai.equal.ds.tokens.EqualMotion
import ai.equal.ds.tokens.EqualPrimitives
import androidx.annotation.RawRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Call Handling (Known Calls (new users) design, Known-call notifications design):
// which calls the assistant answers, who always rings you, the permissions it needs, and the
// Android notification while it answers a known caller. Mirrors web/src/components/CallHandling.tsx.

/** 0 4 14 black/6%: the unselected [ChoiceCard]. */
private val ChoiceShadow = EqualShadow(listOf(EqualShadowLayer(0.dp, 4.dp, 14.dp, EqualPrimitives.black.copy(alpha = 0.06f))))

/** 0 2 8 black/7%: the white initials disc. */
private val DiscShadow = EqualShadow(listOf(EqualShadowLayer(0.dp, 2.dp, 8.dp, EqualPrimitives.black.copy(alpha = 0.07f))))

/** 0 −8 30 black/12%: the permission sheet. */
private val PermissionSheetShadow = EqualShadow(listOf(EqualShadowLayer(0.dp, (-8).dp, 30.dp, EqualPrimitives.black.copy(alpha = 0.12f))))

/** 0 4 16 black/18%: the notification card over the wallpaper. */
private val NotificationShadow = EqualShadow(listOf(EqualShadowLayer(0.dp, 4.dp, 16.dp, EqualPrimitives.black.copy(alpha = 0.18f))))

/** Supplied art at a non-square size, fitted inside (never cropped). */
@Composable
private fun FitArt(asset: EqualAssets, size: DpSize, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = asset.resId),
        contentDescription = null,
        modifier = modifier.size(size),
        contentScale = ContentScale.Fit,
    )
}

/** Underlined text action (`button` `text/action`), with an optional trailing chevron. */
@Composable
private fun UnderlinedLink(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, chevron: Boolean = false) {
    val colors = EqualTheme.colors
    val interaction = rememberInteraction()
    // 48dp touch area around the 36dp link (layout unchanged).
    Box(
        modifier
            .touchTarget()
            .clickable(interactionSource = interaction, indication = null, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            Modifier
                .height(36.dp)
                .eqPressFeedback(interaction, CircleShape)
                .clip(CircleShape)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            EqualText(
                text = text,
                style = EqualTheme.type.button.copy(textDecoration = TextDecoration.Underline),
                color = colors.textAction,
                maxLines = 1,
            )
            if (chevron) {
                Spacer(Modifier.width(4.dp))
                EqualIcon(EqualIcons.ChevronRight, contentDescription = null, tint = colors.textAction, size = 16.dp)
            }
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Question
// ---------------------------------------------------------------------------------------------

/**
 * Hero art (or its Lottie) + the question + the one-line promise under it, centred. Padding
 * 16 / 24 / 0. [art] draws at 280 × 208; [lottie] (e.g. `R.raw.lottie_call_handling_contacts`)
 * replaces it at 300 × 300, trimmed 44 top / 48 bottom so the spacing matches the still. [title]
 * `headline` Bold, [subtitle] `title` Regular `text/secondary`.
 */
@Composable
fun CallQuestion(
    art: EqualAssets,
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    @RawRes lottie: Int? = null,
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    Column(
        modifier
            .fillMaxWidth()
            .padding(start = 24.dp, top = 16.dp, end = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        if (lottie != null) {
            EqualLottie(
                rawRes = lottie,
                width = 300.dp,
                height = 300.dp,
                modifier = Modifier.layout { measurable, constraints ->
                    val p = measurable.measure(constraints)
                    val top = 44.dp.roundToPx()
                    val bottom = 48.dp.roundToPx()
                    layout(p.width, (p.height - top - bottom).coerceAtLeast(0)) { p.place(0, -top) }
                },
            )
        } else {
            FitArt(art, DpSize(280.dp, 208.dp))
        }
        Spacer(Modifier.height(6.dp))
        EqualText(
            text = title,
            style = type.headline.copy(fontWeight = FontWeight.Bold),
            color = colors.textPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.semantics { this.contentDescription = title },
        )
        if (subtitle != null) {
            EqualText(
                text = subtitle,
                style = type.title.copy(fontWeight = FontWeight.W400),
                color = colors.textSecondary,
                textAlign = TextAlign.Center,
            )
        }
    }
}

/**
 * Radio card: [icon] 28 → 16 → [title] `subheading` (+ [SuggestedTag] when [suggested]) over
 * [description] `body1` `text/secondary` → the 24dp radio. Radius 20, padding 18 / 18 / 18 / 20,
 * `choice/default-bg` with a soft shadow. Selected: `action/fill/unread` and a 2dp `gradient/choice-stroke`
 * outline (green fading to lime), radio filled `button/primary`. [enabled] false dims it to 45%
 * (e.g. no contacts permission). Put the cards in a column with `Modifier.selectableGroup()`.
 */
@Composable
fun ChoiceCard(
    icon: ImageVector,
    title: String,
    description: String,
    selected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
    suggested: Boolean = false,
    enabled: Boolean = true,
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    val shape = RoundedCornerShape(EqualRadiusChoice)
    val interaction = rememberInteraction()
    val surface = if (selected) {
        Modifier
            .clip(shape)
            .background(colors.actionFillUnread)
            .border(2.dp, EqualTheme.gradients.choiceStroke.toBrush(), shape)
    } else {
        Modifier
            .eqShadow(ChoiceShadow, shape, outsideOnly = true)
            .clip(shape)
            .background(colors.choiceDefaultBg)
    }
    Row(
        modifier
            .fillMaxWidth()
            .alpha(if (enabled) 1f else 0.45f)
            .eqPressFeedback(interaction, shape)
            .then(surface)
            .selectable(
                selected = selected,
                enabled = enabled,
                role = Role.RadioButton,
                interactionSource = interaction,
                indication = null,
                onClick = onSelect,
            )
            .padding(start = 20.dp, top = 18.dp, end = 18.dp, bottom = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        EqualIcon(icon, contentDescription = null, tint = colors.textPrimary, size = 28.dp)
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                EqualText(text = title, style = type.subheading, color = colors.textPrimary, modifier = Modifier.weight(1f, fill = false))
                if (suggested) SuggestedTag()
            }
            EqualText(text = description, style = type.body1, color = colors.textSecondary)
        }
        ChoiceRadio(selected)
    }
}

private val EqualRadiusChoice = 20.dp

@Composable
private fun ChoiceRadio(on: Boolean) {
    val colors = EqualTheme.colors
    Box(
        Modifier
            .size(24.dp)
            .border(2.dp, if (on) colors.buttonPrimary else colors.strokeDark, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        if (on) Box(Modifier.size(11.dp).background(colors.buttonPrimary, CircleShape))
    }
}

/** "⭐ Suggested" tag: pill on `bg/surface`, 1dp `stroke/subtle`, padding 2 / 8, emoji + `caption` Regular `text/secondary`. */
@Composable
fun SuggestedTag(modifier: Modifier = Modifier, label: String = "Suggested") {
    val colors = EqualTheme.colors
    Row(
        modifier
            .clip(CircleShape)
            .background(colors.bgSurface)
            .border(1.dp, colors.strokeSubtle, CircleShape)
            .padding(horizontal = 8.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        EmojiGlyph("⭐", size = 12.dp, modifier = Modifier.clearAndSetSemantics {})
        EqualText(text = label, style = EqualTheme.type.captionRegular, color = colors.textSecondary, maxLines = 1)
    }
}

/**
 * Blue info note, e.g. "Contact access needed" + why + "Give contacts permission ›". Radius 16,
 * padding 16, `info-note/bg` with `info-note/text`; [title] `subheading` after the info icon,
 * [body] `body1`, [link] an underlined text action with a chevron ([onLink]). All centred.
 * With [onDismiss] it is the left-aligned dismissible notice: 16dp filled info glyph
 * (`info-note/title`), [title] `subheading` SemiBold over [body] `body1` `text/secondary`, and a
 * 24dp close ("Dismiss", 48dp touch area) at the right.
 */
@Composable
fun InfoNote(
    title: String,
    modifier: Modifier = Modifier,
    body: String? = null,
    link: String? = null,
    onLink: () -> Unit = {},
    /** Dismissible notice (You: "Settings live here now"): left-aligned, with a close. */
    onDismiss: (() -> Unit)? = null,
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    if (onDismiss != null) {
        Row(
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(colors.infoNoteBg)
                .padding(16.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            EqualIcon(EqualIcons.InfoFill, contentDescription = null, tint = colors.infoNoteTitle, size = 16.dp, modifier = Modifier.padding(top = 4.dp))
            Column(Modifier.weight(1f)) {
                EqualText(text = title, style = type.subheading.copy(fontWeight = FontWeight.SemiBold), color = colors.textPrimary)
                if (body != null) EqualText(text = body, style = type.body1, color = colors.textSecondary)
            }
            CompactIconButton(EqualIcons.Close, "Dismiss", onClick = onDismiss, tint = colors.textSecondary, size = 24.dp, iconSize = 16.dp)
        }
        return
    }
    Column(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colors.infoNoteBg)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            EqualIcon(EqualIcons.Info, contentDescription = null, tint = colors.textPrimary, size = 18.dp)
            EqualText(text = title, style = type.subheading, color = colors.textPrimary)
        }
        if (body != null) EqualText(text = body, style = type.body1, color = colors.textPrimary, textAlign = TextAlign.Center)
        if (link != null) UnderlinedLink(text = link, onClick = onLink, chevron = true)
    }
}

// ---------------------------------------------------------------------------------------------
// Contact picking
// ---------------------------------------------------------------------------------------------

/** The badge on a [ContactPick]. */
enum class ContactPickMark { Add, Remove }

/** White `initials-disc/bg` circle with a soft shadow and the initials in `title` Medium. */
@Composable
private fun InitialsDisc(initials: String, size: Dp, modifier: Modifier = Modifier) {
    Box(
        modifier
            .size(size)
            .eqShadow(DiscShadow, CircleShape)
            .background(EqualTheme.colors.initialsDiscBg, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        EqualText(text = initials, style = EqualTheme.type.title, color = EqualTheme.colors.textPrimary, maxLines = 1)
    }
}

/**
 * A suggested contact, 76 wide: the 56dp initials disc with a 22dp + ([ContactPickMark.Add]) or ×
 * ([ContactPickMark.Remove]) badge at its top-right (`text/secondary` disc, `bg/surface` glyph, 2dp
 * `bg/primary` ring), the [name] under it (`body1`, centred). Lay several in a horizontally
 * scrolling row with 8dp gaps.
 */
@Composable
fun ContactPick(
    initials: String,
    name: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    mark: ContactPickMark = ContactPickMark.Add,
) {
    val colors = EqualTheme.colors
    val interaction = rememberInteraction()
    val label = "${if (mark == ContactPickMark.Add) "Exclude" else "Remove"} $name"
    Column(
        modifier
            .width(76.dp)
            .eqPressFeedback(interaction, RoundedCornerShape(12.dp))
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .clearAndSetSemantics {
                contentDescription = label
                role = Role.Button
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box {
            InitialsDisc(initials, 56.dp)
            Box(
                Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 6.dp, y = (-4).dp)
                    .size(22.dp)
                    .border(2.dp, colors.bgPrimary, CircleShape)
                    .padding(2.dp)
                    .background(colors.textSecondary, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                EqualIcon(
                    if (mark == ContactPickMark.Add) EqualIcons.Add else EqualIcons.Close,
                    contentDescription = null,
                    tint = colors.bgSurface,
                    size = 14.dp,
                )
            }
        }
        EqualText(text = name, style = EqualTheme.type.body1, color = colors.textPrimary, textAlign = TextAlign.Center, maxLines = 2)
    }
}

/**
 * A contact row with a checkbox: 52dp initials disc → 16 → [name] `title` Medium (one line) over
 * [phone] `body1` `text/secondary` (tabular) → the 26dp checkbox ("Select [name]"). Padding
 * 10 / 28 / 10 / 16 (the right leaves room for an [AlphabetIndex]).
 */
@Composable
fun ContactCheckRow(
    initials: String,
    name: String,
    phone: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = EqualTheme.colors
    // The whole row toggles (one TalkBack stop: name, phone, checked state).
    Row(
        modifier
            .fillMaxWidth()
            .toggleable(value = checked, role = Role.Checkbox, onValueChange = onCheckedChange)
            .padding(start = 16.dp, top = 10.dp, end = 28.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        InitialsDisc(initials, 52.dp)
        Column(Modifier.weight(1f)) {
            EqualText(text = name, style = EqualTheme.type.title, color = colors.textPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
            EqualText(text = phone, style = EqualTheme.type.body1.tabular(), color = colors.textSecondary, maxLines = 1)
        }
        EqualCheckbox(checked = checked, onCheckedChange = null, size = 26.dp)
    }
}

/**
 * A–Z fast-scroll index down the right edge of a contact list (11sp Medium `text/primary`, gap 1).
 * Place it in a Box over the rows with `Modifier.align(Alignment.CenterEnd).padding(end = 2.dp)`.
 */
@Composable
fun AlphabetIndex(modifier: Modifier = Modifier, onPick: (Char) -> Unit = {}) {
    val colors = EqualTheme.colors
    val style = EqualTheme.type.caption.copy(fontSize = 11.sp, lineHeight = 14.sp)
    Column(
        modifier.semantics { paneTitle = "Jump to letter" },
        verticalArrangement = Arrangement.spacedBy(1.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ('A'..'Z').forEach { l ->
            // 26 letters must fit the list height, so each target is 48dp wide but only as tall
            // as its row (taller targets would overlap and pick the wrong letter).
            Box(
                Modifier
                    .touchTarget(minHeight = 0.dp)
                    .clickable(onClickLabel = "Jump to $l", role = Role.Button) { onPick(l) },
                contentAlignment = Alignment.Center,
            ) {
                EqualText(
                    text = l.toString(),
                    style = style,
                    color = colors.textPrimary,
                    modifier = Modifier.padding(horizontal = 2.dp),
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Setup moments
// ---------------------------------------------------------------------------------------------

/**
 * Dark takeover that invites a new user into setup, drawn over the whole screen (an overlay):
 * `gradient/setup-prompt`, the 240 × 169 [art], [title] `headline` and [subtitle] `title` Regular
 * in `text/on-dark`, then a full-width [cta] with a 1.5dp `button/secondary` ring. Fades in over
 * `motion.slow` (held still when animations are off). With [onDismiss], system Back calls it.
 */
@Composable
fun SetupPrompt(
    onStart: () -> Unit,
    modifier: Modifier = Modifier,
    art: EqualAssets = EqualAssets.CallSetup,
    title: String = "Equal now answers contacts",
    subtitle: String = "Pick who still rings your phone.",
    cta: String = "Start",
    onDismiss: (() -> Unit)? = null,
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    if (onDismiss != null) EqualBackHandler(onBack = onDismiss)
    val reduceMotion = rememberReduceMotion()
    val visible = remember { MutableTransitionState(reduceMotion).apply { targetState = true } }
    AnimatedVisibility(
        visibleState = visible,
        modifier = modifier.fillMaxSize(),
        enter = fadeIn(tween(EqualMotion.slow, easing = EqualEasing.standard)),
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .background(EqualTheme.gradients.setupPrompt.toBrush())
                // Swallow taps so nothing behind the takeover reacts.
                .clickable(interactionSource = rememberInteraction(), indication = null, onClick = {})
                .semantics { paneTitle = title }
                .navigationBarsPadding()
                .padding(start = 16.dp, end = 16.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.Bottom,
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                FitArt(art, DpSize(240.dp, 169.dp))
                Spacer(Modifier.height(20.dp))
                EqualText(text = title, style = type.headline, color = colors.textOnDark, textAlign = TextAlign.Center, modifier = Modifier.semantics { heading() })
                EqualText(
                    text = subtitle,
                    style = type.title.copy(fontWeight = FontWeight.W400),
                    color = colors.textOnDark.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center,
                )
            }
            EqualButton(
                text = cta,
                onClick = onStart,
                size = EqualButtonSize.Block,
                modifier = Modifier.border(1.5.dp, colors.buttonSecondary, CircleShape),
            )
        }
    }
}

/**
 * Full-screen result after setup ("Perfect!", "All Sorted!") on the `gradient/setup-done` wash
 * (mint to lime). [header] (status bar / back, on a transparent background) sits on the wash;
 * then the 300 × 262 [art], [title] `headline` Bold 28/34, [body] `title` Regular, and the
 * full-width [cta] at the bottom (padding 0 16 24).
 */
@Composable
fun SetupDone(
    title: String,
    body: String,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
    art: EqualAssets = EqualAssets.CallHandlingDone,
    cta: String = "Done",
    header: (@Composable () -> Unit)? = null,
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    Column(
        modifier
            .fillMaxSize()
            .background(EqualTheme.gradients.setupDone.toBrush()),
    ) {
        header?.invoke()
        Column(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(start = 28.dp, top = 48.dp, end = 28.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            FitArt(art, DpSize(300.dp, 262.dp))
            EqualText(
                text = title,
                style = type.headline.copy(fontWeight = FontWeight.Bold, fontSize = 28.sp, lineHeight = 34.sp),
                color = colors.textPrimary,
                textAlign = TextAlign.Center,
            )
            EqualText(text = body, style = type.title.copy(fontWeight = FontWeight.W400), color = colors.textPrimary, textAlign = TextAlign.Center)
        }
        EqualButton(
            text = cta,
            onClick = onDone,
            size = EqualButtonSize.Block,
            modifier = Modifier
                .navigationBarsPadding()
                .padding(start = 16.dp, end = 16.dp, bottom = 24.dp),
        )
    }
}

/**
 * Ask-once permission sheet: the 110dp 3D [art], [title] `headline`, why ([body] `title` Regular
 * `text/secondary`), one full-width [cta] ([onAllow]) and an optional underlined [secondary]
 * action (e.g. "Skip, answer everyone"). `sheet/bg`, top radius 32, padding 32 / 20 / 24; the
 * close ([onClose]) floats at the top-right.
 *
 * As an overlay (default) it fills its parent: `scrim` (tap → [onScrim]) and the sheet at the
 * bottom, which slides up 40% + fades in over `motion.slow` (none when animations are off).
 * [inline] draws only the sheet, for docs. System Back calls [onClose] when it is set.
 */
@Composable
fun PermissionSheet(
    art: EqualAssets,
    title: String,
    body: String,
    cta: String,
    onAllow: () -> Unit,
    modifier: Modifier = Modifier,
    onClose: (() -> Unit)? = null,
    secondary: String? = null,
    onSecondary: () -> Unit = {},
    onScrim: () -> Unit = onClose ?: {},
    inline: Boolean = false,
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    val shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    val sheet: @Composable (Modifier) -> Unit = { m ->
        Box(
            m
                .fillMaxWidth()
                .eqShadow(PermissionSheetShadow, shape)
                .clip(shape)
                .background(colors.bgPrimary)
                // Swallow taps so they don't reach the scrim.
                .clickable(interactionSource = rememberInteraction(), indication = null, onClick = {})
                .semantics { paneTitle = title },
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(start = 20.dp, top = 32.dp, end = 20.dp, bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                EqualAssetImage(art, size = 110.dp)
                Spacer(Modifier.height(12.dp))
                EqualText(text = title, style = type.headline, color = colors.textPrimary, textAlign = TextAlign.Center, modifier = Modifier.semantics { heading() })
                Spacer(Modifier.height(4.dp))
                EqualText(text = body, style = type.title.copy(fontWeight = FontWeight.W400), color = colors.textSecondary, textAlign = TextAlign.Center)
                Spacer(Modifier.height(24.dp))
                EqualButton(text = cta, onClick = onAllow, size = EqualButtonSize.Block)
                if (secondary != null) {
                    Spacer(Modifier.height(10.dp))
                    UnderlinedLink(text = secondary, onClick = onSecondary)
                }
            }
            if (onClose != null) {
                EqualIconButton(
                    icon = EqualIcons.Close,
                    contentDescription = "Close",
                    onClick = onClose,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp),
                )
            }
        }
    }
    if (inline) {
        sheet(modifier)
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
                .clickable(interactionSource = rememberInteraction(), indication = null, onClickLabel = "Close", onClick = onScrim),
        )
        AnimatedVisibility(
            visibleState = visible,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = slideInVertically(tween(EqualMotion.slow, easing = EqualEasing.standard)) { (it * 0.4f).toInt() } +
                fadeIn(tween(EqualMotion.slow, easing = EqualEasing.standard)),
        ) {
            sheet(Modifier)
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Notifications
// ---------------------------------------------------------------------------------------------

/** Stage of the call on a [CallNotification]. */
enum class CallNotificationState {
    /** "Your Equal AI is answering your call" with the Equal app badge. */
    Answering,

    /** One line: what the caller wants. */
    Summary,

    /** Two lines, the call button and the quick replies (Takeover first). */
    Expanded,

    /** The replies give way to "This needs your attention, connecting this call to you". */
    Connecting,
}

/** Default [CallNotification] summary (the design's Mom call). */
const val SampleCallSummary = "She is asking when will you be coming to the wedding, she also asked you to bring her sweets."

/** Default [CallNotification] quick replies. Takeover always comes first. */
val SampleCallReplies: List<String> = listOf("Takeover", "I’ll bring them all", "I’ll call back to get more")

/**
 * Android ongoing-call notification while the assistant answers a known caller: the app line
 * ("Equal AI • Ongoing" + a live [elapsed] timer), the caller ([category] avatar 40, [name]
 * `subheading`) and what they want, then quick replies ([replies], Takeover first) or the
 * hand-off line. Radius 24, `bg/surface` (a system surface, so it follows the phone's theme).
 *
 * On device this content goes into a real Android notification: build it with
 * `NotificationCompat` (an ongoing, `CATEGORY_CALL` notification with
 * `setCustomContentView` / `setCustomBigContentView` RemoteViews, or `CallStyle` actions for the
 * replies); Compose can't render inside the system shade. This composable is the design
 * reference and the in-app preview (docs, the coded [DeviceScreen]). Keep it to who, what they
 * want and one tap to act; don't style the shell beyond what Android draws.
 */
@Composable
fun CallNotification(
    name: String,
    state: CallNotificationState,
    modifier: Modifier = Modifier,
    category: EqualCategory = EqualCategory.HomeServices,
    elapsed: String = "00:06",
    summary: String = SampleCallSummary,
    replies: List<String> = SampleCallReplies,
    onReply: (String) -> Unit = {},
    onAnswer: () -> Unit = {},
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    val shape = RoundedCornerShape(24.dp)
    val big = state == CallNotificationState.Expanded || state == CallNotificationState.Connecting
    Column(
        modifier
            .fillMaxWidth()
            .eqShadow(NotificationShadow, shape)
            .clip(shape)
            .background(colors.bgSurface),
    ) {
        // App line (the ticking timer is not a live region; only the message below is).
        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 12.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            EqualText(text = "Equal AI", style = type.captionRegular, color = colors.textSecondary)
            EqualText(text = "•", style = type.captionRegular.copy(fontSize = 10.sp), color = colors.textSecondary, modifier = Modifier.clearAndSetSemantics {})
            EqualText(text = "Ongoing", style = type.captionRegular, color = colors.textAction)
            Row(
                Modifier
                    .clip(CircleShape)
                    .border(1.dp, colors.strokeSubtle, CircleShape)
                    .padding(horizontal = 8.dp, vertical = 1.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                EqualIcon(EqualIcons.GraphicEq, contentDescription = null, tint = colors.textAction, size = 12.dp)
                EqualText(text = elapsed, style = type.captionRegular.tabular(), color = colors.textSecondary)
            }
            Spacer(Modifier.weight(1f))
            EqualIcon(EqualIcons.ChevronDown, contentDescription = null, tint = colors.textSecondary, size = 20.dp)
        }
        // Caller
        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box {
                CategoryAvatar(category = category, size = 40.dp)
                if (big) {
                    Box(
                        Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = 3.dp, y = 3.dp)
                            .size(18.dp)
                            .border(2.dp, colors.bgSurface, CircleShape)
                            .padding(2.dp)
                            .background(colors.textAction, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        EqualMark(width = 10.dp, color = colors.textOnAction)
                    }
                }
            }
            Column(Modifier.weight(1f)) {
                EqualText(text = name, style = type.subheading, color = colors.textPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                EqualText(
                    text = if (state == CallNotificationState.Answering) "Your Equal AI is answering your call" else summary,
                    style = type.body1,
                    color = colors.textPrimary,
                    maxLines = if (big) 2 else 1,
                    overflow = TextOverflow.Ellipsis,
                    // Announced once when the message changes, never on timer ticks.
                    modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                )
            }
            when (state) {
                CallNotificationState.Expanded -> {
                    val interaction = rememberInteraction()
                    // 48dp touch area around the 44dp button (layout unchanged).
                    Box(
                        Modifier
                            .touchTarget()
                            .clickable(interactionSource = interaction, indication = null, onClickLabel = "Answer the call", role = Role.Button, onClick = onAnswer)
                            .semantics { contentDescription = "Answer the call" },
                        contentAlignment = Alignment.Center,
                    ) {
                        Box(
                            Modifier
                                .size(44.dp)
                                .eqPressFeedback(interaction, CircleShape)
                                .clip(CircleShape)
                                .border(1.5.dp, colors.buttonPrimary, CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            EqualIcon(EqualIcons.CallFilled, contentDescription = null, tint = colors.textAction, size = 20.dp)
                        }
                    }
                }
                CallNotificationState.Connecting -> Unit
                else -> AppBadge()
            }
        }
        if (state == CallNotificationState.Expanded) {
            EqualDivider()
            Row(
                Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(start = 16.dp, top = 10.dp, end = 16.dp, bottom = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                replies.forEach { r -> ReplyChip(r) { onReply(r) } }
            }
        }
        if (state == CallNotificationState.Connecting) {
            EqualDivider()
            EqualText(
                text = "This needs your attention, connecting this call to you",
                style = type.body1Italic,
                color = colors.textPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .semantics { liveRegion = LiveRegionMode.Polite }
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 14.dp),
            )
        }
    }
}

/** The 48dp Equal app tile on the notification: `gradient/fab`, white mark + "equal". */
@Composable
private fun AppBadge() {
    Column(
        Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(EqualTheme.gradients.fab.toBrush())
            .clearAndSetSemantics {},
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(1.dp, Alignment.CenterVertically),
    ) {
        EqualMark(width = 18.dp, color = EqualTheme.colors.textOnAction)
        EqualText(
            text = "equal",
            style = EqualTheme.type.badge.copy(fontSize = 9.sp, lineHeight = 10.sp),
            color = EqualTheme.colors.textOnAction,
        )
    }
}

/** Quick reply: 34 high pill, h16, `gradient/chip-large`, 1dp `action/stroke/unread`, `body1`. */
@Composable
private fun ReplyChip(label: String, onClick: () -> Unit) {
    val colors = EqualTheme.colors
    val interaction = rememberInteraction()
    // 48dp-high touch area around the 34dp chip (layout unchanged).
    Box(
        Modifier
            .touchTarget()
            .clickable(interactionSource = interaction, indication = null, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .height(34.dp)
                .eqPressFeedback(interaction, CircleShape)
                .clip(CircleShape)
                .background(EqualTheme.gradients.chipLarge.toBrush())
                .border(1.dp, colors.actionStrokeUnread, CircleShape)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            EqualText(text = label, style = EqualTheme.type.body1, color = colors.textPrimary, maxLines = 1)
        }
    }
}

/**
 * A coded stand-in for the phone's home / lock screen behind a notification (no wallpaper art):
 * a dark radial wash, the status line ([time] + glyphs) and a large clock, with [content] (the
 * [CallNotification]) padded 4 / 8 under the status line. For docs and previews only; on device
 * the system draws all of this.
 */
@Composable
fun DeviceScreen(
    modifier: Modifier = Modifier,
    time: String = "2:13",
    content: @Composable ColumnScope.() -> Unit = {},
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    val wash = Brush.radialGradient(
        0f to EqualPrimitives.grey03,
        0.45f to EqualPrimitives.grey01,
        1f to EqualPrimitives.black,
    )
    Box(
        modifier
            .fillMaxSize()
            .background(EqualPrimitives.black)
            .background(wash),
    ) {
        // The clock sits behind the notification.
        EqualText(
            text = time,
            style = type.headline.copy(fontSize = 64.sp, lineHeight = 64.sp, fontWeight = FontWeight.Light).tabular(),
            color = colors.textOnDark.copy(alpha = 0.9f),
            modifier = Modifier
                .padding(start = 24.dp, top = 210.dp)
                .clearAndSetSemantics {},
        )
        Column(Modifier.fillMaxWidth()) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, top = 14.dp, end = 20.dp, bottom = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                EqualText(text = time, style = type.body2.tabular(), color = colors.textOnDark)
                EqualText(text = "▲ ● ▮", style = type.body2, color = colors.textOnDark, modifier = Modifier.clearAndSetSemantics {})
            }
            Column(Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp), content = content)
        }
    }
}

/** Strip under a chat header while the assistant is on the call: 44 high, `ongoing-bar/bg`, "Ongoing call · [elapsed]" `subheading` in `ongoing-bar/text`. */
@Composable
fun OngoingCallBar(elapsed: String, modifier: Modifier = Modifier) {
    val colors = EqualTheme.colors
    Row(
        modifier
            .fillMaxWidth()
            .height(44.dp)
            .background(colors.ongoingBarBg),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
    ) {
        EqualIcon(EqualIcons.GraphicEq, contentDescription = null, tint = colors.ongoingBarText, size = 18.dp)
        // Two runs that read as one line: "Ongoing call" is a live region announced once when the
        // bar appears; the ticking timer is readable on focus but never announced on its own.
        val style = EqualTheme.type.subheading.tabular()
        Row {
            EqualText(
                text = "Ongoing call -",
                style = style,
                color = colors.ongoingBarText,
                modifier = Modifier.semantics {
                    contentDescription = "Ongoing call"
                    liveRegion = LiveRegionMode.Polite
                },
            )
            EqualText(text = " $elapsed", style = style, color = colors.ongoingBarText)
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Previews
// ---------------------------------------------------------------------------------------------

@EqualPreviews
@Composable
private fun ChoiceCardsPreview() {
    PreviewFrame {
        var all by remember { mutableStateOf(false) }
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            ChoiceCard(EqualIcons.Globe, "All calls except", "Equal answers all your calls, except the people you choose.", selected = all, onSelect = { all = true })
            ChoiceCard(EqualIcons.Call, "Unknown numbers only", "Equal only answers calls from numbers you don’t have saved.", selected = !all, onSelect = { all = false })
            ChoiceCard(
                EqualIcons.Globe,
                "All calls except",
                "Unknown numbers, and people you know when you don’t pick up",
                selected = false,
                onSelect = {},
                suggested = true,
                enabled = false,
            )
            InfoNote(title = "Contact access needed", body = "Without it, the assistant cannot handle all calls.", link = "Give contacts permission")
        }
    }
}

@EqualPreviews
@Composable
private fun ContactPickersPreview() {
    PreviewFrame(padding = 0.dp) {
        val picked = remember { mutableStateListOf("AR") }
        Row(Modifier.padding(start = 12.dp, top = 8.dp, end = 12.dp, bottom = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ContactPick("SU", "Mom", onClick = {})
            ContactPick("PA", "Pappa", onClick = {})
            ContactPick("VC", "Vijay Cousin", onClick = {}, mark = ContactPickMark.Remove)
        }
        Box {
            Column {
                listOf("AR" to "Arnav Raj", "SS" to "Shubham Singh").forEach { (i, n) ->
                    ContactCheckRow(i, n, "+91 8986461540", checked = i in picked, onCheckedChange = { if (it) picked.add(i) else picked.remove(i) })
                }
            }
        }
    }
}

@EqualPreviews
@Composable
private fun PermissionSheetPreview() {
    PreviewFrame(padding = 0.dp) {
        PermissionSheet(
            art = EqualAssets.PermissionContacts,
            title = "Allow contacts permission",
            body = "I need your contact list to know who is calling.",
            cta = "Allow",
            onAllow = {},
            onClose = {},
            inline = true,
        )
    }
}

@EqualPreviews
@Composable
private fun CallNotificationPreview() {
    EqualTheme {
        Box(Modifier.height(560.dp)) {
            DeviceScreen {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    CallNotification(name = "Mom", state = CallNotificationState.Answering)
                    CallNotification(name = "Mom", state = CallNotificationState.Expanded)
                    CallNotification(name = "Mom", state = CallNotificationState.Connecting)
                }
            }
        }
    }
}

@EqualPreviews
@Composable
private fun SetupMomentsPreview() {
    PreviewFrame(padding = 0.dp) {
        OngoingCallBar(elapsed = "02:34")
        Box(Modifier.height(640.dp)) {
            SetupDone(
                title = "Perfect!",
                body = "Equal will handle the calls. Arnav, Shubham will always ring you.",
                onDone = {},
            )
        }
        Box(Modifier.height(560.dp)) { SetupPrompt(onStart = {}) }
        CallQuestion(art = EqualAssets.CallHandlingExclude, lottie = R.raw.lottie_call_handling_contacts, title = "Who should still ring you?")
    }
}
