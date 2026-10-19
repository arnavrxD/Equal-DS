package ai.equal.ds.components

import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.foundation.selection.selectableGroup
import ai.equal.ds.icons.EqualIcon
import ai.equal.ds.icons.EqualIcons
import ai.equal.ds.theme.EqualShadows
import ai.equal.ds.theme.EqualTheme
import ai.equal.ds.theme.eqPressFeedback
import ai.equal.ds.theme.eqShadow
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

/** Bottom navigation destinations (SPEC §4). */
enum class EqualTab { Chats, You }

/**
 * Floating bottom navigation (SPEC §4): pill container (padding 8, `bg/surface`, shadow `nav`)
 * with two 99 × 52 capsules, plus the 58dp orb FAB beside it (gap 16).
 *
 * Selected capsule fill `nav/selected-bg`, label `text/action`. Unselected: label `text/tertiary`,
 * icon `text/secondary`. [chatsBadgeCount] > 0 shows a 16dp count badge on the Chats icon.
 * The You item is the user's [ProfileAvatar] ([avatar]) at 24dp wide, lifted 3dp.
 */
@Composable
fun BottomNavBar(
    selectedTab: EqualTab,
    onTabSelected: (EqualTab) -> Unit,
    onFabClick: () -> Unit,
    modifier: Modifier = Modifier,
    chatsBadgeCount: Int = 0,
    chatsLabel: String = "Chats",
    youLabel: String = "You",
    fabContentDescription: String = "Ask Equal AI",
    avatar: ProfileAvatarVariant = ProfileAvatarVariant.Male,
    /** Paid shows the gold-ringed avatar on the You tab. */
    tier: PlanTier = PlanTier.Free,
) {
    val colors = EqualTheme.colors
    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            Modifier
                .eqShadow(EqualShadows.nav, CircleShape)
                .background(colors.bgSurface, CircleShape)
                .padding(8.dp)
                .selectableGroup(),
        ) {
            NavItem(
                label = chatsLabel,
                selected = selectedTab == EqualTab.Chats,
                onClick = { onTabSelected(EqualTab.Chats) },
            ) { selected ->
                Box {
                    EqualIcon(
                        imageVector = if (selected) EqualIcons.ChatBubble else EqualIcons.ChatBubbleOutline,
                        contentDescription = null,
                        tint = if (selected) colors.textAction else colors.textSecondary,
                        size = 24.dp,
                    )
                    if (chatsBadgeCount > 0) {
                        NavCountBadge(
                            count = chatsBadgeCount,
                            // SPEC offset (x +10, y −6), compensating for the 1.5dp ring.
                            modifier = Modifier.offset(x = 8.5.dp, y = (-7.5).dp),
                        )
                    }
                }
            }
            NavItem(
                label = youLabel,
                selected = selectedTab == EqualTab.You,
                onClick = { onTabSelected(EqualTab.You) },
            ) {
                // The head rises above the ring; lift it 3dp so the ring sits on the icon line.
                ProfileAvatar(
                    variant = avatar,
                    width = 24.dp,
                    tier = tier,
                    contentDescription = null,
                    modifier = Modifier.offset(y = (-3).dp),
                )
            }
        }
        EqualFab(onClick = onFabClick, contentDescription = fabContentDescription)
    }
}

@Composable
private fun NavItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable (selected: Boolean) -> Unit,
) {
    val colors = EqualTheme.colors
    Column(
        Modifier
            .size(width = 99.dp, height = 52.dp)
            .clip(CircleShape)
            .background(if (selected) colors.navSelectedBg else colors.bgSurface)
            .selectable(selected = selected, role = Role.Tab, onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        icon(selected)
        Spacer(Modifier.height(2.dp))
        EqualText(
            text = label,
            style = EqualTheme.type.caption,
            color = if (selected) colors.textAction else colors.textSecondary,
            maxLines = 1,
        )
    }
}

/** 16dp count badge with a 1.5dp `bg/surface` ring, used on the tab icon. */
@Composable
private fun NavCountBadge(count: Int, modifier: Modifier = Modifier) {
    val colors = EqualTheme.colors
    Box(
        modifier
            .size(19.dp)
            .clearAndSetSemantics { contentDescription = "$count unread" }
            .background(colors.bgSurface, CircleShape)
            .padding(1.5.dp)
            .background(colors.buttonPrimary, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        EqualText(
            text = if (count > 9) "9+" else count.toString(),
            style = EqualTheme.type.badgeCount,
            color = colors.textOnAction,
            maxLines = 1,
        )
    }
}

/**
 * 58dp FAB (SPEC §4): the [EqualLogoOrb]. The image carries its own glow, so there is no
 * gradient, shadow or clip here (clipping would cut the glow).
 */
@Composable
fun EqualFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = "Ask Equal AI",
) {
    val interaction = rememberInteraction()
    EqualLogoOrb(
        size = 58.dp,
        contentDescription = contentDescription,
        modifier = modifier
            .eqPressFeedback(interaction, CircleShape)
            .clickable(interactionSource = interaction, indication = null, role = Role.Button, onClick = onClick),
    )
}

@EqualPreviews
@Composable
private fun BottomNavPreview() {
    PreviewFrame {
        var tab by remember { mutableStateOf(EqualTab.Chats) }
        BottomNavBar(selectedTab = tab, onTabSelected = { tab = it }, onFabClick = {}, chatsBadgeCount = 3)
    }
}
