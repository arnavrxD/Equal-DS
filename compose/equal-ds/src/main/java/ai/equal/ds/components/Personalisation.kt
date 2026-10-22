package ai.equal.ds.components

import ai.equal.ds.icons.EqualIcon
import ai.equal.ds.icons.EqualIcons
import ai.equal.ds.icons.EqualSymbols
import ai.equal.ds.theme.EqualShadows
import ai.equal.ds.theme.EqualTheme
import ai.equal.ds.theme.eqShadow
import ai.equal.ds.theme.toBrush
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/** The 80 orb with three floating trait pills (FYI sheet hero). Decorative. */
@Composable
fun TraitOrbHero(modifier: Modifier = Modifier, traits: List<String> = listOf("Greets friends by name", "Says you’ll call back at 7", "Replies in Hinglish")) {
    val colors = EqualTheme.colors
    Box(modifier.fillMaxWidth().height(180.dp).clearAndSetSemantics {}, contentAlignment = Alignment.Center) {
        EqualLogoOrb(size = 80.dp, contentDescription = null)
        val spots = listOf(Alignment.TopStart to Offset(8f, 20f), Alignment.CenterEnd to Offset(-4f, -20f), Alignment.BottomStart to Offset(28f, -16f))
        traits.take(3).forEachIndexed { i, t ->
            val (align, off) = spots[i]
            Box(
                Modifier.align(align).offset(off.x.dp, off.y.dp).eqShadow(EqualShadows.card, CircleShape).clip(CircleShape).background(colors.bgSurface).height(32.dp).padding(horizontal = 12.dp),
                contentAlignment = Alignment.Center,
            ) { EqualText(text = t, style = EqualTheme.type.caption, color = colors.textPrimary, maxLines = 1) }
        }
    }
}

/** One row of [InfoRows]. */
data class InfoRow(val icon: ImageVector, val title: String, val body: String)

/** Icon rows in a white card: 24 icon (`text/action`), `subheading` 600 title, `caption` body. */
@Composable
fun InfoRows(rows: List<InfoRow>, modifier: Modifier = Modifier) {
    val colors = EqualTheme.colors
    Column(modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(colors.bgSurface)) {
        rows.forEachIndexed { i, r ->
            if (i > 0) EqualDivider()
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp).semantics(mergeDescendants = true) {}, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                EqualIcon(r.icon, contentDescription = null, tint = colors.textAction, size = 24.dp)
                Column {
                    EqualText(text = r.title, style = EqualTheme.type.subheading.copy(fontWeight = FontWeight.W600), color = colors.textPrimary)
                    EqualText(text = r.body, style = EqualTheme.type.captionRegular, color = colors.textSecondary)
                }
            }
        }
    }
}

/** [NoticedCard] states. */
enum class NoticedState { Ready, Reveal }

/**
 * Equal Noticed (You tab, ABOUT YOU). Ready: 18 orb + "Equal Noticed...", share, [summary]
 * paragraphs, thumbs and "Updates in …". Reveal: `gradient/noticed-reveal` with "Updated summary is
 * ready · Tap to reveal" and twinkling dots (still under reduced motion).
 */
@Composable
fun NoticedCard(summary: List<String>, modifier: Modifier = Modifier, state: NoticedState = NoticedState.Ready, updatesIn: String = "21 days", onReveal: () -> Unit = {}, onShare: () -> Unit = {}) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    val shape = RoundedCornerShape(16.dp)
    if (state == NoticedState.Reveal) {
        val reduce = rememberReduceMotion()
        val a = if (reduce) {
            0.7f
        } else {
            val t = rememberInfiniteTransition(label = "twinkle")
            val v by t.animateFloat(0.7f, 0.25f, infiniteRepeatable(tween(1200), RepeatMode.Reverse), label = "a")
            v
        }
        val dot = colors.textOnAction
        Column(
            modifier.fillMaxWidth().defaultMinSize(minHeight = 160.dp).clip(shape)
                .background(EqualTheme.gradients.noticedReveal.toBrush())
                .drawBehind {
                    listOf(0.18f to 0.26f, 0.82f to 0.30f, 0.70f to 0.78f, 0.28f to 0.76f).forEach { (x, y) -> drawCircle(dot.copy(alpha = a), radius = 2.dp.toPx(), center = Offset(size.width * x, size.height * y)) }
                }
                .clickable(role = Role.Button, onClick = onReveal)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            EqualText(text = "Updated summary is ready", style = type.title.copy(fontWeight = FontWeight.W600), color = colors.textOnAction, textAlign = TextAlign.Center)
            Spacer(Modifier.height(4.dp))
            EqualText(text = "Tap to reveal", style = type.bodyLarge.copy(fontWeight = FontWeight.W500), color = colors.textOnAction)
        }
        return
    }
    var vote by remember { mutableStateOf<Boolean?>(null) }
    Column(modifier.fillMaxWidth().clip(shape).background(colors.bgPrimary).border(1.dp, colors.strokeSubtle, shape).padding(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            EqualLogoOrb(size = 18.dp, contentDescription = null)
            EqualText(text = "Equal Noticed...", style = type.caption.copy(fontWeight = FontWeight.W700), color = colors.textPrimary, modifier = Modifier.weight(1f).semantics { heading() })
            SmallIcon(EqualIcons.Share, "Share", onShare)
        }
        Spacer(Modifier.height(8.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) { summary.forEach { EqualText(text = it, style = type.body1, color = colors.textPrimary) } }
        Spacer(Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            SmallIcon(if (vote == true) EqualSymbols.thumbUpFill else EqualSymbols.thumbUp, "Helpful", { vote = if (vote == true) null else true }, selected = vote == true)
            SmallIcon(if (vote == false) EqualSymbols.thumbDownFill else EqualSymbols.thumbDown, "Not helpful", { vote = if (vote == false) null else false }, selected = vote == false)
            Spacer(Modifier.weight(1f))
            EqualText(text = "Updates in $updatesIn", style = type.captionRegular, color = colors.textSecondary)
        }
    }
}

