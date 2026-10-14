package ai.equal.ds.catalog

import ai.equal.ds.assets.EqualAssets
import ai.equal.ds.components.ActionChipData
import ai.equal.ds.components.AssistantToggleArt
import ai.equal.ds.components.CategoryAvatar
import ai.equal.ds.components.ExitOfferDialog
import ai.equal.ds.components.HelpFaqSheet
import ai.equal.ds.components.LockedRecording
import ai.equal.ds.components.OfferStrip
import ai.equal.ds.components.PaymentResult
import ai.equal.ds.components.PaymentResultStatus
import ai.equal.ds.components.PaywallSheet
import ai.equal.ds.components.PendingAction
import ai.equal.ds.components.ProcessingStrip
import ai.equal.ds.components.ProcessingStripVariant
import ai.equal.ds.components.SampleSummaryActions
import ai.equal.ds.components.SummaryActions
import ai.equal.ds.components.SocialProof
import androidx.compose.ui.unit.Dp
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.graphics.Color
import ai.equal.ds.assets.EqualCategory
import ai.equal.ds.components.AiAvatar
import ai.equal.ds.components.AiAvatarSize
import ai.equal.ds.components.AiSummaryStatus
import ai.equal.ds.components.CallSummaryBubble
import ai.equal.ds.components.CallSummaryLock
import ai.equal.ds.components.CancelSheet
import ai.equal.ds.components.CancelStep
import ai.equal.ds.components.ChatBubble
import ai.equal.ds.components.ChatBubbleType
import ai.equal.ds.components.ChatListItem
import ai.equal.ds.components.CouponTicket
import ai.equal.ds.components.DividerLabel
import ai.equal.ds.components.EqualButtonVariant
import ai.equal.ds.components.EqualDivider
import ai.equal.ds.components.FaqList
import ai.equal.ds.components.HelpCenterSheet
import ai.equal.ds.components.LockedPreviewCard
import ai.equal.ds.components.PaymentStatus
import ai.equal.ds.components.PaymentStatusSheet
import ai.equal.ds.components.PaywallFooter
import ai.equal.ds.components.PaywallHero
import ai.equal.ds.components.PlanExpiry
import ai.equal.ds.components.Quota
import ai.equal.ds.components.PlanTier
import ai.equal.ds.components.FeatureList
import ai.equal.ds.components.ValueCard
import ai.equal.ds.components.ValueSheetVariant
import ai.equal.ds.components.PlaybackSpeed
import ai.equal.ds.components.PriceTag
import ai.equal.ds.components.StatusBar
import ai.equal.ds.components.VoiceNotePlayer
import ai.equal.ds.theme.EqualTheme
import ai.equal.ds.tokens.EqualMotion
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay

// Monetization screens (Monetization Flow design), composed only from DS components. Port of
// web/src/screens/MonetizationScreens.tsx. The Android team runs experiments on these, so every
// screen takes the experiment knobs as parameters (price, coupon, copy, entry touchpoint) instead
// of hard-coding one variant. Pass a pinned state (overlay / cancelStep) to show one frame; leave
// it null to run the live flow. The Home / Chat / You touchpoints live on the revamped samples in
// Catalog.kt: HomeScreenSample(tier, lockedFrom, onLockedChat), ChatScreenSample(lock = ChatLock.*)
// and YouScreenSample(plan = YouPlan.*).

// =============================================================================================
// Scaffold
// =============================================================================================

/**
 * Frame of the monetization screens (web `Screen`): surface fill, [header] (status bar…), a
 * scrolling body, an optional [footer] (above the navigation bar) and an [overlay] (status sheets)
 * drawn over everything.
 */
@Composable
private fun MonetizationScaffold(
    modifier: Modifier,
    header: @Composable () -> Unit,
    footer: (@Composable () -> Unit)? = null,
    overlay: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Box(
        modifier
            .fillMaxSize()
            .background(EqualTheme.colors.bgSurface),
    ) {
        Column(Modifier.fillMaxSize()) {
            Box(Modifier.zIndex(1f)) { header() }
            Box(Modifier.weight(1f).fillMaxWidth()) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    content = content,
                )
            }
            if (footer != null) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .zIndex(1f)
                        .navigationBarsPadding(),
                ) { footer() }
            }
        }
        overlay?.invoke()
    }
}

