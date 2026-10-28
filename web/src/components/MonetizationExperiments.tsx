// Monetization experiments (Plan Expired / Usage Cap touchpoints, Recordings + Actions locked,
// All Payment States, Payment Pending, the sheet paywall and the exit offer). These sit beside the
// original paywall components in Monetization.tsx; the Android team A/B tests one against another.
import { inertWhen } from '../foundations/a11y';
import { useId, type ReactNode } from 'react';
import { Announce, useFocusHandoff, useModal } from '../foundations/a11y';
import { Asset } from '../foundations/Asset';
import { Emoji, Icon } from '../foundations/Icon';
import type { IconName } from '../foundations/icons';
import { Text } from '../foundations/Text';
import { Button, IconButton, TextButton } from './Button';
import { AnimatedCrown, DividerLabel, FaqList, PaywallFooter, type FaqItem } from './Monetization';
import { VoicePlayer } from './VoiceNote';
import './MonetizationExperiments.css';

/** Coded art: the soft rings on the tinted payment-result wash. */
export function PaymentResultRings() {
  return <span className="eq-result__rings" aria-hidden />;
}

/** Coded art: the spinning processing ring around the "i" disc. */
export function ProcessingRing() {
  return <span className="eq-processing__ring" aria-hidden><Text variant="inherit" color="none">i</Text></span>;
}

/** Coded art: the light rings behind the crown on the paywall sheet. */
export function PaywallSheetRings() {
  return <span className="eq-paywall-sheet__rings" aria-hidden />;
}


// ---------------------------------------------------------------- payment result pages

export type PaymentResultStatus = 'success' | 'pending' | 'failed';

export type PaymentResultProps = {
  status: PaymentResultStatus;
  /** success: "Plan active till" date. */
  activeTill?: string;
  /** pending: Refresh Status (default) or Contact Support (after the user closed it once). */
  pendingAction?: 'refresh' | 'support';
  onClose?: () => void;
  onBack?: () => void;
  onRefresh?: () => void;
  onRetry?: () => void;
  onContactSupport?: () => void;
  /** Status bar drawn on the wash. */
  header?: ReactNode;
};

const RESULT = {
  success: { art: 'status-confirmed', title: 'Congratulations', sub: 'You’re Unlimited', body: 'You’re all set. Every call’s recording, summary and conversation is now unlocked.' },
  pending: { art: 'status-pending', title: 'Payment Pending', sub: undefined, body: 'We’re confirming your UPI payment. This may take a few moments.' },
  failed: { art: 'status-failed', title: 'Payment Failed', sub: undefined, body: 'Many banks block UPI Autopay by default. A different UPI app, linked to another bank, usually works.' },
} as const;

/**
 * Full-screen payment result (All Payment States): tinted wash with soft rings, the 3D seal, a DM Sans
 * display title, one line of help, and the action pinned at the bottom. Pending closes with ×; failed
 * goes back with ‹. Success has no chrome and leads on to home.
 */
