package ai.equal.ds.catalog

import ai.equal.ds.R
import ai.equal.ds.assets.EqualAssetImage
import ai.equal.ds.assets.EqualAssets
import ai.equal.ds.components.AlphabetIndex
import ai.equal.ds.components.CallNotification
import ai.equal.ds.components.CallNotificationState
import ai.equal.ds.components.CallQuestion
import ai.equal.ds.components.ChoiceCard
import ai.equal.ds.components.ContactCheckRow
import ai.equal.ds.components.ContactPick
import ai.equal.ds.components.ContactPickMark
import ai.equal.ds.components.DeviceScreen
import ai.equal.ds.components.EqualAppBar
import ai.equal.ds.components.EqualButton
import ai.equal.ds.components.EqualButtonSize
import ai.equal.ds.components.EqualFrameHeightDp
import ai.equal.ds.components.EqualFrameWidthDp
import ai.equal.ds.components.EqualText
import ai.equal.ds.components.InfoNote
import ai.equal.ds.components.PermissionSheet
import ai.equal.ds.components.SearchField
import ai.equal.ds.components.SetupDone
import ai.equal.ds.icons.EqualIcons
import ai.equal.ds.theme.EqualTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// Call Handling (Known Calls (new users) design): You → Your assistant → Call Handling,
// and the setup a new user is invited into from home. Two questions: which calls the assistant
// answers, then who always rings you. Copy is an experiment, so `copy` switches the wording.
// Port of web/src/screens/CallHandlingScreens.tsx.

/** Which calls the assistant answers. */
enum class CallMode {
    /** All calls, except the people the user picks (needs the contacts permission). */
    All,

    /** Only numbers not saved in the phone. */
    Unknown,
}

/** Steps of [CallHandlingScreen]: question → (permissions) → exclude → Perfect!, or → All Sorted!. */
enum class CallHandlingStep { Question, PermContacts, PermHistory, Exclude, Perfect, Sorted }

/** One option's copy on the question screen. */
@Immutable
data class CallChoiceCopy(val title: String, val description: String)

/**
 * The question-screen copy experiment. [Which] is the control; [Whose] tags All calls as
 * Suggested, says what each option means for your contacts and, without the contacts
 * permission, dims All calls under an [InfoNote].
 */
enum class CallHandlingCopy(
    val title: String,
    val subtitle: String,
    val all: CallChoiceCopy,
    val unknown: CallChoiceCopy,
    val note: String,
    val cta: String,
) {
    Which(
        title = "Who should Equal answer for?",
        subtitle = "You choose. Equal follows.",
        all = CallChoiceCopy("All calls except", "Equal answers all your calls, except the people you choose."),
        unknown = CallChoiceCopy("Unknown numbers only", "Equal only answers calls from numbers you don’t have saved."),
        note = "You can change this anytime.",
        cta = "Next",
    ),
    Whose(
        title = "Whose calls should I answer?",
        subtitle = "I’ll pick up when you can’t, and tell you what they wanted.",
        all = CallChoiceCopy("All calls except", "Unknown numbers, and people you know when you don’t pick up"),
        unknown = CallChoiceCopy("Only unknown numbers", "Numbers not saved in your phone. Your contacts always ring you."),
        note = "This isn’t permanent — you can change it anytime.",
        cta = "Proceed",
    ),
}

/** A contact on the pickers. */
@Immutable
data class PickContact(val id: String, val initials: String, val name: String, val phone: String)

/** People the assistant suggests excluding (most called first). */
val SuggestedContacts: List<PickContact> = listOf(
    PickContact("mom", "SU", "Mom", "+91 8986461540"),
    PickContact("pappa", "PA", "Pappa", "+91 8986461540"),
    PickContact("vijay", "VC", "Vijay Cousin", "+91 8986461540"),
    PickContact("arnav", "AR", "Arnav Raj", "+91 8986461540"),
)

/** The sample contact list. */
val AllContacts: List<PickContact> = listOf(
    PickContact("arnav", "AR", "Arnav Raj", "+91 8986461540"),
    PickContact("shubham", "SS", "Shubham Singh", "+91 8986461540"),
    PickContact("aditya", "AD", "Aditya Rao", "+91 8986461540"),
)

