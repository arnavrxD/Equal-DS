package ai.equal.ds.catalog

import ai.equal.ds.components.EqualFrameHeightDp
import ai.equal.ds.components.EqualFrameWidthDp
import ai.equal.ds.assets.EqualAssetImage
import ai.equal.ds.assets.EqualAssets
import ai.equal.ds.components.MeetAssistantCard
import ai.equal.ds.components.PlanTier
import ai.equal.ds.components.SectionHeaderSize
import ai.equal.ds.components.CallHistoryList
import ai.equal.ds.components.SystemNotificationPreview
import ai.equal.ds.components.SystemStatusState
import androidx.compose.ui.zIndex
import ai.equal.ds.components.defaultContactInfoActions
import ai.equal.ds.components.ContactInfoHeader
import ai.equal.ds.components.CallHistoryRow
import ai.equal.ds.components.CallDirection
import ai.equal.ds.assets.EqualCategory
import ai.equal.ds.components.AddressCard
import ai.equal.ds.components.AddressForm
import ai.equal.ds.components.ChatBubble
import ai.equal.ds.components.ChatBubbleType
import ai.equal.ds.components.ConfirmSheetContent
import ai.equal.ds.components.EmptyState
import ai.equal.ds.components.EqualAppBar
import ai.equal.ds.components.EqualBottomSheet
import ai.equal.ds.components.EqualButton
import ai.equal.ds.components.EqualButtonSize
import ai.equal.ds.components.EqualSnackbar
import ai.equal.ds.components.EqualText
import ai.equal.ds.components.EqualTextButton
import ai.equal.ds.components.EqualTextField
import ai.equal.ds.components.FullSheet
import ai.equal.ds.components.Gender
import ai.equal.ds.components.GenderSelector
import ai.equal.ds.components.IllustrationHeader
import ai.equal.ds.components.Instruction
import ai.equal.ds.components.InstructionCard
import ai.equal.ds.components.InstructionPreview
import ai.equal.ds.components.InstructionSource
import ai.equal.ds.components.LargeActionChip
import ai.equal.ds.components.LargeActionChipState
import ai.equal.ds.components.PresetCard
import ai.equal.ds.components.ActiveInstructionsRow
import ai.equal.ds.components.PresetInstructionCard
import ai.equal.ds.components.RecommendedCard
import ai.equal.ds.components.RowGroup
import ai.equal.ds.components.SavedAddress
import ai.equal.ds.components.SectionHeader
import ai.equal.ds.components.SettingsRow
import ai.equal.ds.components.SuggestionsComingCard
import ai.equal.ds.components.ToggleRow
import ai.equal.ds.components.VoiceInputBar
import ai.equal.ds.components.formatAddress
import ai.equal.ds.icons.EqualIcons
import ai.equal.ds.theme.EqualTheme
import ai.equal.ds.tokens.EqualMotion
import android.content.res.Configuration
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Flow screens: Profile (Personal Details, Silent Mode, WhatsApp Updates), Addresses, Instructions.
// Composed only from DS components. Port of web/src/screens/FlowScreens.tsx (source: the flow designs).

// =============================================================================================
// Scaffold
// =============================================================================================

/**
 * Screen frame used by the flows: `bg/primary`, a centred [EqualAppBar] with the status bar, a
 * scrolling body, an optional [footer] (e.g. the Add button, 16dp padding + nav inset) and an
 * [overlay] layer (sheets, toasts) drawn on top of everything.
 */
@Composable
private fun FlowScaffold(
    title: String,
    onBack: (() -> Unit)?,
    modifier: Modifier = Modifier,
    trailing: @Composable RowScope.() -> Unit = {},
    footer: (@Composable () -> Unit)? = null,
    overlay: @Composable BoxScope.() -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    Box(
        modifier
            .fillMaxSize()
            .background(EqualTheme.colors.bgPrimary),
    ) {
        Column(Modifier.fillMaxSize()) {
            EqualAppBar(title = title, onBackClick = onBack, statusBar = true, actions = trailing)
            Column(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                content = content,
            )
            if (footer != null) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(16.dp),
                ) { footer() }
            }
        }
        overlay()
    }
}

