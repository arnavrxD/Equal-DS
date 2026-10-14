// Monetization (Monetization Flow design): the Equal Unlimited paywall, its touchpoints (locked chats,
// bubbles and audio), payment results, plan banners and the cancellation flow.
// Typography is DM Sans only: the design's serif display lines are set in DM Sans (400 over 700).
import { useEffect, useId, useState, type ReactNode } from 'react';
import { Asset, type AssetName } from '../foundations/Asset';
import { motion } from '../tokens/tokens';
import { Icon } from '../foundations/Icon';
import { icons, type IconName } from '../foundations/icons';
import { Text } from '../foundations/Text';
import { Button, IconButton, TextButton } from './Button';
import './Monetization.css';

// ---------------------------------------------------------------- shared pieces

/** Premium Lock (Premium Lock.svg): the gold crown with a padlock that marks locked content (rows, bubbles, audio). */
export function LockBadge({ size = 18 }: { size?: number }) {
  return <Asset name="crown-lock" size={size} alt="Locked" />;
}

/**
 * Locked content: the children are blurred (still the real layout, so the paywall shows what's
 * behind it) with a crown-lock badge. `inline` keeps it on one line (chat-list previews).
 */
export function Locked({ children, badge = true, inline = false }: { children: ReactNode; badge?: boolean; inline?: boolean }) {
  return (
    <span className={`eq-locked ${inline ? 'is-inline' : ''}`}>
      <span className="eq-locked__content" aria-hidden>{children}</span>
      <span className="eq-sr">Locked. Unlock Equal Unlimited to see this.</span>
      {badge && <span className="eq-locked__badge"><LockBadge size={inline ? 16 : 18} /></span>}
    </span>
  );
}

/** "——— LABEL ———" section label with gold hairlines (FAQ, Why people choose). */
export function DividerLabel({ children, tone = 'primary' }: { children: ReactNode; tone?: 'primary' | 'gold' }) {
  return (
    <div className="eq-divider-label">
      <span className="eq-divider-label__rule" />
      <Text variant="caption" color={tone === 'gold' ? undefined : 'primary'} style={{ textTransform: 'uppercase', letterSpacing: '0.02em', color: tone === 'gold' ? 'var(--eq-text-gold)' : undefined, whiteSpace: 'nowrap' }}>{children}</Text>
      <span className="eq-divider-label__rule" />
    </div>
  );
}

// ---------------------------------------------------------------- paywall

/**
 * The 3D gold crown, alive: a slow turntable sway (±20° around the vertical axis) with a gentle
 * float, 4.8s per cycle. Stands in for the rotating-crown video until it can be exported (a frame
 * sequence or MP4 becomes a Lottie / animated WebP). Static under reduced motion or `animated={false}`.
 */
export function AnimatedCrown({ size = 64, animated = true }: { size?: number; animated?: boolean }) {
  return (
    <span className={`eq-crown ${animated ? 'is-animated' : ''}`} style={{ width: size, height: size }}>
      <Asset name="crown-gold" size={size} alt="" />
    </span>
  );
}

export type PaywallHeroProps = {
  /** Regular first line. */
  title?: string;
  /** Bold display line. */
  titleStrong?: string;
  subtitle?: ReactNode;
  onBack?: () => void;
  onSupport?: () => void;
  children?: ReactNode;
};

/**
 * Gold hero of the Equal Unlimited pages: `gradient/paywall-hero` with soft concentric rings, back
 * + Support, the 3D crown (64dp), "Unlock Equal" 22/26 Regular over "Unlimited" 38/42 Bold, and
 * the subtitle (body1, text/secondary, max 2 lines). `children` (price, preview, coupon) follow.
 */
export function PaywallHero({ title = 'Unlock Equal', titleStrong = 'Unlimited', subtitle = 'Unlock Summaries, Transcripts, Recordings & Unlimited Call Screening', onBack, onSupport, children }: PaywallHeroProps) {
  return (
    <section className="eq-paywall-hero">
      <div className="eq-paywall-hero__bar">
        <IconButton icon="arrow-back" label="Back" onClick={onBack} />
        <button type="button" className="eq-reset-btn eq-touch eq-paywall-support" onClick={onSupport}>
          <Icon name="help-circle" size={20} />
          <span className="eq-t-body2">Support</span>
        </button>
      </div>
      <div className="eq-paywall-hero__head">
        <AnimatedCrown size={64} />
        <Text as="div" variant="title" style={{ fontWeight: 400, fontSize: 22, lineHeight: '26px', marginTop: 12 }}>{title}</Text>
        <Text as="h1" variant="headline" style={{ fontSize: 38, lineHeight: '42px', fontWeight: 700, letterSpacing: '-0.03em' }}>{titleStrong}</Text>
        {subtitle && <Text as="p" variant="body1" color="secondary" align="center" style={{ marginTop: 8, maxWidth: 300 }}>{subtitle}</Text>}
      </div>
      {children && <div className="eq-paywall-hero__body">{children}</div>}
    </section>
  );
}

export type PriceTagProps = {
  price: number | string;
  /** The struck list price. Adds "₹249 /Monthly" beside the price and "Saved ₹100" under it. */
  was?: number;
  label?: string | null;
  currency?: string;
  period?: string;
  /** A DiscountBadge under the price, e.g. "Discounted!!" or "Special Coupon Applied!". */
  badge?: string;
  /** Monetization local components design: the old price is struck through, then the new one lands and the badge pops. */
  animate?: boolean;
};

