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
        "What do I get with Equal Unlimited?",
        "Unlimited call screenings, full recordings, summaries, and zero daily cap. Your assistant handles every unknown call, no limits.",
    ),
    FaqItem(
        "Will Equal screen all types of calls?",
        "Yes! Delivery, ride pickups, spam, loan calls, Equal handles them all and tells you exactly why they called.",
    ),
    FaqItem(
        "Can I cancel my Equal Unlimited plan later?",
        "Yes, cancel anytime. Your Unlimited access stays active until the end of your billing period.",
    ),
    FaqItem(
        "Will I get a refund if I’m not happy with Equal Unlimited?",
        "We don’t offer refunds, but you can write to our support team and we’ll do our best to help.",
    ),
    FaqItem("View Equal Unlimited Fair Usage Policy", "Tap to read the full policy."),
)

@Composable
private fun TestimonialAvatar(asset: EqualAssets) {
    EqualAssetImage(asset, size = 36.dp, modifier = Modifier.clip(CircleShape))
}

val PaywallTestimonials: List<Testimonial> = listOf(
    Testimonial(
        quote = "As a freelancer, client calls and spam used to mix together. Now I get proper summaries and live transcript support. I know exactly what each caller wants without wasting time. Very efficient tool",
        name = "keerthana",
        avatar = { TestimonialAvatar(EqualAssets.AvatarTestimonial1) },
    ),
    Testimonial(
        quote = "It is a very useful app for screening the unknown calls. I used to get up to 10 spam calls a day. This is an important app for me. This is a must download.",
        name = "Arnav Raj",
        avatar = { TestimonialAvatar(EqualAssets.AvatarTestimonial2) },
    ),
    Testimonial(
        quote = "My parents get loan and insurance calls all day. Equal answers them, and I just read the summary in the evening.",
        name = "Riya Kapoor",
        avatar = { TestimonialAvatar(EqualAssets.AvatarTestimonial3) },
    ),
)

/** The paywall subtitle experiment (web `paywallSubtitles`). */
enum class PaywallSubtitle(val copy: String) {
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
        title = "Weekly call limit reached, Limit Resets in 3days",
        body = "You’ve reached your limit on Equal AI. Subscribe to Equal Unlimited to get Unlimited Recordings, AI Call Summaries and Conversations.\n\nTrusted by 10 Lakh+ Indians.",
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