/** Assistant row of a message list (web `ChatBubble kind="assistant" bare`): 28dp avatar column + 8 + [content]. */
@Composable
private fun AssistantRow(modifier: Modifier = Modifier, content: @Composable RowScope.() -> Unit) {
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(Modifier.width(28.dp), contentAlignment = Alignment.TopCenter) { AiAvatar(size = AiAvatarSize.Small) }
        content()
    }
}

@Composable
private fun SampleRecording() {
    VoiceNotePlayer(
        isPlaying = false,
        onPlayPauseClick = {},
        progress = 0f,
        elapsed = "0:00",
        total = "2:37",
        speed = PlaybackSpeed.X1,
        onSpeedChange = {},
    )
}

// =============================================================================================
// Paywall
// =============================================================================================

/** Where the user entered the paywall from; picks the "what you're missing" preview. */
enum class PaywallTouchpoint {
    ChatList,
    Chat,
    Audio,

    /** Actions on Home: a row with crown-locked action chips. */
    Actions,

    /** Recordings + Transcripts Locked: the locked summary ([LockedSummaryPreview]). */
    Recordings,

    /** The locked summary with its locked Call Back / Add to Calendar rows. */
    RecordingsActions,
}

/** Coupon experiment: no coupon, coupon offered, or already applied. */
enum class PaywallCoupon { None, Available, Applied }

/**
 * Overlay pinned on [PaywallScreen] (docs). [None] pins the bare paywall; the others pin a
 * [PaymentStatusSheet] or the [HelpCenterSheet].
 */
enum class PaywallOverlay { None, Waiting, Confirmed, Pending, Failed, Welcome, Help }

private fun PaywallOverlay.paymentStatus(): PaymentStatus? = when (this) {
    PaywallOverlay.Waiting -> PaymentStatus.Waiting
    PaywallOverlay.Confirmed -> PaymentStatus.Confirmed
    PaywallOverlay.Pending -> PaymentStatus.Pending
    PaywallOverlay.Failed -> PaymentStatus.Failed
    PaywallOverlay.Welcome -> PaymentStatus.Welcome
    PaywallOverlay.None, PaywallOverlay.Help -> null
}

/**
 * The locked call summary the experiments preview (web `LockedSummaryPreview`): a 24dp category
 * avatar and a full-width [CallSummaryBubble] with the gold [LockedRecording] in its `recording`
 * slot, plus, with [actions], the crown-locked [SummaryActions] in its `footer`.
 */
@Composable
fun LockedSummaryPreview(
    modifier: Modifier = Modifier,
    actions: Boolean = false,
) {
    Row(modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        CategoryAvatar(category = EqualCategory.HomeServices, size = 24.dp)
        CallSummaryBubble(
            summary = "Amit invited you to dinner this Sunday, 8 PM at his place. He asked you to confirm by tonight.",
            timestamp = null,
            width = Dp.Unspecified,
            modifier = Modifier.weight(1f),
            recording = { LockedRecording() },
            footer = if (actions) ({ SummaryActions(actions = SampleSummaryActions, locked = true) }) else null,
        )
    }
}

