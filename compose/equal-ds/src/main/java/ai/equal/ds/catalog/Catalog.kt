package ai.equal.ds.catalog

import ai.equal.ds.components.SampleSummaryActions
import ai.equal.ds.components.SummaryActions
import ai.equal.ds.components.LockedRecording
import ai.equal.ds.components.AssistantNoticeCard
import ai.equal.ds.components.EmojiGlyph
import ai.equal.ds.components.UsageCapCard
import ai.equal.ds.components.HomeNoticeCard
import ai.equal.ds.components.OfferStrip
import ai.equal.ds.components.ProcessingStripVariant
import ai.equal.ds.components.ProcessingStrip
import ai.equal.ds.components.PlanBadgeTone
import ai.equal.ds.components.AssistantToggleArt
import ai.equal.ds.components.EqualFrameHeightDp
import ai.equal.ds.components.EqualFrameWidthDp
import ai.equal.ds.assets.EqualAssetImage
import ai.equal.ds.assets.EqualAssets
import ai.equal.ds.assets.EqualCategory
import ai.equal.ds.components.ActionCard
import ai.equal.ds.components.PlanTier
import ai.equal.ds.components.instructionLines
import ai.equal.ds.components.ActionCardTone
import ai.equal.ds.components.ActionCardLine
import ai.equal.ds.components.SystemStatusCard
import ai.equal.ds.components.SystemStatusState
import ai.equal.ds.components.ActionCardAction
import ai.equal.ds.components.ActionChipData
import ai.equal.ds.components.AddressCard
import ai.equal.ds.components.AppBarAlign
import ai.equal.ds.components.Gender
import ai.equal.ds.components.GenderSelector
import ai.equal.ds.components.InstructionPreview
import ai.equal.ds.components.PresetCard
import ai.equal.ds.components.PresetInstructionCard
import ai.equal.ds.components.RecommendedCard
import ai.equal.ds.components.SuggestionsComingCard
import ai.equal.ds.components.formatAddress
import ai.equal.ds.components.AiAvatar
import ai.equal.ds.components.AiAvatarSize
import ai.equal.ds.components.AiChatPanel
import ai.equal.ds.components.AiSuggestion
import ai.equal.ds.components.AiSummaryCard
import ai.equal.ds.components.AiSummaryStatus
import ai.equal.ds.components.AssistantToggleCard
import ai.equal.ds.components.BottomNavBar
import ai.equal.ds.components.CallDirection
import ai.equal.ds.components.CallHistoryRow
import ai.equal.ds.components.CallSummaryBubble
import ai.equal.ds.components.CategoryAvatar
import ai.equal.ds.components.ChatBubble
import ai.equal.ds.components.ChatBubbleType
import ai.equal.ds.components.ChatHeader
import ai.equal.ds.components.ChatHeaderVariant
import ai.equal.ds.components.ChatListItem
import ai.equal.ds.components.ChatWallpaper
import ai.equal.ds.components.ChatListItemState
import ai.equal.ds.components.ConfirmSheetContent
import ai.equal.ds.components.ContactInfoHeader
import ai.equal.ds.components.ContextMenu
import ai.equal.ds.components.ContextMenuItem
import ai.equal.ds.components.CountBadge
import ai.equal.ds.components.DayDivider
import ai.equal.ds.components.EmptyState
import ai.equal.ds.components.EqualAppBar
import ai.equal.ds.components.EqualBottomSheet
import ai.equal.ds.components.EqualButton
import ai.equal.ds.components.EqualButtonSize
import ai.equal.ds.components.EqualButtonVariant
import ai.equal.ds.components.EqualCheckbox
import ai.equal.ds.components.EqualDialogContent
import ai.equal.ds.components.EqualDivider
import ai.equal.ds.components.EqualIconButton
import ai.equal.ds.components.EqualIconButtonVariant
import ai.equal.ds.components.EqualLogo
import ai.equal.ds.components.EqualLogoOrb
import ai.equal.ds.components.EqualMark
import ai.equal.ds.components.EqualMascot
import ai.equal.ds.icons.EqualLogoVariant
import ai.equal.ds.components.EqualPreviews
import ai.equal.ds.components.EqualSnackbar
import ai.equal.ds.components.EqualSwitch
import ai.equal.ds.components.EqualTab
import ai.equal.ds.components.EqualText
import ai.equal.ds.components.EqualTextButton
import ai.equal.ds.components.EqualTextField
import ai.equal.ds.components.FeatureBadge
import ai.equal.ds.components.FeedbackCard
import ai.equal.ds.components.HomeHeader
import ai.equal.ds.components.InstructionCard
import ai.equal.ds.components.LargeActionChip
import ai.equal.ds.components.NewBadge
import ai.equal.ds.components.PlanBadge
import ai.equal.ds.components.PlanCard
import ai.equal.ds.components.CallSummaryLock
import ai.equal.ds.components.CancelSheet
import ai.equal.ds.components.CancelStep
import ai.equal.ds.components.CouponTicket
import ai.equal.ds.components.DividerLabel
import ai.equal.ds.components.DividerLabelTone
import ai.equal.ds.components.FaqItem
import ai.equal.ds.components.FaqList
import ai.equal.ds.components.HelpCenterSheet
import ai.equal.ds.components.LockBadge
import ai.equal.ds.components.LockedContent
import ai.equal.ds.components.LockedPreviewCard
import ai.equal.ds.components.PaymentStatus
import ai.equal.ds.components.PaymentStatusSheet
import ai.equal.ds.components.PaywallFooter
import ai.equal.ds.components.PaywallHero
import ai.equal.ds.components.PlanBanner
import ai.equal.ds.components.PlanExpiry
import ai.equal.ds.components.PlanExpiryBanner
import ai.equal.ds.components.DiscountBadge
import ai.equal.ds.components.ExpiryChip
import ai.equal.ds.components.ExpiryTone
import ai.equal.ds.components.FeatureList
import ai.equal.ds.components.Quota
import ai.equal.ds.components.QuotaCard
import ai.equal.ds.components.ValueCard
import ai.equal.ds.components.ValueSheet
import ai.equal.ds.components.ValueSheetVariant
import ai.equal.ds.components.PriceTag
import ai.equal.ds.components.RatingPill
import ai.equal.ds.components.StatBlock
import ai.equal.ds.components.StatusCard
import ai.equal.ds.components.StatusTone
import ai.equal.ds.components.Testimonial
import ai.equal.ds.components.TestimonialCarousel
import ai.equal.ds.components.UnlockAudioPill
import ai.equal.ds.components.UnlockDetailsCard
import ai.equal.ds.components.RedirectNote
import ai.equal.ds.components.PlaybackSpeed
import ai.equal.ds.components.ProfileAvatar
import ai.equal.ds.components.ProfileAvatarVariant
import ai.equal.ds.components.ProfileHeader
import ai.equal.ds.components.ProfileHero
import ai.equal.ds.components.RowGroup
import ai.equal.ds.components.InviteCard
import ai.equal.ds.components.SearchField
import ai.equal.ds.components.LoadingDots
import ai.equal.ds.components.MessageResult
import ai.equal.ds.components.RecentSearches
import ai.equal.ds.components.SearchBar
import ai.equal.ds.components.SearchSection
import ai.equal.ds.components.SearchState as SearchStateView
import ai.equal.ds.components.SectionHeader
import ai.equal.ds.components.SegmentedChips
import ai.equal.ds.components.SelectionChatHeader
import ai.equal.ds.components.SettingsRow
import ai.equal.ds.components.SmallActionChip
import ai.equal.ds.components.SmallActionChipStyle
import ai.equal.ds.components.StatusBar
import ai.equal.ds.components.ThreadEvent
import ai.equal.ds.components.ThreadNotice
import ai.equal.ds.components.ToggleRow
import ai.equal.ds.components.TopBar
import ai.equal.ds.components.VoiceInputBar
import ai.equal.ds.components.VoiceNotePlayer
import ai.equal.ds.components.WhatsNewCard
import ai.equal.ds.components.MeetAssistantCard
import ai.equal.ds.components.OngoingCallBar
import ai.equal.ds.components.PermissionSheet
import ai.equal.ds.components.SetupPrompt
import ai.equal.ds.components.MeetAssistantLayout
import ai.equal.ds.components.SystemNotificationPreview
import ai.equal.ds.components.defaultContactInfoActions
import ai.equal.ds.icons.EqualIcons
import ai.equal.ds.theme.EqualTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.horizontalScroll
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.zIndex

private val SampleChips = listOf(
    ActionChipData("Call back", "📞"),
    ActionChipData("Update address", "✏️"),
    ActionChipData("Add to calendar", "🗓️"),
)

