import { lazy, Suspense, useEffect, useMemo, useRef, useState, type ReactNode } from 'react';
import { aliases, EqualLogo, Icon, iconNames, typography } from '@equal/ds';
import { componentDocs } from '../registry/components';
import { allPages, sectionFor, sections, type Section } from './nav';
import { Link, useRoute } from './router';
import { useTheme } from './theme';

// ---------------------------------------------------------------- routes (code-split per page)
const Home = lazy(() => import('../pages/Home'));
const Start = lazy(() => import('../pages/Start'));
const Colour = lazy(() => import('../pages/foundations/Colour'));
const Typography = lazy(() => import('../pages/foundations/Typography'));
const Layout = lazy(() => import('../pages/foundations/Layout'));
const Icons = lazy(() => import('../pages/foundations/Icons'));
const Emoji = lazy(() => import('../pages/foundations/Emoji'));
const Brand = lazy(() => import('../pages/foundations/Brand'));
const ComponentsIndex = lazy(() => import('../pages/ComponentsIndex'));
const ComponentPage = lazy(() => import('../pages/ComponentPage'));
const Screens = lazy(() => import('../pages/Screens'));
const Prototype = lazy(() => import('../pages/Prototype'));
const Flow = lazy(() => import('../pages/Flow'));
const Patterns = lazy(() => import('../pages/Patterns'));
const Conflicts = lazy(() => import('../pages/reference/Conflicts'));
const Assets = lazy(() => import('../pages/reference/Assets'));
const NotFound = lazy(() => import('../pages/NotFound'));
const Claude = lazy(() => import('../pages/Claude'));
const Decisions = lazy(() => import('../pages/reference/Decisions'));
const FigmaMap = lazy(() => import('../pages/reference/FigmaMap'));

function pageFor(path: string): ReactNode {
  if (path === '/') return <Home />;
  if (path === '/start') return <Start />;
  if (path === '/start/claude') return <Claude />;
  if (path === '/foundations/colour') return <Colour />;
  if (path === '/foundations/typography') return <Typography />;
  if (path === '/foundations/layout') return <Layout />;
  if (path === '/foundations/icons') return <Icons />;
  if (path === '/foundations/emoji') return <Emoji />;
  if (path === '/foundations/brand') return <Brand />;
  if (path === '/components') return <ComponentsIndex />;
  if (path.startsWith('/components/')) {
    const doc = componentDocs.find((d) => d.id === path.slice(12));
    return doc ? <ComponentPage doc={doc} /> : <NotFound />;
  }
  if (path === '/screens') return <Screens />;
  if (path === '/prototype') return <Prototype />;
  if (path.startsWith('/flows/')) return <Flow id={path.slice(7)} />;
  if (path === '/patterns') return <Patterns />;
  if (path === '/reference/conflicts') return <Conflicts />;
  if (path === '/reference/assets') return <Assets />;
  if (path === '/reference/decisions') return <Decisions />;
  if (path === '/reference/figma-map') return <FigmaMap />;
  return <NotFound />;
}

// ---------------------------------------------------------------- search index
type Hit = { group: string; label: string; detail?: string; path: string; anchor?: string; swatch?: string; icon?: string };

function useIndex(): Hit[] {
  return useMemo(() => {
    const hits: Hit[] = [];
    allPages.forEach((p) => hits.push({ group: 'Pages', label: p.label, detail: p.hint, path: p.path }));
    componentDocs.forEach((d) =>
      d.components.forEach((c) => {
        if (c !== d.name) hits.push({ group: 'Components', label: c, detail: d.name, path: `/components/${d.id}` });
      }),
    );
    aliases.forEach((a) => hits.push({ group: 'Colour tokens', label: a.name, detail: a.cssVar, path: '/foundations/colour', anchor: `t-${a.cssVar.slice(5)}`, swatch: a.light }));
    Object.keys(typography).forEach((t) => hits.push({ group: 'Type tokens', label: t, detail: `.eq-t-${t.replace(/[A-Z]/g, (c) => '-' + c.toLowerCase())}`, path: '/foundations/typography', anchor: `type-${t}` }));
    iconNames.forEach((n) => hits.push({ group: 'Icons', label: n, path: '/foundations/icons', anchor: `icon-${n}`, icon: n }));
    return hits;
  }, []);
}

function score(h: Hit, q: string) {
  const l = h.label.toLowerCase();
  const d = (h.detail ?? '').toLowerCase();
  if (l === q) return 100;
  if (l.startsWith(q)) return 80;
  if (l.includes(q)) return 60;
  if (d.includes(q)) return 30;
  const parts = q.split(/\s+/).filter(Boolean);
  if (parts.length > 1 && parts.every((p) => l.includes(p) || d.includes(p))) return 20;
  return 0;
}

