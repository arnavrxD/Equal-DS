// Site-only primitives (documentation chrome). Everything visual inside examples is the real DS.
import { createContext, useContext, useEffect, useMemo, useRef, useState, type ReactNode } from 'react';
import { Icon } from '@equal/ds';
import { copyText } from '../app/copy';
import { useTheme, type ExampleTheme } from '../app/theme';

// ---------------------------------------------------------------- inline code
/** Render `backtick` spans in registry prose as <code>. */
export function md(text: string): ReactNode {
  if (!text.includes('`')) return text;
  return text.split(/(`[^`]+`)/g).map((part, i) => (part.startsWith('`') && part.endsWith('`') ? <code key={i}>{part.slice(1, -1)}</code> : part));
}

// ---------------------------------------------------------------- toast
const ToastCtx = createContext<(msg: string) => void>(() => {});
export function ToastProvider({ children }: { children: ReactNode }) {
  const [msg, setMsg] = useState<string | null>(null);
  const t = useRef<ReturnType<typeof setTimeout>>(undefined);
  const show = (m: string) => {
    setMsg(m);
    clearTimeout(t.current);
    t.current = setTimeout(() => setMsg(null), 1600);
  };
  return (
    <ToastCtx.Provider value={show}>
      {children}
      {msg && <div className="s-toast" role="status">{msg}</div>}
    </ToastCtx.Provider>
  );
}
export const useToast = () => useContext(ToastCtx);

/** Copyable code identifier. `k` is a small kind label (CSS, TS, Kotlin). */
export function CopyTag({ value, k, label }: { value: string; k?: string; label?: string }) {
  const toast = useToast();
  const [copied, setCopied] = useState(false);
  return (
    <button
      type="button"
      className="s-tag"
      data-copied={copied}
      title={`Copy ${value}`}
      onClick={async () => {
        if (await copyText(value)) {
          setCopied(true);
          toast(`Copied ${value}`);
          setTimeout(() => setCopied(false), 1200);
        }
      }}
    >
      {k && <span className="s-tag__k">{k}</span>}
      {label ?? value}
    </button>
  );
}

// ---------------------------------------------------------------- headings + on-this-page
type TocItem = { id: string; label: string };
const TocCtx = createContext<{ register: (i: TocItem) => void } | null>(null);
export const slug = (s: string) => s.toLowerCase().replace(/&/g, 'and').replace(/[^a-z0-9]+/g, '-').replace(/^-|-$/g, '');

export function H2({ children, id }: { children: string; id?: string }) {
  const ctx = useContext(TocCtx);
  const anchor = id ?? slug(children);
  useEffect(() => ctx?.register({ id: anchor, label: children }), [anchor, children, ctx]);
  return <h2 className="s-h2" id={anchor}>{children}</h2>;
}

/** Page layout with a live "On this page" rail built from its <H2>s. */
export function Page({ title, lead, meta, children, toc = true }: { title: string; lead?: ReactNode; meta?: ReactNode; children: ReactNode; toc?: boolean }) {
  const [items, setItems] = useState<TocItem[]>([]);
  const [active, setActive] = useState<string | null>(null);
  const api = useMemo(() => ({ register: (i: TocItem) => setItems((xs) => (xs.some((x) => x.id === i.id) ? xs : [...xs, i])) }), []);
  useEffect(() => {
    document.title = `${title} · Equal AI Design System`;
  }, [title]);
  useEffect(() => {
    if (!items.length) return;
    const els = items.map((i) => document.getElementById(i.id)).filter(Boolean) as HTMLElement[];
    const io = new IntersectionObserver((entries) => {
      const vis = entries.filter((e) => e.isIntersecting).sort((a, b) => a.boundingClientRect.top - b.boundingClientRect.top);
      if (vis[0]) setActive(vis[0].target.id);
    }, { rootMargin: '-72px 0px -65% 0px' });
    els.forEach((e) => io.observe(e));
    return () => io.disconnect();
  }, [items]);
  const ordered = items;
  return (
    <TocCtx.Provider value={api}>
      <div className={`s-page ${toc ? '' : 's-page--notoc'}`}>
        <article className="s-article">
          <h1 className="s-title">{title}</h1>
          {lead && <p className="s-lead">{lead}</p>}
          {meta && <div className="s-meta">{meta}</div>}
          {children}
        </article>
        {toc && ordered.length > 1 && (
          <nav className="s-toc" aria-label="On this page">
            <b>On this page</b>
            {ordered.map((i) => (
              <a key={i.id} href={`#${i.id}`} data-active={active === i.id} onClick={(e) => (e.preventDefault(), document.getElementById(i.id)?.scrollIntoView({ behavior: 'smooth', block: 'start' }))}>
                {i.label}
              </a>
            ))}
          </nav>
        )}
      </div>
    </TocCtx.Provider>
  );
}

