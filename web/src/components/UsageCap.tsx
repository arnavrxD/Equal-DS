// Known Call Usage Cap design: the weekly allowance split by caller type (known / unknown), on the
// Home card, the gold limit sheets and the green View Details sheet. Every number is a prop.
import { type ReactNode } from 'react';
import { useModal } from '../foundations/a11y';
import { Asset } from '../foundations/Asset';
import { Icon } from '../foundations/Icon';
import { Text } from '../foundations/Text';
import { Button, IconButton, TextButton } from './Button';
import { ExpiryChip, PerkList, PlanPriceRow, quotaPerks, type ValuePerk } from './Monetization';
import './UsageCap.css';

export type CallSplit = { known: number; unknown: number };
export type MeterTone = 'known' | 'unknown' | 'gold' | 'gold-deep';

/** A track with flat segments butted together (1px gap); one progressbar with a text value. */
export function SegmentedMeter({ segments, max, height = 6, label, valueText, gold }: { segments: { value: number; tone: MeterTone }[]; max: number; height?: 6 | 8; label: string; valueText: string; gold?: boolean }) {
  const used = segments.reduce((a, s) => a + s.value, 0);
  return (
    <div className={`eq-meter${gold ? ' is-gold' : ''}`} style={{ height }} role="progressbar" aria-label={label} aria-valuemin={0} aria-valuemax={max} aria-valuenow={Math.min(used, max)} aria-valuetext={valueText}>
      {segments.filter((s) => s.value > 0).map((s, i) => <span key={i} className={`eq-meter__seg is-${s.tone}`} style={{ width: `${Math.min(100, (s.value / max) * 100)}%` }} />)}
    </div>
  );
}

/** Legend swatch + label. */
export function MeterLegend({ items, trailing }: { items: { tone: MeterTone; label: ReactNode }[]; trailing?: ReactNode }) {
  return (
    <div className="eq-meter-legend">
      {items.map((it, i) => (
        <span key={i} className="eq-meter-legend__item">
          <span className={`eq-meter-legend__swatch is-${it.tone}`} aria-hidden />
          <Text variant="caption" color="secondary">{it.label}</Text>
        </span>
      ))}
      {trailing && <Text variant="caption" color="secondary" style={{ marginLeft: 'auto', fontWeight: 400 }}>{trailing}</Text>}
    </div>
  );
}

/** One row of the rows layout: label · value ("**n** out of **m**", or a plain count) · 8 bar. */
export function MeterRow({ label, used, limit, tone, plain }: { label: string; used: number; limit: number; tone: MeterTone; /** Show only the count, zero-padded ("04"). */ plain?: boolean }) {
  const pad = (n: number) => String(n).padStart(2, '0');
  return (
    <div className="eq-meter-row">
      <div className="eq-meter-row__head">
        <Text variant="caption">{label}</Text>
        <Text variant="caption" color="secondary" className="eq-tnum">
          {plain ? <Text as="b" variant="inherit" color="primary" style={{ fontWeight: 700 }}>{pad(used)}</Text> : <><Text as="b" variant="inherit" color="primary" style={{ fontWeight: 700 }}>{pad(used)}</Text> out of <Text as="b" variant="inherit" color="primary" style={{ fontWeight: 700 }}>{pad(limit)}</Text></>}
        </Text>
      </div>
      <SegmentedMeter segments={[{ value: used, tone }]} max={limit} height={8} gold={tone === 'gold' || tone === 'gold-deep'} label={label} valueText={`${used} of ${limit} used`} />
    </div>
  );
}

export type UsageState = 'under' | 'nudge' | 'exhausted' | 'paused' | 'known-cap' | 'unknown-cap';

/** Which state a split is in, from the shared total and the per-type limits. */
export function usageState(used: CallSplit, total: number, limits?: CallSplit): UsageState {
  if (limits && used.known >= limits.known && used.known + used.unknown < total) return 'known-cap';
  if (limits && used.unknown >= limits.unknown && used.known + used.unknown < total) return 'unknown-cap';
  const pct = (used.known + used.unknown) / total;
  return pct >= 1 ? 'exhausted' : pct >= 0.8 ? 'nudge' : 'under';
}

