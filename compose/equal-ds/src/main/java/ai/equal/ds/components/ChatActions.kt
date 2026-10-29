package ai.equal.ds.components

import ai.equal.ds.assets.EqualAssetImage
import ai.equal.ds.assets.EqualAssets
import ai.equal.ds.icons.EqualIcon
import ai.equal.ds.icons.EqualIcons
import ai.equal.ds.theme.EqualEasing
import ai.equal.ds.theme.EqualShadow
import ai.equal.ds.theme.EqualShadowLayer
import ai.equal.ds.theme.EqualShadows
import ai.equal.ds.theme.EqualTheme
import ai.equal.ds.theme.eqPressFeedback
import ai.equal.ds.theme.eqShadow
import ai.equal.ds.theme.tabular
import ai.equal.ds.theme.toBrush
import ai.equal.ds.tokens.DarkEqualColors
import ai.equal.ds.tokens.EqualMotion
import ai.equal.ds.tokens.EqualPrimitives
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.BasicText

// Chat action flows (Flows/Call Back, Mute, Reply On WhatsApp, Save Contact, Error states): the
// pieces the AI layer uses to carry out a task in a chat and to fail gracefully.
// Mirrors web/src/components/ChatActions.tsx + ChatActions.css.

// ---------------------------------------------------------------------------------------------
// Assistant turn
// ---------------------------------------------------------------------------------------------

/**
 * The assistant's turn in a task: 28dp orb → 8 → [AssistantReplyCard] ([text], [time], emoji
 * action rows) at [width] (shrinks to fit a narrower parent). [body] adds rich content under the
 * text, e.g. an [ActionCardBody] for "Outgoing call"; pass an empty [text] to show only that.
 * A left-side message in a [MessageList].
 */
@Composable
fun AiTurn(
    text: String,
    modifier: Modifier = Modifier,
    actions: List<ReplyAction> = emptyList(),
    time: String = "03:21",
    width: Dp = 280.dp,
    body: (@Composable ColumnScope.() -> Unit)? = null,
) {
    Row(modifier.messageSide(MessageSide.Left), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.Top) {
        EqualLogoOrb(size = 28.dp, contentDescription = null)
        AssistantReplyCard(
            text = text,
            time = time,
            actions = actions,
            body = body,
            modifier = Modifier.weight(1f, fill = false).width(width),
        )
    }
}

// ---------------------------------------------------------------------------------------------
// Hold to speak
// ---------------------------------------------------------------------------------------------

/**
 * Voice-first input in a task sheet (gap 12): the "Hold to speak" pill (fills, 52 high,
 * `bg/surface`, 1dp `action/stroke/unread`; mic-fill 20 → 8 → `button` `text/primary`) calls
 * [onHold]; the 48dp `bg/surface` keyboard toggle (keyboard 22 `text/secondary`, `raised` shadow)
 * calls [onKeyboard].
 */
