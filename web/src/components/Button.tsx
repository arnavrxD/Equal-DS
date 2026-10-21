import type { ButtonHTMLAttributes, ReactNode } from 'react';
import { Text } from '../foundations/Text';
import { Icon } from '../foundations/Icon';
import type { IconName } from '../foundations/icons';
import './Button.css';

export type ButtonVariant = 'primary' | 'secondary' | 'tonal' | 'danger' | 'gold' | 'outline' | 'gold-outline';
export type ButtonSize = 'sm' | 'md' | 'lg';

export type ButtonProps = Omit<ButtonHTMLAttributes<HTMLButtonElement>, 'children'> & {
  children: ReactNode;
  variant?: ButtonVariant;
  size?: ButtonSize;
  /** Full width. */
  block?: boolean;
  leadingIcon?: IconName;
  trailingIcon?: IconName;
  /** 'full' pill (default) or 'rounded' 8dp — the typography specimens show rounded block buttons. */
  shape?: 'pill' | 'rounded';
};

/**
 * Filled button — the one commit action in a surface (Typography rule 4).
 * Sentence case, `button` token (sm uses `buttonSmall`).
 */
export function Button({ children, variant = 'primary', size = 'md', block, leadingIcon, trailingIcon, shape = 'pill', disabled, className, type = 'button', ...rest }: ButtonProps) {
  const iconSize = size === 'sm' ? 14 : 18;
  return (
    <button
      type={type}
      disabled={disabled}
      className={['eq-reset-btn eq-pressable eq-btn', `eq-btn--${variant}`, `eq-btn--${size}`, `eq-btn--${shape}`, block && 'eq-btn--block', 'eq-touch', className].filter(Boolean).join(' ')}
      {...rest}
    >
      {leadingIcon && <Icon name={leadingIcon} size={iconSize} />}
      <Text variant={size === 'sm' ? 'buttonSmall' : 'button'} color="none">{children}</Text>
      {trailingIcon && <Icon name={trailingIcon} size={iconSize} />}
    </button>
  );
}

export type TextButtonProps = Omit<ButtonHTMLAttributes<HTMLButtonElement>, 'children'> & {
  children: ReactNode;
  /** 'button' 14sp or 'small' 12sp (dense containers). */
  size?: 'button' | 'small';
  tone?: 'action' | 'danger' | 'neutral';
  leadingIcon?: IconName;
  trailingIcon?: IconName;
};

/** Text action — the revise / secondary action. Green = actionable. */
export function TextButton({ children, size = 'button', tone = 'action', leadingIcon, trailingIcon, className, type = 'button', ...rest }: TextButtonProps) {
  const iconSize = size === 'small' ? 14 : 18;
  return (
    <button type={type} className={['eq-reset-btn eq-touch eq-text-btn', `eq-text-btn--${tone}`, className].filter(Boolean).join(' ')} {...rest}>
      {leadingIcon && <Icon name={leadingIcon} size={iconSize} />}
      <Text variant={size === 'small' ? 'buttonSmall' : 'button'} color="none">{children}</Text>
      {trailingIcon && <Icon name={trailingIcon} size={iconSize} />}
    </button>
  );
}

export type IconButtonProps = Omit<ButtonHTMLAttributes<HTMLButtonElement>, 'children'> & {
  icon: IconName;
  /** Accessible label — required. */
  label: string;
  /** neutral = white raised circle (close); primary = green circle (confirm); glass = frosted circle over content; ghost = bare 48dp target. */
  variant?: 'neutral' | 'primary' | 'glass' | 'ghost' | 'danger-ghost';
  size?: number;
  iconSize?: number;
};

export function IconButton({ icon, label, variant = 'ghost', size, iconSize, className, type = 'button', style, ...rest }: IconButtonProps) {
  const s = size ?? (variant === 'ghost' || variant === 'danger-ghost' ? 40 : variant === 'primary' ? 42 : 44);
  return (
    <button
      type={type}
      aria-label={label}
      title={label}
      className={['eq-reset-btn eq-pressable eq-touch eq-icon-btn', `eq-icon-btn--${variant}`, className].filter(Boolean).join(' ')}
      style={{ width: s, height: s, ...style }}
      {...rest}
    >
      <Icon name={icon} size={iconSize ?? (variant === 'ghost' || variant === 'danger-ghost' ? 24 : 22)} />
    </button>
  );
}