// =============================================================================================
// Profile flow
// =============================================================================================

/**
 * Personal Details: Name field (12dp corners) with the helper "Your AI will call you ‘First’" (the first
 * name in Medium `text/primary`), then GENDER and the [GenderSelector].
 */
@Composable
fun PersonalDetailsScreen(
    modifier: Modifier = Modifier,
    initialName: String = "Keshav Reddy",
    initialGender: Gender = Gender.Male,
    onBack: (() -> Unit)? = {},
    onChange: (name: String, gender: Gender) -> Unit = { _, _ -> },
) {
    val colors = EqualTheme.colors
    var name by remember { mutableStateOf(initialName) }
    var gender by remember { mutableStateOf(initialGender) }
    val first = name.trim().split(Regex("\\s+")).firstOrNull()?.takeIf { it.isNotEmpty() } ?: "you"
    FlowScaffold(title = "Personal Details", onBack = onBack, modifier = modifier) {
        Column(
            Modifier.padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(28.dp),
        ) {
            EqualTextField(
                value = name,
                onValueChange = {
                    name = it
                    onChange(it, gender)
                },
                label = "Name",
                helper = buildAnnotatedString {
                    append("Your AI will call you ")
                    withStyle(SpanStyle(color = colors.textPrimary, fontWeight = FontWeight.W500)) {
                        append("‘$first’")
                    }
                },
            )
            Column {
                EqualText(text = "GENDER", style = EqualTheme.type.overline, color = colors.textSecondary)
                Spacer(Modifier.height(10.dp))
                GenderSelector(
                    value = gender,
                    tier = PlanTier.Paid, // the sample user is on Unlimited
                    onValueChange = {
                        gender = it
                        onChange(name, it)
                    },
                )
            }
        }
    }
}

/**
 * Silent Mode: illustration, "Mute Promotional Calls" master toggle, CATEGORIES (dimmed to 45% and
 * disabled while the master is off), and Muted Contacts.
 */
@Composable
fun SilentModeScreen(
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = {},
    title: String = "Silent Mode",
    initialMuteAll: Boolean = true,
) {
    var muteAll by remember { mutableStateOf(initialMuteAll) }
    val cats = remember { mutableStateMapOf<String, Boolean>().apply { SilentCategories.forEach { put(it.id, true) } } }
    val groupAlpha by animateFloatAsState(if (muteAll) 1f else 0.45f, tween(EqualMotion.base), label = "silentAlpha")
    FlowScaffold(title = title, onBack = onBack, modifier = modifier) {
        IllustrationHeader(asset = EqualAssets.IllustrationSilentMode)
        RowGroup {
            ToggleRow(
                label = "Mute Promotional Calls",
                checked = muteAll,
                onCheckedChange = { muteAll = it },
                description = "Equal AI Assistant can identify Promotional Calls and automatically mute them",
                descriptionWraps = true,
            )
        }
        SectionHeader(title = "Categories")
        RowGroup(Modifier.alpha(groupAlpha)) {
            SilentCategories.forEach { c ->
                ToggleRow(
                    label = c.label,
                    checked = muteAll && (cats[c.id] ?: true),
                    onCheckedChange = { cats[c.id] = it },
                    description = c.description,
                    enabled = muteAll,
                )
            }
        }
        Spacer(Modifier.height(20.dp))
        RowGroup {
            SettingsRow(
                label = "Muted Contacts",
                description = "People you’ve muted",
                leadingIcon = EqualIcons.SilentMode,
                onClick = {},
            )
        }
        Spacer(Modifier.height(24.dp))
    }
}

/**
 * WhatsApp Updates: illustration + "Get updates on WhatsApp" toggle. Switching it off opens the
 * turn-off confirm sheet; confirming turns it off, dismissing keeps it on.
 */
