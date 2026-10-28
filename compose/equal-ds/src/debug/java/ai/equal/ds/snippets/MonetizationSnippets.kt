// Compose code shown on the docs site (Components → Monetization). Each function body is
// extracted by scripts/build-snippets.mjs; keep the `// snippet: <doc id> / <example title>` line above it.
// Debug source set only: compiled with the module, never shipped in the release AAR.
@file:Suppress("unused")

package ai.equal.ds.snippets

import ai.equal.ds.components.AnimatedCrown
import ai.equal.ds.assets.EqualAssetImage
import ai.equal.ds.assets.EqualAssets
import ai.equal.ds.assets.EqualCategory
import ai.equal.ds.components.CallSummaryBubble
import ai.equal.ds.components.CallSummaryLock
import ai.equal.ds.components.CancelSheet
import ai.equal.ds.components.CancelStep
import ai.equal.ds.components.ChatBubble
import ai.equal.ds.components.ChatBubbleType
import ai.equal.ds.components.ChatListItem
import ai.equal.ds.components.CouponTicket
import ai.equal.ds.components.DividerLabel
import ai.equal.ds.components.EqualDivider
import ai.equal.ds.components.EqualText
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
import ai.equal.ds.components.PlanCard
import ai.equal.ds.components.PlaybackSpeed
import ai.equal.ds.components.PriceTag
import ai.equal.ds.components.RatingPill
import ai.equal.ds.components.StatBlock
import ai.equal.ds.components.Testimonial
import ai.equal.ds.components.TestimonialCarousel
import ai.equal.ds.components.UnlockAudioPill
import ai.equal.ds.components.UnlockDetailsCard
import ai.equal.ds.components.VoiceNotePlayer
import ai.equal.ds.theme.EqualTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

// ============================================================ Paywall

// snippet: paywall / Hero with price and preview
@Composable
internal fun PaywallHeroSnippet() {
    PaywallHero(onBack = {}, onSupport = { /* open HelpCenterSheet */ }) {
        PriceTag(price = "249")
        LockedPreviewCard {
            Column {
                ChatListItem(name = "HDFC RM", preview = "Loan pre-approval request", date = "1:00 PM",
                    category = EqualCategory.GateAlert)
                EqualDivider()
                ChatListItem(name = "Vijay NoBroker", preview = "About miniature painting", date = "Yesterday",
                    category = EqualCategory.RealEstate, locked = true)
            }
        }
    }
}

// snippet: paywall / Preview by touchpoint
@Composable
internal fun PaywallPreviewByTouchpointSnippet() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Chat list
        LockedPreviewCard {
            ChatListItem(name = "Vijay NoBroker", preview = "About miniature painting", date = "Yesterday",
                category = EqualCategory.RealEstate, locked = true)
        }
        // Chat
        LockedPreviewCard {
            Column(Modifier.padding(horizontal = 4.dp, vertical = 12.dp)) {
                ChatBubble(type = ChatBubbleType.Outgoing, timestamp = "03:21", message = "Hello, who is this?")
                ChatBubble(type = ChatBubbleType.Incoming, incomingCategory = EqualCategory.RealEstate,
                    timestamp = "03:21", message = "Vijay from no broker", modifier = Modifier.padding(top = 12.dp))
                ChatBubble(type = ChatBubbleType.Incoming, incomingCategory = EqualCategory.RealEstate,
                    isFirstInGroup = false, timestamp = "03:21", locked = true, modifier = Modifier.padding(top = 4.dp),
                    message = "Hey this is vijay calling from magicbrick a property advisor")
            }
        }
        // Audio
        LockedPreviewCard {
            CallSummaryBubble(
                summary = "Hey Keshav! Here is the summary of your call.",
                timestamp = "03:21",
                locked = CallSummaryLock.Audio,
                onUnlock = { /* open paywall */ },
                voiceNote = {
                    VoiceNotePlayer(isPlaying = false, onPlayPauseClick = {}, progress = 0f, elapsed = "0:00",
                        total = "2:37", speed = PlaybackSpeed.X1, onSpeedChange = {})
                },
                modifier = Modifier.padding(4.dp),
            )
        }
    }
}

// snippet: paywall / Animated crown
@Composable
internal fun PaywallAnimatedCrownSnippet() {
    // Turntable sway (4.8s loop); static when animations are off. Stands in for the crown video.
    AnimatedCrown(size = 64.dp)
}

