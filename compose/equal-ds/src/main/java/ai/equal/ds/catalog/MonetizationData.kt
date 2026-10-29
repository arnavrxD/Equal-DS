package ai.equal.ds.catalog

import ai.equal.ds.assets.EqualAssetImage
import ai.equal.ds.assets.EqualAssets
import ai.equal.ds.components.FaqItem
import ai.equal.ds.components.Testimonial
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

// Sample content for the monetization screens (Monetization Flow design). Port of
// web/src/screens/monetizationData.ts.

val PaywallFaq: List<FaqItem> = listOf(
    FaqItem(
        "What do I get with Equal AI Unlimited?",
        "You get unlimited call screening, call recordings, AI call summaries, and conversations for every call. New features are added through the year.",
    ),
    FaqItem(
        "How does membership work?",
        "You pay for your first month and setup auto-pay with any UPI app like PhonePe, Google Pay or Paytm. It renews every month, and you can cancel anytime.",
    ),
    FaqItem(
        "Can I cancel anytime?",
        "Yes, you can cancel anytime. You keep full access until your renewal date. After that, you won’t be charged again",
    ),
    FaqItem(
        "Do you offer refunds?",
        "Yes. If you’re not happy within 3 days of your first payment, email us at hey@equal.in and we’ll process your refund.",
    ),
    FaqItem(
        "Which payment methods are supported?",
        "UPI only — pay with any UPI app like PhonePe, Google Pay or Paytm, processed securely through Razorpay.",
    ),
    FaqItem(
        "What happens to my call data?",
        "Recordings and transcripts stay encrypted and tied to your account. We never share them with third parties and you can request deletion from Profile → Account Deletion.",
    ),
    FaqItem(
        "How do I change my plan?",
        "Open Profile → Manage Subscription. You can switch between monthly and yearly plans there; changes apply from the next billing cycle.",
    ),
    FaqItem("View Equal Unlimited Fair Usage Policy", "Tap to read the full policy."),
)

/** The FAQ on the Manage Subscription page, curated separately from the paywall's (web `manageFaq`). */
val ManageFaq: List<FaqItem> = listOf(
    FaqItem(
        "What do I get with Equal AI Unlimited?",
        "Unlimited call screenings, full recordings, summaries, and zero daily cap. Your assistant handles every unknown call, no limits.",
    ),
    FaqItem(
        "Does Equal AI screen all types of calls?",
        "Yes! Delivery, ride pickups, spam, loan calls, Equal AI handles them all and tells you exactly why they called.",
    ),
    FaqItem(
        "Can I cancel my Equal AI Unlimited plan later?",
        "Yes, cancel anytime. Your Unlimited access stays active until the end of your billing period.",
    ),
    FaqItem(
        "Can I get a refund if I’m not happy with Equal AI Unlimited?",
        "Yes. If you’re not happy within 3 days of your first payment, reach us through our helpline and we’ll process your refund.",
    ),
)

@Composable
private fun TestimonialAvatar(asset: EqualAssets) {
    EqualAssetImage(asset, size = 36.dp, modifier = Modifier.clip(CircleShape))
}

val PaywallTestimonials: List<Testimonial> = listOf(
    Testimonial(
        quote = "Equal AI screens every unknown call so I never miss what matters.",
        name = "Riya S.",
        avatar = { TestimonialAvatar(EqualAssets.AvatarTestimonial1) },
    ),
    Testimonial(
        quote = "Equal AI eliminates spam and engages with unknown callers itself.",
        name = "Chaitanya Sai",
        avatar = { TestimonialAvatar(EqualAssets.AvatarTestimonial2) },
    ),
    Testimonial(
        quote = "Saves me time and handles unknown calls like a pro.",
        name = "Sanjana Ramakrishna",
        avatar = { TestimonialAvatar(EqualAssets.AvatarTestimonial3) },
    ),
)

/** The paywall subtitle experiment (web `paywallSubtitles`). */
enum class PaywallSubtitle(val copy: String) {
    /** Production copy (headline_subtitle). */
    Default("Get Unlimited Recordings, AI Call Summaries and Conversations. Trusted by 10 Lakh+ Indians."),
    Unlock("Unlock Summaries, Transcripts, Recordings & Unlimited Call Screening"),
    Get("Get Unlimited Summaries, Transcripts, Recordings & Call Screening"),

    /** Actions touchpoint. */
    Actions("Unlock Unlimited Actions, Summaries, Transcripts, Recordings & Call Screening"),

    /** Usage cap touchpoint. */
    Cap("Unlock Unlimited Call Screening, Recordings & Summaries, Recordings & Transcripts."),
}

/**
 * Messages from Equal AI in its own chat (Plan Expired / Usage Cap option 1; web `officialNotices`):
 * the home row's [preview] and 👑 [chip], and the [ai.equal.ds.components.AssistantNoticeCard]'s
 * [title], [body] and [cta].
 */
enum class OfficialNotice(val preview: String, val chip: String, val title: String, val body: String, val cta: String) {
    PlanExpired(
        preview = "Your Plan Expired, Renew to get Unlimited",
        chip = "Renew Now",
        title = "Your Plan Expired",
        body = "Renew to unlock unlimited recordings, call summaries, transcripts, and unlimited call screening.",
        cta = "Renew Equal AI Unlimited",
    ),
    RecordingsLocked(
        preview = "All Your recordings are locked, Unlock unlimited recordings and more with Pro.",
        chip = "Get Unlimited",
        title = "Your call recordings are locked",
        body = "Subscribe to Equal Unlimited to get Unlimited Recordings, AI Call Summaries and Conversations.\n\nTrusted by 10 Lakh+ Indians.",
        cta = "Get Equal AI Unlimited",
    ),
    UsageCap(
        preview = "Call limit reached, Resets in 3 days",
        chip = "Get Unlimited",
        title = "You’ve used your free calls this week",
        body = "Your weekly limit resets on Monday.\n\nUpgrade to keep Equal AI answering your calls.",
        cta = "Get Equal AI Unlimited",
    ),
}

/** Card under the home header (web `homeNotice`). */
sealed interface HomeNotice {
    /** Plan Expired option 2: 👑 Your Plan Expired · Renew Now ([crown] false: the option without it). */
    data class PlanExpired(val crown: Boolean = true) : HomeNotice

    /** Usage Cap option 2: the weekly meter ("7 Calls left this week"); [icon] false hides the phone. */
    data class UsageCap(val callsLeft: Int, val limit: Int, val icon: Boolean = true) : HomeNotice

    /** Recordings locked option 2: Call Recordings Locked · Get Unlimited ([icon] false hides the phone). */
    data class RecordingsLocked(val icon: Boolean = true) : HomeNotice
}