export type UsageMeterCardProps = {
  used: CallSplit;
  /** The shared weekly allowance (80 in the design). */
  total: number;
  /** Per-type caps; needed for the known-cap / unknown-cap states. */
  limits?: CallSplit;
  /** Derived from used / total / limits when omitted; `paused` is copy 2 of 100%. */
  state?: UsageState;
  resetsIn?: string;
  pausedUntil?: string;
  /** Experiment: A View Details footer, B chevron chip + green CTA, C chevron chip + gold CTA. */
  cta?: 'details' | 'chevron' | 'chevron-gold';
  /** Default: shown from 20 % used. */
  showUpgrade?: boolean;
  /** Phone glyph by default; null hides it. */
  lead?: ReactNode | null;
  onUpgrade?: () => void;
  onDetails?: () => void;
};

/** Home card under the assistant toggle: calls left, the known / unknown meter, reset, and the upgrade path. */
export function UsageMeterCard({ used, total, limits, state, resetsIn = '3 days', pausedUntil = 'Monday', cta = 'details', showUpgrade, lead, onUpgrade, onDetails }: UsageMeterCardProps) {
  const s = state ?? usageState(used, total, limits);
  const sum = used.known + used.unknown;
  const left = Math.max(0, total - sum);
  const upgrade = showUpgrade ?? sum / total >= 0.2;
  const title = s === 'exhausted' ? 'Weekly Call limit reached' : s === 'paused' ? `Assistant Paused till ${pausedUntil}` : s === 'known-cap' ? 'Known Call limit reached' : s === 'unknown-cap' ? 'Unknown Call limit reached' : `${left} Calls left this week`;
  const usedWord = s === 'paused' ? ' used' : '';
  const known = { tone: 'known' as const, label: <>{used.known} Known{usedWord || (s === 'known-cap' ? ' used' : '')}</> };
  const unknown = { tone: 'unknown' as const, label: <>{used.unknown} Unknown{usedWord || (s === 'unknown-cap' ? ' used' : '')}</> };
  const unknownFirst = s === 'unknown-cap';
  const segs = unknownFirst ? [{ value: used.unknown, tone: 'unknown' as const }, { value: used.known, tone: 'known' as const }] : [{ value: used.known, tone: 'known' as const }, { value: used.unknown, tone: 'unknown' as const }];
  const chevron = cta !== 'details';
  return (
    <section className="eq-usage-card" aria-label="Weekly calls">
      <span className="eq-usage-card__glow" aria-hidden />
      <div className="eq-usage-card__top">
        <div className="eq-usage-card__row">
          {lead !== null && (lead ?? <Icon name="call-filled" size={16} />)}
          <Text as="h3" variant="body2" style={{ flex: 1, minWidth: 0, margin: 0 }}>{title}</Text>
          {chevron ? (
            <button type="button" className="eq-reset-btn eq-touch eq-usage-card__chip" aria-label="View details" onClick={onDetails}><Icon name="chevron-right" size={16} /></button>
          ) : upgrade ? (
            <Button size="xs" onClick={onUpgrade}>Upgrade Now</Button>
          ) : null}
        </div>
        <SegmentedMeter segments={segs} max={total} label="Weekly calls used" valueText={`${used.known} known, ${used.unknown} unknown of ${total} calls used`} />
        <MeterLegend items={unknownFirst ? [unknown, known] : [known, unknown]} trailing={s === 'paused' ? undefined : `Resets in ${resetsIn}`} />
      </div>
      {cta === 'details' ? (
        <div className="eq-usage-card__foot">
          <TextButton size="small" trailingIcon="chevron-right" onClick={onDetails}>View Details</TextButton>
        </div>
      ) : upgrade ? (
        <div className="eq-usage-card__foot is-block">
          <Button size="xs" block appearance={cta === 'chevron-gold' ? 'premium' : 'default'} onClick={onUpgrade}>Upgrade Now</Button>
        </div>
      ) : null}
    </section>
  );
}

export type UsageLimitCardProps = {
  used: CallSplit;
  limits: CallSplit;
  total: number;
  /** used: "Used 20% of your Weekly AI Calls"; percent: "20%" beside "Limit Consumed"; reached: "Weekly Limit Reached". */
  head?: 'used' | 'percent' | 'reached';
  meter?: 'stacked' | 'rows';
  /** Which cap was hit (copy stays "Weekly Limit Reached"; it orders the meter). */
  reason?: 'weekly' | 'known' | 'unknown';
  resetsInDays: number;
  perks?: ValuePerk[] | null;
  plan?: string;
  price?: number;
  was?: number;
  period?: string;
};

