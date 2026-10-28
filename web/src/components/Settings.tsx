// Updated UI settings and promos: Call Learning cards, the Account & Data trust strip, the Teach Your
// Assistant promo and the generic feature-intro sheet.
import { type ReactNode } from 'react';
import { useModal } from '../foundations/a11y';
import { Asset, type AssetName } from '../foundations/Asset';
import { Emoji, Icon } from '../foundations/Icon';
import type { IconName } from '../foundations/icons';
import { Text } from '../foundations/Text';
import { Button, IconButton, TextButton } from './Button';
import { NewBadge } from './Badge';
import './Settings.css';

export type LearningTint = 'orange' | 'lime' | 'green';

/** What the assistant learns, one card of the carousel: emoji tile (56), title, why, and an example in a glass box. */
export function LearningCard({ emoji, tint, title, body, example, width = 320 }: { emoji: string; tint: LearningTint; title: string; body: string; example: ReactNode; width?: number }) {
  return (
    <article className={`eq-learning-card is-${tint}`} style={{ width }}>
      <span className="eq-learning-card__tile" aria-hidden><Emoji char={emoji} size={32} /></span>
      <Text as="h3" variant="body2" style={{ fontWeight: 600, marginTop: 12 }}>{title}</Text>
      <Text as="p" variant="caption" color="secondary" style={{ margin: '2px 0 0', fontWeight: 400 }}>{body}</Text>
      <Text as="p" variant="captionWrap" className="eq-learning-card__example">{example}</Text>
    </article>
  );
}

export const learningCards = [
  { emoji: '📍', tint: 'orange' as const, title: 'Your addresses & directions', body: 'So it can take delivery and pickup calls for you — no stepping away to explain where you live.', example: 'Delivery agent calls for directions → assistant guides them to Gate 2, Tower B' },
  { emoji: '👪', tint: 'lime' as const, title: 'Your relationships', body: 'Helps respond differently to family, friends, coworkers, and businesses.', example: '“Your mom says it’s urgent.” → assistant knows family calls matter, so it gets the details for you.' },
  { emoji: '🔔', tint: 'green' as const, title: 'Your call preferences', body: 'Understands what type of promotional calls you are interested in and which ones annoy you.', example: '“Your bank needs identity verification.” → assistant notifies you immediately; routine promos are handled quietly.' },
];

export type TrustStat = { art: AssetName; value: string; label: string };

/** Account & Data: three stats with 40 art, 1dp dividers, on `trust-strip/bg` (radius 20). */
export function TrustStrip({ stats }: { stats: TrustStat[] }) {
  return (
    <ul className="eq-trust-strip">
      {stats.map((s) => (
        <li key={s.label}>
          <Asset name={s.art} size={40} alt="" />
          <Text as="div" variant="body2" align="center" style={{ fontWeight: 600, marginTop: 6 }}>{s.value}</Text>
          <Text as="div" variant="caption" color="secondary" align="center" style={{ fontWeight: 400 }}>{s.label}</Text>
        </li>
      ))}
    </ul>
  );
}

export const trustStats: TrustStat[] = [
  { art: 'mascot-update', value: '5L+', label: 'Indian Users' },
  { art: 'stat-star', value: '4.4 star', label: 'Rating' },
  { art: 'stat-phone', value: '150 Mn+', label: 'Calls Handled' },
];

/** Teach Your Assistant (Preset Instruction design): the teal promo in the home carousel. */
export function InstructionsPromoCard({ title = 'Teach Your Assistant', body = 'Pick a ready-made instruction or write your own', cta = 'Try Instructions', onCta, onDismiss }: { title?: string; body?: string; cta?: string; onCta?: () => void; onDismiss?: () => void }) {
  return (
    <div className="eq-instr-promo">
      <div style={{ flex: 1, minWidth: 0 }}>
        <Text as="h3" variant="title" color="none" style={{ fontWeight: 700, color: 'var(--eq-instructions-promo-title)' }}>{title}</Text>
        <Text as="p" variant="caption" style={{ margin: '4px 0 0', fontWeight: 400 }}>{body}</Text>
        <button type="button" className="eq-reset-btn eq-touch eq-instr-promo__cta" onClick={onCta}>
          <Icon name="instructions" size={16} />
          <Text variant="buttonSmall" color="none" style={{ fontWeight: 700 }}>{cta}</Text>
        </button>
      </div>
      <Asset name="mascot-instructions" size={96} alt="" />
      {onDismiss && <IconButton icon="close" label="Dismiss" onClick={onDismiss} size={32} iconSize={20} className="eq-instr-promo__close" />}
    </div>
  );
}

export type FeatureIntroSheetProps = {
  hero: ReactNode;
  badge?: string;
  title: string;
  bullets: { icon: IconName; text: string }[];
  cta: string;
  onCta?: () => void;
  secondary?: string;
  onSecondary?: () => void;
  onClose?: () => void;
  mode?: 'overlay' | 'inline';
};

/** New-feature sheet: tinted hero, NEW FEATURE badge, headline, icon bullets, primary CTA and a text secondary. */
export function FeatureIntroSheet({ hero, badge = 'New feature', title, bullets, cta, onCta, secondary, onSecondary, onClose, mode = 'overlay' }: FeatureIntroSheetProps) {
  const modal = useModal({ open: true, onClose, enabled: mode === 'overlay' });
  const panel = (
    <div className={`eq-feature-intro${mode === 'inline' ? ' is-inline' : ''}`} role="dialog" aria-label={title} {...modal}>
      <div className="eq-feature-intro__hero">{hero}</div>
      {onClose && <IconButton icon="close" label="Close" variant="glass" size={40} onClick={onClose} className="eq-feature-intro__close" />}
      <div className="eq-feature-intro__body">
        <NewBadge label={badge} tone="soft" />
        <Text as="h2" variant="headline" style={{ fontWeight: 600, marginTop: 8 }}>{title}</Text>
        <ul className="eq-feature-intro__bullets">
          {bullets.map((b) => (
            <li key={b.text}><Icon name={b.icon} size={20} color="var(--eq-text-action)" /><Text variant="body1" style={{ fontWeight: 500 }}>{b.text}</Text></li>
          ))}
        </ul>
        <Button size="lg" block onClick={onCta}>{cta}</Button>
        {secondary && <div style={{ display: 'flex', justifyContent: 'center', marginTop: 8 }}><TextButton onClick={onSecondary}>{secondary}</TextButton></div>}
      </div>
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