/** "🎊 LIMITED TIME DISCOUNT 🎊" + the big gold price between gold hairlines; with `was`, the price drop. */
export function PriceTag({ price, was, label = 'Limited time discount', currency = '₹', period, badge, animate }: PriceTagProps) {
  const [phase, setPhase] = useState<'old' | 'strike' | 'done'>(animate && was !== undefined ? 'old' : 'done');
  useEffect(() => {
    if (!animate || was === undefined) return setPhase('done');
    setPhase('old');
    // Prototype timing: the coupon springs at 300ms, the price drops at 800ms (600ms).
    const t1 = setTimeout(() => setPhase('strike'), motion.duration.couponDelay);
    const t2 = setTimeout(() => setPhase('done'), motion.duration.priceDelay);
    return () => (clearTimeout(t1), clearTimeout(t2));
  }, [animate, was, price]);
  const saved = was !== undefined && typeof price === 'number' ? was - price : undefined;
  return (
    <div className={`eq-price ${phase !== 'done' ? `is-${phase}` : ''}`}>
      {label && (
        <Text as="div" variant="caption" align="center" style={{ textTransform: 'uppercase', color: 'var(--eq-text-gold)', fontWeight: 500 }}>
          <span aria-hidden>🎊 </span>{label}<span aria-hidden> 🎊</span>
        </Text>
      )}
      <div className="eq-price__row">
        <span className="eq-price__rule" />
        {phase === 'done' ? (
          <span className="eq-price__now">
            <span className="eq-gold-text eq-price__value">{currency}{price}</span>
            {was !== undefined && (
              <span className="eq-price__was">
                <s className="eq-tnum">{currency}{was}</s>
                <span>{period ?? '/Monthly'}</span>
              </span>
            )}
          </span>
        ) : (
          <span className="eq-gold-text eq-price__value eq-price__old">{currency}{was}<span className="eq-price__strike" aria-hidden /></span>
        )}
        <span className="eq-price__rule" />
      </div>
      {phase === 'done' && saved !== undefined && saved > 0 && <Text as="div" variant="caption" align="center" style={{ color: 'var(--eq-text-brand)', fontWeight: 700 }}>Saved {currency}{saved}</Text>}
      {phase === 'done' && badge && <DiscountBadge label={badge} animate={animate} />}
      {was === undefined && period && <Text as="div" variant="caption" color="tertiary" align="center" style={{ fontWeight: 400 }}>{period}</Text>}
    </div>
  );
}

/** Coded green wavy seal (12 lobes) behind "25% OFF". */
/** The coded green wavy seal behind "25% OFF" (inside DiscountBadge). */
export function OffSeal({ size = 28, off = '25%' }: { size?: number; off?: string }) {
  const lobes = 12, r = 50, amp = 4;
  const pts = Array.from({ length: 121 }, (_, i) => {
    const t = (i / 120) * Math.PI * 2;
    const rr = r - amp + amp * Math.cos(lobes * t);
    return `${(50 + rr * Math.cos(t)).toFixed(2)},${(50 + rr * Math.sin(t)).toFixed(2)}`;
  });
  return (
    <svg className="eq-off-seal" width={size} height={size} viewBox="0 0 100 100" aria-hidden>
      <polygon points={pts.join(' ')} fill="var(--eq-text-brand)" />
      <text x="50" y="47" textAnchor="middle" fontSize="26" fontWeight="700" fill="var(--eq-text-on-action)" fontFamily="DM Sans, sans-serif">{off}</text>
      <text x="50" y="72" textAnchor="middle" fontSize="22" fontWeight="700" fill="var(--eq-text-on-action)" fontFamily="DM Sans, sans-serif">OFF</text>
    </svg>
  );
}

/** "25% OFF" seal + a green label ("Discounted!!", "Special Coupon Applied!"). Pops in with `animate`. */
export function DiscountBadge({ label = 'Discounted!!', off = '25%', animate }: { label?: string; off?: string; animate?: boolean }) {
  return (
    <span className={`eq-discount-badge ${animate ? 'is-animated' : ''}`} role="status">
      <OffSeal off={off} />
      <span className="eq-t-caption" style={{ fontWeight: 700 }}>{label}</span>
    </span>
  );
}

/** Cream card on the hero that frames a preview of what's locked (rows, bubbles or a call summary). */
export function LockedPreviewCard({ children }: { children: ReactNode }) {
  return (
    <div className="eq-paywall-card">
      <div className="eq-paywall-card__inner">{children}</div>
    </div>
  );
}

export type CouponTicketProps = {
  code: string;
  description: ReactNode;
  /** Big gold line, e.g. "25% OFF". */
  discount: string;
  applied?: boolean;
  onApply?: () => void;
};

/**
 * Coupon ticket: notched card (`gradient/coupon`, 1dp `paywall/card-stroke`, radius 20). Code
 * subheading Bold `text/gold`, description caption Regular, APPLY / APPLIED (buttonSmall Bold
 * `text/gold`) on the right; a dashed rule with the 3D % seal (28dp); "25% OFF" 30/36 Bold gold.
 */
export function CouponTicket({ code, description, discount, applied, onApply }: CouponTicketProps) {
  return (
    <div className={`eq-coupon ${applied ? 'is-applied' : ''}`}>
      <div className="eq-coupon__top">
        <div style={{ minWidth: 0 }}>
          <Text as="div" variant="subheading" style={{ fontWeight: 700, color: 'var(--eq-text-gold)' }}>{code}</Text>
          <Text as="div" variant="caption" color="secondary" style={{ fontWeight: 400 }}>{description}</Text>
        </div>
        {applied ? (
          <span className="eq-t-button-small eq-coupon__state" aria-live="polite">APPLIED</span>
        ) : (
          <button type="button" className="eq-reset-btn eq-touch eq-t-button-small eq-coupon__state" onClick={onApply}>APPLY</button>
        )}
      </div>
      <div className="eq-coupon__rule"><span /><Asset name="coupon-seal" size={28} alt="" /><span /></div>
      <div className="eq-gold-text eq-coupon__discount">{discount}</div>
    </div>
  );
}

export type FaqItem = { q: string; a: ReactNode };