private val SampleLargeChips = listOf(
    ActionChipData("Reply on WhatsApp", "💬"),
    ActionChipData("Add to Calendar", "🗓️"),
)

/** What the chat task sheet shows as the user's request when the Reply on WhatsApp chip opens it. */
private const val ChatTaskRequest = "Reply on whatsapp"

private val LiveChips = listOf(
    ActionChipData("Takeover", "📞"),
    ActionChipData("Ask to reschedule", "🗓️"),
)

private val OngoingChips = listOf(
    ActionChipData("Call Back", "📞"),
    ActionChipData("Update Address", "✏️"),
)

/** Labelled catalog block. */
@Composable
private fun CatalogSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    SectionHeader(title = title, inset = 16.dp)
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        content = content,
    )
}

/**
 * Scrollable catalog of every Equal DS component, grouped and labelled. Wrap in [EqualTheme]
 * (the catalog does this itself when [wrapInTheme] is true).
 */
@Composable
fun EqualCatalog(
    modifier: Modifier = Modifier,
    wrapInTheme: Boolean = true,
) {
    if (wrapInTheme) {
        EqualTheme { CatalogContent(modifier) }
    } else {
        CatalogContent(modifier)
    }
}

@Composable
private fun CatalogContent(modifier: Modifier) {
    val colors = EqualTheme.colors
    Column(
        modifier
            .fillMaxSize()
            .background(colors.bgPrimary)
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
            .padding(bottom = 32.dp),
    ) {
        EqualText(
            text = "Equal AI design system",
            style = EqualTheme.type.headline,
            color = colors.textPrimary,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp),
        )

        // §1–3
        SectionHeader(title = "1 · Status bar / 2 · Top bar")
        StatusBar()
        TopBar()

        CatalogSection("3 · Assistant toggle + switch") {
            var on by remember { mutableStateOf(true) }
            AssistantToggleCard(checked = on, onCheckedChange = { on = it })
            AssistantToggleCard(checked = !on, onCheckedChange = { on = !it })
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                EqualSwitch(checked = on, onCheckedChange = { on = it })
                EqualSwitch(checked = !on, onCheckedChange = { on = !it })
                EqualCheckbox(checked = on, onCheckedChange = { on = it })
            }
        }

        CatalogSection("4 · Bottom navigation") {
            var tab by remember { mutableStateOf(EqualTab.Chats) }
            BottomNavBar(selectedTab = tab, onTabSelected = { tab = it }, onFabClick = {}, chatsBadgeCount = 3)
        }

        SectionHeader(title = "5 · Chat headers")
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ChatHeader(name = "Unknown caller", subtitle = "+91 98765 43210", onBackClick = {})
            ChatHeader(
                name = "Dr. Mehta Clinic",
                subtitle = "On call with Equal AI",
                subtitleIsPhone = false,
                variant = ChatHeaderVariant.LiveCall,
                onBackClick = {},
            )
            var all by remember { mutableStateOf(false) }
            SelectionChatHeader(
                selectedCount = 1,
                allSelected = all,
                onSelectAllChange = { all = it },
                onBackClick = {},
                onMarkReadClick = {},
                onDeleteClick = {},
            )
            EqualAppBar(title = "Addresses", onBackClick = {})
            EqualAppBar(
                title = "Active Instructions",
                onBackClick = {},
                actions = { EqualTextButton("Add", onClick = {}, leadingIcon = EqualIcons.Add) },
            )
            EqualAppBar(title = "App bar (start)", onBackClick = {}, align = AppBarAlign.Start, elevated = true)
        }

        SectionHeader(title = "Home header")
        var assistantOn by remember { mutableStateOf(true) }
        HomeHeader(assistantOn = assistantOn, onAssistantChange = { assistantOn = it })

        SectionHeader(title = "6 · Chat list item")
        ChatListItem(
            name = "Unknown caller",
            preview = "Asked for Arnav, didn't leave a message",
            date = "12:30",
            state = ChatListItemState.Unread,
            unreadCount = 2,
            chips = SampleChips,
            category = EqualCategory.NA,
        )
        EqualDivider()
        ChatListItem(
            name = "+91 98765 43210",
            preview = "Delivery partner called",
            date = "Yesterday",
            chips = SampleChips,
            category = EqualCategory.Delivery,
        )
        EqualDivider()
        ChatListItem(
            name = "HDFC Bank",
            preview = "Asked about a loan offer",
            date = "Mon",
            state = ChatListItemState.Selected,
            chips = SampleChips.take(2),
            category = EqualCategory.Financial,
        )
        EqualDivider()
        ChatListItem(
            name = "AX-SWIGGY",
            preview = "Your OTP is 4821",
            date = "Sun",
            category = EqualCategory.OTP,
        )
        EqualDivider()
        ChatListItem(name = "Priya Sharma", preview = "Saved contact (initials)", date = "Sat", initials = "PS")

        CatalogSection("Categories (${EqualCategory.entries.size})") {
            CategoryGallery()
        }

        CatalogSection("9 · Small action chips / 10 · Large action chips") {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SmallActionChip("Call back", "📞", onClick = {})
                SmallActionChip("Update address", "✏️", onClick = {}, style = SmallActionChipStyle.Read)
            }
            SmallActionChip("Call back", "📞", onClick = {}, style = SmallActionChipStyle.Outlined)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                LargeActionChip("Takeover", "📞", onClick = {})
                LargeActionChip("Reschedule", "🗓️", onClick = {})
            }
        }

        CatalogSection("11 · Chat bubbles") {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                ChatBubble("Hello, this is Equal AI answering for Arnav.", "03:31", ChatBubbleType.Outgoing, sender = "Equal AI")
                ChatBubble("How can I help?", "03:31", ChatBubbleType.Outgoing, sender = "Equal AI", isFirstInGroup = false)
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                ChatBubble("Calling about tomorrow's appointment.", "03:32", ChatBubbleType.Incoming, sender = "Dr. Mehta Clinic")
                ChatBubble("Is 4 PM still okay?", "03:32", ChatBubbleType.Incoming, isFirstInGroup = false)
            }
            ChatBubble("Want me to confirm the slot?", "03:35", ChatBubbleType.Assistant, sender = "Equal AI")
        }

        CatalogSection("11 · Voice note + call summary") {
            var playing by remember { mutableStateOf(false) }
            var speed by remember { mutableStateOf(PlaybackSpeed.X1) }
            CallSummaryBubble(
                summary = "Dr. Mehta Clinic called to confirm your appointment tomorrow at 4 PM.",
                timestamp = "03:36",
                voiceNote = {
                    VoiceNotePlayer(
                        isPlaying = playing,
                        onPlayPauseClick = { playing = !playing },
                        progress = 0.4f,
                        elapsed = "0:12",
                        total = "0:30",
                        speed = speed,
                        onSpeedChange = { speed = it },
                    )
                },
                onShareClick = {},
            )
        }

        CatalogSection("Brand · logo & mascot") {
            EqualLogo(height = 32.dp)
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp), verticalAlignment = Alignment.CenterVertically) {
                EqualLogo(height = 20.dp)
                EqualLogo(height = 20.dp, variant = EqualLogoVariant.WORDMARK)
                EqualMark(width = 23.dp, color = EqualTheme.colors.textAction)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp), verticalAlignment = Alignment.CenterVertically) {
                EqualMascot(size = 40.dp)
                EqualMascot(size = 64.dp)
                EqualMascot(size = 40.dp, dimmed = true)
            }
        }

        CatalogSection("12 · AI avatar (logo orb) / 16 · AI suggestion") {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                AiAvatar(size = AiAvatarSize.Small)
                AiAvatar(size = AiAvatarSize.Medium)
                AiAvatar(size = AiAvatarSize.Large)
                EqualLogoOrb(size = 58.dp)
            }
            AiSuggestion(text = "They sounded like a genuine clinic. Want me to confirm?")
        }

        SectionHeader(title = "12b · AI chat panel")
        var panelText by remember { mutableStateOf("") }
        AiChatPanel(
            prompt = "What would you like me to do about this call?",
            actions = SampleLargeChips,
            onActionClick = {},
            composerValue = panelText,
            onComposerValueChange = { panelText = it },
            onSend = { panelText = "" },
        )

        CatalogSection("13 · Feedback card / 14 · Action card") {
            var rating by remember { mutableIntStateOf(0) }
            FeedbackCard(
                question = "How did Equal AI handle this call?",
                rating = rating,
                onRatingChange = { rating = it },
                onShareFeedbackClick = {},
            )
            ActionCard(
                title = "WhatsApp · Amit Nanda",
                quote = "Can you send me the address for tomorrow?",
                timestamp = "10:02",
                actions = listOf(
                    ActionCardAction("Send address", emoji = "📤", onClick = {}),
                    ActionCardAction("Edit message", emoji = "✏️", onClick = {}),
                ),
            )
            ActionCard(
                title = "Dr. Mehta's Clinic Appointment",
                category = EqualCategory.Others,
                lines = listOf(
                    ActionCardLine("Tomorrow, 9:00 AM"),
                    ActionCardLine("Bring previous medical reports", ActionCardTone.Tertiary),
                ),
                timestamp = "03:21",
                actions = listOf(ActionCardAction("Confirm", emoji = "👍", onClick = {}), ActionCardAction("Edit", emoji = "✏️", onClick = {})),
            )
            ActionCard(
                title = "Instruction saved",
                icon = EqualIcons.Instructions,
                lines = instructionLines("Delivery agents call", "Ask them to leave parcels with security"),
                timestamp = "03:21",
                actions = listOf(ActionCardAction("Edit rule", emoji = "✏️", onClick = {})),
            )
            ActionCard(
                title = "WhatsApp · Amit Nanda",
                quote = "Running 10 minutes late.",
                timestamp = "10:05",
                actions = listOf(ActionCardAction("Reply", emoji = "💬", onClick = {})),
            )
        }

        CatalogSection("15 · Composer") {
            var empty by remember { mutableStateOf("") }
            VoiceInputBar(value = empty, onValueChange = { empty = it }, onSend = {})
            var typed by remember { mutableStateOf("Call them back at 5") }
            VoiceInputBar(value = typed, onValueChange = { typed = it }, onSend = { typed = "" })
        }

        CatalogSection("17 · Bottom sheet") {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(460.dp)
                    .clipToBounds(),
            ) {
                EqualBottomSheet(visible = true, onDismissRequest = {}) {
                    ConfirmSheetContent(
                        title = "Delete this chat?",
                        phone = "+91 98765 43210",
                        body = "The call recording and summary will be removed for good.",
                        confirmLabel = "Delete chat",
                        onConfirm = {},
                    )
                }
            }
        }

        CatalogSection("18 · Context menu") {
            ContextMenu(
                items = listOf(
                    ContextMenuItem("Mark read", EqualIcons.DoneAll, onClick = {}),
                    ContextMenuItem("Share", EqualIcons.Share, onClick = {}),
                    ContextMenuItem("Delete", EqualIcons.Delete, onClick = {}, destructive = true),
                ),
            )
        }

        CatalogSection("19 · Buttons") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                EqualButton("Small", onClick = {}, size = EqualButtonSize.Sm)
                EqualButton("Medium", onClick = {})
                EqualButton("Large", onClick = {}, size = EqualButtonSize.Lg)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                EqualButton("Secondary", onClick = {}, variant = EqualButtonVariant.Secondary)
                EqualButton("Delete", onClick = {}, variant = EqualButtonVariant.Danger, leadingIcon = EqualIcons.Delete)
                EqualButton("Disabled", onClick = {}, enabled = false)
            }
            EqualButton("Continue", onClick = {}, size = EqualButtonSize.Block, trailingIcon = EqualIcons.ChevronRight)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                EqualTextButton("Edit", onClick = {})
                EqualTextButton("Remove", onClick = {}, danger = true)
                EqualIconButton(EqualIcons.Close, "Close", onClick = {})
                EqualIconButton(EqualIcons.Check, "Confirm", onClick = {}, variant = EqualIconButtonVariant.Primary)
            }
        }

        CatalogSection("20 · Badges") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                NewBadge()
                CountBadge(3)
                CountBadge(42)
                FeatureBadge()
                PlanBadge()
            }
        }

        CatalogSection("21 · Call status indicators") {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                DayDivider("Today")
                ThreadEvent("Call Started 03:31", Modifier.padding(top = 8.dp))
                ThreadNotice("Call has ended. Equal AI shared a summary below.", Modifier.padding(top = 8.dp))
            }
        }

        CatalogSection("22 · What's new") {
            WhatsNewCard(
                title = "Language Preference",
                body = "Set your preferred language and choose how your assistant sounds",
                buttonLabel = "Try Voices",
                buttonIcon = EqualIcons.Translate,
                onButtonClick = {},
                onCloseClick = {},
            )
        }

        CatalogSection("Flow · temporarily unavailable") {
            var status by remember { mutableStateOf(SystemStatusState.Unavailable) }
            SystemStatusCard(state = status, onNotifyClick = { status = SystemStatusState.Notified })
            SystemNotificationPreview(title = "Equal AI Assistant is back on", body = "Equal AI is now handling all your unknown calls.")
            MeetAssistantCard(onCallClick = {}, onDismiss = {})
            MeetAssistantCard(onCallClick = {}, layout = MeetAssistantLayout.Banner, onDismiss = {})
        }

        SectionHeader(title = "Derived · settings")
        SettingsRow(label = "Call screening", description = "Choose who Equal AI answers", showNewBadge = true, onClick = {})
        var spam by remember { mutableStateOf(true) }
        ToggleRow(label = "Spam protection", checked = spam, onCheckedChange = { spam = it }, description = "Block known spam numbers")
        SectionHeader(title = "Call history")
        RowGroup {
            CallHistoryRow(CallDirection.Incoming, meta = "Today • 30 Sec.", time = "02:30 PM")
            CallHistoryRow(CallDirection.Outgoing, meta = "Today • 2 Min.", time = "11:02 AM")
            CallHistoryRow(CallDirection.Missed, meta = "Yesterday • 0 Sec.", time = "06:04 PM")
        }

        CatalogSection("Flows · cards") {
            AddressCard(
                label = "Home",
                address = formatAddress(SampleAddresses.first()),
                onEdit = {},
                onDelete = {},
            )
            InstructionCard(instruction = InstructionLibrary.declineSales, onEdit = {}, onDelete = {})
            PresetInstructionCard(instruction = InstructionLibrary.delivery, applied = false, onApply = {})
            PresetInstructionCard(instruction = InstructionLibrary.takeMessage, applied = true, onApply = {})
            Row(
                Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Recommendations.forEach { RecommendedCard(emoji = it.emoji, title = it.title, description = it.description, onAdd = {}) }
            }
            SuggestionsComingCard()
            Presets.forEach { PresetCard(emoji = it.emoji, tint = it.tint, title = it.title, description = it.description, onClick = {}) }
            InstructionPreview(
                title = InstructionLibrary.delivery.title,
                whenText = AnnotatedString(InstructionLibrary.delivery.whenText),
                action = fillTemplate(InstructionLibrary.delivery.doText, mapOf("landmark" to "the blue gate", "wait" to "5 minutes")),
                time = "03:21",
                confirmed = true,
            )
            Spacer(Modifier.height(16.dp))
            var gender by remember { mutableStateOf(Gender.Male) }
            GenderSelector(value = gender, onValueChange = { gender = it })
        }

        CatalogSection("You tab · plan card / AI summary / invite") {
            PlanCard(onManageClick = {})
            PlanCard(price = "Expired on 12 Aug", expired = true, onManageClick = {})
            AiSummaryCard(status = AiSummaryStatus.Empty)
            AiSummaryCard(status = AiSummaryStatus.Loading)
            AiSummaryCard(status = AiSummaryStatus.Ready, summary = SampleSummary)
            InviteCard(onShare = {}, modifier = Modifier.padding(top = 24.dp))
        }
        SectionHeader(title = "You tab · profile hero", inset = 16.dp)
        ProfileHero(name = "Keshav", phone = "+91 98789 56749", onEdit = {}) {
            PlanCard(onManageClick = {})
        }

        CatalogSection("Monetization") {
            PaywallHero {
                PriceTag(price = "249")
                LockedPreviewCard {
                    ChatListItem(
                        name = "Vijay NoBroker",
                        preview = "About miniature painting",
                        date = "Yesterday",
                        category = EqualCategory.RealEstate,
                        locked = true,
                    )
                }
                CouponTicket(code = "EQUAL25", description = "Save Flat ₹100 with this Code", discount = "25% OFF")
                CouponTicket(code = "EQUAL25", description = "Save Flat ₹100 with this Code", discount = "25% OFF", applied = true)
            }
            PaywallFooter(onClick = {})
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                LockBadge()
                LockedContent {
                    EqualText(text = "Hey this is vijay calling", style = EqualTheme.type.body1, color = colors.textPrimary)
                }
                UnlockAudioPill(onClick = {})
            }
            ChatBubble(type = ChatBubbleType.Incoming, timestamp = "03:21", message = "We have a 2BHK in Sector 12", locked = true)
            CallSummaryBubble(
                summary = "Hey Keshav! Here is the summary of your call.",
                timestamp = "03:21",
                locked = CallSummaryLock.All,
                voiceNote = {
                    VoiceNotePlayer(
                        isPlaying = false,
                        onPlayPauseClick = {},
                        progress = 0f,
                        elapsed = "0:00",
                        total = "2:37",
                        speed = PlaybackSpeed.X1,
                        onSpeedChange = {},
                    )
                },
            )
            UnlockDetailsCard(onUnlock = {})
            Column {
                PlanCard(onManageClick = {}, manageLabel = "Reactivate plan")
                PlanBanner(text = "Reactivate your current Plan to use Equal Unlimited", attached = true)
            }
            PlanBanner(text = "Reactivate your current Plan to keep using Equal Unlimited", onClick = {})
            DividerLabel(text = "Why people choose", tone = DividerLabelTone.Gold)
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                StatBlock(value = "400k+", label = "People never miss a call")
                Spacer(Modifier.height(16.dp))
                RatingPill(rating = 4.5f)
            }
            TestimonialCarousel(
                items = listOf(
                    Testimonial("It is a very useful app for screening the unknown calls.", "Arnav Raj", avatar = {
                        EqualAssetImage(EqualAssets.AvatarTestimonial1, 36.dp)
                    }),
                    Testimonial("My parents get loan calls all day. Equal answers them.", "Riya Kapoor", avatar = {
                        EqualAssetImage(EqualAssets.AvatarTestimonial2, 36.dp)
                    }),
                ),
            )
            FaqList(
                items = listOf(
                    FaqItem("Can I cancel my Equal Unlimited plan later?", "Yes, cancel anytime."),
                    FaqItem("Will Equal screen all types of calls?", "Yes, Equal handles them all."),
                ),
                defaultOpen = listOf(0),
            )
            PaymentStatus.entries.forEach { PaymentStatusSheet(status = it, inline = true) }
            HelpCenterSheet(inline = true)
            CancelStep.entries.forEach { CancelSheet(step = it, inline = true) }
            StatusCard(tone = StatusTone.Gold, art = EqualAssets.CrownGold, strong = "Congratulations", body = "StatusCard on its own.") {
                RedirectNote(seconds = 9)
            }
        }

        CatalogSection("Monetization · plan value & renewal") {
            PriceTag(price = "149", was = 249, badge = "Special Coupon Applied!")
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                DiscountBadge()
                DiscountBadge(label = "Special Coupon Applied!")
            }
            PlanExpiryBanner(expiry = PlanExpiry.Expiring(2))
            PlanExpiryBanner(expiry = PlanExpiry.Today)
            PlanExpiryBanner(expiry = PlanExpiry.Expired("30th July"))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ExpiryChip(text = "25th Jun ’26", tone = ExpiryTone.Active)
                ExpiryChip(text = "03 Days", tone = ExpiryTone.Warning)
                ExpiryChip(text = "Today", tone = ExpiryTone.Danger)
            }
            ValueSheet(expiry = PlanExpiry.Expiring(3), inline = true)
            ValueSheet(expiry = PlanExpiry.Today, variant = ValueSheetVariant.Flush, inline = true)
            ValueCard(calls = 20, title = null, perks = null, expiry = PlanExpiry.Active("25th Jun ’26"))
            QuotaCard(quota = Quota(used = 24, limit = 30, resetsInDays = 4))
            QuotaCard(quota = Quota(used = 30, limit = 30, resetsInDays = 2))
            FeatureList()
            PlanCard(onManageClick = {}, expired = true)
            EqualButton(text = "Cancel Subscription", onClick = {}, variant = EqualButtonVariant.GoldOutline, size = EqualButtonSize.Block)
        }

        CatalogSection("Derived · profile") {
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp), verticalAlignment = Alignment.Bottom) {
                ProfileAvatar(variant = ProfileAvatarVariant.Male, width = 70.dp)
                ProfileAvatar(variant = ProfileAvatarVariant.Female, width = 70.dp)
                ProfileAvatar(variant = ProfileAvatarVariant.Male, width = 24.dp)
                ProfileAvatar(variant = ProfileAvatarVariant.Female, width = 24.dp)
            }
            ProfileHeader(name = "Arnav Raj", phone = "+91 98765 43210")
            ProfileHeader(name = "Riya Kapoor", phone = "+91 98765 01234", avatar = ProfileAvatarVariant.Female)
            ContactInfoHeader(
                name = "Unknown caller",
                phone = "+91 91234 56789",
                actions = defaultContactInfoActions({}, {}, {}, {}),
            )
        }

        CatalogSection("Derived · inputs") {
            var name by remember { mutableStateOf("") }
            EqualTextField(value = name, onValueChange = { name = it }, label = "Full name", placeholder = "Enter your name")
            var landmark by remember { mutableStateOf("") }
            EqualTextField(
                value = landmark,
                onValueChange = { landmark = it },
                label = "Landmark",
                optional = true,
                placeholder = "Near the park",
                helper = AnnotatedString("Helps delivery partners find you"),
            )
            var pin by remember { mutableStateOf("5600") }
            EqualTextField(value = pin, onValueChange = { pin = it }, label = "Pincode", error = "Enter a 6-digit pincode")
            var query by remember { mutableStateOf("") }
            SearchField(value = query, onValueChange = { query = it })
            var seg by remember { mutableIntStateOf(0) }
            SegmentedChips(options = listOf("Home", "Work", "Other"), selectedIndex = seg, onSelectedIndexChange = { seg = it })
        }

        CatalogSection("Search") {
            var q by remember { mutableStateOf("Sa") }
            SearchBar(value = q, onValueChange = { q = it })
            SearchSection(title = "Recent searches", onClear = {})
            RecentSearches(items = SampleRecentSearches)
            MessageResult(name = "Santosh Anna", time = "3:02 PM", text = "Caller said that they\u2019ll call you in the evening", query = q)
            LoadingDots()
            SearchStateView(art = EqualAssets.IllustrationNoResults, size = 110.dp, title = "No results found", body = "Couldn\u2019t find anything matching. Try a different name, number, category or keyword")
        }

        CatalogSection("Derived · dialog, empty state, snackbar") {
            EqualDialogContent(
                title = "Delete instruction?",
                subject = "When a delivery partner calls",
                body = "Equal AI will stop following this instruction.",
                confirmLabel = "Delete",
                onConfirm = {},
                destructive = true,
            )
            EmptyState(
                title = "No chats yet",
                body = "Calls Equal AI answers will show up here.",
                illustration = { EqualAssetImage(EqualAssets.IllustrationTrash, 96.dp) },
                actionLabel = "Set up assistant",
            )
            EqualSnackbar(message = "Chat deleted", actionLabel = "Undo")
        }
    }
}

