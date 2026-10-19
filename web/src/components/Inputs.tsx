import { useId, type InputHTMLAttributes, type ReactNode, type TextareaHTMLAttributes } from 'react';
import { radioGroupKeyDown, rovingTabIndex } from '../foundations/a11y';
import { Icon } from '../foundations/Icon';
import { Text } from '../foundations/Text';
import './Inputs.css';

export type TextFieldProps = Omit<InputHTMLAttributes<HTMLInputElement>, 'onChange' | 'value' | 'size'> & {
  /** Overline label. Omit for fields in a group that shares one label. */
  label?: string;
  value: string;
  onChange?: (v: string) => void;
  optional?: boolean;
  /** Marks the label with * (e.g. LABEL*). */
  required?: boolean;
  helper?: ReactNode;
  error?: string;
  multiline?: boolean;
  align?: 'start' | 'center';
};

/** Form field — overline label, bodyLarge value (Edit address). */
export function TextField({ label, value, onChange, optional, required, helper, error, multiline, align = 'start', className, ...rest }: TextFieldProps) {
  const inputCls = ['eq-field__input eq-t-body-large', className].filter(Boolean).join(' ');
  const id = useId();
  const msgId = `${id}-msg`;
  // Errors and helper text are linked to the field; an error also marks it invalid.
  const a11y = {
    'aria-invalid': error ? true : undefined,
    'aria-describedby': [rest['aria-describedby'], error || helper ? msgId : undefined].filter(Boolean).join(' ') || undefined,
    'aria-required': required || undefined,
    'aria-label': label ?? rest['aria-label'],
  } as const;
  return (
    <div className={`eq-field eq-field--${align} ${error ? 'has-error' : ''}`}>
      {label && (
        <label htmlFor={id} style={{ display: 'flex', gap: 6, alignItems: 'baseline', marginBottom: 8 }}>
          <Text variant="overline" color="secondary">{label.toUpperCase() + (required ? '*' : '')}</Text>
          {optional && <Text variant="caption" color="secondary" style={{ fontWeight: 400 }}>(Optional)</Text>}
        </label>
      )}
      {multiline ? (
        <textarea {...(rest as TextareaHTMLAttributes<HTMLTextAreaElement>)} {...a11y} id={id} className={inputCls} value={value} rows={2} onChange={(e) => onChange?.(e.target.value)} />
      ) : (
        <input {...rest} {...a11y} id={id} className={inputCls} value={value} onChange={(e) => onChange?.(e.target.value)} />
      )}
      {(error || helper) && (
        <Text as="div" id={msgId} variant="captionWrap" color={error ? 'danger' : 'secondary'} style={{ marginTop: 6 }}>
          {error ?? helper}
        </Text>
      )}
    </div>
  );
}

export type SearchFieldProps = {
  value: string;
  onChange?: (v: string) => void;
  placeholder?: string;
  autoFocus?: boolean;
  /** Home: the field is an entry point; focusing it opens the search screen. */
  onActivate?: () => void;
};

/** Search input — bodyLarge, pill. */
export function SearchField({ value, onChange, placeholder = 'Search for names', autoFocus, onActivate }: SearchFieldProps) {
  return (
    <label className="eq-search">
      <Icon name="search" size={20} color="var(--eq-text-secondary)" />
      <input className="eq-t-body-large" value={value} onChange={(e) => onChange?.(e.target.value)} placeholder={placeholder} aria-label={placeholder} autoFocus={autoFocus} onFocus={onActivate ? (e) => (e.currentTarget.blur(), onActivate()) : undefined} readOnly={!!onActivate} />
      {value && (
        <button type="button" className="eq-reset-btn eq-touch" aria-label="Clear" onClick={() => onChange?.('')} style={{ color: 'var(--eq-text-secondary)' }}>
          <Icon name="close" size={18} />
        </button>
      )}
    </label>
  );
}

export type SegmentedChipsProps<T extends string> = {
  options: readonly T[];
  value: T;
  onChange?: (v: T) => void;
  /** Accessible name of the group (e.g. 'Address label'). */
  label?: string;
  /** id of a visible element that names the group (instead of `label`). */
  labelledBy?: string;
};

/** Segmented label chips (Home / Work / Other). button token. Arrow keys move the selection. */
export function SegmentedChips<T extends string>({ options, value, onChange, label, labelledBy }: SegmentedChipsProps<T>) {
  const checked = options.indexOf(value);
  return (
    <div role="radiogroup" aria-label={labelledBy ? undefined : label} aria-labelledby={labelledBy} onKeyDown={radioGroupKeyDown} style={{ display: 'flex', gap: 8, flexWrap: 'wrap' }}>
      {options.map((o, i) => (
        <button key={o} type="button" role="radio" aria-checked={o === value} tabIndex={rovingTabIndex(i, checked)} className={`eq-reset-btn eq-pressable eq-touch eq-seg ${o === value ? 'is-on' : ''}`} onClick={() => onChange?.(o)}>
          <Text variant="button" color="none">{o}</Text>
        </button>
      ))}
    </div>
  );
}
