import { useId, type ReactNode } from 'react';
import { Icon } from '../foundations/Icon';
import type { IconName } from '../foundations/icons';
import { Text } from '../foundations/Text';
import { NewBadge } from './Badge';
import { Switch } from './Switch';

export type SettingsRowProps = {
  label: string;
  description?: ReactNode;
  /** Description wraps → captionWrap (Regular, 18 line height). Single line stays caption. */
  wrap?: boolean;
  icon?: IconName;
  /** Puts the icon in a 36dp tinted circle of this colour (e.g. `var(--eq-preset-tint-lime)` on Active Instructions). */
  iconBg?: string;
  isNew?: boolean;
  trailing?: 'chevron' | 'none' | ReactNode;
  onClick?: () => void;
};

/** Settings row — the densest vertical stack in the app. subheading + caption. */
export function SettingsRow({ label, description, wrap, icon, iconBg, isNew, trailing = 'chevron', onClick }: SettingsRowProps) {
  const Tag = onClick ? 'button' : 'div';
  return (
    <Tag
      type={onClick ? 'button' : undefined}
      onClick={onClick}
      className={onClick ? 'eq-reset-btn' : undefined}
      style={{ display: 'flex', alignItems: 'center', gap: 12, minHeight: 64, width: '100%', padding: '12px 12px 12px 16px', background: 'var(--eq-bg-surface)', textAlign: 'left' }}
    >
      {icon && (iconBg ? (
        <span style={{ width: 36, height: 36, borderRadius: '50%', background: iconBg, display: 'inline-flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0 }}>
          <Icon name={icon} size={20} color="var(--eq-text-primary)" />
        </span>
      ) : <Icon name={icon} size={24} color="var(--eq-text-primary)" />)}
      <div style={{ flex: 1, minWidth: 0 }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
          <Text variant="subheading" truncate>{label}</Text>
          {isNew && <NewBadge />}
        </div>
        {description && (
          <Text as="div" variant={wrap ? 'captionWrap' : 'caption'} color="secondary" truncate={!wrap} style={wrap ? undefined : { fontWeight: 400 }}>
            {description}
          </Text>
        )}
      </div>
      {trailing === 'chevron' ? <Icon name="chevron-right" size={22} color="var(--eq-text-placeholder)" /> : trailing === 'none' ? null : trailing}
    </Tag>
  );
}

export type ToggleRowProps = Omit<SettingsRowProps, 'trailing' | 'onClick'> & { checked: boolean; onChange?: (v: boolean) => void };

/** Settings row with a trailing switch. */
export function ToggleRow({ checked, onChange, ...row }: ToggleRowProps) {
  const descId = useId();
  // Tapping anywhere on the row toggles; the switch stays the one keyboard / screen-reader control,
  // described by the row's description.
  return (
    <div onClick={(e) => { if (!(e.target as Element).closest('[role="switch"]')) onChange?.(!checked); }} style={{ cursor: onChange ? 'pointer' : undefined }}>
      <SettingsRow
        {...row}
        description={row.description ? <span id={descId}>{row.description}</span> : undefined}
        trailing={<Switch checked={checked} onChange={onChange} label={row.label} describedBy={row.description ? descId : undefined} />}
      />
    </div>
  );
}

/** Groups rows into a rounded card with dividers. */
export function RowGroup({ children, inset = 20 }: { children: ReactNode; /** Side margin in dp (You tab: 20). */ inset?: number }) {
  return <div style={{ margin: `0 ${inset}px`, borderRadius: 16, overflow: 'hidden', background: 'var(--eq-bg-surface)', display: 'flex', flexDirection: 'column' }} className="eq-row-group">{children}</div>;
}