/** The gold card on the weekly limit sheets (20 %, 100 %, known cap, unknown cap). */
export function UsageLimitCard({ used, limits, total, head = 'reached', meter = 'stacked', reason = 'weekly', resetsInDays, perks = quotaPerks, plan = 'Equal AI Unlimited', price = 199, was = 249, period = '/Monthly' }: UsageLimitCardProps) {
  const sum = used.known + used.unknown;
  const pct = Math.min(100, Math.round((sum / total) * 100));
  const left = Math.max(0, total - sum);
  const reached = head === 'reached';
  const usedWord = reached ? ' used' : '';
  const stackedItems = reason === 'unknown'
    ? [{ tone: 'gold' as const, label: <>{used.unknown} Unknown{usedWord}</> }, { tone: 'gold-deep' as const, label: <>{used.known} Known</> }]
    : reached
      ? [{ tone: 'gold-deep' as const, label: <>{used.known} Known used</> }, { tone: 'gold' as const, label: <>{used.unknown} Unknown{reason === 'known' ? '' : ' used'}</> }]
      : [{ tone: 'gold' as const, label: <>{used.unknown} Unknown</> }, { tone: 'gold-deep' as const, label: <>{used.known} Known</> }];
  const segs = stackedItems.map((it) => ({ value: it.tone === 'gold' ? used.unknown : used.known, tone: it.tone }));
  const big = { fontSize: 30, lineHeight: '36px', fontWeight: 700, letterSpacing: '-0.02em' } as const;
  return (
    <section className="eq-value-card is-rings" aria-label={reached ? 'Weekly limit reached' : `${pct}% of weekly calls used`}>
      <Asset name="premium-glow" width={360} height={246} className="eq-value-card__glow" />
      <div className="eq-quota__head">
        {head === 'used' && <Text as="div" variant="headline" align="center" style={big}>Used {pct}% of your Weekly AI Calls</Text>}
        {head === 'percent' && (
          <div style={{ display: 'flex', alignItems: 'center', gap: 12, justifyContent: 'center' }}>
            <Text as="div" variant="headline" className="eq-tnum" style={big}>{pct}%</Text>
            <span>
              <Text as="div" variant="subheading">Limit Consumed</Text>
              <Text as="div" variant="subheading" style={{ fontWeight: 600 }}>{left} Calls Left this week</Text>
            </span>
          </div>
        )}
        {reached && (
          <>
            <Text as="div" variant="headline" align="center" style={{ fontWeight: 700 }}>Weekly Limit Reached</Text>
            <Text as="p" variant="body1" color="secondary" align="center" style={{ margin: 0 }}>
              <Text as="b" variant="inherit" color="primary">{sum} out of {total} ({pct}%)</Text> Of Your Calls Handled by your Assistant this week
            </Text>
          </>
        )}
        {meter === 'stacked' ? (
          <div className="eq-usage-gold-box">
            <SegmentedMeter segments={segs} max={total} height={8} gold label="Weekly calls used" valueText={`${used.known} known, ${used.unknown} unknown of ${total} calls used`} />
            <MeterLegend items={stackedItems} />
          </div>
        ) : (
          <div className="eq-usage-gold-box">
            <MeterRow label="Known Calls" used={used.known} limit={limits.known} tone="gold-deep" plain={!reached} />
            <MeterRow label="Unknown Calls" used={used.unknown} limit={limits.unknown} tone="gold" plain={!reached} />
          </div>
        )}
        {head === 'used' && meter === 'stacked' && <Text as="div" variant="subheading" align="center">{left} Calls Left this week</Text>}
      </div>
      <div className="eq-value-card__expiry">
        <span className="eq-divider-label__rule" />
        <Text as="span" variant="body1" style={{ fontWeight: 400 }}>Resets in</Text>
        <ExpiryChip tone="active">{resetsInDays} {resetsInDays === 1 ? 'Day' : 'Days'}</ExpiryChip>
        <span className="eq-divider-label__rule" />
      </div>
      {perks && perks.length > 0 && <PerkList perks={perks} />}
      <PlanPriceRow label="Subscribe to" plan={plan} price={price} was={was} period={period} />
    </section>
  );
}