@Composable
private fun SmallIcon(icon: ImageVector, label: String, onClick: () -> Unit, selected: Boolean? = null) {
    Box(
        Modifier.minimumInteractiveComponentSize().clickable(role = Role.Button, onClickLabel = label, onClick = onClick)
            .semantics { contentDescription = label; if (selected != null) stateDescription = if (selected) "Selected" else "Not selected" },
        contentAlignment = Alignment.Center,
    ) { EqualIcon(icon, contentDescription = null, tint = EqualTheme.colors.textSecondary, size = 20.dp) }
}

/** First-time coach mark over the reveal card: title, line and Got it, with a tail. */
@Composable
fun NoticedCoach(onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    val colors = EqualTheme.colors
    Box(modifier) {
        Column(Modifier.widthIn(max = 304.dp).eqShadow(EqualShadows.nav, RoundedCornerShape(16.dp)).clip(RoundedCornerShape(16.dp)).background(colors.bgSurface).padding(horizontal = 16.dp, vertical = 12.dp)) {
            EqualText(text = "I’m getting to know you!", style = EqualTheme.type.subheading.copy(fontWeight = FontWeight.W600), color = colors.textPrimary, modifier = Modifier.semantics { heading() })
            EqualText(text = "The more I learn, the better I can step in and handle your calls.", style = EqualTheme.type.body1, color = colors.textSecondary)
            Spacer(Modifier.height(8.dp))
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) { EqualButton(text = "Got it", onClick = onDismiss, size = EqualButtonSize.Xs) }
        }
    }
}

/** What stays (green checks) / What goes (grey crosses). */
@Composable
fun KeepLoseList(stays: List<String>, goes: List<String>, modifier: Modifier = Modifier) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        EqualText(text = "What stays", style = type.body1.copy(fontWeight = FontWeight.W600), color = colors.textPrimary, modifier = Modifier.semantics { heading() })
        stays.forEach { Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) { EqualIcon(EqualIcons.Check, contentDescription = null, tint = colors.textAction, size = 16.dp); EqualText(text = it, style = type.body1, color = colors.textPrimary) } }
        Spacer(Modifier.height(4.dp))
        EqualText(text = "What goes", style = type.body1.copy(fontWeight = FontWeight.W600), color = colors.textPrimary, modifier = Modifier.semantics { heading() })
        goes.forEach { Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) { EqualIcon(EqualIcons.Close, contentDescription = null, tint = colors.textSecondary, size = 16.dp); EqualText(text = it, style = type.body1, color = colors.textPrimary) } }
    }
}

/** Overlapping 24 initials discs (excluded contacts). Decorative. */
@Composable
fun AvatarStack(initials: List<String>, modifier: Modifier = Modifier) {
    val colors = EqualTheme.colors
    Row(modifier.clearAndSetSemantics {}, horizontalArrangement = Arrangement.spacedBy((-6).dp)) {
        initials.forEach {
            Box(Modifier.size(24.dp).border(2.dp, colors.bgSurface, CircleShape).padding(2.dp).clip(CircleShape).background(colors.initialsDiscBg), contentAlignment = Alignment.Center) {
                EqualText(text = it, style = EqualTheme.type.badge, color = colors.textPrimary)
            }
        }
    }
}

/** A line of a [ChatPreviewCard]. */
data class PreviewLine(val fromCaller: Boolean, val text: String)

