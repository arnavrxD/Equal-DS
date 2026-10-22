// Known Call Usage Cap design: the Home card in every state and option, the gold limit sheets, the
// green View Details sheet, the P0 free home, and a live run. Every number is a prop.
import { useState } from 'react';
import { UsageDetailsSheet, UsageLimitCard, ValueSheet, type CallSplit, type UsageLimitCardProps, type UsageMeterCardProps, type UsageState } from '../index';
import { HomeScreen } from './Screens';

export const usageTotal = 80;
export const usageLimits: CallSplit = { known: 68, unknown: 18 };

/** The Home card data per state (the design's numbers). */
export const usageCards: Record<UsageState, Omit<UsageMeterCardProps, 'cta'>> = {
  under: { used: { known: 9, unknown: 4 }, total: usageTotal, resetsIn: '6 days', state: 'under' },
  nudge: { used: { known: 49, unknown: 15 }, total: usageTotal, resetsIn: '3 days', state: 'nudge' },
  exhausted: { used: { known: 62, unknown: 18 }, total: usageTotal, resetsIn: '2 days', state: 'exhausted' },
  paused: { used: { known: 62, unknown: 18 }, total: usageTotal, pausedUntil: 'Monday', state: 'paused' },
  'known-cap': { used: { known: 62, unknown: 18 }, total: usageTotal, resetsIn: '2 days', state: 'known-cap' },
  'unknown-cap': { used: { known: 18, unknown: 40 }, total: usageTotal, resetsIn: '2 days', state: 'unknown-cap' },
};

export type UsageSheetKey = '20-used' | '20-percent' | '20-rows' | '100' | '100-rows' | 'known' | 'known-rows' | 'unknown' | 'unknown-rows';

/** The gold weekly limit sheets. */
export const usageSheets: Record<UsageSheetKey, Omit<UsageLimitCardProps, 'limits' | 'total'>> = {
  '20-used': { used: { known: 12, unknown: 4 }, head: 'used', meter: 'stacked', resetsInDays: 4 },
  '20-percent': { used: { known: 12, unknown: 4 }, head: 'percent', meter: 'stacked', resetsInDays: 4 },
  '20-rows': { used: { known: 12, unknown: 4 }, head: 'percent', meter: 'rows', resetsInDays: 4 },
  '100': { used: { known: 62, unknown: 18 }, head: 'reached', meter: 'stacked', resetsInDays: 2 },
  '100-rows': { used: { known: 62, unknown: 18 }, head: 'reached', meter: 'rows', resetsInDays: 2 },
  known: { used: { known: 68, unknown: 12 }, head: 'reached', meter: 'stacked', reason: 'known', resetsInDays: 2 },
  'known-rows': { used: { known: 68, unknown: 12 }, head: 'reached', meter: 'rows', reason: 'known', resetsInDays: 2 },
  unknown: { used: { known: 48, unknown: 18 }, head: 'reached', meter: 'stacked', reason: 'unknown', resetsInDays: 2 },
  'unknown-rows': { used: { known: 48, unknown: 18 }, head: 'reached', meter: 'rows', reason: 'unknown', resetsInDays: 2 },
};

export type UsageCapHomeProps = {
  state?: UsageState;
  cta?: UsageMeterCardProps['cta'];
  /** Pin a gold limit sheet over Home. */
  sheet?: UsageSheetKey;
  /** Pin the green View Details sheet (option A perks, option B info note). */
  details?: 'perks' | 'info';
  /** P0 baseline: no usage card, the 👑 Upgrade pill. */
  p0?: boolean;
  onUpgrade?: () => void;
};

/** Free Home in one usage state. View Details / the chevron open the details sheet; closing a sheet keeps Home. */
export function UsageCapHome({ state = 'nudge', cta = 'details', sheet, details, p0, onUpgrade }: UsageCapHomeProps) {
  const [openSheet, setOpenSheet] = useState(sheet);
  const [openDetails, setOpenDetails] = useState(details);
  const card = usageCards[state];
  const overlay = openSheet ? (
    <ValueSheet card={<UsageLimitCard {...usageSheets[openSheet]} limits={usageLimits} total={usageTotal} />} cta={openSheet.startsWith('20') ? 'Upgrade to Equal AI Unlimited' : 'Get Equal AI Unlimited'} onRenew={onUpgrade} onClose={() => setOpenSheet(undefined)} />
  ) : openDetails ? (
    <UsageDetailsSheet used={{ known: 9, unknown: 4 }} limits={{ known: 68, unknown: 12 }} total={usageTotal} resetsInDays={2} body={openDetails} onUpgrade={onUpgrade} onClose={() => setOpenDetails(undefined)} />
  ) : undefined;
  if (p0) return <HomeScreen tier="free" assistantOff upgradePill promo="meet-assistant-carousel" onRenew={onUpgrade} />;
  return (
    <HomeScreen
      tier="free"
      assistantOff={state === 'exhausted' || state === 'paused'}
      promo="meet-assistant-carousel"
      usageMeter={{ ...card, cta, onUpgrade, onDetails: () => setOpenDetails('perks') }}
      overlay={overlay}
    />
  );
}