export function PaymentResult({ status, activeTill = '10th Sept 2026', pendingAction = 'refresh', onClose, onBack, onRefresh, onRetry, onContactSupport, header }: PaymentResultProps) {
  const r = RESULT[status];
  return (
    <section className={`eq-result is-${status}`} aria-label={r.title}>
      <PaymentResultRings />
      {header}
      <div className="eq-result__bar">
        {status === 'pending' && <IconButton icon="close" label="Close" variant="neutral" size={36} iconSize={20} onClick={onClose} className="eq-result__chrome" />}
        {status === 'failed' && <IconButton icon="arrow-back" label="Back" variant="neutral" size={36} iconSize={20} onClick={onBack} className="eq-result__chrome" />}
      </div>
      <div className="eq-result__body">
        <Asset name={r.art} size={84} alt="" />
        <Text as="h1" variant="headline" align="center" style={{ fontSize: 30, lineHeight: '36px', fontWeight: 700, letterSpacing: '-0.02em', marginTop: 12 }}>{r.title}</Text>
        {r.sub && <Text as="div" variant="title" align="center" style={{ fontSize: 22, lineHeight: '28px', fontWeight: 400 }}>{r.sub}</Text>}
        <Text as="p" variant="body1" color="secondary" align="center" style={{ margin: '8px 0 0', maxWidth: 280 }}>{r.body}</Text>
        {status === 'success' && (
          <div style={{ alignSelf: 'stretch', marginTop: 20 }}>
            <DividerLabel><Text as="span" variant="caption" color="secondary" style={{ textTransform: 'uppercase', fontWeight: 400 }}>Plan active till</Text></DividerLabel>
            <Text as="div" variant="subheading" align="center" style={{ fontWeight: 700, marginTop: 4 }}>{activeTill}</Text>
          </div>
        )}
      </div>
      {status !== 'success' && (
        <div className="eq-result__footer">
          {status === 'pending' && pendingAction === 'refresh' && <Button size="lg" block leadingIcon="refresh" onClick={onRefresh}>Refresh Status</Button>}
          {status === 'pending' && pendingAction === 'support' && <Button size="lg" block onClick={onContactSupport}>Contact Support</Button>}
          {status === 'failed' && (
            <>
              <Button size="lg" block onClick={onRetry}>Retry payment</Button>
              <div style={{ display: 'flex', justifyContent: 'center', marginTop: 12 }}><TextButton onClick={onContactSupport}>Contact Support</TextButton></div>
            </>
          )}
        </div>
      )}
    </section>
  );
}

// ---------------------------------------------------------------- payment processing

/**
 * "Payment processing · We'll update your order once it's confirmed." with a spinning arc around an
 * info dot. strip: under a chat header; card: inside the home header; toast: above the paywall CTA.
 */
export function ProcessingStrip({ variant = 'strip', title = 'Payment processing', body = 'We’ll update your order once it’s confirmed.' }: { variant?: 'strip' | 'card' | 'toast'; title?: string; body?: string }) {
  return (
    <div className={`eq-processing is-${variant}`} role="status" aria-live="polite">
      <ProcessingRing />
      <span style={{ minWidth: 0 }}>
        <Text as="div" variant="body2" style={{ fontWeight: 500 }}>{title}</Text>
        <Text as="div" variant="caption" color="secondary" style={{ fontWeight: 400 }}>{body}</Text>
      </span>
    </div>
  );
}

// ---------------------------------------------------------------- sheet paywall

export type PaywallSheetProps = {
  price?: number;
  discountedPrice?: number;
  /** Coupon state: EQUAL25 offered (APPLY) or applied. */
  applied?: boolean;
  onApply?: () => void;
  onSupport?: () => void;
  onUnlock?: () => void;
  onScrim?: () => void;
  cta?: string;
  body?: string;
  /** Payment already in flight: toast above a disabled CTA. */
  processing?: boolean;
  mode?: 'overlay' | 'inline';
  /** Escape. Defaults to `onScrim` (leaving the sheet shows the exit offer). */
  onClose?: () => void;
};

/**
 * The paywall as a bottom sheet over the screen the user was on: compact gold card (crown, "Equal AI
 * Unlimited", one line of proof, price, the EQUAL25 coupon row) and the gold CTA. Support opens the
 * Help Center sheet; leaving without paying shows the exit offer.
 */