// ---------------------------------------------------------------- example canvas
/** Lightweight JSX highlighting: strings, tags/components, comments. */
const KOTLIN_KEYWORDS = /^(val|var|fun|by|if|else|when|for|in|return|true|false|null|object|private|internal)$/;

/** Tiny highlighter: comments, strings, JSX tags; for Kotlin also keywords and `Composable(` calls. */
export function highlight(code: string, lang: 'tsx' | 'kotlin' | 'css' = 'tsx'): ReactNode[] {
  const out: ReactNode[] = [];
  const re =
    lang === 'kotlin'
      ? /(\/\/[^\n]*)|("[^"\n]*")|(\b[A-Z][A-Za-z0-9]*(?=\s*[({])|\b[a-z]+\b)/g
      : /(\/\/[^\n]*|\{\/\*[\s\S]*?\*\/\}|\/\*[\s\S]*?\*\/)|("[^"\n]*"|'[^'\n]*'|`[^`]*`)|(<\/?[A-Z][A-Za-z0-9.]*|<\/?[a-z][a-z0-9]*|\/?>)/g;
  let last = 0;
  let m: RegExpExecArray | null;
  let i = 0;
  while ((m = re.exec(code))) {
    if (lang === 'kotlin' && m[3] && /^[a-z]/.test(m[3]) && !KOTLIN_KEYWORDS.test(m[3])) continue;
    if (m.index > last) out.push(code.slice(last, m.index));
    out.push(<span key={i++} className={m[1] ? 'c' : m[2] ? 's' : 'k'}>{m[0]}</span>);
    last = m.index + m[0].length;
  }
  out.push(code.slice(last));
  return out;
}

/** A copyable code block with optional language tabs (e.g. React / Compose). */
export function CodeTabs({ tabs, initial }: { tabs: { label: string; code: string; lang?: 'tsx' | 'kotlin' | 'css' }[]; initial?: number }) {
  const [i, setI] = useState(initial ?? 0);
  const toast = useToast();
  const t = tabs[Math.min(i, tabs.length - 1)];
  if (!t) return null;
  return (
    <div className="s-codetabs">
      <div className="s-codetabs__bar">
        {tabs.length > 1 ? (
          <div className="s-seg" role="group" aria-label="Language">
            {tabs.map((x, n) => (
              <button key={x.label} type="button" aria-pressed={n === i} onClick={() => setI(n)}>{x.label}</button>
            ))}
          </div>
        ) : (
          <span className="s-codetabs__label">{t.label}</span>
        )}
        <button type="button" className="s-copybtn" onClick={async () => (await copyText(t.code)) && toast(`${t.label} code copied`)}>Copy</button>
      </div>
      <pre className="s-code"><code>{highlight(t.code, t.lang)}</code></pre>
    </div>
  );
}

export type ExampleProps = {
  title: string;
  note?: string;
  children: ReactNode;
  code?: string;
  /** Jetpack Compose equivalent (from compose-snippets.json, compiled Kotlin). */
  compose?: string;
  /** Width of the canvas content (px) or 'wide'. */
  width?: number | 'wide';
  surface?: 'primary' | 'surface';
  /** Force a theme regardless of the site switch. */
  theme?: ExampleTheme;
};

