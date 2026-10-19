// Shared accessibility behaviour: modal focus management, menu and radio-group keyboard
// navigation, polite announcements and the reduced-motion query. Every overlay, menu and radio
// group in the system uses these, so the behaviour is the same everywhere.
import { useEffect, useRef, useState, type KeyboardEvent as ReactKeyboardEvent, type ReactNode, type RefObject } from 'react';

const FOCUSABLE =
  'a[href], area[href], button:not([disabled]), input:not([disabled]):not([type="hidden"]), select:not([disabled]), textarea:not([disabled]), iframe, [tabindex]:not([tabindex="-1"]), [contenteditable="true"]';

/** Tabbable elements inside `root`, in DOM order, skipping hidden and inert ones. */
export function getFocusable(root: HTMLElement): HTMLElement[] {
  return Array.from(root.querySelectorAll<HTMLElement>(FOCUSABLE)).filter((el) => {
    if (el.closest('[inert]')) return false;
    if (el.getClientRects().length === 0) return false;
    return getComputedStyle(el).visibility !== 'hidden';
  });
}

/** True when the user asked the OS for reduced motion. */
export function prefersReducedMotion(): boolean {
  return typeof window !== 'undefined' && !!window.matchMedia?.('(prefers-reduced-motion: reduce)').matches;
}

/** Live `prefers-reduced-motion` value that follows OS changes. */
export function useReducedMotion(): boolean {
  const [reduce, setReduce] = useState(prefersReducedMotion);
  useEffect(() => {
    const mq = window.matchMedia?.('(prefers-reduced-motion: reduce)');
    if (!mq) return;
    const on = () => setReduce(mq.matches);
    on();
    mq.addEventListener?.('change', on);
    return () => mq.removeEventListener?.('change', on);
  }, []);
  return reduce;
}

// The last pointer / key press. An overlay that mounts already open only takes focus when the user
// just did something next to it (so docs pages that render many open sheets never steal focus).
let lastInteraction: { target: EventTarget | null; time: number } = { target: null, time: 0 };
if (typeof document !== 'undefined') {
  const note = (e: Event) => { lastInteraction = { target: e.target, time: Date.now() }; };
  document.addEventListener('pointerdown', note, true);
  document.addEventListener('keydown', note, true);
}

/** The overlay's outermost layer (the absolutely / fixed positioned element that covers the screen). */
function overlayLayer(panel: HTMLElement): HTMLElement | null {
  const isLayer = (el: Element) => { const pos = getComputedStyle(el).position; return pos === 'absolute' || pos === 'fixed'; };
  let el: HTMLElement | null = panel;
  while (el && !isLayer(el)) el = el.parentElement;
  if (!el) return null;
  while (el.parentElement && el.parentElement !== document.body && isLayer(el.parentElement)) el = el.parentElement;
  return el;
}

/** The container an overlay covers: the PhoneFrame, or the element its layer sits in. */
function overlayScope(panel: HTMLElement): Element {
  const frame = panel.closest('[data-eq-frame]');
  if (frame) return frame;
  return overlayLayer(panel)?.parentElement ?? document.body;
}

/** Make the screen behind an open overlay inert (the layer's siblings); returns the undo. */
function inertBehind(panel: HTMLElement): () => void {
  const layer = overlayLayer(panel);
  const parent = layer?.parentElement;
  if (!layer || !parent || parent === document.body) return () => {};
  const changed: Element[] = [];
  for (const sib of Array.from(parent.children)) {
    if (sib === layer || sib.hasAttribute('inert') || sib.contains(panel)) continue;
    sib.setAttribute('inert', '');
    changed.push(sib);
  }
  return () => changed.forEach((el) => el.removeAttribute('inert'));
}

function userJustActedNear(panel: HTMLElement): boolean {
  const t = lastInteraction.target as Node | null;
  if (!t || Date.now() - lastInteraction.time > 1500) return false;
  return overlayScope(panel).contains(t);
}

const modalStack: HTMLElement[] = [];

export type UseModalOptions = {
  /** The overlay is showing. */
  open: boolean;
  /** Called on Escape (and wired by the component to the scrim / close button). */
  onClose?: () => void;
  /** false for inline (docs) renders: no focus move, no trap, no aria-modal. */
  enabled?: boolean;
};

export type ModalProps<T extends HTMLElement> = {
  ref: RefObject<T>;
  tabIndex: -1;
  'aria-modal'?: true;
  'data-eq-modal': '';
  onKeyDown: (e: ReactKeyboardEvent<T>) => void;
};

