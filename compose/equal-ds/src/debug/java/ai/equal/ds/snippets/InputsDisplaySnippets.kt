// Compose code shown on the docs site (Components → Action chips, Inputs, Display, Status bar, Top bar).
// Each function body is extracted by scripts/build-snippets.mjs; keep the
// `// snippet: <doc id> / <example title>` line above it.
// Debug source set only: compiled with the module, never shipped in the release AAR.
@file:Suppress("unused")

package ai.equal.ds.snippets

import ai.equal.ds.components.EqualSkeletonTone
import ai.equal.ds.components.EqualSkeletonText
import ai.equal.ds.components.EqualSkeletonShape
import ai.equal.ds.components.EqualSkeleton
import ai.equal.ds.components.EqualRadioOption
import ai.equal.ds.components.EqualRadioGroup
import ai.equal.ds.components.EqualRadio
import ai.equal.ds.components.EqualControlSize
import ai.equal.ds.components.EqualCheckboxField
import ai.equal.ds.assets.EqualCategory
import ai.equal.ds.components.AiAvatar
import ai.equal.ds.components.AiAvatarSize
import ai.equal.ds.components.CategoryAvatar
import ai.equal.ds.components.ChatListItem
import ai.equal.ds.components.ChatListItemState
import ai.equal.ds.components.ContactAvatar
import ai.equal.ds.components.ContactTone
import ai.equal.ds.components.InitialsAvatar
import ai.equal.ds.components.CountBadge
import ai.equal.ds.components.EmptyState
import ai.equal.ds.components.EqualAppBar
import ai.equal.ds.components.EqualCheckbox
import ai.equal.ds.components.EqualIconButton
import ai.equal.ds.components.EqualLogo
import ai.equal.ds.components.EqualLogoOrb
import ai.equal.ds.components.EqualMark
import ai.equal.ds.components.EqualMascot
import ai.equal.ds.components.EqualSwitch
import ai.equal.ds.components.EqualText
import ai.equal.ds.components.EqualTextField
import ai.equal.ds.components.FeatureBadge
import ai.equal.ds.components.LargeActionChip
import ai.equal.ds.components.LargeActionChipState
import ai.equal.ds.components.NewBadge
import ai.equal.ds.components.PlanBadge
import ai.equal.ds.components.ProfileAvatar
import ai.equal.ds.components.ProfileAvatarVariant
import ai.equal.ds.components.RowGroup
import ai.equal.ds.components.SearchField
import ai.equal.ds.components.SegmentedChips
import ai.equal.ds.components.SelectionChatHeader
import ai.equal.ds.components.SettingsRow
import ai.equal.ds.components.SmallActionChip
import ai.equal.ds.components.SmallActionChipStyle
import ai.equal.ds.components.StatusBar
import ai.equal.ds.components.TextFieldAlign
import ai.equal.ds.components.ToggleRow
import ai.equal.ds.components.TopBar
import ai.equal.ds.components.VoiceInputBar
import ai.equal.ds.icons.EqualIcons
import ai.equal.ds.icons.EqualLogoVariant
import ai.equal.ds.theme.EqualTheme
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import ai.equal.ds.components.EqualLottie
import ai.equal.ds.R
import ai.equal.ds.components.PlanTier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

// ============================================================ Action chips

// snippet: action-chips / Small chip states
@Composable
internal fun ActionChipsSmallStatesSnippet() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SmallActionChip(label = "Call Back", emoji = "📞", onClick = {})
            SmallActionChip(label = "Update Address", emoji = "✏️", onClick = {})
        }
        SmallActionChip(label = "Call Back", emoji = "📞", onClick = {}, style = SmallActionChipStyle.Read)
        SmallActionChip(label = "Call Back", emoji = "📞", onClick = {}, style = SmallActionChipStyle.Outlined)
        // Compose: no chevron toggle; the trailing chevron is always shown.
        SmallActionChip(label = "Add to Calendar", emoji = "🗓️", onClick = {}, style = SmallActionChipStyle.Outlined)
    }
}

