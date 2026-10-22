import './Spinner.css';

export type SpinnerProps = {
  /** Diameter (default 20). */
  size?: number;
  /** Accessible name; omit when the surrounding text already says what is loading. */
  label?: string;
};

/**
 * Circular progress (Assistant Snooze design): a 2dp `stroke/medium` track with a quarter
 * `button/primary` arc (lime in dark), one turn per 900 ms. Still under reduced motion.
 */
export function Spinner({ size = 20, label }: SpinnerProps) {
  return (
    <span className="eq-spinner" role={label ? 'progressbar' : undefined} aria-label={label} aria-hidden={label ? undefined : true} style={{ width: size, height: size }}>
      <svg viewBox="0 0 20 20" width={size} height={size}>
        <circle cx="10" cy="10" r="9" fill="none" stroke="var(--eq-stroke-medium)" strokeWidth="2" />
        <circle cx="10" cy="10" r="9" fill="none" stroke="var(--eq-button-primary)" strokeWidth="2" strokeLinecap="round" strokeDasharray="14.1 56.5" />
      </svg>
    </span>
  );
}
