import type { ReactNode } from 'react';
import { Text } from '../foundations/Text';

const pill = {
  display: 'inline-flex',
  alignItems: 'center',
  height: 24,
  padding: '0 14px',
  borderRadius: 8,
  background: 'var(--eq-bg-surface)',
  border: '1px solid var(--eq-stroke-subtle)',
} as const;

/** Thread events hug their label: 6 / 12 padding (Personal Number Outbound Call). */
const eventPill = { ...pill, height: undefined, padding: '6px 12px' } as const;

/** Day divider in a thread ("Today", "12 July"). */
export function DayDivider({ label }: { label: string }) {
  return (
    <div className="eq-center-row" style={{ display: 'flex', justifyContent: 'center' }}>
      <span style={pill}>
        <Text variant="micro">{label}</Text>
      </span>
    </div>
  );
}

/** Thread event ("Call started 03:31", "Call ended 03:31"). */
export function ThreadEvent({ label }: { label: string }) {
  return (
    <div className="eq-center-row" style={{ display: 'flex', justifyContent: 'center' }}>
      <span style={eventPill}>
        <Text variant="micro" color="secondary">{label}</Text>
      </span>
    </div>
  );
}

/** Thread notice ("Call has ended. You are now talking to your AI assistant."). */
export function ThreadNotice({ children }: { children: ReactNode }) {
  return (
    <div className="eq-center-row" style={{ display: 'flex', justifyContent: 'center' }}>
      <div style={{ maxWidth: 240, padding: '8px 16px', borderRadius: 8, background: 'var(--eq-notice-bg)' }}>
        <Text as="p" variant="captionWrap" color="secondary" align="center">{children}</Text>
      </div>
    </div>
  );
}
