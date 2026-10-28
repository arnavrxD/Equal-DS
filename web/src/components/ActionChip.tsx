import type { ButtonHTMLAttributes, ReactNode } from 'react';
import { Text } from '../foundations/Text';
import { Emoji, Icon } from '../foundations/Icon';
import { Asset } from '../foundations/Asset';
import './ActionChip.css';

export type SmallActionChipProps = Omit<ButtonHTMLAttributes<HTMLButtonElement>, 'children'> & {
  label: string;
  /** Unicode emoji — actions use emoji (📞 Call back, ✏️ Update address, 📅 Add to Calendar). */
  emoji?: string;
  /** unread = glass green; read = glass grey; outlined = selected-row state. */
  state?: 'unread' | 'read' | 'outlined';
  chevron?: boolean;
  /** Free plan (Actions touchpoint): gold chip, crown-lock instead of the emoji; tapping opens the paywall. */
  locked?: boolean;
};

/** Small action chip — outside chats, in the chat list (Component refs #6, #9). buttonSmall token. */
export function SmallActionChip({ label, emoji, state = 'unread', chevron = true, locked, className, type = 'button', ...rest }: SmallActionChipProps) {
  return (
    <button type={type} className={['eq-reset-btn eq-pressable eq-touch eq-chip-sm', `eq-chip-sm--${state}`, locked && 'eq-chip-sm--locked', className].filter(Boolean).join(' ')} {...rest}>
      {locked ? <Asset name="crown-lock" size={14} alt="Locked" /> : emoji && <Emoji char={emoji} size={12} />}
      <Text variant="buttonSmall" color="none">{label}</Text>
      {chevron && <Icon name="chevron-right" size={14} />}
    </button>
  );
}

export type LargeActionChipProps = Omit<ButtonHTMLAttributes<HTMLButtonElement>, 'children'> & {
  label: string;
  /** Unicode emoji. */
  emoji?: string;
  /** unread = green glass (the suggested action); read = grey glass (the alternative, e.g. Edit). */
  state?: 'unread' | 'read';
};

/** Large post-call action chip — inside chats (Component ref #10). button token. */
export function LargeActionChip({ label, emoji, state = 'unread', className, type = 'button', ...rest }: LargeActionChipProps) {
  return (
    <button type={type} className={['eq-reset-btn eq-pressable eq-touch eq-chip-lg', `eq-chip-lg--${state}`, className].filter(Boolean).join(' ')} {...rest}>
      {emoji && <Emoji char={emoji} size={14} />}
      <Text variant="button" color="none">{label}</Text>
    </button>
  );
}

/** Horizontally scrolling chip row that clips at the right edge. */
export function ChipRow({ children, gap = 12, wrap = false }: { children: ReactNode; gap?: number; wrap?: boolean }) {
  return (
    <div className={wrap ? undefined : 'eq-hscroll'} style={{ display: 'flex', gap, flexWrap: wrap ? 'wrap' : 'nowrap', padding: '2px 0 4px' }}>
      {children}
    </div>
  );
}