@Composable
fun WhatsAppUpdatesScreen(
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = {},
    initialOn: Boolean = true,
    showSheet: Boolean = false,
) {
    var on by remember { mutableStateOf(initialOn) }
    var confirm by remember { mutableStateOf(showSheet) }
    FlowScaffold(
        title = "WhatsApp Updates",
        onBack = onBack,
        modifier = modifier,
        overlay = {
            EqualBottomSheet(visible = confirm, onDismissRequest = { confirm = false }) {
                ConfirmSheetContent(
                    illustration = EqualAssets.IllustrationWhatsapp,
                    title = "Turn off WhatsApp updates?",
                    body = "Important updates and alerts will no longer reach you on WhatsApp",
                    confirmLabel = "Turn off updates",
                    destructive = false,
                    onConfirm = {
                        on = false
                        confirm = false
                    },
                )
            }
        },
    ) {
        IllustrationHeader(asset = EqualAssets.IllustrationWhatsapp)
        RowGroup {
            ToggleRow(
                label = "Get updates on WhatsApp",
                checked = on,
                onCheckedChange = { v -> if (v) on = true else confirm = true },
                // Web renders the second paragraph 12dp below; a blank line approximates it.
                description = "We’ll share call alerts, account updates, and helpful tips right when you need them.\n\nNo spam. Only what matters.",
                descriptionWraps = true,
            )
        }
    }
}

// =============================================================================================
// Contact Info
// =============================================================================================

/** One call in the Contact Info history. */
data class CallRecord(val direction: CallDirection, val meta: String, val time: String)

private val SampleCallHistory = listOf(
    CallRecord(CallDirection.Incoming, "Today \u2022 30 Sec.", "02:30 PM"),
    CallRecord(CallDirection.Missed, "Yesterday \u2022 0 Sec.", "02:30 PM"),
    CallRecord(CallDirection.Incoming, "Today \u2022 30 Sec.", "02:30 PM"),
    CallRecord(CallDirection.Incoming, "Today \u2022 30 Sec.", "02:30 PM"),
)

/**
 * Contact Info, matching web `ContactInfoScreen`: centred app bar, [ContactInfoHeader] (96dp
 * avatar, Call / Mute↔Unmute / Save / Share tiles), then CALL HISTORY rows in a [RowGroup].
 */
@Composable
fun ContactInfoScreen(
    modifier: Modifier = Modifier,
    name: String = "Amit Nanda",
    phone: String = "+91 9871746113",
    category: EqualCategory? = EqualCategory.Career,
    initials: String? = null,
    history: List<CallRecord> = SampleCallHistory,
    onBack: (() -> Unit)? = {},
) {
    var muted by remember { mutableStateOf(false) }
    FlowScaffold(title = "Contact Info", onBack = onBack, modifier = modifier) {
        ContactInfoHeader(
            name = name,
            phone = phone,
            category = category,
            initials = initials,
            actions = defaultContactInfoActions(
                onCall = {},
                onMute = { muted = !muted },
                onSave = {},
                onShare = {},
                muted = muted,
            ),
        )
        SectionHeader(title = "Call history", inset = 22.dp, size = SectionHeaderSize.Label)
        CallHistoryList {
            history.forEach { CallHistoryRow(direction = it.direction, meta = it.meta, time = it.time) }
        }
        Spacer(Modifier.height(24.dp))
    }
}

// =============================================================================================
// Addresses flow
// =============================================================================================

private const val NewAddress = "new"

/**
 * Addresses: saved address cards (Edit / Delete address), an "Add new Address" footer, the
 * add / edit form in a titled sheet and the delete confirmation. Empty list → empty state.
 * [openEdit] (an id or "new") and [confirmDelete] open a sheet initially, for previews.
 */
