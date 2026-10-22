import { Text } from '../foundations/Text';
import { Switch } from './Switch';
import { Mascot } from './Avatar';
import { Asset } from '../foundations/Asset';
import { Icon } from '../foundations/Icon';
import { Spinner } from './Spinner';
import type { ReactNode } from 'react';

export type AssistantToggleProps = {
  on: boolean;
  onChange?: (on: boolean) => void;
  title?: string;
  description?: string;
  /** Art experiment (Component 25): the robot mascot (default, final) or the 3D phone with sparkles. */
  art?: 'robot' | 'phone';
  /** Line under the title while off (default "Unknown calls will ring normally"); snooze: `<>Equal will be back <b>Today 6:45pm</b></>`. */
  offDescription?: ReactNode;
  /** Assistant Snooze: the switch is replaced by a spinner while the request runs (not interactive). */
  busy?: 'deactivating' | 'activating';
  /** Assistant Healthcheck design: the pink strip under the card, "Poor Assistant Health · Fix Now →". */
  healthAlert?: { label?: string; cta?: string; onFix?: () => void };
};

const GLOW = 'color-mix(in srgb, var(--eq-action-stroke-unread) 40%, transparent)';
const GLOWS = `radial-gradient(50% 160% at 0% 50%, ${GLOW}, transparent), radial-gradient(50% 160% at 100% 50%, ${GLOW}, transparent), var(--eq-assistant-toggle-bg)`;

/** Assistant availability card (Component ref #3). */
export function AssistantToggle({ on, onChange, title, description = 'Equal is managing your unknown calls!', art = 'robot', offDescription = 'Unknown calls will ring normally', healthAlert, busy }: AssistantToggleProps) {
  // Deactivating keeps the on look; activating keeps the off look until the request succeeds.
  const lit = busy ? busy === 'deactivating' : on;
  const busyCopy = busy === 'deactivating' ? ['Deactivating your assistant', 'This will take just a moment…'] : busy === 'activating' ? ['Activating your assistant', 'Resuming on your Equal AI Assistant'] : null;
  const card = (
    <div
      aria-busy={busy ? true : undefined}
      style={{
        display: 'flex',
        alignItems: 'center',
        gap: 10,
        minHeight: 64,
        padding: '12px 10px 12px 12px',
        borderRadius: 20,
        // On: Figma's flat assistant-toggle/bg fill with a soft action/stroke/unread glow at each end.
        background: lit ? GLOWS : 'var(--eq-bg-surface)',
        border: lit ? '1px solid transparent' : '1px solid var(--eq-stroke-subtle)',
        transition: 'background var(--eq-duration-base)',
      }}
    >
      {art === 'phone' ? <Asset name="assistant-phone" size={36} alt="" style={{ opacity: lit ? 1 : 0.5 }} /> : <Mascot size={36} dimmed={!lit} />}
      <div style={{ flex: 1, minWidth: 0 }} aria-live="polite">
        <Text as="div" variant="subheading">{busyCopy ? busyCopy[0] : title ?? (on ? 'AI Assistant is On' : 'AI Assistant is Off')}</Text>
        <Text as="div" variant="caption" color="secondary" truncate style={{ fontWeight: 400 }}>
          {busyCopy ? busyCopy[1] : on ? description : offDescription}
        </Text>
      </div>
      {busy ? <span style={{ width: 40, height: 24, display: 'inline-flex', alignItems: 'center', justifyContent: 'center' }}><Spinner size={20} /></span> : <Switch checked={on} onChange={onChange} label="AI Assistant" />}
    </div>
  );
  if (!healthAlert) return card;
  return (
    <div style={{ borderRadius: 20, background: 'var(--eq-expiry-banner-danger-bg)', overflow: 'hidden' }}>
      {card}
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: 8, minHeight: 44, padding: '4px 8px 4px 16px' }}>
        <Text variant="body2">{healthAlert.label ?? 'Poor Assistant Health'}</Text>
        <button type="button" className="eq-reset-btn eq-touch" onClick={healthAlert.onFix} style={{ display: 'inline-flex', alignItems: 'center', gap: 4, padding: '8px', color: 'var(--eq-text-danger)' }}>
          <Text variant="buttonSmall" color="none">{healthAlert.cta ?? 'Fix Now'}</Text>
          <Icon name="arrow-forward" size={16} />
        </button>
      </div>
    </div>
  );
}
