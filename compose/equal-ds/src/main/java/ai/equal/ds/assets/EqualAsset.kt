package ai.equal.ds.assets

import ai.equal.ds.R
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import android.util.TypedValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize

/**
 * Image assets. Each maps to `res/drawable-xxxhdpi/ds_asset_<name_with_underscores>`. Every slot is now supplied
 * art: a bitmap in `drawable-xxxhdpi`. A future slot can ship a checkerboard vector `.xml` in `drawable` until its art arrives. To supply
 * one, delete the `.xml` and drop a `.webp`/`.png` with the same file name into `drawable-xxxhdpi`.
 *
 * Category avatars are real images: see [EqualCategory]. The AI logo is `EqualLogoOrb`. Profile
 * avatars (`ds_avatar_male/female.webp`) are drawn by `ProfileAvatar`.
 * Action emoji are Unicode text, not assets.
 */
enum class EqualAssets(val assetName: String, @DrawableRes val resId: Int) {
    /** Robot mascot on the AI Assistant card (supplied, drawable-xxxhdpi). Draw with [ai.equal.ds.components.EqualMascot]. */
    MascotRobot("mascot-robot", R.drawable.ds_asset_mascot_robot),

    /** Delete bottom sheet (72dp). */
    IllustrationTrash("illustration-trash", R.drawable.ds_asset_illustration_trash),

    /** Invite a Friend card, 3D high-five (96dp, breaks out of the card top). */
    IllustrationHighFive("illustration-high-five", R.drawable.ds_asset_illustration_high_five),


    /** What's New card image slot (94 × 100). */
    WhatsNewIllustration("whats-new-illustration", R.drawable.ds_asset_whats_new_illustration),

    /** Silent Mode screen header (160dp). */
    IllustrationSilentMode("illustration-silent-mode", R.drawable.ds_asset_illustration_silent_mode),

    /** WhatsApp Updates header (160dp) and turn-off confirm sheet (72dp). */
    IllustrationWhatsapp("illustration-whatsapp", R.drawable.ds_asset_illustration_whatsapp),

    /** Addresses empty state. */
    IllustrationAddressesEmpty("illustration-addresses-empty", R.drawable.ds_asset_illustration_addresses_empty),

    /** Active Instructions empty state. */
    IllustrationInstructionsEmpty("illustration-instructions-empty", R.drawable.ds_asset_illustration_instructions_empty),

    /** "New suggestions coming" card: the robot reading among call bubbles (140dp, top-right). */
    IllustrationSuggestions("illustration-suggestions", R.drawable.ds_asset_illustration_suggestions),

    /** Instructions hub › Active Instructions row: the robot with a magic wand (40dp on the lime tint). */
    MascotInstructions("mascot-instructions", R.drawable.ds_asset_mascot_instructions),

    /** Screen & Protect preset: 3D shield with a tick (33dp; 16dp in the source badge). */
    PresetScreenProtect("preset-screen-protect", R.drawable.ds_asset_preset_screen_protect),

    /** Handle everyday calls preset: 3D receiver with a question mark (33dp; 16dp in the source badge). */
    PresetEverydayCalls("preset-everyday-calls", R.drawable.ds_asset_preset_everyday_calls),

    /** Run my business preset: 3D briefcase (33dp; 16dp in the source badge). */
    PresetBusiness("preset-business", R.drawable.ds_asset_preset_business),

    /** Mic permission dialog: 3D studio mic with sound waves (84dp). */
    PermissionMicrophone("permission-microphone", R.drawable.ds_asset_permission_microphone),

    /** In-app call: the assistant persona on a transparent background, on the gradient call disc (200dp). */
    AvatarAssistantBust("avatar-assistant-bust", R.drawable.ds_asset_avatar_assistant_bust),

    /** Gender selector bust, male (60 × 56). Falls back to ProfileAvatar while a placeholder. */
    AvatarMaleBust("avatar-male-bust", R.drawable.ds_asset_avatar_male_bust),

    /** Gender selector bust, female (60 × 56). Falls back to ProfileAvatar while a placeholder. */
    AvatarFemaleBust("avatar-female-bust", R.drawable.ds_asset_avatar_female_bust),