private val DefaultPicked = listOf("arnav", "shubham")

/**
 * Call Handling frame: `bg/primary`, a centred [EqualAppBar] with the status bar, a scrolling body,
 * the [footer] (padding 12 / 16 / 20, on [footerBackground]) and an [overlay] layer.
 */
@Composable
private fun CallScaffold(
    title: String,
    onBack: (() -> Unit)?,
    modifier: Modifier = Modifier,
    footer: (@Composable ColumnScope.() -> Unit)? = null,
    footerBackground: Color = Color.Transparent,
    overlay: @Composable BoxScope.() -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    Box(
        modifier
            .fillMaxSize()
            .background(EqualTheme.colors.bgPrimary),
    ) {
        Column(Modifier.fillMaxSize()) {
            EqualAppBar(title = title, onBackClick = onBack, statusBar = true)
            Column(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                content = content,
            )
            if (footer != null) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .background(footerBackground)
                        .navigationBarsPadding()
                        .padding(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 20.dp),
                    content = footer,
                )
            }
        }
        overlay()
    }
}

/** Footer note (`body1` `text/secondary`, centred) + the full-width button. */
@Composable
private fun ColumnScope.NoteAndButton(note: String?, cta: String, onClick: () -> Unit) {
    if (note != null) {
        EqualText(
            text = note,
            style = EqualTheme.type.body1,
            color = EqualTheme.colors.textSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
        )
    }
    EqualButton(text = cta, onClick = onClick, size = EqualButtonSize.Block)
}

/** "Suggested" / "All Contacts" label: `subheading` Regular, padding 16 / 16 / 4. */
@Composable
private fun ContactSection(title: String) {
    EqualText(
        text = title,
        style = EqualTheme.type.subheading.copy(fontWeight = FontWeight.W400),
        color = EqualTheme.colors.textPrimary,
        modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 4.dp),
    )
}

