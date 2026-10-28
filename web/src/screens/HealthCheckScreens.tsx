// Assistant Health Check (Assistant Healthcheck design): every state, and a live run.
import { useEffect, useRef, useState } from 'react';
import { AppBar, Button, DeviceScreen, HealthCheckCard, HealthHero, PermissionRow, Screen, SystemNotification, Text, type CardStatus, type HeroState, type RowStatus } from '../index';
import { HomeScreen } from './Screens';

export type HcCard = { status: CardStatus; expanded?: boolean; rows: RowStatus[]; action?: 'grant' };
export type HcCarrier = { status: CardStatus; subtitle?: boolean; reasons?: boolean; action?: 'fix' | 'fixing' | 'retry' };
export type HcState = { hero: HeroState; done?: boolean; app: HcCard; phone: HcCard; carrier: HcCarrier; cta: 'start' | 'checking' | 'home'; last: string };

const APP_ROWS = [
  { icon: 'phone-incoming', title: 'Phone', description: 'Your assistant needs this to handle calls' },
  { icon: 'notifications', title: 'Notifications', description: 'Required to send you call alerts' },
  { icon: 'contacts', title: 'Contacts', description: 'Required to skip calls from saved contacts' },
] as const;
const PHONE_ROWS = [{ icon: 'layers', title: 'Overlay', description: 'Required to show call controls over your screen' }] as const;
const REASONS = { lead: 'Carrier check failed. This may be due to:', items: ['Your connection may be temporarily unavailable. Check if you are connected to the Internet.','Your recharge plan might be inactive or expired'] };

const idle = (expanded = false): HcState => ({ hero: 'idle', app: { status: 'none', expanded, rows: ['idle', 'idle', 'idle'] }, phone: { status: 'none', expanded, rows: ['idle'] }, carrier: { status: 'none', subtitle: true }, cta: 'start', last: 'Last checked: 2h ago' });
const run = (app: RowStatus[], appStatus: CardStatus = 'none', hero: HeroState = 'loading', action?: 'grant'): HcState => ({ hero, app: { status: appStatus, expanded: true, rows: app, action }, phone: { status: 'none', rows: ['idle'] }, carrier: { status: 'none', subtitle: true }, cta: 'checking', last: 'Last checked: 2h ago' });
const appOk = { status: 'passed' as CardStatus, rows: ['granted', 'granted', 'granted'] as RowStatus[] };
const phoneOk = { status: 'passed' as CardStatus, rows: ['granted'] as RowStatus[] };
const late = (phone: HcCard, carrier: HcCarrier, hero: HeroState = 'loading'): HcState => ({ hero, app: appOk, phone, carrier, cta: 'checking', last: 'Last checked: 2h ago' });

/** Every designed state, keyed as on the flow page. */
export const healthCheckStates: Record<string, HcState> = {
  idle: idle(),
  expanded: idle(true),
  'phone-checking': run(['checking', 'idle', 'idle']),
  'notification-checking': run(['granted', 'checking', 'idle']),
  'contacts-checking': run(['granted', 'notGranted', 'checking']),
  'permissions-failed': run(['granted', 'notGranted', 'notGranted'], 'none', 'fail', 'grant'),
  'overlay-pending': late({ status: 'none', expanded: true, rows: ['pending'] }, { status: 'pending' }),
  'overlay-checking': late({ status: 'none', expanded: true, rows: ['checking'] }, { status: 'pending' }),
  'overlay-failed': late({ status: 'none', expanded: true, rows: ['notGranted'], action: 'grant' }, { status: 'pending' }, 'fail'),
  'carrier-pending': late(phoneOk, { status: 'pending' }),
  'carrier-checking': late(phoneOk, { status: 'checking', subtitle: true }),
  'carrier-issues': late(phoneOk, { status: 'issues', subtitle: true, action: 'fix' }, 'fail'),
  fixing: late(phoneOk, { status: 'issues', subtitle: true, action: 'fixing' }, 'loading'),
  'fix-failed': late(phoneOk, { status: 'issues', reasons: true, action: 'retry' }, 'fail'),
  'fixing-again': late(phoneOk, { status: 'issues', reasons: true, action: 'fixing' }, 'loading'),
  'check-failed': late(phoneOk, { status: 'failed', reasons: true, action: 'retry' }, 'fail'),
  'all-good': { hero: 'pass', done: true, app: appOk, phone: phoneOk, carrier: { status: 'passed' }, cta: 'home', last: 'Last checked: 1s ago' },
};

export type HealthCheckHandlers = { onStart?: () => void; onHome?: () => void; onBack?: () => void; onGrantApp?: () => void; onGrantPhone?: () => void; onFix?: () => void; onToggleApp?: () => void; onTogglePhone?: () => void };