@Composable
fun HoldToSpeakBar(
    modifier: Modifier = Modifier,
    onHold: () -> Unit = {},
    onKeyboard: () -> Unit = {},
) {
    val colors = EqualTheme.colors
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        val speak = rememberInteraction()
        Row(
            Modifier
                .weight(1f)
                .eqPressFeedback(speak, CircleShape)
                .height(52.dp)
                .clip(CircleShape)
                .background(colors.bgSurface)
                .border(1.dp, colors.actionStrokeUnread, CircleShape)
                .clickable(interactionSource = speak, indication = null, role = Role.Button, onClick = onHold),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        ) {
            EqualIcon(EqualIcons.MicFill, contentDescription = null, tint = colors.textPrimary, size = 20.dp)
            EqualText(text = "Hold to speak", style = EqualTheme.type.button, color = colors.textPrimary, maxLines = 1)
        }
        val kbd = rememberInteraction()
        Box(
            Modifier
                .eqPressFeedback(kbd, CircleShape)
                .size(48.dp)
                .eqShadow(EqualShadows.raised, CircleShape)
                .clip(CircleShape)
                .background(colors.bgSurface)
                .clickable(interactionSource = kbd, indication = null, role = Role.Button, onClick = onKeyboard),
            contentAlignment = Alignment.Center,
        ) {
            EqualIcon(EqualIcons.Keyboard, contentDescription = "Type instead", tint = colors.textSecondary, size = 22.dp)
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Edit bubble
// ---------------------------------------------------------------------------------------------

/** 0 4 16 black/12%: the floating edit bubble. */
private val EditBubbleShadow = EqualShadow(listOf(EqualShadowLayer(0.dp, 4.dp, 16.dp, EqualPrimitives.black.copy(alpha = 0.12f))))

/** The dim over the chat behind an [EditBubble]: black/35%. */
val EditDimColor = EqualPrimitives.black.copy(alpha = 0.35f)

/**
 * Edit a drafted message in place: the bubble floats above the keyboard (padding 8 / 12, gap 8).
 * The field (`bg/surface`, radius 20, padding 12 / 16, `bodyLarge`, caret `text/action`, shadow
 * 0 4 16 black/12%) grows with the text up to 300dp, then scrolls; the 48dp `button/primary` check
 * ([onDone], `text/on-action`) saves it. Dim the chat behind it with [EditDimColor].
 * [autoFocus] focuses the field (and so opens the keyboard) on first composition.
 */
@Composable
fun EditBubble(
    value: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit = {},
    onDone: () -> Unit = {},
    autoFocus: Boolean = false,
) {
    val colors = EqualTheme.colors
    val shape = RoundedCornerShape(20.dp)
    val focus = remember { FocusRequester() }
    LaunchedEffect(autoFocus) { if (autoFocus) focus.requestFocus() }
    Row(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .weight(1f)
                .heightIn(max = 300.dp)
                .eqShadow(EditBubbleShadow, shape)
                .clip(shape)
                .background(colors.bgSurface)
                .focusRequester(focus)
                .semantics { contentDescription = "Edit message" },
            textStyle = EqualTheme.type.bodyLarge.copy(color = colors.textPrimary),
            cursorBrush = SolidColor(colors.textAction),
            decorationBox = { inner -> Box(Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) { inner() } },
        )
        val interaction = rememberInteraction()
        Box(
            Modifier
                .eqPressFeedback(interaction, CircleShape)
                .size(48.dp)
                .clip(CircleShape)
                .background(colors.buttonPrimary)
                .clickable(interactionSource = interaction, indication = null, role = Role.Button, onClick = onDone),
            contentAlignment = Alignment.Center,
        ) {
            EqualIcon(EqualIcons.Check, contentDescription = "Done", tint = colors.textOnAction, size = 24.dp)
        }
    }
}

// ---------------------------------------------------------------------------------------------
// New contact form
// ---------------------------------------------------------------------------------------------

/** What a [NewContactForm] is pre-filled with (from the call). */
@Immutable
data class NewContactValue(
    val first: String,
    val phone: String,
    val last: String? = null,
    val email: String? = null,
    /** Sync to phone. `null`: off in [NewContactVariant.Grouped], on in [NewContactVariant.Outlined]. */
    val sync: Boolean? = null,
)

/** The two parity options for [NewContactForm] the team is choosing between. */
enum class NewContactVariant {
    /** iOS-revamp style: grouped rows on `sheet/bg`, Save in the header. */
    Grouped,

    /** WhatsApp style: outlined fields on `bg/surface` with a full-width Save at the bottom. */
    Outlined,
}

/**
 * New contact form. Header (padding 16 / 16 / 8 / 4, gap 8): close → "New contact" `title` Medium,
 * centred → small Save ([onSave]). Radius 24 on top.
 * - [NewContactVariant.Grouped]: `sheet/bg`; white groups (margin 8 / 16, radius 16) of
 *   name rows, Phone / Mobile / Email pairs (110dp label column, `subheading` Regular; "This phone
 *   number is on WhatsApp" `micro`) and the Sync contact to phone switch ([onSync]).
 * - [NewContactVariant.Outlined]: `bg/surface`; 48dp outlined fields (radius 8, 1dp
 *   `stroke/dark`, focused `button/primary`, floating 12/16 label) led by 22dp icons, the sync row
 *   with its hint, and a block Save pinned to the bottom (give the form a min height, e.g. the
 *   viewport, so it pins).
 */
@Composable
fun NewContactForm(
    value: NewContactValue,
    modifier: Modifier = Modifier,
    variant: NewContactVariant = NewContactVariant.Grouped,
    onClose: () -> Unit = {},
    onSave: () -> Unit = {},
    onSync: (Boolean) -> Unit = {},
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    val regular = type.subheading.copy(fontWeight = FontWeight.Normal)
    val grouped = variant == NewContactVariant.Grouped
    val header: @Composable () -> Unit = {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            PlainIconButton(EqualIcons.Close, "Close", onClose, tint = colors.textPrimary)
            EqualText(
                text = "New contact",
                style = type.title.copy(fontWeight = FontWeight.Medium),
                color = colors.textPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
            )
            EqualButton(text = "Save", onClick = onSave, size = EqualButtonSize.Xs)
        }
    }
    Column(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(if (grouped) colors.bgPrimary else colors.bgSurface),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        if (grouped) {
            Column {
                header()
                FormGroup {
                    Box(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp)) {
                        EqualText(text = value.first, style = regular, color = colors.textPrimary)
                    }
                    EqualDivider()
                    Box(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp)) {
                        EqualText(text = value.last ?: "Last name", style = regular, color = if (value.last != null) colors.textPrimary else colors.textSecondary)
                    }
                }
                FormGroup {
                    FormPair("Phone", "India")
                    EqualDivider()
                    FormPair("Mobile", value.phone, hint = "This phone number is on WhatsApp")
                    EqualDivider()
                    FormPair("Email", value.email ?: "Add email", placeholder = value.email == null)
                }
                FormGroup {
                    Row(
                        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        EqualText(text = "Sync contact to phone", style = regular, color = colors.textPrimary, modifier = Modifier.weight(1f))
                        EqualSwitch(checked = value.sync ?: false, onCheckedChange = onSync)
                    }
                }
            }
        } else {
            Column {
                header()
                Column(
                    Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    FieldLine(EqualIcons.Person) { OutlinedField("First name", value.first, Modifier.weight(1f)) }
                    FieldLine(null) { OutlinedField("Last name", value.last.orEmpty(), Modifier.weight(1f)) }
                    FieldLine(EqualIcons.Call) {
                        OutlinedField("Country", "IN +91", Modifier.width(92.dp))
                        OutlinedField("Phone", value.phone.removePrefix("+91 "), Modifier.weight(1f), tabular = true)
                    }
                    EqualText(
                        text = "This phone number is on WhatsApp",
                        style = type.micro,
                        color = colors.textSecondary,
                        modifier = Modifier.padding(start = 38.dp),
                    )
                    FieldLine(null) { OutlinedField("Email ID", value.email.orEmpty(), Modifier.weight(1f)) }
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.Top) {
                        EqualIcon(EqualIcons.Refresh, contentDescription = null, tint = colors.textSecondary, size = 22.dp)
                        Column(Modifier.weight(1f)) {
                            EqualText(text = "Sync contact to phone", style = regular, color = colors.textPrimary)
                            EqualText(text = "Only contacts with a phone number can be synced", style = type.captionRegular, color = colors.textSecondary)
                        }
                        EqualSwitch(checked = value.sync ?: true, onCheckedChange = onSync)
                    }
                }
            }
            Box(Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, bottom = 20.dp)) {
                EqualButton(text = "Save", onClick = onSave, size = EqualButtonSize.Block, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun FormGroup(content: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(EqualTheme.colors.bgSurface),
        content = content,
    )
}

@Composable
private fun FormPair(label: String, value: String, hint: String? = null, placeholder: Boolean = false) {
    val colors = EqualTheme.colors
    val regular = EqualTheme.type.subheading.copy(fontWeight = FontWeight.Normal)
    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        EqualText(text = label, style = regular, color = colors.textPrimary, modifier = Modifier.width(110.dp))
        Column(Modifier.weight(1f)) {
            EqualText(text = value, style = regular.tabular(), color = if (placeholder) colors.textSecondary else colors.textPrimary)
            if (hint != null) EqualText(text = hint, style = EqualTheme.type.micro, color = colors.textSecondary)
        }
    }
}

