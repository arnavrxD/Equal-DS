import { Icon } from '../foundations/Icon';
import { Text } from '../foundations/Text';

export type StatusBarProps = { time?: string; background?: string; /** Text and glyph colour (default text/primary). */ color?: string };

/** Material 3 status-bar height on the 390 × 800 frame. */
export const STATUS_BAR_HEIGHT = 52;

/**
 * Android status bar, Material 3 (mocks and flows only; real apps draw edge-to-edge under the
 * system bar via WindowInsets). 52dp high, 24dp side padding. Time: the system font (Roboto)
 * Medium 14/20, tracking 0.1. Right: signal_cellular_4_bar (filled triangle), wifi and an upright
 * battery_full, 18dp, gap 4. Colour `text/primary`.
 */
export function StatusBar({ time = '12:30', background = 'var(--eq-bg-primary)', color = 'var(--eq-text-primary)' }: StatusBarProps) {
  return (
    // A mock of the system bar: hidden from screen readers (the time is fake).
    <div aria-hidden style={{ width: '100%', boxSizing: 'border-box', height: STATUS_BAR_HEIGHT, padding: '0 24px', display: 'flex', alignItems: 'center', justifyContent: 'space-between', background, color, flexShrink: 0 }}>
      <Text variant="inherit" color="none" className="eq-tnum" style={{ fontSize: 14, lineHeight: '20px', fontWeight: 500, letterSpacing: '0.1px' }}>{time}</Text>
      <span style={{ display: 'flex', alignItems: 'center', gap: 4 }}>
        <Icon name="wifi" size={18} />
        <Icon name="signal" size={18} />
        <Icon name="battery" size={18} />
      </span>
    </div>
  );
}