/**
 * One sample chat row on [HomeScreenSample]. [initials] marks a saved contact (initials avatar
 * with the person badge); otherwise the [category] art is shown. [id] matches [AssistantResult].
 */
class HomeSampleChat(
    val name: String,
    val preview: String,
    val date: String,
    val category: EqualCategory,
    val unread: Int = 0,
    val chips: List<ActionChipData> = emptyList(),
    val initials: String? = null,
    val id: String = name,
)

/** The default Home rows. */
val HomeSampleChats: List<HomeSampleChat> = listOf(
    HomeSampleChat("Unknown caller", "Asked for Arnav, didn't leave a message", "12:30", EqualCategory.NA, unread = 2, chips = SampleChips),
    HomeSampleChat("+91 98765 43210", "Delivery partner called about a parcel", "11:04", EqualCategory.Delivery, unread = 1, chips = SampleChips.take(1)),
    HomeSampleChat("HDFC Bank", "Asked about a loan offer", "Yesterday", EqualCategory.Financial, chips = SampleChips.drop(1)),
    HomeSampleChat("AX-SWIGGY", "Your OTP is 4821", "Mon", EqualCategory.OTP),
    HomeSampleChat("Vijay · NoBroker", "Offer on a 2BHK flat in Sector 12", "10 July", EqualCategory.RealEstate),
    HomeSampleChat("Swiggy Delivery", "Your order is at the gate", "09 July", EqualCategory.Delivery),
    HomeSampleChat("Amazon", "OTP for your delivery is 4821", "07 July", EqualCategory.OTP),
)

