// Compose code shown on the docs site (Components → You tab and Flows). Each function body is extracted by
// scripts/build-snippets.mjs; keep the `// snippet: <doc id> / <example title>` line above it.
// Debug source set only: compiled with the module, never shipped in the release AAR.
@file:Suppress("unused")

package ai.equal.ds.snippets

import ai.equal.ds.assets.EqualAssetImage
import ai.equal.ds.assets.EqualAssets
import ai.equal.ds.assets.EqualCategory
import ai.equal.ds.catalog.InstructionLibrary
import ai.equal.ds.catalog.SampleAddresses
import ai.equal.ds.components.AddressCard
import ai.equal.ds.components.AddressForm
import ai.equal.ds.components.AiSummaryCard
import ai.equal.ds.components.AiSummaryStatus
import ai.equal.ds.components.CallDirection
import ai.equal.ds.components.CallHistoryRow
import ai.equal.ds.components.ContactInfoAction
import ai.equal.ds.components.ContactInfoHeader
import ai.equal.ds.components.EmptyState
import ai.equal.ds.components.EqualAppBar
import ai.equal.ds.components.EqualTextField
import ai.equal.ds.components.Gender
import ai.equal.ds.components.GenderSelector
import ai.equal.ds.components.IllustrationHeader
import ai.equal.ds.components.Instruction
import ai.equal.ds.components.InstructionCard
import ai.equal.ds.components.InstructionPreview
import ai.equal.ds.components.InstructionSource
import ai.equal.ds.components.InviteCard
import ai.equal.ds.components.PlanCard
import ai.equal.ds.components.PresetCard
import ai.equal.ds.components.ActiveInstructionsRow
import ai.equal.ds.components.PresetInstructionCard
import ai.equal.ds.components.PresetArt
import ai.equal.ds.components.PresetTint
import ai.equal.ds.components.ProfileAvatar
import ai.equal.ds.components.ProfileAvatarVariant
import ai.equal.ds.components.ProfileHeader
import ai.equal.ds.components.ProfileHero
import ai.equal.ds.components.RecommendedCard
import ai.equal.ds.components.RowGroup
import ai.equal.ds.components.SavedAddress
import ai.equal.ds.components.SectionHeader
import ai.equal.ds.components.SuggestionsComingCard
import ai.equal.ds.components.ToggleRow
import ai.equal.ds.components.defaultContactInfoActions
import ai.equal.ds.components.formatAddress
import ai.equal.ds.icons.EqualIcons
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import ai.equal.ds.components.PlanTier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp

// snippet: empty-state / Addresses empty
@Composable
internal fun EmptyStateAddressesSnippet() {
    EmptyState(
        title = "Never explain your address again",
        body = "Save frequently used addresses, and your assistant will use them to handle delivery, pickup calls, etc. more naturally.",
        // Compose: the illustration is a slot, not an asset name
        illustration = { EqualAssetImage(EqualAssets.IllustrationAddressesEmpty, 96.dp) },
        actionLabel = "Add new Address",
        onActionClick = {},
    )
}

// snippet: empty-state / No results
@Composable
internal fun EmptyStateNoResultsSnippet() {
    val query = "Priya"
    EmptyState(title = "No chats found", body = "Nothing matches \"$query\". Try a name or number.")
}

// snippet: empty-state / Illustration header
@Composable
internal fun EmptyStateIllustrationHeaderSnippet() {
    IllustrationHeader(asset = EqualAssets.IllustrationSilentMode)
    RowGroup {
        ToggleRow(
            label = "Mute Promotional Calls",
            description = "Equal AI Assistant can identify Promotional Calls and automatically mute them",
            descriptionWraps = true,
            checked = true,
            onCheckedChange = {},
        )
    }
}

// snippet: profile-hero / You tab hero with plan card
@Composable
internal fun ProfileHeroWithPlanSnippet() {
    ProfileHero(name = "Keshav", phone = "+91 98789 56749", onEdit = {}) {
        PlanCard(onManageClick = {})
    }
}

// snippet: profile-hero / Female avatar, no plan
@Composable
internal fun ProfileHeroFemaleNoPlanSnippet() {
    ProfileHero(
        name = "Priya Sharma",
        phone = "+91 99100 22114",
        avatar = ProfileAvatarVariant.Female,
        plan = null,
        onEdit = {},
    )
}

