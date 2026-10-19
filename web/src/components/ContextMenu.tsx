import { Fragment, useEffect, useRef } from 'react';
import { Text } from '../foundations/Text';
import { menuKeyDown } from '../foundations/a11y';
import { Icon } from '../foundations/Icon';
import type { IconName } from '../foundations/icons';

export type MenuItem = { label: string; icon?: IconName; danger?: boolean; onSelect?: () => void };

/** Long-press context menu (Component ref #18). */
export function ContextMenu({ items, minWidth = 131, autoFocus = false, onClose, label }: { items: MenuItem[]; minWidth?: number; /** Focus the first item on mount (a menu opened from a trigger). */ autoFocus?: boolean; /** Escape and Tab call it. */ onClose?: () => void; /** Accessible name of the menu. */ label?: string }) {
  const ref = useRef<HTMLDivElement>(null);
  useEffect(() => {
    if (autoFocus) ref.current?.querySelector<HTMLElement>('[role="menuitem"]')?.focus({ preventScroll: true });
  }, [autoFocus]);
  return (
    <div ref={ref} role="menu" aria-label={label} onKeyDown={(e) => menuKeyDown(e, onClose)} style={{ minWidth, display: 'inline-flex', flexDirection: 'column', background: 'var(--eq-bg-surface)', borderRadius: 12, boxShadow: 'var(--eq-shadow-card)', overflow: 'hidden' }}>
      {items.map((it, i) => (
        <Fragment key={it.label}>
          {i > 0 && <hr className="eq-divider" />}
          <button
            role="menuitem"
            type="button"
            tabIndex={i === 0 ? 0 : -1}
            className="eq-reset-btn"
            onClick={it.onSelect}
            style={{ height: 40, padding: '0 16px 0 12px', display: 'flex', alignItems: 'center', gap: 10, color: it.danger ? 'var(--eq-text-danger)' : 'var(--eq-text-primary)', textAlign: 'left' }}
          >
            {it.icon && <Icon name={it.icon} size={16} color={it.danger ? 'var(--eq-text-danger)' : 'var(--eq-text-secondary)'} />}
            <Text variant="body1" color="none">{it.label}</Text>
          </button>
        </Fragment>
      ))}
    </div>
  );
}
