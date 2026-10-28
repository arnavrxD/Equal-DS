package ai.equal.ds.catalog

import ai.equal.ds.assets.EqualAssetImage
import ai.equal.ds.assets.EqualAssets
import ai.equal.ds.components.AvatarStack
import ai.equal.ds.components.CallerContextCard
import ai.equal.ds.components.CallerContextState
import ai.equal.ds.components.CarouselDots
import ai.equal.ds.components.ChatPreviewCard
import ai.equal.ds.components.EqualAppBar
import ai.equal.ds.components.EqualBottomSheet
import ai.equal.ds.components.EqualButton
import ai.equal.ds.components.EqualButtonAppearance
import ai.equal.ds.components.EqualButtonSize
import ai.equal.ds.components.EqualLogoOrb
import ai.equal.ds.components.EqualText
import ai.equal.ds.components.InfoNote
import ai.equal.ds.components.InfoRow
import ai.equal.ds.components.InfoRows
import ai.equal.ds.components.KeepLoseList
import ai.equal.ds.components.PreviewLine
import ai.equal.ds.components.RowGroup
import ai.equal.ds.components.SectionHeader
import ai.equal.ds.components.SettingsRow
import ai.equal.ds.components.TraitOrbHero
import ai.equal.ds.icons.EqualIcon
import ai.equal.ds.icons.EqualIcons
import ai.equal.ds.icons.EqualSymbols
import ai.equal.ds.theme.EqualTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp

/** Home with the "Equal is now getting to know your callers" FYI sheet. Port of web `PersonalisationFyiHome`. */
@Composable
fun PersonalisationFyiHomeSample(modifier: Modifier = Modifier, open: Boolean = true) {
    var show by remember(open) { mutableStateOf(open) }
    val colors = EqualTheme.colors
    HomeScreenSample(
        modifier,
        overlay = {
            EqualBottomSheet(visible = show, onDismissRequest = { show = false }, paneTitle = "Your Equal experience is now more personal.") {
                TraitOrbHero()
                EqualText(text = "Your Equal experience is now more personal.", style = EqualTheme.type.headline.copy(fontWeight = FontWeight.W700), color = colors.textPrimary, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                EqualText(text = "Equal learns from your calls, while you choose what shapes your future calls.", style = EqualTheme.type.body1, color = colors.textSecondary, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(16.dp))
                InfoRows(
                    listOf(
                        InfoRow(EqualSymbols.autoFix, "It builds up over time", "It gets a little sharper with every call. Nothing changes overnight."),
                        InfoRow(EqualSymbols.privacy, "Ignores sensitive topics", "Health, finances, legal, family, religion never used."),
                        InfoRow(EqualSymbols.silentMode, "Off for anyone you choose", "Switch it off from a caller’s profile, or in Settings."),
                    ),
                )
                Spacer(Modifier.height(20.dp))
                EqualButton(text = "Got it", onClick = { show = false }, size = EqualButtonSize.Block)
            }
        },
    )
}

private data class Pickup(val emoji: String, val title: String, val body: String, val lines: List<PreviewLine>)

private val Pickups = listOf(
    Pickup("📍", "Your places", "Deliveries, pickups and more, without you repeating yourself.", listOf(PreviewLine(true, "I am at the main gate. Which tower?"), PreviewLine(false, "Come in from Gate 2, Tower B. Take service lift, not the main one"), PreviewLine(true, "Got it."))),
    Pickup("👪", "Your people", "Calls handled differently for family, friends, colleagues and more.", listOf(PreviewLine(true, "Is he free? Papa’s appointment got moved to 4."), PreviewLine(false, "He’s in a meeting. Tell me and I’ll get it to him now. Which hospital?"), PreviewLine(true, "Fortis, Bannerghatta."))),
    Pickup("🔔", "Your preferences", "The calls you want to hear, and the ones you don’t, handled your way.", listOf(PreviewLine(true, "Calling from your bank regarding your id verification."), PreviewLine(false, "Got it. Please hold on while I connect you to him."), PreviewLine(true, "Sure."))),
)

/** The delete-learning sheet body (global, or for one [caller]). */
@Composable
fun ColumnScope.DeleteLearningContent(caller: String? = null, onDelete: () -> Unit = {}) {
    val colors = EqualTheme.colors
    val first = caller?.substringBefore(' ')
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        EqualAssetImage(EqualAssets.IllustrationTrash, size = 72.dp)
        Spacer(Modifier.height(16.dp))
        EqualText(text = if (caller != null) "Reset what Equal learned about $first?" else "Reset everything Equal has learned?", style = EqualTheme.type.title.copy(fontWeight = FontWeight.W700), color = colors.textPrimary, textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))
        EqualText(text = if (caller != null) "Clear what Equal has learned so far and start fresh with future calls." else "Clear what Equal has learned about all your callers and about you, and start fresh with future calls.", style = EqualTheme.type.body1, color = colors.textSecondary, textAlign = TextAlign.Center)
    }
    Spacer(Modifier.height(16.dp))
    if (caller != null) {
        KeepLoseList(stays = listOf("Who’s calling and their number"), goes = listOf("Why they usually call", "How you’d want the call answered", "Which language to answer in"))
    } else {
        KeepLoseList(stays = listOf("Your callers and their numbers", "Who you’ve excluded from personalisation"), goes = listOf("What Equal learned about each caller", "What Equal learned about you"))
    }
    Spacer(Modifier.height(16.dp))
    EqualText(text = if (caller != null) "Equal can start learning about them again if learning is turned on." else "Equal can start learning again from your next calls.", style = EqualTheme.type.bodyLarge, color = colors.textSecondary)
    Spacer(Modifier.height(20.dp))
    EqualButton(text = if (caller != null) "Reset learning" else "Reset all learnings", onClick = onDelete, size = EqualButtonSize.Block, appearance = EqualButtonAppearance.Critical, leadingIcon = EqualIcons.Delete)
}