/** Id of the saved contact the Global MYA sample acts on (Amit Nanda). */
const val MyaChatId = "amit-nanda"

/** Global MYA: the saved contact Amit Nanda first (initials AN), as in the design, then the default rows. */
val MyaSampleChats: List<HomeSampleChat> = listOf(
    HomeSampleChat("Amit Nanda", "Amit called to remind you about\u2026", "12 July", EqualCategory.NA, initials = "AN", id = MyaChatId),
) + HomeSampleChats

/** What the assistant did, shown on the chat it acted on ([chatId]): [preview] replaces the row's preview, unread 0. */
data class AssistantResult(val chatId: String, val preview: String)

/** The Global MYA result: Amit Nanda's row reads "Reminder added". */
val MyaReminderResult: AssistantResult = AssistantResult(chatId = MyaChatId, preview = "Reminder added")

/**
 * Home screen composed only from DS components: status bar + home header, the chat rows and the
 * floating bottom navigation. Monetization knobs (port of web HomeScreen): [tier] (the plan badge
 * shows only for [PlanTier.Paid]; also the avatar ring), [lockedFrom] (free plan: rows from this
 * index on are locked and call [onLockedChat]). The header is the logo + plan badge and the
 * assistant toggle; the "Search for names" field sits first in the list and opens search ([onSearch]).
 *
 * Plan value & renewal: [planExpiry] puts the [PlanExpiryBanner] first under the header (Renew
 * Now → [onRenew]); with it, [valueSheet] opens the "What Unlimited saved you" [ValueSheet]
 * ([ValueSheetVariant.Card] or the [ValueSheetVariant.Flush] experiment) over the screen, closed
 * by the floating close or the scrim; Renew your Equal AI → [onRenew]. Free plan: [quotaSheet]
 * opens the weekly quota [ValueSheet] ("6 out of 30 Calls Left" / "Limit Reached") the same way;
 * Get Equal AI Unlimited → [onRenew] (open the paywall).
 *
 * Global MYA: the nav FAB (green orb) opens the live [AssistantSheet]; when it finishes, the
 * chat it acted on shows [MyaReminderResult]. [assistant] pins the sheet at a stage (docs);
 * [assistantResult] shows a result up front. [chats] sets the rows (e.g. [MyaSampleChats]); use
 * [MeetAssistantCarousel] as the [promo] for the MYA banner.
 *
 * Call Handling for new users (Known Calls Flow): pass `chats = emptyList()` (the search field
 * hides with no chats), [MeetAssistantHero] as the [promo] and [assistantDescription]; then
 * [callSetup] draws the "Set up your calls" [SetupPrompt] takeover or the "Finish your setup?"
 * [PermissionSheet]; Start / Finish call [onCallSetup] (open [CallHandlingScreen]).
 *
 * Monetization experiments (web `HomeScreen`): [planExpired] (red PLAN EXPIRED badge),
 * [assistantOff] (the toggle starts Off: usage cap), [assistantArt] (Component 25), [homeNotice]
 * (Plan Expired card or the weekly [UsageCapCard]), [official] (Equal AI's own verified chat on top
 * with its 👑 chip; tapping the row calls [onOfficialChat]), [lockedActions] (every row's chips
 * crown-locked), [paymentProcessing] (the "Payment processing" card under the toggle),
 * [paywallSheet] (locked taps open the [PaywallSheetFlow] instead of [onLockedChat]),
 * [paywallSheetStage] (pins the sheet flow open at a stage) and [offerStrip] (the "Go unlimited
 * for ₹149" strip; also shown once the exit offer is declined; Claim Now reopens the sheet with
 * EQUAL25 applied). The notice CTAs and the official chip open the sheet when it is on, otherwise
 * they call [onRenew].
 */
