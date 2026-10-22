import { useEffect, useRef, useState, type CSSProperties } from 'react';
import type { AnimationItem } from 'lottie-web';

/**
 * Lottie animations: drop `<name>.json` into `equal-ds/assets/lottie/` and play it with
 * `<Lottie name="<name>" />` (picked up by a Vite glob, like image assets). Android plays the same
 * file with `EqualLottie` (`npm run assets` copies it to `res/raw/lottie_<name>.json`).
 *
 * Illustrative motion only (mascot moments, celebrations, typing); micro-interactions stay in code
 * on the motion tokens. Respects reduced motion: shows the first frame and doesn't play. `tint`
 * recolours every fill and stroke (single-colour animations that should follow the theme, e.g.
 * `var(--eq-text-action)`: green in light, lime in dark).
 */
// Lazy: each file is fetched the first time it plays, so large animations don't weigh down every page.
const files = import.meta.glob('../../../assets/lottie/*.json', { import: 'default' }) as Record<string, () => Promise<object>>;
const loaders: Record<string, () => Promise<object>> = Object.fromEntries(
  Object.entries(files).map(([path, load]) => [path.split('/').pop()!.replace(/\.json$/, ''), load]),
);
export const lottieNames = Object.keys(loaders).sort();

export type LottieProps = {
  /** File name in assets/lottie/ (without .json). */
  name?: string;
  /** Or pass the parsed JSON directly. */
  animationData?: object;
  width?: number;
  height?: number;
  loop?: boolean;
  autoplay?: boolean;
  /** CSS colour that replaces every fill/stroke, e.g. `var(--eq-text-action)`. */
  tint?: string;
  /** Accessible name; omit for decorative animations. */
  label?: string;
  style?: CSSProperties;
  /** Called when a non-looping play reaches its last frame (it then holds that frame). */
  onComplete?: () => void;
  /** Under reduced motion, show the last frame instead of the first (end states such as a check). */
  stillFrame?: 'first' | 'last';
  /** Don't play: hold `stillFrame` (a settled end state). */
  still?: boolean;
};

export function Lottie({ name, animationData, width = 120, height = 120, loop = true, autoplay = true, tint, label, style, onComplete, stillFrame = 'first', still = false }: LottieProps) {
  const ref = useRef<HTMLDivElement>(null);
  const done = useRef(onComplete);
  done.current = onComplete;
  const [loaded, setLoaded] = useState<object | undefined>(undefined);
  const known = !!animationData || (!!name && name in loaders);
  useEffect(() => {
    if (animationData || !name || !loaders[name]) return;
    let live = true;
    loaders[name]().then((d) => { if (live) setLoaded(d); });
    return () => { live = false; };
  }, [name, animationData]);
  const data = animationData ?? loaded;
  useEffect(() => {
    if (!data || !ref.current) return;
    let anim: AnimationItem | undefined;
    let cancelled = false;
    const reduce = still || (typeof window !== 'undefined' && window.matchMedia?.('(prefers-reduced-motion: reduce)').matches);
    import('lottie-web/build/player/lottie_light').then(({ default: lottie }) => {
      if (cancelled || !ref.current) return;
      // Lottie mutates the data it plays; give it a copy so other players start clean.
      const item = lottie.loadAnimation({ container: ref.current, renderer: 'svg', loop: !reduce && loop, autoplay: !reduce && autoplay, animationData: structuredClone(data) });
      if (reduce) item.addEventListener('DOMLoaded', () => { item.goToAndStop(stillFrame === 'last' ? item.totalFrames - 1 : 0, true); done.current?.(); });
      else item.addEventListener('complete', () => done.current?.());
      anim = item;
    });
    return () => {
      cancelled = true;
      anim?.destroy();
    };
  }, [data, loop, autoplay, stillFrame, still]);
  if (!known) {
    return <span className="eq-asset-placeholder" data-asset-placeholder={`lottie:${name}`} title={`lottie: ${name}`} style={{ width, height, display: 'inline-block' }} />;
  }
  return (
    <div
      ref={ref}
      className={tint ? 'eq-lottie eq-lottie--tint' : 'eq-lottie'}
      role={label ? 'img' : undefined}
      aria-label={label}
      aria-hidden={label ? undefined : true}
      style={{ width, height, flexShrink: 0, ...(tint ? { ['--eq-lottie-tint' as string]: tint } : null), ...style }}
    />
  );
}
