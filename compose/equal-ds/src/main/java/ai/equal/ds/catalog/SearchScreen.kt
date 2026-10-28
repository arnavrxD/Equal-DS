package ai.equal.ds.catalog

import ai.equal.ds.assets.EqualAssets
import ai.equal.ds.assets.EqualCategory
import ai.equal.ds.components.ActionChipData
import ai.equal.ds.components.ChatListItem
import ai.equal.ds.components.ChatListItemState
import ai.equal.ds.components.EqualDivider
import ai.equal.ds.components.EqualFrameWidthDp
import ai.equal.ds.components.KeyboardSpace
import ai.equal.ds.components.LoadingDots
import ai.equal.ds.components.MessageResult
import ai.equal.ds.components.RecentSearch
import ai.equal.ds.components.RecentSearches
import ai.equal.ds.components.SearchBar
import ai.equal.ds.components.SearchSection
import ai.equal.ds.components.StatusBar
import ai.equal.ds.components.SearchState as SearchStateView
import ai.equal.ds.theme.EqualTheme
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

// =============================================================================================
// Search (Search design), matching web `SearchScreen`
// =============================================================================================

/** Search screen states, web `SearchState` ('empty' … 'messages'). */
enum class SearchState { Empty, Offline, Recent, NoResults, Loading, Results, MessagesLoading, Messages }

/** The sample recent searches: Amit Nanda (career), Shankar (ride), DSR Builder (financial). */
val SampleRecentSearches: List<RecentSearch> = listOf(
    RecentSearch("Amit Nanda", EqualCategory.Career),
    RecentSearch("Shankar", EqualCategory.Ride),
    RecentSearch("DSR Builder", EqualCategory.Financial),
)

/** Chats the sample search looks through: Saurabh and Santosh Anna first, then the Home rows. */
val SearchSampleChats: List<HomeSampleChat> = listOf(
    HomeSampleChat(
        "Saurabh",
        "About a miniature painting that you asked for",
        "Yesterday",
        EqualCategory.HomeServices,
        unread = 2,
        chips = listOf(ActionChipData("Call back", "📞"), ActionChipData("Update Address", "✏️")),
        id = "s1",
    ),
    HomeSampleChat("Santosh Anna", "Order left with security", "12 July", EqualCategory.Delivery, id = "s2"),
) + HomeSampleChats

/** One message hit in the sample search. */
data class SearchMessage(val name: String, val time: String, val text: String)

/** Messages the sample search looks through. */
val SearchSampleMessages: List<SearchMessage> = listOf(
    SearchMessage("Santosh Anna", "3:02 PM", "Caller said that they’ll call you in the evening"),
    SearchMessage("Amit Nanda", "3:02 PM", "Same address, near Gachibowli"),
)

private enum class SearchPhase { Idle, Loading, Chats, Done }

