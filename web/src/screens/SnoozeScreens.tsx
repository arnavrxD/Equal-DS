// Assistant Snooze + Force Update designs: every state on Home, and live runs.
import { useEffect, useRef, useState, type ReactNode } from 'react';
import { AlertSheet, ForceUpdateScreen, SnoozeDurationDialog, SystemNotification, TroubleshootList, activationFixes, alertSheets, type AlertSheetKind, type SnoozeDuration } from '../index';
import { HomeScreen } from './Screens';

type Toggle = { on: boolean; busy?: 'deactivating' | 'activating'; offDescription?: ReactNode };
type Overlay = { kind: 'dialog'; value: SnoozeDuration | null } | { kind: 'notification'; body: string } | { kind: 'sheet'; sheet: AlertSheetKind };
export type SnoozeState = { toggle: Toggle; overlay?: Overlay };

const back = (when: string) => <>Equal will be back <b>{when}</b></>;
const RESTING = 'Assistant is resting.';

/** Every designed state, keyed as on the flow page (`flow-snooze / <key>`). */
export const snoozeStates: Record<string, SnoozeState> = {
  on: { toggle: { on: true } },
  'dialog-empty': { toggle: { on: true }, overlay: { kind: 'dialog', value: null } },
  'dialog-selected': { toggle: { on: true }, overlay: { kind: 'dialog', value: '15m' } },
  deactivating: { toggle: { on: true, busy: 'deactivating' } },
  'off-notification': { toggle: { on: false, offDescription: back('Today 6:45pm') }, overlay: { kind: 'notification', body: 'Equal AI will be back 6:45pm today' } },
  off: { toggle: { on: false, offDescription: back('Today 6:45pm') } },
  'off-resting': { toggle: { on: false, offDescription: RESTING } },
  activating: { toggle: { on: false, busy: 'activating', offDescription: back('Today 6:45pm') } },
  'on-again': { toggle: { on: true } },
  'error-airplane': { toggle: { on: true }, overlay: { kind: 'sheet', sheet: 'airplane' } },
  'error-snooze-failed': { toggle: { on: true }, overlay: { kind: 'sheet', sheet: 'snooze-failed' } },
  'error-offline': { toggle: { on: true }, overlay: { kind: 'sheet', sheet: 'offline' } },
  'error-activation-failed': { toggle: { on: false, offDescription: back('Today 6:45pm') }, overlay: { kind: 'sheet', sheet: 'activation-failed' } },
};

/** The return time for a duration, on the sample clock (12:30 pm). */
export function snoozeUntil(d: SnoozeDuration): { toggle: ReactNode; notification: string } {
  switch (d) {
    case '15m': return { toggle: back('Today 12:45pm'), notification: 'Equal AI will be back 12:45pm today' };
    case '1h': return { toggle: back('Today 1:30pm'), notification: 'Equal AI will be back 1:30pm today' };
    case '6h': return { toggle: back('Today 6:30pm'), notification: 'Equal AI will be back 6:30pm today' };
    case 'rest-of-day': return { toggle: back('Tomorrow'), notification: 'Equal AI will be back tomorrow' };
    case 'manual': return { toggle: RESTING, notification: 'Turn it back on from Chats anytime' };
  }
}

export function SheetLayer({ children, onClose }: { children: ReactNode; onClose?: () => void }) {
  return (
    <div className="eq-status-sheet-layer">
      <div className="eq-status-sheet-layer__scrim" onClick={onClose} />
      <div style={{ position: 'relative' }}>{children}</div>
    </div>
  );
}

export type SnoozeHandlers = {
  onToggle?: (on: boolean) => void;
  onSelect?: (v: SnoozeDuration) => void;
  onConfirm?: (v: SnoozeDuration) => void;
  onCancel?: () => void;
  onSheetCta?: () => void;
  onSheetSecondary?: () => void;
  onSheetClose?: () => void;
};

/** Home in one Snooze state. */
export function SnoozeScreen({ state, onToggle, onSelect, onConfirm, onCancel, onSheetCta, onSheetSecondary, onSheetClose }: { state: SnoozeState } & SnoozeHandlers) {
  const o = state.overlay;
  const overlay = !o ? undefined
    : o.kind === 'dialog' ? <SnoozeDurationDialog value={o.value} onChange={onSelect} onConfirm={onConfirm} onCancel={onCancel} />
    : o.kind === 'notification' ? (
      <div className="eq-heads-up" style={{ position: 'absolute', left: 8, right: 8, top: 8, zIndex: 50 }}>
        <SystemNotification title="Equal AI Assistant is off" body={o.body} />
      </div>
    ) : (
      <SheetLayer onClose={onSheetClose}>
        <AlertSheet {...alertSheets[o.sheet]} onCta={onSheetCta} onSecondary={onSheetSecondary} onClose={onSheetClose}>
          {o.sheet === 'activation-failed' ? <TroubleshootList items={activationFixes} /> : undefined}
        </AlertSheet>
      </SheetLayer>
    );
  return <HomeScreen promo="meet-assistant" assistantArt="phone" assistantControl={{ ...state.toggle, onChange: onToggle }} overlay={overlay} />;
}