export function PaywallSheet({ price = 249, discountedPrice = 149, applied, onApply, onSupport, onUnlock, onScrim, cta = 'Get Equal AI Unlimited', body = 'Get Unlimited Recordings, AI Call Summaries and Conversations. Trusted by 10 Lakh+ Indians.', processing, mode = 'overlay', onClose }: PaywallSheetProps) {
  const modal = useModal({ open: true, onClose: onClose ?? onScrim, enabled: mode === 'overlay' });
  const panel = (
    <div className={`eq-paywall-sheet ${mode === 'inline' ? 'is-inline' : ''}`} role="dialog" aria-label="Equal AI Unlimited" {...modal}>
      <div className="eq-paywall-sheet__card">
        <PaywallSheetRings />
        <button type="button" className="eq-reset-btn eq-touch eq-paywall-support eq-paywall-sheet__support" onClick={onSupport}>
          <Icon name="help-circle" size={18} />
          <Text variant="body2" color="none">Support</Text>
        </button>
        <AnimatedCrown size={56} />
        <Text as="div" variant="title" align="center" style={{ fontWeight: 400, fontSize: 22, lineHeight: '26px', marginTop: 8 }}>Equal AI</Text>
        <Text as="div" variant="headline" align="center" style={{ fontSize: 34, lineHeight: '38px', fontWeight: 700, letterSpacing: '-0.03em' }}>Unlimited</Text>
        <Text as="p" variant="caption" color="secondary" align="center" style={{ fontWeight: 400, margin: '8px 8px 0' }}>{body}</Text>
        <Text as="div" variant="micro" align="center" style={{ textTransform: 'uppercase', color: 'var(--eq-text-gold)', fontWeight: 500, marginTop: 14 }}><Emoji char="🎊" inline />{' '}Limited time discount{' '}<Emoji char="🎊" inline /></Text>
        <div className="eq-price__row" style={{ marginTop: 4 }}>
          <span className="eq-price__rule" />
          <Text variant="inherit" color="none" className="eq-gold-text eq-tnum" key={applied ? 'd' : 'p'} style={{ fontSize: 34, lineHeight: '40px', animation: applied ? 'eq-price-land var(--eq-duration-priceDrop) var(--eq-easing-standard) both' : undefined }}>₹{applied ? discountedPrice : price}</Text>
          <span className="eq-price__rule" />
        </div>
        <CouponRow applied={applied} onApply={onApply} />
      </div>
      <PaywallFooter flat cta={processing ? 'Get Equal AI Unlimited' : cta} disabled={processing} onClick={onUnlock} above={processing ? <ProcessingStrip variant="toast" /> : undefined} />
    </div>
  );
  if (mode === 'inline') return panel;
  return (
    <div className="eq-status-sheet-layer">
      <div className="eq-status-sheet-layer__scrim" onClick={onScrim} />
      {panel}
    </div>
  );
}

/** Compact coupon row: code, what it saves, APPLY → APPLIED. */
export function CouponRow({ code = 'EQUAL25', description = 'Get extra 25% off your first month', applied, onApply }: { code?: string; description?: string; applied?: boolean; onApply?: () => void }) {
  const handoff = useFocusHandoff<HTMLSpanElement>(!!applied);
  return (
    <div className={`eq-coupon-row ${applied ? 'is-applied' : ''}`}>
      <span style={{ flex: 1, minWidth: 0 }}>
        <Text as="div" variant="subheading" style={{ fontWeight: 700, color: 'var(--eq-text-gold)' }}>{code}</Text>
        <Text as="div" variant="caption" style={{ fontWeight: 400 }}>{description}</Text>
      </span>
      {applied ? (
        <span ref={handoff.targetRef} tabIndex={-1} className="eq-t-body2" aria-label={`${code} applied`} style={{ fontWeight: 700, color: 'var(--eq-text-gold)' }}><Text variant="inherit" color="none">Applied</Text></span>
      ) : (
        <button type="button" className="eq-reset-btn eq-touch eq-coupon-row__apply eq-t-body2" aria-label={`Apply ${code}`} onClick={() => { handoff.markFocus(); onApply?.(); }}><Text variant="inherit" color="none">Apply</Text></button>
      )}
      <Announce message={applied ? `Coupon ${code} applied` : ''} />
    </div>
  );
}

/** Help Center as a sheet: back, title, one line, then the FAQ. */
export function HelpFaqSheet({ items, onBack, mode = 'overlay', onScrim, onClose }: { items: FaqItem[]; onBack?: () => void; mode?: 'overlay' | 'inline'; onScrim?: () => void; /** Escape. Defaults to `onBack`, then `onScrim`. */ onClose?: () => void }) {
  const modal = useModal({ open: true, onClose: onClose ?? onBack ?? onScrim, enabled: mode === 'overlay' });
  const panel = (
    <div className={`eq-paywall-sheet is-help ${mode === 'inline' ? 'is-inline' : ''}`} role="dialog" aria-label="Help Center" {...modal}>
      <div style={{ display: 'flex', alignItems: 'center', gap: 4, padding: '16px 12px 0 4px' }}>
        <IconButton icon="arrow-back" label="Back" onClick={onBack} />
        <Text as="h2" variant="title" style={{ fontWeight: 500 }}>Help Center</Text>
      </div>
      <Text as="p" variant="body1" color="secondary" style={{ fontWeight: 400, margin: '4px 16px 12px' }}>Check out this section to get answers for all the frequently asked questions</Text>
      <div style={{ padding: '0 16px 20px' }}><FaqList items={items} title={null} defaultOpen={[3]} /></div>
    </div>
  );
  if (mode === 'inline') return panel;
  return (
    <div className="eq-status-sheet-layer">
      <div className="eq-status-sheet-layer__scrim" onClick={onScrim} />
      {panel}
    </div>
  );
}

