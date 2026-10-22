// Compose code shown on the docs site (Personalisation, List actions). Each function body is
// extracted by scripts/build-snippets.mjs; keep the `// snippet: <doc id> / <title>` line above it.
@file:Suppress("unused")

package ai.equal.ds.snippets

import ai.equal.ds.components.AvatarStack
import ai.equal.ds.components.CallerContextCard
import ai.equal.ds.components.CallerContextState
import ai.equal.ds.components.ContextMenuItem
import ai.equal.ds.components.EqualSnackbar
import ai.equal.ds.components.InfoRow
import ai.equal.ds.components.InfoRows
import ai.equal.ds.components.KeepLoseList
import ai.equal.ds.components.NoticedCard
import ai.equal.ds.components.NoticedCoach
import ai.equal.ds.components.NoticedState
import ai.equal.ds.components.SelectionChatHeader
import ai.equal.ds.components.TraitOrbHero
import ai.equal.ds.icons.EqualSymbols
import androidx.compose.runtime.Composable

// snippet: personalisation / Equal Noticed
@Composable
internal fun NoticedSnippet() {
    NoticedCoach(onDismiss = { /* dismiss */ })
    NoticedCard(summary = listOf("You’re a Logistics Legend…"), state = NoticedState.Reveal, onReveal = { /* reveal */ })
    NoticedCard(summary = listOf("You’re a Logistics Legend…"), onShare = { /* share */ })
}

// snippet: personalisation / Caller context
@Composable
internal fun CallerContextSnippet() {
    CallerContextCard(name = "Amit Nanda", state = CallerContextState.Empty)
    CallerContextCard(name = "Amit Nanda", state = CallerContextState.Filled, summary = "Amit manages your flat in Koramangala…", onStop = { /* stop */ }, onDelete = { /* delete */ })
    CallerContextCard(name = "Amit Nanda", state = CallerContextState.Paused, summary = "Amit manages your flat in Koramangala…", onResume = { /* resume */ })
}

// snippet: personalisation / Hero, rows and lists
@Composable
internal fun PersonalisationPartsSnippet() {
    TraitOrbHero()
    InfoRows(listOf(InfoRow(EqualSymbols.autoFix, "It builds up over time", "It gets a little sharper with every call.")))
    KeepLoseList(stays = listOf("Everything it knows today"), goes = listOf("Anything that changes ahead"))
    AvatarStack(listOf("AS", "AM", "BC"))
}

// snippet: list-actions / Selection headers
@Composable
internal fun SelectionHeadersSnippet() {
    SelectionChatHeader(
        selectedCount = 1,
        allSelected = false,
        onSelectAllChange = {},
        onBackClick = {},
        onMarkReadClick = {},
        onDeleteClick = {},
        single = true,
        onMuteClick = { /* mute sheet */ },
        menuItems = listOf(ContextMenuItem("Add to favourite", EqualSymbols.favourite, { /* favourite */ })),
    )
    SelectionChatHeader(selectedCount = 3, allSelected = false, onSelectAllChange = {}, onBackClick = {}, onMarkReadClick = {}, onDeleteClick = {})
}

// snippet: list-actions / Toasts
@Composable
internal fun ListToastsSnippet() {
    EqualSnackbar(message = "Deleting Conversation", busy = true)
    EqualSnackbar(message = "Add to favourites", onClose = { /* dismiss */ })
}

