package ai.equal.ds.components

import ai.equal.ds.assets.EqualAssetImage
import ai.equal.ds.assets.EqualAssets
import ai.equal.ds.assets.EqualCategory
import ai.equal.ds.icons.EqualIcon
import ai.equal.ds.icons.EqualIcons
import ai.equal.ds.theme.EqualTheme
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Search (Search design), matching web `Search.tsx`: the search bar, recent searches, result
// sections, message hits with the match in bold, and the empty / no internet / no results /
// loading states.

/**
 * Pill search bar, web `SearchBar`: 52 high, `bg/surface`, 1.5dp `button/primary` outline (it is
 * the active field), back arrow inside on the left, `bodyLarge` input (placeholder
 * `text/secondary`, caret `text/action`) and a clear × (`text/secondary`) once there is a query.
 * Margin 8 vertical / 16 horizontal is part of the bar. [autoFocus] focuses it on first show.
 */
@Composable
fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    placeholder: String = "Search for names",
    autoFocus: Boolean = false,
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    val focus = remember { FocusRequester() }
    if (autoFocus) {
        LaunchedEffect(Unit) { runCatching { focus.requestFocus() } }
    }
    Row(
        modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .height(52.dp)
            .clip(CircleShape)
            .background(colors.bgSurface)
            .border(1.5.dp, colors.buttonPrimary, CircleShape)
            .padding(start = 4.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Box(
            Modifier
                .minimumInteractiveComponentSize()
                .clip(CircleShape)
                .clickable(onClickLabel = "Back", role = Role.Button, onClick = onBack)
                .semantics { contentDescription = "Back" },
            contentAlignment = Alignment.Center,
        ) {
            EqualIcon(EqualIcons.Back, contentDescription = null, tint = colors.textPrimary, size = 22.dp)
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .weight(1f)
                .focusRequester(focus)
                .semantics { contentDescription = placeholder },
            singleLine = true,
            textStyle = type.bodyLarge.copy(color = colors.textPrimary),
            cursorBrush = SolidColor(colors.textAction),
            decorationBox = { inner ->
                Box(contentAlignment = Alignment.CenterStart) {
                    if (value.isEmpty()) {
                        EqualText(text = placeholder, style = type.bodyLarge, color = colors.textSecondary, maxLines = 1)
                    }
                    inner()
                }
            },
        )
        if (value.isNotEmpty()) {
            Box(
                Modifier
                    .minimumInteractiveComponentSize()
                    .clip(CircleShape)
                    .clickable(onClickLabel = "Clear search", role = Role.Button, onClick = { onValueChange("") })
                    .semantics { contentDescription = "Clear search" },
                contentAlignment = Alignment.Center,
            ) {
                EqualIcon(EqualIcons.Close, contentDescription = null, tint = colors.textSecondary, size = 18.dp)
            }
        }
    }
}

/**
 * Section label, web `SearchSection`: [title] as an `overline` in `text/secondary` (uppercased
 * here), with an optional "Clear all" pill on the right (`caption`, `bg/surface`, `text/primary`).
 */
@Composable
fun SearchSection(
    title: String,
    modifier: Modifier = Modifier,
    onClear: (() -> Unit)? = null,
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    Row(
        modifier
            .fillMaxWidth()
            // The pill's 48dp touch target takes the place of 12dp of padding above and below.
            .padding(start = 16.dp, end = 16.dp, top = if (onClear != null) 2.dp else 14.dp, bottom = if (onClear != null) 0.dp else 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        EqualText(text = title.uppercase(), style = type.overline, color = colors.textSecondary, maxLines = 1, modifier = Modifier.semantics { heading() })
        if (onClear != null) {
            Box(
                Modifier.minimumInteractiveComponentSize(),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    Modifier
                        .clip(CircleShape)
                        .background(colors.bgSurface)
                        .clickable(role = Role.Button, onClick = onClear)
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                ) {
                    EqualText(text = "Clear all", style = type.caption, color = colors.textPrimary, maxLines = 1)
                }
            }
        }
    }
}