/** FAQ accordion under a "FREQUENTLY ASKED QUESTIONS" label. One white card, rows split by dividers. */
export function FaqList({ items, title = 'Frequently asked questions', defaultOpen = [] }: { items: FaqItem[]; title?: string | null; defaultOpen?: number[] }) {
  const [open, setOpen] = useState<number[]>(defaultOpen);
  return (
    <section className="eq-faq">
      {title && <DividerLabel>{title}</DividerLabel>}
      <div className="eq-faq__card">
        {items.map((it, i) => {
          const isOpen = open.includes(i);
          return <FaqEntry key={it.q} item={it} open={isOpen} onToggle={() => setOpen((o) => (isOpen ? o.filter((x) => x !== i) : [...o, i]))} />;
        })}
      </div>
    </section>
  );
}

/** One question in a FAQ: the question row with a chevron, and the answer when open. */
export function FaqEntry({ item, open, onToggle }: { item: FaqItem; open: boolean; onToggle?: () => void }) {
  return (
    <div className={`eq-faq__item ${open ? 'is-open' : ''}`}>
      <button type="button" className="eq-reset-btn eq-faq__q" aria-expanded={open} onClick={onToggle}>
        <Text as="span" variant="bodyLarge" style={{ flex: 1 }}>{item.q}</Text>
        <Icon name="chevron-down" size={20} color="var(--eq-text-secondary)" rotate={open ? 180 : 0} />
      </button>
      {open && <Text as="div" variant="body1" color="secondary" style={{ padding: '0 16px 14px' }}>{item.a}</Text>}
    </div>
  );
}

/**
 * Sticky paywall footer: block CTA + "Cancel Anytime. Automatically renews". Every paywall-style
 * footer uses it: the paywall (gold), the plan page (gold-outline Cancel Subscription), the sheet
 * paywall (`flat`), and the processing state (`disabled`, with the processing toast in `above`).
 */
export function PaywallFooter({ cta = 'Unlock Unlimited', note = 'Cancel Anytime. Automatically renews', onClick, variant = 'gold', disabled, flat, above }: { cta?: string; note?: string | null; onClick?: () => void; variant?: 'gold' | 'gold-outline' | 'primary'; disabled?: boolean; /** No surface or shadow: sits inside a sheet. */ flat?: boolean; /** Shown above the button, e.g. the processing toast. */ above?: ReactNode }) {
  return (
    <div className={`eq-paywall-footer ${flat ? 'is-flat' : ''}`}>
      {above && <div style={{ marginBottom: 10 }}>{above}</div>}
      <Button variant={variant} size="lg" block disabled={disabled} onClick={onClick}>{cta}</Button>
      {note && <Text as="div" variant="caption" color="secondary" align="center" style={{ fontWeight: 400, marginTop: 8 }}>{note}</Text>}
    </div>
  );
}

// ---------------------------------------------------------------- status cards & sheets

export type StatusTone = 'neutral' | 'success' | 'warning' | 'danger' | 'gold';

export type StatusCardProps = {
  tone: StatusTone;
  /** 3D art in the ring header (100dp). */
  art: AssetName;
  /** The two display lines: bold `strong` (28/32) and regular `light` (22/26). */
  strong: string;
  light?: string;
  /** Which line comes first ("Payment / Confirmed" vs "We're Sad to / See you Cancel"). */
  strongFirst?: boolean;
  body?: ReactNode;
  children?: ReactNode;
};

/**
 * The ring card used by every payment result and the cancellation steps: radius 20, tinted
 * gradient (`gradient/status-*`) with soft concentric rings behind 100dp 3D art, two display lines
 * in DM Sans (bold over regular, or the reverse), body (body1, text/secondary), optional content.
 */
export function StatusCard({ tone, art, strong, light, strongFirst = true, body, children }: StatusCardProps) {
  const lines = [
    <Text key="s" as="div" variant="headline" align="center" style={{ fontSize: 28, lineHeight: '32px', fontWeight: 700, letterSpacing: '-0.02em' }}>{strong}</Text>,
    light ? <Text key="l" as="div" variant="title" align="center" style={{ fontSize: 22, lineHeight: '26px', fontWeight: 400 }}>{light}</Text> : null,
  ];
  return (
    <div className={`eq-status-card is-${tone}`}>
      {(tone === 'success' || tone === 'gold') && <Sunburst />}
      <div className="eq-status-card__art">{art === 'crown-gold' ? <AnimatedCrown size={100} /> : <Asset name={art} size={100} alt="" />}</div>
      <div className="eq-status-card__title">{strongFirst ? lines : [...lines].reverse()}</div>
      {body && <Text as="p" variant="body1" color="secondary" align="center" style={{ marginTop: 8 }}>{body}</Text>}
      {children && <div style={{ marginTop: 16, width: '100%' }}>{children}</div>}
    </div>
  );
}

/** Live "Redirecting … in 00:5 Secs" countdown (text/action). Calls `onDone` at zero. */
export function RedirectNote({ seconds = 5, to, onDone }: { seconds?: number; to?: string; onDone?: () => void }) {
  const [left, setLeft] = useState(seconds);
  useEffect(() => setLeft(seconds), [seconds]);
  useEffect(() => {
    if (left <= 0) {
      // No destination (docs / pinned states): start the countdown again.
      if (onDone) onDone();
      else setLeft(seconds);
      return;
    }
    const t = setTimeout(() => setLeft((l) => l - 1), 1000);
    return () => clearTimeout(t);
  }, [left, onDone]);
  return (
    <Text as="div" variant="body2" color="action" align="center" aria-live="polite">
      Redirecting{to ? ` to ${to}` : ''} in 00:{left} Secs
    </Text>
  );
}

/**
 * Sheet over a dimmed screen with no close button (payment results, cancellation): scrim
 * `scrim`, panel `bg/surface` radius 24 top, padding 12 / 12 / 20; card on top, footer below.
 * `mode="inline"` renders it statically for docs.
 */
