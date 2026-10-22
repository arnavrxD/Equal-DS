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
            EqualBottomSheet(visible = show, onDismissRequest = { show = false }, paneTitle = "Equal is now getting to know your callers") {
                TraitOrbHero()
                EqualText(text = "Equal is now getting to know your callers", style = EqualTheme.type.headline.copy(fontWeight = FontWeight.W700), color = colors.textPrimary, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                EqualText(text = "Over the next few calls, it’ll start handling each caller the way you would.", style = EqualTheme.type.body1, color = colors.textSecondary, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(16.dp))
                InfoRows(
                    listOf(
                        InfoRow(EqualSymbols.autoFix, "It builds up over time", "It gets a little sharper with every call. Nothing changes overnight."),
                        InfoRow(EqualSymbols.privacy, "Ignores sensitive topics", "Health, finances, legal, family, religion never used."),
                        InfoRow(EqualSymbols.silentMode, "Off for anyone you choose", "Switch it off from a caller’s profile, or in Settings."),
                    ),
                )
                Spacer(Modifier.height(20.dp))
                EqualButton(text = "Got it!", onClick = { show = false }, size = EqualButtonSize.Block)
            }
        },
    )
}

private data class Pickup(val emoji: String, val title: String, val body: String, val lines: List<PreviewLine>)

private val Pickups = listOf(
    Pickup("📍", "Your addresses & directions", "To take delivery and pickup calls for you without your intervention.", listOf(PreviewLine(true, "Ma’am, I’m at the gate. Which tower?"), PreviewLine(false, "Gate 2, Tower B. Leave it with the guard, please."))),
    Pickup("👪", "Your relationships", "Helps respond differently to family, friends, coworkers, and businesses.", listOf(PreviewLine(true, "Beta, it’s urgent. Call me back."), PreviewLine(false, "I’ll tell Keshav right away. What happened?"))),
    Pickup("🔔", "Your call preferences", "Understands what type of promotional calls you are interested in and which ones annoy you.", listOf(PreviewLine(true, "Sir, a pre-approved loan offer…"), PreviewLine(false, "Not interested, thanks. Please remove this number."))),
)

/** The delete-learning sheet body (global, or for one [caller]). */
@Composable
fun ColumnScope.DeleteLearningContent(caller: String? = null, onDelete: () -> Unit = {}) {
    val colors = EqualTheme.colors
    val first = caller?.substringBefore(' ')
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        EqualAssetImage(EqualAssets.IllustrationTrash, size = 72.dp)
        Spacer(Modifier.height(16.dp))
        EqualText(text = if (caller != null) "Delete what Equal knows about $caller?" else "Delete what Equal knows?", style = EqualTheme.type.title.copy(fontWeight = FontWeight.W700), color = colors.textPrimary, textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))
        EqualText(text = if (caller != null) "This will erase everything Equal has learned about $first and remove their personalisation." else "This will erase everything Equal has learned about you and remove all personalisations.", style = EqualTheme.type.body1, color = colors.textSecondary, textAlign = TextAlign.Center)
    }
    Spacer(Modifier.height(16.dp))
    KeepLoseList(stays = listOf("Who’s calling and their number"), goes = listOf("Why they usually call", "How you’d want the call answered"))
    Spacer(Modifier.height(16.dp))
    EqualText(text = if (caller != null) "Equal can start learning about them again if learning is turned on." else "Equal will start learning about you and allowed contacts again from scratch.", style = EqualTheme.type.bodyLarge, color = colors.textSecondary)
    Spacer(Modifier.height(20.dp))
    EqualButton(text = "Delete all learning", onClick = onDelete, size = EqualButtonSize.Block, appearance = EqualButtonAppearance.Critical, leadingIcon = EqualIcons.Delete)
}