    /** Sleeping robot with its warm glow: system status card (104dp). */
    MascotSleeping("mascot-sleeping", R.drawable.ds_asset_mascot_sleeping),

    /** Assistant persona with the orb badge baked in: Meet Your Assistant (84 × 94 compact, 100 × 112 banner). */
    AvatarAssistant("avatar-assistant", R.drawable.ds_asset_avatar_assistant),

    /** Phone with sparkles: the assistant toggle art experiment (Component 25, 36dp; supplied, drawable-xxxhdpi). */
    AssistantPhone("assistant-phone", R.drawable.ds_asset_assistant_phone),

    // ---- Monetization ------------------------------------------------------------------------

    /** 3D gold crown: paywall hero (64dp), Unlock All Details (56dp), Congratulations (100dp). */
    CrownGold("crown-gold", R.drawable.ds_asset_crown_gold),

    /** Premium Lock: crown with a padlock marking locked content (16–18dp). A vector (from Premium Lock.svg), not a placeholder. */
    CrownLock("crown-lock", R.drawable.ds_asset_crown_lock),

    /** 3D % seal on the coupon ticket rule (28dp). */
    CouponSeal("coupon-seal", R.drawable.ds_asset_coupon_seal),

    /** Waiting for Confirmation, coin art (100dp). */
    StatusWaiting("status-waiting", R.drawable.ds_asset_status_waiting),

    /** Waiting for Confirmation, cash art experiment (100dp). */
    StatusWaitingCash("status-waiting-cash", R.drawable.ds_asset_status_waiting_cash),

    /** Payment Confirmed (100dp). */
    StatusConfirmed("status-confirmed", R.drawable.ds_asset_status_confirmed),

    /** Payment Pending (100dp). */
    StatusPending("status-pending", R.drawable.ds_asset_status_pending),

    /** Payment Failed (100dp). */
    StatusFailed("status-failed", R.drawable.ds_asset_status_failed),

    /** Cancellation feedback / confirm, sad face (100dp). */
    EmojiSad("emoji-sad", R.drawable.ds_asset_emoji_sad),
    IllustrationAirplane("illustration-airplane", R.drawable.ds_asset_illustration_airplane),
    IllustrationShieldLock("illustration-shield-lock", R.drawable.ds_asset_illustration_shield_lock),
    StatStar("stat-star", R.drawable.ds_asset_stat_star),
    StatPhone("stat-phone", R.drawable.ds_asset_stat_phone),
    IllustrationWarning("illustration-warning", R.drawable.ds_asset_illustration_warning),
    IllustrationErrorSad("illustration-error-sad", R.drawable.ds_asset_illustration_error_sad),
    MascotUpdate("mascot-update", R.drawable.ds_asset_mascot_update),
    UpdateArtLight("update-art-light", R.drawable.ds_asset_update_art_light),
    UpdateArtDark("update-art-dark", R.drawable.ds_asset_update_art_dark),

    /** Cancellation request submitted, thumbs up (100dp). */
    ThumbsUp("thumbs-up", R.drawable.ds_asset_thumbs_up),

    /** Gold star on the rating pill (20dp). */
    StarGold("star-gold", R.drawable.ds_asset_star_gold),

    /** Testimonial avatars on the subscription page (36dp). */
    AvatarTestimonial1("avatar-testimonial-1", R.drawable.ds_asset_avatar_testimonial_1),
    AvatarTestimonial2("avatar-testimonial-2", R.drawable.ds_asset_avatar_testimonial_2),
    AvatarTestimonial3("avatar-testimonial-3", R.drawable.ds_asset_avatar_testimonial_3),

    /** What You Get: 3D gold call-screening art (28dp). */
    FeatureCallScreening("feature-call-screening", R.drawable.ds_asset_feature_call_screening),

    /** What You Get: 3D gold recordings art (28dp). */
    FeatureRecordings("feature-recordings", R.drawable.ds_asset_feature_recordings),

    /** What You Get: 3D gold summaries art (28dp). */
    FeatureSummaries("feature-summaries", R.drawable.ds_asset_feature_summaries),