/** The preview card content for each [touchpoint] (web `PaywallPreview`). */
@Composable
fun PaywallPreview(
    touchpoint: PaywallTouchpoint,
    modifier: Modifier = Modifier,
) {
    when (touchpoint) {
        PaywallTouchpoint.Actions -> LockedPreviewCard(modifier) {
            ChatListItem(
                name = "Dr. Mehta Clinic",
                preview = "Amit invited you to dinner this Sunday",
                date = "Yesterday",
                category = EqualCategory.NA,
                chips = listOf(ActionChipData("Call Back", "📞"), ActionChipData("Update Address", "✏️")),
                lockedActions = true,
            )
        }
        PaywallTouchpoint.Recordings, PaywallTouchpoint.RecordingsActions -> LockedPreviewCard(modifier) {
            LockedSummaryPreview(actions = touchpoint == PaywallTouchpoint.RecordingsActions)
        }
        PaywallTouchpoint.ChatList -> LockedPreviewCard(modifier) {
            Column {
                ChatListItem(name = "HDFC RM", preview = "Loan pre-approval request", date = "1:00 PM", category = EqualCategory.GateAlert)
                EqualDivider()
                ChatListItem(
                    name = "Vijay NoBroker",
                    preview = "About miniature painting",
                    date = "Yesterday",
                    category = EqualCategory.RealEstate,
                    locked = true,
                )
            }
        }
        PaywallTouchpoint.Chat -> LockedPreviewCard(modifier) {
            Column(
                Modifier.padding(horizontal = 4.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                ChatBubble(message = "Hello, who is this?", timestamp = "03:21", type = ChatBubbleType.Outgoing)
                ChatBubble(
                    message = "Vijay from no broker",
                    timestamp = "03:21",
                    type = ChatBubbleType.Incoming,
                    incomingCategory = EqualCategory.RealEstate,
                    modifier = Modifier.padding(top = 8.dp),
                )
                ChatBubble(
                    message = "Hey this is vijay calling from magicbrick a property advisor",
                    timestamp = "03:21",
                    type = ChatBubbleType.Incoming,
                    incomingCategory = EqualCategory.RealEstate,
                    isFirstInGroup = false,
                    locked = true,
                )
            }
        }
        PaywallTouchpoint.Audio -> LockedPreviewCard(modifier) {
            Column(
                Modifier.padding(horizontal = 4.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                ChatBubble(message = "Hi Arnav, Called you regarding…", timestamp = "03:21", type = ChatBubbleType.Outgoing, locked = true)
                AssistantRow(Modifier.padding(top = 8.dp)) {
                    CallSummaryBubble(
                        summary = "Hey Keshav! Here is the summary of your call. Hi Arnav, Called you regarding the property visit.",
                        timestamp = "03:22",
                        voiceNote = { SampleRecording() },
                        locked = CallSummaryLock.Audio,
                        modifier = Modifier.width(250.dp),
                    )
                }
            }
        }
    }
}

/**
 * The Equal Unlimited paywall: hero (crown, title, [subtitle]), [PriceTag], the [touchpoint]
 * preview, the EQUAL25 coupon, FAQ (4th open) and the sticky gold Unlock Unlimited.
 *
 * Experiments: [price] / [discountedPrice], [coupon], [subtitle], [touchpoint], [waitingArt]
 * (coin or cash), [cta] ("Unlock Unlimited"; "Get Equal AI Unlimited" when renewing). APPLY runs
 * the price drop: [PriceTag] strikes [price], lands [discountedPrice] with "Saved ₹N" and pops the
 * "Special Coupon Applied!" badge (animated only on the tap; [PaywallCoupon.Applied] opens with
 * the final state).
 *
 * [overlay] null runs the flow: Unlock → Waiting → (2.5s) Confirmed → (5s redirect) Welcome →
 * (5s redirect) [onDone]; Support / Contact Support open the Help Center; Retry restarts the
 * payment. A non-null [overlay] pins that frame and never navigates.
 *
 * [processing] (a payment already in flight: the user closed Payment Pending): the footer shows a
 * toast [ProcessingStrip] above a disabled "Unlock Unlimited", so nobody pays twice.
 */
@Composable
fun PaywallScreen(
    modifier: Modifier = Modifier,
    touchpoint: PaywallTouchpoint = PaywallTouchpoint.ChatList,
    price: Int = 249,
    discountedPrice: Int = 149,
    coupon: PaywallCoupon = PaywallCoupon.Available,
    subtitle: PaywallSubtitle = PaywallSubtitle.Unlock,
    overlay: PaywallOverlay? = null,
    waitingArt: EqualAssets = EqualAssets.StatusWaiting,
    cta: String = "Unlock Unlimited",
    /** A payment is already in flight: toast above a disabled CTA. */
    processing: Boolean = false,
    onBack: () -> Unit = {},
    onDone: () -> Unit = {},
) {
    var applied by remember(coupon) { mutableStateOf(coupon == PaywallCoupon.Applied) }
    var justApplied by remember(coupon) { mutableStateOf(false) }
    var live by remember { mutableStateOf(PaywallOverlay.None) }
    val pinned = overlay != null
    val state = overlay ?: live
    LaunchedEffect(pinned, live) {
        if (!pinned && live == PaywallOverlay.Waiting) {
            delay(EqualMotion.paymentDelay.toLong())
            live = PaywallOverlay.Confirmed
        }
    }
    MonetizationScaffold(
        modifier = modifier,
        header = { StatusBar(background = EqualTheme.colors.paywallHeroTop) },
        footer = {
            if (processing) {
                PaywallFooter(
                    onClick = {},
                    disabled = true,
                    above = { ProcessingStrip(variant = ProcessingStripVariant.Toast) },
                )
            } else {
                PaywallFooter(onClick = { live = PaywallOverlay.Waiting }, cta = cta)
            }
        },
        overlay = when {
            state == PaywallOverlay.Help -> {
                { HelpCenterSheet() }
            }
            state.paymentStatus() != null -> {
                {
                    // Keyed so each status restarts its redirect countdown.
                    key(state) { PaymentStatusSheet(
                        status = state.paymentStatus()!!,
                        waitingArt = waitingArt,
                        onRetry = { live = PaywallOverlay.Waiting },
                        onContactSupport = { live = PaywallOverlay.Help },
                        onRedirect = {
                            if (!pinned) {
                                if (state == PaywallOverlay.Confirmed) {
                                    live = PaywallOverlay.Welcome
                                } else {
                                    live = PaywallOverlay.None
                                    onDone()
                                }
                            }
                        },
                    ) }
                }
            }
            else -> null
        },
    ) {
        PaywallHero(subtitle = subtitle.copy, onBack = onBack, onSupport = { live = PaywallOverlay.Help }) {
            if (applied) {
                PriceTag(price = discountedPrice.toString(), was = price, badge = "Special Coupon Applied!", animate = justApplied)
            } else {
                PriceTag(price = price.toString())
            }
            PaywallPreview(touchpoint = touchpoint)
            if (coupon != PaywallCoupon.None) {
                CouponTicket(
                    code = "EQUAL25",
                    description = "Save Flat ₹${price - discountedPrice} with this Code",
                    discount = "25% OFF",
                    applied = applied,
                    onApply = {
                        applied = true
                        justApplied = true
                    },
                )
            }
        }
        FaqList(
            items = PaywallFaq,
            defaultOpen = listOf(3),
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 24.dp),
        )
    }
}

// =============================================================================================
// Subscription management
// =============================================================================================

/** [SubscriptionScreen] plan state. */
enum class SubscriptionState {
    /** Plan running; Cancel Subscription (gold outline) pinned at the bottom. */
    Active,

    /** Cancelled: access until the date, pinned Re-Subscribe. */
    Cancelled,

    /** The plan ended: the value card shows "Plan Expired on", pinned Renew your Equal AI (→ the paywall). */
    Expired,
}

/**
 * Manage plan (Monetization value design): the gold hero "Equal AI / Unlimited", What
 * Unlimited Saved You ([ValueCard] with [calls], no heading or perks; "Plan active until
 * [activeUntil]" or "Plan Expired on [expiredOn]"), What You Get ([FeatureList]), the social proof
 * ([SocialProof]: Why people choose EQUAL UNLIMITED, 400k+, the rating, testimonials), FAQ
 * (4th open), and the plan action pinned at the bottom:
 * - [SubscriptionState.Active]: Cancel Subscription ([EqualButtonVariant.GoldOutline]) + "Cancel
 *   Anytime. Automatically renews";
 * - [SubscriptionState.Cancelled]: gold Re-Subscribe;
 * - [SubscriptionState.Expired]: gold Renew your Equal AI → [onRenew] (open the paywall with
 *   `cta = "Get Equal AI Unlimited"`).
 *
 * [cancelStep] null runs the cancellation: Cancel Subscription → feedback (Proceed once there is a
 * reason) → are you sure (Cancel goes back) → submitted → (3s redirect) [SubscriptionState.Cancelled]
 * with Re-Subscribe. A non-null [cancelStep] pins that sheet (seeded with [reason]) and never
 * navigates.
 */
@Composable
fun SubscriptionScreen(
    modifier: Modifier = Modifier,
    state: SubscriptionState = SubscriptionState.Active,
    activeUntil: String = "25th Jun ’26",
    /** [SubscriptionState.Expired]: the date the plan ended. */
    expiredOn: String = "30th July",
    /** Calls the assistant handled in the plan (the value card). */
    calls: Int = 20,
    cancelStep: CancelStep? = null,
    /** Pre-fills the feedback (docs: "Proceed enabled"). */
    reason: String? = null,
    onBack: () -> Unit = {},
    /** [SubscriptionState.Expired]: Renew your Equal AI. */
    onRenew: () -> Unit = {},
) {
    val colors = EqualTheme.colors
    var step by remember { mutableStateOf<CancelStep?>(null) }
    var cancelled by remember(state) { mutableStateOf(state == SubscriptionState.Cancelled) }
    var text by remember { mutableStateOf(reason ?: "") }
    val pinned = cancelStep != null
    val current = cancelStep ?: step
    val expired = state == SubscriptionState.Expired
    MonetizationScaffold(
        modifier = modifier,
        header = { StatusBar(background = colors.paywallHeroTop) },
        footer = {
            when {
                expired -> PaywallFooter(onClick = onRenew, cta = "Renew your Equal AI")
                cancelled -> PaywallFooter(onClick = { cancelled = false }, cta = "Re-Subscribe")
                else -> PaywallFooter(
                    onClick = { step = CancelStep.Feedback },
                    cta = "Cancel Subscription",
                    variant = EqualButtonVariant.GoldOutline,
                )
            }
        },
        overlay = current?.let { s ->
            {
                CancelSheet(
                    step = s,
                    reason = if (pinned) reason else text,
                    onReasonChange = if (pinned) null else ({ text = it }),
                    onProceed = { step = CancelStep.Confirm },
                    onKeep = { step = null },
                    onConfirm = { step = CancelStep.Submitted },
                    onRedirect = {
                        if (!pinned) {
                            step = null
                            cancelled = true
                        }
                    },
                )
            }
        },
    ) {
        PaywallHero(title = "Equal AI", subtitle = PaywallSubtitle.Unlock.copy, onBack = onBack) {
            DividerLabel(text = "What Unlimited Saved You")
            ValueCard(
                calls = calls,
                title = null,
                perks = null,
                expiry = if (expired) PlanExpiry.Expired(expiredOn) else PlanExpiry.Active(activeUntil),
            )
            DividerLabel(text = "What You Get")
            FeatureList()
        }
        SocialProof(testimonials = PaywallTestimonials)
        FaqList(
            items = PaywallFaq,
            defaultOpen = listOf(3),
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 24.dp),
        )
    }
}