export function StatusSheet({ children, footer, mode = 'overlay', label = 'Status' }: { children: ReactNode; footer?: ReactNode; mode?: 'overlay' | 'inline'; label?: string }) {
  const panel = (
    <div className={`eq-status-sheet ${mode === 'inline' ? 'is-inline' : ''}`} role="dialog" aria-modal={mode === 'overlay'} aria-label={label}>
      {children}
      {footer && <div className="eq-status-sheet__footer">{footer}</div>}
    </div>
  );
  if (mode === 'inline') return panel;
  return (
    <div className="eq-status-sheet-layer">
      <div className="eq-status-sheet-layer__scrim" />
      {panel}
    </div>
  );
}

export type PaymentStatus = 'waiting' | 'confirmed' | 'pending' | 'failed' | 'welcome';

const PAYMENT: Record<PaymentStatus, { tone: StatusTone; art: AssetName; strong: string; light?: string; strongFirst: boolean }> = {
  waiting: { tone: 'neutral', art: 'status-waiting', strong: 'Confirmation', light: 'Waiting for', strongFirst: false },
  confirmed: { tone: 'success', art: 'status-confirmed', strong: 'Payment', light: 'Confirmed', strongFirst: true },
  pending: { tone: 'warning', art: 'status-pending', strong: 'Payment', light: 'Pending', strongFirst: true },
  failed: { tone: 'danger', art: 'status-failed', strong: 'Payment', light: 'Failed', strongFirst: true },
  welcome: { tone: 'gold', art: 'crown-gold', strong: 'Congratulations', light: 'Welcome to Equal Unlimited.', strongFirst: true },
};

/** Monetization local components design: the waiting sheet's title steps while the payment goes through. */
export const PAYMENT_PROGRESS = [
  { light: 'Connecting To', strong: 'Razorpay', strongFirst: false },
  { light: 'Waiting for', strong: 'Confirmation', strongFirst: false },
  { light: 'Verifying', strong: 'Transaction', strongFirst: false },
] as const;

function usePaymentProgress(on: boolean, every = 1400) {
  const [i, setI] = useState(0);
  useEffect(() => {
    if (!on) return setI(0);
    const t = setInterval(() => setI((x) => (x + 1) % PAYMENT_PROGRESS.length), every);
    return () => clearInterval(t);
  }, [on, every]);
  return i;
}

/**
 * Payment result sheet. waiting → "Once your payment is being verified…" (no action); confirmed →
 * redirect countdown; pending → "If money was deducted from card (**2652)…" + Contact Support;
 * failed → same body + Retry Payment; welcome → "No more limits…" + redirect to home.
 */
export function PaymentStatusSheet({ status, cardLast4 = '2652', onRetry, onContactSupport, onRedirect, mode = 'overlay', waitingArt = 'status-waiting', progress = true }: { status: PaymentStatus; cardLast4?: string; onRetry?: () => void; onContactSupport?: () => void; onRedirect?: () => void; mode?: 'overlay' | 'inline'; waitingArt?: 'status-waiting' | 'status-waiting-cash'; /** Waiting: cycle the progress steps (Connecting To Razorpay → Waiting for Confirmation → Verifying Transaction). */ progress?: boolean }) {
  const step = usePaymentProgress(status === 'waiting' && progress);
  const s = status === 'waiting' && progress ? { ...PAYMENT.waiting, ...PAYMENT_PROGRESS[step] } : PAYMENT[status];
  const refund = `If money was deducted from card (**${cardLast4}), refund will be initiated within 24hours`;
  const body =
    status === 'waiting' ? 'Once your payment is being verified. You will be Redirected Automatically' : status === 'pending' || status === 'failed' ? refund : status === 'welcome' ? 'No more limits. Everything Equal AI can do, now yours.' : undefined;
  const footer =
    status === 'confirmed' ? <RedirectNote seconds={5} onDone={onRedirect} /> : status === 'welcome' ? <RedirectNote seconds={5} to="home" onDone={onRedirect} /> : status === 'pending' ? <Button block size="lg" onClick={onContactSupport}>Contact Support</Button> : status === 'failed' ? <Button block size="lg" onClick={onRetry}>Retry Payment</Button> : undefined;
  return (
    <StatusSheet mode={mode} label={`${s.strong} ${s.light ?? ''}`} footer={footer}>
      <StatusCard key={status === 'waiting' ? `w${step}` : status} tone={s.tone} art={status === 'waiting' ? waitingArt : s.art} strong={s.strong} light={s.light} strongFirst={s.strongFirst} body={body} />
    </StatusSheet>
  );
}

/** Help Center sheet from Support: Call Us / Mail Us rows with green icon tiles. */
export function HelpCenterSheet({ phone = '+91 9876 543210', email = 'hello@equal.in', onCall, onMail, mode = 'overlay' }: { phone?: string; email?: string; onCall?: () => void; onMail?: () => void; mode?: 'overlay' | 'inline' }) {
  return (
    <StatusSheet mode={mode} label="Help Center">
      <div style={{ padding: '8px 8px 0' }}>
        <SheetHeading title="Help Center" body="At the moment we can help you in following ways, feel free to contact us." />
        <div style={{ display: 'flex', flexDirection: 'column', gap: 12, marginTop: 16 }}>
          <HelpContactRow title="Call Us" value={phone} icon="call" onClick={onCall} />
          <HelpContactRow title="Mail Us" value={email} icon="mail" onClick={onMail} />
        </div>
      </div>
    </StatusSheet>
  );
}

/** A sheet's title (title, 500) and one line of help under it. */
export function SheetHeading({ title, body }: { title: ReactNode; body?: ReactNode }) {
  return (
    <div>
      <Text as="h2" variant="title" style={{ fontWeight: 500 }}>{title}</Text>
      {body && <Text as="p" variant="body1" color="secondary" style={{ marginTop: 4 }}>{body}</Text>}
    </div>
  );
}