/** AI Personalization: what Equal picks up, what it skips, excluded contacts and Delete all learning. Port of web `PersonalisationScreen`. */
@Composable
fun PersonalisationScreen(modifier: Modifier = Modifier, sheet: Boolean = false, onBack: () -> Unit = {}) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    var open by remember(sheet) { mutableStateOf(sheet) }
    Box(modifier.fillMaxSize().background(colors.bgPrimary)) {
        Column(Modifier.fillMaxSize()) {
            EqualAppBar(title = "Your Equal Experience", onBackClick = onBack, statusBar = true)
            Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) {
                Column(Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)) {
                    androidx.compose.foundation.text.BasicText(
                        text = buildAnnotatedString {
                            append("Your ")
                            withStyle(SpanStyle(color = colors.textAction)) { append("Equal") }
                            append(" experience, made more personal.")
                        },
                        style = type.headline.copy(fontWeight = FontWeight.W700, color = colors.textPrimary),
                    )
                    Spacer(Modifier.height(8.dp))
                    EqualText(text = "Equal learns from all your calls, with you choosing what shapes future calls.", style = type.bodyLarge, color = colors.textSecondary)
                }
                Spacer(Modifier.height(24.dp))
                SectionHeader(title = "WHAT BECOMES MORE PERSONAL", inset = 16.dp)
                Row(Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Pickups.forEach { p ->
                        Column(Modifier.width(312.dp).background(colors.bgSurface, RoundedCornerShape(16.dp)).padding(16.dp)) {
                            Box(Modifier.size(40.dp).background(colors.bgPrimary, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) { EqualText(text = p.emoji, style = type.title, color = colors.textPrimary) }
                            Spacer(Modifier.height(8.dp))
                            EqualText(text = p.title, style = type.body1.copy(fontWeight = FontWeight.W600), color = colors.textPrimary)
                            EqualText(text = p.body, style = type.captionRegular, color = colors.textSecondary)
                            Spacer(Modifier.height(12.dp))
                            ChatPreviewCard(p.lines)
                        }
                    }
                }
                CarouselDots(count = Pickups.size, active = 0)
                Box(Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)) {
                    InfoNote(title = "Sensitive details", body = "Equal doesn’t learn from sensitive details like OTPs, card numbers, UPI PINs and government IDs.")
                }
                Spacer(Modifier.height(24.dp))
                SectionHeader(title = "WHAT EQUAL DOESN’T LEARN", inset = 16.dp)
                RowGroup(inset = 16.dp) {
                    SettingsRow(
                        label = "Contacts you exclude",
                        description = "Equal still answers their calls, but won’t use your interactions with them to personalise future calls.",
                        descriptionWraps = true,
                        leadingIcon = EqualIcons.Person,
                    )
                    Row(Modifier.padding(start = 56.dp, bottom = 12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AvatarStack(listOf("AS", "AM", "BC"))
                        EqualText(text = "Aarav, Anjali, Brian", style = type.caption, color = colors.textPrimary)
                    }
                }
                Spacer(Modifier.height(24.dp))
                SectionHeader(title = "WANT TO START FRESH?", inset = 16.dp)
                RowGroup(inset = 16.dp) {
                    SettingsRow(label = "Reset all learnings", description = "Start fresh with future calls.", leadingIcon = EqualIcons.Delete, leadingIconBg = colors.expiryChipDangerBg, onClick = { open = true })
                }
                Spacer(Modifier.height(32.dp))
            }
        }
        EqualBottomSheet(visible = open, onDismissRequest = { open = false }, paneTitle = "Reset everything Equal has learned?") { DeleteLearningContent(onDelete = { open = false }) }
    }
}

