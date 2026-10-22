// Compose code shown on the docs site (Flows → Assistant health check). Each function body is
// extracted by scripts/build-snippets.mjs; keep the `// snippet: <doc id> / <key>` line above it.
@file:Suppress("unused")

package ai.equal.ds.snippets

import ai.equal.ds.catalog.HealthCheckEntry
import ai.equal.ds.catalog.HealthCheckEntryKind
import ai.equal.ds.catalog.HealthCheckFlowSample
import ai.equal.ds.theme.EqualTheme
import androidx.compose.runtime.Composable

// snippet: flow-health-check / entry-notification
@Composable
internal fun FlowHealthCheckEntryNotificationSnippet() {
    EqualTheme {
        HealthCheckEntry(entry = HealthCheckEntryKind.Notification, onFix = { /* open Health Check */ })
    }
}

// snippet: flow-health-check / entry-actions
@Composable
internal fun FlowHealthCheckEntryActionsSnippet() {
    EqualTheme {
        HealthCheckEntry(entry = HealthCheckEntryKind.NotificationActions, onFix = { /* open Health Check */ })
    }
}

// snippet: flow-health-check / entry-home
@Composable
internal fun FlowHealthCheckEntryHomeSnippet() {
    EqualTheme {
        HealthCheckEntry(entry = HealthCheckEntryKind.Home, onFix = { /* open Health Check */ })
    }
}

// snippet: flow-health-check / idle
@Composable
internal fun FlowHealthCheckIdleSnippet() {
    EqualTheme {
        HealthCheckFlowSample(initial = "idle", onBack = { /* pop */ })
    }
}

// snippet: flow-health-check / expanded
@Composable
internal fun FlowHealthCheckExpandedSnippet() {
    EqualTheme {
        HealthCheckFlowSample(initial = "expanded", onBack = { /* pop */ })
    }
}

// snippet: flow-health-check / phone-checking
@Composable
internal fun FlowHealthCheckPhoneCheckingSnippet() {
    EqualTheme {
        HealthCheckFlowSample(initial = "phone-checking", onBack = { /* pop */ })
    }
}

// snippet: flow-health-check / notification-checking
@Composable
internal fun FlowHealthCheckNotificationCheckingSnippet() {
    EqualTheme {
        HealthCheckFlowSample(initial = "notification-checking", onBack = { /* pop */ })
    }
}

// snippet: flow-health-check / contacts-checking
@Composable
internal fun FlowHealthCheckContactsCheckingSnippet() {
    EqualTheme {
        HealthCheckFlowSample(initial = "contacts-checking", onBack = { /* pop */ })
    }
}

// snippet: flow-health-check / permissions-failed
@Composable
internal fun FlowHealthCheckPermissionsFailedSnippet() {
    EqualTheme {
        HealthCheckFlowSample(initial = "permissions-failed", onBack = { /* pop */ })
    }
}

// snippet: flow-health-check / overlay-pending
@Composable
internal fun FlowHealthCheckOverlayPendingSnippet() {
    EqualTheme {
        HealthCheckFlowSample(initial = "overlay-pending", onBack = { /* pop */ })
    }
}

// snippet: flow-health-check / overlay-checking
@Composable
internal fun FlowHealthCheckOverlayCheckingSnippet() {
    EqualTheme {
        HealthCheckFlowSample(initial = "overlay-checking", onBack = { /* pop */ })
    }
}

// snippet: flow-health-check / overlay-failed
@Composable
internal fun FlowHealthCheckOverlayFailedSnippet() {
    EqualTheme {
        HealthCheckFlowSample(initial = "overlay-failed", onBack = { /* pop */ })
    }
}

// snippet: flow-health-check / carrier-pending
@Composable
internal fun FlowHealthCheckCarrierPendingSnippet() {
    EqualTheme {
        HealthCheckFlowSample(initial = "carrier-pending", onBack = { /* pop */ })
    }
}

// snippet: flow-health-check / carrier-checking
@Composable
internal fun FlowHealthCheckCarrierCheckingSnippet() {
    EqualTheme {
        HealthCheckFlowSample(initial = "carrier-checking", onBack = { /* pop */ })
    }
}

// snippet: flow-health-check / carrier-issues
@Composable
internal fun FlowHealthCheckCarrierIssuesSnippet() {
    EqualTheme {
        HealthCheckFlowSample(initial = "carrier-issues", onBack = { /* pop */ })
    }
}

// snippet: flow-health-check / fixing
@Composable
internal fun FlowHealthCheckFixingSnippet() {
    EqualTheme {
        HealthCheckFlowSample(initial = "fixing", onBack = { /* pop */ })
    }
}

// snippet: flow-health-check / fix-failed
@Composable
internal fun FlowHealthCheckFixFailedSnippet() {
    EqualTheme {
        HealthCheckFlowSample(initial = "fix-failed", onBack = { /* pop */ })
    }
}

// snippet: flow-health-check / fixing-again
@Composable
internal fun FlowHealthCheckFixingAgainSnippet() {
    EqualTheme {
        HealthCheckFlowSample(initial = "fixing-again", onBack = { /* pop */ })
    }
}

// snippet: flow-health-check / check-failed
@Composable
internal fun FlowHealthCheckCheckFailedSnippet() {
    EqualTheme {
        HealthCheckFlowSample(initial = "check-failed", onBack = { /* pop */ })
    }
}

// snippet: flow-health-check / all-good
@Composable
internal fun FlowHealthCheckAllGoodSnippet() {
    EqualTheme {
        HealthCheckFlowSample(initial = "all-good", onBack = { /* pop */ })
    }
}

// snippet: flow-health-check / live
@Composable
internal fun FlowHealthCheckLiveSnippet() {
    EqualTheme {
        HealthCheckFlowSample(onBack = { /* pop */ })
    }
}