/** A way to reach support: title, value (number or email) and the green icon tile. */
export function HelpContactRow({ title, value, icon, onClick }: { title: string; value: string; icon: IconName; onClick?: () => void }) {
  return (
    <button type="button" className="eq-reset-btn eq-pressable eq-help-row" onClick={onClick}>
      <span style={{ flex: 1, minWidth: 0 }}>
        <Text as="div" variant="subheading">{title}</Text>
        <Text as="div" variant="body1" color="secondary">{value}</Text>
      </span>
      <span className="eq-help-row__icon"><Icon name={icon} size={22} color="var(--eq-text-action)" /></span>
    </button>
  );
}

/**
 * Social proof on the plan page: "Why people choose EQUAL UNLIMITED", the headline stat, the
 * rating and the testimonial carousel, on the gold-washed panel.
 */
export function SocialProof({ stat = { value: '400k+', label: 'People never miss a call' }, rating = 4.5, testimonials }: { stat?: { value: string; label: string }; rating?: number; testimonials: Testimonial[] }) {
  return (
    <section className="eq-subscription-proof">
      <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 2 }}>
        <DividerLabel>Why people choose</DividerLabel>
        <Text as="div" variant="title" style={{ fontWeight: 700, color: 'var(--eq-text-gold)' }}>EQUAL UNLIMITED</Text>
      </div>
      <StatBlock value={stat.value} label={stat.label} />
      <div style={{ display: 'flex', justifyContent: 'center' }}><RatingPill rating={rating} /></div>
      <div style={{ margin: '0 -16px' }}><TestimonialCarousel items={testimonials} /></div>
    </section>
  );
}

// ---------------------------------------------------------------- touchpoints

/**
 * Chat-thread paywall card, docked over the bottom of a locked conversation: gold ring header,
 * crown 56, "Unlock All Details" 22/26 Bold, "See full assistant response", green 🔒 Unlock Details.
 */
export function UnlockDetailsCard({ title = 'Unlock All Details', body = 'See full assistant response', cta = 'Unlock Details', onUnlock }: { title?: string; body?: string; cta?: string; onUnlock?: () => void }) {
  return (
    <div className="eq-unlock-card">
      <AnimatedCrown size={56} />
      <Text as="div" variant="title" align="center" style={{ fontWeight: 700, fontSize: 22, lineHeight: '26px', marginTop: 6 }}>{title}</Text>
      <Text as="div" variant="body1" color="secondary" align="center">{body}</Text>
      <div style={{ marginTop: 14 }}>
        <Button variant="gold" size="lg" leadingIcon="lock" onClick={onUnlock}>{cta}</Button>
      </div>
    </div>
  );
}

/** Pill over a locked recording: 🔒 Unlock Audio (green, 32 high). */
export function UnlockAudioPill({ onClick, label = 'Unlock Audio' }: { onClick?: () => void; label?: string }) {
  return (
    <button type="button" className="eq-reset-btn eq-pressable eq-unlock-audio" onClick={onClick}>
      <Icon name="lock" size={14} />
      <span className="eq-t-button-small" style={{ fontWeight: 700 }}>{label}</span>
    </button>
  );
}

/** Amber strip for a lapsed plan: info icon + "Reactivate your current Plan to …". */
export function PlanBanner({ children, onClick, attached }: { children: ReactNode; onClick?: () => void; /** Hangs under the plan card (rounded bottom only). */ attached?: boolean }) {
  return (
    <div role={onClick ? 'button' : 'status'} tabIndex={onClick ? 0 : undefined} onClick={onClick} className={`eq-plan-banner ${attached ? 'is-attached' : ''}`}>
      <Icon name="info-fill" size={16} />
      <Text as="span" variant="caption" style={{ color: 'inherit' }}>{children}</Text>
    </div>
  );
}

// ---------------------------------------------------------------- subscription page

/** Big gold stat ("400k+") with its label. */
export function StatBlock({ value, label }: { value: string; label: string }) {
  return (
    <div style={{ textAlign: 'center' }}>
      <div className="eq-gold-text eq-stat">{value}</div>
      <Text as="div" variant="body2">{label}</Text>
    </div>
  );
}

/** "⭐ Rated 4.5 /5" pill. */
export function RatingPill({ rating, outOf = 5 }: { rating: number; outOf?: number }) {
  return (
    <span className="eq-rating-pill">
      <Asset name="star-gold" size={20} alt="" />
      <Text variant="body2">Rated</Text>
      <Text variant="body2" style={{ fontWeight: 700 }}>{rating}</Text>
      <Text variant="body1" color="tertiary">/{outOf}</Text>
    </span>
  );
}

export type Testimonial = { quote: string; name: string; avatar?: ReactNode };

/** Horizontally scrolling testimonial cards (240 wide) with page dots. */
export function TestimonialCarousel({ items }: { items: Testimonial[] }) {
  const [page, setPage] = useState(0);
  return (
    <div className="eq-testimonials">
      <div
        className="eq-testimonials__track"
        onScroll={(e) => {
          const el = e.currentTarget;
          setPage(Math.round(el.scrollLeft / 252));
        }}
      >
        {items.map((t) => (
          <figure key={t.name} className="eq-testimonial">
            <Text as="blockquote" variant="body1" color="secondary" style={{ margin: 0 }}>{t.quote}</Text>
            <figcaption>
              {t.avatar}
              <Text variant="body2">{t.name}</Text>
            </figcaption>
          </figure>
        ))}
      </div>
      <div className="eq-dots" aria-hidden>
        {items.map((t, i) => <span key={t.name} className={i === page ? 'is-on' : ''} />)}
      </div>
    </div>
  );
}

// ---------------------------------------------------------------- cancellation

export type CancelStep = 'feedback' | 'confirm' | 'submitted';