// snippet: profile-hero / Compact header
@Composable
internal fun ProfileHeaderCompactSnippet() {
    ProfileHeader(name = "Keshav", phone = "+91 98789 56749")
}

// snippet: plan-card / Current and expired
@Composable
internal fun PlanCardStatesSnippet() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        PlanCard(onManageClick = {})
        // Compose: status="expired" is a Boolean flag
        PlanCard(expired = true, onManageClick = {})
    }
}

// snippet: plan-card / Custom tier
@Composable
internal fun PlanCardCustomTierSnippet() {
    PlanCard(tier = "LITE", price = "₹49/Monthly")
}

// snippet: ai-summary-card / Ready
@Composable
internal fun AiSummaryReadySnippet() {
    // Compose: status isn't inferred from summary; pass Ready explicitly
    AiSummaryCard(
        status = AiSummaryStatus.Ready,
        summary = "Your family calls you a lot. They always say it's urgent. They almost never say why.",
    )
}

// snippet: ai-summary-card / Empty
@Composable
internal fun AiSummaryEmptySnippet() {
    AiSummaryCard()
}

// snippet: ai-summary-card / Loading
@Composable
internal fun AiSummaryLoadingSnippet() {
    AiSummaryCard(status = AiSummaryStatus.Loading, lastUpdated = "8:06 AM")
}

// snippet: invite-card / Default
@Composable
internal fun InviteCardDefaultSnippet() {
    InviteCard(onShare = {})
}

// snippet: invite-card / Custom copy
@Composable
internal fun InviteCardCustomCopySnippet() {
    InviteCard(title = "Give a month free", subtitle = "Share Equal AI with family", onShare = {})
}

// snippet: contact-info-header / Saved contact
@Composable
internal fun ContactInfoSavedSnippet() {
    ContactInfoHeader(
        name = "Amit Nanda",
        phone = "+91 98717 46113",
        initials = "AN",
        actions = listOf(
            ContactInfoAction("Call", EqualIcons.Call, onClick = {}),
            ContactInfoAction("Mute", EqualIcons.SilentMode, onClick = {}),
            ContactInfoAction("Save", EqualIcons.PersonAdd, onClick = {}),
            ContactInfoAction("Share", EqualIcons.Share, onClick = {}),
        ),
    )
}

// snippet: contact-info-header / Unknown caller, with history
@Composable
internal fun ContactInfoUnknownWithHistorySnippet() {
    ContactInfoHeader(
        name = "Arnav Raj",
        phone = "+91 98717 46113",
        category = EqualCategory.Career,
        // Call · Mute · Save · Share
        actions = defaultContactInfoActions(onCall = {}, onMute = {}, onSave = {}, onShare = {}),
    )
    SectionHeader(title = "Call history")
    RowGroup {
        CallHistoryRow(direction = CallDirection.Incoming, meta = "Today • 30 Sec.", time = "02:30 PM")
        CallHistoryRow(direction = CallDirection.Missed, meta = "Yesterday", time = "11:04 AM")
    }
}

// snippet: gender-selector / Interactive
@Composable
internal fun GenderSelectorInteractiveSnippet() {
    var gender by remember { mutableStateOf(Gender.Male) }
    GenderSelector(value = gender, onValueChange = { gender = it })
    ProfileAvatar(
        variant = if (gender == Gender.Male) ProfileAvatarVariant.Male else ProfileAvatarVariant.Female,
        width = 56.dp,
    )
}

// snippet: gender-selector / In Personal Details
@Composable
internal fun GenderSelectorPersonalDetailsSnippet() {
    var name by remember { mutableStateOf("Keshav") }
    var gender by remember { mutableStateOf(Gender.Female) }
    EqualAppBar(title = "Personal Details", onBackClick = {})
    EqualTextField(
        value = name,
        onValueChange = { name = it },
        label = "Name",
        helper = AnnotatedString("Your AI will call you '$name'"),
    )
    SectionHeader(title = "Gender", inset = 0.dp)
    GenderSelector(value = gender, onValueChange = { gender = it })
}