// =============================================================================================
// Sheet paywall + exit offer (experiment)
// =============================================================================================

/** Where the sheet paywall is in its little flow (web `PaywallSheetStage`). */
enum class PaywallSheetStage {
    /** The [PaywallSheet] at ₹249 with EQUAL25 · APPLY. */
    Sheet,

    /** The [HelpFaqSheet] (from Support; back returns). */
    Help,

    /** "Leave Without Unlocking?" ([ExitOfferDialog], from tapping outside). */
    Exit,

    /** The sheet again at ₹149 with APPLIED (after Claim). */
    Claimed,

    /** A payment in flight: toast above a disabled CTA. */
    Processing,
}

/** Where [PaywallSheetFlow] opened; picks the exit offer labels. */
enum class PaywallSheetContext {
    /** Not Now / Claim 25% Off. */
    Home,

    /** Leave / Keep 25% Off. */
    Chat,
}

/**
 * The sheet paywall over any screen, as a live flow (web `PaywallSheetFlow`): Support → Help
 * Center (back returns; tapping outside asks to leave), tapping outside the sheet → "Leave Without
 * Unlocking?"; Claim → the sheet with EQUAL25 applied (outside again goes back to the plain
 * sheet); dismiss → [onDecline] then [onClose] (home then shows the [OfferStrip]). [stage] seeds
 * the flow (and re-seeds it when it changes). Draw it as the last child of a full-size Box.
 */
