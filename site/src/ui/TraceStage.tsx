import { useEffect, useMemo, useRef, useState, type ReactNode } from 'react';
import { ANDROID_FRAME, PhoneFrame } from '@equal/ds';
import { componentDocs } from '../registry/components';
import { useRoute } from '../app/router';
import { traceChain, type TraceStep } from '../app/trace';
import { docLevel, foundationAtoms, type Level } from '../registry/levels';

/** Where each traceable component lives in the docs, and its atomic level. */
type Target = { path: string; level: Level };
const targets = new Map<string, Target>();
componentDocs.forEach((d) => d.components.forEach((c) => targets.set(c, { path: `/components/${d.id}`, level: docLevel[d.id] ?? 'molecule' })));
Object.entries(foundationAtoms).forEach(([c, f]) => targets.set(c, { path: f.path, level: 'atom' }));
const traceable = new Set(targets.keys());

export type StagePhone = {
  key: string;
  label: string;
  to?: string;
  render: () => ReactNode;
  /** React code shown in the stage's Code section. */
  code?: string;
  /** Extend this phone vertically (dp, ≥ 800) when its content is longer than the frame. */
  height?: number;
};

type Box = { x: number; y: number; w: number; h: number };

/**
 * Horizontal stage of live phones. In Trace mode, hovering outlines the DS component under the
 * cursor and clicking opens its page; in Try mode the phones are fully interactive.
 */
export function TraceStage({ phones, scale = 0.78, mode, height = 800, wrap = false }: { phones: StagePhone[]; scale?: number; mode: 'trace' | 'try'; height?: number; wrap?: boolean }) {
  const stage = useRef<HTMLDivElement>(null);
  // The chain under the cursor, innermost (atom) first, and which step of it is selected.
  const [steps, setSteps] = useState<TraceStep[]>([]);
  const [depth, setDepth] = useState(0);
  const { go } = useRoute();
  const w = ANDROID_FRAME.width * scale;

  const locate = (target: EventTarget | null) => {
    const root = stage.current;
    if (!root || !(target instanceof Element)) return setSteps([]);
    const chain = traceChain(target, traceable, root);
    setSteps((prev) => {
      if (prev[0]?.element !== chain[0]?.element) setDepth(0);
      return chain;
    });
  };

  // ↑ walks out (atom → molecule → organism), ↓ walks back in.
  useEffect(() => {
    if (mode !== 'trace' || !steps.length) return;
    const onKey = (e: KeyboardEvent) => {
      if (e.key === 'ArrowUp') { e.preventDefault(); setDepth((d) => Math.min(d + 1, steps.length - 1)); }
      if (e.key === 'ArrowDown') { e.preventDefault(); setDepth((d) => Math.max(d - 1, 0)); }
    };
    window.addEventListener('keydown', onKey);
    return () => window.removeEventListener('keydown', onKey);
  }, [mode, steps.length]);

  const step = steps[Math.min(depth, steps.length - 1)];
  const box: Box | null = (() => {
    const root = stage.current;
    if (!root || !step) return null;
    const r = step.element.getBoundingClientRect();
    const sr = root.getBoundingClientRect();
    return { x: r.left - sr.left + root.scrollLeft, y: r.top - sr.top + root.scrollTop, w: r.width, h: r.height };
  })();
  const tagAbove = box ? box.y > 64 : true;

  const handlers = useMemo(
    () =>
      mode === 'trace'
        ? {
            onMouseMove: (e: React.MouseEvent) => locate(e.target instanceof Element && e.target.closest('.s-phone__label') ? null : e.target),
            onMouseLeave: () => setSteps([]),
            onClickCapture: (e: React.MouseEvent) => {
              const root = stage.current;
              if (!root || !(e.target instanceof Element) || e.target.closest('.s-phone__label')) return;
              e.preventDefault();
              e.stopPropagation();
              const chain = traceChain(e.target, traceable, root);
              const picked = chain[Math.min(depthRef.current, chain.length - 1)];
              const t = picked && targets.get(picked.name);
              if (t) go(t.path);
            },
          }
        : {},
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [mode],
  );
  const depthRef = useRef(0);
  depthRef.current = depth;

  return (
    <div ref={stage} className={`s-stage ${wrap ? 's-stage--wrap' : ''}`} {...handlers} style={{ cursor: mode === 'trace' ? 'crosshair' : undefined }} onScroll={() => setSteps([])}>
      {phones.map((p) => (
        <div key={p.key} className="s-phone" style={{ width: w }}>
          <div className="s-phone__label">
            <span>{p.label}</span>
            {p.to && <a href={`#${p.to}`}>Open</a>}
          </div>
          <div style={{ width: w, height: Math.max(p.height ?? height, ANDROID_FRAME.height) * scale }}>
            <div className="s-phone__scale" style={{ width: ANDROID_FRAME.width, height: Math.max(p.height ?? height, ANDROID_FRAME.height), transform: `scale(${scale})` }}>
              <PhoneFrame height={p.height ?? height}>{p.render()}</PhoneFrame>
            </div>
          </div>
        </div>
      ))}
      {mode === 'trace' && box && step && (
        <>
          <div className={`s-trace-box is-${targets.get(step.name)?.level ?? 'atom'}`} style={{ transform: `translate(${box.x - 2}px, ${box.y - 2}px)`, width: box.w + 4, height: box.h + 4 }} />
          <div className="s-trace-tag" style={{ transform: `translate(${box.x - 2}px, ${tagAbove ? box.y - 62 : box.y + box.h + 8}px)` }}>
            <span className="s-trace-chain">
              {steps.slice(0, 4).map((st, i) => {
                const lvl = targets.get(st.name)?.level ?? 'atom';
                return (
                  <span key={st.name + i} className={`s-trace-step ${i === Math.min(depth, steps.length - 1) ? 'is-on' : ''}`}>
                    {i > 0 && <span className="s-trace-sep">›</span>}
                    <b>{st.name}</b>
                    <em className={`s-trace-lvl is-${lvl}`}>{lvl}</em>
                  </span>
                );
              })}
            </span>
            <kbd>{steps.length > 1 ? '↑ ↓ change level · ' : ''}click to open</kbd>
          </div>
        </>
      )}
    </div>
  );
}