/**
 * Cancellation sheet, three steps: feedback (sad face; textarea, Proceed disabled until there is
 * text), confirm ("Are you really Sure?" — Cancel outline / Confirm), submitted (thumbs up,
 * "Your Request is Submitted", redirect to home). Access stays until `accessUntil`.
 */
export function CancelSheet({ step, accessUntil = '15 June 2026', reason, onReasonChange, onProceed, onKeep, onConfirm, onRedirect, mode = 'overlay' }: { step: CancelStep; accessUntil?: string; reason?: string; onReasonChange?: (v: string) => void; onProceed?: () => void; onKeep?: () => void; onConfirm?: () => void; onRedirect?: () => void; mode?: 'overlay' | 'inline' }) {
  const [text, setText] = useState(reason ?? '');
  const value = reason ?? text;
  const set = (v: string) => (onReasonChange ? onReasonChange(v) : setText(v));
  if (step === 'feedback') {
    return (
      <StatusSheet mode={mode} label="Cancel subscription" footer={<Button block size="lg" disabled={!value.trim()} onClick={onProceed}>Proceed</Button>}>
        <StatusCard tone="neutral" art="emoji-sad" light="We’re Sad to" strong="See you Cancel" strongFirst={false} body="Tell us why you’re Cancelling Subscription. Your feedback helps us improve.">
          <textarea className="eq-cancel-reason eq-t-body1" value={value} onChange={(e) => set(e.target.value)} placeholder="Tell us more in detail.." rows={3} aria-label="Why are you cancelling?" />
        </StatusCard>
      </StatusSheet>
    );
  }
  if (step === 'confirm') {
    return (
      <StatusSheet
        mode={mode}
        label="Confirm cancellation"
        footer={
          <div style={{ display: 'flex', gap: 12 }}>
            <Button variant="outline" size="lg" block onClick={onKeep}>Cancel</Button>
            <Button size="lg" block onClick={onConfirm}>Confirm</Button>
          </div>
        }
      >
        <StatusCard tone="neutral" art="emoji-sad" light="Are you really" strong="Sure?" strongFirst={false} body={`Your subscription stays active & have full access until ${accessUntil}. We’ll stop the autopay, and your account will switch to free after that date.`} />
      </StatusSheet>
    );
  }
  return (
    <StatusSheet mode={mode} label="Request submitted" footer={<RedirectNote seconds={3} to="home" onDone={onRedirect} />}>
      <StatusCard tone="neutral" art="thumbs-up" light="Your Request is" strong="Submitted" strongFirst={false} body={`No more charges from us. You’ll keep full access until ${accessUntil}.`} />
    </StatusSheet>
  );
}

// ---------------------------------------------------------------- plan value & renewal (Monetization value design)

/** Slowly rotating ray burst behind success art (Payment Confirmed, Congratulations). Still under reduced motion. */
export function Sunburst({ rays = 24 }: { rays?: number }) {
  const step = 360 / rays;
  return <span className="eq-sunburst" aria-hidden style={{ ['--eq-sunburst-step' as string]: `${step}deg` }} />;
}

/** Where the plan stands. Drives the expiry chip, the home banner and the value card's date row. */
export type PlanExpiry =
  | { state: 'active'; until: string }
  | { state: 'expiring'; days: number }
  | { state: 'today' }
  | { state: 'expired'; date: string };

const pad = (n: number) => String(n).padStart(2, '0');

/** The date row text and chip for a plan state. */
export function expiryParts(e: PlanExpiry): { label: string; chip: string; tone: 'active' | 'warning' | 'danger' } {
  if (e.state === 'active') return { label: 'Plan active until', chip: e.until, tone: 'active' };
  if (e.state === 'expiring') return { label: 'Plan Expires in', chip: `${pad(e.days)} ${e.days === 1 ? 'Day' : 'Days'}`, tone: 'warning' };
  if (e.state === 'today') return { label: 'Plan Expires', chip: 'Today', tone: 'danger' };
  return { label: 'Plan Expired on', chip: e.date, tone: 'danger' };
}

/** "03 Days" · "Today" · "30th July" · "25th Jun ’26": the tinted chip on the value card's date row. */
export function ExpiryChip({ tone, children }: { tone: 'active' | 'warning' | 'danger'; children: ReactNode }) {
  return <span className={`eq-expiry-chip is-${tone} eq-tnum`}>{children}</span>;
}

/**
 * Home strip under the header when a plan is about to end or has ended: amber for "Expires in
 * 2 Days", red for "Expires Today" and "Expired on 30th July", with Renew Now.
 */
export function PlanExpiryBanner({ expiry, onRenew }: { expiry: Exclude<PlanExpiry, { state: 'active' }>; onRenew?: () => void }) {
  const tone = expiry.state === 'expiring' ? 'warning' : 'danger';
  const text =
    expiry.state === 'expiring' ? <>Your Plan Expires in <b>{expiry.days} {expiry.days === 1 ? 'Day' : 'Days'}</b></>
    : expiry.state === 'today' ? <>Your Plan Expires <b>Today</b></>
    : <>Your Plan Expired on <b>{expiry.date}</b></>;
  return (
    <div className={`eq-expiry-banner is-${tone}`} role="status">
      <span className="eq-expiry-banner__icon"><Icon name="info-fill" size={20} /></span>
      <Text as="span" variant="body2" style={{ color: 'inherit', flex: 1, minWidth: 0 }}>{text}</Text>
      <button type="button" className="eq-reset-btn eq-touch eq-expiry-banner__cta eq-t-button" onClick={onRenew}>Renew Now</button>
    </div>
  );
}

/**
 * Premium icon: a filled Material Symbol (16 dp by default) painted with the gold radial gradient
 * (`premium-icon/start` → `premium-icon/end`). Use on monetization perk rows; use the `-fill` icons.
 */