/**
 * Modal behaviour for sheets, dialogs and full-screen panels. Spread the result onto the panel
 * (the element with role="dialog"):
 *
 *   const modal = useModal({ open, onClose, enabled: mode === 'overlay' });
 *   <div role="dialog" aria-label="…" {...modal}>…</div>
 *
 * When it opens it remembers the focused element, moves focus to the first focusable control in
 * the panel (or the panel itself), makes the screen behind it inert, keeps Tab / Shift+Tab inside,
 * closes on Escape, sets aria-modal="true", and gives focus back when it closes. An overlay that
 * mounts already open (docs pages) only does this when the user just acted next to it.
 */
export function useModal<T extends HTMLElement = HTMLDivElement>({ open, onClose, enabled = true }: UseModalOptions): ModalProps<T> {
  const ref = useRef<T>(null);
  const onCloseRef = useRef(onClose);
  onCloseRef.current = onClose;
  const mounted = useRef(false);
  const active = useRef(false);

  useEffect(() => {
    const firstRun = !mounted.current;
    mounted.current = true;
    const panel = ref.current;
    if (!enabled || !open || !panel) return;
    // Mounted already open: only take focus if the user just opened it from nearby.
    if (firstRun && !userJustActedNear(panel)) return;

    const prev = document.activeElement;
    const fromClick = (lastInteraction.target as Element | null)?.closest?.(FOCUSABLE) as HTMLElement | null;
    const restore = prev instanceof HTMLElement && prev !== document.body && !panel.contains(prev) ? prev : fromClick && !panel.contains(fromClick) ? fromClick : null;

    active.current = true;
    modalStack.push(panel);
    const undoInert = inertBehind(panel);
    if (!panel.contains(document.activeElement)) (getFocusable(panel)[0] ?? panel).focus({ preventScroll: true });

    return () => {
      undoInert();
      active.current = false;
      const i = modalStack.lastIndexOf(panel);
      if (i >= 0) modalStack.splice(i, 1);
      const now = document.activeElement;
      const focusWasInside = !now || now === document.body || panel.contains(now) || !panel.isConnected;
      if (restore && restore.isConnected && focusWasInside) restore.focus({ preventScroll: true });
    };
  }, [open, enabled]);

  const onKeyDown = (e: ReactKeyboardEvent<T>) => {
    const panel = ref.current;
    if (!panel || !enabled || !open) return;
    if (e.key === 'Escape' && onCloseRef.current) {
      e.stopPropagation();
      e.preventDefault();
      onCloseRef.current();
      return;
    }
    if (e.key !== 'Tab' || !active.current || modalStack[modalStack.length - 1] !== panel) return;
    const items = getFocusable(panel);
    if (items.length === 0) { e.preventDefault(); panel.focus(); return; }
    const first = items[0];
    const last = items[items.length - 1];
    const cur = document.activeElement;
    if (e.shiftKey && (cur === first || cur === panel || !panel.contains(cur))) { e.preventDefault(); last.focus(); }
    else if (!e.shiftKey && (cur === last || !panel.contains(cur))) { e.preventDefault(); first.focus(); }
  };

  return { ref, tabIndex: -1, 'aria-modal': enabled && open ? true : undefined, 'data-eq-modal': '', onKeyDown };
}

/** ArrowUp / ArrowDown / Home / End between the `[role="menuitem"]` children of a menu. */
export function menuKeyDown(e: ReactKeyboardEvent<HTMLElement>, onClose?: () => void) {
  const items = Array.from(e.currentTarget.querySelectorAll<HTMLElement>('[role="menuitem"]:not([aria-disabled="true"]):not(:disabled)'));
  if (e.key === 'Escape' && onClose) { e.preventDefault(); e.stopPropagation(); onClose(); return; }
  if (e.key === 'Tab' && onClose) { onClose(); return; }
  if (!items.length) return;
  const i = items.indexOf(document.activeElement as HTMLElement);
  let next = -1;
  if (e.key === 'ArrowDown') next = i < 0 ? 0 : (i + 1) % items.length;
  else if (e.key === 'ArrowUp') next = i < 0 ? items.length - 1 : (i - 1 + items.length) % items.length;
  else if (e.key === 'Home') next = 0;
  else if (e.key === 'End') next = items.length - 1;
  if (next < 0) return;
  e.preventDefault();
  items[next].focus();
}