function SearchPalette({ open, onClose }: { open: boolean; onClose: () => void }) {
  const index = useIndex();
  const { go } = useRoute();
  const [q, setQ] = useState('');
  const [sel, setSel] = useState(0);
  const input = useRef<HTMLInputElement>(null);
  useEffect(() => {
    if (open) {
      setQ('');
      setSel(0);
      setTimeout(() => input.current?.focus(), 0);
    }
  }, [open]);
  const results = useMemo(() => {
    const t = q.trim().toLowerCase();
    if (!t) return index.filter((h) => h.group === 'Pages').slice(0, 12);
    return index
      .map((h) => ({ h, s: score(h, t) }))
      .filter((x) => x.s > 0)
      .sort((a, b) => b.s - a.s)
      .slice(0, 40)
      .map((x) => x.h);
  }, [q, index]);
  if (!open) return null;
  const pick = (h: Hit) => {
    onClose();
    go(h.path, h.anchor);
  };
  let lastGroup = '';
  return (
    <div className="s-palette-scrim" onMouseDown={(e) => e.target === e.currentTarget && onClose()}>
      <div className="s-palette" role="dialog" aria-modal="true" aria-label="Search the design system">
        <div className="s-palette__field">
          <Icon name="search" size={20} color="var(--site-ink-3)" />
          <input
            ref={input}
            value={q}
            placeholder="Search tokens, components, icons, screens…"
            aria-label="Search"
            aria-controls="s-palette-list"
            onChange={(e) => (setQ(e.target.value), setSel(0))}
            onKeyDown={(e) => {
              if (e.key === 'ArrowDown') (e.preventDefault(), setSel((s) => Math.min(s + 1, results.length - 1)));
              if (e.key === 'ArrowUp') (e.preventDefault(), setSel((s) => Math.max(s - 1, 0)));
              if (e.key === 'Enter' && results[sel]) pick(results[sel]);
              if (e.key === 'Escape') onClose();
            }}
          />
          <span className="s-kbd">esc</span>
        </div>
        <div className="s-palette__list" id="s-palette-list" role="listbox">
          {results.length === 0 && (
            <div className="s-palette__empty">
              Nothing matches “{q}”. Try a token like <b>text/action</b>, a component like <b>ChatListItem</b>, or an icon like <b>call</b>.
            </div>
          )}
          {results.map((h, i) => {
            const head = h.group !== lastGroup ? (lastGroup = h.group) : null;
            return (
              <div key={`${h.group}-${h.label}-${i}`}>
                {head && <div className="s-palette__group">{head}</div>}
                <button type="button" role="option" aria-selected={i === sel} className="s-palette__item" onMouseEnter={() => setSel(i)} onClick={() => pick(h)}>
                  {h.swatch ? <span className="s-swatch-dot" style={{ background: h.swatch }} /> : h.icon ? <Icon name={h.icon as never} size={18} /> : <Icon name="chevron-right" size={18} color="var(--site-ink-3)" />}
                  <span>{h.label}</span>
                  {h.detail && <small>{h.detail}</small>}
                </button>
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
}

// ---------------------------------------------------------------- shell
function ThemeSwitch() {
  const { theme, exampleTheme, setExampleTheme } = useTheme();
  const cur = exampleTheme === 'both' ? 'both' : theme;
  const opts = [
    { v: 'light', label: 'Light', icon: 'light-mode', title: 'Light theme' },
    { v: 'dark', label: 'Dark', icon: 'dark-mode', title: 'Dark theme' },
    { v: 'both', label: 'Both', icon: 'contrast', title: 'Show examples in both themes' },
  ] as const;
  return (
    <div className="s-seg s-theme" role="group" aria-label="Theme">
      {opts.map((o) => (
        <button key={o.v} type="button" aria-pressed={cur === o.v} onClick={() => setExampleTheme(o.v)} title={o.title} aria-label={o.title}>
          <Icon name={o.icon} size={16} />
          <span className="s-theme__label">{o.label}</span>
        </button>
      ))}
    </div>
  );
}

function Sidebar({ section, open, onNavigate }: { section: Section; open: boolean; onNavigate: () => void }) {
  const { path } = useRoute();
  const [q, setQ] = useState('');
  const [closed, setClosed] = useState<Record<string, boolean>>({});
  const isComp = section.id === 'components';
  const groups = section.groups
    .map((g) => ({ ...g, pages: q ? g.pages.filter((p) => p.label.toLowerCase().includes(q.toLowerCase())) : g.pages }))
    .filter((g) => g.pages.length);
  return (
    <aside className="s-side" data-open={open} aria-label={`${section.label} navigation`}>
      {isComp && (
        <label className="s-side__search">
          <Icon name="search" size={18} color="var(--site-ink-3)" />
          <input value={q} onChange={(e) => setQ(e.target.value)} placeholder="Search components…" aria-label="Filter components" />
        </label>
      )}
      {groups.map((g) => {
        const shut = !q && closed[g.label] && !g.pages.some((p) => p.path === path);
        return (
          <div key={g.label || 'root'} className="s-side__group">
            {g.label && (
              <button type="button" className="s-side__label s-side__toggle" aria-expanded={!shut} onClick={() => setClosed((c) => ({ ...c, [g.label]: !shut }))}>
                {g.label}
                <Icon name="chevron-down" size={16} style={{ transform: shut ? 'rotate(-90deg)' : undefined, transition: 'transform 160ms' }} />
              </button>
            )}
            {!shut && g.pages.map((p) => (
              <Link key={p.path} to={p.path} aria-current={path === p.path ? 'page' : undefined} onClick={onNavigate}>{p.label}</Link>
            ))}
          </div>
        );
      })}
      {isComp && groups.length === 0 && <p style={{ padding: '0 10px', fontSize: 13, color: 'var(--site-ink-3)' }}>No component matches “{q}”.</p>}
    </aside>
  );
}

export function Shell() {
  const { path, anchor } = useRoute();
  const [search, setSearch] = useState(false);
  const [menu, setMenu] = useState(false);
  const home = path === '/';
  const section = sectionFor(path);
  const bare = home || section?.id === 'playground';

  useEffect(() => {
    const on = (e: KeyboardEvent) => {
      if ((e.metaKey || e.ctrlKey) && e.key.toLowerCase() === 'k') (e.preventDefault(), setSearch((s) => !s));
      else if (e.key === '/' && !(e.target instanceof HTMLInputElement || e.target instanceof HTMLTextAreaElement)) (e.preventDefault(), setSearch(true));
    };
    window.addEventListener('keydown', on);
    return () => window.removeEventListener('keydown', on);
  }, []);

  // New page: scroll to top, or to the requested anchor once it renders.
  useEffect(() => {
    setMenu(false);
    if (!anchor) {
      window.scrollTo({ top: 0 });
      return;
    }
    let tries = 0;
    const t = setInterval(() => {
      const el = document.getElementById(anchor);
      if (el || ++tries > 40) {
        clearInterval(t);
        if (el) {
          el.scrollIntoView({ block: 'center' });
          el.animate?.([{ boxShadow: '0 0 0 3px var(--site-accent)' }, { boxShadow: '0 0 0 3px transparent' }], { duration: 1400, easing: 'ease-out' });
        }
      }
    }, 50);
    return () => clearInterval(t);
  }, [path, anchor]);

  return (
    <>
      <a href="#main" className="s-visually-hidden" onFocus={(e) => e.currentTarget.classList.remove('s-visually-hidden')}>Skip to content</a>
      <header className="s-top">
        {!bare && (
          <button type="button" className="s-iconbtn" aria-label="Open sections" aria-expanded={menu} onClick={() => setMenu((m) => !m)}>
            <Icon name={menu ? 'close' : 'menu'} size={20} />
          </button>
        )}
        <Link to="/" className="s-brand" aria-label="Equal AI Design System, home">
          <EqualLogo height={20} />
          <span className="s-brand__name">Design System</span>
        </Link>
        <span className="s-top__spacer" />
        <nav className="s-topnav" aria-label="Sections">
          {sections.map((sec) => (
            <Link key={sec.id} to={sec.home} aria-current={section?.id === sec.id ? 'page' : undefined}>{sec.label}</Link>
          ))}
        </nav>
        <button type="button" className="s-searchbtn" onClick={() => setSearch(true)} aria-label="Search (⌘K)">
          <Icon name="search" size={18} />
          <span>Search</span>
          <span className="s-kbd">⌘K</span>
        </button>
        <ThemeSwitch />
        <Link to="/start" className="s-cta">Get started</Link>
      </header>
      <div className={`s-frame ${bare ? 's-frame--home' : ''}`}>
        {!bare && section && <Sidebar key={section.id} section={section} open={menu} onNavigate={() => setMenu(false)} />}
        <main id="main" className="s-main">
          <Suspense fallback={<div style={{ minHeight: '80vh' }} />}>{pageFor(path)}</Suspense>
        </main>
      </div>
      <SearchPalette open={search} onClose={() => setSearch(false)} />
    </>
  );
}