/** AI Personalization: what Equal picks up, what it skips, excluded contacts and Delete all learning. Port of web `PersonalisationScreen`. */
@Composable
fun PersonalisationScreen(modifier: Modifier = Modifier, sheet: Boolean = false, onBack: () -> Unit = {}) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    var open by remember(sheet) { mutableStateOf(sheet) }
    Box(modifier.fillMaxSize().background(colors.bgPrimary)) {
        Column(Modifier.fillMaxSize()) {
            EqualAppBar(title = "AI Personalization", onBackClick = onBack, statusBar = true, align = ai.equal.ds.components.AppBarAlign.Start)
            Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) {
                Column(Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)) {
                    androidx.compose.foundation.text.BasicText(
                        text = buildAnnotatedString {
                            append("What ")
                            withStyle(SpanStyle(color = colors.textAction)) { append("Equal AI") }
                            append(" knows about you")
                        },
                        style = type.headline.copy(fontWeight = FontWeight.W700, color = colors.textPrimary),
                    )
                    Spacer(Modifier.height(8.dp))
                    EqualText(text = "The more Equal understands, the better it sounds like you when it answers.", style = type.bodyLarge, color = colors.textSecondary)
                }
                Spacer(Modifier.height(24.dp))
                SectionHeader(title = "WHAT EQUAL PICKS UP", inset = 16.dp)
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
                    InfoNote(title = "Ignored on purpose", body = "OTPs, card numbers, UPI PINs and government IDs are never saved, on any call.")
                }
                Spacer(Modifier.height(24.dp))
                SectionHeader(title = "WHAT EQUAL SKIPS OUT", inset = 16.dp)
                RowGroup(inset = 16.dp) {
                    SettingsRow(
                        label = "Excluded contacts from personalisation",
                        description = "Equal still answers these calls when you can’t. It just won’t listen in when you’re the one talking.",
                        descriptionWraps = true,
                        leadingIcon = EqualIcons.Person,
                    )
                    Row(Modifier.padding(start = 56.dp, bottom = 12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AvatarStack(listOf("AS", "AM", "BC"))
                        EqualText(text = "Aarav, Anjali, Brian", style = type.caption, color = colors.textPrimary)
                    }
                }
                Spacer(Modifier.height(24.dp))
                SectionHeader(title = "MANAGE LEARNING", inset = 16.dp)
                RowGroup(inset = 16.dp) {
                    SettingsRow(label = "Delete all learning", description = "Deletes all data acquired about you", leadingIcon = EqualIcons.Delete, leadingIconBg = colors.expiryChipDangerBg, onClick = { open = true })
                }
                Spacer(Modifier.height(32.dp))
            }
        }
        EqualBottomSheet(visible = open, onDismissRequest = { open = false }, paneTitle = "Delete what Equal knows?") { DeleteLearningContent(onDelete = { open = false }) }
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
            EqualBottomSheet(visible = open == CallerContextSheet.Stop, onDismissRequest = { open = null }, paneTitle = "Stop personalising for $name?") {
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(contentAlignment = Alignment.Center) {
                        EqualLogoOrb(size = 48.dp, contentDescription = null)
                        EqualIcon(EqualSymbols.stopCircle, contentDescription = null, tint = colors.textOnAction, size = 24.dp)
                    }
                    Spacer(Modifier.height(12.dp))
                    EqualText(text = "Stop personalising for $name?", style = EqualTheme.type.title.copy(fontWeight = FontWeight.W700), color = colors.textPrimary, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(8.dp))
                    EqualText(text = "Equal will remember everything it has already learned about this caller, but won’t learn anything new.", style = EqualTheme.type.bodyLarge, color = colors.textSecondary, textAlign = TextAlign.Center)
                }
                Spacer(Modifier.height(16.dp))
                KeepLoseList(stays = listOf("Everything it knows today"), goes = listOf("Anything that changes ahead"))
                Spacer(Modifier.height(16.dp))
                EqualText(text = "You can resume learning anytime.", style = EqualTheme.type.bodyLarge, color = colors.textSecondary)
                Spacer(Modifier.height(20.dp))
                EqualButton(text = "Stop learning", onClick = { s = CallerContextState.Paused; open = null }, size = EqualButtonSize.Block, leadingIcon = EqualSymbols.stopCircle)
            }
            EqualBottomSheet(visible = open == CallerContextSheet.Delete, onDismissRequest = { open = null }, paneTitle = "Delete what Equal knows about $name?") {
                DeleteLearningContent(caller = name, onDelete = { open = null })
            }
        },
    )
}

