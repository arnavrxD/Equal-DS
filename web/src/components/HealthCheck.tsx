// Assistant Health Check (Assistant Healthcheck design): the hero, check cards, permission rows.
import { useState, type ReactNode } from 'react';
import { Icon } from '../foundations/Icon';
import type { IconName } from '../foundations/icons';
import { Text } from '../foundations/Text';
import { Button } from './Button';
import { Lottie } from './Lottie';
import './HealthCheck.css';

export type HeroState = 'idle' | 'loading' | 'pass' | 'fail';

/**
 * The hero (shield + status coin): the supplied Lotties, chained by state. idle = loading frame 0;
 * loading loops; pass / fail play the transition from the previous state and hold the last frame.
 * Transitions out of pass / fail into loading play check→loading / cross→loading, then the loop.
 */
export function HealthHero({ state, size = 200 }: { state: HeroState; size?: number }) {
  const [from, setFrom] = useState<HeroState>(state);
  // Settled once the transition into `state` has played (no one-frame flash of the end state).
  const settled = from === state;
  const transition: Record<string, string> = {
    'loading>pass': 'health-loading-check', 'idle>pass': 'health-loading-check', 'loading>fail': 'health-loading-cross', 'idle>fail': 'health-loading-cross',
    'fail>loading': 'health-cross-loading', 'pass>loading': 'health-check-loading', 'fail>pass': 'health-cross-check', 'pass>fail': 'health-check-cross',
  };
  const key = `${from}>${state}`;
  const name = !settled && transition[key] ? transition[key] : state === 'pass' ? 'health-loading-check' : state === 'fail' ? 'health-loading-cross' : 'health-loading';
  const looping = name === 'health-loading' && state === 'loading';
  const pinned = settled && (state === 'pass' || state === 'fail');
  return (
    <div className="eq-health-hero" style={{ width: size * 0.8, height: size }} aria-hidden>
      <Lottie
        key={`${name}-${pinned}`}
        name={name}
        width={size * 0.8}
        height={size}
        loop={looping}
        still={pinned || state === 'idle'}
        stillFrame={state === 'pass' || state === 'fail' ? 'last' : 'first'}
        onComplete={() => { if (!settled) setFrom(state); }}
      />
    </div>
  );
}

export type HealthTone = 'neutral' | 'progress' | 'success' | 'danger';

/** Status label: spinner / filled check / warning (14) + `caption`; announced politely. */
export function HealthStatusLabel({ tone, label }: { tone: HealthTone; label: string }) {
  const icon: IconName | null = tone === 'progress' ? 'progress-activity' : tone === 'success' ? 'check-circle-fill' : tone === 'danger' ? 'warning-fill' : null;
  const color = tone === 'success' ? 'success' : tone === 'danger' ? 'danger' : tone === 'progress' ? 'primary' : 'secondary';
  return (
    <span className="eq-health-label" aria-live="polite">
      {icon && <Icon name={icon} size={14} color={`var(--eq-text-${color})`} className={tone === 'progress' ? 'eq-spin' : undefined} />}
      <Text variant="caption" color={color}>{label}</Text>
    </span>
  );
}

export type RowStatus = 'idle' | 'pending' | 'checking' | 'granted' | 'notGranted';

/**
 * 48 status ring (2dp) around a 40 `health-check/icon-bg` disc with a 24 icon. The icon is outlined in
 * `text/placeholder`; only the row being checked fills it, in `text/secondary`, while the arc spins.
 * Icons never turn red: Not Granted shows on the ring and the label.
 */
export function StatusRing({ status, icon, size = 48 }: { status: RowStatus; icon: IconName; size?: number }) {
  const filled = status === 'checking' ? (`${icon}-fill` as IconName) : icon;
  return (
    <span className={`eq-ring eq-ring--${status}`} style={{ width: size, height: size }} aria-hidden>
      {status === 'checking' && <span className="eq-ring__arc" />}
      <span className="eq-ring__disc"><Icon name={filled} size={24} /></span>
    </span>
  );
}