/**
 * Menu behaviour for a popup role="menu" opened from a trigger: focus the first item when it
 * opens, arrow keys between items, Escape closes and returns focus to the trigger, Tab closes.
 * Spread `menuProps` on the menu; put `triggerRef` on the button that opens it (with
 * aria-haspopup="menu" and aria-expanded).
 */
export function useMenu<M extends HTMLElement = HTMLDivElement, B extends HTMLElement = HTMLButtonElement>({ open, onClose }: { open: boolean; onClose?: () => void }) {
  const menuRef = useRef<M>(null);
  const triggerRef = useRef<B>(null);
  const mounted = useRef(false);
  useEffect(() => {
    const firstRun = !mounted.current;
    mounted.current = true;
    const menu = menuRef.current;
    if (!open || !menu || firstRun) return;
    menu.querySelector<HTMLElement>('[role="menuitem"]')?.focus({ preventScroll: true });
    return () => {
      const now = document.activeElement;
      if (!now || now === document.body || menu.contains(now) || !menu.isConnected) triggerRef.current?.focus({ preventScroll: true });
    };
  }, [open]);
  const close = () => { onClose?.(); };
  return {
    triggerRef,
    menuProps: { ref: menuRef, onKeyDown: (e: ReactKeyboardEvent<M>) => menuKeyDown(e, close) },
  };
}

/**
 * Roving radio group: put on the role="radiogroup" container. Arrow keys move focus and selection
 * to the next / previous radio (wrapping), Home / End to the first / last. Give the checked radio
 * tabIndex 0 and the rest -1 (see `rovingTabIndex`).
 */
export function radioGroupKeyDown(e: ReactKeyboardEvent<HTMLElement>) {
  const radios = Array.from(e.currentTarget.querySelectorAll<HTMLElement>('[role="radio"]:not([aria-disabled="true"]):not(:disabled)'));
  if (!radios.length) return;
  const i = radios.indexOf(document.activeElement as HTMLElement);
  if (i < 0) return;
  let next = -1;
  if (e.key === 'ArrowRight' || e.key === 'ArrowDown') next = (i + 1) % radios.length;
  else if (e.key === 'ArrowLeft' || e.key === 'ArrowUp') next = (i - 1 + radios.length) % radios.length;
  else if (e.key === 'Home') next = 0;
  else if (e.key === 'End') next = radios.length - 1;
  if (next < 0) return;
  e.preventDefault();
  radios[next].focus();
  radios[next].click();
}

/** tabIndex for radio `i` of a roving group: the checked one, or the first when none is checked. */
export function rovingTabIndex(i: number, checkedIndex: number): 0 | -1 {
  return i === (checkedIndex < 0 ? 0 : checkedIndex) ? 0 : -1;
}

/**
 * A persistent, visually hidden polite live region. It is in the DOM before the message arrives
 * (screen readers skip regions created in the same render as their text), and only speaks when
 * `message` changes. `onMount` also speaks the first message once (e.g. a redirect notice) when the
 * element appeared in response to the user.
 */
export function Announce({ message, onMount = false }: { message: string; onMount?: boolean }) {
  const [said, setSaid] = useState('');
  const first = useRef(true);
  useEffect(() => {
    // On mount, only speak if the user just did something (not when a docs page renders it).
    const skip = first.current && (!onMount || Date.now() - lastInteraction.time > 1500);
    first.current = false;
    if (skip) return;
    setSaid('');
    const t = setTimeout(() => setSaid(message), 120);
    return () => clearTimeout(t);
  }, [message, onMount]);
  return <span className="eq-sr" role="status" aria-live="polite" aria-atomic="true">{said}</span>;
}

/** Visually hidden text for screen readers. */
export function VisuallyHidden({ children }: { children: ReactNode }) {
  return <span className="eq-sr">{children}</span>;
}

/**
 * When a control is replaced by another element (APPLY → APPLIED), move focus to the replacement
 * if the original had it. Call `markFocus` in the original's onClick; put `targetRef` (with
 * tabIndex -1) on the replacement.
 */
export function useFocusHandoff<T extends HTMLElement = HTMLElement>(swapped: boolean) {
  const targetRef = useRef<T>(null);
  const pending = useRef(false);
  useEffect(() => {
    if (swapped && pending.current) targetRef.current?.focus({ preventScroll: true });
    pending.current = false;
  }, [swapped]);
  return { targetRef, markFocus: () => { pending.current = true; } };
}

/** `inert` for a closed overlay that stays mounted (React 18 has no typed prop for it). */
export function inertWhen(on: boolean): object {
  return on ? { inert: '' } : {};
}