@Composable
fun HomeScreenSample(
    modifier: Modifier = Modifier,
    /** Temporarily Unavailable design: a non-null state swaps the assistant toggle for [SystemStatusCard]. */
    systemStatus: SystemStatusState? = null,
    onNotifyClick: () -> Unit = {},
    /** Promo card above the list (e.g. [MeetAssistantCard]); hidden while [systemStatus] is set. */
    promo: (@Composable () -> Unit)? = null,
    /** Free plan: rows from this index on are locked (blurred preview, crown-lock). */
    lockedFrom: Int? = null,
    /** The sample user is on Unlimited, so the screen defaults to the paid (gold) avatar and badge. */
    tier: PlanTier = PlanTier.Paid,
    avatar: ProfileAvatarVariant = ProfileAvatarVariant.Male,
    onSearch: () -> Unit = {},
    /** Tapping a locked row (the chat-list touchpoint, opens the paywall). */
    onLockedChat: () -> Unit = {},
    /** The strip under the header when the plan ends soon or has ended. */
    planExpiry: PlanExpiry.Ending? = null,
    /** Show the value sheet (after a call near the end of the plan); needs [planExpiry]. */
    valueSheet: ValueSheetVariant? = null,
    /** Renew Now / Renew your Equal AI / Get Equal AI Unlimited. */
    onRenew: () -> Unit = {},
    /** Free plan: show the weekly quota sheet; its CTA calls [onRenew]. */
    quotaSheet: Quota? = null,
    /** The chat rows. */
    chats: List<HomeSampleChat> = HomeSampleChats,
    /** Global MYA: pin the assistant sheet at a stage (docs). Otherwise the green orb opens it live. */
    assistant: MyaStage? = null,
    /** Global MYA done: the chat the assistant acted on shows what it did. */
    assistantResult: AssistantResult? = null,
    /** Call Handling for new users: the "Set up your calls" takeover, or the "Finish your setup?" sheet. */
    callSetup: CallSetupOverlay? = null,
    /** Start / Finish on [callSetup]: open [CallHandlingScreen]. */
    onCallSetup: () -> Unit = {},
    /** "Skip, answer everyone" on the Finish sheet. */
    onSkipCallSetup: () -> Unit = {},
    /** The assistant toggle's line (new users: "Equal is managing your calls!"). */
    assistantDescription: String? = null,
    /** Red PLAN EXPIRED badge beside the logo. */
    planExpired: Boolean = false,
    /** The assistant toggle starts Off (usage cap reached). */
    assistantOff: Boolean = false,
    /** Toggle art experiment (Component 25). */
    assistantArt: AssistantToggleArt = AssistantToggleArt.Robot,
    /** Card under the header: Plan Expired (option 2) or the weekly usage meter (Usage Cap option 2). */
    homeNotice: HomeNotice? = null,
    /** Equal AI's own chat on top with its notice (Plan Expired / Usage Cap option 1). */
    official: OfficialNotice? = null,
    onOfficialChat: () -> Unit = {},
    /** Actions touchpoint: every row's action chips are crown-locked. */
    lockedActions: Boolean = false,
    /** "Payment processing" card under the toggle (the user closed Payment Pending). */
    paymentProcessing: Boolean = false,
    /** Sheet paywall experiment: locked taps open the sheet. */
    paywallSheet: Boolean = false,
    /** Pins the sheet paywall open at a stage (docs); implies [paywallSheet]. */
    paywallSheetStage: PaywallSheetStage? = null,
    /** "Go unlimited for ₹149 with EQUAL25 · Claim Now" strip (shown after the exit offer is declined). */
    offerStrip: Boolean = false,
) {
    val colors = EqualTheme.colors
    var assistantOn by remember(assistantOff) { mutableStateOf(!assistantOff) }
    var pw by remember(paywallSheetStage) { mutableStateOf(paywallSheetStage) }
    var offer by remember(offerStrip) { mutableStateOf(offerStrip) }
    val sheetOn = paywallSheet || paywallSheetStage != null
    val openPaywall = { if (sheetOn) pw = PaywallSheetStage.Sheet else onLockedChat() }
    val renew = { if (sheetOn) pw = PaywallSheetStage.Sheet else onRenew() }
    var tab by remember { mutableStateOf(EqualTab.Chats) }
    var sheetOpen by remember(valueSheet, quotaSheet) { mutableStateOf(valueSheet != null || quotaSheet != null) }
    var myaOpen by remember { mutableStateOf(false) }
    var result by remember(assistantResult) { mutableStateOf(assistantResult) }
    Box(
        modifier
            .fillMaxSize()
            .background(colors.bgPrimary),
    ) {
        Column(Modifier.fillMaxSize()) {
            HomeHeader(
                assistantOn = assistantOn,
                onAssistantChange = { assistantOn = it },
                modifier = Modifier.zIndex(1f),
                planLabel = when {
                    planExpired -> "Plan Expired"
                    tier == PlanTier.Paid -> "Unlimited"
                    else -> null
                },
                status = systemStatus?.let { state -> { SystemStatusCard(state = state, onNotifyClick = onNotifyClick) } },
                assistantDescription = assistantDescription,
                planTone = if (planExpired) PlanBadgeTone.Expired else PlanBadgeTone.Paid,
                assistantArt = assistantArt,
                below = if (paymentProcessing) ({ ProcessingStrip(variant = ProcessingStripVariant.Card) }) else null,
            )
            Column(
                Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 120.dp),
            ) {
                if (planExpiry != null) PlanExpiryBanner(expiry = planExpiry, onRenew = onRenew)
                if (offer) OfferStrip(onClick = { pw = PaywallSheetStage.Claimed })
                when (homeNotice) {
                    HomeNotice.PlanExpired -> HomeNoticeCard(
                        lead = { EmojiGlyph(emoji = "👑", size = 14.dp) },
                        title = "Your Plan Expired",
                        subtitle = "Renew to get Unlimited access back",
                        cta = "Renew Now",
                        onCta = renew,
                    )
                    is HomeNotice.UsageCap -> UsageCapCard(left = homeNotice.callsLeft, limit = homeNotice.limit, onUpgrade = renew)
                    null -> Unit
                }
                if (chats.isNotEmpty()) {
                    SearchField(
                        value = "",
                        onValueChange = {},
                        onActivate = onSearch,
                        modifier = Modifier.padding(start = 16.dp, top = 12.dp, end = 16.dp),
                    )
                }
                if (promo != null && systemStatus == null) {
                    Box(Modifier.padding(start = 12.dp, top = 12.dp, end = 12.dp, bottom = 4.dp)) { promo() }
                }
                if (official != null) {
                    ChatListItem(
                        name = "Equal AI",
                        preview = official.preview,
                        date = "Now",
                        state = ChatListItemState.Unread,
                        unreadCount = 1,
                        chips = listOf(ActionChipData(official.chip, "👑")),
                        onChipClick = { renew() },
                        onClick = onOfficialChat,
                        official = true,
                    )
                    if (chats.isNotEmpty()) EqualDivider()
                }
                chats.forEachIndexed { i, chat ->
                    if (i > 0) EqualDivider()
                    val locked = lockedFrom != null && i >= lockedFrom
                    val acted = result?.takeIf { it.chatId == chat.id }
                    val unread = if (acted != null) 0 else chat.unread
                    ChatListItem(
                        name = chat.name,
                        preview = acted?.preview ?: chat.preview,
                        date = chat.date,
                        state = if (unread > 0) ChatListItemState.Unread else ChatListItemState.Read,
                        unreadCount = unread,
                        chips = chat.chips,
                        category = chat.category,
                        initials = chat.initials,
                        locked = locked,
                        onClick = if (locked) openPaywall else ({}),
                        lockedActions = lockedActions,
                        onLockedAction = openPaywall,
                    )
                }
            }
        }
        BottomNavBar(
            selectedTab = tab,
            onTabSelected = { tab = it },
            onFabClick = { myaOpen = true },
            chatsBadgeCount = 3,
            avatar = avatar,
            tier = tier,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 16.dp),
        )
        val stage = pw
        if (stage != null) {
            PaywallSheetFlow(stage = stage, onClose = { pw = null }, onDecline = { offer = true })
        } else if (callSetup == CallSetupOverlay.Prompt) {
            SetupPrompt(onStart = onCallSetup)
        } else if (callSetup == CallSetupOverlay.Finish) {
            PermissionSheet(
                art = EqualAssets.CallHandlingAll,
                title = "Finish your setup?",
                body = "You chose all calls. One thing is left.\nTell me who should always ring you.",
                cta = "Finish",
                onAllow = onCallSetup,
                onClose = {},
                secondary = "Skip, answer everyone",
                onSecondary = onSkipCallSetup,
            )
        } else if (assistant != null) {
            AssistantSheet(stage = assistant)
        } else if (myaOpen) {
            AssistantSheet(
                onClose = { myaOpen = false },
                onDone = {
                    myaOpen = false
                    result = MyaReminderResult
                },
            )
        } else if (sheetOpen && quotaSheet != null) {
            ValueSheet(
                quota = quotaSheet,
                onClose = { sheetOpen = false },
                onRenew = onRenew,
            )
        } else if (sheetOpen && planExpiry != null && valueSheet != null) {
            ValueSheet(
                expiry = planExpiry,
                variant = valueSheet,
                onClose = { sheetOpen = false },
                onRenew = onRenew,
            )
        }
    }
}