/** One recent search: [initials] for a saved contact, otherwise the [category] avatar. */
data class RecentSearch(
    val label: String,
    val category: EqualCategory? = null,
    val initials: String? = null,
)

/**
 * Recent searches as avatar tiles, web `RecentSearches`: horizontally scrolling, gap 14, each a
 * 72dp-wide column with a 52dp avatar and the `caption` (Regular) label truncated underneath.
 * Tapping a tile calls [onPick] with its label (search for it again).
 */
@Composable
fun RecentSearches(
    items: List<RecentSearch>,
    modifier: Modifier = Modifier,
    onPick: (String) -> Unit = {},
) {
    val colors = EqualTheme.colors
    val caption = EqualTheme.type.caption.copy(fontWeight = FontWeight.Normal)
    Row(
        modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        items.forEach { r ->
            Column(
                Modifier
                    .width(72.dp)
                    .clickable(onClickLabel = "Search ${r.label}", role = Role.Button) { onPick(r.label) },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                ContactAvatar(size = 52.dp, category = r.category ?: EqualCategory.NA, initials = r.initials)
                EqualText(
                    text = r.label,
                    style = caption,
                    color = colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

/**
 * Web `Highlight`: [text] with every case-insensitive occurrence of [query] (trimmed) in
 * [style] (Bold by default; pass a colour such as `text/primary` to tint the match too).
 */
fun highlight(
    text: String,
    query: String,
    style: SpanStyle = SpanStyle(fontWeight = FontWeight.Bold),
): AnnotatedString {
    val q = query.trim()
    if (q.isEmpty()) return AnnotatedString(text)
    return buildAnnotatedString {
        var i = 0
        var at = text.indexOf(q, i, ignoreCase = true)
        while (at != -1) {
            if (at > i) append(text.substring(i, at))
            withStyle(style) { append(text.substring(at, at + q.length)) }
            i = at + q.length
            at = text.indexOf(q, i, ignoreCase = true)
        }
        if (i < text.length) append(text.substring(i))
    }
}

/**
 * A message hit, web `MessageResult`: [name] (`subheading`) and [time] (`micro`, `text/secondary`)
 * on top, then the line (`body1` Regular) with every match of [query] in Bold `text/primary`.
 * Padding 16 / 10. Separate consecutive hits with an [EqualDivider].
 */
@Composable
fun MessageResult(
    name: String,
    time: String,
    text: String,
    query: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    val line = remember(text, query, colors.textPrimary) {
        highlight(text, query, SpanStyle(fontWeight = FontWeight.Bold, color = colors.textPrimary))
    }
    Column(
        modifier
            .fillMaxWidth()
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            EqualText(
                text = name,
                style = type.subheading,
                color = colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            Spacer(Modifier.width(8.dp))
            EqualText(text = time, style = type.micro, color = colors.textSecondary, maxLines = 1)
        }
        BasicText(
            text = line,
            style = type.body1.copy(color = colors.textPrimary, fontWeight = FontWeight.Normal),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/**
 * Web `LoadingDots`: three 9dp `button/primary` dots, gap 8, 24 above and below, pulsing in turn
 * (1s loop, 150ms apart: 30% → 100% opacity, 0.85 → 1 scale). Under reduced motion they hold
 * still at full opacity. Announced politely as [label].
 */
@Composable
fun LoadingDots(
    modifier: Modifier = Modifier,
    label: String = "Loading",
) {
    val colors = EqualTheme.colors
    val reduceMotion = rememberReduceMotion()
    Row(
        modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp)
            .semantics {
                liveRegion = LiveRegionMode.Polite
                contentDescription = label
            },
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
    ) {
        if (reduceMotion) {
            repeat(3) { Dot(color = colors.buttonPrimary, alpha = 1f, scale = 1f) }
        } else {
            val transition = rememberInfiniteTransition(label = "loadingDots")
            repeat(3) { i ->
                val t by transition.animateFloat(
                    initialValue = 0f,
                    targetValue = 0f,
                    animationSpec = infiniteRepeatable(
                        animation = keyframes {
                            durationMillis = 1000
                            0f at 0 using FastOutSlowInEasing
                            1f at 400 using FastOutSlowInEasing
                            0f at 800
                            0f at 1000
                        },
                        initialStartOffset = StartOffset(i * 150),
                    ),
                    label = "dot$i",
                )
                Dot(color = colors.buttonPrimary, alpha = 0.3f + 0.7f * t, scale = 0.85f + 0.15f * t)
            }
        }
    }
}

@Composable
private fun Dot(color: Color, alpha: Float, scale: Float) {
    Box(
        Modifier
            .size(9.dp)
            .scale(scale)
            .alpha(alpha)
            .background(color, CircleShape)
            .clearAndSetSemantics { },
    )
}

/**
 * Centred illustration state, web `SearchState` (search empty, no internet, no results): [art] at
 * [size], 12 → [title] (`title`, `text/primary`), 4 → [body] (`body1` Regular, `text/secondary`,
 * at most 280 wide). Padding 48 top / 28 sides / 24 bottom.
 */
@Composable
fun SearchState(
    art: EqualAssets,
    title: String,
    body: String,
    modifier: Modifier = Modifier,
    size: Dp = 160.dp,
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    Column(
        modifier
            .fillMaxWidth()
            .padding(start = 28.dp, end = 28.dp, top = 48.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        EqualAssetImage(art, size)
        Spacer(Modifier.height(12.dp))
        EqualText(text = title, style = type.title, color = colors.textPrimary, textAlign = TextAlign.Center, modifier = Modifier.semantics { heading() })
        Spacer(Modifier.height(4.dp))
        EqualText(
            text = body,
            style = type.body1.copy(fontWeight = FontWeight.Normal),
            color = colors.textSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = 280.dp),
        )
    }
}

// ---------------------------------------------------------------------------------------------
// Previews (light + dark)
// ---------------------------------------------------------------------------------------------

@EqualPreviews
@Composable
private fun SearchBarPreview() {
    PreviewFrame(padding = 0.dp) {
        var empty by remember { mutableStateOf("") }
        SearchBar(value = empty, onValueChange = { empty = it })
        var typed by remember { mutableStateOf("Sa") }
        SearchBar(value = typed, onValueChange = { typed = it })
    }
}

@EqualPreviews
@Composable
private fun RecentSearchesPreview() {
    PreviewFrame(padding = 0.dp) {
        SearchSection(title = "Recent searches", onClear = {})
        RecentSearches(
            items = listOf(
                RecentSearch("Amit Nanda", EqualCategory.Career),
                RecentSearch("Shankar", EqualCategory.Ride),
                RecentSearch("DSR Builder", EqualCategory.Financial),
                RecentSearch("Priya Sharma", initials = "PS"),
            ),
        )
    }
}

@EqualPreviews
@Composable
private fun MessageResultsPreview() {
    PreviewFrame(padding = 0.dp) {
        SearchSection(title = "Messages")
        MessageResult(name = "Santosh Anna", time = "3:02 PM", text = "Caller said that they’ll call you in the evening", query = "Sa")
        EqualDivider()
        MessageResult(name = "Amit Nanda", time = "3:02 PM", text = "Same address, near Gachibowli", query = "Sa")
        SearchSection(title = "Chats")
        LoadingDots(label = "Searching chats")
    }
}

@EqualPreviews
@Composable
private fun SearchStatesPreview() {
    PreviewFrame(padding = 0.dp) {
        SearchState(art = EqualAssets.IllustrationSearch, size = 120.dp, title = "Search across your calls", body = "Look for names, phone numbers, categories, keywords")
        SearchState(art = EqualAssets.IllustrationNoInternet, size = 100.dp, title = "No internet connection", body = "We could not connect to a stable internet. Check your connection and try again.")
        SearchState(art = EqualAssets.IllustrationNoResults, size = 110.dp, title = "No results found", body = "Couldn’t find anything matching. Try a different name, number, category or keyword")
    }
}
