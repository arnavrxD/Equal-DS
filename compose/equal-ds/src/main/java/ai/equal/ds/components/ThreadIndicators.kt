package ai.equal.ds.components

import ai.equal.ds.theme.EqualTheme
import ai.equal.ds.tokens.EqualRadius
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/** The thread pill: radius 8, `bg/surface`, 1dp `stroke/subtle`, one `micro` line. */
@Composable
private fun ThreadPill(text: String, textColor: Color, modifier: Modifier, hug: Boolean) {
    val colors = EqualTheme.colors
    val shape = RoundedCornerShape(EqualRadius.rSm)
    Box(
        modifier
            .messageSide(MessageSide.Center)
            .then(if (hug) Modifier else Modifier.height(24.dp))
            .background(colors.bgSurface, shape)
            .border(1.dp, colors.strokeSubtle, shape)
            .then(if (hug) Modifier.padding(vertical = 6.dp, horizontal = 12.dp) else Modifier.padding(horizontal = 14.dp)),
        contentAlignment = Alignment.Center,
    ) {
        EqualText(text = text, style = EqualTheme.type.micro, color = textColor, maxLines = 1)
    }
}

/**
 * Day divider, e.g. "Today" (SPEC §21). 24 high, h14, radius 8, `bg/surface`,
 * 1dp `stroke/subtle`, `micro` `text/primary`. A centre row in a [MessageList].
 */
@Composable
fun DayDivider(text: String, modifier: Modifier = Modifier) {
    ThreadPill(text = text, textColor = EqualTheme.colors.textPrimary, modifier = modifier, hug = false)
}

/**
 * Thread event, e.g. "Call Started 03:31" (SPEC §21). Same pill as [DayDivider] but it hugs its
 * label: no fixed height, padding 6 v / 12 h, `text/secondary`. A centre row in a [MessageList].
 */
@Composable
fun ThreadEvent(text: String, modifier: Modifier = Modifier) {
    ThreadPill(text = text, textColor = EqualTheme.colors.textSecondary, modifier = modifier, hug = true)
}

/**
 * Thread notice (SPEC §21). Max width 232, padding 8 v / 16 h, radius 8, `notice/bg`,
 * `captionWrap` `text/secondary`, centred. A centre row in a [MessageList].
 */
@Composable
fun ThreadNotice(text: String, modifier: Modifier = Modifier) {
    val colors = EqualTheme.colors
    Box(
        modifier
            .messageSide(MessageSide.Center)
            .widthIn(max = 232.dp)
            .background(colors.noticeBg, RoundedCornerShape(EqualRadius.rSm))
            .padding(vertical = 8.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        EqualText(text = text, style = EqualTheme.type.captionWrap, color = colors.textSecondary, textAlign = TextAlign.Center)
    }
}

@EqualPreviews
@Composable
private fun ThreadIndicatorsPreview() {
    PreviewFrame {
        DayDivider("Today")
        VSpace(8.dp)
        ThreadEvent("Call Started 03:31")
        VSpace(8.dp)
        ThreadNotice("Call has ended. Equal AI shared a summary below.")
    }
}
