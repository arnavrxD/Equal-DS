package ai.equal.ds.catalog

import ai.equal.ds.assets.EqualAssets
import ai.equal.ds.components.ChatListItem
import ai.equal.ds.components.ChatListItemState
import ai.equal.ds.components.ConfirmSheetContent
import ai.equal.ds.components.ContextMenuItem
import ai.equal.ds.components.EqualBottomSheet
import ai.equal.ds.components.EqualDivider
import ai.equal.ds.components.EqualSnackbar
import ai.equal.ds.components.EqualTab
import ai.equal.ds.components.BottomNavBar
import ai.equal.ds.components.HomeHeader
import ai.equal.ds.components.SelectionChatHeader
import ai.equal.ds.components.StatusBar
import ai.equal.ds.icons.EqualSymbols
import ai.equal.ds.theme.EqualTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/** Chat-list action stages (web `ChatListActionsStage`). */
enum class ChatListActionsStage { List, One, Many, Delete, Deleting, Mute, Unmute, Muted, Favourite }

/**
 * Chats with selection (Updated UI): long-press → one selected (mark read · mute · delete · more menu);
 * more rows → the multi-select header. Delete and mute confirm in a sheet; deleting and favourites
 * show a toast; a muted row shows the bell-off glyph. Port of web `ChatListActions`.
 */
@Composable
fun ChatListActionsSample(modifier: Modifier = Modifier, stage: ChatListActionsStage = ChatListActionsStage.List) {
    val colors = EqualTheme.colors
    val chatsAll = HomeSampleChats
    var s by remember(stage) { mutableStateOf(stage) }
    var selected by remember(stage) {
        mutableStateOf(
            when (stage) {
                ChatListActionsStage.List, ChatListActionsStage.Muted, ChatListActionsStage.Deleting, ChatListActionsStage.Favourite -> emptyList()
                ChatListActionsStage.Many -> chatsAll.take(3).map { it.id }
                else -> listOf(chatsAll[2].id)
            },
        )
    }
    var muted by remember(stage) { mutableStateOf(if (stage == ChatListActionsStage.Muted || stage == ChatListActionsStage.Unmute) listOf(chatsAll[2].id) else emptyList()) }
    var gone by remember { mutableStateOf(emptyList<String>()) }
    LaunchedEffect(s) {
        if ((s == ChatListActionsStage.Deleting || s == ChatListActionsStage.Favourite) && s != stage) { delay(2000); s = ChatListActionsStage.List }
    }
    val chats = chatsAll.filter { it.id !in gone }
    val target = chats.firstOrNull { it.id == selected.firstOrNull() } ?: chatsAll[2]
    val clear = { selected = emptyList(); s = ChatListActionsStage.List }
    val toggle = { id: String ->
        selected = if (id in selected) selected - id else selected + id
        s = when (selected.size) { 0 -> ChatListActionsStage.List; 1 -> ChatListActionsStage.One; else -> ChatListActionsStage.Many }
    }
    val selecting = selected.isNotEmpty()
    Box(modifier.fillMaxSize().background(colors.bgPrimary)) {
        Column(Modifier.fillMaxSize()) {
            when {
                !selecting -> HomeHeader(assistantOn = true, onAssistantChange = {})
                selected.size == 1 -> {
                    StatusBar(background = colors.bgSurface)
                    SelectionChatHeader(
                        selectedCount = 1, allSelected = false, onSelectAllChange = {}, onBackClick = clear, onMarkReadClick = clear,
                        onDeleteClick = { s = ChatListActionsStage.Delete },
                        single = true,
                        muted = target.id in muted,
                        onMuteClick = { s = if (target.id in muted) ChatListActionsStage.Unmute else ChatListActionsStage.Mute },
                        menuItems = listOf(
                            ContextMenuItem("Mark as read", EqualSymbols.markChatRead, clear),
                            ContextMenuItem("Add to favourite", EqualSymbols.favourite, { selected = emptyList(); s = ChatListActionsStage.Favourite }),
                            ContextMenuItem("Share contact", EqualSymbols.share, clear),
                        ),
                    )
                }
                else -> {
                    StatusBar(background = colors.bgSurface)
                    SelectionChatHeader(
                        selectedCount = selected.size, allSelected = selected.size == chats.size,
                        onSelectAllChange = { all -> selected = if (all) chats.map { it.id } else emptyList() },
                        onBackClick = clear, onMarkReadClick = clear, onDeleteClick = { s = ChatListActionsStage.Delete },
                    )
                }
            }
            Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(top = 8.dp, bottom = 120.dp)) {
                chats.forEachIndexed { i, c ->
                    if (i > 0) EqualDivider()
                    ChatListItem(
                        name = c.name, preview = c.preview, date = c.date,
                        state = when { c.id in selected -> ChatListItemState.Selected; c.unread > 0 -> ChatListItemState.Unread; else -> ChatListItemState.Read },
                        unreadCount = c.unread, chips = c.chips, category = c.category, initials = c.initials,
                        muted = c.id in muted,
                        onClick = { if (selecting) toggle(c.id) },
                        onLongClick = { toggle(c.id) },
                    )
                }
            }
        }
        if (!selecting) {
            Column(Modifier.align(Alignment.BottomCenter).navigationBarsPadding()) {
                when (s) {
                    ChatListActionsStage.Deleting -> EqualSnackbar(message = "Deleting Conversation", busy = true, modifier = Modifier.padding(horizontal = 16.dp))
                    ChatListActionsStage.Favourite -> EqualSnackbar(message = "Add to favourites", onClose = { s = ChatListActionsStage.List }, modifier = Modifier.padding(horizontal = 16.dp))
                    else -> Unit
                }
                BottomNavBar(selectedTab = EqualTab.Chats, onTabSelected = {}, onFabClick = {}, chatsBadgeCount = 2, modifier = Modifier.fillMaxWidth())
            }
        }
        EqualBottomSheet(visible = s == ChatListActionsStage.Delete, onDismissRequest = { s = if (selected.size > 1) ChatListActionsStage.Many else ChatListActionsStage.One }, paneTitle = "Delete chat") {
            ConfirmSheetContent(
                title = if (selected.size > 1) "Delete ${selected.size} chats" else "Delete chat with ${target.name}",
                body = "This removes the conversation and its call summaries from this phone.",
                confirmLabel = "Delete",
                onConfirm = { gone = gone + selected; selected = emptyList(); s = ChatListActionsStage.Deleting },
            )
        }
        EqualBottomSheet(visible = s == ChatListActionsStage.Mute || s == ChatListActionsStage.Unmute, onDismissRequest = { s = ChatListActionsStage.One }, paneTitle = if (s == ChatListActionsStage.Mute) "Mute ${target.name}" else "Unmute ${target.name}") {
            val muting = s == ChatListActionsStage.Mute
            ConfirmSheetContent(
                title = "${if (muting) "Mute" else "Unmute"} ${target.name}",
                body = if (muting) "Their calls won’t ring. Your assistant will still answer and keep you posted." else "Are you sure? You’ll start receiving calls from them again",
                confirmLabel = if (muting) "Mute" else "Unmute",
                illustration = EqualAssets.MascotSleeping,
                destructive = false,
                onConfirm = { muted = if (muting) muted + target.id else muted - target.id; clear() },
            )
        }
    }
}
