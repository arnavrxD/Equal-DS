// Monetization screens (Monetization Flow design), composed only from DS components. The Android team
// runs experiments on these, so every screen takes the experiment knobs as props (price, coupon,
// copy, entry touchpoint) instead of hard-coding one variant.
import { useEffect, useState } from 'react';
import {
  CategoryAvatar,
  LockedRecording,
  SummaryActions,
  PaywallSheet,
  HelpFaqSheet,
  ExitOfferDialog,
  PaymentResult,
  ProcessingStrip,
  type PaymentResultStatus,
  FeatureList,
  ValueCard,
  Asset,
  CallSummary,
  CancelSheet,
  ChatBubble,
  ChatListItem,
  CouponTicket,
  DividerLabel,
  FaqList,
  HelpCenterSheet,
  LockedPreviewCard,
  MessageList,
  PaymentStatusSheet,
  PaywallFooter,
  SocialProof,
  PaywallHero,
  PriceTag,
  RatingPill,
  Screen,
  StatBlock,
  StatusBar,
  TestimonialCarousel,
  Text,
  Button,
  type CancelStep,
  type PaymentStatus,
} from '../index';
import { motion } from '../tokens/tokens';
import { paywallFaq, paywallSubtitles, testimonials } from './monetizationData';

/** Where the user entered the paywall from; picks the "what you're missing" preview. */
export type PaywallTouchpoint = 'chat-list' | 'chat' | 'audio' | 'actions' | 'recordings' | 'recordings-actions';

/** The preview card content for each touchpoint. */
/** The locked call summary the experiments preview: gold crown-locked recording, the summary, and optionally the locked actions. */
export function LockedSummaryPreview({ actions }: { actions?: boolean }) {
  return (
    <div style={{ display: 'flex', gap: 8, padding: 8 }}>
      <CategoryAvatar category="home-services" size={24} />
      <div style={{ flex: 1, minWidth: 0 }}>
        <CallSummary
          width="100%"
          summary="Amit invited you to dinner this Sunday, 8 PM at his place. He asked you to confirm by tonight."
          recording={<LockedRecording />}
          footer={actions ? <SummaryActions locked actions={summaryActions} /> : undefined}
        />
      </div>
    </div>
  );
}

export const summaryActions = [
  { label: 'Call Back', icon: 'call-filled' as const },
  { label: 'Add to Calendar', icon: 'calendar' as const },
];

export function PaywallPreview({ touchpoint }: { touchpoint: PaywallTouchpoint }) {
  if (touchpoint === 'actions') {
    return (
      <LockedPreviewCard>
        <ChatListItem name="Dr. Mehta Clinic" preview="Amit invited you to dinner this Sunday" date="Yesterday" avatar={{ category: 'na' }} actions={[{ label: 'Call Back', emoji: '📞' }, { label: 'Update Address', emoji: '✏️' }]} lockedActions />
      </LockedPreviewCard>
    );
  }
  if (touchpoint === 'recordings' || touchpoint === 'recordings-actions') {
    return <LockedPreviewCard><LockedSummaryPreview actions={touchpoint === 'recordings-actions'} /></LockedPreviewCard>;
  }
  if (touchpoint === 'chat-list') {
    return (
      <LockedPreviewCard>
        <div style={{ display: 'flex', flexDirection: 'column', gap: 1, background: 'var(--eq-stroke-subtle)' }}>
          <ChatListItem name="HDFC RM" preview="Loan pre-approval request" date="1:00 PM" avatar={{ category: 'gate-alert' }} />
          <ChatListItem name="Vijay NoBroker" preview="About miniature painting" date="Yesterday" avatar={{ category: 'real-estate' }} locked />
        </div>
      </LockedPreviewCard>
    );
  }
  if (touchpoint === 'chat') {
    return (
      <LockedPreviewCard>
        <div style={{ padding: '12px 4px' }}>
          <MessageList>
            <ChatBubble kind="outgoing">Hello, who is this?</ChatBubble>
            <ChatBubble kind="incoming" category="real-estate">Vijay from no broker</ChatBubble>
            <ChatBubble kind="incoming" category="real-estate" first={false} locked>Hey this is vijay calling from magicbrick a property advisor</ChatBubble>
          </MessageList>
        </div>
      </LockedPreviewCard>
    );
  }
  return (
    <LockedPreviewCard>
      <div style={{ padding: '12px 4px' }}>
        <MessageList>
          <ChatBubble kind="outgoing" locked>Hi Arnav, Called you regarding…</ChatBubble>
          <ChatBubble kind="assistant" bare>
            <CallSummary summary="Hey Keshav! Here is the summary of your call. Hi Arnav, Called you regarding the property visit." locked="audio" width={250} />
          </ChatBubble>
        </MessageList>
      </div>
    </LockedPreviewCard>
  );
}