@Composable
fun PaywallSheetFlow(
    modifier: Modifier = Modifier,
    stage: PaywallSheetStage = PaywallSheetStage.Sheet,
    context: PaywallSheetContext = PaywallSheetContext.Home,
    onClose: () -> Unit = {},
    onDecline: () -> Unit = {},
    onUnlock: () -> Unit = {},
) {
    var live by remember(stage) { mutableStateOf(stage) }
    when (val s = live) {
        PaywallSheetStage.Help -> HelpFaqSheet(
            items = PaywallFaq,
            modifier = modifier,
            onBack = { live = PaywallSheetStage.Sheet },
            onScrim = { live = PaywallSheetStage.Exit },
        )
        PaywallSheetStage.Exit -> ExitOfferDialog(
            modifier = modifier,
            dismissLabel = if (context == PaywallSheetContext.Chat) "Leave" else "Not Now",
            claimLabel = if (context == PaywallSheetContext.Chat) "Keep 25% Off" else "Claim 25% Off",
            onDismiss = {
                onDecline()
                onClose()
            },
            onClaim = { live = PaywallSheetStage.Claimed },
        )
        else -> key(s == PaywallSheetStage.Claimed) {
            PaywallSheet(
                modifier = modifier,
                applied = s == PaywallSheetStage.Claimed,
                processing = s == PaywallSheetStage.Processing,
                onApply = { live = PaywallSheetStage.Claimed },
                onSupport = { live = PaywallSheetStage.Help },
                onScrim = { live = if (s == PaywallSheetStage.Claimed) PaywallSheetStage.Sheet else PaywallSheetStage.Exit },
                onUnlock = onUnlock,
            )
        }
    }
}