/**
 * Exit offer when the user leaves the paywall: "Leave Without Unlocking?" with the coupon as the
 * commit. Labels are an experiment (Not Now / Claim 25% Off on home; Leave / Keep 25% Off in a chat).
 */
export function ExitOfferDialog({ title = 'Leave Without Unlocking?', body = 'Claim 25% off with EQUAL25 first, go Unlimited for ₹149/month.', dismissLabel = 'Not Now', claimLabel = 'Claim 25% Off', onDismiss, onClaim, mode = 'overlay' }: { title?: string; body?: string; dismissLabel?: string; claimLabel?: string; onDismiss?: () => void; onClaim?: () => void; mode?: 'overlay' | 'inline' }) {
  const modal = useModal({ open: true, onClose: onDismiss, enabled: mode === 'overlay' });
  const bodyId = useId();
  const card = (
    <div className="eq-exit" role="alertdialog" aria-label={title} aria-describedby={bodyId} {...modal}>
      <Text as="h2" variant="title" style={{ fontWeight: 500 }}>{title}</Text>
      <Text as="p" id={bodyId} variant="body1" color="secondary" style={{ fontWeight: 400, margin: '4px 0 16px' }}>{body}</Text>
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10 }}>
        <Button variant="gold-outline" onClick={onDismiss}>{dismissLabel}</Button>
        <Button variant="gold" onClick={onClaim}>{claimLabel}</Button>
      </div>
    </div>
  );
  if (mode === 'inline') return card;
  return (
    <div className="eq-exit-layer">
      <div className="eq-status-sheet-layer__scrim" style={{ background: 'rgba(0,0,0,0.55)' }} />
      {card}
    </div>
  );
}

/** Home strip after the user declined: "Go unlimited for ₹149 with EQUAL25 · Claim Now". */
export function OfferStrip({ children = <>Go unlimited for ₹149 with EQUAL25</>, cta = 'Claim Now', onClick }: { children?: ReactNode; cta?: string; onClick?: () => void }) {
  return (
    <div className="eq-offer-strip" role="status">
      <Icon name="info-fill" size={16} />
      <Text as="span" variant="body2" style={{ color: 'inherit', flex: 1, minWidth: 0, fontWeight: 500 }}>{children}</Text>
      <button type="button" className="eq-reset-btn eq-touch eq-offer-strip__cta eq-t-body2" onClick={onClick}><Text variant="inherit" color="none">{cta}</Text></button>
    </div>
  );
}

// ---------------------------------------------------------------- home & chat touchpoints

/**
 * Home card under the header (Plan Expired option 2, Usage Cap option 2): title (optionally led by an
 * icon or emoji), one line, a small green CTA, and an optional usage bar.
 */
export function HomeNoticeCard({ title, subtitle, cta, onCta, lead, progress, progressLabel }: { title: ReactNode; subtitle: ReactNode; cta: string; onCta?: () => void; lead?: ReactNode; progress?: number; /** Accessible name of the usage bar (defaults to the title when it is text). */ progressLabel?: string }) {
  return (
    <div className="eq-notice">
      <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
        <span style={{ flex: 1, minWidth: 0 }}>
          <Text as="div" variant="subheading" style={{ display: 'flex', alignItems: 'center', gap: 6 }}>{lead}{title}</Text>
          <Text as="div" variant="caption" color="secondary" style={{ fontWeight: 400 }}>{subtitle}</Text>
        </span>
        <Button size="md" onClick={onCta}>{cta}</Button>
      </div>
      {progress !== undefined && (
        <div className="eq-notice__bar" role="progressbar" aria-label={progressLabel ?? (typeof title === 'string' ? title : 'Usage')} aria-valuemin={0} aria-valuemax={100} aria-valuenow={Math.round(progress * 100)} aria-valuetext={`${Math.round(progress * 100)}% used`}><span style={{ width: `${Math.round(progress * 100)}%` }} /></div>
      )}
    </div>
  );
}