/**
 * Search, web `SearchScreen`: [SearchBar] (green-outlined pill, back inside) under the status bar,
 * then recent searches as avatar tiles, then live results in two sections: CHATS first (they load
 * fastest), MESSAGES after with the match in bold. Empty, no internet and no results each have
 * their own illustration.
 *
 * Live (no [state]): typing → [SearchState.Loading] (500ms) → chats + [SearchState.MessagesLoading]
 * (to 1200ms) → [SearchState.Messages], or [SearchState.NoResults] when nothing matches. "Clear
 * all" on recents empties them ([SearchState.Empty]); on CHATS it clears the query.
 * [state] + [query] pin a state (docs). [keyboard] shows [KeyboardSpace] (docs / previews only:
 * the keyboard is the system's).
 */
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onOpenChat: (HomeSampleChat) -> Unit = {},
    state: SearchState? = null,
    query: String = "",
    keyboard: Boolean = false,
    recentSearches: List<RecentSearch> = SampleRecentSearches,
) {
    val colors = EqualTheme.colors
    var q by remember(query) { mutableStateOf(query) }
    var recent by remember(recentSearches) { mutableStateOf(recentSearches) }
    var phase by remember { mutableStateOf(SearchPhase.Idle) }
    LaunchedEffect(q, state) {
        if (state != null || q.isBlank()) {
            phase = SearchPhase.Idle
            return@LaunchedEffect
        }
        phase = SearchPhase.Loading
        delay(500)
        phase = SearchPhase.Chats
        delay(700)
        phase = SearchPhase.Done
    }
    val term = q.trim()
    val matchingChats = if (term.isEmpty()) {
        emptyList()
    } else {
        SearchSampleChats.filter { "${it.name} ${it.preview}".contains(term, ignoreCase = true) }.take(4)
    }
    val matchingMessages = if (term.isEmpty()) emptyList() else SearchSampleMessages.filter { it.text.contains(term, ignoreCase = true) }
    val s: SearchState = state ?: when {
        term.isEmpty() -> if (recent.isNotEmpty()) SearchState.Recent else SearchState.Empty
        phase == SearchPhase.Loading -> SearchState.Loading
        phase == SearchPhase.Done && matchingChats.isEmpty() && matchingMessages.isEmpty() -> SearchState.NoResults
        phase == SearchPhase.Chats -> SearchState.MessagesLoading
        phase == SearchPhase.Done -> SearchState.Messages
        else -> SearchState.Loading
    }
    val shownChats = matchingChats.take(2)
    val showRecentLabel = s == SearchState.Empty || s == SearchState.Offline || s == SearchState.Recent || s == SearchState.NoResults
    val withMessages = s == SearchState.MessagesLoading || s == SearchState.Messages

    Column(
        modifier
            .fillMaxSize()
            .background(colors.bgPrimary),
    ) {
        StatusBar()
        SearchBar(value = q, onValueChange = { q = it }, onBack = onBack, autoFocus = state == null)
        Column(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
        ) {
            if (showRecentLabel) {
                SearchSection(title = "Recent searches", onClear = if (s == SearchState.Recent) ({ recent = emptyList() }) else null)
            }
            when (s) {
                SearchState.Recent -> RecentSearches(items = recent, onPick = { q = it })
                SearchState.Empty -> SearchStateView(
                    art = EqualAssets.IllustrationSearch,
                    size = 170.dp,
                    title = "Search across your calls",
                    body = "Look for names, phone numbers, categories, keywords",
                )
                SearchState.Offline -> SearchStateView(
                    art = EqualAssets.IllustrationNoInternet,
                    size = 130.dp,
                    title = "No internet connection",
                    body = "We could not connect to a stable internet. Check your connection and try again.",
                )
                SearchState.NoResults -> SearchStateView(
                    art = EqualAssets.IllustrationNoResults,
                    size = 150.dp,
                    title = "No results found",
                    body = "Couldn’t find anything matching. Try a different name, number, category or keyword",
                )
                SearchState.Loading, SearchState.Results, SearchState.MessagesLoading, SearchState.Messages -> {
                    SearchSection(title = "Chats", onClear = if (withMessages) ({ q = "" }) else null)
                    if (s == SearchState.Loading) {
                        LoadingDots(label = "Searching chats")
                    } else {
                        shownChats.forEachIndexed { i, c ->
                            if (i > 0) EqualDivider()
                            ChatListItem(
                                name = c.name,
                                preview = c.preview,
                                date = c.date,
                                state = if (c.unread > 0) ChatListItemState.Unread else ChatListItemState.Read,
                                unreadCount = c.unread,
                                chips = c.chips,
                                category = c.category,
                                initials = c.initials,
                                onClick = { onOpenChat(c) },
                            )
                        }
                    }
                    if (withMessages) {
                        SearchSection(title = "Messages")
                        if (s == SearchState.MessagesLoading) {
                            LoadingDots(label = "Searching messages")
                        } else {
                            matchingMessages.forEachIndexed { i, m ->
                                if (i > 0) EqualDivider()
                                MessageResult(name = m.name, time = m.time, text = m.text, query = q)
                            }
                        }
                    }
                }
            }
        }
        if (keyboard) KeyboardSpace()
    }
}

// ---- Previews (every state, light + dark) -----------------------------------------------------

@Preview(name = "Light", widthDp = EqualFrameWidthDp, heightDp = 980, showBackground = true)
@Preview(
    name = "Dark",
    widthDp = EqualFrameWidthDp,
    heightDp = 980,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
)
private annotation class SearchPreviews

@FlowPreviews @Composable private fun SearchEmptyPreview() = EqualTheme { SearchScreen(state = SearchState.Empty, keyboard = true) }
@FlowPreviews @Composable private fun SearchOfflinePreview() = EqualTheme { SearchScreen(state = SearchState.Offline, keyboard = true) }
@FlowPreviews @Composable private fun SearchRecentPreview() = EqualTheme { SearchScreen(keyboard = true) }
@FlowPreviews @Composable private fun SearchNoResultsPreview() = EqualTheme { SearchScreen(state = SearchState.NoResults, query = "Xyz", keyboard = true) }
@FlowPreviews @Composable private fun SearchLoadingPreview() = EqualTheme { SearchScreen(state = SearchState.Loading, query = "S", keyboard = true) }
@FlowPreviews @Composable private fun SearchResultsPreview() = EqualTheme { SearchScreen(state = SearchState.Results, query = "S", keyboard = true) }
@SearchPreviews @Composable private fun SearchMessagesLoadingPreview() = EqualTheme { SearchScreen(state = SearchState.MessagesLoading, query = "S", keyboard = true) }
@SearchPreviews @Composable private fun SearchMessagesPreview() = EqualTheme { SearchScreen(state = SearchState.Messages, query = "Sa", keyboard = true) }