/** A small, inert chat preview (What Equal picks up): "Ongoing call" pill and two bubbles. */
@Composable
fun ChatPreviewCard(lines: List<PreviewLine>, modifier: Modifier = Modifier) {
    val colors = EqualTheme.colors
    Column(
        modifier.fillMaxWidth().defaultMinSize(minHeight = 180.dp).clip(RoundedCornerShape(16.dp)).background(colors.bgPrimary).padding(12.dp).clearAndSetSemantics {},
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(Modifier.align(Alignment.CenterHorizontally).clip(CircleShape).background(colors.ongoingBarBg).padding(horizontal = 8.dp, vertical = 2.dp)) {
            EqualText(text = "Ongoing call", style = EqualTheme.type.badge, color = colors.ongoingBarText)
        }
        lines.forEach { l ->
            Box(Modifier.fillMaxWidth(), contentAlignment = if (l.fromCaller) Alignment.CenterStart else Alignment.CenterEnd) {
                Box(Modifier.widthIn(max = 220.dp).clip(RoundedCornerShape(12.dp)).background(if (l.fromCaller) colors.chatBubbleOtherBg else colors.chatBubbleUserBg).padding(horizontal = 10.dp, vertical = 6.dp)) {
                    EqualText(text = l.text, style = EqualTheme.type.captionRegular, color = colors.textPrimary)
                }
            }
        }
    }
}

/** [CallerContextCard] states. */
enum class CallerContextState { Empty, Learning, Filled, Paused }

/**
 * Caller context in Contact Info: "Equal’s Context for <name>" + Personalising… + a menu (Share ·
 * Stop personalising · Delete all learning); empty / learning hint, filled (3 lines + Read more,
 * thumbs) or paused (grey, Resume personalising). `caller-context/bg` (paused `…/paused-bg`).
 */
@Composable
fun CallerContextCard(
    name: String,
    state: CallerContextState,
    modifier: Modifier = Modifier,
    summary: String = "",
    onStop: () -> Unit = {},
    onDelete: () -> Unit = {},
    onResume: () -> Unit = {},
    onShare: () -> Unit = {},
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    val shape = RoundedCornerShape(16.dp)
    var menu by remember { mutableStateOf(false) }
    var more by remember { mutableStateOf(false) }
    Column(
        modifier.fillMaxWidth().clip(shape).background(if (state == CallerContextState.Paused) colors.callerContextPausedBg else colors.callerContextBg)
            .border(1.dp, colors.strokeSubtle, shape).padding(start = 12.dp, end = 4.dp, top = 8.dp, bottom = 8.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            EqualLogoOrb(size = 18.dp, contentDescription = null)
            EqualText(
                text = if (state == CallerContextState.Learning) "Learning more with every new call..." else "Equal’s Context for $name",
                style = type.caption.copy(fontWeight = FontWeight.W600), color = colors.textPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f).semantics { heading() },
            )
            if (state != CallerContextState.Paused) EqualText(text = "Personalising...", style = type.overline.copy(fontWeight = FontWeight.W600), color = colors.textAction, maxLines = 1)
            Box {
                SmallIcon(EqualIcons.MoreVert, "More options", { menu = !menu })
                ContextMenuPopup(
                    expanded = menu,
                    onDismissRequest = { menu = false },
                    items = listOf(
                        ContextMenuItem("Share", EqualIcons.Share, { menu = false; onShare() }),
                        ContextMenuItem("Stop personalising", EqualSymbols.stopCircle, { menu = false; onStop() }),
                        ContextMenuItem("Delete all learning", EqualIcons.Delete, { menu = false; onDelete() }, destructive = true),
                    ),
                    alignment = Alignment.TopEnd,
                )
            }
        }
        Column(Modifier.padding(end = 8.dp)) {
            if (state == CallerContextState.Empty || state == CallerContextState.Learning) {
                EqualText(
                    text = "A summary appears here after a few more calls with ${name.substringBefore(' ')}.",
                    style = type.captionWrap.copy(fontStyle = FontStyle.Italic), color = colors.textSecondary, textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                )
            } else {
                EqualText(text = summary, style = type.body1, color = colors.textSecondary, maxLines = if (more) Int.MAX_VALUE else 3, overflow = TextOverflow.Ellipsis)
                if (!more) {
                    Row(
                        Modifier.minimumInteractiveComponentSize().clickable(role = Role.Button, onClick = { more = true }),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        EqualText(text = "Read more", style = type.buttonSmall, color = colors.textAction)
                        EqualIcon(EqualIcons.ChevronDown, contentDescription = null, tint = colors.textAction, size = 16.dp)
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SmallIcon(EqualSymbols.thumbUp, "Helpful", {})
                    SmallIcon(EqualSymbols.thumbDown, "Not helpful", {})
                    Spacer(Modifier.weight(1f))
                    if (state == CallerContextState.Paused) EqualButton(text = "Resume personalising", onClick = onResume, size = EqualButtonSize.Xs)
                }
            }
        }
    }
}