// snippet: action-chips / Large chips
@Composable
internal fun ActionChipsLargeSnippet() {
    // Compose: no ChipRow; a Row with spacedBy(8.dp) does the same job.
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        LargeActionChip(label = "Takeover", emoji = "📞", onClick = {})
        LargeActionChip(label = "Ask to reschedule", emoji = "🗓️", onClick = {})
    }
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        LargeActionChip(label = "Edit", emoji = "✍️", onClick = {}, state = LargeActionChipState.Read)
        LargeActionChip(label = "Confirm", emoji = "👍", onClick = {})
    }
}

// snippet: action-chips / In a chat row
@Composable
internal fun ActionChipsInChatRowSnippet() {
    // Compose: no ChipRow; a horizontally scrolling Row (gap 12) clips at the right edge.
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SmallActionChip(label = "Call Back", emoji = "📞", onClick = {})
        SmallActionChip(label = "Update Address", emoji = "✏️", onClick = {})
        SmallActionChip(label = "Add to Calendar", emoji = "🗓️", onClick = {})
    }
}

// ============================================================ Switch

// snippet: switch / Interactive
@Composable
internal fun SwitchInteractiveSnippet() {
    var on by remember { mutableStateOf(true) }

    // Compose: no label param; set the accessible label with semantics.
    EqualSwitch(
        checked = on,
        onCheckedChange = { on = it },
        modifier = Modifier.semantics { contentDescription = "AI Assistant" },
    )
}

// snippet: switch / States
@Composable
internal fun SwitchStatesSnippet() {
    Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
        EqualSwitch(checked = true, onCheckedChange = {})
        EqualSwitch(checked = false, onCheckedChange = {})
        EqualSwitch(checked = true, onCheckedChange = {}, enabled = false)
        EqualSwitch(checked = false, onCheckedChange = {}, enabled = false)
    }
}

// snippet: switch / In context
@Composable
internal fun SwitchInContextSnippet() {
    var mute by remember { mutableStateOf(true) }

    RowGroup {
        ToggleRow(
            label = "Mute Promotional Calls",
            description = "Equal AI Assistant can identify Promotional Calls and automatically mute them",
            descriptionWraps = true,
            checked = mute,
            onCheckedChange = { mute = it },
        )
    }
}

// ============================================================ Checkbox

// snippet: checkbox / Interactive
@Composable
internal fun CheckboxInteractiveSnippet() {
    var all by remember { mutableStateOf(false) }
    var loanOffers by remember { mutableStateOf(false) }

    // Compose: no label / labelVariant params; put an EqualText beside the box.
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        EqualCheckbox(checked = all, onCheckedChange = { all = it })
        EqualText(text = "Select All", style = EqualTheme.type.bodyLarge, color = EqualTheme.colors.textPrimary)
    }
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        EqualCheckbox(checked = loanOffers, onCheckedChange = { loanOffers = it })
        EqualText(text = "Loan Offers", style = EqualTheme.type.body1, color = EqualTheme.colors.textPrimary)
    }
}

// snippet: checkbox / Sizes and bare
@Composable
internal fun CheckboxSizesSnippet() {
    EqualCheckboxField(checked = true, onCheckedChange = {}, label = "Label", size = EqualControlSize.S)
    EqualCheckboxField(checked = true, onCheckedChange = {}, label = "Label", size = EqualControlSize.M)
    EqualCheckboxField(checked = true, onCheckedChange = {}, label = "Label", size = EqualControlSize.L)
    EqualCheckboxField(checked = false, onCheckedChange = {}, label = "Label", support = "Support text")
}

// snippet: radio / Group
@Composable
internal fun RadioGroupSnippet() {
    var v by remember { mutableStateOf<String?>(null) }
    EqualRadioGroup(
        options = listOf(
            EqualRadioOption("30m", "30 minutes"),
            EqualRadioOption("1h", "1 hour"),
            EqualRadioOption("tomorrow", "Until tomorrow morning", support = "Back at 9:00 AM"),
        ),
        selected = v,
        onSelect = { v = it },
    )
}

