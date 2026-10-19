package ai.equal.ds.components

import ai.equal.ds.assets.EqualCategory
import ai.equal.ds.icons.EqualIcon
import ai.equal.ds.icons.EqualIcons
import ai.equal.ds.theme.EqualTheme
import ai.equal.ds.theme.tabular
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.border
import ai.equal.ds.tokens.EqualRadius
import ai.equal.ds.tokens.EqualPrimitives
import ai.equal.ds.theme.eqShadow
import ai.equal.ds.theme.eqPressFeedback
import ai.equal.ds.theme.EqualShadowLayer
import ai.equal.ds.theme.EqualShadow

/**
 * Profile header (derived). [ProfileAvatar] 56 → 12 → [name `headline` + [PlanBadge] (gap 8),
 * phone `body1` `text/secondary` (tabular)]. For the You tab hero use [ProfileHero].
 */
@Composable
fun ProfileHeader(
    name: String,
    phone: String,
    modifier: Modifier = Modifier,
    planLabel: String? = "Unlimited",
    avatar: ProfileAvatarVariant = ProfileAvatarVariant.Male,
    tier: PlanTier = if (planLabel != null) PlanTier.Paid else PlanTier.Free,
) {
    val colors = EqualTheme.colors
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        ProfileAvatar(variant = avatar, width = 56.dp, tier = tier)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                EqualText(
                    text = name,
                    style = EqualTheme.type.headline,
                    color = colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false).semantics { heading() },
                )
                if (planLabel != null) {
                    Spacer(Modifier.width(8.dp))
                    PlanBadge(text = planLabel)
                }
            }
            EqualText(text = phone, style = EqualTheme.type.body1.tabular(), color = colors.textSecondary, maxLines = 1)
        }
    }
}

/** An action under [ContactInfoHeader]. */
@Immutable
data class ContactInfoAction(val label: String, val icon: ImageVector, val onClick: () -> Unit)

/**
 * Contact info header (Contact Info design, measured 1:1): [ContactAvatar] 96 ([category] image or
 * [initials]) → 10 → name 22/28 Bold → phone `bodyLarge` `text/tertiary` (tabular). Padding
 * 8 top, 22 sides. Below (24 top), a row of equal action tiles (gap 12): 72 high, radius 8,
 * `bg/surface` (faint shadow in light; 1dp `stroke/subtle` in dark), icon 24 `text/action` → 8 →
 * label `body1` `text/primary`.
 */
@Composable
fun ContactInfoHeader(
    name: String,
    phone: String,
    actions: List<ContactInfoAction>,
    modifier: Modifier = Modifier,
    category: EqualCategory? = EqualCategory.NA,
    initials: String? = null,
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    Column(
        modifier
            .fillMaxWidth()
            .padding(start = 22.dp, end = 22.dp, top = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ContactAvatar(size = 96.dp, category = category, initials = initials)
        Spacer(Modifier.height(10.dp))
        EqualText(
            text = name,
            style = type.headline.copy(fontSize = 22.sp, lineHeight = 28.sp, fontWeight = FontWeight.Bold),
            color = colors.textPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.semantics { heading() },
        )
        EqualText(text = phone, style = type.bodyLarge.tabular(), color = colors.textSecondary, maxLines = 1)
        if (actions.isNotEmpty()) {
            Spacer(Modifier.height(24.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                actions.forEach { action ->
                    ActionTile(action, Modifier.weight(1f))
                }
            }
        }
    }
}

/** Tile shadow (web: 0 1px 2px black/4%). */
private val ActionTileShadow = EqualShadow(listOf(EqualShadowLayer(0.dp, 1.dp, 2.dp, EqualPrimitives.black.copy(alpha = 0.04f))))

@Composable
private fun ActionTile(action: ContactInfoAction, modifier: Modifier) {
    val colors = EqualTheme.colors
    val shape = RoundedCornerShape(8.dp)
    val interaction = rememberInteraction()
    Column(
        modifier
            .eqPressFeedback(interaction, shape)
            .height(72.dp)
            .eqShadow(ActionTileShadow, shape)
            .background(colors.bgSurface, shape)
            .then(if (colors.isDark) Modifier.border(1.dp, colors.strokeSubtle, shape) else Modifier)
            .clip(shape)
            .clickable(interactionSource = interaction, indication = null, role = Role.Button, onClick = action.onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        EqualIcon(action.icon, contentDescription = null, tint = colors.textAction, size = 24.dp)
        Spacer(Modifier.height(8.dp))
        EqualText(text = action.label, style = EqualTheme.type.body1, color = colors.textPrimary, maxLines = 1)
    }
}

/**
 * Default Call / Mute / Save / Share actions for [ContactInfoHeader]. Mute uses the silent-mode
 * icon; pass [muted] to show "Unmute".
 */
fun defaultContactInfoActions(
    onCall: () -> Unit,
    onMute: () -> Unit,
    onSave: () -> Unit,
    onShare: () -> Unit,
    muted: Boolean = false,
): List<ContactInfoAction> = listOf(
    ContactInfoAction("Call", EqualIcons.Call, onCall),
    ContactInfoAction(if (muted) "Unmute" else "Mute", EqualIcons.SilentMode, onMute),
    ContactInfoAction("Save", EqualIcons.PersonAdd, onSave),
    ContactInfoAction("Share", EqualIcons.Share, onShare),
)

@EqualPreviews
@Composable
private fun ProfilePreview() {
    PreviewFrame {
        ProfileHeader(name = "Arnav Raj", phone = "+91 98765 43210")
        VSpace(16.dp)
        ContactInfoHeader(
            name = "Unknown caller",
            phone = "+91 91234 56789",
            actions = defaultContactInfoActions({}, {}, {}, {}),
            category = EqualCategory.Career,
        )
    }
}