export function PremiumIcon({ name, size = 16 }: { name: IconName; size?: number }) {
  const id = `eq-premium-${useId().replace(/:/g, '')}`;
  const def = icons[name];
  return (
    <svg width={size} height={size} viewBox={def.viewBox} aria-hidden style={{ flexShrink: 0, display: 'block' }}>
      <defs>
        <radialGradient id={id} cx="30%" cy="25%" r="85%">
          <stop offset="0%" stopColor="var(--eq-premium-icon-start)" />
          <stop offset="100%" stopColor="var(--eq-premium-icon-end)" />
        </radialGradient>
      </defs>
      {def.paths.map((d, k) => <path key={k} d={d} fill={`url(#${id})`} />)}
    </svg>
  );
}

export type ValuePerk = { icon: IconName; text: ReactNode };

export const defaultValuePerks: ValuePerk[] = [
  { icon: 'timer', text: <>Your Assistant stayed <b>on duty 24/7</b></> },
  { icon: 'call-sparkle-fill', text: <>Screened &amp; Transcribed <b>every call</b></> },
  { icon: 'person', text: <>Never asked you to <b>deal with an Unknown Number</b></> },
];

export type ValueCardProps = {
  /** Calls the assistant handled this plan period. */
  calls: number;
  expiry: PlanExpiry;
  /** In-card heading (the sheet); `null` when the page shows it as a divider above. */
  title?: string | null;
  /** The three perk rows (the sheet); `null` hides them (the subscription page). */
  perks?: ValuePerk[] | null;
  plan?: string;
  price?: number;
  /** Struck list price. */
  was?: number;
  period?: string;
  /** Background: soft gold rings (sheet) or the gold circuit lines across the top (plan page). Default: circuit when `perks` is null. */
  pattern?: 'rings' | 'circuit';
};

/**
 * "What Unlimited saved you": the gold ring card that shows the plan's value before asking for a
 * renewal. Calls handled, the perks, the expiry row and the current plan with its price.
 */
export function ValueCard({ calls, expiry, title = 'What Unlimited saved you', perks = defaultValuePerks, plan = 'Equal AI Unlimited', price = 39, was = 49, period = '/Monthly', pattern }: ValueCardProps) {
  const e = expiryParts(expiry);
  const bg = pattern ?? (perks === null ? 'circuit' : 'rings');
  return (
    <section className={`eq-value-card is-${bg}`} aria-label={title ?? 'What Unlimited saved you'}>
      {bg === 'circuit' ? <Asset name="value-card-pattern" width={329} height={192} className="eq-value-card__circuit" /> : <Asset name="premium-glow" width={360} height={246} className="eq-value-card__glow" />}
      {title && <div className="eq-value-card__title"><DividerLabel><Text as="span" variant="subheading" style={{ textTransform: 'uppercase', fontWeight: 500 }}>{title}</Text></DividerLabel></div>}
      <div className="eq-value-card__stat">
        <span className="eq-value-card__count eq-tnum">{calls}</span>
        <span>
          <Text as="div" variant="title" style={{ fontWeight: 500 }}>Calls Handled</Text>
          <Text as="div" variant="body1" style={{ fontWeight: 400 }}>by your Assistant</Text>
        </span>
      </div>
      {perks && perks.length > 0 && (
        <ul className="eq-value-card__perks">
          {perks.map((p, i) => (
            <li key={i}>
              <span style={{ paddingTop: 3 }}><PremiumIcon name={p.icon} /></span>
              <Text as="span" variant="body2" style={{ fontWeight: 400 }}>{p.text}</Text>
            </li>
          ))}
        </ul>
      )}
      <div className="eq-value-card__expiry">
        <span className="eq-divider-label__rule" />
        <Text as="span" variant="body1" style={{ fontWeight: 400 }}>{e.label}</Text>
        <ExpiryChip tone={e.tone}>{e.chip}</ExpiryChip>
        <span className="eq-divider-label__rule" />
      </div>
      <div className="eq-value-card__plan">
        <span>
          <Text as="div" variant="caption" color="secondary" style={{ fontWeight: 400 }}>Current Plan</Text>
          <Text as="div" variant="title" style={{ fontWeight: 600 }}>{plan}</Text>
        </span>
        <span className="eq-tnum" style={{ whiteSpace: 'nowrap' }}>
          <Text as="span" variant="subheading" style={{ fontWeight: 700 }}>₹{price}</Text>{' '}
          {was !== undefined && <Text as="s" variant="caption" color="secondary" style={{ fontWeight: 400 }}>₹{was}</Text>}{' '}
          <Text as="span" variant="caption" color="secondary" style={{ fontWeight: 400 }}>{period}</Text>
        </span>
      </div>
    </section>
  );
}

/** Free-plan weekly quota. */
export type Quota = { used: number; limit: number; resetsInDays: number };

export const quotaPerks: ValuePerk[] = [
  { icon: 'timer', text: <>Your Assistant stayed <b>on duty 24/7</b></> },
  { icon: 'call-sparkle-fill', text: <b>Unlimited Recordings</b> },
  { icon: 'call-log', text: <>AI Call Summaries &amp; Transcripts</> },
];

/**
 * Free plan: how much of this week's call quota the assistant has used, with the upsell.
 * Under the limit: "6 out of 30 Calls Left" + a gold progress bar; at the limit: "Limit Reached".
 */