/** Call Handling overlay on [HomeScreenSample] for a new user. */
enum class CallSetupOverlay {
    /** The dark "Set up your calls" takeover (Start). */
    Prompt,

    /** The user stopped halfway: "Finish your setup?" with Finish or Skip, answer everyone. */
    Finish,
}

/** The new-user home promo: [MeetAssistantCard] in [MeetAssistantLayout.Hero], "Talk With Your Assistant". */
@Composable
fun MeetAssistantHero(
    modifier: Modifier = Modifier,
    onCallClick: () -> Unit = {},
) {
    MeetAssistantCard(
        onCallClick = onCallClick,
        modifier = modifier,
        layout = MeetAssistantLayout.Hero,
        title = "Talk With Your Assistant",
        body = "Explore the app, check out features, ask questions, or just say \"Hi\"!",
    )
}

/** Monetization lock on [ChatScreenSample]. */
enum class ChatLock {
    /** Paid: nothing locked. */
    None,

    /** Free plan: every bubble blurred, the recording locked and the AI panel replaced by [UnlockDetailsCard]. */
    Details,

    /** Free plan: only the call summary recording is locked (Unlock Audio). */
    Audio,

    /** Plan lapsed: readable, with the Reactivate [PlanBanner] under the header. */
    Lapsed,
}

/**
 * Chat screen composed only from DS components: header, day divider, bubbles, call summary,
 * feedback card and the AI chat panel. [lock] adds the monetization touchpoints (see [ChatLock]);
 * every unlock (card, Unlock Audio, Reactivate banner) calls [onUnlock].
 *
 * [name] / [category] / [initials] / [subtitle] set the caller; [live] swaps the header to the
 * live call (Takeover pill, "You have a visitor") with Takeover / Ask to reschedule. Known call in
 * progress (Known-call notifications design): [ongoing] (e.g. "02:34") adds the
 * [OngoingCallBar] under the header and the assistant's follow-up ("Our appointment is all
 * set…") with Call Back / Update Address.
 *
 * [ChatLock.Details] shows the gold [LockedRecording] in the call summary and the gold
 * [UnlockDetailsCard]. Monetization experiments (web `ChatScreen`): [official] turns the screen
 * into Equal AI's own conversation (orb header, the [AssistantNoticeCard]; its CTA calls
 * [onUnlock], or opens the sheet when [paywallSheet] is on); [summaryActions] adds Call Back / Add
 * to Calendar ([SummaryActions], green) under the summary; [paymentProcessing] puts the
 * [ProcessingStrip] under the header; [paywallSheet] makes every unlock open the
 * [PaywallSheetFlow] (chat labels) and [paywallSheetStage] pins it open.
 *
 * Global MYA in a chat (web `ChatScreen aiTask`): the AI panel asks "Want me to send <first name>
 * a quick reply on WhatsApp?" (first name for saved contacts with [initials], else the full
 * [name]); its Reply on WhatsApp chip, or sending a message from its composer,
 * opens the [ChatTaskSheet] (live). [aiTask] pins the sheet open at a stage (docs).
 */