    /**
     * Gold circuit lines across the top of the plan page [ai.equal.ds.components.ValueCard] (329 × 192,
     * drawn full width). A vector (from value-card-pattern.svg), not a placeholder, so don't gate it on
     * [rememberIsPlaceholder] (which reads every `.xml` as a placeholder).
     */
    ValueCardPattern("value-card-pattern", R.drawable.ds_asset_value_card_pattern),

    /**
     * Soft gold glow across the top of the rings [ai.equal.ds.components.ValueCard] and the
     * [ai.equal.ds.components.QuotaCard] in light theme (1440 × 984, 360dp wide; drawn full width).
     */
    PremiumGlow("premium-glow", R.drawable.ds_asset_premium_glow),

    // ---- Call Handling -----------------------------------------------------------------------

    /** "Which calls should I answer?" hero: you with Mom and an unknown caller (280 × 208). */
    CallHandlingAll("call-handling-all", R.drawable.ds_asset_call_handling_all),

    /** "Anyone you want to answer yourself?" hero (280 × 208). Animated version: `R.raw.lottie_call_handling_contacts`. */
    CallHandlingExclude("call-handling-exclude", R.drawable.ds_asset_call_handling_exclude),

    /** Perfect! / All Sorted!: the robot on a call between Mom and Unknown (300 × 262). */
    CallHandlingDone("call-handling-done", R.drawable.ds_asset_call_handling_done),

    /** "Set up your calls" dark takeover art (240 × 169). */
    CallSetup("call-setup", R.drawable.ds_asset_call_setup),

    /** Contacts permission sheet (110dp). */
    PermissionContacts("permission-contacts", R.drawable.ds_asset_permission_contacts),

    /** Call history permission sheet (110dp). */
    PermissionCallHistory("permission-call-history", R.drawable.ds_asset_permission_call_history),

    /** Direct Calls empty state: no saved contacts (140dp). */
    IllustrationNoContacts("illustration-no-contacts", R.drawable.ds_asset_illustration_no_contacts),

    /** Search empty state: the robot with a magnifier (170dp on the screen). */
    IllustrationSearch("illustration-search", R.drawable.ds_asset_illustration_search),

    /** No internet: struck-through wifi (130dp on Search; works anywhere the network is needed). */
    IllustrationNoInternet("illustration-no-internet", R.drawable.ds_asset_illustration_no_internet),

    /** Search no results: the thinking robot (150dp on the screen). */
    IllustrationNoResults("illustration-no-results", R.drawable.ds_asset_illustration_no_results),

    // ---- Docs only ---------------------------------------------------------------------------

    /**
     * The system keyboard (1560 × 1200, 390 × 300dp at full width), drawn by
     * [ai.equal.ds.components.KeyboardSpace] in docs and previews only. Never ship it as UI: the
     * keyboard is the OS's.
     */
    SystemKeyboard("system-keyboard", R.drawable.ds_asset_system_keyboard),

}

/**
 * True while [asset] is still the shipped checkerboard. Every placeholder is a vector `.xml`
 * and supplied art is a bitmap (`.png` / `.webp`), so this reads the resource's file path.
 * Components use it to fall back (e.g. [ai.equal.ds.components.GenderSelector]).
 */
@Composable
fun rememberIsPlaceholder(asset: EqualAssets): Boolean {
    val resources = LocalContext.current.resources
    return remember(asset, resources) {
        val value = TypedValue()
        resources.getValue(asset.resId, value, true)
        value.string?.toString()?.endsWith(".xml") ?: true
    }
}

/** Renders [asset] in a square [size] box, fitting the image inside. */
@Composable
fun EqualAssetImage(
    asset: EqualAssets,
    size: Dp,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    Image(
        painter = painterResource(id = asset.resId),
        contentDescription = contentDescription,
        modifier = modifier.size(size),
        contentScale = ContentScale.Fit,
    )
}

/** Renders [asset] in a non-square [size] box (e.g. the 94 × 100 What's New slot), cropping to fill. */
@Composable
fun EqualAssetImage(
    asset: EqualAssets,
    size: DpSize,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    Image(
        painter = painterResource(id = asset.resId),
        contentDescription = contentDescription,
        modifier = modifier.size(size),
        contentScale = ContentScale.Crop,
    )
}
