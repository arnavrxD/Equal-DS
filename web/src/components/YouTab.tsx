// You tab surfaces — from the You (Settings) design.
import type { ReactNode } from 'react';
import { Asset } from '../foundations/Asset';
import { Icon } from '../foundations/Icon';
import { Text } from '../foundations/Text';
import { AiAvatar, ProfileAvatar, type PlanTier, type ProfileAvatarVariant } from './Avatar';
import { PlanBadge } from './Badge';
import { IconButton } from './Button';
import './YouTab.css';

// ---------------------------------------------------------------- Profile hero

export type ProfileHeroProps = {
  name: string;
  phone: string;
  avatar?: ProfileAvatarVariant;
  /** Paid (default while a plan is set) shows the gold-ringed avatar; free the plain bust. */
  tier?: PlanTier;
  plan?: string | null;
  onEdit?: () => void;
  /** Rendered at the bottom of the gold hero (the plan card overlaps it). */
  children?: ReactNode;
};

/** Gold gradient header: profile avatar, headline name + plan marker + edit, phone; hosts the plan card. */
export function ProfileHero({ name, phone, avatar = 'male', plan = 'Unlimited', tier = plan ? 'paid' : 'free', onEdit, children }: ProfileHeroProps) {
  return (
    <section className="eq-hero">
      <svg className="eq-hero__arcs" viewBox="0 0 360 260" preserveAspectRatio="xMidYMin slice" aria-hidden>
        <circle cx="180" cy="300" r="250" />
        <circle cx="180" cy="300" r="290" />
      </svg>
      {/* The header is light in both themes, so its content always renders with light tokens. */}
      <div className="eq-hero__row" data-theme="light">
        <ProfileAvatar variant={avatar} size={70} tier={tier} />
        <div style={{ minWidth: 0, flex: 1 }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
            <Text as="h1" variant="headline" truncate style={{ flexShrink: 1 }}>{name}</Text>
            {plan && <PlanBadge label={plan} />}
            {onEdit && <IconButton icon="edit-profile" label="Edit profile" onClick={onEdit} size={28} iconSize={18} />}
          </div>
          <Text as="div" variant="body1" color="secondary" className="eq-tnum">{phone}</Text>
        </div>
      </div>
      {children && <div className="eq-hero__footer">{children}</div>}
    </section>
  );
}

// ---------------------------------------------------------------- Plan card

export type PlanCardProps = {
  product?: string;
  tier?: string;
  price?: string;
  status?: 'current' | 'expired';
  onManage?: () => void;
  /** Link label: 'Manage plan' (active), 'Restore Plan' or 'Reactivate plan' (lapsed). */
  actionLabel?: string;
};

/** Gold line pattern (coded) — the right half of the plan card. */
export function PlanPattern() {
  const lines = Array.from({ length: 9 }, (_, i) => i * 7);
  return (
    <svg className="eq-plan__pattern" viewBox="0 0 180 110" preserveAspectRatio="xMaxYMax slice" aria-hidden>
      <defs>
        <linearGradient id="eq-plan-fade" x1="0" x2="1" y1="0" y2="0">
          <stop offset="0" stopColor="var(--eq-plan-card-pattern)" stopOpacity="0" />
          <stop offset="0.55" stopColor="var(--eq-plan-card-pattern)" stopOpacity="0.55" />
          <stop offset="1" stopColor="var(--eq-plan-card-pattern)" stopOpacity="0.8" />
        </linearGradient>
      </defs>
      {lines.map((o) => (
        <polyline key={o} points={`${20 + o},110 ${20 + o},${80 - o * 0.2} ${60 + o},${40 - o * 0.2} ${120 + o},${40 - o * 0.2} ${150 + o},${10 - o * 0.2} ${190},${10 - o * 0.2}`} fill="none" stroke="url(#eq-plan-fade)" strokeWidth="0.9" />
      ))}
      {lines.slice(0, 5).map((o) => (
        <polyline key={`b${o}`} points={`${90 + o},110 ${90 + o},${95 - o * 0.3} ${110 + o},${75 - o * 0.3} ${190},${75 - o * 0.3}`} fill="none" stroke="url(#eq-plan-fade)" strokeWidth="0.9" />
      ))}
    </svg>
  );
}

/** Dark plan card — gold "Current Plan" pill, product in title token, gold tier, coded gold pattern. */
export function PlanCard({ product = 'Equal AI', tier = 'UNLIMITED', price = '₹149/Monthly', status = 'current', onManage, actionLabel = 'Manage plan' }: PlanCardProps) {
  return (
    <div className="eq-plan">
      <PlanPattern />
      <div className="eq-plan__top">
        <span className={`eq-plan__pill ${status === 'expired' ? 'is-expired' : ''}`}>
          <Text variant="caption" color="on-dark" style={status === 'expired' ? { fontWeight: 500 } : undefined}>{status === 'expired' ? 'Plan Expired' : 'Current Plan'}</Text>
        </span>
        {onManage && (
          <button type="button" className="eq-reset-btn eq-touch eq-t-button-small eq-plan__manage" onClick={onManage}>
            {actionLabel}
          </button>
        )}
      </div>
      <Text as="div" variant="title" color="on-dark" style={{ marginTop: 12, position: 'relative' }}>
        {product} <span style={{ color: 'var(--eq-text-accent-gold)' }}>{tier}</span>
      </Text>
      <Text as="div" variant="caption" color="on-dark-secondary" className="eq-tnum" style={{ fontWeight: 400, position: 'relative' }}>{price}</Text>
    </div>
  );
}

// ---------------------------------------------------------------- AI Summary

export type AiSummaryStatus = 'empty' | 'loading' | 'ready';

export type AiSummaryCardProps = {
  status?: AiSummaryStatus;
  summary?: ReactNode;
  /** Footer line. Defaults per state: empty/ready → "Usually ready after your first few calls". */
  footer?: string;
  /** Shown in the loading state, e.g. "8:06 AM". */
  lastUpdated?: string;
  label?: string;
};

/** "Your AI Summary" — prose the assistant wrote about the user. Three states: empty · loading · ready. */
export function AiSummaryCard({ status, summary, footer, lastUpdated = '8:06 AM', label = 'Your AI Summary' }: AiSummaryCardProps) {
  const s: AiSummaryStatus = status ?? (summary ? 'ready' : 'empty');
  const foot = footer ?? (s === 'loading' ? `Last Updated: ${lastUpdated}` : 'Usually ready after your first few calls');
  return (
    <div className={`eq-summary-card is-${s}`} aria-busy={s === 'loading'}>
      <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
        <AiAvatar size={28} />
        <Text variant="caption" color="secondary">{label}</Text>
      </div>
      <div className="eq-summary-card__body">
        {s === 'empty' && (
          <Text as="p" variant="bodyLarge" align="center" style={{ fontWeight: 500 }}>I'm still getting to know you...</Text>
        )}
        {s === 'loading' && (
          <div className="eq-skeleton" aria-label="Summary is updating">
            {[100, 100, 100, 100, 100].map((w, i) => (
              <span key={i} style={{ width: `${w}%`, animationDelay: `${i * 90}ms` }} />
            ))}
          </div>
        )}
        {s === 'ready' && <Text as="p" variant="bodyLarge">{summary}</Text>}
      </div>
      <Text as="div" variant="caption" color="tertiary" align="right" style={{ fontWeight: 400 }}>{foot}</Text>
    </div>
  );
}

// ---------------------------------------------------------------- Invite

export type InviteCardProps = {
  title?: string;
  subtitle?: string;
  onShare?: () => void;
  /** Degrees. The supplied art already carries the design's −6.39° tilt, so this defaults to 0. */
  artRotation?: number;
};

/** "Invite a Friend" — mint pill card; the 3D high-five art (113 × 112) rises 29dp above the card, flush left. */
export function InviteCard({ title = 'Invite a Friend', subtitle = 'Invite Friends To Join', onShare, artRotation = 0 }: InviteCardProps) {
  return (
    <div className="eq-invite">
      <span className="eq-invite__art" style={artRotation ? { transform: `rotate(${artRotation}deg)` } : undefined}>
        <Asset name="illustration-high-five" width={113} height={112} />
      </span>
      <div style={{ flex: 1, minWidth: 0 }}>
        <Text as="div" variant="subheading" truncate>{title}</Text>
        <Text as="div" variant="caption" color="secondary" truncate style={{ fontWeight: 400 }}>{subtitle}</Text>
      </div>
      <button type="button" className="eq-reset-btn eq-pressable eq-invite__share" aria-label="Share invite" onClick={onShare}>
        <Icon name="share" size={22} />
      </button>
    </div>
  );
}
