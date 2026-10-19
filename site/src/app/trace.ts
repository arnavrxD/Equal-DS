// Trace: map any DOM node inside a live screen to the nearest design-system component,
// by walking React's fiber tree (component names survive the build via esbuild keepNames).

type Fiber = { type: unknown; return: Fiber | null; child: Fiber | null; sibling: Fiber | null; stateNode: unknown };

const fiberOf = (node: Element): Fiber | null => {
  const key = Object.keys(node).find((k) => k.startsWith('__reactFiber$'));
  return key ? ((node as unknown as Record<string, Fiber>)[key] ?? null) : null;
};

const nameOf = (f: Fiber): string | null => {
  const t = f.type as { name?: string; displayName?: string } | null;
  if (typeof t === 'function') return (t as { displayName?: string; name?: string }).displayName || (t as { name?: string }).name || null;
  return null;
};

/** First host DOM element rendered by a component fiber. */
const hostOf = (f: Fiber): Element | null => {
  let c: Fiber | null = f.child;
  while (c) {
    if (c.stateNode instanceof Element) return c.stateNode;
    const deeper = hostOf(c);
    if (deeper) return deeper;
    c = c.sibling;
  }
  return null;
};

export type TraceHit = { name: string; element: Element; chain: string[] };

/** Nearest DS component under `node` whose name is in `names`, plus its DS ancestors (innermost first). */
export function traceAt(node: Element | null, names: Set<string>, boundary: Element): TraceHit | null {
  if (!node) return null;
  let f = fiberOf(node);
  const chain: string[] = [];
  let hit: TraceHit | null = null;
  while (f) {
    const n = nameOf(f);
    if (n && names.has(n)) {
      const el = hostOf(f);
      if (el && boundary.contains(el)) {
        if (!hit) hit = { name: n, element: el, chain };
        if (chain[chain.length - 1] !== n) chain.push(n);
      }
    }
    f = f.return;
  }
  return hit;
}

export type TraceStep = { name: string; element: Element };

/**
 * Every DS component under `node`, innermost first (atom → molecule → organism), each with its own
 * host element. Consecutive repeats of the same name and steps with the same box are collapsed.
 */
export function traceChain(node: Element | null, names: Set<string>, boundary: Element): TraceStep[] {
  if (!node) return [];
  let f = fiberOf(node);
  const steps: TraceStep[] = [];
  while (f) {
    const n = nameOf(f);
    if (n && names.has(n)) {
      const el = hostOf(f);
      if (el && boundary.contains(el)) {
        const last = steps[steps.length - 1];
        if (!last || (last.name !== n && last.element !== el)) steps.push({ name: n, element: el });
        else if (last.element === el && last.name !== n) steps[steps.length - 1] = { name: n, element: el };
      }
    }
    f = f.return;
  }
  return steps;
}
