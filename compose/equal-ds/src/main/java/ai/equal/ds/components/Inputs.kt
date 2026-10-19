package ai.equal.ds.components

import androidx.compose.foundation.clickable
import ai.equal.ds.icons.EqualIcon
import ai.equal.ds.icons.EqualIcons
import ai.equal.ds.theme.EqualTheme
import ai.equal.ds.theme.eqFocusRing
import ai.equal.ds.tokens.EqualRadius
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ai.equal.ds.theme.tabular
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.layout.heightIn


/** Text alignment inside [EqualTextField]. */
enum class TextFieldAlign { Start, Center }

/**
 * Text field (derived), matching web `TextField`.
 * - Optional [label]: `overline` `text/tertiary`, uppercased, with `*` when [required], plus a
 *   `caption` Regular "(Optional)" when [optional]. Omit it for fields that share a group label
 *   (then pass [contentDescription]).
 * - Input: min height 48, `bg/surface`, 12dp corners on every text field, padding 12 / 14. No
 *   visible border in light mode; `stroke/subtle` in dark. Focus: `action/stroke/unread` + `focusRing`; error: `text/danger` border.
 * - Value `bodyLarge` `text/primary`, placeholder `text/placeholder`; [align] Start or Center.
 *   [multiline] shows two lines minimum. [tabular] turns on tabular figures (pincode).
 * - Below: [error] (`captionWrap` `text/danger`) or [helper] (rich text, `captionWrap`
 *   `text/secondary`; style spans for bold parts).
 */
@Composable
fun EqualTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String = "",
    optional: Boolean = false,
    optionalLabel: String = "(Optional)",
    required: Boolean = false,
    helper: AnnotatedString? = null,
    error: String? = null,
    enabled: Boolean = true,
    multiline: Boolean = false,
    align: TextFieldAlign = TextFieldAlign.Start,
    tabular: Boolean = false,
    contentDescription: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    val interaction = rememberInteraction()
    val focused by interaction.collectIsFocusedAsState()
    val fieldShape = RoundedCornerShape(EqualRadius.rMd)
    val borderColor = when {
        error != null -> colors.textDanger
        focused -> colors.actionStrokeUnread
        colors.isDark -> colors.strokeSubtle
        else -> Color.Transparent
    }
    val textAlign = if (align == TextFieldAlign.Center) TextAlign.Center else TextAlign.Start
    val baseStyle = if (tabular) type.bodyLarge.tabular() else type.bodyLarge
    val a11y = contentDescription ?: label
    Column(modifier.fillMaxWidth()) {
        if (label != null) {
            Row(verticalAlignment = Alignment.Bottom) {
                EqualText(
                    text = label.uppercase() + if (required) "*" else "",
                    style = type.overline,
                    color = colors.textSecondary,
                    maxLines = 1,
                )
                if (optional) {
                    Spacer(Modifier.width(6.dp))
                    EqualText(text = optionalLabel, style = type.captionRegular, color = colors.textSecondary, maxLines = 1)
                }
            }
            Spacer(Modifier.height(8.dp))
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
                .eqFocusRing(focused && error == null, fieldShape)
                .background(colors.bgSurface, fieldShape)
                .border(1.dp, borderColor, fieldShape)
                .semantics {
                    if (a11y != null) this.contentDescription = a11y
                    // TalkBack announces the error when the field is focused.
                    if (error != null) error(error)
                },
            enabled = enabled,
            singleLine = !multiline,
            minLines = if (multiline) 2 else 1,
            textStyle = baseStyle.copy(color = colors.textPrimary, textAlign = textAlign),
            cursorBrush = SolidColor(colors.textAction),
            interactionSource = interaction,
            keyboardOptions = keyboardOptions,
            decorationBox = { inner ->
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    contentAlignment = if (align == TextFieldAlign.Center) Alignment.Center else Alignment.CenterStart,
                ) {
                    if (value.isEmpty() && placeholder.isNotEmpty()) {
                        EqualText(
                            text = placeholder,
                            style = baseStyle,
                            color = colors.textPlaceholder,
                            maxLines = if (multiline) 2 else 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = textAlign,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    inner()
                }
            },
        )
        if (error != null) {
            Spacer(Modifier.height(6.dp))
            EqualText(text = error, style = type.captionWrap, color = colors.textDanger)
        } else if (helper != null) {
            Spacer(Modifier.height(6.dp))
            BasicText(text = helper, style = type.captionWrap.copy(color = colors.textSecondary))
        }
    }
}