/** Orbit hero on View Details: three rings, six sparkles, a glow and the 80 disc with call-sparkle. Coded; still under reduced motion. */
export function UsageOrbit() {
  const sparkles = [[18, 30], [82, 22], [8, 70], [92, 66], [30, 92], [70, 90]];
  return (
    <div className="eq-orbit" aria-hidden>
      {[276, 186, 116].map((d, i) => <span key={d} className="eq-orbit__ring" style={{ width: d, height: d, animationDelay: `${i * 400}ms` }} />)}
      {sparkles.map(([x, y], i) => (
        <svg key={i} className="eq-orbit__spark" style={{ left: `${x}%`, top: `${y}%`, animationDelay: `${i * 300}ms` }} width="17" height="17" viewBox="0 0 24 24"><path d="M12 0c.6 6.4 5 11 12 12-7 1-11.4 5.6-12 12-.6-6.4-5-11-12-12C7 11 11.4 6.4 12 0Z" /></svg>
      ))}
      <span className="eq-orbit__glow" />
      <span className="eq-orbit__disc"><Icon name="call-sparkle-fill" size={44} color="var(--eq-text-on-action)" /></span>
    </div>
  );
}

export type UsageDetailsSheetProps = {
  used: CallSplit;
  limits: CallSplit;
  total: number;
  resetsInDays: number;
  /** Option A: the perk list; option B: the info note. */
  body?: 'perks' | 'info';
  /** The unknown-call protection limit named in the info note (30 in the design). */
  protectionLimit?: number;
  onUpgrade?: () => void;
  onClose?: () => void;
  mode?: 'overlay' | 'inline';
};

/** The green View Details sheet: orbit hero, calls left, per-type meters, reset, perks or the info note, Get Equal AI Unlimited. */
export function UsageDetailsSheet({ used, limits, total, resetsInDays, body = 'perks', protectionLimit = 30, onUpgrade, onClose, mode = 'overlay' }: UsageDetailsSheetProps) {
  const modal = useModal({ open: true, onClose, enabled: mode === 'overlay' });
  const left = Math.max(0, total - used.known - used.unknown);
  const panel = (
    <div className={`eq-usage-details${mode === 'inline' ? ' is-inline' : ''}`} role="dialog" aria-label="Weekly call details" {...modal}>
      {onClose && <IconButton icon="close" label="Close" variant="neutral" size={44} onClick={onClose} className="eq-usage-details__close" />}
      <UsageOrbit />
      <Text as="h2" variant="title" align="center" style={{ fontWeight: 700, marginTop: 24 }}>{left} Calls left this week</Text>
      <div className="eq-usage-details__meters">
        <MeterRow label="Known Calls" used={used.known} limit={limits.known} tone="known" />
        <MeterRow label="Unknown Calls" used={used.unknown} limit={limits.unknown} tone="unknown" />
      </div>
      <div className="eq-value-card__expiry is-green">
        <span className="eq-divider-label__rule" />
        <Text as="span" variant="body1" style={{ fontWeight: 400 }}>Resets in</Text>
        <ExpiryChip tone="success">{resetsInDays} {resetsInDays === 1 ? 'Day' : 'Days'}</ExpiryChip>
        <span className="eq-divider-label__rule" />
      </div>
      {body === 'perks' ? (
        <ul className="eq-usage-perks">
          {quotaPerks.map((p, i) => (
            <li key={i}><Icon name={p.icon} size={20} color="var(--eq-text-action)" /><Text variant="body1">{i === 0 ? 'Your Assistant stayed on duty 24/7' : i === 1 ? 'Unlimited Recordings' : 'AI Call Summaries & Transcripts'}</Text></li>
          ))}
        </ul>
      ) : (
        <div className="eq-usage-note" role="note">
          <span className="eq-usage-note__ripple" aria-hidden />
          <Text as="div" variant="subheading" style={{ display: 'flex', alignItems: 'center', gap: 4, fontWeight: 600, color: 'var(--eq-info-note-title)' }}><Icon name="info-fill" size={20} /> Know how weekly call limit works</Text>
          <Text as="p" variant="caption" color="secondary" style={{ margin: '8px 0 0', fontWeight: 400 }}>Known and unknown calls share the same {total}-call allowance.</Text>
          <Text as="p" variant="caption" color="secondary" style={{ margin: '4px 0 0', fontWeight: 400 }}>Unknown calls also have a {protectionLimit}-call protection limit. If one call type reaches its limit first, the other can continue while shared capacity remains.</Text>
        </div>
      )}
      <div style={{ alignSelf: 'stretch', marginTop: 24 }}><Button size="lg" block onClick={onUpgrade}>Get Equal AI Unlimited</Button></div>
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
