import { useRef, type KeyboardEvent, type ReactNode, type Ref } from 'react';
import { Text } from '../foundations/Text';
import './Radio.css';

/** Figma Radio Button / Checkbox "Size": S 16 · M 20 · L 24 (label 12 / 14 / 16 Medium). */
export type ControlSize = 's' | 'm' | 'l';
const LABEL = { s: 'caption', m: 'body2', l: 'subheading' } as const;

export type RadioProps = {
  checked: boolean;
  onChange?: () => void;
  label?: ReactNode;
  /** Support text under the label (12, text/secondary). */
  support?: ReactNode;
  size?: ControlSize;
  disabled?: boolean;
  /** Accessible name when there is no visible label. */
  ariaLabel?: string;
  /** Roving tab index, set by RadioGroup. */
  tabIndex?: number;
  onKeyDown?: (e: KeyboardEvent<HTMLButtonElement>) => void;
  buttonRef?: Ref<HTMLButtonElement>;
};

/**
 * Radio (Figma "Radio Button"): a 16 / 20 / 24 ring (2px text/secondary) that fills brand green with
 * a white inner ring when selected; the label turns text/action. Use inside RadioGroup so arrow keys
 * move the selection.
 */
export function Radio({ checked, onChange, label, support, size = 'm', disabled, ariaLabel, tabIndex, onKeyDown, buttonRef }: RadioProps) {
  return (
    <button
      ref={buttonRef}
      type="button"
      role="radio"
      aria-checked={checked}
      aria-label={ariaLabel}
      disabled={disabled}
      tabIndex={tabIndex}
      onKeyDown={onKeyDown}
      onClick={() => onChange?.()}
      className={`eq-reset-btn eq-touch eq-radio eq-radio--${size} ${checked ? 'is-on' : ''}`}
    >
      <span className="eq-radio__control" aria-hidden />
      {(label || support) && (
        <span className="eq-radio__text">
          {label && <Text variant={LABEL[size]} color={checked ? 'action' : 'primary'}>{label}</Text>}
          {support && <Text as="span" variant="captionWrap" color="secondary">{support}</Text>}
        </span>
      )}
    </button>
  );
}

export type RadioOption<T extends string> = { value: T; label: ReactNode; support?: ReactNode; disabled?: boolean };

/** A labelled group of Radios with roving focus: Tab enters the group, arrow keys move and select. */
export function RadioGroup<T extends string>({ label, options, value, onChange, size = 'm', gap = 16 }: { label: string; options: RadioOption<T>[]; value: T | null; onChange: (v: T) => void; size?: ControlSize; gap?: number }) {
  const refs = useRef<(HTMLButtonElement | null)[]>([]);
  const enabled = options.map((o, i) => (o.disabled ? -1 : i)).filter((i) => i >= 0);
  const current = Math.max(0, options.findIndex((o) => o.value === value));
  const move = (from: number, dir: 1 | -1) => {
    const pos = enabled.indexOf(from);
    const next = enabled[(pos + dir + enabled.length) % enabled.length];
    onChange(options[next].value);
    refs.current[next]?.focus();
  };
  return (
    <div role="radiogroup" aria-label={label} className="eq-radio-group" style={{ gap }}>
      {options.map((o, i) => (
          <Radio
            key={o.value}
            buttonRef={(el) => { refs.current[i] = el; }}
            checked={o.value === value}
            onChange={() => onChange(o.value)}
            label={o.label}
            support={o.support}
            size={size}
            disabled={o.disabled}
            tabIndex={i === current ? 0 : -1}
            onKeyDown={(e) => {
              if (e.key === 'ArrowDown' || e.key === 'ArrowRight') { e.preventDefault(); move(i, 1); }
              if (e.key === 'ArrowUp' || e.key === 'ArrowLeft') { e.preventDefault(); move(i, -1); }
            }}
          />
      ))}
    </div>
  );
}
