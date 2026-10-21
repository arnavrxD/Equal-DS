import { useMemo, useState } from 'react';
import { Icon, iconNames, iconSource } from '@equal/ds';
import { copyText } from '../../app/copy';
import { Callout, CopyTag, H2, Page, useToast } from '../../ui/kit';
import { FoundationCode } from '../../ui/FoundationCode';

const kt = (n: string) => n.replace(/-(.)/g, (_, c) => c.toUpperCase());

export default function Icons() {
  const [q, setQ] = useState('');
  const [sizePx, setSize] = useState(24);
  const toast = useToast();
  const list = useMemo(() => iconNames.filter((n) => !q || n.includes(q.toLowerCase()) || iconSource[n].includes(q.toLowerCase())), [q]);
  return (
    <Page
      title="Icons"
      lead="Every flat icon is a Material Symbol (Outlined, weight 400; filled where the design fills it), plus a few glyphs Symbols lacks (WhatsApp, call + sparkle), drawn on the same 960 grid with the same 1.5dp stroke so every icon has one weight. One manifest generates web and Compose with identical paths."
      meta={<><CopyTag k="React" value={'<Icon name="call" size={24} />'} /><CopyTag k="Kotlin" value="EqualSymbols.call" /><CopyTag k="Add one" value="npm run icons" /></>}
    >
      <H2>The set</H2>
      <div className="s-filters">
        <input className="s-input" placeholder={`Filter ${iconNames.length} icons…`} value={q} onChange={(e) => setQ(e.target.value)} aria-label="Filter icons" />
        <div className="s-seg" role="group" aria-label="Preview size">
          {[20, 24, 32].map((s) => (
            <button key={s} type="button" aria-pressed={sizePx === s} onClick={() => setSize(s)}>{s}</button>
          ))}
        </div>
        <span style={{ fontSize: 13, color: 'var(--site-ink-3)' }}>Click an icon to copy its JSX</span>
      </div>
      <div className="s-grid" style={{ ['--min' as string]: '128px' }}>
        {list.map((n) => (
          <button key={n} id={`icon-${n}`} type="button" className="s-cell" onClick={async () => (await copyText(`<Icon name="${n}" size={24} />`)) && toast(`Copied <Icon name="${n}" />`)}>
            <Icon name={n} size={sizePx} />
            <b>{n}</b>
            <span>{iconSource[n].replace('material-symbols/outlined/', '')}</span>
            <span style={{ opacity: 0.8 }}>EqualSymbols.{kt(n)}</span>
          </button>
        ))}
      </div>
      {list.length === 0 && <Callout>No icon matches “{q}”. Browse Material Symbols, add the name to <code>scripts/build-icons.mjs</code> and run <code>npm run icons</code>.</Callout>}
      <H2>Rules</H2>
      <p className="s-p">Icons are 24dp on a 48dp touch target and inherit <code>currentColor</code>. Headers and rows use <code>text/primary</code>; actionable glyphs use <code>text/action</code>; missed calls and destructive items use <code>text/danger</code>. Actions on chips use emoji, not icons; see Action emoji. The six-dot Equal mark is a logo, not an icon.</p>
      <H2>Code</H2>
      <FoundationCode id="icons" />
    </Page>
  );
}