// =============================================================================================
// Payment results (full screen, experiment)
// =============================================================================================

/**
 * All Payment States as screens (web `PaymentResultScreen`): [PaymentResult] with the status bar
 * on the wash. Live: Refresh Status → Congratulations, Retry payment → Payment Pending. With
 * [onDone], Congratulations calls it after `motion.paymentDelay` (on to home).
 */
@Composable
fun PaymentResultScreen(
    status: PaymentResultStatus,
    modifier: Modifier = Modifier,
    pendingAction: PendingAction = PendingAction.Refresh,
    onClose: () -> Unit = {},
    onBack: () -> Unit = {},
    onContactSupport: () -> Unit = {},
    onDone: (() -> Unit)? = null,
) {
    var s by remember(status) { mutableStateOf(status) }
    val done by rememberUpdatedState(onDone)
    LaunchedEffect(s, onDone != null) {
        if (s == PaymentResultStatus.Success && done != null) {
            delay(EqualMotion.paymentDelay.toLong())
            done?.invoke()
        }
    }
    Box(
        modifier
            .fillMaxSize()
            .background(EqualTheme.colors.bgSurface),
    ) {
        PaymentResult(
            status = s,
            header = { StatusBar(background = Color.Transparent) },
            pendingAction = pendingAction,
            onClose = onClose,
            onBack = onBack,
            onRefresh = { s = PaymentResultStatus.Success },
            onRetry = { s = PaymentResultStatus.Pending },
            onContactSupport = onContactSupport,
        )
    }
}

// =============================================================================================
// Previews (every state, light + dark)
// =============================================================================================

