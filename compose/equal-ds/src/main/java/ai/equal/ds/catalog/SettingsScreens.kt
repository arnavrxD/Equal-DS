package ai.equal.ds.catalog

import ai.equal.ds.assets.EqualAssetImage
import ai.equal.ds.assets.EqualAssets
import ai.equal.ds.components.CarouselDots
import ai.equal.ds.components.EqualAppBar
import ai.equal.ds.components.EqualBottomSheet
import ai.equal.ds.components.EqualButton
import ai.equal.ds.components.EqualButtonSize
import ai.equal.ds.components.EqualFrameHeightDp
import ai.equal.ds.components.EqualFrameWidthDp
import ai.equal.ds.components.EqualText
import ai.equal.ds.components.FeatureBullet
import ai.equal.ds.components.FeatureIntroSheet
import ai.equal.ds.components.Instruction
import ai.equal.ds.components.InstructionCard
import ai.equal.ds.components.LearningCard
import ai.equal.ds.components.LearningCards
import ai.equal.ds.components.RowGroup
import ai.equal.ds.components.SectionHeader
import ai.equal.ds.components.SettingsRow
import ai.equal.ds.components.ToggleRow
import ai.equal.ds.components.TrustStats
import ai.equal.ds.components.TrustStrip
import ai.equal.ds.icons.EqualIcons
import ai.equal.ds.icons.EqualSymbols
import ai.equal.ds.theme.EqualTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

// Updated UI: Call Learning & Consent, Account & Data, and the Instructions promo + its
// new-feature sheet (port of web SettingsScreens.tsx).

/** Card width + gap of the Call Learning carousel (320 + 12). */
private val LearningStep = 332.dp

/**
 * Call Learning & Consent: the "Allow AI to learn from calls" toggle card, YOUR ASSISTANT, the
 * horizontally scrolling [LearningCard]s with [CarouselDots]. Turning the toggle off asks first:
 * the "Turn off Call Learning?" [EqualBottomSheet] (robot mascot, Turn off). [on] sets the start
 * state and [sheet] pins the sheet open (docs).
 */
@Composable
fun CallLearningScreen(
    modifier: Modifier = Modifier,
    on: Boolean = true,
    sheet: Boolean = false,
    onBack: () -> Unit = {},
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    var learning by remember(on) { mutableStateOf(on) }
    var sheetOpen by remember(sheet) { mutableStateOf(sheet) }
    val scroll = rememberScrollState()
    val stepPx = with(LocalDensity.current) { LearningStep.toPx() }
    val slide by remember { derivedStateOf { (scroll.value / stepPx).roundToInt().coerceIn(0, LearningCards.lastIndex) } }
    Box(
        modifier
            .fillMaxSize()
            .background(colors.bgPrimary),
    ) {
        Column(Modifier.fillMaxSize()) {
            EqualAppBar(title = "Call Learning & Consent", onBackClick = onBack, statusBar = true)
            Column(
                Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
            ) {
                Box(
                    Modifier
                        .padding(start = 16.dp, top = 16.dp, end = 16.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(colors.bgSurface),
                ) {
                    ToggleRow(
                        label = "Allow AI to learn from calls",
                        checked = learning,
                        onCheckedChange = { v -> if (v) learning = true else sheetOpen = true },
                        descriptionWraps = true,
                        description = "When this is on, assistant pays attention to how your calls go so it can handle them more like you would, remembering your preferences, your addresses, and the people who matter. This is only ever used to make your experience better. Nothing is sold, and nothing is shared with anyone.",
                    )
                }
                Spacer(Modifier.height(24.dp))
                SectionHeader(title = "Your assistant", inset = 16.dp)
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .horizontalScroll(scroll)
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    LearningCards.forEach { LearningCard(it) }
                }
                CarouselDots(count = LearningCards.size, active = slide)
                Spacer(Modifier.height(24.dp))
            }
        }
        EqualBottomSheet(
            visible = sheetOpen,
            onDismissRequest = { sheetOpen = false },
            paneTitle = "Turn off Call Learning?",
        ) {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                EqualAssetImage(EqualAssets.MascotRobot, 96.dp)
                Spacer(Modifier.height(12.dp))
                EqualText(
                    text = "Turn off Call Learning?",
                    style = type.title.copy(fontWeight = FontWeight.SemiBold),
                    color = colors.textPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.semantics { heading() },
                )
                Spacer(Modifier.height(8.dp))
                EqualText(
                    text = "The assistant won’t learn your preferences, recognise frequent callers, or adapt to how you handle conversations.",
                    style = type.body1,
                    color = colors.textSecondary,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(24.dp))
                EqualButton(
                    text = "Turn off",
                    onClick = {
                        learning = false
                        sheetOpen = false
                    },
                    size = EqualButtonSize.Block,
                )
            }
        }
    }
}

/**
 * Account & Data: the 140dp shield, "Trust is the foundation of everything we do at Equal AI"
 * (`headline` SemiBold, Equal AI in `text/action`), the [TrustStrip], then PRIVACY & SECURITY
 * (Privacy Policy; Call Learning & Consent → [onCallLearning]), LEGAL (Terms & Conditions) and
 * ACCOUNT (Logout; Delete account on `expiry-chip/danger-bg`).
 */
