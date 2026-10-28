package ai.equal.ds.components

import ai.equal.ds.assets.EqualCategory
import ai.equal.ds.icons.EqualIcon
import ai.equal.ds.icons.EqualIcons
import ai.equal.ds.icons.EqualSymbols
import ai.equal.ds.theme.EqualTheme
import ai.equal.ds.theme.eqPressFeedback
import ai.equal.ds.theme.tabular
import ai.equal.ds.tokens.EqualRadius
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.selection.toggleable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Missed-call card: the thread card for a call the assistant couldn't pick up. Three cases, one
// component: we know why they call (reason + caller art), we know only the name, or we know nothing.
// Mirrors web/src/components/MissedCall.tsx + MissedCall.css.

/** Was the "likely calls for" reason right? `null` = not rated. */
enum class CallFeedback { Up, Down }

/**
 * Thread card for a call the assistant missed (width [width], shrinks to fit), gap 10 to the thumbs.
 * Card: `bg/surface`, radius 16. Head (margin 6, padding 10 / 12, radius 12, `missed-call/bg`,
 * 1dp `missed-call/stroke`, gap 12):
 * - with a [reason] and [category]: the 48dp caller art with a 20dp missed badge
 *   (`missed-call/icon-bg`, phone-missed-fill 12 `missed-call/icon`, 2dp `missed-call/bg` ring);
 * - otherwise the 48dp `missed-call/icon-bg` disc with phone-missed-fill 24.
 *
 * Title `subheading`: "[caller] Called", or "Missed Call" when [caller] is null; then
 * why `body2` Regular `text/secondary`. With a [reason]: "USUALLY CALLS FOR" `overline` + the
 * reason `body1` Regular `text/secondary` (bold spans kept) + [time]; without, [time] sits under
 * the head text. Then the 48dp Call back row (call 20 + `subheading` in `text/action`, 1dp
 * `stroke/subtle` top). [onFeedback] adds 40dp 👍 / 👎 toggles (on: filled icon, `text/action`
 * icon and stroke); tapping the selected one clears it.
 */
@Composable
fun MissedCallCard(
    onCallBack: () -> Unit,
    modifier: Modifier = Modifier,
    caller: String? = null,
    reason: AnnotatedString? = null,
    category: EqualCategory? = null,
    time: String? = null,
    callBackLabel: String = "Call back",
    feedback: CallFeedback? = null,
    onFeedback: ((CallFeedback?) -> Unit)? = null,
    width: Dp = 320.dp,
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    val title = if (caller != null) "$caller Called" else "Missed Call"
    val body = "Missed by Assistant"
    val art = if (reason != null) category else null
    Column(modifier.widthIn(max = width).width(width), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Column(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(EqualRadius.rLg))
                .background(colors.bgSurface),
        ) {
            val headShape = RoundedCornerShape(12.dp)
            Row(
                Modifier
                    .padding(6.dp)
                    .fillMaxWidth()
                    .background(colors.missedCallBg, headShape)
                    .border(1.dp, colors.missedCallStroke, headShape)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                if (art != null) {
                    Box(Modifier.size(48.dp)) {
                        CategoryAvatar(category = art, size = 48.dp, contentDescription = null)
                        Box(
                            Modifier
                                .align(Alignment.BottomEnd)
                                .offset(x = 2.dp, y = 2.dp)
                                .size(24.dp)
                                .background(colors.missedCallBg, CircleShape)
                                .padding(2.dp)
                                .background(colors.missedCallIconBg, CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            EqualIcon(EqualSymbols.phoneMissedFill, contentDescription = null, tint = colors.missedCallIcon, size = 12.dp)
                        }
                    }
                } else {
                    Box(Modifier.size(48.dp).background(colors.missedCallIconBg, CircleShape), contentAlignment = Alignment.Center) {
                        EqualIcon(EqualSymbols.phoneMissedFill, contentDescription = null, tint = colors.missedCallIcon, size = 24.dp)
                    }
                }
                Column(Modifier.weight(1f)) {
                    EqualText(text = title, style = type.subheading, color = colors.textPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    EqualText(text = body, style = type.body2.copy(fontWeight = FontWeight.Normal), color = colors.textSecondary)
                    if (reason == null && time != null) {
                        EqualText(
                            text = time,
                            style = type.micro.tabular(),
                            color = colors.textSecondary,
                            maxLines = 1,
                            modifier = Modifier.align(Alignment.End).padding(top = 2.dp),
                        )
                    }
                }
            }
            if (reason != null) {
                Column(
                    Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    EqualText(text = "USUALLY CALLS FOR", style = type.overline, color = colors.textSecondary)
                    BasicText(text = reason, style = type.body1.copy(fontWeight = FontWeight.Normal, color = colors.textSecondary))
                    if (time != null) {
                        EqualText(text = time, style = type.micro.tabular(), color = colors.textPrimary, maxLines = 1, modifier = Modifier.align(Alignment.End))
                    }
                }
            }
            EqualDivider()
            val interaction = rememberInteraction()
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .eqPressFeedback(interaction, RoundedCornerShape(0.dp))
                    .clickable(interactionSource = interaction, indication = null, role = Role.Button, onClick = onCallBack),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            ) {
                EqualIcon(EqualIcons.Call, contentDescription = null, tint = colors.textAction, size = 20.dp)
                EqualText(text = callBackLabel, style = type.subheading, color = colors.textAction, maxLines = 1)
            }
        }
        if (onFeedback != null) {
            Row(
                Modifier.semantics { contentDescription = "Was this helpful?" },
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                CallFeedback.entries.forEach { v ->
                    val on = feedback == v
                    val up = v == CallFeedback.Up
                    val icon = when {
                        up && on -> EqualSymbols.thumbUpFill
                        up -> EqualSymbols.thumbUp
                        on -> EqualSymbols.thumbDownFill
                        else -> EqualSymbols.thumbDown
                    }
                    val tint = if (on) colors.textAction else colors.textSecondary
                    val interaction = rememberInteraction()
                    // 48dp touch area around the 40dp visual (layout unchanged).
                    Box(
                        Modifier
                            .touchTarget()
                            .toggleable(value = on, interactionSource = interaction, indication = null, role = Role.Button) {
                                onFeedback(if (on) null else v)
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Box(
                            Modifier
                                .eqPressFeedback(interaction, CircleShape)
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(colors.bgSurface)
                                .border(1.dp, if (on) colors.textAction else colors.strokeSubtle, CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            EqualIcon(icon, contentDescription = if (up) "Helpful" else "Not helpful", tint = tint, size = 20.dp)
                        }
                    }
                }
            }
        }
    }
}

/** The sample reason: "Promote **L&T Realty residential** projects." */
val SampleMissedReason: AnnotatedString = buildAnnotatedString {
    append("Promote ")
    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("L&T Realty residential") }
    append(" projects.")
}

// ---------------------------------------------------------------------------------------------
// Previews
// ---------------------------------------------------------------------------------------------

@EqualPreviews
@Composable
private fun MissedCallCardPreview() {
    PreviewFrame {
        var fb by remember { mutableStateOf<CallFeedback?>(null) }
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            MissedCallCard(
                onCallBack = {},
                caller = "Kuldeep Solanki",
                category = EqualCategory.RealEstate,
                reason = SampleMissedReason,
                time = "03:21",
                feedback = fb,
                onFeedback = { fb = it },
            )
            MissedCallCard(onCallBack = {}, caller = "Valluri", time = "3:31 PM")
            MissedCallCard(onCallBack = {}, time = "3:31 PM")
        }
    }
}