export function QuotaCard({ quota, perks = quotaPerks, plan = 'Equal AI Unlimited', price = 149, was = 249, period = '/Monthly' }: { quota: Quota; perks?: ValuePerk[] | null; plan?: string; price?: number; was?: number; period?: string }) {
  const { used, limit, resetsInDays } = quota;
  const pct = Math.min(100, Math.round((used / limit) * 100));
  const reached = used >= limit;
  const left = Math.max(0, limit - used);
  return (
    <section className="eq-value-card is-rings" aria-label={reached ? 'Limit reached' : `${left} out of ${limit} calls left`}>
      <Asset name="premium-glow" width={360} height={246} className="eq-value-card__glow" />
      <div className="eq-quota__head">
        {reached ? (
          <Text as="div" variant="headline" align="center" style={{ fontSize: 30, lineHeight: '36px', fontWeight: 700, letterSpacing: '-0.02em' }}>Limit Reached</Text>
        ) : (
          <Text as="div" variant="headline" align="center" className="eq-tnum" style={{ fontSize: 30, lineHeight: '36px', fontWeight: 700, letterSpacing: '-0.02em' }}>
            {left} out of {limit} <span style={{ fontSize: 18, fontWeight: 500, letterSpacing: 0 }}>Calls Left</span>
          </Text>
        )}
        {!reached && (
          <div className="eq-quota__bar" role="progressbar" aria-valuemin={0} aria-valuemax={limit} aria-valuenow={used} aria-label="Calls handled this week">
            <span style={{ width: `${pct}%` }} />
          </div>
        )}
        <Text as="p" variant="body1" color="secondary" align="center" style={{ margin: 0 }}>
          {reached ? <><b style={{ color: 'var(--eq-text-primary)' }}>{used} out of {limit} ({pct}%)</b> Of Your Calls Handled by your Assistant this week</> : <><b style={{ color: 'var(--eq-text-primary)' }}>{pct}%</b> Of Your Calls Handled by your Assistant this week</>}
        </Text>
      </div>
      <div className="eq-value-card__expiry">
        <span className="eq-divider-label__rule" />
        <Text as="span" variant="body1" style={{ fontWeight: 400 }}>Your Quota Resets in</Text>
        <ExpiryChip tone="active">{resetsInDays} {resetsInDays === 1 ? 'Day' : 'Days'}</ExpiryChip>
        <span className="eq-divider-label__rule" />
      </div>
      {perks && perks.length > 0 && (
        <ul className="eq-value-card__perks">
          {perks.map((p, i) => (
            <li key={i}>
              <span style={{ paddingTop: 3 }}><PremiumIcon name={p.icon} /></span>
              <Text as="span" variant="body2" style={{ fontWeight: 400 }}>{p.text}</Text>
            </li>
          ))}
        </ul>
      )}
      <div className="eq-value-card__plan">
        <span>
          <Text as="div" variant="caption" color="secondary" style={{ fontWeight: 400 }}>Subscribe to</Text>
          <Text as="div" variant="title" style={{ fontWeight: 600 }}>{plan}</Text>
        </span>
        <span className="eq-tnum" style={{ whiteSpace: 'nowrap' }}>
          <Text as="span" variant="subheading" style={{ fontWeight: 700 }}>₹{price}</Text>{' '}
          {was !== undefined && <Text as="s" variant="caption" color="secondary" style={{ fontWeight: 400 }}>₹{was}</Text>}{' '}
          <Text as="span" variant="caption" color="secondary" style={{ fontWeight: 400 }}>{period}</Text>
        </span>
      </div>
    </section>
  );
}

export type ValueSheetProps = {
  calls?: number;
  /** Paid plan state (required unless `quota` is set). */
  expiry?: PlanExpiry;
  onRenew?: () => void;
  onClose?: () => void;
  /** card: the value card inset on the sheet; flush: the card is the sheet's top (experiment). */
  variant?: 'card' | 'flush';
  cta?: string;
  mode?: 'overlay' | 'inline';
  perks?: ValuePerk[] | null;
  /** Free plan: show the weekly QuotaCard instead (CTA defaults to "Get Equal AI Unlimited"). */
  quota?: Quota;
};

/**
 * "Value bottom sheet once called": after the assistant handles a call near the end of a plan, a
 * sheet shows what Unlimited saved the user, with Renew your Equal AI. The close button floats above.
 */
export function ValueSheet({ calls = 20, expiry = { state: 'active', until: '' }, onRenew, onClose, variant = 'card', cta, mode = 'overlay', perks, quota }: ValueSheetProps) {
  const panel = (
    <div className={`eq-value-sheet is-${variant} ${mode === 'inline' ? 'is-inline' : ''}`} role="dialog" aria-modal={mode === 'overlay'} aria-label="What Unlimited saved you">
      <IconButton icon="close" label="Close" variant="neutral" size={44} className="eq-value-sheet__close" onClick={onClose} />
      <div className="eq-value-sheet__body">{quota ? <QuotaCard quota={quota} perks={perks} /> : <ValueCard calls={calls} expiry={expiry} perks={perks} />}</div>
      <div className="eq-value-sheet__footer"><Button variant="gold" size="lg" block onClick={onRenew}>{cta ?? (quota ? 'Get Equal AI Unlimited' : 'Renew your Equal AI')}</Button></div>
    </div>
  );
  if (mode === 'inline') return panel;
  return (
    <div className="eq-status-sheet-layer">
      <div className="eq-status-sheet-layer__scrim" onClick={onClose} />
      {panel}
    </div>
  );
}

export type FeatureItem = { art: AssetName; label: string };

export const unlimitedFeatures: FeatureItem[] = [
  { art: 'feature-call-screening', label: '24/7 Unlimited Call Screening' },
  { art: 'feature-recordings', label: 'Unlimited Recordings' },
  { art: 'feature-summaries', label: 'Unlimited Summaries' },
];

/** "What You Get": one card per Unlimited feature, 28dp 3D gold icon + label. */
export function FeatureList({ items = unlimitedFeatures }: { items?: FeatureItem[] }) {
  return (
    <ul className="eq-feature-list">
      {items.map((f) => (
        <li key={f.label}>
          <Asset name={f.art} size={28} alt="" />
          <Text as="span" variant="subheading" style={{ fontWeight: 400 }}>{f.label}</Text>
        </li>
      ))}
    </ul>
  );
}

export { TextButton as PaywallLink };