// snippet: paywall / Sticky footer
@Composable
internal fun PaywallStickyFooterSnippet() {
    PaywallFooter(
        onClick = { /* start payment */ },
        cta = "Get Equal AI Unlimited",
        note = "Cancel anytime · UPI secure · No hidden charges",
    )
}

// ============================================================ Coupon ticket

// snippet: coupon-ticket / Available
@Composable
internal fun CouponTicketAvailableSnippet() {
    CouponTicket(code = "EQUAL25", description = "Get extra 25% off your first month", discount = "25% OFF on UPI", onApply = {})
}

// snippet: coupon-ticket / Applied
@Composable
internal fun CouponTicketAppliedSnippet() {
    CouponTicket(code = "EQUAL25", description = "Get extra 25% off your first month", discount = "25% OFF on UPI", applied = true)
}

// ============================================================ Locked content

// snippet: locked-content / Locked chat rows
@Composable
internal fun LockedChatRowsSnippet() {
    Column {
        ChatListItem(name = "Amit Nanda", preview = "Amit is done with onboarding.", date = "Yesterday",
            category = EqualCategory.Career)
        ChatListItem(name = "Vijay CA", preview = "Your KYC is pending", date = "1 May",
            category = EqualCategory.Financial, locked = true, onClick = { /* open paywall */ })
        ChatListItem(name = "Vijay OLA", preview = "On the gate, need approval.", date = "1 Jul",
            category = EqualCategory.Ride, locked = true, onClick = { /* open paywall */ })
    }
}

// snippet: locked-content / Locked bubbles and audio
@Composable
internal fun LockedBubblesAudioSnippet() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        ChatBubble(type = ChatBubbleType.Outgoing, timestamp = "03:21", message = "Hello, who is this?", locked = true)
        ChatBubble(type = ChatBubbleType.Incoming, incomingCategory = EqualCategory.RealEstate, timestamp = "03:21",
            message = "We have a 2BHK in Sector 12 with good offer", locked = true)
        CallSummaryBubble(
            summary = "Hey Keshav! Here is the summary of your call.",
            timestamp = "03:22",
            locked = CallSummaryLock.Audio,
            onUnlock = { /* open paywall */ },
            voiceNote = {
                VoiceNotePlayer(isPlaying = false, onPlayPauseClick = {}, progress = 0f, elapsed = "0:00",
                    total = "2:37", speed = PlaybackSpeed.X1, onSpeedChange = {})
            },
        )
    }
}

// snippet: locked-content / Unlock All Details
@Composable
internal fun UnlockAllDetailsSnippet() {
    UnlockDetailsCard(onUnlock = { /* open paywall */ })
}

// snippet: locked-content / Badge and wrapper
@Composable
internal fun LockBadgeWrapperSnippet() {
    Row(horizontalArrangement = Arrangement.spacedBy(24.dp), verticalAlignment = Alignment.CenterVertically) {
        LockBadge()
        LockedContent {
            EqualText(text = "Hey this is vijay calling from magicbrick", style = EqualTheme.type.body1,
                color = EqualTheme.colors.textPrimary)
        }
        UnlockAudioPill(onClick = { /* open paywall */ })
    }
}

// ============================================================ Payment status

// snippet: payment-status / Waiting for Confirmation
@Composable
internal fun PaymentWaitingSnippet() {
    PaymentStatusSheet(status = PaymentStatus.Waiting, inline = true)
}

// snippet: payment-status / Payment Confirmed
@Composable
internal fun PaymentConfirmedSnippet() {
    PaymentStatusSheet(status = PaymentStatus.Confirmed, onRedirect = { /* go home */ }, inline = true)
}

// snippet: payment-status / Payment Pending
@Composable
internal fun PaymentPendingSnippet() {
    PaymentStatusSheet(status = PaymentStatus.Pending, onContactSupport = { /* open HelpCenterSheet */ }, inline = true)
}

// snippet: payment-status / Payment Failed
@Composable
internal fun PaymentFailedSnippet() {
    PaymentStatusSheet(status = PaymentStatus.Failed, onRetry = { /* retry payment */ }, inline = true)
}

// snippet: payment-status / Congratulations
@Composable
internal fun PaymentWelcomeSnippet() {
    PaymentStatusSheet(status = PaymentStatus.Welcome, onRedirect = { /* go home */ }, inline = true)
}

