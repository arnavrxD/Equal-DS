import { Icon } from '../foundations/Icon';
import { Text } from '../foundations/Text';
import type { ControlSize } from './Radio';
import './Radio.css';

export type CheckboxProps = {
  checked: boolean;
  onChange?: (checked: boolean) => void;
  label?: string;
  /** Figma Checkbox "Size": 's' 16 · 'm' 20 · 'l' 24. A number keeps the older glyph size API. */
  size?: ControlSize | number;
  /** Token used for the label (older API; the Figma sizes pick their own). */
  labelVariant?: 'bodyLarge' | 'body1';
  /** Support text under the label. */
  support?: string;
  /** Accessible name when there is no visible label (e.g. a checkbox at the end of a contact row). */
  ariaLabel?: string;
};

const PX = { s: 16, m: 20, l: 24 } as const;
const LABEL = { s: 'caption', m: 'body2', l: 'subheading' } as const;

/**
 * Checkbox (Figma "Checkbox"): a 16 / 20 / 24 box, radius 4, 1.5px text/secondary edge; checked
 * fills brand green with a white check and the label turns text/action.
 */
export function Checkbox({ checked, onChange, label, size = 'l', labelVariant, support, ariaLabel }: CheckboxProps) {
  const key: ControlSize = typeof size === 'number' ? (size <= 16 ? 's' : size <= 20 ? 'm' : 'l') : size;
  const px = PX[key];
  return (
    <button
      type="button"
      role="checkbox"
      aria-checked={checked}
      aria-label={ariaLabel}
      onClick={() => onChange?.(!checked)}
      className={`eq-reset-btn eq-touch eq-radio eq-checkbox ${checked ? 'is-on' : ''}`}
      style={{ ['--eq-radio-size' as string]: `${px}px` }}
    >
      <span className="eq-checkbox__box" aria-hidden>{checked && <Icon name="check" size={Math.round(px * 0.7)} color="var(--eq-text-on-action)" />}</span>
      {(label || support) && (
        <span className="eq-radio__text">
          {label && <Text variant={labelVariant ?? LABEL[key]} color={checked ? 'action' : 'primary'}>{label}</Text>}
          {support && <Text as="span" variant="captionWrap" color="secondary">{support}</Text>}
        </span>
      )}
    </button>
  );
}