/** The Health Check screen for one state (390 wide; taller states scroll). */
export function HealthCheckScreen({ state, onStart, onHome, onBack, onGrantApp, onGrantPhone, onFix, onToggleApp, onTogglePhone }: { state: HcState } & HealthCheckHandlers) {
  const s = state;
  const running = s.cta === 'checking';
  return (
    <Screen
      header={<AppBar statusBar title="Assistant Health Check" onBack={onBack} />}
      footer={
        // Pinned: the cards scroll above it as they expand.
        <div style={{ padding: '12px 24px 24px', background: 'var(--eq-bg-primary)' }}>
          <Button block size="lg" disabled={running} onClick={s.cta === 'home' ? onHome : onStart}>{s.cta === 'home' ? 'Back To Home' : running ? 'Checking…' : 'Start Health Check'}</Button>
          <Text as="p" variant="captionWrap" color="secondary" align="center" style={{ marginTop: 8 }}>{s.last}</Text>
        </div>
      }
    >
      <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', padding: '16px 24px 24px' }}>
        <HealthHero state={s.hero} size={180} />
        <Text as="h1" variant="headline" style={{ alignSelf: 'stretch', marginTop: 16 }}>{s.done ? 'Check complete. Your assistant is good to go' : 'Check your assistant’s health. See if it’s operational.'}</Text>
        <div style={{ alignSelf: 'stretch', display: 'flex', flexDirection: 'column', gap: 8, marginTop: 24 }} aria-busy={running || undefined}>
          <HealthCheckCard title="App Permissions" status={s.app.status} expandable={!running || s.app.status === 'passed'} expanded={s.app.expanded} onToggle={onToggleApp} action={s.app.action ? { label: 'Grant Permission', onClick: onGrantApp } : undefined}>
            {APP_ROWS.map((r, i) => <PermissionRow key={r.title} icon={r.icon} title={r.title} description={r.description} status={s.app.rows[i]} />)}
          </HealthCheckCard>
          <HealthCheckCard title="Phone settings" status={s.phone.status} expandable={!running || s.phone.status === 'passed'} expanded={s.phone.expanded} onToggle={onTogglePhone} action={s.phone.action ? { label: 'Grant Permission', onClick: onGrantPhone } : undefined}>
            {PHONE_ROWS.map((r, i) => <PermissionRow key={r.title} icon={r.icon} title={r.title} description={r.description} status={s.phone.rows[i]} />)}
          </HealthCheckCard>
          <HealthCheckCard
            title="Carrier Check"
            status={s.carrier.status}
            subtitle={s.carrier.subtitle ? 'Verifies your calls reach the assistant' : undefined}
            reasons={s.carrier.reasons ? REASONS : undefined}
            action={s.carrier.action ? { label: s.carrier.action === 'retry' ? 'Try Again' : 'Fix Now', loading: s.carrier.action === 'fixing', onClick: onFix } : undefined}
          />
        </div>
      </div>
    </Screen>
  );
}

/**
 * Live run (Try it): Start → Phone ✓, Notification ✗, Contacts ✗ → Grant Permission → re-check ✓ →
 * Overlay ✗ → Grant → ✓ → carrier finds issues → Fix Now → the first fix fails → Try Again → All good.
 */
export function HealthCheckFlow({ state: initial = 'idle', onBack = () => {}, onHome }: { state?: keyof typeof healthCheckStates; onBack?: () => void; /** Back To Home after the check; default restarts at idle. */ onHome?: () => void }) {
  const [s, setS] = useState<HcState>(healthCheckStates[initial]);
  const timers = useRef<number[]>([]);
  const fixes = useRef(0);
  useEffect(() => { setS(healthCheckStates[initial]); return () => timers.current.forEach(clearTimeout); }, [initial]);
  const seq = (steps: HcState[], gap = 900) => { timers.current.forEach(clearTimeout); timers.current = steps.map((st, i) => window.setTimeout(() => setS(st), gap * (i + 1))); };
  const H = healthCheckStates;
  const start = () => { setS(H['phone-checking']); seq([H['notification-checking'], H['contacts-checking'], H['permissions-failed']]); };
  const grantApp = () => { setS(run(['granted', 'checking', 'checking'], 'none', 'loading')); seq([H['overlay-pending'], H['overlay-checking'], H['overlay-failed']]); };
  const grantPhone = () => { setS({ ...H['overlay-checking'], hero: 'loading' }); seq([H['carrier-pending'], H['carrier-checking'], H['carrier-issues']]); };
  const fix = () => { fixes.current += 1; const ok = fixes.current > 1; setS(fixes.current > 1 ? H['fixing-again'] : H.fixing); seq([ok ? H['all-good'] : H['fix-failed']], 1800); };
  return (
    <HealthCheckScreen
      state={s}
      onBack={onBack}
      onStart={start}
      onHome={() => { fixes.current = 0; if (onHome) onHome(); else setS(H.idle); }}
      onGrantApp={grantApp}
      onGrantPhone={grantPhone}
      onFix={fix}
      onToggleApp={() => setS((v) => ({ ...v, app: { ...v.app, expanded: !v.app.expanded } }))}
      onTogglePhone={() => setS((v) => ({ ...v, phone: { ...v.phone, expanded: !v.phone.expanded } }))}
    />
  );
}

/** How the user gets to Health Check: the notification (collapsed / with actions) or Home with the Fix Now strip. */
export function HealthCheckEntry({ entry, onFix }: { entry: 'notification' | 'notification-actions' | 'home'; onFix?: () => void }) {
  if (entry === 'home') return <HomeScreen healthAlert={{ onFix }} />;
  return (
    <DeviceScreen>
      <div style={{ padding: '8px 8px 0' }} className="eq-heads-up">
        <SystemNotification title="Assistant health" body="Calls are bypassing Equal AI. Fix now." actions={entry === 'notification-actions' ? [{ label: 'Fix Now', onClick: onFix }, { label: 'Notify Later' }] : undefined} />
      </div>
    </DeviceScreen>
  );
}