/**
 * Search field (derived). 48 high pill, `bg/surface`, search icon 20 `text/tertiary`, `bodyLarge`.
 * [onActivate]: Home uses the field as an entry point; tapping it opens the search screen instead
 * of taking input.
 */
@Composable
fun SearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search for names",
    onActivate: (() -> Unit)? = null,
    contentDescription: String = placeholder,
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    if (onActivate != null) {
        Row(
            modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(CircleShape)
                .background(colors.bgSurface)
                .clickable(onClickLabel = placeholder, role = Role.Button, onClick = onActivate)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            EqualIcon(EqualIcons.Search, contentDescription = null, tint = colors.textSecondary, size = 20.dp)
            Spacer(Modifier.width(8.dp))
            EqualText(text = placeholder, style = type.bodyLarge, color = colors.textPlaceholder, maxLines = 1)
        }
        return
    }
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(colors.bgSurface, CircleShape)
            // The placeholder disappears once there is text, so the field needs its own name.
            .semantics { this.contentDescription = contentDescription },
        singleLine = true,
        textStyle = type.bodyLarge.copy(color = colors.textPrimary),
        cursorBrush = SolidColor(colors.textAction),
        decorationBox = { inner ->
            Row(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                EqualIcon(EqualIcons.Search, contentDescription = null, tint = colors.textSecondary, size = 20.dp)
                Spacer(Modifier.width(8.dp))
                Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                    if (value.isEmpty()) {
                        EqualText(text = placeholder, style = type.bodyLarge, color = colors.textPlaceholder, maxLines = 1)
                    }
                    inner()
                }
            }
        },
    )
}

/**
 * Segmented label chips, e.g. Home / Work / Other (derived). 36 high pills, h16, `button`.
 * Selected: `chip/selected-bg` + `chip/selected-text` (dark chip). Unselected: `bg/surface`,
 * 1dp `stroke/medium`, `text/secondary`. [contentDescription] names the radio group for TalkBack
 * (e.g. "Address label").
 */
@Composable
fun SegmentedChips(
    options: List<String>,
    selectedIndex: Int,
    onSelectedIndexChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    val colors = EqualTheme.colors
    Row(
        modifier
            .selectableGroup()
            .then(if (contentDescription != null) Modifier.semantics { this.contentDescription = contentDescription } else Modifier),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        options.forEachIndexed { index, option ->
            val selected = index == selectedIndex
            val surface = if (selected) {
                Modifier
                    .background(colors.chipSelectedBg, CircleShape)
                    .border(1.dp, colors.chipSelectedBg, CircleShape)
            } else {
                Modifier
                    .background(colors.bgSurface, CircleShape)
                    .border(1.dp, colors.strokeMedium, CircleShape)
            }
            Box(
                Modifier
                    .minimumInteractiveComponentSize()
                    .height(36.dp)
                    .then(surface)
                    .clip(CircleShape)
                    .selectable(selected = selected, role = Role.RadioButton, onClick = { onSelectedIndexChange(index) })
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center,
            ) {
                EqualText(
                    text = option,
                    style = EqualTheme.type.button,
                    color = if (selected) colors.chipSelectedText else colors.textSecondary,
                    maxLines = 1,
                )
            }
        }
    }
}

@EqualPreviews
@Composable
private fun InputsPreview() {
    PreviewFrame {
        var name by remember { mutableStateOf("") }
        EqualTextField(value = name, onValueChange = { name = it }, label = "Full name", placeholder = "Enter your name")
        VSpace(12.dp)
        var landmark by remember { mutableStateOf("Near the park") }
        EqualTextField(
            value = landmark,
            onValueChange = { landmark = it },
            label = "Landmark",
            optional = true,
            helper = AnnotatedString("Helps delivery partners find you"),
        )
        VSpace(12.dp)
        var pin by remember { mutableStateOf("5600") }
        EqualTextField(value = pin, onValueChange = { pin = it }, label = "Pincode", error = "Enter a 6-digit pincode")
        VSpace(12.dp)
        var query by remember { mutableStateOf("") }
        SearchField(value = query, onValueChange = { query = it })
        VSpace(12.dp)
        var selected by remember { mutableIntStateOf(0) }
        SegmentedChips(options = listOf("Home", "Work", "Other"), selectedIndex = selected, onSelectedIndexChange = { selected = it })
    }
}