export type PaywallScreenProps = {
  touchpoint?: PaywallTouchpoint;
  /** Experiment: list price before the coupon. */
  price?: number;
  /** Experiment: price once the coupon is applied. */
  discountedPrice?: number;
  /** Experiment: no coupon, coupon offered, or already applied. */
  coupon?: 'none' | 'available' | 'applied';
  /** Experiment: subtitle copy. */
  subtitle?: keyof typeof paywallSubtitles;
  /** Pin an overlay state (docs); otherwise the flow runs: Unlock → waiting → confirmed → welcome. */
  overlay?: PaymentStatus | 'help' | null;
  /** CTA copy: "Unlock Unlimited" (default), "Get Equal AI Unlimited" when renewing. */
  cta?: string;
  /** A payment is already in flight (the user closed Payment Pending): toast above a disabled CTA. */
  processing?: boolean;
  onBack?: () => void;
  onDone?: () => void;
};

/** The Equal Unlimited paywall: hero, price, locked preview, coupon, FAQ and the sticky gold CTA. */
export function PaywallScreen({ touchpoint = 'chat-list', price = 249, discountedPrice = 149, coupon = 'available', subtitle = 'unlock', overlay, cta, processing, onBack, onDone }: PaywallScreenProps) {
  const [applied, setApplied] = useState(coupon === 'applied');
  const [justApplied, setJustApplied] = useState(false);
  const [live, setLive] = useState<PaymentStatus | 'help' | null>(null);
  useEffect(() => setApplied(coupon === 'applied'), [coupon]);
  const state = overlay !== undefined ? overlay : live;
  useEffect(() => {
    if (overlay !== undefined || live !== 'waiting') return;
    const t = setTimeout(() => setLive('confirmed'), motion.duration.paymentDelay);
    return () => clearTimeout(t);
  }, [live, overlay]);
  const shown = price;
  return (
    <Screen
      surface="surface"
      header={<StatusBar background="var(--eq-paywall-hero-top)" />}
      footer={processing ? <PaywallFooter cta="Unlock Unlimited" disabled above={<ProcessingStrip variant="toast" />} /> : <PaywallFooter cta={cta} onClick={() => setLive('waiting')} />}
      overlay={
        state === 'help' ? (
          <HelpCenterSheet />
        ) : state ? (
          <PaymentStatusSheet status={state} onRetry={() => setLive('waiting')} onContactSupport={() => setLive('help')} onRedirect={overlay !== undefined ? undefined : () => (state === 'confirmed' ? setLive('welcome') : (setLive(null), onDone?.()))} />
        ) : undefined
      }
    >
      <PaywallHero subtitle={paywallSubtitles[subtitle]} onBack={onBack} onSupport={() => setLive('help')}>
        {applied ? <PriceTag price={discountedPrice} was={price} badge="Special Coupon Applied!" animate={justApplied} /> : <PriceTag price={shown} />}
        <PaywallPreview touchpoint={touchpoint} />
        {coupon !== 'none' && <CouponTicket code="EQUAL25" description={`Save Flat ₹${price - discountedPrice} with this Code`} discount="25% OFF" applied={applied} onApply={() => (setApplied(true), setJustApplied(true))} />}
      </PaywallHero>
      <div style={{ padding: '16px 16px 24px' }}>
        <FaqList items={paywallFaq} defaultOpen={[3]} />
      </div>
    </Screen>
  );
}

// ---------------------------------------------------------------- subscription management

export type SubscriptionScreenProps = {
  /**
   * active: plan running, Cancel Subscription sticks to the bottom; cancelled: access until the
   * date, Re-Subscribe; expired: the plan ended, Renew your Equal AI (goes to the paywall).
   */
  state?: 'active' | 'cancelled' | 'expired';
  activeUntil?: string;
  /** expired: the date it ended. */
  expiredOn?: string;
  /** Calls the assistant handled in the plan (the value card). */
  calls?: number;
  /** Pin a cancellation step (docs); otherwise Cancel Subscription runs the flow. */
  cancelStep?: CancelStep | null;
  /** Pre-fill the feedback (docs: "Proceed enabled"). */
  reason?: string;
  onBack?: () => void;
  /** expired: Renew your Equal AI. */
  onRenew?: () => void;
};

/**
 * Manage plan (Monetization value design): "Equal AI Unlimited" hero, What Unlimited
 * Saved You (value card), What You Get, social proof and FAQ, with the plan action pinned at the bottom.
 */