/** Horizontally scrolling [ContactPick]s, gap 8, padding 8 / 12 / 4. */
@Composable
private fun PickRow(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Row(
        modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(start = 12.dp, top = 8.dp, end = 12.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) { content() }
}

/** [ContactCheckRow]s with the [AlphabetIndex] down the right edge. */
@Composable
private fun CheckList(contacts: List<PickContact>, picked: List<String>, onToggle: (String) -> Unit, index: Boolean) {
    Box(Modifier.fillMaxWidth()) {
        Column(Modifier.fillMaxWidth()) {
            contacts.forEach { c ->
                ContactCheckRow(
                    initials = c.initials,
                    name = c.name,
                    phone = c.phone,
                    checked = c.id in picked,
                    onCheckedChange = { onToggle(c.id) },
                )
            }
        }
        if (index) AlphabetIndex(Modifier.align(Alignment.CenterEnd).padding(end = 2.dp))
    }
}

/**
 * Call Handling setup (You → Your assistant → Call Handling, or Start on the home takeover). It
 * runs: [CallHandlingStep.Question] → (All calls without the permission: contacts, then call
 * history sheets) → [CallHandlingStep.Exclude] (who always rings you) → [CallHandlingStep.Perfect];
 * Only unknown numbers → [CallHandlingStep.Sorted]. [step] pins a step (docs).
 *
 * Experiment knobs: [copy] (question wording), [mode] (pre-selected answer), [contactsAllowed]
 * (otherwise All calls asks for it, and [CallHandlingCopy.Whose] dims it under the blue note),
 * [picked] (pre-picked contact ids on the exclude step, see [AllContacts]).
 */
@Composable
fun CallHandlingScreen(
    modifier: Modifier = Modifier,
    step: CallHandlingStep? = null,
    copy: CallHandlingCopy = CallHandlingCopy.Which,
    mode: CallMode = CallMode.All,
    contactsAllowed: Boolean = true,
    picked: List<String> = DefaultPicked,
    onBack: (() -> Unit)? = null,
    onDone: () -> Unit = {},
) {
    var live by remember { mutableStateOf(CallHandlingStep.Question) }
    var chosen by remember(mode) { mutableStateOf(mode) }
    var allowed by remember(contactsAllowed) { mutableStateOf(contactsAllowed) }
    val selected = remember(picked) { mutableStateListOf<String>().apply { addAll(picked) } }
    val s = step ?: live
    val go: (CallHandlingStep) -> Unit = { if (step == null) live = it }
    val toggle: (String) -> Unit = { id -> if (id in selected) selected.remove(id) else selected.add(id) }

    when (s) {
        CallHandlingStep.Perfect, CallHandlingStep.Sorted -> {
            val names = AllContacts.filter { it.id in selected }.map { it.name.substringBefore(' ') }
            val body = if (s == CallHandlingStep.Perfect) {
                "Equal will handle the calls." + if (names.isNotEmpty()) {
                    " " + names.take(3).joinToString(", ") + (if (names.size > 3) " and ${names.size - 3} more" else "") + " will always ring you."
                } else {
                    ""
                }
            } else {
                "Equal will answer unknown numbers only. Your contacts will ring you, like always."
            }
            SetupDone(
                title = if (s == CallHandlingStep.Perfect) "Perfect!" else "You’re all set!",
                body = body,
                onDone = onDone,
                modifier = modifier,
                header = {
                    EqualAppBar(
                        title = "",
                        onBackClick = if (s == CallHandlingStep.Sorted) onBack ?: { go(CallHandlingStep.Question) } else null,
                        statusBar = true,
                        background = Color.Transparent,
                    )
                },
            )
        }

        CallHandlingStep.Exclude -> CallScaffold(
            title = "Call Handling",
            onBack = { go(CallHandlingStep.Question) },
            modifier = modifier,
            footerBackground = EqualTheme.colors.bgSurface,
            footer = {
                NoteAndButton(
                    note = "You can change this anytime.",
                    cta = if (selected.isNotEmpty()) "Select ${selected.size} contacts" else "Select contacts",
                    onClick = { go(CallHandlingStep.Perfect) },
                )
            },
        ) {
            CallQuestion(
                art = EqualAssets.CallHandlingExclude,
                lottie = R.raw.lottie_call_handling_contacts,
                title = "Who should still ring you?",
            )
            var query by remember { mutableStateOf("") }
            SearchField(
                value = query,
                onValueChange = { query = it },
                placeholder = "Search contacts...",
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp),
            )
            ContactSection("Suggested")
            PickRow {
                SuggestedContacts.forEach { c -> ContactPick(initials = c.initials, name = c.name, onClick = { toggle(c.id) }) }
            }
            ContactSection("All Contacts")
            CheckList(AllContacts, selected, toggle, index = true)
        }

        else -> {
            val sheet = s == CallHandlingStep.PermContacts || s == CallHandlingStep.PermHistory
            CallScaffold(
                title = "Call Handling",
                onBack = onBack ?: {},
                modifier = modifier,
                footer = {
                    NoteAndButton(
                        note = if (sheet) null else copy.note,
                        cta = copy.cta,
                        onClick = {
                            when {
                                chosen == CallMode.Unknown -> go(CallHandlingStep.Sorted)
                                allowed -> go(CallHandlingStep.Exclude)
                                else -> go(CallHandlingStep.PermContacts)
                            }
                        },
                    )
                },
                overlay = {
                    if (s == CallHandlingStep.PermContacts) {
                        PermissionSheet(
                            art = EqualAssets.PermissionContacts,
                            title = "Allow contacts permission",
                            body = "I need your contact list to know who is calling.",
                            cta = "Allow",
                            onAllow = {
                                allowed = true
                                go(CallHandlingStep.PermHistory)
                            },
                            onClose = { go(CallHandlingStep.Question) },
                        )
                    } else if (s == CallHandlingStep.PermHistory) {
                        PermissionSheet(
                            art = EqualAssets.PermissionCallHistory,
                            title = "Allow call logs permission",
                            body = "Your contact list can be long. Allow Equal AI to access your phone call logs to show the people you call the most, at the top.",
                            cta = "Allow",
                            onAllow = { go(CallHandlingStep.Exclude) },
                            onClose = { go(CallHandlingStep.Exclude) },
                            onScrim = { go(CallHandlingStep.Question) },
                        )
                    }
                },
            ) {
                CallQuestion(art = EqualAssets.CallHandlingAll, title = copy.title, subtitle = copy.subtitle)
                Column(
                    Modifier
                        .fillMaxWidth()
                        .selectableGroup()
                        .padding(start = 16.dp, top = 24.dp, end = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    val whose = copy == CallHandlingCopy.Whose
                    ChoiceCard(
                        icon = EqualIcons.Globe,
                        title = copy.all.title,
                        description = copy.all.description,
                        selected = chosen == CallMode.All,
                        onSelect = { chosen = CallMode.All },
                        suggested = whose,
                        enabled = !(whose && !allowed),
                    )
                    ChoiceCard(
                        icon = EqualIcons.Call,
                        title = copy.unknown.title,
                        description = copy.unknown.description,
                        selected = chosen == CallMode.Unknown,
                        onSelect = { chosen = CallMode.Unknown },
                    )
                    if (whose && !allowed) {
                        InfoNote(
                            title = "Contact access needed",
                            body = "Without it, the assistant cannot handle all calls.",
                            link = "Give contacts permission",
                            onLink = { go(CallHandlingStep.PermContacts) },
                        )
                    }
                }
            }
        }
    }
}

/**
 * Always ring me (edit, from the You tab): "I will never answer these people.", search, the
 * chosen people (× to remove), suggested and all contacts with checkboxes and the A–Z index. The
 * button counts who will ring you directly.
 */
@Composable
fun AlwaysRingScreen(
    modifier: Modifier = Modifier,
    initial: List<String> = DefaultPicked,
    onBack: () -> Unit = {},
) {
    val picked = remember(initial) { mutableStateListOf<String>().apply { addAll(initial) } }
    val chosen = SuggestedContacts.take(3)
    val toggle: (String) -> Unit = { id -> if (id in picked) picked.remove(id) else picked.add(id) }
    val count = picked.size + chosen.size + 3
    CallScaffold(
        title = "Always ring me",
        onBack = onBack,
        modifier = modifier,
        footerBackground = EqualTheme.colors.bgSurface,
        footer = { NoteAndButton(note = "You can change this anytime.", cta = "$count people will ring you directly", onClick = onBack) },
    ) {
        EqualText(
            text = "I will never answer these people.",
            style = EqualTheme.type.title.copy(fontWeight = FontWeight.W400),
            color = EqualTheme.colors.textSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        var query by remember { mutableStateOf("") }
        SearchField(
            value = query,
            onValueChange = { query = it },
            placeholder = "Search contacts...",
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp),
        )
        PickRow(Modifier.padding(top = 8.dp)) {
            chosen.forEach { c -> ContactPick(initials = c.initials, name = c.name, onClick = {}, mark = ContactPickMark.Remove) }
        }
        ContactSection("Suggested")
        CheckList(AllContacts.take(3), picked, toggle, index = false)
        ContactSection("All Contacts")
        CheckList(AllContacts, picked, toggle, index = true)
    }
}

/** Direct Calls with no saved contacts: the 140dp illustration, why it's empty, and Got it. */
@Composable
fun DirectCallsEmptyScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onDone: () -> Unit = {},
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    CallScaffold(
        title = "Direct Calls",
        onBack = onBack,
        modifier = modifier,
        footerBackground = colors.bgSurface,
        footer = { NoteAndButton(note = "Find this later under You, then Call Handling.", cta = "Got it", onClick = onDone) },
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(start = 28.dp, top = 120.dp, end = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            EqualAssetImage(EqualAssets.IllustrationNoContacts, size = 140.dp)
            EqualText(
                text = "No contacts saved",
                style = type.headline.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 10.dp),
            )
            EqualText(
                text = "Save numbers in your phone first. Then tell me who should always ring you.",
                style = type.title.copy(fontWeight = FontWeight.W400),
                color = colors.textSecondary,
                textAlign = TextAlign.Center,
            )
        }
    }
}