/**
 * Live run: tap the switch → pick a duration (OK stays disabled until you do) → Deactivating →
 * Off with the heads-up → tap the switch → Activating → On. `fail` makes the next request fail
 * with that sheet once; Try again / Try now retries and succeeds.
 */
export function SnoozeFlow({ state: initial = 'on', fail }: { state?: keyof typeof snoozeStates; fail?: AlertSheetKind }) {
  const [s, setS] = useState<SnoozeState>(snoozeStates[initial]);
  const [until, setUntil] = useState<ReturnType<typeof snoozeUntil>>(snoozeUntil('15m'));
  const failed = useRef(false);
  const timers = useRef<number[]>([]);
  const later = (ms: number, f: () => void) => { timers.current.push(window.setTimeout(f, ms)); };
  useEffect(() => { setS(snoozeStates[initial]); failed.current = false; return () => timers.current.forEach(clearTimeout); }, [initial]);
  const fails = (kind: 'off' | 'on') => !!fail && !failed.current && (kind === 'on' ? fail === 'activation-failed' : fail !== 'activation-failed' && fail !== 'update-available');
  const deactivate = (u: ReturnType<typeof snoozeUntil>) => {
    setS({ toggle: { on: true, busy: 'deactivating' } });
    later(1500, () => {
      if (fails('off')) { failed.current = true; setS({ toggle: { on: true }, overlay: { kind: 'sheet', sheet: fail! } }); return; }
      setS({ toggle: { on: false, offDescription: u.toggle }, overlay: { kind: 'notification', body: u.notification } });
      later(3500, () => setS((v) => (v.overlay?.kind === 'notification' ? { toggle: v.toggle } : v)));
    });
  };
  const activate = () => {
    setS((v) => ({ toggle: { ...v.toggle, busy: 'activating' } }));
    later(1500, () => {
      if (fails('on')) { failed.current = true; setS((v) => ({ toggle: { ...v.toggle, busy: undefined }, overlay: { kind: 'sheet', sheet: 'activation-failed' } })); return; }
      setS({ toggle: { on: true } });
    });
  };
  const dialogValue = s.overlay?.kind === 'dialog' ? s.overlay.value : null;
  const sheet = s.overlay?.kind === 'sheet' ? s.overlay.sheet : undefined;
  const retry = () => (sheet === 'activation-failed' ? activate() : deactivate(until));
  return (
    <SnoozeScreen
      state={s}
      onToggle={(on) => {
        if (s.toggle.busy) return;
        if (!on) setS({ toggle: s.toggle, overlay: { kind: 'dialog', value: null } });
        else activate();
      }}
      onSelect={(v) => setS({ toggle: s.toggle, overlay: { kind: 'dialog', value: v } })}
      onConfirm={() => { if (dialogValue) { const u = snoozeUntil(dialogValue); setUntil(u); deactivate(u); } }}
      onCancel={() => setS({ toggle: s.toggle })}
      onSheetCta={() => (sheet === 'airplane' || sheet === 'offline' ? setS({ toggle: s.toggle }) : retry())}
      onSheetSecondary={() => (sheet === 'activation-failed' ? setS({ toggle: s.toggle }) : retry())}
      onSheetClose={() => setS({ toggle: s.toggle })}
    />
  );
}

/** Force update: `hard` blocks the app; `soft` is the dismissible sheet over Chats. */
export function ForceUpdate({ kind, onUpdate, onLater }: { kind: 'hard' | 'soft'; onUpdate?: () => void; onLater?: () => void }) {
  if (kind === 'hard') return <ForceUpdateScreen onUpdate={onUpdate} />;
  return <SoftUpdate onUpdate={onUpdate} onLater={onLater} />;
}

function SoftUpdate({ onUpdate, onLater }: { onUpdate?: () => void; onLater?: () => void }) {
  const [open, setOpen] = useState(true);
  const close = () => { setOpen(false); onLater?.(); };
  return (
    <HomeScreen
      promo="meet-assistant"
      overlay={open ? (
        <SheetLayer onClose={close}>
          <AlertSheet {...alertSheets['update-available']} onCta={onUpdate} onSecondary={close} onClose={close} />
        </SheetLayer>
      ) : undefined}
    />
  );
}
