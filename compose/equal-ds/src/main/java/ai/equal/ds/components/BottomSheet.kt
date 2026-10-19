package ai.equal.ds.components

import ai.equal.ds.assets.EqualAssetImage
import ai.equal.ds.assets.EqualAssets
import ai.equal.ds.icons.EqualIcons
import ai.equal.ds.theme.EqualEasing
import ai.equal.ds.theme.EqualTheme
import ai.equal.ds.theme.tabular
import ai.equal.ds.tokens.EqualMotion
import ai.equal.ds.tokens.EqualRadius
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.BoxWithConstraints

/**
 * Bottom sheet overlay (SPEC §17). Place it last inside a full-screen Box so it draws on top.
 * Scrim `scrim` (tap to dismiss); sheet `sheet/bg`, top radius 32, padding 24 (+ navigation bar
 * inset). Close button top-right (inset 12): 44 `bg/surface` circle, shadow `raised`, close 22
 * `text/secondary`. Enter/exit: fade + slide, 320ms standard.
 *
 * With a [title] the sheet uses top radius 24 and a title row instead of the floating close:
 * [ghost close button] [centred `subheading` title] [spacer the same width]. The sheet is at
 * most 92% of the available height; taller content scrolls.
 *
 * Accessibility: system Back calls [onDismissRequest] while [visible]; the panel is announced
 * with [paneTitle]; under reduced motion it appears and leaves without the fade / slide.
 */
@Composable
fun EqualBottomSheet(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    showCloseButton: Boolean = true,
    title: String? = null,
    paneTitle: String = title ?: "Bottom sheet",
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = EqualTheme.colors
    val radius = if (title != null) EqualRadius.r2xl else EqualRadius.r3xl
    val reduceMotion = rememberReduceMotion()
    EqualBackHandler(enabled = visible, onBack = onDismissRequest)
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val maxSheetHeight = maxHeight * 0.92f
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(EqualMotion.base)).unlessReduced(reduceMotion),
            exit = fadeOut(tween(EqualMotion.base)).unlessReduced(reduceMotion),
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(colors.bgOverlay)
                    .clickable(
                        interactionSource = rememberInteraction(),
                        indication = null,
                        onClickLabel = "Dismiss",
                        role = Role.Button,
                        onClick = onDismissRequest,
                    ),
            )
        }
        AnimatedVisibility(
            visible = visible,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = slideInVertically(tween(EqualMotion.slow, easing = EqualEasing.standard)) { it }.unlessReduced(reduceMotion),
            exit = slideOutVertically(tween(EqualMotion.base, easing = EqualEasing.standard)) { it }.unlessReduced(reduceMotion),
        ) {
            Box(
                modifier
                    .fillMaxWidth()
                    .overlayPane(paneTitle)
                    .heightIn(max = maxSheetHeight)
                    .background(colors.bgPrimary, RoundedCornerShape(topStart = radius, topEnd = radius))
                    // Swallow taps so they don't reach the scrim.
                    .pointerInput(Unit) { detectTapGestures { } },
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .navigationBarsPadding(),
                ) {
                    if (title != null) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(start = 12.dp, end = 12.dp, top = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            PlainIconButton(EqualIcons.Close, "Close", onDismissRequest, tint = colors.textPrimary)
                            EqualText(
                                text = title,
                                style = EqualTheme.type.subheading,
                                color = colors.textPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(1f).semantics { heading() },
                            )
                            Spacer(Modifier.width(48.dp))
                        }
                    }
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp, end = 24.dp, top = if (title != null) 12.dp else 24.dp, bottom = 24.dp),
                        content = content,
                    )
                }
                if (showCloseButton && title == null) {
                    EqualIconButton(
                        icon = EqualIcons.Close,
                        contentDescription = "Close",
                        onClick = onDismissRequest,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 12.dp, end = 12.dp),
                        variant = EqualIconButtonVariant.Neutral,
                        iconSize = 22.dp,
                    )
                }
            }
        }
    }
}

/**
 * Confirm-sheet content (SPEC §17), centred: illustration 72 → 20 → title `title` (wraps) →
 * phone `body1` `text/secondary` (tabular) → 12 → body `body1` `text/secondary` → 28 →
 * full-width button. [destructive] uses the danger button (45 high, trash icon 18).
 */
@Composable
fun ConfirmSheetContent(
    title: String,
    body: String,
    confirmLabel: String,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    phone: String? = null,
    illustration: EqualAssets = EqualAssets.IllustrationTrash,
    destructive: Boolean = true,
) {
    val colors = EqualTheme.colors
    val type = EqualTheme.type
    Column(modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        EqualAssetImage(asset = illustration, size = 72.dp)
        Spacer(Modifier.height(20.dp))
        EqualText(text = title, style = type.title, color = colors.textPrimary, textAlign = TextAlign.Center, modifier = Modifier.semantics { heading() })
        if (phone != null) {
            EqualText(text = phone, style = type.body1.tabular(), color = colors.textSecondary, textAlign = TextAlign.Center)
        }
        Spacer(Modifier.height(12.dp))
        EqualText(text = body, style = type.body1, color = colors.textSecondary, textAlign = TextAlign.Center)
        Spacer(Modifier.height(28.dp))
        EqualButton(
            text = confirmLabel,
            onClick = onConfirm,
            size = EqualButtonSize.Block,
            variant = if (destructive) EqualButtonVariant.Danger else EqualButtonVariant.Primary,
            leadingIcon = if (destructive) EqualIcons.Delete else null,
            iconSize = 18.dp,
        )
    }
}

@EqualPreviews
@Composable
private fun BottomSheetPreview() {
    PreviewFrame(padding = 0.dp) {
        Box(Modifier.height(480.dp)) {
            EqualBottomSheet(visible = true, onDismissRequest = {}) {
                ConfirmSheetContent(
                    title = "Delete this chat?",
                    phone = "+91 98765 43210",
                    body = "The call recording and summary will be removed for good.",
                    confirmLabel = "Delete chat",
                    onConfirm = {},
                )
            }
        }
    }
}