/** Usage meter for the free weekly cap: "📞 7 Calls left this week · Resets Mon | Unlimited on premium". */
export function UsageCapCard({ left, limit, resets = 'Mon', onUpgrade, lead }: { left: number; limit: number; resets?: string; onUpgrade?: () => void; /** Phone glyph by default; null hides it (option without the icon). */ lead?: ReactNode | null }) {
  return (
    <HomeNoticeCard
      lead={lead === null ? undefined : lead ?? <Icon name="call-filled" size={16} />}
      title={`${left} Calls left this week`}
      subtitle={<>Resets {resets} <span className="eq-notice__sep" aria-hidden /> Unlimited on premium</>}
      cta="Get Unlimited"
      onCta={onUpgrade}
      progress={(limit - left) / limit}
      progressLabel="Weekly calls used"
    />
  );
}

/**
 * A message from Equal AI itself in its own chat (Plan Expired option 1, Usage Cap option 1): a voice
 * note, "Equal AI", the headline, the body, and one CTA with the crown (Renew / Get Equal AI Unlimited).
 */
export function AssistantNoticeCard({ title, body, cta, onCta, duration = 97, width = 296 }: { title: string; body: ReactNode; cta: string; onCta?: () => void; duration?: number; width?: number }) {
  return (
    <div className="eq-assistant-notice" style={{ width }}>
      <div style={{ padding: 8 }}><VoicePlayer duration={duration} /></div>
      <div style={{ padding: '0 12px 12px' }}>
        <Text as="div" variant="body2" color="action">Equal AI</Text>
        <Text as="div" variant="subheading">{title}</Text>
        <Text as="div" variant="body1" style={{ fontWeight: 400, marginTop: 4 }}>{body}</Text>
      </div>
      <hr className="eq-divider" />
      <button type="button" className="eq-reset-btn eq-pressable eq-assistant-notice__cta" onClick={onCta}>
        <Asset name="crown-lock" size={18} alt="" />
        <Text variant="body2" color="none" style={{ fontWeight: 500 }}>{cta}</Text>
      </button>
    </div>
  );
}

export type SummaryAction = { label: string; icon: IconName; onClick?: () => void };

/**
 * Action rows under a call summary (Call Back, Add to Calendar). `locked` (free plan) swaps each icon
 * for the crown-lock and turns the label gold; tapping opens the paywall.
 */
export function SummaryActions({ actions, locked, onLocked }: { actions: SummaryAction[]; locked?: boolean; onLocked?: () => void }) {
  return (
    <div className="eq-summary-actions">
      {actions.map((a) => (
        <button key={a.label} type="button" className={`eq-reset-btn eq-pressable eq-summary-actions__row ${locked ? 'is-locked' : ''}`} onClick={locked ? onLocked : a.onClick}>
          {locked ? <Asset name="crown-lock" size={16} alt="Locked" /> : <Icon name={a.icon} size={18} />}
          <Text variant="body2" color="none" style={{ fontWeight: 500 }}>{a.label}</Text>
        </button>
      ))}
    </div>
  );
}

/** Gold-washed, blurred recording with the crown-lock in the middle (recordings locked on the free plan). */
export function LockedRecording({ duration = 157, onUnlock }: { duration?: number; onUnlock?: () => void }) {
  return (
    // A div with button semantics: the player inside draws its own (inert) button, which can't nest in a <button>.
    <div
      role="button"
      tabIndex={0}
      className="eq-reset-btn eq-locked-recording"
      onClick={onUnlock}
      onKeyDown={(e) => { if (e.key === 'Enter' || e.key === ' ') { e.preventDefault(); onUnlock?.(); } }}
      aria-label="Recording locked. Unlock Equal AI Unlimited"
    >
      <span className="eq-locked-recording__player" aria-hidden {...inertWhen(true)}><VoicePlayer duration={duration} /></span>
      <span className="eq-locked-recording__badge" aria-hidden><Asset name="crown-lock" size={26} alt="" /></span>
    </div>
  );
}