// ============================================================ Help Center sheet

// snippet: help-center-sheet / Default
@Composable
internal fun HelpCenterSheetSnippet() {
    HelpCenterSheet(phone = "+91 9876 543210", email = "hey@equal.in", onCall = {}, onMail = {}, inline = true)
}

// ============================================================ FAQ

// snippet: faq / Accordion
@Composable
internal fun FaqAccordionSnippet() {
    val faq = listOf(
        FaqItem("What do I get with Equal AI Unlimited?", "Unlimited call screenings, full recordings, summaries, and zero daily cap."),
        FaqItem("Does Equal AI screen all types of calls?", "Yes! Delivery, ride pickups, spam, loan calls, Equal AI handles them all."),
        FaqItem("Can I cancel my Equal AI Unlimited plan later?", "Yes, cancel anytime. Your Unlimited access stays active until the end of your billing period."),
        FaqItem("Can I get a refund if I’m not happy with Equal AI Unlimited?", "Yes. If you’re not happy within 3 days of your first payment, reach us through our helpline and we’ll process your refund."),
    )
    FaqList(items = faq, defaultOpen = listOf(3))
}

// snippet: faq / Section label
@Composable
internal fun FaqSectionLabelSnippet() {
    DividerLabel(text = "Why people choose")
}

// ============================================================ Plan banner

// snippet: plan-banner / Under the plan card
@Composable
internal fun PlanBannerAttachedSnippet() {
    Column {
        PlanCard(onManageClick = { /* reactivate */ }, manageLabel = "Reactivate plan")
        PlanBanner(text = "Reactivate your current Plan to use Equal Unlimited", attached = true)
    }
}

// snippet: plan-banner / At the top of a chat
@Composable
internal fun PlanBannerChatSnippet() {
    PlanBanner(text = "Reactivate your current Plan to keep using Equal Unlimited", onClick = { /* reactivate */ })
}

// ============================================================ Social proof

// snippet: social-proof / Stat and rating
@Composable
internal fun StatAndRatingSnippet() {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        StatBlock(value = "10 Lakh+", label = "People know who called and why")
        RatingPill(rating = 4.5f)
    }
}

// snippet: social-proof / Testimonials
@Composable
internal fun TestimonialsSnippet() {
    val avatar = @Composable { asset: EqualAssets -> EqualAssetImage(asset, size = 36.dp, modifier = Modifier.clip(CircleShape)) }
    TestimonialCarousel(
        items = listOf(
            Testimonial(
                quote = "Equal AI screens every unknown call so I never miss what matters.",
                name = "Riya S.",
                avatar = { avatar(EqualAssets.AvatarTestimonial1) },
            ),
            Testimonial(
                quote = "Equal AI eliminates spam and engages with unknown callers itself.",
                name = "Chaitanya Sai",
                avatar = { avatar(EqualAssets.AvatarTestimonial2) },
            ),
            Testimonial(
                quote = "Saves me time and handles unknown calls like a pro.",
                name = "Sanjana Ramakrishna",
                avatar = { avatar(EqualAssets.AvatarTestimonial3) },
            ),
        ),
    )
}

// ============================================================ Cancellation

// snippet: cancel-sheet / Feedback
@Composable
internal fun CancelFeedbackSnippet() {
    var reason by remember { mutableStateOf("") }
    CancelSheet(step = CancelStep.Feedback, reason = reason, onReasonChange = { reason = it },
        onProceed = { /* next step */ }, inline = true)
}

// snippet: cancel-sheet / Feedback filled
@Composable
internal fun CancelFeedbackFilledSnippet() {
    var reason by remember { mutableStateOf("Felt it too expensive, I hope it would be under my budget") }
    CancelSheet(step = CancelStep.Feedback, reason = reason, onReasonChange = { reason = it },
        onProceed = { /* next step */ }, inline = true)
}

// snippet: cancel-sheet / Are you really sure?
@Composable
internal fun CancelConfirmSnippet() {
    CancelSheet(step = CancelStep.Confirm, accessUntil = "15 June 2026", onKeep = { /* close */ },
        onConfirm = { /* cancel subscription */ }, inline = true)
}

// snippet: cancel-sheet / Request submitted
@Composable
internal fun CancelSubmittedSnippet() {
    CancelSheet(step = CancelStep.Submitted, accessUntil = "15 June 2026", onRedirect = { /* go home */ }, inline = true)
}
