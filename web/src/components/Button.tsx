import type { ButtonHTMLAttributes, ReactNode } from 'react';
import { Text } from '../foundations/Text';
import { Icon } from '../foundations/Icon';
import type { IconName } from '../foundations/icons';
import './Button.css';

/** Legacy single-name variants; each maps onto an appearance × emphasis pair (see `resolveButton`). */
export type ButtonVariant = 'primary' | 'secondary' | 'tonal' | 'danger' | 'gold' | 'outline' | 'gold-outline';
/** Figma Button "Appearance": brand green, red for destructive, gold for subscription surfaces. */
export type ButtonAppearance = 'default' | 'critical' | 'premium';
/** Figma Button "State": filled, gradient-with-edge, or text only. Disabled is the `disabled` attribute. */
export type ButtonEmphasis = 'primary' | 'secondary' | 'tertiary';
/** Figma Button "Size": XS 24 · S 32 · M 40 · L 48. */
export type ButtonSize = 'xs' | 'sm' | 'md' | 'lg';

export type ButtonProps = Omit<ButtonHTMLAttributes<HTMLButtonElement>, 'children'> & {
  children: ReactNode;
  appearance?: ButtonAppearance;
  emphasis?: ButtonEmphasis;
  /** Shorthand for an appearance × emphasis pair (older call sites). Ignored when `appearance` or `emphasis` is set. */
  variant?: ButtonVariant;
  size?: ButtonSize;
  /** Full width. */
  block?: boolean;
  leadingIcon?: IconName;
  trailingIcon?: IconName;
  /** 'full' pill (default) or 'rounded' 8dp — the typography specimens show rounded block buttons. */
  shape?: 'pill' | 'rounded';
  /** Premium primary only: the soft gold halo behind the button (Figma "Button Glow"). */
  glow?: boolean;
  /** Premium primary only: the sweeping sheen (Figma "Buton", Animated). On by default. */
  sheen?: boolean;
};

const LEGACY: Record<ButtonVariant, [ButtonAppearance, ButtonEmphasis] | 'secondary-lime' | 'tonal'> = {
  primary: ['default', 'primary'],
  danger: ['critical', 'primary'],
  gold: ['premium', 'primary'],
  outline: ['default', 'secondary'],
  'gold-outline': ['premium', 'secondary'],
  secondary: 'secondary-lime',
  tonal: 'tonal',
};

/** The CSS modifier for a button: `default-primary` … `premium-tertiary`, or the two system extras. */
export function resolveButton(appearance?: ButtonAppearance, emphasis?: ButtonEmphasis, variant: ButtonVariant = 'primary'): string {
  if (appearance || emphasis) return `${appearance ?? 'default'}-${emphasis ?? 'primary'}`;
  const v = LEGACY[variant];
  return Array.isArray(v) ? v.join('-') : v;
}

/**
 * The one Button (Figma "Button", with "Buton" and "Button Glow" folded in): appearance × emphasis ×
 * size. Labels are sentence case: xs / sm use `buttonSmall` (12 SemiBold), md / lg `buttonLarge`
 * (16 SemiBold). Premium primary is the gold subscription CTA with its sheen and optional glow.
 */
export function Button({ children, appearance, emphasis, variant = 'primary', size = 'md', block, leadingIcon, trailingIcon, shape = 'pill', glow, sheen = true, disabled, className, type = 'button', ...rest }: ButtonProps) {
  const kind = resolveButton(appearance, emphasis, variant);
  const small = size === 'xs' || size === 'sm';
  const iconSize = { xs: 14, sm: 16, md: 20, lg: 24 }[size];
  const premiumPrimary = kind === 'premium-primary';
  return (
    <button
      type={type}
      disabled={disabled}
      className={['eq-reset-btn eq-pressable eq-btn', `eq-btn--${kind}`, `eq-btn--${size}`, `eq-btn--${shape}`, block && 'eq-btn--block', premiumPrimary && sheen && 'has-sheen', premiumPrimary && glow && 'has-glow', 'eq-touch', className].filter(Boolean).join(' ')}
      {...rest}
    >
      {leadingIcon && <Icon name={leadingIcon} size={iconSize} />}
      <Text variant={small ? 'buttonSmall' : 'buttonLarge'} color="none" className={kind === 'premium-secondary' || kind === 'premium-tertiary' ? 'eq-btn__gold-label' : undefined}>{children}</Text>
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