@Composable
fun AccountDataScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onCallLearning: () -> Unit = {},
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    val trustLine = buildAnnotatedString {
        append("Trust is the foundation of everything we do at ")
        withStyle(SpanStyle(color = colors.textAction)) { append("Equal AI") }
    }
    Column(
        modifier
            .fillMaxSize()
            .background(colors.bgPrimary),
    ) {
        EqualAppBar(title = "Account & Data", onBackClick = onBack, statusBar = true)
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, top = 16.dp, end = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                EqualAssetImage(EqualAssets.IllustrationShieldLock, 140.dp)
                Spacer(Modifier.height(8.dp))
                EqualText(
                    text = trustLine,
                    style = type.headline.copy(fontWeight = FontWeight.SemiBold),
                    color = colors.textPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.widthIn(max = 350.dp).semantics { heading() },
                )
                Spacer(Modifier.height(16.dp))
                TrustStrip(stats = TrustStats)
            }
            Spacer(Modifier.height(24.dp))
            SectionHeader(title = "Privacy & Security")
            RowGroup {
                SettingsRow(label = "Privacy Policy", description = "How your data is used and protected", leadingIcon = EqualSymbols.privacy, onClick = {})
                SettingsRow(label = "Call Learning & Consent", description = "Manage data sharing", leadingIcon = EqualSymbols.key, onClick = onCallLearning)
            }
            Spacer(Modifier.height(24.dp))
            SectionHeader(title = "Legal")
            RowGroup {
                SettingsRow(label = "Terms & Conditions", description = "Read our terms of service", leadingIcon = EqualSymbols.article, onClick = {})
            }
            Spacer(Modifier.height(24.dp))
            SectionHeader(title = "Account")
            RowGroup {
                SettingsRow(label = "Logout", description = "Sign out of your account", leadingIcon = EqualSymbols.logout, onClick = {})
                SettingsRow(
                    label = "Delete Account",
                    description = "Permanently delete your account and data",
                    leadingIcon = EqualIcons.Delete,
                    leadingIconBg = colors.expiryChipDangerBg,
                    onClick = {},
                )
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}

/** Account & Data → Call Learning & Consent, live (Back returns). */
@Composable
fun AccountDataFlowSample(modifier: Modifier = Modifier) {
    var learning by remember { mutableStateOf(false) }
    if (learning) {
        CallLearningScreen(modifier = modifier, onBack = { learning = false })
    } else {
        AccountDataScreen(modifier = modifier, onCallLearning = { learning = true })
    }
}

/** The loan-offer preset shown in the Instructions new-feature sheet's hero. */
val LoanInstruction: Instruction = Instruction(
    id = "loan",
    title = "Loan offers",
    whenText = "I get a loan offer call",
    doText = "Say I’m not interested and ask them to remove my number",
)

/** The Instructions new-feature sheet's bullets. */
val InstructionsIntroBullets: List<FeatureBullet> = listOf(
    FeatureBullet(EqualIcons.Edit, "Tell your assistant what to do on any call, in your own words"),
    FeatureBullet(EqualSymbols.touch, "Start in one tap with presets for spam, loan and delivery calls"),
    FeatureBullet(EqualIcons.CallLog, "See which instruction was used in every call summary"),
)

/**
 * Home with the Teach Your Assistant promo ([HomeScreenSample] `instructionsPromo`); Try
 * Instructions opens the Instructions [FeatureIntroSheet] (hero: the loan [InstructionCard]).
 * [sheet] pins the sheet open (docs). Try Instructions, Not now, the close and the scrim close it.
 */
@Composable
fun InstructionsPromoHomeSample(
    modifier: Modifier = Modifier,
    sheet: Boolean = false,
) {
    var open by remember(sheet) { mutableStateOf(sheet) }
    HomeScreenSample(
        modifier = modifier,
        instructionsPromo = true,
        onPromo = { open = true },
        overlay = if (open) {
            {
                FeatureIntroSheet(
                    title = "Get every call handled your way with Instructions",
                    bullets = InstructionsIntroBullets,
                    cta = "Try Instructions",
                    hero = { InstructionCard(instruction = LoanInstruction, modifier = Modifier.widthIn(max = 320.dp)) },
                    onCta = { open = false },
                    secondary = "Not now",
                    onSecondary = { open = false },
                    onClose = { open = false },
                )
            }
        } else {
            null
        },
    )
}

// ---------------------------------------------------------------------------------------------
// Previews
// ---------------------------------------------------------------------------------------------

@Preview(name = "Call Learning · light", widthDp = EqualFrameWidthDp, heightDp = EqualFrameHeightDp, showBackground = true)
@Composable
private fun CallLearningLightPreview() {
    EqualTheme(darkTheme = false) { CallLearningScreen() }
}

@Preview(name = "Call Learning · off sheet · dark", widthDp = EqualFrameWidthDp, heightDp = EqualFrameHeightDp, showBackground = true)
@Composable
private fun CallLearningSheetDarkPreview() {
    EqualTheme(darkTheme = true) { CallLearningScreen(sheet = true) }
}

@Preview(name = "Account & Data · light", widthDp = EqualFrameWidthDp, heightDp = 1100, showBackground = true)
@Composable
private fun AccountDataLightPreview() {
    EqualTheme(darkTheme = false) { AccountDataScreen() }
}

@Preview(name = "Account & Data · dark", widthDp = EqualFrameWidthDp, heightDp = 1100, showBackground = true)
@Composable
private fun AccountDataDarkPreview() {
    EqualTheme(darkTheme = true) { AccountDataScreen() }
}

@Preview(name = "Instructions promo · sheet · light", widthDp = EqualFrameWidthDp, heightDp = EqualFrameHeightDp, showBackground = true)
@Composable
private fun InstructionsPromoSheetLightPreview() {
    EqualTheme(darkTheme = false) { InstructionsPromoHomeSample(sheet = true) }
}

@Preview(name = "Splash · dark", widthDp = EqualFrameWidthDp, heightDp = EqualFrameHeightDp, showBackground = true)
@Composable
private fun SplashDarkPreview() {
    EqualTheme(darkTheme = true) { SplashScreenSample() }
}