/**
 * Known-call notification (Known-call notifications design) on the coded [DeviceScreen], for
 * docs. On device the system draws the shade; see [CallNotification].
 */
@Composable
fun KnownCallNotificationScreen(state: CallNotificationState, modifier: Modifier = Modifier) {
    DeviceScreen(modifier) {
        CallNotification(name = "Mom", state = state)
    }
}

// ---------------------------------------------------------------------------------------------
// Previews
// ---------------------------------------------------------------------------------------------

@Preview(name = "Call Handling · question · light", widthDp = EqualFrameWidthDp, heightDp = EqualFrameHeightDp, showBackground = true)
@Composable
private fun CallHandlingQuestionLightPreview() {
    EqualTheme(darkTheme = false) { CallHandlingScreen() }
}

@Preview(name = "Call Handling · whose, no permission · dark", widthDp = EqualFrameWidthDp, heightDp = EqualFrameHeightDp, showBackground = true)
@Composable
private fun CallHandlingWhoseDarkPreview() {
    EqualTheme(darkTheme = true) { CallHandlingScreen(copy = CallHandlingCopy.Whose, mode = CallMode.Unknown, contactsAllowed = false) }
}

@Preview(name = "Call Handling · contacts sheet · dark", widthDp = EqualFrameWidthDp, heightDp = EqualFrameHeightDp, showBackground = true)
@Composable
private fun CallHandlingPermDarkPreview() {
    EqualTheme(darkTheme = true) { CallHandlingScreen(copy = CallHandlingCopy.Whose, step = CallHandlingStep.PermContacts, contactsAllowed = false) }
}