@Composable
private fun FieldLine(icon: androidx.compose.ui.graphics.vector.ImageVector?, content: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        if (icon != null) {
            EqualIcon(icon, contentDescription = null, tint = EqualTheme.colors.textSecondary, size = 22.dp)
        } else {
            Spacer(Modifier.width(22.dp))
        }
        content()
    }
}

/** WhatsApp-style outlined field: 48 high, radius 8, 1dp `stroke/dark` (focused `button/primary`), label floating on the border. */
@Composable
private fun OutlinedField(label: String, initial: String, modifier: Modifier, tabular: Boolean = false) {
    val colors = EqualTheme.colors
    val shape = RoundedCornerShape(8.dp)
    var text by remember(initial) { mutableStateOf(initial) }
    val interaction = rememberInteraction()
    val focused by interaction.collectIsFocusedAsState()
    val style = EqualTheme.type.bodyLarge.copy(color = colors.textPrimary)
    Box(modifier.padding(top = 8.dp)) {
        BasicTextField(
            value = text,
            onValueChange = { text = it },
            singleLine = true,
            interactionSource = interaction,
            textStyle = if (tabular) style.tabular() else style,
            cursorBrush = SolidColor(colors.textAction),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .border(if (focused) 1.5.dp else 1.dp, if (focused) colors.strokeFocus else colors.strokeDark, shape)
                .semantics { contentDescription = label },
            decorationBox = { inner ->
                Box(Modifier.fillMaxSize().padding(horizontal = 14.dp), contentAlignment = Alignment.CenterStart) { inner() }
            },
        )
        EqualText(
            text = label,
            style = EqualTheme.type.captionRegular,
            color = colors.textSecondary,
            maxLines = 1,
            modifier = Modifier
                .offset(x = 10.dp, y = (-8).dp)
                .background(colors.bgSurface)
                .padding(horizontal = 4.dp)
                .clearAndSetSemantics { },
        )
    }
}

