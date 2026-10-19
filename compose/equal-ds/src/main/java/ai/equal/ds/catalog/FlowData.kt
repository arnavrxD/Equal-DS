package ai.equal.ds.catalog

import ai.equal.ds.components.Instruction
import ai.equal.ds.components.InstructionSource
import ai.equal.ds.components.PresetTint
import ai.equal.ds.components.SavedAddress
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

// Sample content for the Profile / Addresses / Instructions flows (copy from the flow designs).
// Port of web/src/screens/flowData.ts.

val SampleAddresses: List<SavedAddress> = listOf(
    SavedAddress(id = "a1", label = "Home", line1 = "House no. 4, Tony Street", line2 = "Financial District", city = "Hyderabad", pincode = "500032"),
    SavedAddress(id = "a2", label = "Work", line1 = "House no. 4, Tony Street", line2 = "Financial District", city = "Hyderabad", pincode = "500032"),
    SavedAddress(id = "a3", label = "Arnav’s Place", line1 = "House no. 4, Tony Street", line2 = "Financial District", city = "Hyderabad", pincode = "500032"),
    SavedAddress(
        id = "a4",
        label = "Work",
        line1 = "Flat 4B, Block C, Skyline Towers, Phase 2, HITECH city",
        line2 = "MG Road, Opposite City Center Mall, Banjara Hills",
        city = "Hyderabad",
        pincode = "500034",
        direction = "Near City Center Mall",
    ),
)

/** A preset bundle of instructions ("Browse presets"). */
data class Preset(
    val id: String,
    val emoji: String,
    val tint: PresetTint,
    val title: String,
    val description: String,
    val instructions: List<Instruction>,
)

private val Screen = InstructionSource("🛡️", "Screen & Protect")
private val Everyday = InstructionSource("📞", "Handle everyday calls")
private val Business = InstructionSource("💼", "Run my business")

object InstructionLibrary {
    val declineSales = Instruction(
        id = "decline-sales",
        title = "Decline sales, promo spam calls",
        source = Screen,
        whenText = "The caller is selling loan, insurance, credit card, product, or another unsolicited offer",
        doText = "Politely say I am not interested and end the conversation without discussing the offer",
    )
    val loanEmi = Instruction(
        id = "loan-emi",
        title = "Handle loan, EMI & recovery calls safely",
        source = Business,
        whenText = "The caller discusses a loan, EMI overdue balance, recovery, or legal notice",
        doText = "Note the lender, amount and due date, say I will review and respond and never confirm payment, identity, account details, or negotiate",
    )
    val difficult = Instruction(
        id = "difficult",
        title = "Stay brief and calm with difficult calls",
        source = Everyday,
        whenText = "The caller is automated, repetitive, abusive or rude",
        doText = "Stay calm or brief, do not argue, and end politely, if the caller will not state a legitimate purpose",
    )
    val takeMessage = Instruction(
        id = "take-message",
        title = "Take a message & callback request",
        source = Everyday,
        whenText = "A genuine caller has clear reason to speak with me and I am unavailable",
        doText = "Collect their name, callback number and message, then say you will pass the message to me",
    )
    val delivery = Instruction(
        id = "delivery",
        title = "Handle delivery and courier calls",
        source = Everyday,
        whenText = "the caller is delivering food, a parcel, documents, or goods",
        doText = "share only {landmark}, ask them to wait for {wait}, and never ask for or share an OTP or full address",
    )
    val wrongNumber = Instruction(
        id = "wrong-number",
        title = "Close wrong number calls politely",
        source = Screen,
        whenText = "The caller has the wrong number",
        doText = "Inform them and close without sharing details",
    )
}

val Presets: List<Preset> = listOf(
    Preset(
        id = "screen",
        emoji = "🛡️",
        tint = PresetTint.Green,
        title = "Screen & Protect",
        description = "Decline spam, hold the line on loan and payments call, close wrong call",
        instructions = listOf(InstructionLibrary.declineSales, InstructionLibrary.loanEmi, InstructionLibrary.difficult),
    ),
    Preset(
        id = "everyday",
        emoji = "📞",
        tint = PresetTint.Blue,
        title = "Handle everyday calls",
        description = "Take message, guide deliveries, relay to family, start in your language",
        instructions = listOf(InstructionLibrary.takeMessage, InstructionLibrary.delivery, InstructionLibrary.difficult),
    ),
    Preset(
        id = "business",
        emoji = "💼",
        tint = PresetTint.Orange,
        title = "Run my business",
        description = "Capture enquiries, orders, bookings, job, and supplier calls",
        instructions = listOf(InstructionLibrary.loanEmi, InstructionLibrary.takeMessage),
    ),
)

/** A "Recommended for you" card, the instruction it sets up, and the chat opener. */
data class Recommendation(
    val id: String,
    val emoji: String,
    val title: String,
    val description: String,
    val ask: String,
    val instruction: Instruction,
)

val Recommendations: List<Recommendation> = listOf(
    Recommendation(
        id = "rec-decline",
        emoji = "🚫",
        title = "Decline sales & spam",
        description = "Politely declines sales and insurance calls.",
        ask = "Decline Sales & Spam calls",
        instruction = InstructionLibrary.declineSales.copy(
            title = "Decline sales calls",
            whenText = "Caller is selling a loan, insurance or product",
            doText = "Politely decline and end the conversation",
        ),
    ),
    Recommendation(
        id = "rec-message",
        emoji = "💬",
        title = "Take a message",
        description = "Takes their name, number and message.",
        ask = "Take a message & Callback request",
        instruction = InstructionLibrary.takeMessage,
    ),
)

/** A follow-up question the setup chat asks; the answer fills `{key}` in the Do line. */
data class FollowUp(val key: String, val question: String)

/** Follow-up questions for instructions that need input from the user. */
val FollowUps: Map<String, List<FollowUp>> = mapOf(
    "delivery" to listOf(
        FollowUp("landmark", "Which landmark should I share with delivery partners?"),
        FollowUp("wait", "How long should I ask them to wait before calling again?"),
    ),
)

/** A Silent Mode category toggle. */
data class SilentCategory(val id: String, val label: String, val description: String)

val SilentCategories: List<SilentCategory> = listOf(
    SilentCategory("loan", "Loan Offers", "AI will mute loan promotional calls"),
    SilentCategory("card", "Credit Cards", "AI will mute credit card offer calls"),
    SilentCategory("job", "Job Offers", "Mute promotional calls automatically"),
    SilentCategory("estate", "Real Estate", "AI will mute property promotional calls"),
    SilentCategory("others", "Others", "AI will mute promotional calls not listed above"),
)

private val Placeholder = Regex("\\{(\\w+)\\}")

/** The Do line with each `{key}` replaced by its answer in Medium weight ("…" until answered). */
fun fillTemplate(template: String, answers: Map<String, String>): AnnotatedString = buildAnnotatedString {
    var last = 0
    for (match in Placeholder.findAll(template)) {
        append(template.substring(last, match.range.first))
        withStyle(SpanStyle(fontWeight = FontWeight.W500)) {
            append(answers[match.groupValues[1]] ?: "…")
        }
        last = match.range.last + 1
    }
    append(template.substring(last))
}

/** The Do line with each `{key}` replaced by its answer (empty if missing). */
fun plainTemplate(template: String, answers: Map<String, String>): String =
    Placeholder.replace(template) { answers[it.groupValues[1]].orEmpty() }