export function SubscriptionScreen({ state = 'active', activeUntil = '25th Jun ’26', expiredOn = '30th July', calls = 20, cancelStep, reason, onBack, onRenew }: SubscriptionScreenProps) {
  const [step, setStep] = useState<CancelStep | null>(null);
  const [cancelled, setCancelled] = useState(state === 'cancelled');
  const [text, setText] = useState(reason ?? '');
  useEffect(() => setCancelled(state === 'cancelled'), [state]);
  const current = cancelStep !== undefined ? cancelStep : step;
  const expired = state === 'expired';
  const footer = expired ? (
    <PaywallFooter cta="Renew your Equal AI" onClick={onRenew} />
  ) : cancelled ? (
    <PaywallFooter cta="Re-Subscribe" onClick={() => setCancelled(false)} />
  ) : (
    <PaywallFooter variant="gold-outline" cta="Cancel Subscription" onClick={() => setStep('feedback')} />
  );
  return (
    <Screen
      surface="surface"
      header={<StatusBar background="var(--eq-paywall-hero-top)" />}
      footer={footer}
      overlay={
        current ? (
          <CancelSheet
            step={current}
            reason={cancelStep !== undefined ? reason : text}
            onReasonChange={setText}
            onProceed={() => setStep('confirm')}
            onKeep={() => setStep(null)}
            onConfirm={() => setStep('submitted')}
            onRedirect={cancelStep !== undefined ? undefined : () => (setStep(null), setCancelled(true))}
          />
        ) : undefined
      }
    >
      <PaywallHero title="Equal AI" subtitle={paywallSubtitles.unlock} onBack={onBack}>
        <DividerLabel>What Unlimited Saved You</DividerLabel>
        <ValueCard calls={calls} title={null} perks={null} expiry={expired ? { state: 'expired', date: expiredOn } : { state: 'active', until: activeUntil }} />
        <DividerLabel>What You Get</DividerLabel>
        <FeatureList />
      </PaywallHero>
      <SocialProof testimonials={testimonials.map((t, i) => ({ ...t, avatar: <Asset name={(['avatar-testimonial-1', 'avatar-testimonial-2', 'avatar-testimonial-3'] as const)[i % 3]} size={36} alt="" /> }))} />
      <div style={{ padding: '16px 16px 24px' }}>
        <FaqList items={paywallFaq} defaultOpen={[3]} />
      </div>
    </Screen>
  );
}

// ---------------------------------------------------------------- sheet paywall + exit offer (experiment)

/** Where the sheet paywall is in its little flow. */
export type PaywallSheetStage = 'sheet' | 'help' | 'exit' | 'claimed' | 'processing';

/**
 * The sheet paywall over any screen, as a live flow: Support → Help Center (back returns), tapping
 * outside → "Leave Without Unlocking?"; Claim → the sheet with EQUAL25 applied; Not Now → closes and
 * calls `onDecline` (home then shows the offer strip). `context="chat"` uses Leave / Keep 25% Off.
 */
export function PaywallSheetFlow({ stage, onClose, onDecline, onUnlock, context = 'home' }: { stage?: PaywallSheetStage; onClose?: () => void; onDecline?: () => void; onUnlock?: () => void; context?: 'home' | 'chat' }) {
  const [live, setLive] = useState<PaywallSheetStage>(stage ?? 'sheet');
  useEffect(() => setLive(stage ?? 'sheet'), [stage]);
  const s = live;
  if (s === 'help') return <HelpFaqSheet items={paywallFaq} onBack={() => setLive('sheet')} onScrim={() => setLive('exit')} />;
  if (s === 'exit') {
    return (
      <ExitOfferDialog
        dismissLabel={context === 'chat' ? 'Leave' : 'Not Now'}
        claimLabel={context === 'chat' ? 'Keep 25% Off' : 'Claim 25% Off'}
        onDismiss={() => (onDecline?.(), onClose?.())}
        onClaim={() => setLive('claimed')}
      />
    );
  }
  return (
    <PaywallSheet
      applied={s === 'claimed'}
      processing={s === 'processing'}
      onApply={() => setLive('claimed')}
      onSupport={() => setLive('help')}
      onScrim={() => setLive(s === 'claimed' ? 'sheet' : 'exit')}
      onUnlock={onUnlock}
    />
  );
}

// ---------------------------------------------------------------- payment results (full screen, experiment)

/** All Payment States as screens: Congratulations, Payment Pending (Refresh Status or Contact Support), Payment Failed. */
export function PaymentResultScreen({ status, pendingAction, onClose, onBack, onDone }: { status: PaymentResultStatus; pendingAction?: 'refresh' | 'support'; onClose?: () => void; onBack?: () => void; onDone?: () => void }) {
  const [s, setS] = useState<PaymentResultStatus>(status);
  useEffect(() => setS(status), [status]);
  useEffect(() => {
    if (s !== 'success' || !onDone) return;
    const t = setTimeout(onDone, motion.duration.paymentDelay);
    return () => clearTimeout(t);
  }, [s, onDone]);
  return (
    <Screen surface="surface">
      <PaymentResult header={<StatusBar background="transparent" />} status={s} pendingAction={pendingAction} onClose={onClose} onBack={onBack} onRefresh={() => setS('success')} onRetry={() => setS('pending')} />
    </Screen>
  );
}
