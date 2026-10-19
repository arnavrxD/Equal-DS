import type { CSSProperties } from 'react';
import { icons, type IconName } from './icons';

export type IconProps = {
  /** DS icon name. Flat icons are Material Symbols (Outlined 400); see scripts/build-icons.mjs. */
  name: IconName;
  /** dp. Defaults to 24. */
  size?: number;
  /** Any CSS colour or token var. Defaults to currentColor. */
  color?: string;
  /** Degrees, e.g. -35 for the composer send plane. */
  rotate?: number;
  className?: string;
  style?: CSSProperties;
  /** Accessible label. Omit for decorative icons. */
  label?: string;
};

export function Icon({ name, size = 24, color = 'currentColor', rotate, className, style, label }: IconProps) {
  const def = icons[name];
  return (
    <svg
      viewBox={def.viewBox}
      width={size}
      height={size}
      fill={color}
      className={className}
      style={{ flexShrink: 0, display: 'block', transform: rotate ? `rotate(${rotate}deg)` : undefined, ...style }}
      role={label ? 'img' : undefined}
      aria-label={label}
      aria-hidden={label ? undefined : true}
    >
      {def.paths.map((d, i) => (
        <path key={i} d={d} />
      ))}
    </svg>
  );
}

/**
 * Unicode emoji — actions use emoji, not images (Call back 📞, Update address ✏️, Add to calendar 🗓️).
 * Sized in px (dp) so it doesn't scale with type settings; uses the platform emoji font.
 */
export function Emoji({ char, size = 16, label, inline, className, style }: { char: string; size?: number; label?: string; /** Size it from the surrounding font, like a plain character in the text (ignores `size`). */ inline?: boolean; className?: string; style?: CSSProperties }) {
  if (inline) {
    return (
      <span className={className} role={label ? 'img' : undefined} aria-label={label} aria-hidden={label ? undefined : true} style={style}>
        {char}
      </span>
    );
  }
  return (
    <span
      className={className}
      role={label ? 'img' : undefined}
      aria-label={label}
      aria-hidden={label ? undefined : true}
      style={{ fontSize: size, lineHeight: `${size}px`, width: size, height: size, display: 'inline-flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0, fontFamily: '"Apple Color Emoji","Noto Color Emoji","Segoe UI Emoji",sans-serif', letterSpacing: 0, ...style }}
    >
      {char}
    </span>
  );
}
