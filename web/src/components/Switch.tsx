import './Switch.css';

export type SwitchProps = {
  checked: boolean;
  onChange?: (checked: boolean) => void;
  disabled?: boolean;
  label: string;
  /** id of the text that describes the setting (aria-describedby). */
  describedBy?: string;
};

/** Toggle — 40 × 24 track, 18 thumb. Controlled. */
export function Switch({ checked, onChange, disabled, label, describedBy }: SwitchProps) {
  return (
    <button
      type="button"
      role="switch"
      aria-checked={checked}
      aria-label={label}
      aria-describedby={describedBy}
      disabled={disabled}
      onClick={() => onChange?.(!checked)}
      className={`eq-reset-btn eq-touch eq-switch ${checked ? 'is-on' : ''}`}
    >
      <span className="eq-switch__thumb" />
    </button>
  );
}