@Composable
fun ChatScreenSample(
    modifier: Modifier = Modifier,
    lock: ChatLock = ChatLock.None,
    onUnlock: () -> Unit = {},
    name: String = "Dr. Mehta Clinic",
    subtitle: String = "+91 98765 43210",
    category: EqualCategory = EqualCategory.HomeServices,
    initials: String? = null,
    live: Boolean = false,
    ongoing: String? = null,
    onTakeover: () -> Unit = {},
    /** Equal AI's own conversation with its notice (Plan Expired / Usage Cap option 1). */
    official: OfficialNotice? = null,
    /** Call Back / Add to Calendar rows under the call summary. */
    summaryActions: Boolean = false,
    /** "Payment processing" strip under the header. */
    paymentProcessing: Boolean = false,
    /** Sheet paywall experiment: every unlock opens the sheet. */
    paywallSheet: Boolean = false,
    /** Pins the sheet paywall open at a stage (docs); implies [paywallSheet]. */
    paywallSheetStage: PaywallSheetStage? = null,
    /** Pins the chat's AI task sheet ("Replying <first name> on WhatsApp") open at a stage (docs). */
    aiTask: ChatTaskStage? = null,
    onBack: () -> Unit = {},
) {
    var pw by remember(paywallSheetStage) { mutableStateOf(paywallSheetStage) }
    var task by remember(aiTask) { mutableStateOf(if (aiTask != null) ChatTaskRequest else null) }
    // Saved contacts (initials) go by their first name; businesses keep the full name.
    val first = if (initials != null) name.substringBefore(' ') else name
    val sheetOn = paywallSheet || paywallSheetStage != null
    val unlock = { if (sheetOn) pw = PaywallSheetStage.Sheet else onUnlock() }
    Box(modifier.fillMaxSize()) {
        if (official != null) {
            OfficialChat(notice = official, onBack = onBack, onCta = unlock)
        } else {
            ChatThread(
                lock = lock,
                onUnlock = unlock,
                name = name,
                subtitle = subtitle,
                category = category,
                initials = initials,
                live = live,
                ongoing = ongoing,
                onTakeover = onTakeover,
                summaryActions = summaryActions,
                paymentProcessing = paymentProcessing,
                firstName = first,
                onTask = { task = it },
                onBack = onBack,
            )
        }
        val stage = pw
        val request = task
        if (stage != null) {
            PaywallSheetFlow(stage = stage, context = PaywallSheetContext.Chat, onClose = { pw = null })
        } else if (request != null) {
            ChatTaskSheet(contact = first, request = request, stage = aiTask, onClose = { task = null })
        }
    }
}

/** Equal AI's own conversation (web `ChatScreen official`). */
@Composable
private fun OfficialChat(notice: OfficialNotice, onBack: () -> Unit, onCta: () -> Unit) {
    val colors = EqualTheme.colors
    Column(
        Modifier
            .fillMaxSize()
            .background(colors.bgPrimary),
    ) {
        StatusBar(background = colors.bgSurface)
        ChatHeader(
            name = "Equal AI",
            subtitle = "+91-81720 33604",
            onBackClick = onBack,
            official = true,
            modifier = Modifier.zIndex(1f),
        )
        ChatWallpaper(
            Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { DayDivider("Today") }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AiAvatar(size = AiAvatarSize.Small)
                    AssistantNoticeCard(title = notice.title, body = notice.body, cta = notice.cta, onCta = onCta)
                }
            }
        }
    }
}

@Composable
private fun ChatThread(
    lock: ChatLock,
    onUnlock: () -> Unit,
    name: String,
    subtitle: String,
    category: EqualCategory,
    initials: String?,
    live: Boolean,
    ongoing: String?,
    onTakeover: () -> Unit,
    summaryActions: Boolean,
    paymentProcessing: Boolean,
    firstName: String,
    onTask: (String) -> Unit,
    onBack: () -> Unit,
) {
    val modifier = Modifier
    val colors = EqualTheme.colors
    var rating by remember { mutableIntStateOf(0) }
    var playing by remember { mutableStateOf(false) }
    var speed by remember { mutableStateOf(PlaybackSpeed.X1) }
    var message by remember { mutableStateOf("") }
    val locked = lock == ChatLock.Details
    Column(
        modifier
            .fillMaxSize()
            .background(colors.bgPrimary),
    ) {
        StatusBar(background = colors.bgSurface)
        Column(Modifier.zIndex(1f)) {
            ChatHeader(
                name = name,
                subtitle = if (live) "You have a visitor" else subtitle,
                subtitleIsPhone = !live,
                onBackClick = onBack,
                category = category,
                initials = initials,
                variant = if (live) ChatHeaderVariant.LiveCall else ChatHeaderVariant.Default,
                onTakeoverClick = onTakeover,
            )
            if (ongoing != null) {
                OngoingCallBar(elapsed = ongoing)
            } else if (paymentProcessing) {
                ProcessingStrip()
            } else if (lock == ChatLock.Lapsed) {
                PlanBanner(text = "Reactivate your current Plan to keep using Equal Unlimited", onClick = onUnlock)
            }
        }
        ChatWallpaper(
            Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { DayDivider("Today") }
                Box(Modifier.fillMaxWidth().padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                    ThreadEvent("Call Started 03:31")
                }
                ChatBubble(
                    "Hello, this is Equal AI answering for Arnav. How can I help?",
                    "03:31",
                    ChatBubbleType.Outgoing,
                    sender = "Equal AI",
                    locked = locked,
                )
                Box(Modifier.padding(top = 8.dp)) {
                    ChatBubble(
                        "Hi, I'm calling from Dr. Mehta Clinic about tomorrow's appointment.",
                        "03:32",
                        ChatBubbleType.Incoming,
                        sender = "Dr. Mehta Clinic",
                        locked = locked,
                    )
                }
                ChatBubble("Is 4 PM still okay?", "03:32", ChatBubbleType.Incoming, isFirstInGroup = false, locked = locked)
                Box(Modifier.padding(top = 8.dp)) {
                    ChatBubble("I'll let him know and confirm by message.", "03:33", ChatBubbleType.Outgoing, sender = "Equal AI", locked = locked)
                }
                Box(Modifier.fillMaxWidth().padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                    ThreadEvent("Call Ended 03:34")
                }
                val recording: @Composable () -> Unit = {
                    VoiceNotePlayer(
                        isPlaying = playing,
                        onPlayPauseClick = { playing = !playing },
                        progress = 0.35f,
                        elapsed = "0:10",
                        total = "0:30",
                        speed = speed,
                        onSpeedChange = { speed = it },
                    )
                }
                if (locked || summaryActions) {
                    // Recordings locked: the gold crown-locked recording; with actions, Call Back / Add to Calendar.
                    CallSummaryBubble(
                        summary = "Dr. Mehta Clinic called to confirm your appointment tomorrow at 4 PM.",
                        timestamp = "03:36",
                        voiceNote = recording,
                        recording = if (locked) ({ LockedRecording(onUnlock = onUnlock) }) else null,
                        footer = if (summaryActions) ({ SummaryActions(actions = SampleSummaryActions) }) else null,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                } else {
                    CallSummaryBubble(
                        summary = "Dr. Mehta Clinic called to confirm your appointment tomorrow at 4 PM.",
                        timestamp = "03:36",
                        voiceNote = recording,
                        onShareClick = {},
                        locked = if (lock == ChatLock.Audio) CallSummaryLock.Audio else null,
                        onUnlock = onUnlock,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
                FeedbackCard(
                    question = "How did Equal AI handle this call?",
                    rating = rating,
                    onRatingChange = { rating = it },
                    onShareFeedbackClick = {},
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
        if (locked) {
            UnlockDetailsCard(
                onUnlock = onUnlock,
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(start = 12.dp, end = 12.dp, bottom = 12.dp),
            )
        } else {
            AiChatPanel(
                prompt = if (ongoing != null) "Our appointment is all set. Want me to help with anything else?" else "Want me to send $firstName a quick reply on WhatsApp?",
                actions = when {
                    ongoing != null -> OngoingChips
                    live -> LiveChips
                    else -> SampleLargeChips
                },
                onActionClick = { action -> if (ongoing == null && !live && action.label == "Reply on WhatsApp") onTask(ChatTaskRequest) },
                composerValue = message,
                onComposerValueChange = { message = it },
                onSend = {
                    val text = message.trim()
                    message = ""
                    if (ongoing == null && !live && text.isNotEmpty()) onTask(text)
                },
                modifier = Modifier.navigationBarsPadding(),
            )
        }
    }
}

private const val SampleSummary =
    "Your family calls you a lot. They always say it's urgent. They almost never say why. " +
        "Everyone wants you, not me. You keep me on anyway, and let me be the one who asks."

/**
 * You tab (the You (Settings) design) composed only from DS components: status bar, profile hero
 * with the plan card, assistant / about-you / settings groups, invite card and the bottom nav
 * with You active. [summary] picks the AI summary state; [plan] the plan card link ([onManagePlan])
 * and, for [YouPlan.Reactivate], the attached Reactivate [PlanBanner]; [YouPlan.Expired] shows the
 * filled "Plan Expired" pill. [tier] drives the avatar ring and plan pill (pass [PlanTier.Free]
 * with [YouPlan.Restore] and [YouPlan.Expired]). The first assistant row, Call Handling ("Choose
 * which calls I answer", New), calls [onOpenCallHandling] (open [CallHandlingScreen]).
 */
@Composable
fun YouScreenSample(
    modifier: Modifier = Modifier,
    summary: AiSummaryStatus = AiSummaryStatus.Ready,
    avatar: ProfileAvatarVariant = ProfileAvatarVariant.Male,
    tier: PlanTier = PlanTier.Paid,
    plan: YouPlan = YouPlan.Active,
    onManagePlan: () -> Unit = {},
    onOpenCallHandling: () -> Unit = {},
) {
    val colors = EqualTheme.colors
    var assistantOn by remember { mutableStateOf(true) }
    var tab by remember { mutableStateOf(EqualTab.You) }
    Box(
        modifier
            .fillMaxSize()
            .background(colors.bgPrimary),
    ) {
        Column(Modifier.fillMaxSize()) {
            // The status bar sits on the gold hero in both themes.
            StatusBar(background = colors.profileHeroStatusBg, color = colors.profileHeroStatusText)
            Column(
                Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 120.dp),
            ) {
                ProfileHero(
                    name = "Keshav",
                    phone = "+91 98789 56749",
                    avatar = avatar,
                    plan = if (tier == PlanTier.Paid) "Unlimited" else null,
                    tier = tier,
                    onEdit = {},
                ) {
                    PlanCard(onManageClick = onManagePlan, manageLabel = plan.manageLabel, expired = plan == YouPlan.Expired)
                    if (plan == YouPlan.Reactivate) {
                        PlanBanner(text = "Reactivate your current Plan to use Equal Unlimited", attached = true)
                    }
                }

                SectionHeader(title = "Your assistant")
                AssistantToggleCard(
                    checked = assistantOn,
                    onCheckedChange = { assistantOn = it },
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 12.dp),
                )
                RowGroup {
                    SettingsRow(
                        label = "Call Handling",
                        description = "Choose which calls I answer",
                        leadingIcon = EqualIcons.Call,
                        showNewBadge = true,
                        onClick = onOpenCallHandling,
                    )
                    SettingsRow(
                        label = "Instructions",
                        description = "Tell assistant how you'd like it to help",
                        leadingIcon = EqualIcons.Instructions,
                        showNewBadge = true,
                        onClick = {},
                    )
                    SettingsRow(
                        label = "Assistant Health Check",
                        description = "Check Real-Time Assistant Connectivity",
                        leadingIcon = EqualIcons.HealthCheck,
                        onClick = {},
                    )
                    SettingsRow(
                        label = "Silent Mode",
                        description = "Mute promotional calls automatically",
                        leadingIcon = EqualIcons.SilentMode,
                        onClick = {},
                    )
                    SettingsRow(
                        label = "Voice & Language",
                        description = "Choose assistant's voice and languages",
                        leadingIcon = EqualIcons.VoiceLanguage,
                        onClick = {},
                    )
                }

                SectionHeader(title = "About you")
                AiSummaryCard(
                    status = summary,
                    summary = SampleSummary,
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 12.dp),
                )
                RowGroup {
                    SettingsRow(
                        label = "Your Addresses",
                        description = "Add addresses your assistant should know",
                        leadingIcon = EqualIcons.AddressPin,
                        showNewBadge = true,
                        onClick = {},
                    )
                }

                SectionHeader(title = "Settings & support")
                RowGroup {
                    SettingsRow(
                        label = "WhatsApp Updates",
                        description = "Assistant and App Updates",
                        leadingIcon = EqualIcons.WhatsApp,
                        onClick = {},
                    )
                    SettingsRow(
                        label = "Account Data",
                        description = "Your account and data management",
                        leadingIcon = EqualIcons.AccountData,
                        onClick = {},
                    )
                    SettingsRow(
                        label = "Help Center",
                        description = "Your What, Why and How",
                        leadingIcon = EqualIcons.HelpCenter,
                        onClick = {},
                    )
                }

                // Web: 12 top padding + 24 card margin, so the high-five art has room to rise.
                InviteCard(onShare = {}, modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 36.dp, bottom = 24.dp))
            }
        }
        BottomNavBar(
            selectedTab = tab,
            onTabSelected = { tab = it },
            onFabClick = {},
            chatsBadgeCount = 2,
            avatar = avatar,
            tier = tier,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 16.dp),
        )
    }
}