// ---------------------------------------------------------------------------------------------
// Permission dialog
// ---------------------------------------------------------------------------------------------

/** 0 12 40 black/22%: the centered dialog card. */
private val DialogShadow = EqualShadow(listOf(EqualShadowLayer(0.dp, 12.dp, 40.dp, EqualPrimitives.black.copy(alpha = 0.22f))))

/**
 * Centered permission dialog over a dimmed chat (no permission yet). Fills its parent: black/45%
 * scrim (tap → [onClose]) and, padded 20, a card (`bg/surface`, radius 24, padding 24 / 16 / 16,
 * shadow 0 12 40 black/22%): 36dp close top-right, [art] 84 → 12 → [title] `title` Medium →
 * [body] `body1` `text/secondary` → 16 → block [cta] ([onAction]). It pops in (scale 0.92 → 1 +
 * fade over `motion.couponSpring`); still under reduced motion. With [onClose], system Back calls it.
 */
@Composable
fun PermissionDialog(
    title: String,
    body: String,
    cta: String,
    modifier: Modifier = Modifier,
    art: EqualAssets = EqualAssets.PermissionContacts,
    onAction: () -> Unit = {},
    onClose: (() -> Unit)? = null,
    /** Second action beside the CTA, e.g. "Not now" (vOIP mic prompt). */
    secondary: String? = null,
    onSecondary: (() -> Unit)? = null,
    /** Show the close button (the vOIP mic prompt has none). */
    closable: Boolean = true,
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    if (onClose != null) EqualBackHandler(onBack = onClose)
    val close = onClose ?: {}
    val still = rememberReduceMotion()
    val enter = remember { Animatable(if (still) 1f else 0f) }
    LaunchedEffect(Unit) { if (!still) enter.animateTo(1f, tween(EqualMotion.couponSpring, easing = EqualEasing.emphasized)) }
    val shape = RoundedCornerShape(24.dp)
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(
            Modifier
                .matchParentSize()
                .background(EqualPrimitives.black.copy(alpha = 0.45f))
                .clickable(interactionSource = rememberInteraction(), indication = null, onClickLabel = "Close", onClick = close),
        )
        Box(
            Modifier
                .padding(20.dp)
                .fillMaxWidth()
                .graphicsLayer {
                    val p = enter.value
                    alpha = p.coerceIn(0f, 1f)
                    scaleX = 0.92f + 0.08f * p
                    scaleY = 0.92f + 0.08f * p
                }
                .eqShadow(DialogShadow, shape)
                .clip(shape)
                .background(colors.bgSurface)
                .pointerInput(Unit) { detectTapGestures { } }
                .semantics { paneTitle = title },
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                EqualAssetImage(art, size = 84.dp)
                EqualText(
                    text = title,
                    style = type.title.copy(fontWeight = FontWeight.Medium),
                    color = colors.textPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 12.dp).semantics { heading() },
                )
                EqualText(
                    text = body,
                    style = type.body1.copy(fontWeight = FontWeight.Normal),
                    color = colors.textSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp),
                )
                if (secondary != null) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        EqualButton(text = secondary, onClick = onSecondary ?: close, emphasis = EqualButtonEmphasis.Secondary)
                        EqualButton(text = cta, onClick = onAction, modifier = Modifier.weight(1f))
                    }
                } else {
                    EqualButton(text = cta, onClick = onAction, size = EqualButtonSize.Block, modifier = Modifier.fillMaxWidth())
                }
            }
            if (closable) SmallCloseButton(close, Modifier.align(Alignment.TopEnd).padding(top = 12.dp, end = 12.dp))
        }
    }
}