@Composable
fun AddressesScreen(
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = {},
    initial: List<SavedAddress> = SampleAddresses,
    openEdit: String? = null,
    confirmDelete: String? = null,
) {
    val list = remember { mutableStateListOf<SavedAddress>().apply { addAll(initial) } }
    var editing by remember { mutableStateOf(openEdit) }
    var deleting by remember { mutableStateOf(confirmDelete) }
    val editingId = editing
    val current = if (editingId != null && editingId != NewAddress) list.firstOrNull { it.id == editingId } else null
    FlowScaffold(
        title = "Addresses",
        onBack = onBack,
        modifier = modifier,
        footer = {
            EqualButton(
                text = "Add new Address",
                onClick = { editing = NewAddress },
                size = EqualButtonSize.Block,
                leadingIcon = EqualIcons.Add,
            )
        },
        overlay = {
            EqualBottomSheet(
                visible = editingId != null,
                onDismissRequest = { editing = null },
                title = if (editingId == NewAddress) "Add Address" else "Edit Address",
            ) {
                if (editingId != null) {
                    key(editingId) {
                        AddressForm(
                            initial = current,
                            onSave = { a ->
                                if (editingId == NewAddress) {
                                    list.add(a.copy(id = "a${System.currentTimeMillis()}"))
                                } else {
                                    val i = list.indexOfFirst { it.id == editingId }
                                    if (i >= 0) list[i] = a.copy(id = editingId)
                                }
                                editing = null
                            },
                        )
                    }
                }
            }
            EqualBottomSheet(visible = deleting != null, onDismissRequest = { deleting = null }) {
                ConfirmSheetContent(
                    title = "Delete Address",
                    body = "Are you sure you want to delete this address? This action cannot be undone.",
                    confirmLabel = "Delete",
                    destructive = true,
                    onConfirm = {
                        val id = deleting
                        list.removeAll { it.id == id }
                        deleting = null
                    },
                )
            }
        },
    ) {
        if (list.isEmpty()) {
            Spacer(Modifier.height(72.dp))
            EmptyState(
                title = "Never explain your address again",
                body = "Save frequently used addresses, and your assistant will use them to handle delivery, pickup calls, etc. more naturally.",
                illustration = { EqualAssetImage(EqualAssets.IllustrationAddressesEmpty, 96.dp) },
            )
        } else {
            Column(
                Modifier.padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                list.forEach { a ->
                    AddressCard(
                        label = a.label,
                        address = formatAddress(a),
                        onEdit = { editing = a.id },
                        onDelete = { deleting = a.id },
                    )
                }
            }
        }
    }
}

// =============================================================================================
// Instructions flow: setup chat
// =============================================================================================

/** One message in the setup chat. */
sealed interface SetupMessage {
    data class You(val text: String) : SetupMessage
    data class Ai(val text: String) : SetupMessage
    data class Preview(val instruction: Instruction, val confirmed: Boolean = false) : SetupMessage
}

/** What the setup chat is setting up: the instruction, the opening message, and whether it asks follow-ups. */
data class InstructionSetup(val instruction: Instruction, val ask: String, val needsInput: Boolean)

private const val GotIt = "Got it! Here’s your instruction:"
private const val ChatTime = "03:21"

private fun openingMessages(setup: InstructionSetup?): List<SetupMessage> {
    if (setup == null) return emptyList()
    val first = SetupMessage.You(setup.ask)
    val questions = FollowUps[setup.instruction.id].orEmpty()
    return if (setup.needsInput && questions.isNotEmpty()) {
        listOf(first, SetupMessage.Ai(questions[0].question))
    } else {
        listOf(first, SetupMessage.Ai(GotIt), SetupMessage.Preview(setup.instruction))
    }
}

/**
 * "Setting up instruction" chat in a [FullSheet], ported from web `InstructionSetupSheet`.
 * - No-input instructions go straight to the preview with Edit (read) / Confirm chips.
 * - Instructions with follow-ups ask each question first (the composer placeholder becomes
 *   "Type your answer…"); the preview bolds the answers and offers Change something else / That's all.
 * - Editing asks "What would you like to update in this?" and the next message replaces the Do line.
 * - Confirming adds a ✅ to the last preview and calls [onDone] after 700ms.
 * State resets whenever [setup] or [visible] changes.
 */
