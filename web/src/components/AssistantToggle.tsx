import { Text } from '../foundations/Text';
import { Switch } from './Switch';
import { Mascot } from './Avatar';
import { Asset } from '../foundations/Asset';

export type AssistantToggleProps = {
  on: boolean;
  onChange?: (on: boolean) => void;
  title?: string;
  description?: string;
  /** Art experiment (Component 25): the robot mascot (default, final) or the 3D phone with sparkles. */
  art?: 'robot' | 'phone';
};

const GLOW = 'color-mix(in srgb, var(--eq-action-stroke-unread) 40%, transparent)';
const GLOWS = `radial-gradient(50% 160% at 0% 50%, ${GLOW}, transparent), radial-gradient(50% 160% at 100% 50%, ${GLOW}, transparent), var(--eq-assistant-toggle-bg)`;

/** Assistant availability card (Component ref #3). */
export function AssistantToggle({ on, onChange, title, description = 'Equal is managing your unknown calls!', art = 'robot' }: AssistantToggleProps) {
  return (
    <div
      style={{
        display: 'flex',
        alignItems: 'center',
        gap: 10,
        minHeight: 64,
        padding: '12px 10px 12px 12px',
        borderRadius: 20,
        // On: Figma's flat assistant-toggle/bg fill with a soft action/stroke/unread glow at each end.
        background: on ? GLOWS : 'var(--eq-bg-surface)',
        border: on ? '1px solid transparent' : '1px solid var(--eq-stroke-subtle)',
        transition: 'background var(--eq-duration-base)',
      }}
    >
      {art === 'phone' ? <Asset name="assistant-phone" size={36} alt="" style={{ opacity: on ? 1 : 0.5 }} /> : <Mascot size={36} dimmed={!on} />}
      <div style={{ flex: 1, minWidth: 0 }}>
        <Text as="div" variant="subheading">{title ?? (on ? 'AI Assistant is On' : 'AI Assistant is Off')}</Text>
        <Text as="div" variant="caption" color="secondary" truncate style={{ fontWeight: 400 }}>
          {on ? description : 'Unknown calls will ring normally'}
        </Text>
      </div>
      <Switch checked={on} onChange={onChange} label="AI Assistant" />
    </div>
  );
}
