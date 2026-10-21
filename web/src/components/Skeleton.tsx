import type { CSSProperties } from 'react';
import './Skeleton.css';

export type SkeletonProps = {
  /** line: a text line (radius 4); circle: an avatar; rect: a card or image. */
  shape?: 'line' | 'circle' | 'rect';
  width?: number | string;
  height?: number;
  /** neutral on app surfaces; gold on the cream-gold subscription cards (Figma "Shimmer"). */
  tone?: 'neutral' | 'gold';
  radius?: number;
  style?: CSSProperties;
};

/**
 * Skeleton (Figma "Shimmer"): a placeholder with a highlight sweeping across it while content loads.
 * Decorative (aria-hidden); put `aria-busy` on the region that is loading. No sweep under reduced motion.
 */
export function Skeleton({ shape = 'line', width = '100%', height, tone = 'neutral', radius, style }: SkeletonProps) {
  const h = height ?? (shape === 'line' ? 15 : shape === 'circle' ? 40 : 80);
  const w = shape === 'circle' ? h : width;
  const r = radius ?? (shape === 'circle' ? 999 : shape === 'line' ? 4 : 12);
  return <span aria-hidden className={`eq-skeleton eq-skeleton--${tone}`} style={{ width: w, height: h, borderRadius: r, ...style }} />;
}

/** A few skeleton lines, the last one shorter. */
export function SkeletonText({ lines = 3, tone = 'neutral', gap = 8 }: { lines?: number; tone?: 'neutral' | 'gold'; gap?: number }) {
  return (
    <span aria-hidden style={{ display: 'flex', flexDirection: 'column', gap }}>
      {Array.from({ length: lines }, (_, i) => <Skeleton key={i} tone={tone} width={i === lines - 1 && lines > 1 ? '62%' : '100%'} />)}
    </span>
  );
}