@Composable
fun InstructionSetupSheet(
    setup: InstructionSetup?,
    visible: Boolean,
    onDismissRequest: () -> Unit,
    onDone: (Instruction) -> Unit,
) {
    val msgs = remember(setup, visible) { mutableStateListOf<SetupMessage>().apply { addAll(openingMessages(setup)) } }
    val answers = remember(setup, visible) { mutableStateMapOf<String, String>() }
    var step by remember(setup, visible) { mutableIntStateOf(0) }
    var editing by remember(setup, visible) { mutableStateOf(false) }
    var input by remember(setup, visible) { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val scroll = rememberScrollState()
    val questions = setup?.let { FollowUps[it.instruction.id] }.orEmpty()
    val asking = setup?.needsInput == true && step < questions.size

    LaunchedEffect(msgs.size) { scroll.animateScrollTo(scroll.maxValue) }

    fun send(text: String) {
        if (setup == null || text.isBlank()) return
        if (asking) {
            val q = questions[step]
            answers[q.key] = text
            msgs.add(SetupMessage.You(text))
            if (step + 1 < questions.size) {
                msgs.add(SetupMessage.Ai(questions[step + 1].question))
            } else {
                msgs.add(SetupMessage.Ai(GotIt))
                msgs.add(SetupMessage.Preview(setup.instruction))
            }
            step += 1
            return
        }
        // Free-text edit of the instruction.
        msgs.add(SetupMessage.You(text))
        msgs.add(SetupMessage.Ai(GotIt))
        msgs.add(SetupMessage.Preview(setup.instruction.copy(doText = text)))
        editing = false
    }

    fun confirm() {
        if (setup == null) return
        val lastIndex = msgs.indexOfLast { it is SetupMessage.Preview }
        val last = msgs.getOrNull(lastIndex) as? SetupMessage.Preview
        if (last != null) msgs[lastIndex] = last.copy(confirmed = true)
        msgs.add(SetupMessage.You(if (setup.needsInput) "That’s all" else "Confirm"))
        val base = last?.instruction ?: setup.instruction
        val done = base.copy(doText = plainTemplate(base.doText, answers))
        scope.launch {
            delay(700)
            onDone(done)
        }
    }

    fun askEdit() {
        editing = true
        msgs.add(SetupMessage.You(if (setup?.needsInput == true) "Change something else" else "Edit"))
        msgs.add(SetupMessage.Ai("What would you like to update in this?"))
    }

    val showChips = setup != null && !asking && !editing
    FullSheet(
        visible = visible,
        onDismissRequest = onDismissRequest,
        title = "Setting up instruction",
        scrollState = scroll,
        footer = {
            if (showChips) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    if (setup?.needsInput == true) {
                        LargeActionChip("Change something else", "✏️", onClick = ::askEdit, state = LargeActionChipState.Read)
                        LargeActionChip("That's all", "👌", onClick = ::confirm)
                    } else {
                        LargeActionChip("Edit", "✍️", onClick = ::askEdit, state = LargeActionChipState.Read)
                        LargeActionChip("Confirm", "👍", onClick = ::confirm)
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
            VoiceInputBar(
                value = input,
                onValueChange = { input = it },
                onSend = {
                    send(input)
                    input = ""
                },
                placeholder = if (asking) "Type your answer…" else "Type a message...",
            )
        },
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            msgs.forEach { m ->
                when (m) {
                    is SetupMessage.You -> ChatBubble(message = m.text, timestamp = ChatTime, type = ChatBubbleType.Outgoing, sender = "You")
                    is SetupMessage.Ai -> ChatBubble(message = m.text, timestamp = ChatTime, type = ChatBubbleType.Assistant, sender = "Equal AI")
                    is SetupMessage.Preview -> InstructionPreview(
                        title = m.instruction.title,
                        whenText = AnnotatedString(m.instruction.whenText),
                        action = fillTemplate(m.instruction.doText, answers),
                        time = ChatTime,
                        confirmed = m.confirmed,
                        modifier = Modifier.padding(start = 36.dp, bottom = if (m.confirmed) 16.dp else 0.dp),
                    )
                }
            }
        }
    }
}

// =============================================================================================
// Instructions flow: hub / preset / active
// =============================================================================================

/** Which page of the Instructions flow is showing. */
sealed interface InstructionsView {
    data object Hub : InstructionsView
    data object Active : InstructionsView
    data class PresetDetail(val presetId: String) : InstructionsView
}

private val DefaultActive = listOf(
    InstructionLibrary.declineSales,
    InstructionLibrary.loanEmi,
    InstructionLibrary.difficult,
    InstructionLibrary.takeMessage,
)

/**
 * The whole Instructions flow: hub (recommended, active count, presets) → preset detail (Apply) →
 * setup chat, and Active Instructions with edit / delete. Adding or deleting flashes a toast.
 */
@Composable
fun InstructionsFlow(
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = {},
    initialActive: List<Instruction> = DefaultActive,
    initialView: InstructionsView = InstructionsView.Hub,
    noRecommendations: Boolean = false,
    initialSetup: InstructionSetup? = null,
) {
    var view by remember { mutableStateOf(initialView) }
    val active = remember { mutableStateListOf<Instruction>().apply { addAll(initialActive) } }
    var setup by remember { mutableStateOf(initialSetup) }
    var toast by remember { mutableStateOf<String?>(null) }
    fun isActive(id: String) = active.any { it.id == id }
    val recs = if (noRecommendations) emptyList() else Recommendations.filter { !isActive(it.instruction.id) }

    fun startSetup(instruction: Instruction, ask: String = instruction.title) {
        setup = InstructionSetup(instruction, ask, needsInput = FollowUps.containsKey(instruction.id))
    }
    fun startCustom() = startSetup(
        Instruction(
            id = "custom-${System.currentTimeMillis()}",
            title = "New instruction",
            whenText = "Calls like this come in",
            doText = "Tell me what to do",
        ),
        ask = "Add a new instruction",
    )
    LaunchedEffect(toast) {
        if (toast != null) {
            delay(2000)
            toast = null
        }
    }

    val overlay: @Composable BoxScope.() -> Unit = {
        InstructionSetupSheet(
            setup = setup,
            visible = setup != null,
            onDismissRequest = { setup = null },
            onDone = { i ->
                active.removeAll { it.id == i.id }
                active.add(i)
                setup = null
                toast = "Instruction added"
            },
        )
        val message = toast
        if (message != null) {
            EqualSnackbar(
                message = message,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(start = 16.dp, end = 16.dp, bottom = 96.dp),
            )
        }
    }
    val addButton: @Composable () -> Unit = {
        EqualButton(
            text = "Add New Instruction",
            onClick = { startCustom() },
            size = EqualButtonSize.Block,
            leadingIcon = EqualIcons.Add,
        )
    }

    when (val v = view) {
        is InstructionsView.PresetDetail -> {
            val p = Presets.first { it.id == v.presetId }
            FlowScaffold(title = p.title, onBack = { view = InstructionsView.Hub }, modifier = modifier, overlay = overlay) {
                EqualText(
                    text = p.description,
                    style = EqualTheme.type.body1,
                    color = EqualTheme.colors.textSecondary,
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 12.dp),
                )
                Column(
                    Modifier.padding(start = 20.dp, end = 20.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    p.instructions.forEach { ins ->
                        PresetInstructionCard(
                            instruction = ins,
                            applied = isActive(ins.id),
                            onApply = { startSetup(ins.copy(source = InstructionSource(p.art, p.title))) },
                        )
                    }
                }
            }
        }

        InstructionsView.Active -> FlowScaffold(
            title = "Active Instructions",
            onBack = { view = InstructionsView.Hub },
            modifier = modifier,
            trailing = {
                if (active.isNotEmpty()) EqualTextButton(text = "Add", onClick = { startCustom() }, leadingIcon = EqualIcons.Add)
            },
            footer = if (active.isEmpty()) addButton else null,
            overlay = overlay,
        ) {
            if (active.isEmpty()) {
                Spacer(Modifier.height(96.dp))
                EmptyState(
                    title = "No instructions yet",
                    body = "Create your first instruction to tell your assistant how to handle calls",
                    illustration = { EqualAssetImage(EqualAssets.IllustrationInstructionsEmpty, 96.dp) },
                )
            } else {
                SectionHeader(title = "Active instructions - ${active.size}")
                Column(
                    Modifier.padding(start = 20.dp, end = 20.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    active.toList().forEach { ins ->
                        InstructionCard(
                            instruction = ins,
                            onEdit = { startSetup(ins, ask = "Edit: ${ins.title}") },
                            onDelete = {
                                active.removeAll { it.id == ins.id }
                                toast = "Instruction deleted"
                            },
                        )
                    }
                }
            }
        }

        InstructionsView.Hub -> FlowScaffold(
            title = "Instructions",
            onBack = onBack,
            modifier = modifier,
            footer = addButton,
            overlay = overlay,
        ) {
            SectionHeader(title = "Recommended for you")
            if (recs.isNotEmpty()) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(start = 20.dp, end = 20.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    recs.forEach { r ->
                        RecommendedCard(
                            emoji = r.emoji,
                            title = r.title,
                            description = r.description,
                            onAdd = { startSetup(r.instruction, ask = r.ask) },
                        )
                    }
                }
            } else {
                SuggestionsComingCard(Modifier.padding(horizontal = 20.dp))
            }
            SectionHeader(title = "Your instructions")
            ActiveInstructionsRow(
                count = active.size,
                onClick = { view = InstructionsView.Active },
                modifier = Modifier.padding(horizontal = 20.dp),
            )
            SectionHeader(title = "Browse presets")
            Column(
                Modifier.padding(start = 20.dp, end = 20.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Presets.forEach { p ->
                    PresetCard(
                        art = p.art,
                        tint = p.tint,
                        title = p.title,
                        description = p.description,
                        onClick = { view = InstructionsView.PresetDetail(p.id) },
                    )
                }
            }
        }
    }
}

/** Which scripted setup chat [InstructionSetupDemo] shows. */
enum class SetupDemo { Decline, Delivery }

/** Static render of the setup chat at its opening state, for docs and design review. */
@Composable
fun InstructionSetupDemo(modifier: Modifier = Modifier, which: SetupDemo = SetupDemo.Decline) {
    val setup = when (which) {
        SetupDemo.Decline -> InstructionSetup(Recommendations[0].instruction, Recommendations[0].ask, needsInput = false)
        SetupDemo.Delivery -> InstructionSetup(InstructionLibrary.delivery, "Handle delivery and courier calls", needsInput = true)
    }
    InstructionsFlow(modifier = modifier, initialSetup = setup)
}

// =============================================================================================
// Previews (every state, light + dark)
// =============================================================================================

@Preview(name = "Light", widthDp = EqualFrameWidthDp, heightDp = EqualFrameHeightDp, showBackground = true)
@Preview(
    name = "Dark",
    widthDp = EqualFrameWidthDp,
    heightDp = EqualFrameHeightDp,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
)
internal annotation class FlowPreviews

@FlowPreviews @Composable private fun PersonalDetailsPreview() = EqualTheme { PersonalDetailsScreen() }
@FlowPreviews @Composable private fun PersonalDetailsFemalePreview() = EqualTheme { PersonalDetailsScreen(initialName = "Riya Kapoor", initialGender = Gender.Female) }
@FlowPreviews @Composable private fun SilentModeOnPreview() = EqualTheme { SilentModeScreen() }
@FlowPreviews @Composable private fun SilentModeOffPreview() = EqualTheme { SilentModeScreen(initialMuteAll = false) }
@FlowPreviews @Composable private fun WhatsAppOnPreview() = EqualTheme { WhatsAppUpdatesScreen() }
@FlowPreviews @Composable private fun WhatsAppOffPreview() = EqualTheme { WhatsAppUpdatesScreen(initialOn = false) }
@FlowPreviews @Composable private fun WhatsAppSheetPreview() = EqualTheme { WhatsAppUpdatesScreen(showSheet = true) }
@FlowPreviews @Composable private fun ContactInfoPreview() = EqualTheme { ContactInfoScreen() }
@FlowPreviews @Composable private fun ContactInfoInitialsPreview() = EqualTheme { ContactInfoScreen(name = "Priya Sharma", initials = "PS") }
@FlowPreviews @Composable private fun AddressesListPreview() = EqualTheme { AddressesScreen() }
@FlowPreviews @Composable private fun AddressesEmptyPreview() = EqualTheme { AddressesScreen(initial = emptyList()) }
@FlowPreviews @Composable private fun AddressesEditPreview() = EqualTheme { AddressesScreen(openEdit = "a4") }
@FlowPreviews @Composable private fun AddressesAddPreview() = EqualTheme { AddressesScreen(openEdit = NewAddress) }
@FlowPreviews @Composable private fun AddressesDeletePreview() = EqualTheme { AddressesScreen(confirmDelete = "a2") }
@FlowPreviews @Composable private fun InstructionsHubPreview() = EqualTheme { InstructionsFlow() }
@FlowPreviews @Composable private fun InstructionsHubNoRecsPreview() = EqualTheme { InstructionsFlow(noRecommendations = true) }
@FlowPreviews @Composable private fun InstructionsPresetPreview() = EqualTheme { InstructionsFlow(initialView = InstructionsView.PresetDetail("everyday")) }
@FlowPreviews @Composable private fun InstructionsActivePreview() = EqualTheme { InstructionsFlow(initialView = InstructionsView.Active) }
@FlowPreviews @Composable private fun InstructionsActiveEmptyPreview() = EqualTheme { InstructionsFlow(initialView = InstructionsView.Active, initialActive = emptyList()) }
@FlowPreviews @Composable private fun SetupDeclinePreview() = EqualTheme { InstructionSetupDemo(which = SetupDemo.Decline) }
@FlowPreviews @Composable private fun SetupDeliveryPreview() = EqualTheme { InstructionSetupDemo(which = SetupDemo.Delivery) }

// ---------------------------------------------------------------- Temporarily Unavailable (Temporarily Unavailable design)

enum class SystemStatusStage { Unavailable, Notified, BackOn }

/**
 * Chats while Equal AI is down, then back. Pass [stage] to pin a state; leave it null to run the
 * flow: tap "Notify me when ready" → Notified → after [recoverAfterMillis] the heads-up
 * notification drops in and Meet Your Assistant takes the promo slot. Port of web SystemStatusFlow.
 */
@Composable
fun SystemStatusFlowSample(
    modifier: Modifier = Modifier,
    stage: SystemStatusStage? = null,
    recoverAfterMillis: Long = 2500,
) {
    var live by remember { mutableStateOf(SystemStatusStage.Unavailable) }
    var banner by remember { mutableStateOf(true) }
    val current = stage ?: live
    LaunchedEffect(stage, live) {
        if (stage == null && live == SystemStatusStage.Notified) {
            delay(recoverAfterMillis)
            banner = true
            live = SystemStatusStage.BackOn
        } else if (stage == null && live == SystemStatusStage.BackOn) {
            delay(4000)
            banner = false
        }
    }
    Box(modifier.fillMaxSize()) {
        key(current == SystemStatusStage.BackOn) {
            HomeScreenSample(
                systemStatus = when (current) {
                    SystemStatusStage.Unavailable -> SystemStatusState.Unavailable
                    SystemStatusStage.Notified -> SystemStatusState.Notified
                    SystemStatusStage.BackOn -> null
                },
                onNotifyClick = { live = SystemStatusStage.Notified },
                promo = { MeetAssistantCard(onCallClick = {}, onDismiss = {}) },
            )
        }
        if (current == SystemStatusStage.BackOn && (stage != null || banner)) {
            SystemNotificationPreview(
                title = "Equal AI Assistant is back on",
                body = "Equal AI is now handling all your unknown calls.",
                modifier = Modifier
                    .zIndex(2f)
                    .padding(start = 6.dp, top = 30.dp, end = 6.dp),
            )
        }
    }
}