// snippet: radio / Sizes
@Composable
internal fun RadioSizesSnippet() {
    EqualRadio(selected = true, onClick = {}, label = "Label", size = EqualControlSize.S)
    EqualRadio(selected = true, onClick = {}, label = "Label", size = EqualControlSize.M)
    EqualRadio(selected = true, onClick = {}, label = "Label", size = EqualControlSize.L)
    EqualRadio(selected = false, onClick = {}, label = "Label", size = EqualControlSize.L)
}

// snippet: skeleton / Loading a chat row
@Composable
internal fun SkeletonRowSnippet() {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        EqualSkeleton(shape = EqualSkeletonShape.Circle, height = 48.dp)
        EqualSkeletonText(lines = 2, modifier = Modifier.weight(1f))
    }
}

// snippet: skeleton / Gold (locked summary)
@Composable
internal fun SkeletonGoldSnippet() {
    EqualSkeletonText(lines = 3, tone = EqualSkeletonTone.Gold)
}

// snippet: checkbox / In the selection header
@Composable
internal fun CheckboxSelectionHeaderSnippet() {
    val ids = listOf("arnav", "amit", "priya")
    var selected by remember { mutableStateOf(setOf<String>()) }

    SelectionChatHeader(
        selectedCount = selected.size,
        allSelected = selected.size == ids.size,
        onSelectAllChange = { all -> selected = if (all) ids.toSet() else emptySet() },
        onBackClick = {},
        onMarkReadClick = {},
        onDeleteClick = {},
    )
}

// ============================================================ Text field

// snippet: text-field / Name
@Composable
internal fun TextFieldNamePillSnippet() {
    var name by remember { mutableStateOf("Keshav") }

    EqualTextField(
        value = name,
        onValueChange = { name = it },
        label = "Name",
        placeholder = "Your name",
        helper = AnnotatedString("Your AI will call you 'Keshav'"),
    )
}

// snippet: text-field / Optional label
@Composable
internal fun TextFieldOptionalSnippet() {
    var direction by remember { mutableStateOf("") }

    EqualTextField(
        value = direction,
        onValueChange = { direction = it },
        label = "Guiding direction",
        optional = true,
        placeholder = "e.g. Near City Center Mall",
    )
}

// snippet: text-field / Multiline and grouped
@Composable
internal fun TextFieldMultilineSnippet() {
    var line1 by remember { mutableStateOf("") }
    var line2 by remember { mutableStateOf("") }

    // Fields that share one ADDRESS label omit `label` and pass contentDescription instead.
    EqualTextField(
        value = line1,
        onValueChange = { line1 = it },
        contentDescription = "Address line 1",
        placeholder = "Flat, house no., building",
        multiline = true,
    )
    EqualTextField(
        value = line2,
        onValueChange = { line2 = it },
        contentDescription = "Address line 2",
        placeholder = "Street, area, landmark",
        multiline = true,
    )
}

// snippet: text-field / Error
@Composable
internal fun TextFieldErrorSnippet() {
    var pin by remember { mutableStateOf("") }

    EqualTextField(
        value = pin,
        onValueChange = { pin = it.filter(Char::isDigit).take(6) },
        contentDescription = "Pincode",
        placeholder = "Pincode",
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        tabular = true,
        align = TextFieldAlign.Center,
        error = if (pin.length == 6) null else "Enter a 6-digit pincode",
    )
}

// ============================================================ Search field

// snippet: search-field / Empty
@Composable
internal fun SearchFieldEmptySnippet() {
    var q by remember { mutableStateOf("") }

    // Compose: the default placeholder is "Search", so pass "Search chats".
    SearchField(value = q, onValueChange = { q = it }, placeholder = "Search chats")
}

