import { useMemo, useState } from 'react';
import { aliases, gradients, primitives } from '@equal/ds';
import tokenUsage from '../../registry/token-usage.json';
import { Link } from '../../app/router';
import { copyText } from '../../app/copy';
import { Callout, CopyTag, H2, Page, useToast } from '../../ui/kit';
import { FoundationCode } from '../../ui/FoundationCode';

const camel = (css: string) => css.replace(/^--eq-/, '').replace(/-(.)/g, (_, c) => c.toUpperCase());
const GROUPS: { key: string; label: string; note: string }[] = [
  { key: 'bg', label: 'Background', note: 'Surfaces that set spatial hierarchy: the page ground and the slabs on it.' },
  { key: 'text', label: 'Text', note: 'Content, labels, placeholders, action text and disabled states. Green (lime in dark) only for what you can touch.' },
  { key: 'button', label: 'Button', note: 'Fills for primary, secondary, danger and disabled controls.' },
  { key: 'stroke', label: 'Stroke', note: 'Subtle for card edges and dividers, medium for inputs, dark for high-contrast separators.' },
  { key: 'action', label: 'Action chips', note: 'The glass chips in the chat list.' },
  { key: 'component', label: 'Component', note: 'Tokens scoped to one element: bubbles, cards, chips, plan card, wallpaper.' },
];

type Usage = { pages: { id: string; name: string }[]; screens: string[] };
const usageOf = (name: string) => (tokenUsage as Record<string, Usage>)[name];

/** Purpose (why the colour exists) and where it is used: docs pages it paints, plus screens. */
function Where({ name, why }: { name: string; why?: string }) {
  const u = usageOf(name);
  return (
    <div className="s-where">
      {why && <div><span className="s-where__k">Purpose</span>{why}</div>}
      <div>
        <span className="s-where__k">Used in</span>
        {u && (u.pages.length || u.screens.length) ? (
          <span className="s-where__list">
            {u.pages.map((p) => <Link key={p.id} to={`/components/${p.id}`} className="s-where__chip">{p.name}</Link>)}
            {u.screens.map((sc) => <span key={sc} className="s-where__chip is-screen">{sc}</span>)}
          </span>
        ) : <span style={{ color: 'var(--site-ink-3)' }}>Not used by any component yet</span>}
      </div>
    </div>
  );
}

function Ramp({ name, values }: { name: string; values: Record<string, string> }) {
  const toast = useToast();
  return (
    <div className="s-swatches" style={{ marginTop: 12 }}>
      {Object.entries(values).map(([k, hex]) => (
        <button key={k} type="button" className="s-sw" title={`Copy ${hex}`} onClick={async () => (await copyText(hex)) && toast(`Copied ${hex}`)}>
          <div className="s-sw__chip" style={{ background: hex }} />
          <div className="s-sw__meta">
            <b>{name}/{k}</b>
            <span>{hex}</span>
          </div>
        </button>
      ))}
    </div>
  );
}