@Preview(name = "Call Handling · exclude · light", widthDp = EqualFrameWidthDp, heightDp = 1060, showBackground = true)
@Composable
private fun CallHandlingExcludeLightPreview() {
    EqualTheme(darkTheme = false) { CallHandlingScreen(step = CallHandlingStep.Exclude) }
}

@Preview(name = "Call Handling · exclude · dark", widthDp = EqualFrameWidthDp, heightDp = 1060, showBackground = true)
@Composable
private fun CallHandlingExcludeDarkPreview() {
    EqualTheme(darkTheme = true) { CallHandlingScreen(step = CallHandlingStep.Exclude) }
}

@Preview(name = "Call Handling · perfect · light", widthDp = EqualFrameWidthDp, heightDp = EqualFrameHeightDp, showBackground = true)
@Composable
private fun CallHandlingPerfectLightPreview() {
    EqualTheme(darkTheme = false) { CallHandlingScreen(step = CallHandlingStep.Perfect) }
}

@Preview(name = "Call Handling · sorted · dark", widthDp = EqualFrameWidthDp, heightDp = EqualFrameHeightDp, showBackground = true)
@Composable
private fun CallHandlingSortedDarkPreview() {
    EqualTheme(darkTheme = true) { CallHandlingScreen(step = CallHandlingStep.Sorted, mode = CallMode.Unknown) }
}

@Preview(name = "Always ring me · light", widthDp = EqualFrameWidthDp, heightDp = 1180, showBackground = true)
@Composable
private fun AlwaysRingLightPreview() {
    EqualTheme(darkTheme = false) { AlwaysRingScreen() }
}

@Preview(name = "Always ring me · dark", widthDp = EqualFrameWidthDp, heightDp = 1180, showBackground = true)
@Composable
private fun AlwaysRingDarkPreview() {
    EqualTheme(darkTheme = true) { AlwaysRingScreen() }
}

@Preview(name = "Direct Calls · light", widthDp = EqualFrameWidthDp, heightDp = EqualFrameHeightDp, showBackground = true)
@Composable
private fun DirectCallsLightPreview() {
    EqualTheme(darkTheme = false) { DirectCallsEmptyScreen() }
}

@Preview(name = "Direct Calls · dark", widthDp = EqualFrameWidthDp, heightDp = EqualFrameHeightDp, showBackground = true)
@Composable
private fun DirectCallsDarkPreview() {
    EqualTheme(darkTheme = true) { DirectCallsEmptyScreen() }
}

@Preview(name = "Known call · expanded · light", widthDp = EqualFrameWidthDp, heightDp = EqualFrameHeightDp, showBackground = true)
@Composable
private fun KnownCallLightPreview() {
    EqualTheme(darkTheme = false) { KnownCallNotificationScreen(CallNotificationState.Expanded) }
}

@Preview(name = "Known call · connecting · dark", widthDp = EqualFrameWidthDp, heightDp = EqualFrameHeightDp, showBackground = true)
@Composable
private fun KnownCallDarkPreview() {
    EqualTheme(darkTheme = true) { KnownCallNotificationScreen(CallNotificationState.Connecting) }
}