/** Live example in light, dark or both (follows the site switch), with a code tab. */
export function Example({ title, note, children, code, compose, width = 360, surface = 'primary', theme }: ExampleProps) {
  const site = useTheme();
  const mode = theme ?? site.exampleTheme;
  const [tab, setTab] = useState<'preview' | 'react' | 'compose'>('preview');
  const toast = useToast();
  const modes: ('light' | 'dark')[] = mode === 'both' ? ['light', 'dark'] : [mode];
  const inner = <div className="s-canvas__inner" style={width === 'wide' ? undefined : { maxWidth: width }}>{children}</div>;
  const shown = tab === 'react' ? code : tab === 'compose' ? compose : undefined;
  return (
    <figure className="s-example">
      <div className="s-example__bar">
        <div style={{ minWidth: 0 }}>
          <div className="s-example__title">{title}</div>
          {note && <div className="s-example__note">{md(note)}</div>}
        </div>
        {(code || compose) && (
          <div className="s-example__tabs">
            <div className="s-seg" role="group" aria-label="View">
              <button type="button" aria-pressed={tab === 'preview'} onClick={() => setTab('preview')}>Preview</button>
              {code && <button type="button" aria-pressed={tab === 'react'} onClick={() => setTab('react')}>React</button>}
              {compose && <button type="button" aria-pressed={tab === 'compose'} onClick={() => setTab('compose')}>Compose</button>}
            </div>
          </div>
        )}
      </div>
      {tab === 'preview' || !shown ? (
        <div className={`s-example__stage ${modes.length > 1 ? 's-example__stage--both' : ''}`}>
          {modes.map((m) => (
            <div key={m} data-theme={m} className={`s-canvas ${surface === 'surface' ? 's-canvas--surface' : ''}`}>
              {modes.length > 1 && <span className="s-canvas__mode">{m}</span>}
              {inner}
            </div>
          ))}
        </div>
      ) : (
        <div style={{ position: 'relative' }}>
          <button type="button" className="s-copybtn" style={{ position: 'absolute', top: 12, right: 12 }} onClick={async () => (await copyText(shown)) && toast(tab === 'compose' ? 'Compose code copied' : 'React code copied')}>
            Copy
          </button>
          <pre className="s-code"><code>{highlight(shown, tab === 'compose' ? 'kotlin' : 'tsx')}</code></pre>
        </div>
      )}
    </figure>
  );
}

// ---------------------------------------------------------------- tables & lists
export type PropRow = { name: string; type: string; default?: string; description: string; required?: boolean };
export function PropsTable({ rows }: { rows: PropRow[] }) {
  return (
    <div style={{ overflowX: 'auto' }}>
      <table className="s-table">
        <thead>
          <tr><th>Prop</th><th>Type</th><th>Default</th><th>Description</th></tr>
        </thead>
        <tbody>
          {rows.map((r) => (
            <tr key={r.name}>
              <td><code>{r.name}</code>{r.required && <span className="s-req" title="Required">*</span>}</td>
              <td><code style={{ color: 'var(--site-ink-2)' }}>{r.type}</code></td>
              <td>{r.default ? <code>{r.default}</code> : <span style={{ color: 'var(--site-ink-3)' }}>–</span>}</td>
              <td>{md(r.description)}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export function DoDont({ dos, donts }: { dos: string[]; donts: string[] }) {
  return (
    <div className="s-dodont">
      <div className="s-dd s-dd--do">
        <h4><span className="s-dd__mark"><Icon name="check" size={14} /></span>Do</h4>
        <ul>{dos.map((d) => <li key={d}>{md(d)}</li>)}</ul>
      </div>
      <div className="s-dd s-dd--dont">
        <h4><span className="s-dd__mark"><Icon name="close" size={14} /></span>Don’t</h4>
        <ul>{donts.map((d) => <li key={d}>{md(d)}</li>)}</ul>
      </div>
    </div>
  );
}

export function Callout({ children }: { children: ReactNode }) {
  return (
    <div className="s-callout">
      <Icon name="info" size={20} />
      <div>{children}</div>
    </div>
  );
}