export default function Colour() {
  const [q, setQ] = useState('');
  const [group, setGroup] = useState<string>('all');
  const [derivedOnly, setDerivedOnly] = useState(false);
  const list = useMemo(
    () =>
      aliases.filter(
        (a) => (group === 'all' || a.group === group) && (!derivedOnly || a.derived) && (!q || a.name.includes(q.toLowerCase()) || a.cssVar.includes(q.toLowerCase())),
      ),
    [q, group, derivedOnly],
  );
  return (
    <Page
      title="Colour"
      lead="Primitives are raw values; alias tokens give them meaning and switch between light and dark. Components only ever read alias tokens."
      meta={<><CopyTag k="CSS" value="var(--eq-text-action)" /><CopyTag k="TS" value="color.textAction" /><CopyTag k="Kotlin" value="EqualTheme.colors.textAction" /></>}
    >
      <H2>Primitives</H2>
      <p className="s-p">Brand green and lime are the action colours; black and white anchor contrast. Only the neutrals and reds a token or component uses are kept (7 greys, 3 reds); the rest of the original ramps were removed because nothing used them. Click a swatch to copy its hex.</p>
      <Ramp name="brand" values={primitives.brand} />
      <Ramp name="neutrals" values={primitives.neutrals} />
      <Ramp name="red" values={primitives.red} />

      <H2>Alias tokens</H2>
      <p className="s-p">
        Every alias token with its light and dark value. Rows tagged <b>derived</b> are final values taken from the component references and screens, because the original colour guide doesn’t define them. Text colours pass WCAG AA in both themes.
      </p>
      <div className="s-filters">
        <input className="s-input" placeholder="Filter tokens…" value={q} onChange={(e) => setQ(e.target.value)} aria-label="Filter tokens" />
        {[{ key: 'all', label: 'All' }, ...GROUPS].map((g) => (
          <button key={g.key} type="button" className="s-filter" aria-pressed={group === g.key} onClick={() => setGroup(g.key)}>{g.label}</button>
        ))}
        <button type="button" className="s-filter" aria-pressed={derivedOnly} onClick={() => setDerivedOnly((p) => !p)}>Derived only</button>
      </div>
      {GROUPS.filter((g) => list.some((a) => a.group === g.key)).map((g) => (
        <section key={g.key}>
          <h3 className="s-h3">{g.label}</h3>
          <p className="s-p" style={{ fontSize: 14 }}>{g.note}</p>
          <div className="s-table" style={{ display: 'block' }}>
            <div className="s-alias" role="table" aria-label={`${g.label} tokens`}>
              {list
                .filter((a) => a.group === g.key)
                .map((a) => (
                  <div key={a.name} style={{ display: 'contents' }} role="row">
                    <div className="s-alias__name" id={`t-${a.cssVar.slice(5)}`} role="cell">
                      <div style={{ minWidth: 0 }}>
                        <div style={{ display: 'flex', alignItems: 'center', gap: 8, flexWrap: 'wrap' }}>
                          <b>{a.name}</b>
                          {a.derived && <span className="s-pill" title="Final value, derived from the screens and component references (not in the original colour guide)">derived</span>}
                        </div>
                        <div style={{ display: 'flex', gap: 6, marginTop: 8, flexWrap: 'wrap' }}>
                          <CopyTag value={`var(${a.cssVar})`} label={a.cssVar} />
                          <CopyTag k="Kt" value={`EqualTheme.colors.${camel(a.cssVar)}`} label={camel(a.cssVar)} />
                        </div>
                        <Where name={a.name} why={a.why ?? undefined} />
                      </div>
                    </div>
                    {(['light', 'dark'] as const).map((m) => (
                      <div key={m} role="cell">
                        <span className="s-chip-dual" style={{ background: a[m] }} />
                        <div style={{ minWidth: 0 }}>
                          <div style={{ fontFamily: 'var(--site-mono)', fontSize: 12.5, color: 'var(--site-ink)' }}>{a[m]}</div>
                          <div style={{ fontSize: 12, color: 'var(--site-ink-3)' }}>{m} · {m === 'light' ? a.lightRef : a.darkRef}</div>
                        </div>
                      </div>
                    ))}
                  </div>
                ))}
            </div>
          </div>
        </section>
      ))}
      {list.length === 0 && <Callout>No token matches “{q}”. Try <b>text</b>, <b>stroke</b> or <b>bubble</b>.</Callout>}

      <H2>Gradients</H2>
      <p className="s-p">Gradients seen in the component references and screens. They are derived from the screens (the colour guide doesn’t define gradients) and are final.</p>
      <div className="s-grid" style={{ ['--min' as string]: '240px' }}>
        {gradients.map((g) => (
          <div key={g.name} className="s-dd" style={{ padding: 0, overflow: 'hidden' }}>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr' }}>
              <div style={{ height: 88, background: g.light }} />
              <div style={{ height: 88, background: g.dark }} />
            </div>
            <div style={{ padding: 14 }}>
              <b style={{ fontWeight: 500 }}>{g.name}</b>
              <div style={{ margin: '6px 0' }}><CopyTag value={`var(${g.cssVar})`} label={g.cssVar} /></div>
              <Where name={`gradient/${g.name.replace(/^gradient\//, '')}`} why={g.why} />
            </div>
          </div>
        ))}
      </div>
      <H2>Code</H2>
      <FoundationCode id="colour" />
    </Page>
  );
}