// snippet: search-field / With a query
@Composable
internal fun SearchFieldWithQuerySnippet() {
    var q by remember { mutableStateOf("Amit") }

    // Compose: no built-in clear (X) button; clear by setting the value to "".
    SearchField(value = q, onValueChange = { q = it }, placeholder = "Search chats")
}

// snippet: search-field / Filtering the chat list
@Composable
internal fun SearchFieldFilteringSnippet() {
    val chats = listOf("Arnav Raj", "Amit Kumar", "Priya Nair")
    var q by remember { mutableStateOf("") }
    val results = chats.filter { it.contains(q, ignoreCase = true) }

    SearchField(value = q, onValueChange = { q = it }, placeholder = "Search chats")
    if (results.isEmpty()) {
        EmptyState(title = "No chats found", body = "Nothing matches \"$q\". Try a name or number.")
    } else {
        results.forEach { name ->
            ChatListItem(name = name, preview = "You have a visitor", date = "Yesterday")
        }
    }
}

// ============================================================ Segmented chips

// snippet: segmented-chips / Address label
@Composable
internal fun SegmentedChipsAddressSnippet() {
    val labels = listOf("Home", "Work", "Other")
    var selected by remember { mutableStateOf(0) }

    SegmentedChips(options = labels, selectedIndex = selected, onSelectedIndexChange = { selected = it })
}

// snippet: segmented-chips / Any string union
@Composable
internal fun SegmentedChipsSpeedSnippet() {
    val speeds = listOf("Slow", "Normal", "Fast")
    var selected by remember { mutableStateOf(1) }

    // Compose: options are a List<String> addressed by index, not a typed union.
    SegmentedChips(options = speeds, selectedIndex = selected, onSelectedIndexChange = { selected = it })
}

// ============================================================ Composer

// snippet: composer / Interactive
@Composable
internal fun ComposerInteractiveSnippet() {
    val sent = remember { mutableStateListOf<String>() }
    var text by remember { mutableStateOf("") }

    // Compose: always controlled; clear the value yourself after send.
    VoiceInputBar(
        value = text,
        onValueChange = { text = it },
        onSend = {
            sent += text.trim()
            text = ""
        },
    )
}

// snippet: composer / States
@Composable
internal fun ComposerStatesSnippet() {
    var text by remember { mutableStateOf("Sure, I'll be there!") }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        VoiceInputBar(value = "", onValueChange = {}, onSend = {})
        // Compose: no forceFocus; the focused look appears when the field has focus.
        VoiceInputBar(value = text, onValueChange = { text = it }, onSend = {})
    }
}

// ============================================================ Badges

// snippet: badges / All badges
@Composable
internal fun BadgesAllSnippet() {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
        NewBadge()
        // Compose: no tone = "soft"; NewBadge has the solid fill only.
        CountBadge(count = 5)
        CountBadge(count = 12)
        CountBadge(count = 140) // renders 99+
        FeatureBadge()
        PlanBadge()
    }
}

// snippet: badges / In context
@Composable
internal fun BadgesInContextSnippet() {
    Column {
        TopBar() // PlanBadge
        RowGroup(inset = 0.dp) {
            SettingsRow(
                label = "Instructions",
                description = "Tell assistant how you'd like it to help",
                leadingIcon = EqualIcons.Instructions,
                showNewBadge = true, // NewBadge
            )
        }
        ChatListItem(
            name = "Arnav Raj",
            preview = "You have a visitor",
            date = "Yesterday",
            state = ChatListItemState.Unread,
            unreadCount = 5, // CountBadge
        )
    }
}

// ============================================================ Avatars

// snippet: avatars / Caller categories
@Composable
internal fun AvatarsCategoriesSnippet() {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        CategoryAvatar(category = EqualCategory.NA, size = 48.dp)
        CategoryAvatar(category = EqualCategory.Delivery, size = 48.dp)
        CategoryAvatar(category = EqualCategory.RealEstate, size = 48.dp)
    }
}