/** Plan state on [YouScreenSample]; picks the plan card link. */
enum class YouPlan(internal val manageLabel: String) {
    /** Plan running: Manage plan. */
    Active("Manage plan"),

    /** Plan ended: Restore Plan (use with [PlanTier.Free]). */
    Restore("Restore Plan"),

    /** Autopay off: Reactivate plan + the attached Reactivate banner under the card. */
    Reactivate("Reactivate plan"),

    /** Plan ended: the red "Plan Expired" pill; Manage plan opens the renewal page (use with [PlanTier.Free]). */
    Expired("Manage plan"),
}

/** All [EqualCategory] images at 48dp with their labels, in a wrapping grid. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoryGallery(modifier: Modifier = Modifier) {
    val colors = EqualTheme.colors
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        EqualCategory.entries.forEach { category ->
            Column(Modifier.width(72.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                CategoryAvatar(category = category, size = 48.dp)
                EqualText(
                    text = category.name,
                    style = EqualTheme.type.caption,
                    color = colors.textSecondary,
                    maxLines = 1,
                    modifier = Modifier.padding(top = 4.dp),
                )
                EqualText(text = category.label, style = EqualTheme.type.captionRegular, color = colors.textTertiary, maxLines = 1)
            }
        }
    }
}

@EqualPreviews
@Composable
private fun CategoryGalleryPreview() {
    EqualTheme {
        Box(Modifier.background(EqualTheme.colors.bgPrimary).padding(16.dp)) { CategoryGallery() }
    }
}

/** The orb at every size. The glow overflows each 24/36/48/58 box. */
@Composable
private fun OrbRow() {
    Row(
        Modifier
            .background(EqualTheme.colors.bgPrimary)
            .padding(24.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        EqualLogoOrb(size = 24.dp)
        EqualLogoOrb(size = 36.dp)
        EqualLogoOrb(size = 48.dp)
        EqualLogoOrb(size = 58.dp)
    }
}

@Preview(name = "Orb · light", showBackground = true)
@Composable
private fun OrbLightPreview() {
    EqualTheme(darkTheme = false) { OrbRow() }
}

@Preview(name = "Orb · dark", showBackground = true)
@Composable
private fun OrbDarkPreview() {
    EqualTheme(darkTheme = true) { OrbRow() }
}

@EqualPreviews
@Composable
private fun CatalogPreview() {
    EqualCatalog()
}

@Preview(name = "You · empty", widthDp = EqualFrameWidthDp, heightDp = 1500, showBackground = true)
@Composable
private fun YouScreenEmptyPreview() {
    EqualTheme(darkTheme = false) { YouScreenSample(summary = AiSummaryStatus.Empty) }
}

@Preview(name = "You · loading", widthDp = EqualFrameWidthDp, heightDp = 1500, showBackground = true)
@Composable
private fun YouScreenLoadingPreview() {
    EqualTheme(darkTheme = false) { YouScreenSample(summary = AiSummaryStatus.Loading) }
}

@Preview(name = "You · ready", widthDp = EqualFrameWidthDp, heightDp = 1500, showBackground = true)
@Composable
private fun YouScreenReadyPreview() {
    EqualTheme(darkTheme = false) { YouScreenSample(summary = AiSummaryStatus.Ready, avatar = ProfileAvatarVariant.Female) }
}

@Preview(name = "You · ready · dark", widthDp = EqualFrameWidthDp, heightDp = 1500, showBackground = true)
@Composable
private fun YouScreenReadyDarkPreview() {
    EqualTheme(darkTheme = true) { YouScreenSample(summary = AiSummaryStatus.Ready) }
}

@Preview(name = "Home · light", widthDp = EqualFrameWidthDp, heightDp = EqualFrameHeightDp, showBackground = true)
@Composable
private fun HomeScreenLightPreview() {
    EqualTheme(darkTheme = false) { HomeScreenSample() }
}

@Preview(name = "Home · dark", widthDp = EqualFrameWidthDp, heightDp = EqualFrameHeightDp, showBackground = true)
@Composable
private fun HomeScreenDarkPreview() {
    EqualTheme(darkTheme = true) { HomeScreenSample() }
}

@Preview(name = "Chat · light", widthDp = EqualFrameWidthDp, heightDp = EqualFrameHeightDp, showBackground = true)
@Composable
private fun ChatScreenLightPreview() {
    EqualTheme(darkTheme = false) { ChatScreenSample() }
}

@Preview(name = "Chat · dark", widthDp = EqualFrameWidthDp, heightDp = EqualFrameHeightDp, showBackground = true)
@Composable
private fun ChatScreenDarkPreview() {
    EqualTheme(darkTheme = true) { ChatScreenSample() }
}
