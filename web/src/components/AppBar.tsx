import type { ReactNode } from 'react';
import { Text } from '../foundations/Text';
import { IconButton } from './Button';
import { StatusBar } from './StatusBar';

export type AppBarProps = {
  title: string;
  onBack?: () => void;
  trailing?: ReactNode;
  statusBar?: boolean;
  /** Surface behind the bar. */
  surface?: 'primary' | 'surface' | 'transparent';
  elevated?: boolean;
  /** Title alignment. The flows use a centred title. */
  align?: 'center' | 'start';
};

/** Standard app bar — back + subheading title + trailing actions. */
export function AppBar({ title, onBack, trailing, statusBar = false, surface = 'primary', elevated, align = 'center' }: AppBarProps) {
  const bg = surface === 'transparent' ? 'transparent' : surface === 'primary' ? 'var(--eq-bg-primary)' : 'var(--eq-bg-surface)';
  return (
    <header style={{ width: '100%', background: bg, boxShadow: elevated ? 'var(--eq-shadow-header)' : undefined, position: 'relative', zIndex: 2, flexShrink: 0 }}>
      {statusBar && <StatusBar background={bg} />}
      {align === 'center' ? (
        <div style={{ height: 56, display: 'grid', gridTemplateColumns: '1fr auto 1fr', alignItems: 'center', padding: '0 8px 0 4px' }}>
          <span>{onBack && <IconButton icon="arrow-back" label="Back" onClick={onBack} />}</span>
          <Text as="h1" variant="subheading" truncate align="center">{title}</Text>
          <span style={{ display: 'flex', justifyContent: 'flex-end' }}>{trailing}</span>
        </div>
      ) : (
        <div style={{ height: 64, display: 'flex', alignItems: 'center', gap: 8, padding: onBack ? '0 8px 0 4px' : '0 8px 0 16px' }}>
          {onBack && <IconButton icon="arrow-back" label="Back" onClick={onBack} />}
          <Text as="h1" variant="subheading" truncate style={{ flex: 1 }}>{title}</Text>
          {trailing}
        </div>
      )}
    </header>
  );
}

/** Uppercase region header ("Your assistant", "Settings & support"). Overline token. */
export function SectionHeader({ children, action, qualifier, inset = 20, size = 'overline', as = 'h2' }: { children: string; action?: ReactNode; qualifier?: string; /** Side padding in dp. */ inset?: number; /** `label`: 13sp medium `text/secondary` (Contact Info). */ size?: 'overline' | 'label'; /** Heading level for screen-reader heading navigation (looks the same); 'span' for none. */ as?: 'h2' | 'h3' | 'h4' | 'span' }) {
  return (
    <div style={{ display: 'flex', alignItems: 'center', gap: 6, padding: size === 'label' ? `30px ${inset}px 18px` : `24px ${inset}px 12px` }}>
      {size === 'label' ? (
        <Text as={as} aria-label={children} variant="caption" color="secondary" style={{ fontSize: 13, lineHeight: '18px', letterSpacing: '0.01em' }}>{children.toUpperCase()}</Text>
      ) : (
        <Text as={as} aria-label={children} variant="overline" color="secondary">{children.toUpperCase()}</Text>
      )}
      {qualifier && <Text variant="caption" color="secondary" style={{ fontWeight: 400 }}>{qualifier}</Text>}
      <span style={{ flex: 1 }} />
      {action}
    </div>
  );
}