// snippet: address / Saved addresses
@Composable
internal fun AddressSavedListSnippet() {
    var addresses by remember { mutableStateOf(SampleAddresses) }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        addresses.forEach { a ->
            AddressCard(
                label = a.label,
                address = formatAddress(a),
                onEdit = {},
                onDelete = { addresses = addresses - a },
            )
        }
    }
}

// snippet: address / Long address
@Composable
internal fun AddressLongSnippet() {
    AddressCard(
        label = "Work",
        address = "Flat 4B, Block C, Skyline Towers, Phase 2, HITECH city, MG Road, Opposite City Center Mall, Banjara Hills, Hyderabad- 500034",
        onEdit = {},
    )
}

// snippet: address / Edit address form
@Composable
internal fun AddressEditFormSnippet() {
    AddressForm(
        // Compose: SavedAddress needs an id (web takes a Partial)
        initial = SavedAddress(
            id = "a1",
            label = "Home",
            line1 = "House no. 4, Tony Street",
            line2 = "Financial District",
            city = "Hyderabad",
            pincode = "500032",
        ),
        onSave = {},
    )
}

// snippet: instruction-cards / Recommended for you
@Composable
internal fun InstructionRecommendedSnippet() {
    var added by remember { mutableStateOf(setOf<String>()) }
    if (added.size < 2) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            if ("decline" !in added) {
                RecommendedCard(
                    emoji = "🚫",
                    title = "Decline sales & spam",
                    description = "Politely declines sales and insurance calls.",
                    onAdd = { added = added + "decline" },
                )
            }
            if ("message" !in added) {
                RecommendedCard(
                    emoji = "💬",
                    title = "Take a message",
                    description = "Takes their name, number and message.",
                    onAdd = { added = added + "message" },
                )
            }
        }
    } else {
        SuggestionsComingCard()
    }
}

// snippet: instruction-cards / Your instructions and browse presets
@Composable
internal fun InstructionBrowsePresetsSnippet() {
    ActiveInstructionsRow(count = 5, onClick = {})

    PresetCard(
        art = PresetArt.ScreenProtect, tint = PresetTint.Green, title = "Screen & Protect",
        description = "Decline spam, hold the line on loan and payments call, close wrong call", onClick = {},
    )
    PresetCard(
        art = PresetArt.EverydayCalls, tint = PresetTint.Blue, title = "Handle everyday calls",
        description = "Take message, guide deliveries, relay to family, start in your language", onClick = {},
    )
    PresetCard(
        art = PresetArt.Business, tint = PresetTint.Orange, title = "Run my business",
        description = "Capture enquiries, orders, bookings, job, and supplier calls", onClick = {},
    )
}

// snippet: instruction-cards / Preset instructions
@Composable
internal fun InstructionPresetSnippet() {
    var applied by remember { mutableStateOf(false) }
    PresetInstructionCard(
        instruction = InstructionLibrary.declineSales,
        applied = applied,
        onApply = { applied = true },
    )
}

// snippet: instruction-cards / Active instructions
@Composable
internal fun InstructionActiveSnippet() {
    InstructionCard(
        instruction = Instruction(
            id = "decline-sales",
            title = "Decline sales, promo spam calls",
            source = InstructionSource(art = PresetArt.ScreenProtect, label = "Screen & Protect"),
            whenText = "The caller is selling loan, insurance, credit card, product, or another unsolicited offer",
            doText = "Politely say I am not interested and end the conversation without discussing the offer",
        ),
        onEdit = {},
        onDelete = {},
    )
}

// snippet: instruction-cards / Preview in the setup chat
@Composable
internal fun InstructionPreviewSnippet() {
    var confirmed by remember { mutableStateOf(false) }
    InstructionPreview(
        title = "Decline sales calls",
        whenText = AnnotatedString("Caller is selling a loan, insurance or product"),
        action = AnnotatedString("Politely decline and end the conversation"),
        time = "03:21",
        confirmed = confirmed,
    )
}

// snippet: gender-selector / Free vs paid
@Composable
internal fun GenderFreeVsPaidSnippet() {
    var gender by remember { mutableStateOf(Gender.Male) }
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        GenderSelector(value = gender, onValueChange = { gender = it }, tier = PlanTier.Free)
        GenderSelector(value = gender, onValueChange = { gender = it }, tier = PlanTier.Paid)
    }
}