@FlowPreviews @Composable private fun HomeFreePreview() = EqualTheme {
    HomeScreenSample(tier = PlanTier.Free, lockedFrom = 4)
}
@FlowPreviews @Composable private fun ChatDetailsLockedPreview() = EqualTheme { ChatScreenSample(lock = ChatLock.Details) }
@FlowPreviews @Composable private fun ChatAudioLockedPreview() = EqualTheme { ChatScreenSample(lock = ChatLock.Audio) }
@FlowPreviews @Composable private fun ChatLapsedPreview() = EqualTheme { ChatScreenSample(lock = ChatLock.Lapsed) }
@FlowPreviews @Composable private fun PaywallChatListPreview() = EqualTheme { PaywallScreen(touchpoint = PaywallTouchpoint.ChatList) }
@FlowPreviews @Composable private fun PaywallChatPreview() = EqualTheme { PaywallScreen(touchpoint = PaywallTouchpoint.Chat) }
@FlowPreviews @Composable private fun PaywallAudioPreview() = EqualTheme {
    PaywallScreen(touchpoint = PaywallTouchpoint.Audio, coupon = PaywallCoupon.Applied, subtitle = PaywallSubtitle.Get)
}
@FlowPreviews @Composable private fun PaywallWaitingPreview() = EqualTheme { PaywallScreen(overlay = PaywallOverlay.Waiting) }
@FlowPreviews @Composable private fun PaywallConfirmedPreview() = EqualTheme { PaywallScreen(overlay = PaywallOverlay.Confirmed) }
@FlowPreviews @Composable private fun PaywallPendingPreview() = EqualTheme { PaywallScreen(overlay = PaywallOverlay.Pending) }
@FlowPreviews @Composable private fun PaywallFailedPreview() = EqualTheme { PaywallScreen(overlay = PaywallOverlay.Failed) }
@FlowPreviews @Composable private fun PaywallHelpPreview() = EqualTheme { PaywallScreen(overlay = PaywallOverlay.Help) }
@FlowPreviews @Composable private fun PaywallWelcomePreview() = EqualTheme { PaywallScreen(overlay = PaywallOverlay.Welcome) }
@FlowPreviews @Composable private fun YouActivePreview() = EqualTheme { YouScreenSample(summary = AiSummaryStatus.Ready, plan = YouPlan.Active) }
@FlowPreviews @Composable private fun YouRestorePreview() = EqualTheme {
    YouScreenSample(summary = AiSummaryStatus.Ready, tier = PlanTier.Free, plan = YouPlan.Restore)
}
@FlowPreviews @Composable private fun YouReactivatePreview() = EqualTheme { YouScreenSample(summary = AiSummaryStatus.Ready, plan = YouPlan.Reactivate) }
@FlowPreviews @Composable private fun SubscriptionPreview() = EqualTheme { SubscriptionScreen() }
@FlowPreviews @Composable private fun SubscriptionFeedbackPreview() = EqualTheme { SubscriptionScreen(cancelStep = CancelStep.Feedback) }
@FlowPreviews @Composable private fun SubscriptionFeedbackFilledPreview() = EqualTheme {
    SubscriptionScreen(cancelStep = CancelStep.Feedback, reason = "Felt it too expensive, I hope it would be under my budget")
}
@FlowPreviews @Composable private fun SubscriptionConfirmPreview() = EqualTheme { SubscriptionScreen(cancelStep = CancelStep.Confirm) }
@FlowPreviews @Composable private fun SubscriptionSubmittedPreview() = EqualTheme { SubscriptionScreen(cancelStep = CancelStep.Submitted) }
@FlowPreviews @Composable private fun SubscriptionCancelledPreview() = EqualTheme { SubscriptionScreen(state = SubscriptionState.Cancelled) }
@FlowPreviews @Composable private fun SubscriptionExpiredPreview() = EqualTheme { SubscriptionScreen(state = SubscriptionState.Expired) }
@FlowPreviews @Composable private fun HomeExpiringPreview() = EqualTheme { HomeScreenSample(planExpiry = PlanExpiry.Expiring(2)) }
@FlowPreviews @Composable private fun HomeExpiresTodayPreview() = EqualTheme { HomeScreenSample(planExpiry = PlanExpiry.Today) }
@FlowPreviews @Composable private fun HomeExpiredPreview() = EqualTheme {
    HomeScreenSample(tier = PlanTier.Free, planExpiry = PlanExpiry.Expired("30th July"))
}
@FlowPreviews @Composable private fun HomeValueSheetPreview() = EqualTheme {
    HomeScreenSample(planExpiry = PlanExpiry.Expiring(3), valueSheet = ValueSheetVariant.Card)
}
@FlowPreviews @Composable private fun HomeValueSheetFlushPreview() = EqualTheme {
    HomeScreenSample(planExpiry = PlanExpiry.Expiring(3), valueSheet = ValueSheetVariant.Flush)
}
@FlowPreviews @Composable private fun HomeQuotaLeftPreview() = EqualTheme {
    HomeScreenSample(tier = PlanTier.Free, quotaSheet = Quota(used = 24, limit = 30, resetsInDays = 4))
}
@FlowPreviews @Composable private fun HomeQuotaLimitPreview() = EqualTheme {
    HomeScreenSample(tier = PlanTier.Free, lockedFrom = 4, quotaSheet = Quota(used = 30, limit = 30, resetsInDays = 2))
}
@FlowPreviews @Composable private fun YouExpiredPreview() = EqualTheme { YouScreenSample(tier = PlanTier.Free, plan = YouPlan.Expired) }
@FlowPreviews @Composable private fun RenewPaywallPreview() = EqualTheme { PaywallScreen(cta = "Get Equal AI Unlimited") }