/** 36dp neutral close: `bg/surface`, `raised` shadow, close 18 `text/secondary` (48dp touch target). */
@Composable
private fun SmallCloseButton(onClick: () -> Unit, modifier: Modifier) {
    val interaction = rememberInteraction()
    Box(modifier.minimumInteractiveComponentSize(), contentAlignment = Alignment.Center) {
        Box(
            Modifier
                .eqPressFeedback(interaction, CircleShape)
                .size(36.dp)
                .eqShadow(EqualShadows.raised, CircleShape)
                .clip(CircleShape)
                .background(EqualTheme.colors.bgSurface)
                .clickable(interactionSource = interaction, indication = null, role = Role.Button, onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            EqualIcon(EqualIcons.Close, contentDescription = "Close", tint = EqualTheme.colors.textSecondary, size = 18.dp)
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Offline
// ---------------------------------------------------------------------------------------------

/** How an [OfflineSheet] is drawn. */
enum class OfflineSheetMode {
    /** Over a chat: scrim + the panel sliding up from the bottom. */
    Overlay,

    /** Only the panel (docs). */
    Inline,
}

/**
 * No-internet sheet over a chat: panel (`bg/surface`, radius 28 on top, padding 24 / 16 / 20,
 * shadow 0 -8 30 black/12%) with the no-internet illustration 64 → 8 → "No internet connection"
 * `title` Medium → why `body2` `text/secondary` → 16 → block Refresh ([onRefresh]).
 * [OfflineSheetMode.Overlay] fills its parent with the `scrim` and slides the panel up over
 * `motion.slow` (none under reduced motion). With [onDismiss], system Back and a scrim tap call it.
 */
@Composable
fun OfflineSheet(
    modifier: Modifier = Modifier,
    mode: OfflineSheetMode = OfflineSheetMode.Overlay,
    onRefresh: () -> Unit = {},
    onDismiss: (() -> Unit)? = null,
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    val shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    val panel: @Composable (Modifier) -> Unit = { m ->
        Column(
            m
                .fillMaxWidth()
                .eqShadow(SheetShadow, shape)
                .clip(shape)
                .background(colors.bgSurface)
                .semantics { paneTitle = "No internet connection" }
                .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            EqualAssetImage(EqualAssets.IllustrationNoInternet, size = 64.dp)
            EqualText(
                text = "No internet connection",
                style = type.title.copy(fontWeight = FontWeight.Medium),
                color = colors.textPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp).semantics { heading() },
            )
            EqualText(
                text = OfflineBody,
                style = type.body2.copy(fontWeight = FontWeight.Normal),
                color = colors.textSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 2.dp, bottom = 16.dp),
            )
            EqualButton(text = "Refresh", onClick = onRefresh, size = EqualButtonSize.Block, modifier = Modifier.fillMaxWidth())
        }
    }
    if (mode == OfflineSheetMode.Inline) {
        panel(modifier)
        return
    }
    val reduceMotion = rememberReduceMotion()
    if (onDismiss != null) EqualBackHandler(onBack = onDismiss)
    val visible = remember { MutableTransitionState(reduceMotion).apply { targetState = true } }
    Box(modifier.fillMaxSize()) {
        Box(
            Modifier
                .matchParentSize()
                .background(colors.bgOverlay)
                .then(
                    if (onDismiss != null) {
                        Modifier.clickable(interactionSource = rememberInteraction(), indication = null, onClickLabel = "Dismiss", onClick = onDismiss)
                    } else {
                        Modifier.pointerInput(Unit) { detectTapGestures { } }
                    },
                ),
        )
        AnimatedVisibility(
            visibleState = visible,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = slideInVertically(tween(EqualMotion.slow, easing = EqualEasing.standard)) { it } +
                fadeIn(tween(EqualMotion.slow, easing = EqualEasing.standard)),
        ) {
            panel(Modifier)
        }
    }
}

private const val OfflineBody = "We could not connect to a stable internet. Check your connection and try again."

/**
 * Full-screen no-internet state (nothing to show yet) on `gradient/offline-screen`: centred
 * illustration 120 → 12 → "No internet connection" `title` Medium → why `body1` `text/secondary`
 * (padding 24 / 28), with a block Try again ([onRetry]) pinned at the bottom (padding 0 / 16 / 24).
 */
@Composable
fun OfflineScreen(
    modifier: Modifier = Modifier,
    onRetry: () -> Unit = {},
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    Column(
        modifier
            .fillMaxSize()
            .background(SolidColor(EqualTheme.colors.chatWallpaperBg)),
    ) {
        Column(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 28.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            EqualAssetImage(EqualAssets.IllustrationNoInternet, size = 120.dp)
            EqualText(
                text = "No internet connection",
                style = type.title.copy(fontWeight = FontWeight.Medium),
                color = colors.textPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 12.dp),
            )
            EqualText(
                text = OfflineBody,
                style = type.body1.copy(fontWeight = FontWeight.Normal),
                color = colors.textSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
        Box(Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, bottom = 24.dp)) {
            EqualButton(text = "Try again", onClick = onRetry, size = EqualButtonSize.Block, modifier = Modifier.fillMaxWidth())
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Dialer stand-in
// ---------------------------------------------------------------------------------------------

/**
 * A coded stand-in for the phone's dialer during Call Back, for docs and previews only: the OS
 * draws the real dialer, so in the app hand off with `Intent.ACTION_DIAL` / `ACTION_CALL` and show
 * the result when the user comes back. Always dark (system UI): "Calling via [carrier]", [number]
 * 40/48, [place]; Keypad / Mute / Phone / More keys (60dp); the red End call pill ([onEnd]).
 * Colours are primitives because it imitates system chrome, not Equal UI.
 */
@Composable
fun DialerStandIn(
    number: String,
    modifier: Modifier = Modifier,
    place: String = "Bangalore, Karnataka",
    carrier: String = "airtel",
    onEnd: () -> Unit = {},
) {
    val type = EqualTheme.type
    val white = EqualPrimitives.white
    val dim = white.copy(alpha = 0.8f)
    val line = type.subheading.copy(fontWeight = FontWeight.Normal, textAlign = TextAlign.Center)
    Column(
        modifier
            .fillMaxSize()
            .background(EqualPrimitives.grey02)
            .semantics { contentDescription = "Calling $number" }
            .padding(start = 16.dp, end = 16.dp, top = 72.dp, bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            BasicText(
                text = buildAnnotatedString {
                    append("Calling via ")
                    withStyle(SpanStyle(color = DarkEqualColors.contactAvatarBlueText)) { append(carrier) }
                },
                style = line.copy(color = dim),
            )
            EqualText(
                text = number,
                style = type.headline.copy(fontSize = 40.sp, lineHeight = 48.sp, fontWeight = FontWeight.Normal, letterSpacing = (-0.4).sp).tabular(),
                color = white,
                textAlign = TextAlign.Center,
            )
            EqualText(text = place, style = line, color = dim)
        }
        Spacer(Modifier.weight(1f))
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            listOf(
                "Keypad" to ai.equal.ds.icons.EqualSymbols.menu,
                "Mute" to EqualIcons.Mic,
                "Phone" to EqualIcons.Call,
                "More" to EqualIcons.MoreVert,
            ).forEach { (label, icon) ->
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(Modifier.size(60.dp).background(EqualPrimitives.grey03, CircleShape), contentAlignment = Alignment.Center) {
                        EqualIcon(icon, contentDescription = null, tint = white, size = 24.dp)
                    }
                    EqualText(text = label, style = type.captionRegular, color = dim, maxLines = 1)
                }
            }
        }
        Box(
            Modifier
                .padding(top = 28.dp)
                .width(128.dp)
                .height(56.dp)
                .clip(CircleShape)
                .background(EqualPrimitives.red12)
                .clickable(role = Role.Button, onClickLabel = "End call", onClick = onEnd),
            contentAlignment = Alignment.Center,
        ) {
            EqualIcon(EqualIcons.CallFilled, contentDescription = "End call", tint = EqualPrimitives.red02, size = 30.dp, modifier = Modifier.rotate(135f))
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Previews
// ---------------------------------------------------------------------------------------------

@EqualPreviews
@Composable
private fun AiTurnPreview() {
    PreviewFrame {
        Column(
            Modifier.background(EqualTheme.colors.chatWallpaperBg).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            AiTurn(
                text = "Are you sure? You’ll not get any call from them even if it’s important",
                actions = listOf(ReplyAction(label = "Mute", emoji = "🔕")),
            )
            HoldToSpeakBar()
        }
    }
}

@EqualPreviews
@Composable
private fun EditBubblePreview() {
    PreviewFrame(padding = 0.dp) {
        Box(Modifier.background(EditDimColor)) {
            EditBubble(value = "Hi Amit! Count me in for Sunday 8 PM. See you there 👍")
        }
    }
}

@EqualPreviews
@Composable
private fun NewContactGroupedPreview() {
    PreviewFrame(padding = 0.dp) {
        NewContactForm(value = NewContactValue(first = "Rahul (Electrician)", phone = "+91 98717 46113"))
    }
}

@EqualPreviews
@Composable
private fun NewContactOutlinedPreview() {
    PreviewFrame(padding = 0.dp) {
        NewContactForm(
            value = NewContactValue(first = "Rahul (Electrician)", phone = "+91 98717 46113", sync = true),
            variant = NewContactVariant.Outlined,
            modifier = Modifier.heightIn(min = 520.dp),
        )
    }
}

@EqualPreviews
@Composable
private fun OfflinePreview() {
    PreviewFrame {
        OfflineSheet(mode = OfflineSheetMode.Inline)
        Spacer(Modifier.height(16.dp))
        Box(Modifier.height(440.dp)) { OfflineScreen() }
    }
}

@EqualPreviews
@Composable
private fun PermissionDialogPreview() {
    PreviewFrame(padding = 0.dp) {
        Box(Modifier.height(520.dp)) {
            PermissionDialog(
                title = "Allow contacts permission",
                body = "I need your contacts permission to save contacts into your phone",
                cta = "Open Settings",
            )
        }
    }
}

@EqualPreviews
@Composable
private fun DialerStandInPreview() {
    PreviewFrame(padding = 0.dp) {
        Box(Modifier.height(EqualFrameHeightDp.dp)) { DialerStandIn(number = "+91 80 4681 0750") }
    }
}