/** You → AI Personalisation → the screen, live. */
@Composable
fun PersonalisationFlowSample(modifier: Modifier = Modifier) {
    var screen by remember { mutableStateOf(false) }
    if (screen) PersonalisationScreen(modifier, onBack = { screen = false }) else YouScreenSample(modifier, noticed = YouNoticed.Ready, onPersonalisation = { screen = true })
}

private const val AmitContext = "Amit manages your flat in Koramangala. He calls about rent and maintenance, almost always after 7 PM, and prefers a quick call to a message. He speaks Hindi with you but switches to English for anything involving paperwork, and he likes a heads-up a day before any visit."

/** Which sheet is open over [CallerContextScreen]. */
enum class CallerContextSheet { Stop, Delete }

/** Contact Info with the caller context card; its menu opens the stop and delete sheets. Port of web `CallerContextScreen`. */
@Composable
fun CallerContextScreen(modifier: Modifier = Modifier, state: CallerContextState = CallerContextState.Filled, sheet: CallerContextSheet? = null) {
    var s by remember(state) { mutableStateOf(state) }
    var open by remember(sheet) { mutableStateOf(sheet) }
    val colors = EqualTheme.colors
    val name = "Amit Nanda"
    ContactInfoScreen(
        modifier = modifier,
        name = name,
        initials = "AN",
        category = null,
        context = { CallerContextCard(name = name, state = s, summary = AmitContext, onStop = { open = CallerContextSheet.Stop }, onDelete = { open = CallerContextSheet.Delete }, onResume = { s = CallerContextState.Filled }) },
        overlay = {
            EqualBottomSheet(visible = open == CallerContextSheet.Stop, onDismissRequest = { open = null }, paneTitle = "Stop learning about $name?") {
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(contentAlignment = Alignment.Center) {
                        EqualLogoOrb(size = 48.dp, contentDescription = null)
                        EqualIcon(EqualSymbols.stopCircle, contentDescription = null, tint = colors.textOnAction, size = 24.dp)
                    }
                    Spacer(Modifier.height(12.dp))
                    EqualText(text = "Stop learning about $name?", style = EqualTheme.type.title.copy(fontWeight = FontWeight.W700), color = colors.textPrimary, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(8.dp))
                    EqualText(text = "Equal keeps what it’s learned, but won’t learn from future calls.", style = EqualTheme.type.bodyLarge, color = colors.textSecondary, textAlign = TextAlign.Center)
                }
                Spacer(Modifier.height(16.dp))
                KeepLoseList(stays = listOf("Everything Equal knows today"), goes = listOf("New information from future calls"))
                Spacer(Modifier.height(16.dp))
                EqualText(text = "You can turn this back on anytime.", style = EqualTheme.type.bodyLarge, color = colors.textSecondary)
                Spacer(Modifier.height(20.dp))
                EqualButton(text = "Stop learning", onClick = { s = CallerContextState.Paused; open = null }, size = EqualButtonSize.Block, leadingIcon = EqualSymbols.stopCircle)
            }
            EqualBottomSheet(visible = open == CallerContextSheet.Delete, onDismissRequest = { open = null }, paneTitle = "Reset what Equal learned about ${name.substringBefore(' ')}?") {
                DeleteLearningContent(caller = name, onDelete = { open = null })
            }
        },
    )
}