// Monetization touchpoints & sheet paywall (experiments)
@FlowPreviews @Composable private fun TouchExpiredHomePreview() = EqualTheme {
    HomeScreenSample(tier = PlanTier.Free, planExpired = true, official = OfficialNotice.PlanExpired)
}
@FlowPreviews @Composable private fun TouchExpiredChatPreview() = EqualTheme { ChatScreenSample(official = OfficialNotice.PlanExpired) }
@FlowPreviews @Composable private fun TouchExpiredCardPreview() = EqualTheme {
    HomeScreenSample(tier = PlanTier.Free, planExpired = true, homeNotice = HomeNotice.PlanExpired)
}
@FlowPreviews @Composable private fun TouchCapCardPreview() = EqualTheme {
    HomeScreenSample(tier = PlanTier.Free, assistantOff = true, homeNotice = HomeNotice.UsageCap(callsLeft = 7, limit = 30))
}
@FlowPreviews @Composable private fun TouchActionsHomePreview() = EqualTheme {
    HomeScreenSample(tier = PlanTier.Free, lockedActions = true, paywallSheet = true)
}
@FlowPreviews @Composable private fun TouchActionsPaywallPreview() = EqualTheme {
    PaywallScreen(touchpoint = PaywallTouchpoint.Actions, subtitle = PaywallSubtitle.Actions, cta = "Get Equal AI Unlimited")
}
@FlowPreviews @Composable private fun TouchRecordingsActionsPreview() = EqualTheme { ChatScreenSample(lock = ChatLock.Details, summaryActions = true) }
@FlowPreviews @Composable private fun TouchRecordingsPaywallPreview() = EqualTheme {
    PaywallScreen(touchpoint = PaywallTouchpoint.RecordingsActions, cta = "Get Equal AI Unlimited")
}
@FlowPreviews @Composable private fun TouchPhoneTogglePreview() = EqualTheme { HomeScreenSample(assistantArt = AssistantToggleArt.Phone) }
@FlowPreviews @Composable private fun SheetPaywallPreview() = EqualTheme { HomeScreenSample(tier = PlanTier.Free, paywallSheetStage = PaywallSheetStage.Sheet) }
@FlowPreviews @Composable private fun SheetHelpPreview() = EqualTheme { HomeScreenSample(tier = PlanTier.Free, paywallSheetStage = PaywallSheetStage.Help) }
@FlowPreviews @Composable private fun SheetExitPreview() = EqualTheme { HomeScreenSample(tier = PlanTier.Free, paywallSheetStage = PaywallSheetStage.Exit) }
@FlowPreviews @Composable private fun SheetOfferPreview() = EqualTheme { HomeScreenSample(tier = PlanTier.Free, offerStrip = true) }
@FlowPreviews @Composable private fun SheetClaimedPreview() = EqualTheme { HomeScreenSample(tier = PlanTier.Free, paywallSheetStage = PaywallSheetStage.Claimed) }
@FlowPreviews @Composable private fun SheetChatExitPreview() = EqualTheme {
    ChatScreenSample(lock = ChatLock.Details, summaryActions = true, paywallSheetStage = PaywallSheetStage.Exit)
}
@FlowPreviews @Composable private fun ResultSuccessPreview() = EqualTheme { PaymentResultScreen(status = PaymentResultStatus.Success) }
@FlowPreviews @Composable private fun ResultPendingPreview() = EqualTheme { PaymentResultScreen(status = PaymentResultStatus.Pending) }
@FlowPreviews @Composable private fun ResultFailedPreview() = EqualTheme { PaymentResultScreen(status = PaymentResultStatus.Failed) }
@FlowPreviews @Composable private fun ResultPendingSupportPreview() = EqualTheme {
    PaymentResultScreen(status = PaymentResultStatus.Pending, pendingAction = PendingAction.Support)
}
@FlowPreviews @Composable private fun ProcessingChatPreview() = EqualTheme { ChatScreenSample(lock = ChatLock.Details, summaryActions = true, paymentProcessing = true) }
@FlowPreviews @Composable private fun ProcessingPaywallPreview() = EqualTheme { PaywallScreen(touchpoint = PaywallTouchpoint.RecordingsActions, processing = true) }
@FlowPreviews @Composable private fun ProcessingHomePreview() = EqualTheme { HomeScreenSample(paymentProcessing = true) }