const ROW_LABEL: Partial<Record<RowStatus, [HealthTone, string]>> = { pending: ['neutral', 'Pending'], checking: ['progress', 'Checking'], granted: ['success', 'Granted'], notGranted: ['danger', 'Not Granted'] };

/** Permission row: ring → title (`body2`) over description (`captionWrap`) → status at the top-right. */
export function PermissionRow({ icon, title, description, status }: { icon: IconName; title: string; description: string; status: RowStatus }) {
  const l = ROW_LABEL[status];
  return (
    <div className="eq-perm-row" role="group" aria-label={`${title}${l ? `, ${l[1]}` : ''}`}>
      <StatusRing status={status} icon={icon} />
      <div style={{ flex: 1, minWidth: 0 }}>
        <Text as="div" variant="body2">{title}</Text>
        <Text as="p" variant="captionWrap" color="secondary">{description}</Text>
      </div>
      {l && <HealthStatusLabel tone={l[0]} label={l[1]} />}
    </div>
  );
}

export type CardStatus = 'none' | 'pending' | 'checking' | 'passed' | 'issues' | 'failed';
const CARD_LABEL: Partial<Record<CardStatus, [HealthTone, string]>> = { pending: ['neutral', 'Pending'], checking: ['progress', 'Checking'], passed: ['success', 'All Good'], issues: ['danger', 'Issues Found'], failed: ['danger', 'Check Failed'] };

export type HealthCheckCardProps = {
  title: string;
  status?: CardStatus;
  subtitle?: string;
  /** Group cards expand to show their permission rows. */
  expandable?: boolean;
  expanded?: boolean;
  onToggle?: () => void;
  children?: ReactNode;
  /** Carrier failure: a lead line and bullets. */
  reasons?: { lead: string; items: string[] };
  action?: { label: string; onClick?: () => void; loading?: boolean };
};

/**
 * Check card (radius 16, `bg/surface`, 1dp `stroke/subtle`): header (title `subheading` + status +
 * drop-down caret when expandable), optional subtitle, rows separated by dividers, failure reasons,
 * and an inline md block action (Grant Permission / Fix Now / Fixing / Try Again).
 */
export function HealthCheckCard({ title, status = 'none', subtitle, expandable, expanded, onToggle, children, reasons, action }: HealthCheckCardProps) {
  const l = CARD_LABEL[status];
  const caret = expandable && status !== 'checking';
  const head = (
    <>
      <span style={{ flex: 1, minWidth: 0 }}>
        <Text as="span" variant="subheading" style={{ display: 'block' }}>{title}</Text>
        {subtitle && <Text as="span" variant="captionWrap" color="secondary" style={{ display: 'block' }}>{subtitle}</Text>}
      </span>
      {l && <HealthStatusLabel tone={l[0]} label={l[1]} />}
      {caret && <Icon name="arrow-drop-down" size={24} color="var(--eq-text-secondary)" className={`eq-health-caret ${expanded ? 'is-open' : ''}`} />}
    </>
  );
  return (
    <section className="eq-health-card" aria-label={title}>
      {caret ? (
        <button type="button" className="eq-reset-btn eq-health-card__head" aria-expanded={!!expanded} onClick={onToggle}>{head}</button>
      ) : (
        <div className="eq-health-card__head">{head}</div>
      )}
      {expanded && children && <div className="eq-health-card__rows">{children}</div>}
      {reasons && (
        <div className="eq-health-card__reasons">
          <Text as="p" variant="captionWrap" color="secondary">{reasons.lead}</Text>
          <ul>{reasons.items.map((r) => <li key={r}><Text variant="captionWrap" color="secondary">{r}</Text></li>)}</ul>
        </div>
      )}
      {action && <div className="eq-health-card__action"><Button block size="md" loading={action.loading} onClick={action.onClick}>{action.loading ? 'Fixing' : action.label}</Button></div>}
    </section>
  );
}