// snippet: avatars / Contact avatar
@Composable
internal fun AvatarsContactSnippet() {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
        ContactAvatar(size = 48.dp)
        ContactAvatar(size = 48.dp, category = EqualCategory.Others)
        ContactAvatar(size = 48.dp, initials = "AN")
        ContactAvatar(size = 48.dp, initials = "AN", selected = true)
        ContactAvatar(size = 36.dp, category = EqualCategory.Career)
    }
}

// snippet: avatars / Saved-contact colours
@Composable
internal fun AvatarsSavedContactColoursSnippet() {
    Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
        // The tone is picked from the initials by default; pass tone to choose.
        listOf("AN", "AR", "KS", "PR", "VJ", "MS").zip(ContactTone.entries).forEach { (initials, tone) ->
            InitialsAvatar(initials = initials, size = 48.dp, tone = tone)
        }
    }
}

// snippet: avatars / Assistant, user and mascot
@Composable
internal fun AvatarsAssistantUserMascotSnippet() {
    Row(horizontalArrangement = Arrangement.spacedBy(20.dp), verticalAlignment = Alignment.Bottom) {
        AiAvatar(size = AiAvatarSize.Small)
        AiAvatar(size = AiAvatarSize.Medium)
        AiAvatar(size = AiAvatarSize.Large)
        EqualLogoOrb(size = 58.dp)
        ProfileAvatar(variant = ProfileAvatarVariant.Male, width = 70.dp)
        ProfileAvatar(variant = ProfileAvatarVariant.Female, width = 70.dp)
        EqualMascot(size = 40.dp)
        EqualMascot(size = 40.dp, dimmed = true)
    }
}

// ============================================================ Status bar

// snippet: status-bar / Default
@Composable
internal fun StatusBarDefaultSnippet() {
    StatusBar()
}

// snippet: status-bar / On a white header
@Composable
internal fun StatusBarWhiteHeaderSnippet() {
    Column {
        StatusBar(time = "9:41", background = EqualTheme.colors.bgSurface)
        EqualAppBar(title = "Contact Info", onBackClick = {}, background = EqualTheme.colors.bgSurface)
    }
}

// ============================================================ Top bar

// snippet: top-bar / Default
@Composable
internal fun TopBarDefaultSnippet() {
    TopBar()
}

// snippet: top-bar / Without a plan, with a trailing action
@Composable
internal fun TopBarTrailingSnippet() {
    TopBar(planLabel = null) {
        EqualIconButton(icon = EqualIcons.Search, contentDescription = "Search chats", onClick = {})
    }
}

// snippet: top-bar / Logo and mark
@Composable
internal fun TopBarLogoAndMarkSnippet() {
    Row(horizontalArrangement = Arrangement.spacedBy(24.dp), verticalAlignment = Alignment.CenterVertically) {
        EqualLogo(height = 20.dp)
        EqualLogo(height = 20.dp, variant = EqualLogoVariant.WORDMARK)
        EqualMark(width = 23.dp, color = EqualTheme.colors.textAction)
    }
}

// snippet: avatars / Profile avatar: free vs paid
@Composable
internal fun ProfileAvatarTierSnippet() {
    Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
        ProfileAvatar(variant = ProfileAvatarVariant.Male, width = 70.dp, tier = PlanTier.Free)
        ProfileAvatar(variant = ProfileAvatarVariant.Male, width = 70.dp, tier = PlanTier.Paid)
    }
}

// snippet: lottie / Typing dots
@Composable
internal fun LottieTypingDotsSnippet() {
    // res/raw/lottie_typing_dots.json (copied from assets/lottie/ by `npm run assets`)
    EqualLottie(
        rawRes = R.raw.lottie_typing_dots,
        width = 120.dp,
        height = 40.dp,
        tint = EqualTheme.colors.textAction,
        contentDescription = "Equal AI is typing",
    )
}

// snippet: lottie / Original colours, play once
@Composable
internal fun LottiePlayOnceSnippet() {
    EqualLottie(rawRes = R.raw.lottie_typing_dots, width = 120.dp, height = 40.dp, iterations = 1)
}
